/*
 * OfertaDao.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.PlantillaDAO;
import com.gizlocorp.adm.modelo.Plantilla;

/**
 * Clase
 * 
 * @author gizlo
 * @revision $Revision: 1.7 $
 */
@Stateless
public class PlantillaDAOImpl extends GenericJpaDAO<Plantilla, Long> implements
		PlantillaDAO {

	@Override
	public Plantilla obtenerPlantilla(String codigo, Long idOrganizacion) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select p from Plantilla p where 1=1");

		if (codigo != null && !codigo.isEmpty()) {
			sql.append("and p.codigo = :codigo ");
			mapa.put("codigo", codigo);
		}

		if (idOrganizacion != null && idOrganizacion != 0) {
			sql.append("and p.idOrganizacion = :idOrganizacion");
			mapa.put("idOrganizacion", idOrganizacion);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (Plantilla) q
				.getResultList().get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<Plantilla> listaPlantilla(String codigo, String titulo,
			Long idOrganizacion) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select p from Plantilla p where 1=1");

		if (codigo != null && !codigo.isEmpty()) {
			sql.append("and p.codigo = :codigo ");
			mapa.put("codigo", codigo);
		}

		if (idOrganizacion != null && idOrganizacion != 0) {
			sql.append("and p.idOrganizacion = :idOrganizacion ");
			mapa.put("idOrganizacion", idOrganizacion);
		}

		if (titulo != null && !titulo.isEmpty()) {
			sql.append("and upper(p.titulo) like :titulo ");
			mapa.put("titulo", "%" + titulo.toUpperCase() + "%");
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<Plantilla> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

}
