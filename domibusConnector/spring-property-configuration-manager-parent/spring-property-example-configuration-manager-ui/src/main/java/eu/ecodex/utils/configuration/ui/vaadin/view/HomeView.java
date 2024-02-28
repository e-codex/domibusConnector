package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainView.class)
public class HomeView extends Div {

    public HomeView() {
        this.add(new H1("I am the Home View"));
    }

}
