package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.UsuarioExternoDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.UsuarioExterno;

@Stateless
public class UsuarioExternoDAOImpl extends
		GenericJpaDAO<UsuarioExterno, String> implements UsuarioExternoDAO {

	@SuppressWarnings("unchecked")
	public List<UsuarioExterno> obtenerParametros(String nombres,
			String apellidos, String identificacion) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Usuario c where 1=1 ");
		if (identificacion != null && !identificacion.isEmpty()) {
			sql.append("and c.persona.identificacion = :identificacion ");
			mapa.put("identificacion", identificacion);
		}

		if (nombres != null && !nombres.isEmpty()) {
			sql.append("and upper(c.persona.nombres) like :nombres ");
			mapa.put("nombres", "%" + nombres.toUpperCase() + "%");
		}

		if (apellidos != null && !apellidos.isEmpty()) {
			sql.append("and upper(c.persona.apellidos) like :apellidos ");
			mapa.put("apellidos", "%" + apellidos.toUpperCase() + "%");
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<UsuarioExterno> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UsuarioExterno obtenerUsuarioExterno(String username) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Usuario c where c.username = :username ");
		mapa.put("username", username);

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<UsuarioExterno> result = q.getResultList();
		return result != null && !result.isEmpty() ? result.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UsuarioExterno> obtenerUsuariosAutogenerados()
			throws GizloException {
		try {
			Query q = em
					.createQuery("select c from Usuario c where c.esClaveAutogenerada = 'S'");

			List<UsuarioExterno> result = q.getResultList();
			return result;
		} catch (Exception ex) {
			throw new GizloException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean existeUsuario(String username) {
		Query q = em
				.createNativeQuery("SELECT USERNAME FROM ADM.TB_USUARIO WHERE USERNAME = '"
						+ username + "'");
		List<String> result = q.getResultList();
		return (result != null && !result.isEmpty());
	}
}
