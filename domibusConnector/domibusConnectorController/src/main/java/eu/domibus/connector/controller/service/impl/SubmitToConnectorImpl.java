package eu.domibus.connector.controller.service.impl;

import eu.domibus.connector.controller.queues.ToConnectorQueue;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import javax.transaction.Transactional;

import static eu.domibus.connector.controller.queues.QueuesConfiguration.TO_CONNECTOR_QUEUE_BEAN;

@Service
@RequiredArgsConstructor
public class SubmitToConnectorImpl implements SubmitToConnector {

    private final ToConnectorQueue toConnectorQueue;

    @Override
    @Transactional
    public void submitToConnector(DomibusConnectorMessage message, DomibusConnectorLinkPartner.LinkPartnerName linkPartner, LinkType linkType) {
        if (linkType == LinkType.GATEWAY) {
            message.getMessageDetails().setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
            message.getMessageDetails().setGatewayName(linkPartner.getLinkName());
            toConnectorQueue.putOnQueue(message);
        } else if (linkType == LinkType.BACKEND) {
            message.getMessageDetails().setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
            message.getMessageDetails().setConnectorBackendClientName(linkPartner.getLinkName());
            toConnectorQueue.putOnQueue(message);
        } else {
            throw new RuntimeException("linkType not known!");
        }
    }

}
