package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

public enum TipoEjecucion implements Identificable<TipoEjecucion> {

	LOT("Lote"), SEC("Secuencia");

	private String descripcion;

	private TipoEjecucion(final String descripcion) {
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
	public TipoEjecucion getIdentificador() {
		return this;
	}
}
