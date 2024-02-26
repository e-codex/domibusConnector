package eu.ecodex.utils.configuration.service;

import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;

import java.util.Collection;
import java.util.List;

public interface ConfigurationPropertyChecker {

    /**
     *
     * @param configurationPropertySource - the property source which provides the properties to check
     * @param basePackageFilter - the filter under which all with with @see {@link org.springframework.boot.context.properties.ConfigurationProperties}
     *                          annotated Properties are bound and checked within this binding
     * @throws org.springframework.boot.context.properties.bind.BindException in case of an failure during binding
     * @return - a list of validation errors
     */
    List<ValidationErrors> validateConfiguration(ConfigurationPropertySource configurationPropertySource, String... basePackageFilter);

    List<ValidationErrors> validateConfiguration(ConfigurationPropertySource configurationPropertySource, Collection<Class> configurationClasses);

}
