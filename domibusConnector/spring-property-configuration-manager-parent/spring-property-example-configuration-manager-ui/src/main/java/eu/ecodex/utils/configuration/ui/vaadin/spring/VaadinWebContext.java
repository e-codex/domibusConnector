package eu.ecodex.utils.configuration.ui.vaadin.spring;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.vaadin.flow.spring.annotation.EnableVaadin;

@Configuration
@EnableWebMvc
@EnableVaadin("eu.ecodex.utils.configuration.ui.vaadin")
public class VaadinWebContext {
}
