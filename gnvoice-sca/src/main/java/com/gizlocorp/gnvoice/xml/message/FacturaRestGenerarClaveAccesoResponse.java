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
public class FacturaRestGenerarClaveAccesoResponse extends GizloResponse
		implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String httpStatus;
	private Respuesta respuesta;

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	public Respuesta getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(Respuesta respuesta) {
		this.respuesta = respuesta;
	}

	@XmlRootElement(name = "respuesta")
	public static class Respuesta {

		private String claveAcceso;

		public String getClaveAcceso() {
			return claveAcceso;
		}

		public void setClaveAcceso(String claveAcceso) {
			this.claveAcceso = claveAcceso;
		}

	}
}
