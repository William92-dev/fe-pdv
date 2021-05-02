package com.gizlocorp.gnvoice.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;

@Local
public interface ServicioClaveContigencia {

	ClaveContingencia recuperarNoUsada(Long idOrganizacion,
			String TipoAmbienteEnum, String ClaveAcceso) throws GizloException;

	void actualizarClaveContigencia(ClaveContingencia claveContingencia)
			throws GizloException;

	void ingresarClaveContigencia(ClaveContingencia claveContingencia)
			throws GizloException;

	List<ClaveContingencia> listarClaves(Long idOrganizacion,
			TipoAmbienteEnum TipoAmbienteEnum, Logico estado)
			throws GizloException;

	int contarClaves(Long idOrganizacion, String TipoAmbienteEnum, Logico estado)
			throws GizloException;
	
	List<ClaveContingencia> recuperarClaveRelacionada(String claveAcceso)
			throws GizloException;
}
