package com.gizlocorp.gnvoice.xml.message;

import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
public class ComprobanteRequest {

	private TipoComprobante comprobante;

	private String claveAcceso;

	private String claveFirma;

	private String rutaArchivo;

	private String rutaFirma;
	
	private String correoElectronicoNotificacion;

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	public String getClaveFirma() {
		return claveFirma;
	}

	public void setClaveFirma(String claveFirma) {
		this.claveFirma = claveFirma;
	}

	public String getRutaArchivo() {
		return rutaArchivo;
	}

	public void setRutaArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}

	public String getRutaFirma() {
		return rutaFirma;
	}

	public void setRutaFirma(String rutaFirma) {
		this.rutaFirma = rutaFirma;
	}

	public TipoComprobante getComprobante() {
		return comprobante;
	}

	public void setComprobante(TipoComprobante comprobante) {
		this.comprobante = comprobante;
	}

	public String getCorreoElectronicoNotificacion() {
		return correoElectronicoNotificacion;
	}

	public void setCorreoElectronicoNotificacion(
			String correoElectronicoNotificacion) {
		this.correoElectronicoNotificacion = correoElectronicoNotificacion;
	}

}
