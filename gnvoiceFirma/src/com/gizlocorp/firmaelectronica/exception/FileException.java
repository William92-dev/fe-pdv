package com.gizlocorp.firmaelectronica.exception;

/**
 * Class FileException.
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class FileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557590022795001423L;

	/**
	 * Instancia un nuevo file exception.
	 */
	public FileException() {
		super();
	}

	/**
	 * Instancia un nuevo file exception.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public FileException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instancia un nuevo file exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public FileException(final String arg0) {
		super(arg0);
	}

	/**
	 * Instancia un nuevo file exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public FileException(final Throwable arg0) {
		super(arg0);
	}

}
