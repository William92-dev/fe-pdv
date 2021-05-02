package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

public enum Estado implements Identificable<Estado> {

	PENDIENTE("PENDIENTE"), COMPLETADO("COMPLETADO"), ERROR("ERROR"), DEVUELTA(
			"DEVUELTA"), RECIBIDA("RECIBIDA"), AUTORIZADO("AUTORIZADO"), RECHAZADO(
			"RECHAZADO"), CANCELADO("CANCELADO"), CONTINGENCIA("CONTINGENCIA");

	private String descripcion;

	private Estado(final String descripcion) {
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
	public Estado getIdentificador() {
		return this;
	}
}
