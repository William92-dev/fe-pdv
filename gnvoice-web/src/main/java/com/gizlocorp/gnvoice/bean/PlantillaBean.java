package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal;
import com.gizlocorp.gnvoice.bean.databean.PlantillaDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("plantillaBean")
public class PlantillaBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(PlantillaBean.class.getName());

	@Inject
	private PlantillaDataBean plantillaDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioPlantillaImpl!com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal")
	ServicioPlantillaLocal servicioPlantillaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	private SessionBean sessionBean;

	@PostConstruct
	public void postContruct() {
		try {
			Long idOrganizacion = sessionBean.getIdOrganizacion();
			List<Plantilla> liPlantillas = servicioPlantillaLocal
					.listaPlantilla(null, null, idOrganizacion);
			plantillaDataBean.setListPlantillas(liPlantillas);

		} catch (GizloException e) {
			log.error("Ha ocurrido un error al listar Parametros", e);
		}
	}

	public String nuevaPlantilla() {
		plantillaDataBean.setPlantillaSeleccionada(new Plantilla());
		return "formularioPlantilla";
	}

	public String editarPlantilla() {

		return "formularioPlantilla";
	}

	public void guardar() {
		if (valida()) {
			plantillaDataBean.getPlantillaSeleccionada().setEstado(Estado.ACT);
			try {
				servicioPlantillaLocal.guardarPlantilla(plantillaDataBean
						.getPlantillaSeleccionada());
				infoMessage("Ingresado Correctamente!",
						"Ingresado Correctamente!");
			} catch (GizloException e) {
				log.error("Ha ocurrido un error al Guardar Plantilla", e);
				errorMessage("Ha ocurrido un error al Guardar Plantilla");
			}
		}
	}

	/**
	 * regresa
	 * 
	 * @return
	 */
	public String regresar() {
		plantillaDataBean.setPlantillaSeleccionada(new Plantilla());
		return "listaPlantilla";
	}

	public void consultarPlantilla() {
		List<Plantilla> liPlantillas = null;
		try {
			Long idOrganizacion = sessionBean.getIdOrganizacion();
			liPlantillas = servicioPlantillaLocal.listaPlantilla(
					plantillaDataBean.getCodigo(),
					plantillaDataBean.getTitulo(), idOrganizacion);
		} catch (GizloException e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al Listar Plantilla");
		}
		plantillaDataBean.setListPlantillas(liPlantillas);
	}

	private boolean valida() {
		boolean retorno = true;
		if (!(plantillaDataBean.getPlantillaSeleccionada().getCodigo().length() > 1 && plantillaDataBean
				.getPlantillaSeleccionada().getCodigo().length() < 20)) {
			infoMessage("Codigo Debe Tener Maximo 20 Caracteres",
					"Codigo Debe Tener Maximo 20 Caracteres");
			return false;
		}
		if (!(plantillaDataBean.getPlantillaSeleccionada().getDescripcion()
				.length() > 1)) {
			infoMessage("La Descripcion no puede estar vacia. ",
					"La Descripcion no puede estar vacia.");
			return false;
		}
		if (!(plantillaDataBean.getPlantillaSeleccionada().getTitulo().length() > 1)) {
			infoMessage("El Titulo no puede estar vacio. ",
					"El Titulo no puede estar vacio.");
			return false;
		}

		return retorno;
	}

	public PlantillaDataBean getPlantillaDataBean() {
		return plantillaDataBean;
	}

	public void setPlantillaDataBean(PlantillaDataBean plantillaDataBean) {
		this.plantillaDataBean = plantillaDataBean;
	}

}
