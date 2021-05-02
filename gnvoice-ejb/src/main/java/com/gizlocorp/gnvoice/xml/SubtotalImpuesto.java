package com.gizlocorp.gnvoice.xml;

import java.io.Serializable;
import java.math.BigDecimal;

public class SubtotalImpuesto  implements Serializable {
	private String codigoProducto;
	private Integer codigoImpuesto;
	private String codigo;
	private BigDecimal porcentaje;
	private BigDecimal baseImponible;
	private BigDecimal subtotal;
	private BigDecimal valorIce;

	public BigDecimal getValorIce() {
		/* 28 */return this.valorIce;
	}

	public void setValorIce(BigDecimal valorIce) {
		/* 32 */this.valorIce = valorIce;
	}

	public SubtotalImpuesto(String codigoProducto, Integer codigoImpuesto,
			String codigo, BigDecimal porcentaje, BigDecimal baseImponible,
			BigDecimal subtotal) {
		/* 36 */this.codigoProducto = codigoProducto;
		/* 37 */this.codigoImpuesto = codigoImpuesto;
		/* 38 */this.codigo = codigo;
		/* 39 */this.porcentaje = porcentaje;
		/* 40 */this.baseImponible = baseImponible;
		/* 41 */this.subtotal = subtotal;
	}

	public String getCodigo() {
		/* 45 */return this.codigo;
	}

	public void setCodigo(String codigo) {
		/* 49 */this.codigo = codigo;
	}

	public BigDecimal getSubtotal() {
		/* 53 */return this.subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		/* 57 */this.subtotal = subtotal;
	}

	public Integer getCodigoImpuesto() {
		/* 61 */return this.codigoImpuesto;
	}

	public void setCodigoImpuesto(Integer codigoImpuesto) {
		/* 65 */this.codigoImpuesto = codigoImpuesto;
	}

	public String getCodigoProducto() {
		/* 69 */return this.codigoProducto;
	}

	public void setCodigoProducto(String codigoProducto) {
		/* 73 */this.codigoProducto = codigoProducto;
	}

	public BigDecimal getPorcentaje() {
		/* 77 */return this.porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		/* 81 */this.porcentaje = porcentaje;
	}

	public BigDecimal getBaseImponible() {
		/* 85 */return this.baseImponible;
	}

	public void setBaseImponible(BigDecimal baseImponible) {
		/* 89 */this.baseImponible = baseImponible;
	}
}

// SubtotalImpuesto

