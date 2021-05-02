package com.gizlocorp.gnvoice.servicio.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.gnvoice.dao.FacturaDAO;
import com.gizlocorp.gnvoice.dao.GuiaRemisionDAO;
import com.gizlocorp.gnvoice.dao.NotaCreditoDAO;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoDocumento;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.GuiaRemision;
import com.gizlocorp.gnvoice.modelo.NotaCredito;
import com.gizlocorp.gnvoice.servicio.local.ServicioComprobante;
import com.gizlocorp.gnvoice.utilitario.Constantes;

import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;

@Stateless
public class ServicioComprobanteImpl implements ServicioComprobante {

	public static final Logger log = Logger
			.getLogger(ServicioComprobanteImpl.class);

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB
	FacturaDAO facturaDAO;

	@EJB
	GuiaRemisionDAO guiaDAO;

	@EJB
	NotaCreditoDAO notaCreditoDAO;

	public static void main(String args[]) throws MalformedURLException {
		String wsdlLocation = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
		URL url = new URL(wsdlLocation);
		AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(
				url, new QName("http://ec.gob.sri.ws.autorizacion",
						"AutorizacionComprobantesService"));

		AutorizacionComprobantes autorizacionws = servicioAutorizacion
				.getAutorizacionComprobantesPort();
		RespuestaComprobante respuestaComprobanteAut = autorizacionws
				.autorizacionComprobante("1308201401179236625900110010010000000495658032312");

		log.debug(respuestaComprobanteAut.getAutorizaciones().getAutorizacion()
				.get(0).getComprobante());
	}

	@Override
	public String consultaRutaComprobante(
			com.gizlocorp.gnvoice.enumeracion.TipoComprobante comprobante,
			TipoDocumento tipoDocumento, String claveAcceso) {

		switch (comprobante) {
		case FACTURA:
			Factura factura = facturaDAO.obtenerComprobante(claveAcceso, null,
					null, null);
			if (TipoDocumento.XML.equals(tipoDocumento)) {
				return factura.getArchivo();
			}
			return factura != null ? factura.getArchivoLegible() : null;
		case GUIA:
			GuiaRemision guia = guiaDAO.obtenerComprobante(claveAcceso, null,
					null, null);
			if (TipoDocumento.XML.equals(tipoDocumento)) {
				return guia.getArchivo();
			}
			return guia != null ? guia.getArchivoLegible() : null;
		case NOTA_CREDITO:
			NotaCredito notaCredito = notaCreditoDAO.obtenerComprobante(
					claveAcceso, null, null, null);
			if (TipoDocumento.XML.equals(tipoDocumento)) {
				return notaCredito.getArchivo();
			}
			return notaCredito != null ? notaCredito.getArchivoLegible() : null;
		}
		return "";
	}

	@Override
	public Autorizacion autorizarComprobante(String ambienteEmision,
			String claveAcceso, Long codigoOrganizacion) {
		Autorizacion respuesta = null;

		log.debug("CLAVE DE ACCESO: " + claveAcceso);
		int contador = 0;
		int reintentos = 0;
		int retardo = 4000;

		// try {
		// reintentos = Integer.valueOf(servicioParametro.consultarParametro(
		// Constantes.REINTENTO_SRI, codigoOrganizacion).getValor());
		// retardo = Integer.valueOf(servicioParametro.consultarParametro(
		// Constantes.INTERVARLO_REINTENTO_SRI, codigoOrganizacion)
		// .getValor()) * 1000;
		//
		// } catch (GizloException ex) {
		// log.debug(ex.getMessage(), ex);
		// }

		try {
			while (contador <= reintentos) {
				try {
					AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(
							obtenerWSDL(ambienteEmision,
									Constantes.AUTORIZACION_WS,
									codigoOrganizacion), new QName(
									"http://ec.gob.sri.ws.autorizacion",
									"AutorizacionComprobantesService"));

					AutorizacionComprobantes autorizacionws = servicioAutorizacion
							.getAutorizacionComprobantesPort();

					((BindingProvider) autorizacionws).getRequestContext().put(
							"com.sun.xml.ws.connect.timeout", retardo);
					((BindingProvider) autorizacionws).getRequestContext().put(
							"com.sun.xml.internal.ws.connect.timeout", retardo);

					((BindingProvider) autorizacionws).getRequestContext().put(
							"com.sun.xml.internal.ws.request.timeout", retardo);
					((BindingProvider) autorizacionws).getRequestContext().put(
							"com.sun.xml.ws.request.timeout", retardo);

					RespuestaComprobante respuestaComprobanteAut = autorizacionws
							.autorizacionComprobante(claveAcceso);

					if (respuestaComprobanteAut != null
							&& respuestaComprobanteAut.getAutorizaciones() != null
							&& respuestaComprobanteAut.getAutorizaciones()
									.getAutorizacion() != null
							&& !respuestaComprobanteAut.getAutorizaciones()
									.getAutorizacion().isEmpty()
							&& respuestaComprobanteAut.getAutorizaciones()
									.getAutorizacion().get(0) != null) {

						Autorizacion autorizacion = respuestaComprobanteAut
								.getAutorizaciones().getAutorizacion().get(0);

						for (Autorizacion aut : respuestaComprobanteAut
								.getAutorizaciones().getAutorizacion()) {
							if (aut != null
									&& "AUTORIZADO".equals(aut.getEstado())) {
								autorizacion = aut;
								break;
							}
						}

						autorizacion.setAmbiente(null);

						autorizacion.setComprobante("<![CDATA["
								+ autorizacion.getComprobante() + "]]>");

						if (autorizacion.getMensajes() != null
								&& autorizacion.getMensajes().getMensaje() != null
								&& !autorizacion.getMensajes().getMensaje()
										.isEmpty()) {

							for (Mensaje msj : autorizacion.getMensajes()
									.getMensaje()) {
								msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils
										.escapeXml(msj.getMensaje()) : null);
								msj.setInformacionAdicional(msj
										.getInformacionAdicional() != null ? StringEscapeUtils
										.escapeXml(msj
												.getInformacionAdicional())
										: null);
							}
						}

						if ("AUTORIZADO".equals(autorizacion.getEstado())) {
							autorizacion.setMensajes(null);
						}

						return autorizacion;

					} else {
						log.debug("Renvio por autorizacion en blanco");
						contador++;
						// if (reintentos != contador) {
						// Thread.currentThread();
						// Thread.sleep(retardo);
						//
						// }
					}

				} catch (Exception e) {
					contador++;
					log.debug("Renvio por timeout");

					// log.debug(e.getMessage(), e);
					// Thread.currentThread();
					// Thread.sleep(retardo);
					respuesta = new Autorizacion();
					respuesta.setEstado("ERROR");
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			respuesta = new Autorizacion();
			respuesta.setEstado("ERROR");
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new Autorizacion();
			respuesta.setEstado("AUTORIZACION NULL");

		}

		return respuesta;
	}

	private URL obtenerWSDL(String ambienteEmision, String servicio,
			Long codigoOrganizacion) throws Exception {
		String wsdlLocation = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
		if (Constantes.RECEPCION_WS.equals(servicio)) {
			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambienteEmision)) {
				wsdlLocation = servicioParametro.consultarParametro(
						Constantes.SERVICIO_REC_SRI_PRODUCCION,
						codigoOrganizacion).getValor();

			} else {
				wsdlLocation = servicioParametro.consultarParametro(
						Constantes.SERVICIO_REC_SRI_PRUEBA, codigoOrganizacion)
						.getValor();
			}
		} else {
			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambienteEmision)) {
				wsdlLocation = servicioParametro.consultarParametro(
						Constantes.SERVICIO_AUT_SRI_PRODUCCION,
						codigoOrganizacion).getValor();
			} else {
				wsdlLocation = servicioParametro.consultarParametro(
						Constantes.SERVICIO_AUT_SRI_PRUEBA, codigoOrganizacion)
						.getValor();
			}
		}

		log.debug("WSDL SRI: " + wsdlLocation);
		URL url = new URL(wsdlLocation);
		return url;
	}

	@Override
	public RespuestaSolicitud enviarComprobante(String ambienteEmision,
			String rutaArchivoXml, Long codigoOrganizacion) {
		RespuestaSolicitud respuesta = null;

		int contador = 0;
		int reintentos = 0;
		int retardo = 4000;

		// try {
		// reintentos = Integer.valueOf(servicioParametro.consultarParametro(
		// Constantes.REINTENTO_SRI, codigoOrganizacion).getValor());
		// retardo = Integer.valueOf(servicioParametro.consultarParametro(
		// Constantes.INTERVARLO_REINTENTO_SRI, codigoOrganizacion)
		// .getValor()) * 1000;
		//
		// } catch (GizloException ex) {
		// log.debug(ex.getMessage(), ex);
		// }

		try {
			byte[] documentFirmado = DocumentUtil.readFile(rutaArchivoXml);
			log.debug("Inicia proceso interoperabilidad SRI ...");
			while (contador <= reintentos) {
				try {
					RecepcionComprobantesService servicioRecepcion = new RecepcionComprobantesService(
							obtenerWSDL(ambienteEmision,
									Constantes.RECEPCION_WS, codigoOrganizacion));
					RecepcionComprobantes recepcion = servicioRecepcion
							.getRecepcionComprobantesPort();

					((BindingProvider) recepcion).getRequestContext().put(
							"com.sun.xml.ws.connect.timeout", retardo);
					((BindingProvider) recepcion).getRequestContext().put(
							"com.sun.xml.internal.ws.connect.timeout", retardo);

					((BindingProvider) recepcion).getRequestContext().put(
							"com.sun.xml.internal.ws.request.timeout", retardo);
					((BindingProvider) recepcion).getRequestContext().put(
							"com.sun.xml.ws.request.timeout", retardo);

					respuesta = recepcion.validarComprobante(documentFirmado);

					log.debug("Estado enviado del SRI del servicio Recepcion: ...:"
							+ respuesta.getEstado());

					return respuesta;

				} catch (Exception e) {
					contador++;
					log.debug("renvio por timeout");
					// log.debug(e.getMessage(), e);
					// Thread.currentThread();
					// Thread.sleep(retardo);
					// respuesta = new RespuestaSolicitud();
					// respuesta.setEstado(e.getMessage());
				}
			}

		} catch (Exception ex) {
			log.debug(ex.getMessage(), ex);
			respuesta = new RespuestaSolicitud();
			respuesta.setEstado(ex.getMessage());
		}

		return respuesta;
	}

}
