package com.gizlocorp.gnvoice.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.jboss.security.auth.spi.Util;
import org.switchyard.component.bean.Service;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.UsuarioExterno;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;
import com.gizlocorp.adm.servicio.local.ServicioRolLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioRolLocal;
import com.gizlocorp.gnvoice.service.Seguridad;
import com.gizlocorp.gnvoice.xml.message.SeguridadRecuperarCredencialesResponse;
import com.gizlocorp.gnvoice.xml.message.SeguridadRecuperarCredencialesResponse.Login;
import com.gizlocorp.gnvoice.xml.message.SeguridadRecuperarCredencialesResponse.Respuesta;

@Service(Seguridad.class)
public class SeguridadBean implements Seguridad {

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;

	// @EJB(lookup =
	// "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	// ServicioCatalogoLocal catalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioPersonaLocal")
	ServicioPersonaLocal personaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioRolImpl!com.gizlocorp.adm.servicio.local.ServicioRolLocal")
	ServicioRolLocal rolLocal;

	// @EJB(lookup =
	// "java:global/adm-ejb/ServicioBitacoraEventoImpl!com.gizlocorp.adm.servicio.local.ServicioBitacoraEventoLocal")
	// ServicioBitacoraEventoLocal servicioBitacoraEvento;

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioRolImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioRolLocal")
	ServicioUsuarioRolLocal servicioUsuarioRolLocal;

	private static Logger log = Logger.getLogger(SeguridadBean.class.getName());

	@Override
	public SeguridadRecuperarCredencialesResponse recuperarCredenciales(
			String identificacion) {

		SeguridadRecuperarCredencialesResponse respuestaSeguridad = new SeguridadRecuperarCredencialesResponse();
		SeguridadRecuperarCredencialesResponse.Login login = new Login();
		SeguridadRecuperarCredencialesResponse.Respuesta respuesta = new Respuesta();

		try {

			if (identificacion == null || identificacion.isEmpty()
					|| identificacion.contains(" ")
					|| identificacion.length() > 20) {
				login.setClave("Identificacion invalida");
				login.setUsuario("Identificacion invalida");
				login.setEstado("E");

				respuesta.setLogin(login);
				respuestaSeguridad.setHttpStatus("104");
				respuestaSeguridad.setRespuesta(respuesta);

				return respuestaSeguridad;
			}

			List<UsuarioExterno> usuarios = usuarioUsuarioLocal
					.obtenerParametros(null, null, identificacion);

			// String claveActualizada = PasswordGenerator.generate();
			String claveActualizada = identificacion;
			String clave = Util.createPasswordHash("SHA-256", "hex", null,
					null, claveActualizada);

			UsuarioExterno usuario = null;
			if (usuarios == null || usuarios.isEmpty()) {
				log.debug("NUEVO USUARIO");
				usuario = new UsuarioExterno();
				usuario.setEstado(Estado.ACT);
				usuario.setUsername(identificacion);
				usuario.setPassword(claveActualizada);
				usuario.setEsClaveAutogenerada(Logico.S);

				Persona persona = new Persona();
				persona.setApellidos("ACTUALIZA");
				persona.setNombres("ACTUALIZA");
				persona.setCorreoElectronico("ACTUALIZA");
				persona.setIdentificacion(identificacion);

				// ThreadLocalCHAdm.setUsername("anonimo");
				personaLocal.guardarPersona(persona);
				usuario.setPersona(persona);
				usuarioUsuarioLocal.guardarUsuarioExterno(usuario);

				List<UsuarioRol> rolesUsuario = new ArrayList<UsuarioRol>();

				Rol rol = rolLocal.buscarRol("CONSU", null);
				UsuarioRol usuarioRol = new UsuarioRol();
				usuarioRol.setUsuario(usuario);
				usuarioRol.setRol(rol);
				rolesUsuario.add(usuarioRol);

				/*
				 * rol = rolLocal.buscarRol("SEGUM", null); usuarioRol= new
				 * UsuarioRol(); usuarioRol.setUsuario(usuario);
				 * usuarioRol.setRol(rol); rolesUsuario.add(usuarioRol);
				 */

				log.debug("ROL DE USUARIO: CONSULTA");
				rolLocal.guardarUsuarioRol(rolesUsuario);
			} else {
				log.debug("ROL DE USUARIO EXISTENTE");
				usuario = usuarios.get(0);
			}

			if (usuario.getEsClaveAutogenerada() != null
					&& usuario.getEsClaveAutogenerada().equals(Logico.S)) {
				log.debug("SETEA CLAVE");

				usuario.setPassword(clave);
				// ThreadLocalCHAdm.setUsername("anonimo");
				usuarioUsuarioLocal.guardarUsuarioExterno(usuario);

				login.setClave(claveActualizada);
				login.setUsuario(usuario.getUsername());
				login.setEstado("S");
				respuesta.setLogin(login);
				respuestaSeguridad.setHttpStatus("104");
				respuestaSeguridad.setRespuesta(respuesta);

			} else {
				login.setClave("VALUE");
				login.setUsuario(usuario.getUsername());
				login.setEstado("N");

				respuesta.setLogin(login);
				respuestaSeguridad.setHttpStatus("105");
				respuestaSeguridad.setRespuesta(respuesta);
			}

			return respuestaSeguridad;

		} catch (Exception ex) {

			log.error("Error proceso seguridad", ex);

			login.setClave("Error al actualizar Usuario");
			login.setUsuario("Error al actualizar Usuario");
			login.setEstado("E");

			respuesta.setLogin(login);
			respuestaSeguridad.setHttpStatus("104");
			respuestaSeguridad.setRespuesta(respuesta);

			return respuestaSeguridad;
		}

	}

}
