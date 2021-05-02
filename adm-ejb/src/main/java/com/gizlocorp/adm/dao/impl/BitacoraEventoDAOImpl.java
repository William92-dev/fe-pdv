package com.gizlocorp.adm.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.BitacoraEventoDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.BitacoraEvento;

@Stateless
public class BitacoraEventoDAOImpl extends
		GenericJpaDAO<BitacoraEvento, String> implements BitacoraEventoDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<BitacoraEvento> obtenerEventoReproceso(String codigoEvento)
			throws GizloException {
		StringBuilder sql = new StringBuilder();

		sql.append("select c from BitacoraEvento c where c.evento.codigo = :evento ");
		Query q = em.createQuery(sql.toString());
		q.setParameter("evento", codigoEvento);

		List<BitacoraEvento> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BitacoraEvento> obtenerEventosPorParametros(Long idEvento,
			String usuario, Date fechaDesde, Date fechaHasta, String detalle)
			throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from BitacoraEvento c where 1 = 1 ");

		if (idEvento != null && idEvento.longValue() > 0L) {
			sql.append("and c.evento.id = :evento ");
			mapa.put("evento", idEvento);
		}

		if (usuario != null && !usuario.isEmpty()) {
			sql.append("and upper(c.usuario) like :usuario ");
			mapa.put("usuario", usuario.toUpperCase());
		}

		if (detalle != null && !detalle.isEmpty()) {
			sql.append("and upper(c.descripcion) like :detalle ");
			mapa.put("detalle", detalle.toUpperCase());
		}

		if (fechaDesde != null) {
			sql.append("and c.fecha >= :fechaDesde ");
			mapa.put("fechaDesde", fechaDesde);
		}

		if (fechaHasta != null) {
			sql.append("and c.fecha <= :fechaHasta ");
			mapa.put("fechaHasta", fechaHasta);
		}

		sql.append("order by c.fecha asc, c.evento.nombre asc");

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<BitacoraEvento> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BitacoraEvento> obtenerEvento(String codigoEvento,
			Long idOrganizacion) throws GizloException {
		StringBuilder sql = new StringBuilder();
		sql.append("select c from BitacoraEvento c where c.evento.codigo = :evento");

		if (idOrganizacion != null && idOrganizacion != 0) {
			sql.append(" and c.organizacion.id = :idOrganizacion");
		}
		Query q = em.createQuery(sql.toString());
		q.setParameter("evento", codigoEvento);
		if (idOrganizacion != null && idOrganizacion != 0) {
			q.setParameter("idOrganizacion", idOrganizacion);
		}

		List<BitacoraEvento> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

}
