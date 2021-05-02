package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

public enum TipoGeneracion implements Identificable<TipoGeneracion> {

	EMI("Emitido"), REC("Recibido");

	private String descripcion;

	private TipoGeneracion(final String descripcion) {
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
	public TipoGeneracion getIdentificador() {
		return this;
	}
}
