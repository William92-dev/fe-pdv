package com.gizlocorp.gnvoiceFybeca.modelo;

import java.io.Serializable;

import javax.persistence.Column;

/**
 * The persistent class for the Factura database table.
 * 
 */
// @Entity
public class TipoComprobante implements Serializable {
	private static final long serialVersionUID = 1L;

	// @Id
	@Column(name = "CODIGO")
	private Long codigo;

	@Column(name = "DESCRIPCION", length = 500)
	private String descripcion;

	public TipoComprobante() {
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}