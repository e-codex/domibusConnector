package eu.domibus.connector.controller.queues.listener;

import eu.domibus.connector.controller.processor.EvidenceMessageProcessor;
import eu.domibus.connector.controller.processor.ToBackendBusinessMessageProcessor;
import eu.domibus.connector.controller.processor.ToGatewayBusinessMessageProcessor;
import eu.domibus.connector.controller.queues.producer.ToConnectorQueue;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import javax.jms.Message;
import javax.jms.MessageListener;

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CONNECTOR_QUEUE_BEAN;

@Component
public class ToConnectorControllerListener {

    private static final Logger LOGGER = LogManager.getLogger(ToConnectorControllerListener.class);

    private final ToGatewayBusinessMessageProcessor toGatewayBusinessMessageProcessor;
    private final ToBackendBusinessMessageProcessor toBackendBusinessMessageProcessor;
    private final EvidenceMessageProcessor evidenceMessageProcessor;

    public ToConnectorControllerListener(ToGatewayBusinessMessageProcessor toGatewayBusinessMessageProcessor,
                                         ToBackendBusinessMessageProcessor toBackendBusinessMessageProcessor,
                                         EvidenceMessageProcessor evidenceMessageProcessor
                                         ) {
        this.toGatewayBusinessMessageProcessor = toGatewayBusinessMessageProcessor;
        this.toBackendBusinessMessageProcessor = toBackendBusinessMessageProcessor;
        this.evidenceMessageProcessor = evidenceMessageProcessor;
    }

    @Transactional //(rollbackFor = Exception.class)
    @JmsListener(destination = TO_CONNECTOR_QUEUE_BEAN)
    @eu.domibus.connector.lib.logging.MDC(name = LoggingMDCPropertyNames.MDC_DC_QUEUE_LISTENER_PROPERTY_NAME, value = "ToConnectorControllerListener")
    public void handleMessage(DomibusConnectorMessage message) {
        if (message == null || message.getMessageDetails() == null) {
            throw new IllegalArgumentException("Message and Message Details must not be null");
        }
        String messageId = message.getConnectorMessageId().toString();
        try (MDC.MDCCloseable mdcCloseable = MDC.putCloseable(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, messageId)) {
            DomibusConnectorMessageDirection direction = message.getMessageDetails().getDirection();
            if (DomainModelHelper.isEvidenceMessage(message)) {
                evidenceMessageProcessor.processMessage(message);
            } else if (DomainModelHelper.isBusinessMessage(message) && direction.getTarget() == MessageTargetSource.GATEWAY) {
                toGatewayBusinessMessageProcessor.processMessage(message);
            } else if (DomainModelHelper.isBusinessMessage(message) && direction.getTarget() == MessageTargetSource.BACKEND) {
                toBackendBusinessMessageProcessor.processMessage(message);
            } else {
                throw new IllegalStateException("Illegal Message format received!");
            }
        } catch (Exception exc) {
            //cannot recover here: put into DLQ!
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            boolean rollbackStatus = TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
            LOGGER.error(LoggingMarker.Log4jMarker.BUSINESS_LOG, "Failed to process message due [{}]! Rollback is [{}], check DLQ", exc.getMessage(), rollbackStatus);
            String error = String.format("Failed to process messsage. Rollback is [%s], Reason for rollback is:\n%s", rollbackStatus, exc.getMessage());
            LOGGER.error(error, exc);
//            throw exc;
//            DomibusConnectorMessageError build = DomibusConnectorMessageErrorBuilder.createBuilder()
//                    .setText(error)
//                    .setDetails(exc)
//                    .setSource(ToConnectorControllerListener.class)
//                    .build();
//            message.getMessageProcessErrors().add(build);
//            toConnectorQueue.putOnErrorQueue(message);
        }
    }

}
