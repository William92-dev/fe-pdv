package com.gizlocorp.gnvoice.orpos.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.component.bean.Service;

import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.MailDelivery;
import com.gizlocorp.adm.utilitario.MailMessage;
import com.gizlocorp.adm.utilitario.TripleDESUtil;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.EstadoOffline;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.excepcion.FileException;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaRequest;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaResponse;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWs;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWsService;
import com.gizlocorp.gnvoice.orpos.service.NotaCreditoORPOS;
import com.gizlocorp.gnvoice.reporte.NotaCreditoReporte;
import com.gizlocorp.gnvoice.service.impl.CacheBean;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoAutorizarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoAutorizarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoReceptarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoReceptarResponse;

@Service(NotaCreditoORPOS.class)
public class NotaCreditoORPOSBean implements NotaCreditoORPOS {

	@EJB
	CacheBean cacheBean;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito")
	ServicioNotaCredito servicioNotaCredito;

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	public static final Logger log = LoggerFactory
			.getLogger(NotaCreditoORPOSBean.class);

	private ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud enviarComprobanteOffline(
			String wsdlLocation, String rutaArchivoXml) {
		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuesta = null;

		int retardo = 8000;

		try {
			byte[] documentFirmado = readFile(rutaArchivoXml);
			log.info("Inicia proceso reccepcion offline SRI ..." + wsdlLocation
					+ " ..." + rutaArchivoXml);
			// while (contador <= reintentos) {
			try {
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService servicioRecepcion = new ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService(
						new URL(wsdlLocation));

				// log.info("Inicia proceso reccepcion offline SRI 111...");
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion
						.getRecepcionComprobantesOfflinePort();

				// log.info("Inicia proceso reccepcion offline SRI ...222");

				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.internal.ws.connect.timeout", retardo);

				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.internal.ws.request.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.ws.request.timeout", retardo);

				// log.info("Validando en reccepcion offline SRI
				// 333..."+documentFirmado.length);

				respuesta = recepcion.validarComprobante(documentFirmado);

				log.info("Validando en reccepcion offline SRI 444..."
						+ respuesta.getEstado());

				return respuesta;

			} catch (Exception e) {
				log.info("renvio por timeout.. " + e.getMessage());
			}

		} catch (Exception ex) {
			log.info(ex.getMessage(), ex);
			respuesta = new ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud();
			respuesta.setEstado(ex.getMessage());
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud();
			respuesta.setEstado("ENVIO NULL");

		}
		return respuesta;
	}

	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(
			String wsdlLocation, String claveAcceso) {
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

		// log.info("CLAVE DE ACCESO: " + claveAcceso);
		int timeout = 7000;

		try {
			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
					new URL(wsdlLocation), new QName(
							"http://ec.gob.sri.ws.autorizacion",
							"AutorizacionComprobantesOfflineService"));

			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion
					.getAutorizacionComprobantesOfflinePort();

			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.internal.ws.connect.timeout", timeout);

			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.internal.ws.request.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.ws.request.timeout", timeout);

			ec.gob.sri.comprobantes.ws.offline.aut.RespuestaComprobante respuestaComprobanteAut = autorizacionws
					.autorizacionComprobante(claveAcceso);

			// 2 reintentos
			for (int i = 0; i < 2; i++) {
				if (respuestaComprobanteAut != null
						&& respuestaComprobanteAut.getAutorizaciones() != null
						&& respuestaComprobanteAut.getAutorizaciones()
								.getAutorizacion() != null
						&& !respuestaComprobanteAut.getAutorizaciones()
								.getAutorizacion().isEmpty()
						&& respuestaComprobanteAut.getAutorizaciones()
								.getAutorizacion().get(0) != null) {
					break;
				} else {
					// log.info("Renvio por autorizacion en blanco");
					respuestaComprobanteAut = autorizacionws
							.autorizacionComprobante(claveAcceso);

				}
			}

			if (respuestaComprobanteAut != null
					&& respuestaComprobanteAut.getAutorizaciones() != null
					&& respuestaComprobanteAut.getAutorizaciones()
							.getAutorizacion() != null
					&& !respuestaComprobanteAut.getAutorizaciones()
							.getAutorizacion().isEmpty()
					&& respuestaComprobanteAut.getAutorizaciones()
							.getAutorizacion().get(0) != null) {

				ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacion = respuestaComprobanteAut
						.getAutorizaciones().getAutorizacion().get(0);

				for (ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion aut : respuestaComprobanteAut
						.getAutorizaciones().getAutorizacion()) {
					if (aut != null && "AUTORIZADO".equals(aut.getEstado())) {
						autorizacion = aut;
						break;
					}
				}

				autorizacion.setAmbiente(null);

				autorizacion.setComprobante("<![CDATA["
						+ autorizacion.getComprobante() + "]]>");

				if (autorizacion.getMensajes() != null
						&& autorizacion.getMensajes().getMensaje() != null
						&& !autorizacion.getMensajes().getMensaje().isEmpty()) {

					for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msj : autorizacion
							.getMensajes().getMensaje()) {
						msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils
								.escapeXml(msj.getMensaje()) : null);
						msj.setInformacionAdicional(msj
								.getInformacionAdicional() != null ? StringEscapeUtils
								.escapeXml(msj.getInformacionAdicional())
								: null);
					}
				}

				if ("AUTORIZADO".equals(autorizacion.getEstado())) {
					autorizacion.setMensajes(null);
				}

				return autorizacion;

			}

		} catch (Exception ex) {
			log.info("Renvio por timeout");
			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
			respuesta.setEstado("ERROR");
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
			respuesta.setEstado("ERROR");

		}

		return respuesta;
	}

	@Override
	public NotaCreditoAutorizarResponse autorizar(
			NotaCreditoAutorizarRequest mensaje) {

		NotaCreditoAutorizarResponse respuesta = new NotaCreditoAutorizarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

		com.gizlocorp.gnvoice.modelo.NotaCredito comprobanteExistente = null;

		if (mensaje == null || mensaje.getClaveAccesoComprobante() == null
				|| mensaje.getClaveAccesoComprobante().isEmpty()) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta
					.setMensajeSistema("Mensaje de entrada incorrecto. Clave Acceso no puede venir vacio (null)");
			respuesta
					.setMensajeCliente("Datos de comprobante incorrectos. Clave Acceso Vacios.");
			return respuesta;
		}

		try {
			comprobanteExistente = servicioNotaCredito.obtenerComprobante(
					mensaje.getClaveAccesoComprobante(), null, null, null);
		} catch (Exception ex) {

			try {
				InitialContext ic;
				ic = new InitialContext();
				servicioNotaCredito = (ServicioNotaCredito) ic
						.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
			} catch (NamingException e) {
				e.printStackTrace();
			}
			comprobanteExistente = servicioNotaCredito.obtenerComprobante(
					mensaje.getClaveAccesoComprobante(), null, null, null);
		}

		if (comprobanteExistente != null) {
			log.debug("COMPROBANTE EXISTENTE !");
			if (Estado.AUTORIZADO.getDescripcion().equals(
					comprobanteExistente.getEstado())) {
				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");
				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setClaveAccesoComprobante(comprobanteExistente
						.getClaveAcceso());
				respuesta.setFechaAutorizacion(comprobanteExistente
						.getFechaAutorizacion());
				respuesta.setNumeroAutorizacion(comprobanteExistente
						.getNumeroAutorizacion());

				return respuesta;
			}
		}

		String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

		// si existio verifica que haya sido procesado este en estado
		// procesado
		if (comprobanteExistente != null) {

			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(
					wsdlLocationAutorizacion,
					comprobanteExistente.getClaveAcceso());

			log.info("Procesamiento autorizacion: "
					+ respAutorizacionComExt.getEstado());

			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
				log.info("Comprobante autoriado: "
						+ comprobanteExistente.getClaveAcceso());
				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta
						.setFechaAutorizacion(respAutorizacionComExt
								.getFechaAutorizacion() != null ? respAutorizacionComExt
								.getFechaAutorizacion().toGregorianCalendar()
								.getTime()
								: null);
				respuesta.setNumeroAutorizacion(respAutorizacionComExt
						.getNumeroAutorizacion());
				respuesta.setClaveAccesoComprobante(comprobanteExistente
						.getClaveAcceso());

				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");

				comprobanteExistente
						.setNumeroAutorizacion(respAutorizacionComExt
								.getNumeroAutorizacion());
				comprobanteExistente
						.setFechaAutorizacion(respAutorizacionComExt
								.getFechaAutorizacion() != null ? respAutorizacionComExt
								.getFechaAutorizacion().toGregorianCalendar()
								.getTime()
								: null);
				comprobanteExistente.setEstado(EstadoOffline.AUTORIZADO
						.getDescripcion());

				// autoriza(comprobanteExistente);
				// TODO actualizar comproabten
				try {
					servicioNotaCredito.autorizaOffline(comprobanteExistente);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioNotaCredito = (ServicioNotaCredito) ic
								.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
						servicioNotaCredito
								.autorizaOffline(comprobanteExistente);
					} catch (Exception ex2) {
						log.warn("no actualiza soluciones");
					}
				}

				return respuesta;
			} else if ("PROCESAMIENTO".equals(respAutorizacionComExt
					.getEstado())) {
				log.info("Comprobante en procesamiento: "
						+ comprobanteExistente.getClaveAcceso());

				respuesta.setEstado(respAutorizacionComExt.getEstado());
				respuesta.setMensajes(respuesta.getMensajes());
				respuesta.setMensajeSistema("Comprobante "
						+ respAutorizacionComExt.getEstado());
				respuesta.setMensajeCliente("Comprobante "
						+ respAutorizacionComExt.getEstado());
				respuesta.setClaveAccesoComprobante(comprobanteExistente
						.getClaveAcceso());
				comprobanteExistente.setEstado(EstadoOffline.PROCESAMIENTO
						.getDescripcion());

				try {
					servicioNotaCredito.autorizaOffline(comprobanteExistente);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioNotaCredito = (ServicioNotaCredito) ic
								.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
						servicioNotaCredito
								.autorizaOffline(comprobanteExistente);
					} catch (Exception ex2) {
						log.warn("no actualiza soluciones");
					}
				}

				return respuesta;

			} else if (!"ERROR_TECNICO".equals(respAutorizacionComExt
					.getEstado())) {
				log.info("Comprobante no autorizado: "
						+ comprobanteExistente.getClaveAcceso());

				respuesta.setEstado(respAutorizacionComExt.getEstado());
				respuesta.setMensajes(respuesta.getMensajes());
				respuesta.setMensajeSistema("Comprobante "
						+ respAutorizacionComExt.getEstado());
				respuesta.setMensajeCliente("Comprobante "
						+ respAutorizacionComExt.getEstado());
				respuesta.setClaveAccesoComprobante(comprobanteExistente
						.getClaveAcceso());
				comprobanteExistente.setEstado(EstadoOffline.NOAUTORIZADO
						.getDescripcion());

				try {
					servicioNotaCredito.autorizaOffline(comprobanteExistente);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioNotaCredito = (ServicioNotaCredito) ic
								.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
						servicioNotaCredito
								.autorizaOffline(comprobanteExistente);
					} catch (Exception ex2) {
						log.warn("no actualiza soluciones");
					}
				}

				return respuesta;
			}
		}
		return respuesta;
	}

	@Override
	public NotaCreditoReceptarResponse receptar(
			NotaCreditoReceptarRequest mensaje) {
		NotaCreditoReceptarResponse respuesta = new NotaCreditoReceptarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

		log.debug("Inicia proceso NotaCreditocion");
		String rutaArchivoXml = null;
		String rutaArchivoFirmadoXml = null;
		String rutaArchivoRechazadoXml = null;

		String rutaArchivoAutorizacionPdf = null;
		String rutaArchivoRechazadoPdf = null;

		String claveAcceso = null;
		Organizacion emisor = null;
		com.gizlocorp.gnvoice.modelo.NotaCredito comprobante = null;
		com.gizlocorp.gnvoice.modelo.NotaCredito comprobanteExistente = null;

		String wsdlLocationRecepcion = null;
		String wsdlLocationAutorizacion = null;


		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// GENERA DE COMPROBANTE
//		String dirServidor = "";

	
		
		try {
			log.debug("Inicia validaciones previas");
			if (   mensaje.getNotaCredito() == null 
				|| mensaje.getNotaCredito().getInfoTributaria() == null
				|| mensaje.getNotaCredito().getInfoTributaria().getClaveAcceso() == null
				|| mensaje.getNotaCredito().getInfoTributaria().getClaveAcceso().isEmpty()
					) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema(
						"Mensaje de entrada incorrecto. Clave Acceso no puede venir vacio (nula)");
				respuesta.setMensajeCliente("Datos de comprobante incorrectos. Clave Acceso vacia");
				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getNotaCredito().getInfoTributaria().getRuc());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "
						+ mensaje.getNotaCredito().getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "
						+ mensaje.getNotaCredito().getInfoTributaria().getRuc());
				return respuesta;
			}
			
			try {
				comprobanteExistente = servicioNotaCredito.obtenerComprobante(
						 mensaje.getNotaCredito().getInfoTributaria().getClaveAcceso(), 
						 null, null, null);
				
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				servicioNotaCredito = (ServicioNotaCredito) ic
						.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
				comprobanteExistente = servicioNotaCredito.obtenerComprobante(
						 mensaje.getNotaCredito().getInfoTributaria().getClaveAcceso(), 
						 null, null, null);

			}

			if (comprobanteExistente != null) {
				log.debug("COMPROBANTE EXISTENTE !");
				if (Estado.AUTORIZADO.getDescripcion().equals(comprobanteExistente.getEstado())) {
					respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
					respuesta.setMensajeCliente("Comprobante AUTORIZADO");
					respuesta.setMensajeSistema("Comprobante AUTORIZADO");
					respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
					respuesta.setFechaAutorizacion(comprobanteExistente.getFechaAutorizacion());
					respuesta.setNumeroAutorizacion(comprobanteExistente.getNumeroAutorizacion());

					return respuesta;
				}
			}

			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
			wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

			// si existio verifica que haya sido procesado este en estado
			// procesado
			if (comprobanteExistente != null) {

				ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(
						wsdlLocationAutorizacion, comprobanteExistente.getClaveAcceso());

				log.info("Procesamiento autorizacion: " + respAutorizacionComExt.getEstado());

				if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
					log.info("Comprobante autoriado: " + comprobanteExistente.getClaveAcceso());
					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

					respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
					respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null
							? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
					respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
					respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());

					respuesta.setMensajeSistema("Comprobante AUTORIZADO");
					respuesta.setMensajeCliente("Comprobante AUTORIZADO");

					comprobanteExistente.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
					comprobanteExistente.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null
							? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
					comprobanteExistente.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());

					// TODO actualizar comproabten
					try {
						servicioNotaCredito.autorizaOffline(comprobanteExistente);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (ServicioNotaCredito) ic
									.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
							servicioNotaCredito.autorizaOffline(comprobante);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}

					return respuesta;
				} else if ("PROCESAMIENTO".equals(respAutorizacionComExt.getEstado())) {
					log.info("Comprobante en procesamiento: " + comprobanteExistente.getClaveAcceso());

					respuesta.setEstado(respAutorizacionComExt.getEstado());
					respuesta.setMensajes(respuesta.getMensajes());
					respuesta.setMensajeSistema("Comprobante " + respAutorizacionComExt.getEstado());
					respuesta.setMensajeCliente("Comprobante " + respAutorizacionComExt.getEstado());
					respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
					comprobanteExistente.setEstado(EstadoOffline.PROCESAMIENTO.getDescripcion());

					try {
						servicioNotaCredito.autorizaOffline(comprobanteExistente);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (ServicioNotaCredito) ic
									.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
							servicioNotaCredito.autorizaOffline(comprobanteExistente);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}

					return respuesta;

				} else if (!"ERROR_TECNICO".equals(respAutorizacionComExt.getEstado())) {
					log.info("Comprobante no autorizado: " + comprobanteExistente.getClaveAcceso());

					respuesta.setEstado(respAutorizacionComExt.getEstado());
					respuesta.setMensajes(respuesta.getMensajes());
					respuesta.setMensajeSistema("Comprobante " + respAutorizacionComExt.getEstado());
					respuesta.setMensajeCliente("Comprobante " + respAutorizacionComExt.getEstado());
					respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
					comprobanteExistente.setEstado(EstadoOffline.NOAUTORIZADO.getDescripcion());

					try {
						servicioNotaCredito.autorizaOffline(comprobanteExistente);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (ServicioNotaCredito) ic
									.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
							servicioNotaCredito.autorizaOffline(comprobante);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}

					return respuesta;
				}
			}

//			Parametro dirParametro = cacheBean.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
//			dirServidor = dirParametro != null ? dirParametro.getValor() : "";

			Parametro ambParametro = cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
			Parametro monParamtro = cacheBean.consultarParametro(Constantes.MONEDA, emisor.getId());
			Parametro codFacParametro = cacheBean.consultarParametro(Constantes.COD_NOTA_CREDITO, emisor.getId());
			Parametro codFacModParametro = cacheBean.consultarParametro(Constantes.COD_NOTA_CREDITO, emisor.getId());

			// Setea nodo principal
			mensaje.getNotaCredito().setId("comprobante");
			// mensaje.getNotaCredito().setVersion(Constantes.VERSION);
			mensaje.getNotaCredito().setVersion(Constantes.VERSION_1);

			mensaje.getNotaCredito().getInfoNotaCredito().setMoneda(monParamtro.getValor());
			mensaje.getNotaCredito().getInfoTributaria().setAmbiente(ambParametro.getValor());
			mensaje.getNotaCredito().getInfoTributaria().setCodDoc(codFacParametro.getValor());
			mensaje.getNotaCredito().getInfoNotaCredito().setCodDocModificado(codFacModParametro.getValor());

			Parametro seqParametro = null;
			if (mensaje.getNotaCredito().getInfoTributaria().getSecuencial() != null
					&& !mensaje.getNotaCredito().getInfoTributaria().getSecuencial().isEmpty()) {
				seqParametro = new Parametro();
				seqParametro.setValor(mensaje.getNotaCredito().getInfoTributaria().getSecuencial());
			} else {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Comprobante no tiene secuencial ");
				respuesta.setMensajeCliente("Comprobante no tiene secuencial ");
				return respuesta;

			}

			if (seqParametro.getValor().length() > 9) {
				seqParametro.setValor("1");
			} else if (seqParametro.getValor().length() < 9) {
				StringBuilder secuencial = new StringBuilder();

				int secuencialTam = 9 - seqParametro.getValor().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(seqParametro.getValor());
				seqParametro.setValor(secuencial.toString());
			}

			mensaje.getNotaCredito().getInfoTributaria().setSecuencial(seqParametro.getValor());

			mensaje.getNotaCredito().getInfoTributaria().setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			mensaje.getNotaCredito().getInfoTributaria().setDirMatriz(emisor.getDireccion());

			if (mensaje.getNotaCredito().getInfoTributaria().getEstab() == null
					|| mensaje.getNotaCredito().getInfoTributaria().getEstab().isEmpty()) {
				mensaje.getNotaCredito().getInfoTributaria().setEstab(emisor.getEstablecimiento());

			}

			mensaje.getNotaCredito().getInfoTributaria().setNombreComercial(emisor.getNombreComercial());
			if (mensaje.getNotaCredito().getInfoTributaria().getPtoEmi() == null
					|| mensaje.getNotaCredito().getInfoTributaria().getPtoEmi().isEmpty()) {
				mensaje.getNotaCredito().getInfoTributaria().setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getNotaCredito().getInfoTributaria().setRazonSocial(emisor.getNombre());

			mensaje.getNotaCredito().getInfoNotaCredito().setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			if (mensaje.getNotaCredito().getInfoNotaCredito().getDirEstablecimiento() == null
					|| mensaje.getNotaCredito().getInfoNotaCredito().getDirEstablecimiento().isEmpty()) {
				mensaje.getNotaCredito().getInfoNotaCredito().setDirEstablecimiento(emisor.getDirEstablecimiento());
			}
			mensaje.getNotaCredito().getInfoNotaCredito()
					.setObligadoContabilidad(emisor.getEsObligadoContabilidad().getDescripcion().toUpperCase());

			log.info("****paso 1***");
			
			claveAcceso = mensaje.getNotaCredito().getInfoTributaria().getClaveAcceso();

			log.info("finaliza validaciones previas");
			log.info("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil.convertirEsquemaAEntidadNotacredito(mensaje.getNotaCredito());
			comprobante.setClaveInterna(mensaje.getCodigoExternoComprobante());
			comprobante.setCorreoNotificacion(mensaje.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getCodigoAgencia());

			comprobante.setNumDocModificado(mensaje.getNotaCredito().getInfoNotaCredito().getNumDocModificado());
			log.info("**EJB NumDocModificado***" + comprobante.getNumDocModificado());
			

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			try {
				servicioNotaCredito.autorizaOffline(comprobante);
			} catch (Exception ex) {
				try {
					InitialContext ic = new InitialContext();
					servicioNotaCredito = (ServicioNotaCredito) ic
							.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
					servicioNotaCredito.autorizaOffline(comprobante);
				} catch (Exception ex2) {
					log.warn("no actualiza soluciones");
				}
			}
			log.info("**paso 3***");
			// Genera comprobante
			rutaArchivoXml = DocumentUtil
					.createDocument(
							getNotaCreditoXML(mensaje.getNotaCredito()),
							mensaje.getNotaCredito().getInfoNotaCredito()
									.getFechaEmision(),
							claveAcceso,
							com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_CREDITO
									.getDescripcion(), "enviado");
			log.debug("ruta de archivo es: " + rutaArchivoXml);
			
			String validacionXsd = ComprobanteUtil.validaArchivoXSD(
					rutaArchivoXml,
					com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
							+ "/gnvoice/recursos/xsd/notaCredito.xsd");

			log.info("**paso 4***");

			if (validacionXsd != null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Error en esquema comprobante. " + validacionXsd);
				respuesta.setMensajeCliente("Error en la estructura del comprobante enviado.");
				// Actualiza estado comprobante
				log.info("comprobante: " + comprobante + " archivo: "+ rutaArchivoXml);
				comprobante.setArchivo(rutaArchivoXml);
				return respuesta;
			}

			log.info("Comprobante Generado Correctamente!!");
			// Actualiza estado comprobante
			comprobante.setArchivo(rutaArchivoXml);

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("Ha ocurrido un error en el sistema: "+ ex.getMessage());
			respuesta.setMensajeCliente("Ha ocurrido un error en el sistema, comprobante no Generado");

			if (comprobante != null && comprobante.getId() != null) {
				comprobante.setArchivo(rutaArchivoXml);
			}

			return respuesta;		
		
		}
		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// FIRMA COMPROBANTE
		comprobante.setArchivo(rutaArchivoFirmadoXml);

		FirmaElectronicaWsService serviceFirma = new FirmaElectronicaWsService();
		FirmaElectronicaWs firmaElectronica = serviceFirma.getFirmaElectronicaWsPort();
		FirmaElectronicaRequest firmaRequest = new FirmaElectronicaRequest();
		try {
			firmaRequest.setClave(TripleDESUtil._decrypt(emisor.getClaveToken()));
		} catch (Exception ex) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("No se puede desencriptar clave Firma: " + ex.getMessage());
			respuesta.setMensajeCliente("No se puede desencriptar clave Firma");

			comprobante.setArchivo(rutaArchivoXml);
			

			return respuesta;
		}
		firmaRequest.setRutaFirma(emisor.getToken());
		firmaRequest.setRutaArchivo(rutaArchivoXml);
		FirmaElectronicaResponse firmaResponse = firmaElectronica.firmarDocumento(firmaRequest);

		if (firmaResponse == null || "ERROR".equals(firmaResponse.getEstado())) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("Error al firmar el documento: " + firmaResponse.getMensajesistema());
			respuesta.setMensajeCliente("Error al firmar el documento");

			comprobante.setArchivo(rutaArchivoXml);
			

			return respuesta;
		}

		rutaArchivoFirmadoXml = firmaResponse.getRutaArchivoFirmado();
		comprobante.setArchivo(rutaArchivoFirmadoXml);
		comprobante.setModoEnvio("OFFLINE");
		comprobante.setProceso("ORPOS");
		

		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuestaRecepcion = enviarComprobanteOffline(
				wsdlLocationRecepcion, rutaArchivoFirmadoXml);

//		boolean notificacion = false;
		if ("RECIBIDA".equals(respuestaRecepcion.getEstado())) {
			comprobante.setArchivo(rutaArchivoFirmadoXml);

			try {
				String comprobantexml = DocumentUtil.readContentFile(rutaArchivoFirmadoXml);

				com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXML = getNotaCreditoXML(comprobantexml);

				ReporteUtil reporte = new ReporteUtil();
				
				rutaArchivoAutorizacionPdf = reporte.generarReporte(
						Constantes.REP_NOTA_CREDITO,
						new NotaCreditoReporte(notaCreditoXML),
						mensaje.getNotaCredito().getInfoTributaria().getClaveAcceso(),
						null, mensaje
								.getNotaCredito().getInfoNotaCredito()
								.getFechaEmision(), "autorizado",
						false, emisor.getLogoEmpresa());

				 if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
					Plantilla t = cacheBean.obtenerPlantilla(
							Constantes.NOTIFIACION, emisor.getId());

					Map<String, Object> parametrosBody = new HashMap<String, Object>();
					parametrosBody.put("nombre", mensaje.getNotaCredito()
							.getInfoNotaCredito().getRazonSocialComprador());
					parametrosBody.put("secuencial", mensaje.getNotaCredito()
							.getInfoTributaria().getSecuencial());
					parametrosBody.put("tipo", "NOTA DE CREDITO");
					parametrosBody.put("fechaEmision", mensaje.getNotaCredito()
							.getInfoNotaCredito().getFechaEmision());
					parametrosBody.put("total", mensaje.getNotaCredito()
							.getInfoNotaCredito().getValorModificacion());
					parametrosBody.put("emisor", emisor.getNombre());
					parametrosBody.put("ruc", emisor.getRuc());
					parametrosBody.put("numeroAutorizacion", notaCreditoXML
							.getInfoTributaria().getClaveAcceso());

					MailMessage mailMensaje = new MailMessage();
					mailMensaje.setSubject(t
							.getTitulo()
							.replace("$tipo", "NOTA DE CREDITO")
							.replace(
									"$numero",
									mensaje.getNotaCredito()
											.getInfoTributaria()
											.getSecuencial()));
					mailMensaje.setFrom(cacheBean.consultarParametro(
							Constantes.CORREO_REMITE, emisor.getId())
							.getValor());
					mailMensaje.setTo(Arrays.asList(mensaje
							.getCorreoElectronicoNotificacion().split(";")));
					mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(
							parametrosBody, t.getDescripcion(), t.getValor()));
					mailMensaje.setAttachment(Arrays.asList(
							rutaArchivoFirmadoXml,
							rutaArchivoAutorizacionPdf));
					MailDelivery.send(
							mailMensaje,
							cacheBean.consultarParametro(Constantes.SMTP_HOST,
									emisor.getId()).getValor(),
							cacheBean.consultarParametro(Constantes.SMTP_PORT,
									emisor.getId()).getValor(),
							cacheBean.consultarParametro(
									Constantes.SMTP_USERNAME, emisor.getId())
									.getValor(),
							cacheBean.consultarParametro(
									Constantes.SMTP_PASSWORD, emisor.getId())
									.getValorDesencriptado(), emisor
									.getAcronimo());
//					notificacion = true;				
				 }

			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);

			}

			respuesta.setEstado(Estado.RECIBIDA.getDescripcion());

			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setNumeroAutorizacion(claveAcceso);

			respuesta.setMensajeSistema("Comprobante AUTORIZADO");
			respuesta.setMensajeCliente("Comprobante AUTORIZADO");

			comprobante.setNumeroAutorizacion(claveAcceso);
			comprobante.setArchivo(rutaArchivoFirmadoXml);
			comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
			
			
			try {
				servicioNotaCredito.autorizaOffline(comprobante);
			} catch (Exception ex) {
				try {
					InitialContext ic = new InitialContext();
					servicioNotaCredito = (ServicioNotaCredito) ic
							.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
					servicioNotaCredito.autorizaOffline(comprobante);
				} catch (Exception ex2) {
					log.warn("no actualiza soluciones");
				}
			}

			return respuesta;

			/*---FIN AUTORIZACION------------------------------------------------------------------------------------------------------------------*/
		} else if ("DEVUELTA".equals(respuestaRecepcion.getEstado())) {
			// DEVUELTA
			// Configura valores de respuesta
			StringBuilder mensajeRespuesta = new StringBuilder();
			if (respuestaRecepcion.getComprobantes() != null
					&& respuestaRecepcion.getComprobantes().getComprobante() != null
					&& !respuestaRecepcion.getComprobantes().getComprobante().isEmpty()) {
				MensajeRespuesta msgObj = null;
				for (ec.gob.sri.comprobantes.ws.offline.rec.Comprobante comprobanteRecepcion : respuestaRecepcion
						.getComprobantes().getComprobante()) {
					if (comprobanteRecepcion.getMensajes() != null) {
						for (ec.gob.sri.comprobantes.ws.offline.rec.Mensaje msg : comprobanteRecepcion.getMensajes()
								.getMensaje()) {
							msgObj = new MensajeRespuesta();
							msgObj.setIdentificador(msg.getIdentificador());
							msgObj.setInformacionAdicional(msg.getInformacionAdicional());
							msgObj.setMensaje(msg.getMensaje());
							msgObj.setTipo(msg.getTipo());
							respuesta.getMensajes().add(msgObj);

							mensajeRespuesta.append(" Identificador: ");
							mensajeRespuesta.append(msg.getIdentificador());
							mensajeRespuesta.append(" Mensaje: ");
							mensajeRespuesta.append(msg.getMensaje());
							mensajeRespuesta.append(" Informacion Adicional: ");
							mensajeRespuesta.append(msg.getInformacionAdicional());
							mensajeRespuesta.append("<br>");
						}
					}
				}
				respuesta.setMensajeSistema(mensajeRespuesta.toString());
				respuesta.setMensajeCliente("Comprobante DEVUELTO");
			}


			respuesta.setEstado(Estado.DEVUELTA.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			comprobante.setArchivo(rutaArchivoRechazadoXml);
			comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);
			try {
				servicioNotaCredito.autorizaOffline(comprobante);
			} catch (Exception ex) {
				try {
					InitialContext ic = new InitialContext();
					servicioNotaCredito = (ServicioNotaCredito) ic
							.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
					servicioNotaCredito.autorizaOffline(comprobante);
				} catch (Exception ex2) {
					log.warn("no actualiza soluciones");
				}
			}

			return respuesta;

		} else {
			// TRANSMITIDO SIN RESPUESTA
			respuesta.setMensajeCliente("TRANSMITIDO SIN RESPUESTA CONTIGENCIA");
			respuesta.setEstado(Estado.PENDIENTE.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			comprobante.setArchivo(rutaArchivoRechazadoXml);
			comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);

			return respuesta;
		}
	}

	@Override
	public NotaCreditoConsultarResponse consultar(
			NotaCreditoConsultarRequest mensaje) {

		log.info("***Ingresando a consultar NotaCredito Orpos***");

		NotaCreditoConsultarResponse respuesta = new NotaCreditoConsultarResponse();
		List<com.gizlocorp.gnvoice.xml.notacredito.NotaCredito> comprobantes = new ArrayList<com.gizlocorp.gnvoice.xml.notacredito.NotaCredito>();
		try {
			if ((mensaje.getRuc() != null && !mensaje.getRuc().isEmpty())
					|| (mensaje.getClaveAcceso() != null && !mensaje
							.getClaveAcceso().isEmpty())
					|| (mensaje.getSecuencial() != null && !mensaje
							.getSecuencial().isEmpty())
					|| (mensaje.getCodigoExterno() != null && !mensaje
							.getCodigoExterno().isEmpty())
					|| (mensaje.getEstado() != null && !mensaje.getEstado()
							.isEmpty())) {

				log.info("***Ingresando a consultar NotaCredito Orpos 222***"
						+ servicioNotaCredito);

				List<com.gizlocorp.gnvoice.modelo.NotaCredito> items = servicioNotaCredito
						.obtenerComprobanteConsulta(mensaje.getRuc(),
								mensaje.getClaveAcceso(),
								mensaje.getSecuencial(),
								mensaje.getCodigoExterno(), mensaje.getEstado());

				log.info("***Ingresando a consultar NotaCredito Orpos 333***");

				if (items != null && !items.isEmpty()) {
					for (com.gizlocorp.gnvoice.modelo.NotaCredito item : items) {
						try {
							String xmlString = DocumentUtil
									.readContentFile(item.getArchivo()
											.toString());
							com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXML = getNotaCreditoXML(xmlString);
							comprobantes.add(notaCreditoXML);
						} catch (Exception ex) {
							log.error(
									"Ha ocurrido un error al leer comprobante ",
									ex);
						}
					}
				}

				log.info("***Ingresando a consultar NotaCredito Orpos 444***");

				respuesta.setEstado("");
				respuesta.setNotasCredito(comprobantes);
			}
		} catch (Exception ex) {
			// respuesta.setNotaCreditos(new ArrayList<factu>());
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			MensajeRespuesta msgObj = new MensajeRespuesta();
			msgObj.setIdentificador("-100");
			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
			respuesta.getMensajes().add(msgObj);

			log.error(ex.getMessage(), ex);
		}

		return respuesta;
	}

	private String getNotaCreditoXML(Object comprobante)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.notacredito.NotaCredito getNotaCreditoXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.class);
	}

	private static byte[] readFile(String filePath) throws FileException {
		try {
			File file = new File(filePath);
			byte[] content = org.apache.commons.io.FileUtils
					.readFileToByteArray(file);
			return content;

		} catch (IOException e) {
			throw new FileException(e);
		}
	}

}
