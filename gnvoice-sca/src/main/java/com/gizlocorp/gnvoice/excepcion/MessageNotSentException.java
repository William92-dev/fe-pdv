package com.gizlocorp.gnvoice.excepcion;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class MessageNotSentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3158109026760819181L;

	/**
	 * Constructor
	 * 
	 * @param mensaje
	 *            , mensaje de error
	 */
	public MessageNotSentException(final String mensaje) {
		super(mensaje);
	}

	/**
	 * Constructor
	 * 
	 * @param mensaje
	 *            , mensaje de error
	 * @param e
	 *            , causa
	 */
	public MessageNotSentException(final String mensaje, final Throwable e) {
		super(mensaje, e);
	}

	/**
	 * Constructor
	 * 
	 * @param e
	 *            , causa
	 */
	public MessageNotSentException(final Throwable e) {
		super(e);
	}

}
