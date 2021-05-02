package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;

@Local
public interface OrganizacionDAO extends GenericDAO<Organizacion, String> {
	List<Organizacion> listar(String nombre, String acronimo, String ruc,
			Long idOrganizacion) throws GizloException;

	Organizacion consultarOrganizacion(String ruc) throws GizloException;

	List<Organizacion> listarActivas() throws GizloException;
	
	Organizacion consultarOrganizacionID(Long id) throws GizloException; 
}
