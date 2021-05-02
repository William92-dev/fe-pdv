
package com.gizlocorp.gnvoice.wsclient.notacredito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notaCreditoRecibirRequest complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notaCreditoRecibirRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="comprobanteProveedor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notaCreditoRecibirRequest", propOrder = {
    "comprobanteProveedor", "comprobanteProveedorPDF", "proceso", "tipo", "ordenCompra", "mailOrigen"
})
public class NotaCreditoRecibirRequest {

    protected String comprobanteProveedor;
    protected String comprobanteProveedorPDF;
	protected String proceso;
	protected String tipo;
	protected String ordenCompra;
	protected String mailOrigen;

    /**
     * Obtiene el valor de la propiedad comprobanteProveedor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComprobanteProveedor() {
        return comprobanteProveedor;
    }

    /**
     * Define el valor de la propiedad comprobanteProveedor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComprobanteProveedor(String value) {
        this.comprobanteProveedor = value;
    }

	public String getComprobanteProveedorPDF() {
		return comprobanteProveedorPDF;
	}

	public void setComprobanteProveedorPDF(String comprobanteProveedorPDF) {
		this.comprobanteProveedorPDF = comprobanteProveedorPDF;
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
