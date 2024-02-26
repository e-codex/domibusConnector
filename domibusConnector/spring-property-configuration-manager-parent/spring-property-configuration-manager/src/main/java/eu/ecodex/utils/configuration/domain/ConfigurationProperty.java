package eu.ecodex.utils.configuration.domain;

import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

public class ConfigurationProperty {

    //TODO: replace with org.springframework.boot.context.properties.source.ConfigurationPropertyName
    private String propertyName;

    private String beanPropertyName;

    private String description;

    private String label;

    private Class type;

    /**
     * Holds the with org.springframework.boot.context.properties.bind.DefaultValue
     * annotated default value
     */
    private String[] defaultValue;

    /**
     * The class this property is part of
     */
    @Nullable
    private Class parentClass;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getBeanPropertyName() {
        return beanPropertyName;
    }

    public void setBeanPropertyName(String beanPropertyName) {
        this.beanPropertyName = beanPropertyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String[] getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String[] defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String toString() {
        return new ToStringCreator(this)
                .append("propertyName", propertyName)
                .append("label", label)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationProperty)) return false;

        ConfigurationProperty that = (ConfigurationProperty) o;

        return propertyName != null ? propertyName.equals(that.propertyName) : that.propertyName == null;
    }

    @Override
    public int hashCode() {
        return propertyName != null ? propertyName.hashCode() : 0;
    }

    public Class getParentClass() {
        return parentClass;
    }

    public void setParentClass(Class parentClass) {
        this.parentClass = parentClass;
    }
}
