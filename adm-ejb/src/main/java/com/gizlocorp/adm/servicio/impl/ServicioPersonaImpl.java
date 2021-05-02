package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.PersonaDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;

@Stateless
public class ServicioPersonaImpl implements ServicioPersonaLocal {

	@EJB
	PersonaDAO personaDAO;

	public void guardarPersona(Persona persona) {
		if (persona.getId() == null) {
			try {
				personaDAO.persist(persona);
			} catch (GizloPersistException e) {
				e.printStackTrace();
			}
		} else {
			try {
				personaDAO.update(persona);
			} catch (GizloUpdateException e) {
				e.printStackTrace();
			}
		}

	}

	public List<Persona> obtenerPorParametros(String identificacion,
			String nombres, String apellidos, Long idOrganizacion)
			throws GizloException {

		return personaDAO.obtenerPorParametros(identificacion, nombres,
				apellidos, idOrganizacion);
	}

}
