package eu.ecodex.utils.configuration.example1;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.Data;

import java.time.Duration;

@ConfigurationDescription("A nested property class")
@Data
public class NestedProperty {

    @ConfigurationLabel("Testprop")
    String test;

    @ConfigurationLabel("Duration property")
    @ConfigurationDescription("The duration for something")
    Duration duration;



}
