package eu.ecodex.utils.configuration.testdata.subpackage1;



import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



@Component
@ConfigurationProperties(prefix="example.configuration")
@ConfigurationLabel("Example Configuration")
@ConfigurationDescription("Properties for the example configuration")
public class ExamplePropertiesConfig {

    @NotNull
    @ConfigurationLabel("A text")
    private String text;


    /**
     * a number which should be max 60
     */
    @NotNull
    @ConfigurationDescription("A number.........")
    @Max(60)
    private Integer number;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
