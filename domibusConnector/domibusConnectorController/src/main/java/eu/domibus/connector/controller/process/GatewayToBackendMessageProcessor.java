package eu.domibus.connector.controller.process;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.domibus.connector.controller.exception.DomibusConnectorControllerException;
import eu.domibus.connector.controller.exception.DomibusConnectorMessageException;
import eu.domibus.connector.controller.exception.DomibusConnectorMessageExceptionBuilder;
import eu.domibus.connector.controller.service.DomibusConnectorBackendDeliveryService;
import eu.domibus.connector.controller.service.DomibusConnectorGatewaySubmissionService;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.domibus.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.domibus.connector.persistence.service.DomibusConnectorPersistenceService;
import eu.domibus.connector.persistence.service.PersistenceException;
import eu.domibus.connector.security.DomibusConnectorSecurityToolkit;
import eu.domibus.connector.security.exception.DomibusConnectorSecurityException;

@Component("GatewayToBackendMessageProcessor")
public class GatewayToBackendMessageProcessor implements DomibusConnectorMessageProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayToBackendMessageProcessor.class);
	
	@Value("${domibus.connector.to.connector.test.service:null}")
	private String connectorTestService;
	
	@Value("${domibus.connector.to.connector.test.action:null}")
	private String connectorTestAction;
	
	@Resource
	private DomibusConnectorPersistenceService persistenceService;
	
	@Resource
	private DomibusConnectorGatewaySubmissionService gwSubmissionService;
	
	@Resource
	private DomibusConnectorMessageIdGenerator messageIdGenerator;
	
	@Resource
	private DomibusConnectorEvidencesToolkit evidencesToolkit;
	
	@Resource
	private DomibusConnectorSecurityToolkit securityToolkit;
	
	@Resource
	private DomibusConnectorBackendDeliveryService backendDeliveryService;
	
	@Override
	public void processMessage(DomibusConnectorMessage message) {
		
		
		createRelayREMMDEvidenceAndSendIt(message, true);
		
		
		try {
			securityToolkit.validateContainer(message);
		} catch (DomibusConnectorSecurityException e) {
			createNonDeliveryEvidenceAndSendIt(message);
			throw e;
		}
		
		if(isConnector2ConnectorTest(message)){
			// if it is a connector to connector test message defined by service and action, do NOT deliver message to the backend, but 
			// only send a DELIVERY evidence back.
			LOGGER.info("Message with id {} is a connector to connector test message. \nIt will NOT be delivered to the backend!", message.getConnectorMessageId());
			createDeliveryEvidenceAndSendIt(message);
			LOGGER.info("Connector to Connector Test message {} is confirmed!", message.getConnectorMessageId());
		}else{
			backendDeliveryService.deliverMessageToBackend(message);
		}

		 // TODO this needs to be done by the backend link!!!
//		persistenceService.setMessageDeliveredToNationalSystem(message);

		LOGGER.info("Successfully processed message {} from GW to backend.", message.getConnectorMessageId());
	}
	
	private boolean isConnector2ConnectorTest(DomibusConnectorMessage message) {
		return (!StringUtils.isEmpty(connectorTestService) && message.getMessageDetails().getService().getService().equals(connectorTestService)) 
				&& (!StringUtils.isEmpty(connectorTestAction) && message.getMessageDetails().getAction().getAction().equals(connectorTestAction));
	}
	
	private void createDeliveryEvidenceAndSendIt(DomibusConnectorMessage originalMessage)
			throws DomibusConnectorControllerException, DomibusConnectorMessageException {

		DomibusConnectorMessageConfirmation delivery = null;
		try {
			delivery = evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.DELIVERY,originalMessage, null, null);
		} catch (DomibusConnectorEvidencesToolkitException e) {
            throw DomibusConnectorMessageExceptionBuilder.createBuilder()
                    .setMessage(originalMessage)
                    .setText("Error creating Delivery evidence for message!")
                    .setSource(this.getClass())
                    .setCause(e)
                    .build();
		}

		DomibusConnectorAction action = persistenceService.getDeliveryNonDeliveryToRecipientAction();

		sendEvidenceToBackToGateway(originalMessage, action, delivery);

		persistenceService.confirmMessage(originalMessage);
	}
	
	private void createNonDeliveryEvidenceAndSendIt(DomibusConnectorMessage originalMessage)
			throws DomibusConnectorControllerException, DomibusConnectorMessageException {

		DomibusConnectorMessageConfirmation nonDelivery = null;
		try {
			nonDelivery = evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.NON_DELIVERY, originalMessage,DomibusConnectorRejectionReason.OTHER, null);
		} catch (DomibusConnectorEvidencesToolkitException e) {
            throw DomibusConnectorMessageExceptionBuilder.createBuilder()
                    .setMessage(originalMessage)
                    .setText("Error creating NonDelivery evidence for message!")
                    .setSource(this.getClass())
                    .setCause(e)
                    .build();
		}

		DomibusConnectorAction action = persistenceService.getDeliveryNonDeliveryToRecipientAction();

		sendEvidenceToBackToGateway(originalMessage, action, nonDelivery);

		persistenceService.rejectMessage(originalMessage);
	}
	
	private void createRelayREMMDEvidenceAndSendIt(DomibusConnectorMessage originalMessage, boolean isAcceptance)
			throws DomibusConnectorControllerException, DomibusConnectorMessageException {
		DomibusConnectorMessageConfirmation messageConfirmation = null;
		try {
			messageConfirmation = isAcceptance ? evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, originalMessage, null, null)
					: evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.RELAY_REMMD_REJECTION, originalMessage, DomibusConnectorRejectionReason.OTHER, null);
		} catch (DomibusConnectorEvidencesToolkitException e) {
            throw DomibusConnectorMessageExceptionBuilder.createBuilder()
                    .setMessage(originalMessage)
                    .setText("Error creating RelayREMMD evidence for message!")
                    .setSource(this.getClass())
                    .setCause(e)
                    .build();
		}

		DomibusConnectorAction action = persistenceService.getRelayREMMDAcceptanceRejectionAction();

		sendEvidenceToBackToGateway(originalMessage, action, messageConfirmation);

		if (!isAcceptance) {
			persistenceService.rejectMessage(originalMessage);
		}
	}
	
	private void sendEvidenceToBackToGateway(DomibusConnectorMessage originalMessage, DomibusConnectorAction action,
			DomibusConnectorMessageConfirmation messageConfirmation) throws DomibusConnectorControllerException,
	DomibusConnectorMessageException {

		originalMessage.addConfirmation(messageConfirmation);
		persistenceService.persistEvidenceForMessageIntoDatabase(originalMessage, messageConfirmation.getEvidence(),
				messageConfirmation.getEvidenceType());
		
		
		DomibusConnectorMessageDetails details = new DomibusConnectorMessageDetails();
		details.setRefToMessageId(originalMessage.getMessageDetails().getEbmsMessageId());
		details.setConversationId(originalMessage.getMessageDetails().getConversationId());
		details.setService(originalMessage.getMessageDetails().getService());
		details.setAction(action);
		details.setFromParty(originalMessage.getMessageDetails().getToParty());
		details.setToParty(originalMessage.getMessageDetails().getFromParty());

		DomibusConnectorMessage evidenceMessage = new DomibusConnectorMessage(details, messageConfirmation);
		
        try {
            this.gwSubmissionService.submitToGateway(evidenceMessage);
        } catch (Exception e) {
            throw DomibusConnectorMessageExceptionBuilder.createBuilder()
                    .setMessage(originalMessage)
                    .setText("Exception sending confirmation message '" + originalMessage.getConnectorMessageId() + "' back to gateway ")
                    .setSource(this.getClass())
                    .setCause(e)
                    .build();
        }


        try {
            persistenceService.setEvidenceDeliveredToGateway(originalMessage, messageConfirmation.getEvidenceType());
        } catch (PersistenceException ex) {
            LOGGER.error("Evidence could not persisted", ex);
            //TODO: further exception handling!
        }
	}

}
