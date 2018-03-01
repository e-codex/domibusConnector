package eu.domibus.connector.ws.gateway.delivery.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.2.1
 * 2018-03-01T09:43:04.447+01:00
 * Generated source version: 3.2.1
 * 
 */
@WebServiceClient(name = "DomibusConnectorGatewayDeliveryWSService", 
                  wsdlLocation = "file:/C:/Entwicklung/repos/connector/domibusConnector/domibusConnectorAPI/src/main/resources/wsdl/DomibusConnectorGatewayDeliveryWebService.wsdl",
                  targetNamespace = "http://connector.domibus.eu/ws/gateway/delivery/webservice/") 
public class DomibusConnectorGatewayDeliveryWSService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://connector.domibus.eu/ws/gateway/delivery/webservice/", "DomibusConnectorGatewayDeliveryWSService");
    public final static QName DomibusConnectorDeliveryWebService = new QName("http://connector.domibus.eu/ws/gateway/delivery/webservice/", "DomibusConnectorDeliveryWebService");
    static {
        URL url = null;
        try {
            url = new URL("file:/C:/Entwicklung/repos/connector/domibusConnector/domibusConnectorAPI/src/main/resources/wsdl/DomibusConnectorGatewayDeliveryWebService.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(DomibusConnectorGatewayDeliveryWSService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/C:/Entwicklung/repos/connector/domibusConnector/domibusConnectorAPI/src/main/resources/wsdl/DomibusConnectorGatewayDeliveryWebService.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public DomibusConnectorGatewayDeliveryWSService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public DomibusConnectorGatewayDeliveryWSService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DomibusConnectorGatewayDeliveryWSService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    public DomibusConnectorGatewayDeliveryWSService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public DomibusConnectorGatewayDeliveryWSService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public DomibusConnectorGatewayDeliveryWSService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    




    /**
     *
     * @return
     *     returns DomibusConnectorGatewayDeliveryWebService
     */
    @WebEndpoint(name = "DomibusConnectorDeliveryWebService")
    public DomibusConnectorGatewayDeliveryWebService getDomibusConnectorDeliveryWebService() {
        return super.getPort(DomibusConnectorDeliveryWebService, DomibusConnectorGatewayDeliveryWebService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DomibusConnectorGatewayDeliveryWebService
     */
    @WebEndpoint(name = "DomibusConnectorDeliveryWebService")
    public DomibusConnectorGatewayDeliveryWebService getDomibusConnectorDeliveryWebService(WebServiceFeature... features) {
        return super.getPort(DomibusConnectorDeliveryWebService, DomibusConnectorGatewayDeliveryWebService.class, features);
    }

}
