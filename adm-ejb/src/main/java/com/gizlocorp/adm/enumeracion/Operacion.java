/**
 * 
 */
package com.gizlocorp.adm.enumeracion;

/**
 * 
 * @author
 * @version $Revision: 1.0 $
 */
public enum Operacion {
	ADD("Ingresar"), MOD("Modificar"), DEL("Eliminar"), ALL("Todos");

	private String descripcion;

	private Operacion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}
}
