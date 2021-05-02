package com.gizlocorp.gnvoice.reporte;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.adm.utilitario.StringUtil;
import com.gizlocorp.gnvoice.xml.guia.Destinatario;
import com.gizlocorp.gnvoice.xml.guia.Detalle;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision;

/**
 * 
 * @author
 */
public class GuiaRemisionReporte {
	private GuiaRemision guiaRemision;
	private String nombreComprobante;
	private String numDocSustento;
	private String fechaEmisionSustento;
	private String numeroAutorizacion;
	private String motivoTraslado;
	private String destino;
	private String rucDestinatario;
	private String razonSocial;
	private String docAduanero;
	private String codigoEstab;
	private String ruta;
	private List<DetalleGuiaReporte> detalles;
	private List<GuiaRemisionReporte> guiaRemisionList;

	// private List<InformacionAdicional>infoAdicional;

	public GuiaRemisionReporte(GuiaRemision guiaRemision) {
		this.guiaRemision = guiaRemision;
		// getInfoAdicional();
	}

	public GuiaRemisionReporte() {
	}

	/**
	 * @return the guiaRemision
	 */
	public GuiaRemision getGuiaRemision() {
		return guiaRemision;
	}

	/**
	 * @param guiaRemision
	 *            the guiaRemision to set
	 */
	public void setGuiaRemision(GuiaRemision guiaRemision) {
		this.guiaRemision = guiaRemision;
	}

	/**
	 * @return the nombreComprobante
	 */
	public String getNombreComprobante() {
		return nombreComprobante;
	}

	/**
	 * @param nombreComprobante
	 *            the nombreComprobante to set
	 */
	public void setNombreComprobante(String nombreComprobante) {
		this.nombreComprobante = nombreComprobante;
	}

	/**
	 * @return the numDocSustento
	 */
	public String getNumDocSustento() {
		return numDocSustento;
	}

	/**
	 * @param numDocSustento
	 *            the numDocSustento to set
	 */
	public void setNumDocSustento(String numDocSustento) {
		this.numDocSustento = numDocSustento;
	}

	/**
	 * @return the fechaEmisionSustento
	 */
	public String getFechaEmisionSustento() {
		return fechaEmisionSustento;
	}

	/**
	 * @param fechaEmisionSustento
	 *            the fechaEmisionSustento to set
	 */
	public void setFechaEmisionSustento(String fechaEmisionSustento) {
		this.fechaEmisionSustento = fechaEmisionSustento;
	}

	/**
	 * @return the numeroAutorizacion
	 */
	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	/**
	 * @param numeroAutorizacion
	 *            the numeroAutorizacion to set
	 */
	public void setNumeroAutorizacion(String numeroAutorizacion) {
		this.numeroAutorizacion = numeroAutorizacion;
	}

	/**
	 * @return the motivoTraslado
	 */
	public String getMotivoTraslado() {
		return motivoTraslado;
	}

	/**
	 * @param motivoTraslado
	 *            the motivoTraslado to set
	 */
	public void setMotivoTraslado(String motivoTraslado) {
		this.motivoTraslado = motivoTraslado;
	}

	/**
	 * @return the destino
	 */
	public String getDestino() {
		return destino;
	}

	/**
	 * @param destino
	 *            the destino to set
	 */
	public void setDestino(String destino) {
		this.destino = destino;
	}

	/**
	 * @return the rucDestinatario
	 */
	public String getRucDestinatario() {
		return rucDestinatario;
	}

	/**
	 * @param rucDestinatario
	 *            the rucDestinatario to set
	 */
	public void setRucDestinatario(String rucDestinatario) {
		this.rucDestinatario = rucDestinatario;
	}

	/**
	 * @return the razonSocial
	 */
	public String getRazonSocial() {
		return razonSocial;
	}

	/**
	 * @param razonSocial
	 *            the razonSocial to set
	 */
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	/**
	 * @return the docAduanero
	 */
	public String getDocAduanero() {
		return docAduanero;
	}

	/**
	 * @param docAduanero
	 *            the docAduanero to set
	 */
	public void setDocAduanero(String docAduanero) {
		this.docAduanero = docAduanero;
	}

	/**
	 * @return the cosdigoEstab
	 */
	public String getCodigoEstab() {
		return codigoEstab;
	}

	/**
	 * @param cosdigoEstab
	 *            the cosdigoEstab to set
	 */
	public void setCodigoEstab(String cosdigoEstab) {
		this.codigoEstab = cosdigoEstab;
	}

	/**
	 * @return the ruta
	 */
	public String getRuta() {
		return ruta;
	}

	/**
	 * @param ruta
	 *            the ruta to set
	 */
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	/**
	 * @return the guiaRemisionList
	 */
	public List<GuiaRemisionReporte> getGuiaRemisionList() {
		guiaRemisionList = new ArrayList<GuiaRemisionReporte>();
		for (Destinatario dest : this.guiaRemision.getDestinatarios()
				.getDestinatario()) {
			GuiaRemisionReporte gr = new GuiaRemisionReporte();
			gr.setNombreComprobante(StringUtil.obtenerDocumentoModificado(dest
					.getCodDocSustento()));
			gr.setNumDocSustento(dest.getNumDocSustento());
			gr.setFechaEmisionSustento(dest.getFechaEmisionDocSustento());
			gr.setNumeroAutorizacion(dest.getNumAutDocSustento());
			gr.setMotivoTraslado(StringEscapeUtils.unescapeXml(dest
					.getMotivoTraslado()));
			gr.setDestino(dest.getDirDestinatario());
			gr.setRucDestinatario(dest.getIdentificacionDestinatario());
			gr.setRazonSocial(StringEscapeUtils.unescapeXml(dest
					.getRazonSocialDestinatario()));
			gr.setDocAduanero(dest.getDocAduaneroUnico());
			gr.setCodigoEstab(dest.getCodEstabDestino());
			gr.setRuta(dest.getRuta());
			gr.setDetalles(obtenerDetalles(dest));
			// gr.setInfoAdicional(getInfoAdicional());
			guiaRemisionList.add(gr);
		}

		return guiaRemisionList;
	}

	private List<DetalleGuiaReporte> obtenerDetalles(Destinatario dest) {
		List<DetalleGuiaReporte> list = new ArrayList<DetalleGuiaReporte>();
		for (Detalle detalle : dest.getDetalles().getDetalle()) {
			DetalleGuiaReporte dgr = new DetalleGuiaReporte();
			dgr.setCantidad(detalle.getCantidad().toString());
			dgr.setDescripcion(StringEscapeUtils.unescapeXml(detalle
					.getDescripcion()));
			dgr.setCodigoPrincipal(detalle.getCodigoInterno());
			dgr.setCodigoAuxiliar(detalle.getCodigoAdicional());
			list.add(dgr);
		}
		return list;
	}

	/**
	 * @param guiaRemisionList
	 *            the guiaRemisionList to set
	 */
	public void setGuiaRemisionList(List<GuiaRemisionReporte> guiaRemisionList) {
		this.guiaRemisionList = guiaRemisionList;
	}

	/**
	 * @return the detalles
	 */
	public List<DetalleGuiaReporte> getDetalles() {
		return detalles;
	}

	/**
	 * @param detalles
	 *            the detalles to set
	 */
	public void setDetalles(List<DetalleGuiaReporte> detalles) {
		this.detalles = detalles;
	}

	/**
	 * @return the infoAdicional
	 */
	// public List<InformacionAdicional> getInfoAdicional() {
	//
	// // if (guiaRemision.getInfoAdicional() != null) {
	// // infoAdicional = new ArrayList<InformacionAdicional>();
	// // if (guiaRemision.getInfoAdicional().getCampoAdicional() != null &&
	// !guiaRemision.getInfoAdicional().getCampoAdicional().isEmpty()) {
	// // for (GuiaRemision.InfoAdicional.CampoAdicional ca :
	// guiaRemision.getInfoAdicional().getCampoAdicional()) {
	// // infoAdicional.add(new InformacionAdicional(ca.getValue(),
	// ca.getNombre()));
	// // }
	// // }
	// // }
	// return infoAdicional;
	// }

	/**
	 * @param infoAdicional
	 *            the infoAdicional to set
	 */
	// public void setInfoAdicional(List<InformacionAdicional> infoAdicional) {
	// this.infoAdicional = infoAdicional;
	// }
}
