package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.TipoCatalogoDAO;
import com.gizlocorp.adm.modelo.TipoCatalogo;


@Stateless
public class TipoCatalogoDAOImpl extends GenericJpaDAO<TipoCatalogo, String> implements TipoCatalogoDAO{

	
	@SuppressWarnings("unchecked")
	public List<TipoCatalogo> obtenerBasico(String codigo, String nombre) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from TipoCatalogo c where 1=1");
		if(codigo != null && !codigo.isEmpty()){
			sql.append("and c.codigo = :codigo");
			mapa.put("codigo", codigo);
		}
		
		if(nombre != null && !nombre.isEmpty()){
			sql.append("and upper(c.nombre) like :nombre ");			
			mapa.put("nombre", "%"+nombre.toUpperCase()+"%");
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}
		
		List<TipoCatalogo> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;

	}

}
