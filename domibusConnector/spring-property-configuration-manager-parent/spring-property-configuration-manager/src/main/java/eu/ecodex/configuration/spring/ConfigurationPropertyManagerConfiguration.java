package eu.ecodex.configuration.spring;

import eu.ecodex.utils.configuration.service.ConfigurationPropertyChecker;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCheckerImpl;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollectorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationPropertyManagerConfiguration {


    @Bean
    public ConfigurationPropertyCollectorImpl configurationPropertyManagerImpl() {
        return new ConfigurationPropertyCollectorImpl();
    }

    @Bean
    public ConfigurationPropertyChecker configurationPropertyChecker() {
        return new ConfigurationPropertyCheckerImpl();
    }


}
