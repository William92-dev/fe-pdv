/*
 * FarcomedResponse.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.gnvoice.xml.message;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gizlocorp.gnvoice.enumeracion.Estado;

import ec.gob.sri.comprobantes.ws.aut.Mensaje;

/**
 * Mensaje de Salida del GNVOICE
 * 
 * @author
 * @revision $Revision: 1.1 $
 */
public class ComprobanteResponse {

	private Estado estado;

	private String numeroAutorizacion;

	private String claveAccesoComprobante;

	private Date fechaAutorizacion;

	private List<Mensaje> mensajes;

	private String archivoFirmado;

	/**
	 * @return the estado
	 */
	public Estado getEstado() {
		return this.estado;
	}

	/**
	 * @param estado
	 *            the estado to set
	 */
	public void setEstado(final Estado estado) {
		this.estado = estado;
	}

	private List<ControlComprobante> controlComprobantes;

	public List<ControlComprobante> getControlComprobantes() {
		return controlComprobantes;
	}

	public void setControlComprobantes(
			List<ControlComprobante> controlComprobantes) {
		this.controlComprobantes = controlComprobantes;
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

	public List<Mensaje> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}

	public String getArchivoFirmado() {
		return archivoFirmado;
	}

	public void setArchivoFirmado(String archivoFirmado) {
		this.archivoFirmado = archivoFirmado;
	}

	public static class ControlComprobante {

		private Estado estado;
		private String descripcion;
		private Calendar fecha;

		public Estado getEstado() {
			return estado;
		}

		public void setEstado(Estado estado) {
			this.estado = estado;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public Calendar getFecha() {
			return fecha;
		}

		public void setFecha(Calendar fecha) {
			this.fecha = fecha;
		}

	}
}
