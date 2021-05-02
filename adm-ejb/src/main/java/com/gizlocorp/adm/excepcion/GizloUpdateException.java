/**
 * 
 */
package com.gizlocorp.adm.excepcion;

import javax.ejb.ApplicationException;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */
@ApplicationException(rollback = true)
public class GizloUpdateException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String MSG = "Error actualizando registro en la tabla %s.";

	public GizloUpdateException(Object entity) {
		super(GizloException.format(MSG, entity));
	}

	public GizloUpdateException(Object entity, Throwable ex) {
		super(GizloException.format(MSG, entity), ex);
	}

}
