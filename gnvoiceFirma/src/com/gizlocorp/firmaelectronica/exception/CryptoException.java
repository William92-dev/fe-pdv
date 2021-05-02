package com.gizlocorp.firmaelectronica.exception;

/**
 * Class CryptoException.
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class CryptoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557590022795001423L;

	/**
	 * Instancia un nuevo crypto exception.
	 */
	public CryptoException() {
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
	public CryptoException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instancia un nuevo crypto exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public CryptoException(final String arg0) {
		super(arg0);
	}

	/**
	 * Instancia un nuevo crypto exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public CryptoException(final Throwable arg0) {
		super(arg0);
	}

}
