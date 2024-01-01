package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;

import javax.validation.*;
import java.util.HashSet;
import java.util.Set;

public class KeyFromKeyStoreLoadableValidator  implements ConstraintValidator<CheckKeyIsLoadableFromKeyStore, KeyAndKeyStoreConfigurationProperties> {

    private Validator validator;

    @Override
    public void initialize(CheckKeyIsLoadableFromKeyStore constraintAnnotation) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public boolean isValid(KeyAndKeyStoreConfigurationProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validations = new HashSet<>();
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path = validator.validateProperty(value, "privateKey");
        validations.addAll(path);
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> keyStoreValidation = validator.validateProperty(value, "keyStore");
        validations.addAll(keyStoreValidation);
        if (!validations.isEmpty()) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        return HelperMethods.checkKeyIsLoadable(context, value.getKeyStore(), value.getPrivateKey());
    }





}
