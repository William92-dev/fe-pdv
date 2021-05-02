package com.gizlocorp.gnvoice.xml;

import java.io.Serializable;
import java.sql.Date;

public class Respuesta  implements Serializable {
	private Integer codigo;
	private String claveDeAcceso;
	private String archivo;
	private String estado;
	private Date fecha;

	public Respuesta() {
	}

	public Respuesta(Integer codigo, String claveDeAcceso, String archivo,
			String estado, Date fecha) {
		/* 29 */this.codigo = codigo;
		/* 30 */this.claveDeAcceso = claveDeAcceso;
		/* 31 */this.archivo = archivo;
		/* 32 */this.estado = estado;
		/* 33 */this.fecha = fecha;
	}

	public String getArchivo() {
		/* 37 */return this.archivo;
	}

	public void setArchivo(String archivo) {
		/* 41 */this.archivo = archivo;
	}

	public String getClaveDeAcceso() {
		/* 45 */return this.claveDeAcceso;
	}

	public void setClaveDeAcceso(String claveDeAcceso) {
		/* 49 */this.claveDeAcceso = claveDeAcceso;
	}

	public Integer getCodigo() {
		/* 53 */return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		/* 57 */this.codigo = codigo;
	}

	public String getEstado() {
		/* 61 */return this.estado;
	}

	public void setEstado(String estado) {
		/* 65 */this.estado = estado;
	}

	public Date getFecha() {
		/* 69 */return this.fecha;
	}

	public void setFecha(Date fecha) {
		/* 73 */this.fecha = fecha;
	}
}

// Respuesta

