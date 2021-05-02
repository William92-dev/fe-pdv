package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Persona;

@Local
public interface ServicioPersonaLocal {

	void guardarPersona(Persona persona);

	List<Persona> obtenerPorParametros(String identificacion, String nombres,
			String apellidos, Long idOrganizacion) throws GizloException;

}
