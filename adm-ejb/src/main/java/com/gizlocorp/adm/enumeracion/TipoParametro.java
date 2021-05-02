/**
 * 
 */
package com.gizlocorp.adm.enumeracion;

/**
 * 
 * @author
 * @version $Revision: 1.0 $
 */
public enum TipoParametro {
	PSW("Contraseña"), TEX("Texto"), TXL("Texto Largo"), DAT("Fecha");

	private String descripcion;

	private TipoParametro(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}
}
