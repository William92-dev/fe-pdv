package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.wsclient.factura.GizloResponse;

@SessionScoped
@Named("receptarFacturaMailDataBean")
public class ReceptarFacturaMailDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<GizloResponse> respuestas;

	private TipoComprobante tipoComprobanteDes;
	
	private byte[] data;
	
	private String name;
	
	private String extension;

	

	public List<GizloResponse> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(List<GizloResponse> respuestas) {
		this.respuestas = respuestas;
	}

	public TipoComprobante getTipoComprobanteDes() {
		return tipoComprobanteDes;
	}

	public void setTipoComprobanteDes(TipoComprobante tipoComprobanteDes) {
		this.tipoComprobanteDes = tipoComprobanteDes;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	
}
