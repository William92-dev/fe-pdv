package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.gizlocorp.gnvoice.xml.factura.Factura;

/**
 * Mensaje de Entrada del factura
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "facturaReceptarRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class FacturaReceptarRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Factura factura;
	private String codigoExternoComprobante;
	private String correoElectronicoNotificacion;
	private String identificadorUsuario;
	private String codigoAgencia;
	private Boolean notificacion;
	
	
	
	public String getCodigoExternoComprobante() {
		return codigoExternoComprobante;
	}
	public void setCodigoExternoComprobante(String codigoExternoComprobante) {
		this.codigoExternoComprobante = codigoExternoComprobante;
	}
	public String getCorreoElectronicoNotificacion() {
		return correoElectronicoNotificacion;
	}
	public void setCorreoElectronicoNotificacion(
			String correoElectronicoNotificacion) {
		this.correoElectronicoNotificacion = correoElectronicoNotificacion;
	}
	public String getIdentificadorUsuario() {
		return identificadorUsuario;
	}
	public void setIdentificadorUsuario(String identificadorUsuario) {
		this.identificadorUsuario = identificadorUsuario;
	}
	public String getCodigoAgencia() {
		return codigoAgencia;
	}
	public void setCodigoAgencia(String codigoAgencia) {
		this.codigoAgencia = codigoAgencia;
	}
	public Factura getFactura() {
		return factura;
	}
	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	public Boolean getNotificacion() {
		return notificacion;
	}
	public void setNotificacion(Boolean notificacion) {
		this.notificacion = notificacion;
	}
	
}
