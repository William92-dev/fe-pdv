
package com.gizlocorp.gnvoice.wsclient.notacredito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notaCreditoConsultarRequest complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notaCreditoConsultarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCredito" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notaCreditoConsultarRequest", propOrder = {
    "notaCredito"
})
public class NotaCreditoConsultarRequest {

    @XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
    protected NotaCredito notaCredito;

    /**
     * Obtiene el valor de la propiedad notaCredito.
     * 
     * @return
     *     possible object is
     *     {@link NotaCredito }
     *     
     */
    public NotaCredito getNotaCredito() {
        return notaCredito;
    }

    /**
     * Define el valor de la propiedad notaCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link NotaCredito }
     *     
     */
    public void setNotaCredito(NotaCredito value) {
        this.notaCredito = value;
    }

}
