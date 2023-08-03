package eu.domibus.connector.dss.configuration.validation;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

public class ValidEtisValidationPolicyXmlValidator implements ConstraintValidator<ValidEtsiValidationPolicyXml, String> {

    private static final Logger LOGGER = LogManager.getLogger(ValidEtisValidationPolicyXmlValidator.class);

    private final EtsiXmlPolicyValidator etsiXmlPolicyValidator;

    public ValidEtisValidationPolicyXmlValidator(EtsiXmlPolicyValidator etsiXmlPolicyValidator) {
        this.etsiXmlPolicyValidator = etsiXmlPolicyValidator;
    }


    @Override
    public void initialize(ValidEtsiValidationPolicyXml constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return etsiXmlPolicyValidator.validate(value);
    }
}
