package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jboss.security.auth.spi.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioExterno;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.gnvoice.bean.databean.RestablecerUsuarioDataBean;
import com.gizlocorp.gnvoice.json.model.Dato;
import com.gizlocorp.gnvoice.json.model.DatosPersona;
import com.gizlocorp.gnvoice.json.model.Respuesta;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.RestServiceInvoker;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

/**
 * 
 * @author Usuario
 * 
 */

// @Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("restablecerUsuarioBean")
public class RestablecerUsuarioBean extends BaseBean implements Serializable {

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametroLocal;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(RestablecerUsuarioBean.class
			.getName());

	private boolean habilitado = true;

	@Inject
	private RestablecerUsuarioDataBean restablecerUsuarioDataBean;

	private UsuarioExterno usuarioExternoSeleccionada;

	private String password = "";

	@EJB(lookup = "java:global/adm-ejb/ServicioPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioPersonaLocal")
	ServicioPersonaLocal servicioPersonaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;

	

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	SessionBean sessionBean;

	@PostConstruct
	public void postContruct() {
		try {
			UsuarioExterno usuario = cargarUsuario();

			if (restablecerUsuarioDataBean.getListOrganizacion() == null
					|| restablecerUsuarioDataBean.getListOrganizacion()
							.isEmpty()) {
				List<Organizacion> organzaciones = servicioOrganizacion
						.listarOrganizaciones(null, null, null, null);
				restablecerUsuarioDataBean.setListOrganizacion(organzaciones);
			}

			String nombre = null, apellido = null, correo = null;
			try {
				Parametro parametro = servicioParametroLocal
						.consultarParametro(Constantes.URL_CONSULTA_USUARIO,
								null);
				String url = parametro != null ? parametro.getValor()
						: "http://10.10.200.67:8080/WebApplicationCorporacionGPF/resources/FacturacionElectronicaGPF/consultaDatosCliente/";

				String identificacion = usuario.getPersona()
						.getIdentificacion(); // "1720811346"
				String urlWS = url + identificacion;
				String response = RestServiceInvoker.invokeWS(urlWS);
				if (response == null) {
					throw new Exception("No se encontro resultados");
				}
				log.debug("RESPUESTA: " + response);
				ObjectMapper mapper = new ObjectMapper();
				log.debug("mapper: ");
				DatosPersona datosPersona = mapper.readValue(response,
						DatosPersona.class);
				log.debug("DATOS PERSONA: " + datosPersona);

				if (datosPersona != null) {
					if (datosPersona.getDatosPersona() != null
							&& !datosPersona.getDatosPersona().isEmpty()) {
						for (Dato dato : datosPersona.getDatosPersona()) {
							if (dato.getHttpStatus().equals("104")) {
								if (dato.getRespuestas() != null
										&& !dato.getRespuestas().isEmpty()) {
									for (Respuesta respuesta : dato
											.getRespuestas()) {
										nombre = (respuesta.getPrimerNombre() != null ? respuesta
												.getPrimerNombre() : "")
												+ " "
												+ (respuesta.getSegundoNombre() != null ? respuesta
														.getSegundoNombre()
														: "");
										apellido = (respuesta
												.getPrimerApellido() != null ? respuesta
												.getPrimerApellido() : "")
												+ " "
												+ (respuesta
														.getSegundoApellido() != null ? respuesta
														.getSegundoApellido()
														: "");
										correo = (respuesta.getMail() != null ? respuesta
												.getMail() : "");
									}
								}
							} else {
								log.debug("*** SERVICIO: " + urlWS);
								log.debug("*** HTTPSTATUS: "
										+ dato.getHttpStatus());
								log.debug("*** MENSAJE: " + dato.getMensaje());
							}
						}
					}
				}
			} catch (Exception e) {
				log.debug("RESTINVOKE OCURRIO ERROR: " + e);
			}

			this.setUsuarioExternoSeleccionada(usuario);
			this.getUsuarioExternoSeleccionada().setPersona(
					usuario.getPersona());

			// ACTUALIZA DATOS
			this.getUsuarioExternoSeleccionada()
					.getPersona()
					.setNombres(
							(nombre != null && !nombre.isEmpty()) ? nombre
									: "ACTUALIZA");
			this.getUsuarioExternoSeleccionada()
					.getPersona()
					.setApellidos(
							(apellido != null && !apellido.isEmpty()) ? apellido
									: "ACTUALIZA");
			this.getUsuarioExternoSeleccionada()
					.getPersona()
					.setCorreoElectronico(
							(correo != null && !correo.isEmpty()) ? correo
									: "ACTUALIZA");

			String pass = this.getUsuarioExternoSeleccionada().getPassword();
			this.getUsuarioExternoSeleccionada().setPassword(pass);
			if (this.getUsuarioExternoSeleccionada().getPersona()
					.getOrganizacion() == null) {
				this.getUsuarioExternoSeleccionada().getPersona()
						.setOrganizacion(new Organizacion());
			}

			
		} catch (Exception ex) {
			log.error("Error  " + ex.getMessage(), ex);
			errorMessage("Ha ocurrido un error al listar Usuario");
		}

	}

	private UsuarioExterno cargarUsuario() {
		UsuarioExterno usuario = null;
		try {
			if (usuario == null) {
				Principal principal = FacesContext.getCurrentInstance()
						.getExternalContext().getUserPrincipal();

				if (principal != null && principal.getName() != null) {
					usuario = usuarioUsuarioLocal
							.obtenerUsuarioExterno(principal.getName());
				}

			}

		} catch (Exception e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al listar Parametros");
		}
		return usuario;
	}

	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	/**
	 * regresa
	 * 
	 * @return
	 */
	public String regresar() {
		ExternalContext ec = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession httpSession = (HttpSession) ec.getSession(false);

		if (httpSession != null) {
			httpSession.invalidate();
		}
		return "inicio";

	}

	public void guardar() {
		try {
			if (valida()) {
				if (this.getUsuarioExternoSeleccionada() != null
						&& this.getUsuarioExternoSeleccionada().getId() != null) {

					this.getUsuarioExternoSeleccionada()
							.setEsClaveAutogenerada(Logico.N);
					this.getUsuarioExternoSeleccionada().setEstado(Estado.ACT);

					String createPasswordHash = Util.createPasswordHash(
							"SHA-256", "hex", null, null, this.getPassword());
					this.getUsuarioExternoSeleccionada().setPassword(
							createPasswordHash.toLowerCase());

					this.getUsuarioExternoSeleccionada().getPersona()
							.setOrganizacion(null);
					servicioPersonaLocal.guardarPersona(this
							.getUsuarioExternoSeleccionada().getPersona());
					usuarioUsuarioLocal.guardarUsuarioExterno(this
							.getUsuarioExternoSeleccionada());

					infoMessage("Usuario actualizado correctamente.",
							"Usuario actualizado correctamente");
				}
			}
		} catch (Exception ex) {
			log.error("Error  " + ex.getMessage(), ex);
			errorMessage("Ha ocurrido un error al guardar Usuario");
		}
		// return null;
	}

	public void consultaPersona() {
		if (this.getUsuarioExternoSeleccionada().getPersona()
				.getIdentificacion() != null) {
			try {
				List<Persona> liPersonas = servicioPersonaLocal
						.obtenerPorParametros(this
								.getUsuarioExternoSeleccionada().getPersona()
								.getIdentificacion(), null, null, null);

				if (liPersonas != null && !liPersonas.isEmpty()) {
					this.getUsuarioExternoSeleccionada().setPersona(
							liPersonas.get(0));
				}
			} catch (GizloException ex) {
				log.error("Error: " + ex.getMessage(), ex);
				errorMessage("Ha ocurrido un error al listar Personas");
			}
		}
	}

	private boolean valida() {
		boolean retorno = true;

		Usuario usuario = usuarioUsuarioLocal.obtenerUsuario(this
				.getUsuarioExternoSeleccionada().getUsername());

		if (usuario != null
				&& !usuario.getId().equals(
						this.getUsuarioExternoSeleccionada().getId())) {
			errorMessage("Nombre de usuario ya existe",
					"Nombre de usuario ya existe");
			return false;
		}

		if (this.getUsuarioExternoSeleccionada().getUsername().length() < 3) {
			errorMessage("Usuario Debe Tener Minimo 3 Caracteres",
					"Usuario Debe Tener Minimo 3 Caracteres");
			return false;
		}
		if (this.getUsuarioExternoSeleccionada().getPassword().length() < 3) {
			errorMessage("Clave Debe Tener Minimo 3 Caracteres ",
					"Clave Debe Tener Minimo 3 Caracteres");
			return false;
		}

		return retorno;
	}

	public UsuarioExterno getUsuarioExternoSeleccionada() {
		return usuarioExternoSeleccionada;
	}

	public void setUsuarioExternoSeleccionada(
			UsuarioExterno usuarioExternoSeleccionada) {
		this.usuarioExternoSeleccionada = usuarioExternoSeleccionada;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
