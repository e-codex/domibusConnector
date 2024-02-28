package eu.ecodex.utils.configuration.ui.vaadin.tools;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

import java.lang.annotation.*;

@Qualifier(UiConfigurationPropertyConverter.VALUE)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UiConfigurationPropertyConverter {

    String VALUE = "UiConfigurationPropertyConverter";
}
