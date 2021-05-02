package com.gizlocorp.gnvoice.wsclient.notadebito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para notaDebitoProcesarRequest complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notaDebitoProcesarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaDebito" minOccurs="0"/>
 *         &lt;element name="codigoExterno" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "notaDebitoProcesarRequest", propOrder = { "notaDebito", "codigoExterno",
		"correoElectronicoNotificacion" })
@XmlRootElement(name = "notaDebitoProcesarRequest")
public class NotaDebitoProcesarRequest {

	@XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
	protected NotaDebito notaDebito;
	@XmlElement(required = true)
	protected String codigoExterno;
	@XmlElement(required = true)
	protected String correoElectronicoNotificacion;

	/**
	 * Obtiene el valor de la propiedad notaDebito.
	 * 
	 * @return possible object is {@link NotaDebito }
	 * 
	 */
	public NotaDebito getNotaDebito() {
		return notaDebito;
	}

	/**
	 * Define el valor de la propiedad notaDebito.
	 * 
	 * @param value
	 *            allowed object is {@link NotaDebito }
	 * 
	 */
	public void setNotaDebito(NotaDebito value) {
		this.notaDebito = value;
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
