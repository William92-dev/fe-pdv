package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;


public class AdFarmaciasBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private Long codigo;
	private String nombre;
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	 

		
}
