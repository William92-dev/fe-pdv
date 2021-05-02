/**
 * 
 */
package com.gizlocorp.adm.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

/**
 * 
 * @author
 * @version $Revision: 1.0 $
 */
public enum Logico implements Identificable<Logico> {
	S("Si"), N("No");

	private String descripcion;

	private Logico(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	@Override
	public Logico getIdentificador() {
		// TODO Auto-generated method stub
		return this;
	}
}
