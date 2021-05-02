package com.gizlocorp.adm.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.BitacoraEvento;

@Local
public interface BitacoraEventoDAO extends GenericDAO<BitacoraEvento, String> {

	List<BitacoraEvento> obtenerEventoReproceso(String codigoEvento)
			throws GizloException;

	List<BitacoraEvento> obtenerEventosPorParametros(Long idEvento,
			String usuario, Date fechaDesde, Date fechaHasta, String detalle)
			throws GizloException;

	List<BitacoraEvento> obtenerEvento(String codigoEvento, Long idOrganizacion)
			throws GizloException;
}
