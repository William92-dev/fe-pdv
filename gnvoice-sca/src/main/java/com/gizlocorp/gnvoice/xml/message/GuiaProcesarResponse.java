package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mensaje de Salida del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "guiaProcesarResponse", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class GuiaProcesarResponse extends GizloResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
