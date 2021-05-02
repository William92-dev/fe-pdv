package com.gizlocorp.gnvoice.xml.guia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "destinatario", propOrder = { "identificacionDestinatario",
		"razonSocialDestinatario", "dirDestinatario", "motivoTraslado",
		"docAduaneroUnico", "codEstabDestino", "ruta", "codDocSustento",
		"numDocSustento", "numAutDocSustento", "fechaEmisionDocSustento",
		"detalles" })
public class Destinatario  implements Serializable {
	protected String identificacionDestinatario;
	@XmlElement(required = true)
	protected String razonSocialDestinatario;
	@XmlElement(required = true)
	protected String dirDestinatario;
	@XmlElement(required = true)
	protected String motivoTraslado;
	protected String docAduaneroUnico;
	protected String codEstabDestino;
	protected String ruta;
	protected String codDocSustento;
	protected String numDocSustento;
	protected String numAutDocSustento;
	protected String fechaEmisionDocSustento;
	@XmlElement(required = true)
	protected Detalles detalles;

	public String getIdentificacionDestinatario() {
		return this.identificacionDestinatario;
	}

	public void setIdentificacionDestinatario(String value) {
		this.identificacionDestinatario = value;
	}

	public String getRazonSocialDestinatario() {
		return this.razonSocialDestinatario;
	}

	public void setRazonSocialDestinatario(String value) {
		this.razonSocialDestinatario = value;
	}

	public String getDirDestinatario() {
		return this.dirDestinatario;
	}

	public void setDirDestinatario(String value) {
		this.dirDestinatario = value;
	}

	public String getMotivoTraslado() {
		return this.motivoTraslado;
	}

	public void setMotivoTraslado(String value) {
		this.motivoTraslado = value;
	}

	public String getDocAduaneroUnico() {
		return this.docAduaneroUnico;
	}

	public void setDocAduaneroUnico(String value) {
		this.docAduaneroUnico = value;
	}

	public String getCodEstabDestino() {
		return this.codEstabDestino;
	}

	public void setCodEstabDestino(String value) {
		this.codEstabDestino = value;
	}

	public String getRuta() {
		return this.ruta;
	}

	public void setRuta(String value) {
		this.ruta = value;
	}

	public String getCodDocSustento() {
		return this.codDocSustento;
	}

	public void setCodDocSustento(String value) {
		this.codDocSustento = value;
	}

	public String getNumDocSustento() {
		return this.numDocSustento;
	}

	public void setNumDocSustento(String value) {
		this.numDocSustento = value;
	}

	public String getNumAutDocSustento() {
		return this.numAutDocSustento;
	}

	public void setNumAutDocSustento(String value) {
		this.numAutDocSustento = value;
	}

	public String getFechaEmisionDocSustento() {
		return this.fechaEmisionDocSustento;
	}

	public void setFechaEmisionDocSustento(String value) {
		this.fechaEmisionDocSustento = value;
	}

	public Detalles getDetalles() {
		return this.detalles;
	}

	public void setDetalles(Detalles value) {
		this.detalles = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "detalle" })
	public static class Detalles  implements Serializable {
		@XmlElement(required = true)
		protected List<Detalle> detalle;

		public List<Detalle> getDetalle() {
			if (this.detalle == null) {
				this.detalle = new ArrayList();
			}
			return this.detalle;
		}
	}
}
