package com.gizlocorp.gnvoiceFybeca.enumeration;

public enum ProcesoEnum {

	LINEA("LINEA"), LOTE("LOTE"), BATCH("BATCH");

	private String descripcion;

	private ProcesoEnum(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

}
