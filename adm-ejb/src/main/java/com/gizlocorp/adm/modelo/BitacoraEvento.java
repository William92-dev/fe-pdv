package com.gizlocorp.adm.modelo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "tb_bitacora_evento")
public class BitacoraEvento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_bitacora_evento", sequenceName = "seq_bitacora_evento", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_bitacora_evento")
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "evento")
	private Catalogo evento;

	@Column(name = "descripcion", length = 500)
	private String descripcion;

	@Column(name = "usuario", length = 200)
	private String usuario;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", nullable = true)
	private Date fecha;

	@ManyToOne()
	@JoinColumn(name = "id_organizacion")
	private Organizacion organizacion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Catalogo getEvento() {
		return evento;
	}

	public void setEvento(Catalogo evento) {
		this.evento = evento;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

	public String getFechaStr() {
		if (fecha != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			return df.format(fecha);
		}
		return "";
	}

}
