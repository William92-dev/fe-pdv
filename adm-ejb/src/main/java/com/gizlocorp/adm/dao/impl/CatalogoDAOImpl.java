package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.CatalogoDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Catalogo;

@Stateless
public class CatalogoDAOImpl extends GenericJpaDAO<Catalogo, String> implements
		CatalogoDAO {

	@SuppressWarnings("unchecked")
	public List<Catalogo> listObtenerPorParametros(String codigo, String nombre)
			throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Catalogo c where 1=1 ");
		if (codigo != null && !codigo.isEmpty()) {
			sql.append("and c.codigo = :codigo");
			mapa.put("codigo", codigo);
		}

		if (nombre != null && !nombre.isEmpty()) {
			sql.append("and upper(c.nombre) like :nombre ");
			mapa.put("nombre", "%" + nombre.toUpperCase() + "%");
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<Catalogo> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

	@SuppressWarnings("unchecked")
	public List<Catalogo> listCatalogoPorTipoCatalogo(
			String nombretipoCatalogo, String coigoTipoCatalogo)
			throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Catalogo c where 1=1 ");

		if (nombretipoCatalogo != null && !nombretipoCatalogo.isEmpty()) {
			sql.append("and c.tipoCatalogo.nombre = :nombretipoCatalogo");
			mapa.put("nombretipoCatalogo", nombretipoCatalogo);
		}

		if (coigoTipoCatalogo != null && !coigoTipoCatalogo.isEmpty()) {
			sql.append("and c.tipoCatalogo.codigo = :coigoTipoCatalogo");
			mapa.put("coigoTipoCatalogo", coigoTipoCatalogo);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<Catalogo> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

	@SuppressWarnings("unchecked")
	public Catalogo obtenerPorId(Long idCatalogo) throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Catalogo c where id = :idCatalogo ");
		mapa.put("idCatalogo", idCatalogo);

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<Catalogo> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result.get(0);
	}

}
