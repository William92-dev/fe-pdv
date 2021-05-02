
package com.gizlocorp.gnvoice.wsclient.notadebito;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notaDebitoConsultarResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notaDebitoConsultarResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}gizloResponse">
 *       &lt;sequence>
 *         &lt;element name="notasDebito" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaDebito" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "notaDebitoConsultarResponse", propOrder = {
    "notasDebito"
})
public class NotaDebitoConsultarResponse
    extends GizloResponse
{

    protected NotaDebitoConsultarResponse.NotasDebito notasDebito;

    /**
     * Obtiene el valor de la propiedad notasDebito.
     * 
     * @return
     *     possible object is
     *     {@link NotaDebitoConsultarResponse.NotasDebito }
     *     
     */
    public NotaDebitoConsultarResponse.NotasDebito getNotasDebito() {
        return notasDebito;
    }

    /**
     * Define el valor de la propiedad notasDebito.
     * 
     * @param value
     *     allowed object is
     *     {@link NotaDebitoConsultarResponse.NotasDebito }
     *     
     */
    public void setNotasDebito(NotaDebitoConsultarResponse.NotasDebito value) {
        this.notasDebito = value;
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
     *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaDebito" maxOccurs="unbounded" minOccurs="0"/>
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
        "notaDebito"
    })
    public static class NotasDebito {

        @XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
        protected List<NotaDebito> notaDebito;

        /**
         * Gets the value of the notaDebito property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the notaDebito property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNotaDebito().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotaDebito }
         * 
         * 
         */
        public List<NotaDebito> getNotaDebito() {
            if (notaDebito == null) {
                notaDebito = new ArrayList<NotaDebito>();
            }
            return this.notaDebito;
        }

    }

}
