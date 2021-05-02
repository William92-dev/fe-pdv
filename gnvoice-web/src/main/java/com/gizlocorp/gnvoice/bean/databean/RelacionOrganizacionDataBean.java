package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.RelacionOrganizacion;


@SessionScoped
@Named("relacionOrganizacionDataBean")
public class RelacionOrganizacionDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RelacionOrganizacion relacionOrganizacion;
	private List<RelacionOrganizacion> listRelacionOrganizaciones;
	private boolean existePerson;
	private boolean nuevo;
	private boolean editar;
	
	/** Parametros de Consulta */
	private String nombre;
	private String acronimo;
	private String ruc;
	private String tipoRelacion;
	
	
	public RelacionOrganizacion getRelacionOrganizacion() {
		return relacionOrganizacion;
	}
	public void setRelacionOrganizacion(RelacionOrganizacion relacionOrganizacion) {
		this.relacionOrganizacion = relacionOrganizacion;
	}
	public List<RelacionOrganizacion> getListRelacionOrganizaciones() {
		return listRelacionOrganizaciones;
	}
	public void setListRelacionOrganizaciones(
			List<RelacionOrganizacion> listRelacionOrganizaciones) {
		this.listRelacionOrganizaciones = listRelacionOrganizaciones;
	}
	public boolean isExistePerson() {
		return existePerson;
	}
	public void setExistePerson(boolean existePerson) {
		this.existePerson = existePerson;
	}
	public boolean isNuevo() {
		return nuevo;
	}
	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	public boolean isEditar() {
		return editar;
	}
	public void setEditar(boolean editar) {
		this.editar = editar;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getAcronimo() {
		return acronimo;
	}
	public void setAcronimo(String acronimo) {
		this.acronimo = acronimo;
	}
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getTipoRelacion() {
		return tipoRelacion;
	}
	public void setTipoRelacion(String tipoRelacion) {
		this.tipoRelacion = tipoRelacion;
	}

	
	
	
	
	
	
	

}
