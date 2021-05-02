package com.gizlocorp.gnvoice.reporte;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito;

/**
 * 
 * @author gizlo
 */
public class NotaCreditoReporte {
	private NotaCredito notaCredito;
	private List<DetallesAdicionalesReporte> detallesAdiciones;
	private List<InformacionAdicional> infoAdicional;

	public NotaCreditoReporte(NotaCredito notaCredito) {
		this.notaCredito = notaCredito;
	}

	/**
	 * @return the notaCredito
	 */
	public NotaCredito getNotaCredito() {
		return notaCredito;
	}

	/**
	 * @param notaCredito
	 *            the notaCredito to set
	 */
	public void setNotaCredito(NotaCredito notaCredito) {
		this.notaCredito = notaCredito;
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

		for (NotaCredito.Detalles.Detalle det : getNotaCredito().getDetalles()
				.getDetalle()) {
			DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
			detAd.setCodigoPrincipal(det.getCodigoInterno());
			detAd.setCodigoAuxiliar(det.getCodigoAdicional());
			detAd.setDescripcion(StringEscapeUtils.unescapeXml(det
					.getDescripcion()));
			detAd.setCantidad(df.format(det.getCantidad()));
			detAd.setPrecioTotalSinImpuesto(df.format(det
					.getPrecioTotalSinImpuesto()));
			detAd.setPrecioUnitario(df.format(det.getPrecioUnitario()));
			if (det.getDescuento() != null) {
				detAd.setDescuento(df.format(det.getDescuento()));

			}
			int i = 0;
			if (det.getDetallesAdicionales() != null
					&& det.getDetallesAdicionales().getDetAdicional() != null) {
				for (NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional detAdicional : det
						.getDetallesAdicionales().getDetAdicional()) {
					if (i == 0) {
						detAd.setDetalle1(StringEscapeUtils
								.unescapeXml(detAdicional.getNombre()));
					}
					if (i == 1) {
						detAd.setDetalle2(StringEscapeUtils
								.unescapeXml(detAdicional.getNombre()));
					}
					if (i == 2) {
						detAd.setDetalle3(StringEscapeUtils
								.unescapeXml(detAdicional.getNombre()));
					}
					i++;
				}

			}
			detAd.setInfoAdicional(getInfoAdicional());
			detallesAdiciones.add(detAd);
		}
		return detallesAdiciones;
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
		if (notaCredito.getInfoAdicional() != null) {
			infoAdicional = new ArrayList<InformacionAdicional>();
			if (notaCredito.getInfoAdicional().getCampoAdicional() != null
					&& !notaCredito.getInfoAdicional().getCampoAdicional()
							.isEmpty()) {
				for (NotaCredito.InfoAdicional.CampoAdicional ca : notaCredito
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
