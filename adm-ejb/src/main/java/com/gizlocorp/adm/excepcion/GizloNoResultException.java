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
public class GizloNoResultException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public GizloNoResultException(String msg) {
		super(msg);
	}

	public GizloNoResultException(Throwable ex) {
		super(ex);
	}

	public GizloNoResultException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
