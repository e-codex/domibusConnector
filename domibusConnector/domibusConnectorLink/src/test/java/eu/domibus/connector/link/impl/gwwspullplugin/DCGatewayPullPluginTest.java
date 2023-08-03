package eu.domibus.connector.link.impl.gwwspullplugin;

import eu.domibus.connector.controller.service.SubmitToLinkService;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.ecodex.dc5.message.model.DC5Message;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.testdata.TransitionCreator;
import eu.domibus.connector.ws.gateway.webservice.GetMessageByIdRequest;
import eu.domibus.connector.ws.gateway.webservice.ListPendingMessageIdsResponse;
import eu.ecodex.dc5.transport.model.DC5TransportRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.TestSocketUtils;
import test.eu.domibus.connector.link.LinkTestContext;
import test.eu.domibus.connector.link.impl.gwwspullplugin.TestGwWebService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static eu.domibus.connector.link.service.DCLinkPluginConfiguration.LINK_PLUGIN_PROFILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static test.eu.domibus.connector.link.LinkTestContext.SUBMIT_TO_CONNECTOR_QUEUE;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {LinkTestContext.class },
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
//                "connector.link.fail-on-link-plugin-error=true"
        }
)
@ActiveProfiles({"gwwspullplugin-test", "test", LINK_PLUGIN_PROFILE_NAME, "plugin-gwwspullplugin"})
//@Disabled
class DCGatewayPullPluginTest {

    private static final Logger LOGGER = LogManager.getLogger(DCGatewayPullPluginTest.class);

    private static Integer PORT;

    /**
     *  find free tcp port on first call and then always return this port
     * @return a free tcp port
     */
    public static int GET_PORT() {
        if (PORT == null) {
            PORT = TestSocketUtils.findAvailableTcpPort();
        }
        return PORT;
    }

    /**
     * Register the correct gw-address within the spring-test-context
     * @param registry
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("connector.link.gateway.link-config.properties.gw-address", () -> "http://localhost:" + GET_PORT() + "/services/pullservice");
    }

    @Autowired
    SubmitToLinkPartner submitToLinkPartner;

    static ConfigurableApplicationContext CTX;
    static TestGwWebService testGwWebService;

    @BeforeAll
    public static void setupTestGwContext() {
        HashMap props = new HashMap<>();
        props.put("server.port", GET_PORT());
        ConfigurableApplicationContext CTX = TestGwWebService.startContext(props);
        testGwWebService = CTX.getBean(TestGwWebService.class);
    }

    @Autowired
    DCActiveLinkManagerService linkManagerService;



    @Autowired
    @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
    public BlockingQueue<DC5Message> toConnectorSubmittedMessages;


    @Test
    public void testPluginIsLoaded() {
        List<LinkPlugin> availableLinkPlugins = linkManagerService.getAvailableLinkPlugins();
        assertThat(availableLinkPlugins).extracting(LinkPlugin::getPluginName).contains("gwwspullplugin");
    }

    @Test
    public void testPluginConfigs() {
        Collection<ActiveLinkPartner> activeLinkPartners = linkManagerService.getActiveLinkPartners();
        assertThat(activeLinkPartners).hasSize(1);
    }


    @Test
    public void testPullFromTestGw() throws Exception {

        ListPendingMessageIdsResponse response = new ListPendingMessageIdsResponse();
        response.getMessageIds().add("id1");
        response.getMessageIds().add("id2");
        ListPendingMessageIdsResponse emptyResponse = new ListPendingMessageIdsResponse();

        Mockito.when(testGwWebService.listPendingMessagesMock()
                        .listPendingMessageIds())
                .thenReturn(response, emptyResponse);

        DomibusConnectorMessageType msg1 = TransitionCreator.createMessage();
        msg1.getMessageDetails().setEbmsMessageId("ebms1");
        DomibusConnectorMessageType msg2 = TransitionCreator.createMessage();
        msg2.getMessageDetails().setEbmsMessageId("ebms2");

        Mockito.reset(testGwWebService.getMessageByIdMock());
        Mockito.when(testGwWebService.getMessageByIdMock().getMessageById(Mockito.any(GetMessageByIdRequest.class))).thenReturn(msg1, msg2);

//        DC5Message poll1 = toConnectorSubmittedMessages.poll(120, TimeUnit.SECONDS);
//        assertThat(poll1).isNotNull();
//        DC5Message poll2 = toConnectorSubmittedMessages.poll(30, TimeUnit.SECONDS);
//        assertThat(poll2).isNotNull();


    }

    @Test
    @Disabled //TODO: mock
    public void testSubmitToGw() throws Exception {
        DC5Message message = DomainEntityCreator.createMessage();

        DC5TransportRequest.TransportRequestId transportRequestId = DC5TransportRequest.TransportRequestId.of("transport1");

        submitToLinkPartner.submitToLink(transportRequestId, DomibusConnectorLinkPartner.LinkPartnerName.of("default_gateway"));


//        gatewaySubmissionService.submitToGateway(message);
//        ActiveLinkPartner defaultGateway = linkManagerService.getActiveLinkPartnerByName("default_gateway").get();


        DomibusConnectorMessageType msg = testGwWebService.submittedMessagesList().poll(30, TimeUnit.SECONDS);
        assertThat(msg).isNotNull();

    }

}