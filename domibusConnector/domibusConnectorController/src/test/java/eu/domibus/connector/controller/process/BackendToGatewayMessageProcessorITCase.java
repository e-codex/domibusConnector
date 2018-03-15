package eu.domibus.connector.controller.process;

import eu.domibus.connector.controller.exception.DomibusConnectorGatewaySubmissionException;
import eu.domibus.connector.controller.test.util.ITCaseTestContext;
import eu.domibus.connector.controller.test.util.LoadStoreMessageFromPath;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorBigDataReference;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.persistence.service.DomibusConnectorMessagePersistenceService;
import eu.domibus.connector.persistence.service.impl.BigDataWithMessagePersistenceService;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ITCaseTestContext.class})
@TestPropertySource("classpath:application-test.properties")
@Commit
@ActiveProfiles("ITCaseTestContext")
@Sql(scripts = "/testdata.sql") //adds testdata to database like domibus-blue party
public class BackendToGatewayMessageProcessorITCase {


    @Autowired
    @Qualifier("BackendToGatewayMessageProcessor")
    private DomibusConnectorMessageProcessor backendToGatewayMessageProcessor;

    @Autowired
    @Qualifier(ITCaseTestContext.TO_GW_DELIVERD_MESSAGES_LIST_BEAN_NAME)
    private List<DomibusConnectorMessage> toGatewayDeliveredMessages;

    @Autowired
    @Qualifier(ITCaseTestContext.TO_BACKEND_DELIVERD_MESSAGES_LIST_BEAN_NAME)
    private List<DomibusConnectorMessage> toBackendDeliveredMessages;

    @Autowired
    @Qualifier("GatewayToBackendMessageProcessor")
    private DomibusConnectorMessageProcessor gatewayToBackendMessageProcessor;

    @Autowired
    private DomibusConnectorMessagePersistenceService messagePersistenceService;

    @Autowired
    private BigDataWithMessagePersistenceService bigDataWithMessagePersistenceService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    ITCaseTestContext.DomibusConnectorGatewaySubmissionServiceInterceptor submissionServiceInterceptor;

    @Test
    public void testProcessMessage() throws IOException, DataSetException, SQLException {
        
        DomibusConnectorMessage message = prepareMessage("msg1");

        backendToGatewayMessageProcessor.processMessage(message);

        assertThat(toGatewayDeliveredMessages).hasSize(1);

        DomibusConnectorMessage msg = toGatewayDeliveredMessages.get(0);

        //verify DB
        DatabaseDataSourceConnection conn = new DatabaseDataSourceConnection(dataSource);
        QueryDataSet dataSet = new QueryDataSet(conn);
        dataSet.addTable("DOMIBUS_CONNECTOR_BIGDATA", "SELECT * FROM DOMIBUS_CONNECTOR_BIGDATA");
        ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_BIGDATA");
        FlatXmlDataSet.write(dataSet, System.out);
//        Object content = domibusConnectorTable.getValue(2, "CONTENT");
//        assertThat(content).isNotNull();
//        content = domibusConnectorTable.getValue(4, "CONTENT");
//        assertThat(content).isNotNull();



//        //write by gw rcv message to file system
//        FileSystemUtils.deleteRecursively(new File("./target/testm/"));
//        msg = bigDataWithMessagePersistenceService.loadAllBigFilesFromMessage(msg);
//        LoadStoreMessageFromPath.storeMessageTo(new FileSystemResource("./target/testm/"), msg);


//        System.out.println("#############\n#############\n#############\n#############\nOTHER WAY!!!!");
//
//        bigDataWithMessagePersistenceService.loadAllBigFilesFromMessage(msg);
//
//        gatewayToBackendMessageProcessor.processMessage(msg);
    }

    /*
     * Test send to GW, but gw fails
     * check that SUBMISSION_REJECTION evidence exists in db AND is sent to backend!
     */
    @Test
    public void testProcessMessage_sendToGwFails() throws IOException, DataSetException, SQLException, DomibusConnectorGatewaySubmissionException {

        Mockito.doThrow(new DomibusConnectorGatewaySubmissionException("a error occured!"))
                .when(submissionServiceInterceptor).submitToGateway(Mockito.any(DomibusConnectorMessage.class));


        DomibusConnectorMessage message = prepareMessage("msg2");
        try {
            backendToGatewayMessageProcessor.processMessage(message);
        } catch (Exception e) {

        }

        assertThat(toBackendDeliveredMessages).hasSize(1);

        //assertThat(toGatewayDeliveredMessages).hasSize(1);
//        DomibusConnectorMessage msg = toGatewayDeliveredMessages.get(0);

        //verify DB
        DatabaseDataSourceConnection conn = new DatabaseDataSourceConnection(dataSource);
        QueryDataSet dataSet = new QueryDataSet(conn);
        dataSet.addTable("DOMIBUS_CONNECTOR_MESSAGE ", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE ");
        ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE ");
        FlatXmlDataSet.write(dataSet, System.out);

        QueryDataSet evidenceDataSet = new QueryDataSet(conn);
        evidenceDataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE  ", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE  ");
        ITable evidenceTable = evidenceDataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE  ");
        FlatXmlDataSet.write(evidenceDataSet, System.out);

        Object type = evidenceTable.getValue(1, "TYPE");
        assertThat(type).isEqualTo("SUBMISSION_REJECTION");

    }


    private DomibusConnectorMessage prepareMessage(String idsuffix) {
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.getMessageDetails().setEbmsMessageId(null);
        message.getMessageDetails().setBackendMessageId("backend_" + idsuffix);
        message.getMessageDetails().setConnectorBackendClientName("bob");
        message.setConnectorMessageId("msg_" + idsuffix);
        message.getMessageConfirmations().clear();


        DomibusConnectorBigDataReference bigDataReference = LoadStoreMessageFromPath.loadResourceAsBigDataRef(new ClassPathResource("testmessages/testdata/ExamplePdfSigned.pdf"));
        DomibusConnectorMessageDocument signedDocument = DomibusConnectorMessageDocumentBuilder.createBuilder()
                .setContent(bigDataReference)
                .setName("SignedDocument")
                .build();
        message.getMessageContent().setDocument(signedDocument);

        message = messagePersistenceService.persistMessageIntoDatabase(message, DomibusConnectorMessageDirection.NAT_TO_GW);
        message = bigDataWithMessagePersistenceService.persistAllBigFilesFromMessage(message);
        message = messagePersistenceService.mergeMessageWithDatabase(message);
        return message;
    }
}
