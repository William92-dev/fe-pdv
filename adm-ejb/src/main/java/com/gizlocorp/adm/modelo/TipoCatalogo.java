/**
 * 
 */
package com.gizlocorp.adm.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */

// @Audited
@Entity
@Table(name = "tb_tipo_catalogo")
public class TipoCatalogo implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_tipo_catalogo", sequenceName = "seq_tipo_catalogo", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tipo_catalogo")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(nullable = false, length = 5, unique = true)
	private String codigo;

	@Column(name = "nombre", length = 200, nullable = false)
	private String nombre;

	@Column(name = "descripcion", length = 200, nullable = true)
	private String descripcion;

	@Column(name = "estado", length = 3, nullable = false)
	private String estado;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoCatalogo")
	private List<Catalogo> catalogos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<Catalogo> getCatalogos() {
		return catalogos;
	}

	public void setCatalogos(List<Catalogo> catalogos) {
		this.catalogos = catalogos;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String toString() {
		StringBuilder entidad = new StringBuilder();

		entidad.append("Codigo: ");
		entidad.append(this.getCodigo());
		entidad.append(", ");
		entidad.append("Nombre: ");
		entidad.append(this.getNombre());
		entidad.append(", ");
		entidad.append("Descripcion: ");
		entidad.append(this.getDescripcion());

		return entidad.toString();
	}
}
