package com.gizlocorp.adm.dao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.UsuarioRolDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioRol;

/**
 * @author Jose Vinueza
 * 
 */

@Stateless
public class UsuarioRolDAOImpl extends GenericJpaDAO<UsuarioRol, String>
		implements UsuarioRolDAO {

	@Override
	public List<UsuarioRol> obtenerUsuarioRolPorUsuario(Usuario usuario)
			throws GizloException {
		try {
			String hql = "select r from UsuarioRol r where r.usuario=:usuario";
			Query q = em.createQuery(hql);
			q.setParameter("usuario", usuario);

			@SuppressWarnings("unchecked")
			List<UsuarioRol> result = q.getResultList();
			return result;

		} catch (Exception e) {
			throw new GizloException(
					"Se produjo un error al buscar el rol por nombre.", e);
		}
	}
}
