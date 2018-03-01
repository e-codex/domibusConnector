package eu.domibus.connector.gateway.link.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import eu.domibus.connector.controller.service.DomibusConnectorGatewayDeliveryService;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.testutil.TransitionCreator;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWSService;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;

/**
 * a simple test to check if the webservice (DomibusConnectorDeliveryWS) is published and reachable
 * with the current spring configuration
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */

@RunWith(SpringRunner.class)
@Import(GatewayLinkContextConfigurationITCase.TestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"gw-ws-link"})
public class GatewayLinkContextConfigurationITCase {
    
    @SpringBootApplication(scanBasePackages="eu.domibus.connector.gateway.link")
    public static class TestConfiguration {
        
    }
    
    @LocalServerPort //use with WebEnvironment.RANDOM_PORT
    int port;
    
    @Value("${spring.webservices.path}")
    String webservicesPath;

    @Autowired
    GatewayLinkWsServiceProperties gatewayLinkWsServiceProperties;
    
    @MockBean
    DomibusConnectorGatewayDeliveryService controllerDeliveryService;
    
    
    @Test
    public void testCallSoapService() throws InterruptedException, MalformedURLException {
        
        String publish = gatewayLinkWsServiceProperties.getPublishAddress(); 
        
        String url = "http://localhost:" + port + webservicesPath + "/" + publish;
        System.out.println("URL " + url);
        
        System.out.println("sleep started server port " + port);
        Thread.sleep(300);
        System.out.println("sleep ended, calling service");        
        
        URL wsdlURL = new URL(url + "?wsdl"); 
        QName SERVICE_NAME = DomibusConnectorGatewayDeliveryWSService.DomibusConnectorDeliveryWebService;
        
        MTOMFeature mtom = new MTOMFeature();

        Service service = Service.create(wsdlURL, SERVICE_NAME, mtom);

        DomibusConnectorGatewayDeliveryWebService client = service.getPort(DomibusConnectorGatewayDeliveryWebService.class);
        
        DomibusConnectorMessageType msg = TransitionCreator.createMessage();
        
        DomibsConnectorAcknowledgementType response = client.deliverMessage(msg);
        
        System.out.println("RESPONSE result: " + response.isResult());
        
        assertThat(response.isResult()).isTrue();
        
        
    }
    
}
