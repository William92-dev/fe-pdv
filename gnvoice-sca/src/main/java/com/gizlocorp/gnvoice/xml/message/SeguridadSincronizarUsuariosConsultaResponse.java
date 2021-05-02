package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Mensaje de Salida del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "transaccion")
@XmlType(name = "", propOrder = { "httpStatus", "respuesta" })
public class SeguridadSincronizarUsuariosConsultaResponse implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String httpStatus;
	private String respuesta;

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}
}
