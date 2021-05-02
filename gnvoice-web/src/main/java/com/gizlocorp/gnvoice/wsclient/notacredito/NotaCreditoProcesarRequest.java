package com.gizlocorp.gnvoice.wsclient.notacredito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para notaCreditoProcesarRequest complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notaCreditoProcesarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCredito" minOccurs="0"/>
 *          &lt;element name="codigoExterno" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="correoElectronicoNotificacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notaCreditoProcesarRequest", propOrder = { "notaCredito","codigoExterno",
		"correoElectronicoNotificacion" })
@XmlRootElement(name = "notaCreditoProcesarRequest")
public class NotaCreditoProcesarRequest {

	@XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
	protected NotaCredito notaCredito;
	@XmlElement(required = true)
	protected String codigoExterno;
	@XmlElement(required = true)
	protected String correoElectronicoNotificacion;

	/**
	 * Obtiene el valor de la propiedad notaCredito.
	 * 
	 * @return possible object is {@link NotaCredito }
	 * 
	 */
	public NotaCredito getNotaCredito() {
		return notaCredito;
	}

	/**
	 * Define el valor de la propiedad notaCredito.
	 * 
	 * @param value
	 *            allowed object is {@link NotaCredito }
	 * 
	 */
	public void setNotaCredito(NotaCredito value) {
		this.notaCredito = value;
	}

	/**
	 * Obtiene el valor de la propiedad codigoExterno.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodigoExterno() {
		return codigoExterno;
	}

	/**
	 * Define el valor de la propiedad codigoExterno.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	/**
	 * Obtiene el valor de la propiedad correoElectronicoNotificacion.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCorreoElectronicoNotificacion() {
		return correoElectronicoNotificacion;
	}

	/**
	 * Define el valor de la propiedad correoElectronicoNotificacion.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCorreoElectronicoNotificacion(String value) {
		this.correoElectronicoNotificacion = value;
	}

}
