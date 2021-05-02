package com.gizlocorp.adm.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.RolDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.UsuarioRol;

@Stateless
public class RolDAOImpl extends GenericJpaDAO<Rol, String> implements RolDAO {

	@SuppressWarnings("unchecked")
	public List<Rol> obtenerPorBasico(String codigo, String nombre, Rol rolPadre) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Rol c ");
		boolean axu = false;
		if (codigo != null && !codigo.isEmpty()) {
			sql.append("where c.codigo = :codigo");
			mapa.put("codigo", codigo);
			axu = true;
		}

		if (nombre != null && !nombre.isEmpty()) {
			if (axu) {
				sql.append("and c.nombre = :nombre");
			} else {
				sql.append("where c.nombre = :nombre");
				axu = true;
			}
			mapa.put("nombre", nombre);
		}

		if (rolPadre != null) {
			if (axu)
				sql.append("and c.rolPadre = :rolPadre");
			else
				sql.append("where c.rolPadre = :rolPadre");

			mapa.put("rolPadre", rolPadre);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<Rol> result = q.getResultList();
		return result == null || result.isEmpty() ? null : result;
	}

	@Override
	public void guardarUsuarioRol(List<UsuarioRol> listaUsuarioRol)
			throws GizloException {
		try {
			for (UsuarioRol usuarioRol : listaUsuarioRol) {
				em.persist(usuarioRol);
			}
		} catch (Exception e) {
			throw new GizloException(
					"Se produjo un error al guardar roles de usuario", e);
		}
	}

	@Override
	public Rol buscarPorNombre(String nombreRol) throws GizloException {
		try {
			String hql = "select r from Rol r where r.nombre=:nombreRol";
			Query q = em.createQuery(hql);
			q.setParameter("nombreRol", nombreRol);

			List<Rol> result = q.getResultList();
			return result != null && !result.isEmpty() ? result.get(0) : null;

		} catch (Exception e) {
			throw new GizloException(
					"Se produjo un error al buscar el rol por nombre.", e);
		}
	}
}
