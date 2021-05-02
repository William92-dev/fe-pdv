package com.gizlocorp.adm.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "tb_sistema_externo")
public class SistemaExterno {
	
	@Id
	@SequenceGenerator(name = "seq_sis_externo", sequenceName = "seq_sis_externo", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sis_externo")
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "usuario_sistema", length = 100, nullable = false)
	private String usuarioSistema;
	
	@Column(name = "clave", nullable = false)
	private String clave;
	
	@Column(name = "usuario_aplicacion", length = 100, nullable = false)
	private String usuarioAplicacion;
	
	@Column(name = "fecha")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsuarioSistema() {
		return usuarioSistema;
	}

	public void setUsuarioSistema(String usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getUsuarioAplicacion() {
		return usuarioAplicacion;
	}

	public void setUsuarioAplicacion(String usuarioAplicacion) {
		this.usuarioAplicacion = usuarioAplicacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	

}
