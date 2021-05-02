package com.gizlocorp.gnvoice.reporte;

import java.math.BigDecimal;

/**
 * 
 * @author gizlo
 */

public class TotalComprobante {
	private String subtotal12;
	private String subtotal0;
	private String subtotalNoSujetoIva;
	private BigDecimal subtotal;
	private String iva12;
	private String totalIce;
	private BigDecimal totalIRBPNR;
	private BigDecimal subtotalExentoIVA;
	private String subtotal14;
	private String iva14;

	public String getSubtotal12() {
		return this.subtotal12;
	}

	public void setSubtotal12(String subtotal12) {
		this.subtotal12 = subtotal12;
	}

	public String getSubtotal0() {
		return this.subtotal0;
	}

	public void setSubtotal0(String subtotal0) {
		this.subtotal0 = subtotal0;
	}

	public BigDecimal getSubtotal() {
		return this.subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public String getIva12() {
		return this.iva12;
	}

	public void setIva12(String iva12) {
		this.iva12 = iva12;
	}

	public String getTotalIce() {
		return this.totalIce;
	}

	public void setTotalIce(String totalIce) {
		this.totalIce = totalIce;
	}

	public String getSubtotalNoSujetoIva() {
		return this.subtotalNoSujetoIva;
	}

	public void setSubtotalNoSujetoIva(String subtotalNoSujetoIva) {
		this.subtotalNoSujetoIva = subtotalNoSujetoIva;
	}

	public BigDecimal getTotalIRBPNR() {
		return this.totalIRBPNR;
	}

	public void setTotalIRBPNR(BigDecimal totalIRBPNR) {
		this.totalIRBPNR = totalIRBPNR;
	}

	public BigDecimal getSubtotalExentoIVA() {
		return this.subtotalExentoIVA;
	}

	public void setSubtotalExentoIVA(BigDecimal subtotalExentoIVA) {
		this.subtotalExentoIVA = subtotalExentoIVA;
	}

	public String getSubtotal14() {
		return subtotal14;
	}

	public void setSubtotal14(String subtotal14) {
		this.subtotal14 = subtotal14;
	}

	public String getIva14() {
		return iva14;
	}

	public void setIva14(String iva14) {
		this.iva14 = iva14;
	}
}
