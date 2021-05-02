package com.gizlocorp.gnvoice.xml.message;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.gizlocorp.gnvoice.xml.factura.Factura;

/**
 * Mensaje de Salida del
 * 
 * @author Usuario
 * 
 */
@XmlRootElement(name = "facturaConsultarResponse", namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
public class FacturaConsultarResponse extends GizloResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Factura> facturas;
	

	@XmlElementWrapper(name = "facturas")
	@XmlElement(name = "factura")
	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}

}
