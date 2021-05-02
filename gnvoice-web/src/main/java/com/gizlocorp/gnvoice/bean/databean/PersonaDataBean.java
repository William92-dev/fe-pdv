package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.Persona;


@SessionScoped
@Named("personaDataBean")
public class PersonaDataBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Parametros de Consulta */
	private String nombre;
	private String apellido;
	private String identificacion;
	
	
	private Persona personaSeleccionada;
	private List<Persona> listPersonas;
	private boolean opcionNuevo = false;
	private boolean opcionEditar = false;
	
	
	
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
	public Persona getPersonaSeleccionada() {
		return personaSeleccionada;
	}
	public void setPersonaSeleccionada(Persona personaSeleccionada) {
		this.personaSeleccionada = personaSeleccionada;
	}
	public List<Persona> getListPersonas() {
		return listPersonas;
	}
	public void setListPersonas(List<Persona> listPersonas) {
		this.listPersonas = listPersonas;
	}
	public boolean isOpcionNuevo() {
		return opcionNuevo;
	}
	public void setOpcionNuevo(boolean opcionNuevo) {
		this.opcionNuevo = opcionNuevo;
	}
	public boolean isOpcionEditar() {
		return opcionEditar;
	}
	public void setOpcionEditar(boolean opcionEditar) {
		this.opcionEditar = opcionEditar;
	}
	
	
	

}
