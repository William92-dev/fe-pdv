package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.dao.UsuarioRolDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioRolLocal;

@Stateless
public class ServicioUsuarioRolImpl implements ServicioUsuarioRolLocal {

	@EJB
	UsuarioRolDAO usuarioRolDAO;

	private static Logger log = Logger.getLogger(ServicioUsuarioRolImpl.class
			.getName());

	@Override
	public void guardarUsuarioRol(UsuarioRol usuarioRol) {
		try {
			List<UsuarioRol> roles = usuarioRolDAO
					.obtenerUsuarioRolPorUsuario(usuarioRol.getUsuario());
			log.debug("*************" + roles);
			if (roles != null && !roles.isEmpty()) {
				for (UsuarioRol rol : roles) {
					usuarioRolDAO.delete(rol);
				}
			}
			if (usuarioRol.getId() == null) {
				usuarioRolDAO.persist(usuarioRol);
			} else {
				usuarioRolDAO.update(usuarioRol);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public UsuarioRol obtenerUsuarioRolPorUsuario(Usuario usuario) {
		List<UsuarioRol> usuarioRol = null;
		try {
			usuarioRol = usuarioRolDAO.obtenerUsuarioRolPorUsuario(usuario);
			return usuarioRol != null && !usuarioRol.isEmpty() ? usuarioRol
					.get(0) : null;

		} catch (GizloException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
