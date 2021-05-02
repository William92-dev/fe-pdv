package com.gizlocorp.firmaelectronica.exception;

/**
 * Class Base64XMLException.
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class Base64XMLException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2486981408902400814L;

	/**
	 * Instancia un nuevo crypto exception.
	 */
	public Base64XMLException() {
		super();
	}

	/**
	 * Instancia un nuevo crypto exception.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public Base64XMLException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instancia un nuevo crypto exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public Base64XMLException(final String arg0) {
		super(arg0);
	}

	/**
	 * Instancia un nuevo crypto exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public Base64XMLException(final Throwable arg0) {
		super(arg0);
	}

}
