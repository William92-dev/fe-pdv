package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.TipoRelacion;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.RelacionPersona;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;
import com.gizlocorp.adm.servicio.local.ServicioRelacionPersonaLocal;
import com.gizlocorp.gnvoice.bean.databean.RelacionPersonaDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

/**
 * 
 * @author Jose Vinueza
 * 
 */

// @Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("relacionPersonaBean")
public class RelacionPersonaBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(RelacionPersonaBean.class
			.getName());

	@Inject
	private RelacionPersonaDataBean relacionPersonaDataBean;

	// @Inject private ApplicationBean appBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioRelacionPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioRelacionPersonaLocal")
	ServicioRelacionPersonaLocal servicioRelacionPersonaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioPersonaLocal")
	ServicioPersonaLocal servicioPersonaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	SessionBean sessionBean;

	@PostConstruct
	public void postContruct() {
		try {
			servicioRelacionPersonaLocal.obtenerPorParametros(
					relacionPersonaDataBean.getIdentificacion(),
					relacionPersonaDataBean.getNombre(),
					relacionPersonaDataBean.getApellido(),
					relacionPersonaDataBean.getIdOrganizacion(),
					relacionPersonaDataBean.getTipoRelacion());
			
		} catch (Exception e) {
			log.debug("Error al Listar Relacion Persona" + e.getMessage());
		}
	}

	public List<SelectItem> getTipoRelacion() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoRelacion estado : TipoRelacion.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	/**
	 * Busca Persona
	 */
	public void buscarPersona() {

		try {
			relacionPersonaDataBean
					.setListRelacionPersona(servicioRelacionPersonaLocal.obtenerPorParametros(
							relacionPersonaDataBean.getIdentificacion(),
							relacionPersonaDataBean.getNombre(),
							relacionPersonaDataBean.getApellido(),
							relacionPersonaDataBean.getIdOrganizacion(),
							relacionPersonaDataBean.getTipoRelacion()));
		} catch (GizloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void guardar() {
		try {
			if (relacionPersonaDataBean.isNuevo()) {
				if (verificaRelacionPersona()) {
					String info = "Ya existe el la Relacion de la Persona !";
					infoMessage(info, info);
					return;
				}
				if (!relacionPersonaDataBean.isExistePerson()) {
					servicioPersonaLocal.guardarPersona(relacionPersonaDataBean
							.getRelacionPersona().getPersona());
				}
				relacionPersonaDataBean.getRelacionPersona().setIdPersona(
						relacionPersonaDataBean.getRelacionPersona()
								.getPersona().getId());
				servicioRelacionPersonaLocal
						.guardarRelacionPersona(relacionPersonaDataBean
								.getRelacionPersona());
				infoMessage("Ingreso Exitosa!", "Ingreso Exitosa!");
			} else {
				if (relacionPersonaDataBean.isEditar()) {
					servicioRelacionPersonaLocal
							.guardarRelacionPersona(relacionPersonaDataBean
									.getRelacionPersona());
					infoMessage("Modificacion Exitosa!",
							"Modificacion Exitosa!");

				}
			}
		} catch (Exception e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al guardar");
		}
	}

	public boolean verificaRelacionPersona() {
		boolean booRetorno = false;
		try {
			if (relacionPersonaDataBean.isExistePerson()) {
				booRetorno = servicioRelacionPersonaLocal
						.existeRelacionPersona(relacionPersonaDataBean
								.getRelacionPersona().getPersona(),
								relacionPersonaDataBean.getRelacionPersona()
										.getIdOrganizacion(),
								relacionPersonaDataBean.getRelacionPersona()
										.getTipoRelacion());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return booRetorno;
	}

	public void consultaPersona() {
		relacionPersonaDataBean.setExistePerson(false);
		if (relacionPersonaDataBean.getRelacionPersona().getPersona()
				.getIdentificacion() != null) {
			if ((relacionPersonaDataBean.getRelacionPersona().getPersona()
					.getIdentificacion().length() == 10 || relacionPersonaDataBean
					.getRelacionPersona().getPersona().getIdentificacion()
					.length() == 13)) {
				try {
					List<Persona> liPersonas = servicioPersonaLocal
							.obtenerPorParametros(relacionPersonaDataBean
									.getRelacionPersona().getPersona()
									.getIdentificacion(), null, null, null);

					if (liPersonas != null && !liPersonas.isEmpty()) {
						relacionPersonaDataBean.getRelacionPersona()
								.setPersona(liPersonas.get(0));
						relacionPersonaDataBean.setExistePerson(true);
					}
				} catch (GizloException ex) {
					log.error("Error: " + ex.getMessage(), ex);
					errorMessage("Ha ocurrido un error al listar Personas");
				}

			}
		}
	}

	/**
	 * regresa
	 * 
	 * @return
	 */
	public String regresar() {
		relacionPersonaDataBean.setRelacionPersona(new RelacionPersona());
		return "listaRelacionPersona";
	}

	public void obtieneOrganizacion() {
		try {
			Organizacion organizacion = new Organizacion();
			organizacion = servicioOrganizacion
					.consultarOrganizacionID(relacionPersonaDataBean
							.getRelacionPersona().getIdOrganizacion());
			relacionPersonaDataBean.getRelacionPersona().setOrganizacion(
					organizacion);
		} catch (GizloException e) {
			e.printStackTrace();
		}
	}

	/**
	 * nuevo relacion persona
	 * 
	 * @return
	 */
	public String nuevoRelacionPersona() {
		relacionPersonaDataBean.setRelacionPersona(new RelacionPersona());
		relacionPersonaDataBean.getRelacionPersona().setPersona(new Persona());
		relacionPersonaDataBean.getRelacionPersona().setOrganizacion(
				new Organizacion());
		relacionPersonaDataBean.setNuevo(true);
		relacionPersonaDataBean.setEditar(false);
		return "formularioRelacionPersona";
	}

	/**
	 * editar persona
	 * 
	 * @return
	 */
	public String editarPersonaRelacion() {
		relacionPersonaDataBean.setEditar(true);
		relacionPersonaDataBean.setNuevo(false);
		return "formularioRelacionPersona";
	}

	public RelacionPersonaDataBean getRelacionPersonaDataBean() {
		return relacionPersonaDataBean;
	}

	public void setRelacionPersonaDataBean(
			RelacionPersonaDataBean relacionPersonaDataBean) {
		this.relacionPersonaDataBean = relacionPersonaDataBean;
	}

}
