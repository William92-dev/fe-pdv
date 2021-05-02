package com.gizlocorp.gnvoice.xml.notadebito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "detalle", propOrder = { "motivoModificacion" })
public class Detalle {
	@XmlElement(required = true)
	protected String motivoModificacion;

	public String getMotivoModificacion() {
		return this.motivoModificacion;
	}

	public void setMotivoModificacion(String value) {
		this.motivoModificacion = value;
	}
}
