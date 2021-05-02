package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito;

/**
 * Mensaje de Salida del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "notaCreditoConsultarResponse", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class NotaCreditoConsultarResponse extends GizloResponse implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<NotaCredito> notasCredito;

	@XmlElementWrapper(name = "notasCredito")
	@XmlElement(name = "notaCredito")
	public List<NotaCredito> getNotasCredito() {
		return notasCredito;
	}

	public void setNotasCredito(List<NotaCredito> notasCredito) {
		this.notasCredito = notasCredito;
	}

}
