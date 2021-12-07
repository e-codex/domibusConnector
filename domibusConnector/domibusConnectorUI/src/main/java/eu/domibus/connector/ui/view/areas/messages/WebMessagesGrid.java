package eu.domibus.connector.ui.view.areas.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.klaudeta.PaginatedGrid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.event.SortEvent;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.ValueProvider;

import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;

public class WebMessagesGrid extends PaginatedGrid<WebMessage> {

	// collect all hideable columns, to be iterated over later.
	private Map<String,Column> hideableColumns = new HashMap<>();
	
	private final MessageDetails details;
	private final DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService;
	
	WebMessage exampleWebMessage = new WebMessage();
	
	Page<WebMessage> currentPage;
	
	CallbackDataProvider<WebMessage, WebMessage> callbackDataProvider;

	public WebMessagesGrid(MessageDetails details, DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService) {
		super();
		this.details = details;
		this.dcMessagePersistenceService = dcMessagePersistenceService;
		addAllColumns();
		
		for(Column<WebMessage> col : getColumns()) {
			col.setResizable(true);
		}
		
		setMultiSort(false);
		setWidth("100%");
		addSortListener(this::handleSortEvent);
		
		callbackDataProvider
			= new CallbackDataProvider<WebMessage, WebMessage>(this::fetchCallback, this::countCallback);
		setDataProvider(callbackDataProvider);
	}

	private void addAllColumns(){
		addComponentColumn(webMessage -> geMessageDetailsLink(webMessage)).setHeader("Details").setWidth("30px");
		
		addColumn(
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getFrom()!=null?webMessage.getMessageInfo().getFrom().getPartyString():"")
				.setHeader("From Party").setWidth("70px").setKey("messageInfo.from.partyId").setSortable(true);
		addColumn(
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getTo()!=null?webMessage.getMessageInfo().getTo().getPartyString():"")
				.setHeader("To Party").setWidth("70px").setKey("messageInfo.to.partyId").setSortable(true);

		addHideableColumn(WebMessage::getConnectorMessageId, "Connector Message ID", "450px", "connectorMessageId", false, false);
		addHideableColumn(WebMessage::getEbmsMessageId, "ebMS Message ID", "450px", "ebmsMessageId", false, true);
		addHideableColumn(WebMessage::getBackendMessageId, "Backend Message ID", "450px", "backendMessageId", false, true);
		addHideableColumn(WebMessage::getConversationId, "Conversation ID", "450px", "conversationId", false, false);
		addHideableColumn(
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getOriginalSender():""
				, "Original sender", "300px", "messageInfo.originalSender", true, false);
		addHideableColumn(
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getFinalRecipient():""
				, "Final recipient", "300px", "messageInfo.finalRecipient", true, false);
		addHideableColumn(
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getService()!=null?webMessage.getMessageInfo().getService().getServiceString():""
				, "Service", "150px", "messageInfo.service.service", true, true);
		addHideableColumn(
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getAction()!=null?webMessage.getMessageInfo().getAction().getAction():""
				, "Action", "150px", "messageInfo.action.action", true, true);
		addHideableColumn(WebMessage::getBackendName, "backend name", "150px", "backendName", true, false);
		addHideableColumn(WebMessage::getDirection, "direction", "150px", "direction", false, false);
		addHideableColumn(WebMessage::getDeliveredToNationalSystem, "delivered backend", "300px", "deiliveredToNationalSystem", true, false);
		addHideableColumn(WebMessage::getDeliveredToGateway, "delivered gateway", "300px", "deliveredToGateway", true, false);
		addHideableColumn(WebMessage::getCreated, "created", "300px", "created", true, true);
		addHideableColumn(WebMessage::getConfirmed, "confirmed", "300px", "confirmed", true, false);
		addHideableColumn(WebMessage::getRejected, "rejected", "300px", "rejected", true, false);
		
	}

	
	private void addHideableColumn(ValueProvider<WebMessage, ?> valueProvider, String header, String width, String key, boolean sortable, boolean visible){
		Column<WebMessage> column = addColumn(valueProvider).setHeader(header).setKey(key).setWidth(width).setSortable(sortable);
		column.setVisible(visible);
		hideableColumns.put(header, column);
	}
	
	public Map<String, Column> getHideableColumns() {
		return hideableColumns;
	}
	
	public Set<String> getHideableColumnNames() {
		return hideableColumns.keySet();
	}

	public Button geMessageDetailsLink(WebMessage connectorMessage) {
		Button getDetails = new Button(new Icon(VaadinIcon.SEARCH));
		getDetails.addClickListener(e -> details.show(connectorMessage));
		return getDetails;
	}
	
	private void handleSortEvent(SortEvent<Grid<WebMessage>, GridSortOrder<WebMessage>> gridGridSortOrderSortEvent) {
		gridGridSortOrderSortEvent.getSortOrder().stream()
				.map(webMessageGridSortOrder -> {
					SortDirection direction = webMessageGridSortOrder.getDirection();
					return direction;

				});
	}
	
	private Example<WebMessage> createExample() {
		return Example.of(exampleWebMessage, ExampleMatcher.matchingAny()
				.withIgnoreCase()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
	}
	
	private int countCallback(Query<WebMessage, WebMessage> webMessageWebMessageQuery) {
		return (int) dcMessagePersistenceService.count(createExample());
	}

	private Stream<WebMessage> fetchCallback(Query<WebMessage, WebMessage> webMessageWebMessageQuery) {

		int offset = webMessageWebMessageQuery.getOffset();
		
		List<Sort.Order> collect = getSortOrder()
				.stream()
				.filter(sortOrder -> sortOrder.getSorted().getKey() != null)
				.map(sortOrder ->
						sortOrder.getDirection() == SortDirection.ASCENDING ? Sort.Order.asc(sortOrder.getSorted().getKey()) : Sort.Order.desc(sortOrder.getSorted().getKey()))
				.collect(Collectors.toList());
		Sort sort = Sort.by(collect.toArray(new Sort.Order[]{}));

		//creating page request with sort order and offset
		PageRequest pageRequest = PageRequest.of(offset / getPageSize(), getPageSize(), sort);
		Page<WebMessage> all = dcMessagePersistenceService.findAll(createExample(), pageRequest);
		
		this.currentPage = all;

		return all.stream();
	}
	
	public void reloadList() {
		callbackDataProvider.refreshAll();
	}
	
	public void setExampleWebMessage(WebMessage example) {
		this.exampleWebMessage = example;
	}
}
