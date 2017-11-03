
package connector.domibus.eu.domibusconnectorgatewayservice._1_0;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.3
 * 2017-11-03T07:34:26.701+01:00
 * Generated source version: 3.1.3
 */

@WebFault(name = "DomibusConnectorGatewayServiceFault", targetNamespace = "http://eu.domibus.connector/DomibusConnectorGatewayService/1.0/")
public class RequestPendingMessagesFault extends Exception {
    
    private java.lang.String domibusConnectorGatewayServiceFault;

    public RequestPendingMessagesFault() {
        super();
    }
    
    public RequestPendingMessagesFault(String message) {
        super(message);
    }
    
    public RequestPendingMessagesFault(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestPendingMessagesFault(String message, java.lang.String domibusConnectorGatewayServiceFault) {
        super(message);
        this.domibusConnectorGatewayServiceFault = domibusConnectorGatewayServiceFault;
    }

    public RequestPendingMessagesFault(String message, java.lang.String domibusConnectorGatewayServiceFault, Throwable cause) {
        super(message, cause);
        this.domibusConnectorGatewayServiceFault = domibusConnectorGatewayServiceFault;
    }

    public java.lang.String getFaultInfo() {
        return this.domibusConnectorGatewayServiceFault;
    }
}
