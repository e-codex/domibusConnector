
package backend.ecodex.org;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.1
 * 2013-01-15T08:40:49.428+01:00
 * Generated source version: 2.6.1
 */

@WebFault(name = "FaultDetail", targetNamespace = "http://org.ecodex.backend")
public class SendMessageFault extends Exception {
    
    private backend.ecodex.org.FaultDetail faultDetail;

    public SendMessageFault() {
        super();
    }
    
    public SendMessageFault(String message) {
        super(message);
    }
    
    public SendMessageFault(String message, Throwable cause) {
        super(message, cause);
    }

    public SendMessageFault(String message, backend.ecodex.org.FaultDetail faultDetail) {
        super(message);
        this.faultDetail = faultDetail;
    }

    public SendMessageFault(String message, backend.ecodex.org.FaultDetail faultDetail, Throwable cause) {
        super(message, cause);
        this.faultDetail = faultDetail;
    }

    public backend.ecodex.org.FaultDetail getFaultInfo() {
        return this.faultDetail;
    }
}
