package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;


import jakarta.validation.constraints.NotNull;

public class SignatureConfigurationProperties extends KeyAndKeyStoreConfigurationProperties {

    @NotNull
    String encryptionAlgorithm = "RSA";

    @NotNull
    String digestAlgorithm = "SHA256";

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }
}
