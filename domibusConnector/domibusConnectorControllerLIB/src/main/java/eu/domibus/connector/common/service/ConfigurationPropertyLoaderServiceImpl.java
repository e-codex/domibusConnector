package eu.domibus.connector.common.service;

import com.google.common.base.CaseFormat;
import eu.domibus.connector.common.annotations.ConnectorConversationService;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class ConfigurationPropertyLoaderServiceImpl implements ConfigurationPropertyManagerService {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyLoaderServiceImpl.class);


    private final ApplicationContext ctx;
    private final ConversionService conversionService;
    private final DCBusinessDomainManager businessDomainManager;
//    private final ConfigurationPropertyCollector configurationPropertyCollector;
    private final Validator validator;
    private final BeanToPropertyMapConverter beanToPropertyMapConverter;



    public ConfigurationPropertyLoaderServiceImpl(ApplicationContext ctx,
                                                  @ConnectorConversationService ConversionService conversionService,
                                                  DCBusinessDomainManager businessDomainManager,
//                                                  ConfigurationPropertyCollector configurationPropertyCollector,
                                                  Validator validator, BeanToPropertyMapConverter beanToPropertyMapConverter) {
        this.ctx = ctx;
        this.conversionService = conversionService;
        this.businessDomainManager = businessDomainManager;
//        this.configurationPropertyCollector = configurationPropertyCollector;
        this.validator = validator;
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
    }

    @Override
    public <T> T loadConfiguration(DomibusConnectorBusinessDomain.BusinessDomainId laneId, Class<T> clazz) {
        String prefix = getPrefixFromAnnotation(clazz);

        return this.loadConfiguration(laneId, clazz, prefix);
    }

    private String getPrefixFromAnnotation(Class<?> clazz) {
        if (!AnnotatedElementUtils.hasAnnotation(clazz, ConfigurationProperties.class)) {
            throw new IllegalArgumentException("clazz must be annotated with " + ConfigurationProperties.class);
        }
        LOGGER.debug("Loading property class [{}]", clazz);

        ConfigurationProperties annotation = clazz.getAnnotation(ConfigurationProperties.class);
        String prefix = annotation.prefix();
        return prefix;
    }

    public <T> T loadConfiguration(@Nullable DomibusConnectorBusinessDomain.BusinessDomainId laneId, Class<T> clazz, String prefix) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz is not allowed to be null!");
        }
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("Prefix is not allowed to be null!");
        }
        LOGGER.debug("Loading property class [{}]", clazz);

        MapConfigurationPropertySource messageLaneSource = loadLaneProperties(laneId);

        Environment environment = ctx.getEnvironment();
        Iterable<ConfigurationPropertySource> environmentSource = ConfigurationPropertySources.get(environment);
        List<ConfigurationPropertySource> configSources = new ArrayList<>();
        configSources.add(messageLaneSource);
        environmentSource.forEach(s -> configSources.add(s));

        PropertySourcesPlaceholdersResolver placeholdersResolver = new PropertySourcesPlaceholdersResolver(environment);

        Binder binder = new Binder(configSources, placeholdersResolver, conversionService, null);

        Bindable<T> bindable = Bindable.of(clazz);
        T t = binder.bindOrCreate(prefix, bindable);

        return t;
    }


    private MapConfigurationPropertySource loadLaneProperties(DomibusConnectorBusinessDomain.BusinessDomainId laneId) {
        Optional<DomibusConnectorBusinessDomain> businessDomain = businessDomainManager.getBusinessDomain(laneId);
        if (businessDomain.isPresent()) {
            MapConfigurationPropertySource mapConfigurationPropertySource = new MapConfigurationPropertySource(businessDomain.get().getMessageLaneProperties());
            return mapConfigurationPropertySource;
        } else {
            throw new IllegalArgumentException(String.format("No active business domain for id [%s]", laneId));
        }
    }


    @Override
    public <T> Set<ConstraintViolation<T>> validateConfiguration(DomibusConnectorBusinessDomain.BusinessDomainId laneId, T updatedConfigClazz) {
        if (laneId == null) {
            throw new IllegalArgumentException("LaneId is not allowed to be null!");
        }
        return validator.validate(updatedConfigClazz);
    }

    /**
     *
     * A {@link BusinessDomainConfigurationChange} event is fired with the changed properties
     * and affected BusinessDomain
     * So factories, Scopes can react to this event and refresh the settings
     *
     * @param laneId the laneId, if null defaultLaneId is used
     * @param updatedConfigClazz - the configurationClazz which has been altered, updated
     *                           only the changed properties are updated at the configuration source
     *
     *
     */
    @Override
    public void updateConfiguration(DomibusConnectorBusinessDomain.BusinessDomainId laneId, Object updatedConfigClazz) {
        if (laneId == null) {
            throw new IllegalArgumentException("LaneId is not allowed to be null!");
        }

        Object currentConfig = this.loadConfiguration(laneId, updatedConfigClazz.getClass());
        Map<String, String> previousProps = createPropertyMap(currentConfig); //collect current active properties
        Map<String, String> props = createPropertyMap(updatedConfigClazz); //collect updated properties

        //only collect differences
        Map<String, String> diffProps = new HashMap<>();
        props.entrySet().stream()
                .filter(entry -> !Objects.equals(previousProps.get(entry.getKey()), entry.getValue()))
                .forEach(e -> diffProps.put(e.getKey().toString(), e.getValue()));

        LOGGER.debug("Updating of [{}] the following properties [{}]", updatedConfigClazz.getClass(), diffProps);

        businessDomainManager.updateConfig(laneId, diffProps);
        ctx.publishEvent(new BusinessDomainConfigurationChange(this, laneId, diffProps));
    }

    Map<String, String> createPropertyMap(Object configurationClazz) {
        String prefix = getPrefixFromAnnotation(configurationClazz.getClass());
        return beanToPropertyMapConverter.readBeanPropertiesToMap(configurationClazz, prefix);
    }

}
