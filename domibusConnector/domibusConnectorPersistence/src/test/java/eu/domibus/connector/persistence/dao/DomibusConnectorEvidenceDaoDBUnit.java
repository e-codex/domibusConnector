
package eu.domibus.connector.persistence.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.persistence.model.enums.EvidenceType;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@CommonPersistenceTest
@DataSet(value = "/database/testdata/dbunit/DomibusConnectorEvidence.xml", cleanAfter = true, cleanBefore = true)
public class DomibusConnectorEvidenceDaoDBUnit {

    @Autowired
    private DomibusConnectorEvidenceDao evidenceDao;

    @Autowired
    private DomibusConnectorMessageDao messageDao;

    @Autowired
    private DatabaseDataSourceConnection ddsc;
    
////    @BeforeEach
//    public void setUp() throws Exception {
//        //Load testdata
//        IDataSet dataSet = new FlatXmlDataSetBuilder()
//                .setColumnSensing(true)
//                .build((new ClassPathResource("database/testdata/dbunit/DomibusConnectorEvidence.xml").getInputStream()));
//
//        DatabaseDataSourceConnection conn = ddsc;
//        DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
//
//    }

    @Test
    public void testFindEvidencesForMessage() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {

            List<PDomibusConnectorEvidence> evidences = evidenceDao.findByMessage_Id(73L);

            assertThat(evidences).hasSize(3);
        });
    }

    @Test
    public void testSetDeliveredToGateway() throws SQLException, AmbiguousTableNameException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            int result = evidenceDao.setDeliveredToGateway(82L);
            assertThat(result).isEqualTo(1); //check on row updated

            //check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=82");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
            Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_GW");
            assertThat(value).isCloseTo(new Date(), 2000);
        });
    }

    @Test
    public void testSetDeliveredToGateway_updateNonExistant_shouldReturnZero() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            int result = evidenceDao.setDeliveredToGateway(882L);
            assertThat(result).isEqualTo(0); //check on row updated
        });
    }

//    @Test(timeout=20000)
//    public void testSetDeliveredToGateway_ByMessageIdAndType() throws SQLException, DataSetException {
//        PDomibusConnectorMessage dbMessage = new PDomibusConnectorMessage();
//        dbMessage.setId(73L);
//        int result = evidenceDao.setDeliveredToGateway(dbMessage, EvidenceType.SUBMISSION_REJECTION);
//
//        assertThat(result).isEqualTo(1);
//        //check result in DB
//        DatabaseDataSourceConnection conn = new DatabaseDataSourceConnection(ds);
//        QueryDataSet dataSet = new QueryDataSet(conn);
//        dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=82");
//
//        ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
//        Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_GW");
//        assertThat(value).isNotNull();
//        assertThat(value).isCloseTo(new Date(), 2000);
//    }


//    @Test(timeout=20000)
//    public void testSetDeliveredToBackend() throws SQLException, AmbiguousTableNameException, DataSetException {
//        int result = evidenceDao.setDeliveredToBackend(83L);
//        assertThat(result).isEqualTo(1); //check one row updated
//
//         //check result in DB
//        DatabaseDataSourceConnection conn = new DatabaseDataSourceConnection(ds);
//        DatabaseConfig config = conn.getConfig();
//        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
//
//        QueryDataSet dataSet = new QueryDataSet(conn);
//        dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=83");
//
//        ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
//        Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_NAT");
//        assertThat(value).isCloseTo(new Date(), 2000);
//    }

    @Test
    public void testSetDeliveredToBackend_ByMessageIdAndType() throws SQLException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            PDomibusConnectorMessage dbMessage = new PDomibusConnectorMessage();
            dbMessage.setId(74L);
            int result = evidenceDao.setDeliveredToBackend(dbMessage, EvidenceType.SUBMISSION_ACCEPTANCE);

            assertThat(result).isEqualTo(1);
            //check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=85");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
            Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_NAT");
            assertThat(value).isNotNull();
            assertThat(value).isCloseTo(new Date(), 2000);

            conn.close();
        });
    }


//    @Test(timeout=20000)
//    public void testSetDeliveredToBackend_updateNoneExistant_shouldReturnZero() {
//        int result = evidenceDao.setDeliveredToBackend(83231L);
//        assertThat(result).isEqualTo(0); //check one row updated
//    }

    @Test
    public void testSaveEvidence() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            PDomibusConnectorEvidence dbEvidence = new PDomibusConnectorEvidence();

            PDomibusConnectorMessage dbMessage = messageDao.findById(75L).get();
            assertThat(dbMessage).isNotNull();

            byte[] evidence = "Hallo Welt".getBytes();

            dbEvidence.setMessage(dbMessage);
            dbEvidence.setEvidence(new String(evidence));
            dbEvidence.setType(eu.domibus.connector.persistence.model.enums.EvidenceType.DELIVERY);
            dbEvidence.setDeliveredToGateway(null);
            dbEvidence.setDeliveredToNationalSystem(null);
            dbEvidence.setConnectorMessageId("testid");

            evidenceDao.save(dbEvidence);
        });
    }


}
