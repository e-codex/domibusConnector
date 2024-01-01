package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.binder.Validator;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;

public interface ConfigurationValidatorFactory {

    boolean canHandle(Class clazz);

    Validator createValidator(ConfigurationProperty configurationProperty);

}
