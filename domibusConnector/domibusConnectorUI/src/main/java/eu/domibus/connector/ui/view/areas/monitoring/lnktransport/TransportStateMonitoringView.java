package eu.domibus.connector.ui.view.areas.monitoring.lnktransport;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.event.SortEvent;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.controller.transport.DCTransportRetryService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.persistence.service.TransportStepPersistenceService;
import eu.domibus.connector.ui.component.OpenHelpButtonFactory;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.messages.MessageDetails;
import eu.domibus.connector.ui.view.areas.monitoring.MonitoringLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.vaadin.gatanaso.MultiselectComboBox;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.domibus.connector.domain.model.helper.DomainModelHelper.isBusinessMessage;


@UIScope
@Component
@Order(2)
@Route(value = TransportStateMonitoringView.ROUTE_PREFIX, layout = MonitoringLayout.class)
@TabMetadata(title = "Link Msg", tabGroup = MonitoringLayout.TAB_GROUP_NAME)
public class TransportStateMonitoringView extends VerticalLayout implements AfterNavigationObserver {

    private final static Logger LOGGER = LogManager.getLogger(TransportStateMonitoringView.class);

    public static final String TITLE = "Message Transport";
    public static final String ROUTE_PREFIX = "linktransport";
    public static final int INITIAL_PAGE_SIZE = 20;
    //TODO: add compile check, that this file exists within dependencies! Maybe with java annotation processor?
    public static final String HELP_ID = "doc/message_transport_overview.html";

    private final DCTransportRetryService dcTransportRetryService;
    private final TransportStepPersistenceService transportStepPersistenceService;
    private final DCLinkFacade dcLinkFacade;
    private final OpenHelpButtonFactory openHelpButtonFactory;

    private int pageSize = INITIAL_PAGE_SIZE;
    private PaginatedGrid<DomibusConnectorTransportStep> paginatedGrid;
    private Page<DomibusConnectorTransportStep> currentPage;
    private CallbackDataProvider<DomibusConnectorTransportStep, DomibusConnectorTransportStep> callbackDataProvider;

    private Set<TransportState> filterForState = Stream.of(TransportState.values())
            .filter(s -> s != TransportState.ACCEPTED)
            .collect(Collectors.toSet());
    private Set<String> selectedLinkPartners = new HashSet<>();

    private CheckboxGroup<TransportState> checkboxGroup = new CheckboxGroup<>();
    private MultiselectComboBox<String> multiselectComboBox = new MultiselectComboBox<>();

    public TransportStateMonitoringView(DCTransportRetryService dcTransportRetryService,
                                        TransportStepPersistenceService transportStepPersistenceService,
                                        DCLinkFacade dcLinkFacade,
                                        OpenHelpButtonFactory openHelpButtonFactory) {
        this.dcTransportRetryService = dcTransportRetryService;
        this.transportStepPersistenceService = transportStepPersistenceService;
        this.dcLinkFacade = dcLinkFacade;
        this.openHelpButtonFactory = openHelpButtonFactory;
        initUI();
    }

    private void initUI() {


        callbackDataProvider = new CallbackDataProvider<>(this::fetchCallback, this::countCallback);

        HorizontalLayout titleBar = new HorizontalLayout();

        titleBar.add(new H2(new Text(TITLE), openHelpButtonFactory.createHelpButton(HELP_ID)));

        HorizontalLayout buttonBar = new HorizontalLayout();
        IntegerField pageSizeField = new IntegerField("Page Size");
        pageSizeField.setValue(INITIAL_PAGE_SIZE);
        pageSizeField.addValueChangeListener(e -> {
           this.pageSize = e.getValue();
           this.paginatedGrid.setPageSize(pageSize);
           this.callbackDataProvider.refreshAll();
        });
        buttonBar.add(pageSizeField);


        checkboxGroup.setLabel("Transport State");
        checkboxGroup.setItems(TransportState.values());
        checkboxGroup.setItemLabelGenerator(Enum::name);
        checkboxGroup.addSelectionListener((selectEvent) -> {
            this.filterForState = selectEvent.getAllSelectedItems();
            this.callbackDataProvider.refreshAll();
        });

        buttonBar.add(checkboxGroup);


        multiselectComboBox.setLabel("Link Partner");
        multiselectComboBox.setAllowCustomValues(true);
        multiselectComboBox.addSelectionListener((selectEvent) -> {
            this.selectedLinkPartners = selectEvent.getAllSelectedItems();
            this.callbackDataProvider.refreshAll();
        });




        this.add(titleBar);
        this.add(buttonBar);

        paginatedGrid = new PaginatedGrid<>(DomibusConnectorTransportStep.class);
        paginatedGrid.setDataProvider(callbackDataProvider);
        paginatedGrid.setPageSize(this.pageSize);

        paginatedGrid.setColumns(); //reset all columns...

        paginatedGrid.addComponentColumn(this::buttonProvider);


        paginatedGrid
                .addColumn("attempt");
        paginatedGrid
                .addColumn(s -> s.getLastStatusUpdate() == null ? "" : s.getLastStatusUpdate().getTransportState().name())
                .setHeader("Last Transport State");
        paginatedGrid.addColumn(DomibusConnectorTransportStep::getConnectorMessageId)
                .setSortable(true)
                .setHeader("Connector Message Id");
        paginatedGrid.addColumn("linkPartnerName")
                .setSortable(true);
        paginatedGrid.addColumn("created")
                .setSortable(true);
        paginatedGrid.addColumn("remoteMessageId")
                .setSortable(true);
        paginatedGrid.addColumn(s -> s.getTransportId().getTransportId())
                .setSortable(true)
                .setHeader("Transport Id");

        this.add(paginatedGrid);
    }

    private HorizontalLayout buttonProvider(DomibusConnectorTransportStep step) {
        HorizontalLayout layout = new HorizontalLayout();

        DomibusConnectorMessage msg = step.getTransportedMessage();
        DomibusConnectorMessageId connectorMessageId = msg.getConnectorMessageId();

        //goto business message button
        Button gotoBusinessMessageButton = new Button(VaadinIcon.ENVELOPE.create());
        gotoBusinessMessageButton.addClickListener((e) -> {
            MessageDetails.navigateTo(connectorMessageId);
        });
        layout.add(gotoBusinessMessageButton);
        gotoBusinessMessageButton.setEnabled(connectorMessageId != null && isBusinessMessage(msg));

        //retry message
        Button retryMessageButton = new Button(VaadinIcon.ROTATE_LEFT.create());
        retryMessageButton.addClickListener((e) -> {
            this.retryTransport(step);
        });
        layout.add(retryMessageButton);
        retryMessageButton.setEnabled(dcTransportRetryService.isRetryAble(step));

        return layout;
    }


    private void retryTransport(DomibusConnectorTransportStep step) {
        try {
            dcTransportRetryService.retryTransport(step);
            Notification.show("Successfully retried message");
        } catch (RuntimeException exc) {
            //TODO: improve error message and User notification!
            Notification.show("ERROR while retrying message: " + exc.getMessage());
        }
        this.callbackDataProvider.refreshItem(step);
    }

    private int countCallback(Query<DomibusConnectorTransportStep, DomibusConnectorTransportStep> tfQuery) {
        //TODO: introduce own count call on DB so not ALL items are read from DB or check if Pageable.ofSize(0) avoids fetching items
        Page<DomibusConnectorTransportStep> stepByLastState = getDomibusConnectorTransportSteps(Pageable.ofSize(1));
        return (int) stepByLastState.getTotalElements();
    }

    private Stream<DomibusConnectorTransportStep> fetchCallback(Query<DomibusConnectorTransportStep, DomibusConnectorTransportStep> tfQuery) {
        int offset = tfQuery.getOffset();

        List<Sort.Order> collect = paginatedGrid.getSortOrder()
                .stream()
                .filter(sortOrder -> sortOrder.getSorted().getKey() != null)
                .map(sortOrder ->
                        sortOrder.getDirection() == SortDirection.ASCENDING ? Sort.Order.asc(sortOrder.getSorted().getKey()) : Sort.Order.desc(sortOrder.getSorted().getKey()))
                .collect(Collectors.toList());
        Sort sort = Sort.by(collect.toArray(new Sort.Order[]{}));


        PageRequest pageRequest = PageRequest.of(offset / paginatedGrid.getPageSize(), paginatedGrid.getPageSize(), sort);
        Page<DomibusConnectorTransportStep> domibusConnectorTransportSteps = getDomibusConnectorTransportSteps(pageRequest);
        this.currentPage = domibusConnectorTransportSteps;
        return domibusConnectorTransportSteps.stream();
    }

    private Page<DomibusConnectorTransportStep> getDomibusConnectorTransportSteps(Pageable p) {
//        TransportState[] transportStates = filterForState.toArray(new TransportState[0]);
        Set<DomibusConnectorLinkPartner.LinkPartnerName> linkPartners = this.selectedLinkPartners.stream().map(l -> new DomibusConnectorLinkPartner.LinkPartnerName(l)).collect(Collectors.toSet());
        Page<DomibusConnectorTransportStep> stepByLastState = transportStepPersistenceService.findLastAttemptStepByLastStateIsOneOf(filterForState, linkPartners, p);
        return stepByLastState;
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        checkboxGroup.select(filterForState.toArray(new TransportState[0]));

        String[] sel = dcLinkFacade.getAllLinks()
                .stream()
                .map(l -> l.getLinkPartnerName().getLinkName())
                .toArray(String[]::new);
        multiselectComboBox.setItems(sel);
        multiselectComboBox.select(sel);

        this.callbackDataProvider.refreshAll();


    }

}
