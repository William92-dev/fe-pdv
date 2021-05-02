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
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;

//import com.gizlocorp.sistemaPericial.model.CatalogoEspecialidad;

//@Audited
@Entity
@Table(name = "tb_factura_recepcion")
public class FacturaRecepcion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3639413639314219254L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_factura_recepcion", sequenceName = "seq_factura_recepcion", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_factura_recepcion")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "ruc", nullable = false, length = 13)
	private String ruc;

	@Column(name = "tipo_generacion", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private TipoGeneracion tipoGeneracion;

	@Column(name = "estado", length = 15, nullable = false)
	private String estado;

	@Column(name = "fecha_emision_base")
	@Temporal(TemporalType.DATE)
	private Date fechaEmisionBase;

	@Column(name = "dir_establecimiento", length = 300)
	private String dirEstablecimiento;

	@Column(nullable = true, name = "contribuyente_especial", length = 5)
	private String contribuyenteEspecial;

	@Column(nullable = true, name = "obligado_contabilidad", length = 3)
	private String obligadoContabilidad;

	@Column(name = "tipo_id_comprador", length = 50)
	private String tipoIdentificacionComprador;

	@Column(name = "guia_remision", length = 100)
	private String guiaRemision;

	@Column(name = "razon_social_comprador", length = 300)
	private String razonSocialComprador;

	@Column(nullable = true, name = "identificacion_comprador", length = 50)
	private String identificacionComprador;

	@Column(name = "total_sin_impuestos", length = 8, scale = 2)
	private BigDecimal totalSinImpuestos;

	@Column(name = "total_descuento", length = 8, scale = 2)
	private BigDecimal totalDescuento;

	@Column(name = "propina", length = 8, scale = 2)
	private BigDecimal propina;

	@Column(name = "importe_total", length = 8, scale = 2)
	private BigDecimal importeTotal;
	
	@Column(name = "base_cero", length = 8, scale = 2)
	private BigDecimal baseCero;
	
	@Column(name = "base_doce", length = 8, scale = 2)
	private BigDecimal baseDoce;
	
	@Column(name = "iva", length = 8, scale = 2)
	private BigDecimal iva;

	@Column(name = "moneda", length = 50)
	private String moneda;

	@Column(name = "info_adicional", length = 500)
	private String infoAdicional;

	@Column(nullable = true, name = "cod_secuencial", length = 100)
	private String codSecuencial;

	@Column(name = "pto_emision", length = 100)
	private String ptoEmision;

	@Column(nullable = true, unique = true, name = "clave_acceso", length = 100)
	private String claveAcceso;

	@Column(nullable = true, name = "cod_doc", length = 100)
	private String codDoc;

	@Column(nullable = true, name = "cod_punto_emision", length = 100)
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

	@Column(name = "tipo_ambiente", length = 1, nullable = true)
	private String tipoAmbiente;

	@Column(name = "tipo_emision", length = 1, nullable = true)
	private String tipoEmision;

	@Column(name = "correo_notificacion", length = 300, nullable = true)
	private String correoNotificacion;

	@Column(name = "tarea", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	private Tarea tareaActual;

	@Column(name = "agencia", length = 20)
	private String agencia;

	 @Column(name = "proceso", length = 100)
	 private String proceso;

	@Column(name = "identificador_usuario", length = 50)
	private String identificadorUsuario;

	@Column(name = "tipo_ejecucion", nullable = true, length = 3)
	@Enumerated(EnumType.STRING)
	private TipoEjecucion tipoEjecucion;

	// @Transient
	// private List<SeguimientoFactura> seguimiento;

	@Column(name = "requiere_cancelacion", length = 1, nullable = true)
	@Enumerated(EnumType.STRING)
	private Logico requiereCancelacion;

	@Column(name = "observacion_cancelacion", length = 2000)
	private String observacionCancelacion;

	@Column(nullable = true, name = "sec_nota_credito", length = 100)
	private String secuencialNotaCredito;

	@Column(nullable = true, name = "sec_original", length = 100)
	private String secuencialOriginal;

	 @Column(nullable = true, name = "orden_compra", length = 100)
	 private String ordenCompra;
	 
	 @Column(nullable = true, name = "mensaje_reim", length = 100)
	 private String mensajeErrorReim;
	 
//	 @Column(name = "orden_compra")
//	 private boolean traducido;
	
	// @Column(nullable = true, name = "modo_lectura", length = 100)
	// private String modoLectura;
	 
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
	 
	 @Column(name = "base_ice", length = 8, scale = 2)
	 private BigDecimal baseIce;
		
	 @Column(name = "base_irbp", length = 8, scale = 2)
	 private BigDecimal baseIrbp;
		
	 @Column(name = "ice", length = 8, scale = 2)
	 private BigDecimal ice;
		
	 @Column(name = "irbp", length = 8, scale = 2)
	 private BigDecimal irbp;
	 
	 
	 @Column(name = "cantidadTotal", length = 8, scale = 2)
	 private BigDecimal cantidadTotal;

	 
	 
	 
	 
	@Transient
	private String razonSocial;

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

	public Date getFechaEmisionBase() {
		return fechaEmisionBase;
	}

	public void setFechaEmisionBase(Date fechaEmisionBase) {
		this.fechaEmisionBase = fechaEmisionBase;
	}

	public String getDirEstablecimiento() {
		return dirEstablecimiento;
	}

	public void setDirEstablecimiento(String dirEstablecimiento) {
		this.dirEstablecimiento = dirEstablecimiento;
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

	public String getTipoIdentificacionComprador() {
		return tipoIdentificacionComprador;
	}

	public void setTipoIdentificacionComprador(
			String tipoIdentificacionComprador) {
		this.tipoIdentificacionComprador = tipoIdentificacionComprador;
	}

	public String getGuiaRemision() {
		return guiaRemision;
	}

	public void setGuiaRemision(String guiaRemision) {
		this.guiaRemision = guiaRemision;
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

	public BigDecimal getTotalSinImpuestos() {
		return totalSinImpuestos;
	}

	public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
		this.totalSinImpuestos = totalSinImpuestos;
	}

	public BigDecimal getTotalDescuento() {
		return totalDescuento;
	}

	public void setTotalDescuento(BigDecimal totalDescuento) {
		this.totalDescuento = totalDescuento;
	}

	public BigDecimal getPropina() {
		return propina;
	}

	public void setPropina(BigDecimal propina) {
		this.propina = propina;
	}

	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
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

	// public List<SeguimientoFactura> getSeguimiento() {
	// return seguimiento;
	// }
	//
	// public void setSeguimiento(List<SeguimientoFactura> seguimiento) {
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

	public Logico getRequiereCancelacion() {
		return requiereCancelacion;
	}

	public void setRequiereCancelacion(Logico requiereCancelacion) {
		this.requiereCancelacion = requiereCancelacion;
	}

	public String getObservacionCancelacion() {
		return observacionCancelacion;
	}

	public void setObservacionCancelacion(String observacionCancelacion) {
		this.observacionCancelacion = observacionCancelacion;
	}

	public String getSecuencialNotaCredito() {
		return secuencialNotaCredito;
	}

	public void setSecuencialNotaCredito(String secuencialNotaCredito) {
		this.secuencialNotaCredito = secuencialNotaCredito;
	}

	public String getSecuencialOriginal() {
		return secuencialOriginal;
	}

	public void setSecuencialOriginal(String secuencialOriginal) {
		this.secuencialOriginal = secuencialOriginal;
	}

	 public String getProceso() {
		 return proceso;
	 }
	
	 public void setProceso(String proceso) {
		 this.proceso = proceso;
	 }

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
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

	public BigDecimal getBaseCero() {
		return baseCero;
	}

	public void setBaseCero(BigDecimal baseCero) {
		this.baseCero = baseCero;
	}

	public BigDecimal getBaseDoce() {
		return baseDoce;
	}

	public void setBaseDoce(BigDecimal baseDoce) {
		this.baseDoce = baseDoce;
	}

	public BigDecimal getIva() {
		return iva;
	}

	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}

	public BigDecimal getBaseIce() {
		return baseIce;
	}

	public void setBaseIce(BigDecimal baseIce) {
		this.baseIce = baseIce;
	}

	public BigDecimal getBaseIrbp() {
		return baseIrbp;
	}

	public void setBaseIrbp(BigDecimal baseIrbp) {
		this.baseIrbp = baseIrbp;
	}

	public BigDecimal getIce() {
		return ice;
	}

	public void setIce(BigDecimal ice) {
		this.ice = ice;
	}

	public BigDecimal getIrbp() {
		return irbp;
	}

	public void setIrbp(BigDecimal irbp) {
		this.irbp = irbp;
	}

	public BigDecimal getCantidadTotal() {
		return cantidadTotal;
	}

	public void setCantidadTotal(BigDecimal cantidadTotal) {
		this.cantidadTotal = cantidadTotal;
	}
	
	
	
	 
	
	
	
	  

	
//	public boolean isTraducido() {
//		return traducido;
//	}
//
//	public void setTraducido(boolean traducido) {
//		this.traducido = traducido;
//	}
	
	// public String getModoLectura() {
	// return modoLectura;
	// }
	//
	// public void setModoLectura(String modoLectura) {
	// this.modoLectura = modoLectura;
	// }

	
}
