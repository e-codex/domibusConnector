package eu.ecodex.utils.configuration.testdata.subpackage2;

import eu.ecodex.utils.configuration.testdata.subpackage1.ExamplePropertiesConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.example.dc")
@Data
public class Prop {

    private String abc;

    private String dgf;

    @NestedConfigurationProperty
    private ExamplePropertiesConfig examplePropertiesConfig = new ExamplePropertiesConfig();

}
