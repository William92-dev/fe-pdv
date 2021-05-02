package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mensaje de Entrada del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "facturaConsultarRequest", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class FacturaConsultarRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Factura factura;
	private String ruc;
	private String claveAcceso;
	private String secuencial;
	private String codigoExterno;
	private String estado;
	
	private Integer pagina;
	private Integer tamanoPagina;
	
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getClaveAcceso() {
		return claveAcceso;
	}
	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}
	public String getSecuencial() {
		return secuencial;
	}
	public void setSecuencial(String secuencial) {
		this.secuencial = secuencial;
	}
	public String getCodigoExterno() {
		return codigoExterno;
	}
	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Integer getPagina() {
		return pagina;
	}
	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}
	public Integer getTamanoPagina() {
		return tamanoPagina;
	}
	public void setTamanoPagina(Integer tamanoPagina) {
		this.tamanoPagina = tamanoPagina;
	}
	
}
