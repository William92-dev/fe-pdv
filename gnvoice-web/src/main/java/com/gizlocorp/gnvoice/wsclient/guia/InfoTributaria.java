
package com.gizlocorp.gnvoice.wsclient.guia;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para infoTributaria complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="infoTributaria">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ambiente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="razonSocial" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombreComercial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="claveAcceso" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codDoc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="estab" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ptoEmi" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="secuencial" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dirMatriz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoTributaria", propOrder = {
    "ambiente",
    "tipoEmision",
    "razonSocial",
    "nombreComercial",
    "ruc",
    "claveAcceso",
    "codDoc",
    "estab",
    "ptoEmi",
    "secuencial",
    "dirMatriz"
})
public class InfoTributaria {

    @XmlElement(required = true)
    protected String ambiente;
    @XmlElement(required = true)
    protected String tipoEmision;
    @XmlElement(required = true)
    protected String razonSocial;
    protected String nombreComercial;
    @XmlElement(required = true)
    protected String ruc;
    @XmlElement(required = true)
    protected String claveAcceso;
    @XmlElement(required = true)
    protected String codDoc;
    @XmlElement(required = true)
    protected String estab;
    @XmlElement(required = true)
    protected String ptoEmi;
    @XmlElement(required = true)
    protected String secuencial;
    @XmlElement(required = true)
    protected String dirMatriz;

    /**
     * Obtiene el valor de la propiedad ambiente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmbiente() {
        return ambiente;
    }

    /**
     * Define el valor de la propiedad ambiente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmbiente(String value) {
        this.ambiente = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoEmision.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoEmision() {
        return tipoEmision;
    }

    /**
     * Define el valor de la propiedad tipoEmision.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoEmision(String value) {
        this.tipoEmision = value;
    }

    /**
     * Obtiene el valor de la propiedad razonSocial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRazonSocial() {
        return razonSocial;
    }

    /**
     * Define el valor de la propiedad razonSocial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRazonSocial(String value) {
        this.razonSocial = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreComercial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreComercial() {
        return nombreComercial;
    }

    /**
     * Define el valor de la propiedad nombreComercial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreComercial(String value) {
        this.nombreComercial = value;
    }

    /**
     * Obtiene el valor de la propiedad ruc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuc() {
        return ruc;
    }

    /**
     * Define el valor de la propiedad ruc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuc(String value) {
        this.ruc = value;
    }

    /**
     * Obtiene el valor de la propiedad claveAcceso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveAcceso() {
        return claveAcceso;
    }

    /**
     * Define el valor de la propiedad claveAcceso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveAcceso(String value) {
        this.claveAcceso = value;
    }

    /**
     * Obtiene el valor de la propiedad codDoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodDoc() {
        return codDoc;
    }

    /**
     * Define el valor de la propiedad codDoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodDoc(String value) {
        this.codDoc = value;
    }

    /**
     * Obtiene el valor de la propiedad estab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstab() {
        return estab;
    }

    /**
     * Define el valor de la propiedad estab.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstab(String value) {
        this.estab = value;
    }

    /**
     * Obtiene el valor de la propiedad ptoEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPtoEmi() {
        return ptoEmi;
    }

    /**
     * Define el valor de la propiedad ptoEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPtoEmi(String value) {
        this.ptoEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad secuencial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecuencial() {
        return secuencial;
    }

    /**
     * Define el valor de la propiedad secuencial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecuencial(String value) {
        this.secuencial = value;
    }

    /**
     * Obtiene el valor de la propiedad dirMatriz.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirMatriz() {
        return dirMatriz;
    }

    /**
     * Define el valor de la propiedad dirMatriz.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirMatriz(String value) {
        this.dirMatriz = value;
    }

}
