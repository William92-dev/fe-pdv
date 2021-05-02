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
public class GizloDeleteException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String MSG = "Error eliminando registro en la tabla %s, entidad: %s";

	public GizloDeleteException(Object entity) {
		super(GizloException.format(MSG, entity));
	}

	public GizloDeleteException(Object entity, Throwable ex) {
		super(GizloException.format(MSG, entity), ex);
	}

}
