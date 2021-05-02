/*
 * FarcomedResponse.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.gnvoice.xml.message;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Mensaje de Salida del GNVOICE
 * 
 * @author
 * @revision $Revision: 1.1 $
 */
public class GizloResponse {

	private String estado;

	private String mensajeSistema;

	private String mensajeCliente;

	private List<MensajeRespuesta> mensajes;

	private String numeroAutorizacion;

	private String claveAccesoComprobante;

	private Date fechaAutorizacion;

	/**
	 * @return the estado
	 */
	@XmlElement(name = "estado")
	public String getEstado() {
		return this.estado;
	}

	/**
	 * @param estado
	 *            the estado to set
	 */
	public void setEstado(final String estado) {
		this.estado = estado;
	}

	/**
	 * @return the mensajeCliente
	 */
	@XmlElement(name = "mensajeCliente")
	public String getMensajeCliente() {
		return this.mensajeCliente;
	}

	/**
	 * @param mensajeCliente
	 *            the mensajeCliente to set
	 */
	public void setMensajeCliente(final String mensajeCliente) {
		this.mensajeCliente = mensajeCliente;
	}

	/**
	 * @return the mensajeSistema
	 */
	@XmlElement(name = "mensajeSistema")
	public String getMensajeSistema() {
		return this.mensajeSistema;
	}

	/**
	 * @param mensajeSistema
	 *            the mensajeSistema to set
	 */
	public void setMensajeSistema(final String mensajeSistema) {
		this.mensajeSistema = mensajeSistema;
	}

	@XmlElementWrapper(name = "mensajes")
	@XmlElement(name = "mensaje")
	public List<MensajeRespuesta> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<MensajeRespuesta> mensajes) {
		this.mensajes = mensajes;
	}

	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	public String getClaveAccesoComprobante() {
		return claveAccesoComprobante;
	}

	public void setClaveAccesoComprobante(String claveAccesoComprobante) {
		this.claveAccesoComprobante = claveAccesoComprobante;
	}

	@XmlElement(name = "fechaAutorizacion")
	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}

}
