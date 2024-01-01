package eu.ecodex.utils.configuration.example1;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import eu.ecodex.utils.configuration.example1.validators.CompareStrings;
import eu.ecodex.utils.configuration.example1.validators.StringComparisonMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;

@Component
@ConfigurationProperties(prefix = "com.example1")
@CompareStrings(propertyNames = {"password1", "password2"}, matchMode = StringComparisonMode.EQUAL, allowNull = true)
public class Example1ConfigurationProperties {

    @ConfigurationLabel("Property1")
    @ConfigurationDescription("I am a good long description of this property!")
    String property1;


    @ConfigurationLabel("Property 2")
    @ConfigurationDescription("A 2nd property")
    String property2;


    @ConfigurationLabel("Password1")
    @ConfigurationDescription("A 2nd property")
    String password1;

    @ConfigurationLabel("Password2")
    @ConfigurationDescription("A 2nd property")
    String password2;

    @ConfigurationLabel("maxProperty")
    @ConfigurationDescription("A property")
    @Max(value = 200)
    int maxProperty;


    @ConfigurationLabel("minProperty")
    @ConfigurationDescription("A minProperty")
    @Min(value = 200)
    int minProperty;

    @ConfigurationLabel("A Path")
    @ConfigurationDescription("Path to anything")
    @NotNull
    Path thePath;

    @NestedConfigurationProperty
    NestedProperty subsetting = new NestedProperty();

    @NestedConfigurationProperty
    NestedBasePropertyInherited subset2 = new NestedBasePropertyInherited();

    public NestedProperty getSubsetting() {
        return subsetting;
    }

    public void setSubsetting(NestedProperty subsetting) {
        this.subsetting = subsetting;
    }

    public Path getThePath() {
        return thePath;
    }

    public void setThePath(Path thePath) {
        this.thePath = thePath;
    }

    public String getProperty1() {
        return property1;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public int getMaxProperty() {
        return maxProperty;
    }

    public void setMaxProperty(int maxProperty) {
        this.maxProperty = maxProperty;
    }

    public int getMinProperty() {
        return minProperty;
    }

    public void setMinProperty(int minProperty) {
        this.minProperty = minProperty;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public NestedBasePropertyInherited getSubset2() {
        return subset2;
    }

    public void setSubset2(NestedBasePropertyInherited subset2) {
        this.subset2 = subset2;
    }
}
