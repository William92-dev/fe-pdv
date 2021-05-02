package com.gizlocorp.gnvoice.enumeracion;

import com.gizlocorp.adm.utilitario.Identificable;

public enum Tarea implements Identificable<Tarea> {

	GEN("Generar Comprobante"), FIR("Firmar Comprobante"), ENV(
			"Enviar Comprobante"), AUT("Autorizar Comprobante"), CON(
			"Contigencia"), NOT("Notificar Cliente/Proveedor"), ASL(
			"Asignar Lote"), REP("Receptar Comprobante");

	private String descripcion;

	private Tarea(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

	/**
	 * @return EstadoEnum
	 */
	@Override
	public Tarea getIdentificador() {
		return this;
	}
}