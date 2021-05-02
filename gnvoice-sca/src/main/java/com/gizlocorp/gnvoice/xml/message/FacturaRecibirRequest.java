package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "facturaRecibirRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class FacturaRecibirRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String comprobanteProveedor;
	private String comprobanteProveedorPDF;
	private String proceso;
	private String tipo;
	private String ordenCompra;
	private String mailOrigen;
	
	public String getComprobanteProveedorPDF() {
		return comprobanteProveedorPDF;
	}

	public void setComprobanteProveedorPDF(String comprobanteProveedorPDF) {
		this.comprobanteProveedorPDF = comprobanteProveedorPDF;
	}

	public String getComprobanteProveedor() {
		return comprobanteProveedor;
	}

	public void setComprobanteProveedor(String comprobanteProveedor) {
		this.comprobanteProveedor = comprobanteProveedor;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(String ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	public String getMailOrigen() {
		return mailOrigen;
	}

	public void setMailOrigen(String mailOrigen) {
		this.mailOrigen = mailOrigen;
	}

}
