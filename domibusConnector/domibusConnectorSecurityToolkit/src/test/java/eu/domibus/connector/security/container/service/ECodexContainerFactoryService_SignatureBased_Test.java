package eu.domibus.connector.security.container.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test.context.SecurityToolkitTestContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;


//@TestPropertySource({"classpath:test.properties", "classpath:test-sig.properties"})
@SpringBootTest(classes = SecurityToolkitTestContext.class)
@ActiveProfiles({"test", "test-sig", "seclib-test"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ECodexContainerFactoryService_SignatureBased_Test extends ECodexContainerFactoryServiceITCaseTemplate {

}