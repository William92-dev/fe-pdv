/*
 * Seleccionable.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.adm.utilitario;

/**
 * Interfaz que agrega una caracteristica a una clase para se pueda identificar
 * por id y nombre
 * 
 * @author
 * @revision $Revision: 1.2 $
 */
public interface Identificable<ID> {

	/**
	 * @return el identificador
	 */
	ID getIdentificador();

	/**
	 * @return la descripcion
	 */
	String getDescripcion();
}
