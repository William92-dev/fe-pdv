package com.gizlocorp.firmaelectronica.xml.message;

import java.io.Serializable;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
public class FirmaElectronicaRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rutaArchivo;
	private String rutaFirma;
	private String clave;
	private Boolean lote;

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

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Boolean getLote() {
		return lote;
	}

	public void setLote(Boolean lote) {
		this.lote = lote;
	}
}
