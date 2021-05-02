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
import com.gizlocorp.adm.modelo.TipoCatalogo;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioTipoCatalogoLocal;
import com.gizlocorp.gnvoice.bean.databean.TipoCatalogoDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("tipoCatalogoBean")
public class TipoCatalogoBean extends BaseBean implements Serializable {


	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	SessionBean sessionBean;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(TipoCatalogoBean.class
			.getName());

	@Inject
	private TipoCatalogoDataBean tipoCatalogoDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioTipoCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioTipoCatalogoLocal")
	ServicioTipoCatalogoLocal servicioTipoCatalogoLocal;

	@PostConstruct
	public void postContruct() {
		try {
			List<TipoCatalogo> liTipoCatalogos = servicioTipoCatalogoLocal
					.listObtenerPorParametros(null, null);
			tipoCatalogoDataBean.setListTipoCatalogos(liTipoCatalogos);

			

		} catch (GizloException e) {
			log.error("Ha ocurrido un error al listar Parametros", e);
		}
	}

	public String nuevoTipoCatalogo() {
		tipoCatalogoDataBean.setTipoCatalogoSeleccionada(new TipoCatalogo());
		return "formularioTipoCatalogo";
	}

	public String editarTipoCatalogo() {
		return "formularioTipoCatalogo";
	}

	public String regresar() {
		return "listaTipoCatalogo";
	}

	public void eliminarTipoCatalogo() {

		try {
			servicioTipoCatalogoLocal.eliminarTipoCatalogo(tipoCatalogoDataBean
					.getTipoCatalogoSeleccionada());
			infoMessage("Eliminado correctamente!", "Eliminado correctamente!");
		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al eliminar Tipo Catalogo revise las dependencias");
			log.error("Error  " + e.getMessage(), e);
		}
	}

	public void guardar() {
		if (valida()) {
			try {
				if (tipoCatalogoDataBean.getTipoCatalogoSeleccionada().getId() != null) {
					servicioTipoCatalogoLocal
							.guardarTipoCatalogo(tipoCatalogoDataBean
									.getTipoCatalogoSeleccionada());
					infoMessage("Modificado Correctamente!",
							"Modificado Correctamente!");
				} else {
					tipoCatalogoDataBean.getTipoCatalogoSeleccionada()
							.setEstado(Estado.ACT.name());
					servicioTipoCatalogoLocal
							.guardarTipoCatalogo(tipoCatalogoDataBean
									.getTipoCatalogoSeleccionada());
					infoMessage("Ingresado Correctamente!",
							"Ingresado Correctamente!");
					tipoCatalogoDataBean
							.setTipoCatalogoSeleccionada(new TipoCatalogo());
				}
			} catch (GizloException e) {
				log.error("Error" + e.getMessage(), e);
				errorMessage("Ha ocurrido un error al Guardar Tipo Catalogo");
			}
		}
	}

	public void consultarTipoCatalogo() {
		try {
			List<TipoCatalogo> liTipoCatalogos = servicioTipoCatalogoLocal
					.listObtenerPorParametros(tipoCatalogoDataBean.getCodigo(),
							tipoCatalogoDataBean.getNombre());
			tipoCatalogoDataBean.setListTipoCatalogos(liTipoCatalogos);
		} catch (GizloException ex) {
			log.error("Error" + ex.getMessage(), ex);
			errorMessage("Ha ocurrido un error al listar Parametros");
		}
		// plantillaDataBean.setListPlantillas(liPlantillas);
	}

	private boolean valida() {
		boolean retorno = true;

		// if(!(plantillaDataBean.getPlantillaSeleccionada().getCodigo().length()>1
		// &&
		// plantillaDataBean.getPlantillaSeleccionada().getCodigo().length()<5)){
		// infoMessage("Codigo Debe Tener Maximo 5 Caracteres","Codigo Debe Tener Maximo 5 Caracteres");
		// return false;
		// }
		// if(!(plantillaDataBean.getPlantillaSeleccionada().getDescripcion().length()>1)){
		// infoMessage("La Descripcion no puede estar vacia. ","La Descripcion no puede estar vacia.");
		// return false;
		// }
		// if(!(plantillaDataBean.getPlantillaSeleccionada().getTitulo().length()>1)){
		// infoMessage("El Titulo no puede estar vacio. ","El Titulo no puede estar vacio.");
		// return false;
		// }

		return retorno;
	}

	public TipoCatalogoDataBean getTipoCatalogoDataBean() {
		return tipoCatalogoDataBean;
	}

	public void setTipoCatalogoDataBean(
			TipoCatalogoDataBean tipoCatalogoDataBean) {
		this.tipoCatalogoDataBean = tipoCatalogoDataBean;
	}

}
