package com.gizlocorp.gnvoice.xml.notadebito;

import javax.xml.bind.annotation.XmlRegistry;

import com.gizlocorp.gnvoice.xml.InfoTributaria;

@XmlRegistry
public class ObjectFactory {
	public NotaDebito.InfoAdicional createNotaDebitoInfoAdicional() {
		return new NotaDebito.InfoAdicional();
	}

	public Impuesto createImpuesto() {
		return new Impuesto();
	}

	public NotaDebito.Motivos createNotaDebitoMotivos() {
		return new NotaDebito.Motivos();
	}

	public NotaDebito.InfoNotaDebito.Impuestos createNotaDebitoInfoNotaDebitoImpuestos() {
		return new NotaDebito.InfoNotaDebito.Impuestos();
	}

	public NotaDebito.InfoNotaDebito createNotaDebitoInfoNotaDebito() {
		return new NotaDebito.InfoNotaDebito();
	}

	public NotaDebito.InfoAdicional.CampoAdicional createNotaDebitoInfoAdicionalCampoAdicional() {
		return new NotaDebito.InfoAdicional.CampoAdicional();
	}

	public Detalle createDetalle() {
		return new Detalle();
	}

	public InfoTributaria createInfoTributaria() {
		return new InfoTributaria();
	}

	public NotaDebito createNotaDebito() {
		return new NotaDebito();
	}

	public NotaDebito.Motivos.Motivo createNotaDebitoMotivosMotivo() {
		return new NotaDebito.Motivos.Motivo();
	}
}
