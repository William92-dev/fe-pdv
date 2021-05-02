/**
 * 
 */
package com.gizlocorp.adm.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */

// @Audited
@Entity
@Table(name = "tb_ubicacion_geografica")
public class UbicacionGeografica implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_ubic_geog", sequenceName = "seq_ubic_geog", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ubic_geog")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(unique = true, nullable = false, length = 5)
	private String codigo;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "estado", length = 3, nullable = false)
	private String estado;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ubic_geog_padre")
	private UbicacionGeografica ubicacionPadre;

	@OneToMany(mappedBy = "ubicacionPadre", fetch = FetchType.EAGER)
	private List<UbicacionGeografica> ubicacionesHijas;

	@Transient
	private boolean ultimoNivel;

	public boolean isUltimoNivel() {
		return ultimoNivel;
	}

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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public UbicacionGeografica getUbicacionPadre() {
		return ubicacionPadre;
	}

	public void setUbicacionPadre(UbicacionGeografica ubicacionPadre) {
		this.ubicacionPadre = ubicacionPadre;
	}

	public List<UbicacionGeografica> getUbicacionesHijas() {
		return ubicacionesHijas;
	}

	public void setUbicacionesHijas(List<UbicacionGeografica> ubicacionesHijas) {
		this.ubicacionesHijas = ubicacionesHijas;
	}

	public void addUbicacionHija(UbicacionGeografica hija) {
		hija.setUbicacionPadre(this);
		if (ubicacionesHijas == null) {
			ubicacionesHijas = new ArrayList<UbicacionGeografica>();
		}
		this.ubicacionesHijas.add(hija);
	}

	public void setUltimoNivel(boolean ultimoNivel) {
		this.ultimoNivel = ultimoNivel;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}
