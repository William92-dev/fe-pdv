package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

public enum EstadoOffline implements Identificable<EstadoOffline> {

	AUTORIZADO("AUTORIZADO"), PROCESAMIENTO("PROCESAMIENTO"), NOAUTORIZADO("NOAUTORIZADO");

	private String descripcion;

	private EstadoOffline(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

	/**
	 * @return EstadoEnum
	 */
	@Override
	public EstadoOffline getIdentificador() {
		return this;
	}
}
