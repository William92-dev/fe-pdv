
package com.gizlocorp.gnvoice.wsclient.retencion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para retencionRecibirRequest complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="retencionRecibirRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="comprobanteProveedor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "retencionRecibirRequest", propOrder = {
    "comprobanteProveedor"
})
public class RetencionRecibirRequest {

    protected String comprobanteProveedor;

    /**
     * Obtiene el valor de la propiedad comprobanteProveedor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComprobanteProveedor() {
        return comprobanteProveedor;
    }

    /**
     * Define el valor de la propiedad comprobanteProveedor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComprobanteProveedor(String value) {
        this.comprobanteProveedor = value;
    }

}
