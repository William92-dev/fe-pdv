package com.gizlocorp.adm.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;

//@Audited
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@Table(name = "tb_usuario")
public abstract class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_usuario", sequenceName = "seq_usuario", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
	@Column(name = "id", nullable = false)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "usuario")
	protected List<UsuarioRol> usuariosRoles;

	@Column(name = "username", length = 20, nullable = false, unique = true)
	protected String username;

	@ManyToOne()
	@JoinColumn(name = "id_persona", nullable = false)
	protected Persona persona;

	@Column(name = "es_clv_autogenerada", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	private Logico esClaveAutogenerada;

	@Column(name = "estado", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private Estado estado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<UsuarioRol> getUsuariosRoles() {
		return usuariosRoles;
	}

	public void setUsuariosRoles(List<UsuarioRol> usuariosRoles) {
		this.usuariosRoles = usuariosRoles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Logico getEsClaveAutogenerada() {
		return esClaveAutogenerada;
	}

	public void setEsClaveAutogenerada(Logico esClaveAutogenerada) {
		this.esClaveAutogenerada = esClaveAutogenerada;
	}

	public String toString() {
		StringBuilder entidad = new StringBuilder();
		Persona persona = this.getPersona();

		entidad.append("Identificacion: ");
		entidad.append(persona.getIdentificacion());
		entidad.append(", ");
		entidad.append("Nombre: ");
		entidad.append(persona.getNombres());
		entidad.append(", ");
		entidad.append("Apellido: ");
		entidad.append(persona.getApellidos());
		entidad.append(", ");
		entidad.append("Correo Electronico: ");
		entidad.append(persona.getCorreoElectronico());
		entidad.append(", ");
		entidad.append("Nombre Usuario: ");
		entidad.append(this.getUsername());
		entidad.append(", ");
		entidad.append("Empresa: ");
		entidad.append(((persona.getOrganizacion() != null) ? persona
				.getOrganizacion().getNombre() : ""));
		entidad.append(", ");
		entidad.append("Estado: ");
		entidad.append(this.getEstado().getDescripcion());
		entidad.append(", ");
		entidad.append("Foto: ");
		entidad.append(persona.getFoto() != null ? persona.getFoto() : "");

		return entidad.toString();
	}
}
