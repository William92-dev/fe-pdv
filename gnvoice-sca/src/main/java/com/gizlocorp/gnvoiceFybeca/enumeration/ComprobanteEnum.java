package com.gizlocorp.gnvoiceFybeca.enumeration;

public enum ComprobanteEnum {

	FACTURA("FACTURA"), NOTA_CREDITO("NOTA DE CRÉDITO"), NOTA_DEBITO(
			"NOTA DE DÉBITO"), GUIA_REMISION("GUIA DE REMISION"), RETENCION(
			"COMPROBANTE DE RETENCION");

	private String descripcion;

	private ComprobanteEnum(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

}
