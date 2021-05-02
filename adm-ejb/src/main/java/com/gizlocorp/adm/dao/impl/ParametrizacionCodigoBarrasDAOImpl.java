package com.gizlocorp.adm.dao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.ParametrizacionCodigoBarrasDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;

@Stateless
public class ParametrizacionCodigoBarrasDAOImpl extends
		GenericJpaDAO<ParametrizacionCodigoBarras, String> implements ParametrizacionCodigoBarrasDAO {

	

	@Override
	public List<ParametrizacionCodigoBarras> obtenerCodigoBarrasPorParametros(
			String codigoArticulo, String tipoArticulo, Estado estado, String ruc)
			throws GizloException {
		StringBuilder sql = new StringBuilder();
		sql.append("select c from ParametrizacionCodigoBarras c where 1 = 1");

		if (codigoArticulo != null && codigoArticulo.isEmpty()) {
			sql.append(" and c.codigoArticulo = :codigoArticulo");
		}
		
		if (tipoArticulo != null && tipoArticulo.isEmpty()) {
			sql.append(" and c.tipoArticulo = :tipoArticulo");
		}
		
		if (estado != null) {
			sql.append(" and c.estado = :estado");
		}
		
		Query q = em.createQuery(sql.toString());
		if (codigoArticulo != null && codigoArticulo.isEmpty()) {
			q.setParameter("codigoArticulo", codigoArticulo);
		}
		
		if (tipoArticulo != null && tipoArticulo.isEmpty()) {
			q.setParameter("tipoArticulo", tipoArticulo);
		}
		
		if (estado != null) {
			q.setParameter("estado", estado);
		}

		@SuppressWarnings("unchecked")
		List<ParametrizacionCodigoBarras> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

}
