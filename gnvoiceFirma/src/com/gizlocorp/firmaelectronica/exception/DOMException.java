package com.gizlocorp.firmaelectronica.exception;

/**
 * Class DOMException.
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class DOMException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557590022795001423L;

	/**
	 * Instancia un nuevo dom exception.
	 */
	public DOMException() {
		super();
	}

	/**
	 * Instancia un nuevo dom exception.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public DOMException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instancia un nuevo dom exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public DOMException(final String arg0) {
		super(arg0);
	}

	/**
	 * Instancia un nuevo dom exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public DOMException(final Throwable arg0) {
		super(arg0);
	}

}
