package com.gizlocorp.adm.modelo;

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

import com.gizlocorp.adm.enumeracion.Estado;


@Entity
@Table(name = "tb_par_codigo_barras")
public class ParametrizacionCodigoBarras implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seq_par_codigo_barras", sequenceName = "seq_par_codigo_barras", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_par_codigo_barras")
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "codigo_articulo", nullable = false)
	private String codigoArticulo;
	
	@Column(name = "tipo_articulo", nullable = false)
	private String tipoArticulo;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "estado", length = 3)
	private Estado estado;
	
	@Column(name = "ruc", nullable = false)
	private String ruc;
	
	@Column(name = "fecha_registro", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaRegistro;
	
	@Column(name = "usuario_registro", nullable = true)
	private String usuarioRegistro;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigoArticulo() {
		return codigoArticulo;
	}

	public void setCodigoArticulo(String codigoArticulo) {
		this.codigoArticulo = codigoArticulo;
	}

	public String getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	
	
}
