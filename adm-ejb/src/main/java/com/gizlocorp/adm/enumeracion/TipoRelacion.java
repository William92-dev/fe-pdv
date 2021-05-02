package com.gizlocorp.adm.enumeracion;

public enum TipoRelacion {
	
	CLI("Cliente"), PRO("Proveedor"), TRA("Transportista");

	private String descripcion;

	private TipoRelacion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

}
