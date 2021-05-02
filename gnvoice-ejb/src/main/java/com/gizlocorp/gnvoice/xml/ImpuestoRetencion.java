package com.gizlocorp.gnvoice.xml;

import java.math.BigDecimal;
import java.util.List;

public class ImpuestoRetencion {
	private Integer codigo;
	private String codigoRetencion;
	private BigDecimal baseImponible;
	private BigDecimal porcentajeRetener;
	private BigDecimal valorRetenido;
	private String codDocSustento;
	private String numDocSustento;
	private String fechaEmisionDocSustento;
	private String nombreImpuesto;
	private List<CampoAdicional> camposAdicionales;

	public ImpuestoRetencion(Integer codigo, String codigoRetencion,
			BigDecimal porcentajeRetener, BigDecimal baseImponible,
			BigDecimal valorRetenido, String codDocSustento,
			String numDocSustento, String fechaEmisionDocSustento) {
		/* 31 */this.codigo = codigo;
		/* 32 */this.codigoRetencion = codigoRetencion;
		/* 33 */this.porcentajeRetener = porcentajeRetener;
		/* 34 */this.baseImponible = baseImponible;
		/* 35 */this.valorRetenido = valorRetenido;
		/* 36 */this.codDocSustento = codDocSustento;
		/* 37 */this.numDocSustento = numDocSustento;
		/* 38 */this.fechaEmisionDocSustento = fechaEmisionDocSustento;
	}

	public String getNombreImpuesto() {
		/* 45 */return this.nombreImpuesto;
	}

	public void setNombreImpuesto(String nombreImpuesto) {
		/* 52 */this.nombreImpuesto = nombreImpuesto;
	}

	public Integer getCodigo() {
		/* 59 */return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		/* 66 */this.codigo = codigo;
	}

	public String getCodigoRetencion() {
		/* 73 */return this.codigoRetencion;
	}

	public void setCodigoRetencion(String codigoRetencion) {
		/* 80 */this.codigoRetencion = codigoRetencion;
	}

	public BigDecimal getPorcentajeRetener() {
		/* 87 */return this.porcentajeRetener;
	}

	public void setPorcentajeRetener(BigDecimal porcentajeRetener) {
		/* 94 */this.porcentajeRetener = porcentajeRetener;
	}

	public BigDecimal getBaseImponible() {
		/* 101 */return this.baseImponible;
	}

	public void setBaseImponible(BigDecimal baseImponible) {
		/* 108 */this.baseImponible = baseImponible;
	}

	public BigDecimal getValorRetenido() {
		/* 115 */return this.valorRetenido;
	}

	public void setValorRetenido(BigDecimal valorRetenido) {
		/* 122 */this.valorRetenido = valorRetenido;
	}

	public String getCodDocSustento() {
		/* 129 */return this.codDocSustento;
	}

	public void setCodDocSustento(String codDocSustento) {
		/* 136 */this.codDocSustento = codDocSustento;
	}

	public String getNumDocSustento() {
		/* 143 */return this.numDocSustento;
	}

	public void setNumDocSustento(String numDocSustento) {
		/* 150 */this.numDocSustento = numDocSustento;
	}

	public String getFechaEmisionDocSustento() {
		/* 157 */return this.fechaEmisionDocSustento;
	}

	public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
		/* 164 */this.fechaEmisionDocSustento = fechaEmisionDocSustento;
	}

	public List<CampoAdicional> getCamposAdicionales() {
		/* 171 */return this.camposAdicionales;
	}

	public void setCamposAdicionales(List<CampoAdicional> camposAdicionales) {
		/* 178 */this.camposAdicionales = camposAdicionales;
	}
}

// ImpuestoRetencion

