package eu.domibus.connector.link.service;

import eu.domibus.connector.controller.service.SubmitToLink;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkInfo;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.link.LinkTestContext;
import eu.domibus.connector.link.impl.gwjmsplugin.GwJmsPluginFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.jms.MapMessage;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = LinkTestContext.class)
@ActiveProfiles("test")
@Configuration
@EnableJms
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DomibusConnectorLinkManagerTest {

    private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorLinkManagerTest.class);

    @Autowired
    private DomibusConnectorLinkManager linkManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    @BeforeAll
    public static void beforeAll() {
//        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

    }

    @Test
    @Order(1)
    void testLinkSetup() {
        SubmitToLink firstLINK = linkManager.getLink("firstLINK");
        assertThat(firstLINK).isNotNull();
    }

    @Test
    @Order(10)
    void addLink() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            DomibusConnectorLinkInfo linkInfo = new DomibusConnectorLinkInfo();
            String linkName = "JMS-GW-PLUGIN";


            linkInfo.setLinkName(linkName);
            linkInfo.setDescription("A description for this link...");
            linkInfo.setEnabled(true);
            linkInfo.setLinkType(LinkType.GATEWAY);
            Properties props = new Properties();
            props.put("link.gwjmsplugin.put-attachment-in-queue", "true");
            props.put("link.gwjmsplugin.put-attachment-in-queue", "true");

            props.put("link.gwjmsplugin.username", "username");
            props.put("link.gwjmsplugin.password", "password");
            props.put("link.gwjmsplugin.in-queue", "contogw");
            props.put("link.gwjmsplugin.reply-queue", "replyfromgwtocon");
            props.put("link.gwjmsplugin.out-queue", "gwtocon");
            props.put("link.gwjmsplugin.error-notify-producer-queue", "errornotifyproducer");
            props.put("link.gwjmsplugin.error-notify-conusmer-queue", "errornotifyconsumer");


            DomibusConnectorLinkConfiguration linkConfig = new DomibusConnectorLinkConfiguration();
            linkConfig.setLinkImpl(GwJmsPluginFactory.LINK_IMPL_NAME);
            linkConfig.setProperties(props);
            linkInfo.setLinkConfiguration(linkConfig);

            linkManager.addLink(linkInfo);

            SubmitToLink link = linkManager.getLink(linkName);

            DomibusConnectorMessage sentMessage = DomainEntityCreator.createMessage();
            assertThat(link).isNotNull();
            link.submitToLink(sentMessage);


            MapMessage contogw = (MapMessage) jmsTemplate.receive("contogw");

            LOGGER.debug("rcv message is: [{}]", contogw);

            assertThat(contogw).isNotNull();
            assertThat(contogw.getStringProperty("conversationId")).isEqualTo(sentMessage.getMessageDetails().getConversationId());

            //TODO: check attachments...
        });

    }

    @Test
    @Order(20)
    void getLink() {
        String linkName = "JMS-GW-PLUGIN";
        SubmitToLink link = linkManager.getLink(linkName);

        assertThat(link).isNotNull();
    }


}