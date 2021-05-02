package com.gizlocorp.gnvoiceFybeca.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the Factura database table.
 * 
 */
// @Entity
public class Respuesta implements Serializable {
	private static final long serialVersionUID = 1L;

	// @Id
	// //@SequenceGenerator(name = "seq_F5502FA", sequenceName = "seq_F5502FA",
	// allocationSize = 1)
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "seq_F5502FA")
	@Column(name = "NUMERO_INTERNO")
	private Long id;

	@Column(name = "FARMACIA")
	private Long idFarmacia;

	@Column(name = "DOCUMENTO")
	private Long idDocumento;

	@Column(name = "TIPO_COMPROBANTE")
	private Long tipoComprobante;

	@Column(name = "FECHA")
	@Temporal(TemporalType.DATE)
	private Date fecha;

	@Column(name = "USUARIO")
	private String usuario;

	@Column(name = "FIRMAE", length = 1)
	private String firmaE;

	@Column(name = "AUT_FIRMAE", length = 500)
	private String autFirmaE;

	@Column(name = "FECHA_FIRMAE")
	@Temporal(TemporalType.DATE)
	private Date fechaFirmaE;

	@Column(name = "COMPROBANTE_FIRMAE", length = 20)
	private String comprobanteFirmaE;

	@Column(name = "CLAVE_ACCESO", length = 50)
	private String claveAcceso;

	@Column(name = "OBSERVACION_ELEC")
	private String observacion;

	@Column(name = "CORREO_ELECTRONICO", length = 100)
	private String correoElectronico;

	@Column(name = "ESTADO", length = 1)
	private String estado;

	@Column(name = "TIPO_PROCESO", length = 20)
	private String tipoProceso;
	
	private String sid;

	public Respuesta() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdFarmacia() {
		return idFarmacia;
	}

	public void setIdFarmacia(Long idFarmacia) {
		this.idFarmacia = idFarmacia;
	}

	public Long getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}

	public Long getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(Long tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getFirmaE() {
		return firmaE;
	}

	public void setFirmaE(String firmaE) {
		this.firmaE = firmaE;
	}

	public String getAutFirmaE() {
		return autFirmaE;
	}

	public void setAutFirmaE(String autFirmaE) {
		this.autFirmaE = autFirmaE;
	}

	public Date getFechaFirmaE() {
		return fechaFirmaE;
	}

	public void setFechaFirmaE(Date fechaFirmaE) {
		this.fechaFirmaE = fechaFirmaE;
	}

	public String getComprobanteFirmaE() {
		return comprobanteFirmaE;
	}

	public void setComprobanteFirmaE(String comprobanteFirmaE) {
		this.comprobanteFirmaE = comprobanteFirmaE;
	}

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getTipoProceso() {
		return tipoProceso;
	}

	public void setTipoProceso(String tipoProceso) {
		this.tipoProceso = tipoProceso;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

}