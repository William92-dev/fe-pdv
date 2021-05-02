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
public class Contacto implements Serializable {
	private static final long serialVersionUID = 1L;

	// @Id
	// //@SequenceGenerator(name = "seq_F5502FA", sequenceName = "seq_F5502FA",
	// allocationSize = 1)
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "seq_F5502FA")
	@Column(name = "PERSONA")
	private Long persona;

	@Column(name = "CODIGO")
	private Long codigo;

	@Column(name = "VALOR")
	private String valor;

	@Column(name = "DIRECCION")
	private Long direccion;

	@Column(name = "DRN_PERSONA")
	private Long drnPersona;

	@Column(name = "TIPO")
	private Long tipo;

	@Column(name = "FECHA_CREACION")
	@Temporal(TemporalType.DATE)
	private Date fechaCreacion;

	@Column(name = "USUARIO_CREA")
	private String usuarioCrea;

	@Column(name = "FECHA_ACTUALIZACION")
	@Temporal(TemporalType.DATE)
	private Date fechaActualizacion;

	@Column(name = "USUARIO_ACTUALIZA")
	private String usuarioActualiza;

	public Contacto() {
	}

	public Long getPersona() {
		return persona;
	}

	public void setPersona(Long persona) {
		this.persona = persona;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Long getDrnPersona() {
		return drnPersona;
	}

	public void setDrnPersona(Long drnPersona) {
		this.drnPersona = drnPersona;
	}

	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getUsuarioCrea() {
		return usuarioCrea;
	}

	public void setUsuarioCrea(String usuarioCrea) {
		this.usuarioCrea = usuarioCrea;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public String getUsuarioActualiza() {
		return usuarioActualiza;
	}

	public void setUsuarioActualiza(String usuarioActualiza) {
		this.usuarioActualiza = usuarioActualiza;
	}

	public Long getDireccion() {
		return direccion;
	}

	public void setDireccion(Long direccion) {
		this.direccion = direccion;
	}
}