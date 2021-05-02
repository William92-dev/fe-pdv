package com.gizlocorp.gnvoice.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;

@Local
public interface ClaveContingenciaDAO extends
		GenericDAO<ClaveContingencia, Long> {

	ClaveContingencia recuperar(String clave) throws GizloException;

	ClaveContingencia recuperarNoUsada(Long idOrganizacion, String tipoAmbiente, String claveAcceso)
			throws GizloException;

	List<ClaveContingencia> listarClaves(Long idOrganizacion,
			TipoAmbienteEnum tipoAmbiente, Logico estado) throws GizloException;

	int contarClaves(Long idOrganizacion, String tipoAmbiente, Logico estado)
			throws GizloException;
	
	List<ClaveContingencia> recuperarClaveRelacionada(String claveAcceso)
			throws GizloException;
}
