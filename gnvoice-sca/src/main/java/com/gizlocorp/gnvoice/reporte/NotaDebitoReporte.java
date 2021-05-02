package com.gizlocorp.gnvoice.reporte;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.gnvoice.xml.notadebito.NotaDebito;

/**
 * 
 * @author gizlo
 */
public class NotaDebitoReporte {

	private NotaDebito notaDebito;
	private List<DetallesAdicionalesReporte> detallesAdiciones;
	private List<InformacionAdicional> infoAdicional;

	public NotaDebitoReporte(NotaDebito notaDebito) {
		this.notaDebito = notaDebito;
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

		for (NotaDebito.Motivos.Motivo motivo : notaDebito.getMotivos()
				.getMotivo()) {
			DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
			detAd.setRazonModificacion(StringEscapeUtils.unescapeXml(motivo
					.getRazon()));
			detAd.setValorModificacion(df.format(motivo.getValor()));
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
	 * @return the notaDebito
	 */
	public NotaDebito getNotaDebito() {
		return notaDebito;
	}

	/**
	 * @param notaDebito
	 *            the notaDebito to set
	 */
	public void setNotaDebito(NotaDebito notaDebito) {
		this.notaDebito = notaDebito;
	}

	/**
	 * @return the infoAdicional
	 */
	public List<InformacionAdicional> getInfoAdicional() {

		if (notaDebito.getInfoAdicional() != null
				&& !notaDebito.getInfoAdicional().getCampoAdicional().isEmpty()) {
			infoAdicional = new ArrayList<InformacionAdicional>();
			for (NotaDebito.InfoAdicional.CampoAdicional info : notaDebito
					.getInfoAdicional().getCampoAdicional()) {
				InformacionAdicional ia = new InformacionAdicional(
						StringEscapeUtils.unescapeXml(info.getValue()),
						StringEscapeUtils.unescapeXml(info.getNombre()));
				infoAdicional.add(ia);
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
