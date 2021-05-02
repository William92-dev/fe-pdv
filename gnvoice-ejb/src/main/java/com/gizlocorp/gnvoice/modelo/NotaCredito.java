package com.gizlocorp.gnvoice.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "tb_nota_credito")
public class NotaCredito implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7552736195897481734L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_nota_credito", sequenceName = "seq_nota_credito", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nota_credito")
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

	@Column(name = "fecha_emision")
	@Temporal(TemporalType.DATE)
	private Date fechaEmisionDb;

	@Column(name = "dir_establecimiento", length = 300)
	private String dirEstablecimiento;

	@Column(name = "tipo_id_comprador", length = 50)
	private String tipoIdentificacionComprador;

	@Column(name = "razon_social_comprador", length = 300)
	private String razonSocialComprador;

	@Column(nullable = false, name = "identificacion_comprador", length = 50)
	private String identificacionComprador;

	@Column(nullable = false, name = "contribuyente_especial", length = 5)
	private String contribuyenteEspecial;

	@Column(nullable = false, name = "obligado_contabilidad", length = 3)
	private String obligadoContabilidad;

	@Column(name = "rise", length = 50)
	private String rise;

	@Column(nullable = false, name = "cod_doc_modificado", length = 100)
	private String codDocModificado;

	@Column(nullable = false, name = "num_doc_modificado", length = 100)
	private String numDocModificado;

	@Column(name = "fech_emi_doc_sustento")
	@Temporal(TemporalType.DATE)
	private Date fechaEmisionDocSustentoDb;

	@Column(name = "total_sin_impuestos", length = 8, scale = 2)
	private BigDecimal totalSinImpuestos;

	@Column(name = "valor_modificacion", length = 8, scale = 2)
	private BigDecimal valorModificacion;

	@Column(name = "moneda", length = 50)
	private String moneda;

	private String motivo;

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

	@Column(name = "proceso", length = 100)
	private String proceso;
	
	@Column(nullable = true, name = "orden_compra", length = 100)
	private String ordenCompra;
	
	// @Transient
	// private List<SeguimientoNotaCredito> seguimiento;

	@Column(name = "agencia", length = 20)
	private String agencia;

	@Column(name = "identificador_usuario", length = 50)
	private String identificadorUsuario;

	@Column(name = "tipo_ejecucion", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private TipoEjecucion tipoEjecucion;
	
	@Column(nullable = true, name = "mensaje_reim", length = 100)
	 private String mensajeErrorReim;

	@Column(name = "modo_envio", length = 15, nullable = true)
	private String modoEnvio;
	
	 @Column(name = "fecha_recep_mail", nullable = true) 
	 @Temporal(TemporalType.TIMESTAMP)
	 private Date fechaRecepcionMail;

	 @Column(name = "fecha_lec_trad", nullable = true)
	 @Temporal(TemporalType.TIMESTAMP)
	 private Date fechaLecturaTraductor;

	 @Column(name = "fecha_ent_reim", nullable = true)
	 @Temporal(TemporalType.TIMESTAMP)
	 private Date fechaEntReim;
	
	 @Column(name = "archivo_gen_reim", nullable = true, length = 255)
	 private String archivoGenReim;
	 
	 
	 @Column(nullable = true, name = "is_oferton", length = 4)
	 private String isOferton;
	 
	 
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

	public Date getFechaEmisionDb() {
		return fechaEmisionDb;
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

	public String getTipoIdentificacionComprador() {
		return tipoIdentificacionComprador;
	}

	public void setTipoIdentificacionComprador(
			String tipoIdentificacionComprador) {
		this.tipoIdentificacionComprador = tipoIdentificacionComprador;
	}

	public String getRazonSocialComprador() {
		if (razonSocialComprador != null) {
			razonSocialComprador = StringEscapeUtils
					.unescapeXml(razonSocialComprador);
		}
		return razonSocialComprador;
	}

	public void setRazonSocialComprador(String razonSocialComprador) {
		this.razonSocialComprador = razonSocialComprador;
	}

	public String getIdentificacionComprador() {
		return identificacionComprador;
	}

	public void setIdentificacionComprador(String identificacionComprador) {
		this.identificacionComprador = identificacionComprador;
	}

	public String getRise() {
		return rise;
	}

	public void setRise(String rise) {
		this.rise = rise;
	}

	public String getCodDocModificado() {
		return codDocModificado;
	}

	public void setCodDocModificado(String codDocModificado) {
		this.codDocModificado = codDocModificado;
	}

	public String getNumDocModificado() {
		return numDocModificado;
	}

	public void setNumDocModificado(String numDocModificado) {
		this.numDocModificado = numDocModificado;
	}

	public Date getFechaEmisionDocSustentoDb() {
		return fechaEmisionDocSustentoDb;
	}

	public void setFechaEmisionDocSustentoDb(Date fechaEmisionDocSustentoDb) {
		this.fechaEmisionDocSustentoDb = fechaEmisionDocSustentoDb;
	}

	public BigDecimal getTotalSinImpuestos() {
		return totalSinImpuestos;
	}

	public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
		this.totalSinImpuestos = totalSinImpuestos;
	}

	public BigDecimal getValorModificacion() {
		return valorModificacion;
	}

	public void setValorModificacion(BigDecimal valorModificacion) {
		this.valorModificacion = valorModificacion;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
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

	// public List<SeguimientoNotaCredito> getSeguimiento() {
	// return seguimiento;
	// }
	//
	// public void setSeguimiento(List<SeguimientoNotaCredito> seguimiento) {
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

	public TipoEjecucion getTipoEjecucion() {
		return tipoEjecucion;
	}

	public void setTipoEjecucion(TipoEjecucion tipoEjecucion) {
		this.tipoEjecucion = tipoEjecucion;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public String getOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(String ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	public String getMensajeErrorReim() {
		return mensajeErrorReim;
	}

	public void setMensajeErrorReim(String mensajeErrorReim) {
		this.mensajeErrorReim = mensajeErrorReim;
	}

	public String getModoEnvio() {
		return modoEnvio;
	}

	public void setModoEnvio(String modoEnvio) {
		this.modoEnvio = modoEnvio;
	}

	public Date getFechaRecepcionMail() {
		return fechaRecepcionMail;
	}

	public void setFechaRecepcionMail(Date fechaRecepcionMail) {
		this.fechaRecepcionMail = fechaRecepcionMail;
	}

	public Date getFechaLecturaTraductor() {
		return fechaLecturaTraductor;
	}

	public void setFechaLecturaTraductor(Date fechaLecturaTraductor) {
		this.fechaLecturaTraductor = fechaLecturaTraductor;
	}

	public Date getFechaEntReim() {
		return fechaEntReim;
	}

	public void setFechaEntReim(Date fechaEntReim) {
		this.fechaEntReim = fechaEntReim;
	}

	public String getArchivoGenReim() {
		return archivoGenReim;
	}

	public void setArchivoGenReim(String archivoGenReim) {
		this.archivoGenReim = archivoGenReim;
	}

	public String getIsOferton() {
		return isOferton;
	}

	public void setIsOferton(String isOferton) {
		this.isOferton = isOferton;
	}
	
	
	 

}
