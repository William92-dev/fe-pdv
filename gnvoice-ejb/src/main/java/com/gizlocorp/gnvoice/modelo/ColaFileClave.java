package com.gizlocorp.gnvoice.modelo;

import java.io.Serializable;
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
@Table(name = "tb_cola_file_clave")
public class ColaFileClave implements Serializable {

	private static final long serialVersionUID = 3639413639314219254L;
	
	@Id
	@SequenceGenerator(name = "seq_cola_file_clave", sequenceName = "seq_cola_file_clave", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cola_file_clave")
	@Column(name = "id")
	private Long id;
	
	
	@Column(name = "nombre")
	private String nombre;
	
	
	@Column(name = "ruta")
	private String ruta;
	
	
	@Column(name = "estado")
	private String estado;
	
	
	@Column(name = "descripcion")
	private String descripcion;
	
	
	@Column(name = "fechaRegistro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaRegistro;
	
	
	@Column(name = "fechaProceso")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaProceso;
	
	
	@Column(name = "userName")
	private String userName;
	
	@Column(name = "persona")
	private String persona;
	
	
	@Column(name = "tipoIngreso")
	private String tipoIngreso;
	
	
	public ColaFileClave() {
		// TODO Auto-generated constructor stub
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getRuta() {
		return ruta;
	}


	public void setRuta(String ruta) {
		this.ruta = ruta;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

    
	
	public Date getFechaRegistro() {
		return fechaRegistro;
	}


	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	
	

	public Date getFechaProceso() {
		return fechaProceso;
	}


	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPersona() {
		return persona;
	}


	public void setPersona(String persona) {
		this.persona = persona;
	}


	public String getTipoIngreso() {
		return tipoIngreso;
	}


	public void setTipoIngreso(String tipoIngreso) {
		this.tipoIngreso = tipoIngreso;
	}


	@Override
	public String toString() {
		return "ColaFileClave [id=" + id + ", nombre=" + nombre + ", ruta=" + ruta + ", estado=" + estado
				+ ", descripcion=" + descripcion + ", fechaRegistro=" + fechaRegistro + ", fechaProceso=" + fechaProceso
				+ ", userName=" + userName + ", persona=" + persona + ", tipoIngreso=" + tipoIngreso + "]";
	}


	


	
   
	

}
