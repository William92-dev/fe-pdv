package com.gizlocorp.gnvoice.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;

//@Audited
@Entity
@Table(name = "tb_guia_remision")
public class GuiaRemision implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1163627046221254413L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_guia_remision", sequenceName = "seq_guia_remision", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_guia_remision")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "ruc", nullable = false, length = 13)
	private String ruc;

	@Column(name = "tipo_generacion", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private TipoGeneracion tipoGeneracion;

	@Column(name = "estado", length = 15, nullable = false)
	private String estado;

	@Column(name = "dir_establecimiento", length = 300)
	private String dirEstablecimiento;

	@Column(name = "dir_partida", length = 300)
	private String dirPartida;

	@Column(name = "razon_social_transportista", length = 300)
	private String razonSocialTransportista;

	@Column(name = "tipo_id_transportista", length = 50)
	private String tipoIdentificacionTransportista;

	@Column(nullable = false, name = "ruc_transportista", length = 20)
	private String rucTransportista;

	@Column(name = "rise", length = 50)
	private String rise;

	@Column(nullable = false, name = "obligado_contabilidad", length = 3)
	private String obligadoContabilidad;

	@Column(nullable = false, name = "contribuyente_especial", length = 5)
	private String contribuyenteEspecial;

	@Column(name = "fecha_ini_transporteDb")
	@Temporal(TemporalType.DATE)
	private Date fechaIniTransporteDb;

	@Column(name = "fecha_fin_transporteDb")
	@Temporal(TemporalType.DATE)
	private Date fechaFinTransporteDb;

	@Column(name = "placa", length = 10)
	private String placa;

	@Column(nullable = false, name = "cod_secuencial", length = 100)
	private String codSecuencial;

	@Column(name = "pto_emision", length = 50)
	private String ptoEmision;

	@Column(nullable = false, unique = true, name = "clave_acceso", length = 100)
	private String claveAcceso;

	@Column(nullable = false, name = "cod_doc", length = 100)
	private String codDoc;

	@Column(nullable = false, name = "cod_punto_emision", length = 100)
	private String codPuntoEmision;

	@Column(name = "clave_interna", length = 100)
	private String claveInterna;

	@Column(name = "archivo", length = 200)
	private String archivo;

	@Column(name = "archivo_legible", length = 200)
	private String archivoLegible;

	@Column(name = "numero_autorizacion", length = 200)
	private String numeroAutorizacion;

	@Column(name = "fecha_autorizacion")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaAutorizacion;

	@Column(name = "tipo_ambiente", length = 1, nullable = false)
	private String tipoAmbiente;

	@Column(name = "tipo_emision", length = 1, nullable = false)
	private String tipoEmision;

	@Column(name = "correo_notificacion", length = 300, nullable = true)
	private String correoNotificacion;

	@Column(name = "tarea", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private Tarea tareaActual;

	@Column(name = "agencia", length = 20)
	private String agencia;

	@Column(name = "identificador_usuario", length = 50)
	private String identificadorUsuario;

	@Column(name = "documentos_relacionados", length = 500, nullable = true)
	private String documentosRelaciondos;

	@Column(name = "tipo_ejecucion", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private TipoEjecucion tipoEjecucion;

	// @Transient
	// private List<SeguimientoGuiaRemision> seguimiento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDirEstablecimiento() {
		return dirEstablecimiento;
	}

	public void setDirEstablecimiento(String dirEstablecimiento) {
		this.dirEstablecimiento = dirEstablecimiento;
	}

	public String getDirPartida() {
		return dirPartida;
	}

	public void setDirPartida(String dirPartida) {
		this.dirPartida = dirPartida;
	}

	public String getRazonSocialTransportista() {
		if (razonSocialTransportista != null) {
			razonSocialTransportista = StringEscapeUtils
					.unescapeXml(razonSocialTransportista);
		}
		return razonSocialTransportista;
	}

	public void setRazonSocialTransportista(String razonSocialTransportista) {
		this.razonSocialTransportista = razonSocialTransportista;
	}

	public String getTipoIdentificacionTransportista() {
		return tipoIdentificacionTransportista;
	}

	public void setTipoIdentificacionTransportista(
			String tipoIdentificacionTransportista) {
		this.tipoIdentificacionTransportista = tipoIdentificacionTransportista;
	}

	public String getRucTransportista() {
		return rucTransportista;
	}

	public void setRucTransportista(String rucTransportista) {
		this.rucTransportista = rucTransportista;
	}

	public String getRise() {
		return rise;
	}

	public void setRise(String rise) {
		this.rise = rise;
	}

	public Date getFechaIniTransporteDb() {
		return fechaIniTransporteDb;
	}

	public void setFechaIniTransporteDb(Date fechaIniTransporteDb) {
		this.fechaIniTransporteDb = fechaIniTransporteDb;
	}

	public Date getFechaFinTransporteDb() {
		return fechaFinTransporteDb;
	}

	public void setFechaFinTransporteDb(Date fechaFinTransporteDb) {
		this.fechaFinTransporteDb = fechaFinTransporteDb;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getCodSecuencial() {
		return codSecuencial;
	}

	public void setCodSecuencial(String codSecuencial) {
		this.codSecuencial = codSecuencial;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getPtoEmision() {
		return ptoEmision;
	}

	public void setPtoEmision(String ptoEmision) {
		this.ptoEmision = ptoEmision;
	}

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	public String getCodDoc() {
		return codDoc;
	}

	public void setCodDoc(String codDoc) {
		this.codDoc = codDoc;
	}

	public String getCodPuntoEmision() {
		return codPuntoEmision;
	}

	public void setCodPuntoEmision(String codPuntoEmision) {
		this.codPuntoEmision = codPuntoEmision;
	}

	public String getClaveInterna() {
		return claveInterna;
	}

	public void setClaveInterna(String claveInterna) {
		this.claveInterna = claveInterna;
	}

	public String getArchivo() {
		return archivo;
	}

	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

	public String getArchivoLegible() {
		return archivoLegible;
	}

	public void setArchivoLegible(String archivoLegible) {
		this.archivoLegible = archivoLegible;
	}

	public String getObligadoContabilidad() {
		return obligadoContabilidad;
	}

	public void setObligadoContabilidad(String obligadoContabilidad) {
		this.obligadoContabilidad = obligadoContabilidad;
	}

	public String getContribuyenteEspecial() {
		return contribuyenteEspecial;
	}

	public void setContribuyenteEspecial(String contribuyenteEspecial) {
		this.contribuyenteEspecial = contribuyenteEspecial;
	}

	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public TipoGeneracion getTipoGeneracion() {
		return tipoGeneracion;
	}

	public void setTipoGeneracion(TipoGeneracion tipoGeneracion) {
		this.tipoGeneracion = tipoGeneracion;
	}

	public Tarea getTareaActual() {
		return tareaActual;
	}

	public void setTareaActual(Tarea tareaActual) {
		this.tareaActual = tareaActual;
	}

	public String getTipoAmbiente() {
		return tipoAmbiente;
	}

	public void setTipoAmbiente(String tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public String getTipoEmision() {
		return tipoEmision;
	}

	public void setTipoEmision(String tipoEmision) {
		this.tipoEmision = tipoEmision;
	}

	public String getCorreoNotificacion() {
		return correoNotificacion;
	}

	public void setCorreoNotificacion(String correoNotificacion) {
		this.correoNotificacion = correoNotificacion;
	}

	// public List<SeguimientoGuiaRemision> getSeguimiento() {
	// return seguimiento;
	// }
	//
	// public void setSeguimiento(List<SeguimientoGuiaRemision> seguimiento) {
	// this.seguimiento = seguimiento;
	// }

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getIdentificadorUsuario() {
		return identificadorUsuario;
	}

	public void setIdentificadorUsuario(String identificadorUsuario) {
		this.identificadorUsuario = identificadorUsuario;
	}

	public String getDocumentosRelaciondos() {
		return documentosRelaciondos;
	}

	public void setDocumentosRelaciondos(String documentosRelaciondos) {
		this.documentosRelaciondos = documentosRelaciondos;
	}

	public TipoEjecucion getTipoEjecucion() {
		return tipoEjecucion;
	}

	public void setTipoEjecucion(TipoEjecucion tipoEjecucion) {
		this.tipoEjecucion = tipoEjecucion;
	}

}
