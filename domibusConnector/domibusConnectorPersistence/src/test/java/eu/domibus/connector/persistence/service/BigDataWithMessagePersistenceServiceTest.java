package eu.domibus.connector.persistence.service;

import eu.domibus.connector.persistence.service.impl.BigDataWithMessagePersistenceContentManagerImpl;
import eu.domibus.connector.domain.transformer.util.LargeFileReferenceMemoryBacked;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 *
 *
 */
@Disabled("not active")
public class BigDataWithMessagePersistenceServiceTest {
    
    @Mock
    private LargeFilePersistenceService bigDataPersistenceServiceImpl;

    DomibusConnectorMessageContentManager persistenceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        BigDataWithMessagePersistenceContentManagerImpl impl = new BigDataWithMessagePersistenceContentManagerImpl();
        impl.setLargeFilePersistenceService(bigDataPersistenceServiceImpl);
        persistenceService = impl;
    }
    
    private static LargeFileReferenceMemoryBacked generateNewDomibusConnectorBigDataReferenceMemoryBacked() {
        LargeFileReferenceMemoryBacked ref = new LargeFileReferenceMemoryBacked();
        ref.setStorageIdReference(UUID.randomUUID().toString());
        return ref;
    }

//    @Test(timeout=20000)
//    public void testPersistAllBigFilesFromMessage() {
//        DomibusConnectorMessage msg = DomainEntityCreatorForPersistenceTests.createMessage();
//        
//        Mockito.when(bigDataPersistenceServiceImpl.createDomibusConnectorBigDataReference(any(DomibusConnectorMessage.class)))
//                .thenReturn(generateNewDomibusConnectorBigDataReferenceMemoryBacked());
//                
//        persistenceService.persistAllBigFilesFromMessage(msg);
//        
//        // 1 attachment and one document should saved to db makes 2 calls
//        Mockito.verify(bigDataPersistenceServiceImpl, Mockito.times(2)).createDomibusConnectorBigDataReference(any(DomibusConnectorMessage.class));
//    }

//    @Test
//    public void testLoadAllBigFilesFromMessage() {
//        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
//            DomibusConnectorMessage msg = DomainEntityCreatorForPersistenceTests.createMessage();
//            persistenceService.loadAllBigFilesFromMessage(msg);
//
//            // 2 attachments should be load from db: one document, one attachment
//            Mockito.verify(bigDataPersistenceServiceImpl, Mockito.times(2)).getReadableDataSource(any(LargeFileReference.class));
//        });
//    }
    
}
