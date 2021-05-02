/**
 * 
 */
package com.gizlocorp.adm.excepcion;

import javax.ejb.ApplicationException;
import javax.persistence.NonUniqueResultException;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */
@ApplicationException(rollback = true)
public class GizloNonUniqueResultException extends
		NonUniqueResultException {

	private static final long serialVersionUID = 1L;

}
