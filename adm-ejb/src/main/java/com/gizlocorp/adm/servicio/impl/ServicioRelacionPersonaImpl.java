package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.RelacionPersonaDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.RelacionPersona;
import com.gizlocorp.adm.servicio.local.ServicioRelacionPersonaLocal;


@Stateless
public class ServicioRelacionPersonaImpl implements ServicioRelacionPersonaLocal{

	@EJB RelacionPersonaDAO relacionPersonaDAO;

	@Override
	public List<RelacionPersona> obtenerPorParametros(String identificacion, String nombres, String apellidos, Long idOrganizacion, String tipoRelacion) throws GizloException {		
		return relacionPersonaDAO.obtenerPorParametros(identificacion, nombres, apellidos, idOrganizacion, tipoRelacion);
	}

	@Override
	public boolean existeRelacionPersona(Persona idPersona, Long idOrganizacion, String tipoRelacion) throws GizloException{
		return relacionPersonaDAO.existeRelacionPersona(idPersona, idOrganizacion, tipoRelacion);
	}

	@Override
	public void guardarRelacionPersona(RelacionPersona relacionPersona) throws GizloException {
		if(relacionPersona.getId() != null){
			try {
				relacionPersonaDAO.update(relacionPersona);
			} catch (GizloUpdateException e) {
				e.printStackTrace();
			}
		}else{
			try {
				relacionPersonaDAO.persist(relacionPersona);
			} catch (GizloPersistException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	
	

}
