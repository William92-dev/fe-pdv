
package com.gizlocorp.gnvoice.wsclient.guia;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para destinatario complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="destinatario">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificacionDestinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="razonSocialDestinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dirDestinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="motivoTraslado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="docAduaneroUnico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codEstabDestino" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codDocSustento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numDocSustento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numAutDocSustento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaEmisionDocSustento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="detalles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="detalle" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}detalle" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "destinatario", propOrder = {
    "identificacionDestinatario",
    "razonSocialDestinatario",
    "dirDestinatario",
    "motivoTraslado",
    "docAduaneroUnico",
    "codEstabDestino",
    "ruta",
    "codDocSustento",
    "numDocSustento",
    "numAutDocSustento",
    "fechaEmisionDocSustento",
    "detalles"
})
public class Destinatario {

    protected String identificacionDestinatario;
    @XmlElement(required = true)
    protected String razonSocialDestinatario;
    @XmlElement(required = true)
    protected String dirDestinatario;
    @XmlElement(required = true)
    protected String motivoTraslado;
    protected String docAduaneroUnico;
    protected String codEstabDestino;
    protected String ruta;
    protected String codDocSustento;
    protected String numDocSustento;
    protected String numAutDocSustento;
    protected String fechaEmisionDocSustento;
    @XmlElement(required = true)
    protected Destinatario.Detalles detalles;

    /**
     * Obtiene el valor de la propiedad identificacionDestinatario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificacionDestinatario() {
        return identificacionDestinatario;
    }

    /**
     * Define el valor de la propiedad identificacionDestinatario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificacionDestinatario(String value) {
        this.identificacionDestinatario = value;
    }

    /**
     * Obtiene el valor de la propiedad razonSocialDestinatario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRazonSocialDestinatario() {
        return razonSocialDestinatario;
    }

    /**
     * Define el valor de la propiedad razonSocialDestinatario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRazonSocialDestinatario(String value) {
        this.razonSocialDestinatario = value;
    }

    /**
     * Obtiene el valor de la propiedad dirDestinatario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirDestinatario() {
        return dirDestinatario;
    }

    /**
     * Define el valor de la propiedad dirDestinatario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirDestinatario(String value) {
        this.dirDestinatario = value;
    }

    /**
     * Obtiene el valor de la propiedad motivoTraslado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivoTraslado() {
        return motivoTraslado;
    }

    /**
     * Define el valor de la propiedad motivoTraslado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivoTraslado(String value) {
        this.motivoTraslado = value;
    }

    /**
     * Obtiene el valor de la propiedad docAduaneroUnico.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocAduaneroUnico() {
        return docAduaneroUnico;
    }

    /**
     * Define el valor de la propiedad docAduaneroUnico.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocAduaneroUnico(String value) {
        this.docAduaneroUnico = value;
    }

    /**
     * Obtiene el valor de la propiedad codEstabDestino.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodEstabDestino() {
        return codEstabDestino;
    }

    /**
     * Define el valor de la propiedad codEstabDestino.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodEstabDestino(String value) {
        this.codEstabDestino = value;
    }

    /**
     * Obtiene el valor de la propiedad ruta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * Define el valor de la propiedad ruta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuta(String value) {
        this.ruta = value;
    }

    /**
     * Obtiene el valor de la propiedad codDocSustento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodDocSustento() {
        return codDocSustento;
    }

    /**
     * Define el valor de la propiedad codDocSustento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodDocSustento(String value) {
        this.codDocSustento = value;
    }

    /**
     * Obtiene el valor de la propiedad numDocSustento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumDocSustento() {
        return numDocSustento;
    }

    /**
     * Define el valor de la propiedad numDocSustento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumDocSustento(String value) {
        this.numDocSustento = value;
    }

    /**
     * Obtiene el valor de la propiedad numAutDocSustento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumAutDocSustento() {
        return numAutDocSustento;
    }

    /**
     * Define el valor de la propiedad numAutDocSustento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumAutDocSustento(String value) {
        this.numAutDocSustento = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaEmisionDocSustento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    /**
     * Define el valor de la propiedad fechaEmisionDocSustento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaEmisionDocSustento(String value) {
        this.fechaEmisionDocSustento = value;
    }

    /**
     * Obtiene el valor de la propiedad detalles.
     * 
     * @return
     *     possible object is
     *     {@link Destinatario.Detalles }
     *     
     */
    public Destinatario.Detalles getDetalles() {
        return detalles;
    }

    /**
     * Define el valor de la propiedad detalles.
     * 
     * @param value
     *     allowed object is
     *     {@link Destinatario.Detalles }
     *     
     */
    public void setDetalles(Destinatario.Detalles value) {
        this.detalles = value;
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
     *         &lt;element name="detalle" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}detalle" maxOccurs="unbounded"/>
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
        "detalle"
    })
    public static class Detalles {

        @XmlElement(required = true)
        protected List<Detalle> detalle;

        /**
         * Gets the value of the detalle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the detalle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDetalle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Detalle }
         * 
         * 
         */
        public List<Detalle> getDetalle() {
            if (detalle == null) {
                detalle = new ArrayList<Detalle>();
            }
            return this.detalle;
        }

    }

}
