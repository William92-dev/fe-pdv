/*
 * Status.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.gnvoice.utilitario.Identificable;

/**
 * Enumerador de estados
 * 
 * @author gizlo
 * @revision $Revision: 1.2 $
 */
public enum EstadoEnum implements Identificable<EstadoEnum> {

	A("Activo"), I("Inactivo");

	private String descripcion;

	private EstadoEnum(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

	@Override
	public EstadoEnum getIdentificador() {
		return this;
	}
}
