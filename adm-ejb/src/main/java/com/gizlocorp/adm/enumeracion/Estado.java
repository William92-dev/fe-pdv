/**
 * 
 */
package com.gizlocorp.adm.enumeracion;

/**
 * 
 * @author
 * @version $Revision: 1.0 $
 */
public enum Estado {
	ACT("Activo"), INA("Inactivo");

	private String descripcion;

	private Estado(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}
}
