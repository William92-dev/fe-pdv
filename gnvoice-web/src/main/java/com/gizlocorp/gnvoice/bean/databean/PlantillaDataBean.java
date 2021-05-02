package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.Plantilla;


@SessionScoped
@Named("plantillaDataBean")
public class PlantillaDataBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Parametros de Consulta */
	private String codigo;
	private String titulo;
	
	private Plantilla plantillaSeleccionada;
	private List<Plantilla> listPlantillas;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public Plantilla getPlantillaSeleccionada() {
		return plantillaSeleccionada;
	}
	public void setPlantillaSeleccionada(Plantilla plantillaSeleccionada) {
		this.plantillaSeleccionada = plantillaSeleccionada;
	}
	public List<Plantilla> getListPlantillas() {
		return listPlantillas;
	}
	public void setListPlantillas(List<Plantilla> listPlantillas) {
		this.listPlantillas = listPlantillas;
	}
	
	
	
	
	
	

}
