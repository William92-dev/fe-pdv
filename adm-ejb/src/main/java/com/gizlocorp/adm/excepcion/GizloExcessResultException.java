package com.gizlocorp.adm.excepcion;

public class GizloExcessResultException extends Exception {

	/**
	 * Clase de excepcion para cuando los resultados de la busqueda pasan los mil registros
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param mensaje
	 */
	public GizloExcessResultException(String mensaje){
		super(mensaje);
	}

}
