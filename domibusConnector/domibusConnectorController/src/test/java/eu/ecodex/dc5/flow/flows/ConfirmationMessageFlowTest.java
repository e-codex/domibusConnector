package eu.ecodex.dc5.flow.flows;

import eu.domibus.connector.controller.service.SubmitToLinkService;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.security.DomibusConnectorSecurityToolkit;
import eu.ecodex.dc5.domain.CurrentBusinessDomain;
import eu.ecodex.dc5.message.model.DC5BusinessMessageState;
import eu.ecodex.dc5.message.model.DC5Message;
import eu.ecodex.dc5.message.repo.DC5MessageRepo;
import eu.ecodex.dc5.process.MessageProcessManager;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@FlowTestAnnotation
@Log4j2
class ConfirmationMessageFlowTest {

    @Autowired
    ConfirmationMessageFlow confirmationMessageFlow;

    @Autowired
    DC5MessageRepo messageRepo;

    @Autowired
    PlatformTransactionManager txManager;

    @MockBean
    DomibusConnectorSecurityToolkit securityToolkit;

    @MockBean
    SubmitToLinkService submitToLinkService;

    @Autowired
    MessageProcessManager messageProcessManager;


    @Before
    public void before() {
//        Mockito.when(securityToolkit.validateContainer(Mockito.any())).thenAnswer(a ->a.getArgument(0));
//        Mockito.when(securityToolkit.buildContainer(Mockito.any())).thenAnswer(a ->a.getArgument(0));
    }


    public DC5Message createMessage() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        return txTemplate.execute(state -> {
            DC5Message dc5Message = DomainEntityCreator.createAlreadyOutgoneEpoFormAMessage();
            dc5Message.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultBusinessDomainId());

            DC5Message m = messageRepo.save(dc5Message);
            return m;
        });
    }


    public DC5Message createRelayRemmdRejectMessage(DC5Message refTo) {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        return txTemplate.execute(state -> {
            DC5Message dc5Message = DomainEntityCreator.createRelayRemmdRejectEvidenceForMessage(refTo);
            dc5Message.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultBusinessDomainId());

            DC5Message m = messageRepo.save(dc5Message);
            return m;
        });
    }
    public DC5Message createRelayRemmdAcceptMessage(DC5Message refTo) {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        return txTemplate.execute(state -> {
            DC5Message dc5Message = DomainEntityCreator.createRelayRemmdAcceptanceEvidenceForMessage(refTo);
            dc5Message.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultBusinessDomainId());

            DC5Message m = messageRepo.save(dc5Message);
            return m;
        });
    }

    @Test
    public void testRelayRemmdAcceptanceRcv() {
        DC5Message message = createMessage();

        Long msgId = createRelayRemmdAcceptMessage(message).getId();

        messageProcessManager.startProcess();

        TransactionTemplate txTemplate = new TransactionTemplate(txManager);

        txTemplate.execute(state -> {
            DC5Message msg = messageRepo.getById(msgId);
            CurrentBusinessDomain.setCurrentBusinessDomain(DomibusConnectorBusinessDomain.getDefaultBusinessDomainId());
            confirmationMessageFlow.processMessage(msg);
            return msg;
        });

        txTemplate.executeWithoutResult(s -> {
            DC5Message businessMsg = messageRepo.getById(message.getId());

            assertThat(businessMsg.getMessageContent().getCurrentState().getState())
                    .as("Message state must be relayed")
                    .isEqualTo(DC5BusinessMessageState.BusinessMessagesStates.RELAYED);

        });

    }

    @Test
    public void testRelayRemmdRejectRcv() {
        DC5Message message = createMessage();

        Long msgId = createRelayRemmdRejectMessage(message).getId();

        messageProcessManager.startProcess();

        TransactionTemplate txTemplate = new TransactionTemplate(txManager);

        txTemplate.execute(state -> {
            DC5Message msg = messageRepo.getById(msgId);
            CurrentBusinessDomain.setCurrentBusinessDomain(DomibusConnectorBusinessDomain.getDefaultBusinessDomainId());
            confirmationMessageFlow.processMessage(msg);
            return msg;
        });

        txTemplate.executeWithoutResult(s -> {
            DC5Message businessMsg = messageRepo.getById(message.getId());

            assertThat(businessMsg.getMessageContent().getCurrentState().getState())
                    .as("Message state must be reject")
                    .isEqualTo(DC5BusinessMessageState.BusinessMessagesStates.REJECTED);

        });

    }

}