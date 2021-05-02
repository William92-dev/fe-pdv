package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.gizlocorp.gnvoice.xml.guia.GuiaRemision;

/**
 * Mensaje de Salida del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "guiaConsultarResponse", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class GuiaConsultarResponse extends GizloResponse implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<GuiaRemision> guias;

	@XmlElementWrapper(name = "guias")
	@XmlElement(name = "guia")
	public List<GuiaRemision> getGuias() {
		return guias;
	}

	public void setGuias(List<GuiaRemision> guias) {
		this.guias = guias;
	}

}
