package com.gizlocorp.gnvoice.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.ClaveContingenciaDAO;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;

@Stateless
public class ClaveContingenciaDAOImpl extends
		GenericJpaDAO<ClaveContingencia, Long> implements ClaveContingenciaDAO {

	private static Logger log = Logger.getLogger(ClaveContingenciaDAOImpl.class
			.getName());

	public ClaveContingencia recuperar(String clave) throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from ClaveContingencia c where c.clave = :clave ");
		mapa.put("clave", clave);

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (ClaveContingencia) q
				.getResultList().get(0) : null;
	}

	@Override
	public ClaveContingencia recuperarNoUsada(Long idOrganizacion,
			String tipoAmbiente, String claveAcceso) throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from ClaveContingencia c where c.idOrganizacion = :idOrganizacion and c.usada = :usada and c.tipoAmbiente = :tipoAmbiente");

		mapa.put("tipoAmbiente", tipoAmbiente);
		mapa.put("idOrganizacion", idOrganizacion);
		mapa.put("usada", Logico.N);

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		ClaveContingencia c  = (q.getResultList() != null && !q.getResultList().isEmpty()) ? (ClaveContingencia) q
				.getResultList().get(0) : null;
		if(c!=null){
			c.setClaveAcceso(claveAcceso);
			c.setUsada(Logico.S);
		}
		
		em.merge(c);
		
		return c;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClaveContingencia> listarClaves(Long idOrganizacion,
			TipoAmbienteEnum tipoAmbiente, Logico estado) throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			sql.append("select c from ClaveContingencia c where 1=1");
			if (idOrganizacion != null && idOrganizacion != 0) {
				sql.append(" and c.idOrganizacion = :idOrganizacion");
				mapa.put("idOrganizacion", idOrganizacion);
			}

			if (tipoAmbiente != null) {
				sql.append(" and c.tipoAmbiente = :tipoAmbiente");
				mapa.put("tipoAmbiente", tipoAmbiente.getCode());
			}

			if (estado != null) {
				sql.append(" and c.usada = :estado");
				mapa.put("estado", estado);
			}

			log.debug("LISTAR CLAVES: " + idOrganizacion + " " + tipoAmbiente
					+ " " + estado);
			Query q = em.createQuery(sql.toString());
			for (String key : mapa.keySet()) {
				q.setParameter(key, mapa.get(key));
			}

			return q.getResultList();
		} catch (Exception e) {
			throw new GizloException("Ha ocurrido un error al listar claves!",
					e);
		}

	}

	@Override
	public int contarClaves(Long idOrganizacion, String tipoAmbiente,
			Logico estado) throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			sql.append("select count(c) from ClaveContingencia c where 1=1");
			if (idOrganizacion != null && idOrganizacion != 0) {
				sql.append(" and c.idOrganizacion = :idOrganizacion");
				mapa.put("idOrganizacion", idOrganizacion);
			}

			if (tipoAmbiente != null) {
				sql.append(" and c.tipoAmbiente = :tipoAmbiente");
				mapa.put("tipoAmbiente", tipoAmbiente);
			}

			if (estado != null) {
				sql.append(" and c.usada = :estado");
				mapa.put("estado", estado);
			}

			// log.debug("LISTAR CLAVES: "+idOrganizacion+" "+tipoAmbiente+" "+estado);
			Query q = em.createQuery(sql.toString());
			for (String key : mapa.keySet()) {
				q.setParameter(key, mapa.get(key));
			}

			Long respuesta = (Long) q.getSingleResult();
			// Long respuesta = (q.getResultList() != null &&
			// !q.getResultList().isEmpty()) ? (Long) (q.getResultList().get(0))
			// : 0;
			log.debug("RESPUESTA: " + respuesta);
			return respuesta != null ? respuesta.intValue() : 0;
			// return respuesta.intValue();
		} catch (Exception e) {
			throw new GizloException("Ha ocurrido un error al contar claves!",
					e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClaveContingencia> recuperarClaveRelacionada(String claveAcceso)
			throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			sql.append("select c from ClaveContingencia c where 1=1");
			
			if (claveAcceso != null) {
				sql.append(" and c.claveAcceso = :claveAcceso");
				mapa.put("claveAcceso", claveAcceso);
			}

			log.debug("LISTAR CLAVES: ");
			Query q = em.createQuery(sql.toString());
			for (String key : mapa.keySet()) {
				q.setParameter(key, mapa.get(key));
			}

			return q.getResultList();
		} catch (Exception e) {
			throw new GizloException("Ha ocurrido un error al listar claves!",
					e);
		}
	}
}
