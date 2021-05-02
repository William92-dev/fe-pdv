package com.gizlocorp.gnvoice.reporte;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.gnvoice.xml.factura.Factura;

/**
 * 
 * @author gizlo
 */
public class FacturaReporte {

	private Factura factura;
	private String detalle1;
	private String detalle2;
	private String detalle3;
	private List<DetallesAdicionalesReporte> detallesAdiciones;
	private List<InformacionAdicional> infoAdicional;

	public FacturaReporte(Factura factura) {
		this.factura = factura;
	}

	/**
	 * @return the factura
	 */
	public Factura getFactura() {
		return factura;
	}

	/**
	 * @param factura
	 *            the factura to set
	 */
	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	/**
	 * @return the detalle1
	 */
	public String getDetalle1() {
		return detalle1;
	}

	/**
	 * @param detalle1
	 *            the detalle1 to set
	 */
	public void setDetalle1(String detalle1) {
		this.detalle1 = detalle1;
	}

	/**
	 * @return the detalle2
	 */
	public String getDetalle2() {
		return detalle2;
	}

	/**
	 * @param detalle2
	 *            the detalle2 to set
	 */
	public void setDetalle2(String detalle2) {
		this.detalle2 = detalle2;
	}

	/**
	 * @return the detalle3
	 */
	public String getDetalle3() {
		return detalle3;
	}

	/**
	 * @param detalle3
	 *            the detalle3 to set
	 */
	public void setDetalle3(String detalle3) {
		this.detalle3 = detalle3;
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
		DecimalFormat dfu = new DecimalFormat("#0.0000", otherSymbols);

		for (Factura.Detalles.Detalle det : getFactura().getDetalles()
				.getDetalle()) {
			DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
			detAd.setCodigoPrincipal(det.getCodigoPrincipal());
			detAd.setCodigoAuxiliar(det.getCodigoAuxiliar());
			detAd.setDescripcion(StringEscapeUtils.unescapeXml(det
					.getDescripcion()));
			detAd.setCantidad(df.format(det.getCantidad()));
			detAd.setPrecioTotalSinImpuesto(df.format(det
					.getPrecioTotalSinImpuesto()));
			detAd.setPrecioUnitario(dfu.format(det.getPrecioUnitario()));
			//detAd.setPrecioUnitario(df.format(det.getPrecioUnitario()));
			
			if (det.getDescuento() != null) {
				detAd.setDescuento(df.format(det.getDescuento()));
			}
			int i = 0;
			if (det.getDetallesAdicionales() != null
					&& det.getDetallesAdicionales().getDetAdicional() != null
					&& !det.getDetallesAdicionales().getDetAdicional()
							.isEmpty()) {
				for (Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional detAdicional : det
						.getDetallesAdicionales().getDetAdicional()) {
					if (i == 0) {
						detAd.setDetalle1(detAdicional.getNombre());
					}
					if (i == 1) {
						detAd.setDetalle2(detAdicional.getNombre());
					}
					if (i == 2) {
						detAd.setDetalle3(detAdicional.getNombre());
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
		if (getFactura().getInfoAdicional() != null) {
			infoAdicional = new ArrayList<InformacionAdicional>();
			if (getFactura().getInfoAdicional().getCampoAdicional() != null
					&& !factura.getInfoAdicional().getCampoAdicional()
							.isEmpty()) {
				for (Factura.InfoAdicional.CampoAdicional ca : getFactura()
						.getInfoAdicional().getCampoAdicional()) {
					infoAdicional.add(new InformacionAdicional(ca.getValue(),
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
