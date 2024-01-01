package eu.ecodex.utils.configuration.ui.vaadin.view;

import eu.ecodex.configuration.spring.EnablePropertyConfigurationManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication(scanBasePackages = {"eu.ecodex.utils.configuration.ui", "eu.ecodex.utils.configuration.example1"})
@EnablePropertyConfigurationManager
public class ConfigManagerUi {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        builder.sources(ConfigManagerUi.class);

        builder.run(args);
    }

}
