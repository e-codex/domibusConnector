package eu.domibus.connector.controller.service.impl;

import eu.domibus.connector.controller.process.DomibusGatewayLoopbackReceiveProcessor;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.tools.logging.SetMessageOnLoggingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.controller.exception.DomibusConnectorControllerException;
import eu.domibus.connector.controller.service.DomibusConnectorGatewayDeliveryService;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.persistence.service.DomibusConnectorMessageContentManager;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;


@Component("domibusConnectorGatewayDeliveryServiceImpl")
public class DomibusConnectorGatewayDeliveryServiceImpl implements DomibusConnectorGatewayDeliveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorGatewayDeliveryServiceImpl.class);

    private PutMessageOnQueue putMessageOnQueue;
    private DCMessagePersistenceService messagePersistenceService;
    private DomibusConnectorMessageContentManager bigDataOfMessagePersistenceService;
    private DomibusConnectorMessageIdGenerator messageIdGenerator;
    private DomibusGatewayLoopbackReceiveProcessor domibusGatewayLoopbackReceiveProcessor;

    //setter
    @Autowired
    @Qualifier(PutMessageOnQueue.GATEWAY_TO_CONTROLLER_QUEUE)
    public void setPutMessageOnQueue(PutMessageOnQueue putMessageOnQueue) {
        this.putMessageOnQueue = putMessageOnQueue;
    }

    @Autowired
    public void setMessagePersistenceService(DCMessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    @Autowired
    public void setBigDataOfMessagePersistenceService(DomibusConnectorMessageContentManager bigDataOfMessagePersistenceService) {
        this.bigDataOfMessagePersistenceService = bigDataOfMessagePersistenceService;
    }

    @Autowired
    public void setMessageIdGenerator(DomibusConnectorMessageIdGenerator messageIdGenerator) {
        this.messageIdGenerator = messageIdGenerator;
    }

    @Autowired
    public void setDomibusGatewayLoopbackReceiveProcessor(DomibusGatewayLoopbackReceiveProcessor domibusGatewayLoopbackReceiveProcessor) {
        this.domibusGatewayLoopbackReceiveProcessor = domibusGatewayLoopbackReceiveProcessor;
    }

    @Override
    public void deliverMessageFromGatewayToController(DomibusConnectorMessage message) throws DomibusConnectorControllerException {
        if (message == null) {
            throw new DomibusConnectorControllerException("Message must not be null!");
        }

        if (message.getConnectorMessageId() == null) {
            DomibusConnectorMessageId connectorMessageId = messageIdGenerator.generateDomibusConnectorMessageId();
            message.setConnectorMessageId(connectorMessageId);
        }
        SetMessageOnLoggingContext.putConnectorMessageIdOnMDC(message);

        //Check consistence of message:
        // Either a message content, or at least one confirmation must exist for processing
        if (!checkMessageForProcessability(message))
            throw new DomibusConnectorControllerException("Message cannot be processed as it contains neither message content, nor message confirmation!");

        domibusGatewayLoopbackReceiveProcessor.processMessage(message);



        try {
            message = messagePersistenceService.persistMessageIntoDatabase(message, DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
        } catch (PersistenceException e) {
            throw new DomibusConnectorControllerException("Message could not be persisted!", e);
        }


        putMessageOnQueue.putMessageOnMessageQueue(message);
    }


    private boolean checkMessageForProcessability(DomibusConnectorMessage message) {

        if (message == null)
            return false;

        if (message.getMessageContent() != null)
            return true;

        if (!CollectionUtils.isEmpty(message.getTransportedMessageConfirmations()))
            return true;

        return false;
    }


}
