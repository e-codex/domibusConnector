package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;

import jakarta.validation.constraints.NotNull;

public class SignatureConfigurationProperties extends KeyAndKeyStoreConfigurationProperties {

    //connector.ecodex-container.signature.encryption-algorithm=blabla

    //connector.business-domain[domain1].properties.connector.ecodex-container.signature.encryption-algorithm=12345


    @NotNull
    EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.RSA;

    @NotNull
    DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA256;

    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }
}
