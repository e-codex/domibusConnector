
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für From complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="From">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PartyId" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartyId" maxOccurs="unbounded"/>
 *         &lt;element name="Role" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}non-empty-string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "From", propOrder = {
    "partyId",
    "role"
})
public class From {

    @XmlElement(name = "PartyId", required = true)
    protected List<PartyId> partyId;
    @XmlElement(name = "Role", required = true)
    protected String role;

    /**
     * Gets the value of the partyId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partyId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartyId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartyId }
     * 
     * 
     */
    public List<PartyId> getPartyId() {
        if (partyId == null) {
            partyId = new ArrayList<PartyId>();
        }
        return this.partyId;
    }

    /**
     * Ruft den Wert der role-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Legt den Wert der role-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

}
