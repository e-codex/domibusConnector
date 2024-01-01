package eu.ecodex.utils.configuration.ui.vaadin.spring;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.vaadin.flow.spring.annotation.EnableVaadin;

@Configuration
@EnableWebMvc
@EnableVaadin("eu.ecodex.utils.configuration.ui.vaadin")
@MultipartConfig
public class VaadinWebContext {
}
