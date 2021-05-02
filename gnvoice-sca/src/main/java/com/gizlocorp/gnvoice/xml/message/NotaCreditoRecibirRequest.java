package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "notaCreditoRecibirRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class NotaCreditoRecibirRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String comprobanteProveedor;
	private String comprobanteProveedorPdf;
	private String proceso;
	private String tipo;
	private String ordenCompra;
	private String mailOrigen;

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

	public String getComprobanteProveedorPdf() {
		return comprobanteProveedorPdf;
	}

	public void setComprobanteProveedorPdf(String comprobanteProveedorPdf) {
		this.comprobanteProveedorPdf = comprobanteProveedorPdf;
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
