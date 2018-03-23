package eu.domibus.connector.controller.service.queue;

import eu.domibus.connector.controller.helper.SetMessageOnLoggingContext;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.persistence.service.DomibusConnectorMessagePersistenceService;
import eu.domibus.connector.persistence.service.PersistenceException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class GetDomibusConnectorMessageFromJmsMessageImpl implements GetDomibusConnectorMessageFromJmsMessage {

    private static final Logger LOGGER  = LoggerFactory.getLogger(GetDomibusConnectorMessageFromJmsMessageImpl.class);

    @Autowired
    private DomibusConnectorMessagePersistenceService persistenceService;

    @Override
    public DomibusConnectorMessage getMessage(Message message) {
        String jmsDestination = "";
        try {
            if (message instanceof TextMessage) {
                jmsDestination = message.getJMSDestination().toString();
                TextMessage msg = (TextMessage) message;

                String connectorMessageId = msg.getText();
                SetMessageOnLoggingContext.putConnectorMessageIdOnMDC(connectorMessageId);
                LOGGER.info("received messageID [{}] from queue [{}].", connectorMessageId, jmsDestination);
                DomibusConnectorMessage connectorMessage = null;
                try {
                    connectorMessage = persistenceService.findMessageByConnectorMessageId(connectorMessageId);
                } catch (PersistenceException e) {
                    LOGGER.error("Message [{}] could not be loaded from database!", connectorMessageId, e);
                }

                if (connectorMessage != null) {
                    return connectorMessage;
                } else {
                    LOGGER.error("Message [{}] is null!");
                    throw new RuntimeException("Cannot get message null from queue!");
                }

            } else {
                throw new IllegalArgumentException("Message must be of type TextMessage");
            }
        } catch (JMSException e) {
            LOGGER.error("Exception receiving message from queue [{}].", jmsDestination);
            throw new RuntimeException("A jms exception occured while loading message", e);
        }
    }
}
