package eu.domibus.connector.common;

import eu.domibus.connector.common.annotations.ConnectorConversationService;
import eu.domibus.connector.common.service.*;
import eu.domibus.connector.domain.model.DC5BusinessDomain;
import eu.ecodex.dc5.domain.BusinessDomainConfigurationChange;
import eu.ecodex.dc5.domain.DCBusinessDomainManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.*;

@Service
public class ConfigurationPropertyManagerServiceImpl implements ConfigurationPropertyManagerService {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyManagerServiceImpl.class);


    private final ApplicationContext ctx;
    private final ConversionService conversionService;
    private final DCBusinessDomainManager businessDomainManager;
    private final Validator validator;
    private final BeanToPropertyMapConverter beanToPropertyMapConverter;
    private final PropertyMapToBeanConverter propertyMapToBeanConverter;


    public ConfigurationPropertyManagerServiceImpl(ApplicationContext ctx,
                                                   @ConnectorConversationService ConversionService conversionService,
                                                   DCBusinessDomainManager businessDomainManager,
                                                   PropertyMapToBeanConverter propertyMapToBeanConverter,
                                                   Validator validator,
                                                   BeanToPropertyMapConverter beanToPropertyMapConverter) {
        this.ctx = ctx;
        this.conversionService = conversionService;
        this.businessDomainManager = businessDomainManager;
        this.validator = validator;
        this.propertyMapToBeanConverter = propertyMapToBeanConverter;
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
    }

    @Override
    public <T> T loadConfiguration(DC5BusinessDomain.BusinessDomainId laneId, Class<T> clazz) {
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

    /**
     * Binds a class to the configuration properties loaded
     * from the message lane and the spring environment
     *
     * @param laneId - the lane id
     * @param clazz - the clazz to init
     * @param prefix - the prefix for the properties
     * @param <T> a class
     * @return the configuration object
     */
    public <T> T loadConfiguration(@Nullable DC5BusinessDomain.BusinessDomainId laneId, Class<T> clazz, String prefix) {
        Objects.requireNonNull(laneId);
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

    @Override
    public <T> T loadConfigurationOnlyFromMap(Map<String, String> map, Class<T> clazz, String prefix) {
        return propertyMapToBeanConverter.loadConfigurationOnlyFromMap(map, clazz, prefix);
    }

    @Override
    public <T> T loadConfigurationOnlyFromMap(Map<String, String> map, Class<T> clazz) {
        String prefix = getPrefixFromAnnotation(clazz);
        return propertyMapToBeanConverter.loadConfigurationOnlyFromMap(map, clazz, prefix);
    }


    private MapConfigurationPropertySource loadLaneProperties(DC5BusinessDomain.BusinessDomainId laneId) {
        Optional<DC5BusinessDomain> businessDomain = businessDomainManager.getDomain(laneId);
        if (businessDomain.isPresent()) {
            MapConfigurationPropertySource mapConfigurationPropertySource = new MapConfigurationPropertySource(businessDomain.get().getProperties());
            return mapConfigurationPropertySource;
        } else {
            throw new IllegalArgumentException(String.format("No active business domain for id [%s]", laneId));
        }
    }


    @Override
    public <T> Set<ConstraintViolation<T>> validateConfiguration(T updatedConfigClazz) {
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
    public void updateConfiguration(DC5BusinessDomain.BusinessDomainId laneId, Object updatedConfigClazz) {
        final ArrayList<Object> updatedClazzes = new ArrayList<>();
        updatedClazzes.add(updatedConfigClazz);
        Map<String, String> diffProps = getUpdatedConfiguration(laneId, updatedClazzes);
        updateConfiguration(laneId, updatedConfigClazz.getClass(), diffProps);
    }

    @Override
    public void updateConfiguration(DC5BusinessDomain.BusinessDomainId laneId, Class<?> updatedConfigClazz, Map<String, String> diffProps) {
        LOGGER.debug("Updating of [{}] the following properties [{}]", updatedConfigClazz, diffProps);

        businessDomainManager.updateConfig(laneId, diffProps);
        ctx.publishEvent(new BusinessDomainConfigurationChange(this, laneId, diffProps));
    }

    @Override
    public Map<String, String> getUpdatedConfiguration(DC5BusinessDomain.BusinessDomainId laneId, List<Object> updatedConfigClazzes) {
        if (laneId == null) {
            throw new IllegalArgumentException("LaneId is not allowed to be null!");
        }

        Map<String, String> diffProps = new HashMap<>();

        for (Object o : updatedConfigClazzes) {
            Object currentConfig = this.loadConfiguration(laneId, o.getClass());
            Map<String, String> previousProps = createPropertyMap(currentConfig); //collect current active properties
            Map<String, String> props = createPropertyMap(o); //collect updated properties
            //only collect differences
            props.entrySet().stream()
                    .filter(entry -> !Objects.equals(previousProps.get(entry.getKey()), entry.getValue()))
                    .forEach(e -> diffProps.put(e.getKey().toString(), e.getValue()));
        }

        return diffProps;
    }

    Map<String, String> createPropertyMap(Object configurationClazz) {
        String prefix = getPrefixFromAnnotation(configurationClazz.getClass());
        return beanToPropertyMapConverter.readBeanPropertiesToMap(configurationClazz, prefix);
    }

}
