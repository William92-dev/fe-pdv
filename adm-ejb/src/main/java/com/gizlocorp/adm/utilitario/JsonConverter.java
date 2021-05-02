/*
 * JsonConverter.java
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
public class JsonConverter implements Converter {

	@Override
	public <T> T convertirAObjeto(final InputStream valor, final Class<T> clase)
			throws ConverterException {
		throw new ConverterException("");
	}

	@Override
	public <T> T convertirAObjeto(final String valor, final Class<T> clase)
			throws ConverterException {
		throw new ConverterException("");
	}

	@Override
	public String convertirDeObjeto(final Object o) throws ConverterException {
		throw new ConverterException("");
	}

	@Override
	public String convertirDeObjetoFormat(Object o) throws ConverterException {
		throw new ConverterException("");
	}

}
