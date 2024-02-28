package eu.ecodex.utils.configuration.service;

import eu.ecodex.configuration.spring.EnablePropertyConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EnablePropertyConfigurationManager
class ConfigurationPropertyCheckerImplTest {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCheckerImplTest.class);


//    @Autowired
//    private ConfigurationPropertyCollectorImpl configurationPropertyManager;

    @Autowired
    private ConfigurationPropertyChecker configPropertyChecker;

    private Properties getExampleProperties1() {
        Properties properties = new Properties();
        properties.put("example.configuration.text", "text");
        properties.put("example.configuration.number", "59");
        properties.put("example.abc.address", "Testgasse 2");

        return properties;

    }

//    @BeforeEach
//    public void getConfigPropertyChecker() {
//        this.configPropertyChecker = configurationPropertyManager.getConfigChecker();
//    }

//    @Test
//    void getStringValueForProperty() {
//
//    }
//
//    @Test
//    void getValueForProperty() {
//    }
//
//    @Test
//    void isConfigurationValid() {
//    }
//
//    @Test
//    void isConfigurationValid1() {
//    }


    @Test
    void isValid() {
        ConfigurationPropertySource configurationPropertySource = new MapConfigurationPropertySource(getExampleProperties1());
        List<ValidationErrors> validationErrors = configPropertyChecker.validateConfiguration(configurationPropertySource, "eu.ecodex.utils.configuration.testdata");
        assertThat(validationErrors).isEmpty();

    }

    @Test
    void isValid_shouldReturnValidationErrors() {


        Properties properties = getExampleProperties1();
        properties.put("example.configuration.number", "89"); //number is too big MAX(60)

        ConfigurationPropertySource configurationPropertySource = new MapConfigurationPropertySource(properties);


        List<ValidationErrors> validationErrors = configPropertyChecker.validateConfiguration(configurationPropertySource, "eu.ecodex.utils.configuration.testdata");

        assertThat(validationErrors).hasSize(1);

    }

}