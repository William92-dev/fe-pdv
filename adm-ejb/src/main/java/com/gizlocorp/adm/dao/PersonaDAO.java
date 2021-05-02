package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Persona;


@Local
public interface PersonaDAO extends GenericDAO<Persona, String>{
	
	List<Persona> obtenerPorParametros(String identificacion, String nombres, String apellidos, Long idOrganizacion) throws GizloException;

}
