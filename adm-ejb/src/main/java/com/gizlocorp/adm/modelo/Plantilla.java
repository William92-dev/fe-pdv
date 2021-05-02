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

import com.gizlocorp.adm.enumeracion.Estado;

/**
 * The persistent class for the Plantilla database table.
 * 
 */

// @Audited
@Entity
@Table(name = "tb_plantilla")
public class Plantilla implements Serializable {
	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_plantilla", sequenceName = "seq_plantilla", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_plantilla")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(nullable = false, length = 50)
	private String codigo;

	@Column(length = 500)
	private String descripcion;

	@Column(nullable = false, length = 1000)
	private String valor;

	@Column(nullable = false, length = 200)
	private String titulo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "estado", length = 3)
	private Estado estado;

	@Column(name = "id_aplicacion")
	private Long idAplicacion;

	@Column(name = "id_organizacion", nullable = true)
	private Long idOrganizacion;

	public Plantilla() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdAplicacion() {
		return idAplicacion;
	}

	public void setIdAplicacion(Long idAplicacion) {
		this.idAplicacion = idAplicacion;
	}

	public Long getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public String toString() {
		StringBuilder entidad = new StringBuilder();

		entidad.append("Codigo: ");
		entidad.append(this.getCodigo());
		entidad.append(", ");
		entidad.append("Descripcion:");
		entidad.append(this.getDescripcion());
		entidad.append(", ");
		entidad.append("Titulo: ");
		entidad.append(this.getTitulo());
		entidad.append(", ");
		entidad.append("Mensaje: ");
		entidad.append(this.getValor());

		return entidad.toString();
	}
}