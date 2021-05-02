package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.modelo.Organizacion;

@SessionScoped
@Named("restablecerUsuarioDataBean")
public class RestablecerUsuarioDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Estado estado;
	private List<Organizacion> listOrganizacion;

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<Organizacion> getListOrganizacion() {
		return listOrganizacion;
	}

	public void setListOrganizacion(List<Organizacion> listOrganizacion) {
		this.listOrganizacion = listOrganizacion;
	}
}
