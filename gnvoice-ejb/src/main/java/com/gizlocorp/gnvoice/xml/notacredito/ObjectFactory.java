package com.gizlocorp.gnvoice.xml.notacredito;

import javax.xml.bind.annotation.XmlRegistry;

import com.gizlocorp.gnvoice.xml.InfoTributaria;

@XmlRegistry
public class ObjectFactory {
	public NotaCredito createNotaCredito() {
		return new NotaCredito();
	}

	public Detalle createDetalle() {
		return new Detalle();
	}

	public TotalConImpuestos createTotalConImpuestos() {
		return new TotalConImpuestos();
	}

	public TotalConImpuestos.TotalImpuesto createTotalConImpuestosTotalImpuesto() {
		return new TotalConImpuestos.TotalImpuesto();
	}

	public NotaCredito.InfoNotaCredito createNotaCreditoInfoNotaCredito() {
		return new NotaCredito.InfoNotaCredito();
	}

	public NotaCredito.InfoAdicional.CampoAdicional createNotaCreditoInfoAdicionalCampoAdicional() {
		return new NotaCredito.InfoAdicional.CampoAdicional();
	}

	public NotaCredito.Detalles createNotaCreditoDetalles() {
		return new NotaCredito.Detalles();
	}

	public InfoTributaria createInfoTributaria() {
		return new InfoTributaria();
	}

	public Impuesto createImpuesto() {
		return new Impuesto();
	}

	public NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional createNotaCreditoDetallesDetalleDetallesAdicionalesDetAdicional() {
		return new NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional();
	}

	public NotaCredito.Detalles.Detalle createNotaCreditoDetallesDetalle() {
		return new NotaCredito.Detalles.Detalle();
	}

	public NotaCredito.Detalles.Detalle.DetallesAdicionales createNotaCreditoDetallesDetalleDetallesAdicionales() {
		return new NotaCredito.Detalles.Detalle.DetallesAdicionales();
	}

	public NotaCredito.InfoAdicional createNotaCreditoInfoAdicional() {
		return new NotaCredito.InfoAdicional();
	}

	public NotaCredito.Detalles.Detalle.Impuestos createNotaCreditoDetallesDetalleImpuestos() {
		return new NotaCredito.Detalles.Detalle.Impuestos();
	}
}
