package eu.ecodex.utils.configuration.service;

import eu.ecodex.configuration.spring.EnablePropertyConfigurationManager;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.domain.ConfigurationPropertyNode;
import eu.ecodex.utils.configuration.testdata.ConfigurationPackageTestdata;
import eu.ecodex.utils.configuration.testdata.subpackage1.Subpackage1;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EnablePropertyConfigurationManager
class ConfigurationPropertyCollectorImplTest {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCollectorImplTest.class);



    @Autowired
    private ConfigurationPropertyCollectorImpl configurationPropertyManager;

    @Test
    void test_getAll_byClassName() {
        Collection<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties(Subpackage1.class);

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(2)
        );
    }


    @Test
    void getAll() {

        Collection<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties("eu.ecodex.utils.configuration.testdata.subpackage1");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(2)
        );
    }

    @Test
    void getAll_withSubpackageFiltering() {

        Collection<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties("eu.ecodex.utils.configuration.testdata.subpackage1");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(2)
        );
    }

    @Test
    void test_hierachicalProperties() {
        ConfigurationPropertyNode configurationPropertiesHirachie = configurationPropertyManager.getConfigurationPropertiesHirachie("eu.ecodex.utils.configuration.testdata");
        assertThat(configurationPropertiesHirachie).isNotNull();
        Optional<ConfigurationPropertyNode> subNodeExample = configurationPropertiesHirachie.getChild("example");
        assertThat(subNodeExample.isPresent()).isTrue();

        assertThat(subNodeExample.get().getChild("abc")).isNotEmpty();

                //.get().getChild("configuration").get().getChild("testdata");
    }

    @Test
    void test_hierachicalProperties_withNestedProperties() {
        ConfigurationPropertyNode configurationPropertiesHirachie = configurationPropertyManager.getConfigurationPropertiesHirachie("eu.ecodex.utils.configuration.testdata.subpackage2");

        assertThat(configurationPropertiesHirachie).isNotNull();

        Optional<ConfigurationPropertyNode> childCom = configurationPropertiesHirachie.getChild("com");
        assertThat(childCom).as("There must be a child com").isNotEmpty();
        Optional<ConfigurationPropertyNode> comExample = childCom.get().getChild("example");
        assertThat(comExample).as("Node com.example must exist").isNotEmpty();
        Optional<ConfigurationPropertyNode> comExampleDc = comExample.get().getChild("dc");
        assertThat(comExampleDc).as("Node com.example.dc must exist").isNotEmpty();

        assertThat(comExampleDc.get().getChildren()).hasSize(3);
        Optional<ConfigurationPropertyNode> comExampleDcAbc = comExampleDc.get().getChild("abc");
        assertThat(comExampleDcAbc).as("property node com.example.dc.abc must exist").isNotEmpty();

        assertThat(comExampleDcAbc.get().getProperty()).as("must be a property node!").isNotNull();

        assertThat(comExampleDcAbc.get().getProperty().getPropertyName()).isEqualTo("com.example.dc.abc");

    }

}