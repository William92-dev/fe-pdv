/*
 * FarcomedResponse.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.firmaelectronica.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * Mensaje de Salida del GNVOICE
 * 
 * @author
 * @revision $Revision: 1.1 $
 */

public class FirmaElectronicaResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String estado;

	private String mensajeSistema;

	private String mensajeCliente;

	private String rutaArchivoFirmado;

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
	@XmlElement(name = "mensajecliente")
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
	@XmlElement(name = "mensajesistema")
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

	@XmlElement(name = "rutaArchivoFirmado")
	public String getRutaArchivoFirmado() {
		return rutaArchivoFirmado;
	}

	public void setRutaArchivoFirmado(String rutaArchivoFirmado) {
		this.rutaArchivoFirmado = rutaArchivoFirmado;
	}
}
