package com.gizlocorp.gnvoice.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
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
//import com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.MailDelivery;
import com.gizlocorp.adm.utilitario.MailMessage;
import com.gizlocorp.adm.utilitario.TripleDESUtil;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.EstadoOffline;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.excepcion.FileException;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaRequest;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaResponse;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWs;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWsService;
import com.gizlocorp.gnvoice.reporte.GuiaRemisionReporte;
import com.gizlocorp.gnvoice.service.Guia;
import com.gizlocorp.gnvoice.servicio.local.ServicioGuia;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.FechaUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.message.GuiaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaRecibirResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;

import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;

//@Interceptors(CurrentUserProvider.class)
@Service(Guia.class)
@Stateless
public class GuiaBean implements Guia {
	public static final Logger log = LoggerFactory.getLogger(GuiaBean.class);

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia")
	ServicioGuia servicioGuia;

	@EJB
	CacheBean cacheBean;

	private Autorizacion autorizarComprobante(String wsdlLocation,String claveAcceso) {
		Autorizacion respuesta = null;

		int timeout = 7000;

		try {
			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(new URL(wsdlLocation), new QName(
							"http://ec.gob.sri.ws.autorizacion",
							"AutorizacionComprobantesService"));

			AutorizacionComprobantes autorizacionws = servicioAutorizacion.getAutorizacionComprobantesPort();

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", timeout);

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);

			RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

			// 2 reintentos
			for (int i = 0; i < 2; i++) {
				if (respuestaComprobanteAut != null
						&& respuestaComprobanteAut.getAutorizaciones() != null
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
						&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {
					break;
				} else {
					// log.debug("Renvio por autorizacion en blanco");
					respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

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

				Autorizacion autorizacion = respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0);

				for (Autorizacion aut : respuestaComprobanteAut
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

					for (Mensaje msj : autorizacion.getMensajes().getMensaje()) {
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
			log.debug("Renvio por timeout");
			respuesta = new Autorizacion();
			respuesta.setEstado("ERROR");
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new Autorizacion();
			respuesta.setEstado("ERROR");

		}

		return respuesta;
	}

	private RespuestaSolicitud enviarComprobante(String wsdlLocation,
			String rutaArchivoXml) {
		RespuestaSolicitud respuesta = null;

		int retardo = 4000;

		try {
			byte[] documentFirmado = DocumentUtil.readFile(rutaArchivoXml);
			// log.debug("Inicia proceso interoperabilidad SRI ...");
			// while (contador <= reintentos) {
			try {
				RecepcionComprobantesService servicioRecepcion = new RecepcionComprobantesService(new URL(wsdlLocation));
				RecepcionComprobantes recepcion = servicioRecepcion.getRecepcionComprobantesPort();

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.request.timeout", retardo);

				respuesta = recepcion.validarComprobante(documentFirmado);

				return respuesta;

			} catch (Exception e) {
				log.debug("renvio por timeout");
			}

		} catch (Exception ex) {
			log.debug(ex.getMessage(), ex);
			respuesta = new RespuestaSolicitud();
			respuesta.setEstado(ex.getMessage());
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new RespuestaSolicitud();
			respuesta.setEstado("ENVIO NULL");

		}
		return respuesta;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public GuiaProcesarResponse procesar(GuiaProcesarRequest mensaje) {
		GuiaProcesarResponse respuesta = new GuiaProcesarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
		log.debug("Inicia proceso guia");
		String rutaArchivoXml = null;
		String rutaArchivoFirmadoXml = null;
		String rutaArchivoAutorizacionXml = null;
		String rutaArchivoRechazadoXml = null;

		String rutaArchivoAutorizacionPdf = null;
		String rutaArchivoRechazadoPdf = null;

		String ambiente = null;
		String claveAcceso = null;
		Organizacion emisor = null;
		String wsdlLocationRecepcion = null;
		String wsdlLocationAutorizacion = null;

		com.gizlocorp.gnvoice.modelo.GuiaRemision comprobante = null;

		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// GENERA DE COMPROBANTE
		// TODO contigencia
		// boolean existioComprobante = false;
		try {

			log.debug("Inicia validaciones previas");
			if (mensaje.getGuia() == null
					|| mensaje.getGuia().getInfoTributaria() == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta
						.setMensajeSistema("Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
				respuesta
						.setMensajeCliente("Datos de comprobante incorrectos. Factura vacia");

				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getGuia()
					.getInfoTributaria().getRuc());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "+ mensaje.getGuia().getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "+ mensaje.getGuia().getInfoTributaria().getRuc());
				return respuesta;
			}

			if (mensaje.getGuia().getInfoTributaria().getSecuencial() == null|| mensaje.getGuia().getInfoTributaria().getSecuencial().isEmpty()) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Secuencial no puede ser NULL");
				respuesta.setMensajeCliente("Secuencial no puede ser NULL");
				return respuesta;
			}

			Parametro ambParametro = cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
			Parametro codFacParametro = cacheBean.consultarParametro(Constantes.COD_GUIA, emisor.getId());
			ambiente = ambParametro.getValor();

			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
			wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				wsdlLocationRecepcion = cacheBean.consultarParametro(Constantes.SERVICIO_REC_SRI_PRODUCCION, emisor.getId()).getValor();

			} else {
				wsdlLocationRecepcion = cacheBean.consultarParametro(Constantes.SERVICIO_REC_SRI_PRUEBA, emisor.getId()).getValor();
			}
			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				wsdlLocationAutorizacion = cacheBean.consultarParametro(
						Constantes.SERVICIO_AUT_SRI_PRODUCCION, emisor.getId())
						.getValor();
			} else {
				wsdlLocationAutorizacion = cacheBean.consultarParametro(
						Constantes.SERVICIO_AUT_SRI_PRUEBA, emisor.getId())
						.getValor();
			}

			com.gizlocorp.gnvoice.modelo.GuiaRemision comprobanteExistente = null;
			try {
				comprobanteExistente = servicioGuia.obtenerComprobante(null
																	  ,mensaje.getCodigoExterno()
																	  ,mensaje.getGuia().getInfoTributaria().getRuc()
																	  ,mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				servicioGuia = (ServicioGuia) ic.lookup("java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia");
				comprobanteExistente = servicioGuia.obtenerComprobante(null
																	   ,mensaje.getCodigoExterno()
																	   ,mensaje.getGuia().getInfoTributaria().getRuc()
																	   ,mensaje.getAgencia());

			}

			if (comprobanteExistente != null && Estado.AUTORIZADO.getDescripcion().equals( comprobanteExistente.getEstado())) {
				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");
				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
				respuesta.setFechaAutorizacion(comprobanteExistente.getFechaAutorizacion());
				respuesta.setNumeroAutorizacion(comprobanteExistente.getNumeroAutorizacion());
				return respuesta;
			}

			// Setea nodo principal
			mensaje.getGuia().setId("comprobante");
			mensaje.getGuia().setVersion(Constantes.VERSION);

			mensaje.getGuia().getInfoTributaria().setAmbiente(ambParametro.getValor());
			mensaje.getGuia().getInfoTributaria().setCodDoc(codFacParametro.getValor());

			if (mensaje.getGuia().getInfoTributaria().getSecuencial().length() < 9) {
				StringBuilder secuencial = new StringBuilder();
				int secuencialTam = 9 - mensaje.getGuia().getInfoTributaria().getSecuencial().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(mensaje.getGuia().getInfoTributaria().getSecuencial());
				mensaje.getGuia().getInfoTributaria().setSecuencial(secuencial.toString());
			}

			mensaje.getGuia().getInfoTributaria().setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			String dirMatriz = emisor.getDireccion();

			mensaje.getGuia()
					.getInfoTributaria()
					.setDirMatriz(
							dirMatriz != null ? StringEscapeUtils
									.escapeXml(dirMatriz.trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "") : null);

			if (mensaje.getGuia().getInfoTributaria().getEstab() == null
					|| mensaje.getGuia().getInfoTributaria().getEstab()
							.isEmpty()) {
				mensaje.getGuia().getInfoTributaria()
						.setEstab(emisor.getEstablecimiento());

			}
			mensaje.getGuia()
					.getInfoTributaria()
					.setNombreComercial(
							emisor.getNombreComercial() != null ? StringEscapeUtils
									.escapeXml(
											emisor.getNombreComercial().trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "")
									: null);

			if (mensaje.getGuia().getInfoTributaria().getPtoEmi() == null
					|| mensaje.getGuia().getInfoTributaria().getPtoEmi()
							.isEmpty()) {
				mensaje.getGuia().getInfoTributaria()
						.setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getGuia()
					.getInfoTributaria()
					.setRazonSocial(
							emisor.getNombre() != null ? StringEscapeUtils
									.escapeXml(emisor.getNombre()) : null);

			mensaje.getGuia()
					.getInfoGuiaRemision()
					.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			if (mensaje.getGuia().getInfoGuiaRemision().getDirEstablecimiento() == null
					|| mensaje.getGuia().getInfoGuiaRemision()
							.getDirEstablecimiento().isEmpty()) {
				mensaje.getGuia()
						.getInfoGuiaRemision()
						.setDirEstablecimiento(
								emisor.getDirEstablecimiento() != null ? StringEscapeUtils
										.escapeXml(emisor
												.getDirEstablecimiento())
										: null);
			}
			mensaje.getGuia()
					.getInfoGuiaRemision()
					.setObligadoContabilidad(
							emisor.getEsObligadoContabilidad().getDescripcion()
									.toUpperCase());

			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getEstab());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getPtoEmi());
			log.debug("codigo numerico - secuencial de factura: "+ mensaje.getGuia().getInfoTributaria().getSecuencial());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);

			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje
					.getGuia().getInfoGuiaRemision().getFechaIniTransporte(),
					codFacParametro.getValor(), mensaje.getGuia()
							.getInfoTributaria().getRuc(), mensaje.getGuia()
							.getInfoTributaria().getAmbiente(),
					codigoNumerico.toString());
			// }

			// TODO clave de contigencia
			// Parametro parametroContingencia = cacheBean
			// .consultarParametro(Constantes.CONTINGENCIA, emisor.getId());
			// verificando si esta en contingencia
			// if (parametroContingencia != null
			// && parametroContingencia.getValor().equalsIgnoreCase("S")
			// && !existioComprobante) {
			// // verifico si hay claves de contingencias disponibles.
			// ClaveContingencia claveNoUsada = servicioClaveContigencia
			// .recuperarNoUsada(emisor.getId(), ambiente, claveAcceso);
			// if (claveNoUsada != null) {
			// claveAcceso = ComprobanteUtil
			// .generarClaveAccesoProveedor(mensaje.getGuia()
			// .getInfoGuiaRemision().getFechaEmision(),
			// codFacParametro.getValor(),
			// claveNoUsada.getClave());
			// mensaje.getGuia()
			// .getInfoTributaria()
			// .setTipoEmision(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo());
			// mensaje.getGuia().getInfoTributaria()
			// .setClaveAcceso(claveAcceso);
			// }
			// } else {
			mensaje.getGuia().getInfoTributaria().setClaveAcceso(claveAcceso);
			// }
			// -jose--------------------------------------------------------------------------------------------------------------------------------*/

			log.debug("finaliza validaciones previas");
			log.debug("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil
					.convertirEsquemaAEntidadGuiaRemision(mensaje.getGuia());

			comprobante.setClaveInterna(mensaje.getCodigoExterno());
			comprobante.setCorreoNotificacion(mensaje
					.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje
					.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getAgencia());

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			Autorizacion respAutorizacionComExt = autorizarComprobante(
					wsdlLocationAutorizacion, claveAcceso);

			if (comprobanteExistente != null
					&& comprobanteExistente.getId() != null) {
				comprobante.setId(comprobanteExistente.getId());
				comprobante.setEstado(comprobanteExistente.getEstado());
				comprobante.setClaveInterna(comprobanteExistente
						.getClaveInterna());
			}

			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
				try {
					// Genera comprobante legible
					if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
						try {
							String comprobanteXML = respAutorizacionComExt.getComprobante();
							comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
							comprobanteXML = comprobanteXML.replace("]]>", "");

							com.gizlocorp.gnvoice.xml.guia.GuiaRemision factAutorizadaXML = getGuiaXML(comprobanteXML);
							ReporteUtil reporte = new ReporteUtil();

							rutaArchivoAutorizacionPdf = reporte
									.generarReporte(
											Constantes.REP_GUIA_REMISION,
											new GuiaRemisionReporte(
													factAutorizadaXML),
											respAutorizacionComExt
													.getNumeroAutorizacion(),
											FechaUtil
													.formatearFecha(
															respAutorizacionComExt
																	.getFechaAutorizacion()
																	.toGregorianCalendar()
																	.getTime(),
															FechaUtil.patronFechaTiempo24),
											factAutorizadaXML,
											factAutorizadaXML
													.getInfoGuiaRemision()
													.getFechaIniTransporte(),
											"autorizado", false, emisor
													.getLogoEmpresa());

						} catch (Exception e) {
							log.debug(e.getMessage(), e);
						}
					}
					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
					try {

						rutaArchivoAutorizacionXml = DocumentUtil
								.createDocument(
										getGuiaXML(respAutorizacionComExt),
										mensaje.getGuia().getInfoGuiaRemision()
												.getFechaIniTransporte(),
										claveAcceso, TipoComprobante.FACTURA
												.getDescripcion(), "autorizado");

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}

					// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje
					// .getGuia().getInfoGuiaRemision()
					// .getIdentificacionComprador());

					respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
					respuesta.setClaveAccesoComprobante(claveAcceso);
					respuesta
							.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion()
									.toGregorianCalendar().getTime()
									: null);
					respuesta.setNumeroAutorizacion(respAutorizacionComExt
							.getNumeroAutorizacion());

					respuesta.setMensajeSistema("Comprobante AUTORIZADO");
					respuesta.setMensajeCliente("Comprobante AUTORIZADO");

					comprobante.setNumeroAutorizacion(respAutorizacionComExt
							.getNumeroAutorizacion());
					comprobante
							.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion()
									.toGregorianCalendar().getTime()
									: null);
					comprobante.setArchivo(rutaArchivoAutorizacionXml);
					comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);

					try {
						servicioGuia.autoriza(comprobante);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioGuia = (ServicioGuia) ic
									.lookup("java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia");
							servicioGuia.autoriza(comprobante);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}

					return respuesta;
				} catch (Exception ex) {
					log.warn("Validar Autorizacion");
				}
			}

			// Genera comprobante
			rutaArchivoXml = DocumentUtil.createDocument(getGuiaXML(mensaje
					.getGuia()), mensaje.getGuia().getInfoGuiaRemision()
					.getFechaIniTransporte(), claveAcceso,
					com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA
							.getDescripcion(), "enviado");
			log.debug("ruta de archivo es: " + rutaArchivoXml);

			String validacionXsd = ComprobanteUtil.validaArchivoXSD(
					rutaArchivoXml,
					com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
							+ "/gnvoice/recursos/xsd/guiaRemision.xsd");

			if (validacionXsd != null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Error en esquema comprobante. "
						+ validacionXsd);
				respuesta
						.setMensajeCliente("Error en la estructura del comprobante enviado.");
				// Actualiza estado comprobante
				log.debug("comprobante: " + comprobante + " archivo: "
						+ rutaArchivoXml);
				comprobante.setArchivo(rutaArchivoXml);
				return respuesta;
			}

			log.debug("Comprobante Generado Correctamente!!");
			// Actualiza estado comprobante
			comprobante.setArchivo(rutaArchivoXml);

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("Ha ocurrido un error en el sistema: "
					+ ex.getMessage());
			respuesta
					.setMensajeCliente("Ha ocurrido un error en el sistema, comprobante no Generado");

			if (comprobante != null && comprobante.getId() != null) {
				comprobante.setArchivo(rutaArchivoXml);
			}

			return respuesta;
		}
		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// FIRMA COMPROBANTE
		comprobante.setArchivo(rutaArchivoFirmadoXml);

		FirmaElectronicaWsService serviceFirma = new FirmaElectronicaWsService();
		FirmaElectronicaWs firmaElectronica = serviceFirma
				.getFirmaElectronicaWsPort();

		FirmaElectronicaRequest firmaRequest = new FirmaElectronicaRequest();
		try {
			firmaRequest
					.setClave(TripleDESUtil._decrypt(emisor.getClaveToken()));
		} catch (Exception ex) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta
					.setMensajeSistema("No se puede desencriptar clave Firma: "
							+ ex.getMessage());
			respuesta.setMensajeCliente("No se puede desencriptar clave Firma");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		firmaRequest.setRutaFirma(emisor.getToken());
		firmaRequest.setRutaArchivo(rutaArchivoXml);
		FirmaElectronicaResponse firmaResponse = null;

		try {
			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.ws.connect.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.internal.ws.connect.timeout", 5000L);

			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.internal.ws.request.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.ws.request.timeout", 5000L);

			firmaResponse = firmaElectronica.firmarDocumento(firmaRequest);
		} catch (Exception ex) {
			log.error("********* FIRMA", ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("No se puede firmar: "
					+ ex.getMessage());
			respuesta.setMensajeCliente("No se puede firmar");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		if (firmaResponse == null || "ERROR".equals(firmaResponse.getEstado())) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("Error al firmar el documento: "
					+ firmaResponse.getMensajesistema());
			respuesta.setMensajeCliente("Error al firmar el documento");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		rutaArchivoFirmadoXml = firmaResponse.getRutaArchivoFirmado();
		comprobante.setArchivo(rutaArchivoFirmadoXml);

		comprobante.setArchivo(rutaArchivoFirmadoXml);

		// TODO descomentar contigencia
		// si es nuevo y se contigenia
		// notificas---jose---------------------------------------------------------------------------------------------*/
		// Parametro parametroContingencia = null;
		// try {
		// parametroContingencia = cacheBean.consultarParametro(
		// Constantes.CONTINGENCIA, emisor.getId());
		// } catch (Exception e2) {
		// log.debug(e2.getMessage(), e2);
		// }
		//
		// if (parametroContingencia != null
		// && parametroContingencia.getValor().equalsIgnoreCase("S")
		// && !existioComprobante) {
		// String rutaArchivoContigenciaPdf = "";
		// if (mensaje.getCorreoElectronicoNotificacion() != null
		// && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
		// try {
		// ReporteUtil reporte = new ReporteUtil();
		// rutaArchivoContigenciaPdf = reporte.generarReporte(
		// Constantes.REP_FACTURA,
		// new FacturaReporte(mensaje.getGuia()), null,
		// null, mensaje.getGuia().getInfoGuiaRemision()
		// .getFechaEmision(), "contigencia", false,
		// emisor.getLogoEmpresa());
		//
		// Plantilla t = cacheBean.obtenerPlantilla(
		// Constantes.NOTIFIACION, emisor.getId());
		//
		// Map<String, Object> parametrosBody = new HashMap<String, Object>();
		// parametrosBody.put("nombre", mensaje.getGuia()
		// .getInfoGuiaRemision().getRazonSocialComprador());
		// parametrosBody.put("tipo", "FACTURA");
		// parametrosBody.put("secuencial", mensaje.getGuia()
		// .getInfoTributaria().getSecuencial());
		// parametrosBody.put("fechaEmision", mensaje.getGuia()
		// .getInfoGuiaRemision().getFechaEmision());
		// parametrosBody.put("total", mensaje.getGuia()
		// .getInfoGuiaRemision().getImporteTotal());
		// parametrosBody.put("emisor", emisor.getNombre());
		// parametrosBody.put("ruc", emisor.getRuc());
		// parametrosBody.put("numeroAutorizacion", "");
		//
		// MailMessage mailMensaje = new MailMessage();
		// mailMensaje.setSubject(t
		// .getTitulo()
		// .replace("$tipo", "FACTURA")
		// .replace(
		// "$numero",
		// mensaje.getGuia().getInfoTributaria()
		// .getSecuencial()));
		// mailMensaje.setFrom(cacheBean.consultarParametro(
		// Constantes.CORREO_REMITE, emisor.getId())
		// .getValor());
		// mailMensaje.setTo(Arrays.asList(mensaje
		// .getCorreoElectronicoNotificacion().split(";")));
		// mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(
		// parametrosBody, t.getDescripcion(), t.getValor()));
		// mailMensaje.setAttachment(Arrays.asList(
		// rutaArchivoAutorizacionXml,
		// rutaArchivoContigenciaPdf));
		// MailDelivery.send(
		// mailMensaje,
		// cacheBean.consultarParametro(
		// Constantes.SMTP_HOST, emisor.getId())
		// .getValor(),
		// cacheBean.consultarParametro(
		// Constantes.SMTP_PORT, emisor.getId())
		// .getValor(),
		// cacheBean.consultarParametro(
		// Constantes.SMTP_USERNAME, emisor.getId())
		// .getValor(),
		// cacheBean.consultarParametro(
		// Constantes.SMTP_PASSWORD, emisor.getId())
		// .getValorDesencriptado(), emisor
		// .getAcronimo());
		// } catch (Exception e) {
		// log.error("Error en envio de correo electronico", e);
		// }
		// }
		//
		// comprobante.setArchivo(rutaArchivoFirmadoXml);
		// comprobante.setArchivoLegible(rutaArchivoContigenciaPdf);
		//
		// }
		// fin--jose--------------------------------------------------------------------------------------------------------------------------------*/
		RespuestaSolicitud respuestaRecepcion = enviarComprobante(
				wsdlLocationRecepcion, rutaArchivoFirmadoXml);

		if ("RECIBIDA".equals(respuestaRecepcion.getEstado())) {
			comprobante.setArchivo(rutaArchivoFirmadoXml);

			respuesta.setEstado(Estado.RECIBIDA.getDescripcion());
			try {
				Thread.currentThread();
				Thread.sleep(60000L);
			} catch (InterruptedException e) {
				log.debug(e.getMessage(), e);
			}
			/*-------------------------------------------------------------------------------------------------------------------------------------*/
			// AUTORIZACION COMPROBANTE
			comprobante.setArchivo(rutaArchivoFirmadoXml);

			Autorizacion respAutorizacion = autorizarComprobante(
					wsdlLocationAutorizacion, claveAcceso);

			if ("AUTORIZADO".equals(respAutorizacion.getEstado())) {
				if (mensaje.getCorreoElectronicoNotificacion() != null
						&& !mensaje.getCorreoElectronicoNotificacion()
								.isEmpty()) {
					try {
						String comprobanteXML = respAutorizacion
								.getComprobante();
						comprobanteXML = comprobanteXML
								.replace("<![CDATA[", "");
						comprobanteXML = comprobanteXML.replace("]]>", "");

						com.gizlocorp.gnvoice.xml.guia.GuiaRemision factAutorizadaXML = getGuiaXML(comprobanteXML);

						ReporteUtil reporte = new ReporteUtil();

						rutaArchivoAutorizacionPdf = reporte.generarReporte(
								Constantes.REP_GUIA_REMISION,
								new GuiaRemisionReporte(factAutorizadaXML),
								respAutorizacion.getNumeroAutorizacion(),
								FechaUtil.formatearFecha(respAutorizacion
										.getFechaAutorizacion()
										.toGregorianCalendar().getTime(),
										FechaUtil.patronFechaTiempo24),
								factAutorizadaXML, factAutorizadaXML
										.getInfoGuiaRemision()
										.getFechaIniTransporte(), "autorizado",
								false, emisor.getLogoEmpresa());

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}
				}
				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
				try {
					rutaArchivoAutorizacionXml = DocumentUtil.createDocument(
							getGuiaXML(respAutorizacion), mensaje.getGuia()
									.getInfoGuiaRemision()
									.getFechaIniTransporte(), claveAcceso,
							TipoComprobante.FACTURA.getDescripcion(),
							"autorizado");

				} catch (Exception e) {
					log.debug(e.getMessage(), e);
				}

				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(respAutorizacion
						.getFechaAutorizacion() != null ? respAutorizacion
						.getFechaAutorizacion().toGregorianCalendar().getTime()
						: null);
				respuesta.setNumeroAutorizacion(respAutorizacion
						.getNumeroAutorizacion());

				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");

				comprobante.setNumeroAutorizacion(respAutorizacion
						.getNumeroAutorizacion());
				comprobante.setFechaAutorizacion(respAutorizacion
						.getFechaAutorizacion() != null ? respAutorizacion
						.getFechaAutorizacion().toGregorianCalendar().getTime()
						: null);
				comprobante.setArchivo(rutaArchivoAutorizacionXml);
				comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);

				try {
					servicioGuia.autoriza(comprobante);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioGuia = (ServicioGuia) ic
								.lookup("java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia");
						servicioGuia.autoriza(comprobante);

					} catch (Exception ex2) {
						log.warn("no se guardo el comprobante");
					}
				}

				// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje.getGuia()
				// .getInfoGuiaRemision().getIdentificacionComprador());

				return respuesta;

			} else if ("NO AUTORIZADO".equals(respAutorizacion.getEstado())
					|| "RECHAZADO".equals(respAutorizacion.getEstado())) {
				// NO AUTORIZADO

				StringBuilder mensajeRespuesta = new StringBuilder();
				if (respAutorizacion != null
						&& respAutorizacion.getMensajes() != null
						&& respAutorizacion.getMensajes().getMensaje() != null
						&& !respAutorizacion.getMensajes().getMensaje()
								.isEmpty()) {

					MensajeRespuesta msgObj = null;
					for (Mensaje msg : respAutorizacion.getMensajes()
							.getMensaje()) {
						msgObj = new MensajeRespuesta();
						msgObj.setIdentificador(msg.getIdentificador());
						msgObj.setInformacionAdicional(msg
								.getInformacionAdicional());
						msgObj.setMensaje(msg.getMensaje());
						msgObj.setTipo(msg.getTipo());
						respuesta.getMensajes().add(msgObj);

						mensajeRespuesta.append(" Identificador: ");
						mensajeRespuesta.append(msg.getIdentificador());
						mensajeRespuesta.append(" Mensaje: ");
						mensajeRespuesta.append(msg.getMensaje());
						mensajeRespuesta.append(" Informacion Adicional: ");
						mensajeRespuesta.append(msg.getInformacionAdicional());
						mensajeRespuesta.append(" <br>");
					}

					respuesta.setMensajeSistema(mensajeRespuesta.toString());
					respuesta.setMensajeCliente("Comprobante NO AUTORIZADO");
				}

				respuesta.setEstado(Estado.RECHAZADO.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				comprobante.setArchivo(rutaArchivoRechazadoXml);
				comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);

				return respuesta;
			} else {
				/*-------------------------------------------------------------------------------------------------------------------------------------*/
				// TRANSMITIDO SIN RESPUESTA
				respuesta
						.setMensajeCliente("TRANSMITIDO SIN RESPUESTA AUTORIZACION");
				// Genera comprobante legible
				// try {
				// byte[] documentFirmado = DocumentUtil
				// .readFile(rutaArchivoFirmadoXml);
				//
				// rutaArchivoRechazadoXml = DocumentUtil
				// .createDocument(
				// documentFirmado,
				// mensaje.getGuia().getInfoGuiaRemision()
				// .getFechaEmision(),
				// claveAcceso,
				// com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA
				// .getDescripcion(), "rechazado");
				//
				// } catch (Exception e) {
				// log.debug(e.getMessage(), e);
				// }

				respuesta.setEstado(Estado.PENDIENTE.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				comprobante.setArchivo(rutaArchivoRechazadoXml);
				comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);

				return respuesta;

			}

			/*---FIN AUTORIZACION------------------------------------------------------------------------------------------------------------------*/
		} else if ("DEVUELTA".equals(respuestaRecepcion.getEstado())) {
			// DEVUELTA
			// Configura valores de respuesta
			StringBuilder mensajeRespuesta = new StringBuilder();
			if (respuestaRecepcion.getComprobantes() != null
					&& respuestaRecepcion.getComprobantes().getComprobante() != null
					&& !respuestaRecepcion.getComprobantes().getComprobante()
							.isEmpty()) {
				MensajeRespuesta msgObj = null;
				for (Comprobante comprobanteRecepcion : respuestaRecepcion
						.getComprobantes().getComprobante()) {
					if (comprobanteRecepcion.getMensajes() != null) {
						for (ec.gob.sri.comprobantes.ws.Mensaje msg : comprobanteRecepcion
								.getMensajes().getMensaje()) {
							msgObj = new MensajeRespuesta();
							msgObj.setIdentificador(msg.getIdentificador());
							msgObj.setInformacionAdicional(msg
									.getInformacionAdicional());
							msgObj.setMensaje(msg.getMensaje());
							msgObj.setTipo(msg.getTipo());
							respuesta.getMensajes().add(msgObj);

							mensajeRespuesta.append("Identificador: ");
							mensajeRespuesta.append(msg.getIdentificador());
							mensajeRespuesta.append(" Mensaje: ");
							mensajeRespuesta.append(msg.getMensaje());
							mensajeRespuesta.append(" Informacion Adicional: ");
							mensajeRespuesta.append(msg
									.getInformacionAdicional());
							mensajeRespuesta.append(" <br>");
						}
					}
				}
				respuesta.setMensajeSistema(mensajeRespuesta.toString());
				respuesta.setMensajeCliente("Comprobante DEVUELTO");
			}

			// Genera comprobante legible y registra comprobante rechazado
			// try {
			// byte[] documentFirmado = DocumentUtil
			// .readFile(rutaArchivoFirmadoXml);
			//
			// rutaArchivoRechazadoXml = DocumentUtil
			// .createDocument(
			// documentFirmado,
			// mensaje.getGuia().getInfoGuiaRemision()
			// .getFechaEmision(),
			// claveAcceso,
			// com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA
			// .getDescripcion(), "rechazado");
			//
			// } catch (Exception e) {
			// log.debug(e.getMessage(), e);
			// }

			respuesta.setEstado(Estado.DEVUELTA.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			comprobante.setArchivo(rutaArchivoRechazadoXml);
			comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);

			return respuesta;

		} else {

			// TRANSMITIDO SIN RESPUESTA
			respuesta
					.setMensajeCliente("TRANSMITIDO SIN RESPUESTA CONTIGENCIA");
			// Genera comprobante legible
			// try {
			//
			// byte[] documentFirmado = DocumentUtil
			// .readFile(rutaArchivoFirmadoXml);
			//
			// rutaArchivoRechazadoXml = DocumentUtil
			// .createDocument(
			// documentFirmado,
			// mensaje.getGuia().getInfoGuiaRemision()
			// .getFechaEmision(),
			// claveAcceso,
			// com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA
			// .getDescripcion(), "rechazado");
			//
			// } catch (Exception e) {
			// log.debug(e.getMessage(), e);
			// }

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
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public GuiaProcesarResponse procesarOffLine(GuiaProcesarRequest mensaje) {
		GuiaProcesarResponse respuesta = new GuiaProcesarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
		log.debug("Inicia proceso guia offline");
		String rutaArchivoXml = null;
		String rutaArchivoFirmadoXml = null;
		String rutaArchivoAutorizacionXml = null;
		String rutaArchivoRechazadoXml = null;

		String rutaArchivoAutorizacionPdf = null;
		String rutaArchivoRechazadoPdf = null;

		String claveAcceso = null;
		Organizacion emisor = null;
		String wsdlLocationRecepcion = null;
		String wsdlLocationAutorizacion = null;

		com.gizlocorp.gnvoice.modelo.GuiaRemision comprobante = null;

		try {

			log.debug("Inicia validaciones previas");
			if (mensaje.getGuia() == null || mensaje.getGuia().getInfoTributaria() == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema("Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
				respuesta.setMensajeCliente("Datos de comprobante incorrectos. Factura vacia");
				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getGuia().getInfoTributaria().getRuc());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "+ mensaje.getGuia().getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "+ mensaje.getGuia().getInfoTributaria().getRuc());
				return respuesta;
			}

			if (mensaje.getGuia().getInfoTributaria().getSecuencial() == null|| mensaje.getGuia().getInfoTributaria().getSecuencial().isEmpty()) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Secuencial no puede ser NULL");
				respuesta.setMensajeCliente("Secuencial no puede ser NULL");
				return respuesta;
			}

			Parametro ambParametro = cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
			Parametro codFacParametro = cacheBean.consultarParametro(Constantes.COD_GUIA, emisor.getId());
			

			//TODO CAMBIAR EN PRODUCCION
			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
			wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

			com.gizlocorp.gnvoice.modelo.GuiaRemision comprobanteExistente = null;

			try {
				comprobanteExistente = servicioGuia.obtenerComprobante(null,mensaje.getCodigoExterno(), mensaje.getGuia().getInfoTributaria().getRuc(),mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				servicioGuia = (ServicioGuia) ic.lookup("java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia");
				comprobanteExistente = servicioGuia.obtenerComprobante(null,mensaje.getCodigoExterno(), mensaje.getGuia().getInfoTributaria().getRuc(),mensaje.getAgencia());
			}

			if (comprobanteExistente != null&& Estado.AUTORIZADO.getDescripcion().equals(comprobanteExistente.getEstado())) {
				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");
				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
				respuesta.setFechaAutorizacion(comprobanteExistente.getFechaAutorizacion());
				respuesta.setNumeroAutorizacion(comprobanteExistente.getNumeroAutorizacion());

				return respuesta;
			}

			// Setea nodo principal
			mensaje.getGuia().setId("comprobante");
			mensaje.getGuia().setVersion(Constantes.VERSION);

			mensaje.getGuia().getInfoTributaria().setAmbiente(ambParametro.getValor());
			mensaje.getGuia().getInfoTributaria().setCodDoc(codFacParametro.getValor());

			if (mensaje.getGuia().getInfoTributaria().getSecuencial().length() < 9) {
				StringBuilder secuencial = new StringBuilder();

				int secuencialTam = 9 - mensaje.getGuia().getInfoTributaria().getSecuencial().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(mensaje.getGuia().getInfoTributaria().getSecuencial());
				mensaje.getGuia().getInfoTributaria().setSecuencial(secuencial.toString());
			}

			mensaje.getGuia().getInfoTributaria().setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			String dirMatriz = emisor.getDireccion();

			mensaje.getGuia()
					.getInfoTributaria()
					.setDirMatriz(
							dirMatriz != null ? StringEscapeUtils
									.escapeXml(dirMatriz.trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "") : null);

			if (mensaje.getGuia().getInfoTributaria().getEstab() == null || mensaje.getGuia().getInfoTributaria().getEstab().isEmpty()) {
				mensaje.getGuia().getInfoTributaria().setEstab(emisor.getEstablecimiento());

			}
			
			mensaje.getGuia().getInfoTributaria().setNombreComercial(
							emisor.getNombreComercial() != null ? StringEscapeUtils
									.escapeXml(
											emisor.getNombreComercial().trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "")
									: null);

			if (mensaje.getGuia().getInfoTributaria().getPtoEmi() == null|| mensaje.getGuia().getInfoTributaria().getPtoEmi().isEmpty()) {
				mensaje.getGuia().getInfoTributaria().setPtoEmi(emisor.getPuntoEmision());
			}
			
			mensaje.getGuia()
					.getInfoTributaria()
					.setRazonSocial(
							emisor.getNombre() != null ? StringEscapeUtils
									.escapeXml(emisor.getNombre()) : null);

			mensaje.getGuia()
					.getInfoGuiaRemision()
					.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			
			if (mensaje.getGuia().getInfoGuiaRemision().getDirEstablecimiento() == null
					|| mensaje.getGuia().getInfoGuiaRemision()
							.getDirEstablecimiento().isEmpty()) {
				mensaje.getGuia()
						.getInfoGuiaRemision()
						.setDirEstablecimiento(
								emisor.getDirEstablecimiento() != null ? StringEscapeUtils
										.escapeXml(emisor
												.getDirEstablecimiento())
										: null);
			}
			mensaje.getGuia()
					.getInfoGuiaRemision()
					.setObligadoContabilidad(
							emisor.getEsObligadoContabilidad().getDescripcion()
									.toUpperCase());


			
			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getEstab());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getPtoEmi());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);

			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje.getGuia().getInfoGuiaRemision().getFechaIniTransporte(),
																	  codFacParametro.getValor(),
																	  mensaje.getGuia().getInfoTributaria().getRuc(),
																	  mensaje.getGuia().getInfoTributaria().getAmbiente(),
																	  codigoNumerico.toString());

			mensaje.getGuia().getInfoTributaria().setClaveAcceso(claveAcceso);

			
			log.debug("finaliza validaciones previas");
			log.debug("Inicia generacion de comproabante offline");
			comprobante = ComprobanteUtil.convertirEsquemaAEntidadGuiaRemision(mensaje.getGuia());

			comprobante.setClaveInterna(mensaje.getCodigoExterno());
			comprobante.setIdentificadorUsuario("1720811346");
			comprobante.setEstado("AUTORIZADO");;
			
			comprobante.setCorreoNotificacion(mensaje.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getAgencia());

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			

			if (comprobanteExistente != null && comprobanteExistente.getId() != null) {
				comprobante.setId(comprobanteExistente.getId());
				comprobante.setEstado(comprobanteExistente.getEstado());
				comprobante.setClaveInterna(comprobanteExistente.getClaveInterna());
			}
			
			
			log.info("Inicia servicio 123");
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(wsdlLocationAutorizacion, claveAcceso);
			
			
	
			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
				try {

					respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
					respuesta.setClaveAccesoComprobante(claveAcceso);
					
					respuesta.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion()
									.toGregorianCalendar().getTime()
									: null);
					respuesta.setMensajeSistema("Comprobante AUTORIZADO");
					respuesta.setMensajeCliente("Comprobante AUTORIZADO");
										
					 comprobante.setNumeroAutorizacion(claveAcceso);
					 
						if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
							try {
								String comprobanteXML = respAutorizacionComExt.getComprobante();
								comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
								comprobanteXML = comprobanteXML.replace("]]>", "");

								com.gizlocorp.gnvoice.xml.guia.GuiaRemision factAutorizadaXML = getGuiaXML(comprobanteXML);
								ReporteUtil reporte = new ReporteUtil();

								rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_GUIA_REMISION
																					,new GuiaRemisionReporte(factAutorizadaXML)
																					,respAutorizacionComExt.getNumeroAutorizacion()
																					,FechaUtil.formatearFecha(respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime(),FechaUtil.patronFechaTiempo24)
																					,factAutorizadaXML
																					,factAutorizadaXML.getInfoGuiaRemision().getFechaIniTransporte()
																					,"autorizado"
																					,false
																					,emisor.getLogoEmpresa());
							} catch (Exception e) {
								log.debug(e.getMessage(), e);
							}
						}
					
						try {

							rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getGuiaXML(respAutorizacionComExt)
																					,mensaje.getGuia().getInfoGuiaRemision().getFechaIniTransporte()
																					,claveAcceso
																					,TipoComprobante.FACTURA.getDescripcion()
																					,"autorizado");

						} catch (Exception e) {
							log.debug(e.getMessage(), e);
						}
					
					 comprobante.setArchivo(rutaArchivoAutorizacionXml);
		             comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
		             comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
					 
					try {
						servicioGuia.autorizaOffline(comprobante);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioGuia = (ServicioGuia) ic.lookup("java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia");
							servicioGuia.autorizaOffline(comprobante);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}

					return respuesta;
				} catch (Exception ex) {
					log.warn("Validar Autorizacion");
				}
			} else if ("PROCESAMIENTO".equals(respAutorizacionComExt.getEstado()) || "EN PROCESO".equals(respAutorizacionComExt.getEstado())) {
				
                log.debug("Comprobante en procesamiento: "+ comprobante.getClaveAcceso());

                respuesta.setEstado(Estado.PENDIENTE.getDescripcion());
                respuesta.setMensajeSistema("Comprobante en procesamiento"+ respAutorizacionComExt.getEstado());
                respuesta.setMensajeCliente("Comprobante en procesamiento"+ respAutorizacionComExt.getEstado());
                respuesta.setClaveAccesoComprobante(comprobante.getClaveAcceso());

                return respuesta;

            } else if (!"ERROR_TECNICO".equals(respAutorizacionComExt.getEstado())) {
                log.debug("Comprobante no autorizado:  " + respAutorizacionComExt.getEstado());

                respuesta.setEstado(Estado.RECHAZADO.getDescripcion());
                respuesta.setMensajeSistema("Comprobante No autorizado: "+ respAutorizacionComExt.getEstado());
                respuesta.setMensajeCliente("Comprobante No autorizado: "+ respAutorizacionComExt.getEstado());
                respuesta.setClaveAccesoComprobante(comprobante.getClaveAcceso());

                MensajeRespuesta msgObj = null;
                StringBuilder mensajeRespuesta = new StringBuilder();
                if (respAutorizacionComExt.getMensajes() != null) {
                    for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msg : respAutorizacionComExt.getMensajes().getMensaje()) {
                        msgObj = new MensajeRespuesta();
                        msgObj.setIdentificador(msg.getIdentificador());
                        msgObj.setInformacionAdicional(msg.getInformacionAdicional());
                        msgObj.setMensaje(msg.getMensaje());
                        msgObj.setTipo(msg.getTipo());
                        respuesta.getMensajes().add(msgObj);

                        mensajeRespuesta.append("Identificador: ");
                        mensajeRespuesta.append(msg.getIdentificador());
                        mensajeRespuesta.append(" Mensaje: ");
                        mensajeRespuesta.append(msg.getMensaje());
                        mensajeRespuesta.append(" Informacion Adicional: ");
                        mensajeRespuesta.append(msg.getInformacionAdicional());
                        mensajeRespuesta.append(" <br>");
                        respuesta.setMensajeSistema(mensajeRespuesta.toString());
                        respuesta.setMensajeCliente(mensajeRespuesta.toString());
                    }
                }
                respuesta.setMensajeSistema(mensajeRespuesta.toString());
                respuesta.setMensajeCliente(mensajeRespuesta.toString());

            }

			
			
			log.info("NUEVO DOCUMENTO -> " + getGuiaXML(mensaje.getGuia()));
			

			rutaArchivoXml = DocumentUtil.createDocument(getGuiaXML(mensaje.getGuia())
														, mensaje.getGuia().getInfoGuiaRemision().getFechaIniTransporte()
														, claveAcceso
														, com.gizlocorp.gnvoice.enumeracion.TipoComprobante.GUIA.getDescripcion()
														, "enviado");
			
			
			
			log.info("ruta de xml a validar --- >" + rutaArchivoXml);

			String validacionXsd = ComprobanteUtil.validaArchivoXSD(rutaArchivoXml,
								   com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
							       + "/gnvoice/recursos/xsd/guiaRemision.xsd");

			if (validacionXsd != null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Error en esquema comprobante. "+ validacionXsd);
				respuesta.setMensajeCliente("Error en la estructura del comprobante enviado.");
				// Actualiza estado comprobante
				log.debug(" Error de comprovante comprobante: " + comprobante + " archivo: "+ rutaArchivoXml);
				comprobante.setArchivo(rutaArchivoXml);
				return respuesta;
			}

			log.debug("Comprobante Generado Correctamente!!");
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

			respuesta.setMensajeSistema("No se puede desencriptar clave Firma: "+ ex.getMessage());
			respuesta.setMensajeCliente("No se puede desencriptar clave Firma");
			comprobante.setArchivo(rutaArchivoXml);
			return respuesta;
		}

		firmaRequest.setRutaFirma(emisor.getToken());
		firmaRequest.setRutaArchivo(rutaArchivoXml);
		FirmaElectronicaResponse firmaResponse = null;

		try {
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.ws.connect.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.ws.request.timeout", 5000L);

			firmaResponse = firmaElectronica.firmarDocumento(firmaRequest);
		} catch (Exception ex) {
			log.error("********* FIRMA", ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("No se puede firmar: "+ ex.getMessage());
			respuesta.setMensajeCliente("No se puede firmar");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		if (firmaResponse == null || "ERROR".equals(firmaResponse.getEstado())) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("Error al firmar el documento: "+ firmaResponse.getMensajesistema());
			respuesta.setMensajeCliente("Error al firmar el documento");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		rutaArchivoFirmadoXml = firmaResponse.getRutaArchivoFirmado();
		comprobante.setArchivo(rutaArchivoFirmadoXml);

	
		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuestaRecepcion = enviarComprobanteOffline(wsdlLocationRecepcion, rutaArchivoFirmadoXml);

		if ("RECIBIDA".equals(respuestaRecepcion.getEstado())) {
			comprobante.setArchivo(rutaArchivoFirmadoXml);
			respuesta.setEstado(Estado.RECIBIDA.getDescripcion());
			
			
			try {
				Thread.currentThread();
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				log.debug(e.getMessage(), e);
			}
			
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(wsdlLocationAutorizacion, claveAcceso);
			
			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
				try {

					respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
					respuesta.setClaveAccesoComprobante(claveAcceso);
					
					respuesta.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion()
									.toGregorianCalendar().getTime()
									: null);
					respuesta.setMensajeSistema("Comprobante AUTORIZADO");
					respuesta.setMensajeCliente("Comprobante AUTORIZADO");
										
					 comprobante.setNumeroAutorizacion(claveAcceso);
					 
						if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
							try {
								String comprobanteXML = respAutorizacionComExt.getComprobante();
								comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
								comprobanteXML = comprobanteXML.replace("]]>", "");

								com.gizlocorp.gnvoice.xml.guia.GuiaRemision factAutorizadaXML = getGuiaXML(comprobanteXML);
								ReporteUtil reporte = new ReporteUtil();

								rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_GUIA_REMISION
																					,new GuiaRemisionReporte(factAutorizadaXML)
																					,respAutorizacionComExt.getNumeroAutorizacion()
																					,FechaUtil.formatearFecha(respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime(),FechaUtil.patronFechaTiempo24)
																					,factAutorizadaXML
																					,factAutorizadaXML.getInfoGuiaRemision().getFechaIniTransporte()
																					,"autorizado"
																					,false
																					,emisor.getLogoEmpresa());
							} catch (Exception e) {
								log.debug(e.getMessage(), e);
							}
						}
					
						try {

							rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getGuiaXML(respAutorizacionComExt)
																					,mensaje.getGuia().getInfoGuiaRemision().getFechaIniTransporte()
																					,claveAcceso
																					,TipoComprobante.FACTURA.getDescripcion()
																					,"autorizado");

						} catch (Exception e) {
							log.debug(e.getMessage(), e);
						}
					
					 comprobante.setArchivo(rutaArchivoAutorizacionXml);
		             comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
		             comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
					 
					try {
						servicioGuia.autorizaOffline(comprobante);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioGuia = (ServicioGuia) ic.lookup("java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia");
							servicioGuia.autorizaOffline(comprobante);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}
					
					return respuesta;
				} catch (Exception ex) {
					log.warn("Validar Autorizacion");
				}
			} 

			comprobante.setNumeroAutorizacion(claveAcceso);
			comprobante.setArchivo(rutaArchivoFirmadoXml);
			comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
			comprobante.setEstado(Estado.RECIBIDA.getDescripcion());

	        return respuesta;


		} else if ("DEVUELTA".equals(respuestaRecepcion.getEstado())) {
			// DEVUELTA
			// Configura valores de respuesta
			StringBuilder mensajeRespuesta = new StringBuilder();
			if (respuestaRecepcion.getComprobantes() != null
					&& respuestaRecepcion.getComprobantes().getComprobante() != null
					&& !respuestaRecepcion.getComprobantes().getComprobante()
							.isEmpty()) {
				MensajeRespuesta msgObj = null;
				for (ec.gob.sri.comprobantes.ws.offline.rec.Comprobante comprobanteRecepcion : respuestaRecepcion
						.getComprobantes().getComprobante()) {
					if (comprobanteRecepcion.getMensajes() != null) {
						for (ec.gob.sri.comprobantes.ws.offline.rec.Mensaje msg : comprobanteRecepcion
								.getMensajes().getMensaje()) {
							msgObj = new MensajeRespuesta();
							msgObj.setIdentificador(msg.getIdentificador());
							msgObj.setInformacionAdicional(msg
									.getInformacionAdicional());
							msgObj.setMensaje(msg.getMensaje());
							msgObj.setTipo(msg.getTipo());
							respuesta.getMensajes().add(msgObj);

							mensajeRespuesta.append("Identificador: ");
							mensajeRespuesta.append(msg.getIdentificador());
							mensajeRespuesta.append(" Mensaje: ");
							mensajeRespuesta.append(msg.getMensaje());
							mensajeRespuesta.append(" Informacion Adicional: ");
							mensajeRespuesta.append(msg
									.getInformacionAdicional());
							mensajeRespuesta.append(" <br>");
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

			return respuesta;

		} else {
			respuesta
					.setMensajeCliente("TRANSMITIDO SIN RESPUESTA CONTIGENCIA");

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
	public GuiaConsultarResponse consultar(GuiaConsultarRequest mensaje) {
		GuiaConsultarResponse respuesta = new GuiaConsultarResponse();
		List<com.gizlocorp.gnvoice.xml.guia.GuiaRemision> comprobantes = new ArrayList<com.gizlocorp.gnvoice.xml.guia.GuiaRemision>();
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

				List<com.gizlocorp.gnvoice.modelo.GuiaRemision> items = servicioGuia
						.obtenerComprobanteConsulta(mensaje.getRuc(),
								mensaje.getClaveAcceso(),
								mensaje.getSecuencial(),
								mensaje.getCodigoExterno(), mensaje.getEstado());
				if (items != null && !items.isEmpty()) {
					for (com.gizlocorp.gnvoice.modelo.GuiaRemision item : items) {
						try {
							String xmlString = DocumentUtil
									.readContentFile(item.getArchivo()
											.toString());

							com.gizlocorp.gnvoice.xml.guia.GuiaRemision guiaXML = getGuiaXML(xmlString);
							comprobantes.add(guiaXML);
						} catch (Exception ex) {
							log.error(
									"Ha ocurrido un error al leer comprobante ",
									ex);
						}
					}
				}

				respuesta.setEstado("");
				respuesta.setGuias(comprobantes);
			}
		} catch (Exception ex) {
			// respuesta.setGuiaRemisions(new ArrayList<factu>());
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			MensajeRespuesta msgObj = new MensajeRespuesta();
			msgObj.setIdentificador("-100");
			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
			respuesta.getMensajes().add(msgObj);

			log.error(ex.getMessage(), ex);
		}

		return respuesta;
	}

	@Override
	public GuiaRecibirResponse recibir(GuiaRecibirRequest mensaje) {
		GuiaRecibirResponse respuesta = new GuiaRecibirResponse();
		try {

			String autorizacion = DocumentUtil.readContentFile(mensaje
					.getComprobanteProveedor());

			Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);

			String documento = autorizacionXML.getComprobante();
			documento = documento.replace("<![CDATA[", "");
			documento = documento.replace("]]>", "");

			com.gizlocorp.gnvoice.xml.guia.GuiaRemision documentoXML = getGuiaXML(documento);

			com.gizlocorp.gnvoice.modelo.GuiaRemision documentoObj = servicioGuia
					.obtenerComprobante(documentoXML.getInfoTributaria()
							.getClaveAcceso(), null, null, null);

			if (documentoObj == null) {
				documentoObj = ComprobanteUtil
						.convertirEsquemaAEntidadGuiaRemision(documentoXML);

				documentoObj.setArchivo(mensaje.getComprobanteProveedor());
				documentoObj.setNumeroAutorizacion(autorizacionXML
						.getNumeroAutorizacion());
				documentoObj.setEstado("AUTORIZADO");
				documentoObj.setArchivo(mensaje.getComprobanteProveedor());
				documentoObj.setTareaActual(Tarea.AUT);
				documentoObj.setTipoGeneracion(TipoGeneracion.REC);
				documentoObj
						.setFechaAutorizacion(autorizacionXML
								.getFechaAutorizacion().toGregorianCalendar()
								.getTime());

				Organizacion emisor = cacheBean
						.obtenerOrganizacion(documentoXML.getInfoTributaria()
								.getRuc());

				if (emisor == null) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta
							.setMensajeSistema("No existe registrado un emisor con RUC: "
									+ documentoXML.getInfoTributaria().getRuc());
					respuesta
							.setMensajeCliente("No existe registrado un emisor con RUC: "
									+ documentoXML.getInfoTributaria().getRuc());
					return respuesta;
				}

				Parametro dirParametro = cacheBean.consultarParametro(
						Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
				String dirServidor = dirParametro != null ? dirParametro
						.getValor() : "";
				try {
					ReporteUtil reporte = new ReporteUtil();
					String rutaArchivoAutorizacionPdf = reporte.generarReporte(
							Constantes.REP_GUIA_REMISION,
							new GuiaRemisionReporte(documentoXML),
							autorizacionXML.getNumeroAutorizacion(), FechaUtil
									.formatearFecha(autorizacionXML
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											FechaUtil.patronFechaTiempo24),
							documentoXML, documentoXML.getInfoGuiaRemision()
									.getFechaIniTransporte(), "recibido",
							false, dirServidor
									+ "/gnvoice/recursos/reportes/coffee.jpg");

					documentoObj.setArchivoLegible(rutaArchivoAutorizacionPdf);

				} catch (Exception ex) {
					log.warn(ex.getMessage(), ex);
				}

				servicioGuia.recibirGuiaRemision(documentoObj);

				respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
				respuesta
						.setMensajeCliente("El comprobante se a cargado de forma exitosa");
				respuesta
						.setMensajeSistema("El comprobante se a cargado de forma exitosa");

			} else {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta
						.setMensajeCliente("Ha ocurrido un problema: comprobante ya existe");
				respuesta
						.setMensajeSistema("Ha ocurrido un problema: comprobante ya existe");
			}

		} catch (Exception ex) {
			respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			MensajeRespuesta msgObj = new MensajeRespuesta();
			msgObj.setIdentificador("-100");
			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
			respuesta.getMensajes().add(msgObj);
			respuesta.setMensajeCliente("Ha ocurrido un problema");
			respuesta.setMensajeSistema(ex.getMessage());

		}

		return respuesta;
	}
	
	
	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,
			String claveAcceso) {
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

		 log.info("CLAVE DE ACCESO AUTORIZAR OFFLINE: " + claveAcceso);
		int timeout = 7000;

		try {
			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
					new URL(wsdlLocation),
					new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));

			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion
					.getAutorizacionComprobantesOfflinePort();

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
					timeout);

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
					timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);

			ec.gob.sri.comprobantes.ws.offline.aut.RespuestaComprobante respuestaComprobanteAut = autorizacionws
					.autorizacionComprobante(claveAcceso);

			// 2 reintentos
			for (int i = 0; i < 2; i++) {
				if (respuestaComprobanteAut != null && respuestaComprobanteAut.getAutorizaciones() != null
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
						&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {
					break;
				} else {
					// log.info("Renvio por autorizacion en blanco");
					respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

				}
			}

			if (respuestaComprobanteAut != null && respuestaComprobanteAut.getAutorizaciones() != null
					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
					&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {

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

				autorizacion.setComprobante("<![CDATA[" + autorizacion.getComprobante() + "]]>");

				if (autorizacion.getMensajes() != null && autorizacion.getMensajes().getMensaje() != null
						&& !autorizacion.getMensajes().getMensaje().isEmpty()) {

					for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msj : autorizacion.getMensajes().getMensaje()) {
						msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils.escapeXml(msj.getMensaje()) : null);
						msj.setInformacionAdicional(msj.getInformacionAdicional() != null
								? StringEscapeUtils.escapeXml(msj.getInformacionAdicional()) : null);
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

	private String getGuiaXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.guia.GuiaRemision getGuiaXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirAObjeto(comprobanteXML,
				com.gizlocorp.gnvoice.xml.guia.GuiaRemision.class);
	}

	private Autorizacion getAutorizacionXML(String comprobanteXML)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirAObjeto(comprobanteXML, Autorizacion.class);
	}
	
	

	//para off line
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

				 log.info("Inicia proceso reccepcion offline SRI 111...");
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion.getRecepcionComprobantesOfflinePort();

				 log.info("Inicia proceso reccepcion offline SRI ...222");

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", retardo);

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.request.timeout", retardo);
 
				 log.info("Validando en reccepcion offline SRI 333..." +documentFirmado.length);

				respuesta = recepcion.validarComprobante(documentFirmado);

				log.info("Validando en reccepcion offline SRI 444..." + respuesta.getEstado());

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
