package eu.ecodex.utils.configuration.service;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;

import eu.ecodex.utils.configuration.domain.ConfigurationPropertiesBean;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.domain.ConfigurationPropertyNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ConfigurationPropertyCollectorImpl implements ConfigurationPropertyCollector {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCollector.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Validator validator;

    public ConfigurationPropertyChecker getConfigChecker() {
        return new ConfigurationPropertyCheckerImpl( this, validator);
    }

    /**
     * returns a list of all configuration properties
     * each ConfigurationProperty object holds the configuration property key, optional if set a description and label name
     * for further information see {@link ConfigurationProperty}
     *
     * @param basePackageFilter - the provided string is used as a filter, only with {@link ConfigurationProperties} annotated classes
     *                          under this package path are scanned and returned
     * @return a list of ConfigurationProperty objects
     */
    @Override
    public Collection<ConfigurationProperty> getConfigurationProperties(String... basePackageFilter) {
        return getConfigurationProperties(Arrays.asList(basePackageFilter));
    }

    @Override
    public ConfigurationPropertyNode getConfigurationPropertiesHirachie(String... basePackageFilter) {
        return getConfigurationPropertiesHirachie(Arrays.asList(basePackageFilter));
    }

    private ConfigurationPropertyNode getConfigurationPropertiesHirachie(List<String> asList) {
        ConfigurationPropertyNode rootNode = new ConfigurationPropertyNode();
        this.getConfigurationBeans(asList).stream()
                .forEach(clz -> this.getConfigurationPropertyHirachieFromClazz(new ArrayList<>(), rootNode, clz.getBeanClazz()));
        return rootNode;
    }


    @Override
    public Collection<ConfigurationProperty> getConfigurationProperties(Class... basePackageClasses) {
        List<String> collect = Stream.of(basePackageClasses)
                .map(basePackageClass -> basePackageClass.getPackage().getName())
                .collect(Collectors.toList());

        return this.getConfigurationProperties(collect);
    }

    private Collection<ConfigurationProperty> getConfigurationProperties(List<String> basePackageFilter) {
        return getConfigurationPropertiesMap(basePackageFilter).values();
    }

    private Map<String, ConfigurationProperty> getConfigurationPropertiesMap(List<String> basePackageFilter) {
        List<ConfigurationPropertiesBean> configurationBeans = this.getConfigurationBeans(basePackageFilter);

        Map<String, ConfigurationProperty> collect = configurationBeans
                .stream()
                //filter out classes which aren't in package path basePackageFilter
//                .filter(new PackageFilter(basePackageFilter))
                .map(this::processBean)
                .map(List::stream)
                .flatMap(Function.identity())
                .collect(Collectors.toMap(c -> c.getPropertyName(), c -> c));

        return collect;
    }


    private List<ConfigurationProperty> processBean(ConfigurationPropertiesBean entry) {
        LOGGER.trace("processing config bean with name: [{}]", entry.getBeanName());
        Object bean = entry.getBean();
        Class<?> beanClazz = bean.getClass();

//        if (!beanClass.getPackage().getName().startsWith(basePackageFilter)) {
//            LOGGER.debug("ignore bean [{}] because its not in the scanning package path [{}]", beanClass, basePackageFilter);
//            return Stream.empty();
//        }
        return getConfigurationPropertyFromClazz(beanClazz);

    }

    @Override
    public List<ConfigurationProperty> getConfigurationPropertyFromClazz(Class<?> beanClass) {
        List<ConfigurationProperty> configList = new ArrayList<>();
        getConfigurationPropertyHirachieFromClazz(configList,null, beanClass);
        return configList;
    }

    private ConfigurationPropertyNode getConfigurationPropertyHirachieFromClazz(List<ConfigurationProperty> configList, ConfigurationPropertyNode rootNode, Class<?> beanClass) {
        if (!beanClass.isAnnotationPresent(ConfigurationProperties.class)) {
            throw new IllegalArgumentException("Class must be annotated with " + ConfigurationProperties.class);
        }
        ConfigurationProperties configurationProperties = beanClass.getAnnotation(ConfigurationProperties.class);

        if (rootNode == null) {
            rootNode = new ConfigurationPropertyNode();
        }
        String prefix = configurationProperties.prefix();
        ConfigurationPropertyNode currentNode = rootNode;

        String[] split = prefix.split("\\.");
        for (int i = 0; i < split.length; i++) {
            String nodeName = split[i];
            if (currentNode.getChild(nodeName).isPresent()) {
                currentNode = currentNode.getChild(nodeName).get();
            } else {
                ConfigurationPropertyNode newNode = new ConfigurationPropertyNode();
                newNode.setNodeName(nodeName);
                currentNode.addChild(newNode);
                currentNode = newNode;
            }
        }

        processPropertyClazz(configList, currentNode, beanClass);
        return currentNode;
    }


//    private static class StackMember {
//        ConfigurationPropertyNode parentNode;
//        Class<?> configurationClass;
//        Field field;
//    }

    private void processPropertyClazz(List<ConfigurationProperty> configList, ConfigurationPropertyNode parent, Class<?> configurationClass) {
        List<Field> fields = collectDeclaredFields(configurationClass, new ArrayList<>()); //.getDeclaredFields(); //TODO: also scan inherited fields...

        fields.stream()
                .filter(field -> {
                    PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(configurationClass, field.getName());
                    if (propertyDescriptor != null) {
                        return propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null;
                    }
                    return false;
                })
                .forEach( field -> this.processFieldOfBean(configList, parent, configurationClass, field));
    }

    /**
     * walks trough parent classes
     * and collects all declared fields
     * @param configurationClass - the class walk through
     * @return List of declared fields
     */
    private List<Field> collectDeclaredFields(Class<?> configurationClass, List<Field> fields) {
        if (configurationClass != null) {
            fields.addAll(Arrays.asList(configurationClass.getDeclaredFields()));
            collectDeclaredFields(configurationClass.getSuperclass(), fields);
        }
        return fields;
    }

    private void processFieldOfBean(List<ConfigurationProperty> configList, ConfigurationPropertyNode parent, Class parentClass, Field field) {
        LOGGER.trace("processing field [{}]", field);
        ConfigurationPropertyNode node;
        node = new ConfigurationPropertyNode();
        node.setNodeName(field.getName());
        parent.addChild(node);

        if (null != AnnotationUtils.getAnnotation(field, NestedConfigurationProperty.class)) {
            processPropertyClazz(configList, node, field.getType());
        } else {

            ConfigurationProperty c = new ConfigurationProperty();
//            c.setPropertyName(parent.getFullNodePath() + field.getName());
            c.setPropertyName(node.getFullNodePath());
            c.setBeanPropertyName(field.getName());
            c.setParentClass(parentClass);
            configList.add(c);
            node.setProperty(c);

            ConfigurationDescription descriptionAnnotation = AnnotationUtils.getAnnotation(field, ConfigurationDescription.class);
            if (descriptionAnnotation != null) {
                String description = (String) AnnotationUtils.getValue(descriptionAnnotation, "description");
                c.setDescription(description);
            }

            ConfigurationLabel configLabelAnnotation = AnnotationUtils.getAnnotation(field, ConfigurationLabel.class);
            if (configLabelAnnotation != null) {
                String label = (String) AnnotationUtils.getValue(configLabelAnnotation);
                c.setLabel(label);
            }

            DefaultValue defaultValueAnnotation = AnnotationUtils.getAnnotation(field, DefaultValue.class);
            if (defaultValueAnnotation != null) {
                c.setDefaultValue(defaultValueAnnotation.value());
            }

            c.setType(field.getType());



        }

    }

    @Override
    public List<ConfigurationPropertiesBean> getConfigurationBeans(List<String> basePackageFilter) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigurationProperties.class));

        //all @ConfigurationProperties bean definitions
        List<BeanDefinition> collect = basePackageFilter.stream().map(basePackage -> scanner.findCandidateComponents(basePackage))
                .flatMap(beanDefinitions -> beanDefinitions.stream())
                .collect(Collectors.toList());

        //TODO: check if active! @Profile + @ConditionalOnProperty



        Map<String, Object> configurationBeans = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
        List<ConfigurationPropertiesBean> configurationBeansList = configurationBeans.entrySet().stream()
                .filter(new PackageFilter(basePackageFilter))
                .map(this::processConfigurationPropertiesBean)
                .collect(Collectors.toList());

        return configurationBeansList;
    }

    private ConfigurationPropertiesBean processConfigurationPropertiesBean(Map.Entry<String, Object> entry) {
        ConfigurationPropertiesBean c = new ConfigurationPropertiesBean();

        Object bean = entry.getValue();
        Class<?> beanClass = bean.getClass();

        c.setBeanName(entry.getKey());
        c.setBeanClazz(beanClass);
        c.setBean(bean);

        ConfigurationProperties configurationProperties = beanClass.getAnnotation(ConfigurationProperties.class);
        String pPrefix = configurationProperties.prefix();
        c.setPropertyPrefix(pPrefix);

        ConfigurationLabel configLabelAnnotation = beanClass.getAnnotation(ConfigurationLabel.class);
        if (configLabelAnnotation != null) {
            String label = (String) AnnotationUtils.getValue(configLabelAnnotation);
            c.setLabel(label);
        }

        ConfigurationDescription descriptionAnnotation = beanClass.getAnnotation(ConfigurationDescription.class);
        if (descriptionAnnotation != null) {
            String description = (String) AnnotationUtils.getValue(descriptionAnnotation);
            c.setDescription(description);
        }


        return c;
    }


}
