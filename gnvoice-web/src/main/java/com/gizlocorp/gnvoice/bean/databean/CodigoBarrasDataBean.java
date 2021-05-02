package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;


@SessionScoped
@Named("codigoBarrasDataBean")
public class CodigoBarrasDataBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ParametrizacionCodigoBarras codigoBarrasSelecionado;
	private List<ParametrizacionCodigoBarras> listCodigoBarras;
	
	
	public ParametrizacionCodigoBarras getCodigoBarrasSelecionado() {
		return codigoBarrasSelecionado;
	}
	public void setCodigoBarrasSelecionado(
			ParametrizacionCodigoBarras codigoBarrasSelecionado) {
		this.codigoBarrasSelecionado = codigoBarrasSelecionado;
	}
	public List<ParametrizacionCodigoBarras> getListCodigoBarras() {
		return listCodigoBarras;
	}
	public void setListCodigoBarras(
			List<ParametrizacionCodigoBarras> listCodigoBarras) {
		this.listCodigoBarras = listCodigoBarras;
	}
	
	
	
	

}
