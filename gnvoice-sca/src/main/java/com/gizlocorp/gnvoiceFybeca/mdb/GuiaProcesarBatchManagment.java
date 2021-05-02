package com.gizlocorp.gnvoiceFybeca.mdb;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.MailDelivery;
import com.gizlocorp.adm.utilitario.MailMessage;
import com.gizlocorp.adm.utilitario.StringUtil;
import com.gizlocorp.adm.utilitario.TripleDESUtil;
import com.gizlocorp.gnvoice.dao.GuiaRemisionDAO;
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
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.reporte.GuiaRemisionReporte;
import com.gizlocorp.gnvoice.service.impl.CacheBean;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.guia.Destinatario;
import com.gizlocorp.gnvoice.xml.guia.Detalle;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision.Destinatarios;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision.InfoGuiaRemision;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoiceFybeca.enumeration.ComprobanteEnum;
import com.gizlocorp.gnvoiceFybeca.enumeration.EstadoEnum;
import com.gizlocorp.gnvoiceFybeca.enumeration.GeneradoEnum;
import com.gizlocorp.gnvoiceFybeca.enumeration.ProcesoEnum;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.modelo.Respuesta;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;

import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;

/**
 * Message-Driven Bean implementation class for: MensajeManagment
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/guiaProcesarBatchQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "700"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "45000") })
@DependsOn("CacheBean")
public class GuiaProcesarBatchManagment implements MessageListener {

	@EJB(lookup = "java:global/gnvoice-ejb/GuiaRemisionDAOImpl!com.gizlocorp.gnvoice.dao.GuiaRemisionDAO")
	com.gizlocorp.gnvoice.dao.GuiaRemisionDAO guiaRemisionDAO;
	@EJB
	CacheBean cacheBean;

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;
	private static Logger log = Logger.getLogger(GuiaProcesarBatchManagment.class.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onMessage(final Message message) {

		ObjectMessage objectMessage = null;
		if (!(message instanceof ObjectMessage)) {
			log.debug("Mensaje recuperado no es instancia de ObjectMessage, se desechara "+ message);
			return;
		}
		objectMessage = (ObjectMessage) message;
		try {
			if ((objectMessage == null) || !(objectMessage.getObject() instanceof String)) {
				log.debug("El objeto seteado en el mensaje no es de tipo String, se desechara "+ message);
				return;
			}

			String mensaje = (String) objectMessage.getObject();
			if (mensaje == null || mensaje.isEmpty()) {
				log.error("El mensaje es vacio o nulo");
				return;
			}

			String[] parametros = mensaje.split("&");

			if (parametros.length != 2) {
				log.error("Parametros incompletos: " + mensaje);
				return;
			}

			Long farmacia = Long.parseLong(parametros[0]);
			String sid = parametros[1];

			CredencialDS credencialDS = new CredencialDS();
			credencialDS.setDatabaseId(sid);
			credencialDS.setUsuario("WEBLINK");
			credencialDS.setClave("weblink_2013");

			Connection conn = null;
			Long tipo = null;
			List<Long> listado = null;

			try {
				conn = Conexion.obtenerConexionFybeca(credencialDS);
				tipo = obtenerTipo(conn,ComprobanteEnum.GUIA_REMISION.getDescripcion());
				listado = listarDocumentos(conn, farmacia, tipo);

			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}

			if (listado != null && !listado.isEmpty()) {
				log.debug("GuiaS A PROCESAR (SIN PROCESAR): Listado de Guias es: "
						+ listado.size()
						+ (listado.size() > 1000 ? "*******************************************************************************************************************************"
								: "")
						+ ", Farmacia: "
						+ farmacia
						+ ", tipo: "
						+ tipo);

				GuiaProcesarRequest item = null;
				GuiaProcesarResponse response = null;
				Respuesta respuesta = null;

				for (Long documentoVenta : listado) {
					try {
						conn = Conexion.obtenerConexionFybeca(credencialDS);
						if (tipo == null) {
							tipo = obtenerTipo(conn,ComprobanteEnum.GUIA_REMISION.getDescripcion());
						}

						if (conn == null || conn.isClosed()) {
							log.info("************************** CONEXION SE CERRO");
							conn = Conexion.obtenerConexionFybeca(credencialDS);
						}
						if (conn != null && !conn.isClosed()) {
							item = listar(conn, documentoVenta, farmacia, tipo);

							if (item != null) {
								try {

									item.setSid(sid);
									response = procesarOffline(item);

									log.debug("********* Farmacia: " + farmacia
											+ " - documento: " + documentoVenta
											+ " tipo: " + tipo + " Respuesta:"
											+ response.getEstado() + " "
											+ response.getMensajeCliente()
											+ " "
											+ response.getMensajeSistema());

									respuesta = new Respuesta();

									respuesta.setAutFirmaE(response.getNumeroAutorizacion());
									respuesta.setClaveAcceso(response.getClaveAccesoComprobante());
									respuesta.setComprobanteFirmaE(response.getNumeroAutorizacion());
									respuesta.setCorreoElectronico(item.getCorreoElectronicoNotificacion());


									if (response.getEstado().equals(Estado.PENDIENTE.getDescripcion())
											|| response.getEstado().equals(Estado.COMPLETADO.getDescripcion())
											|| response.getEstado().equals(Estado.RECIBIDA.getDescripcion())) {
										respuesta.setEstado(EstadoEnum.PENDIENTE.getDescripcion());
									}
									if (response.getEstado().equals(Estado.AUTORIZADO.getDescripcion())) {
										respuesta.setEstado(EstadoEnum.APROBADO.getDescripcion());
									}
									if (response.getEstado().equals(
											Estado.CANCELADO.getDescripcion())
											|| response.getEstado().equals(Estado.ERROR.getDescripcion())
											|| response.getEstado().equals(Estado.DEVUELTA.getDescripcion())
											|| response.getEstado().equals(Estado.RECHAZADO.getDescripcion())) {
										respuesta.setEstado(EstadoEnum.RECHAZADO.getDescripcion());
									}

									respuesta.setFecha(Calendar.getInstance().getTime());
									respuesta.setFechaFirmaE(response
													.getFechaAutorizacion() != null ? response
													.getFechaAutorizacion()
													: null);

									if (response.getEstado().equals(EstadoEnum.APROBADO.getDescripcion())) {
										respuesta.setFirmaE(GeneradoEnum.SI.getDescripcion());
									} else {
										respuesta.setFirmaE(GeneradoEnum.NO.getDescripcion());
									}

									respuesta.setIdDocumento(documentoVenta);
									respuesta.setIdFarmacia(farmacia);
									respuesta.setObservacion(response.getEstado()
											+ " "
											+ response.getMensajeSistema());
									respuesta.setTipoComprobante(tipo);
									respuesta.setTipoProceso(ProcesoEnum.BATCH.getDescripcion());
									respuesta.setUsuario("");
									respuesta.setSid(sid);

									// inqueue(respuesta);

									if (respuesta.getObservacion() != null && !respuesta.getObservacion().isEmpty()) {
										try {
											respuesta.setObservacion(respuesta
													.getObservacion()
													.replaceAll("[^\\x00-\\x7F]",""));
										} catch (Exception ex) {
											log.info("mensaje uncode no valida");
										}
									}

									if (conn == null || conn.isClosed()) {
										conn = Conexion.obtenerConexionFybeca(credencialDS);

									}

									if (conn != null && !conn.isClosed()) {
										if (consultar(conn,
												respuesta.getIdFarmacia(),
												respuesta.getIdDocumento(),
												null,
												respuesta.getTipoComprobante()) == null) {
											insertarRespuesta(conn, respuesta);
										} else {
											actualizarRespuesta(conn, respuesta);
										}
									}

								} catch (Exception e) {
									log.error("Mensaje fallido", e);

								}
							}
						}

					} catch (Exception e) {
						log.error("Error proceso BATCH: " + farmacia + "  "
								+ sid + e.getMessage(), e);
					} finally {
						try {
							if (conn != null)
								conn.close();
						} catch (Exception ex) {
							log.error(
									"Error cerrar conexion: " + ex.getMessage(),
									ex);
						}
					}
				}
			}

			// }

		} catch (Exception ex) {
			log.error("Mensaje fallido: " + message.toString(), ex);
		}
	}
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public GuiaProcesarResponse procesarOffline(GuiaProcesarRequest mensaje) {
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

			log.info("Inicia validaciones previas metodo offline");
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
			ambiente = ambParametro.getValor();

			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
            wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

			com.gizlocorp.gnvoice.modelo.GuiaRemision comprobanteExistente = null;
			
			try {
				comprobanteExistente = guiaRemisionDAO.obtenerComprobante(null,mensaje.getCodigoExterno(), mensaje.getGuia().getInfoTributaria().getRuc(),mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				guiaRemisionDAO = (GuiaRemisionDAO) ic.lookup("java:global/gnvoice-ejb/GuiaRemisionDAOImpl!com.gizlocorp.gnvoice.dao.GuiaRemisionDAO");
				comprobanteExistente = guiaRemisionDAO.obtenerComprobante(null,mensaje.getCodigoExterno(), mensaje.getGuia().getInfoTributaria().getRuc(),mensaje.getAgencia());

			}

			if (comprobanteExistente != null && Estado.AUTORIZADO.getDescripcion().equals(comprobanteExistente.getEstado())) {
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

			mensaje.getGuia().getInfoTributaria()
							.setDirMatriz(dirMatriz != null ? StringEscapeUtils
							.escapeXml(dirMatriz.trim())
							.replaceAll("[\n]", "")
							.replaceAll("[\b]", "") : null);

			if (mensaje.getGuia().getInfoTributaria().getEstab() == null|| mensaje.getGuia().getInfoTributaria().getEstab().isEmpty()) {
				mensaje.getGuia().getInfoTributaria().setEstab(emisor.getEstablecimiento());
			}
			
			mensaje.getGuia()
					.getInfoTributaria()
					.setNombreComercial(
							emisor.getNombreComercial() != null ? StringEscapeUtils
									.escapeXml(emisor.getNombreComercial().trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "")
									: null);

			if (mensaje.getGuia().getInfoTributaria().getPtoEmi() == null|| mensaje.getGuia().getInfoTributaria().getPtoEmi().isEmpty()) {
				mensaje.getGuia().getInfoTributaria().setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getGuia().getInfoTributaria().setRazonSocial(emisor.getNombre() != null ? StringEscapeUtils.escapeXml(emisor.getNombre()) : null);

			mensaje.getGuia().getInfoGuiaRemision().setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			
			if (mensaje.getGuia().getInfoGuiaRemision().getDirEstablecimiento() == null || mensaje.getGuia().getInfoGuiaRemision().getDirEstablecimiento().isEmpty()) {
				mensaje.getGuia()
						.getInfoGuiaRemision()
						.setDirEstablecimiento(emisor.getDirEstablecimiento() != null ? StringEscapeUtils.escapeXml(emisor.getDirEstablecimiento()): null);
			}
			mensaje.getGuia()
					.getInfoGuiaRemision()
					.setObligadoContabilidad(emisor.getEsObligadoContabilidad().getDescripcion().toUpperCase());

			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getEstab());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getPtoEmi());
			log.debug("codigo numerico - secuencial de factura: "+ mensaje.getGuia().getInfoTributaria().getSecuencial());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria().getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);

			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje.getGuia().getInfoGuiaRemision().getFechaIniTransporte()
																     ,codFacParametro.getValor()
																     ,mensaje.getGuia().getInfoTributaria().getRuc()
																     , mensaje.getGuia().getInfoTributaria().getAmbiente()
																     ,codigoNumerico.toString());
			mensaje.getGuia().getInfoTributaria().setClaveAcceso(claveAcceso);
			
			log.debug("finaliza validaciones previas");
			log.debug("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil.convertirEsquemaAEntidadGuiaRemision(mensaje.getGuia());

			comprobante.setClaveInterna(mensaje.getCodigoExterno());
			comprobante.setCorreoNotificacion(mensaje.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getAgencia());
		

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			
			if (comprobanteExistente != null && comprobanteExistente.getId() != null) {
				comprobante.setId(comprobanteExistente.getId());
				comprobante.setEstado(comprobanteExistente.getEstado());
				comprobante.setClaveInterna(comprobanteExistente.getClaveInterna());
			}
			
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(wsdlLocationAutorizacion, claveAcceso);



			if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {
				
                respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

                respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
                respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null? 
                							   respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
                							   : null);
                respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
                respuesta.setClaveAccesoComprobante(claveAcceso);

                respuesta.setMensajeSistema("Comprobante AUTORIZADO");
                respuesta.setMensajeCliente("Comprobante AUTORIZADO");

                comprobante.setNumeroAutorizacion(claveAcceso);
                comprobante.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null ? 
                								 respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
                								 : null);
				try {
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
					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
					try {

						rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getGuiaXML(respAutorizacionComExt)
																				,mensaje.getGuia().getInfoGuiaRemision().getFechaIniTransporte()
																				,claveAcceso
																				,TipoComprobante.FACTURA.getDescripcion()
																				,"autorizado");

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}
					
					comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
					respuesta.setClaveAccesoComprobante(claveAcceso);
					respuesta.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion()
									.toGregorianCalendar().getTime()
									: null);
					

					try {
						String comprobantexml = DocumentUtil.readContentFile(rutaArchivoFirmadoXml);
						com.gizlocorp.gnvoice.xml.guia.GuiaRemision facturaXML = getGuiaXML(comprobantexml);
						ReporteUtil reporte = new ReporteUtil();
						rutaArchivoAutorizacionPdf = reporte.generarReporte(Constantes.REP_GUIA_REMISION
										       , new GuiaRemisionReporte(facturaXML)
										       ,claveAcceso
										       ,FechaUtil.formatearFecha(new java.util.Date(),FechaUtil.patronFechaTiempo24)
										       ,facturaXML
										       ,facturaXML.getInfoGuiaRemision().getFechaIniTransporte()
										       ,"autorizado"
										       ,false
										       ,emisor.getLogoEmpresa());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					
					comprobante.setArchivo(rutaArchivoAutorizacionXml);
					comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);

					autorizaOffline(comprobante);

					return respuesta;
				} catch (Exception ex) {
					log.warn("Validar Autorizacion");
				}
			}else if ("PROCESAMIENTO".equals(respAutorizacionComExt.getEstado()) || "EN PROCESO".equals(respAutorizacionComExt.getEstado())) {
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

			// Genera comprobante
			rutaArchivoXml = DocumentUtil.createDocument(getGuiaXML(mensaje.getGuia())
														,mensaje.getGuia().getInfoGuiaRemision().getFechaIniTransporte()
														,claveAcceso
														,com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA.getDescripcion()
														,"enviado");
			
			log.debug("ruta de archivo es: " + rutaArchivoXml);

			String validacionXsd = ComprobanteUtil.validaArchivoXSD(
					rutaArchivoXml,com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+ "/gnvoice/recursos/xsd/guiaRemision.xsd");

			if (validacionXsd != null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Error en esquema comprobante. "+ validacionXsd);
				respuesta.setMensajeCliente("Error en la estructura del comprobante enviado.");
				// Actualiza estado comprobante
				log.debug("comprobante: " + comprobante + " archivo: "+ rutaArchivoXml);
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
				
                respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
                respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
                respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null? 
                							   respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
                							   : null);
                respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
                respuesta.setClaveAccesoComprobante(claveAcceso);
                respuesta.setMensajeSistema("Comprobante AUTORIZADO");
                respuesta.setMensajeCliente("Comprobante AUTORIZADO");
                comprobante.setNumeroAutorizacion(claveAcceso);
                comprobante.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null ? 
                								 respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
                								 : null);
				try {
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
					
					respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
					comprobante.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
					respuesta.setClaveAccesoComprobante(claveAcceso);
					respuesta.setFechaAutorizacion(respAutorizacionComExt
									.getFechaAutorizacion() != null ? respAutorizacionComExt
									.getFechaAutorizacion()
									.toGregorianCalendar().getTime()
									: null);
					comprobante.setArchivo(rutaArchivoAutorizacionXml);
					comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
					autorizaOffline(comprobante);
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
	
	private ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud enviarComprobanteOffline(String wsdlLocation,String rutaArchivoXml) {
		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuesta = null;

		int retardo = 4000;

		try {
			byte[] documentFirmado = DocumentUtil.readFile(rutaArchivoXml);
			try {
                ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService servicioRecepcion = new ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService(
                        new URL(wsdlLocation));

                 ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion.getRecepcionComprobantesOfflinePort();
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
			respuesta = new ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud();
			respuesta.setEstado(ex.getMessage());
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud();
			respuesta.setEstado("ENVIO NULL");

		}
		return respuesta;
	}
	
	
	
	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,String claveAcceso) {
		
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;
		int timeout = 7000;

		try {
            ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion 
	            = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
	                    new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion","AutorizacionComprobantesOfflineService"));

	        ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion.getAutorizacionComprobantesOfflinePort();


			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);

			ec.gob.sri.comprobantes.ws.offline.aut.RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

			for (int i = 0; i < 2; i++) {
				if (respuestaComprobanteAut != null
						&& respuestaComprobanteAut.getAutorizaciones() != null
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
						&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {
					 	break;
				} else {
					respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);
				}
			}

			if (respuestaComprobanteAut != null
					&& respuestaComprobanteAut.getAutorizaciones() != null
					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
					&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {

				ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacion = respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0);

				for (ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion aut : respuestaComprobanteAut.getAutorizaciones().getAutorizacion()) {
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

					for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msj : autorizacion.getMensajes().getMensaje()) {
						msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils.escapeXml(msj.getMensaje()) : null);
						msj.setInformacionAdicional(msj.getInformacionAdicional() != null ? StringEscapeUtils.escapeXml(msj.getInformacionAdicional()): null);
					}
				}

				if ("AUTORIZADO".equals(autorizacion.getEstado())) {
					autorizacion.setMensajes(null);
				}

				return autorizacion;

			}

		} catch (Exception ex) {
			log.debug("Renvio por timeout");
			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
			respuesta.setEstado("ERROR");
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
			respuesta.setEstado("ERROR");

		}

		return respuesta;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public com.gizlocorp.gnvoice.modelo.GuiaRemision autorizaOffline(com.gizlocorp.gnvoice.modelo.GuiaRemision comprobante) {

		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = guiaRemisionDAO.update(comprobante);

			} else {
				comprobante = guiaRemisionDAO.persist(comprobante);

			}
		} catch (Exception ex) {
			try {
				InitialContext ic = new InitialContext();
				guiaRemisionDAO = (GuiaRemisionDAO) ic.lookup("java:global/gnvoice-ejb/GuiaRemisionDAOImpl!com.gizlocorp.gnvoice.dao.GuiaRemisionDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = guiaRemisionDAO.update(comprobante);

				} else {
					comprobante = guiaRemisionDAO.persist(comprobante);

				}
			} catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}

		return comprobante;
	}
	
	
	
	

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private Long obtenerTipo(Connection conn, String comprobante)
			throws GizloException {
		PreparedStatement ps = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select * from farmacias.fa_tipo_comprobante where descripcion = ?");
			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, comprobante);
			ResultSet set = ps.executeQuery();

			while (set.next()) {
				return set.getLong("codigo");
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return null;
	}

	public List<Long> listarDocumentos(Connection conn, Long farmacia, Long tipo)
			throws GizloException {

		PreparedStatement ps = null;
		ResultSet set = null;
		List<Long> facturas = new ArrayList<Long>();

		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append("select m.documento as codigo_externo ");
			sqlString.append("from fa_movimientos_mercaderia     m, ");
			sqlString.append("FA_OBSERVACIONES_MOVIMIENTO   o, ");
			sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
			sqlString.append("fa_movimientos_mercaderia_mas a, ");
			sqlString.append("fa_movimientos_mercaderia_adi mm, ");
			sqlString.append("ad_farmacias                  f, ");
			sqlString.append("ad_clasificacion_movimientos  cm, ");
			sqlString.append("fa_rutas_movmer               rm, ");
			sqlString.append("ab_personas                   p, ");
			sqlString.append("  ds_conductores_vehiculo       cv ");
			sqlString.append("where m.farmacia = O.farmacia(+) ");
			sqlString.append(" and m.documento = o.documento(+) ");
			sqlString.append("and fa_secs.documento_venta = m.documento ");
			sqlString.append("and fa_secs.farmacia = m.farmacia ");
			sqlString.append("and m.farmacia = a.farmacia(+) ");
			sqlString.append("and m.documento = a.documento(+) ");
			sqlString.append("and m.farmacia = mm.farmacia(+) ");
			sqlString.append("and m.documento = mm.documento(+) ");
			sqlString.append("and m.farmacia = f.codigo(+) ");
			sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
			sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
			sqlString.append("and m.farmacia = rm.farmacia(+) ");
			sqlString.append("and m.documento = rm.documento(+) ");
			sqlString.append("and p.codigo(+) = rm.num2 ");
			sqlString.append("and cv.persona(+) = rm.num2 ");
			sqlString.append("and cv.placa(+) = rm.car1 ");
			sqlString.append("and fa_secs.tipo_documento = ? ");
			sqlString
					.append("and m.tipo_movimiento || m.clasificacion_movimiento <> '0401' ");
			sqlString.append("and m.farmacia = ? ");
			sqlString
					.append("and m.fecha >= to_date('01/10/2014','dd/mm/yyyy') ");
			sqlString
					.append("and not exists (select documento from farmacias.FA_DATOS_SRI_ELECTRONICA ");
			sqlString
					.append("where farmacia = ? and TIPO_COMPROBANTE = ? and estado in ('A','C') and aut_firmae is not null ");
			sqlString
					.append("and FA_DATOS_SRI_ELECTRONICA.Documento = m.documento and FA_DATOS_SRI_ELECTRONICA.Farmacia = m.farmacia) ");
			sqlString.append("and rownum < 3001 ");

			sqlString
					.append("and (rm.secuencia = ( select max(secuencia) from fa_rutas_movmer q where m.documento = q.documento ");
			sqlString
					.append(" and q.farmacia = m.farmacia) or rm.secuencia is null ) ");

			sqlString.append("union ");
			sqlString.append("select m.documento as codigo_externo ");
			sqlString.append("from fa_movimientos_mercaderia     m, ");
			sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
			sqlString.append("   FA_OBSERVACIONES_MOVIMIENTO   o, ");
			sqlString.append("fa_movimientos_mercaderia_mas a, ");
			sqlString.append("fa_movimientos_mercaderia_adi mm, ");
			sqlString.append("ad_farmacias                  f, ");
			sqlString.append("fa_datos_transportista        t, ");
			sqlString.append("fa_datos_vehiculo             p, ");
			sqlString.append("ad_clasificacion_movimientos  cm ");
			sqlString.append("where m.farmacia = O.farmacia(+) ");
			sqlString.append("and m.documento = o.documento(+) ");
			sqlString.append("and m.farmacia = a.farmacia(+) ");
			sqlString.append("and m.documento = a.documento(+) ");
			sqlString.append("and m.farmacia = mm.farmacia(+) ");
			sqlString.append("and m.documento = mm.documento(+) ");
			sqlString.append("and m.farmacia = f.codigo(+) ");
			sqlString.append("and a.cp_num3 = t.id(+) ");
			sqlString.append("and t.id = p.id(+) ");
			sqlString.append("and fa_secs.documento_venta = m.documento ");
			sqlString.append("and fa_secs.farmacia = m.farmacia ");
			sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
			sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
			sqlString.append("and m.TIPO_MOVIMIENTO = '04' ");
			sqlString.append("AND m.CLASIFICACION_MOVIMIENTO = '01' ");
			sqlString.append("and fa_secs.tipo_documento = ? ");
			sqlString.append("and m.farmacia = ? ");
			sqlString
					.append("and m.fecha >= to_date('01/10/2014','dd/mm/yyyy') ");
			sqlString
					.append("and not exists (select documento from farmacias.FA_DATOS_SRI_ELECTRONICA ");
			sqlString
					.append("where farmacia = ? and TIPO_COMPROBANTE = ? and estado in ('A','C') and aut_firmae is not null ");
			sqlString
					.append("and FA_DATOS_SRI_ELECTRONICA.Documento = m.documento and FA_DATOS_SRI_ELECTRONICA.Farmacia = m.farmacia) ");
			sqlString.append("and rownum < 3001 ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, tipo);
			ps.setLong(2, farmacia);
			ps.setLong(3, farmacia);
			ps.setLong(4, tipo);
			ps.setLong(5, tipo);
			ps.setLong(6, farmacia);
			ps.setLong(7, farmacia);
			ps.setLong(8, tipo);

			set = ps.executeQuery();

			while (set.next()) {
				facturas.add(set.getLong("codigo_externo"));

			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return facturas;

	}

	public GuiaProcesarRequest listar(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException {

		PreparedStatement ps = null;

		GuiaProcesarRequest guiaFybeca = null;

		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select distinct fa_secs.secuencia as secuencial, mm.razon_social razonSocial, mm.razon_social nombreComercial, mm.ruc, ");
			sqlString.append("m.documento as codigo_externo, ");
			sqlString.append("m.guia_remision as numDocSustento, ");
			sqlString.append("substr(numero_ruc, 11, 3) as estab, ");
			sqlString.append("substr(m.guia_remision, 5, 3) as ptoEmi, ");
			sqlString.append("mm.direccion as dirEstablecimiento, ");
			sqlString
					.append("decode(rm.num2, null, 1002097, rm.num2) codigo_transportista, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_establecimiento, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_partida, ");
			sqlString
					.append("decode(rm.num2,null, 'CARLOS EDUARDO PAZMINO GAONA', ");
			sqlString
					.append("(p.primer_nombre||' '||p.segundo_nombre||' '||p.primer_apellido || ' ' || p.segundo_apellido)) razon_social_transportista, ");
			sqlString
					.append("decode(rm.num2,null,'C',p.tipo_identificacion) tipo_iden_transportista, ");
			sqlString
					.append("decode(rm.num2,null,'1714472873',p.identificacion) ruc_transportista, ");
			sqlString.append("'NO' obligado_contabilidad, ");
			sqlString.append("'NO' contribuyente_especial, ");
			sqlString.append("m.fecha fecha_inicio, ");
			sqlString.append("m.fecha fecha_fin, ");
			sqlString.append("decode(rm.car1,null,'POW-703',rm.car1) placa, ");
			sqlString.append("m.documento, m.farmacia, ");
			sqlString.append("m.clasificacion_movimiento, ");
			sqlString.append("m.tipo_movimiento ");
			sqlString.append("from fa_movimientos_mercaderia     m, ");
			sqlString.append("FA_OBSERVACIONES_MOVIMIENTO   o, ");
			sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
			sqlString.append("fa_movimientos_mercaderia_mas a, ");
			sqlString.append("fa_movimientos_mercaderia_adi mm, ");
			sqlString.append("ad_farmacias                  f, ");
			sqlString.append("ad_clasificacion_movimientos  cm, ");
			sqlString.append("fa_rutas_movmer               rm, ");
			sqlString.append("ab_personas                   p, ");
			sqlString.append("  ds_conductores_vehiculo       cv ");
			sqlString.append("where m.farmacia = O.farmacia(+) ");
			sqlString.append(" and m.documento = o.documento(+) ");
			sqlString.append("and fa_secs.documento_venta = m.documento ");
			sqlString.append("and fa_secs.farmacia = m.farmacia ");
			sqlString.append("and m.farmacia = a.farmacia(+) ");
			sqlString.append("and m.documento = a.documento(+) ");
			sqlString.append("and m.farmacia = mm.farmacia(+) ");
			sqlString.append("and m.documento = mm.documento(+) ");
			sqlString.append("and m.farmacia = f.codigo(+) ");
			sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
			sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
			sqlString.append("and m.farmacia = rm.farmacia(+) ");
			sqlString.append("and m.documento = rm.documento(+) ");
			sqlString.append("and p.codigo(+) = rm.num2 ");
			sqlString.append("and cv.persona(+) = rm.num2 ");
			sqlString.append("and cv.placa(+) = rm.car1 ");
			sqlString.append("and fa_secs.tipo_documento = ? ");
			sqlString
					.append("and m.tipo_movimiento || m.clasificacion_movimiento <> '0401' ");
			sqlString.append("and m.farmacia = ? ");
			sqlString.append("and m.documento = ? ");

			sqlString
					.append("and (rm.secuencia = ( select max(secuencia) from fa_rutas_movmer q where m.documento = q.documento ");
			sqlString
					.append(" and q.farmacia = m.farmacia) or rm.secuencia is null ) ");

			sqlString.append("union ");
			sqlString
					.append("select distinct fa_secs.secuencia as secuencial, mm.razon_social razonSocial, mm.razon_social nombreComercial, mm.ruc, ");
			sqlString.append("m.documento as codigo_externo, ");
			sqlString.append("m.guia_remision as numDocSustento, ");
			sqlString.append("substr(numero_ruc, 11, 3) as estab, ");
			sqlString.append("substr(m.guia_remision, 5, 3) as ptoEmi, ");
			sqlString.append("mm.direccion as dirEstablecimiento, ");
			sqlString.append("a.cp_num3 codigo_transportista, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_establecimiento, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_partida, ");
			sqlString
					.append("t.primer_nombre||' '||t.segundo_nombre||' '||t.primer_apellido ||' ' || t.segundo_apellido razon_social_transportista, ");
			sqlString.append("t.tipo_identificacion tipo_iden_transportista, ");
			sqlString.append("t.identificacion ruc_transportista, ");
			sqlString.append("'NO' obligado_contabilidad, ");
			sqlString.append("'NO' contribuyente_especial, ");
			sqlString.append("m.fecha fecha_inicio, ");
			sqlString.append("m.fecha fecha_fin, ");
			sqlString.append("p.placa, ");
			sqlString.append("m.documento, ");
			sqlString.append("m.farmacia, ");
			sqlString.append("m.clasificacion_movimiento, ");
			sqlString.append("m.tipo_movimiento ");
			sqlString.append("from fa_movimientos_mercaderia     m, ");
			sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
			sqlString.append("   FA_OBSERVACIONES_MOVIMIENTO   o, ");
			sqlString.append("fa_movimientos_mercaderia_mas a, ");
			sqlString.append("fa_movimientos_mercaderia_adi mm, ");
			sqlString.append("ad_farmacias                  f, ");
			sqlString.append("fa_datos_transportista        t, ");
			sqlString.append("fa_datos_vehiculo             p, ");
			sqlString.append("ad_clasificacion_movimientos  cm ");
			sqlString.append("where m.farmacia = O.farmacia(+) ");
			sqlString.append("and m.documento = o.documento(+) ");
			sqlString.append("and m.farmacia = a.farmacia(+) ");
			sqlString.append("and m.documento = a.documento(+) ");
			sqlString.append("and m.farmacia = mm.farmacia(+) ");
			sqlString.append("and m.documento = mm.documento(+) ");
			sqlString.append("and m.farmacia = f.codigo(+) ");
			sqlString.append("and a.cp_num3 = t.id(+) ");
			sqlString.append("and t.id = p.id(+) ");
			sqlString.append("and fa_secs.documento_venta = m.documento ");
			sqlString.append("and fa_secs.farmacia = m.farmacia ");
			sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
			sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
			sqlString.append("and m.TIPO_MOVIMIENTO = '04' ");
			sqlString.append("AND m.CLASIFICACION_MOVIMIENTO = '01' ");
			sqlString.append("and fa_secs.tipo_documento = ? ");
			sqlString.append("and m.farmacia = ? ");
			sqlString.append("and m.documento = ? ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, tipo);
			ps.setLong(2, farmacia);

			ps.setLong(3, documentoVenta);

			ps.setLong(4, tipo);
			ps.setLong(5, farmacia);
			ps.setLong(6, documentoVenta);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				guiaFybeca = null;
				try {
					guiaFybeca = obtenerGuia(conn, set, farmacia, tipo);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}
				if (guiaFybeca != null) {
					String correo = obtenerCorreo(conn,
							Long.valueOf(guiaFybeca.getCodigoExterno()),
							farmacia);

					guiaFybeca.setCorreoElectronicoNotificacion(StringUtil
							.validateEmail(correo));

					List<Destinatario> destinatarios = listarDestinatarios(
							conn, Long.valueOf(guiaFybeca.getCodigoExterno()),
							farmacia, tipo);// ,farmacia
					log.debug("**servicio listar destinatarios: "
							+ (destinatarios != null ? destinatarios.size() : 0));
					if (destinatarios != null && !destinatarios.isEmpty()) {
						for (Destinatario destinatario : destinatarios) {
							List<Detalle> detalles = listarDetallesDestinatario(
									conn,
									destinatario
											.getIdentificacionDestinatario(),
									Long.valueOf(guiaFybeca.getCodigoExterno()),
									farmacia);
							log.debug("**servicio listar total con detalles: "
									+ (detalles != null ? detalles.size() : 0));
							if (detalles != null && !detalles.isEmpty()) {
								destinatario
										.setDetalles(new Destinatario.Detalles());
								destinatario.getDetalles().getDetalle()
										.addAll(detalles);
							}
						}
						guiaFybeca.getGuia().setDestinatarios(
								new Destinatarios());
						guiaFybeca.getGuia().getDestinatarios()
								.getDestinatario().addAll(destinatarios);
					}

					break;
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return guiaFybeca;

	}

	private GuiaProcesarRequest obtenerGuia(Connection conn, ResultSet set,
			Long farmacia, Long tipo) throws SQLException, GizloException {

		GuiaProcesarRequest guiaProcesarRequest = null;
		log.debug("Guia Generar: RUC: " + set.getString("ruc"));
		String tipoIdentificacion = "";
		GuiaRemision guia = new GuiaRemision();

		// INFO TRIBUTARIA
		InfoTributaria infoTributaria = new InfoTributaria();
		// infoTributaria.setCodDoc("04");
		infoTributaria.setEstab(set.getString("estab"));
		infoTributaria.setPtoEmi(set.getString("ptoEmi"));
		infoTributaria.setRuc(set.getString("ruc"));

		infoTributaria.setSecuencial(set.getLong("secuencial") + "");

		guia.setInfoTributaria(infoTributaria);

		// INFO GUIA
		InfoGuiaRemision infoGuiaRemision = new InfoGuiaRemision();

		String dirEstablecimiento = set.getString("dir_establecimiento");
		dirEstablecimiento = StringUtil.validateInfoXML(dirEstablecimiento);

		infoGuiaRemision
				.setDirEstablecimiento(dirEstablecimiento != null ? StringEscapeUtils
						.escapeXml(dirEstablecimiento.trim())
						.replaceAll("[\n]", "").replaceAll("[\b]", "")
						: null);

		String dirPartida = set.getString("dir_partida");
		dirPartida = StringUtil.validateInfoXML(dirPartida);

		infoGuiaRemision.setDirPartida(dirPartida.trim());

		infoGuiaRemision
				.setFechaIniTransporte((set.getDate("fecha_inicio") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fecha_inicio").getTime()),
								FechaUtil.DATE_FORMAT) : null));
		infoGuiaRemision
				.setFechaFinTransporte((set.getDate("fecha_fin") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fecha_fin").getTime()),
								FechaUtil.DATE_FORMAT) : null));

		infoGuiaRemision.setPlaca(set.getString("placa"));

		String razonSocialComprador = set
				.getString("razon_social_transportista");
		razonSocialComprador = StringUtil.validateInfoXML(razonSocialComprador);

		infoGuiaRemision.setRazonSocialTransportista(razonSocialComprador
				.trim());
		infoGuiaRemision
				.setRucTransportista(set.getString("ruc_transportista"));

		if (set.getString("tipo_iden_transportista") != null
				&& !set.getString("tipo_iden_transportista").isEmpty()) {
			tipoIdentificacion = set.getString("tipo_iden_transportista");

			if (tipoIdentificacion != null && !tipoIdentificacion.isEmpty()) {

				if (tipoIdentificacion.equalsIgnoreCase("C")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("05");
				} else if (tipoIdentificacion.equalsIgnoreCase("R")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("04");
				} else if (tipoIdentificacion.equalsIgnoreCase("P")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("06");
				} else if (tipoIdentificacion.equalsIgnoreCase("N")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("07");
				}
			}

			if (infoGuiaRemision != null
					&& infoGuiaRemision.getTipoIdentificacionTransportista() != null
					&& infoGuiaRemision.getTipoIdentificacionTransportista()
							.equalsIgnoreCase("07")) {
				infoGuiaRemision
						.setTipoIdentificacionTransportista("9999999999999");
				infoGuiaRemision
						.setRazonSocialTransportista("CONSUMIDOR FINAL");
			} else {
				String identificacion = set
						.getString("tipo_iden_transportista");
				infoGuiaRemision.setTipoIdentificacionTransportista("06");

				if (StringUtil.esNumero(identificacion)) {
					if (identificacion.length() == 10) {
						infoGuiaRemision
								.setTipoIdentificacionTransportista("05");
					} else if (identificacion.length() == 13
							&& identificacion.substring(10).equals("001")) {
						infoGuiaRemision
								.setTipoIdentificacionTransportista("04");
					}
				}
			}
		} else {
			infoGuiaRemision
					.setTipoIdentificacionTransportista("9999999999999");
			infoGuiaRemision.setTipoIdentificacionTransportista("07");
			infoGuiaRemision.setRazonSocialTransportista("CONSUMIDOR FINAL");
		}

		guia.setInfoGuiaRemision(infoGuiaRemision);

		guiaProcesarRequest = new GuiaProcesarRequest();
		guiaProcesarRequest.setGuia(guia);
		guiaProcesarRequest
				.setCodigoExterno(set.getLong("codigo_externo") + "");
		guiaProcesarRequest.setIdentificadorUsuario(null);
		guiaProcesarRequest.setAgencia(farmacia.toString());
		return guiaProcesarRequest;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private String obtenerIdentificacion(Connection conn, String identificacion)
			throws GizloException {

		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select ap.tipo_identificacion as tipoIdentificacion from ab_personas ap where ap.identificacion = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, identificacion);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("tipoIdentificacion");
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return resultado;
	}

	private List<Destinatario> listarDestinatarios(Connection conn,
			Long documentoVenta, Long farmacia, Long tipo)
			throws GizloException {
		try {
			PreparedStatement ps = null;
			List<Destinatario> destinatarios = new ArrayList<Destinatario>();
			Destinatario destinatarioXML = null;

			try {
				StringBuilder sqlString = new StringBuilder();

				sqlString
						.append("select mm.ruc identificacion_dstino, mm.razon_social razon_social_dstino, ");
				sqlString
						.append("(select calle || ' ' || numero || ' ' || interseccion from ad_bodegas where codigo = m.bodega) dir_destino, ");
				sqlString.append("cm.descripcion motivo_traslado, ");
				sqlString
						.append("(select substr(numero_ruc, 11, 3) from ad_bodegas where codigo = m.bodega) estab_destino, ");
				sqlString
						.append("null ruta, m.documento codigo_externo, m.guia_remision doc_sustento, null numero_autorizacion, ");
				sqlString.append("m.fecha fecha_emision ");
				sqlString.append("from fa_movimientos_mercaderia     m, ");
				sqlString.append("FA_OBSERVACIONES_MOVIMIENTO   o, ");
				sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
				sqlString.append("fa_movimientos_mercaderia_mas a, ");
				sqlString.append("fa_movimientos_mercaderia_adi mm, ");
				sqlString.append("ad_farmacias                  f, ");
				sqlString.append("ad_clasificacion_movimientos  cm, ");
				sqlString.append("fa_rutas_movmer               rm, ");
				sqlString.append("ab_personas                   p, ");
				sqlString.append("  ds_conductores_vehiculo       cv ");
				sqlString.append("where m.farmacia = O.farmacia(+) ");
				sqlString.append(" and m.documento = o.documento(+) ");
				sqlString.append("and fa_secs.documento_venta = m.documento ");
				sqlString.append("and fa_secs.farmacia = m.farmacia ");
				sqlString.append("and m.farmacia = a.farmacia(+) ");
				sqlString.append("and m.documento = a.documento(+) ");
				sqlString.append("and m.farmacia = mm.farmacia(+) ");
				sqlString.append("and m.documento = mm.documento(+) ");
				sqlString.append("and m.farmacia = f.codigo(+) ");
				sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
				sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
				sqlString.append("and m.farmacia = rm.farmacia(+) ");
				sqlString.append("and m.documento = rm.documento(+) ");
				sqlString.append("and p.codigo(+) = rm.num2 ");
				sqlString.append("and cv.persona(+) = rm.num2 ");
				sqlString.append("and cv.placa(+) = rm.car1 ");
				sqlString.append("and fa_secs.tipo_documento = ? ");
				sqlString
						.append("and m.tipo_movimiento || m.clasificacion_movimiento <> '0401' ");
				sqlString.append("and m.farmacia = ? ");
				sqlString.append("and m.documento = ? ");

				sqlString
						.append("and (rm.secuencia = ( select max(secuencia) from fa_rutas_movmer q where m.documento = q.documento ");
				sqlString
						.append(" and q.farmacia = m.farmacia) or rm.secuencia is null ) ");

				sqlString.append("union ");

				sqlString
						.append("select mm.ruc identificacion_dstino, mm.razon_social razon_social_dstino, ");
				sqlString
						.append("(select calle || ' ' || numero || ' ' || interseccion from ad_farmacias where codigo = m.farmacia_transaccion) dir_destino, ");
				sqlString.append("cm.descripcion motivo_traslado, ");
				sqlString
						.append("(select substr(numero_ruc, 11, 3) from ad_farmacias where codigo = m.farmacia_transaccion) estab_destino, ");
				sqlString
						.append("null ruta, m.documento codigo_externo, m.guia_remision doc_sustento, null numero_autorizacion, ");
				sqlString.append("m.fecha fecha_emision ");
				sqlString.append("from fa_movimientos_mercaderia     m, ");
				sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
				sqlString.append("   FA_OBSERVACIONES_MOVIMIENTO   o, ");
				sqlString.append("fa_movimientos_mercaderia_mas a, ");
				sqlString.append("fa_movimientos_mercaderia_adi mm, ");
				sqlString.append("ad_farmacias                  f, ");
				sqlString.append("fa_datos_transportista        t, ");
				sqlString.append("fa_datos_vehiculo             p, ");
				sqlString.append("ad_clasificacion_movimientos  cm ");
				sqlString.append("where m.farmacia = O.farmacia(+) ");
				sqlString.append("and m.documento = o.documento(+) ");
				sqlString.append("and m.farmacia = a.farmacia(+) ");
				sqlString.append("and m.documento = a.documento(+) ");
				sqlString.append("and m.farmacia = mm.farmacia(+) ");
				sqlString.append("and m.documento = mm.documento(+) ");
				sqlString.append("and m.farmacia = f.codigo(+) ");
				sqlString.append("and a.cp_num3 = t.id(+) ");
				sqlString.append("and t.id = p.id(+) ");
				sqlString.append("and fa_secs.documento_venta = m.documento ");
				sqlString.append("and fa_secs.farmacia = m.farmacia ");
				sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
				sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
				sqlString.append("and m.TIPO_MOVIMIENTO = '04' ");
				sqlString.append("AND m.CLASIFICACION_MOVIMIENTO = '01' ");
				sqlString.append("and fa_secs.tipo_documento = ? ");
				sqlString.append("and m.farmacia = ? ");
				sqlString.append("and m.documento = ? ");

				ps = conn.prepareStatement(sqlString.toString());
				ps.setLong(1, tipo);
				ps.setLong(2, farmacia);
				ps.setLong(3, documentoVenta);
				ps.setLong(4, tipo);
				ps.setLong(5, farmacia);
				ps.setLong(6, documentoVenta);

				ResultSet set = ps.executeQuery();
				String razonSocialDestinatario = null;
				String motivoTraslado = null;

				while (set.next()) {
					destinatarioXML = new Destinatario();
					destinatarioXML.setCodDocSustento("01");
					destinatarioXML.setCodEstabDestino(set
							.getString("estab_destino"));
					destinatarioXML.setDirDestinatario(set
							.getString("dir_destino"));
					destinatarioXML.setDocAduaneroUnico(null);
					destinatarioXML.setFechaEmisionDocSustento(set
							.getDate("fecha_emision") != null ? FechaUtil
							.formatearFecha(
									FechaUtil.convertirLongADate(set.getDate(
											"fecha_emision").getTime()),
									FechaUtil.DATE_FORMAT) : null);
					destinatarioXML.setIdentificacionDestinatario(set
							.getString("identificacion_dstino"));

					motivoTraslado = set.getString("motivo_traslado");
					motivoTraslado = StringUtil.validateInfoXML(motivoTraslado);

					destinatarioXML.setMotivoTraslado(motivoTraslado);

					// destinatarioXML.setNumAutDocSustento(set.getString("doc_sustento"));
					destinatarioXML.setNumDocSustento(set.getString(
							"doc_sustento").replace("-152-", "-501-"));

					razonSocialDestinatario = set
							.getString("razon_social_dstino");
					razonSocialDestinatario = StringUtil
							.validateInfoXML(razonSocialDestinatario);

					destinatarioXML
							.setRazonSocialDestinatario(razonSocialDestinatario);
					destinatarios.add(destinatarioXML);
				}

			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				try {
					if (ps != null)
						ps.close();
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}

			return destinatarios;
		} catch (Exception e) {
			log.error("Error listar destinatarios" + e.getMessage(), e);
			throw new GizloException("Error al listar destinatarios", e);
		}
	}

	private String obtenerCorreo(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException, SQLException {

		// PreparedStatement ps = null;
		CallableStatement cstmt = null;
		String correo = null;

		StringBuilder sqlString = new StringBuilder();

		// sqlString.append("select * from ab_medios_contacto where tipo = ? and persona in (select codigo from ab_personas where identificacion = ?) and ROWNUM <=1");
		sqlString
				.append("{call farmacias.fa_pkg_mov_general.pro_recupera_mail_cliente(?,?,?)}");
		cstmt = conn.prepareCall(sqlString.toString());
		cstmt.registerOutParameter(3, Types.VARCHAR);

		cstmt.setLong(1, documentoVenta);
		cstmt.setLong(2, farmacia);
		cstmt.execute();
		correo = cstmt.getString(3);

		// ps = conn.prepareStatement(sqlString.toString());
		// ps.setLong(1, tipo);
		// ps.setString(2, identificacion);

		// ResultSet set = ps.executeQuery();

		// while (set.next()) {
		// correo = set.getString("VALOR");
		// }

		if (cstmt != null)
			cstmt.close();

		return correo;

	}

	private List<Detalle> listarDetallesDestinatario(Connection conn,
			String idDestinatario, Long idGuia, Long idFarmacia)
			throws GizloException {
		try {
			PreparedStatement ps = null;
			List<Detalle> detalles = new ArrayList<Detalle>();
			Detalle detalleXML = null;

			try {
				StringBuilder sqlString = new StringBuilder();

				log.debug("Guia Remision:  " + idGuia + " " + idFarmacia);
				sqlString
						.append("select item, (p.nombre || ' ' || i.presentacion) as descripcion, cantidad from fa_detalles_otros_movimientos d, ");
				sqlString
						.append("pr_items i, pr_productos p where d.item = i.codigo and i.producto  = p.codigo ");
				sqlString.append("and d.documento = ? and d.farmacia = ? ");

				ps = conn.prepareStatement(sqlString.toString());
				ps.setLong(1, idGuia);
				ps.setLong(2, idFarmacia);

				ResultSet set = ps.executeQuery();
				String descripcion = null;

				while (set.next()) {
					detalleXML = new Detalle();
					detalleXML.setCantidad(new BigDecimal(set
							.getLong("cantidad")));
					detalleXML.setCodigoInterno(set.getLong("item") + "");

					descripcion = set.getString("descripcion");
					descripcion = StringUtil.validateInfoXML(descripcion);

					detalleXML.setDescripcion(descripcion);
					detalles.add(detalleXML);
				}

			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				try {
					if (ps != null)
						ps.close();
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}

			return detalles;
		} catch (Exception e) {
			log.error("Error listar destinatarios" + e.getMessage());
			throw new GizloException("Error al listar destinatarios", e);
		}
	}

	private Respuesta consultar(Connection conn, Long farmacia, Long documento,
			String estado, Long tipo) throws GizloException {
		PreparedStatement ps = null;
		Respuesta respuesta = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select * from farmacias.FA_DATOS_SRI_ELECTRONICA where farmacia = ? and documento = ? and TIPO_COMPROBANTE = ? ");

			if (estado != null && !estado.isEmpty()) {
				sqlString.append("and estado = ?");
			}

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documento);
			ps.setLong(3, tipo);

			if (estado != null && !estado.isEmpty()) {
				ps.setString(4, estado);
			}

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				respuesta = new Respuesta();
				respuesta.setId(set.getLong("NUMERO_INTERNO"));
				respuesta.setIdFarmacia(set.getLong("FARMACIA"));
				respuesta.setIdDocumento(set.getLong("DOCUMENTO"));
				respuesta.setTipoComprobante(set.getLong("TIPO_COMPROBANTE"));
				respuesta.setFecha(set.getDate("FECHA"));
				respuesta.setUsuario(set.getString("USUARIO"));
				respuesta.setFirmaE(set.getString("FIRMAE"));
				respuesta.setAutFirmaE(set.getString("AUT_FIRMAE"));
				respuesta.setFecha(set.getDate("FECHA_FIRMAE"));
				respuesta.setComprobanteFirmaE(set
						.getString("COMPROBANTE_FIRMAE"));
				respuesta.setClaveAcceso(set.getString("CLAVE_ACCESO"));
				respuesta.setObservacion(set.getString("OBSERVACION_ELEC"));
				/*
				 * respuesta .setObservacion(set.getNClob("OBSERVACION_ELEC") !=
				 * null ? set .getNClob("OBSERVACION_ELEC").toString() : null);
				 */
				respuesta.setCorreoElectronico(set
						.getString("CORREO_ELECTRONICO"));
				respuesta.setEstado(set.getString("ESTADO"));
				respuesta.setTipoProceso(set.getString("TIPO_PROCESO"));
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return respuesta;
	}

	private void insertarRespuesta(Connection conn, Respuesta respuesta)
			throws GizloException {

		PreparedStatement ps = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append(Constantes.INSERT_FA_DATOS_SRI_ELECTRONICA);
			ps = conn.prepareStatement(sqlString.toString());
			// ps.setLong(1, respuesta.getId());
			ps.setLong(1, respuesta.getIdFarmacia());
			ps.setLong(2, respuesta.getIdDocumento());
			ps.setLong(3, respuesta.getTipoComprobante());
			ps.setDate(4, respuesta.getFecha() != null ? new Date(respuesta
					.getFecha().getTime()) : null);
			ps.setString(5, respuesta.getUsuario());
			ps.setString(6, respuesta.getFirmaE());
			ps.setString(7, respuesta.getAutFirmaE());
			ps.setDate(8, respuesta.getFechaFirmaE() != null ? new Date(
					respuesta.getFechaFirmaE().getTime()) : null);
			ps.setString(9, respuesta.getComprobanteFirmaE());
			ps.setString(10, respuesta.getClaveAcceso());
			/*
			 * NClob clob = conn.createNClob(); clob.setString(1,
			 * respuesta.getObservacion()); ps.setNClob(11, clob);
			 */
			ps.setString(11, respuesta.getObservacion());
			ps.setString(12, respuesta.getCorreoElectronico());
			ps.setString(13, respuesta.getEstado());
			ps.setString(14, respuesta.getTipoProceso());

			ps.executeUpdate();
			// conn.commit();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			/*
			 * try { conn.rollback(); } catch (SQLException e1) {
			 * log.error(e1.getMessage()); }
			 */
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	private void actualizarRespuesta(Connection conn, Respuesta respuesta)
			throws GizloException {
		PreparedStatement ps = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append(Constantes.UPDATE_FA_DATOS_SRI_ELECTRONICA);
			ps = conn.prepareStatement(sqlString.toString());

			// ps.setLong(1, respuesta.getId());
			ps.setLong(1, respuesta.getIdFarmacia());
			ps.setLong(2, respuesta.getIdDocumento());
			ps.setLong(3, respuesta.getTipoComprobante());
			ps.setDate(4, respuesta.getFecha() != null ? new Date(respuesta
					.getFecha().getTime()) : null);
			ps.setString(5, respuesta.getUsuario());
			ps.setString(6, respuesta.getFirmaE());
			ps.setString(7, respuesta.getAutFirmaE());
			ps.setDate(8, respuesta.getFechaFirmaE() != null ? new Date(
					respuesta.getFechaFirmaE().getTime()) : null);
			ps.setString(9, respuesta.getComprobanteFirmaE());
			ps.setString(10, respuesta.getClaveAcceso());
			/*
			 * NClob clob = conn.createNClob(); clob.setString(1,
			 * respuesta.getObservacion()); ps.setNClob(11, clob);
			 */
			ps.setString(11, respuesta.getObservacion());
			ps.setString(12, respuesta.getCorreoElectronico());
			ps.setString(13, respuesta.getEstado());
			ps.setString(14, respuesta.getTipoProceso());

			ps.setLong(15, respuesta.getIdFarmacia());
			ps.setLong(16, respuesta.getIdDocumento());
			ps.setLong(17, respuesta.getTipoComprobante());

			ps.executeUpdate();
			// conn.commit();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			/*
			 * try { conn.rollback(); } catch (SQLException e1) {
			 * log.error(e1.getMessage()); }
			 */
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private Autorizacion autorizarComprobante(String wsdlLocation,String claveAcceso) {
		
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

				respuesta
						.setMensajeSistema("No existe registrado un emisor con RUC: "
								+ mensaje.getGuia().getInfoTributaria()
										.getRuc());
				respuesta
						.setMensajeCliente("No existe registrado un emisor con RUC: "
								+ mensaje.getGuia().getInfoTributaria()
										.getRuc());
				return respuesta;
			}

			if (mensaje.getGuia().getInfoTributaria().getSecuencial() == null
					|| mensaje.getGuia().getInfoTributaria().getSecuencial()
							.isEmpty()) {
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
			Parametro codFacParametro = cacheBean.consultarParametro(
					Constantes.COD_GUIA, emisor.getId());
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

			com.gizlocorp.gnvoice.modelo.GuiaRemision comprobanteExistente = null;
			try {
				comprobanteExistente = guiaRemisionDAO.obtenerComprobante(null,
						mensaje.getCodigoExterno(), mensaje.getGuia()
								.getInfoTributaria().getRuc(),
						mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				guiaRemisionDAO = (GuiaRemisionDAO) ic
						.lookup("java:global/gnvoice-ejb/GuiaRemisionDAOImpl!com.gizlocorp.gnvoice.dao.GuiaRemisionDAO");
				comprobanteExistente = guiaRemisionDAO.obtenerComprobante(null,
						mensaje.getCodigoExterno(), mensaje.getGuia()
								.getInfoTributaria().getRuc(),
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
			mensaje.getGuia().setId("comprobante");
			mensaje.getGuia().setVersion(Constantes.VERSION);

			mensaje.getGuia().getInfoTributaria()
					.setAmbiente(ambParametro.getValor());
			mensaje.getGuia().getInfoTributaria()
					.setCodDoc(codFacParametro.getValor());

			if (mensaje.getGuia().getInfoTributaria().getSecuencial().length() < 9) {
				StringBuilder secuencial = new StringBuilder();

				int secuencialTam = 9 - mensaje.getGuia().getInfoTributaria()
						.getSecuencial().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(mensaje.getGuia().getInfoTributaria()
						.getSecuencial());
				mensaje.getGuia().getInfoTributaria()
						.setSecuencial(secuencial.toString());
			}

			mensaje.getGuia().getInfoTributaria()
					.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

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

			// TODO contigencia
			// if (existioComprobante
			// && comprobanteExistente != null
			// && comprobanteExistente.getTipoEmision().equals(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo())) {
			// claveAcceso = comprobanteExistente.getClaveAcceso();
			// mensaje.getGuia()
			// .getInfoTributaria()
			// .setTipoEmision(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo());
			// } else {
			// genera clave de acceso
			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria()
					.getEstab());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria()
					.getPtoEmi());
			log.debug("codigo numerico - secuencial de factura: "
					+ mensaje.getGuia().getInfoTributaria().getSecuencial());
			codigoNumerico.append(mensaje.getGuia().getInfoTributaria()
					.getSecuencial());
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
					if (mensaje.getCorreoElectronicoNotificacion() != null
							&& !mensaje.getCorreoElectronicoNotificacion()
									.isEmpty()) {
						try {
							String comprobanteXML = respAutorizacionComExt
									.getComprobante();
							comprobanteXML = comprobanteXML.replace(
									"<![CDATA[", "");
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

					autoriza(comprobante);

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

				autoriza(comprobante);

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

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public com.gizlocorp.gnvoice.modelo.GuiaRemision autoriza(
			com.gizlocorp.gnvoice.modelo.GuiaRemision comprobante) {

		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = guiaRemisionDAO.update(comprobante);

			} else {
				comprobante = guiaRemisionDAO.persist(comprobante);

			}
		} catch (Exception ex) {
			try {
				InitialContext ic = new InitialContext();
				guiaRemisionDAO = (GuiaRemisionDAO) ic
						.lookup("java:global/gnvoice-ejb/GuiaRemisionDAOImpl!com.gizlocorp.gnvoice.dao.GuiaRemisionDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = guiaRemisionDAO.update(comprobante);

				} else {
					comprobante = guiaRemisionDAO.persist(comprobante);

				}
			} catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}

		return comprobante;
	}
}
