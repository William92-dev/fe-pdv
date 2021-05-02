package com.gizlocorp.gnvoice.xml.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito;

/**
 * <p>
 * Java class for notaCreditoReceptarRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="notaCreditoReceptarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCredito" minOccurs="0"/>
 *         &lt;element name="codigoExternoComprobante" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="correoElectronicoNotificacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificadorUsuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoAgencia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "notaCreditoReceptarRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class NotaCreditoReceptarRequest {

	@XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
	protected NotaCredito notaCredito;
	@XmlElement(required = true)
	protected String codigoExternoComprobante;
	@XmlElement(required = true)
	protected String correoElectronicoNotificacion;
	@XmlElement(required = true)
	protected String identificadorUsuario;
	@XmlElement(required = true)
	protected String codigoAgencia;

	/**
	 * Gets the value of the notaCredito property.
	 * 
	 * @return possible object is {@link NotaCredito }
	 * 
	 */
	public NotaCredito getNotaCredito() {
		return notaCredito;
	}

	/**
	 * Sets the value of the notaCredito property.
	 * 
	 * @param value
	 *            allowed object is {@link NotaCredito }
	 * 
	 */
	public void setNotaCredito(NotaCredito value) {
		this.notaCredito = value;
	}

	/**
	 * Gets the value of the codigoExternoComprobante property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodigoExternoComprobante() {
		return codigoExternoComprobante;
	}

	/**
	 * Sets the value of the codigoExternoComprobante property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodigoExternoComprobante(String value) {
		this.codigoExternoComprobante = value;
	}

	/**
	 * Gets the value of the correoElectronicoNotificacion property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCorreoElectronicoNotificacion() {
		return correoElectronicoNotificacion;
	}

	/**
	 * Sets the value of the correoElectronicoNotificacion property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCorreoElectronicoNotificacion(String value) {
		this.correoElectronicoNotificacion = value;
	}

	/**
	 * Gets the value of the identificadorUsuario property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdentificadorUsuario() {
		return identificadorUsuario;
	}

	/**
	 * Sets the value of the identificadorUsuario property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdentificadorUsuario(String value) {
		this.identificadorUsuario = value;
	}

	/**
	 * Gets the value of the codigoAgencia property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodigoAgencia() {
		return codigoAgencia;
	}

	/**
	 * Sets the value of the codigoAgencia property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodigoAgencia(String value) {
		this.codigoAgencia = value;
	}

}
