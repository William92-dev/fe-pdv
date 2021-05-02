package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.dao.OrganizacionDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;

@Stateless
public class OrganizacionDAOImpl extends GenericJpaDAO<Organizacion, String>
		implements OrganizacionDAO {
	private static Logger log = Logger.getLogger(OrganizacionDAOImpl.class
			.getName());

	@SuppressWarnings("unchecked")
	public List<Organizacion> listar(String nombre, String acronimo,
			String ruc, Long idOrganizacion) throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			sql.append("select c from Organizacion c where 1 = 1");
			log.debug("	ESTADO " + Estado.ACT);
			// mapa.put("estado", Estado.ACT);

			if (nombre != null && !nombre.isEmpty()) {
				sql.append(" and c.nombre = :nombre");
				mapa.put("nombre", nombre);
			}

			if (acronimo != null && !acronimo.isEmpty()) {
				sql.append(" and c.acronimo = :acronimo");
				mapa.put("acronimo", acronimo);
			}

			if (ruc != null && !ruc.isEmpty()) {
				sql.append(" and c.ruc = :ruc");
				mapa.put("ruc", ruc);
			}

			if (idOrganizacion != null && idOrganizacion != 0) {
				sql.append(" and c.idOrganizacion = :idOrganizacion");
				mapa.put("idOrganizacion", idOrganizacion);
			}

			Query q = em.createQuery(sql.toString());
			for (String key : mapa.keySet()) {
				q.setParameter(key, mapa.get(key));
			}

			return q.getResultList();
		} catch (Exception e) {
			throw new GizloException("Error al listar Organizaciones", e);
		}
	}

	@Override
	public Organizacion consultarOrganizacion(String ruc) throws GizloException {
		StringBuilder sql = new StringBuilder();
		sql.append("select c from Organizacion c where c.estado = :estado and c.ruc = :ruc");

		javax.persistence.Query q = this.em.createQuery(sql.toString());
		q.setParameter("ruc", ruc);
		q.setParameter("estado", Estado.ACT);
		return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (Organizacion) q
				.getResultList().get(0) : null;
	}

	@Override
	public List<Organizacion> listarActivas() throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select c from Organizacion c ");
			javax.persistence.Query q = this.em.createQuery(sql.toString());
			// q.setParameter("estado", Estado.ACT);
			return q.getResultList();
		} catch (Exception e) {
			throw new GizloException("Error al listar organizaciones activas",
					e);
		}
	}

	@Override
	public Organizacion consultarOrganizacionID(Long id) throws GizloException {
		StringBuilder sql = new StringBuilder();
		sql.append("select c from Organizacion c where c.id = :id");

		javax.persistence.Query q = this.em.createQuery(sql.toString());
		q.setParameter("id", id);
		return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (Organizacion) q
				.getResultList().get(0) : null;
	}

}
