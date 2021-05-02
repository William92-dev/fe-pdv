package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

@ViewScoped
@Named("autenticacionBean")
public class AutenticacionBean extends BaseBean implements Serializable {

	private static Logger log = Logger.getLogger(AutenticacionBean.class
			.getName());

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametroLocal;

	@Inject
	private SessionBean sessionBean;

	private void cargarUsuario() {
		try {
			if (sessionBean.getUsuario() == null) {
				Principal principal = FacesContext.getCurrentInstance()
						.getExternalContext().getUserPrincipal();

				if (principal != null && principal.getName() != null) {
					Usuario usuario = usuarioUsuarioLocal
							.obtenerUsuario(principal.getName());

					if (usuario != null && usuario.getPersona() != null) {
						sessionBean.setUsuario(usuario);

						if (usuario.getPersona().getOrganizacion() != null) {
							sessionBean.setIdOrganizacion(usuario.getPersona()
									.getOrganizacion().getId());
							sessionBean.setRucOrganizacion(usuario.getPersona()
									.getOrganizacion().getRuc());
							sessionBean.setNombreOrganizacion(usuario
									.getPersona().getOrganizacion()
									.getNombreComercial());
							sessionBean.setLogoOrganizacion(usuario
									.getPersona().getOrganizacion()
									.getLogoEmpresa());
						}

						Parametro parametro = servicioParametroLocal
								.consultarParametro(
										Constantes.URL_SERVIDOR,
										usuario.getPersona().getOrganizacion() != null ? usuario
												.getPersona().getOrganizacion()
												.getId()
												: null);
						sessionBean
								.setUrlServidor(parametro != null ? parametro
										.getValor() : "");

						parametro = servicioParametroLocal
								.consultarParametro(
										Constantes.DIRECTORIO_SERVIDOR,
										usuario.getPersona().getOrganizacion() != null ? usuario
												.getPersona().getOrganizacion()
												.getId()
												: null);
						sessionBean
								.setDirectorioServidor(parametro != null ? parametro
										.getValor() : "");

						if (usuario != null
								&& usuario.getUsuariosRoles() != null
								&& !usuario.getUsuariosRoles().isEmpty()) {
							sessionBean.setRoleStr(usuario.getUsuariosRoles()
									.get(0).getRol().getNombre());
							sessionBean.setCodigoRol(usuario.getUsuariosRoles()
									.get(0).getRol().getCodigo());
						}

						
					}

				}

			}

		} catch (Exception e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al listar Parametros");
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6758452864069767772L;
	private String username;
	private String password;
	private String originalURL;

	@PostConstruct
	public void init() {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		originalURL = (String) externalContext.getRequestMap().get(
				RequestDispatcher.FORWARD_REQUEST_URI);

		if (originalURL == null) {
			originalURL = externalContext.getRequestContextPath()
					+ "/home.xhtml";
		} else {
			String originalQuery = (String) externalContext.getRequestMap()
					.get(RequestDispatcher.FORWARD_QUERY_STRING);

			if (originalQuery != null) {
				originalURL += "?" + originalQuery;
			}
		}
	}

	public String login() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext
					.getRequest();
			request.login(username, password);

			Usuario usuario = usuarioUsuarioLocal.obtenerUsuario(username);
			if (usuario != null && Estado.INA.equals(usuario.getEstado())) {
				infoMessage("Usuario inactivo", "Usuario inactivo");

				sessionBean.resetear();
				externalContext.invalidateSession();

				return null;
			}

			

			cargarUsuario();

			return "inicio";
		} catch (Exception e) {
			e.printStackTrace();
			infoMessage("Usuario o contraseña incorrecta",
					"Usuario o contraseña incorrecta");
		}
		return null;
	}

	public String logout() {
		try {
			sessionBean.resetear();
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.invalidateSession();
			return "inicio";

		} catch (Exception e) {
			infoMessage("A ocurrido un error al salir del sistema",
					"A ocurrido un error al salir del sistema");
		}
		return null;

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
