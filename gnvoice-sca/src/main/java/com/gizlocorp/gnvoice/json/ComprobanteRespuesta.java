package com.gizlocorp.gnvoice.json;

import java.util.Date;

public class ComprobanteRespuesta {

	private String estado;

	private String mensajeSistema;

	private String mensajeCliente;

	private String numeroAutorizacion;

	private String claveAcceso;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMensajeSistema() {
		return mensajeSistema;
	}

	public void setMensajeSistema(String mensajeSistema) {
		this.mensajeSistema = mensajeSistema;
	}

	public String getMensajeCliente() {
		return mensajeCliente;
	}

	public void setMensajeCliente(String mensajeCliente) {
		this.mensajeCliente = mensajeCliente;
	}

	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}

	private Date fechaAutorizacion;
}
