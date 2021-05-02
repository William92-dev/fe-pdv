package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "guiaRecibirRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class GuiaRecibirRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String comprobanteProveedor;

	public String getComprobanteProveedor() {
		return comprobanteProveedor;
	}

	public void setComprobanteProveedor(String comprobanteProveedor) {
		this.comprobanteProveedor = comprobanteProveedor;
	}

}
