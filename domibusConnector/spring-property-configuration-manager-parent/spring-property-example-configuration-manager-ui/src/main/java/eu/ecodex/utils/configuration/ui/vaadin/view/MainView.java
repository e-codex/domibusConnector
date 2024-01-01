package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@HtmlImport("styles/shared-styles.html")

@PageTitle("Spring Properties Configuration Manager")
public class MainView extends AppLayout {

    private static final Logger LOGGER = LogManager.getLogger(MainView.class);

    private Map<Tab, Class<? extends Component>> tabToViewMap = new HashMap<>();
    private Tabs tabs = new Tabs();

    public MainView() {

    }

    @PostConstruct
    public void init() {
        this.setContent(new Label("The Content Label"));

        addTab(new Tab("Home"), HomeView.class);
        addTab(new Tab("TreeGridView"), TreeGridView.class);
        addTab(new Tab("ListGridView"), ListGridView.class);
        addTab(new Tab("ConfigFormView"), ConfigFormView.class);

        addToNavbar(tabs);

        tabs.addSelectedChangeListener(this::tabSelected);

        tabs.setSelectedIndex(0);

    }

    private void addTab(Tab tab, Class<? extends Component> viewClazz) {
        this.tabToViewMap.put(tab, viewClazz);
        tabs.add(tab);
    }

    private void tabSelected(Tabs.SelectedChangeEvent selectedChangeEvent) {
        Tab selectedTab = selectedChangeEvent.getSelectedTab();
        selectedTab.getUI().ifPresent(ui -> ui.navigate(tabToViewMap.getOrDefault(selectedTab, HomeView.class)));
    }


}
