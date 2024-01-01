package eu.domibus.connector.dss.configuration.validation;

import eu.europa.esig.dss.policy.ValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Component
@Log4j2
public class EtsiXmlPolicyValidatorImpl implements EtsiXmlPolicyValidator {

    private final ApplicationContext applicationContext;

    @Override
    public boolean validate(String value) {
        try {
            Resource resource = applicationContext.getResource(value);
            InputStream policyDataStream = resource.getInputStream();
            ValidationPolicy validationPolicy = null;
            validationPolicy = ValidationPolicyFacade.newFacade().getValidationPolicy(policyDataStream);
            return true;
        } catch (IOException ioe) {
            log.warn("Error while loading resource", ioe);
            return false;
        } catch (XMLStreamException | jakarta.xml.bind.JAXBException | SAXException e) {
//            e.printStackTrace();
            //TODO: error...
            log.warn("Parsing error during validation", e);
            return false;
        }
    }
}
