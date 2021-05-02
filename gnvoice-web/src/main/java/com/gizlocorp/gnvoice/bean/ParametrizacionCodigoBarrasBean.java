package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.servicio.local.ServicioCodigoBarrasLocal;
import com.gizlocorp.gnvoice.bean.databean.CodigoBarrasDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;


@ViewScoped
@Named("parametrizacionCodigoBarrasBean")
public class ParametrizacionCodigoBarrasBean  extends BaseBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1734915049743713674L;
	
	private static Logger log = Logger.getLogger(ParametrizacionCodigoBarrasBean.class.getName());
	
	@Inject
	private CodigoBarrasDataBean codigoBarrasDataBean;
	
	@Inject
	SessionBean sessionBean;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioCodigoBarrasImpl!com.gizlocorp.adm.servicio.local.ServicioCodigoBarrasLocal")
	ServicioCodigoBarrasLocal servicioCodigoBarrasLocal;
	
	private String codigo;
	private Estado estado;
	private String userName;
	private ParametrizacionCodigoBarras codigoBarrasSelecionado;
	
	
	public String nuevo() {
		codigoBarrasDataBean.setCodigoBarrasSelecionado(new ParametrizacionCodigoBarras());
		return "formularioCodigoBarras";
	}
	
	public String editar() {
		return "formularioCodigoBarras";
	}
	
	public String regresar() {
		codigoBarrasDataBean.setCodigoBarrasSelecionado(new ParametrizacionCodigoBarras());
		return "listaCodigoBarras";
	}
	
	public String buscar() {
		try {
			
			List<ParametrizacionCodigoBarras> parametros = servicioCodigoBarrasLocal.obtenerCodigoBarrasPorParametros(codigo, null, estado, null);

			if (parametros == null) {
				parametros = new ArrayList<ParametrizacionCodigoBarras>();
			}
			codigoBarrasDataBean.setListCodigoBarras(parametros);
		} catch (GizloException e) {
			log.error("Ha ocurrido un error al listar Parametros", e);
			errorMessage("Ha ocurrido un error al listar Parametros");
		}

		return null;
	}
	
	public void guardar() {
		if (valida()) {
			codigoBarrasDataBean.getCodigoBarrasSelecionado().setUsuarioRegistro(userName);
			codigoBarrasDataBean.getCodigoBarrasSelecionado().setFechaRegistro(new Date());
			if (codigoBarrasDataBean.getCodigoBarrasSelecionado().getId() != null) {
				servicioCodigoBarrasLocal.guardar(codigoBarrasDataBean.getCodigoBarrasSelecionado());
				infoMessage("Modificado Correctamente!", "Modificado Correctamente!");
			} else {
				servicioCodigoBarrasLocal.guardar(codigoBarrasDataBean.getCodigoBarrasSelecionado());
				infoMessage("Ingresado correctamente!", "Ingresado correctamente!");
			}
		}
	}
	
	private boolean valida() {
		boolean retorno = true;

		if (!(codigoBarrasDataBean.getCodigoBarrasSelecionado().getCodigoArticulo() != null 
				&& !codigoBarrasDataBean.getCodigoBarrasSelecionado().getCodigoArticulo().isEmpty())) {
			infoMessage("Debe Ingresar Codigo de Articulo",
					"Debe Ingresar Codigo de Articulo");
			return false;
		}
		if (!(codigoBarrasDataBean.getCodigoBarrasSelecionado().getTipoArticulo() != null 
				&& !codigoBarrasDataBean.getCodigoBarrasSelecionado().getTipoArticulo().isEmpty())) {
			infoMessage("Debe ingresar Tipo de Articulo",
					"Debe ingresar Tipo de Articulo");
			return false;
		}
		if (!(codigoBarrasDataBean.getCodigoBarrasSelecionado().getEstado() != null)) {
			infoMessage("Debe ingresar Estado",
					"Debe ingresar Estado");
			return false;
		}
	
		return retorno;
	}
	
	@PostConstruct
	public void postContruct() {
		try {
			userName= sessionBean.getUsuario().getUsername();
			List<ParametrizacionCodigoBarras> parametros = servicioCodigoBarrasLocal.obtenerCodigoBarrasPorParametros(codigo, null, estado, null);
			codigoBarrasDataBean.setListCodigoBarras(parametros);

		} catch (GizloException e) {
			log.error("Ha ocurrido un error al listar Parametros", e);
		}
	}
	
	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
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

	public CodigoBarrasDataBean getCodigoBarrasDataBean() {
		return codigoBarrasDataBean;
	}

	public void setCodigoBarrasDataBean(CodigoBarrasDataBean codigoBarrasDataBean) {
		this.codigoBarrasDataBean = codigoBarrasDataBean;
	}

	public ParametrizacionCodigoBarras getCodigoBarrasSelecionado() {
		return codigoBarrasSelecionado;
	}

	public void setCodigoBarrasSelecionado(
			ParametrizacionCodigoBarras codigoBarrasSelecionado) {
		this.codigoBarrasSelecionado = codigoBarrasSelecionado;
	}


}
