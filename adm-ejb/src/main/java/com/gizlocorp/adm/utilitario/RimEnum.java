package com.gizlocorp.adm.utilitario;

public enum RimEnum {

	REIM("Reim"), NINGUNO("Ninguno");

	private String descripcion;

	private RimEnum(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

}
