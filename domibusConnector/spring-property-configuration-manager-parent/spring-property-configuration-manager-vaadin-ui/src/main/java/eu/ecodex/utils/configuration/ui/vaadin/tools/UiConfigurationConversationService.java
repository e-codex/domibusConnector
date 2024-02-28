package eu.ecodex.utils.configuration.ui.vaadin.tools;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Qualifier(UiConfigurationConversationService.CONVERSION_SERVICE_QUALIFIER)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UiConfigurationConversationService {

    String CONVERSION_SERVICE_QUALIFIER = "CONFIGURATION_PROPERTY_UI_CONVERSION_SERVICE";
}
