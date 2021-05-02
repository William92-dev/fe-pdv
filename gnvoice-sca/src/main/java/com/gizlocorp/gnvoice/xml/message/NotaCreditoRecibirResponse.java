package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mensaje de Salida del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "notaCreditoRecibirResponse", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class NotaCreditoRecibirResponse extends GizloResponse implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5743413629944359284L;

}
