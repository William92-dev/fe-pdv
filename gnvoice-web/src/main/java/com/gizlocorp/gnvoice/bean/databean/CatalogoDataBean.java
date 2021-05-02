package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.Catalogo;
import com.gizlocorp.adm.modelo.TipoCatalogo;


@SessionScoped
@Named("catalogoDataBean")
public class CatalogoDataBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Parametros de Consulta */
	private String codigo;
	private String nombre;
	private Long idTipoCatalogo;
	
	private Catalogo catalogoSeleccionada;
	private List<Catalogo> listCatalogos;
	
	private List<TipoCatalogo> liTipoCatalogos;
	
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
	public Catalogo getCatalogoSeleccionada() {
		return catalogoSeleccionada;
	}
	public void setCatalogoSeleccionada(Catalogo catalogoSeleccionada) {
		this.catalogoSeleccionada = catalogoSeleccionada;
	}
	public List<Catalogo> getListCatalogos() {
		return listCatalogos;
	}
	public void setListCatalogos(List<Catalogo> listCatalogos) {
		this.listCatalogos = listCatalogos;
	}
	public List<TipoCatalogo> getLiTipoCatalogos() {
		return liTipoCatalogos;
	}
	public void setLiTipoCatalogos(List<TipoCatalogo> liTipoCatalogos) {
		this.liTipoCatalogos = liTipoCatalogos;
	}
	public Long getIdTipoCatalogo() {
		return idTipoCatalogo;
	}
	public void setIdTipoCatalogo(Long idTipoCatalogo) {
		this.idTipoCatalogo = idTipoCatalogo;
	}
	
	
	
	
	
	

}
