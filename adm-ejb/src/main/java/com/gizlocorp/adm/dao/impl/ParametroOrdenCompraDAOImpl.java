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

import com.gizlocorp.adm.dao.ParametroOrdenCompraDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;
import com.gizlocorp.adm.servicio.impl.ServicioParametroImpl;

/**
 * Clase
 * 
 * @author gizlo
 * @revision $Revision: 1.7 $
 */
@Stateless
public class ParametroOrdenCompraDAOImpl extends GenericJpaDAO<ParametroOrdenCompra, Long> implements
ParametroOrdenCompraDAO {

	private static Logger log = Logger.getLogger(ServicioParametroImpl.class.getName());

	@Override
	public ParametroOrdenCompra obtenerParametro(String codigo, String rucProveedor){
		StringBuilder sql = new StringBuilder();
		sql.append("select p from ParametroOrdenCompra p where p.valor = :codigo");
		if (rucProveedor != null && !"".equals(rucProveedor)) {
			sql.append(" and p.proveedor = :rucProveedor");
		}
		javax.persistence.Query q = this.em.createQuery(sql.toString());
		q.setParameter("codigo", codigo);
		// q.setParameter("estado", Estado.ACT);

		if (rucProveedor != null && !"".equals(rucProveedor)) {
			q.setParameter("rucProveedor", rucProveedor);
		}
		return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (ParametroOrdenCompra) q
				.getResultList().get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ParametroOrdenCompra> listaParametro(String codigo, Estado estado) throws GizloException {
		try {
			StringBuilder hsql = new StringBuilder(
					"select p from ParametroOrdenCompra p where 1=1");
	
			if (estado != null) {
				hsql.append(" and p.estado = :estado");
			}



			hsql.append(" order by p.valor");

			Query q = em.createQuery(hsql.toString());
			if (estado != null) {
				q.setParameter("estado", estado);
			}

			return q.getResultList();

		} catch (Exception e) {
			throw new GizloException("Error al listar Parametros", e);
		}

	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ParametroOrdenCompra> listaParametroRucProveedor(String rucProveedor) throws GizloException {
		try {
			StringBuilder hsql = new StringBuilder(
					"select p from ParametroOrdenCompra p where p.proveedor = :proveedor");

				hsql.append(" and p.proveedor = :proveedor");
			hsql.append(" order by p.valor");

			Query q = em.createQuery(hsql.toString());
				q.setParameter("proveedor", rucProveedor);

			return q.getResultList();

		} catch (Exception e) {
			throw new GizloException("Error al listar Parametros", e);
		}

	}
}
