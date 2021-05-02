package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.UbicacionGeograficaDAO;
import com.gizlocorp.adm.modelo.UbicacionGeografica;


@Stateless
public class UbicacionGeograficaDAOImpl extends GenericJpaDAO<UbicacionGeografica, String> implements UbicacionGeograficaDAO{

		
	@SuppressWarnings("unchecked")
	public List<UbicacionGeografica> obtenerBasico(String codigo, String nombre) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from UbicacionGeografica c ");
		boolean axu=false;
		if(codigo != null && !codigo.isEmpty()){
			sql.append("where c.codigo = :codigo");
			mapa.put("codigo", codigo);
			axu = true;
		}
		
		if(nombre != null && !nombre.isEmpty()){
			if(axu){
				sql.append("and c.nombre = :nombre");
			}else{
				sql.append("where c.nombre = :nombre");				
			}
			mapa.put("nombre", nombre);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}
		
		List<UbicacionGeografica> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

}
