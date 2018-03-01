package eu.domibus.connector.ws.backend.delivery.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.2.1
 * 2018-03-01T09:43:04.751+01:00
 * Generated source version: 3.2.1
 * 
 */
@WebService(targetNamespace = "http://connector.domibus.eu/ws/backend/delivery/webservice/", name = "DomibusConnectorBackendDeliveryWebService")
@XmlSeeAlso({eu.domibus.connector.domain.transition.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface DomibusConnectorBackendDeliveryWebService {

    @WebMethod
    @WebResult(name = "deliverMessageResponse", targetNamespace = "", partName = "deliverMessageResponse")
    public eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType deliverMessage(
        @WebParam(partName = "deliverMessageRequest", name = "deliverMessageRequest", targetNamespace = "")
        eu.domibus.connector.domain.transition.DomibusConnectorMessageType deliverMessageRequest
    );
}
