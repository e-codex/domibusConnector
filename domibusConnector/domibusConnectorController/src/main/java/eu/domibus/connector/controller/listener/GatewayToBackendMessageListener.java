package eu.domibus.connector.controller.listener;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.domibus.connector.controller.process.DomibusConnectorMessageProcessor;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;

@Component("GatewayToBackendMessageListener")
public class GatewayToBackendMessageListener extends AbstractControllerMessageListener implements MessageListener {

	private static final String queueName = "domibus.connector.internal.gateway.to.controller.queue";
	
	@Resource(name="GatewayToBackendMessageProcessor")
	private DomibusConnectorMessageProcessor messageProcessor;
	
	@Resource(name="GatewayToBackendConfirmationProcessor")
	private DomibusConnectorMessageProcessor confirmationProcessor;
	
	@Override
	@Transactional
	@JmsListener(destination = "${"+queueName+"}") //Funktioniert das????
	public void onMessage(Message message) {
		handleMessage(message, queueName);
	}

	@Override
	void startProcessing(DomibusConnectorMessage connectorMessage) {
		if(connectorMessage.getMessageContent()!=null) {
			// as there is a message content, it cannot only be a confirmation message
			messageProcessor.processMessage(connectorMessage);
		}else if(!CollectionUtils.isEmpty(connectorMessage.getMessageConfirmations())) {
			// as there is no message content, but at least one message confirmation,
			// it is a confirmation message
			confirmationProcessor.processMessage(connectorMessage);
		}
	}

}
