package com.gizlocorp.gnvoice.xml;

public class InformacionAdicionalProducto {
	private String atributo;
	private String valor;
	private Integer codigoProducto;
	private Integer codigo;

	public InformacionAdicionalProducto() {
	}

	public InformacionAdicionalProducto(String atributo, String valor) {
		/* 21 */this.atributo = atributo;
		/* 22 */this.valor = valor;
	}

	public String getAtributo() {
		/* 29 */return this.atributo;
	}

	public void setAtributo(String atributo) {
		/* 36 */this.atributo = atributo;
	}

	public String getValor() {
		/* 43 */return this.valor;
	}

	public void setValor(String valor) {
		/* 50 */this.valor = valor;
	}

	public Integer getCodigoProducto() {
		/* 57 */return this.codigoProducto;
	}

	public void setCodigoProducto(Integer codigoProducto) {
		/* 64 */this.codigoProducto = codigoProducto;
	}

	public Integer getCodigo() {
		/* 71 */return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		/* 78 */this.codigo = codigo;
	}
}

// InformacionAdicionalProducto

