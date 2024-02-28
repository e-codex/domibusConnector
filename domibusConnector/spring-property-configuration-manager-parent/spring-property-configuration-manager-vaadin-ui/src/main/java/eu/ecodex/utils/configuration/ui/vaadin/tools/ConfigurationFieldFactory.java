package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;

import java.util.Map;

public interface ConfigurationFieldFactory {

    boolean canHandle(Class clazz);

    public AbstractField createField(ConfigurationProperty configurationProperty, Binder<Map<String, String>> binder);

}
