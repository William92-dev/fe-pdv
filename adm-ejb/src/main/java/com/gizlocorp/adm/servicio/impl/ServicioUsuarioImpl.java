package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.security.auth.spi.Util;

import com.gizlocorp.adm.dao.PersonaDAO;
import com.gizlocorp.adm.dao.RolDAO;
import com.gizlocorp.adm.dao.UsuarioDAO;
import com.gizlocorp.adm.dao.UsuarioExternoDAO;
import com.gizlocorp.adm.dao.UsuarioRolDAO;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioExterno;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;

@Stateless
public class ServicioUsuarioImpl implements ServicioUsuarioLocal {

	@EJB
	UsuarioExternoDAO usuarioExternoDAO;
	@EJB
	UsuarioDAO usuarioDAO;
	// @EJB
	// RecursoDAO recursoDAO;
	// @EJB
	// OpcionDAO opcionDAO;
	@EJB
	UsuarioRolDAO usuarioRolDAO;
	@EJB
	PersonaDAO personaDAO;
	@EJB
	RolDAO rolDAO;

	private static Logger log = Logger.getLogger(ServicioUsuarioImpl.class
			.getName());

	public void guardarUsuarioExterno(UsuarioExterno usuarioExterno) {
		if (usuarioExterno.getId() == null) {
			try {
				String createPasswordHash = Util.createPasswordHash("SHA-256",
						"hex", null, null, usuarioExterno.getPassword());
				usuarioExterno.setPassword(createPasswordHash.toLowerCase());
				usuarioExternoDAO.persist(usuarioExterno);
			} catch (GizloPersistException e) {
				log.error("error usuario externo", e);
			}
		} else {
			try {
				String createPasswordHash = Util.createPasswordHash("SHA-256",
						"hex", null, null, usuarioExterno.getPassword());
				usuarioExterno.setPassword(createPasswordHash.toLowerCase());
				usuarioExternoDAO.update(usuarioExterno);
			} catch (GizloUpdateException e) {
				log.error("error usuario externo", e);
			}
		}

	}

	@Override
	public void eliminarUsuario(UsuarioExterno usuarioExterno)
			throws GizloException {
		try {
			List<UsuarioRol> roles = usuarioRolDAO
					.obtenerUsuarioRolPorUsuario(usuarioExterno);
			if (roles != null && !roles.isEmpty()) {
				for (UsuarioRol rol : roles) {
					usuarioRolDAO.delete(rol);
				}
			}
			usuarioExternoDAO.delete(usuarioExterno);
		} catch (GizloDeleteException e) {
			log.error("error usuario externo", e);
		}

	}

	@Override
	public List<UsuarioExterno> obtenerParametros(String nombres,
			String apellidos, String identificacion) {
		return usuarioExternoDAO.obtenerParametros(nombres, apellidos,
				identificacion);
	}

	@Override
	public List<Usuario> obtenerUsuarioParametros(String nombres,
			String apellidos, String identificacion) {
		return usuarioDAO.obtenerParametros(nombres, apellidos, identificacion);
	}

	@Override
	public Usuario obtenerUsuario(String username) {
		return usuarioDAO.obtenerUsuario(username);
	}

	@Override
	public UsuarioExterno obtenerUsuarioExterno(String username) {
		return usuarioExternoDAO.obtenerUsuarioExterno(username);
	}

	@Override
	public boolean tienePermiso(String username, String url)
			throws GizloException {
		boolean tienePermiso = false;
		Usuario usuario = usuarioDAO.obtenerUsuario(username);
		if (usuario == null) {
			throw new GizloException("No existe este usuario!");
		}
		//List<UsuarioRol> roles = usuario.getUsuariosRoles();
		// List<Opcion> opciones = null;
		//
		// if (roles != null && !roles.isEmpty()) {
		// opciones = new ArrayList<Opcion>();
		// Modulo recurso = (Modulo) recursoDAO.obtenerPorURL(url);
		// if (recurso == null) {
		// throw new GizloException("No existe esta url recurso!");
		// }
		// List<Opcion> opcionesTmp = null;
		// for (UsuarioRol usuRol : roles) {
		// opcionesTmp = opcionDAO.buscarPorRolModulo(usuRol.getRol(),
		// recurso);
		// if (opcionesTmp != null && !opcionesTmp.isEmpty()) {
		// opciones.addAll(opcionDAO.buscarPorRolModulo(
		// usuRol.getRol(), recurso));
		// }
		// }
		// }
		//
		// if (opciones != null && !opciones.isEmpty()) {
		// tienePermiso = true;
		// }
		return tienePermiso;

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<String> obtenerUsuariosSincronizar() {
		return usuarioDAO.obtenerUsuariosSincronizar();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void crearUsuarioConsulta(String identificacion) {
		try {

			if (identificacion == null || identificacion.isEmpty()
					|| identificacion.contains(" ")
					|| identificacion.length() > 20) {

				return;
			}

			List<Rol> rol = null;

			if (!usuarioExternoDAO.existeUsuario(identificacion)) {
				rol = rolDAO.obtenerPorBasico("CONSU", null, null);

				if (rol != null && !rol.isEmpty()) {
					String clave = Util.createPasswordHash("SHA-256", "hex",
							null, null, identificacion);
					UsuarioExterno usuario = null;
					Persona persona = null;
					UsuarioRol usuarioRol = null;

					usuario = new UsuarioExterno();
					usuario.setEstado(com.gizlocorp.adm.enumeracion.Estado.ACT);
					usuario.setUsername(identificacion);
					usuario.setPassword(clave);
					usuario.setEsClaveAutogenerada(Logico.S);

					persona = new Persona();
					persona.setApellidos("ACTUALIZA");
					persona.setNombres("ACTUALIZA");
					persona.setCorreoElectronico("ACTUALIZA");
					persona.setIdentificacion(identificacion);

					personaDAO.persist(persona);
					usuario.setPersona(persona);
					usuarioExternoDAO.persist(usuario);

					usuarioRol = new UsuarioRol();
					usuarioRol.setUsuario(usuario);
					usuarioRol.setRol(rol.get(0));

					usuarioRolDAO.persist(usuarioRol);
				}

			}

		} catch (Exception ex) {
			log.error("error usuario externo", ex);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<UsuarioExterno> obtenerUsuariosAutogenerados() {
		try {
			return usuarioExternoDAO.obtenerUsuariosAutogenerados();

		} catch (Exception ex) {
			log.error("error usuario externo obtenerUsuariosAutogenerados", ex);

		}

		return null;
	}
}
