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
import javax.persistence.Transient;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;

//@Audited
@Entity
@Table(name = "tb_organizacion")
public class Organizacion implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_organizacion", sequenceName = "seq_organizacion", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_organizacion")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "acronimo", length = 50, nullable = false)
	private String acronimo;

	@Column(name = "id_organizacion_padre", insertable = true, updatable = true)
	private Long idOrganizacionPadre;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_organizacion_padre", insertable = false, updatable = false)
	private Organizacion organizacionPadre;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "organizacionPadre")
	private List<Organizacion> organizaciones;

	@ManyToOne()
	@JoinColumn(name = "id_ciudad", insertable = false, updatable = false)
	private UbicacionGeografica ciudad;

	@Column(name = "id_ciudad", insertable = true, updatable = true)
	private Long id_ciudad;

	@Column(name = "telefono", length = 15, nullable = true)
	private String telefono;

	@Column(name = "direccion", nullable = true, length = 300)
	private String direccion;

	@Column(name = "estado", length = 3)
	@Enumerated(EnumType.STRING)
	private Estado estado;

	@Column(name = "token", nullable = true, length = 200)
	private String token;

	@Column(name = "clave_token", nullable = true, length = 50)
	private String claveToken;

	@Column(name = "en_contingencia", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	private Logico enContigencia;

	@Column(name = "nombre_comercial", nullable = true, length = 100)
	protected String nombreComercial;

	@Column(name = "ruc", length = 13, nullable = false, unique = true)
	protected String ruc;

	@Column(nullable = true, length = 3)
	private String establecimiento;

	@Column(nullable = true, length = 3)
	private String puntoEmision;

	@Column(name = "correo_electronico", nullable = true, length = 150)
	private String correoElectronico;

	@Column(name = "resol_contribuyente_esp", nullable = true, length = 5)
	private String resolContribuyenteEsp;

	@Column(name = "dir_establecimiento", nullable = true, length = 300)
	private String dirEstablecimiento;

	@Column(name = "logo_empresa", nullable = true, length = 100)
	private String logoEmpresa;
	
	@Column(name = "logo_empresa_opcional" , length = 100)
	private String logoEmpresaOpcional;
	
	@Column(name = "cedis_virtual", nullable = true, length = 100)
	private String ceDisVirtual;

	@Column(name = "es_obligado_contabilidad", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	private Logico esObligadoContabilidad;
	
	@Column(name = "acronimo_opcional", length = 50, nullable = true)
	private String acronimoOpcional;

	@Transient
	private boolean enContigenciaBol;

	@Transient
	private boolean esObligadoContabilidadBol;

	public Organizacion() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Organizacion> getOrganizaciones() {
		return organizaciones;
	}

	public void setOrganizaciones(List<Organizacion> organizaciones) {
		this.organizaciones = organizaciones;
	}

	public Organizacion getOrganizacionPadre() {
		return organizacionPadre;
	}

	public void setOrganizacionPadre(Organizacion organizacionPadre) {
		this.organizacionPadre = organizacionPadre;
	}

	public String getNombre() {
		return nombre;
	}

	public String getAcronimo() {
		return acronimo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdOrganizacionPadre() {
		return idOrganizacionPadre;
	}

	public void setIdOrganizacionPadre(Long idOrganizacionPadre) {
		this.idOrganizacionPadre = idOrganizacionPadre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setAcronimo(String acronimo) {
		this.acronimo = acronimo;
	}

	public UbicacionGeografica getCiudad() {
		return ciudad;
	}

	public void setCiudad(UbicacionGeografica ciudad) {
		this.ciudad = ciudad;
	}

	public Long getId_ciudad() {
		return id_ciudad;
	}

	public void setId_ciudad(Long id_ciudad) {
		this.id_ciudad = id_ciudad;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClaveToken() {
		return claveToken;
	}

	public void setClaveToken(String claveToken) {
		this.claveToken = claveToken;
	}

	public Logico getEnContigencia() {
		return enContigencia;
	}

	public void setEnContigencia(Logico enContigencia) {
		this.enContigencia = enContigencia;
	}

	public String getNombreComercial() {
		return nombreComercial;
	}

	public void setNombreComercial(String nombreComercial) {
		this.nombreComercial = nombreComercial;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getEstablecimiento() {
		return establecimiento;
	}

	public void setEstablecimiento(String establecimiento) {
		this.establecimiento = establecimiento;
	}

	public String getPuntoEmision() {
		return puntoEmision;
	}

	public void setPuntoEmision(String puntoEmision) {
		this.puntoEmision = puntoEmision;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public boolean isEnContigenciaBol() {
		return Logico.S.equals(enContigencia);
	}

	public void setEnContigenciaBol(boolean enContigenciaBol) {
		this.enContigenciaBol = enContigenciaBol;
	}

	public String getResolContribuyenteEsp() {
		return resolContribuyenteEsp;
	}

	public void setResolContribuyenteEsp(String resolContribuyenteEsp) {
		this.resolContribuyenteEsp = resolContribuyenteEsp;
	}

	public String getDirEstablecimiento() {
		return dirEstablecimiento;
	}

	public void setDirEstablecimiento(String dirEstablecimiento) {
		this.dirEstablecimiento = dirEstablecimiento;
	}

	public String getLogoEmpresa() {
		return logoEmpresa;
	}

	public void setLogoEmpresa(String logoEmpresa) {
		this.logoEmpresa = logoEmpresa;
	}

	public Logico getEsObligadoContabilidad() {
		return esObligadoContabilidad;
	}

	public void setEsObligadoContabilidad(Logico esObligadoContabilidad) {
		this.esObligadoContabilidad = esObligadoContabilidad;
	}

	public boolean isEsObligadoContabilidadBol() {
		return Logico.S.equals(esObligadoContabilidad);
	}

	public void setEsObligadoContabilidadBol(boolean esObligadoContabilidadBol) {
		this.esObligadoContabilidadBol = esObligadoContabilidadBol;
	}

	 public String getCeDisVirtual() {
	 return ceDisVirtual;
	 }
	
	 public void setCeDisVirtual(String ceDisVirtual) {
	 this.ceDisVirtual = ceDisVirtual;
	 }

	 
	  
	 
	public String getLogoEmpresaOpcional() {
		return logoEmpresaOpcional;
	}

	public void setLogoEmpresaOpcional(String logoEmpresaOpcional) {
		this.logoEmpresaOpcional = logoEmpresaOpcional;
	}
	
	
	
	

	public String getAcronimoOpcional() {
		return acronimoOpcional;
	}

	public void setAcronimoOpcional(String acronimoOpcional) {
		this.acronimoOpcional = acronimoOpcional;
	}

	public String toString() {
		StringBuilder entidad = new StringBuilder();

		entidad.append("RUC: ");
		entidad.append(this.getRuc());
		entidad.append(", ");
		entidad.append("Nombre: ");
		entidad.append(this.getNombre());
		entidad.append(", ");
		entidad.append("Acronimo: ");
		entidad.append(this.getAcronimo());
		entidad.append(", ");
		entidad.append("Nombre Comercial: ");
		entidad.append(this.getNombreComercial());
		entidad.append(", ");
		entidad.append("Correo: ");
		entidad.append(this.getCorreoElectronico());
		entidad.append(", ");
		entidad.append("Direccion: ");
		entidad.append(this.getDireccion());
		entidad.append(", ");
		entidad.append("Punto de Emision: ");
		entidad.append(this.getPuntoEmision());
		entidad.append(", ");
		entidad.append("Establecimiento: ");
		entidad.append(this.getEstablecimiento());
		entidad.append(", ");
		entidad.append("Telefono: ");
		entidad.append(this.getTelefono());
		entidad.append(", ");
		entidad.append("Obligado a llevar contabilidad: ");
		entidad.append(this.getEsObligadoContabilidad().getDescripcion());
		entidad.append(", ");
		entidad.append("Resolucion contribuyente especial: ");
		entidad.append(this.getResolContribuyenteEsp());
		entidad.append(", ");
		entidad.append("Token: ");
		entidad.append(this.getToken());
		entidad.append(", ");
		entidad.append("Logo: ");
		entidad.append(this.getLogoEmpresa());
		entidad.append(", ");
		entidad.append("Estado: ");
		entidad.append(this.getEstado());

		return entidad.toString();
	}
}
