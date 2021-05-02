/**
 * 
 */
package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.RelacionOrganizacion;

/**
 * 
 * @author
 * @version
 */
@Local
public interface ServicioRelacionOrganizacionLocal {
	
	void guardarRelacionOrganizacion(RelacionOrganizacion relacionOrganizacion) throws GizloException;
	
	boolean existeRelacionOrganizacion(Organizacion organizacion, Organizacion organizacionRelacion, String tipoRelacion) throws GizloException;
	
	List<RelacionOrganizacion> buscarPorParametros(String ruc, String nombre, String tipoRelacon) throws GizloException;
	
}
