/*
 * OfertaDao.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.adm.dao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.dao.ParametroDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.servicio.impl.ServicioParametroImpl;

/**
 * Clase
 * 
 * @author gizlo
 * @revision $Revision: 1.7 $
 */
@Stateless
public class ParametroDAOImpl extends GenericJpaDAO<Parametro, Long> implements
		ParametroDAO {

	private static Logger log = Logger.getLogger(ServicioParametroImpl.class
			.getName());

	@Override
	public Parametro obtenerParametro(String codigo, Long idOrganizacion) {
		StringBuilder sql = new StringBuilder();
		sql.append("select p from Parametro p where p.codigo = :codigo");
		if (idOrganizacion != null && idOrganizacion != 0) {
			sql.append(" and p.idOrganizacion = :idOrganizacion");
		}
		javax.persistence.Query q = this.em.createQuery(sql.toString());
		q.setParameter("codigo", codigo);
		// q.setParameter("estado", Estado.ACT);

		if (idOrganizacion != null && idOrganizacion != 0) {
			q.setParameter("idOrganizacion", idOrganizacion);
		}
		return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (Parametro) q
				.getResultList().get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listaParametro(String codigo, Estado estado,
			Long idOrganizacion) throws GizloException {
		try {
			StringBuilder hsql = new StringBuilder(
					"select p from Parametro p where 1=1");
			if (codigo != null && !codigo.isEmpty()) {
				hsql.append(" and upper(p.codigo) = upper(:codigo)");
			}
			if (estado != null) {
				hsql.append(" and p.estado = :estado");
			}

			if (idOrganizacion != null && idOrganizacion != 0) {
				hsql.append(" and p.idOrganizacion = :idOrganizacion ");
			} else {
				hsql.append(" and idOrganizacion is null");
			}

			hsql.append(" order by p.codigo");

			Query q = em.createQuery(hsql.toString());
			if (codigo != null && !codigo.isEmpty()) {
				q.setParameter("codigo", codigo);
			}
			if (estado != null) {
				q.setParameter("estado", estado);
			}
			if (idOrganizacion != null && idOrganizacion != 0) {
				q.setParameter("idOrganizacion", idOrganizacion);
			}
			return q.getResultList();

		} catch (Exception e) {
			throw new GizloException("Error al listar Parametros", e);
		}

	}
}
