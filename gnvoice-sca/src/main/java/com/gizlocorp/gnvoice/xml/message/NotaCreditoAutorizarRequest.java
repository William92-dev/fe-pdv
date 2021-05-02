package com.gizlocorp.gnvoice.xml.message;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Java class for notaCreditoAutorizarRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="notaCreditoAutorizarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="claveAccesoComprobante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "notaCreditoAutorizarRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class NotaCreditoAutorizarRequest {

	protected String claveAccesoComprobante;

	/**
	 * Gets the value of the claveAccesoComprobante property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getClaveAccesoComprobante() {
		return claveAccesoComprobante;
	}

	/**
	 * Sets the value of the claveAccesoComprobante property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setClaveAccesoComprobante(String value) {
		this.claveAccesoComprobante = value;
	}

}
