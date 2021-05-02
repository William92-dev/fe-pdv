package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.utilitario.RimEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.wsclient.factura.GizloResponse;

@SessionScoped
@Named("receptarComprobanteDataBean")
public class ReceptarComprobanteDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<GizloResponse> respuestas;

	private TipoComprobante tipoComprobanteDes;
	
	private byte[] data;
	
	private String name;
	
	private byte[] dataMasivo;
	
	private String nameMasivo;
	
	private byte[] dataPdf;
	
	private String namePdf;
	
	private RimEnum proceso;

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

	public byte[] getDataMasivo() {
		return dataMasivo;
	}

	public void setDataMasivo(byte[] dataMasivo) {
		this.dataMasivo = dataMasivo;
	}

	public String getNameMasivo() {
		return nameMasivo;
	}

	public void setNameMasivo(String nameMasivo) {
		this.nameMasivo = nameMasivo;
	}

	public byte[] getDataPdf() {
		return dataPdf;
	}

	public void setDataPdf(byte[] dataPdf) {
		this.dataPdf = dataPdf;
	}

	public String getNamePdf() {
		return namePdf;
	}

	public void setNamePdf(String namePdf) {
		this.namePdf = namePdf;
	}

	public RimEnum getProceso() {
		return proceso;
	}

	public void setProceso(RimEnum proceso) {
		this.proceso = proceso;
	}

	

	

}
