
package backend.ecodex.org._1_1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bodyload" type="{http://org.ecodex.backend/1_1/}PayloadType" minOccurs="0"/>
 *         &lt;element name="payload" type="{http://org.ecodex.backend/1_1/}PayloadType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "bodyload",
    "payload"
})
@XmlRootElement(name = "downloadMessageResponse")
public class DownloadMessageResponse {

    protected PayloadType bodyload;
    protected List<PayloadType> payload;

    /**
     * Ruft den Wert der bodyload-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PayloadType }
     *     
     */
    public PayloadType getBodyload() {
        return bodyload;
    }

    /**
     * Legt den Wert der bodyload-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PayloadType }
     *     
     */
    public void setBodyload(PayloadType value) {
        this.bodyload = value;
    }

    /**
     * Gets the value of the payload property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payload property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayload().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PayloadType }
     * 
     * 
     */
    public List<PayloadType> getPayload() {
        if (payload == null) {
            payload = new ArrayList<PayloadType>();
        }
        return this.payload;
    }

}
