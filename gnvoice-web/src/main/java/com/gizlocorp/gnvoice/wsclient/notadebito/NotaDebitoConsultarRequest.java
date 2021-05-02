
package com.gizlocorp.gnvoice.wsclient.notadebito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notaDebitoConsultarRequest complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notaDebitoConsultarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaDebito" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notaDebitoConsultarRequest", propOrder = {
    "notaDebito"
})
public class NotaDebitoConsultarRequest {

    @XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
    protected NotaDebito notaDebito;

    /**
     * Obtiene el valor de la propiedad notaDebito.
     * 
     * @return
     *     possible object is
     *     {@link NotaDebito }
     *     
     */
    public NotaDebito getNotaDebito() {
        return notaDebito;
    }

    /**
     * Define el valor de la propiedad notaDebito.
     * 
     * @param value
     *     allowed object is
     *     {@link NotaDebito }
     *     
     */
    public void setNotaDebito(NotaDebito value) {
        this.notaDebito = value;
    }

}
