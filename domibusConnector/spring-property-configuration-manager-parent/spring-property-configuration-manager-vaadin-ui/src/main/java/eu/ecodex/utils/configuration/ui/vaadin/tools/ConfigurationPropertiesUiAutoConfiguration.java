package eu.ecodex.utils.configuration.ui.vaadin.tools;

import eu.ecodex.utils.configuration.ui.vaadin.tools.configfield.DefaultTextFieldFactory;
import eu.ecodex.utils.configuration.ui.vaadin.tools.configforms.ConfigurationFormsFactoryImpl;
import eu.ecodex.utils.configuration.ui.vaadin.tools.views.ListConfigurationPropertiesComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ComponentScan(basePackageClasses = {ListConfigurationPropertiesComponent.class, DefaultTextFieldFactory.class, ConfigurationFormsFactoryImpl.class})
public class ConfigurationPropertiesUiAutoConfiguration {


    @Autowired(required = false)
    @Qualifier(ConfigurationPropertiesBinding.VALUE)
    private Set<Converter<?, ?>> converters = new HashSet<>();

    @Autowired(required = false)
    @Qualifier(UiConfigurationPropertyConverter.VALUE)
    private Set<Converter<?, ?>> uiConfigurationConverters = new HashSet<>();

    @Bean
    @UiConfigurationConversationService
    public ConversionService configurationPropertyUiConversionService() {
        Set<Converter<?, ?>> mergedConverters = Stream.of(uiConfigurationConverters.stream(), converters.stream())
                .flatMap(Function.identity())
                .distinct()
                .collect(Collectors.toSet());

        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(mergedConverters);
        bean.afterPropertiesSet();
        ConversionService object = bean.getObject();
        return object;
    }

}
