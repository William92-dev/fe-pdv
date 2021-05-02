package com.gizlocorp.gnvoice.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.gnvoice.dao.ClaveContingenciaDAO;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
import com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia;
//import javax.ejb.EJB;
//import com.gizlocorp.gnvoice.service.ProcesadorSRI;
//import com.gizlocorp.gnvoice.xml.message.ComprobanteRequest;
//import com.gizlocorp.gnvoice.xml.message.ComprobanteResponse;

@Stateless
public class ServicioClaveContigenciaImpl implements ServicioClaveContigencia {
	public static final Logger log = Logger
			.getLogger(ServicioClaveContigenciaImpl.class);

	@EJB
	ClaveContingenciaDAO claveContingenciaDAO;

	@Override
	public ClaveContingencia recuperarNoUsada(Long idOrganizacion,
			String TipoAmbienteEnum, String claveAcceso) throws GizloException {
		return claveContingenciaDAO.recuperarNoUsada(idOrganizacion,TipoAmbienteEnum, claveAcceso);
	}

	@Override
	public void actualizarClaveContigencia(ClaveContingencia claveContingencia)
			throws GizloException {
		try {
			claveContingenciaDAO.update(claveContingencia);
		} catch (GizloUpdateException e) {
			throw new GizloException(e);
		}
	}

	@Override
	public void ingresarClaveContigencia(ClaveContingencia claveContingencia)
			throws GizloException {
		try {
			claveContingencia.setUsada(Logico.N);
			claveContingenciaDAO.persist(claveContingencia);
		} catch (GizloPersistException e) {
			throw new GizloException(e);
		}
	}

	@Override
	public List<ClaveContingencia> listarClaves(Long idOrganizacion,
			TipoAmbienteEnum TipoAmbienteEnum, Logico estado)
			throws GizloException {
		List<ClaveContingencia> claves = claveContingenciaDAO.listarClaves(
				idOrganizacion, TipoAmbienteEnum, estado);

		if (claves != null && !claves.isEmpty()) {
			for (ClaveContingencia clave : claves) {
				if (clave.getTipoAmbiente() != null
						&& !clave.getTipoAmbiente().isEmpty()) {
					for (TipoAmbienteEnum tipo : com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum
							.values()) {
						if (tipo.getCode().equals(clave.getTipoAmbiente())) {
							clave.setTipoAmbienteObj(tipo);
						}
					}
				}

			}
		}

		return claves;
	}

	@Override
	public int contarClaves(Long idOrganizacion, String TipoAmbienteEnum,
			Logico estado) throws GizloException {
		return claveContingenciaDAO.contarClaves(idOrganizacion,
				TipoAmbienteEnum, estado);
	}

	@Override
	public List<ClaveContingencia> recuperarClaveRelacionada(String claveAcceso)
			throws GizloException {
		List<ClaveContingencia> clavesRelacionadas = claveContingenciaDAO.recuperarClaveRelacionada(claveAcceso);
		return clavesRelacionadas;
	}
}
