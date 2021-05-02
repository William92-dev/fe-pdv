package com.gizlocorp.gnvoiceFybeca.enumeration;

public enum GeneradoEnum {

	SI("S"), NO("N");

	private String descripcion;

	private GeneradoEnum(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

}
