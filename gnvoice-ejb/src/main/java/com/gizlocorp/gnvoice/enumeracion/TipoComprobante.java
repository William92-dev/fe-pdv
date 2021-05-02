package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

public enum TipoComprobante implements Identificable<TipoComprobante> {

	FACTURA("factura", "Factura"), GUIA("guia", "Guia de Remision"), RETENCION(
			"retencion", "Comprobante de Retencion"), NOTA_CREDITO(
			"notaCredito", "Nota de Credito"), NOTA_DEBITO("noteDebito",
			"Note de Debito"),ELIMINAR_FACTURA("eliminarfactura", "Eliminar Factura");

	private String descripcion;
	private String etiqueta;

	private TipoComprobante(final String descripcion, final String etiqueta) {
		this.descripcion = descripcion;
		this.etiqueta = etiqueta;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	/**
	 * @return EstadoEnum
	 */
	@Override
	public TipoComprobante getIdentificador() {
		return this;
	}
}
