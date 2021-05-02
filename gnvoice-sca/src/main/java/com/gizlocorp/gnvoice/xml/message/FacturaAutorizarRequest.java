package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "facturaAutorizarRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class FacturaAutorizarRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String claveAccesoComprobante;

	public String getClaveAccesoComprobante() {
		return claveAccesoComprobante;
	}

	public void setClaveAccesoComprobante(String claveAccesoComprobante) {
		this.claveAccesoComprobante = claveAccesoComprobante;
	}
	
	
	
}
