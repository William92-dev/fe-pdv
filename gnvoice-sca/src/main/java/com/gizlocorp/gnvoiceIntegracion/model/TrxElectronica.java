package com.gizlocorp.gnvoiceIntegracion.model;

import java.util.Date;




public class TrxElectronica {
	
	private Long id; 
	private String compania; 
	private String tipoComprobante; 
	private String numeroComprobante;
	private String tipoTransaccion; 
	private String estado;
	private String tipoMensaje;
	  //private String MENSAJE CLOB, 
	  //INFORMACION_ADICIONAL CLOB, 
	private String claveAccesso; 
	private String autorizacion; 
	private String fechaAutorizacion; 
	private String ambiente;
	//private String xml;
	  //RIDE BLOB, 
	private Date fecha; 
	private String rucReceptor;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompania() {
		return compania;
	}
	public void setCompania(String compania) {
		this.compania = compania;
	}
	public String getTipoComprobante() {
		return tipoComprobante;
	}
	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	public String getNumeroComprobante() {
		return numeroComprobante;
	}
	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}
	public String getTipoTransaccion() {
		return tipoTransaccion;
	}
	public void setTipoTransaccion(String tipoTransaccion) {
		this.tipoTransaccion = tipoTransaccion;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getTipoMensaje() {
		return tipoMensaje;
	}
	public void setTipoMensaje(String tipoMensaje) {
		this.tipoMensaje = tipoMensaje;
	}
	public String getClaveAccesso() {
		return claveAccesso;
	}
	public void setClaveAccesso(String claveAccesso) {
		this.claveAccesso = claveAccesso;
	}
	public String getAutorizacion() {
		return autorizacion;
	}
	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}
	public String getFechaAutorizacion() {
		return fechaAutorizacion;
	}
	public void setFechaAutorizacion(String fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}
	public String getAmbiente() {
		return ambiente;
	}
	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}
	/*public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}*/
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getRucReceptor() {
		return rucReceptor;
	}
	public void setRucReceptor(String rucReceptor) {
		this.rucReceptor = rucReceptor;
	}
	  
	  
	  

}
