/**
 * 
 */
package com.gizlocorp.adm.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.gizlocorp.adm.enumeracion.Estado;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */

// @Audited
@Entity
@Table(name = "tb_catalogo")
public class Catalogo implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_catalogo", sequenceName = "seq_catalogo", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_catalogo")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(nullable = false, length = 5)
	private String codigo;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "estado", length = 3, nullable = false)
	@Enumerated(EnumType.STRING)
	private Estado estado;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "catalogoPadre")
	private List<Catalogo> catalogosHijos;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_catalogo_padre")
	private Catalogo catalogoPadre;

	@Column(name = "id_tipo_catalogo", insertable = true, updatable = true)
	private Long idCatalogo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_catalogo", insertable = false, updatable = false)
	private TipoCatalogo tipoCatalogo;

	@Column(name = "descripcion", length = 200)
	private String descripcion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<Catalogo> getCatalogosHijos() {
		return catalogosHijos;
	}

	public void setCatalogosHijos(List<Catalogo> catalogosHijos) {
		this.catalogosHijos = catalogosHijos;
	}

	public Catalogo getCatalogoPadre() {
		return catalogoPadre;
	}

	public void setCatalogoPadre(Catalogo catalogoPadre) {
		this.catalogoPadre = catalogoPadre;
	}

	public TipoCatalogo getTipoCatalogo() {
		return tipoCatalogo;
	}

	public void setTipoCatalogo(TipoCatalogo tipoCatalogo) {
		this.tipoCatalogo = tipoCatalogo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getIdCatalogo() {
		return idCatalogo;
	}

	public void setIdCatalogo(Long idCatalogo) {
		this.idCatalogo = idCatalogo;
	}

	public String toString() {
		StringBuilder entidad = new StringBuilder();

		entidad.append("Nombre: ");
		entidad.append(this.getNombre());
		entidad.append(", ");
		entidad.append("Codigo: ");
		entidad.append(this.getCodigo());
		entidad.append(", ");
		entidad.append("Tipo: ");
		entidad.append(this.getTipoCatalogo().getNombre());
		entidad.append(", ");
		entidad.append("Descripcion: ");
		entidad.append(this.getDescripcion());

		return entidad.toString();
	}
}
