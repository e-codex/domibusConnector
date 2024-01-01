package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.example1.Example1ConfigurationProperties;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import eu.ecodex.utils.configuration.ui.vaadin.tools.views.ListConfigurationPropertiesComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@HtmlImport("styles/shared-styles.html")
@Route(value = "listgridview", layout = MainView.class)
@PageTitle("Spring Properties Configuration Manager")
public class ListGridView extends VerticalLayout {

    private static final Logger LOGGER = LogManager.getLogger(ListGridView.class);

    @Autowired
    ListConfigurationPropertiesComponent listConfigurationPropertiesComponent;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;

    Map<String, String> properties = new HashMap<>();

//    Binder<Map<String, String>> binder = new Binder();

//    Button validate;
    Button saveProperties;
    Button resetProperties;
    Button setConfigClasses;

    public ListGridView() {



    }

    @PostConstruct
    public void init() {

        initUi();

//        List<ConfigurationProperty> configurationProperties = Stream.of(Example1ConfigurationProperties.class)
//                .map(clz -> configurationPropertyCollector.getConfigurationPropertyFromClazz(clz).stream())
//                .flatMap(Function.identity()).collect(Collectors.toList());
//
//        listConfigurationPropertiesComponent.setConfigurationProperties(configurationProperties);

        initProperties();

    }

    private void initUi() {
        setConfigClasses = new Button("set Config Classes");
        setConfigClasses.addClickListener(this::setConfigClasses);
        this.add(setConfigClasses);
        saveProperties = new Button("Save Properties");
        saveProperties.addClickListener(this::saveProperties);
        this.add(saveProperties);
        resetProperties = new Button("Reset Properties");
        resetProperties.addClickListener(this::resetProperties);
        this.add(resetProperties);
        this.add(listConfigurationPropertiesComponent);
    }

    private void setConfigClasses(ClickEvent<Button> buttonClickEvent) {
        List<ConfigurationProperty> configurationProperties = Stream.of(Example1ConfigurationProperties.class)
                .map(clz -> configurationPropertyCollector.getConfigurationPropertyFromClazz(clz).stream())
                .flatMap(Function.identity()).collect(Collectors.toList());

        listConfigurationPropertiesComponent.setConfigurationProperties(configurationProperties);

    }

    private void resetProperties(ClickEvent<Button> buttonClickEvent) {
        initProperties();
    }

    private void saveProperties(ClickEvent<Button> buttonClickEvent) {
        //TODO: call validator
        listConfigurationPropertiesComponent.validate();
    }

    private void initProperties() {
        List<ConfigurationProperty> configurationProperties = Stream.of(Example1ConfigurationProperties.class)
                .map(clz -> configurationPropertyCollector.getConfigurationPropertyFromClazz(clz).stream())
                .flatMap(Function.identity()).collect(Collectors.toList());

        configurationProperties.stream()
                .map(prop -> prop.getPropertyName())
                .filter(propName -> applicationContext.getEnvironment().getProperty(propName) != null)
                .forEach(propName -> properties.put(propName, applicationContext.getEnvironment().getProperty(propName)));

        LOGGER.trace("Setting properties on gui table [{}]", properties);

        listConfigurationPropertiesComponent.getBinder().setBean(properties);
    }


}
