package com.gizlocorp.adm.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.enumeracion.TipoParametro;

/**
 * Entidad que representa la tabla CW_PARAMETRO de la base de datos.
 * 
 * @author gizlo
 * @revision $Revision: 1.4 $
 */

// @Audited
@Entity
@Table(name = "TB_PARAMETRO_ORDEN_COMPRA")
public class ParametroOrdenCompra implements Serializable {

	private static final long serialVersionUID = 7680538347715861330L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_parametro_orden_compra", sequenceName = "seq_parametro_orden_compra", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_parametro_orden_compra")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "descripcion", length = 500)
	private String descripcion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "estado", length = 3)
	private Estado estado;

	@Column(nullable = false, name = "valor", length = 500)
	private String valor;
	
	@Column(nullable = false, name = "proveedor", length = 500)
	private String proveedor;
	

	
	
	
	
	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	


}