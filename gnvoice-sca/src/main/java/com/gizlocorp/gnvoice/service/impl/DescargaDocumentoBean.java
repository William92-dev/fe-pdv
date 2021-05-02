package com.gizlocorp.gnvoice.service.impl;

import java.io.File;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.component.bean.Service;

import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.gnvoice.enumeracion.TipoDocumento;
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.reporte.GuiaRemisionReporte;
import com.gizlocorp.gnvoice.reporte.NotaCreditoReporte;
import com.gizlocorp.gnvoice.service.DescargaDocumento;
import com.gizlocorp.gnvoice.servicio.local.ServicioComprobante;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.servicio.local.ServicioGuia;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.FechaUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
//import com.gizlocorp.gnvoiceIntegracion.dao.TrxElectronicaDAO;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;

@Service(DescargaDocumento.class)
public class DescargaDocumentoBean implements DescargaDocumento {

	private Converter converter;

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioComprobanteImpl!com.gizlocorp.gnvoice.servicio.local.ServicioComprobante")
	ServicioComprobante servicioComprobante;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia")
	ServicioGuia servicioGuia;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito")
	ServicioNotaCredito servicioNotaCredito;

	// @EJB
	// TrxElectronicaDAO trxElectronicaDAO;

	public static final Logger log = LoggerFactory
			.getLogger(DescargaDocumentoBean.class);

	@Override
	public Response obtenerFacturaPDF(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA,
				TipoDocumento.PDF, claveAcceso);
		if (archivo != null && new File(archivo).exists() && !archivo.isEmpty()
				|| !archivo.contains("null.pdf")) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=factura.pdf");
			return response.build();
		} else {
			try {
				String archivoXML = servicioComprobante
						.consultaRutaComprobante(
								com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA,
								TipoDocumento.XML, claveAcceso);

				if (archivoXML != null && new File(archivoXML).exists()) {
					String archivoPDFTmp = archivoXML.replace("xml", "pdf");
					if (archivoPDFTmp != null
							&& new File(archivoPDFTmp).exists()) {
						File file = new File(archivoPDFTmp.toString());
						ResponseBuilder response = Response.ok((Object) file);
						response.header("Content-Disposition",
								"attachment; filename=factura.pdf");
						return response.build();
					}

					String autorizacion = DocumentUtil
							.readContentFile(archivoXML);

					this.converter = ConverterFactory
							.getConverter(XML_CONTENT_TYPE);
					Autorizacion autorizacionXML = this.converter
							.convertirAObjeto(autorizacion, Autorizacion.class);

					String factura = autorizacionXML.getComprobante();
					factura = factura.replace("<![CDATA[", "");
					factura = factura.replace("]]>", "");

					com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = this.converter
							.convertirAObjeto(
									factura,
									com.gizlocorp.gnvoice.xml.factura.Factura.class);

					List<Organizacion> emisores = servicioOrganizacionLocal
							.listarOrganizaciones(null, null, facturaXML
									.getInfoTributaria().getRuc(), null);

					ReporteUtil reporte = new ReporteUtil();
					String rutaArchivoAutorizacionPdf = reporte.generarReporte(
							Constantes.REP_FACTURA, new FacturaReporte(
									facturaXML), autorizacionXML
									.getNumeroAutorizacion(), FechaUtil
									.formatearFecha(autorizacionXML
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											FechaUtil.patronFechaTiempo24),
							facturaXML.getInfoFactura().getFechaEmision(),
							"autorizado", false, emisores.get(0)
									.getLogoEmpresa());

					if (rutaArchivoAutorizacionPdf != null
							&& new File(rutaArchivoAutorizacionPdf).exists()) {
						File file = new File(
								rutaArchivoAutorizacionPdf.toString());
						ResponseBuilder response = Response.ok((Object) file);
						response.header("Content-Disposition",
								"attachment; filename=factura.pdf");
						return response.build();
					}
				}

			} catch (Exception ex) {
				log.error("No se genero PDF", ex.getMessage());
			}
		}

		return null;
	}

	@Override
	public Response obtenerFacturaXML(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA,
				TipoDocumento.XML, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=factura.xml");
			return response.build();
		}

		return null;
	}

	// @Override
	// public Response obtenerFacturaXMLExterna(String claveAcceso) {
	// String archivo =
	// trxElectronicaDAO.concsultarPorClaveAccesso(claveAcceso);
	// if (archivo != null ) {
	//
	// FileOutputStream fos = null;
	// try {
	// fos = new FileOutputStream(claveAcceso+".xml");
	// fos.write(archivo.getBytes());
	// fos.close();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// boolean exis= new File(claveAcceso+".xml").exists();
	// File file = new File(claveAcceso+".xml");
	//
	// ResponseBuilder response = Response.ok((Object) file);
	// response.header("Content-Disposition",
	// "attachment; filename=factura.xml");
	// return response.build();
	// }
	//
	// return null;
	// }

	@Override
	public Response obtenerGuiaPDF(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.GUIA,
				TipoDocumento.PDF, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=guia.pdf");
			return response.build();
		} else {
			try {
				String archivoXML = servicioComprobante
						.consultaRutaComprobante(
								com.gizlocorp.gnvoice.enumeracion.TipoComprobante.GUIA,
								TipoDocumento.XML, claveAcceso);

				if (archivoXML != null && new File(archivoXML).exists()) {
					String archivoPDFTmp = archivoXML.replace("xml", "pdf");
					if (archivoPDFTmp != null
							&& new File(archivoPDFTmp).exists()) {
						File file = new File(archivoPDFTmp.toString());
						ResponseBuilder response = Response.ok((Object) file);
						response.header("Content-Disposition",
								"attachment; filename=guia.pdf");
						return response.build();
					}

					String autorizacion = DocumentUtil
							.readContentFile(archivoXML);

					this.converter = ConverterFactory
							.getConverter(XML_CONTENT_TYPE);
					Autorizacion autorizacionXML = this.converter
							.convertirAObjeto(autorizacion, Autorizacion.class);

					String guia = autorizacionXML.getComprobante();
					guia = guia.replace("<![CDATA[", "");
					guia = guia.replace("]]>", "");

					com.gizlocorp.gnvoice.xml.guia.GuiaRemision guiaXML = this.converter
							.convertirAObjeto(
									guia,
									com.gizlocorp.gnvoice.xml.guia.GuiaRemision.class);

					List<Organizacion> emisores = servicioOrganizacionLocal
							.listarOrganizaciones(null, null, guiaXML
									.getInfoTributaria().getRuc(), null);

					ReporteUtil reporte = new ReporteUtil();
					String rutaArchivoAutorizacionPdf = reporte.generarReporte(
							Constantes.REP_GUIA_REMISION,
							new GuiaRemisionReporte(guiaXML), autorizacionXML
									.getNumeroAutorizacion(), FechaUtil
									.formatearFecha(autorizacionXML
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											FechaUtil.patronFechaTiempo24),
							guiaXML, guiaXML.getInfoGuiaRemision()
									.getFechaIniTransporte(), "autorizado",
							false, emisores.get(0).getLogoEmpresa());

					if (rutaArchivoAutorizacionPdf != null
							&& new File(rutaArchivoAutorizacionPdf).exists()) {
						File file = new File(
								rutaArchivoAutorizacionPdf.toString());
						ResponseBuilder response = Response.ok((Object) file);
						response.header("Content-Disposition",
								"attachment; filename=guia.pdf");
						return response.build();
					}
				}

			} catch (Exception ex) {
				log.error("No se genero PDF", ex.getMessage());
			}
		}

		return null;
	}

	@Override
	public Response obtenerGuiaXML(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.GUIA,
				TipoDocumento.XML, claveAcceso);
		if (new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=guia.xml");
			return response.build();
		}

		return null;
	}

	@Override
	public Response obtenerRetencionPDF(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.RETENCION,
				TipoDocumento.PDF, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=retencion.pdf");
			return response.build();
		}
		return null;
	}

	@Override
	public Response obtenerRetencionXML(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.RETENCION,
				TipoDocumento.XML, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=retencion.xml");
			return response.build();
		}

		return null;
	}

	@Override
	public Response obtenerNotaCreditoPDF(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_CREDITO,
				TipoDocumento.PDF, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=notaCredito.pdf");
			return response.build();
		} else {
			try {
				String archivoXML = servicioComprobante
						.consultaRutaComprobante(
								com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_CREDITO,
								TipoDocumento.XML, claveAcceso);

				if (archivoXML != null && new File(archivoXML).exists()) {
					String archivoPDFTmp = archivoXML.replace("xml", "pdf");
					if (archivoPDFTmp != null
							&& new File(archivoPDFTmp).exists()) {
						File file = new File(archivoPDFTmp.toString());
						ResponseBuilder response = Response.ok((Object) file);
						response.header("Content-Disposition",
								"attachment; filename=notaCredito.pdf");
						return response.build();
					}

					String autorizacion = DocumentUtil
							.readContentFile(archivoXML);

					this.converter = ConverterFactory
							.getConverter(XML_CONTENT_TYPE);
					Autorizacion autorizacionXML = this.converter
							.convertirAObjeto(autorizacion, Autorizacion.class);

					String notaCredito = autorizacionXML.getComprobante();
					notaCredito = notaCredito.replace("<![CDATA[", "");
					notaCredito = notaCredito.replace("]]>", "");

					com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXML = this.converter
							.convertirAObjeto(
									notaCredito,
									com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.class);

					List<Organizacion> emisores = servicioOrganizacionLocal
							.listarOrganizaciones(null, null, notaCreditoXML
									.getInfoTributaria().getRuc(), null);

					ReporteUtil reporte = new ReporteUtil();
					String rutaArchivoAutorizacionPdf = reporte.generarReporte(
							Constantes.REP_NOTA_CREDITO,
							new NotaCreditoReporte(notaCreditoXML),
							autorizacionXML.getNumeroAutorizacion(), FechaUtil
									.formatearFecha(autorizacionXML
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											FechaUtil.patronFechaTiempo24),
							notaCreditoXML.getInfoNotaCredito()
									.getFechaEmision(), "autorizado", false,
							emisores.get(0).getLogoEmpresa());

					if (rutaArchivoAutorizacionPdf != null
							&& new File(rutaArchivoAutorizacionPdf).exists()) {
						File file = new File(
								rutaArchivoAutorizacionPdf.toString());
						ResponseBuilder response = Response.ok((Object) file);
						response.header("Content-Disposition",
								"attachment; filename=notaCredito.pdf");
						return response.build();
					}
				}

			} catch (Exception ex) {
				log.error("No se genero PDF", ex.getMessage());
			}
		}

		return null;
	}

	@Override
	public Response obtenerNotaCreditoXML(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_CREDITO,
				TipoDocumento.XML, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=notaCredito.xml");
			return response.build();
		}

		return null;
	}

	@Override
	public Response obtenerNotaDebitoPDF(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_DEBITO,
				TipoDocumento.PDF, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=notaDebito.pdf");
			return response.build();
		}

		return null;
	}

	@Override
	public Response obtenerNotaDebitoXML(String claveAcceso) {
		String archivo = servicioComprobante.consultaRutaComprobante(
				com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_DEBITO,
				TipoDocumento.XML, claveAcceso);
		if (archivo != null && new File(archivo).exists()) {
			File file = new File(archivo.toString());
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition",
					"attachment; filename=notaDebito.xml");
			return response.build();
		}

		return null;
	}

}
