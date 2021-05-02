package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Catalogo;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.TipoCatalogo;
import com.gizlocorp.adm.modelo.UbicacionGeografica;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioTipoCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioUbicacionGeograficaLocal;
import com.gizlocorp.adm.utilitario.Constantes;

/**
 * 
 * @author
 * @version $Revision: 1.0 $
 */
@Startup
@ApplicationScoped
@Named("applicationBean")
public class ApplicationBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ApplicationBean.class);

	@EJB(lookup = "java:global/adm-ejb/ServicioUbicacionGeograficaImpl!com.gizlocorp.adm.servicio.local.ServicioUbicacionGeograficaLocal")
	private ServicioUbicacionGeograficaLocal servicioUbicacion;

	@EJB(lookup = "java:global/adm-ejb/ServicioTipoCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioTipoCatalogoLocal")
	ServicioTipoCatalogoLocal servicioTipoCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;

	private List<UbicacionGeografica> ciudades;
	private boolean error;

	private List<TipoCatalogo> liTipoCatalogos;
	private List<Catalogo> catalogosTipoIdentificacion;
	private List<Catalogo> catalogosGenero;
	private List<Catalogo> catalogosEstadoCivil;
	private List<Organizacion> organizaciones;
	private List<Catalogo> catalogosEventos;

	@PostConstruct
	public void postContruct() {
		try {
			ciudades = servicioUbicacion.listarCiudades();
			log.debug("CIUDADES: " + ciudades);
		} catch (Exception e) {
			error = true;
			errorMessage("Ha ocurrido un error al listar Ciudades");
		}

		try {
			liTipoCatalogos = servicioTipoCatalogoLocal.listTipoCatalogoAll();
		} catch (GizloException e) {
			error = true;
			errorMessage("Ha ocurrido un error al listar Tipo Catalogo");
		}

		try {
			catalogosTipoIdentificacion = servicioCatalogoLocal
					.listCatalogoPorTipoCatalogo(null,
							Constantes.CODIGO_TIPO_IDENTIFICACION);
		} catch (GizloException e) {
			error = true;
			errorMessage("Ha ocurrido un error al listar Tipo Identificacion");
		}

		try {
			catalogosGenero = servicioCatalogoLocal
					.listCatalogoPorTipoCatalogo(null, Constantes.CODIGO_GENERO);
		} catch (GizloException e) {
			error = true;
			errorMessage("Ha ocurrido un error al listar Tipo Identificacion");
		}

		try {
			catalogosEstadoCivil = servicioCatalogoLocal
					.listCatalogoPorTipoCatalogo(null,
							Constantes.CODIGO_ESTADO_CIVIL);
		} catch (GizloException e) {
			error = true;
			errorMessage("Ha ocurrido un error al listar Tipo Identificacion");
		}

		try {
			organizaciones = servicioOrganizacion.listarOrganizaciones(null,
					null, null, null);
		} catch (Exception e) {
			error = true;
			errorMessage("Ha ocurrido un error al listar Organizaciones");
		}

		try {
			catalogosEventos = servicioCatalogoLocal
					.listCatalogoPorTipoCatalogo(null, Constantes.CODIGO_EVENTO);
		} catch (GizloException e) {
			error = true;
			errorMessage("Ha ocurrido un error al listar Tipo Identificacion");
		}

	}

	public void recargar() {
		postContruct();
		if (!error) {
			// infoMessage(getMensaje("msg", "recargaCatalogos.msg"));
		}
	}

	public List<UbicacionGeografica> getCiudades() {
		return ciudades;
	}

	public void setCiudades(List<UbicacionGeografica> ciudades) {
		this.ciudades = ciudades;
	}

	public List<TipoCatalogo> getLiTipoCatalogos() {
		return liTipoCatalogos;
	}

	public void setLiTipoCatalogos(List<TipoCatalogo> liTipoCatalogos) {
		this.liTipoCatalogos = liTipoCatalogos;
	}

	public List<Catalogo> getCatalogosTipoIdentificacion() {
		return catalogosTipoIdentificacion;
	}

	public void setCatalogosTipoIdentificacion(
			List<Catalogo> catalogosTipoIdentificacion) {
		this.catalogosTipoIdentificacion = catalogosTipoIdentificacion;
	}

	public List<Catalogo> getCatalogosGenero() {
		return catalogosGenero;
	}

	public void setCatalogosGenero(List<Catalogo> catalogosGenero) {
		this.catalogosGenero = catalogosGenero;
	}

	public List<Catalogo> getCatalogosEstadoCivil() {
		return catalogosEstadoCivil;
	}

	public void setCatalogosEstadoCivil(List<Catalogo> catalogosEstadoCivil) {
		this.catalogosEstadoCivil = catalogosEstadoCivil;
	}

	public List<Organizacion> getOrganizaciones() {
		return organizaciones;
	}

	public void setOrganizaciones(List<Organizacion> organizaciones) {
		this.organizaciones = organizaciones;
	}

	public List<Catalogo> getCatalogosEventos() {
		return catalogosEventos;
	}

	public void setCatalogosEventos(List<Catalogo> catalogosEventos) {
		this.catalogosEventos = catalogosEventos;
	}

}
