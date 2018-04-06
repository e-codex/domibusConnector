package eu.domibus.connector.controller.process;

import eu.domibus.connector.controller.service.DomibusConnectorBackendDeliveryService;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import eu.domibus.connector.persistence.service.DomibusConnectorMessagePersistenceService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class GatewayToBackendConfirmationProcessorTest {


    @Mock
    private DomibusConnectorMessagePersistenceService messagePersistenceService;

    @Mock
    private DomibusConnectorEvidencePersistenceService evidencePersistenceService;

    @Mock
    private DomibusConnectorBackendDeliveryService backendDeliveryService;

//    private List<DomibusConnectorMessage> toGwDeliveredMessages;

    private List<DomibusConnectorMessage> toBackendDeliveredMessages;

    private GatewayToBackendConfirmationProcessor gatewayToBackendConfirmationProcessor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        toBackendDeliveredMessages = new ArrayList<>();

//        Mockito.doAnswer(invoc -> toGwDeliveredMessages.add(invoc.getArgumentAt(0, DomibusConnectorMessage.class)))
//                .when(gwSubmissionService).submitToGateway(any(DomibusConnectorMessage.class));

        Mockito.doAnswer( invoc -> toBackendDeliveredMessages.add(invoc.getArgumentAt(0, DomibusConnectorMessage.class)))
                .when(backendDeliveryService).deliverMessageToBackend(any(DomibusConnectorMessage.class));

        gatewayToBackendConfirmationProcessor = new GatewayToBackendConfirmationProcessor();
        gatewayToBackendConfirmationProcessor.setBackendDeliveryService(backendDeliveryService);
        gatewayToBackendConfirmationProcessor.setEvidencePersistenceService(evidencePersistenceService);
        gatewayToBackendConfirmationProcessor.setMessagePersistenceService(messagePersistenceService);

        Mockito.when(messagePersistenceService.findMessageByEbmsId(any())).thenReturn(DomainEntityCreator.createMessage());
    }


    @Test
    public void testProcessMessage() {
        DomibusConnectorMessageConfirmation messageDeliveryConfirmation =
                DomainEntityCreator.createMessageDeliveryConfirmation();

        DomibusConnectorMessageDetails domibusConnectorMessageDetails = DomainEntityCreator.createDomibusConnectorMessageDetails();

        DomibusConnectorMessage msg = DomibusConnectorMessageBuilder.createBuilder()
                .addConfirmation(messageDeliveryConfirmation)
                .setMessageDetails(domibusConnectorMessageDetails)
                .build();


        gatewayToBackendConfirmationProcessor.processMessage(msg);

        //TODO: verify...

    }

}