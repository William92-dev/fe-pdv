package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.gizlocorp.gnvoice.xml.guia.GuiaRemision;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "guiaProcesarRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class GuiaProcesarRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GuiaRemision guia;
	private String codigoExterno;
	private String correoElectronicoNotificacion;
	private String identificadorUsuario;
	private String agencia;
	private String sid;

	public GuiaRemision getGuia() {
		return guia;
	}

	public void setGuia(GuiaRemision guia) {
		this.guia = guia;
	}

	public String getCorreoElectronicoNotificacion() {
		return correoElectronicoNotificacion;
	}

	public void setCorreoElectronicoNotificacion(
			String correoElectronicoNotificacion) {
		this.correoElectronicoNotificacion = correoElectronicoNotificacion;
	}

	public String getCodigoExterno() {
		return codigoExterno;
	}

	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	public String getIdentificadorUsuario() {
		return identificadorUsuario;
	}

	public void setIdentificadorUsuario(String identificadorUsuario) {
		this.identificadorUsuario = identificadorUsuario;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
}
