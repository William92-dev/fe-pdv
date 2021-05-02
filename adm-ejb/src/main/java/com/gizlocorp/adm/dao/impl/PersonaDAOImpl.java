package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.PersonaDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Persona;

@Stateless
public class PersonaDAOImpl extends GenericJpaDAO<Persona, String> implements PersonaDAO{

	@SuppressWarnings("unchecked")
	public List<Persona> obtenerPorParametros(String identificacion, String nombres, String apellidos, Long idOrganizacion) throws GizloException{
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Persona c where 1=1 ");

		if(identificacion != null && !identificacion.isEmpty()){
			sql.append("and c.identificacion = :identificacion ");
			mapa.put("identificacion", identificacion);
		}
		
		if(nombres != null && !nombres.isEmpty()){
			sql.append("and upper(c.nombres) like :nombres ");			
			mapa.put("nombres", "%"+nombres.toUpperCase()+"%");
		}
		
		if(apellidos != null && !apellidos.isEmpty()){			
			sql.append("and upper(c.apellidos) like :apellidos ");			
			mapa.put("apellidos", "%"+apellidos.toUpperCase()+"%");
		}
		
		if(idOrganizacion != null){
			sql.append("and c.Organizacion.id = :idOrganizacion ");
			mapa.put("idOrganizacion", idOrganizacion);
		}
		
		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}
		
		List<Persona> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

}
