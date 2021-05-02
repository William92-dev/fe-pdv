package com.gizlocorp.gnvoice.modelo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;

@Entity
@Table(name = "tb_retencion")
public class Retencion {

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_comp_retencion", sequenceName = "seq_comp_retencion", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_comp_retencion")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "ruc", nullable = false, length = 13)
	private String ruc;

	@Column(name = "tipo_generacion", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private TipoGeneracion tipoGeneracion;

	@Column(name = "estado", length = 15, nullable = false)
	private String estado;

	@Column(name = "info_adicional", length = 500)
	private String infoAdicional;

	@Column(nullable = false, name = "cod_secuencial", length = 100)
	private String codSecuencial;

	@Column(name = "fecha_emision")
	@Temporal(TemporalType.DATE)
	private Date fechaEmisionDb;

	@Column(name = "dir_establecimiento", length = 500)
	private String dirEstablecimiento;

	@Column(nullable = false, name = "contribuyente_especial", length = 5)
	private String contribuyenteEspecial;

	@Column(nullable = false, name = "obligado_contabilidad", length = 3)
	private String obligadoContabilidad;

	@Column(name = "tipo_id_sujeto_retenido", length = 50)
	private String tipoIdentificacionSujetoRetenido;

	@Column(name = "razon_social_sujeto_retenido", length = 100)
	private String razonSocialSujetoRetenido;

	@Column(nullable = false, name = "id_sujeto_retenido", length = 100)
	private String identificacionSujetoRetenido;

	@Column(name = "periodo_fiscal", length = 10)
	private String periodoFiscal;

	@Column(name = "pto_emision", length = 100)
	private String ptoEmision;

	@Column(nullable = false, unique = true, name = "clave_acceso", length = 100)
	private String claveAcceso;

	@Column(nullable = false, name = "cod_documento", length = 20)
	private String codDoc;

	@Column(nullable = false, name = "cod_punto_emision", length = 20)
	private String codPuntoEmision;

	@Column(name = "clave_interna", length = 20)
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

	@Column(name = "correo_notificacion", length = 100, nullable = true)
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


	

	@Column(name = "razon_social", length = 300)
	private String razonSocial;
	
	
	@Column(name = "proceso", length = 100)
	private String proceso;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getInfoAdicional() {
		return infoAdicional;
	}

	public void setInfoAdicional(String infoAdicional) {
		this.infoAdicional = infoAdicional;
	}

	public String getCodSecuencial() {
		return codSecuencial;
	}

	public void setCodSecuencial(String codSecuencial) {
		this.codSecuencial = codSecuencial;
	}

	public Date getFechaEmisionDb() {
		return fechaEmisionDb;
	}

	public String getFechaEmisionDbStr() {
		if (fechaEmisionDb != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			return df.format(fechaEmisionDb);
		}
		return "";
	}

	public void setFechaEmisionDb(Date fechaEmisionDb) {
		this.fechaEmisionDb = fechaEmisionDb;
	}

	public String getDirEstablecimiento() {
		return dirEstablecimiento;
	}

	public void setDirEstablecimiento(String dirEstablecimiento) {
		this.dirEstablecimiento = dirEstablecimiento;
	}

	public String getTipoIdentificacionSujetoRetenido() {
		return tipoIdentificacionSujetoRetenido;
	}

	public void setTipoIdentificacionSujetoRetenido(
			String tipoIdentificacionSujetoRetenido) {
		this.tipoIdentificacionSujetoRetenido = tipoIdentificacionSujetoRetenido;
	}

	public String getRazonSocialSujetoRetenido() {
		if (razonSocialSujetoRetenido != null) {
			razonSocialSujetoRetenido = StringEscapeUtils
					.unescapeXml(razonSocialSujetoRetenido);
		}
		return razonSocialSujetoRetenido;
	}

	public void setRazonSocialSujetoRetenido(String razonSocialSujetoRetenido) {
		this.razonSocialSujetoRetenido = razonSocialSujetoRetenido;
	}

	public String getIdentificacionSujetoRetenido() {
		return identificacionSujetoRetenido;
	}

	public void setIdentificacionSujetoRetenido(
			String identificacionSujetoRetenido) {
		this.identificacionSujetoRetenido = identificacionSujetoRetenido;
	}

	public String getPeriodoFiscal() {
		return periodoFiscal;
	}

	public void setPeriodoFiscal(String periodoFiscal) {
		this.periodoFiscal = periodoFiscal;
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

	public String getContribuyenteEspecial() {
		return contribuyenteEspecial;
	}

	public void setContribuyenteEspecial(String contribuyenteEspecial) {
		this.contribuyenteEspecial = contribuyenteEspecial;
	}

	public String getObligadoContabilidad() {
		return obligadoContabilidad;
	}

	public void setObligadoContabilidad(String obligadoContabilidad) {
		this.obligadoContabilidad = obligadoContabilidad;
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

	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	public String getFechaAutorizacionStr() {
		if (fechaAutorizacion != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			return df.format(fechaAutorizacion);
		}
		return "";
	}

	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
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

	

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
}
