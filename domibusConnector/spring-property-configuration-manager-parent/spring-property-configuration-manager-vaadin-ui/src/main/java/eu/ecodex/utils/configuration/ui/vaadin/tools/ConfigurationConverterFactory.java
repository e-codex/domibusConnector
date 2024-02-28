package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.converter.Converter;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;

public interface ConfigurationConverterFactory {

    boolean canConvert(Class clazz);

    Converter createConverter(ConfigurationProperty configurationProperty);

}
