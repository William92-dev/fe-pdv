package com.gizlocorp.gnvoice.wsclient.factura;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para facturaProcesarRequest complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="facturaProcesarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}factura" minOccurs="0"/>
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
@XmlType(name = "facturaProcesarRequest", propOrder = { "factura","codigoExterno",
		"correoElectronicoNotificacion" })
@XmlRootElement(name = "facturaProcesarRequest")
public class FacturaProcesarRequest {

	@XmlElement(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
	protected Factura factura;
	@XmlElement(required = true)
	protected String codigoExterno;
	@XmlElement(required = true)
	protected String correoElectronicoNotificacion;

	/**
	 * Obtiene el valor de la propiedad factura.
	 * 
	 * @return possible object is {@link Factura }
	 * 
	 */
	public Factura getFactura() {
		return factura;
	}

	/**
	 * Define el valor de la propiedad factura.
	 * 
	 * @param value
	 *            allowed object is {@link Factura }
	 * 
	 */
	public void setFactura(Factura value) {
		this.factura = value;
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
