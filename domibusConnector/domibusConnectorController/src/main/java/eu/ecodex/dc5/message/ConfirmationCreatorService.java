package eu.ecodex.dc5.message;

import eu.domibus.connector.common.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.exception.DomibusConnectorControllerException;
import eu.domibus.connector.controller.exception.ErrorCode;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.domibus.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.ecodex.dc5.message.model.DC5Action;
import eu.ecodex.dc5.message.model.DC5Message;
import eu.ecodex.dc5.message.model.DC5Confirmation;
import lombok.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConfirmationCreatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationCreatorService.class);

    private final DomibusConnectorEvidencesToolkit evidencesToolkit;
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;

    @AllArgsConstructor
    @Builder
    @Getter
    public static class CreateConfirmationRequest {
        @NonNull
        private final DomibusConnectorEvidenceType evidenceType;
        @NonNull
        private final DC5Message businessMsg;
        private final DomibusConnectorRejectionReason reason;
        private final String details;
    }

    public DC5Action createEvidenceAction(DomibusConnectorEvidenceType type) throws DomibusConnectorControllerException {

        EvidenceActionServiceConfigurationProperties evidenceActionServiceConfigurationProperties =
                configurationPropertyLoaderService.loadConfiguration(DC5BusinessDomain.getDefaultBusinessDomainId(), EvidenceActionServiceConfigurationProperties.class);

        switch (type) {
            case DELIVERY:
                return evidenceActionServiceConfigurationProperties
                        .getDelivery().getConnectorAction();
            case NON_DELIVERY:
                return evidenceActionServiceConfigurationProperties
                        .getNonDelivery().getConnectorAction();
            case RETRIEVAL:
                return evidenceActionServiceConfigurationProperties
                        .getRetrieval().getConnectorAction();
            case NON_RETRIEVAL:
                return evidenceActionServiceConfigurationProperties
                        .getNonRetrieval().getConnectorAction();
            case RELAY_REMMD_FAILURE:
                return evidenceActionServiceConfigurationProperties
                        .getRelayREMMDFailure().getConnectorAction();
            case RELAY_REMMD_REJECTION:
                return evidenceActionServiceConfigurationProperties
                        .getRelayREEMDRejection().getConnectorAction();
            case RELAY_REMMD_ACCEPTANCE:
                return evidenceActionServiceConfigurationProperties
                        .getRelayREEMDAcceptance().getConnectorAction();
            case SUBMISSION_ACCEPTANCE:
                return evidenceActionServiceConfigurationProperties
                        .getSubmissionAcceptance().getConnectorAction();
            case SUBMISSION_REJECTION:
                return evidenceActionServiceConfigurationProperties
                        .getSubmissionRejection().getConnectorAction();
            default:
                throw new DomibusConnectorControllerException(ErrorCode.OTHER,"Illegal Evidence type " + type + "! No Action found!");
        }
    }

    public DC5Confirmation createConfirmation(CreateConfirmationRequest request) throws DomibusConnectorEvidencesToolkitException {
        return toDC5Confirmation(evidencesToolkit.createEvidence(request.getEvidenceType(),
                toMessageParams(request.getBusinessMsg()),
                request.getReason(),
                request.getDetails()));
    }

    public DC5Confirmation createConfirmation(DomibusConnectorEvidenceType evidenceType, DC5Message businessMsg, DomibusConnectorRejectionReason reason, String details) throws DomibusConnectorEvidencesToolkitException {
        return toDC5Confirmation(evidencesToolkit.createEvidence(evidenceType, toMessageParams(businessMsg), reason, details));
    }

    public DC5Confirmation createNonDelivery(DC5Message originalMessage, DomibusConnectorRejectionReason deliveryEvidenceTimeout) throws DomibusConnectorEvidencesToolkitException {
        return toDC5Confirmation(evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.NON_DELIVERY, toMessageParams(originalMessage), deliveryEvidenceTimeout, deliveryEvidenceTimeout.getReasonText()));
    }

    public DC5Confirmation createNonRetrieval(DC5Message originalMessage, DomibusConnectorRejectionReason deliveryEvidenceTimeout) throws DomibusConnectorEvidencesToolkitException {
        return toDC5Confirmation(evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.NON_RETRIEVAL, toMessageParams(originalMessage), deliveryEvidenceTimeout, deliveryEvidenceTimeout.getReasonText()));
    }

    public DC5Confirmation createRelayRemmdFailure(DC5Message originalMessage, DomibusConnectorRejectionReason deliveryEvidenceTimeout) throws DomibusConnectorEvidencesToolkitException {
        return toDC5Confirmation(evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.RELAY_REMMD_FAILURE, toMessageParams(originalMessage), deliveryEvidenceTimeout, deliveryEvidenceTimeout.getReasonText()));
    }

    public static DC5Confirmation toDC5Confirmation(DomibusConnectorEvidencesToolkit.Evidence evidence) {
        return DC5Confirmation.builder()
                .evidence(evidence.getEvidence())
                .evidenceType(evidence.getType())
                .build();
    }

    static DomibusConnectorEvidencesToolkit.MessageParameters toMessageParams(DC5Message originalMessage) {
        DomibusConnectorEvidencesToolkit.MessageParameters.MessageParametersBuilder builder = DomibusConnectorEvidencesToolkit.MessageParameters.builder();
        if (originalMessage.getEbmsData() != null && originalMessage.getEbmsData().getEbmsMessageId() != null) {
            builder = builder.ebmsMessageId(originalMessage.getEbmsData().getEbmsMessageId().getEbmsMesssageId());
        }
        if (originalMessage.getEbmsData() != null) {
            builder.senderAddress(originalMessage.getEbmsData().getSender(originalMessage.getTarget()).getOriginalSender())
                    .recipientAddress(originalMessage.getEbmsData().getReceiver(originalMessage.getTarget()).getFinalRecipient());
        }
        if (originalMessage.getBackendData() != null && originalMessage.getBackendData().getBackendMessageId() != null) {
            builder = builder.nationalMessageId(originalMessage.getBackendData().getBackendMessageId().toString());
        }
        if (originalMessage.getMessageContent() != null && originalMessage.getMessageContent().getBusinessContent() != null) {
             builder = builder.businessDocumentHash(DomibusConnectorEvidencesToolkit.HashValue.builder()
                        .hash(originalMessage.getMessageContent().getBusinessContent().getBusinessDocument().getDigest().getDigestValue())
                        .algorithm(originalMessage.getMessageContent().getBusinessContent().getBusinessDocument().getDigest().getDigestAlgorithm())
                     .build());
        }
        if (originalMessage.getMessageContent() != null) {
            builder.relatedEvidences(originalMessage.getMessageContent().getRelatedConfirmations()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(c ->
                        DomibusConnectorEvidencesToolkit.Evidence.builder()
                                .type(c.getEvidenceType())
                                .evidence(c.getEvidence())
                                .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }


}
