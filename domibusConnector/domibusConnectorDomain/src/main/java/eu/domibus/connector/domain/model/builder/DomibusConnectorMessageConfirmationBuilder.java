package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;

public final class DomibusConnectorMessageConfirmationBuilder {

    public static DomibusConnectorMessageConfirmationBuilder createBuilder() { return new DomibusConnectorMessageConfirmationBuilder(); };

    private DomibusConnectorEvidenceType evidenceType;
    private byte evidence[];

    private DomibusConnectorMessageConfirmationBuilder() {}


    public DomibusConnectorMessageConfirmationBuilder setEvidenceType(DomibusConnectorEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
        return this;
    }

    public DomibusConnectorMessageConfirmationBuilder setEvidence(byte[] evidence) {
        this.evidence = evidence;
        return this;
    }

    public DomibusConnectorMessageConfirmation build() {
        if (evidence == null) {
            throw new IllegalArgumentException("Evidence is not allowed to be null!");
        }
        if (evidenceType == null) {
            throw new IllegalArgumentException("Evidence type must be set!");
        }
        return new DomibusConnectorMessageConfirmation(evidenceType, evidence);
    }
}
