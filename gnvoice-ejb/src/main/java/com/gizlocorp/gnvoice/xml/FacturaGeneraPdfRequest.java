package com.gizlocorp.gnvoice.xml;

import java.io.Serializable;

import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.gnvoice.xml.factura.Factura;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;


public class FacturaGeneraPdfRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Factura factura;
	private Autorizacion respAutorizacionComExt;
	private String logoEmpresa;
	private String dirServidor;
	private String correoElectronicoNotificacion;
	private Organizacion emisor;
	private String rutaArchivoAutorizacionXml;
	private Long facturaComprobante;
	
	
	public Factura getFactura() {
		return factura;
	}
	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	public Autorizacion getRespAutorizacionComExt() {
		return respAutorizacionComExt;
	}
	public void setRespAutorizacionComExt(Autorizacion respAutorizacionComExt) {
		this.respAutorizacionComExt = respAutorizacionComExt;
	}
	public String getLogoEmpresa() {
		return logoEmpresa;
	}
	public void setLogoEmpresa(String logoEmpresa) {
		this.logoEmpresa = logoEmpresa;
	}
	public String getDirServidor() {
		return dirServidor;
	}
	public void setDirServidor(String dirServidor) {
		this.dirServidor = dirServidor;
	}
	public String getCorreoElectronicoNotificacion() {
		return correoElectronicoNotificacion;
	}
	public void setCorreoElectronicoNotificacion(
			String correoElectronicoNotificacion) {
		this.correoElectronicoNotificacion = correoElectronicoNotificacion;
	}
	public Organizacion getEmisor() {
		return emisor;
	}
	public void setEmisor(Organizacion emisor) {
		this.emisor = emisor;
	}
	public String getRutaArchivoAutorizacionXml() {
		return rutaArchivoAutorizacionXml;
	}
	public void setRutaArchivoAutorizacionXml(String rutaArchivoAutorizacionXml) {
		this.rutaArchivoAutorizacionXml = rutaArchivoAutorizacionXml;
	}
	public Long getFacturaComprobante() {
		return facturaComprobante;
	}
	public void setFacturaComprobante(Long facturaComprobante) {
		this.facturaComprobante = facturaComprobante;
	}
	
	
	
	
	

}
