/**
 * 
 */
package com.gizlocorp.adm.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */

// @Audited
@Entity
@Table(name = "tb_persona")
public class Persona implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_persona", sequenceName = "seq_persona", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_persona")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "identificacion", nullable = false, length = 20, unique = true)
	private String identificacion;

	@Column(name = "nombres", nullable = false, length = 100)
	private String nombres;

	@Column(name = "apellidos", nullable = false, length = 100)
	private String apellidos;

	@Column(name = "tipo_id", insertable = true, updatable = true)
	private Long idTipoIdentificacion;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tipo_id", insertable = false, updatable = false)
	private Catalogo tipoIdentificacion;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_nacimiento", nullable = true)
	private Date fechaNacimiento;

	@Column(name = "genero", insertable = true, updatable = true)
	private Long idGenero;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "genero", insertable = false, updatable = false)
	private Catalogo genero;

	@Column(name = "estado_civil", insertable = true, updatable = true)
	private Long idEstadoCivil;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "estado_civil", insertable = false, updatable = false)
	private Catalogo estadoCivil;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ciudad_nacimiento")
	private UbicacionGeografica ciudadNacimiento;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ciudad_recidencias")
	private UbicacionGeografica ciudadRecidencia;

	@Column(name = "correo_electronico", nullable = true, length = 150)
	private String correoElectronico;

	@Column(name = "telefono", nullable = true, length = 12)
	private String telefono;

	@Column(name = "celular", nullable = true, length = 12)
	private String celular;

	@ManyToOne()
	@JoinColumn(name = "id_organizacion")
	private Organizacion organizacion;

	@Column(name = "foto", nullable = true, length = 100)
	private String foto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Catalogo getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(Catalogo tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Catalogo getGenero() {
		return genero;
	}

	public void setGenero(Catalogo genero) {
		this.genero = genero;
	}

	public Catalogo getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(Catalogo estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public UbicacionGeografica getCiudadNacimiento() {
		return ciudadNacimiento;
	}

	public void setCiudadNacimiento(UbicacionGeografica ciudadNacimiento) {
		this.ciudadNacimiento = ciudadNacimiento;
	}

	public UbicacionGeografica getCiudadRecidencia() {
		return ciudadRecidencia;
	}

	public void setCiudadRecidencia(UbicacionGeografica ciudadRecidencia) {
		this.ciudadRecidencia = ciudadRecidencia;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

	public Long getIdGenero() {
		return idGenero;
	}

	public void setIdGenero(Long idGenero) {
		this.idGenero = idGenero;
	}

	public Long getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(Long idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}

	public Long getIdTipoIdentificacion() {
		return idTipoIdentificacion;
	}

	public void setIdTipoIdentificacion(Long idTipoIdentificacion) {
		this.idTipoIdentificacion = idTipoIdentificacion;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	@Transient
	public String getNombreCompleto() {
		StringBuilder nombreCompleto = new StringBuilder();
		nombreCompleto.append(nombres);
		nombreCompleto.append(" ");
		nombreCompleto.append(apellidos);
		return nombreCompleto.toString();
	}
}
