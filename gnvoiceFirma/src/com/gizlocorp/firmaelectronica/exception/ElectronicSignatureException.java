package com.gizlocorp.firmaelectronica.exception;

/**
 * Class ElectronicSignatureException.
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class ElectronicSignatureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557590022795001423L;

	/**
	 * Instancia un nuevo electronicSignature exception.
	 */
	public ElectronicSignatureException() {
		super();
	}

	/**
	 * Instancia un nuevo electronicSignature exception.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public ElectronicSignatureException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instancia un nuevo electronicSignature exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public ElectronicSignatureException(final String arg0) {
		super(arg0);
	}

	/**
	 * Instancia un nuevo electronicSignature exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public ElectronicSignatureException(final Throwable arg0) {
		super(arg0);
	}

}
