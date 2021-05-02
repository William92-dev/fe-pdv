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
public class GizloQueryException extends Exception {

	private static final long serialVersionUID = 1L;

}
