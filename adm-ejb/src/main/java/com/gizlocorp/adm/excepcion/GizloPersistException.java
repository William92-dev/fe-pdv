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
public class GizloPersistException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String MSG = "Error insertando registro en la tabla %s, entidad %s.";

	public GizloPersistException(Object entity) {
		super(GizloException.format(MSG, entity));
	}

	public GizloPersistException(Object entity, Throwable ex) {
		super(GizloException.format(MSG, entity), ex);
	}

}
