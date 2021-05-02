package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mensaje de Salida del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "facturaReceptarResponse", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class FacturaReceptarResponse extends GizloResponse implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5874705973982568190L;

}
