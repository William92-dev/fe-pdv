package com.gizlocorp.gnvoiceFybeca.mdb;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import com.gizlocorp.gnvoice.dao.NotaCreditoDAO;
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
import com.gizlocorp.gnvoice.service.impl.CacheBean;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarResponse;
import com.gizlocorp.gnvoice.xml.notacredito.Impuesto;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito.Compensaciones;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito.Compensaciones.Compensacion;
import com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos;
import com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto;
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
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/notaCreditoProcesarBatchQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "700"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "45000") })
@DependsOn("CacheBean")
public class NotaCreditoProcesarBatchManagment implements MessageListener {

	@EJB(lookup = "java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO")
	com.gizlocorp.gnvoice.dao.NotaCreditoDAO notaCreditoDAO;
	
	@EJB
	CacheBean cacheBean;

	private static Logger log = Logger.getLogger(NotaCreditoProcesarBatchManagment.class.getName());

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;
	
	boolean cartorce= false;
	boolean boodoce= false;


   

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
				log.debug("El objeto seteado en el mensaje no es de tipo String, se desechara " + message);
				return;
			}

			// log.debug("**Obteniendo parametros del servicio");
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

			// log.debug("PROCESAR Farmacia: " + farmacia);

			CredencialDS credencialDS = new CredencialDS();
			credencialDS.setDatabaseId(sid);
			credencialDS.setUsuario("WEBLINK");
			credencialDS.setClave("weblink_2013");

			Connection conn = null;
			Long tipo = null;
			List<Long> listado = null;

			try {
				conn = Conexion.obtenerConexionFybeca(credencialDS);
				tipo = obtenerTipo(conn, ComprobanteEnum.NOTA_CREDITO.getDescripcion());

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
				log.debug("NC A PROCESAR (SIN PROCESAR): Listado de NotaCreditos es: "
						+ listado.size()
						+ (listado.size() > 1000 ? "*******************************************************************************************************************************"
								: "")
						+ ", Farmacia: "
						+ farmacia
						+ ", tipo: "
						+ tipo);

				NotaCreditoProcesarRequest item = null;
				NotaCreditoProcesarResponse response = null;
				Respuesta respuesta = null;

				for (Long documentoVenta : listado) {
					try {
						conn = Conexion.obtenerConexionFybeca(credencialDS);
						if (tipo == null) {
							tipo = obtenerTipo(conn,
									ComprobanteEnum.NOTA_CREDITO
											.getDescripcion());
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

									// respuesta.setSid(sid);
									respuesta.setAutFirmaE(response
											.getNumeroAutorizacion());
									respuesta.setClaveAcceso(response
											.getClaveAccesoComprobante());
									respuesta.setComprobanteFirmaE(response
											.getNumeroAutorizacion());
									respuesta
											.setCorreoElectronico(item
													.getCorreoElectronicoNotificacion());
									// respuesta.setEstado(response.getEstado());

									if (response.getEstado().equals(
											Estado.PENDIENTE.getDescripcion())
											|| response.getEstado().equals(
													Estado.COMPLETADO
															.getDescripcion())
											|| response.getEstado().equals(
													Estado.RECIBIDA
															.getDescripcion())) {
										respuesta
												.setEstado(EstadoEnum.PENDIENTE
														.getDescripcion());
									}
									if (response.getEstado().equals(
											Estado.AUTORIZADO.getDescripcion())) {
										respuesta.setEstado(EstadoEnum.APROBADO
												.getDescripcion());
									}
									if (response.getEstado().equals(
											Estado.CANCELADO.getDescripcion())
											|| response.getEstado().equals(
													Estado.ERROR
															.getDescripcion())
											|| response.getEstado().equals(
													Estado.DEVUELTA
															.getDescripcion())
											|| response.getEstado().equals(
													Estado.RECHAZADO
															.getDescripcion())) {
										respuesta
												.setEstado(EstadoEnum.RECHAZADO
														.getDescripcion());
									}

									respuesta.setFecha(Calendar.getInstance()
											.getTime());
									respuesta
											.setFechaFirmaE(response
													.getFechaAutorizacion() != null ? response
													.getFechaAutorizacion()
													: null);

									if (response.getEstado().equals(
											EstadoEnum.APROBADO
													.getDescripcion())) {
										respuesta.setFirmaE(GeneradoEnum.SI
												.getDescripcion());
									} else {
										respuesta.setFirmaE(GeneradoEnum.NO
												.getDescripcion());
									}

									respuesta.setIdDocumento(documentoVenta);
									respuesta.setIdFarmacia(farmacia);
									respuesta.setObservacion(response
											.getEstado()
											+ " "
											+ response.getMensajeSistema());
									respuesta.setTipoComprobante(tipo);
									respuesta.setTipoProceso(ProcesoEnum.BATCH
											.getDescripcion());
									respuesta.setUsuario("");
									respuesta.setSid(sid);

									// inqueue(respuesta);

									if (respuesta.getObservacion() != null
											&& !respuesta.getObservacion()
													.isEmpty()) {
										try {
											respuesta.setObservacion(respuesta
													.getObservacion()
													.replaceAll(
															"[^\\x00-\\x7F]",
															""));
										} catch (Exception ex) {
											log.info("mensaje uncode no valida");
										}
									}

									if (conn == null || conn.isClosed()) {
										conn = Conexion
												.obtenerConexionFybeca(credencialDS);

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
                comprobanteExistente = notaCreditoDAO.obtenerComprobante(
                        null, mensaje.getCodigoExterno(), mensaje
                                .getNotaCredito().getInfoTributaria().getRuc(),
                        mensaje.getAgencia());
            } catch (Exception ex) {
                InitialContext ic = new InitialContext();
                notaCreditoDAO = (NotaCreditoDAO) ic
                        .lookup("java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO");
                comprobanteExistente = notaCreditoDAO.obtenerComprobante(
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

            mensaje.getNotaCredito().getInfoNotaCredito().setMoneda(monParamtro.getValor());
            mensaje.getNotaCredito().getInfoTributaria().setAmbiente(ambParametro.getValor());
            mensaje.getNotaCredito().getInfoTributaria().setCodDoc(codFacParametro.getValor());

            if (mensaje.getNotaCredito().getInfoTributaria().getSecuencial()
                    .length() < 9) {
                StringBuilder secuencial = new StringBuilder();

                int secuencialTam = 9 - mensaje.getNotaCredito().getInfoTributaria().getSecuencial().length();
                for (int i = 0; i < secuencialTam; i++) {
                    secuencial.append("0");
                }
                secuencial.append(mensaje.getNotaCredito().getInfoTributaria().getSecuencial());
                mensaje.getNotaCredito().getInfoTributaria().setSecuencial(secuencial.toString());
            }

            mensaje.getNotaCredito().getInfoTributaria().setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

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
                respuesta
                        .setFechaAutorizacion(respAutorizacionComExt
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

                autoriza(comprobante);

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

            ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline( wsdlLocationAutorizacion, claveAcceso);

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
                respuesta.setMensajeCliente("Comprobante AUTORIZADO");

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

                autoriza(comprobante);

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
	
	
	 private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,String claveAcceso) {
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


	public List<Long> listarDocumentos(Connection conn, Long farmacia, Long tipo)
			throws GizloException {

		PreparedStatement ps = null;
		ResultSet set = null;
		List<Long> facturas = new ArrayList<Long>();

		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append("select f.documento_venta as codigo_externo ");
			sqlString
					.append("from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString
					.append("and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta ");
			sqlString.append("and c.codigo = fa.farmacia and f.farmacia = ? ");
			
			//sqlString.append("and f.fecha >= to_date('01/10/2014','dd/mm/yyyy') ");
			sqlString.append("and f.fecha >= sysdate-91 ");
			
			sqlString.append("and not exists (select documento from farmacias.FA_DATOS_SRI_ELECTRONICA ");
			sqlString
					.append("where farmacia = ? and TIPO_COMPROBANTE = ? and estado in ('A','C') and aut_firmae is not null ");
			sqlString
					.append("and FA_DATOS_SRI_ELECTRONICA.Documento = f.documento_venta and FA_DATOS_SRI_ELECTRONICA.Farmacia = f.farmacia) ");

			sqlString.append("and rownum < 10 ");
			sqlString.append("and fa_secs.tipo_documento = ? ");
			sqlString.append("order by f.fecha");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, farmacia);
			ps.setLong(3, tipo);
			ps.setLong(4, tipo);

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

	private NotaCreditoProcesarRequest listar(Connection conn,
			Long documentoVenta, Long farmacia, Long tipo)
			throws GizloException {

		PreparedStatement ps = null;

		NotaCreditoProcesarRequest notaCreditoFybeca = null;

		try {
			StringBuilder sqlString = new StringBuilder();

			// substr(fa.numero_ruc, 11, 3) establecimiento,
			// substr(f.numero_sri, 5, 3) punto_emision,

			sqlString
					.append("select fa_secs.secuencia as secuencial, f.fecha as fechaEmision, c.calle||' '||c.numero||' '||c.interseccion as dirEstablecimiento, fa.cp_char3 as contribuyenteEspecial, ");
			sqlString
					.append("'SI' as obligadoContabilidad, f.primer_apellido||' '|| f.segundo_apellido||' '|| f.nombres as razonSocialComprador, c.NOMBRE as nombreSucursal, ");
			sqlString
					.append("f.identificacion as identificacionComprador, ROUND((f.venta_total_factura - f.valor_iva),2) as totalSinImpuestos, ");
			sqlString
					.append("'0' as totalDescuento, '0.00' as propina, ROUND(venta_total_factura,2) as importeTotal, 'DOLAR' as moneda, ");
			sqlString
					.append("'1' as ambiente, '1' as tipoEmision, fa.cp_var3 as razonSocial, fa.cp_var3 as nombreComercial, fa.cp_var6 as ruc, ");
			sqlString
					.append("substr(c.numero_ruc, 11, 3) as estab, substr(f.numero_sri, 5, 3) as ptoEmi, f.documento_venta as codigo_externo, ");
			sqlString.append("f.documento_venta_padre as factura_codigo "); // FACTURA
			sqlString
					.append("from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString
					.append("and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta ");
			sqlString.append("and c.codigo = fa.farmacia and f.farmacia = ? ");

			sqlString.append("and f.documento_venta = ? ");
			sqlString.append("and fa_secs.tipo_documento = ? ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);

			ps.setLong(2, documentoVenta);
			ps.setLong(3, tipo);

			ResultSet set = ps.executeQuery();
			
			CampoAdicional email=null;
			CampoAdicional docInterno=null;
			CampoAdicional dirEstablecimiento=null;
			CampoAdicional compensacion=null;

			while (set.next()) {
				notaCreditoFybeca = null;
				try {
					notaCreditoFybeca = obtenerNotaCredito(conn, set, farmacia,
							tipo);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}
				if (notaCreditoFybeca != null) {

					String correo = obtenerCorreo(conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					
					String direccion=obtenerDireccion(conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);

					notaCreditoFybeca
							.setCorreoElectronicoNotificacion(StringUtil
									.validateEmail(correo));

					Map<String, Object> detalesTotalesImpuesto = listarDetalles(
							conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					List<Detalle> detalles = (List<Detalle>) detalesTotalesImpuesto
							.get(Constantes.DETALLES);
					log.debug("**servicio listar detalles: "
							+ (detalles != null ? detalles.size() : 0));

					List<TotalImpuesto> totalesImpuestos = (List<TotalImpuesto>) detalesTotalesImpuesto
							.get(Constantes.TOTAL_IMPUESTO);
					log.debug("**servicio listar total con impuesto: "
							+ (totalesImpuestos != null ? totalesImpuestos
									.size() : 0));

					if (totalesImpuestos != null && !totalesImpuestos.isEmpty()) {
						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito()
								.setTotalConImpuestos(new TotalConImpuestos());
						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito()
								.getTotalConImpuestos().getTotalImpuesto()
								.addAll(totalesImpuestos);
					}

					notaCreditoFybeca
							.getNotaCredito()
							.getInfoNotaCredito()
							.setTotalSinImpuestos(
									(BigDecimal) detalesTotalesImpuesto
											.get(Constantes.TOTAL_SIN_IMPUESTO));
					
					List<Compensacion> compensacions = listarCompensacionISS(conn, documentoVenta, farmacia, cartorce, boodoce);
					List<Compensacion> compensacions2 = listarCompensacionIDE(conn, documentoVenta, farmacia, cartorce, boodoce);
					List<Compensacion> compensacions3 = listarCompensacionITC(conn, documentoVenta, farmacia, cartorce, boodoce);					
					List<Compensacion> compensacionsAll = new ArrayList<Compensacion>();
					log.info("compensaciones notas de credito ****"+compensacionsAll.size());
					if(compensacions != null && !compensacions.isEmpty()){
						compensacionsAll.addAll(compensacions);
					}
					if(compensacions2 != null && !compensacions2.isEmpty()){
						compensacionsAll.addAll(compensacions2);
					}
					if(compensacions3 != null && !compensacions3.isEmpty()){
						compensacionsAll.addAll(compensacions3);
					}
					if(compensacionsAll != null && !compensacionsAll.isEmpty()){
						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().setCompensaciones(new Compensaciones());
						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getCompensaciones().getCompensacion().addAll(compensacionsAll);
					} 

					// DETALLE IMPUESTO
					if (detalles != null && !detalles.isEmpty()) {
						notaCreditoFybeca.getNotaCredito().setDetalles(
								new Detalles());
						notaCreditoFybeca.getNotaCredito().getDetalles()
								.getDetalle().addAll(detalles);
					}
					// notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().setImporteTotal(notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getImporteTotal().subtract(notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getTotalDescuento()));

					InfoAdicional infoAdicional = null;
					
					
					notaCreditoFybeca.setBanderaOferton(banderaOferton(conn,farmacia));
					
					String compensacionStr = StringUtil.validateInfoXML(obtenerCompensacion(
							conn, farmacia));
					
//					if(compensacionStr != null && compensacionStr.equals("S")){
//						if (infoAdicional == null) {
//							infoAdicional = new InfoAdicional();
//						}
//						
//						compensacion = new CampoAdicional();
//						compensacion.setNombre("COMPENSACION");
//						compensacion.setValue("Compensaci�n solidaria 2% ");;
//						
//						infoAdicional.getCampoAdicional().add(docInterno);
//						
//					}
					
					if (notaCreditoFybeca.getCodigoExterno() != null
							&& !notaCreditoFybeca.getCodigoExterno().isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new InfoAdicional();
						}
						
						docInterno = new CampoAdicional();
						docInterno.setNombre("DOCUMENTO INTERNO");
						docInterno.setValue(notaCreditoFybeca.getCodigoExterno());
						
						infoAdicional.getCampoAdicional().add(docInterno);
						
					}
					
					if (notaCreditoFybeca.getCorreoElectronicoNotificacion() != null
							&& !notaCreditoFybeca.getCorreoElectronicoNotificacion().isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new InfoAdicional();
						}
						
						email = new CampoAdicional();
						email.setNombre("EMAIL");
						email.setValue(notaCreditoFybeca.getCorreoElectronicoNotificacion());
						
						infoAdicional.getCampoAdicional().add(email);
						
					}
					
					if (direccion != null
							&& !direccion.isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new InfoAdicional();
						}
						
						dirEstablecimiento = new CampoAdicional();
						dirEstablecimiento.setNombre("DIRECCION");
						dirEstablecimiento.setValue(StringEscapeUtils.escapeXml(direccion));
						
						infoAdicional.getCampoAdicional().add(dirEstablecimiento);
						
					}
					
					if (infoAdicional != null
							&& infoAdicional.getCampoAdicional() != null
							&& !infoAdicional.getCampoAdicional().isEmpty()) {
						notaCreditoFybeca.getNotaCredito().setInfoAdicional(infoAdicional);
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

		return notaCreditoFybeca;

	}

	private NotaCreditoProcesarRequest obtenerNotaCredito(Connection conn,
			ResultSet set, Long farmacia, Long tipo) throws SQLException,
			GizloException {
		NotaCredito notaCredito = null;
		String tipoIdentificacionComprador = null;
		NotaCreditoProcesarRequest notaCreditoProcesarRequest = null;
		// infoNotaCredito.setAdicionales(adicionales);
		notaCredito = new NotaCredito();
		InfoNotaCredito infoNotaCredito = new InfoNotaCredito();

		String razonSocialComprador = set.getString("razonSocialComprador");
		razonSocialComprador = StringUtil.validateInfoXML(razonSocialComprador);

		infoNotaCredito.setRazonSocialComprador(razonSocialComprador.trim());

		String dirEstablecimiento = set.getString("dirEstablecimiento");
		dirEstablecimiento = StringUtil.validateInfoXML(dirEstablecimiento);
		infoNotaCredito
				.setDirEstablecimiento(dirEstablecimiento != null ? dirEstablecimiento
						.replaceAll("[\n]", "").replaceAll("[\b]", "") : null);

		infoNotaCredito
				.setFechaEmision(set.getDate("fechaEmision") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fechaEmision").getTime()),
								FechaUtil.DATE_FORMAT) : null);
		// infoNotaCredito.setFechaEmision(FechaUtil.formatearFecha(FechaUtil.convertirLongADate(Calendar.getInstance().getTime().getTime()),FechaUtil.DATE_FORMAT));

		infoNotaCredito.setMoneda(set.getString("moneda"));

		// infoNotaCredito.setTotalDescuento(set.getBigDecimal("totalDescuento"));
		// log.debug("**totalDescuento: "+obtenerTotalDescuento(credencialDS,
		// set.getLong("codigo_externo"),farmacia));

		if (set.getString("identificacionComprador") != null
				&& !set.getString("identificacionComprador").isEmpty()) {
			tipoIdentificacionComprador = obtenerIdentificacion(conn,
					set.getString("identificacionComprador"));
			if (tipoIdentificacionComprador != null
					&& !tipoIdentificacionComprador.isEmpty()) {

				if (tipoIdentificacionComprador.equalsIgnoreCase("C")) {
					infoNotaCredito.setTipoIdentificacionComprador("05");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("R")) {
					infoNotaCredito.setTipoIdentificacionComprador("04");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("P")) {
					infoNotaCredito.setTipoIdentificacionComprador("06");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("N")) {
					infoNotaCredito.setTipoIdentificacionComprador("07");
				}
			}

			if (infoNotaCredito != null
					&& infoNotaCredito.getTipoIdentificacionComprador() != null
					&& infoNotaCredito.getTipoIdentificacionComprador()
							.equalsIgnoreCase("07")) {
				infoNotaCredito.setIdentificacionComprador("9999999999999");
				infoNotaCredito.setRazonSocialComprador("CONSUMIDOR FINAL");
			} else {
				String identificacion = set
						.getString("identificacionComprador");
				infoNotaCredito.setTipoIdentificacionComprador("06");

				if (StringUtil.esNumero(identificacion)) {
					if (identificacion.length() == 10) {
						infoNotaCredito.setTipoIdentificacionComprador("05");
					} else if (identificacion.length() == 13
							&& identificacion.substring(10).equals("001")) {
						infoNotaCredito.setTipoIdentificacionComprador("04");
					}
				}

				infoNotaCredito.setIdentificacionComprador(set
						.getString("identificacionComprador"));
			}
		} else {
			infoNotaCredito.setIdentificacionComprador("9999999999999");
			infoNotaCredito.setTipoIdentificacionComprador("07");
			infoNotaCredito.setRazonSocialComprador("CONSUMIDOR FINAL");
		}

		infoNotaCredito.setTotalSinImpuestos(set
				.getBigDecimal("totalSinImpuestos"));

		// FACTURA
		infoNotaCredito.setCodDocModificado("01");
		Long tipoDocModificado = obtenerTipo(conn,
				ComprobanteEnum.FACTURA.getDescripcion());

		StringBuilder secuencialFactura = new StringBuilder();
		Long secuencialFacturaLong = obtenerSecuencialFactura(conn,
				set.getLong("factura_codigo"), farmacia, tipoDocModificado);

		if (secuencialFacturaLong == null) {
			secuencialFacturaLong = obtenerSecuencialFacturaAutoimpresor(conn,
					set.getLong("factura_codigo"), farmacia, tipoDocModificado);
		}
		String secuencialTmp = secuencialFacturaLong != null ? secuencialFacturaLong
				+ ""
				: null;

		if (secuencialTmp != null) {
			int secuencialTam = 9 - secuencialTmp.length();
			for (int i = 0; i < secuencialTam; i++) {
				secuencialFactura.append("0");
			}

		}
		secuencialFactura.append(secuencialTmp);

		infoNotaCredito.setNumDocModificado(set.getString("estab") + "-"
				+ set.getString("ptoEmi") + "-" + secuencialFactura.toString()); // factura_codigo

		Date fecha = obtenerFechaEmision(conn, set.getLong("factura_codigo"),
				farmacia);
		infoNotaCredito.setFechaEmisionDocSustento(fecha != null ? FechaUtil
				.formatearFecha(FechaUtil.convertirLongADate(fecha.getTime()),
						FechaUtil.DATE_FORMAT) : null);
		infoNotaCredito.setValorModificacion(set.getBigDecimal("importeTotal"));

		String motivo = obtenerMotivo(conn, set.getLong("codigo_externo"),
				farmacia);
		if (motivo == null) {
			motivo = "MOTIVO";
		}

		motivo = StringUtil.validateInfoXML(motivo);

		infoNotaCredito.setMotivo(motivo);
		notaCredito.setInfoNotaCredito(infoNotaCredito);

		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setCodDoc("04");// set.getString("codDoc")
		infoTributaria.setEstab(set.getString("estab"));
		infoTributaria.setPtoEmi(set.getString("ptoEmi"));
		infoTributaria.setRuc(set.getString("ruc"));
		
		String nombreSucursal = set.getString("nombreSucursal");
		nombreSucursal = StringUtil.validateInfoXML(nombreSucursal);
		infoTributaria.setNombreComercial(nombreSucursal);

		// infoTributaria.setRuc("1792366259001");
		/*
		 * infoTributaria .setSecuencial((set.getLong("secuencial") != 0 ? (set
		 * .getLong("secuencial") + "") : null));
		 */
		// infoTributaria.setSecuencial(set.getLong("codigo_externo") + "");
		infoTributaria.setSecuencial(set.getLong("secuencial") + "");

		notaCredito.setInfoTributaria(infoTributaria);

		notaCreditoProcesarRequest = new NotaCreditoProcesarRequest();
		notaCreditoProcesarRequest.setNotaCredito(notaCredito);
		notaCreditoProcesarRequest.setCodigoExterno(set
				.getLong("codigo_externo") + "");

		notaCreditoProcesarRequest.setIdentificadorUsuario((set
				.getString("identificacionComprador") != null && !set
				.getString("identificacionComprador").isEmpty()) ? set
				.getString("identificacionComprador") : null);
		notaCreditoProcesarRequest.setAgencia(farmacia.toString());
		return notaCreditoProcesarRequest;
	}

	private Date obtenerFechaEmision(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException {

		PreparedStatement ps = null;
		Date resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select f.fecha as fechaEmision from fa_facturas f, ad_farmacias ");
			sqlString.append("where f.farmacia = ? and f.documento_venta = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getDate("fechaEmision");
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

	private Long obtenerSecuencialFactura(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException {
		PreparedStatement ps = null;
		Long resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();
			sqlString
					.append("select fa_secs.secuencia as secuencial from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString
					.append("where fa.documento_venta = f.documento_venta and fa.farmacia = f.farmacia and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta ");
			sqlString
					.append("and c.codigo = fa.farmacia and f.farmacia = ? and f.documento_venta = ? and fa_secs.tipo_documento = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);
			ps.setLong(3, tipo);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getLong("secuencial");
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

	private Long obtenerSecuencialFacturaAutoimpresor(Connection conn,
			Long documentoVenta, Long farmacia, Long tipo)
			throws GizloException {

		PreparedStatement ps = null;
		Long resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select nvl(fa_secs.secuencia,to_number(substr(f.numero_sri,9,15))) as secuencial ");
			sqlString.append("from fa_factura_adicional    fa, ");
			sqlString.append("fa_facturas             f, ");
			sqlString.append("ad_farmacias            c, ");
			sqlString.append("fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString.append("and fa_secs.farmacia(+) = f.farmacia ");
			sqlString
					.append("and fa_secs.documento_venta(+) = f.documento_venta ");
			sqlString.append("and c.codigo = fa.farmacia ");
			sqlString.append("and f.farmacia        = ? ");
			sqlString.append("and f.documento_venta = ? ");
			sqlString.append("aND F.TIPO_MOVIMIENTO = '01' ");
			sqlString.append("AND F.CLASIFICACION_MOVIMIENTO = '01' ");
			sqlString.append("and exists (select *  ");
			sqlString.append("from fa_secuencias_fact_elec c ");
			sqlString
					.append("where c.documento_venta = f.documento_venta_padre ");
			sqlString.append("and c.farmacia = f.farmacia_padre ");
			sqlString.append("and c.tipo_documento = 4)  ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);
			// ps.setLong(3, tipo);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getLong("secuencial");
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

	private String obtenerMotivo(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException {

		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select observacion_factura as motivo from fa_factura_datos fd where fd.farmacia = ? and fd.documento_venta = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("motivo");
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

	private String obtenerIdentificacion(Connection conn, String identificacion)
			throws GizloException {
		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select ap.tipo_identificacion as tipoIdentificacionComprador from ab_personas ap where ap.identificacion = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, identificacion);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("tipoIdentificacionComprador");
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

	private Map<String, Object> listarDetalles(Connection conn,
			Long idNotaCredito, Long idFarmacia) throws GizloException,
			SQLException {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		PreparedStatement ps = null;
		List<Detalle> detalles = new ArrayList<Detalle>();
		Detalle detalleXML = null;

		// <<AG
		List<TotalImpuesto> totalesImpuestos = new ArrayList<TotalImpuesto>();
		List<Impuesto> detalleImpuestos = new ArrayList<Impuesto>();
		Impuesto impuestoXML = null;
		TotalImpuesto totalImpuestoXML = null;
		BigDecimal total12 = new BigDecimal(0f), total0 = new BigDecimal(0f), baseImp0 = new BigDecimal(
				0f), baseImp12 = new BigDecimal(0f), totalSinImpuesto = new BigDecimal(
				0f), valor = new BigDecimal(0f), baseImp = new BigDecimal(0f);
		BigDecimal total14 = new BigDecimal(0f), baseImp14 = new BigDecimal(0f);
		// AG>>
		StringBuilder sqlString = new StringBuilder();

		// ROUND((precio_fybeca - venta)/cantidad,2)
		log.debug("Detalle:  " + idNotaCredito + " " + idFarmacia);
		sqlString
				.append("(SELECT (fa_detalles_servicios.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		// <<AG
		sqlString
				.append("ROUND((cantidad * venta * fa_detalles_servicios.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		// AG>>
		sqlString
				.append("ROUND(venta, 4) as venta, (fa_servicios.nombre) as descripcion from fa_detalles_servicios inner join fa_servicios ");
		sqlString
				.append("on fa_detalles_servicios.item = fa_servicios.codigo ");
		sqlString.append("WHERE documento_venta = ? and farmacia = ? ) ");
		sqlString.append("union all ");
		sqlString
				.append("(SELECT (fa_detalles_factura.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		// <<AG
		sqlString
				.append("ROUND((cantidad * venta * fa_detalles_factura.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		// AG>>
		sqlString
				.append("ROUND(venta, 4) as venta, (pr_productos.nombre || pr_items.presentacion) as descripcion ");
		sqlString.append("FROM fa_detalles_factura ");
		sqlString
				.append("inner join pr_items on fa_detalles_factura.item = pr_items.codigo ");
		sqlString.append("inner join pr_productos ");
		sqlString.append("on pr_items.producto = pr_productos.codigo ");
		sqlString.append("WHERE documento_venta = ? and farmacia = ?)");

		ps = conn.prepareStatement(sqlString.toString());
		ps.setLong(1, idNotaCredito);
		ps.setLong(2, idFarmacia);
		ps.setLong(3, idNotaCredito);
		ps.setLong(4, idFarmacia);

		ResultSet set = ps.executeQuery();
		String descripcion = null;

		while (set.next()) {
			if (set.getBigDecimal("porc_iva") != null) {
				detalleXML = new Detalle();
				detalleXML.setCantidad(new BigDecimal(set.getLong("cantidad")));
				// detalle.setCodigoAuxiliar(set.getString("NOMBRE_PARAM"));
				detalleXML.setCodigoInterno(set.getString("item"));

				descripcion = set.getString("descripcion");
				descripcion = StringUtil.validateInfoXML(descripcion);

				detalleXML.setDescripcion(descripcion);

				if (set.getBigDecimal("porc_iva").equals(new BigDecimal(0))) {
					detalleXML.setDescuento(set.getBigDecimal("descuento0"));
				} else {
					detalleXML.setDescuento(set.getBigDecimal("descuento12"));
				}

				if (set.getBigDecimal("porc_iva").equals(new BigDecimal(0))) {
					detalleXML.setPrecioTotalSinImpuesto(set
							.getBigDecimal("precio_sin_imp"));
				} else {
					detalleXML.setPrecioTotalSinImpuesto(set
							.getBigDecimal("precio_sin_imp"));
				}
				detalleXML.setPrecioUnitario(set.getBigDecimal("venta"));

				// <<AG
				detalleImpuestos = new ArrayList<Impuesto>();

				if (set.getBigDecimal("porc_iva").equals(new BigDecimal(0))) {
					// 0%
					total0 = total0
							.add((set.getBigDecimal("valor") != null ? set
									.getBigDecimal("valor") : new BigDecimal(0)));
					baseImp0 = baseImp0
							.add((set.getBigDecimal("baseImp") != null ? set
									.getBigDecimal("baseImp") : new BigDecimal(
									0)));

					baseImp = set.getBigDecimal("baseImp") != null ? set
							.getBigDecimal("baseImp") : BigDecimal.ZERO;
					valor = set.getBigDecimal("valor") != null ? set
							.getBigDecimal("valor") : BigDecimal.ZERO;

					baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
					valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);

					impuestoXML = new Impuesto();
					impuestoXML.setCodigo("2");
					impuestoXML.setCodigoPorcentaje("0");
					impuestoXML.setTarifa(new BigDecimal(0));
					impuestoXML.setBaseImponible(baseImp);
					impuestoXML.setValor(valor);
					detalleImpuestos.add(impuestoXML);

				} else {
					// 12%
					BigDecimal doce = new BigDecimal("0.12");
					if(set.getBigDecimal("porc_iva").equals(doce)){
						total12 = total12
								.add((set.getBigDecimal("valor") != null ? set
										.getBigDecimal("valor") : new BigDecimal(0)));
						baseImp12 = baseImp12
								.add((set.getBigDecimal("baseImp") != null ? set
										.getBigDecimal("baseImp") : new BigDecimal(
										0)));
	
						baseImp = set.getBigDecimal("baseImp") != null ? set
								.getBigDecimal("baseImp") : BigDecimal.ZERO;
						valor = set.getBigDecimal("valor") != null ? set
								.getBigDecimal("valor") : BigDecimal.ZERO;
	
						baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
						valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	
						impuestoXML = new Impuesto();
						impuestoXML.setCodigo("2");
						impuestoXML.setCodigoPorcentaje("2");
						impuestoXML.setTarifa(new BigDecimal(12));
						impuestoXML.setBaseImponible(baseImp);
						impuestoXML.setValor(valor);
						detalleImpuestos.add(impuestoXML);
					}else{
						//14%
						BigDecimal catorce = new BigDecimal("0.14");						
						if(set.getBigDecimal("porc_iva").equals(catorce)){
							total14 = total14
									.add((set.getBigDecimal("valor") != null ? set
											.getBigDecimal("valor") : new BigDecimal(0)));
							baseImp14 = baseImp14
									.add((set.getBigDecimal("baseImp") != null ? set
											.getBigDecimal("baseImp") : new BigDecimal(
											0)));

							baseImp = set.getBigDecimal("baseImp") != null ? set
									.getBigDecimal("baseImp") : BigDecimal.ZERO;
							valor = set.getBigDecimal("valor") != null ? set
									.getBigDecimal("valor") : BigDecimal.ZERO;

							baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
							valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);

							impuestoXML = new Impuesto();
							impuestoXML.setCodigo("2");
							impuestoXML.setCodigoPorcentaje("3");
							impuestoXML.setTarifa(new BigDecimal(14));
							impuestoXML.setBaseImponible(baseImp);
							impuestoXML.setValor(valor);
							detalleImpuestos.add(impuestoXML);
						}
					}
				}

				if (detalleImpuestos != null && !detalleImpuestos.isEmpty()) {
					detalleXML
							.setImpuestos(new NotaCredito.Detalles.Detalle.Impuestos());
					detalleXML.getImpuestos().getImpuesto()
							.addAll(detalleImpuestos);

				}
				// AG>>

				detalles.add(detalleXML);
			}

		}

		totalSinImpuesto = totalSinImpuesto.add(baseImp0);
		totalSinImpuesto = totalSinImpuesto.add(baseImp12);
		totalSinImpuesto = totalSinImpuesto.add(baseImp14);
		
		totalSinImpuesto = totalSinImpuesto.setScale(2,
				BigDecimal.ROUND_HALF_UP);
		log.info("****total sin impuesto****"+baseImp14);
		// 0%
		baseImp0 = baseImp0.setScale(2, BigDecimal.ROUND_HALF_UP);
		total0 = total0.setScale(2, BigDecimal.ROUND_HALF_UP);

		totalImpuestoXML = new TotalImpuesto();
		totalImpuestoXML.setCodigo("2");
		totalImpuestoXML.setCodigoPorcentaje("0");
		totalImpuestoXML.setBaseImponible(baseImp0);
		totalImpuestoXML.setValor(total0);
		totalesImpuestos.add(totalImpuestoXML);

		if (!total12.equals(new BigDecimal(0))) {
			// 12%
			baseImp12 = baseImp12.setScale(2, BigDecimal.ROUND_HALF_UP);
			total12 = total12.setScale(2, BigDecimal.ROUND_HALF_UP);

			totalImpuestoXML = new TotalImpuesto();
			totalImpuestoXML.setCodigo("2");
			totalImpuestoXML.setCodigoPorcentaje("2");
			totalImpuestoXML.setBaseImponible(baseImp12);
			totalImpuestoXML.setValor(total12);
			totalesImpuestos.add(totalImpuestoXML);
		}
		if (!total14.equals(new BigDecimal(0))) {
			// 14%
			baseImp14 = baseImp14.setScale(2, BigDecimal.ROUND_HALF_UP);
			total14 = total14.setScale(2, BigDecimal.ROUND_HALF_UP);

			totalImpuestoXML = new TotalImpuesto();
			totalImpuestoXML.setCodigo("2");
			totalImpuestoXML.setCodigoPorcentaje("3");
			totalImpuestoXML.setBaseImponible(baseImp14);
			totalImpuestoXML.setValor(total14);
			totalesImpuestos.add(totalImpuestoXML);
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		respuesta.put(Constantes.DETALLES, detalles);
		respuesta.put(Constantes.TOTAL_IMPUESTO, totalesImpuestos);
		respuesta.put(Constantes.TOTAL_SIN_IMPUESTO, totalSinImpuesto);

		return respuesta;
	}

	public String obtenerCorreo(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException {

		// PreparedStatement ps = null;
		CallableStatement cstmt = null;
		String correo = null;

		try {
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

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				// if (ps != null)
				// ps.close();
				if (cstmt != null)
					cstmt.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return correo;

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
			ps.setDate(4, respuesta.getFecha() != null ? new java.sql.Date(
					respuesta.getFecha().getTime()) : null);
			ps.setString(5, respuesta.getUsuario());
			ps.setString(6, respuesta.getFirmaE());
			ps.setString(7, respuesta.getAutFirmaE());
			ps.setDate(8,
					respuesta.getFechaFirmaE() != null ? new java.sql.Date(
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
			ps.setDate(4, respuesta.getFecha() != null ? new java.sql.Date(
					respuesta.getFecha().getTime()) : null);
			ps.setString(5, respuesta.getUsuario());
			ps.setString(6, respuesta.getFirmaE());
			ps.setString(7, respuesta.getAutFirmaE());
			ps.setDate(8,
					respuesta.getFechaFirmaE() != null ? new java.sql.Date(
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

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public com.gizlocorp.gnvoice.modelo.NotaCredito autoriza(
			com.gizlocorp.gnvoice.modelo.NotaCredito comprobante) {

		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = notaCreditoDAO.update(comprobante);

			} else {
				comprobante = notaCreditoDAO.persist(comprobante);

			}
		} catch (Exception ex) {
			try {
				InitialContext ic = new InitialContext();
				notaCreditoDAO = (NotaCreditoDAO) ic
						.lookup("java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = notaCreditoDAO.update(comprobante);

				} else {
					comprobante = notaCreditoDAO.persist(comprobante);

				}
			} catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}

		return comprobante;
	}

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
				comprobanteExistente = notaCreditoDAO.obtenerComprobante(null,
						mensaje.getCodigoExterno(), mensaje.getNotaCredito()
								.getInfoTributaria().getRuc(),
						mensaje.getAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				notaCreditoDAO = (NotaCreditoDAO) ic
						.lookup("java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO");
				comprobanteExistente = notaCreditoDAO.obtenerComprobante(null,
						mensaje.getCodigoExterno(), mensaje.getNotaCredito()
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


			mensaje.getNotaCredito().getInfoTributaria()
					.setClaveAcceso(claveAcceso);

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
							String comprobanteXML = respAutorizacionComExt
									.getComprobante();
							comprobanteXML = comprobanteXML.replace(
									"<![CDATA[", "");
							comprobanteXML = comprobanteXML.replace("]]>", "");

							com.gizlocorp.gnvoice.xml.notacredito.NotaCredito factAutorizadaXML = getNotaCreditoXML(comprobanteXML);
							ReporteUtil reporte = new ReporteUtil();

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

					autoriza(comprobante);

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

				autoriza(comprobante);

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
	
	private String obtenerDireccion(Connection conn, Long farmacia, Long factura)
			throws SQLException {
		PreparedStatement ps = null;
		String resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString
				.append("select direccion from fa_facturas where documento_venta = ? and farmacia = ?");

		ps = conn.prepareStatement(sqlString.toString());

		ps.setLong(1, factura);
		ps.setLong(2, farmacia);

		ResultSet set = ps.executeQuery();

		while (set.next()) {
			resultado = set.getString("direccion");
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		return resultado;
	}
	
	private String banderaOferton(Connection conn, Long farmacia) {
		PreparedStatement ps = null;
		String resultado = "N";
		try {

			StringBuilder sqlStringValOferton = new StringBuilder();
			sqlStringValOferton.append(" select nvl(valor, 'N') valor  ");
			sqlStringValOferton.append("  from fA_parametros_farmacia ");
			sqlStringValOferton.append(" where farmacia = ?           ");
			sqlStringValOferton.append("   and campo = 'PDV_OFERTON'  ");
			sqlStringValOferton.append("   and activo = 'S'           ");

			ps = conn.prepareStatement(sqlStringValOferton.toString());
			ps.setLong(1, farmacia);
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				resultado = set.getString("valor");
			}
			if (ps != null)
				ps.close();
			if (set != null)
				set.close();
		} catch (Exception e) {
		}
		return resultado;
	}
	
	private String obtenerCompensacion(Connection conn, Long farmacia)
			throws SQLException {
		PreparedStatement ps = null;
		String resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString
				.append("select valor from fa_parametros_farmacia where campo = 'IVA_COMPENSACION'  and farmacia = ?");

		ps = conn.prepareStatement(sqlString.toString());

		
		ps.setLong(1, farmacia);

		ResultSet set = ps.executeQuery();

		while (set.next()) {
			resultado = set.getString("valor") != null ? set.getString("valor").toString() : null;
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
		return resultado;
	}
	
	private List<Compensacion> listarCompensacionISS(Connection conn, Long idFactura, Long idFarmacia, boolean cartorce, boolean doce) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlStringSubsidio = new StringBuilder();	
		log.info("****obteniendo Compensacion"+idFarmacia+"---"+idFactura);
		sqlStringSubsidio.append("SELECT VENTA_FACTURA ");
		sqlStringSubsidio.append("FROM fa_detalles_formas_pago ");
		sqlStringSubsidio.append("where forma_pago = 'ISS' ");
		sqlStringSubsidio.append("AND DOCUMENTO_VENTA = ?");
		sqlStringSubsidio.append("AND FARMACIA = ? ");
	
		
		ps = conn.prepareStatement(sqlStringSubsidio.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			compensacionXml = new Compensacion();
			compensacionXml.setCodigo(new BigDecimal("1"));
			compensacionXml.setTarifa(new BigDecimal("2"));
			compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
			list.add(compensacionXml);
		}
		log.info("****obteniendo Compensacion 333"+list.size());
		
		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
				
		return list;
				
	}
	
	private List<Compensacion> listarCompensacionIDE(Connection conn, Long idFactura, Long idFarmacia, boolean cartorce, boolean doce) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlStringSubsidio = new StringBuilder();	
		
		sqlStringSubsidio.append("SELECT VENTA_FACTURA ");
		sqlStringSubsidio.append("FROM fa_detalles_formas_pago ");
		sqlStringSubsidio.append("where forma_pago = 'IDE' ");
		sqlStringSubsidio.append("AND DOCUMENTO_VENTA = ?");
		sqlStringSubsidio.append("AND FARMACIA = ? ");
	
		
		ps = conn.prepareStatement(sqlStringSubsidio.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			compensacionXml = new Compensacion();
			if(cartorce){
				compensacionXml.setCodigo(new BigDecimal("2"));
				compensacionXml.setTarifa(new BigDecimal("4"));
				compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
				list.add(compensacionXml);
			}else{
				if(doce){
					compensacionXml.setCodigo(new BigDecimal("5"));
					compensacionXml.setTarifa(new BigDecimal("2"));
					compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
					list.add(compensacionXml);
				}				
			}
			
			
		}
		log.info("****obteniendo Compensacion 333"+list.size());
		
		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
				
		return list;
				
	}
	
	private List<Compensacion> listarCompensacionITC(Connection conn, Long idFactura, Long idFarmacia, boolean cartorce, boolean doce) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlStringSubsidio = new StringBuilder();	
		
		sqlStringSubsidio.append("SELECT VENTA_FACTURA ");
		sqlStringSubsidio.append("FROM fa_detalles_formas_pago ");
		sqlStringSubsidio.append("where forma_pago = 'ITC' ");
		sqlStringSubsidio.append("AND DOCUMENTO_VENTA = ?");
		sqlStringSubsidio.append("AND FARMACIA = ? ");
	
		
		ps = conn.prepareStatement(sqlStringSubsidio.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			compensacionXml = new Compensacion();
			if(cartorce){
				compensacionXml.setCodigo(new BigDecimal("4"));
				compensacionXml.setTarifa(new BigDecimal("1"));
				compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
				list.add(compensacionXml);
			}else{
				if(doce){
					compensacionXml.setCodigo(new BigDecimal("7"));
					compensacionXml.setTarifa(new BigDecimal("1"));
					compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
					list.add(compensacionXml);
				}				
			}
		}
		log.info("****obteniendo Compensacion 333"+list.size());
		
		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
				
		return list;
				
	}

	private String getNotaCreditoXML(Object comprobante)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.notacredito.NotaCredito getNotaCreditoXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirAObjeto(comprobanteXML,
				com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.class);
	}

	private Autorizacion getAutorizacionXML(String comprobanteXML)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirAObjeto(comprobanteXML, Autorizacion.class);
	}
}
