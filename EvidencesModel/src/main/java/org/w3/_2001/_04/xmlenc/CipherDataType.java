//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2012.08.21 um 12:08:20 PM CEST 
//


package org.w3._2001._04.xmlenc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r CipherDataType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CipherDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="CipherValue" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element ref="{http://www.w3.org/2001/04/xmlenc#}CipherReference"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CipherDataType", propOrder = {
    "cipherValue",
    "cipherReference"
})
public class CipherDataType {

    @XmlElement(name = "CipherValue")
    protected byte[] cipherValue;
    @XmlElement(name = "CipherReference")
    protected CipherReferenceType cipherReference;

    /**
     * Ruft den Wert der cipherValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getCipherValue() {
        return cipherValue;
    }

    /**
     * Legt den Wert der cipherValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setCipherValue(byte[] value) {
        this.cipherValue = value;
    }

    /**
     * Ruft den Wert der cipherReference-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CipherReferenceType }
     *     
     */
    public CipherReferenceType getCipherReference() {
        return cipherReference;
    }

    /**
     * Legt den Wert der cipherReference-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CipherReferenceType }
     *     
     */
    public void setCipherReference(CipherReferenceType value) {
        this.cipherReference = value;
    }

}
