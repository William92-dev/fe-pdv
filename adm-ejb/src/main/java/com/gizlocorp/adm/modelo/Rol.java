package com.gizlocorp.adm.modelo;

import java.io.Serializable;
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

//@Audited
@Entity
@Table(name = "tb_rol")
public class Rol implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_rol", sequenceName = "seq_rol", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rol")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(unique = true, nullable = false, length = 5)
	private String codigo;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rolPadre")
	private List<Rol> rolesAsignacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_rol_padre")
	private Rol rolPadre;

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

	public List<Rol> getRolesAsignacion() {
		return rolesAsignacion;
	}

	public void setRolesAsignacion(List<Rol> rolesAsignacion) {
		this.rolesAsignacion = rolesAsignacion;
	}

	public Rol getRolPadre() {
		return rolPadre;
	}

	public void setRolPadre(Rol rolPadre) {
		this.rolPadre = rolPadre;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}
