package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.RelacionPersona;


@Local
public interface RelacionPersonaDAO extends GenericDAO<RelacionPersona, Long>{
	
	List<RelacionPersona> obtenerPorParametros(String identificacion, String nombres, String apellidos, Long idOrganizacion, String tipoRelacion) throws GizloException;
	boolean existeRelacionPersona(Persona idPersona, Long idOrganizacion, String tipoRelacion) throws GizloException;

}
