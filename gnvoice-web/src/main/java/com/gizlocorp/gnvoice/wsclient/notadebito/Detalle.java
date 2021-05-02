
package com.gizlocorp.gnvoice.wsclient.notadebito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para detalle complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="detalle">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="motivoModificacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "detalle", propOrder = {
    "motivoModificacion"
})
public class Detalle {

    @XmlElement(required = true)
    protected String motivoModificacion;

    /**
     * Obtiene el valor de la propiedad motivoModificacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivoModificacion() {
        return motivoModificacion;
    }

    /**
     * Define el valor de la propiedad motivoModificacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivoModificacion(String value) {
        this.motivoModificacion = value;
    }

}
