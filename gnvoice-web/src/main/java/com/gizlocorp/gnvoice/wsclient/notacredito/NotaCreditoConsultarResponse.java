
package com.gizlocorp.gnvoice.wsclient.notacredito;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notaCreditoConsultarResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notaCreditoConsultarResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}gizloResponse">
 *       &lt;sequence>
 *         &lt;element name="notasCredito" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCredito" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notaCreditoConsultarResponse", propOrder = {
    "notasCredito"
})
public class NotaCreditoConsultarResponse
    extends GizloResponse
{

    protected NotaCreditoConsultarResponse.NotasCredito notasCredito;

    /**
     * Obtiene el valor de la propiedad notasCredito.
     * 
     * @return
     *     possible object is
     *     {@link NotaCreditoConsultarResponse.NotasCredito }
     *     
     */
    public NotaCreditoConsultarResponse.NotasCredito getNotasCredito() {
        return notasCredito;
    }

    /**
     * Define el valor de la propiedad notasCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link NotaCreditoConsultarResponse.NotasCredito }
     *     
     */
    public void setNotasCredito(NotaCreditoConsultarResponse.NotasCredito value) {
        this.notasCredito = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCredito" maxOccurs="unbounded" minOccurs="0"/>
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
        "notaCredito"
    })
    public static class NotasCredito {

        @XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
        protected List<NotaCredito> notaCredito;

        /**
         * Gets the value of the notaCredito property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the notaCredito property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNotaCredito().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotaCredito }
         * 
         * 
         */
        public List<NotaCredito> getNotaCredito() {
            if (notaCredito == null) {
                notaCredito = new ArrayList<NotaCredito>();
            }
            return this.notaCredito;
        }

    }

}
