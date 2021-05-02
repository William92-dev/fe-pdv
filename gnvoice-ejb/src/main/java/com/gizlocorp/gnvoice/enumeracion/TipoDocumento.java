package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

public enum TipoDocumento implements Identificable<TipoDocumento> {

	XML("xml"), PDF("pdf");

	private String descripcion;

	private TipoDocumento(final String descripcion) {
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
	public TipoDocumento getIdentificador() {
		return this;
	}
}
