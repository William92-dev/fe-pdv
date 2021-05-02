package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.gizlocorp.gnvoice.xml.factura.Factura;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "facturaProcesarRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class FacturaProcesarRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Factura factura;
	private String codigoExterno;
	private String correoElectronicoNotificacion;
	private String identificadorUsuario;
	private String agencia;
	private String sid;
	private String paciente;
	private String banderaOferton;

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
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

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getBanderaOferton() {
		return banderaOferton;
	}

	public void setBanderaOferton(String banderaOferton) {
		this.banderaOferton = banderaOferton;
	}


	 
	
	
	 
}
