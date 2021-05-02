package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.TipoRelacion;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.RelacionOrganizacion;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;
import com.gizlocorp.adm.servicio.local.ServicioRelacionOrganizacionLocal;
import com.gizlocorp.gnvoice.bean.databean.RelacionOrganizacionDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

/**
 * 
 * @author Jose Vinueza
 * 
 */

// @Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("relacionOrganizacionBean")
public class RelacionOrganizacionBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(RelacionOrganizacionBean.class
			.getName());

	@Inject
	private RelacionOrganizacionDataBean relacionOrganizacionDataBean;

	// @Inject private ApplicationBean appBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioRelacionOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioRelacionOrganizacionLocal")
	ServicioRelacionOrganizacionLocal servicioRelacionOrganizacionLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioPersonaLocal")
	ServicioPersonaLocal servicioPersonaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;


	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	SessionBean sessionBean;

//	@PostConstruct
//	public void postContruct() {
//		try {
//			
//		} catch (Exception e) {
//			log.debug("Error al Listar Relacion Persona" + e.getMessage());
//		}
//	}

	public void buscar() {
		try {
			relacionOrganizacionDataBean
					.setListRelacionOrganizaciones(servicioRelacionOrganizacionLocal
							.buscarPorParametros(relacionOrganizacionDataBean
									.getRuc(), relacionOrganizacionDataBean
									.getNombre(), relacionOrganizacionDataBean
									.getTipoRelacion()));
			List<RelacionOrganizacion> lista = relacionOrganizacionDataBean
					.getListRelacionOrganizaciones();
			log.debug(lista);
		} catch (GizloException e) {
			e.printStackTrace();
		}

	}

	public void guardar() {
		try {
			if (verificaRelacionOrganiacion()) {
				infoMessage("Registro ya Existe", "Registro ya Existe");
				return;
			}
			if (relacionOrganizacionDataBean.getRelacionOrganizacion()
					.getOrganizacion().getId() == null) {
				servicioOrganizacion
						.ingresarOrganizacion(relacionOrganizacionDataBean
								.getRelacionOrganizacion().getOrganizacion());
			}
			if (relacionOrganizacionDataBean.getRelacionOrganizacion()
					.getPersona().getId() == null) {
				servicioPersonaLocal
						.guardarPersona(relacionOrganizacionDataBean
								.getRelacionOrganizacion().getPersona());
			}

			if (relacionOrganizacionDataBean.getRelacionOrganizacion().getId() != null) {
				servicioRelacionOrganizacionLocal
						.guardarRelacionOrganizacion(relacionOrganizacionDataBean
								.getRelacionOrganizacion());
				infoMessage("Actualizado Correctamente",
						"Actualizado Correctamente");
			} else {
				servicioRelacionOrganizacionLocal
						.guardarRelacionOrganizacion(relacionOrganizacionDataBean
								.getRelacionOrganizacion());
				infoMessage("Ingresado Correctamente",
						"Ingresado Correctamente");
			}

		} catch (Exception e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al guardar");
		}
	}

	public boolean verificaRelacionOrganiacion() {
		boolean booRetorno = false;
		try {
			if (relacionOrganizacionDataBean.isNuevo()) {
				booRetorno = servicioRelacionOrganizacionLocal
						.existeRelacionOrganizacion(
								relacionOrganizacionDataBean
										.getRelacionOrganizacion()
										.getOrganizacion(),
								relacionOrganizacionDataBean
										.getRelacionOrganizacion()
										.getOrganizacionRelacion(),
								relacionOrganizacionDataBean
										.getRelacionOrganizacion()
										.getTipoRelacion());
			}
		} catch (GizloException e) {
			e.printStackTrace();
		}

		return booRetorno;
	}

	public void obtieneOrganizacion() {
		try {
			Organizacion organizacion = new Organizacion();
			organizacion = servicioOrganizacion
					.consultarOrganizacionID(relacionOrganizacionDataBean
							.getRelacionOrganizacion()
							.getIdOrganizacionRelacion());
			relacionOrganizacionDataBean.getRelacionOrganizacion()
					.setOrganizacionRelacion(organizacion);
		} catch (GizloException e) {
			e.printStackTrace();
		}
	}

	public void consultaOrganizacion() {
		try {
			Organizacion organizacion = new Organizacion();
			organizacion = servicioOrganizacion.listarOrganizaciones(
					null,
					null,
					relacionOrganizacionDataBean.getRelacionOrganizacion()
							.getOrganizacion().getRuc(), null).get(0);
			relacionOrganizacionDataBean.getRelacionOrganizacion()
					.setOrganizacion(organizacion);
		} catch (GizloException e) {
			e.printStackTrace();
		}

	}

	public void consultaPersona() {
		relacionOrganizacionDataBean.setExistePerson(false);
		if (relacionOrganizacionDataBean.getRelacionOrganizacion().getPersona()
				.getIdentificacion() != null) {
			if ((relacionOrganizacionDataBean.getRelacionOrganizacion()
					.getPersona().getIdentificacion().length() == 10 || relacionOrganizacionDataBean
					.getRelacionOrganizacion().getPersona().getIdentificacion()
					.length() == 13)) {
				try {
					List<Persona> liPersonas = servicioPersonaLocal
							.obtenerPorParametros(relacionOrganizacionDataBean
									.getRelacionOrganizacion().getPersona()
									.getIdentificacion(), null, null, null);

					if (liPersonas != null && !liPersonas.isEmpty()) {
						relacionOrganizacionDataBean.getRelacionOrganizacion()
								.setPersona(liPersonas.get(0));
						relacionOrganizacionDataBean.setExistePerson(true);
					}
				} catch (GizloException ex) {
					log.error("Error: " + ex.getMessage(), ex);
					errorMessage("Ha ocurrido un error al listar Personas");
				}

			}
		}
	}

	/**
	 * nuevo relacion organizacion
	 * 
	 * @return
	 */
	public String nuevoRelacionOrganizacion() {
		relacionOrganizacionDataBean
				.setRelacionOrganizacion(new RelacionOrganizacion());
		relacionOrganizacionDataBean.getRelacionOrganizacion().setOrganizacion(
				new Organizacion());
		relacionOrganizacionDataBean.getRelacionOrganizacion().setPersona(
				new Persona());
		relacionOrganizacionDataBean.setNuevo(true);
		relacionOrganizacionDataBean.setEditar(false);
		return "formularioRelacionOrganizacion";
	}

	/**
	 * regresa
	 * 
	 * @return
	 */
	public String regresar() {
		relacionOrganizacionDataBean
				.setRelacionOrganizacion(new RelacionOrganizacion());
		relacionOrganizacionDataBean.getRelacionOrganizacion().setOrganizacion(
				new Organizacion());
		relacionOrganizacionDataBean.getRelacionOrganizacion().setPersona(
				new Persona());
		return "listaRelacionOrganizacion";
	}

	public List<SelectItem> getTipoRelacion() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoRelacion estado : TipoRelacion.values()) {
			if (!estado.name().equals("TRA"))
				items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	/**
	 * editar persona
	 * 
	 * @return
	 */
	public String editarRelacionOrganizacion() {
		relacionOrganizacionDataBean.setNuevo(false);
		relacionOrganizacionDataBean.setEditar(true);
		return "formularioRelacionOrganizacion";
	}

	public RelacionOrganizacionDataBean getRelacionOrganizacionDataBean() {
		return relacionOrganizacionDataBean;
	}

	public void setRelacionOrganizacionDataBean(
			RelacionOrganizacionDataBean relacionOrganizacionDataBean) {
		this.relacionOrganizacionDataBean = relacionOrganizacionDataBean;
	}

}
