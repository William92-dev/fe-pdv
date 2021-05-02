package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.ParametroOrdenCompra;

@SessionScoped
@Named("parametroOrdenCompraDataBean")
public class ParametroOrdenCompraDataBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;
	private ParametroOrdenCompra parametro;
	private List<ParametroOrdenCompra> parametros;
	private int opcion = 0;
	
	
	public ParametroOrdenCompra getParametro() {
		return parametro;
	}
	public void setParametro(ParametroOrdenCompra parametro) {
		this.parametro = parametro;
	}
	public List<ParametroOrdenCompra> getParametros() {
		return parametros;
	}
	public void setParametros(List<ParametroOrdenCompra> parametros) {
		this.parametros = parametros;
	}
	public int getOpcion() {
		return opcion;
	}
	public void setOpcion(int opcion) {
		this.opcion = opcion;
	}


}