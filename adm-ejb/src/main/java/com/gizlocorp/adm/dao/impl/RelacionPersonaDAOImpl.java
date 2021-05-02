package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.RelacionPersonaDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.RelacionPersona;


@Stateless
public class RelacionPersonaDAOImpl extends GenericJpaDAO<RelacionPersona, Long> implements RelacionPersonaDAO{
	
	@SuppressWarnings("unchecked")
	public List<RelacionPersona> obtenerPorParametros(String identificacion, String nombres, String apellidos, Long idOrganizacion, String tipoRelacion) throws GizloException{
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from RelacionPersona c where 1=1 ");

		if(identificacion != null && !identificacion.isEmpty()){
			sql.append("and c.persona.identificacion = :identificacion ");
			mapa.put("identificacion", identificacion);
		}
		
		if(nombres != null && !nombres.isEmpty()){
			sql.append("and upper(c.persona.nombres) like :nombres ");			
			mapa.put("nombres", "%"+nombres.toUpperCase()+"%");
		}
		
		if(apellidos != null && !apellidos.isEmpty()){			
			sql.append("and upper(c.persona.apellidos) like :apellidos ");			
			mapa.put("apellidos", "%"+apellidos.toUpperCase()+"%");
		}
		
		if(idOrganizacion != null){
			sql.append("and c.organizacion.id = :idOrganizacion ");
			mapa.put("idOrganizacion", idOrganizacion);
		}
		
		if(tipoRelacion != null){
			sql.append("and c.tipoRelacion = :tipoRelacion ");
			mapa.put("tipoRelacion", tipoRelacion);
		}
		
		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}
		
		List<RelacionPersona> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

	@SuppressWarnings("unchecked")
	public boolean existeRelacionPersona(Persona idPersona, Long idOrganizacion,
			String tipoRelacion) throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from RelacionPersona c where c.persona= :idPersona and c.organizacion.id = :idOrganizacion and c.tipoRelacion = :tipoRelacion  ");
		mapa.put("idPersona", idPersona);
		mapa.put("idOrganizacion", idOrganizacion);
		mapa.put("tipoRelacion", tipoRelacion);
		
		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}	
		
		List<RelacionPersona> result = q.getResultList();
		return result == null || result.isEmpty() ? false : true;
	}


}
