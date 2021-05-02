package com.gizlocorp.gnvoice.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
//import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.component.bean.Service;

import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.excepcion.GizloException;
//import com.gizlocorp.adm.modelo.Bitacora;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.servicio.local.ServicioCodigoBarrasLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.MailDelivery;
import com.gizlocorp.adm.utilitario.MailMessage;
import com.gizlocorp.adm.utilitario.StringUtil;
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
//import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.service.Factura;
//import com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ConstantesRim;
import com.gizlocorp.gnvoice.utilitario.FechaUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Compensaciones.Compensacion;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoice.xml.factura.Impuesto;
import com.gizlocorp.gnvoice.xml.message.CeDisResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarResponse;
//import com.gizlocorp.gnvoice.xml.message.ComprobanteRequest;
//import com.gizlocorp.gnvoice.xml.message.ComprobanteResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoice.xml.message.ValidItem;

//import com.sun.tools.internal.ws.processor.model.Request;
import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
//import com.gizlocorp.adm.servicio.local.ServicioBitacoraEventoLocal;
import oracle.retail.integration.custom.bo.cedisservice.v1.CeDisOrderRucRequest;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.CeDisOrderService;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.CeDisWS;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.FaultExceptionMessage;

//@Interceptors(CurrentUserProvider.class)
@Service(Factura.class)
@Stateless
public class FacturaBean implements Factura {
	public static final Logger log = LoggerFactory.getLogger(FacturaBean.class);

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRecepcionFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura")
	ServicioRecepcionFactura servicioRecepcionFactura;

	CeDisResponse ceDisresponse = new CeDisResponse();
	// @EJB(lookup =
	// "java:global/gnvoice-ejb/ServicioClaveContigenciaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia")
	// ServicioClaveContigencia servicioClaveContigencia;

	@EJB
	CacheBean cacheBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioCodigoBarrasImpl!com.gizlocorp.adm.servicio.local.ServicioCodigoBarrasLocal")
	ServicioCodigoBarrasLocal servicioCodigoBarrasLocal;
	

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroOrdenCompraImpl!com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal")
	ServicioParametroOrdenCompraLocal servicioParametroOrdenCompra;

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FacturaProcesarResponse procesarOffline(FacturaProcesarRequest mensaje) {
		FacturaProcesarResponse respuesta = new FacturaProcesarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

		log.info("Inicia proceso facturacion metodo offline");
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

		com.gizlocorp.gnvoice.modelo.Factura comprobante = null;
		
		

		try {
			log.info("Inicia validaciones previas metodo offline");
			if (mensaje.getFactura() == null || mensaje.getFactura().getInfoTributaria() == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema(
						"Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
				respuesta.setMensajeCliente("Datos de comprobante incorrectos. Factura vacia");
				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getFactura().getInfoTributaria().getRuc());
			
			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);
				respuesta.setMensajeSistema(
						"No existe registrado un emisor con RUC: " + mensaje.getFactura().getInfoTributaria().getRuc());
				respuesta.setMensajeCliente(
						"No existe registrado un emisor con RUC: " + mensaje.getFactura().getInfoTributaria().getRuc());
				return respuesta;
			}

			if (mensaje.getFactura().getInfoTributaria().getSecuencial() == null
					|| mensaje.getFactura().getInfoTributaria().getSecuencial().isEmpty()) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);
				respuesta.setMensajeSistema("Secuencial no puede ser NULL");
				respuesta.setMensajeCliente("Secuencial no puede ser NULL");
				return respuesta;
			}
			
			/*try {
				System.out.println("logo oferton "+mensaje.getBanderaOferton()+"----------"+ emisor.getLogoEmpresa());
				if("S".equals(mensaje.getBanderaOferton())){
					emisor.setLogoEmpresa(emisor.getLogoEmpresaOpcional());	
				}
				System.out.println("logo Fin oferton ----------"+ emisor.getLogoEmpresa());
			} catch (Exception e) {
				
			}*/
			


			Parametro ambParametro = cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
			Parametro monParamtro = cacheBean.consultarParametro(Constantes.MONEDA, emisor.getId());
			Parametro codFacParametro = cacheBean.consultarParametro(Constantes.COD_FACTURA, emisor.getId());
			// ambiente = ambParametro.getValor();

			// TODO CAMBIAR EN PRODUCCION cel.sri.gob.ec
			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
			wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
			
			 // wsdlLocationRecepcion = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
		    //  wsdlLocationAutorizacion = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

			com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;
			try {
				comprobanteExistente = servicioFactura.obtenerComprobante(null, mensaje.getCodigoExterno(),mensaje.getFactura().getInfoTributaria().getRuc(), mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				servicioFactura = (ServicioFactura) ic.lookup(
						"java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
				comprobanteExistente = servicioFactura.obtenerComprobante(null, mensaje.getCodigoExterno(),
						mensaje.getFactura().getInfoTributaria().getRuc(), mensaje.getAgencia());

			}

			if (comprobanteExistente != null
					&& Estado.AUTORIZADO.getDescripcion().equals(comprobanteExistente.getEstado())) {
				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");
				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
				respuesta.setFechaAutorizacion(comprobanteExistente.getFechaAutorizacion());
				respuesta.setNumeroAutorizacion(comprobanteExistente.getNumeroAutorizacion());

				return respuesta;
			}

			// Setea nodo principal
			mensaje.getFactura().setId("comprobante");
			// mensaje.getFactura().setVersion(Constantes.VERSION);
			mensaje.getFactura().setVersion(Constantes.VERSION_1);

			mensaje.getFactura().getInfoFactura().setMoneda(monParamtro.getValor());
			mensaje.getFactura().getInfoTributaria().setAmbiente(ambParametro.getValor());
			mensaje.getFactura().getInfoTributaria().setCodDoc(codFacParametro.getValor());

			if (mensaje.getFactura().getInfoTributaria().getSecuencial().length() < 9) {
				StringBuilder secuencial = new StringBuilder();

				int secuencialTam = 9 - mensaje.getFactura().getInfoTributaria().getSecuencial().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
				mensaje.getFactura().getInfoTributaria().setSecuencial(secuencial.toString());
			}

			mensaje.getFactura().getInfoTributaria().setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			String dirMatriz = emisor.getDireccion();

			mensaje.getFactura().getInfoTributaria().setDirMatriz(dirMatriz != null
					? StringEscapeUtils.escapeXml(dirMatriz.trim()).replaceAll("[\n]", "").replaceAll("[\b]", "")
					: null);

			if (mensaje.getFactura().getInfoTributaria().getEstab() == null
					|| mensaje.getFactura().getInfoTributaria().getEstab().isEmpty()) {
				mensaje.getFactura().getInfoTributaria().setEstab(emisor.getEstablecimiento());

			}

			if (mensaje.getFactura().getInfoTributaria().getNombreComercial() == null
					|| mensaje.getFactura().getInfoTributaria().getNombreComercial().isEmpty()) {
				mensaje.getFactura().getInfoTributaria()
						.setNombreComercial(emisor.getNombreComercial() != null
								? StringEscapeUtils.escapeXml(emisor.getNombreComercial().trim()).replaceAll("[\n]", "")
										.replaceAll("[\b]", "")
								: null);
			}

			if (mensaje.getFactura().getInfoTributaria().getPtoEmi() == null
					|| mensaje.getFactura().getInfoTributaria().getPtoEmi().isEmpty()) {
				mensaje.getFactura().getInfoTributaria().setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getFactura().getInfoTributaria().setRazonSocial(emisor.getNombreComercial().trim() != null
					? StringEscapeUtils.escapeXml(emisor.getNombreComercial().trim()) : null);

			mensaje.getFactura().getInfoFactura().setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			
			if (mensaje.getFactura().getInfoFactura().getDirEstablecimiento() == null
					|| mensaje.getFactura().getInfoFactura().getDirEstablecimiento().isEmpty()) {
				mensaje.getFactura().getInfoFactura().setDirEstablecimiento(emisor.getDirEstablecimiento() != null
						? StringEscapeUtils.escapeXml(emisor.getDirEstablecimiento()) : null);
			}
			mensaje.getFactura().getInfoFactura()
					.setObligadoContabilidad(emisor.getEsObligadoContabilidad().getDescripcion().toUpperCase());

			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getEstab());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getPtoEmi());
			log.debug("codigo numerico - secuencial de factura: "
					+ mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);

			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(
					mensaje.getFactura().getInfoFactura().getFechaEmision(), codFacParametro.getValor(),
					mensaje.getFactura().getInfoTributaria().getRuc(),
					mensaje.getFactura().getInfoTributaria().getAmbiente(), codigoNumerico.toString());

			log.info("codigo numerico - secuencial de factura: " + claveAcceso);
			mensaje.getFactura().getInfoTributaria().setClaveAcceso(claveAcceso);

			// -jose--------------------------------------------------------------------------------------------------------------------------------*/

			log.info("finaliza validaciones previas");
			log.debug("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil.convertirEsquemaAEntidadFactura(mensaje.getFactura());

			if (comprobanteExistente != null && comprobanteExistente.getId() != null) {
				comprobante.setId(comprobanteExistente.getId());
				comprobante.setEstado(comprobanteExistente.getEstado());
			}
			
			comprobante.setIsOferton(mensaje.getBanderaOferton());
			comprobante.setClaveInterna(mensaje.getCodigoExterno());
			comprobante.setCorreoNotificacion(mensaje.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getAgencia());

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			comprobante.setSecuencialOriginal(mensaje.getSid());
			comprobante.setModoEnvio("OFFLINE");

			if ((comprobanteExistente != null) && (comprobanteExistente.getId() != null)) {
				comprobante.setId(comprobanteExistente.getId());
				comprobante.setEstado(comprobanteExistente.getEstado());
				comprobante.setClaveInterna(comprobanteExistente.getClaveInterna());
			}

			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(wsdlLocationAutorizacion, claveAcceso);

			log.info("pasando validacion de autorizacion " + claveAcceso);

			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {

				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

				respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
				respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null ? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
				respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
				respuesta.setClaveAccesoComprobante(claveAcceso);

				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				comprobante.setNumeroAutorizacion(claveAcceso);
				comprobante.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null ? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);

				if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
					try {
						String comprobanteXML = respAutorizacionComExt.getComprobante();
						comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
						comprobanteXML = comprobanteXML.replace("]]>", "");

						com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobanteXML);
						ReporteUtil reporte = new ReporteUtil();

						rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_FACTURA_OFFLINE
																		   , new FacturaReporte(factAutorizadaXML)
																		   , respAutorizacionComExt.getNumeroAutorizacion()
																		   , FechaUtil.formatearFecha(respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
																		   , FechaUtil.patronFechaTiempo24)
																		   , factAutorizadaXML.getInfoFactura().getFechaEmision()
																		   , "autorizado"
																		   , false
																		   , "S".equals(mensaje.getBanderaOferton())?emisor.getLogoEmpresaOpcional():emisor.getLogoEmpresa());
						
						log.info("rutaArchivoAutorizacionPdf -> "+rutaArchivoAutorizacionPdf);

					} catch (Exception e) {
						log.info(e.getMessage(), e);
					}
				}
				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
				
				try {

					rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getFacturaXML(respAutorizacionComExt),
							mensaje.getFactura().getInfoFactura().getFechaEmision(), claveAcceso,
							TipoComprobante.FACTURA.getDescripcion(), "autorizado");

				} catch (Exception e) {
					log.info(e.getMessage(), e);
				}
				
				comprobante.setArchivo(rutaArchivoAutorizacionXml);
				comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
				comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
				
				try {
					if (mensaje.getCorreoElectronicoNotificacion() != null&& !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
						Plantilla t = cacheBean.obtenerPlantilla(Constantes.NOTIFIACION, emisor.getId());

						Map<String, Object> parametrosBody = new HashMap<String, Object>();
						parametrosBody.put("nombre", mensaje.getFactura().getInfoFactura().getRazonSocialComprador());
						parametrosBody.put("tipo", "FACTURA");
						parametrosBody.put("secuencial", mensaje.getFactura().getInfoTributaria().getSecuencial());
						parametrosBody.put("fechaEmision", mensaje.getFactura().getInfoFactura().getFechaEmision());
						parametrosBody.put("total", mensaje.getFactura().getInfoFactura().getImporteTotal());
						parametrosBody.put("emisor", emisor.getNombre());
						parametrosBody.put("ruc", emisor.getRuc());
						parametrosBody.put("numeroAutorizacion", claveAcceso);

						MailMessage mailMensaje = new MailMessage();
						mailMensaje.setSubject(t.getTitulo().replace("$tipo", "FACTURA").replace("$numero",mensaje.getFactura().getInfoTributaria().getSecuencial()));
						
						mailMensaje.setFrom(cacheBean.consultarParametro("S".equals(mensaje.getBanderaOferton())?Constantes.CORREO_REMITE_EL_OFERTON:Constantes.CORREO_REMITE, emisor.getId()).getValor());
						mailMensaje.setTo(Arrays.asList(mensaje.getCorreoElectronicoNotificacion().split(";")));
						mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(parametrosBody, t.getDescripcion(), t.getValor()));
						mailMensaje.setAttachment(Arrays.asList(rutaArchivoAutorizacionXml, rutaArchivoAutorizacionPdf));
						
						MailDelivery.send(mailMensaje,
								cacheBean.consultarParametro(Constantes.SMTP_HOST, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_PORT, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_USERNAME, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_PASSWORD, emisor.getId()).getValorDesencriptado(),
								"S".equals(mensaje.getBanderaOferton())?emisor.getAcronimoOpcional():emisor.getAcronimo());
					}

				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
				
				
				

				try {
					servicioFactura.autorizaOffline(comprobante);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioFactura = (ServicioFactura) ic.lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
						servicioFactura.autorizaOffline(comprobante);
					} catch (Exception ex2) {
						log.warn("no se guardo el comprobante");
					}
				}

				return respuesta;
			} else if ("PROCESAMIENTO".equals(respAutorizacionComExt.getEstado())
					|| "EN PROCESO".equals(respAutorizacionComExt.getEstado())) {
				log.debug("Comprobante en procesamiento: " + comprobante.getClaveAcceso());

				respuesta.setEstado(Estado.PENDIENTE.getDescripcion());
				respuesta.setMensajeSistema("Comprobante en procesamiento" + respAutorizacionComExt.getEstado());
				respuesta.setMensajeCliente("Comprobante en procesamiento" + respAutorizacionComExt.getEstado());
				respuesta.setClaveAccesoComprobante(comprobante.getClaveAcceso());

				return respuesta;

			} else if (!"ERROR_TECNICO".equals(respAutorizacionComExt.getEstado())) {
				log.debug("Comprobante no autorizado:  " + respAutorizacionComExt.getEstado());

				respuesta.setEstado(Estado.RECHAZADO.getDescripcion());
				respuesta.setMensajeSistema("Comprobante No autorizado: " + respAutorizacionComExt.getEstado());
				respuesta.setMensajeCliente("Comprobante No autorizado: " + respAutorizacionComExt.getEstado());
				respuesta.setClaveAccesoComprobante(comprobante.getClaveAcceso());

				MensajeRespuesta msgObj = null;
				StringBuilder mensajeRespuesta = new StringBuilder();
				if (respAutorizacionComExt.getMensajes() != null) {
					for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msg : respAutorizacionComExt.getMensajes()
							.getMensaje()) {
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

			log.info("comprobante offline nuevo " + claveAcceso);

			// Genera comprobante
			rutaArchivoXml = DocumentUtil.createDocument( getFacturaXML(mensaje.getFactura())
														, mensaje.getFactura().getInfoFactura().getFechaEmision()
														, claveAcceso
														, com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA.getDescripcion()
														, "enviado");
			log.info("ruta de archivo es: " 
					+ rutaArchivoXml 
					+ "ruta xsd"
					+ com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS 
					+ "/gnvoice/recursos/xsd/factura.xsd");

			String validacionXsd = ComprobanteUtil.validaArchivoXSD(rutaArchivoXml
																	, com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS 
																	+ "/gnvoice/recursos/xsd/factura.xsd");

			if (validacionXsd != null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Error en esquema comprobante. " + validacionXsd);
				respuesta.setMensajeCliente("Error en la estructura del comprobante enviado.");
				// Actualiza estado comprobante
				log.info("comprobante: " + comprobante + " archivo: " + rutaArchivoXml);
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

			respuesta.setMensajeSistema("Ha ocurrido un error en el sistema: " + ex.getMessage());
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
		FirmaElectronicaResponse firmaResponse = null;

		try {
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.ws.connect.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
					5000L);

			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
					5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.ws.request.timeout", 5000L);

			firmaResponse = firmaElectronica.firmarDocumento(firmaRequest);
		} catch (Exception ex) {
			log.error("********* FIRMA", ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("No se puede firmar: " + ex.getMessage());
			respuesta.setMensajeCliente("No se puede firmar");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

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

		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuestaRecepcion = enviarComprobanteOffline(
				wsdlLocationRecepcion, rutaArchivoFirmadoXml);

		if ("RECIBIDA".equals(respuestaRecepcion.getEstado())) {
			try {
				Thread.currentThread();
				Thread.sleep(60000L);
			} catch (InterruptedException e) {
				log.debug(e.getMessage(), e);
			}
			
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(wsdlLocationAutorizacion, claveAcceso);

			log.info("pasando validacion de autorizacion " + claveAcceso);

			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {

				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

				respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
				respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null ? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
				respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				comprobante.setNumeroAutorizacion(claveAcceso);
				comprobante.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);

				if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
					try {
						String comprobanteXML = respAutorizacionComExt.getComprobante();
						comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
						comprobanteXML = comprobanteXML.replace("]]>", "");

						com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobanteXML);
						ReporteUtil reporte = new ReporteUtil();

						rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_FACTURA_OFFLINE
																		   , new FacturaReporte(factAutorizadaXML)
																		   , respAutorizacionComExt.getNumeroAutorizacion()
																		   , FechaUtil.formatearFecha(respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
																		   , FechaUtil.patronFechaTiempo24)
																		   , factAutorizadaXML.getInfoFactura().getFechaEmision()
																		   , "autorizado"
																		   , false
																		   , "S".equals(mensaje.getBanderaOferton())?emisor.getLogoEmpresaOpcional():emisor.getLogoEmpresa());
						
						log.info("rutaArchivoAutorizacionPdf -> "+rutaArchivoAutorizacionPdf);

					} catch (Exception e) {
						log.info(e.getMessage(), e);
					}
				}
				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
				
				try {

					rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getFacturaXML(respAutorizacionComExt),
							mensaje.getFactura().getInfoFactura().getFechaEmision(), claveAcceso,
							TipoComprobante.FACTURA.getDescripcion(), "autorizado");

				} catch (Exception e) {
					log.info(e.getMessage(), e);
				}
				

				
				comprobante.setArchivo(rutaArchivoAutorizacionXml);
				comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
				comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());


				try {
					if (mensaje.getCorreoElectronicoNotificacion() != null&& !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
						Plantilla t = cacheBean.obtenerPlantilla(Constantes.NOTIFIACION, emisor.getId());

						Map<String, Object> parametrosBody = new HashMap<String, Object>();
						parametrosBody.put("nombre", mensaje.getFactura().getInfoFactura().getRazonSocialComprador());
						parametrosBody.put("tipo", "FACTURA");
						parametrosBody.put("secuencial", mensaje.getFactura().getInfoTributaria().getSecuencial());
						parametrosBody.put("fechaEmision", mensaje.getFactura().getInfoFactura().getFechaEmision());
						parametrosBody.put("total", mensaje.getFactura().getInfoFactura().getImporteTotal());
						parametrosBody.put("emisor", emisor.getNombre());
						parametrosBody.put("ruc", emisor.getRuc());
						parametrosBody.put("numeroAutorizacion", claveAcceso);

						MailMessage mailMensaje = new MailMessage();
						mailMensaje.setSubject(t.getTitulo().replace("$tipo", "FACTURA").replace("$numero",
								mensaje.getFactura().getInfoTributaria().getSecuencial()));
						mailMensaje.setFrom(cacheBean.consultarParametro("S".equals(mensaje.getBanderaOferton())?Constantes.CORREO_REMITE_EL_OFERTON:Constantes.CORREO_REMITE, emisor.getId()).getValor());
						mailMensaje.setTo(Arrays.asList(mensaje.getCorreoElectronicoNotificacion().split(";")));
						mailMensaje.setBody(
								ComprobanteUtil.generarCuerpoMensaje(parametrosBody, t.getDescripcion(), t.getValor()));
						mailMensaje.setAttachment(Arrays.asList(rutaArchivoAutorizacionXml, rutaArchivoAutorizacionPdf));
						MailDelivery.send(mailMensaje,
								cacheBean.consultarParametro(Constantes.SMTP_HOST, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_PORT, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_USERNAME, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_PASSWORD, emisor.getId())
										.getValorDesencriptado(),
										 "S".equals(mensaje.getBanderaOferton())?emisor.getAcronimoOpcional():emisor.getAcronimo());
					}

				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
				
				
				try {
					servicioFactura.autorizaOffline(comprobante);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioFactura = (ServicioFactura) ic.lookup(
								"java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
						servicioFactura.autorizaOffline(comprobante);
					} catch (Exception ex2) {
						log.warn("no se guardo el comprobante");
					}
				}
				


				return respuesta;
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

							mensajeRespuesta.append("Identificador: ");
							mensajeRespuesta.append(msg.getIdentificador());
							mensajeRespuesta.append(" Mensaje: ");
							mensajeRespuesta.append(msg.getMensaje());
							mensajeRespuesta.append(" Informacion Adicional: ");
							mensajeRespuesta.append(msg.getInformacionAdicional());
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

	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,
			String claveAcceso) {
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

		// log.info("CLAVE DE ACCESO: " + claveAcceso);
		
		
		System.setProperty("javax.net.ssl.keyStore", "/data/certificados/cacerts"); 
		//System.setProperty("javax.net.ssl.keyStorePassword", "changeit"); 
		System.setProperty("javax.net.ssl.trustStore", "/data/certificados/cacerts"); 
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		System.setProperty("javax.net.ssl.trustAnchors", "/data/certificados/cacerts"); 
		System.setProperty("javax.net.ssl.keyStorePassword", "changeit");  
		//Security.setProperty("ssl.TrustManagerFactory.algorithm" , "XTrust509");
		System.setProperty("org.jboss.security.ignoreHttpsHost", "true");
		
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

	
	
	
	private Autorizacion autorizarComprobante(String wsdlLocation, String claveAcceso) {
		Autorizacion respuesta = null;

		log.info("enterndo sri ");
		int timeout = 7000;

		try {
			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(
					new URL(wsdlLocation),
					new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesService"));

			AutorizacionComprobantes autorizacionws = servicioAutorizacion.getAutorizacionComprobantesPort();

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
					timeout);

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
					timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);
			log.info("enterndo sri22 ");
			RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

			// 2 reintentos
			for (int i = 0; i < 2; i++) {
				if (respuestaComprobanteAut != null && respuestaComprobanteAut.getAutorizaciones() != null
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
						&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {
					break;
				} else {
					// log.debug("Renvio por autorizacion en blanco");
					respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

				}
			}
			log.info("enterndo sri 33");

			if (respuestaComprobanteAut != null && respuestaComprobanteAut.getAutorizaciones() != null
					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
					&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {

				Autorizacion autorizacion = respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0);

				for (Autorizacion aut : respuestaComprobanteAut.getAutorizaciones().getAutorizacion()) {
					if (aut != null && "AUTORIZADO".equals(aut.getEstado())) {
						autorizacion = aut;
						break;
					}
				}
				log.info("enterndo sri 444");

				autorizacion.setAmbiente(null);

				autorizacion.setComprobante("<![CDATA[" + autorizacion.getComprobante() + "]]>");

				if (autorizacion.getMensajes() != null && autorizacion.getMensajes().getMensaje() != null
						&& !autorizacion.getMensajes().getMensaje().isEmpty()) {

					for (Mensaje msj : autorizacion.getMensajes().getMensaje()) {
						msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils.escapeXml(msj.getMensaje()) : null);
						msj.setInformacionAdicional(msj.getInformacionAdicional() != null
								? StringEscapeUtils.escapeXml(msj.getInformacionAdicional()) : null);
					}
				}

				log.info("enterndo sri555 ");

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

	// para off line
	private ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud enviarComprobanteOffline(String wsdlLocation,
			String rutaArchivoXml) {
		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuesta = null;

		int retardo = 8000;

		try {
			byte[] documentFirmado = readFile(rutaArchivoXml);
			log.info("Inicia proceso reccepcion offline SRI ..." + wsdlLocation + " ..." + rutaArchivoXml);
			// while (contador <= reintentos) {
			try {
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService servicioRecepcion = new ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService(
						new URL(wsdlLocation));

				// log.info("Inicia proceso reccepcion offline SRI 111...");
				ec.gob.sri.comprobantes.

						ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion
								.getRecepcionComprobantesOfflinePort();

				// log.info("Inicia proceso reccepcion offline SRI ...222");

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
						retardo);

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
						retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.request.timeout", retardo);

				// log.info("Validando en reccepcion offline SRI
				// 333..."+documentFirmado.length);

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

	private RespuestaSolicitud enviarComprobante(String wsdlLocation, String rutaArchivoXml) {
		RespuestaSolicitud respuesta = null;

		int retardo = 4000;

		try {
			byte[] documentFirmado = DocumentUtil.readFile(rutaArchivoXml);
			// log.debug("Inicia proceso interoperabilidad SRI ...");
			// while (contador <= reintentos) {
			try {
				RecepcionComprobantesService servicioRecepcion = new RecepcionComprobantesService(
						new URL(wsdlLocation));
				RecepcionComprobantes recepcion = servicioRecepcion.getRecepcionComprobantesPort();

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
						retardo);

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
						retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.request.timeout", retardo);

				respuesta = recepcion.validarComprobante(documentFirmado);

				// log.debug("Estado enviado del SRI del servicio Recepcion:
				// ...:"
				// + respuesta.getEstado());

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
	public FacturaProcesarResponse procesar(FacturaProcesarRequest mensaje) {
		FacturaProcesarResponse respuesta = new FacturaProcesarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

		log.info("Inicia proceso facturacion compensacion");
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

		com.gizlocorp.gnvoice.modelo.Factura comprobante = null;

		// boolean booCatorce = false;

		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// GENERA DE COMPROBANTE
		// TODO contigencia
		// boolean existioComprobante = false;
		try {

			log.debug("Inicia validaciones previas");
			if (mensaje.getFactura() == null || mensaje.getFactura().getInfoTributaria() == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema(
						"Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
				respuesta.setMensajeCliente("Datos de comprobante incorrectos. Factura vacia");

				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getFactura().getInfoTributaria().getRuc());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema(
						"No existe registrado un emisor con RUC: " + mensaje.getFactura().getInfoTributaria().getRuc());
				respuesta.setMensajeCliente(
						"No existe registrado un emisor con RUC: " + mensaje.getFactura().getInfoTributaria().getRuc());
				return respuesta;
			}

			if (mensaje.getFactura().getInfoTributaria().getSecuencial() == null
					|| mensaje.getFactura().getInfoTributaria().getSecuencial().isEmpty()) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Secuencial no puede ser NULL");
				respuesta.setMensajeCliente("Secuencial no puede ser NULL");
				return respuesta;
			}

			Parametro ambParametro = cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
			Parametro monParamtro = cacheBean.consultarParametro(Constantes.MONEDA, emisor.getId());
			Parametro codFacParametro = cacheBean.consultarParametro(Constantes.COD_FACTURA, emisor.getId());
			ambiente = ambParametro.getValor();

			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
			wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
			
			
			//wsdlLocationRecepcion = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
		    //  wsdlLocationAutorizacion = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
			

			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				wsdlLocationRecepcion = cacheBean
						.consultarParametro(Constantes.SERVICIO_REC_SRI_PRODUCCION, emisor.getId()).getValor();

			} else {
				wsdlLocationRecepcion = cacheBean.consultarParametro(Constantes.SERVICIO_REC_SRI_PRUEBA, emisor.getId())
						.getValor();
			}
			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				wsdlLocationAutorizacion = cacheBean
						.consultarParametro(Constantes.SERVICIO_AUT_SRI_PRODUCCION, emisor.getId()).getValor();
			} else {
				wsdlLocationAutorizacion = cacheBean
						.consultarParametro(Constantes.SERVICIO_AUT_SRI_PRUEBA, emisor.getId()).getValor();
			}

			com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;
			try {
				comprobanteExistente = servicioFactura.obtenerComprobante(null, mensaje.getCodigoExterno(),
						mensaje.getFactura().getInfoTributaria().getRuc(), mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				servicioFactura = (ServicioFactura) ic.lookup(
						"java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");

				comprobanteExistente = servicioFactura.obtenerComprobante(null, mensaje.getCodigoExterno(),
						mensaje.getFactura().getInfoTributaria().getRuc(), mensaje.getAgencia());

			}

			if (comprobanteExistente != null
					&& Estado.AUTORIZADO.getDescripcion().equals(comprobanteExistente.getEstado())) {
				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");
				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
				respuesta.setFechaAutorizacion(comprobanteExistente.getFechaAutorizacion());
				respuesta.setNumeroAutorizacion(comprobanteExistente.getNumeroAutorizacion());

				return respuesta;
			}

			// Setea nodo principal
			mensaje.getFactura().setId("comprobante");
			// mensaje.getFactura().setVersion(Constantes.VERSION);
			mensaje.getFactura().setVersion(Constantes.VERSION_1);

			mensaje.getFactura().getInfoFactura().setMoneda(monParamtro.getValor());
			mensaje.getFactura().getInfoTributaria().setAmbiente(ambParametro.getValor());
			mensaje.getFactura().getInfoTributaria().setCodDoc(codFacParametro.getValor());

			if (mensaje.getFactura().getInfoTributaria().getSecuencial().length() < 9) {
				StringBuilder secuencial = new StringBuilder();

				int secuencialTam = 9 - mensaje.getFactura().getInfoTributaria().getSecuencial().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
				mensaje.getFactura().getInfoTributaria().setSecuencial(secuencial.toString());
			}

			mensaje.getFactura().getInfoTributaria().setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			String dirMatriz = emisor.getDireccion();

			mensaje.getFactura().getInfoTributaria().setDirMatriz(dirMatriz != null
					? StringEscapeUtils.escapeXml(dirMatriz.trim()).replaceAll("[\n]", "").replaceAll("[\b]", "")
					: null);

			if (mensaje.getFactura().getInfoTributaria().getEstab() == null
					|| mensaje.getFactura().getInfoTributaria().getEstab().isEmpty()) {
				mensaje.getFactura().getInfoTributaria().setEstab(emisor.getEstablecimiento());

			}

			if (mensaje.getFactura().getInfoTributaria().getNombreComercial() == null
					|| mensaje.getFactura().getInfoTributaria().getNombreComercial().isEmpty()) {
				mensaje.getFactura().getInfoTributaria()
						.setNombreComercial(emisor.getNombreComercial() != null
								? StringEscapeUtils.escapeXml(emisor.getNombreComercial().trim()).replaceAll("[\n]", "")
										.replaceAll("[\b]", "")
								: null);
			}

			if (mensaje.getFactura().getInfoTributaria().getPtoEmi() == null
					|| mensaje.getFactura().getInfoTributaria().getPtoEmi().isEmpty()) {
				mensaje.getFactura().getInfoTributaria().setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getFactura().getInfoTributaria().setRazonSocial(emisor.getNombreComercial().trim() != null
					? StringEscapeUtils.escapeXml(emisor.getNombreComercial().trim()) : null);

			mensaje.getFactura().getInfoFactura().setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			if (mensaje.getFactura().getInfoFactura().getDirEstablecimiento() == null
					|| mensaje.getFactura().getInfoFactura().getDirEstablecimiento().isEmpty()) {
				mensaje.getFactura().getInfoFactura().setDirEstablecimiento(emisor.getDirEstablecimiento() != null
						? StringEscapeUtils.escapeXml(emisor.getDirEstablecimiento()) : null);
			}
			mensaje.getFactura().getInfoFactura()
					.setObligadoContabilidad(emisor.getEsObligadoContabilidad().getDescripcion().toUpperCase());

			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getEstab());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getPtoEmi());
			log.debug("codigo numerico - secuencial de factura: "
					+ mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);

			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(
					mensaje.getFactura().getInfoFactura().getFechaEmision(), codFacParametro.getValor(),
					mensaje.getFactura().getInfoTributaria().getRuc(),
					mensaje.getFactura().getInfoTributaria().getAmbiente(), codigoNumerico.toString());

			log.info("codigo numerico - secuencial de factura: " + claveAcceso);
			mensaje.getFactura().getInfoTributaria().setClaveAcceso(claveAcceso);
			// -jose--------------------------------------------------------------------------------------------------------------------------------*/

			log.info("finaliza validaciones previas");
			log.debug("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil.convertirEsquemaAEntidadFactura(mensaje.getFactura());

			comprobante.setClaveInterna(mensaje.getCodigoExterno());
			comprobante.setCorreoNotificacion(mensaje.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getAgencia());

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			comprobante.setSecuencialOriginal(mensaje.getSid());
			Autorizacion respAutorizacionComExt = autorizarComprobante(wsdlLocationAutorizacion, claveAcceso);

			if (comprobanteExistente != null && comprobanteExistente.getId() != null) {
				comprobante.setId(comprobanteExistente.getId());
				comprobante.setEstado(comprobanteExistente.getEstado());
				comprobante.setClaveInterna(comprobanteExistente.getClaveInterna());
			}

			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
				try {
					// Genera comprobante legible
					if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
						try {
							String comprobanteXML = respAutorizacionComExt.getComprobante();
							comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
							comprobanteXML = comprobanteXML.replace("]]>", "");

							com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobanteXML);
							ReporteUtil reporte = new ReporteUtil();

							rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_FACTURA,
									new FacturaReporte(factAutorizadaXML),
									respAutorizacionComExt.getNumeroAutorizacion(),
									FechaUtil.formatearFecha(respAutorizacionComExt.getFechaAutorizacion()
											.toGregorianCalendar().getTime(), FechaUtil.patronFechaTiempo24),
									factAutorizadaXML.getInfoFactura().getFechaEmision(), "autorizado", false,
									emisor.getLogoEmpresa());

						} catch (Exception e) {
							log.debug(e.getMessage(), e);
						}
					}
					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
					try {

						rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getFacturaXML(respAutorizacionComExt),
								mensaje.getFactura().getInfoFactura().getFechaEmision(), claveAcceso,
								TipoComprobante.FACTURA.getDescripcion(), "autorizado");

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}

					// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje
					// .getFactura().getInfoFactura()
					// .getIdentificacionComprador());

					respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
					respuesta.setClaveAccesoComprobante(claveAcceso);
					respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null
							? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
					respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());

					respuesta.setMensajeSistema("Comprobante AUTORIZADO");
					respuesta.setMensajeCliente("Comprobante AUTORIZADO");

					comprobante.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
					comprobante.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null
							? respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
					comprobante.setArchivo(rutaArchivoAutorizacionXml);
					comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);

					try {
						servicioFactura.autoriza(comprobante);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioFactura = (ServicioFactura) ic.lookup(
									"java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
							servicioFactura.autoriza(comprobante);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}

					try {
						if (mensaje.getCorreoElectronicoNotificacion() != null
								&& !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
							Plantilla t = cacheBean.obtenerPlantilla(Constantes.NOTIFIACION, emisor.getId());

							Map<String, Object> parametrosBody = new HashMap<String, Object>();
							parametrosBody.put("nombre",
									mensaje.getFactura().getInfoFactura().getRazonSocialComprador());
							parametrosBody.put("tipo", "FACTURA");
							parametrosBody.put("secuencial", mensaje.getFactura().getInfoTributaria().getSecuencial());
							parametrosBody.put("fechaEmision", mensaje.getFactura().getInfoFactura().getFechaEmision());
							parametrosBody.put("total",
									mensaje.getFactura().getInfoFactura().getImporteTotal().toString());
							parametrosBody.put("emisor", emisor.getNombre());
							parametrosBody.put("ruc", emisor.getRuc());
							parametrosBody.put("numeroAutorizacion", respAutorizacionComExt.getNumeroAutorizacion());

							MailMessage mailMensaje = new MailMessage();
							mailMensaje.setSubject(t.getTitulo().replace("$tipo", "FACTURA").replace("$numero",
									mensaje.getFactura().getInfoTributaria().getSecuencial()));
							mailMensaje.setFrom(
									cacheBean.consultarParametro(Constantes.CORREO_REMITE, emisor.getId()).getValor());
							mailMensaje.setTo(Arrays.asList(mensaje.getCorreoElectronicoNotificacion().split(";")));
							mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(parametrosBody, t.getDescripcion(),
									t.getValor()));
							mailMensaje.setAttachment(
									Arrays.asList(rutaArchivoAutorizacionXml, rutaArchivoAutorizacionPdf));
							MailDelivery.send(mailMensaje,
									cacheBean.consultarParametro(Constantes.SMTP_HOST, emisor.getId()).getValor(),
									cacheBean.consultarParametro(Constantes.SMTP_PORT, emisor.getId()).getValor(),
									cacheBean.consultarParametro(Constantes.SMTP_USERNAME, emisor.getId()).getValor(),
									cacheBean.consultarParametro(Constantes.SMTP_PASSWORD, emisor.getId())
											.getValorDesencriptado(),
									emisor.getAcronimo());
						}

					} catch (Exception ex) {
						log.error(ex.getMessage(), ex);

					}

					return respuesta;
				} catch (Exception ex) {
					log.warn("Validar Autorizacion");
				}
			}

			// Genera comprobante
			rutaArchivoXml = DocumentUtil.createDocument(getFacturaXML(mensaje.getFactura()),
					mensaje.getFactura().getInfoFactura().getFechaEmision(), claveAcceso,
					com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA.getDescripcion(), "enviado");
			log.info("ruta de archivo es: " + rutaArchivoXml + "ruta xsd"
					+ com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS + "/gnvoice/recursos/xsd/factura.xsd");

			String validacionXsd = ComprobanteUtil.validaArchivoXSD(rutaArchivoXml,
					com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS + "/gnvoice/recursos/xsd/factura.xsd");

			if (validacionXsd != null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Error en esquema comprobante. " + validacionXsd);
				respuesta.setMensajeCliente("Error en la estructura del comprobante enviado.");
				// Actualiza estado comprobante
				log.info("comprobante: " + comprobante + " archivo: " + rutaArchivoXml);
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

			respuesta.setMensajeSistema("Ha ocurrido un error en el sistema: " + ex.getMessage());
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
		FirmaElectronicaResponse firmaResponse = null;

		try {
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.ws.connect.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
					5000L);

			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
					5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put("com.sun.xml.ws.request.timeout", 5000L);

			firmaResponse = firmaElectronica.firmarDocumento(firmaRequest);
		} catch (Exception ex) {
			log.error("********* FIRMA", ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("No se puede firmar: " + ex.getMessage());
			respuesta.setMensajeCliente("No se puede firmar");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

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

		comprobante.setArchivo(rutaArchivoFirmadoXml);

		// TODO descomentar contigencia
		// si es nuevo y se contigenia
		// notificas---jose---------------------------------------------------------------------------------------------*/
		// if (existioComprobante) {
		// String rutaArchivoContigenciaPdf = "";
		// if (mensaje.getCorreoElectronicoNotificacion() != null
		// && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
		// try {
		// ReporteUtil reporte = new ReporteUtil();
		// rutaArchivoContigenciaPdf = reporte.generarReporte(
		// Constantes.REP_FACTURA,
		// new FacturaReporte(mensaje.getFactura()), null,
		// null, mensaje.getFactura().getInfoFactura()
		// .getFechaEmision(), "contigencia", false,
		// emisor.getLogoEmpresa());
		//
		// Plantilla t = cacheBean.obtenerPlantilla(
		// Constantes.NOTIFIACION, emisor.getId());
		//
		// Map<String, Object> parametrosBody = new HashMap<String, Object>();
		// parametrosBody.put("nombre", mensaje.getFactura()
		// .getInfoFactura().getRazonSocialComprador());
		// parametrosBody.put("tipo", "FACTURA");
		// parametrosBody.put("secuencial", mensaje.getFactura()
		// .getInfoTributaria().getSecuencial());
		// parametrosBody.put("fechaEmision", mensaje.getFactura()
		// .getInfoFactura().getFechaEmision());
		// parametrosBody.put("total", mensaje.getFactura()
		// .getInfoFactura().getImporteTotal());
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
		// mensaje.getFactura().getInfoTributaria()
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
		// cacheBean.consultarParametro(Constantes.SMTP_HOST,
		// emisor.getId()).getValor(),
		// cacheBean.consultarParametro(Constantes.SMTP_PORT,
		// emisor.getId()).getValor(),
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
		// //TODO grabar en la base
		// respuesta.setEstado(Estado.CONTINGENCIA.getDescripcion());
		// respuesta.setClaveAccesoComprobante(claveAcceso);
		// respuesta.setFechaAutorizacion(null);
		// respuesta.setNumeroAutorizacion(null);
		//
		// respuesta.setMensajeSistema("Comprobante AUTORIZADO");
		// respuesta.setMensajeCliente("Comprobante AUTORIZADO");
		//
		//
		// try {
		// servicioFactura.contingencia(comprobante);
		// } catch (Exception ex) {
		// try {
		// InitialContext ic = new InitialContext();
		// servicioFactura = (ServicioFactura) ic
		// .lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
		// servicioFactura.autoriza(comprobante);
		//
		// } catch (Exception ex2) {
		// log.warn("no se guardo el comprobante");
		// }
		// }
		//
		// return respuesta;
		//
		// }
		// fin--jose--------------------------------------------------------------------------------------------------------------------------------*/
		RespuestaSolicitud respuestaRecepcion = enviarComprobante(wsdlLocationRecepcion, rutaArchivoFirmadoXml);

		if ("RECIBIDA".equals(respuestaRecepcion.getEstado())) {
			comprobante.setArchivo(rutaArchivoFirmadoXml);
			respuesta.setEstado(Estado.RECIBIDA.getDescripcion());
			try {
				Thread.currentThread();
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				log.debug(e.getMessage(), e);
			}
			/*-------------------------------------------------------------------------------------------------------------------------------------*/
			// AUTORIZACION COMPROBANTE
			comprobante.setArchivo(rutaArchivoFirmadoXml);

			Autorizacion respAutorizacion = autorizarComprobante(wsdlLocationAutorizacion, claveAcceso);

			if ("AUTORIZADO".equals(respAutorizacion.getEstado())) {
				if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
					try {
						String comprobanteXML = respAutorizacion.getComprobante();
						comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
						comprobanteXML = comprobanteXML.replace("]]>", "");

						com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobanteXML);

						ReporteUtil reporte = new ReporteUtil();

						rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_FACTURA,
								new FacturaReporte(factAutorizadaXML), respAutorizacion.getNumeroAutorizacion(),
								FechaUtil.formatearFecha(
										respAutorizacion.getFechaAutorizacion().toGregorianCalendar().getTime(),
										FechaUtil.patronFechaTiempo24),
								factAutorizadaXML.getInfoFactura().getFechaEmision(), "autorizado", false,
								emisor.getLogoEmpresa());

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}
				}
				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
				try {
					rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getFacturaXML(respAutorizacion),
							mensaje.getFactura().getInfoFactura().getFechaEmision(), claveAcceso,
							TipoComprobante.FACTURA.getDescripcion(), "autorizado");

				} catch (Exception e) {
					log.debug(e.getMessage(), e);
				}

				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(respAutorizacion.getFechaAutorizacion() != null
						? respAutorizacion.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
				respuesta.setNumeroAutorizacion(respAutorizacion.getNumeroAutorizacion());

				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");

				comprobante.setNumeroAutorizacion(respAutorizacion.getNumeroAutorizacion());
				comprobante.setFechaAutorizacion(respAutorizacion.getFechaAutorizacion() != null
						? respAutorizacion.getFechaAutorizacion().toGregorianCalendar().getTime() : null);
				comprobante.setArchivo(rutaArchivoAutorizacionXml);
				comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);

				try {
					servicioFactura.autoriza(comprobante);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioFactura = (ServicioFactura) ic.lookup(
								"java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
						servicioFactura.autoriza(comprobante);

					} catch (Exception ex2) {
						log.warn("no se guardo el comprobante");
					}
				}

				// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje.getFactura()
				// .getInfoFactura().getIdentificacionComprador());

				try {
					if (mensaje.getCorreoElectronicoNotificacion() != null
							&& !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
						Plantilla t = cacheBean.obtenerPlantilla(Constantes.NOTIFIACION, emisor.getId());

						Map<String, Object> parametrosBody = new HashMap<String, Object>();
						parametrosBody.put("nombre", mensaje.getFactura().getInfoFactura().getRazonSocialComprador());
						parametrosBody.put("tipo", "FACTURA");
						parametrosBody.put("secuencial", mensaje.getFactura().getInfoTributaria().getSecuencial());
						parametrosBody.put("fechaEmision", mensaje.getFactura().getInfoFactura().getFechaEmision());
						parametrosBody.put("total", mensaje.getFactura().getInfoFactura().getImporteTotal());
						parametrosBody.put("emisor", emisor.getNombre());
						parametrosBody.put("ruc", emisor.getRuc());
						parametrosBody.put("numeroAutorizacion", respAutorizacion.getNumeroAutorizacion());

						MailMessage mailMensaje = new MailMessage();
						mailMensaje.setSubject(t.getTitulo().replace("$tipo", "FACTURA").replace("$numero",
								mensaje.getFactura().getInfoTributaria().getSecuencial()));
						mailMensaje.setFrom(
								cacheBean.consultarParametro(Constantes.CORREO_REMITE, emisor.getId()).getValor());
						mailMensaje.setTo(Arrays.asList(mensaje.getCorreoElectronicoNotificacion().split(";")));
						mailMensaje.setBody(
								ComprobanteUtil.generarCuerpoMensaje(parametrosBody, t.getDescripcion(), t.getValor()));
						mailMensaje
								.setAttachment(Arrays.asList(rutaArchivoAutorizacionXml, rutaArchivoAutorizacionPdf));
						MailDelivery.send(mailMensaje,
								cacheBean.consultarParametro(Constantes.SMTP_HOST, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_PORT, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_USERNAME, emisor.getId()).getValor(),
								cacheBean.consultarParametro(Constantes.SMTP_PASSWORD, emisor.getId())
										.getValorDesencriptado(),
								emisor.getAcronimo());
					}

				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);

				}

				return respuesta;

			} else if ("NO AUTORIZADO".equals(respAutorizacion.getEstado())
					|| "RECHAZADO".equals(respAutorizacion.getEstado())) {
				// NO AUTORIZADO

				StringBuilder mensajeRespuesta = new StringBuilder();
				if (respAutorizacion != null && respAutorizacion.getMensajes() != null
						&& respAutorizacion.getMensajes().getMensaje() != null
						&& !respAutorizacion.getMensajes().getMensaje().isEmpty()) {

					MensajeRespuesta msgObj = null;
					for (Mensaje msg : respAutorizacion.getMensajes().getMensaje()) {
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
				respuesta.setMensajeCliente("TRANSMITIDO SIN RESPUESTA AUTORIZACION");
				// Genera comprobante legible
				// try {
				// byte[] documentFirmado = DocumentUtil
				// .readFile(rutaArchivoFirmadoXml);
				//
				// rutaArchivoRechazadoXml = DocumentUtil
				// .createDocument(
				// documentFirmado,
				// mensaje.getFactura().getInfoFactura()
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
					&& !respuestaRecepcion.getComprobantes().getComprobante().isEmpty()) {
				MensajeRespuesta msgObj = null;
				for (Comprobante comprobanteRecepcion : respuestaRecepcion.getComprobantes().getComprobante()) {
					if (comprobanteRecepcion.getMensajes() != null) {
						for (ec.gob.sri.comprobantes.ws.Mensaje msg : comprobanteRecepcion.getMensajes().getMensaje()) {
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
			// mensaje.getFactura().getInfoFactura()
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
			respuesta.setMensajeCliente("TRANSMITIDO SIN RESPUESTA CONTIGENCIA");
			// Genera comprobante legible
			// try {
			//
			// byte[] documentFirmado = DocumentUtil
			// .readFile(rutaArchivoFirmadoXml);
			//
			// rutaArchivoRechazadoXml = DocumentUtil
			// .createDocument(
			// documentFirmado,
			// mensaje.getFactura().getInfoFactura()
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

	// @Override
	// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	// public FacturaProcesarResponse procesarOffline(FacturaProcesarRequest
	// mensaje) {
	// FacturaProcesarResponse respuesta = new FacturaProcesarResponse();
	// respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
	//
	// log.info("Inicia proceso facturacion metodo offline");
	// String rutaArchivoXml = null;
	// String rutaArchivoFirmadoXml = null;
	// String rutaArchivoAutorizacionXml = null;
	// String rutaArchivoRechazadoXml = null;
	//
	// String rutaArchivoAutorizacionPdf = null;
	// String rutaArchivoRechazadoPdf = null;
	//
	//// String ambiente = null;
	// String claveAcceso = null;
	// Organizacion emisor = null;
	// String wsdlLocationRecepcion = null;
	// String wsdlLocationAutorizacion = null;
	//
	// com.gizlocorp.gnvoice.modelo.Factura comprobante = null;
	//
	//// boolean booCatorce = false;
	//
	// /*-------------------------------------------------------------------------------------------------------------------------------------*/
	// // GENERA DE COMPROBANTE
	// // TODO contigencia
	// // boolean existioComprobante = false;
	// try {
	// log.info("Inicia validaciones previas metodo offline");
	// if (mensaje.getFactura() == null ||
	// mensaje.getFactura().getInfoTributaria() == null) {
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setMensajeSistema("Mensaje de entrada incorrecto. Nodo
	// \"comprobante\" no puede venir vacio (null)");
	// respuesta.setMensajeCliente("Datos de comprobante incorrectos. Factura
	// vacia");
	// return respuesta;
	// }
	//
	// emisor =
	// cacheBean.obtenerOrganizacion(mensaje.getFactura().getInfoTributaria().getRuc());
	//
	// if (emisor == null) {
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setClaveAccesoComprobante(null);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	// respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "
	// + mensaje.getFactura().getInfoTributaria()
	// .getRuc());
	// respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "
	// + mensaje.getFactura().getInfoTributaria()
	// .getRuc());
	// return respuesta;
	// }
	//
	// if (mensaje.getFactura().getInfoTributaria().getSecuencial() == null
	// || mensaje.getFactura().getInfoTributaria().getSecuencial()
	// .isEmpty()) {
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setClaveAccesoComprobante(null);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	// respuesta.setMensajeSistema("Secuencial no puede ser NULL");
	// respuesta.setMensajeCliente("Secuencial no puede ser NULL");
	// return respuesta;
	// }
	//
	// Parametro ambParametro =
	// cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
	// Parametro monParamtro = cacheBean.consultarParametro(Constantes.MONEDA,
	// emisor.getId());
	// Parametro codFacParametro =
	// cacheBean.consultarParametro(Constantes.COD_FACTURA, emisor.getId());
	//// ambiente = ambParametro.getValor();
	//
	// wsdlLocationRecepcion =
	// "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
	// wsdlLocationAutorizacion =
	// "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
	//
	// com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;
	// try {
	// comprobanteExistente = servicioFactura.obtenerComprobante(null,
	// mensaje.getCodigoExterno(), mensaje.getFactura()
	// .getInfoTributaria().getRuc(),
	// mensaje.getAgencia());
	// } catch (Exception ex) {
	// InitialContext ic = new InitialContext();
	// servicioFactura = (ServicioFactura) ic
	// .lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
	// comprobanteExistente = servicioFactura.obtenerComprobante(null,
	// mensaje.getCodigoExterno(), mensaje.getFactura()
	// .getInfoTributaria().getRuc(),
	// mensaje.getAgencia());
	//
	// }
	//
	// if (comprobanteExistente != null &&
	// Estado.AUTORIZADO.getDescripcion().equals(
	// comprobanteExistente.getEstado())) {
	// respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
	// respuesta.setMensajeCliente("Comprobante AUTORIZADO");
	// respuesta.setMensajeSistema("Comprobante AUTORIZADO");
	// respuesta.setClaveAccesoComprobante(comprobanteExistente
	// .getClaveAcceso());
	// respuesta.setFechaAutorizacion(comprobanteExistente
	// .getFechaAutorizacion());
	// respuesta.setNumeroAutorizacion(comprobanteExistente
	// .getNumeroAutorizacion());
	//
	// return respuesta;
	// }
	//
	// // Setea nodo principal
	// mensaje.getFactura().setId("comprobante");
	// // mensaje.getFactura().setVersion(Constantes.VERSION);
	// mensaje.getFactura().setVersion(Constantes.VERSION_1);
	//
	// mensaje.getFactura().getInfoFactura()
	// .setMoneda(monParamtro.getValor());
	// mensaje.getFactura().getInfoTributaria()
	// .setAmbiente(ambParametro.getValor());
	// mensaje.getFactura().getInfoTributaria()
	// .setCodDoc(codFacParametro.getValor());
	//
	// if (mensaje.getFactura().getInfoTributaria().getSecuencial()
	// .length() < 9) {
	// StringBuilder secuencial = new StringBuilder();
	//
	// int secuencialTam = 9 - mensaje.getFactura()
	// .getInfoTributaria().getSecuencial().length();
	// for (int i = 0; i < secuencialTam; i++) {
	// secuencial.append("0");
	// }
	// secuencial.append(mensaje.getFactura().getInfoTributaria()
	// .getSecuencial());
	// mensaje.getFactura().getInfoTributaria()
	// .setSecuencial(secuencial.toString());
	// }
	//
	// mensaje.getFactura().getInfoTributaria()
	// .setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
	//
	// String dirMatriz = emisor.getDireccion();
	//
	// mensaje.getFactura()
	// .getInfoTributaria()
	// .setDirMatriz(
	// dirMatriz != null ? StringEscapeUtils
	// .escapeXml(dirMatriz.trim())
	// .replaceAll("[\n]", "")
	// .replaceAll("[\b]", "") : null);
	//
	// if (mensaje.getFactura().getInfoTributaria().getEstab() == null
	// || mensaje.getFactura().getInfoTributaria().getEstab()
	// .isEmpty()) {
	// mensaje.getFactura().getInfoTributaria()
	// .setEstab(emisor.getEstablecimiento());
	//
	// }
	//
	// if (mensaje.getFactura().getInfoTributaria().getNombreComercial() == null
	// || mensaje.getFactura().getInfoTributaria()
	// .getNombreComercial().isEmpty()) {
	// mensaje.getFactura()
	// .getInfoTributaria()
	// .setNombreComercial(
	// emisor.getNombreComercial() != null ? StringEscapeUtils
	// .escapeXml(
	// emisor.getNombreComercial()
	// .trim())
	// .replaceAll("[\n]", "")
	// .replaceAll("[\b]", "") : null);
	// }
	//
	// if (mensaje.getFactura().getInfoTributaria().getPtoEmi() == null
	// || mensaje.getFactura().getInfoTributaria().getPtoEmi()
	// .isEmpty()) {
	// mensaje.getFactura().getInfoTributaria()
	// .setPtoEmi(emisor.getPuntoEmision());
	// }
	// mensaje.getFactura()
	// .getInfoTributaria()
	// .setRazonSocial(
	// emisor.getNombreComercial()
	// .trim()!= null ? StringEscapeUtils
	// .escapeXml(emisor.getNombreComercial()
	// .trim()) : null);
	//
	// mensaje.getFactura()
	// .getInfoFactura()
	// .setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
	// if (mensaje.getFactura().getInfoFactura().getDirEstablecimiento() == null
	// || mensaje.getFactura().getInfoFactura()
	// .getDirEstablecimiento().isEmpty()) {
	// mensaje.getFactura()
	// .getInfoFactura()
	// .setDirEstablecimiento(
	// emisor.getDirEstablecimiento() != null ? StringEscapeUtils
	// .escapeXml(emisor
	// .getDirEstablecimiento())
	// : null);
	// }
	// mensaje.getFactura()
	// .getInfoFactura()
	// .setObligadoContabilidad(
	// emisor.getEsObligadoContabilidad().getDescripcion()
	// .toUpperCase());
	//
	// StringBuilder codigoNumerico = new StringBuilder();
	// codigoNumerico.append(mensaje.getFactura().getInfoTributaria()
	// .getEstab());
	// codigoNumerico.append(mensaje.getFactura().getInfoTributaria()
	// .getPtoEmi());
	// log.debug("codigo numerico - secuencial de factura: "
	// + mensaje.getFactura().getInfoTributaria().getSecuencial());
	// codigoNumerico.append(mensaje.getFactura().getInfoTributaria()
	// .getSecuencial());
	// codigoNumerico.append(Constantes.CODIGO_NUMERICO);
	//
	// claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje
	// .getFactura().getInfoFactura().getFechaEmision(),
	// codFacParametro.getValor(), mensaje.getFactura()
	// .getInfoTributaria().getRuc(), mensaje.getFactura()
	// .getInfoTributaria().getAmbiente(),
	// codigoNumerico.toString());
	//
	// log.info("codigo numerico - secuencial de factura: "+claveAcceso);
	// mensaje.getFactura().getInfoTributaria().setClaveAcceso(claveAcceso);
	// //
	// -jose--------------------------------------------------------------------------------------------------------------------------------*/
	//
	// log.info("finaliza validaciones previas");
	// log.debug("Inicia generacion de comproabante");
	// comprobante =
	// ComprobanteUtil.convertirEsquemaAEntidadFactura(mensaje.getFactura());
	//
	// comprobante.setClaveInterna(mensaje.getCodigoExterno());
	// comprobante.setCorreoNotificacion(mensaje
	// .getCorreoElectronicoNotificacion());
	// comprobante.setIdentificadorUsuario(mensaje
	// .getIdentificadorUsuario());
	// comprobante.setAgencia(mensaje.getAgencia());
	//
	// comprobante.setTipoEjecucion(TipoEjecucion.SEC);
	// comprobante.setSecuencialOriginal(mensaje.getSid());
	// comprobante.setModoEnvio("OFFLINE");
	// ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion
	// respAutorizacionComExt = autorizarComprobanteOffline(
	// wsdlLocationAutorizacion, claveAcceso);
	//
	// log.info("pasando validacion de autorizacion "+claveAcceso);
	//
	// if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
	//
	// respuesta.setMensajeCliente("Comprobante AUTORIZADO");
	//
	// respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
	// respuesta.setFechaAutorizacion(respAutorizacionComExt
	// .getFechaAutorizacion() != null ? respAutorizacionComExt
	// .getFechaAutorizacion().toGregorianCalendar()
	// .getTime()
	// : null);
	// respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	//
	// respuesta.setMensajeSistema("Comprobante AUTORIZADO");
	// respuesta.setMensajeCliente("Comprobante AUTORIZADO");
	//
	// comprobante.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
	// comprobante.setFechaAutorizacion(respAutorizacionComExt
	// .getFechaAutorizacion() != null ? respAutorizacionComExt
	// .getFechaAutorizacion().toGregorianCalendar()
	// .getTime()
	// : null);
	//
	// try {
	//
	// String comprobanteXML = respAutorizacionComExt.getComprobante();
	// comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
	// comprobanteXML = comprobanteXML.replace("]]>", "");
	//
	// com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML =
	// getFacturaXML(comprobanteXML);
	//
	// rutaArchivoAutorizacionXml = DocumentUtil.createDocument(
	// getFacturaXML(factAutorizadaXML), factAutorizadaXML
	// .getInfoFactura().getFechaEmision(),
	// factAutorizadaXML.getInfoTributaria().getClaveAcceso(),
	// TipoComprobante.FACTURA.getDescripcion(),
	// "autorizado");
	//
	// } catch (Exception e) {
	// log.debug(e.getMessage(), e);
	// }
	// comprobante.setArchivo(rutaArchivoAutorizacionXml);
	// comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
	// // TODO actualizar comproabten
	// try {
	// servicioFactura.autorizaOffline(comprobante);
	// } catch (Exception ex) {
	// try {
	// InitialContext ic = new InitialContext();
	// servicioFactura = (ServicioFactura) ic
	// .lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
	// servicioFactura.autorizaOffline(comprobante);
	// } catch (Exception ex2) {
	// log.warn("no se guardo el comprobante");
	// }
	// }
	//
	// return respuesta;
	// } else if ("PROCESAMIENTO".equals(respAutorizacionComExt
	// .getEstado())) {
	// log.info("Comprobante en procesamiento: "
	// + comprobante.getClaveAcceso());
	//
	// respuesta.setEstado(EstadoOffline.PROCESAMIENTO
	// .getDescripcion());
	// respuesta.setMensajes(respuesta.getMensajes());
	// respuesta.setMensajeSistema("Comprobante "
	// + respAutorizacionComExt.getEstado());
	// respuesta.setMensajeCliente("Comprobante "
	// + respAutorizacionComExt.getEstado());
	// respuesta.setClaveAccesoComprobante(comprobante
	// .getClaveAcceso());
	// comprobante.setEstado(EstadoOffline.PROCESAMIENTO.getDescripcion());
	//
	// try {
	// servicioFactura.autorizaOffline(comprobante);
	// } catch (Exception ex) {
	// try {
	// InitialContext ic = new InitialContext();
	// servicioFactura = (ServicioFactura) ic
	// .lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
	// servicioFactura.autorizaOffline(comprobante);
	//
	// } catch (Exception ex2) {
	// log.warn("no se guardo el comprobante");
	// }
	// }
	//
	// return respuesta;
	//
	// } else if (!"ERROR_TECNICO".equals(respAutorizacionComExt.getEstado()) &&
	// comprobanteExistente != null
	// && !"NOAUTORIZADO".equals(comprobanteExistente.getEstado())) {
	// log.info("Comprobante no autorizado: "+ comprobante.getClaveAcceso());
	//
	// respuesta
	// .setEstado(EstadoOffline.NOAUTORIZADO.getDescripcion());
	// respuesta.setMensajes(respuesta.getMensajes());
	// respuesta.setMensajeSistema("Comprobante "+
	// respAutorizacionComExt.getEstado());
	// respuesta.setMensajeCliente("Comprobante "+
	// respAutorizacionComExt.getEstado());
	// respuesta.setClaveAccesoComprobante(comprobante.getClaveAcceso());
	// comprobante.setEstado(EstadoOffline.NOAUTORIZADO.getDescripcion());
	//
	// try {
	// servicioFactura.autorizaOffline(comprobante);
	// } catch (Exception ex) {
	// try {
	// InitialContext ic = new InitialContext();
	// servicioFactura = (ServicioFactura) ic
	// .lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
	// servicioFactura.autorizaOffline(comprobante);
	//
	// } catch (Exception ex2) {
	// log.warn("no se guardo el comprobante");
	// }
	// }
	//
	// return respuesta;
	// }
	//
	// log.info("comprobante offline nuevo "+claveAcceso);
	//
	// // Genera comprobante
	// rutaArchivoXml = DocumentUtil.createDocument(getFacturaXML(mensaje
	// .getFactura()), mensaje.getFactura().getInfoFactura()
	// .getFechaEmision(), claveAcceso,
	// com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA
	// .getDescripcion(), "enviado");
	// log.info("ruta de archivo es: " + rutaArchivoXml+"ruta
	// xsd"+com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
	// + "/gnvoice/recursos/xsd/factura.xsd");
	//
	// String validacionXsd = ComprobanteUtil.validaArchivoXSD(
	// rutaArchivoXml,
	// com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
	// + "/gnvoice/recursos/xsd/factura.xsd");
	//
	// if (validacionXsd != null) {
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	//
	// respuesta.setMensajeSistema("Error en esquema comprobante. "
	// + validacionXsd);
	// respuesta
	// .setMensajeCliente("Error en la estructura del comprobante enviado.");
	// // Actualiza estado comprobante
	// log.info("comprobante: " + comprobante + " archivo: "
	// + rutaArchivoXml);
	// comprobante.setArchivo(rutaArchivoXml);
	// return respuesta;
	// }
	//
	// log.info("Comprobante Generado Correctamente!!");
	// // Actualiza estado comprobante
	// comprobante.setArchivo(rutaArchivoXml);
	//
	// } catch (Exception ex) {
	// log.error(ex.getMessage(), ex);
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	//
	// respuesta.setMensajeSistema("Ha ocurrido un error en el sistema: "
	// + ex.getMessage());
	// respuesta
	// .setMensajeCliente("Ha ocurrido un error en el sistema, comprobante no
	// Generado");
	//
	// if (comprobante != null && comprobante.getId() != null) {
	// comprobante.setArchivo(rutaArchivoXml);
	// }
	//
	// return respuesta;
	// }
	// /*-------------------------------------------------------------------------------------------------------------------------------------*/
	// // FIRMA COMPROBANTE
	// comprobante.setArchivo(rutaArchivoFirmadoXml);
	//
	// FirmaElectronicaWsService serviceFirma = new FirmaElectronicaWsService();
	// FirmaElectronicaWs firmaElectronica = serviceFirma
	// .getFirmaElectronicaWsPort();
	//
	// FirmaElectronicaRequest firmaRequest = new FirmaElectronicaRequest();
	// try {
	// firmaRequest
	// .setClave(TripleDESUtil._decrypt(emisor.getClaveToken()));
	// } catch (Exception ex) {
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	//
	// respuesta
	// .setMensajeSistema("No se puede desencriptar clave Firma: "
	// + ex.getMessage());
	// respuesta.setMensajeCliente("No se puede desencriptar clave Firma");
	//
	// comprobante.setArchivo(rutaArchivoXml);
	//
	// return respuesta;
	// }
	//
	// firmaRequest.setRutaFirma(emisor.getToken());
	// firmaRequest.setRutaArchivo(rutaArchivoXml);
	// FirmaElectronicaResponse firmaResponse = null;
	//
	// try {
	// ((BindingProvider) firmaElectronica).getRequestContext().put(
	// "com.sun.xml.ws.connect.timeout", 5000L);
	// ((BindingProvider) firmaElectronica).getRequestContext().put(
	// "com.sun.xml.internal.ws.connect.timeout", 5000L);
	//
	// ((BindingProvider) firmaElectronica).getRequestContext().put(
	// "com.sun.xml.internal.ws.request.timeout", 5000L);
	// ((BindingProvider) firmaElectronica).getRequestContext().put(
	// "com.sun.xml.ws.request.timeout", 5000L);
	//
	// firmaResponse = firmaElectronica.firmarDocumento(firmaRequest);
	// } catch (Exception ex) {
	// log.error("********* FIRMA", ex);
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	//
	// respuesta.setMensajeSistema("No se puede firmar: "
	// + ex.getMessage());
	// respuesta.setMensajeCliente("No se puede firmar");
	//
	// comprobante.setArchivo(rutaArchivoXml);
	//
	// return respuesta;
	// }
	//
	// if (firmaResponse == null || "ERROR".equals(firmaResponse.getEstado())) {
	// respuesta.setEstado(Estado.ERROR.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	//
	// respuesta.setMensajeSistema("Error al firmar el documento: "
	// + firmaResponse.getMensajesistema());
	// respuesta.setMensajeCliente("Error al firmar el documento");
	//
	// comprobante.setArchivo(rutaArchivoXml);
	//
	// return respuesta;
	// }
	//
	// rutaArchivoFirmadoXml = firmaResponse.getRutaArchivoFirmado();
	// comprobante.setArchivo(rutaArchivoFirmadoXml);
	//
	// comprobante.setArchivo(rutaArchivoFirmadoXml);
	//
	//
	// ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud
	// respuestaRecepcion = enviarComprobanteOffline(wsdlLocationRecepcion,
	// rutaArchivoFirmadoXml);
	//
	// if ("RECIBIDA".equals(respuestaRecepcion.getEstado())) {
	// comprobante.setArchivo(rutaArchivoFirmadoXml);
	//
	// respuesta.setEstado(Estado.RECIBIDA.getDescripcion());
	//
	// try {
	// String comprobantexml =
	// DocumentUtil.readContentFile(rutaArchivoFirmadoXml);
	// com.gizlocorp.gnvoice.xml.factura.Factura facturaXML =
	// getFacturaXML(comprobantexml);
	//
	// ReporteUtil reporte = new ReporteUtil();
	//
	// rutaArchivoAutorizacionPdf = reporte.generarReporte(
	// Constantes.REP_FACTURA_OFFLINE, new FacturaReporte(
	// facturaXML), claveAcceso, null,
	// facturaXML.getInfoFactura()
	// .getFechaEmision(), "autorizado",
	// false, emisor.getLogoEmpresa());
	//
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	//
	// try {
	// if (mensaje.getCorreoElectronicoNotificacion() != null
	// && !mensaje.getCorreoElectronicoNotificacion()
	// .isEmpty()) {
	// Plantilla t = cacheBean.obtenerPlantilla(
	// Constantes.NOTIFIACION, emisor.getId());
	//
	// Map<String, Object> parametrosBody = new HashMap<String, Object>();
	// parametrosBody.put("nombre", mensaje.getFactura()
	// .getInfoFactura().getRazonSocialComprador());
	// parametrosBody.put("tipo", "FACTURA");
	// parametrosBody.put("secuencial", mensaje.getFactura()
	// .getInfoTributaria().getSecuencial());
	// parametrosBody.put("fechaEmision", mensaje.getFactura()
	// .getInfoFactura().getFechaEmision());
	// parametrosBody.put("total", mensaje.getFactura()
	// .getInfoFactura().getImporteTotal());
	// parametrosBody.put("emisor", emisor.getNombre());
	// parametrosBody.put("ruc", emisor.getRuc());
	// parametrosBody.put("numeroAutorizacion",claveAcceso);
	//
	// MailMessage mailMensaje = new MailMessage();
	// mailMensaje.setSubject(t
	// .getTitulo()
	// .replace("$tipo", "FACTURA")
	// .replace(
	// "$numero",
	// mensaje.getFactura()
	// .getInfoTributaria()
	// .getSecuencial()));
	// mailMensaje.setFrom(cacheBean.consultarParametro(
	// Constantes.CORREO_REMITE, emisor.getId())
	// .getValor());
	// mailMensaje
	// .setTo(Arrays.asList(mensaje
	// .getCorreoElectronicoNotificacion()
	// .split(";")));
	// mailMensaje.setBody(ComprobanteUtil
	// .generarCuerpoMensaje(parametrosBody,
	// t.getDescripcion(), t.getValor()));
	// mailMensaje.setAttachment(Arrays.asList(
	// rutaArchivoFirmadoXml,
	// rutaArchivoAutorizacionPdf));
	// MailDelivery
	// .send(mailMensaje,
	// cacheBean.consultarParametro(
	// Constantes.SMTP_HOST,
	// emisor.getId()).getValor(),
	// cacheBean.consultarParametro(
	// Constantes.SMTP_PORT,
	// emisor.getId()).getValor(),
	// cacheBean.consultarParametro(
	// Constantes.SMTP_USERNAME,
	// emisor.getId()).getValor(),
	// cacheBean.consultarParametro(
	// Constantes.SMTP_PASSWORD,
	// emisor.getId())
	// .getValorDesencriptado(),
	// emisor.getAcronimo());
	// }
	//
	// } catch (Exception ex) {
	// log.error(ex.getMessage(), ex);
	// }
	//
	// try {
	// Thread.currentThread();
	// Thread.sleep(500L);
	// } catch (InterruptedException e) {
	// log.debug(e.getMessage(), e);
	// }
	// /*-------------------------------------------------------------------------------------------------------------------------------------*/
	// // AUTORIZACION COMPROBANTE
	// comprobante.setNumeroAutorizacion(claveAcceso);
	// comprobante.setArchivo(rutaArchivoFirmadoXml);
	// comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
	// comprobante.setEstado(Estado.RECIBIDA.getDescripcion());
	//
	//
	// ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacion =
	// autorizarComprobanteOffline(wsdlLocationAutorizacion, claveAcceso);
	//
	// if ("AUTORIZADO".equals(respAutorizacion.getEstado())) {
	//
	// respuesta.setMensajeCliente("Comprobante AUTORIZADO");
	// try {
	// rutaArchivoAutorizacionXml = DocumentUtil.createDocument(
	// getFacturaXML(respAutorizacion), mensaje
	// .getFactura().getInfoFactura()
	// .getFechaEmision(), claveAcceso,
	// TipoComprobante.FACTURA.getDescripcion(),
	// "autorizado");
	//
	// } catch (Exception e) {
	// log.debug(e.getMessage(), e);
	// }
	//
	// respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(respAutorizacion
	// .getFechaAutorizacion() != null ? respAutorizacion
	// .getFechaAutorizacion().toGregorianCalendar().getTime()
	// : null);
	// respuesta.setNumeroAutorizacion(respAutorizacion
	// .getNumeroAutorizacion());
	//
	// respuesta.setMensajeSistema("Comprobante AUTORIZADO");
	// respuesta.setMensajeCliente("Comprobante AUTORIZADO");
	//
	// comprobante.setNumeroAutorizacion(respAutorizacion
	// .getNumeroAutorizacion());
	// comprobante.setFechaAutorizacion(respAutorizacion
	// .getFechaAutorizacion() != null ? respAutorizacion
	// .getFechaAutorizacion().toGregorianCalendar().getTime()
	// : null);
	// comprobante.setArchivo(rutaArchivoAutorizacionXml);
	// comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
	//
	// try {
	// servicioFactura.autorizaOffline(comprobante);
	// } catch (Exception ex) {
	// try {
	// InitialContext ic = new InitialContext();
	// servicioFactura = (ServicioFactura) ic
	// .lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
	// servicioFactura.autorizaOffline(comprobante);
	//
	// } catch (Exception ex2) {
	// log.warn("no se guardo el comprobante");
	// }
	// }
	//
	//
	//
	// return respuesta;
	//
	// }
	// //solo si esta receptada
	// try {
	// servicioFactura.autorizaOffline(comprobante);
	// } catch (Exception ex) {
	// try {
	// InitialContext ic = new InitialContext();
	// servicioFactura = (ServicioFactura) ic
	// .lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
	// servicioFactura.autorizaOffline(comprobante);
	//
	// } catch (Exception ex2) {
	// log.warn("no se guardo el comprobante");
	// }
	// }
	//
	// /*---FIN
	// AUTORIZACION------------------------------------------------------------------------------------------------------------------*/
	// } else if ("DEVUELTA".equals(respuestaRecepcion.getEstado())) {
	// // DEVUELTA
	// // Configura valores de respuesta
	// StringBuilder mensajeRespuesta = new StringBuilder();
	// if (respuestaRecepcion.getComprobantes() != null
	// && respuestaRecepcion.getComprobantes().getComprobante() != null
	// && !respuestaRecepcion.getComprobantes().getComprobante()
	// .isEmpty()) {
	// MensajeRespuesta msgObj = null;
	// for (ec.gob.sri.comprobantes.ws.offline.rec.Comprobante
	// comprobanteRecepcion : respuestaRecepcion
	// .getComprobantes().getComprobante()) {
	// if (comprobanteRecepcion.getMensajes() != null) {
	// for (ec.gob.sri.comprobantes.ws.offline.rec.Mensaje msg :
	// comprobanteRecepcion
	// .getMensajes().getMensaje()) {
	// msgObj = new MensajeRespuesta();
	// msgObj.setIdentificador(msg.getIdentificador());
	// msgObj.setInformacionAdicional(msg
	// .getInformacionAdicional());
	// msgObj.setMensaje(msg.getMensaje());
	// msgObj.setTipo(msg.getTipo());
	// respuesta.getMensajes().add(msgObj);
	//
	// mensajeRespuesta.append("Identificador: ");
	// mensajeRespuesta.append(msg.getIdentificador());
	// mensajeRespuesta.append(" Mensaje: ");
	// mensajeRespuesta.append(msg.getMensaje());
	// mensajeRespuesta.append(" Informacion Adicional: ");
	// mensajeRespuesta.append(msg
	// .getInformacionAdicional());
	// mensajeRespuesta.append(" <br>");
	// }
	// }
	// }
	// respuesta.setMensajeSistema(mensajeRespuesta.toString());
	// respuesta.setMensajeCliente("Comprobante DEVUELTO");
	// }
	//
	// respuesta.setEstado(Estado.DEVUELTA.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	//
	// comprobante.setArchivo(rutaArchivoRechazadoXml);
	// comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);
	//
	// return respuesta;
	//
	// } else {
	// respuesta.setMensajeCliente("TRANSMITIDO SIN RESPUESTA CONTIGENCIA");
	//
	//
	// respuesta.setEstado(Estado.PENDIENTE.getDescripcion());
	// respuesta.setClaveAccesoComprobante(claveAcceso);
	// respuesta.setFechaAutorizacion(null);
	// respuesta.setNumeroAutorizacion(null);
	//
	// comprobante.setArchivo(rutaArchivoRechazadoXml);
	// comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);
	//
	// return respuesta;
	// }
	// return respuesta;
	//
	// }

	@Override
	public FacturaConsultarResponse consultar(FacturaConsultarRequest facturaConsultarRequest) {
		FacturaConsultarResponse respuesta = new FacturaConsultarResponse();
		List<com.gizlocorp.gnvoice.xml.factura.Factura> comprobantes = new ArrayList<com.gizlocorp.gnvoice.xml.factura.Factura>();
		try {
			if ((facturaConsultarRequest.getRuc() != null && !facturaConsultarRequest.getRuc().isEmpty())
					|| (facturaConsultarRequest.getClaveAcceso() != null
							&& !facturaConsultarRequest.getClaveAcceso().isEmpty())
					|| (facturaConsultarRequest.getSecuencial() != null
							&& !facturaConsultarRequest.getSecuencial().isEmpty())
					|| (facturaConsultarRequest.getCodigoExterno() != null
							&& !facturaConsultarRequest.getCodigoExterno().isEmpty())
					|| (facturaConsultarRequest.getEstado() != null
							&& !facturaConsultarRequest.getEstado().isEmpty())) {

				List<com.gizlocorp.gnvoice.modelo.Factura> items = servicioFactura.obtenerComprobanteConsulta(
						facturaConsultarRequest.getRuc(), facturaConsultarRequest.getClaveAcceso(),
						facturaConsultarRequest.getSecuencial(), facturaConsultarRequest.getCodigoExterno(),
						facturaConsultarRequest.getEstado());
				log.debug("Consultando facturas servicio consultar: " + (items != null ? items.size() : "0"));
				if (items != null && !items.isEmpty()) {
					for (com.gizlocorp.gnvoice.modelo.Factura item : items) {
						try {
							String xmlString = DocumentUtil.readContentFile(item.getArchivo().toString());
							com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = getFacturaXML(xmlString);
							comprobantes.add(facturaXML);
						} catch (Exception ex) {
							log.error("Ha ocurrido un error al leer comprobante ", ex);
						}
					}
				}
				respuesta.setEstado("");
				respuesta.setFacturas(comprobantes);
			}
		} catch (Exception ex) {
			// respuesta.setFacturas(new ArrayList<factu>());
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			MensajeRespuesta msgObj = new MensajeRespuesta();
			msgObj.setIdentificador("-100");
			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
			respuesta.getMensajes().add(msgObj);

			// try {
			// Bitacora bitacoraEvento = new Bitacora();
			// bitacoraEvento.setEvento(servicioCatalogoLocal
			// .listObtenerPorParametros(Constantes.EVENTO_ALERTA,
			// null).get(0).getId());
			// bitacoraEvento
			// .setDescripcion("Alerta "
			// + this.getClass()
			// + ": Error en consulta de comprobante por Servicio SOA."
			// + ex.getMessage());
			// bitacoraEvento.setUsuario("gnvoice");
			// bitacoraEvento.setFecha(new Date());
			// servicioBitacoraEvento.insertaEventoMdb(bitacoraEvento);
			// } catch (GizloException e1) {
			// log.error(e1.getMessage(), e1);
			// }

			log.error(ex.getMessage(), ex);
		}

		return respuesta;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FacturaRecibirResponse recibir(FacturaRecibirRequest facturaRecibirRequest) {

		Date fechaTMP = new Date();
		FacturaRecibirResponse respuesta = new FacturaRecibirResponse();

		BigDecimal baseImponible0 = BigDecimal.ZERO;
		BigDecimal baseImponible12 = BigDecimal.ZERO;
		BigDecimal iva = BigDecimal.ZERO;
		BigDecimal subtotal = BigDecimal.ZERO;
		String nombreArchivo = "";
		
		BigDecimal baseImponibleICE = BigDecimal.ZERO;
		BigDecimal baseImponibleIRBP = BigDecimal.ZERO;
		BigDecimal iceTmp = BigDecimal.ZERO;
		BigDecimal irbp = BigDecimal.ZERO;
		BigDecimal cantidadTotal = BigDecimal.ZERO;

		boolean offline = false;

		Autorizacion autorizacionXML = null;
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;

		try {

			String autorizacion = DocumentUtil.readContentFile(facturaRecibirRequest.getComprobanteProveedor());

			String numeroAutorizcionLeng = "";
			if (autorizacion.contains("&lt;numeroAutorizacion&gt;")
					&& autorizacion.contains("&lt;/numeroAutorizacion&gt;")) {
				String[] ab = autorizacion.split("&lt;numeroAutorizacion&gt;");
				numeroAutorizcionLeng = ab[1].split("&lt;/numeroAutorizacion&gt;")[0].trim();
			}

			if (autorizacion.contains("<numeroAutorizacion>") && autorizacion.contains("</numeroAutorizacion>")) {
				String[] ab = autorizacion.split("<numeroAutorizacion>");
				numeroAutorizcionLeng = ab[1].split("</numeroAutorizacion>")[0].trim();

			}

			if (autorizacion.contains("&lt;claveAcceso&gt;")) {
				
				
				

				String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
			//	wsdlLocationRecepcion = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
			   //   wsdlLocationAutorizacion = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

				String claveConsultar = "";
				String numeroAutorizcion = "";

				int a = autorizacion.indexOf("&lt;claveAcceso&gt;");

				int desde = a + 19;
				int hasta = desde + 49;

				claveConsultar = autorizacion.substring(desde, hasta);

				if (autorizacion.contains("&lt;numeroAutorizacion&gt;")
						&& autorizacion.contains("&lt;/numeroAutorizacion&gt;")) {
					String[] ab = autorizacion.split("&lt;numeroAutorizacion&gt;");
					numeroAutorizcion = ab[1].split("&lt;/numeroAutorizacion&gt;")[0].trim();
				}

				if (autorizacion.contains("<numeroAutorizacion>") && autorizacion.contains("</numeroAutorizacion>")) {
					String[] ab = autorizacion.split("<numeroAutorizacion>");
					numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
				}

				if (claveConsultar.equals(numeroAutorizcion)) {
					String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
					   //  wsdlLocationAutorizacionoff = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
					   //dsdsds
					     autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);
					offline = true;

				} else {
					autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
				}

			} else {
				if (autorizacion != null && !autorizacion.isEmpty()) {
					String tmp = autorizacion;
					for (int i = 0; i < tmp.length(); i++) {
						if (tmp.charAt(i) == '<') {
							break;
						}
						autorizacion = autorizacion.replace(tmp.charAt(i), ' ');
					}

					autorizacion = autorizacion.replace("<Autorizacion>", "<autorizacion>");
					autorizacion = autorizacion.replace("</Autorizacion>", "</autorizacion>");

					if (autorizacion.contains("<RespuestaAutorizacionComprobante>")) {
						autorizacion = autorizacion.replace("<RespuestaAutorizacionComprobante>", "");
						autorizacion = autorizacion.replace("</RespuestaAutorizacionComprobante>", "");
					}

					autorizacion = autorizacion.trim();
				}

				if (numeroAutorizcionLeng.length() <= 36) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
					String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
					String claveConsultar = "";
					String numeroAutorizcion = "";

					int a = autorizacion.indexOf("<claveAcceso>");

					int desde = a + 13;
					int hasta = desde + 49;

					claveConsultar = autorizacion.substring(desde, hasta);

					autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);
					autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);

					if (autorizacionOfflineXML != null && autorizacionOfflineXML.getComprobante() != null) {
						offline = true;
					}

					if ((autorizacionOfflineXML == null || autorizacionOfflineXML.getComprobante() == null)
							&& (autorizacionXML == null || autorizacionXML.getComprobante() == null)) {
						respuesta.setEstado(Estado.ERROR.getDescripcion());
						respuesta.setMensajeCliente("Comprobante no tiene numero de autorizacion ");
						respuesta.setMensajeSistema("Comprobante no tiene numero de autorizacion ");

						return respuesta;
					}

				} else {
					try {
						autorizacionXML = getAutorizacionXML(autorizacion);
					} catch (Exception ex) {
						String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

						String claveConsultar = "";
						String numeroAutorizcion = "";

						int a = autorizacion.indexOf("<claveAcceso>");

						int desde = a + 13;
						int hasta = desde + 49;

						claveConsultar = autorizacion.substring(desde, hasta);

						if (autorizacion.contains("<numeroAutorizacion>")
								&& autorizacion.contains("</numeroAutorizacion>")) {
							String[] ab = autorizacion.split("<numeroAutorizacion>");
							numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
						}

						if (claveConsultar.equals(numeroAutorizcion)) {
							String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
							autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff,
									claveConsultar);
							offline = true;
						} else {
							autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
						}
					}
				}
			}
			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = null;
			
			if (offline) {
				if (autorizacionOfflineXML == null) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				if (!(autorizacionOfflineXML.getComprobante() != null)) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				String factura = autorizacionOfflineXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
				try {
					facturaXML = getFacturaXML(factura);
				} catch (Exception ex) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

					String claveConsultar = "";
					String numeroAutorizcion = "";

					if (autorizacion.contains("<claveAcceso>") && autorizacion.contains("</claveAcceso>")) {
						String[] ab = autorizacion.split("<claveAcceso>");
						claveConsultar = ab[1].split("</claveAcceso>")[0].trim();
					}

					if (autorizacion.contains("<numeroAutorizacion>")
							&& autorizacion.contains("</numeroAutorizacion>")) {
						String[] ab = autorizacion.split("<numeroAutorizacion>");
						numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
					}

					if (claveConsultar.equals(numeroAutorizcion)) {
						String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
						autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff,
								claveConsultar);
						offline = true;
						String factura3 = autorizacionOfflineXML.getComprobante();
						factura3 = factura3.replace("<![CDATA[", "");
						factura3 = factura3.replace("]]>", "");
						facturaXML = getFacturaXML(factura3);
					} else {
						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
						String factura2 = autorizacionXML.getComprobante();
						factura2 = factura2.replace("<![CDATA[", "");
						factura2 = factura2.replace("]]>", "");
						facturaXML = getFacturaXML(factura2);

					}
				}

			} else {
				if (autorizacionXML == null) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				if (!(autorizacionXML.getComprobante() != null)) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				String factura = autorizacionXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
				try {
					facturaXML = getFacturaXML(factura);
				} catch (Exception ex) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

					String claveConsultar = "";
					String numeroAutorizcion = "";

					if (autorizacion.contains("<claveAcceso>") && autorizacion.contains("</claveAcceso>")) {
						String[] ab = autorizacion.split("<claveAcceso>");
						claveConsultar = ab[1].split("</claveAcceso>")[0].trim();
					}

					if (autorizacion.contains("<numeroAutorizacion>")
							&& autorizacion.contains("</numeroAutorizacion>")) {
						String[] ab = autorizacion.split("<numeroAutorizacion>");
						numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
					}

					if (claveConsultar.equals(numeroAutorizcion)) {
						String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
						autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff,
								claveConsultar);
						offline = true;
						String factura3 = autorizacionOfflineXML.getComprobante();
						factura3 = factura3.replace("<![CDATA[", "");
						factura3 = factura3.replace("]]>", "");
						facturaXML = getFacturaXML(factura3);
					} else {
						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
						String factura2 = autorizacionXML.getComprobante();
						factura2 = factura2.replace("<![CDATA[", "");
						factura2 = factura2.replace("]]>", "");
						facturaXML = getFacturaXML(factura2);
					}
				}

			}

			// com.gizlocorp.gnvoice.xml.factura.Factura facturaXML =
			// getFacturaXML(factura);
			if (facturaXML != null) {
				subtotal = facturaXML.getInfoFactura().getTotalSinImpuestos();
				if (facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto() != null
						&& !facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().isEmpty()) {
					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							if (impu.getCodigoPorcentaje().equals("0")) {
								baseImponible0 = impu.getBaseImponible();
							} else {
								if (impu.getCodigoPorcentaje().equals("2") || impu.getCodigoPorcentaje().equals("3")) {
									baseImponible12 = impu.getBaseImponible();
									iva = impu.getValor();
								}
							}
						}
						
						if("3".equals(impu.getCodigo())){
							 baseImponibleICE  = impu.getBaseImponible();
							 iceTmp = impu.getValor();
						}
						
						if("5".equals(impu.getCodigo())){
							baseImponibleIRBP  = impu.getBaseImponible();
							irbp = impu.getValor();
						}
					}
				}
				
				try {
					if(!facturaXML.getDetalles().getDetalle().isEmpty()){
						for(Detalle detalle: facturaXML.getDetalles().getDetalle() ){
							cantidadTotal = cantidadTotal.add(detalle.getCantidad());
						}
					}
					
				} catch (Exception e) {
					
					e.printStackTrace();
					// TODO: handle exception
				}
				
			}

			nombreArchivo = "edidlinv_" + FechaUtil.formatearFecha(Calendar.getInstance().getTime(), "yyyyMMdd") + "_"
					+ facturaXML.getInfoTributaria().getClaveAcceso();

			com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaObj = servicioRecepcionFactura
					.obtenerComprobanteRecepcion(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);

			if (facturaObj != null && "AUTORIZADO".equals(facturaObj.getEstado())) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeCliente("El comprobante con la clave de acceso " + facturaObj.getClaveAcceso()
						+ " ya esta registrada en el sistema como " + facturaObj.getTipoGeneracion().getDescripcion());

				respuesta.setMensajeSistema("El comprobante con la clave de acceso " + facturaObj.getClaveAcceso()
						+ " ya esta registrada en el sistema como " + facturaObj.getTipoGeneracion().getDescripcion());
				return respuesta;
			}

			if (facturaObj == null) {
				facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFacturaRecepcion(facturaXML);

			} else {
				com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaAux = facturaObj;
				facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFacturaRecepcion(facturaXML);
				facturaObj.setId(facturaAux.getId());
			}
			if (facturaObj.getContribuyenteEspecial().length() > 5) {
				int leng = facturaObj.getContribuyenteEspecial().length();
				facturaObj.setContribuyenteEspecial(facturaObj.getContribuyenteEspecial().substring(leng - 5, leng));
			}

			if (facturaObj.getObligadoContabilidad() == null) {
				facturaObj.setObligadoContabilidad("NO");
			}

			facturaObj.setInfoAdicional(
					StringUtil.validateInfoXML(facturaXML.getInfoTributaria().getRazonSocial().trim()));

			facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
			facturaObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			facturaObj.setProceso(facturaRecibirRequest.getProceso());
			facturaObj.setArchivo(facturaRecibirRequest.getComprobanteProveedor());

			facturaObj.setFechaLecturaTraductor(new Date());
			facturaObj.setBaseCero(baseImponible0);
			facturaObj.setBaseDoce(baseImponible12);
			facturaObj.setIva(iva);
			
			facturaObj.setBaseIce(baseImponibleICE);
			facturaObj.setBaseIrbp(baseImponibleIRBP);
			facturaObj.setIce(iceTmp);
			facturaObj.setIrbp(irbp);
			facturaObj.setCantidadTotal(cantidadTotal);
			
			
			facturaObj.setTipoGeneracion(TipoGeneracion.REC);

			if (offline) {
				if (autorizacionOfflineXML.getNumeroAutorizacion() == null
						|| autorizacionOfflineXML.getNumeroAutorizacion().isEmpty()) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
					respuesta.setMensajeCliente("El comprobante no tiene numero de Autorizacion");

					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,
							"Comprobante no tiene numero de Autorizacion");
					return respuesta;

				}
			} else {
				if (autorizacionXML.getNumeroAutorizacion() == null
						|| autorizacionXML.getNumeroAutorizacion().isEmpty()) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
					respuesta.setMensajeCliente("El comprobante no tiene numero de Autorizacion");

					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,
							"Comprobante no tiene numero de Autorizacion");
					return respuesta;

				}
			}

			Organizacion emisor = cacheBean.obtenerOrganizacion(facturaXML.getInfoFactura().getIdentificacionComprador());
		
			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
				facturaObj.setEstado(Estado.ERROR.getDescripcion());
				facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,"No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
				return respuesta;
			}
			
			if (facturaRecibirRequest.getMailOrigen() != null && !facturaRecibirRequest.getMailOrigen().isEmpty()) {
				facturaObj.setCorreoNotificacion(facturaRecibirRequest.getMailOrigen());
			}

			boolean manual = false;
			if ((facturaRecibirRequest.getOrdenCompra() != null) && (!facturaRecibirRequest.getOrdenCompra().isEmpty())) {
				manual = true;
			}
			

			if (facturaRecibirRequest.getProceso().equals(com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {

				String ordenCompra = null;
				String ordenCompraName = "orden";
				String ordenCompraName2 = "compra";
				String ordenCompraName3 = "Orden_de_entrega";
				
				
				/*if (facturaXML.getInfoAdicional() != null) {
					for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
						if ((item.getNombre().trim().toUpperCase().contains(ordenCompraName.toUpperCase()))
								|| (item.getNombre().trim().toUpperCase().contains(ordenCompraName2.toUpperCase()))) {
							if (item.getNombre().trim().toUpperCase().contains(ordenCompraName3.toUpperCase())) {
								continue;
							}
							ordenCompra = item.getValue().trim();
							break;
						}
					}
				}*/
				
				if (facturaXML.getInfoAdicional() != null) {
					log.info("RUC ----------->"+facturaXML.getInfoTributaria().getRuc());
					List<ParametroOrdenCompra> listaParametros = servicioParametroOrdenCompra.listaParametroRucProveedor(facturaXML.getInfoTributaria().getRuc());
					log.info("Paramtro base  ----------->"+!listaParametros.isEmpty());
					if (listaParametros != null && !listaParametros.isEmpty()) {
						for (ParametroOrdenCompra parametro : listaParametros) {
							log.info("Paramtro VALOR  ----------->"+parametro.getValor());
							log.info("Paramtro VALOR  ----------->"+parametro.getValor().toUpperCase());
							for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
								log.info("Paramtro VALOR  ----------->"+item.getNombre().toUpperCase());
								if (parametro.getValor().toUpperCase().equals(item.getNombre().toUpperCase())) {
									ordenCompra = item.getValue().trim();
									log.info("Paramtro VALOR  ----------->"+ordenCompra);
									break;
								}
							}
						}
					} else {
						log.info("Paramtro VALOR  ---------XXXXXXXXXXXXXXXXXXXXXXXXX-->"+"Se fue por la exepcion");
						if (facturaXML.getInfoAdicional() != null) {
							for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
								if ((item.getNombre().trim().toUpperCase().contains(ordenCompraName.toUpperCase())) 
									 || (item.getNombre().trim().toUpperCase().contains(ordenCompraName2.toUpperCase()))) {
									if (item.getNombre().trim().toUpperCase().contains(ordenCompraName3.toUpperCase())) {
										continue;
									}
									ordenCompra = item.getValue().trim();
									break;
								}
							}
						}
					}
				}
				
				
				
				
				
				
				
				ceDisresponse = new CeDisResponse();
				if (ordenCompra != null) {
					if (ordenCompra.matches("[0-9]*")) {
						facturaObj.setOrdenCompra(ordenCompra);
						ceDisresponse = consultaWebservice(facturaXML.getInfoTributaria().getRuc(),
								facturaObj.getOrdenCompra());
					} else {
						String orden = ordenCompra.replaceAll("[^0-9]", "");
						if (orden.matches("[0-9]*")) {
							facturaObj.setOrdenCompra(orden);
							log.info("ceDisresponse 111***");
							ceDisresponse = consultaWebservice(facturaXML.getInfoTributaria().getRuc(),
									facturaObj.getOrdenCompra());
							log.info("ceDisresponse 222***");
						} else {
							ceDisresponse = null;
						}

					}
				}

				if ((facturaRecibirRequest.getOrdenCompra() != null) && (!facturaRecibirRequest.getOrdenCompra().isEmpty()) && manual) {
					if (facturaRecibirRequest.getOrdenCompra().matches("[0-9]*")) {
						facturaObj.setOrdenCompra(facturaRecibirRequest.getOrdenCompra());

						ceDisresponse = consultaWebservice(facturaXML.getInfoTributaria().getRuc(),facturaObj.getOrdenCompra());
						ordenCompra = facturaRecibirRequest.getOrdenCompra();
					} else {
						ceDisresponse = null;
					}
				}
				if (ceDisresponse != null && !ceDisresponse.getOrder_status().isEmpty()) {
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					respuesta.setMensajeSistema("Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							"Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					return respuesta;
				}

				if (ordenCompra == null || ordenCompra.isEmpty()) {

					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error nose encuentra el nro orden de compra");
					respuesta.setMensajeSistema("Ha ocurrido un error nose encuentra el nro orden de compra");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra");
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							"El comprobante no tiene orden de compra ingresar manualmente");
					return respuesta;
				}

				if (ceDisresponse == null) {

					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim(
							"Ha ocurrido un error no devolvio datos el web service para en munero de compra."
									+ ordenCompra);
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							new StringBuilder().append("El web service no encontro informacion. para la orden")
									.append(ordenCompra).toString());
					return respuesta;
				}
				
				// valida bodega
				if (validaCedisVirtual(ceDisresponse.getCedis_virtual(),facturaObj.getIdentificacionComprador())) {					
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta.setMensajeSistema("Ha ocurrido un error la factura no corresponde a la empresa en cual se emitio la orden de compra");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Ha ocurrido un error la factura no corresponde a la empresa en cual se emitio la orden de compra."+ ordenCompra);
					facturaObj = this.servicioRecepcionFactura.recibirFactura( facturaObj
																	, new StringBuilder()
																	.append("La factura no coresponde a la empresa en cual se emitio la orden de compra")
											                        .append(ordenCompra).toString());
					return respuesta;
				}
				

				// fin servicio web

				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");// dd/MM/yyyy
				SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd/MM/yyyy");
				Date now = new Date();
				String strDate = sdfDate.format(now);
				String strPorccentaje = "E";
				int num = 2;
				ArrayList list = new ArrayList();
				BigDecimal totalUnidades = new BigDecimal(0);
				BigDecimal totalsinimpiesto0 = new BigDecimal(0);
				BigDecimal totalsinimpiesto14 = new BigDecimal(0);
				Map<String, Detalle> detalleMap = new HashMap<String, Detalle>();// para repetidos
				BigDecimal precioUnitarioaux = new BigDecimal(0);
				//boolean booCambioA14 = false;
				if (facturaXML.getDetalles().getDetalle() != null && !facturaXML.getDetalles().getDetalle().isEmpty()) {
					// logica para detalles repetidos.
					String exepcion = null;
					for (Detalle itera : facturaXML.getDetalles().getDetalle()) {
						exepcion = validaExcepcion(itera.getCodigoPrincipal(), facturaXML.getInfoTributaria().getRuc());
						if (exepcion != null) {
							if (exepcion.equals("EXCE")) {
								continue;
							}
						} else {
							if (itera.getCodigoAuxiliar() != null) {
								exepcion = validaExcepcion(itera.getCodigoAuxiliar(),
										facturaXML.getInfoTributaria().getRuc());
								if (exepcion != null) {

									if (exepcion.equals("EXCE")) {
										continue;
									}
								}
							}

						}

						//
						if (detalleMap.get(itera.getCodigoPrincipal()) == null) {
							if (itera.getDescuento() != null
									&& itera.getDescuento().compareTo(new BigDecimal("0.00")) > 0) {
								itera.setPrecioUnitario(itera.getPrecioTotalSinImpuesto()
										.divide(itera.getCantidad(), 4, RoundingMode.CEILING)
										.setScale(2, BigDecimal.ROUND_HALF_UP));
							}
							detalleMap.put(itera.getCodigoPrincipal(), itera);
						} else {
							Detalle aux = detalleMap.get(itera.getCodigoPrincipal());
							aux.setCantidad(aux.getCantidad().add(itera.getCantidad()));
							if (itera.getDescuento() != null
									&& itera.getDescuento().compareTo(new BigDecimal("0.00")) > 0) {
								aux.setPrecioTotalSinImpuesto(
										aux.getPrecioTotalSinImpuesto().add(itera.getPrecioTotalSinImpuesto()));
								aux.setPrecioUnitario(aux.getPrecioTotalSinImpuesto()
										.divide(aux.getCantidad(), 4, RoundingMode.CEILING)
										.setScale(2, BigDecimal.ROUND_HALF_UP));
							} else {
								aux.setPrecioUnitario(itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP));
							}
							detalleMap.put(aux.getCodigoPrincipal(), aux);
						}
					}

					List<Detalle> listdetalle2 = new ArrayList<Detalle>(detalleMap.values());

					for (Detalle itera : listdetalle2) {
						num++;
						Map map = new HashMap();
						map.put("TDETL", "TDETL");
						String linea = completar(String.valueOf(num).length(), num);

						map.put("linea", linea);
						boolean booUpc = false;
						String tipoPrincipal = null;
						String tipoSecundario = null;
						log.info("codigo principal" + itera.getCodigoPrincipal());
						tipoPrincipal = validaVpn(itera.getCodigoPrincipal());
						if (tipoPrincipal != null) {
							if (tipoPrincipal.equals("UPC")) {
								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC,
										itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
								map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
								map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
							}
							if (tipoPrincipal.equals("VPN")) {
								map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN,
										itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
								map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
							}
							if (tipoPrincipal.equals("ITEM")) {
								map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item,
										itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
								map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
							}
						} else {
							if (itera.getCodigoAuxiliar() != null) {
								tipoSecundario = validaVpn(itera.getCodigoAuxiliar());
								if (tipoSecundario != null) {
									if (tipoSecundario.equals("UPC")) {
										map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC,
												itera.getCodigoAuxiliar().length(), itera.getCodigoAuxiliar()));
										map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
										map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
									}
									if (tipoSecundario.equals("VPN")) {
										map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN,
												itera.getCodigoAuxiliar().length(), itera.getCodigoAuxiliar()));
										map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
										map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
									}
									if (tipoSecundario.equals("ITEM")) {
										map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item,
												itera.getCodigoAuxiliar().length(), itera.getCodigoAuxiliar()));
										map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
										map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
									}
								} else {
									log.info("Nose encontraron los codigos " + itera.getCodigoPrincipal() + "-"
											+ itera.getCodigoAuxiliar());
									respuesta
											.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
									respuesta.setMensajeCliente(
											"Nose encontraron los codigos en lista de Items devuelta por Reim "
													+ itera.getCodigoPrincipal());
									respuesta.setMensajeSistema(
											"Nose encontraron los codigos en lista de Items devuelta por Reim "
													+ itera.getCodigoPrincipal());
									respuesta.setEstado(Estado.ERROR.getDescripcion());
									facturaObj.setEstado(Estado.ERROR.getDescripcion());
									facturaObj.setMensajeErrorReim(
											"Nose encontraron los codigos en lista de Items devuelta por Reim "
													+ itera.getCodigoPrincipal());
									facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
											new StringBuilder()
													.append("Nose encontraron los codigos "
															+ itera.getCodigoPrincipal())
													.append(ordenCompra).toString());
									return respuesta;
								}
							} else {
								log.info("Nose encontraron los codigos " + itera.getCodigoPrincipal() + "-"
										+ itera.getCodigoAuxiliar());
								respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
								respuesta.setMensajeCliente(
										"Nose encontraron los codigos en lista de Items devuelta por Reim "
												+ itera.getCodigoPrincipal());
								respuesta.setMensajeSistema(
										"Nose encontraron los codigos en lista de Items devuelta por Reim "
												+ itera.getCodigoPrincipal());
								respuesta.setEstado(Estado.ERROR.getDescripcion());
								facturaObj.setEstado(Estado.ERROR.getDescripcion());
								facturaObj.setMensajeErrorReim(
										"Nose encontraron los codigos en lista de Items devuelta por Reim "
												+ itera.getCodigoPrincipal());
								facturaObj = this.servicioRecepcionFactura
										.recibirFactura(facturaObj,
												new StringBuilder()
														.append("Nose encontraron los codigos "
																+ itera.getCodigoPrincipal())
														.append(ordenCompra).toString());
								return respuesta;
							}
						}
						map.put("UPC_Supplement",
								completarEspaciosEmblancosLetras(ConstantesRim.UPC_Supplement, 0, ""));

						map.put("Original_Document_Quantity", completarEspaciosEmblancosNumeros(ConstantesRim.Original_Document_Quantity, 4, String.valueOf(itera.getCantidad())));
						BigDecimal cantidad = itera.getCantidad();
						BigDecimal pu;
						pu = itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP);
						totalUnidades = totalUnidades.add(itera.getCantidad());
						if (itera.getImpuestos().getImpuesto() != null) {
							if (!itera.getImpuestos().getImpuesto().isEmpty()) {
								for (Impuesto item : itera.getImpuestos().getImpuesto()) {
									if (item.getCodigoPorcentaje().equals("0")) {
										map.put("Original_VAT_Code",completarEspaciosEmblancosLetras(6, "L".length(), "L"));
										map.put("Original_VAT_rate", completarEspaciosEmblancosNumeros(20, 10,String.valueOf(item.getTarifa())));
										strPorccentaje = "L";
										totalsinimpiesto0 = totalsinimpiesto0.add(pu.multiply(cantidad));

									} else if (item.getCodigoPorcentaje().equals("2")) {
										map.put("Original_VAT_Code", completarEspaciosEmblancosLetras(6, "E".length(), "E"));
										map.put("Original_VAT_rate", completarEspaciosEmblancosNumeros(20, 10,String.valueOf(item.getTarifa())));
										totalsinimpiesto14 = totalsinimpiesto14.add(pu.multiply(cantidad));
										
										log.info("Precio unitario"+pu+"*"+cantidad+" = "+pu.multiply(cantidad) );
										
										log.info("Sumatoria ***  "+totalsinimpiesto14 );
									} 

									if (item.getCodigo().equals("5")) {
										precioUnitarioaux = precioUnitarioaux.add(item.getValor());
									}

									if (item.getCodigo().equals("3")) {
										precioUnitarioaux = precioUnitarioaux.add(item.getValor());
									}
								}
							}
						}

						map.put("Original_Unit_Cost",
								completarEspaciosEmblancosNumeros(20, 4,
										String.valueOf(itera.getPrecioTotalSinImpuesto().add(precioUnitarioaux)
												.divide(itera.getCantidad(), 4, RoundingMode.CEILING).setScale(2, 4))));

						precioUnitarioaux = new BigDecimal(0);

						map.put("Total_Allowance", completarEspaciosEmblancosNumeros(20, 4, "0"));

						list.add(map);
					}
				}
				ArrayList listTotalImpuesto = new ArrayList();
				ArrayList listTotalImpuestoIceIbrp = new ArrayList();
				
				BigDecimal totalImpuesto = new BigDecimal(0);
				BigDecimal totalimpuestoIceIbrp = new BigDecimal(0);
				BigDecimal totalimpuestoIce = new BigDecimal(0);
				BigDecimal descuentosolidario = new BigDecimal(0);
				boolean booiceIbrp = false;
				boolean boodescuentosoli = false;

				if (facturaXML.getInfoFactura().getCompensaciones() != null && !facturaXML.getInfoFactura().getCompensaciones().getCompensacion().isEmpty()) {
					log.info("compensaciones traduciendo 1111");
					for (Compensacion compen : facturaXML.getInfoFactura().getCompensaciones().getCompensacion()) {
						Map mapiceibrp = new HashMap();
						mapiceibrp.put("TNMRC", "TNMRC");
						num++;
						String linea = completar(String.valueOf(num).length(), num);
						mapiceibrp.put("linea", linea);

						mapiceibrp.put("MER_CODE",
								completarEspaciosEmblancosLetras(ConstantesRim.MER_CODE, "DSCSOL".length(), "DSCSOL"));
						mapiceibrp.put("signotrn", "-");

						descuentosolidario = compen.getValor().setScale(2, BigDecimal.ROUND_HALF_UP);

						mapiceibrp.put("MER_AMT",
								completarEspaciosEmblancosNumeros(ConstantesRim.MER_AMT, ConstantesRim.Decimales4,
										String.valueOf(compen.getValor().setScale(2, BigDecimal.ROUND_HALF_UP))));
						mapiceibrp.put("MER_VAT_CODE",
								completarEspaciosEmblancosLetras(ConstantesRim.MER_VAT_CODE, "L".length(), "L"));
						mapiceibrp.put("MER_VAT_RATE", completarEspaciosEmblancosNumeros(ConstantesRim.MER_VAT_RATE,
								ConstantesRim.Decimales10, String.valueOf("0")));
						mapiceibrp.put("Ser_Per_Ind",
								completarEspaciosEmblancosLetras(ConstantesRim.Ser_Per_Ind, "N".length(), "N"));
						mapiceibrp.put("MER_STORE", completarEspaciosEmblancosLetras(10, "".length(), ""));
						boodescuentosoli = true;
						listTotalImpuestoIceIbrp.add(mapiceibrp);
					}
				}
				if (facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto() != null && !facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().isEmpty()) {
					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							log.info("-- -- -->"+impu.getCodigo());
							if (impu.getCodigo().equals("3")) {
								totalimpuestoIce = totalimpuestoIce.add(impu.getValor());
								totalsinimpiesto14 = totalsinimpiesto14.add(impu.getValor());
								log.info("Sumo valor a importe total "+impu.getValor());
								booiceIbrp = true;
							} else if (impu.getCodigo().equals("5")) {
								totalimpuestoIceIbrp = totalimpuestoIceIbrp.add(impu.getValor());
								booiceIbrp = true;
								
							} else if (!impu.getCodigo().equals("2")) {
								log.info(new StringBuilder()
										.append("La factura tiene codigo de impuesto  no contemplado en reim")
										.append(facturaXML.getInfoTributaria().getClaveAcceso()).toString());
								respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
								respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");

								respuesta.setMensajeSistema("La factura tiene codigo de impuesto  no contemplado en reim");

								respuesta.setEstado(
										com.gizlocorp.gnvoice.enumeracion.Estado.COMPLETADO.getDescripcion());
								facturaObj.setEstado(com.gizlocorp.gnvoice.enumeracion.Estado.ERROR.getDescripcion());
								facturaObj.setMensajeErrorReim(
										"La factura tiene codigo de impuesto  no contemplado en reim");

								facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
										new StringBuilder()
												.append("La factura tiene codigo de impuesto  no contemplado en reim")
												.append(ordenCompra).toString());
								return respuesta;
							}
						}
					}
					
					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							Map map = new HashMap();
							map.put("TVATS", "TVATS");
							if (impu.getCodigoPorcentaje().equals("0")) {
								num++;
								String linea = completar(String.valueOf(num).length(), num);
								map.put("linea", linea);
								map.put("VAT_code",completarEspaciosEmblancosLetras(ConstantesRim.VAT_code, "L".length(), "L"));

								map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
										ConstantesRim.Decimales10, String.valueOf("0")));
								map.put("VAT_SIGNO", "+");
								map.put("Cost_VAT_code",completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
												ConstantesRim.Decimales4,
												String.valueOf(totalsinimpiesto0
														.add(totalimpuestoIceIbrp).subtract(descuentosolidario)
														.setScale(2, BigDecimal.ROUND_HALF_UP))));

								totalImpuesto = totalImpuesto.add(impu.getValor());
								listTotalImpuesto.add(map);
							} else {
								if (impu.getCodigoPorcentaje().equals("2") || impu.getCodigoPorcentaje().equals("3")) {
									num++;
									String linea = completar(String.valueOf(num).length(), num);
									map.put("linea", linea);
									map.put("VAT_code", completarEspaciosEmblancosLetras(ConstantesRim.VAT_code,"E".length(), "E"));
									map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,ConstantesRim.Decimales10, String.valueOf("12")));
									impu.setValor(impu.getBaseImponible().multiply(new BigDecimal("0.12")));
									

									map.put("VAT_SIGNO", "+");
									
									log.info("->>>>>>>>>>>"+completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code, ConstantesRim.Decimales4,
											String.valueOf(totalsinimpiesto14.setScale(2, BigDecimal.ROUND_HALF_UP))));
									map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code, ConstantesRim.Decimales4,
											String.valueOf(totalsinimpiesto14.setScale(2, BigDecimal.ROUND_HALF_UP))));

									totalImpuesto = totalImpuesto.add(impu.getValor());
									listTotalImpuesto.add(map);
								}
							}
						}
					}
					if ((booiceIbrp || boodescuentosoli) && !strPorccentaje.equals("L")) {
						Map map = new HashMap();
						map.put("TVATS", "TVATS");
						num++;
						String linea = completar(String.valueOf(num).length(), num);
						map.put("linea", linea);
						map.put("VAT_code",completarEspaciosEmblancosLetras(ConstantesRim.VAT_code, "L".length(), "L"));
						map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,ConstantesRim.Decimales10, String.valueOf("0")));
						
						if (boodescuentosoli && booiceIbrp) {
							map.put("VAT_SIGNO", "+");
							map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
									ConstantesRim.Decimales4, String.valueOf(totalimpuestoIceIbrp
											.subtract(descuentosolidario).setScale(2, BigDecimal.ROUND_HALF_UP))));
						} else {
							if (!boodescuentosoli && booiceIbrp) {
								map.put("VAT_SIGNO", "+");
								map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
										ConstantesRim.Decimales4,
										String.valueOf(totalimpuestoIceIbrp.setScale(2, BigDecimal.ROUND_HALF_UP))));
							} else {
								if (boodescuentosoli && !booiceIbrp) {
									map.put("VAT_SIGNO", "-");
									map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code, ConstantesRim.Decimales4,
											String.valueOf(descuentosolidario.setScale(2, BigDecimal.ROUND_HALF_UP))));
								}
							}
						}

						listTotalImpuesto.add(map);

					}
				}
				int fdetalle = num + 1;// 5
				int fin = num + 2;// 6
				int numLinea = num - 2;// 2
				int totalLinea = num;// 4

				Parametro dirParametro2 = cacheBean.consultarParametro(Constantes.DIR_RIM_ESQUEMA, emisor.getId());
				// String dirServidor2 = dirParametro2 != null ?
				// dirParametro2.getValor() : "";

				String dirServidor2 = "/data/gnvoice/recursos/rim_vm/";

				VelocityEngine ve = new VelocityEngine();
				ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, dirServidor2);
				ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
				// ve.setProperty(RuntimeConstants.RESOURCE_LOADER,
				// "classpath");
				// ve.setProperty("classpath.resource.loader.class",
				// ClasspathResourceLoader.class.getName());
				ve.init();

				Template t = ve.getTemplate("traductor.vm");
				VelocityContext context = new VelocityContext();

				context.put("fecha", strDate);
				context.put("petList", list);

				context.put("Document_Type",
						completarEspaciosEmblancosLetras(ConstantesRim.Document_Type, "MRCHI".length(), "MRCHI"));

				String vendorDocument = new StringBuilder().append(facturaXML.getInfoTributaria().getEstab()).append("")
						.append(facturaXML.getInfoTributaria().getPtoEmi()).append("")
						.append(facturaXML.getInfoTributaria().getSecuencial()).toString();

				context.put("Vendor_Document_Number", completarEspaciosEmblancosLetras(
						ConstantesRim.Vendor_Document_Number, vendorDocument.length(), vendorDocument));
				context.put("Group_ID", completarEspaciosEmblancosLetras(ConstantesRim.Group_ID, "".length(), ""));
				log.info("***Traduciendo Reim factura 444***");
				if (ceDisresponse == null || ceDisresponse.getVendor_type() == null
						|| ceDisresponse.getVendor_id() == null) {
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service ");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim(
							"Ha ocurrido un error no devolvio datos el web service para en munero de compra."
									+ ordenCompra);
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							new StringBuilder().append("El web service no encontro informacion. para la orden")
									.append(ordenCompra).toString());
					return respuesta;
				}

				log.info("***Traduciendo Reim factura 555***");
				context.put("Vendor_Type", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Type,
						ceDisresponse.getVendor_type().length(), ceDisresponse.getVendor_type()));

				context.put("Vendor_ID", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_ID,
						ceDisresponse.getVendor_id().length(), ceDisresponse.getVendor_id()));

				Date fechaEmision = sdfDate2.parse(facturaXML.getInfoFactura().getFechaEmision());
				context.put("Vendor_Document_Date", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Document_Date,
						sdfDate.format(fechaEmision).length(), sdfDate.format(fechaEmision)));

				context.put("Order_Number",
						completarEspaciosEmblancosNumeros(ConstantesRim.Order_Number, 0, String.valueOf(ordenCompra)));

				if ((ceDisresponse.getLocation() != null) && (!ceDisresponse.getLocation().isEmpty())) {
					context.put("Location",
							completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0, ceDisresponse.getLocation()));
				} else {
					context.put("Location", completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0, ""));
				}

				if ((ceDisresponse.getLocation() != null) && (!ceDisresponse.getLocation().isEmpty())) {
					context.put("Location_Type", completarEspaciosEmblancosLetras(ConstantesRim.Location_Type,
							ceDisresponse.getLocation().length(), ceDisresponse.getLocation_type()));
				} else {
					context.put("Location_Type",
							completarEspaciosEmblancosLetras(ConstantesRim.Location_Type, "".length(), ""));
				}

				context.put("Terms", completarEspaciosEmblancosLetras(ConstantesRim.Terms, "".length(), ""));
				context.put("Due_Date", completarEspaciosEmblancosLetras(ConstantesRim.Due_Date, "".length(), ""));
				context.put("Payment_method",
						completarEspaciosEmblancosLetras(ConstantesRim.Payment_method, "".length(), ""));
				context.put("Currency_code",
						completarEspaciosEmblancosLetras(ConstantesRim.Currency_code, "USD".length(), "USD"));

				context.put("Exchange_rate",
						completarEspaciosEmblancosNumeros(ConstantesRim.Exchange_rate, 4, String.valueOf("1")));

				context.put("Total_Cost",
						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Cost, 4,
								String.valueOf(facturaXML.getInfoFactura().getTotalSinImpuestos()
										.add(totalimpuestoIceIbrp).add(totalimpuestoIce).subtract(descuentosolidario)
										.setScale(2, BigDecimal.ROUND_HALF_UP))));

				context.put("Total_VAT_Amount", completarEspaciosEmblancosNumeros(ConstantesRim.Total_VAT_Amount, 4,
						String.valueOf(totalImpuesto.setScale(2, BigDecimal.ROUND_HALF_UP))));

				context.put("Total_Quantity", completarEspaciosEmblancosNumeros(ConstantesRim.Total_Quantity, 4,
						String.valueOf(totalUnidades)));

				context.put("Total_Discount",
						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Discount, 4, String.valueOf(facturaXML
								.getInfoFactura().getTotalDescuento().setScale(2, BigDecimal.ROUND_HALF_UP))));

				context.put("Freight_Type",completarEspaciosEmblancosLetras(ConstantesRim.Freight_Type, "".length(), ""));
				context.put("Paid_Ind", "N");
				context.put("Multi_Location", "N");
				context.put("Merchan_dise_Type", "N");
				context.put("Deal_Id", completarEspaciosEmblancosLetras(ConstantesRim.Deal_Id, "".length(), ""));
				context.put("Deal_Approval_Indicator",completarEspaciosEmblancosLetras(ConstantesRim.Deal_Approval_Indicator, "".length(), ""));
				context.put("RTV_indicator", "N");

				if (offline) {
					context.put("Custom_Document_Reference_1",
							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_1,
									autorizacionOfflineXML.getNumeroAutorizacion().length(),
									autorizacionOfflineXML.getNumeroAutorizacion()));
				} else {
					context.put("Custom_Document_Reference_1",
							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_1,
									autorizacionXML.getNumeroAutorizacion().length(),
									autorizacionXML.getNumeroAutorizacion()));
				}

				String fechaAutorizacion = "";
				if (autorizacionXML.getFechaAutorizacion() != null) {
					fechaAutorizacion = FechaUtil.formatearFecha(
							autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime(), "dd/MM/yyyy HH:mm");
				} else {
					fechaAutorizacion = facturaXML.getInfoFactura().getFechaEmision();
				}

				context.put("Custom_Document_Reference_2", completarEspaciosEmblancosLetras(
						ConstantesRim.Custom_Document_Reference_2, fechaAutorizacion.length(), fechaAutorizacion));

				String ice = "";

				if ((totalimpuestoIce != null) && (totalimpuestoIce.compareTo(new BigDecimal(0)) != 0))
					ice = String.valueOf(totalimpuestoIce);
				else {
					ice = "";
				}

				context.put("Custom_Document_Reference_3", completarEspaciosEmblancosLetras(90, ice.length(), ice));

				String ibrp = "";

				if ((totalimpuestoIceIbrp != null) && (totalimpuestoIceIbrp.compareTo(new BigDecimal(0)) != 0))
					ibrp = String.valueOf(totalimpuestoIceIbrp);
				else {
					ibrp = "";
				}

				context.put("Custom_Document_Reference_4", completarEspaciosEmblancosLetras(90, ibrp.length(), ibrp));

				context.put("Cross_reference",
						completarEspaciosEmblancosLetras(ConstantesRim.Cross_reference, "".length(), ""));

				context.put("petListTotalImpuesto", listTotalImpuesto);
				context.put("petListTotalImpuestoIceIbrp", listTotalImpuestoIceIbrp);
				context.put("secuencial", facturaXML.getInfoTributaria().getSecuencial());

				context.put("finT", completar(String.valueOf(fdetalle).length(), fdetalle));
				context.put("finF", completar(String.valueOf(fin).length(), fin));
				context.put("numerolineaDestalle", completarLineaDetalle(String.valueOf(numLinea).length(), numLinea));
				context.put("totalLinea", completar(String.valueOf(totalLinea).length(), totalLinea));

				StringWriter writer = new StringWriter();
				t.merge(context, writer);
				Calendar now2 = Calendar.getInstance();
				Parametro repositorioRim = this.cacheBean.consultarParametro("RIM_REPOSITO_ARCHIVO", emisor.getId());

				String stvrepositorioRim = repositorioRim != null ? repositorioRim.getValor() : "";

				Random rng = new Random();
				int dig5 = rng.nextInt(90000) + 10000; // siempre 5 digitos

				log.info("***creando archivo traducido factura 999***" + nombreArchivo);

				String archivoCreado = DocumentUtil.createDocumentText(writer.toString().getBytes(), nombreArchivo,
						FechaUtil.formatearFecha(now2.getTime(), "yyyyMMdd").toString(), stvrepositorioRim);

				String archivoCreadoProcesado = archivoCreado + ".loaded";
				File fileOut = new File(archivoCreado);
				File fileLoaded = new File(archivoCreadoProcesado);
				boolean existFileOut = fileOut != null && fileOut.exists() && !fileOut.isDirectory();
				boolean existFileLoaded = fileLoaded != null && fileLoaded.exists() && !fileLoaded.isDirectory();

				if (existFileOut || existFileLoaded) {
					facturaObj.setFechaEntReim(new Date());
					facturaObj.setArchivoGenReim(archivoCreado);
				} else {
					respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					MensajeRespuesta msgObj = new MensajeRespuesta();
					msgObj.setIdentificador("-100");
					msgObj.setMensaje("Ha ocurrido un problema: " + "No se creo el archivo .out reim");
					respuesta.getMensajes().add(msgObj);
					respuesta.setMensajeCliente("No se creo el archivo .out reim");
					respuesta.setMensajeSistema("No se creo el archivo .out reim");
					System.out.println("No se creo el archivo .out reim");
					return respuesta;
				}

			}

			// fin agregado rim

			facturaObj.setNumeroAutorizacion(autorizacionXML.getNumeroAutorizacion());
			if (autorizacionXML.getFechaAutorizacion() != null) {
				facturaObj.setFechaAutorizacion(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime());
			} else {
				SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
				facturaObj.setFechaAutorizacion(formato.parse(facturaXML.getInfoFactura().getFechaEmision()));
			}

			facturaObj.setEstado("AUTORIZADO");
			facturaObj.setTareaActual(Tarea.AUT);
			facturaObj.setTipoGeneracion(TipoGeneracion.REC);

			// si no existe pdf creo pdf
			Parametro dirParametro = cacheBean.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
			String dirServidor = dirParametro != null ? dirParametro.getValor() : "";

			facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,
					"El comprobante se a cargado de forma exitosa");
			respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
			respuesta.setMensajeCliente("El comprobante se a cargado de forma exitosa");
			respuesta.setMensajeSistema("El comprobante se a cargado de forma exitosa");

		} catch (Exception ex) {
			respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			MensajeRespuesta msgObj = new MensajeRespuesta();
			msgObj.setIdentificador("-100");
			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
			respuesta.getMensajes().add(msgObj);
			respuesta.setMensajeCliente("Ha ocurrido un problema");
			respuesta.setMensajeSistema(ex.getMessage());

			log.error(ex.getMessage(), ex);
		}

		return respuesta;
	}

	public boolean validaCedisVirtual(String cedisVirtual, String ruc) {

		if ("1791050665001".equals(ruc) && "8106".equals(cedisVirtual)) {
			return false;
		} else if ("1790710319001".equals(ruc) && "8101".equals(cedisVirtual)) {
			return false;
		} else if ("1791715772001".equals(ruc) && "8102".equals(cedisVirtual)) {
			return false;
		} else if ("1792239710001".equals(ruc) && "8105".equals(cedisVirtual)) {
			return false;
		} else if ("1792650798001".equals(ruc) && "8107".equals(cedisVirtual)) {
			return false;
		} else if ("8104".equals(cedisVirtual) && ("1791849655001".equals(ruc) || "0992638826001".equals(ruc))) {
			return false;
		} else if ("1791849655001".equals(ruc) && ("8000".equals(cedisVirtual) || "8000".equals(cedisVirtual))) {
			return false;
		}

		return true;
	}
	
	public Boolean validaCantidadZero(com.gizlocorp.gnvoice.xml.factura.Factura facturaXML) {

		for (Detalle itera : facturaXML.getDetalles().getDetalle()) {
			if (itera.getCantidad().equals(new BigDecimal("0.00"))) {
				return true;
			}

			if (itera.getCantidad().equals(new BigDecimal("0.0"))) {
				return true;
			}

			if (itera.getCantidad().equals(new BigDecimal("0"))) {
				return true;
			}

		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	// @Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FacturaRecibirResponse recibirOld(FacturaRecibirRequest facturaRecibirRequest) {

		FacturaRecibirResponse respuesta = new FacturaRecibirResponse();

		BigDecimal baseImponible0 = BigDecimal.ZERO;
		BigDecimal baseImponible12 = BigDecimal.ZERO;
		BigDecimal iva = BigDecimal.ZERO;
		BigDecimal subtotal = BigDecimal.ZERO;
		String nombreArchivo = "";

		boolean offline = false;

		try {
			String autorizacion = DocumentUtil.readContentFile(facturaRecibirRequest.getComprobanteProveedor());

			Autorizacion autorizacionXML = null;
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;

			String numeroAutorizcionLeng = "";
			if (autorizacion.contains("&lt;numeroAutorizacion&gt;")
					&& autorizacion.contains("&lt;/numeroAutorizacion&gt;")) {
				String[] ab = autorizacion.split("&lt;numeroAutorizacion&gt;");
				numeroAutorizcionLeng = ab[1].split("&lt;/numeroAutorizacion&gt;")[0].trim();
			}

			if (autorizacion.contains("<numeroAutorizacion>") && autorizacion.contains("</numeroAutorizacion>")) {
				String[] ab = autorizacion.split("<numeroAutorizacion>");
				numeroAutorizcionLeng = ab[1].split("</numeroAutorizacion>")[0].trim();

			}

			if (autorizacion.contains("&lt;claveAcceso&gt;")) {

				String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

				String claveConsultar = "";
				String numeroAutorizcion = "";

				int a = autorizacion.indexOf("&lt;claveAcceso&gt;");

				int desde = a + 19;
				int hasta = desde + 49;

				claveConsultar = autorizacion.substring(desde, hasta);

				if (autorizacion.contains("&lt;numeroAutorizacion&gt;")
						&& autorizacion.contains("&lt;/numeroAutorizacion&gt;")) {
					String[] ab = autorizacion.split("&lt;numeroAutorizacion&gt;");
					numeroAutorizcion = ab[1].split("&lt;/numeroAutorizacion&gt;")[0].trim();
				}

				if (autorizacion.contains("<numeroAutorizacion>") && autorizacion.contains("</numeroAutorizacion>")) {
					String[] ab = autorizacion.split("<numeroAutorizacion>");
					numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
				}

				if (claveConsultar.equals(numeroAutorizcion)) {
					String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
					autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);
					offline = true;

				} else {
					autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
				}

			} else {
				if (autorizacion != null && !autorizacion.isEmpty()) {
					String tmp = autorizacion;
					for (int i = 0; i < tmp.length(); i++) {
						if (tmp.charAt(i) == '<') {
							break;
						}
						autorizacion = autorizacion.replace(tmp.charAt(i), ' ');
					}

					autorizacion = autorizacion.replace("<Autorizacion>", "<autorizacion>");
					autorizacion = autorizacion.replace("</Autorizacion>", "</autorizacion>");

					if (autorizacion.contains("<RespuestaAutorizacionComprobante>")) {
						autorizacion = autorizacion.replace("<RespuestaAutorizacionComprobante>", "");
						autorizacion = autorizacion.replace("</RespuestaAutorizacionComprobante>", "");
					}

					autorizacion = autorizacion.trim();
				}

				if (numeroAutorizcionLeng.length() <= 36) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
					String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
					String claveConsultar = "";
					String numeroAutorizcion = "";

					int a = autorizacion.indexOf("<claveAcceso>");

					int desde = a + 13;
					int hasta = desde + 49;

					claveConsultar = autorizacion.substring(desde, hasta);

					autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);
					autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);

					if (autorizacionOfflineXML != null && autorizacionOfflineXML.getComprobante() != null) {
						offline = true;
					}

					if ((autorizacionOfflineXML == null || autorizacionOfflineXML.getComprobante() == null)
							&& (autorizacionXML == null || autorizacionXML.getComprobante() == null)) {
						respuesta.setEstado(Estado.ERROR.getDescripcion());
						respuesta.setMensajeCliente("Comprobante no tiene numero de autorizacion ");
						respuesta.setMensajeSistema("Comprobante no tiene numero de autorizacion ");

						return respuesta;
					}

				} else {
					try {
						autorizacionXML = getAutorizacionXML(autorizacion);
					} catch (Exception ex) {
						String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

						String claveConsultar = "";
						String numeroAutorizcion = "";

						int a = autorizacion.indexOf("<claveAcceso>");

						int desde = a + 13;
						int hasta = desde + 49;

						claveConsultar = autorizacion.substring(desde, hasta);

						if (autorizacion.contains("<numeroAutorizacion>")
								&& autorizacion.contains("</numeroAutorizacion>")) {
							String[] ab = autorizacion.split("<numeroAutorizacion>");
							numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
						}

						if (claveConsultar.equals(numeroAutorizcion)) {
							String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
							autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff,
									claveConsultar);
							offline = true;
						} else {
							autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
						}
					}
				}
			}
			
			
			
			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = null;
			if (offline) {
				if (autorizacionOfflineXML == null) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				if (!(autorizacionOfflineXML.getComprobante() != null)) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				String factura = autorizacionOfflineXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
				try {
					facturaXML = getFacturaXML(factura);
				} catch (Exception ex) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

					String claveConsultar = "";
					String numeroAutorizcion = "";

					if (autorizacion.contains("<claveAcceso>") && autorizacion.contains("</claveAcceso>")) {
						String[] ab = autorizacion.split("<claveAcceso>");
						claveConsultar = ab[1].split("</claveAcceso>")[0].trim();
					}

					if (autorizacion.contains("<numeroAutorizacion>")
							&& autorizacion.contains("</numeroAutorizacion>")) {
						String[] ab = autorizacion.split("<numeroAutorizacion>");
						numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
					}

					if (claveConsultar.equals(numeroAutorizcion)) {
						String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
						autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff,
								claveConsultar);
						offline = true;
						String factura3 = autorizacionOfflineXML.getComprobante();
						factura3 = factura3.replace("<![CDATA[", "");
						factura3 = factura3.replace("]]>", "");
						facturaXML = getFacturaXML(factura3);
					} else {
						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
						String factura2 = autorizacionXML.getComprobante();
						factura2 = factura2.replace("<![CDATA[", "");
						factura2 = factura2.replace("]]>", "");
						facturaXML = getFacturaXML(factura2);

					}
				}

			} else {
				if (autorizacionXML == null) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				if (!(autorizacionXML.getComprobante() != null)) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return respuesta;
				}
				String factura = autorizacionXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
				try {
					facturaXML = getFacturaXML(factura);
				} catch (Exception ex) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

					String claveConsultar = "";
					String numeroAutorizcion = "";

					if (autorizacion.contains("<claveAcceso>") && autorizacion.contains("</claveAcceso>")) {
						String[] ab = autorizacion.split("<claveAcceso>");
						claveConsultar = ab[1].split("</claveAcceso>")[0].trim();
					}

					if (autorizacion.contains("<numeroAutorizacion>")
							&& autorizacion.contains("</numeroAutorizacion>")) {
						String[] ab = autorizacion.split("<numeroAutorizacion>");
						numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
					}

					if (claveConsultar.equals(numeroAutorizcion)) {
						String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
						autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff,
								claveConsultar);
						offline = true;
						String factura3 = autorizacionOfflineXML.getComprobante();
						factura3 = factura3.replace("<![CDATA[", "");
						factura3 = factura3.replace("]]>", "");
						facturaXML = getFacturaXML(factura3);
					} else {
						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
						String factura2 = autorizacionXML.getComprobante();
						factura2 = factura2.replace("<![CDATA[", "");
						factura2 = factura2.replace("]]>", "");
						facturaXML = getFacturaXML(factura2);
					}
				}

			}

			// com.gizlocorp.gnvoice.xml.factura.Factura facturaXML =
			// getFacturaXML(factura);
			if (facturaXML != null) {
				subtotal = facturaXML.getInfoFactura().getTotalSinImpuestos();
				if (facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto() != null
						&& !facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().isEmpty()) {
					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							if (impu.getCodigoPorcentaje().equals("0")) {
								baseImponible0 = impu.getBaseImponible();
							} else {
								if (impu.getCodigoPorcentaje().equals("2") || impu.getCodigoPorcentaje().equals("3")) {
									baseImponible12 = impu.getBaseImponible();
									iva = impu.getValor();
								}
							}
						}
					}
				}
			}

			nombreArchivo = "edidlinv_" + FechaUtil.formatearFecha(Calendar.getInstance().getTime(), "yyyyMMdd") + "_"
					+ facturaXML.getInfoTributaria().getClaveAcceso();

			com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaObj = servicioRecepcionFactura
					.obtenerComprobanteRecepcion(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);

			if (facturaObj != null && "AUTORIZADO".equals(facturaObj.getEstado())) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeCliente("El comprobante con la clave de acceso " + facturaObj.getClaveAcceso()
						+ " ya esta registrada en el sistema como " + facturaObj.getTipoGeneracion().getDescripcion());

				respuesta.setMensajeSistema("El comprobante con la clave de acceso " + facturaObj.getClaveAcceso()
						+ " ya esta registrada en el sistema como " + facturaObj.getTipoGeneracion().getDescripcion());
				return respuesta;
			}

			if (facturaObj == null) {
				facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFacturaRecepcion(facturaXML);

			} else {
				com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaAux = facturaObj;
				facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFacturaRecepcion(facturaXML);
				facturaObj.setId(facturaAux.getId());
			}
			if (facturaObj.getContribuyenteEspecial().length() > 5) {
				int leng = facturaObj.getContribuyenteEspecial().length();
				facturaObj.setContribuyenteEspecial(facturaObj.getContribuyenteEspecial().substring(leng - 5, leng));
			}

			if (facturaObj.getObligadoContabilidad() == null) {
				facturaObj.setObligadoContabilidad("NO");
			}

			facturaObj.setInfoAdicional(
					StringUtil.validateInfoXML(facturaXML.getInfoTributaria().getRazonSocial().trim()));

			facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
			facturaObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			facturaObj.setProceso(facturaRecibirRequest.getProceso());
			facturaObj.setArchivo(facturaRecibirRequest.getComprobanteProveedor());

			facturaObj.setFechaLecturaTraductor(new Date());
			facturaObj.setBaseCero(baseImponible0);
			facturaObj.setBaseDoce(baseImponible12);
			facturaObj.setIva(iva);
			facturaObj.setTipoGeneracion(TipoGeneracion.REC);

			if (offline) {
				if (autorizacionOfflineXML.getNumeroAutorizacion() == null
						|| autorizacionOfflineXML.getNumeroAutorizacion().isEmpty()) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
					respuesta.setMensajeCliente("El comprobante no tiene numero de Autorizacion");

					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,
							"Comprobante no tiene numero de Autorizacion");
					return respuesta;

				}
			} else {
				if (autorizacionXML.getNumeroAutorizacion() == null
						|| autorizacionXML.getNumeroAutorizacion().isEmpty()) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
					respuesta.setMensajeCliente("El comprobante no tiene numero de Autorizacion");

					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,
							"Comprobante no tiene numero de Autorizacion");
					return respuesta;

				}
			}

			Organizacion emisor = cacheBean.obtenerOrganizacion(facturaXML.getInfoFactura().getIdentificacionComprador());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());

				facturaObj.setEstado(Estado.ERROR.getDescripcion());
				facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,"No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
				return respuesta;
			}

			if (facturaRecibirRequest.getMailOrigen() != null && !facturaRecibirRequest.getMailOrigen().isEmpty()) {
				facturaObj.setCorreoNotificacion(facturaRecibirRequest.getMailOrigen());
			}

			boolean manual = false;

			if ((facturaRecibirRequest.getOrdenCompra() != null) && (!facturaRecibirRequest.getOrdenCompra().isEmpty())) {

				manual = true;
			}

			// TODO agregar velocity
			if (facturaRecibirRequest.getProceso().equals(com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {

				String ordenCompra = null;
				String ordenCompraName = "orden";
				String ordenCompraName2 = "compra";
				String ordenCompraName3 = "Orden_de_entrega";
				if (facturaXML.getInfoAdicional() != null) {
					for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
						if ((item.getNombre().trim().toUpperCase().contains(ordenCompraName.toUpperCase()))
								|| (item.getNombre().trim().toUpperCase().contains(ordenCompraName2.toUpperCase()))) {
							if (item.getNombre().trim().toUpperCase().contains(ordenCompraName3.toUpperCase())) {
								continue;
							}
							ordenCompra = item.getValue().trim();
							break;
						}
					}
				}
				ceDisresponse = new CeDisResponse();
				if (ordenCompra != null) {
					if (ordenCompra.matches("[0-9]*")) {
						facturaObj.setOrdenCompra(ordenCompra);
						ceDisresponse = consultaWebservice(facturaXML.getInfoTributaria().getRuc(),
								facturaObj.getOrdenCompra());
					} else {
						String orden = ordenCompra.replaceAll("[^0-9]", "");
						if (orden.matches("[0-9]*")) {
							facturaObj.setOrdenCompra(orden);
							log.info("ceDisresponse 111***");
							ceDisresponse = consultaWebservice(facturaXML.getInfoTributaria().getRuc(),
									facturaObj.getOrdenCompra());
							log.info("ceDisresponse 222***");
						} else {
							ceDisresponse = null;
						}

					}
				}

				if ((facturaRecibirRequest.getOrdenCompra() != null) && (!facturaRecibirRequest.getOrdenCompra().isEmpty()) && manual) {
					if (facturaRecibirRequest.getOrdenCompra().matches("[0-9]*")) {
						facturaObj.setOrdenCompra(facturaRecibirRequest.getOrdenCompra());

						ceDisresponse = consultaWebservice(facturaXML.getInfoTributaria().getRuc(),facturaObj.getOrdenCompra());
						ordenCompra = facturaRecibirRequest.getOrdenCompra();
					} else {
						ceDisresponse = null;
					}
				}
				if (ceDisresponse != null && !ceDisresponse.getOrder_status().isEmpty()) {
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					respuesta.setMensajeSistema("Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							"Error en el estatus de la Orden" + ceDisresponse.getOrder_status());
					return respuesta;
				}

				if (ordenCompra == null || ordenCompra.isEmpty()) {

					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error nose encuentra el nro orden de compra");
					respuesta.setMensajeSistema("Ha ocurrido un error nose encuentra el nro orden de compra");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra");
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							"El comprobante no tiene orden de compra ingresar manualmente");
					return respuesta;
				}

				if (ceDisresponse == null) {

					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim(
							"Ha ocurrido un error no devolvio datos el web service para en munero de compra."
									+ ordenCompra);
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							new StringBuilder().append("El web service no encontro informacion. para la orden")
									.append(ordenCompra).toString());
					return respuesta;
				}
				// fin servicio web
				
				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");// dd/MM/yyyy
				SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd/MM/yyyy");
				Date now = new Date();
				String strDate = sdfDate.format(now);
				String strPorccentaje = "E";
				int num = 2;
				ArrayList list = new ArrayList();
				BigDecimal totalUnidades = new BigDecimal(0);
				BigDecimal totalsinimpiesto0 = new BigDecimal(0);
				BigDecimal totalsinimpiesto14 = new BigDecimal(0);
				Map<String, Detalle> detalleMap = new HashMap<String, Detalle>();// para
																					// repetidos
				BigDecimal precioUnitarioaux = new BigDecimal(0);
				boolean booCambioA14 = false;
				if (facturaXML.getDetalles().getDetalle() != null && !facturaXML.getDetalles().getDetalle().isEmpty()) {
					// logica para detalles repetidos.
					String exepcion = null;
					for (Detalle itera : facturaXML.getDetalles().getDetalle()) {
						exepcion = validaExcepcion(itera.getCodigoPrincipal(), facturaXML.getInfoTributaria().getRuc());
						if (exepcion != null) {
							if (exepcion.equals("EXCE")) {
								continue;
							}
						} else {
							if (itera.getCodigoAuxiliar() != null) {
								exepcion = validaExcepcion(itera.getCodigoAuxiliar(),
										facturaXML.getInfoTributaria().getRuc());
								if (exepcion != null) {

									if (exepcion.equals("EXCE")) {
										continue;
									}
								}
							}

						}

						//
						if (detalleMap.get(itera.getCodigoPrincipal()) == null) {
							if (itera.getDescuento() != null
									&& itera.getDescuento().compareTo(new BigDecimal("0.00")) > 0) {
								itera.setPrecioUnitario(itera.getPrecioTotalSinImpuesto()
										.divide(itera.getCantidad(), 4, RoundingMode.CEILING)
										.setScale(2, BigDecimal.ROUND_HALF_UP));
							}
							detalleMap.put(itera.getCodigoPrincipal(), itera);
						} else {
							Detalle aux = detalleMap.get(itera.getCodigoPrincipal());
							aux.setCantidad(aux.getCantidad().add(itera.getCantidad()));
							if (itera.getDescuento() != null
									&& itera.getDescuento().compareTo(new BigDecimal("0.00")) > 0) {
								aux.setPrecioTotalSinImpuesto(
										aux.getPrecioTotalSinImpuesto().add(itera.getPrecioTotalSinImpuesto()));
								aux.setPrecioUnitario(aux.getPrecioTotalSinImpuesto()
										.divide(aux.getCantidad(), 4, RoundingMode.CEILING)
										.setScale(2, BigDecimal.ROUND_HALF_UP));
							} else {
								aux.setPrecioUnitario(itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP));
							}
							detalleMap.put(aux.getCodigoPrincipal(), aux);
						}
					}

					List<Detalle> listdetalle2 = new ArrayList<Detalle>(detalleMap.values());
					// for (Detalle itera :
					// facturaXML.getDetalles().getDetalle()) {
					for (Detalle itera : listdetalle2) {
						num++;
						Map map = new HashMap();
						map.put("TDETL", "TDETL");
						String linea = completar(String.valueOf(num).length(), num);

						map.put("linea", linea);
						boolean booUpc = false;
						String tipoPrincipal = null;
						String tipoSecundario = null;
						log.info("codigo principal" + itera.getCodigoPrincipal());
						tipoPrincipal = validaVpn(itera.getCodigoPrincipal());
						if (tipoPrincipal != null) {
							if (tipoPrincipal.equals("UPC")) {
								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC,
										itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
								map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
								map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
							}
							if (tipoPrincipal.equals("VPN")) {
								map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN,
										itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
								map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
							}
							if (tipoPrincipal.equals("ITEM")) {
								map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item,
										itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
								map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
							}
						} else {
							if (itera.getCodigoAuxiliar() != null) {
								tipoSecundario = validaVpn(itera.getCodigoAuxiliar());
								if (tipoSecundario != null) {
									if (tipoSecundario.equals("UPC")) {
										map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC,
												itera.getCodigoAuxiliar().length(), itera.getCodigoAuxiliar()));
										map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
										map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
									}
									if (tipoSecundario.equals("VPN")) {
										map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN,
												itera.getCodigoAuxiliar().length(), itera.getCodigoAuxiliar()));
										map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
										map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
									}
									if (tipoSecundario.equals("ITEM")) {
										map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item,
												itera.getCodigoAuxiliar().length(), itera.getCodigoAuxiliar()));
										map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
										map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
									}
								} else {
									log.info("Nose encontraron los codigos " + itera.getCodigoPrincipal() + "-"+ itera.getCodigoAuxiliar());
									respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
									respuesta.setMensajeCliente("Nose encontraron los codigos en lista de Items devuelta por Reim "+ itera.getCodigoPrincipal());
									respuesta.setMensajeSistema("Nose encontraron los codigos en lista de Items devuelta por Reim "+ itera.getCodigoPrincipal());
									respuesta.setEstado(Estado.ERROR.getDescripcion());
									facturaObj.setEstado(Estado.ERROR.getDescripcion());
									facturaObj.setMensajeErrorReim("No se encontraron los codigos en lista de Items devuelta por Reim "+ itera.getCodigoPrincipal());
									facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,new StringBuilder().append("Nose encontraron los codigos "
															+ itera.getCodigoPrincipal())
													.append(ordenCompra).toString());
									return respuesta;
								}
							} else {
								log.info("Nose encontraron los codigos " + itera.getCodigoPrincipal() + "-"
										+ itera.getCodigoAuxiliar());
								respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
								respuesta.setMensajeCliente(
										"Nose encontraron los codigos en lista de Items devuelta por Reim "
												+ itera.getCodigoPrincipal());
								respuesta.setMensajeSistema(
										"Nose encontraron los codigos en lista de Items devuelta por Reim "
												+ itera.getCodigoPrincipal());
								respuesta.setEstado(Estado.ERROR.getDescripcion());
								facturaObj.setEstado(Estado.ERROR.getDescripcion());
								facturaObj.setMensajeErrorReim(
										"Nose encontraron los codigos en lista de Items devuelta por Reim "
												+ itera.getCodigoPrincipal());
								facturaObj = this.servicioRecepcionFactura
										.recibirFactura(facturaObj,
												new StringBuilder()
														.append("Nose encontraron los codigos "
																+ itera.getCodigoPrincipal())
														.append(ordenCompra).toString());
								return respuesta;
							}
						}
						map.put("UPC_Supplement",
								completarEspaciosEmblancosLetras(ConstantesRim.UPC_Supplement, 0, ""));

						map.put("Original_Document_Quantity", completarEspaciosEmblancosNumeros(
								ConstantesRim.Original_Document_Quantity, 4, String.valueOf(itera.getCantidad())));
						BigDecimal cantidad = itera.getCantidad();
						BigDecimal pu;

						// cambio para item repetidos
						// map.put("Original_Unit_Cost",
						// completarEspaciosEmblancosNumeros(
						// ConstantesRim.Original_Unit_Cost, 4,
						// String.valueOf(itera.getPrecioUnitario().setScale(2,
						// BigDecimal.ROUND_HALF_UP))));
						pu = itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP);
						//

						totalUnidades = totalUnidades.add(itera.getCantidad());
						if (itera.getImpuestos().getImpuesto() != null) {
							if (!itera.getImpuestos().getImpuesto().isEmpty()) {
								for (Impuesto item : itera.getImpuestos().getImpuesto()) {
									if (item.getCodigoPorcentaje().equals("0")) {
										map.put("Original_VAT_Code",
												completarEspaciosEmblancosLetras(6, "L".length(), "L"));
										map.put("Original_VAT_rate", completarEspaciosEmblancosNumeros(20, 10,
												String.valueOf(item.getTarifa())));
										strPorccentaje = "L";
										totalsinimpiesto0 = totalsinimpiesto0.add(pu.multiply(cantidad));

									} else if (item.getCodigoPorcentaje().equals("2")) {
										map.put("Original_VAT_Code",
												completarEspaciosEmblancosLetras(6, "E".length(), "E"));
										map.put("Original_VAT_rate", completarEspaciosEmblancosNumeros(20, 10,
												String.valueOf(item.getTarifa())));
										totalsinimpiesto14 = totalsinimpiesto14.add(pu.multiply(cantidad));
										// booCambioA14 = true;
									} else if (item.getCodigoPorcentaje().equals("3")) {
										map.put("Original_VAT_Code",
												completarEspaciosEmblancosLetras(6, "E".length(), "E"));
										map.put("Original_VAT_rate", completarEspaciosEmblancosNumeros(20, 10,
												String.valueOf(item.getTarifa())));
										totalsinimpiesto14 = totalsinimpiesto14.add(pu.multiply(cantidad));
									}

									if (item.getCodigo().equals("5")) {
										precioUnitarioaux = precioUnitarioaux.add(item.getValor());
									}

									if (item.getCodigo().equals("3")) {
										precioUnitarioaux = precioUnitarioaux.add(item.getValor());
									}
								}
							}
						}

						map.put("Original_Unit_Cost",
								completarEspaciosEmblancosNumeros(20, 4,
										String.valueOf(itera.getPrecioTotalSinImpuesto().add(precioUnitarioaux)
												.divide(itera.getCantidad(), 4, RoundingMode.CEILING).setScale(2, 4))));

						precioUnitarioaux = new BigDecimal(0);

						map.put("Total_Allowance", completarEspaciosEmblancosNumeros(20, 4, "0"));

						list.add(map);
					}
				}
				ArrayList listTotalImpuesto = new ArrayList();// lista para
																// codigos iva
				ArrayList listTotalImpuestoIceIbrp = new ArrayList();// lista
																		// para
																		// codigos
																		// iva
				BigDecimal totalImpuesto = new BigDecimal(0);
				BigDecimal totalimpuestoIceIbrp = new BigDecimal(0);
				BigDecimal totalimpuestoIce = new BigDecimal(0);
				BigDecimal descuentosolidario = new BigDecimal(0);
				boolean booiceIbrp = false;
				boolean boodescuentosoli = false;
				if (booCambioA14) {
					BigDecimal auxValor = new BigDecimal("0");
					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							// para facutras que bienen con 12% cambio a 14%
							if (impu.getCodigoPorcentaje().equals("2")) {
								auxValor = impu.getBaseImponible().multiply(new BigDecimal("0.02"));
							}
						}
					}
					Map mapiceibrp = new HashMap();
					mapiceibrp.put("TNMRC", "TNMRC");
					num++;
					String linea = completar(String.valueOf(num).length(), num);
					mapiceibrp.put("linea", linea);
					mapiceibrp.put("MER_CODE",
							completarEspaciosEmblancosLetras(ConstantesRim.MER_CODE, "DSCSOL".length(), "DSCSOL"));
					mapiceibrp.put("signotrn", "-");
					descuentosolidario = auxValor.setScale(2, BigDecimal.ROUND_HALF_UP);
					mapiceibrp.put("MER_AMT", completarEspaciosEmblancosNumeros(ConstantesRim.MER_AMT,
							ConstantesRim.Decimales4, String.valueOf(auxValor.setScale(2, BigDecimal.ROUND_HALF_UP))));
					mapiceibrp.put("MER_VAT_CODE",
							completarEspaciosEmblancosLetras(ConstantesRim.MER_VAT_CODE, "L".length(), "L"));
					mapiceibrp.put("MER_VAT_RATE", completarEspaciosEmblancosNumeros(ConstantesRim.MER_VAT_RATE,
							ConstantesRim.Decimales10, String.valueOf("0")));
					mapiceibrp.put("Ser_Per_Ind",
							completarEspaciosEmblancosLetras(ConstantesRim.Ser_Per_Ind, "N".length(), "N"));
					mapiceibrp.put("MER_STORE", completarEspaciosEmblancosLetras(10, "".length(), ""));
					boodescuentosoli = true;
					listTotalImpuestoIceIbrp.add(mapiceibrp);
				}
				if (facturaXML.getInfoFactura().getCompensaciones() != null
						&& !facturaXML.getInfoFactura().getCompensaciones().getCompensacion().isEmpty()) {
					log.info("compensaciones traduciendo 1111");
					for (Compensacion compen : facturaXML.getInfoFactura().getCompensaciones().getCompensacion()) {
						Map mapiceibrp = new HashMap();
						mapiceibrp.put("TNMRC", "TNMRC");
						num++;
						String linea = completar(String.valueOf(num).length(), num);
						mapiceibrp.put("linea", linea);

						mapiceibrp.put("MER_CODE",
								completarEspaciosEmblancosLetras(ConstantesRim.MER_CODE, "DSCSOL".length(), "DSCSOL"));
						mapiceibrp.put("signotrn", "-");

						descuentosolidario = compen.getValor().setScale(2, BigDecimal.ROUND_HALF_UP);

						mapiceibrp.put("MER_AMT",
								completarEspaciosEmblancosNumeros(ConstantesRim.MER_AMT, ConstantesRim.Decimales4,
										String.valueOf(compen.getValor().setScale(2, BigDecimal.ROUND_HALF_UP))));
						mapiceibrp.put("MER_VAT_CODE",
								completarEspaciosEmblancosLetras(ConstantesRim.MER_VAT_CODE, "L".length(), "L"));

						mapiceibrp.put("MER_VAT_RATE", completarEspaciosEmblancosNumeros(ConstantesRim.MER_VAT_RATE,
								ConstantesRim.Decimales10, String.valueOf("0")));

						mapiceibrp.put("Ser_Per_Ind",
								completarEspaciosEmblancosLetras(ConstantesRim.Ser_Per_Ind, "N".length(), "N"));
						mapiceibrp.put("MER_STORE", completarEspaciosEmblancosLetras(10, "".length(), ""));
						boodescuentosoli = true;

						listTotalImpuestoIceIbrp.add(mapiceibrp);
					}
				}
				if (facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto() != null
						&& !facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().isEmpty()) {
					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							if (impu.getCodigo().equals("3")) {
								totalimpuestoIce = totalimpuestoIce.add(impu.getValor());

								totalsinimpiesto14 = totalsinimpiesto14.add(impu.getValor());
								booiceIbrp = true;
							} else if (impu.getCodigo().equals("5")) {
								totalimpuestoIceIbrp = totalimpuestoIceIbrp.add(impu.getValor());

								booiceIbrp = true;
							} else if (!impu.getCodigo().equals("2")) {
								log.info(new StringBuilder()
										.append("La factura tiene codigo de impuesto  no contemplado en reim")
										.append(facturaXML.getInfoTributaria().getClaveAcceso()).toString());
								respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
								respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");

								respuesta.setMensajeSistema(
										"La factura tiene codigo de impuesto  no contemplado en reim");

								respuesta.setEstado(
										com.gizlocorp.gnvoice.enumeracion.Estado.COMPLETADO.getDescripcion());
								facturaObj.setEstado(com.gizlocorp.gnvoice.enumeracion.Estado.ERROR.getDescripcion());
								facturaObj.setMensajeErrorReim(
										"La factura tiene codigo de impuesto  no contemplado en reim");

								facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
										new StringBuilder()
												.append("La factura tiene codigo de impuesto  no contemplado en reim")
												.append(ordenCompra).toString());
								return respuesta;
							}
						}
					}

					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							Map map = new HashMap();
							map.put("TVATS", "TVATS");
							if (impu.getCodigoPorcentaje().equals("0")) {
								num++;
								String linea = completar(String.valueOf(num).length(), num);
								map.put("linea", linea);
								map.put("VAT_code",
										completarEspaciosEmblancosLetras(ConstantesRim.VAT_code, "L".length(), "L"));

								map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
										ConstantesRim.Decimales10, String.valueOf("0")));
								map.put("VAT_SIGNO", "+");
								map.put("Cost_VAT_code",
										completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
												ConstantesRim.Decimales4,
												String.valueOf(totalsinimpiesto0
														.add(totalimpuestoIceIbrp).subtract(descuentosolidario)
														.setScale(2, BigDecimal.ROUND_HALF_UP))));

								totalImpuesto = totalImpuesto.add(impu.getValor());
								listTotalImpuesto.add(map);
							} else {
								if (impu.getCodigoPorcentaje().equals("2") || impu.getCodigoPorcentaje().equals("3")) {
									num++;
									String linea = completar(String.valueOf(num).length(), num);
									map.put("linea", linea);
									map.put("VAT_code", completarEspaciosEmblancosLetras(ConstantesRim.VAT_code,
											"E".length(), "E"));

									map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
											ConstantesRim.Decimales10, String.valueOf("12")));
									// para facutras que bienen con 12% cambio a
									// 14%
									if (impu.getCodigoPorcentaje().equals("2")) {
										impu.setValor(impu.getBaseImponible().multiply(new BigDecimal("0.12")));
										// boodescuentosoli = true;
									}
									if (impu.getCodigoPorcentaje().equals("3")) {
										map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
												ConstantesRim.Decimales10, String.valueOf("14")));
									}
									map.put("VAT_SIGNO", "+");
									map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code, ConstantesRim.Decimales4,
											String.valueOf(totalsinimpiesto14.setScale(2, BigDecimal.ROUND_HALF_UP))));

									totalImpuesto = totalImpuesto.add(impu.getValor());
									listTotalImpuesto.add(map);
								}
							}
						}
					}
					if ((booiceIbrp || boodescuentosoli) && !strPorccentaje.equals("L")) {
						Map map = new HashMap();
						map.put("TVATS", "TVATS");
						num++;
						String linea = completar(String.valueOf(num).length(), num);
						map.put("linea", linea);
						map.put("VAT_code",
								completarEspaciosEmblancosLetras(ConstantesRim.VAT_code, "L".length(), "L"));
						map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
								ConstantesRim.Decimales10, String.valueOf("0")));
						if (boodescuentosoli && booiceIbrp) {
							map.put("VAT_SIGNO", "+");
							map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
									ConstantesRim.Decimales4, String.valueOf(totalimpuestoIceIbrp
											.subtract(descuentosolidario).setScale(2, BigDecimal.ROUND_HALF_UP))));
						} else {
							if (!boodescuentosoli && booiceIbrp) {
								map.put("VAT_SIGNO", "+");
								map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
										ConstantesRim.Decimales4,
										String.valueOf(totalimpuestoIceIbrp.setScale(2, BigDecimal.ROUND_HALF_UP))));
							} else {
								if (boodescuentosoli && !booiceIbrp) {
									map.put("VAT_SIGNO", "-");
									map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code, ConstantesRim.Decimales4,
											String.valueOf(descuentosolidario.setScale(2, BigDecimal.ROUND_HALF_UP))));
								}
							}
						}

						listTotalImpuesto.add(map);

					}
				}
				int fdetalle = num + 1;// 5
				int fin = num + 2;// 6
				int numLinea = num - 2;// 2
				int totalLinea = num;// 4

				Parametro dirParametro2 = cacheBean.consultarParametro(Constantes.DIR_RIM_ESQUEMA, emisor.getId());
				// String dirServidor2 = dirParametro2 != null ?
				// dirParametro2.getValor() : "";

				String dirServidor2 = "/data/gnvoice/recursos/rim_vm/";

				VelocityEngine ve = new VelocityEngine();
				ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, dirServidor2);
				ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
				// ve.setProperty(RuntimeConstants.RESOURCE_LOADER,
				// "classpath");
				// ve.setProperty("classpath.resource.loader.class",
				// ClasspathResourceLoader.class.getName());
				ve.init();

				Template t = ve.getTemplate("traductor.vm");
				VelocityContext context = new VelocityContext();

				context.put("fecha", strDate);
				context.put("petList", list);

				context.put("Document_Type",
						completarEspaciosEmblancosLetras(ConstantesRim.Document_Type, "MRCHI".length(), "MRCHI"));

				String vendorDocument = new StringBuilder().append(facturaXML.getInfoTributaria().getEstab()).append("")
						.append(facturaXML.getInfoTributaria().getPtoEmi()).append("")
						.append(facturaXML.getInfoTributaria().getSecuencial()).toString();

				context.put("Vendor_Document_Number", completarEspaciosEmblancosLetras(
						ConstantesRim.Vendor_Document_Number, vendorDocument.length(), vendorDocument));
				context.put("Group_ID", completarEspaciosEmblancosLetras(ConstantesRim.Group_ID, "".length(), ""));
				log.info("***Traduciendo Reim factura 444***");
				if (ceDisresponse == null || ceDisresponse.getVendor_type() == null
						|| ceDisresponse.getVendor_id() == null) {
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service ");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim(
							"Ha ocurrido un error no devolvio datos el web service para en munero de compra."
									+ ordenCompra);
					facturaObj = this.servicioRecepcionFactura.recibirFactura(facturaObj,
							new StringBuilder().append("El web service no encontro informacion. para la orden")
									.append(ordenCompra).toString());
					return respuesta;
				}

				log.info("***Traduciendo Reim factura 555***");
				context.put("Vendor_Type", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Type,
						ceDisresponse.getVendor_type().length(), ceDisresponse.getVendor_type()));

				context.put("Vendor_ID", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_ID,
						ceDisresponse.getVendor_id().length(), ceDisresponse.getVendor_id()));

				Date fechaEmision = sdfDate2.parse(facturaXML.getInfoFactura().getFechaEmision());
				context.put("Vendor_Document_Date", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Document_Date,
						sdfDate.format(fechaEmision).length(), sdfDate.format(fechaEmision)));

				context.put("Order_Number",
						completarEspaciosEmblancosNumeros(ConstantesRim.Order_Number, 0, String.valueOf(ordenCompra)));

				if ((ceDisresponse.getLocation() != null) && (!ceDisresponse.getLocation().isEmpty())) {
					context.put("Location",
							completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0, ceDisresponse.getLocation()));
				} else {
					context.put("Location", completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0, ""));
				}

				if ((ceDisresponse.getLocation() != null) && (!ceDisresponse.getLocation().isEmpty())) {
					context.put("Location_Type", completarEspaciosEmblancosLetras(ConstantesRim.Location_Type,
							ceDisresponse.getLocation().length(), ceDisresponse.getLocation_type()));
				} else {
					context.put("Location_Type",
							completarEspaciosEmblancosLetras(ConstantesRim.Location_Type, "".length(), ""));
				}

				context.put("Terms", completarEspaciosEmblancosLetras(ConstantesRim.Terms, "".length(), ""));
				context.put("Due_Date", completarEspaciosEmblancosLetras(ConstantesRim.Due_Date, "".length(), ""));
				context.put("Payment_method",
						completarEspaciosEmblancosLetras(ConstantesRim.Payment_method, "".length(), ""));
				context.put("Currency_code",
						completarEspaciosEmblancosLetras(ConstantesRim.Currency_code, "USD".length(), "USD"));

				context.put("Exchange_rate",
						completarEspaciosEmblancosNumeros(ConstantesRim.Exchange_rate, 4, String.valueOf("1")));

				context.put("Total_Cost",
						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Cost, 4,
								String.valueOf(facturaXML.getInfoFactura().getTotalSinImpuestos()
										.add(totalimpuestoIceIbrp).add(totalimpuestoIce).subtract(descuentosolidario)
										.setScale(2, BigDecimal.ROUND_HALF_UP))));

				context.put("Total_VAT_Amount", completarEspaciosEmblancosNumeros(ConstantesRim.Total_VAT_Amount, 4,
						String.valueOf(totalImpuesto.setScale(2, BigDecimal.ROUND_HALF_UP))));

				context.put("Total_Quantity", completarEspaciosEmblancosNumeros(ConstantesRim.Total_Quantity, 4,
						String.valueOf(totalUnidades)));

				context.put("Total_Discount",
						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Discount, 4, String.valueOf(facturaXML
								.getInfoFactura().getTotalDescuento().setScale(2, BigDecimal.ROUND_HALF_UP))));

				context.put("Freight_Type",
						completarEspaciosEmblancosLetras(ConstantesRim.Freight_Type, "".length(), ""));
				context.put("Paid_Ind", "N");
				context.put("Multi_Location", "N");
				context.put("Merchan_dise_Type", "N");
				context.put("Deal_Id", completarEspaciosEmblancosLetras(ConstantesRim.Deal_Id, "".length(), ""));
				context.put("Deal_Approval_Indicator",
						completarEspaciosEmblancosLetras(ConstantesRim.Deal_Approval_Indicator, "".length(), ""));
				context.put("RTV_indicator", "N");

				if (offline) {
					context.put("Custom_Document_Reference_1",
							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_1,
									autorizacionOfflineXML.getNumeroAutorizacion().length(),
									autorizacionOfflineXML.getNumeroAutorizacion()));
				} else {
					context.put("Custom_Document_Reference_1",
							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_1,
									autorizacionXML.getNumeroAutorizacion().length(),
									autorizacionXML.getNumeroAutorizacion()));
				}

				String fechaAutorizacion = "";
				if (autorizacionXML.getFechaAutorizacion() != null) {
					fechaAutorizacion = FechaUtil.formatearFecha(
							autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime(), "dd/MM/yyyy HH:mm");
				} else {
					fechaAutorizacion = facturaXML.getInfoFactura().getFechaEmision();
				}

				context.put("Custom_Document_Reference_2", completarEspaciosEmblancosLetras(
						ConstantesRim.Custom_Document_Reference_2, fechaAutorizacion.length(), fechaAutorizacion));

				String ice = "";

				if ((totalimpuestoIce != null) && (totalimpuestoIce.compareTo(new BigDecimal(0)) != 0))
					ice = String.valueOf(totalimpuestoIce);
				else {
					ice = "";
				}
				System.out.println(new StringBuilder().append("Total de impuesto Ice 111: ").append(ice).toString());

				context.put("Custom_Document_Reference_3", completarEspaciosEmblancosLetras(90, ice.length(), ice));

				String ibrp = "";

				if ((totalimpuestoIceIbrp != null) && (totalimpuestoIceIbrp.compareTo(new BigDecimal(0)) != 0))
					ibrp = String.valueOf(totalimpuestoIceIbrp);
				else {
					ibrp = "";
				}
				System.out.println(new StringBuilder().append("Total de impuesto IBRP 222: ").append(ibrp).toString());

				context.put("Custom_Document_Reference_4", completarEspaciosEmblancosLetras(90, ibrp.length(), ibrp));

				context.put("Cross_reference",
						completarEspaciosEmblancosLetras(ConstantesRim.Cross_reference, "".length(), ""));

				context.put("petListTotalImpuesto", listTotalImpuesto);
				context.put("petListTotalImpuestoIceIbrp", listTotalImpuestoIceIbrp);
				context.put("secuencial", facturaXML.getInfoTributaria().getSecuencial());

				context.put("finT", completar(String.valueOf(fdetalle).length(), fdetalle));
				context.put("finF", completar(String.valueOf(fin).length(), fin));
				context.put("numerolineaDestalle", completarLineaDetalle(String.valueOf(numLinea).length(), numLinea));
				context.put("totalLinea", completar(String.valueOf(totalLinea).length(), totalLinea));

				StringWriter writer = new StringWriter();
				t.merge(context, writer);
				Calendar now2 = Calendar.getInstance();
				Parametro repositorioRim = this.cacheBean.consultarParametro("RIM_REPOSITO_ARCHIVO", emisor.getId());

				String stvrepositorioRim = repositorioRim != null ? repositorioRim.getValor() : "";

				Random rng = new Random();
				int dig5 = rng.nextInt(90000) + 10000; // siempre 5 digitos

				// if(facturaXML.getInfoTributaria().getClaveAcceso() !=null &&
				// !facturaXML.getInfoTributaria().getClaveAcceso().isEmpty()){
				// nombreArchivo =
				// facturaXML.getInfoTributaria().getClaveAcceso().toString();
				// }else{
				// nombreArchivo = new StringBuilder().append("edidlinv_")
				// .append(FechaUtil.formatearFecha(now2.getTime(),
				// "yyyyMMddHHmmssSSS").toString()).append(dig5).toString();
				// }

				log.info("***creando archivo traducido factura 999***" + nombreArchivo);

				String archivoCreado = DocumentUtil.createDocumentText(writer.toString().getBytes(), nombreArchivo,
						FechaUtil.formatearFecha(now2.getTime(), "yyyyMMdd").toString(), stvrepositorioRim);

				String archivoCreadoProcesado = archivoCreado + ".loaded";
				File fileOut = new File(archivoCreado);
				File fileLoaded = new File(archivoCreadoProcesado);
				boolean existFileOut = fileOut != null && fileOut.exists() && !fileOut.isDirectory();
				boolean existFileLoaded = fileLoaded != null && fileLoaded.exists() && !fileLoaded.isDirectory();

				if (existFileOut || existFileLoaded) {
					facturaObj.setFechaEntReim(new Date());
					facturaObj.setArchivoGenReim(archivoCreado);
				} else {
					respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					MensajeRespuesta msgObj = new MensajeRespuesta();
					msgObj.setIdentificador("-100");
					msgObj.setMensaje("Ha ocurrido un problema: " + "No se creo el archivo .out reim");
					respuesta.getMensajes().add(msgObj);
					respuesta.setMensajeCliente("No se creo el archivo .out reim");
					respuesta.setMensajeSistema("No se creo el archivo .out reim");
					System.out.println("No se creo el archivo .out reim");
					return respuesta;
				}

			}

			// fin agregado rim

			facturaObj.setNumeroAutorizacion(autorizacionXML.getNumeroAutorizacion());
			if (autorizacionXML.getFechaAutorizacion() != null) {
				facturaObj.setFechaAutorizacion(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime());
			} else {
				SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
				facturaObj.setFechaAutorizacion(formato.parse(facturaXML.getInfoFactura().getFechaEmision()));
			}

			facturaObj.setEstado("AUTORIZADO");
			facturaObj.setArchivo(facturaRecibirRequest.getComprobanteProveedor());
			facturaObj.setTareaActual(Tarea.AUT);

			// si no existe pdf creo pdf
			Parametro dirParametro = cacheBean.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
			String dirServidor = dirParametro != null ? dirParametro.getValor() : "";

			facturaObj = servicioRecepcionFactura.recibirFactura(facturaObj,
					"El comprobante se a cargado de forma exitosa");
			respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
			respuesta.setMensajeCliente("El comprobante se a cargado de forma exitosa");
			respuesta.setMensajeSistema("El comprobante se a cargado de forma exitosa");

		} catch (Exception ex) {
			log.info(ex.toString() + "**" + facturaRecibirRequest.getComprobanteProveedor());
			respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			MensajeRespuesta msgObj = new MensajeRespuesta();
			msgObj.setIdentificador("-100");
			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
			respuesta.getMensajes().add(msgObj);
			respuesta.setMensajeCliente("Ha ocurrido un problema");
			respuesta.setMensajeSistema(ex.getMessage());

			log.error(ex.getMessage(), ex);
		}

		return respuesta;
	}

	private String getFacturaXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.factura.Factura) converter.convertirAObjeto(comprobanteXML,
				com.gizlocorp.gnvoice.xml.factura.Factura.class);
	}

	private Autorizacion getAutorizacionXML(String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (Autorizacion) converter.convertirAObjeto(comprobanteXML, Autorizacion.class);
	}

	public String completar(int num, int linea) {
		StringBuilder secuencial = new StringBuilder();

		int secuencialTam = 10 - num;
		for (int i = 0; i < secuencialTam; i++) {
			secuencial.append("0");
		}
		String retorno = secuencial.append(String.valueOf(linea)).toString();
		return retorno;
	}

	public String completarLineaDetalle(int num, int linea) {
		StringBuilder secuencial = new StringBuilder();

		int secuencialTam = 6 - num;
		for (int i = 0; i < secuencialTam; i++) {
			secuencial.append("0");
		}
		String retorno = secuencial.append(String.valueOf(linea)).toString();
		return retorno;
	}

	public String completarEspaciosEmblancosNumeros(int numEspacios, int numDecimales, String valor) {
		StringBuilder secuencial = new StringBuilder();
		String valorEntero = "";
		String valorDecimal = "";
		StringTokenizer st2 = new StringTokenizer(valor, ".");
		int numEspaciosEnteros = 0;
		int itera = 0;
		while (st2.hasMoreElements()) {
			if (itera == 0) {
				valorEntero = st2.nextElement().toString();
				itera++;
				continue;
			}
			valorDecimal = st2.nextElement().toString();
		}

		numEspaciosEnteros = numEspacios - numDecimales;

		if (valorEntero.isEmpty()) {
			int secuencialTam = numEspacios - numDecimales;
			for (int i = 0; i < secuencialTam; i++)
				secuencial.append("0");
		} else {
			if (numEspacios >= valorEntero.length()) {
				int secuencialTam = numEspaciosEnteros - valorEntero.length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
			}
			secuencial.append(valorEntero).toString();
		}

		if (valorDecimal.isEmpty()) {
			for (int i = 0; i < numDecimales; i++)
				secuencial.append("0");
		} else {
			int secuencialTam = 0;
			if (valorDecimal.length() > numDecimales)
				secuencial.append(valorDecimal.substring(0, 4)).toString();
			else {
				secuencial.append(valorDecimal).toString();
			}
			if (numDecimales >= valorDecimal.length()) {
				secuencialTam = numDecimales - valorDecimal.length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
			}
		}

		return secuencial.toString();
	}

	public String completarEspaciosEmblancosLetras(int numEspacios, int numOcupados, String valor) {
		StringBuilder secuencial = new StringBuilder();
		secuencial.append(valor).toString();
		if (numEspacios >= numOcupados) {
			int secuencialTam = numEspacios - numOcupados;
			for (int i = 0; i < secuencialTam; i++) {
				secuencial.append(" ");
			}
		}

		return secuencial.toString();
	}

	public String validaExcepcion(String codigo, String ruc) {
		String tipo = null;

		if (codigo == null || codigo.isEmpty()) {
			return tipo;
		}
		// String codigoManipulado = codigo.replaceAll("[^0-9]", "");
		try {
			List<ParametrizacionCodigoBarras> listCodigoBarras = servicioCodigoBarrasLocal
					.obtenerCodigoBarrasPorParametros(null, null, com.gizlocorp.adm.enumeracion.Estado.ACT, ruc);

			if (listCodigoBarras != null) {
				for (ParametrizacionCodigoBarras itera : listCodigoBarras) {
					if (itera.getCodigoArticulo().equals(codigo)) {
						return tipo = "EXCE";
					}
				}
			}

		} catch (GizloException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String validaVpn(String codigo) {
		String tipo = null;

		if (codigo == null || codigo.isEmpty()) {
			return tipo;
		}

		// String codigoManipulado = codigo.replaceAll("[^0-9]", "");

		for (ValidItem item : ceDisresponse.getListItem()) {
			if (item.getItem().equals(codigo)) {
				if (item.getItemType().equals("ITEM")) {
					return tipo = "ITEM";
				}
				if (item.getItemType().equals("NPP") || item.getItemType().equals("VPN")) {
					return tipo = "VPN";
				}
				if (item.getItemType().equals("UPC-A") || item.getItemType().equals("EAN8")
						|| item.getItemType().equals("UCC14") || item.getItemType().equals("ISBN13")
						|| item.getItemType().equals("EAN13") || item.getItemType().equals("UPC")
						|| item.getItemType().equals("MANL")) {
					return tipo = "UPC";
				}
			}

		}
		return tipo;
	}

	public CeDisResponse getCeDisresponse() {
		return ceDisresponse;
	}

	public void setCeDisresponse(CeDisResponse ceDisresponse) {
		this.ceDisresponse = ceDisresponse;
	}

	private CeDisResponse consultaWebservice(String rucp, String ordenp) {
		CeDisResponse ceDisResponsePoo = null;
		try {
			log.info("ingresando a web service reim");
			oracle.retail.integration.custom.bo.cedisservice.v1.ObjectFactory fact = new oracle.retail.integration.custom.bo.cedisservice.v1.ObjectFactory();
			JAXBElement<String> ruc = fact.createCeDisOrderRucRequestRucNumber(rucp);
			JAXBElement<String> order = fact.createCeDisOrderRucRequestOrderNumber(ordenp);

			List<JAXBElement<String>> list = new ArrayList<JAXBElement<String>>();
			list.add(order);
			list.add(ruc);

			CeDisOrderRucRequest input = new CeDisOrderRucRequest();
			input.setOrderNumberAndRucNumber(list);
			log.info("ingresando a web service reim 222");
			CeDisWS servicio = new CeDisWS();
			log.info("ingresando a web service reim 333");
			CeDisOrderService respuesta = servicio.getCeDisOrderServicePt();
			log.info("ingresando a web service reim 444");
			oracle.retail.integration.custom.bo.cedisservice.v1.CeDisResponse ceDisOrderRuc = respuesta
					.getCeDisOrderRuc(input);
			log.info("ingresando a web service reim 555");
			if (ceDisOrderRuc != null) {
				ceDisResponsePoo = new CeDisResponse();
				ValidItem validItem = new ValidItem();
				ceDisResponsePoo.setListItem(new ArrayList<ValidItem>());
				if (ceDisOrderRuc.getCedisVirtual().getValue() != null
						&& !ceDisOrderRuc.getCedisVirtual().getValue().isEmpty()) {
					ceDisResponsePoo.setCedis_virtual(ceDisOrderRuc.getCedisVirtual().getValue());
				} else {
					ceDisResponsePoo.setCedis_virtual(null);
				}
				if (ceDisOrderRuc.getLocation().getValue() != null) {
					ceDisResponsePoo.setLocation(String.valueOf(ceDisOrderRuc.getLocation().getValue()));
				} else {
					ceDisResponsePoo.setLocation(null);
				}

				ceDisResponsePoo.setLocation_type(ceDisOrderRuc.getLocationType().getValue());
				if (ceDisOrderRuc.getOrderNumber().getValue() != null) {
					ceDisResponsePoo.setOrder_number(String.valueOf(ceDisOrderRuc.getOrderNumber().getValue()));
				} else {
					ceDisResponsePoo.setOrder_number(null);
				}
				ceDisResponsePoo.setVendor_id(ceDisOrderRuc.getVendorId().getValue());
				ceDisResponsePoo.setVendor_type(ceDisOrderRuc.getVendorType().getValue());

				if (!ceDisOrderRuc.getOrderStatus().equals("'VALID_ORDER'")) {
					System.out.println(ceDisOrderRuc.getOrderStatus());
					ceDisResponsePoo.setOrder_status(ceDisOrderRuc.getOrderStatus());
					return ceDisResponsePoo;
				} else {
					ceDisResponsePoo.setOrder_status("");
				}
				for (oracle.retail.integration.custom.bo.cedisservice.v1.ValidItem itera : ceDisOrderRuc
						.getValidItemList().getValidItem()) {
					validItem = new ValidItem();
					validItem.setItem(itera.getItem());
					validItem.setItemType(itera.getItemType());
					ceDisResponsePoo.getListItem().add(validItem);
				}
			}
			log.info("ingresando a web service reim 333");
		} catch (FaultExceptionMessage e) {
			e.printStackTrace();
		}
		return ceDisResponsePoo;
	}

	private static byte[] readFile(String filePath) throws FileException {
		try {
			File file = new File(filePath);
			byte[] content = org.apache.commons.io.FileUtils.readFileToByteArray(file);
			return content;

		} catch (IOException e) {
			throw new FileException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		FacturaBean bean = new FacturaBean();
		// ceDisresponse =bean.consultaWebservice("0990006792001", "160161");
		// System.out.println(ceDisresponse.toString());
		// String ab= "ASD 234567hhj 8912345 jjj";
		// String ac = ab.replaceAll("[^0-9]", "");
		// System.out.println(ac);
		// System.out.println("aaa");
		// List<String> list = new ArrayList<String>();
		// list.add("a");
		// list.add("b");
		// list.add("c");
		// list.add("a");
		// list.add("b");
		// list.add("c");
		// list.add("c");
		// Set<String> quipu = new HashSet<String>(list);
		// for (String key : quipu) {
		// System.out.println(key + " : " + Collections.frequency(list, key));
		// }
		// FacturaRecibirRequest facturaRecibirRequest = new
		// FacturaRecibirRequest();
		// facturaRecibirRequest.setComprobanteProveedor("C:\\Users\\Usuario\\Desktop\\jrxmlfybeca\\2907201601099078906100120120030025105720251057210.xml");
		// bean.recibir(facturaRecibirRequest);
		// System.out.println("aaa 000");
		System.out.println("prueba");
		bean.enviarComprobante("https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl",
				"C:\\Users\\Usuario\\Desktop\\jrxmlfybeca\\0809201601179071031900121020010001771515658032314signedGNVOICE.xml");
		String validacionXsd = ComprobanteUtil.validaArchivoXSD(
				"C:\\Users\\Usuario\\Desktop\\jrxmlfybeca\\0809201601179071031900121020010001771515658032314.xml",
				"C:\\Users\\Usuario\\Desktop\\jrxmlfybeca\\factura.xsd");
		System.out.println("aaa 11");
		if (validacionXsd != null) {
			System.out.println(validacionXsd);
		}
		System.out.println("aaa 222");

	}

}
