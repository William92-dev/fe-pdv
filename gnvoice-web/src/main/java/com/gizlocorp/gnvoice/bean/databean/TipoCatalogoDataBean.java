package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.TipoCatalogo;


@SessionScoped
@Named("tipoCatalogoDataBean")
public class TipoCatalogoDataBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Parametros de Consulta */
	private String codigo;
	private String nombre;
	
	private TipoCatalogo tipoCatalogoSeleccionada;
	private List<TipoCatalogo> listTipoCatalogos;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public TipoCatalogo getTipoCatalogoSeleccionada() {
		return tipoCatalogoSeleccionada;
	}
	public void setTipoCatalogoSeleccionada(TipoCatalogo tipoCatalogoSeleccionada) {
		this.tipoCatalogoSeleccionada = tipoCatalogoSeleccionada;
	}
	public List<TipoCatalogo> getListTipoCatalogos() {
		return listTipoCatalogos;
	}
	public void setListTipoCatalogos(List<TipoCatalogo> listTipoCatalogos) {
		this.listTipoCatalogos = listTipoCatalogos;
	}
	
	
	
	
	
	

}
