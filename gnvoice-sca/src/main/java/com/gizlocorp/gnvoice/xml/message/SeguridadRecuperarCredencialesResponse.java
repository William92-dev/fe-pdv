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
@XmlType(name = "", propOrder = {"httpStatus", "respuesta"})
public class SeguridadRecuperarCredencialesResponse implements Serializable {

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

		private Login login;

		public Login getLogin() {
			return login;
		}

		public void setLogin(Login login) {
			this.login = login;
		}

	}

	@XmlRootElement(name = "login")
	@XmlType(name = "", propOrder = { "usuario", "clave", "estado"})
	public static class Login {

		private String usuario;

		private String clave;

		private String estado;

		public String getUsuario() {
			return usuario;
		}

		public void setUsuario(String usuario) {
			this.usuario = usuario;
		}

		public String getClave() {
			return clave;
		}

		public void setClave(String clave) {
			this.clave = clave;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

	}

}
