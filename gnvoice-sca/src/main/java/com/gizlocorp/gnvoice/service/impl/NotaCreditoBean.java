package com.gizlocorp.gnvoice.service.impl;

import java.io.File;
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
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
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
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaRequest;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaResponse;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWs;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWsService;
import com.gizlocorp.gnvoice.reporte.NotaCreditoReporte;
import com.gizlocorp.gnvoice.service.NotaCredito;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ConstantesRim;
import com.gizlocorp.gnvoice.utilitario.FechaUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.message.CeDisResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirResponse;
import com.gizlocorp.gnvoice.xml.notacredito.Impuesto;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto;

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
@Service(NotaCredito.class)
@Stateless
public class NotaCreditoBean implements NotaCredito {
	public static final Logger log = LoggerFactory
			.getLogger(NotaCreditoBean.class);

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito")
	ServicioNotaCredito servicioNotaCredito;

	@EJB
	CacheBean cacheBean;
	
	
	 @Override
		@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
		public NotaCreditoProcesarResponse procesarOffline(
				NotaCreditoProcesarRequest mensaje) {
			NotaCreditoProcesarResponse respuesta = new NotaCreditoProcesarResponse();
			respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

			log.debug("Inicia proceso NOTA_CREDITOcion");
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
			com.gizlocorp.gnvoice.modelo.NotaCredito comprobante = null;

			boolean booCatorce = false;

			try {

				log.debug("Inicia validaciones previas");
				if (mensaje.getNotaCredito() == null
						|| mensaje.getNotaCredito().getInfoTributaria() == null) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta
							.setMensajeSistema("Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
					respuesta
							.setMensajeCliente("Datos de comprobante incorrectos. NOTA_CREDITO vacia");

					return respuesta;
				}

				emisor = cacheBean.obtenerOrganizacion(mensaje.getNotaCredito()
						.getInfoTributaria().getRuc());

				if (emisor == null) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setClaveAccesoComprobante(null);
					respuesta.setFechaAutorizacion(null);
					respuesta.setNumeroAutorizacion(null);

					respuesta
							.setMensajeSistema("No existe registrado un emisor con RUC: "
									+ mensaje.getNotaCredito().getInfoTributaria()
									.getRuc());
					respuesta
							.setMensajeCliente("No existe registrado un emisor con RUC: "
									+ mensaje.getNotaCredito().getInfoTributaria()
									.getRuc());
					return respuesta;
				}

				if (mensaje.getNotaCredito().getInfoTributaria().getSecuencial() == null
						|| mensaje.getNotaCredito().getInfoTributaria()
						.getSecuencial().isEmpty()) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setClaveAccesoComprobante(null);
					respuesta.setFechaAutorizacion(null);
					respuesta.setNumeroAutorizacion(null);

					respuesta.setMensajeSistema("Secuencial no puede ser NULL");
					respuesta.setMensajeCliente("Secuencial no puede ser NULL");
					return respuesta;
				}

				Parametro ambParametro = cacheBean.consultarParametro(
						Constantes.AMBIENTE, emisor.getId());
				Parametro monParamtro = cacheBean.consultarParametro(
						Constantes.MONEDA, emisor.getId());
				Parametro codFacParametro = cacheBean.consultarParametro(
						Constantes.COD_NOTA_CREDITO, emisor.getId());
				ambiente = ambParametro.getValor();


				com.gizlocorp.gnvoice.modelo.NotaCredito comprobanteExistente = null;
				try {
					comprobanteExistente = servicioNotaCredito.obtenerComprobante(
							null, mensaje.getCodigoExterno(), mensaje
									.getNotaCredito().getInfoTributaria().getRuc(),
							mensaje.getAgencia());
				} catch (Exception ex) {
					InitialContext ic = new InitialContext();
					servicioNotaCredito = (ServicioNotaCredito) ic
							.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
					comprobanteExistente = servicioNotaCredito.obtenerComprobante(
							null, mensaje.getCodigoExterno(), mensaje
									.getNotaCredito().getInfoTributaria().getRuc(),
							mensaje.getAgencia());

				}

				wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
				wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

				if (comprobanteExistente != null
						&& Estado.AUTORIZADO.getDescripcion().equals(
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

				mensaje.getNotaCredito().setId("comprobante");
				// mensaje.getNotaCredito().setVersion(Constantes.VERSION);
				mensaje.getNotaCredito().setVersion(Constantes.VERSION_1);

				mensaje.getNotaCredito().getInfoNotaCredito()
						.setMoneda(monParamtro.getValor());
				mensaje.getNotaCredito().getInfoTributaria()
						.setAmbiente(ambParametro.getValor());
				mensaje.getNotaCredito().getInfoTributaria()
						.setCodDoc(codFacParametro.getValor());

				if (mensaje.getNotaCredito().getInfoTributaria().getSecuencial()
						.length() < 9) {
					StringBuilder secuencial = new StringBuilder();

					int secuencialTam = 9 - mensaje.getNotaCredito()
							.getInfoTributaria().getSecuencial().length();
					for (int i = 0; i < secuencialTam; i++) {
						secuencial.append("0");
					}
					secuencial.append(mensaje.getNotaCredito().getInfoTributaria()
							.getSecuencial());
					mensaje.getNotaCredito().getInfoTributaria()
							.setSecuencial(secuencial.toString());
				}

				mensaje.getNotaCredito().getInfoTributaria()
						.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

				String dirMatriz = emisor.getDireccion();

				mensaje.getNotaCredito()
						.getInfoTributaria()
						.setDirMatriz(
								dirMatriz != null ? StringEscapeUtils
										.escapeXml(dirMatriz.trim())
										.replaceAll("[\n]", "")
										.replaceAll("[\b]", "") : null);

				if (mensaje.getNotaCredito().getInfoTributaria().getEstab() == null
						|| mensaje.getNotaCredito().getInfoTributaria().getEstab()
						.isEmpty()) {
					mensaje.getNotaCredito().getInfoTributaria()
							.setEstab(emisor.getEstablecimiento());

				}

				if (mensaje.getNotaCredito().getInfoTributaria().getNombreComercial() == null
						|| mensaje.getNotaCredito().getInfoTributaria()
						.getNombreComercial().isEmpty()) {

					mensaje.getNotaCredito()
							.getInfoTributaria()
							.setNombreComercial(
									emisor.getNombreComercial() != null ? StringEscapeUtils
											.escapeXml(
													emisor.getNombreComercial().trim())
											.replaceAll("[\n]", "")
											.replaceAll("[\b]", "")
											: null);

				}


				if (mensaje.getNotaCredito().getInfoTributaria().getPtoEmi() == null
						|| mensaje.getNotaCredito().getInfoTributaria().getPtoEmi()
						.isEmpty()) {
					mensaje.getNotaCredito().getInfoTributaria()
							.setPtoEmi(emisor.getPuntoEmision());
				}
				mensaje.getNotaCredito()
						.getInfoTributaria()
						.setRazonSocial(
								emisor.getNombreComercial() != null ? StringEscapeUtils
										.escapeXml(emisor.getNombreComercial()) : null);

				mensaje.getNotaCredito()
						.getInfoNotaCredito()
						.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
				if (mensaje.getNotaCredito().getInfoNotaCredito()
						.getDirEstablecimiento() == null
						|| mensaje.getNotaCredito().getInfoNotaCredito()
						.getDirEstablecimiento().isEmpty()) {
					mensaje.getNotaCredito()
							.getInfoNotaCredito()
							.setDirEstablecimiento(
									emisor.getDirEstablecimiento() != null ? StringEscapeUtils
											.escapeXml(emisor
													.getDirEstablecimiento())
											: null);
				}
				mensaje.getNotaCredito()
						.getInfoNotaCredito()
						.setObligadoContabilidad(
								emisor.getEsObligadoContabilidad().getDescripcion()
										.toUpperCase());

				// genera clave de acceso
				StringBuilder codigoNumerico = new StringBuilder();
				codigoNumerico.append(mensaje.getNotaCredito().getInfoTributaria()
						.getEstab());
				codigoNumerico.append(mensaje.getNotaCredito().getInfoTributaria()
						.getPtoEmi());
				log.debug("codigo numerico - secuencial de NOTA_CREDITO: "
						+ mensaje.getNotaCredito().getInfoTributaria()
						.getSecuencial());
				codigoNumerico.append(mensaje.getNotaCredito().getInfoTributaria()
						.getSecuencial());
				codigoNumerico.append(Constantes.CODIGO_NUMERICO);

				claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje
								.getNotaCredito().getInfoNotaCredito().getFechaEmision(),
						codFacParametro.getValor(), mensaje.getNotaCredito()
								.getInfoTributaria().getRuc(),
						mensaje.getNotaCredito().getInfoTributaria().getAmbiente(),
						codigoNumerico.toString());

				mensaje.getNotaCredito().getInfoTributaria()
						.setClaveAcceso(claveAcceso);

				log.debug("finaliza validaciones previas");
				log.debug("Inicia generacion de comproabante");
				
				
				comprobante = ComprobanteUtil
						.convertirEsquemaAEntidadNotacredito(mensaje
								.getNotaCredito());
				
				comprobante.setIsOferton(mensaje.getBanderaOferton());
				
				comprobante.setClaveInterna(mensaje.getCodigoExterno());
				comprobante.setCorreoNotificacion(mensaje
						.getCorreoElectronicoNotificacion());
				comprobante.setIdentificadorUsuario(mensaje
						.getIdentificadorUsuario());
				comprobante.setAgencia(mensaje.getAgencia());

				comprobante.setTipoEjecucion(TipoEjecucion.SEC);
				comprobante.setModoEnvio("OFFLINE");

	            if ((comprobanteExistente != null) && (comprobanteExistente.getId() != null)) {
	                comprobante.setId(comprobanteExistente.getId());
	                comprobante.setEstado(comprobanteExistente.getEstado());
	                comprobante.setClaveInterna(comprobanteExistente.getClaveInterna());
	            }


	            ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(
						wsdlLocationAutorizacion, claveAcceso);

				log.info("pasando validacion de autorizacion " + claveAcceso);

				if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {

					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

					respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
					respuesta.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion().toGregorianCalendar()
									.getTime()
									: null);
					respuesta.setNumeroAutorizacion(respAutorizacionComExt
							.getNumeroAutorizacion());
					respuesta.setClaveAccesoComprobante(claveAcceso);

					respuesta.setMensajeSistema("Comprobante AUTORIZADO");

	                comprobante.setNumeroAutorizacion(claveAcceso);
	                comprobante.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion().toGregorianCalendar()
									.getTime()
									: null);

	                if (mensaje.getCorreoElectronicoNotificacion() != null&& !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
						try {
							String comprobanteXML = respAutorizacionComExt.getComprobante();
							comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
							comprobanteXML = comprobanteXML.replace("]]>", "");

							com.gizlocorp.gnvoice.xml.notacredito.NotaCredito factAutorizadaXML = getNotaCreditoXML(comprobanteXML);
							ReporteUtil reporte = new ReporteUtil();
							
							rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_NOTA_CREDITO
																					,new NotaCreditoReporte(factAutorizadaXML)
																					,respAutorizacionComExt.getNumeroAutorizacion()
																					,FechaUtil.formatearFecha(respAutorizacionComExt
																											  .getFechaAutorizacion()
																		                                      .toGregorianCalendar()
																		                                      .getTime()
																		            ,FechaUtil.patronFechaTiempo24)
																					,mensaje.getNotaCredito().getInfoNotaCredito()
																											  .getFechaEmision()
																					,"autorizado"
																					, false
																					,"S".equals(mensaje.getBanderaOferton())?emisor.getLogoEmpresaOpcional():emisor.getLogoEmpresa());

						} catch (Exception e) {
							log.info(e.getMessage(), e);
						}
					}
					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
					try {
						rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getNotaCreditoXML(respAutorizacionComExt)
																				,mensaje.getNotaCredito().getInfoNotaCredito()
																										 .getFechaEmision()
																				,claveAcceso
																				,TipoComprobante.NOTA_CREDITO.getDescripcion()
																				, "autorizado");
					} catch (Exception e) {
						log.info(e.getMessage(), e);
					}
					

					
	                comprobante.setArchivo(rutaArchivoAutorizacionXml);
					comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
	                comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());

	                try {
	                    if (mensaje.getCorreoElectronicoNotificacion() != null
	                            && !mensaje.getCorreoElectronicoNotificacion()
	                            .isEmpty()) {
	                        Plantilla t = cacheBean.obtenerPlantilla(
	                                Constantes.NOTIFIACION, emisor.getId());

	                        Map<String, Object> parametrosBody = new HashMap<String, Object>();
	                        parametrosBody.put("nombre", mensaje
	                                .getNotaCredito().getInfoNotaCredito()
	                                .getRazonSocialComprador());
	                        parametrosBody.put("secuencial", mensaje
	                                .getNotaCredito().getInfoTributaria()
	                                .getSecuencial());
	                        parametrosBody.put("tipo", "NOTA DE CREDITO");
	                        parametrosBody.put("fechaEmision", mensaje
	                                .getNotaCredito().getInfoNotaCredito()
	                                .getFechaEmision());
	                        parametrosBody.put("total", mensaje
	                                .getNotaCredito().getInfoNotaCredito()
	                                .getValorModificacion());
	                        parametrosBody.put("emisor", emisor.getNombre());
	                        parametrosBody.put("ruc", emisor.getRuc());
	                        parametrosBody.put("numeroAutorizacion",
	                                respAutorizacionComExt
	                                        .getNumeroAutorizacion());

	                        MailMessage mailMensaje = new MailMessage();
	                        mailMensaje.setSubject(t
	                                .getTitulo()
	                                .replace("$tipo", "NOTA_CREDITO")
	                                .replace(
	                                        "$numero",
	                                        mensaje.getNotaCredito()
	                                                .getInfoTributaria()
	                                                .getSecuencial()));
	                        mailMensaje.setFrom(cacheBean.consultarParametro("S".equals(mensaje.getBanderaOferton())?Constantes.CORREO_REMITE_EL_OFERTON:
	                                Constantes.CORREO_REMITE, emisor.getId())
	                                .getValor());
	                        mailMensaje.setTo(Arrays.asList(mensaje
	                                .getCorreoElectronicoNotificacion().split(
	                                        ";")));
	                        mailMensaje.setBody(ComprobanteUtil
	                                .generarCuerpoMensaje(parametrosBody,
	                                        t.getDescripcion(), t.getValor()));
	                        mailMensaje.setAttachment(Arrays.asList(
	                                rutaArchivoAutorizacionXml,
	                                rutaArchivoAutorizacionPdf));
	                        MailDelivery.send(
	                                mailMensaje,
	                                cacheBean.consultarParametro(
	                                        Constantes.SMTP_HOST,
	                                        emisor.getId()).getValor(),
	                                cacheBean.consultarParametro(
	                                        Constantes.SMTP_PORT,
	                                        emisor.getId()).getValor(),
	                                cacheBean.consultarParametro(
	                                        Constantes.SMTP_USERNAME,
	                                        emisor.getId()).getValor(),
	                                cacheBean.consultarParametro(
	                                        Constantes.SMTP_PASSWORD,
	                                        emisor.getId())
	                                        .getValorDesencriptado(), 
	                                        "S".equals(mensaje.getBanderaOferton())?emisor.getAcronimoOpcional():emisor.getAcronimo());
	                    }

	                } catch (Exception ex) {
	                    log.error(ex.getMessage(), ex);

	                }

					try {
						servicioNotaCredito.autorizaOffline(comprobante);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (ServicioNotaCredito) ic
									.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");

	                        servicioNotaCredito.autorizaOffline(comprobante);
						} catch (Exception ex2) {
							log.warn("no se guardo el comprobante");
						}
					}

					return respuesta;
				} else if ("PROCESAMIENTO".equals(respAutorizacionComExt
						.getEstado()) || "EN PROCESO".equals(respAutorizacionComExt
						.getEstado())) {
					log.debug("Comprobante en procesamiento: "
							+ comprobante.getClaveAcceso());

					respuesta.setEstado(Estado.PENDIENTE
							.getDescripcion());
					respuesta.setMensajeSistema("Comprobante en procesamiento"
							+ respAutorizacionComExt.getEstado());
					respuesta.setMensajeCliente("Comprobante en procesamiento"
							+ respAutorizacionComExt.getEstado());
					respuesta.setClaveAccesoComprobante(comprobante
							.getClaveAcceso());

					return respuesta;

				} else if (!"ERROR_TECNICO".equals(respAutorizacionComExt.getEstado())) {
					log.debug("Comprobante no autorizado:  " + respAutorizacionComExt.getEstado());

					respuesta.setEstado(Estado.RECHAZADO
							.getDescripcion());
					respuesta.setMensajeSistema("Comprobante No autorizado: "
							+ respAutorizacionComExt.getEstado());
					respuesta.setMensajeCliente("Comprobante No autorizado: "
							+ respAutorizacionComExt.getEstado());
					respuesta.setClaveAccesoComprobante(comprobante
							.getClaveAcceso());

					MensajeRespuesta msgObj = null;
					StringBuilder mensajeRespuesta = new StringBuilder();
					if (respAutorizacionComExt.getMensajes() != null) {
						for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msg : respAutorizacionComExt.getMensajes().getMensaje()) {
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

			ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuestaRecepcion = enviarComprobanteOffline(
					wsdlLocationRecepcion, rutaArchivoFirmadoXml);

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

					log.info("pasando validacion de autorizacion " + claveAcceso);

					if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {

						respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

						respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
						respuesta.setFechaAutorizacion(respAutorizacionComExt
										.getFechaAutorizacion() != null ? respAutorizacionComExt
										.getFechaAutorizacion().toGregorianCalendar()
										.getTime()
										: null);
						respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
						respuesta.setClaveAccesoComprobante(claveAcceso);

						respuesta.setMensajeSistema("Comprobante AUTORIZADO");

		                comprobante.setNumeroAutorizacion(claveAcceso);
		                comprobante.setFechaAutorizacion(respAutorizacionComExt
										.getFechaAutorizacion() != null ? respAutorizacionComExt
										.getFechaAutorizacion().toGregorianCalendar()
										.getTime()
										: null);

		                if (mensaje.getCorreoElectronicoNotificacion() != null&& !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
							try {
								String comprobanteXML = respAutorizacionComExt.getComprobante();
								comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
								comprobanteXML = comprobanteXML.replace("]]>", "");

								com.gizlocorp.gnvoice.xml.notacredito.NotaCredito factAutorizadaXML = getNotaCreditoXML(comprobanteXML);
								ReporteUtil reporte = new ReporteUtil();
								
								rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_NOTA_CREDITO
																						,new NotaCreditoReporte(factAutorizadaXML)
																						,respAutorizacionComExt.getNumeroAutorizacion()
																						,FechaUtil.formatearFecha(respAutorizacionComExt
																												  .getFechaAutorizacion()
																			                                      .toGregorianCalendar()
																			                                      .getTime()
																			            ,FechaUtil.patronFechaTiempo24)
																						,mensaje.getNotaCredito().getInfoNotaCredito()
																												  .getFechaEmision()
																						,"autorizado"
																						, false
																						, "S".equals(mensaje.getBanderaOferton())?emisor.getLogoEmpresaOpcional():emisor.getLogoEmpresa());

							} catch (Exception e) {
								log.info(e.getMessage(), e);
							}
						}
						respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
						try {
							rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getNotaCreditoXML(respAutorizacionComExt)
																					,mensaje.getNotaCredito().getInfoNotaCredito()
																											 .getFechaEmision()
																					,claveAcceso
																					,TipoComprobante.NOTA_CREDITO.getDescripcion()
																					, "autorizado");
						} catch (Exception e) {
							log.info(e.getMessage(), e);
						}
						
		                comprobante.setArchivo(rutaArchivoAutorizacionXml);
						comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
		                comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());

		                try {
		                    if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
		                        Plantilla t = cacheBean.obtenerPlantilla(Constantes.NOTIFIACION, emisor.getId());

		                        Map<String, Object> parametrosBody = new HashMap<String, Object>();
		                        parametrosBody.put("nombre", mensaje.getNotaCredito().getInfoNotaCredito().getRazonSocialComprador());
		                        parametrosBody.put("secuencial", mensaje.getNotaCredito().getInfoTributaria().getSecuencial());
		                        parametrosBody.put("tipo", "NOTA DE CREDITO");
		                        parametrosBody.put("fechaEmision", mensaje.getNotaCredito().getInfoNotaCredito().getFechaEmision());
		                        parametrosBody.put("total", mensaje.getNotaCredito().getInfoNotaCredito().getValorModificacion());
		                        parametrosBody.put("emisor", emisor.getNombre());
		                        parametrosBody.put("ruc", emisor.getRuc());
		                        parametrosBody.put("numeroAutorizacion",respAutorizacionComExt.getNumeroAutorizacion());

		                        MailMessage mailMensaje = new MailMessage();
		                        mailMensaje.setSubject(t.getTitulo().replace("$tipo", "NOTA_CREDITO").replace("$numero",
		                                        mensaje.getNotaCredito()
		                                                .getInfoTributaria()
		                                                .getSecuencial()));
		                        mailMensaje.setFrom(cacheBean.consultarParametro(
		                        		"S".equals(mensaje.getBanderaOferton())?Constantes.CORREO_REMITE_EL_OFERTON:Constantes.CORREO_REMITE, emisor.getId())
		                                .getValor());
		                        mailMensaje.setTo(Arrays.asList(mensaje
		                                .getCorreoElectronicoNotificacion().split(
		                                        ";")));
		                        mailMensaje.setBody(ComprobanteUtil
		                                .generarCuerpoMensaje(parametrosBody,
		                                        t.getDescripcion(), t.getValor()));
		                        mailMensaje.setAttachment(Arrays.asList(
		                                rutaArchivoAutorizacionXml,
		                                rutaArchivoAutorizacionPdf));
		                        MailDelivery.send(
		                                mailMensaje,
		                                cacheBean.consultarParametro(
		                                        Constantes.SMTP_HOST,
		                                        emisor.getId()).getValor(),
		                                cacheBean.consultarParametro(
		                                        Constantes.SMTP_PORT,
		                                        emisor.getId()).getValor(),
		                                cacheBean.consultarParametro(
		                                        Constantes.SMTP_USERNAME,
		                                        emisor.getId()).getValor(),
		                                cacheBean.consultarParametro(
		                                        Constantes.SMTP_PASSWORD,
		                                        emisor.getId())
		                                        .getValorDesencriptado(), 
		                                        "S".equals(mensaje.getBanderaOferton())?emisor.getAcronimoOpcional():emisor.getAcronimo());
		                    }

		                } catch (Exception ex) {
		                    log.error(ex.getMessage(), ex);

		                }

						try {
							servicioNotaCredito.autorizaOffline(comprobante);
						} catch (Exception ex) {
							try {
								InitialContext ic = new InitialContext();
								servicioNotaCredito = (ServicioNotaCredito) ic
										.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");

		                        servicioNotaCredito.autorizaOffline(comprobante);
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

	 
	  private ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud enviarComprobanteOffline(
	            String wsdlLocation, String rutaArchivoXml) {
	        ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuesta = null;

	        int retardo = 8000;

	        try {
	            byte[] documentFirmado = DocumentUtil.readFile(rutaArchivoXml);
	            log.info("Inicia proceso reccepcion offline SRI ..." + wsdlLocation
	                    + " ..." + rutaArchivoXml);
	            // while (contador <= reintentos) {
	            try {
	                ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService servicioRecepcion = new ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService(
	                        new URL(wsdlLocation));

	                // log.info("Inicia proceso reccepcion offline SRI 111...");
	                ec.gob.sri.comprobantes.

	                        ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion
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
	private Autorizacion autorizarComprobante(String wsdlLocation,
			String claveAcceso) {
		Autorizacion respuesta = null;

		// log.debug("CLAVE DE ACCESO: " + claveAcceso);
		int timeout = 7000;

		try {
			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(
					new URL(wsdlLocation), new QName(
							"http://ec.gob.sri.ws.autorizacion",
							"AutorizacionComprobantesService"));

			AutorizacionComprobantes autorizacionws = servicioAutorizacion
					.getAutorizacionComprobantesPort();

			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.internal.ws.connect.timeout", timeout);

			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.internal.ws.request.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put(
					"com.sun.xml.ws.request.timeout", timeout);

			RespuestaComprobante respuestaComprobanteAut = autorizacionws
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
					// log.debug("Renvio por autorizacion en blanco");
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

				Autorizacion autorizacion = respuestaComprobanteAut
						.getAutorizaciones().getAutorizacion().get(0);

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
				RecepcionComprobantesService servicioRecepcion = new RecepcionComprobantesService(
						new URL(wsdlLocation));
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

				// log.debug("Estado enviado del SRI del servicio Recepcion: ...:"
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
	public NotaCreditoProcesarResponse procesar(
			NotaCreditoProcesarRequest mensaje) {

		NotaCreditoProcesarResponse respuesta = new NotaCreditoProcesarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

		log.debug("Inicia proceso NOTA_CREDITOcion");
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
		com.gizlocorp.gnvoice.modelo.NotaCredito comprobante = null;
		
		boolean booCatorce = false;

		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// GENERA DE COMPROBANTE
		// TODO contigencia
		// boolean existioComprobante = false;
		try {

			log.debug("Inicia validaciones previas");
			if (mensaje.getNotaCredito() == null
					|| mensaje.getNotaCredito().getInfoTributaria() == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta
						.setMensajeSistema("Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
				respuesta
						.setMensajeCliente("Datos de comprobante incorrectos. NOTA_CREDITO vacia");

				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getNotaCredito()
					.getInfoTributaria().getRuc());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta
						.setMensajeSistema("No existe registrado un emisor con RUC: "
								+ mensaje.getNotaCredito().getInfoTributaria()
										.getRuc());
				respuesta
						.setMensajeCliente("No existe registrado un emisor con RUC: "
								+ mensaje.getNotaCredito().getInfoTributaria()
										.getRuc());
				return respuesta;
			}

			if (mensaje.getNotaCredito().getInfoTributaria().getSecuencial() == null
					|| mensaje.getNotaCredito().getInfoTributaria()
							.getSecuencial().isEmpty()) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Secuencial no puede ser NULL");
				respuesta.setMensajeCliente("Secuencial no puede ser NULL");
				return respuesta;
			}

			Parametro ambParametro = cacheBean.consultarParametro(
					Constantes.AMBIENTE, emisor.getId());
			Parametro monParamtro = cacheBean.consultarParametro(
					Constantes.MONEDA, emisor.getId());
			Parametro codFacParametro = cacheBean.consultarParametro(
					Constantes.COD_NOTA_CREDITO, emisor.getId());
			ambiente = ambParametro.getValor();

			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
			wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				wsdlLocationRecepcion = cacheBean.consultarParametro(
						Constantes.SERVICIO_REC_SRI_PRODUCCION, emisor.getId())
						.getValor();

			} else {
				wsdlLocationRecepcion = cacheBean.consultarParametro(
						Constantes.SERVICIO_REC_SRI_PRUEBA, emisor.getId())
						.getValor();
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

			com.gizlocorp.gnvoice.modelo.NotaCredito comprobanteExistente = null;
			try {
				comprobanteExistente = servicioNotaCredito.obtenerComprobante(
						null, mensaje.getCodigoExterno(), mensaje
								.getNotaCredito().getInfoTributaria().getRuc(),
						mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				servicioNotaCredito = (ServicioNotaCredito) ic
						.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
				comprobanteExistente = servicioNotaCredito.obtenerComprobante(
						null, mensaje.getCodigoExterno(), mensaje
								.getNotaCredito().getInfoTributaria().getRuc(),
						mensaje.getAgencia());

			}

			if (comprobanteExistente != null
					&& Estado.AUTORIZADO.getDescripcion().equals(
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

			// Setea nodo principal
			mensaje.getNotaCredito().setId("comprobante");
			// mensaje.getNotaCredito().setVersion(Constantes.VERSION);
			mensaje.getNotaCredito().setVersion(Constantes.VERSION_1);

			mensaje.getNotaCredito().getInfoNotaCredito()
					.setMoneda(monParamtro.getValor());
			mensaje.getNotaCredito().getInfoTributaria()
					.setAmbiente(ambParametro.getValor());
			mensaje.getNotaCredito().getInfoTributaria()
					.setCodDoc(codFacParametro.getValor());

			if (mensaje.getNotaCredito().getInfoTributaria().getSecuencial()
					.length() < 9) {
				StringBuilder secuencial = new StringBuilder();

				int secuencialTam = 9 - mensaje.getNotaCredito()
						.getInfoTributaria().getSecuencial().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(mensaje.getNotaCredito().getInfoTributaria()
						.getSecuencial());
				mensaje.getNotaCredito().getInfoTributaria()
						.setSecuencial(secuencial.toString());
			}

			mensaje.getNotaCredito().getInfoTributaria()
					.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			String dirMatriz = emisor.getDireccion();

			mensaje.getNotaCredito()
					.getInfoTributaria()
					.setDirMatriz(
							dirMatriz != null ? StringEscapeUtils
									.escapeXml(dirMatriz.trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "") : null);

			if (mensaje.getNotaCredito().getInfoTributaria().getEstab() == null
					|| mensaje.getNotaCredito().getInfoTributaria().getEstab()
							.isEmpty()) {
				mensaje.getNotaCredito().getInfoTributaria()
						.setEstab(emisor.getEstablecimiento());

			}
			
			if (mensaje.getNotaCredito().getInfoTributaria().getNombreComercial() == null
					|| mensaje.getNotaCredito().getInfoTributaria()
							.getNombreComercial().isEmpty()) {
				
				mensaje.getNotaCredito()
				.getInfoTributaria()
				.setNombreComercial(
						emisor.getNombreComercial() != null ? StringEscapeUtils
								.escapeXml(
										emisor.getNombreComercial().trim())
								.replaceAll("[\n]", "")
								.replaceAll("[\b]", "")
								: null);
				
			}
			

			if (mensaje.getNotaCredito().getInfoTributaria().getPtoEmi() == null
					|| mensaje.getNotaCredito().getInfoTributaria().getPtoEmi()
							.isEmpty()) {
				mensaje.getNotaCredito().getInfoTributaria()
						.setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getNotaCredito()
					.getInfoTributaria()
					.setRazonSocial(
							emisor.getNombreComercial() != null ? StringEscapeUtils
									.escapeXml(emisor.getNombreComercial()) : null);

			mensaje.getNotaCredito()
					.getInfoNotaCredito()
					.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			if (mensaje.getNotaCredito().getInfoNotaCredito()
					.getDirEstablecimiento() == null
					|| mensaje.getNotaCredito().getInfoNotaCredito()
							.getDirEstablecimiento().isEmpty()) {
				mensaje.getNotaCredito()
						.getInfoNotaCredito()
						.setDirEstablecimiento(
								emisor.getDirEstablecimiento() != null ? StringEscapeUtils
										.escapeXml(emisor
												.getDirEstablecimiento())
										: null);
			}
			mensaje.getNotaCredito()
					.getInfoNotaCredito()
					.setObligadoContabilidad(
							emisor.getEsObligadoContabilidad().getDescripcion()
									.toUpperCase());

			// TODO contigencia
			// if (existioComprobante
			// && comprobanteExistente != null
			// && comprobanteExistente.getTipoEmision().equals(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo())) {
			// claveAcceso = comprobanteExistente.getClaveAcceso();
			// mensaje.getNotaCredito()
			// .getInfoTributaria()
			// .setTipoEmision(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo());
			// } else {
			// genera clave de acceso
			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getNotaCredito().getInfoTributaria()
					.getEstab());
			codigoNumerico.append(mensaje.getNotaCredito().getInfoTributaria()
					.getPtoEmi());
			log.debug("codigo numerico - secuencial de NOTA_CREDITO: "
					+ mensaje.getNotaCredito().getInfoTributaria()
							.getSecuencial());
			codigoNumerico.append(mensaje.getNotaCredito().getInfoTributaria()
					.getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);

			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje
					.getNotaCredito().getInfoNotaCredito().getFechaEmision(),
					codFacParametro.getValor(), mensaje.getNotaCredito()
							.getInfoTributaria().getRuc(),
					mensaje.getNotaCredito().getInfoTributaria().getAmbiente(),
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
			// .generarClaveAccesoProveedor(mensaje.getNotaCredito()
			// .getInfoNotaCredito().getFechaEmision(),
			// codFacParametro.getValor(),
			// claveNoUsada.getClave());
			// mensaje.getNotaCredito()
			// .getInfoTributaria()
			// .setTipoEmision(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo());
			// mensaje.getNotaCredito().getInfoTributaria()
			// .setClaveAcceso(claveAcceso);
			// }
			// } else {
			mensaje.getNotaCredito().getInfoTributaria()
					.setClaveAcceso(claveAcceso);
			// }
			// -jose--------------------------------------------------------------------------------------------------------------------------------*/

			log.debug("finaliza validaciones previas");
			log.debug("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil
					.convertirEsquemaAEntidadNotacredito(mensaje
							.getNotaCredito());

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
					if (mensaje.getCorreoElectronicoNotificacion() != null
							&& !mensaje.getCorreoElectronicoNotificacion()
									.isEmpty()) {
						try {
							String comprobanteXML = respAutorizacionComExt.getComprobante();
							comprobanteXML = comprobanteXML.replace("<![CDATA[", "");
							comprobanteXML = comprobanteXML.replace("]]>", "");

							com.gizlocorp.gnvoice.xml.notacredito.NotaCredito factAutorizadaXML = getNotaCreditoXML(comprobanteXML);
							ReporteUtil reporte = new ReporteUtil();
							
							if(mensaje.getNotaCredito().getInfoNotaCredito() != null){
								for(TotalImpuesto item: mensaje.getNotaCredito().getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto()){
									if(item.getCodigoPorcentaje().equals("3")){
										booCatorce = true;
										break;
									}
								}
							}
							if(booCatorce){
								rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_NOTA_CREDITO_14
																					,new NotaCreditoReporte(factAutorizadaXML)
																					,respAutorizacionComExt.getNumeroAutorizacion()
																					,FechaUtil.formatearFecha(respAutorizacionComExt
																											  .getFechaAutorizacion()
																		.toGregorianCalendar()
																		.getTime(),
																FechaUtil.patronFechaTiempo24),
												mensaje.getNotaCredito()
														.getInfoNotaCredito()
														.getFechaEmision(),
												"autorizado", false, emisor
														.getLogoEmpresa());
							}else{
								rutaArchivoAutorizacionPdf = reporte
										.generarReporte(
												Constantes.REP_NOTA_CREDITO,
												new NotaCreditoReporte(
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
												mensaje.getNotaCredito()
														.getInfoNotaCredito()
														.getFechaEmision(),
												"autorizado", false, emisor
														.getLogoEmpresa());
							}

							

						} catch (Exception e) {
							log.debug(e.getMessage(), e);
						}
					}
					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
					try {

						rutaArchivoAutorizacionXml = DocumentUtil
								.createDocument(
										getNotaCreditoXML(respAutorizacionComExt),
										mensaje.getNotaCredito()
												.getInfoNotaCredito()
												.getFechaEmision(),
										claveAcceso,
										TipoComprobante.NOTA_CREDITO
												.getDescripcion(), "autorizado");

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}

					// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje
					// .getNotaCredito().getInfoNotaCredito()
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
						servicioNotaCredito.autoriza(comprobante);
					} catch (Exception ex) {
						try {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (ServicioNotaCredito) ic
									.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
							servicioNotaCredito.autoriza(comprobante);
						} catch (Exception ex2) {
							log.warn("no actualiza soluciones");
						}
					}

					try {
						if (mensaje.getCorreoElectronicoNotificacion() != null
								&& !mensaje.getCorreoElectronicoNotificacion()
										.isEmpty()) {
							Plantilla t = cacheBean.obtenerPlantilla(
									Constantes.NOTIFIACION, emisor.getId());

							Map<String, Object> parametrosBody = new HashMap<String, Object>();
							parametrosBody.put("nombre", mensaje
									.getNotaCredito().getInfoNotaCredito()
									.getRazonSocialComprador());
							parametrosBody.put("secuencial", mensaje
									.getNotaCredito().getInfoTributaria()
									.getSecuencial());
							parametrosBody.put("tipo", "NOTA DE CREDITO");
							parametrosBody.put("fechaEmision", mensaje
									.getNotaCredito().getInfoNotaCredito()
									.getFechaEmision());
							parametrosBody.put("total", mensaje
									.getNotaCredito().getInfoNotaCredito()
									.getValorModificacion());
							parametrosBody.put("emisor", emisor.getNombre());
							parametrosBody.put("ruc", emisor.getRuc());
							parametrosBody.put("numeroAutorizacion",
									respAutorizacionComExt
											.getNumeroAutorizacion());

							MailMessage mailMensaje = new MailMessage();
							mailMensaje.setSubject(t
									.getTitulo()
									.replace("$tipo", "NOTA_CREDITO")
									.replace(
											"$numero",
											mensaje.getNotaCredito()
													.getInfoTributaria()
													.getSecuencial()));
							mailMensaje.setFrom(cacheBean.consultarParametro(
									Constantes.CORREO_REMITE, emisor.getId())
									.getValor());
							mailMensaje.setTo(Arrays.asList(mensaje
									.getCorreoElectronicoNotificacion().split(
											";")));
							mailMensaje.setBody(ComprobanteUtil
									.generarCuerpoMensaje(parametrosBody,
											t.getDescripcion(), t.getValor()));
							mailMensaje.setAttachment(Arrays.asList(
									rutaArchivoAutorizacionXml,
									rutaArchivoAutorizacionPdf));
							MailDelivery.send(
									mailMensaje,
									cacheBean.consultarParametro(
											Constantes.SMTP_HOST,
											emisor.getId()).getValor(),
									cacheBean.consultarParametro(
											Constantes.SMTP_PORT,
											emisor.getId()).getValor(),
									cacheBean.consultarParametro(
											Constantes.SMTP_USERNAME,
											emisor.getId()).getValor(),
									cacheBean.consultarParametro(
											Constantes.SMTP_PASSWORD,
											emisor.getId())
											.getValorDesencriptado(), emisor
											.getAcronimo());
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
		// Constantes.REP_NOTA_CREDITO,
		// new NOTA_CREDITOReporte(mensaje.getNotaCredito()), null,
		// null, mensaje.getNotaCredito().getInfoNotaCredito()
		// .getFechaEmision(), "contigencia", false,
		// emisor.getLogoEmpresa());
		//
		// Plantilla t = cacheBean.obtenerPlantilla(
		// Constantes.NOTIFIACION, emisor.getId());
		//
		// Map<String, Object> parametrosBody = new HashMap<String, Object>();
		// parametrosBody.put("nombre", mensaje.getNotaCredito()
		// .getInfoNotaCredito().getRazonSocialComprador());
		// parametrosBody.put("tipo", "NOTA_CREDITO");
		// parametrosBody.put("secuencial", mensaje.getNotaCredito()
		// .getInfoTributaria().getSecuencial());
		// parametrosBody.put("fechaEmision", mensaje.getNotaCredito()
		// .getInfoNotaCredito().getFechaEmision());
		// parametrosBody.put("total", mensaje.getNotaCredito()
		// .getInfoNotaCredito().getImporteTotal());
		// parametrosBody.put("emisor", emisor.getNombre());
		// parametrosBody.put("ruc", emisor.getRuc());
		// parametrosBody.put("numeroAutorizacion", "");
		//
		// MailMessage mailMensaje = new MailMessage();
		// mailMensaje.setSubject(t
		// .getTitulo()
		// .replace("$tipo", "NOTA_CREDITO")
		// .replace(
		// "$numero",
		// mensaje.getNotaCredito().getInfoTributaria()
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

						com.gizlocorp.gnvoice.xml.notacredito.NotaCredito factAutorizadaXML = getNotaCreditoXML(comprobanteXML);

						ReporteUtil reporte = new ReporteUtil();
						
						if(mensaje.getNotaCredito().getInfoNotaCredito() != null){
							for(TotalImpuesto item: mensaje.getNotaCredito().getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto()){
								if(item.getCodigoPorcentaje().equals("3")){
									booCatorce = true;
									break;
								}
							}
						}
						if(booCatorce){
							rutaArchivoAutorizacionPdf = reporte.generarReporte(
									Constantes.REP_NOTA_CREDITO_14,
									new NotaCreditoReporte(factAutorizadaXML),
									respAutorizacion.getNumeroAutorizacion(),
									FechaUtil.formatearFecha(respAutorizacion
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											FechaUtil.patronFechaTiempo24), mensaje
											.getNotaCredito().getInfoNotaCredito()
											.getFechaEmision(), "autorizado",
									false, emisor.getLogoEmpresa());
						}else{
							rutaArchivoAutorizacionPdf = reporte.generarReporte(
									Constantes.REP_NOTA_CREDITO,
									new NotaCreditoReporte(factAutorizadaXML),
									respAutorizacion.getNumeroAutorizacion(),
									FechaUtil.formatearFecha(respAutorizacion
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											FechaUtil.patronFechaTiempo24), mensaje
											.getNotaCredito().getInfoNotaCredito()
											.getFechaEmision(), "autorizado",
									false, emisor.getLogoEmpresa());
						}

						

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}
				}
				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
				try {
					rutaArchivoAutorizacionXml = DocumentUtil.createDocument(
							getNotaCreditoXML(respAutorizacion), mensaje
									.getNotaCredito().getInfoNotaCredito()
									.getFechaEmision(), claveAcceso,
							TipoComprobante.NOTA_CREDITO.getDescripcion(),
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
					servicioNotaCredito.autoriza(comprobante);
				} catch (Exception ex) {
					try {
						InitialContext ic = new InitialContext();
						servicioNotaCredito = (ServicioNotaCredito) ic
								.lookup("java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito");
						servicioNotaCredito.autoriza(comprobante);

					} catch (Exception ex2) {
						log.warn("no se guardo el comprobante");
					}
				}

				// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje.getNotaCredito()
				// .getInfoNotaCredito().getIdentificacionComprador());

				try {
					if (mensaje.getCorreoElectronicoNotificacion() != null
							&& !mensaje.getCorreoElectronicoNotificacion()
									.isEmpty()) {
						Plantilla t = cacheBean.obtenerPlantilla(
								Constantes.NOTIFIACION, emisor.getId());

						Map<String, Object> parametrosBody = new HashMap<String, Object>();
						parametrosBody
								.put("nombre", mensaje.getNotaCredito()
										.getInfoNotaCredito()
										.getRazonSocialComprador());
						parametrosBody.put("secuencial", mensaje
								.getNotaCredito().getInfoTributaria()
								.getSecuencial());
						parametrosBody.put("tipo", "NOTA DE CREDITO");
						parametrosBody.put("fechaEmision", mensaje
								.getNotaCredito().getInfoNotaCredito()
								.getFechaEmision());
						parametrosBody.put("total", mensaje.getNotaCredito()
								.getInfoNotaCredito().getValorModificacion());
						parametrosBody.put("emisor", emisor.getNombre());
						parametrosBody.put("ruc", emisor.getRuc());
						parametrosBody.put("numeroAutorizacion",
								respAutorizacion.getNumeroAutorizacion());

						MailMessage mailMensaje = new MailMessage();
						mailMensaje.setSubject(t
								.getTitulo()
								.replace("$tipo", "NOTA_CREDITO")
								.replace(
										"$numero",
										mensaje.getNotaCredito()
												.getInfoTributaria()
												.getSecuencial()));
						mailMensaje.setFrom(cacheBean.consultarParametro(
								Constantes.CORREO_REMITE, emisor.getId())
								.getValor());
						mailMensaje
								.setTo(Arrays.asList(mensaje
										.getCorreoElectronicoNotificacion()
										.split(";")));
						mailMensaje.setBody(ComprobanteUtil
								.generarCuerpoMensaje(parametrosBody,
										t.getDescripcion(), t.getValor()));
						mailMensaje.setAttachment(Arrays.asList(
								rutaArchivoAutorizacionXml,
								rutaArchivoAutorizacionPdf));
						MailDelivery
								.send(mailMensaje,
										cacheBean.consultarParametro(
												Constantes.SMTP_HOST,
												emisor.getId()).getValor(),
										cacheBean.consultarParametro(
												Constantes.SMTP_PORT,
												emisor.getId()).getValor(),
										cacheBean.consultarParametro(
												Constantes.SMTP_USERNAME,
												emisor.getId()).getValor(),
										cacheBean.consultarParametro(
												Constantes.SMTP_PASSWORD,
												emisor.getId())
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
				// mensaje.getNotaCredito().getInfoNotaCredito()
				// .getFechaEmision(),
				// claveAcceso,
				// com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_CREDITO
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
			// mensaje.getNotaCredito().getInfoNotaCredito()
			// .getFechaEmision(),
			// claveAcceso,
			// com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_CREDITO
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
			// mensaje.getNotaCredito().getInfoNotaCredito()
			// .getFechaEmision(),
			// claveAcceso,
			// com.gizlocorp.gnvoice.enumeracion.TipoComprobante.NOTA_CREDITO
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
	public NotaCreditoConsultarResponse consultar(
			NotaCreditoConsultarRequest mensaje) {
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

				List<com.gizlocorp.gnvoice.modelo.NotaCredito> items = servicioNotaCredito
						.obtenerComprobanteConsulta(mensaje.getRuc(),
								mensaje.getClaveAcceso(),
								mensaje.getSecuencial(),
								mensaje.getCodigoExterno(), mensaje.getEstado());
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
	
	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,
			String claveAcceso) {
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

		// log.info("CLAVE DE ACCESO: " + claveAcceso);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public NotaCreditoRecibirResponse recibir(NotaCreditoRecibirRequest mensaje) {
		NotaCreditoRecibirResponse respuesta = new NotaCreditoRecibirResponse();
		boolean offline = false;
		try {

			String autorizacion = DocumentUtil.readContentFile(mensaje
					.getComprobanteProveedor());
			
			Autorizacion autorizacionXML = null;
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;
			
			
			
			
			/*********/
			String numeroAutorizcionLeng ="";
			if (autorizacion.contains("&lt;numeroAutorizacion&gt;")
					&& autorizacion.contains("&lt;/numeroAutorizacion&gt;")) {
				String[] ab = autorizacion
						.split("&lt;numeroAutorizacion&gt;");
				numeroAutorizcionLeng = ab[1]
						.split("&lt;/numeroAutorizacion&gt;")[0].trim();
				
			}

			if (autorizacion.contains("<numeroAutorizacion>")
					&& autorizacion.contains("</numeroAutorizacion>")) {
				String[] ab = autorizacion.split("<numeroAutorizacion>");
				numeroAutorizcionLeng = ab[1].split("</numeroAutorizacion>")[0]
						.trim();
				
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
					String[] ab = autorizacion
							.split("&lt;numeroAutorizacion&gt;");
					numeroAutorizcion = ab[1]
							.split("&lt;/numeroAutorizacion&gt;")[0].trim();
				}

				if (autorizacion.contains("<numeroAutorizacion>")
						&& autorizacion.contains("</numeroAutorizacion>")) {
					String[] ab = autorizacion.split("<numeroAutorizacion>");
					numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0]
							.trim();
				}

				if (claveConsultar.equals(numeroAutorizcion)) {				
					String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";					
					autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);					
					offline = true;
					
				} else {
					autorizacionXML = autorizarComprobante(
							wsdlLocationAutorizacion, claveConsultar);
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

					autorizacion = autorizacion.replace("<Autorizacion>",
							"<autorizacion>");
					autorizacion = autorizacion.replace("</Autorizacion>",
							"</autorizacion>");

					if (autorizacion
							.contains("<RespuestaAutorizacionComprobante>")) {
						autorizacion = autorizacion.replace(
								"<RespuestaAutorizacionComprobante>", "");
						autorizacion = autorizacion.replace(
								"</RespuestaAutorizacionComprobante>", "");
					}

					autorizacion = autorizacion.trim();
				}
				
				log.info("***recibiendo... 888*** "+numeroAutorizcionLeng.length());
				
				if(numeroAutorizcionLeng.length() <= 36 ){
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
					
					if(autorizacionOfflineXML != null && autorizacionOfflineXML.getComprobante() != null){
						offline = true;
					}
					
					if((autorizacionOfflineXML == null || autorizacionOfflineXML.getComprobante() == null) && (autorizacionXML == null || autorizacionXML.getComprobante() == null)){
						log.info("***Comprobante no tiene numero de autorizacion 222");
						respuesta.setEstado(Estado.ERROR.getDescripcion());
						respuesta.setMensajeCliente("Comprobante no tiene numero de autorizacion ");
						respuesta.setMensajeSistema("Comprobante no tiene numero de autorizacion ");
						
						return respuesta;
					}
					
				}else{
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
							String[] ab = autorizacion
									.split("<numeroAutorizacion>");
							numeroAutorizcion = ab[1]
									.split("</numeroAutorizacion>")[0].trim();
						}

						if (claveConsultar.equals(numeroAutorizcion)) {				
							String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";						
							autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);
							offline = true;
						} else {
							autorizacionXML = autorizarComprobante(
									wsdlLocationAutorizacion, claveConsultar);
						}
					}
				}
				
			}

			

			// Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);

			
			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito documentoXML = null;
			if(offline){
				
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
				
				String documento = autorizacionOfflineXML.getComprobante();
				documento = documento.replace("<![CDATA[", "");
				documento = documento.replace("]]>", "");
				documentoXML = getNotaCreditoXML(documento);
			}else{
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
				String documento = autorizacionXML.getComprobante();
				documento = documento.replace("<![CDATA[", "");
				documento = documento.replace("]]>", "");
				documentoXML = getNotaCreditoXML(documento);
			}


			com.gizlocorp.gnvoice.modelo.NotaCredito documentoObj = servicioNotaCredito
					.obtenerComprobante(documentoXML.getInfoTributaria()
							.getClaveAcceso(), null, null, null);

			

			if (documentoObj != null
					&& "AUTORIZADO".equals(documentoObj.getEstado())) {
				respuesta
						.setMensajeCliente("La clave de acceso ya esta registrada "
								+ documentoObj.getTipoGeneracion()
										.getDescripcion());
				respuesta
						.setMensajeSistema("La clave de acceso ya esta registrada"
								+ documentoObj.getTipoGeneracion()
										.getDescripcion());
				return respuesta;
			}

			if (documentoObj == null) {
				documentoObj = ComprobanteUtil
						.convertirEsquemaAEntidadNotacredito(documentoXML);
			} else {
				com.gizlocorp.gnvoice.modelo.NotaCredito documentoAux = documentoObj;
				documentoObj = ComprobanteUtil
						.convertirEsquemaAEntidadNotacredito(documentoXML);
				documentoObj.setId(documentoAux.getId());
			}

			documentoObj.setInfoAdicional(StringUtil
					.validateInfoXML(documentoXML.getInfoTributaria()
							.getRazonSocial().trim()));
			documentoObj.setTipoEjecucion(TipoEjecucion.SEC);
			documentoObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
			documentoObj.setProceso(mensaje.getProceso());
			documentoObj.setArchivo(mensaje.getComprobanteProveedor());

			if (autorizacionXML.getNumeroAutorizacion() == null
					|| autorizacionXML.getNumeroAutorizacion().isEmpty()) {
				respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
				respuesta
						.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
				respuesta
						.setMensajeCliente("El Comprobante no tiene numero de Autorizacion");

				documentoObj.setEstado(Estado.ERROR.getDescripcion());
				documentoObj = servicioNotaCredito.recibirNotaCredito(
						documentoObj,
						"Comprobante no tiene numero de Autorizacion");
				return respuesta;

			}

			Organizacion emisor = cacheBean.obtenerOrganizacion(documentoXML
					.getInfoNotaCredito().getIdentificacionComprador());

			if (emisor == null) {
				respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
				respuesta
						.setMensajeSistema("No existe registrado un emisor con RUC: "
								+ documentoXML.getInfoTributaria().getRuc());
				respuesta
						.setMensajeCliente("No existe registrado un emisor con RUC: "
								+ documentoXML.getInfoTributaria().getRuc());

				documentoObj.setEstado(Estado.ERROR.getDescripcion());
				documentoObj = servicioNotaCredito.recibirNotaCredito(
						documentoObj,
						"No existe registrado un emisor con RUC: "
								+ documentoXML.getInfoTributaria().getRuc());
				return respuesta;
			}

//			Parametro ambParametro = cacheBean.consultarParametro(
//					Constantes.AMBIENTE, emisor.getId());
//			String ambiente = ambParametro.getValor();

			// creacion de documentos doc class

			if (mensaje.getMailOrigen() != null
					&& !mensaje.getMailOrigen().isEmpty()) {
				Parametro serRutaDocCalss = cacheBean.consultarParametro(
						Constantes.DOC_CLASS_RUTA, emisor.getId());
				String strRutaDocCalss = serRutaDocCalss != null ? serRutaDocCalss
						.getValor() : "";

				DocumentUtil.createDocumentDocClass(
						getNotaCreditoXML(autorizacionXML), documentoXML
								.getInfoTributaria().getClaveAcceso(),
						strRutaDocCalss);

				if ((mensaje.getComprobanteProveedorPdf() != null)
						&& (!mensaje.getComprobanteProveedorPdf().isEmpty())) {
					File file = new File(mensaje.getComprobanteProveedorPdf()
							.toString());
					if (file.exists()) {
						byte[] bFile = new byte[(int) file.length()];
						DocumentUtil.createDocumentPdfDocClass(bFile,
								documentoXML.getInfoTributaria()
										.getClaveAcceso(), strRutaDocCalss);
					} else {
						File file2 = new File(mensaje
								.getComprobanteProveedorPdf().replace("pdf",
										"PDF"));
						if (file2.exists()) {
							byte[] bFile = new byte[(int) file2.length()];
							DocumentUtil.createDocumentPdfDocClass(bFile,
									autorizacionXML.getClaveAcceso(),
									strRutaDocCalss);
						}
					}

				}
				documentoObj.setCorreoNotificacion(mensaje.getMailOrigen());
			}

			// TODO agregar velocity
			

			if (mensaje.getProceso().equals(
					com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {
				Parametro serRimWS = cacheBean.consultarParametro(
						Constantes.SER_RIM_WS, emisor.getId());
				String dirWsRim = serRimWS != null ? serRimWS.getValor() : "";

				Parametro serRimUri = cacheBean.consultarParametro(
						Constantes.SER_RIM_URI, emisor.getId());
				String dirUriRim = serRimUri != null ? serRimUri.getValor()
						: "";

				String ordenCompra = null;
				String idDevolucion = null;
				boolean esDevolucion = false;
				log.info("***Comenzando a traducir 2222***");
				CeDisResponse ceDisresponse = new CeDisResponse();
				if ((dirWsRim != null) && (!dirWsRim.isEmpty())
						&& (dirUriRim != null) && (!dirUriRim.isEmpty())) {
					ClienteRimBean clienteRimBean = new ClienteRimBean(
							dirWsRim, dirUriRim);
					String ordenCompraName = "orden";
					String ordenCompraName2 = "compra";
					String idDevolucionName = "ID Devolucion";
					if (documentoXML.getInfoAdicional() != null) {
						for (CampoAdicional item : documentoXML
								.getInfoAdicional().getCampoAdicional()) {
							if ((item.getNombre().trim().toUpperCase()
									.contains(ordenCompraName.toUpperCase()))
									|| (item.getNombre().trim().toUpperCase()
											.contains(ordenCompraName2
													.toUpperCase()))) {
								ordenCompra = item.getValue();
								log.info("***Comenzando a traducir 3333***"
										+ ordenCompra);
								break;
							}
							if (item.getNombre().toUpperCase()
									.equals(idDevolucionName.toUpperCase())) {
								idDevolucion = item.getValue();
								esDevolucion = true;
								log.info("***Comenzando a traducir 3333***"
										+ idDevolucion);
								break;
							}
						}
					}
					
					boolean sinCodigo = true;
					if (ordenCompra != null) {
						if (ordenCompra.matches("[0-9]*")) {
							documentoObj.setOrdenCompra(ordenCompra);
							ceDisresponse = clienteRimBean
									.llamadaPorOrdenFactura(ordenCompra);
							sinCodigo = false;
						}
					}
					// else
					if ((mensaje.getOrdenCompra() != null)
							&& (!mensaje.getOrdenCompra().isEmpty())) {
						if (mensaje.getOrdenCompra().matches("[0-9]*")) {
							documentoObj.setOrdenCompra(mensaje
									.getOrdenCompra());
							ceDisresponse = clienteRimBean
									.llamadaPorOrdenFactura(mensaje
											.getOrdenCompra());

							ordenCompra = mensaje.getOrdenCompra();
							sinCodigo = false;
						}
					} else if (idDevolucion != null) {
						if (idDevolucion.matches("[0-9]*")) {
							documentoObj.setOrdenCompra(idDevolucion);
							ceDisresponse = clienteRimBean
									.llamadaPorDevolucion(idDevolucion);
							sinCodigo = false;
						} else if ((mensaje.getOrdenCompra() != null)
								&& (!mensaje.getOrdenCompra().isEmpty())) {
							if (mensaje.getOrdenCompra().matches("[0-9]*")) {
								documentoObj.setOrdenCompra(mensaje
										.getOrdenCompra());
								ceDisresponse = clienteRimBean
										.llamadaPorDevolucion(mensaje
												.getOrdenCompra());
								sinCodigo = false;
							}
						}

					}

					if (sinCodigo) {
						respuesta.setClaveAccesoComprobante(documentoXML
								.getInfoTributaria().getClaveAcceso());
						respuesta
								.setMensajeCliente("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
						respuesta
								.setMensajeSistema("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
						documentoObj
								.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
						documentoObj.setEstado(Estado.ERROR.getDescripcion());
						log.info("***Ha ocurrido un error nose encuentra el nro orden de compra o devolucion Nota credito***");
						documentoObj = this.servicioNotaCredito
								.recibirNotaCredito(documentoObj,
										"El comprobante no tiene orden de compra ingresar manualmente");
						return respuesta;
					}

					if (ceDisresponse == null) {
						respuesta.setClaveAccesoComprobante(documentoXML
								.getInfoTributaria().getClaveAcceso());
						respuesta
								.setMensajeCliente("Ha ocurrido un error en el proceso");
						respuesta
								.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
						documentoObj
								.setMensajeErrorReim("Ha ocurrido un error no devolvio datos el web service");
						documentoObj.setEstado(Estado.ERROR.getDescripcion());
						log.info("***El web service no encontro informacion Nota credito***");
						documentoObj = this.servicioNotaCredito
								.recibirNotaCredito(documentoObj,
										"El web service no encontro informacion.");
						return respuesta;
					}

				}

				// fin servicio web
				// log.info("****paso wb****");
				SimpleDateFormat sdfDate = new SimpleDateFormat(
						"yyyyMMddHHmmss");// dd/MM/yyyy
				SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd/MM/yyyy");
				Date now = new Date();
				String strDate = sdfDate.format(now);
				int num = 2;
				ArrayList list = new ArrayList();
				BigDecimal totalUnidades = new BigDecimal(0);
				if (documentoXML.getDetalles().getDetalle() != null
						&& !documentoXML.getDetalles().getDetalle().isEmpty()) {
					for (Detalle itera : documentoXML.getDetalles()
							.getDetalle()) {
						num++;
						Map map = new HashMap();
						map.put("TDETL", "TDETL");
						String linea = completar(String.valueOf(num).length(),
								num);
						map.put("linea", linea);

			
						
						boolean booVpn=false;
						try{
							if (validaEAN(itera.getCodigoInterno())) {
								map.put("UPC",
										completarEspaciosEmblancosLetras(
												ConstantesRim.UPC, itera
														.getCodigoInterno()
														.length(), itera
														.getCodigoInterno()));							
							} else {
								if(validaEAN(itera.getCodigoAdicional())){
									map.put("UPC",
											completarEspaciosEmblancosLetras(
													ConstantesRim.UPC, itera
															.getCodigoAdicional()
															.length(), itera
															.getCodigoAdicional()));								
								}else{
									map.put("UPC",completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
									booVpn = true;
								}
							}
						}catch(Exception e){
				        	e.printStackTrace();
				        }
						// map.put("UPC",
						// completarEspaciosEmblancosLetras(
						// ConstantesRim.UPC, itera
						// .getCodigoInterno().length(),
						// itera.getCodigoInterno()));
						map.put("UPC_Supplement",
								completarEspaciosEmblancosLetras(
										ConstantesRim.UPC_Supplement, 0, ""));
						map.put("Item",
								completarEspaciosEmblancosLetras(
										ConstantesRim.Item, 0, ""));
//						map.put("VPN",
//								completarEspaciosEmblancosLetras(
//										ConstantesRim.VPN, 0, ""));
						if(booVpn){
							map.put("VPN",
									completarEspaciosEmblancosLetras(
											ConstantesRim.VPN, itera
											.getCodigoInterno()
											.length(), itera
											.getCodigoInterno()));
						}else{
							map.put("VPN",
									completarEspaciosEmblancosLetras(
											ConstantesRim.VPN, 0, ""));
						}
						map.put("Original_Document_Quantity",
								completarEspaciosEmblancosNumeros(
										ConstantesRim.Original_Document_Quantity,
										4, String.valueOf(itera.getCantidad())));

						if (itera.getDescuento() != null
								&& itera.getDescuento().compareTo(
										new BigDecimal("0.00")) > 0) {
							log.info("descuanto 11" + itera.getDescuento());
							BigDecimal pu = itera.getPrecioTotalSinImpuesto()
									.divide(itera.getCantidad(), 4,
											RoundingMode.CEILING);
							log.info("descuanto 22" + pu);
							map.put("Original_Unit_Cost",
									completarEspaciosEmblancosNumeros(
											ConstantesRim.Original_Unit_Cost,
											4, String.valueOf(pu.setScale(2,
													BigDecimal.ROUND_HALF_UP))));
						} else {
							map.put("Original_Unit_Cost",
									completarEspaciosEmblancosNumeros(
											ConstantesRim.Original_Unit_Cost,
											4,
											String.valueOf(itera
													.getPrecioUnitario()
													.setScale(
															2,
															BigDecimal.ROUND_HALF_UP))));
						}
						// map.put("Original_Unit_Cost",
						// completarEspaciosEmblancosNumeros(ConstantesRim.Original_Unit_Cost,
						// 4, String
						// .valueOf(itera.getPrecioUnitario().setScale(2,
						// BigDecimal.ROUND_HALF_UP))));

						totalUnidades = totalUnidades.add(itera.getCantidad());
						if (itera.getImpuestos().getImpuesto() != null
								&& !itera.getImpuestos().getImpuesto()
										.isEmpty()) {
							for (Impuesto item : itera.getImpuestos()
									.getImpuesto()) {
								if (item.getCodigoPorcentaje().equals("0")) {
									map.put("Original_VAT_Code",
											completarEspaciosEmblancosLetras(
													ConstantesRim.Original_VAT_Code,
													"L".length(), "L"));
									map.put("Original_VAT_rate",
											completarEspaciosEmblancosNumeros(
													ConstantesRim.Original_VAT_rate,
													10, String.valueOf(item
															.getTarifa())));
								}else {
									if (item.getCodigoPorcentaje().equals("2") || item.getCodigoPorcentaje().equals("3")) {
										map.put("VAT_code",
												completarEspaciosEmblancosLetras(
														ConstantesRim.VAT_code,
														"E".length(), "E"));

										map.put("VAT_rate",
												completarEspaciosEmblancosNumeros(
														ConstantesRim.VAT_rate,
														ConstantesRim.Decimales10,									
														String.valueOf("12")));
										
										if(item.getCodigoPorcentaje().equals("3")){
											map.put("VAT_rate",
													completarEspaciosEmblancosNumeros(
															ConstantesRim.VAT_rate,
															ConstantesRim.Decimales10,
															String.valueOf("14")));
										}
										
										
									} else {
										log.info("La factura tiene codigo de impuesto  no contemplado en reim"
												+ documentoXML.getInfoTributaria()
														.getClaveAcceso());
										respuesta
												.setClaveAccesoComprobante(documentoXML
														.getInfoTributaria()
														.getClaveAcceso());
										respuesta
												.setMensajeCliente("Ha ocurrido un error en el proceso");
										respuesta
												.setMensajeSistema("La factura tiene codigo de impuesto  no contemplado en reim");
										respuesta.setEstado(Estado.COMPLETADO
												.getDescripcion());
										documentoObj.setEstado(Estado.ERROR
												.getDescripcion());
										documentoObj
												.setMensajeErrorReim("La factura tiene codigo de impuesto  no contemplado en reim");
										documentoObj = this.servicioNotaCredito
												.recibirNotaCredito(
														documentoObj,
														new StringBuilder()
																.append("La factura tiene codigo de impuesto  no contemplado en reim")
																.append(ordenCompra)
																.toString());
										return respuesta;

									}
								}
							}
						}
						map.put("Total_Allowance",
								completarEspaciosEmblancosNumeros(
										ConstantesRim.Total_Allowance,
										ConstantesRim.Decimales4, "0"));
						list.add(map);
					}
				}
				ArrayList listTotalImpuesto = new ArrayList();
				BigDecimal totalImpuesto = new BigDecimal(0);
				if (documentoXML.getInfoNotaCredito().getTotalConImpuestos()
						.getTotalImpuesto() != null
						&& !documentoXML.getInfoNotaCredito()
								.getTotalConImpuestos().getTotalImpuesto()
								.isEmpty()) {
					for (TotalImpuesto impu : documentoXML.getInfoNotaCredito()
							.getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							num++;
							Map map = new HashMap();
							map.put("TVATS", "TVATS");
							String linea = completar(String.valueOf(num)
									.length(), num);
							map.put("linea", linea);
							if (impu.getCodigoPorcentaje().equals("0")) {
								map.put("VAT_code",
										completarEspaciosEmblancosLetras(
												ConstantesRim.VAT_code,
												"L".length(), "L"));

								map.put("VAT_rate",
										completarEspaciosEmblancosNumeros(
												ConstantesRim.VAT_rate,
												ConstantesRim.Decimales10,
												String.valueOf("0")));
							} else {
								if (impu.getCodigoPorcentaje().equals("2")) {
									map.put("VAT_code",
											completarEspaciosEmblancosLetras(
													ConstantesRim.VAT_code,
													"E".length(), "E"));

									map.put("VAT_rate",
											completarEspaciosEmblancosNumeros(
													ConstantesRim.VAT_rate,
													ConstantesRim.Decimales10,
													String.valueOf("12")));
								} else {
									log.info("La nota credito tiene codigo de impuesto  no contemplado en reim"
											+ documentoXML.getInfoTributaria()
													.getClaveAcceso());
									respuesta
											.setClaveAccesoComprobante(documentoXML
													.getInfoTributaria()
													.getClaveAcceso());
									respuesta
											.setMensajeCliente("Ha ocurrido un error en el proceso");
									respuesta
											.setMensajeSistema("La nota credito tiene codigo de impuesto no contemplado en reim");
									respuesta.setEstado(Estado.COMPLETADO
											.getDescripcion());
									documentoObj.setEstado(Estado.ERROR
											.getDescripcion());
									documentoObj
											.setMensajeErrorReim("La nota credito tiene codigo de impuesto no contemplado en reim");
									documentoObj = this.servicioNotaCredito
											.recibirNotaCredito(documentoObj,
													"La factura tiene codigo de impuesto no contemplado en reim.");
									return respuesta;

								}
							}
							// map.put("VAT_rate",
							// completarEspaciosEmblancosNumeros(
							// ConstantesRim.VAT_rate,
							// ConstantesRim.Decimales10,
							// String.valueOf(impu
							// .getCodigoPorcentaje())));
							map.put("Cost_VAT_code",
									completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code,
											ConstantesRim.Decimales4,
											String.valueOf(impu
													.getBaseImponible())));
							totalImpuesto = totalImpuesto.add(impu.getValor());
							listTotalImpuesto.add(map);
						}

					}
				}
				int fdetalle = num + 1;
				int fin = num + 2;
				int numLinea = num - 2;
				int totalLinea = num;
				Parametro dirParametro2 = cacheBean.consultarParametro(
						Constantes.DIR_RIM_ESQUEMA, emisor.getId());
				String dirServidor2 = dirParametro2 != null ? dirParametro2
						.getValor() : "";
				Velocity.setProperty(
						RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
						dirServidor2);
				Velocity.setProperty(
						RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
				Velocity.init();
				Template t = Velocity.getTemplate("traductorNotaCredito.vm");
				VelocityContext context = new VelocityContext();
				context.put("fecha", strDate);
				context.put("petList", list);
				
				// THEAD
				context.put(
						"Document_Type",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Document_Type, "CRDNT".length(),
								"CRDNT"));

				String vendorDocument = new StringBuilder()
						.append(documentoXML.getInfoTributaria().getEstab())
						.append("")
						.append(documentoXML.getInfoTributaria().getPtoEmi())
						.append("")
						.append(documentoXML.getInfoTributaria()
								.getSecuencial()).toString();
				context.put(
						"Vendor_Document_Number",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Vendor_Document_Number,
								vendorDocument.length(), vendorDocument));
				context.put(
						"Group_ID",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Group_ID, "".length(), ""));
				context.put(
						"Vendor_Type",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Vendor_Type, ceDisresponse
										.getVendor_type().length(),
								ceDisresponse.getVendor_type()));

				context.put(
						"Vendor_ID",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Vendor_ID, ceDisresponse
										.getVendor_id().length(), ceDisresponse
										.getVendor_id()));

				Date fechaEmision = sdfDate2.parse(documentoXML
						.getInfoNotaCredito().getFechaEmision());
				context.put(
						"Vendor_Document_Date",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Vendor_Document_Date, sdfDate
										.format(fechaEmision).length(), sdfDate
										.format(fechaEmision)));

				context.put(
						"Order_Number",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Order_Number, 0,
								String.valueOf(ceDisresponse.getOrder_number())));
				if ((ceDisresponse.getLocation() != null)
						&& (!ceDisresponse.getLocation().isEmpty()))
					context.put(
							"Location",
							completarEspaciosEmblancosNumeros(
									ConstantesRim.Location, 0,
									ceDisresponse.getLocation()));
				else {
					context.put(
							"Location",
							completarEspaciosEmblancosNumeros(
									ConstantesRim.Location, 0, ""));
				}

				if ((ceDisresponse.getLocation() != null)
						&& (!ceDisresponse.getLocation().isEmpty()))
					context.put(
							"Location_Type",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Location_Type, ceDisresponse
											.getLocation().length(),
									ceDisresponse.getLocation_type()));
				else {
					context.put(
							"Location_Type",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Location_Type, "".length(),
									""));
				}
				context.put(
						"Terms",
						completarEspaciosEmblancosLetras(ConstantesRim.Terms,
								"".length(), ""));
				context.put(
						"Due_Date",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Due_Date, "".length(), ""));
				context.put(
						"Payment_method",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Payment_method, "".length(), ""));
				context.put(
						"Currency_code",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Currency_code, "USD".length(),
								"USD"));

				context.put(
						"Exchange_rate",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Exchange_rate, 4,
								String.valueOf("1")));

				context.put(
						"Total_Cost",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Total_Cost,
								4,
								String.valueOf(documentoXML
										.getInfoNotaCredito()
										.getTotalSinImpuestos()
										.setScale(2, BigDecimal.ROUND_HALF_UP))));

				context.put(
						"Total_VAT_Amount",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Total_VAT_Amount, 4, String
										.valueOf(totalImpuesto.setScale(2,
												BigDecimal.ROUND_HALF_UP))));

				context.put(
						"Total_Quantity",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Total_Quantity, 4,
								String.valueOf(totalUnidades)));

				context.put(
						"Total_Discount",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Total_Discount, 4,
								String.valueOf("0")));

				context.put(
						"Freight_Type",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Freight_Type, "".length(), ""));
				context.put("Paid_Ind", "N");
				context.put("Multi_Location", "N");
				context.put("Merchan_dise_Type", "N");
				context.put(
						"Deal_Id",
						completarEspaciosEmblancosLetras(ConstantesRim.Deal_Id,
								"".length(), ""));
				context.put(
						"Deal_Approval_Indicator",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Deal_Approval_Indicator,
								"".length(), ""));

				if ((idDevolucion != null) && (!idDevolucion.isEmpty()))
					context.put("RTV_indicator", "Y");
				else {
					context.put("RTV_indicator", "N");
				}

				context.put(
						"Custom_Document_Reference_1",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Custom_Document_Reference_1,
								autorizacionXML.getNumeroAutorizacion()
										.length(), autorizacionXML
										.getNumeroAutorizacion()));

				String fechaAutorizacion = FechaUtil.formatearFecha(
						autorizacionXML.getFechaAutorizacion()
								.toGregorianCalendar().getTime(),
						"dd/MM/yyyy HH:mm");

				context.put(
						"Custom_Document_Reference_2",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Custom_Document_Reference_2,
								fechaAutorizacion.length(), fechaAutorizacion));
				if (esDevolucion)
					context.put(
							"Custom_Document_Reference_3",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Custom_Document_Reference_3,
									emisor.getCeDisVirtual().length(),
									emisor.getCeDisVirtual()));
				else {
					context.put(
							"Custom_Document_Reference_3",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Custom_Document_Reference_3,
									emisor.getCeDisVirtual().length(),
									emisor.getCeDisVirtual()));
				}

				String numModificadostr = documentoXML.getInfoNotaCredito()
						.getNumDocModificado();

				if (numModificadostr.contains("-")) {
					String parametro = numModificadostr.replace("-", "");

					context.put(
							"Custom_Document_Reference_4",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Custom_Document_Reference_4,
									parametro.length(), parametro));
				} else {
					context.put(
							"Custom_Document_Reference_4",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Custom_Document_Reference_4,
									numModificadostr.length(), numModificadostr));
				}

				context.put(
						"Cross_reference",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Cross_reference, "".length(), ""));

				context.put("petListTotalImpuesto", listTotalImpuesto);
				context.put("secuencial", documentoXML.getInfoTributaria()
						.getSecuencial());

				context.put("finT",
						completar(String.valueOf(fdetalle).length(), fdetalle));
				context.put("finF",
						completar(String.valueOf(fin).length(), fin));
				context.put(
						"numerolineaDestalle",
						completarLineaDetalle(
								String.valueOf(numLinea).length(), numLinea));
				context.put(
						"totalLinea",
						completar(String.valueOf(totalLinea).length(),
								totalLinea));

				StringWriter writer = new StringWriter();
				t.merge(context, writer);
				Calendar now2 = Calendar.getInstance();

				Parametro repositorioRim = this.cacheBean.consultarParametro(
						"RIM_REPOSITO_ARCHIVO", emisor.getId());

				String stvrepositorioRim = repositorioRim != null ? repositorioRim
						.getValor() : "";

				Random rng = new Random();
				int dig5 = rng.nextInt(90000) + 10000; // siempre 5 digitos

				String nombreArchivo = new StringBuilder()
						.append("edidlinv_")
						.append(FechaUtil.formatearFecha(now2.getTime(),
								"yyyyMMddHHmmssSSS").toString()).append(dig5)
						.toString();
				String archivoFinal = DocumentUtil.createDocumentText(writer
						.toString().getBytes(), nombreArchivo, FechaUtil
						.formatearFecha(now2.getTime(), "yyyyMMdd").toString(),
						stvrepositorioRim);
				documentoObj.setFechaEntReim(new Date());
				documentoObj.setArchivoGenReim(archivoFinal);

			}

			// fin agregado rim

			documentoObj.setArchivo(mensaje.getComprobanteProveedor());
			documentoObj.setNumeroAutorizacion(autorizacionXML
					.getNumeroAutorizacion());
			documentoObj.setFechaAutorizacion(autorizacionXML
					.getFechaAutorizacion().toGregorianCalendar().getTime());
			documentoObj.setEstado("AUTORIZADO");
			documentoObj.setTareaActual(Tarea.AUT);
			documentoObj.setTipoGeneracion(TipoGeneracion.REC);

			// Organizacion emisor = emisores.get(0);
			Parametro dirParametro = cacheBean.consultarParametro(
					Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
			String dirServidor = dirParametro != null ? dirParametro.getValor()
					: "";
			if ((mensaje.getComprobanteProveedorPdf() == null)
					|| (mensaje.getComprobanteProveedorPdf().isEmpty())) {
				try {
					ReporteUtil reporte = new ReporteUtil();
					String rutaArchivoAutorizacionPdf = reporte
							.generarReporteCreditoReim(
									"/gnvoice/recursos/reportes/notaCreditoFinal.jasper",
									new NotaCreditoReporte(documentoXML),
									autorizacionXML.getNumeroAutorizacion(),
									FechaUtil.formatearFecha(autorizacionXML
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											"yyyy/MM/dd HH:mm"),
									documentoXML.getInfoNotaCredito()
											.getFechaEmision(),
									"recibidoReim",
									false,
									new StringBuilder()
											.append(dirServidor)
											.append("/gnvoice/recursos/reportes/Blanco.png")
											.toString());

					documentoObj.setArchivoLegible(rutaArchivoAutorizacionPdf);
				} catch (Exception ex) {
					log.warn(ex.getMessage(), ex);
				}
			}
			documentoObj = servicioNotaCredito.recibirNotaCredito(documentoObj,
					"El comprobante se a cargado de forma exitosa");
			respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
			respuesta
					.setMensajeCliente("El comprobante se a cargado de forma exitosa");
			respuesta
					.setMensajeSistema("El comprobante se a cargado de forma exitosa");

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

	public String completarEspaciosEmblancosNumeros(int numEspacios,
			int numDecimales, String valor) {
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

	public String completarEspaciosEmblancosLetras(int numEspacios,
			int numOcupados, String valor) {
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

	private Autorizacion getAutorizacionXML(String comprobanteXML)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (Autorizacion) converter.convertirAObjeto(comprobanteXML,
				Autorizacion.class);
	}
	
	public boolean validaEAN(String codigo){
		boolean retorno = false;	
//		if (codigo.matches("[0-9]*")) {
//			return retorno;
//		}
		if(codigo != null && !codigo.isEmpty() && (codigo.length() == 8
				|| codigo.length() == 12
					|| codigo.length() == 13
						|| codigo.length() == 14)){
		int factor = 3;
		int sum = 0;
		
		for (int index = codigo.length()-1; index > 0; --index) {
			int mult = Integer.valueOf(codigo.substring(index-1, index));
			sum = sum + mult * factor;
			factor = 4 - factor;
		}

		int cc = ((1000 - sum) % 10);
		int ca = Integer.valueOf(codigo.substring(codigo.length()-1));

		if (cc == ca) {
			log.info("valido");
			retorno = true;
			return retorno;
		}else{
			log.info("no valido");
			retorno = false;
			return retorno;
		}
	}
		return retorno;
	}
	
	public static void main(String[] args) {
		String oc="2345.";		
		System.out.println(oc.replace(".", ""));
	}

}
