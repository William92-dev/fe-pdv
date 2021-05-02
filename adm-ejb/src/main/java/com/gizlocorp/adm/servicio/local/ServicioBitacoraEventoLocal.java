package com.gizlocorp.adm.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.BitacoraEvento;

@Local
public interface ServicioBitacoraEventoLocal {

	List<BitacoraEvento> obtenerEventoReproceso(String codigoEvento)
			throws GizloException;

	BitacoraEvento actualizarBitacoraEvento(BitacoraEvento bitacoraEvento);

	List<BitacoraEvento> obtenerEventosPorParametros(Long idEvento,
			String usuario, Date fechaDesde, Date fechaHasta, String detalle)
			throws GizloException;

	List<BitacoraEvento> obtenerEvento(String codigoEvento, Long idOrganizacion)
			throws GizloException;
}
