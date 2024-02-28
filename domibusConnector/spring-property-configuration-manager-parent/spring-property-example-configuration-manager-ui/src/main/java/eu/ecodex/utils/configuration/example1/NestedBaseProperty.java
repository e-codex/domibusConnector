package eu.ecodex.utils.configuration.example1;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.Data;

import java.time.Duration;

@ConfigurationDescription("A nested base property class")
@Data
public class NestedBaseProperty {

    @ConfigurationLabel("Testprop")
    String test;


}
