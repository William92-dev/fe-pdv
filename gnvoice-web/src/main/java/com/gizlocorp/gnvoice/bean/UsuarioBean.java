package com.gizlocorp.gnvoice.bean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioExterno;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;
import com.gizlocorp.adm.servicio.local.ServicioRolLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioRolLocal;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.gnvoice.bean.databean.UsuarioDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

/**
 * 
 * @author Usuario
 * 
 */

// @Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("usuarioBean")
public class UsuarioBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(UsuarioBean.class.getName());

	@Inject
	private UsuarioDataBean usuarioDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioPersonaLocal")
	ServicioPersonaLocal servicioPersonaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioRolImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioRolLocal")
	ServicioUsuarioRolLocal usuarioUsuarioRolLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioRolImpl!com.gizlocorp.adm.servicio.local.ServicioRolLocal")
	ServicioRolLocal servicioRol;

	@Inject
	private SessionBean sessionBean;

	@PostConstruct
	public void postContruct() {
		try {

			// List<UsuarioExterno> listExternos = usuarioUsuarioLocal
			// .obtenerParametros(null, null, null);
			// usuarioDataBean.setListUsuarioExternos(listExternos);

		} catch (Exception ex) {
			log.error("Error  " + ex.getMessage(), ex);
			errorMessage("Ha ocurrido un error al listar Usuario");
		}

		
	}

	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	public String nuevoUsuario() {
		usuarioDataBean.setUsuarioExternoSeleccionada(new UsuarioExterno());
		usuarioDataBean.getUsuarioExternoSeleccionada().setPersona(
				new Persona());
		usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
				.setOrganizacion(new Organizacion());
		usuarioDataBean.setRol(new Rol());

		usuarioDataBean.setEsIngreso(true);
		usuarioDataBean.setEsModificacion(false);
		usuarioDataBean.setEditarArchivo(false);

		return "formularioUsuario";
	}

	public String editarUsuario() {
		usuarioDataBean.setRol(new Rol());
		usuarioDataBean.setOrganizacion(new Organizacion());
		String pass = usuarioDataBean.getUsuarioExternoSeleccionada()
				.getPassword();
		usuarioDataBean.getUsuarioExternoSeleccionada().setPassword(pass);
		
		log.info("log usuarios"+usuarioDataBean.getUsuarioExternoSeleccionada().getPersona().getOrganizacion());

		if (usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
				.getOrganizacion() == null) {
			usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
					.setOrganizacion(new Organizacion());
		}else{
			usuarioDataBean.setOrganizacion(usuarioDataBean.getUsuarioExternoSeleccionada().getPersona().getOrganizacion());
		}

		UsuarioRol usuarioRol = usuarioUsuarioRolLocal
				.obtenerUsuarioRolPorUsuario(usuarioDataBean
						.getUsuarioExternoSeleccionada());

		if (usuarioRol != null) {
			usuarioDataBean.setRol(usuarioRol.getRol());
		}
		

		usuarioDataBean.setEsIngreso(false);
		usuarioDataBean.setEsModificacion(true);
		usuarioDataBean.setEditarArchivo(false);

		return "formularioUsuario";
	}

	public String eliminarUsuario() {
		try {
			usuarioUsuarioLocal.eliminarUsuario(usuarioDataBean
					.getUsuarioExternoSeleccionada());
			infoMessage("Eliminado correctamente!", "Eliminado correctamente!");
			usuarioDataBean.setNombre(null);
			usuarioDataBean.setApellido(null);
			usuarioDataBean.setIdentificacion(null);
			consultarUsuario();
		} catch (Exception e) {
			log.error("Error  " + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al eliminar Usuario revise las dependencias");
		}

		return "";
	}

	public String editarFoto() {
		String pass = usuarioDataBean.getUsuarioExternoSeleccionada()
				.getPassword();
		usuarioDataBean.getUsuarioExternoSeleccionada().setPassword(pass);
		usuarioDataBean.setEsIngreso(false);
		usuarioDataBean.setEsModificacion(true);
		usuarioDataBean.setEditarArchivo(true);

		return "formularioUsuario";
	}

	/**
	 * regresa
	 * 
	 * @return
	 */
	public String regresar() {
		usuarioDataBean.setUsuarioExternoSeleccionada(new UsuarioExterno());
		usuarioDataBean.getUsuarioExternoSeleccionada().setPersona(
				new Persona());
		usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
				.setOrganizacion(new Organizacion());
		return "listaUsuario";
	}

	/**
	 * regresa
	 * 
	 * @return
	 */
	public String regresarEm() {
		usuarioDataBean.setUsuarioExternoSeleccionada(new UsuarioExterno());
		usuarioDataBean.getUsuarioExternoSeleccionada().setPersona(
				new Persona());
		usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
				.setOrganizacion(new Organizacion());
		return "listaUsuario_em";
	}

	public void guardar() {
		try {
			log.info("****guardando 14****");
			if (valida()) {
				if (usuarioDataBean.getUsuarioExternoSeleccionada()
						.getPersona().getOrganizacion() == null
						|| ((usuarioDataBean.getUsuarioExternoSeleccionada()
								.getPersona().getOrganizacion() != null && (usuarioDataBean
								.getUsuarioExternoSeleccionada().getPersona()
								.getOrganizacion().getId() == null || usuarioDataBean
								.getUsuarioExternoSeleccionada().getPersona()
								.getOrganizacion().getId() == 0)))) {
					usuarioDataBean.getUsuarioExternoSeleccionada()
							.getPersona().setOrganizacion(null);
				}
				
//				if(usuarioDataBean.getOrganizacion() != null){
//					log.info("fue diferente de null");
//					usuarioDataBean.getUsuarioExternoSeleccionada().getPersona().setOrganizacion(new Organizacion());
//					usuarioDataBean.getUsuarioExternoSeleccionada().getPersona().setOrganizacion(usuarioDataBean.getOrganizacion());
//				}

				List<Rol> roles = servicioRol.listarRol();

				if (sessionBean.getCodigoRol().equals("SOPOR")) {
					usuarioDataBean.setEstado(Estado.ACT);
					for (Rol rol : roles) {
						if (rol.getId().equals("CONSU")) {
							usuarioDataBean.setRol(rol);
						}
					}
				} else {
					for (Rol rol : roles) {
						if (rol.getId()
								.equals(usuarioDataBean.getRol().getId())) {
							usuarioDataBean.setRol(rol);
						}
					}
				}

				UsuarioRol usuarioRol = new UsuarioRol();

				if (usuarioDataBean.getUsuarioExternoSeleccionada().getId() != null) {
					servicioPersonaLocal.guardarPersona(usuarioDataBean
							.getUsuarioExternoSeleccionada().getPersona());
					usuarioUsuarioLocal.guardarUsuarioExterno(usuarioDataBean
							.getUsuarioExternoSeleccionada());

					usuarioRol.setRol(usuarioDataBean.getRol());
					usuarioRol.setUsuario(usuarioDataBean
							.getUsuarioExternoSeleccionada());
					usuarioUsuarioRolLocal.guardarUsuarioRol(usuarioRol);

					infoMessage("Modificado Correctamente!",
							"Modificado Correctamente!");
				} else {
					
//					usuarioDataBean.getUsuarioExternoSeleccionada().getPersona().setOrganizacion(usuarioDataBean.getOrganizacion());
					
					servicioPersonaLocal.guardarPersona(usuarioDataBean
							.getUsuarioExternoSeleccionada().getPersona());

					usuarioDataBean.getUsuarioExternoSeleccionada().setPersona(
							usuarioDataBean.getUsuarioExternoSeleccionada()
									.getPersona());
					usuarioDataBean.getUsuarioExternoSeleccionada().setEstado(
							Estado.ACT);
					usuarioDataBean.getUsuarioExternoSeleccionada()
							.setEsClaveAutogenerada(Logico.N);

					usuarioUsuarioLocal.guardarUsuarioExterno(usuarioDataBean
							.getUsuarioExternoSeleccionada());
					usuarioDataBean.setEsIngreso(false);

					usuarioRol.setRol(usuarioDataBean.getRol());
					usuarioRol.setUsuario(usuarioDataBean
							.getUsuarioExternoSeleccionada());
					usuarioUsuarioRolLocal.guardarUsuarioRol(usuarioRol);

					if (usuarioDataBean.getUsuarioExternoSeleccionada()
							.getPersona().getOrganizacion() == null) {
						usuarioDataBean.getUsuarioExternoSeleccionada()
								.getPersona()
								.setOrganizacion(new Organizacion());
					}

					infoMessage("Ingresado correctamente!",
							"Ingresado correctamente!");
				}
			}
		} catch (Exception ex) {
			log.error("Error entroo  " + ex.getMessage(), ex);
			errorMessage("Ha ocurrido un error al guardar Usuario");
		}

	}

	public void consultarUsuario() {

		if ((usuarioDataBean.getNombre() == null || usuarioDataBean.getNombre()
				.trim().isEmpty())
				&& (usuarioDataBean.getIdentificacion() == null || usuarioDataBean
						.getIdentificacion().trim().isEmpty())) {
			errorMessage("Ingresar un parametro de busqueda. ");

		} else {
			List<UsuarioExterno> listExternos = usuarioUsuarioLocal
					.obtenerParametros(usuarioDataBean.getNombre(),
							usuarioDataBean.getApellido(),
							usuarioDataBean.getIdentificacion());

			usuarioDataBean.setListUsuarioExternos(listExternos);

		}

		// Bitacora bitacoraEvento = new Bitacora();
		// try {
		// bitacoraEvento.setEvento(servicioCatalogoLocal.obtenerPorId(6L)
		// .getId());
		// } catch (GizloException e) {
		// e.printStackTrace();
		// }
		// bitacoraEvento.setDescripcion("Consulta Eventos");
		// bitacoraEvento.setUsuario("gnvoice");
		// bitacoraEvento.setFecha(new Date());
		// servicioBitacoraEvento.insertaEventoMdb(bitacoraEvento);
	}

	public void consultaPersona() {
		if (usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
				.getIdentificacion() != null) {
			try {
				List<Persona> liPersonas = servicioPersonaLocal
						.obtenerPorParametros(usuarioDataBean
								.getUsuarioExternoSeleccionada().getPersona()
								.getIdentificacion(), null, null, null);

				if (liPersonas != null && !liPersonas.isEmpty()) {
					usuarioDataBean.getUsuarioExternoSeleccionada().setPersona(
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

		Usuario usuario = usuarioUsuarioLocal.obtenerUsuario(usuarioDataBean
				.getUsuarioExternoSeleccionada().getUsername());

		if (usuario != null
				&& !usuario.getId()
						.equals(usuarioDataBean.getUsuarioExternoSeleccionada()
								.getId())) {
			errorMessage("Usuario ya existe. Utilizar funcionalidad de Editar",
					"Usuario ya existe. Utilizar funcionalidad de Editar");
			return false;
		}

		if (usuarioDataBean.getUsuarioExternoSeleccionada().getUsername()
				.length() < 3) {
			errorMessage("Usuario Debe Tener Minimo 3 Caracteres",
					"Usuario Debe Tener Minimo 3 Caracteres");
			return false;
		}
		if (usuarioDataBean.getUsuarioExternoSeleccionada().getPassword()
				.length() < 3) {
			errorMessage("Clave Debe Tener Minimo 3 Caracteres ",
					"Clave Debe Tener Minimo 3 Caracteres");
			return false;
		}

		return retorno;
	}

	public UsuarioDataBean getUsuarioDataBean() {
		return usuarioDataBean;
	}

	public void setUsuarioDataBean(UsuarioDataBean usuarioDataBean) {
		this.usuarioDataBean = usuarioDataBean;
	}

	public void cargarDocumento() {
		try {
			InputStream inputStream = usuarioDataBean.getUploadedFile()
					.getInputStream();
			String filename = getFilename(usuarioDataBean.getUploadedFile());
			log.debug(filename);
			byte[] bytes = IOUtils.toByteArray(inputStream);
			String foto = DocumentUtil
					.createDocumentImage(
							bytes,
							filename,
							usuarioDataBean.getUsuarioExternoSeleccionada() != null
									&& usuarioDataBean
											.getUsuarioExternoSeleccionada()
											.getPersona() != null
									&& usuarioDataBean
											.getUsuarioExternoSeleccionada()
											.getPersona().getIdentificacion() != null ? usuarioDataBean
									.getUsuarioExternoSeleccionada()
									.getPersona().getIdentificacion()
									: "999999999", "cliente", sessionBean
									.getDirectorioServidor());

			inputStream.close();

			log.debug("FOTO: " + foto);

			usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
					.setFoto(foto);

		} catch (Exception ex) {
			errorMessage("Ha ocurrido un error al cargar el documento",
					"Ha ocurrido un error al cargar el documento");
			log.error("Error cargarDocumento: " + ex.getMessage(), ex);
		}

	}

	public void cargarDocumentoEm(org.richfaces.event.FileUploadEvent event) {
		try {
			org.richfaces.model.UploadedFile item = event.getUploadedFile();

			String filename = item.getName();
			log.debug(filename);
			byte[] bytes = item.getData();
			String foto = DocumentUtil
					.createDocumentImage(
							bytes,
							filename,
							usuarioDataBean.getUsuarioExternoSeleccionada() != null
									&& usuarioDataBean
											.getUsuarioExternoSeleccionada()
											.getPersona() != null
									&& usuarioDataBean
											.getUsuarioExternoSeleccionada()
											.getPersona().getIdentificacion() != null ? usuarioDataBean
									.getUsuarioExternoSeleccionada()
									.getPersona().getIdentificacion()
									: "999999999", "cliente", sessionBean
									.getDirectorioServidor());

			log.debug("FOTO: " + foto);

			usuarioDataBean.getUsuarioExternoSeleccionada().getPersona()
					.setFoto(foto);

		} catch (Exception ex) {
			errorMessage("Ha ocurrido un error al cargar el documento",
					"Ha ocurrido un error al cargar el documento");
			log.error("Error cargarDocumento: " + ex.getMessage(), ex);
		}

	}

	private static String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim()
						.replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1)
						.substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
			}
		}
		return null;
	}

	public List<SelectItem> getOrganizaciones() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		try {
			List<Organizacion> organzaciones = servicioOrganizacion
					.listarOrganizaciones(null, null, null, null);

			for (Organizacion enumerador : organzaciones) {
				items.add(new SelectItem(enumerador.getId(), enumerador
						.getNombre()));
			}

		} catch (GizloException ex) {
			log.error("Error cargar organizaciones: ", ex);
		}

		return items;
	}

	public List<SelectItem> getRoles() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		try {
			List<Rol> roles = servicioRol.listarRol();

			for (Rol enumerador : roles) {
				items.add(new SelectItem(enumerador.getId(), enumerador
						.getNombre()));
			}

		} catch (GizloException ex) {
			log.error("Error cargar organizaciones: ", ex);
		}

		return items;
	}
}
