package com.gizlocorp.adm.enumeracion;

public enum SistemaExternoEnum {
	
	SISTEMACRPDTA("CRPDTA"), SISTEMABODEGA("BODEGA"), SISTEMAPROVEEDORES("PROVEEDORES");

	private String descripcion;

	private SistemaExternoEnum(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

}
