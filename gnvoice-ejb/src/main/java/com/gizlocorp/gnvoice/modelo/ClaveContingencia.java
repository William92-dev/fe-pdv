package com.gizlocorp.gnvoice.modelo;

import java.util.List;

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

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;

//@Audited
@Entity
@Table(name = "tb_clave_contigencia")
public class ClaveContingencia {

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_clave_contigencia", sequenceName = "seq_clave_contigencia", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_clave_contigencia")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "clave", length = 100)
	private String clave;

	@Column(name = "usada", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	private Logico usada;

	@Column(name = "clave_acceso", length = 100)
	private String claveAcceso;

	@Column(name = "id_organizacion", nullable = true)
	private Long idOrganizacion;

	@Column(name = "tipo_ambiente", nullable = true, length = 1)
	// @Enumerated(EnumType.STRING)
	private String tipoAmbiente;

	@Transient
	private TipoAmbienteEnum tipoAmbienteObj;

	@Transient
	private Organizacion organizacionObj;

	@Transient
	private boolean usadaBol;

	@Transient
	private List<ClaveContingencia> claves;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Logico getUsada() {
		return usada;
	}

	public void setUsada(Logico usada) {
		this.usada = usada;
	}

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	public boolean isUsadaBol() {
		return usadaBol;
	}

	public void setUsadaBol(boolean usadaBol) {
		this.usadaBol = usadaBol;
	}

	public Long getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public String getTipoAmbiente() {
		return tipoAmbiente;
	}

	public void setTipoAmbiente(String tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public TipoAmbienteEnum getTipoAmbienteObj() {
		return tipoAmbienteObj;
	}

	public void setTipoAmbienteObj(TipoAmbienteEnum tipoAmbienteObj) {
		this.tipoAmbienteObj = tipoAmbienteObj;
	}

	public Organizacion getOrganizacionObj() {
		return organizacionObj;
	}

	public void setOrganizacionObj(Organizacion organizacionObj) {
		this.organizacionObj = organizacionObj;
	}

	public List<ClaveContingencia> getClaves() {
		return claves;
	}

	public void setClaves(List<ClaveContingencia> claves) {
		this.claves = claves;
	}
}