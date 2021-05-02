package com.gizlocorp.gnvoice.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.AccessTimeout;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.ColaFileClaveDAO;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
//import com.gizlocorp.adm.excepcion.GizloPersistException;
//import com.gizlocorp.gnvoice.dao.ClaveContingenciaDAO;
//import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
//import com.gizlocorp.gnvoice.modelo.ColaFileClave;
//import com.gizlocorp.gnvoice.dao.ClaveContingenciaDAO;
//import com.gizlocorp.gnvoice.dao.ColaFileClaveDAO;
import com.gizlocorp.gnvoice.modelo.ColaFileClave;


@Stateless
public class ColaFileClaveDAOImpl extends  
			GenericJpaDAO<ColaFileClave, Long> implements ColaFileClaveDAO  {

	
	private static Logger log = Logger.getLogger(ColaFileClaveDAOImpl.class.getName());
	
	@PersistenceContext
	EntityManager em ; 
	
	
	public void persistCola(ColaFileClave entity) {
		try {
			em.persist(entity);
			//return entity;
		} catch (Throwable ex) {
			//throw new GizloPersistException(entity, ex);
		}
	}
		
		
	    @SuppressWarnings("unchecked")
		@Override
		public List<ColaFileClave> listaColaCargada() throws GizloException {
			try {
				StringBuilder sql = new StringBuilder();
				sql.append("select c from ColaFileClave c where 1=1");
				System.out.println(sql);
				log.debug("LISTAR COLA CARGADA: "+sql);
				Query q = em.createQuery(sql.toString());
				return q.getResultList();
			} catch (Exception e) {
				throw new GizloException("Ha ocurrido un error al listar colas cargadas!",
						e);
			}
		}
		
	
	
	
	
	
	
	
	
}
