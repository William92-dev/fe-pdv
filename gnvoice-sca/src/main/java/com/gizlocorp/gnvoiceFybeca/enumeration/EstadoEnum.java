package com.gizlocorp.gnvoiceFybeca.enumeration;

public enum EstadoEnum {

	PENDIENTE("P"), APROBADO("A"), RECHAZADO("R");

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

}
