package com.gizlocorp.gnvoice.reporte;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion;
import com.gizlocorp.gnvoice.xml.retencion.Impuesto;

/**
 * 
 * @author gizlo
 */
public class ComprobanteRetencionReporte {

	private ComprobanteRetencion comprobanteRetencion;
	private List<DetallesAdicionalesReporte> detallesAdiciones;
	private List<InformacionAdicional> infoAdicional;
	private static final String IVA = "IVA";
	private static final String RENTA = "RENTA";
	private static final String ICE = "ICE";
	private static final String ISD = "IMPUESTO A LA SALIDA DE DIVISAS";
	private static final String FACTURA = "FACTURA";
	private static final String NOTA_CREDITO = "NOTA DE CREDITO";
	private static final String NOTA_DEBITO = "NOTA DE DEBITO";
	private static final String GUIA_REMISION = "GUIA DE REMISION";
	private static final String COMP_RETENCION = "COMPROBANTE RETENCION";

	public ComprobanteRetencionReporte(ComprobanteRetencion comprobanteRetencion) {
		this.comprobanteRetencion = comprobanteRetencion;
	}

	/**
	 * @return the comprobanteRetencion
	 */
	public ComprobanteRetencion getComprobanteRetencion() {
		return comprobanteRetencion;
	}

	/**
	 * @param comprobanteRetencion
	 *            the comprobanteRetencion to set
	 */
	public void setComprobanteRetencion(
			ComprobanteRetencion comprobanteRetencion) {
		this.comprobanteRetencion = comprobanteRetencion;
	}

	/**
	 * @return the detallesAdiciones
	 */
	public List<DetallesAdicionalesReporte> getDetallesAdiciones() {
		detallesAdiciones = new ArrayList<DetallesAdicionalesReporte>();

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);

		for (Impuesto im : comprobanteRetencion.getImpuestos().getImpuesto()) {

			DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
			detAd.setBaseImponible(df.format(im.getBaseImponible()));
			detAd.setPorcentajeRetener(df.format(im.getPorcentajeRetener()));
			detAd.setValorRetenido(df.format(im.getValorRetenido()));
			detAd.setNombreImpuesto(obtenerImpuestoDecripcion(im.getCodigo()));
			detAd.setInfoAdicional(getInfoAdicional());
			detAd.setNumeroComprobante(im.getNumDocSustento());
			detAd.setNombreComprobante(obtenerDecripcionComprobante(im
					.getCodDocSustento()));
			detAd.setFechaEmisionCcompModificado(im
					.getFechaEmisionDocSustento());
			detallesAdiciones.add(detAd);
		}
		return detallesAdiciones;
	}

	private String obtenerDecripcionComprobante(String codDocSustento) {
		if ("01".equals(codDocSustento)) {
			return FACTURA;
		}
		if ("04".equals(codDocSustento)) {
			return NOTA_CREDITO;
		}
		if ("05".equals(codDocSustento)) {
			return NOTA_DEBITO;
		}
		if ("06".equals(codDocSustento)) {
			return GUIA_REMISION;
		}
		if ("07".equals(codDocSustento)) {
			return COMP_RETENCION;
		}
		return null;
	}

	private String obtenerImpuestoDecripcion(String codigoImpuesto) {
		if (codigoImpuesto.equals("1")) {
			return RENTA;
		}
		if (codigoImpuesto.equals("2")) {
			return IVA;
		}
		if (codigoImpuesto.equals("3")) {
			return ICE;
		}
		if (codigoImpuesto.equals("6")) {
			return ISD;
		}
		return null;
	}

	/**
	 * @param detallesAdiciones
	 *            the detallesAdiciones to set
	 */
	public void setDetallesAdiciones(
			List<DetallesAdicionalesReporte> detallesAdiciones) {
		this.detallesAdiciones = detallesAdiciones;
	}

	/**
	 * @return the infoAdicional
	 */
	public List<InformacionAdicional> getInfoAdicional() {
		if (comprobanteRetencion.getInfoAdicional() != null) {
			infoAdicional = new ArrayList<InformacionAdicional>();
			if (comprobanteRetencion.getInfoAdicional().getCampoAdicional() != null
					&& !comprobanteRetencion.getInfoAdicional()
							.getCampoAdicional().isEmpty()) {
				for (ComprobanteRetencion.InfoAdicional.CampoAdicional ca : comprobanteRetencion
						.getInfoAdicional().getCampoAdicional()) {
					infoAdicional.add(new InformacionAdicional(
							StringEscapeUtils.unescapeXml(ca.getValue()),
							StringEscapeUtils.unescapeXml(ca.getNombre())));
				}
			}
		}
		return infoAdicional;
	}

	/**
	 * @param infoAdicional
	 *            the infoAdicional to set
	 */
	public void setInfoAdicional(List<InformacionAdicional> infoAdicional) {
		this.infoAdicional = infoAdicional;
	}
}