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
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.gnvoice.bean.databean.ParametroDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("parametroBean")
public class ParametroBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(ParametroBean.class.getName());

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@Inject
	private ParametroDataBean parametroDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	private SessionBean sessionBean;

	private String codigo;
	private Estado estado;
	private boolean datoValido = false;

	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	public String buscarParametro() {
		try {
			Long idOrganizacion = sessionBean.getIdOrganizacion();
			log.debug("Listando parametros " + codigo + " " + estado + " "
					+ sessionBean.getIdOrganizacion());
			List<Parametro> parametros = servicioParametro.listarParametros(
					codigo, estado, idOrganizacion);

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
			Long idOrganizacion = sessionBean.getIdOrganizacion();
			List<Parametro> parametros = servicioParametro.listarParametros(
					null, Estado.ACT, idOrganizacion);
			parametroDataBean.setParametros(parametros);

		} catch (GizloException e) {
			log.error("Ha ocurrido un error al listar Parametros", e);
		}
	}

	public String guardarParametro() {
		log.debug("parametro es: "
				+ parametroDataBean.getParametro().getCodigo());
		datoValido = true;
		try {
			if (parametroDataBean.getParametro().getCodigo() == null
					|| parametroDataBean.getParametro().getCodigo().isEmpty()) {
				datoValido = false;
				infoMessage("Ingrese codigo del Parametro",
						"Ingrese codigo del Parametro");
				log.debug("Ingrese codigo del Parametro");
			} else if (parametroDataBean.getParametro().getCodigo().length() < 2) {
				datoValido = false;
				infoMessage("Ingrese minimo 2 caracteres en el codigo",
						"Ingrese minimo 3 caracteres en el codigo");
				log.debug("Ingrese minimo 3 caracteres en el codigo");
			} else {
				// GUARDA
				if (parametroDataBean.getOpcion() == 0) {
					log.debug("guardando ");
					// bucar parametro x codigo
					Parametro parametro = servicioParametro.consultarParametro(
							parametroDataBean.getParametro().getCodigo(), null);
					if (parametro != null) {
						infoMessage("Codigo ya registrado!. Ingrese otro",
								"Codigo ya registrado!. Ingrese otro");
						log.debug("Codigo ya registrado!. Ingrese otro");
					} else {
						servicioParametro.ingresarParametro(parametroDataBean
								.getParametro());
						infoMessage("Ingresado correctamente!",
								"Ingresado correctamente!");
						log.debug("Ingresado correctamente!");
						// NUEVO
						parametroDataBean.setParametro(new Parametro());
					}
				} else {
					log.debug("editando ");
					String valorDesemcriptado = parametroDataBean
							.getParametro().getValor();
					servicioParametro.actualizarParametro(parametroDataBean
							.getParametro());
					if (Logico.S.equals(parametroDataBean.getParametro()
							.getEncriptado())) {
						parametroDataBean.getParametro().setValor(
								valorDesemcriptado);
					}

					infoMessage("Actualizado correctamente!",
							"Actualizado correctamente!");
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

	/**
	 * Redirecciona a formularioParametro
	 * 
	 * @return
	 */
	public String verParametro() {
		try {
			log.debug("parametro: " + parametroDataBean.getParametro());
			if (parametroDataBean.getParametro() != null
					&& parametroDataBean.getParametro().getId() != null) {
				if (parametroDataBean.getParametro().getValorDesencriptado() != null) {
					parametroDataBean.getParametro().setValor(
							parametroDataBean.getParametro()
									.getValorDesencriptado());
				}
				return "formularioParametro";
			}
		} catch (Exception ex) {
			log.error("Error al cargar parametro", ex);
			errorMessage("Error al cargar parametro",
					"Error al cargar parametro!");
			return null;
		}
		return null;
	}

	public String agregarParametro() {
		parametroDataBean.setParametro(new Parametro());
		return "formularioParametro";
	}

	public String regresar() {
		String retorno = "";
		if (parametroDataBean.getOpcion() == 2) {
			parametroDataBean.setParametro(new Parametro());
			retorno = "listaParametroAplicacion";
		} else
			retorno = "listaParametro";
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

}