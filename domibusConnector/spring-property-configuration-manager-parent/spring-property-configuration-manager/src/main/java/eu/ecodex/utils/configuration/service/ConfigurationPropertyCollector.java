package eu.ecodex.utils.configuration.service;


import eu.ecodex.utils.configuration.domain.ConfigurationPropertiesBean;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.domain.ConfigurationPropertyNode;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;

import java.util.Collection;
import java.util.List;

public interface ConfigurationPropertyCollector {

    /**
     * Returns a list of all Properties (within with {@link org.springframework.boot.context.properties.ConfigurationProperties} annotated Classes)
     * in the provided basePackage path
     * @param basePackage
     * @return the list of Properties
     */
    Collection<ConfigurationProperty> getConfigurationProperties(String... basePackage);

    ConfigurationPropertyNode getConfigurationPropertiesHirachie(String... basePackage);

    Collection<ConfigurationProperty> getConfigurationProperties(Class... basePackageClasses);

    Collection<ConfigurationProperty> getConfigurationPropertyFromClazz(Class<?> beanClass);

    Collection<ConfigurationPropertiesBean> getConfigurationBeans(List<String> basePackageFilter);
}
