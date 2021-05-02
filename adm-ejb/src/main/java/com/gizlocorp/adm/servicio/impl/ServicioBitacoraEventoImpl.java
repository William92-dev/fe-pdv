package com.gizlocorp.adm.servicio.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.dao.BitacoraEventoDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.BitacoraEvento;
import com.gizlocorp.adm.servicio.local.ServicioBitacoraEventoLocal;

@Stateless
public class ServicioBitacoraEventoImpl implements ServicioBitacoraEventoLocal {

	private static Logger log = Logger
			.getLogger(ServicioBitacoraEventoImpl.class.getName());


	@EJB
	BitacoraEventoDAO bitacoraEventoDAO;

	@Override
	public List<BitacoraEvento> obtenerEventoReproceso(String codigoEvento)
			throws GizloException {
		return bitacoraEventoDAO.obtenerEventoReproceso(codigoEvento);
	}

	@Override
	public BitacoraEvento actualizarBitacoraEvento(BitacoraEvento bitacoraEvento) {
		BitacoraEvento respuesta = null;
		try {
			if (bitacoraEvento.getId() == null) {
				bitacoraEventoDAO.persist(bitacoraEvento);
				respuesta = bitacoraEvento;
			} else {
				respuesta = bitacoraEventoDAO.update(bitacoraEvento);
			}
		} catch (Exception ex) {
			respuesta = null;
			log.error(
					"Ha ocurrido un error al grabar en la bitcora de eventos: ",
					ex);
		}

		return respuesta;
	}

	@Override
	public List<BitacoraEvento> obtenerEventosPorParametros(Long idEvento,
			String usuario, Date fechaDesde, Date fechaHasta,
		String detalle) throws GizloException {
		return bitacoraEventoDAO.obtenerEventosPorParametros(idEvento, usuario,
				fechaDesde, fechaHasta, detalle);
	}

	@Override
	public List<BitacoraEvento> obtenerEvento(String codigoEvento,
			Long idOrganizacion) throws GizloException {
		return bitacoraEventoDAO.obtenerEvento(codigoEvento, idOrganizacion);
	}

	
}