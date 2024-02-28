package eu.ecodex.utils.configuration.example1;


import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.Data;

@ConfigurationDescription("A extended property class")
@Data
public class NestedBasePropertyInherited extends NestedBaseProperty {

    @ConfigurationLabel("prop1 label")
    private String prop1;

    @ConfigurationLabel("prop2 label")
    private String prop2;
}
