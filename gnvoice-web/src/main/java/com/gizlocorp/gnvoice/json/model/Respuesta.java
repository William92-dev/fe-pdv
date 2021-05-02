/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gizlocorp.gnvoice.json.model;

//import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author GIZLO
 */
public class Respuesta {
	@JsonProperty("codigo")
	private String codigo;
	@JsonProperty("fechaNacimiento")
	private String fechaNacimiento;
	@JsonProperty("identificacion")
	private String identificacion;
	@JsonProperty("mail")
	private String mail;
	@JsonProperty("primerApellido")
	private String primerApellido;
	@JsonProperty("primerNombre")
	private String primerNombre;
	@JsonProperty("razonSocial")
	private String razonSocial;
	@JsonProperty("segundoApellido")
	private String segundoApellido;
	@JsonProperty("segundoNombre")
	private String segundoNombre;
	@JsonProperty("tipoIdentificacion")
	private String tipoIdentificacion;
	@JsonProperty("tipoPersona")
	private String tipoPersona;

	public Respuesta() {
	}

	public Respuesta(String codigo, String fechaNacimiento,
			String identificacion, String mail, String primerApellido,
			String primerNombre, String razonSocial, String segundoApellido,
			String segundoNombre, String tipoIdentificacion, String tipoPersona) {
		this.codigo = codigo;
		this.fechaNacimiento = fechaNacimiento;
		this.identificacion = identificacion;
		this.mail = mail;
		this.primerApellido = primerApellido;
		this.primerNombre = primerNombre;
		this.razonSocial = razonSocial;
		this.segundoApellido = segundoApellido;
		this.segundoNombre = segundoNombre;
		this.tipoIdentificacion = tipoIdentificacion;
		this.tipoPersona = tipoPersona;
	}

	

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido;
	}

	public String getPrimerNombre() {
		return primerNombre;
	}

	public void setPrimerNombre(String primerNombre) {
		this.primerNombre = primerNombre;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
	}

	public String getSegundoNombre() {
		return segundoNombre;
	}

	public void setSegundoNombre(String segundoNombre) {
		this.segundoNombre = segundoNombre;
	}

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public String getTipoPersona() {
		return tipoPersona;
	}

	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

	@Override
	public String toString() {
		return "codigo = " + codigo + " primerNombre = " + primerNombre;
	}
}
