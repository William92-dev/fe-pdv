package com.gizlocorp.adm.auditoria;

import java.io.Serializable;
import java.util.Date;

public class GizloAudData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7268125523255115070L;

	private Date fecha;

	private String operacion;

	private String usuario;

	private String entidad;

	private String data;

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
