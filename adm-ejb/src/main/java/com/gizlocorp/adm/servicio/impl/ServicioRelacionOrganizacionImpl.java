/**
 * 
 */
package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.RelacionOrganizacionDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.RelacionOrganizacion;
import com.gizlocorp.adm.servicio.local.ServicioRelacionOrganizacionLocal;

/**
 * 
 * @author
 * @version
 */
@Stateless
public class ServicioRelacionOrganizacionImpl implements ServicioRelacionOrganizacionLocal {

	//private static Logger log = Logger.getLogger(ServicioRelacionOrganizacionImpl.class.getName());
	@EJB RelacionOrganizacionDAO relacionOrganizacionDAO;

	@Override
	public void guardarRelacionOrganizacion(RelacionOrganizacion relacionOrganizacion) throws GizloException{
		if(relacionOrganizacion.getId()!=null){
			try {
				relacionOrganizacionDAO.update(relacionOrganizacion);
			} catch (GizloUpdateException e) {
				e.printStackTrace();
			}
		}else{
			try {
				relacionOrganizacionDAO.persist(relacionOrganizacion);
			} catch (GizloPersistException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public boolean existeRelacionOrganizacion(Organizacion organizacion,Organizacion organizacionRelacion, String tipoRelacion) throws GizloException {
		
		return relacionOrganizacionDAO.existeRelacionOrganizacion(organizacion, organizacionRelacion, tipoRelacion);
	}

	@Override
	public List<RelacionOrganizacion> buscarPorParametros(String ruc, String nombre, String tipoRelacon) throws GizloException {
		
		return relacionOrganizacionDAO.buscarPorParametros(ruc, nombre, tipoRelacon);
	}
	
}
