
package com.gizlocorp.gnvoice.firmaelectronica.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para firmaElectronicaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="firmaElectronicaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mensajecliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mensajesistema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rutaArchivoFirmado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "firmaElectronicaResponse", propOrder = {
    "estado",
    "mensajecliente",
    "mensajesistema",
    "rutaArchivoFirmado"
})
public class FirmaElectronicaResponse {

    protected String estado;
    protected String mensajecliente;
    protected String mensajesistema;
    protected String rutaArchivoFirmado;

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad mensajecliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensajecliente() {
        return mensajecliente;
    }

    /**
     * Define el valor de la propiedad mensajecliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensajecliente(String value) {
        this.mensajecliente = value;
    }

    /**
     * Obtiene el valor de la propiedad mensajesistema.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensajesistema() {
        return mensajesistema;
    }

    /**
     * Define el valor de la propiedad mensajesistema.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensajesistema(String value) {
        this.mensajesistema = value;
    }

    /**
     * Obtiene el valor de la propiedad rutaArchivoFirmado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRutaArchivoFirmado() {
        return rutaArchivoFirmado;
    }

    /**
     * Define el valor de la propiedad rutaArchivoFirmado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRutaArchivoFirmado(String value) {
        this.rutaArchivoFirmado = value;
    }

}
