package com.gizlocorp.gnvoice.xml;

import java.io.Serializable;
import java.math.BigDecimal;

public class TotalImpuesto implements Serializable {
	Integer codigo;
	String codigoPorcentaje;
	BigDecimal baseImponible;
	BigDecimal valor;

	public BigDecimal getBaseImponible() {
		/* 23 */return this.baseImponible;
	}

	public void setBaseImponible(BigDecimal baseImponible) {
		/* 27 */this.baseImponible = baseImponible;
	}

	public Integer getCodigo() {
		/* 31 */return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		/* 35 */this.codigo = codigo;
	}

	public String getCodigoPorcentaje() {
		/* 39 */return this.codigoPorcentaje;
	}

	public void setCodigoPorcentaje(String codigoPorcentaje) {
		/* 43 */this.codigoPorcentaje = codigoPorcentaje;
	}

	public BigDecimal getValor() {
		/* 47 */return this.valor;
	}

	public void setValor(BigDecimal valor) {
		/* 51 */this.valor = valor;
	}
}

// TotalImpuesto

