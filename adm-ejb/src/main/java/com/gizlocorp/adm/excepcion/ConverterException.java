/*
 * ConverterException.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.adm.excepcion;

/**
 * Clase
 * 
 * @author
 * @revision $Revision: 1.1 $
 */
public class ConverterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7100096136236248627L;

	public ConverterException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public ConverterException(final String arg0) {
		super(arg0);
	}

	public ConverterException(final Throwable arg0) {
		super(arg0);
	}

}
