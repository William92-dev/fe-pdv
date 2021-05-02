package com.gizlocorp.gnvoice.bean;

import java.io.FileInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;
import com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal;
import com.gizlocorp.gnvoice.bean.databean.ParametroOrdenCompraDataBean;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("parametroOrdenesCompraBean")
public class ParametroOrdenesCompraBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(ParametroOrdenesCompraBean.class.getName());

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroOrdenCompraImpl!com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal")
	ServicioParametroOrdenCompraLocal servicioParametro;

	@Inject
	private ParametroOrdenCompraDataBean parametroDataBean;
	
	
	@Inject
	private SessionBean sessionBean;


	private String codigo;
	private Estado estado;
	private boolean datoValido = false;
	
	
     private String nombrelFilter;
	
	private String  rucFilter;

	
	
	private static final String REPORTE_CLAVES_PATH = "/reportes/etiquetasOrdenCompra.jasper";
	private static final String REPORT_XLS_PATH = "Etiquetas Orden Compra.xls";

	
	
	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	public String buscarParametro() {
		try {
			
			List<ParametroOrdenCompra> parametros = servicioParametro.listarParametros(codigo, estado);
			if (parametros == null) {
				parametros = new ArrayList<>();
			}
			parametroDataBean.setParametros(parametros);
		} catch (GizloException e) {
			log.error("Ha ocurrido un error al listar Parametros", e);
			errorMessage("Ha ocurrido un error al listar Parametros");
		}

		return null;
	}

	@PostConstruct
	public void postContruct() {
		try {
			List<ParametroOrdenCompra> parametros = servicioParametro.listarParametros(null, Estado.ACT);
			parametroDataBean.setParametros(parametros);

		} catch (GizloException e) {
			log.error("Ha ocurrido un error al listar Parametros", e);
		}
	}

	public String guardarParametro() {
		datoValido = true;
		try {
			if (parametroDataBean.getParametro().getValor() == null || parametroDataBean.getParametro().getValor().isEmpty()) {
				datoValido = false;
				infoMessage("Ingrese codigo del Parametro","Ingrese codigo del Parametro");
				log.debug("Ingrese codigo del Parametro");
			} else if (parametroDataBean.getParametro().getValor().length() < 2) {
				datoValido = false;
				infoMessage("Ingrese minimo 2 caracteres en el codigo",
						"Ingrese minimo 3 caracteres en el codigo");
				log.debug("Ingrese minimo 3 caracteres en el codigo");
			} else {
				// GUARDA
				if (parametroDataBean.getOpcion() == 0) {
					log.debug("guardando ");
					// bucar parametro x codigo
					ParametroOrdenCompra parametro = servicioParametro.consultarParametro(parametroDataBean.getParametro().getValor(), parametroDataBean.getParametro().getProveedor());
					if (parametro != null) {
						infoMessage("Codigo ya registrado!. Ingrese otro",
								"Codigo ya registrado!. Ingrese otro");
						log.debug("Codigo ya registrado!. Ingrese otro");
					} else {
						servicioParametro.ingresarParametro(parametroDataBean.getParametro());
						infoMessage("Ingresado correctamente!",
								"Ingresado correctamente!");
						log.debug("Ingresado correctamente!");
						// NUEVO
						parametroDataBean.setParametro(new ParametroOrdenCompra());
					}
				} else {
					log.debug("editando ");
					servicioParametro.actualizarParametro(parametroDataBean.getParametro());
					infoMessage("Actualizado correctamente!","Actualizado correctamente!");
				}
			}

		} catch (GizloException e) {
			// e.printStackTrace();
			log.error("Ha ocurrido un error al guardar Parametro", e);
			errorMessage("Ha ocurrido un error al guardar Parametro",
					"Ha ocurrido un error al guardar Parametro");
		}
		return null;
	}

	public String eliminarParametro() {
		try {
			log.debug("eliminando ");
			parametroDataBean.getParametro().setEstado(Estado.INA);
			servicioParametro.actualizarParametro(parametroDataBean
					.getParametro());
		} catch (GizloException e) {
			log.error("Ha ocurrido un error al eliminar Parametro", e);
			errorMessage("Ha ocurrido un error al eliminar Parametro",
					"Ha ocurrido un error al eliminar Parametro");
		}

		return null;
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarDetalleXLS() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (parametroDataBean != null 
					&!parametroDataBean.getParametros().isEmpty()) {

				map.put("IS_IGNORE_PAGINATION", true);
	

				ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH,
											  map,
											  REPORT_XLS_PATH, 
											  parametroDataBean.getParametros());
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

	/**
	 * Redirecciona a formularioParametro
	 * 
	 * @return
	 */
	public String verParametro() {
		try {
			log.debug("parametro: " + parametroDataBean.getParametro());
			if (parametroDataBean.getParametro() != null && parametroDataBean.getParametro().getId() != null) {
				return "formularioParametroOrdenCompra";
			}
		} catch (Exception ex) {
			log.error("Error al cargar parametro", ex);
			errorMessage("Error al cargar parametro","Error al cargar parametro!");
			return null;
		}
		return null;
	}

	public String agregarParametro() {
		parametroDataBean.setParametro(new ParametroOrdenCompra());
		return "formularioParametroOrdenCompra";
	}

	public String regresar() {
		String retorno = "";
		if (parametroDataBean.getOpcion() == 2) {
			parametroDataBean.setParametro(new ParametroOrdenCompra());
			retorno = "listaParametroAplicacion";
		} else
			retorno = "listaParametroOrdenCompra";
		return retorno;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public boolean isDatoValido() {
		return datoValido;
	}

	public void setDatoValido(boolean datoValido) {
		this.datoValido = datoValido;
	}

	public String getNombrelFilter() {
		return nombrelFilter;
	}

	public void setNombrelFilter(String nombrelFilter) {
		this.nombrelFilter = nombrelFilter;
	}

	public String getRucFilter() {
		return rucFilter;
	}

	public void setRucFilter(String rucFilter) {
		this.rucFilter = rucFilter;
	}
	
	
	 

}