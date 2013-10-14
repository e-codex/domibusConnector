package eu.ecodex.connector.gwc._1_1;

import javax.activation.DataHandler;

import org.jboss.ws.core.soap.attachment.CIDGenerator;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;
import backend.ecodex.org._1_1.SendResponse;
import eu.ecodex.connector.common.message.Message;
import eu.ecodex.connector.common.message.MessageAttachment;
import eu.ecodex.connector.common.message.MessageConfirmation;
import eu.ecodex.connector.common.message.MessageContent;
import eu.ecodex.connector.gwc.exception.ECodexConnectorGatewayWebserviceClientException;
import eu.ecodex.connector.gwc.util.CommonMessageHelper;

public class SendMessageHelper {

    org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendMessageHelper.class);

    private CommonMessageHelper commonMessageHelper;

    public void setCommonMessageHelper(CommonMessageHelper commonMessageHelper) {
        this.commonMessageHelper = commonMessageHelper;
    }

    public void buildMessage(SendRequest request, Messaging ebMSHeaderInfo, Message message)
            throws ECodexConnectorGatewayWebserviceClientException {
        UserMessage userMessage = commonMessageHelper.buildUserMessage(message);

        buildSendRequestAndPayloadInfo(userMessage, request, message);

        ebMSHeaderInfo.getUserMessage().add(userMessage);
    }

    private void buildSendRequestAndPayloadInfo(UserMessage userMessage, SendRequest request, Message message)
            throws ECodexConnectorGatewayWebserviceClientException {

        MessageContent messageContent = message.getMessageContent();

        if (messageContent != null && messageContent.getECodexContent() != null
                && messageContent.getECodexContent().length > 0) {
            buildBodyPayload(messageContent.getECodexContent(), request, CommonMessageHelper.CONTENT_XML_NAME,
                    userMessage);
        }

        int payloadCounter = 0;

        boolean asicsFound = false;
        if (message.getAttachments() != null) {
            for (MessageAttachment attachment : message.getAttachments()) {
                if (attachment.getAttachment() != null && attachment.getAttachment().length > 0) {
                    buildPayloadAndAddToRequest(request, CommonMessageHelper.APPLICATION_MIME_TYPE, ++payloadCounter,
                            attachment.getAttachment(), attachment.getName(), userMessage);

                    if (attachment.getName().endsWith(".asics")) {
                        asicsFound = true;
                    }
                }
            }
        }

        if (!asicsFound && messageContent != null && messageContent.getPdfDocument() != null
                && messageContent.getPdfDocument().length > 0) {
            buildPayloadAndAddToRequest(request, CommonMessageHelper.APPLICATION_MIME_TYPE, ++payloadCounter,
                    messageContent.getECodexContent(), CommonMessageHelper.CONTENT_PDF_NAME, userMessage);
        }

        if (message.getConfirmations() != null) {
            // Pick only the last produced evidence
            MessageConfirmation messageConfirmation = message.getConfirmations().get(
                    message.getConfirmations().size() - 1);

            if (checkMessageConfirmationValid(messageConfirmation)) {
                if (request.getBodyload() == null) {
                    // must be an evidence message
                    buildBodyPayload(messageConfirmation.getEvidence(), request, messageConfirmation.getEvidenceType()
                            .toString(), userMessage);
                } else {
                    buildPayloadAndAddToRequest(request, CommonMessageHelper.XML_MIME_TYPE, ++payloadCounter,
                            messageConfirmation.getEvidence(), messageConfirmation.getEvidenceType().toString(),
                            userMessage);
                }
            }
        }

        if (request.getPayload().isEmpty()) {
            throw new ECodexConnectorGatewayWebserviceClientException("No payload to send. Message without content?");
        }
    }

    private void buildPayloadAndAddToRequest(SendRequest request, String mimeType, int payloadCounter, byte[] content,
            String name, UserMessage userMessage) {
        CIDGenerator generator = new CIDGenerator();

        String cid = generator.generateFromName("payload_" + payloadCounter);

        PayloadType payload = new PayloadType();

        payload.setContentType(mimeType);
        payload.setHref(cid);
        payload.setValue(content);

        request.getPayload().add(payload);

        commonMessageHelper.addPartInfoToPayloadInfo(name, userMessage, "cid:" + payloadCounter);
    }

    private void buildBodyPayload(byte[] content, SendRequest request, String name, UserMessage userMessage)
            throws ECodexConnectorGatewayWebserviceClientException {
        DataHandler bodyPayload;

        try {
            bodyPayload = commonMessageHelper.buildByteArrayDataHandler(content, CommonMessageHelper.XML_MIME_TYPE);
        } catch (Exception e) {
            throw new ECodexConnectorGatewayWebserviceClientException("Could not build BodyPayload!", e);
        }
        request.setBodyload(bodyPayload);

        commonMessageHelper.addPartInfoToPayloadInfo(name, userMessage, "#bodyPayload");
    }

    private boolean checkMessageConfirmationValid(MessageConfirmation messageConfirmation) {
        return messageConfirmation != null && messageConfirmation.getEvidence() != null
                && messageConfirmation.getEvidence().length > 0 && messageConfirmation.getEvidenceType() != null;
    }

    public void extractEbmsMessageIdAndPersistIntoDB(SendResponse response, Message message) {
        if (response.getMessageID() != null && !response.getMessageID().isEmpty()) {
            String ebmsMessageId = response.getMessageID().get(0);
            commonMessageHelper.persistEbmsMessageIdIntoDatabase(ebmsMessageId, message);
        }
    }

}
