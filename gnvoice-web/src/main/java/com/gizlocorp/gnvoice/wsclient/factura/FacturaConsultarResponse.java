
package com.gizlocorp.gnvoice.wsclient.factura;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para facturaConsultarResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="facturaConsultarResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}gizloResponse">
 *       &lt;sequence>
 *         &lt;element name="facturas" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}factura" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "facturaConsultarResponse", propOrder = {
    "facturas"
})
public class FacturaConsultarResponse
    extends GizloResponse
{

    protected FacturaConsultarResponse.Facturas facturas;

    /**
     * Obtiene el valor de la propiedad facturas.
     * 
     * @return
     *     possible object is
     *     {@link FacturaConsultarResponse.Facturas }
     *     
     */
    public FacturaConsultarResponse.Facturas getFacturas() {
        return facturas;
    }

    /**
     * Define el valor de la propiedad facturas.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaConsultarResponse.Facturas }
     *     
     */
    public void setFacturas(FacturaConsultarResponse.Facturas value) {
        this.facturas = value;
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
     *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}factura" maxOccurs="unbounded" minOccurs="0"/>
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
        "factura"
    })
    public static class Facturas {

        @XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
        protected List<Factura> factura;

        /**
         * Gets the value of the factura property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the factura property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFactura().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Factura }
         * 
         * 
         */
        public List<Factura> getFactura() {
            if (factura == null) {
                factura = new ArrayList<Factura>();
            }
            return this.factura;
        }

    }

}
