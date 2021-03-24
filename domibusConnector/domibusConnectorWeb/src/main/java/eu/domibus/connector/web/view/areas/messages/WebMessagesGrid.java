package eu.domibus.connector.web.view.areas.messages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.klaudeta.PaginatedGrid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

import eu.domibus.connector.web.dto.WebMessage;

public class WebMessagesGrid extends PaginatedGrid<WebMessage> {

	// collect all hideable columns, to be iterated over later.
	private Map<String,Column> hideableColumns = new HashMap<>();
	
	private Messages messagesView;

	public WebMessagesGrid(Messages messagesView) {
		super();
		this.messagesView = messagesView;
		addAllColumns();
		
		for(Column<WebMessage> col : getColumns()) {
			col.setResizable(true);
		}
	}

	private void addAllColumns(){
		addComponentColumn(webMessage -> geMessageDetailsLink(webMessage)).setHeader("Details").setWidth("30px");
		
		addColumn(webMessage -> webMessage.getMessageInfo().getFrom().getPartyId()).setHeader("From Party ID").setWidth("70px").setKey("fromPartyId").setSortable(true);
		addColumn(webMessage -> webMessage.getMessageInfo().getTo().getPartyId()).setHeader("To Party ID").setWidth("70px").setKey("toPartyId").setSortable(true);

		addHideableColumn(WebMessage::getConnectorMessageId, "Connector Message ID", "450px", false, false);
		addHideableColumn(WebMessage::getEbmsMessageId, "ebMS Message ID", "450px", false, true);
		addHideableColumn(WebMessage::getBackendMessageId, "Backend Message ID", "450px", false, true);
		addHideableColumn(WebMessage::getConversationId, "Conversation ID", "450px", false, false);
		addHideableColumn(webMessage -> webMessage.getMessageInfo().getOriginalSender(), "Original sender", "300px", true, false);
		addHideableColumn(webMessage -> webMessage.getMessageInfo().getFinalRecipient(), "Final recipient", "300px", true, false);
		addHideableColumn(webMessage -> webMessage.getMessageInfo().getService().getService(), "Service", "150px", true, true);
		addHideableColumn(webMessage -> webMessage.getMessageInfo().getAction().getAction(), "Action", "150px", true, true);
		addHideableColumn(WebMessage::getBackendName, "backend name", "150px", true, false);
		addHideableColumn(WebMessage::getDirection, "direction", "150px", false, false);
//		addHideableColumn(WebMessage::getDirectionSource, "direction source", "150px", true, false);
//		addHideableColumn(WebMessage::getDirectionTarget, "Target", "150px", true, false);
		addHideableColumn(WebMessage::getDeliveredToNationalSystem, "delivered backend", "300px", true, false);
		addHideableColumn(WebMessage::getDeliveredToGateway, "delivered gateway", "300px", true, false);
		addHideableColumn(WebMessage::getCreated, "created", "300px", true, true);
		addHideableColumn(WebMessage::getConfirmed, "confirmed", "300px", true, false);
		addHideableColumn(WebMessage::getRejected, "rejected", "300px", true, false);
		
	}

	
	private Column<WebMessage> addHideableColumn(ValueProvider<WebMessage, ?> valueProvider, String header, String width, boolean sortable, boolean visible){
		Column<WebMessage> column = addColumn(valueProvider).setHeader(header).setWidth(width).setSortable(sortable);
		column.setVisible(visible);
		hideableColumns.put(header, column);
		return column;
	}



//	/**
//	 * Hides all given columns, and shows all others
//	 * @param columns
//	 */
//	public void hideColumns(Set<Column> columns) {
//		for (Column hideableColumn : getHideableColumns()) {
//			hideableColumn.setVisible(!columns.contains(hideableColumn));
//		}
//	}
	
	public Map<String, Column> getHideableColumns() {
		return hideableColumns;
	}
	
	public Set<String> getHideableColumnNames() {
		return hideableColumns.keySet();
	}

	public Button geMessageDetailsLink(WebMessage connectorMessage) {
		Button getDetails = new Button(new Icon(VaadinIcon.SEARCH));
		getDetails.addClickListener(e -> messagesView.showMessageDetails(connectorMessage));
		return getDetails;
	}

}
