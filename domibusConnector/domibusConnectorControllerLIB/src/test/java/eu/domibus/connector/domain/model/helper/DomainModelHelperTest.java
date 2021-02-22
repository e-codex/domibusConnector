package eu.domibus.connector.domain.model.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 *
 *
 */
public class DomainModelHelperTest {
    
    public DomainModelHelperTest() {
    }

    @Test
    public void testIsEvidenceMessage() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createSimpleTestMessage();
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    public void testIsEvidenceTriggerMessage_shouldBeFalse() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                .get(0).setEvidence("evidence".getBytes(StandardCharsets.UTF_8));
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    public void testIsEvidenceTriggerMessage_shouldBeTrue() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                .get(0).setEvidence(null);
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isTrue();
    }

    @Test
    public void testIsEvidenceTriggerMessage_businessMsg_shouldBeFalse() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createSimpleTestMessage();
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    void switchMessageDirection() {
        DomibusConnectorMessage origMsg = DomainEntityCreator.createSimpleTestMessage();
        DomibusConnectorMessageDetails swMsgDetails = DomainModelHelper.switchMessageDirection(DomainEntityCreator.createSimpleTestMessage().getMessageDetails());

        DomibusConnectorMessageDetails origMsgDetails = origMsg.getMessageDetails();

        //assert direction is switched
        assertThat(swMsgDetails.getDirection()).isEqualTo(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
        //assert that party is switched
        assertThat(swMsgDetails.getFromParty()).isEqualTo(origMsgDetails.getToParty());
        assertThat(swMsgDetails.getToParty()).isEqualTo(origMsgDetails.getFromParty());
        //assertThat final recipient/original sender is switched
        assertThat(swMsgDetails.getFinalRecipient()).isEqualTo(origMsgDetails.getOriginalSender());
        assertThat(swMsgDetails.getOriginalSender()).isEqualTo(origMsgDetails.getFinalRecipient());

    }
}
