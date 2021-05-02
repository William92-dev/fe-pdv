package com.gizlocorp.gnvoice.excepcion;

/**
 * Class FacturacionExcepcion. Excepcion
 * 
 * @author andresgiler
 * @revision $Revision: 1.0
 */
public class FacturacionExcepcion extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5239547525218283408L;

	/**
	 * Instancia un nuevo FacturacionExcepcion exception.
	 */
	public FacturacionExcepcion() {
		super();
	}

	/**
	 * Instancia un nuevo FacturacionExcepcion exception.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public FacturacionExcepcion(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instancia un nuevo FacturacionExcepcion exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public FacturacionExcepcion(final String arg0) {
		super(arg0);
	}

	/**
	 * Instancia un nuevo FacturacionExcepcion exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public FacturacionExcepcion(final Throwable arg0) {
		super(arg0);
	}

}
