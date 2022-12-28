package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.dc5.message.model.DC5Message;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPluginActiveLinkPartner;
import eu.ecodex.dc5.transport.model.DC5TransportRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WsBackendSubmitTo implements SubmitToLinkPartner {

    private final DCActiveLinkManagerService linkManagerService;
    private final WsBackendPluginWebServiceClientFactory webServiceClientFactory;
    private final DomibusConnectorDomainMessageTransformerService transformerService;
    private final TransportStateService transportStateService;

    public WsBackendSubmitTo(DCActiveLinkManagerService linkManagerService,
                             WsBackendPluginWebServiceClientFactory webServiceClientFactory,
                             DomibusConnectorDomainMessageTransformerService transformerService,
                             TransportStateService transportStateService) {
        this.linkManagerService = linkManagerService;
        this.webServiceClientFactory = webServiceClientFactory;
        this.transformerService = transformerService;
        this.transportStateService = transportStateService;
    }


    @Override
    public void submitToLink(DC5TransportRequest.TransportRequestId transportRequestId, DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) throws DomibusConnectorSubmitToLinkException {
        DC5TransportRequest transportRequest = transportStateService.getTransportRequest(transportRequestId);
        DC5Message message = transportRequest.getMessage();
        Optional<ActiveLinkPartner> activeLinkPartnerByName = linkManagerService.getActiveLinkPartnerByName(linkPartnerName);
        if (activeLinkPartnerByName.isPresent()) {
            WsBackendPluginActiveLinkPartner activeLinkPartner = (WsBackendPluginActiveLinkPartner) activeLinkPartnerByName.get();
            if (activeLinkPartner.getLinkPartner().getSendLinkMode() == LinkMode.PUSH) {
                pushMessage(transportRequest, activeLinkPartner);
            } else {
                makeMessageReadyForPull(transportRequest, activeLinkPartner);
            }

        } else {
            throw new DomibusConnectorSubmitToLinkException(message, java.lang.String.format("No LinkPartner found with name [%s]", linkPartnerName));
        }
    }

    void makeMessageReadyForPull(DC5TransportRequest message, WsBackendPluginActiveLinkPartner activeLinkPartner) {
        //TODO
        DomibusConnectorBackendDeliveryWebService backendWsClient = webServiceClientFactory.createBackendWsClient(activeLinkPartner);

//        TransportStateService.TransportId transportId = transportStateService.createTransportFor(message, activeLinkPartner.getLinkPartner().getLinkPartnerName());

        TransportStateService.DomibusConnectorTransportState state = new TransportStateService.DomibusConnectorTransportState();
        state.setStatus(TransportState.PENDING);
        state.setText("Message ready for pull by client");
        transportStateService.updateTransportToBackendClientStatus(message.getTransportRequestId(), state);

    }

    void pushMessage(DC5TransportRequest transportRequest, WsBackendPluginActiveLinkPartner activeLinkPartner) {
        //TODO
        DomibusConnectorBackendDeliveryWebService backendWsClient = webServiceClientFactory.createBackendWsClient(activeLinkPartner);

//        TransportStateService.TransportId transportId = transportStateService.createTransportFor(message, activeLinkPartner.getLinkPartner().getLinkPartnerName());

        TransportStateService.DomibusConnectorTransportState state = new TransportStateService.DomibusConnectorTransportState();
        DomibusConnectorMessageType domibusConnectorMessageType = transformerService.transformDomainToTransition(transportRequest.getMessage());
        try {
            DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType = backendWsClient.deliverMessage(domibusConnectorMessageType);

            state.setStatus(domibsConnectorAcknowledgementType.isResult() ? TransportState.ACCEPTED : TransportState.FAILED);
            state.setRemoteMessageId(domibsConnectorAcknowledgementType.getMessageId());
            state.setText(domibsConnectorAcknowledgementType.getResultMessage());

        } catch (Exception e) {
            state.setStatus(TransportState.FAILED);
            state.setText(e.getMessage());
        }
        transportStateService.updateTransportToBackendClientStatus(transportRequest.getTransportRequestId(), state);

    }

}