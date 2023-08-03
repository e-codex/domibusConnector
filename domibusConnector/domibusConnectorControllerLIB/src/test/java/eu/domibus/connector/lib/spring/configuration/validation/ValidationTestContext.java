package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.common.service.DCKeyStoreServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Import(DCKeyStoreServiceImpl.class)
public class ValidationTestContext {


}
