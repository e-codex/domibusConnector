package eu.domibus.connector.keystore;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import org.springframework.core.io.Resource;

import java.security.KeyStore;

public interface DCKeyStoreService {
    Resource loadKeyStoreAsResource(StoreConfigurationProperties storeConfigurationProperties);

    KeyStore loadKeyStore(StoreConfigurationProperties storeConfigurationProperties);

    void validateKeyExists(StoreConfigurationProperties storeConfigurationProperties, String alias, String password);

    void validateCertExists(StoreConfigurationProperties storeConfigurationProperties, String alias);

    public static class CannotLoadKeyStoreException extends RuntimeException {
        public CannotLoadKeyStoreException() {
        }

        public CannotLoadKeyStoreException(String message) {
            super(message);
        }

        public CannotLoadKeyStoreException(String message, Throwable cause) {
            super(message, cause);
        }

        public CannotLoadKeyStoreException(Throwable cause) {
            super(cause);
        }

        public CannotLoadKeyStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException() {
        }

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }

        public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
