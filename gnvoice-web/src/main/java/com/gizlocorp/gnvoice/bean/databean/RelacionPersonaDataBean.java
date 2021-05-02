package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.RelacionPersona;


@SessionScoped
@Named("relacionPersonaDataBean")
public class RelacionPersonaDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RelacionPersona relacionPersona;
	private List<RelacionPersona> listRelacionPersona;
	private boolean existePerson;
	private boolean nuevo;
	private boolean editar;
	
	/** Parametros de Consulta */
	private String nombre;
	private String apellido;
	private String identificacion;
	private String tipoRelacion;
	private Long idOrganizacion;
	
	
	public RelacionPersona getRelacionPersona() {
		return relacionPersona;
	}
	public void setRelacionPersona(RelacionPersona relacionPersona) {
		this.relacionPersona = relacionPersona;
	}
	public List<RelacionPersona> getListRelacionPersona() {
		return listRelacionPersona;
	}
	public void setListRelacionPersona(List<RelacionPersona> listRelacionPersona) {
		this.listRelacionPersona = listRelacionPersona;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	public String getTipoRelacion() {
		return tipoRelacion;
	}
	public void setTipoRelacion(String tipoRelacion) {
		this.tipoRelacion = tipoRelacion;
	}
	public Long getIdOrganizacion() {
		return idOrganizacion;
	}
	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
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
	
	

}
