package eu.domibus.connector.testdata;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class LoadStoreTransitionMessageTest {




    @Test
    public void loadMessageFrom() throws Exception {
        DomibusConnectorMessageType msg1 = LoadStoreTransitionMessage.loadMessageFrom(new ClassPathResource("endtoendtest/messages/epo_forma_backend_to_gw/"));

        assertThat(msg1).isNotNull();

        assertThat(msg1.getMessageAttachments()).hasSize(0);
        assertThat(msg1.getMessageContent().getXmlContent()).isNotNull();
        assertThat(msg1.getMessageContent().getDocument().getDocument()).isNotNull();
    }

    @Test
    public void testStoreMessage() throws Exception {
        File f = new File("./target/teststore/testmsg1/");
        f.mkdirs();
        FileSystemResource resource = new FileSystemResource(f.getAbsolutePath() + "/");

        DomibusConnectorMessageType testmessage = TransitionCreator.createMessage();

        LoadStoreTransitionMessage.storeMessageTo(resource, testmessage, true);

    }

}