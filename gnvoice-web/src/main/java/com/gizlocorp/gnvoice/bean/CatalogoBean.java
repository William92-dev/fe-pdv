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

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Catalogo;
import com.gizlocorp.adm.modelo.TipoCatalogo;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.gnvoice.bean.databean.CatalogoDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("catalogoBean")
public class CatalogoBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(EmisorBean.class.getName());

	@Inject
	CatalogoDataBean catalogoDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	

//	@Inject
//	private SessionBean sessionBean;

	@PostConstruct
	public void postContruct() {
		try {
			List<Catalogo> listCatalogos = servicioCatalogoLocal
					.listObtenerPorParametros(null, null);
			catalogoDataBean.setListCatalogos(listCatalogos);

			
		} catch (GizloException e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al listar Parametros");
		}
	}

	public String nuevoCatalogo() {
		catalogoDataBean.setCatalogoSeleccionada(new Catalogo());

		return "formularioCatalogo";
	}

	public String editarCatalogo() {
		return "formularioCatalogo";
	}

	public String regresar() {
		return "listaCatalogo";
	}

	public void eliminarCatalogo() {
		try {
			servicioCatalogoLocal.eliminarCatalogo(catalogoDataBean
					.getCatalogoSeleccionada());
			infoMessage("Eliminado correctamente!", "Eliminado correctamente!");
		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al eliminar Tipo Catalogo revise las dependencias");
			log.error("Error  " + e.getMessage(), e);
		}
	}

	public void guardar() {
		if (valida()) {
			try {
				if (catalogoDataBean.getCatalogoSeleccionada().getId() != null) {
					servicioCatalogoLocal.guardarCatalogo(catalogoDataBean
							.getCatalogoSeleccionada());
					infoMessage("Modificado Correctamente!",
							"Modificado Correctamente!");
				} else {
					catalogoDataBean.getCatalogoSeleccionada().setEstado(
							Estado.ACT);
					servicioCatalogoLocal.guardarCatalogo(catalogoDataBean
							.getCatalogoSeleccionada());
					infoMessage("Ingresado Correctamente!",
							"Ingresado Correctamente!");
					catalogoDataBean.setCatalogoSeleccionada(new Catalogo());
					catalogoDataBean.getCatalogoSeleccionada().setTipoCatalogo(
							new TipoCatalogo());
				}
			} catch (GizloException e) {
				log.error("Error" + e.getMessage(), e);
				errorMessage("Ha ocurrido un error al Guardar Tipo Catalogo");
			}
		}
	}

	public void consultarCatalogos() {
		try {
			List<Catalogo> listCatalogos = servicioCatalogoLocal
					.listObtenerPorParametros(catalogoDataBean.getCodigo(),
							catalogoDataBean.getNombre());
			catalogoDataBean.setListCatalogos(listCatalogos);
		} catch (GizloException e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al listar Parametros");
		}
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

	public List<SelectItem> getTipoCatalogo1() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoCatalogo tipoCatalogo : catalogoDataBean.getLiTipoCatalogos()) {
			items.add(new SelectItem(tipoCatalogo, tipoCatalogo.getNombre()));
		}

		return items;
	}

	public CatalogoDataBean getCatalogoDataBean() {
		return catalogoDataBean;
	}

	public void setCatalogoDataBean(CatalogoDataBean catalogoDataBean) {
		this.catalogoDataBean = catalogoDataBean;
	}

}
