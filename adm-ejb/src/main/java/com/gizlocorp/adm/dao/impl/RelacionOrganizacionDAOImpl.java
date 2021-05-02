package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.RelacionOrganizacionDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.RelacionOrganizacion;


@Stateless
public class RelacionOrganizacionDAOImpl extends GenericJpaDAO<RelacionOrganizacion, Long> implements RelacionOrganizacionDAO{

	@SuppressWarnings("unchecked")
	public boolean existeRelacionOrganizacion(Organizacion organizacion, Organizacion organizacionRelacion, String tipoRelacion) throws GizloException {				
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from RelacionOrganizacion c where c.organizacion= :organizacion ");
		sql.append(" and c.organizacionRelacion = :organizacionRelacion ");			
		sql.append(" and c.tipoRelacion = :tipoRelacion  ");	
		mapa.put("organizacion", organizacion);
		mapa.put("organizacionRelacion", organizacionRelacion);		
		mapa.put("tipoRelacion", tipoRelacion);
		
		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}	
		
		
		List<RelacionOrganizacion> result = q.getResultList();
		return result == null || result.isEmpty() ? false : true;
		
	}

	@SuppressWarnings("unchecked")
	public List<RelacionOrganizacion> buscarPorParametros(String ruc, String nombre, String tipoRelacon) throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from RelacionOrganizacion c where 1=1 ");

		if(ruc != null && !ruc.isEmpty()){
			sql.append("and c.organizacion.ruc = :ruc ");
			mapa.put("ruc", ruc);
		}
		
		if(nombre != null && !nombre.isEmpty()){
			sql.append("and upper(c.organizacion.nombre) like :nombre ");			
			mapa.put("nombre", "%"+nombre.toUpperCase()+"%");
		}
		
		if(tipoRelacon != null && !tipoRelacon.isEmpty()){			
			sql.append("and c.tipoRelacion = :tipoRelacon ");			
			mapa.put("tipoRelacon", tipoRelacon);
		}
		
		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}	
		
		
		List<RelacionOrganizacion> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}
	
	

}
