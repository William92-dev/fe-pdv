/*
 * Converter.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.adm.utilitario;

import java.io.InputStream;

import com.gizlocorp.adm.excepcion.ConverterException;

/**
 * Clase
 * 
 * @author
 * @revision $Revision: 1.1 $
 */
public interface Converter {

	<T> T convertirAObjeto(InputStream valor, Class<T> clase)
			throws ConverterException;

	<T> T convertirAObjeto(String valor, Class<T> clase)
			throws ConverterException;

	String convertirDeObjeto(Object o) throws ConverterException;

	String convertirDeObjetoFormat(Object o) throws ConverterException;
}
