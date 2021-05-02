/**
 * 
 */
package com.gizlocorp.adm.excepcion;

import java.lang.annotation.Annotation;

import javax.ejb.ApplicationException;
import javax.persistence.Table;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */
@ApplicationException(rollback = true)
public class GizloException extends Exception {

	private static final long serialVersionUID = 1L;

	public GizloException(String msg) {
		super(msg);
	}

	public GizloException(Throwable ex) {
		super(ex);
	}

	public GizloException(String msg, Throwable ex) {
		super(msg, ex);
	}

	/**
	 * Metodo encargado de formatear los mensajes de las excepciones
	 * 
	 * @param formato
	 * @param entidad
	 * @return
	 */
	public static String format(String formato, Object entidad) {
		Annotation[] anotaciones = entidad.getClass().getDeclaredAnnotations();
		String nombreTabla = entidad.getClass().getSimpleName().toUpperCase();
		for (Annotation an : anotaciones) {
			if (an.getClass().getName().equals(Table.class.getName())) {
				Table tb = (Table) an;
				nombreTabla = tb.name();
			}
		}
		return String.format(formato, nombreTabla, entidad.getClass()
				.getCanonicalName());
	}

}
