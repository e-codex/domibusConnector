package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.binder.Binder;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.ui.vaadin.tools.configforms.ConfigurationFormsFactoryImpl;

import java.util.Map;
import java.util.Properties;

public interface ConfigurationFormsFactory {

    AbstractField createField(ConfigurationProperty configurationProperty, Binder<Map<String, String>> binder);

    ConfigurationFormsFactoryImpl.ConfigurationPropertyForm createFormFromConfigurationPropertiesClass(Class example1ConfigurationPropertiesClass);
}
