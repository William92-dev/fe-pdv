package com.gizlocorp.gnvoice.firmaelectronica.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para firmaElectronicaRequest complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="firmaElectronicaRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clave" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rutaArchivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rutaFirma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *           &lt;element name="lote" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "firmaElectronicaRequest", propOrder = { "clave",
		"rutaArchivo", "rutaFirma", "lote" })
public class FirmaElectronicaRequest {

	protected String clave;
	protected String rutaArchivo;
	protected String rutaFirma;
	protected Boolean lote;

	/**
	 * Obtiene el valor de la propiedad clave.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * Define el valor de la propiedad clave.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setClave(String value) {
		this.clave = value;
	}

	/**
	 * Obtiene el valor de la propiedad rutaArchivo.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRutaArchivo() {
		return rutaArchivo;
	}

	/**
	 * Define el valor de la propiedad rutaArchivo.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRutaArchivo(String value) {
		this.rutaArchivo = value;
	}

	/**
	 * Obtiene el valor de la propiedad rutaFirma.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRutaFirma() {
		return rutaFirma;
	}

	/**
	 * Define el valor de la propiedad rutaFirma.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRutaFirma(String value) {
		this.rutaFirma = value;
	}

	public Boolean getLote() {
		return lote;
	}

	public void setLote(Boolean lote) {
		this.lote = lote;
	}

}
