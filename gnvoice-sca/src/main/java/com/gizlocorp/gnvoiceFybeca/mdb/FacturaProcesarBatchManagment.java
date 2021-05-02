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
import com.gizlocorp.gnvoice.dao.FacturaDAO;
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
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.service.impl.CacheBean;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Compensaciones.Compensacion;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Pagos;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Pagos.Pago;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoice.xml.factura.Impuesto;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarResponse;
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
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/facturaProcesarBatchQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "700"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "45000") })
@DependsOn("CacheBean")
public class FacturaProcesarBatchManagment implements MessageListener {

	@EJB(lookup = "java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO")
	FacturaDAO servicioFactura;
	@EJB
	CacheBean cacheBean;
	
	BigDecimal total14General = new BigDecimal(0f), baseImp14 = new BigDecimal(0f);

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	private static Logger log = Logger.getLogger(FacturaProcesarBatchManagment.class.getName());

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
				tipo = obtenerTipo(conn,ComprobanteEnum.FACTURA.getDescripcion());

				if (tipo == null) {
					tipo = 1L;

				}
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
				log.debug("FACTURAS A PROCESAR (SIN PROCESAR): Listado de facturas es: "
						+ listado.size()
						+ (listado.size() > 1000 ? "*******************************************************************************************************************************"
								: "")
						+ ", Farmacia: "
						+ farmacia
						+ ", tipo: "
						+ tipo);

				FacturaProcesarRequest item = null;
				FacturaProcesarResponse response = null;
				Respuesta respuesta = null;

				for (Long documentoVenta : listado) {
					try {
						conn = Conexion.obtenerConexionFybeca(credencialDS);
						if (tipo == null) {
							tipo = obtenerTipo(conn,ComprobanteEnum.FACTURA.getDescripcion());
							if (tipo == null) {
								tipo = 1L;
							}
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
									//response = procesar(item);
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
									if (response.getEstado().equals(Estado.CANCELADO.getDescripcion())
											|| response.getEstado().equals(Estado.ERROR.getDescripcion())
											|| response.getEstado().equals(Estado.DEVUELTA.getDescripcion())
											|| response.getEstado().equals(Estado.RECHAZADO.getDescripcion())) {
										respuesta.setEstado(EstadoEnum.RECHAZADO.getDescripcion());
									}

									respuesta.setFecha(Calendar.getInstance().getTime());
									respuesta.setFechaFirmaE(response.getFechaAutorizacion() != null ? response.getFechaAutorizacion(): null);

									if (response.getEstado().equals(EstadoEnum.APROBADO.getDescripcion())) {
										respuesta.setFirmaE(GeneradoEnum.SI.getDescripcion());
									} else {
										respuesta.setFirmaE(GeneradoEnum.NO.getDescripcion());
									}

									respuesta.setIdDocumento(documentoVenta);
									respuesta.setIdFarmacia(farmacia);
									respuesta.setObservacion(response.getEstado()+ " "+ response.getMensajeSistema());
									respuesta.setTipoComprobante(tipo);
									respuesta.setTipoProceso(ProcesoEnum.BATCH.getDescripcion());
									respuesta.setUsuario("");
									respuesta.setSid(sid);

									// inqueue(respuesta);

									if (respuesta.getObservacion() != null && !respuesta.getObservacion().isEmpty()) {
										try {
											respuesta.setObservacion(respuesta.getObservacion().replaceAll("[^\\x00-\\x7F]",""));
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
						log.error("Error proceso BATCH: " + farmacia + "  "+ sid + e.getMessage(), e);
					} finally {
						try {
							if (conn != null)
								conn.close();
						} catch (Exception ex) {
							log.error("Error cerrar conexion: " + ex.getMessage(),ex);
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
	                respuesta.setMensajeSistema("Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
	                respuesta.setMensajeCliente("Datos de comprobante incorrectos. Factura vacia");
	                return respuesta;
	            }

	            emisor = cacheBean.obtenerOrganizacion(mensaje.getFactura().getInfoTributaria().getRuc());

	            if (emisor == null) {
	                respuesta.setEstado(Estado.ERROR.getDescripcion());
	                respuesta.setClaveAccesoComprobante(null);
	                respuesta.setFechaAutorizacion(null);
	                respuesta.setNumeroAutorizacion(null);
	                respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "+ mensaje.getFactura().getInfoTributaria().getRuc());
	                respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "+ mensaje.getFactura().getInfoTributaria().getRuc());
	                return respuesta;
	            }

	            if (mensaje.getFactura().getInfoTributaria().getSecuencial() == null|| mensaje.getFactura().getInfoTributaria().getSecuencial().isEmpty()) {
	                respuesta.setEstado(Estado.ERROR.getDescripcion());
	                respuesta.setClaveAccesoComprobante(null);
	                respuesta.setFechaAutorizacion(null);
	                respuesta.setNumeroAutorizacion(null);
	                respuesta.setMensajeSistema("Secuencial no puede ser NULL");
	                respuesta.setMensajeCliente("Secuencial no puede ser NULL");
	                return respuesta;
	            }
	            
	          // emisor.setLogoEmpresa(emisor.getLogoEmpresa());
	            
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
	            wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
	            wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

	            com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;

	            try {
	                comprobanteExistente = servicioFactura.obtenerComprobante(null,mensaje.getCodigoExterno(), mensaje.getFactura().getInfoTributaria().getRuc(),mensaje.getAgencia());
	            } catch (Exception ex) {
	                InitialContext ic = new InitialContext();
	                servicioFactura = (FacturaDAO) ic.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
	                comprobanteExistente = servicioFactura.obtenerComprobante(null,mensaje.getCodigoExterno(), mensaje.getFactura().getInfoTributaria().getRuc(),mensaje.getAgencia());

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
	            mensaje.getFactura().setId("comprobante");
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

	            mensaje.getFactura().getInfoTributaria().setDirMatriz(dirMatriz != null ? StringEscapeUtils.escapeXml(dirMatriz.trim()).replaceAll("[\n]", "").replaceAll("[\b]", "") : null);

	            if (mensaje.getFactura().getInfoTributaria().getEstab() == null|| mensaje.getFactura().getInfoTributaria().getEstab().isEmpty()) {
	                mensaje.getFactura().getInfoTributaria().setEstab(emisor.getEstablecimiento());

	            }

	            if (mensaje.getFactura().getInfoTributaria().getNombreComercial() == null|| mensaje.getFactura().getInfoTributaria().getNombreComercial().isEmpty()) {
	                mensaje.getFactura().getInfoTributaria().setNombreComercial(
	                								emisor.getNombreComercial() != null ? StringEscapeUtils.escapeXml(emisor.getNombreComercial().trim())
	                                        .replaceAll("[\n]", "")
	                                        .replaceAll("[\b]", "") : null);
	            }

	            if (mensaje.getFactura().getInfoTributaria().getPtoEmi() == null|| mensaje.getFactura().getInfoTributaria().getPtoEmi().isEmpty()) {
	                mensaje.getFactura().getInfoTributaria().setPtoEmi(emisor.getPuntoEmision());
	            }
	            mensaje.getFactura()
	                    .getInfoTributaria()
	                    .setRazonSocial(emisor.getNombreComercial().trim() != null ? StringEscapeUtils.escapeXml(emisor.getNombreComercial().trim()) : null);

	            mensaje.getFactura()
	                    .getInfoFactura()
	                    .setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
	            if (mensaje.getFactura().getInfoFactura().getDirEstablecimiento() == null|| mensaje.getFactura().getInfoFactura().getDirEstablecimiento().isEmpty()) {
	                mensaje.getFactura()
	                        .getInfoFactura()
	                        .setDirEstablecimiento(
	                                emisor.getDirEstablecimiento() != null ? StringEscapeUtils
	                                        .escapeXml(emisor
	                                                .getDirEstablecimiento())
	                                        : null);
	            }
	            mensaje.getFactura()
	                    .getInfoFactura()
	                    .setObligadoContabilidad(
	                            emisor.getEsObligadoContabilidad().getDescripcion()
	                                    .toUpperCase());

	            StringBuilder codigoNumerico = new StringBuilder();
	            codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getEstab());
	            codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getPtoEmi());
	            log.debug("codigo numerico - secuencial de factura: "+ mensaje.getFactura().getInfoTributaria().getSecuencial());
	            codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
	            codigoNumerico.append(Constantes.CODIGO_NUMERICO);

	            claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje.getFactura().getInfoFactura().getFechaEmision()
	            														,codFacParametro.getValor()
	            														, mensaje.getFactura().getInfoTributaria().getRuc()
	            														, mensaje.getFactura().getInfoTributaria().getAmbiente()
	            														, codigoNumerico.toString());

	            log.info("codigo numerico - secuencial de factura: " + claveAcceso);
	            mensaje.getFactura().getInfoTributaria().setClaveAcceso(claveAcceso);


	            // -jose--------------------------------------------------------------------------------------------------------------------------------*/

	            log.info("finaliza validaciones previas");
	            log.debug("Inicia generacion de comproabante");
	            comprobante = ComprobanteUtil.convertirEsquemaAEntidadFactura(mensaje.getFactura());

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
	                respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null? 
	                							   respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
	                							   : null);
	                respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
	                respuesta.setClaveAccesoComprobante(claveAcceso);

	                respuesta.setMensajeSistema("Comprobante AUTORIZADO");
	                comprobante.setNumeroAutorizacion(claveAcceso);
	                comprobante.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null ? 
	                								 respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
	                								 : null);

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
		                if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
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
		                    mailMensaje.setSubject(t
		                            .getTitulo()
		                            .replace("$tipo", "FACTURA")
		                            .replace("$numero",mensaje.getFactura().getInfoTributaria().getSecuencial()));
		                    mailMensaje.setFrom(cacheBean.consultarParametro(
		                    		"S".equals(mensaje.getBanderaOferton())?Constantes.CORREO_REMITE_EL_OFERTON:Constantes.CORREO_REMITE, emisor.getId()).getValor());
		                    mailMensaje.setTo(Arrays.asList(mensaje.getCorreoElectronicoNotificacion().split(";")));
		                    mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(parametrosBody
		                    														,t.getDescripcion()
		                    														, t.getValor()));
		                    
		                    mailMensaje.setAttachment(Arrays.asList(rutaArchivoAutorizacionXml, rutaArchivoAutorizacionPdf));
		                    
		                    MailDelivery.send(mailMensaje
		                    				 ,cacheBean.consultarParametro(Constantes.SMTP_HOST
		                    						 					   ,emisor.getId()).getValor()
		                    				 							   ,cacheBean.consultarParametro(Constantes.SMTP_PORT
		                    				 									   						 ,emisor.getId()).getValor()
		                    				 							   ,cacheBean.consultarParametro(Constantes.SMTP_USERNAME
		                    				 									   						 ,emisor.getId()).getValor()
		                    				 							   ,cacheBean.consultarParametro(Constantes.SMTP_PASSWORD
		                    				 									   						 ,emisor.getId()).getValorDesencriptado()
		                    				 							   ,"S".equals(mensaje.getBanderaOferton())?emisor.getAcronimoOpcional():emisor.getAcronimo());
		                }

		            } catch (Exception ex) {
		                log.error(ex.getMessage(), ex);
		            }
		            
		            log.info("Autorizado primer intento bacht " + claveAcceso);
	               autorizaOffline(comprobante);

	                return respuesta;
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

	            log.info("comprobante offline nuevo " + claveAcceso);

	            // Genera comprobante
	            rutaArchivoXml = DocumentUtil.createDocument(getFacturaXML(mensaje.getFactura())
	            											 , mensaje.getFactura().getInfoFactura().getFechaEmision()
	            											 , claveAcceso
	            											 , com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA.getDescripcion()
	            											 , "enviado");
	            
	            log.info("ruta de archivo es: " + rutaArchivoXml + "ruta xsd"+ com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+ "/gnvoice/recursos/xsd/factura.xsd");

	            String validacionXsd = ComprobanteUtil.validaArchivoXSD(rutaArchivoXml,com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+ "/gnvoice/recursos/xsd/factura.xsd");

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
	            respuesta.setMensajeSistema("No se puede desencriptar clave Firma: "+ ex.getMessage());
	            respuesta.setMensajeCliente("No se puede desencriptar clave Firma");
	            comprobante.setArchivo(rutaArchivoXml);
	            return respuesta;
	        }

	        firmaRequest.setRutaFirma(emisor.getToken());
	        firmaRequest.setRutaArchivo(rutaArchivoXml);
	        FirmaElectronicaResponse firmaResponse = null;

	        try {
	            ((BindingProvider) firmaElectronica).getRequestContext().put( "com.sun.xml.ws.connect.timeout", 5000L);
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
					Thread.sleep(60000L);
				} catch (InterruptedException e) {
					log.debug(e.getMessage(), e);
				}
	            
	            ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respAutorizacionComExt = autorizarComprobanteOffline(wsdlLocationAutorizacion, claveAcceso);

	            log.info("pasando validacion de autorizacion " + claveAcceso);

	            if ("AUTORIZADO".equals(respAutorizacionComExt.getEstado())) {

	                respuesta.setMensajeCliente("Comprobante  AUTORIZADO");

	                respuesta.setEstado(EstadoOffline.AUTORIZADO.getDescripcion());
	                respuesta.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null? 
	                							   respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
	                							   : null);
	                respuesta.setNumeroAutorizacion(respAutorizacionComExt.getNumeroAutorizacion());
	                respuesta.setClaveAccesoComprobante(claveAcceso);

	                respuesta.setMensajeSistema("Comprobante AUTORIZADO");

	                comprobante.setNumeroAutorizacion(claveAcceso);
	                comprobante.setFechaAutorizacion(respAutorizacionComExt.getFechaAutorizacion() != null ? 
	                								 respAutorizacionComExt.getFechaAutorizacion().toGregorianCalendar().getTime()
	                								 : null);

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
	                
	                log.info("XML: " + rutaArchivoAutorizacionXml+" PDF "+rutaArchivoAutorizacionPdf);

		            try {
		                if (mensaje.getCorreoElectronicoNotificacion() != null && !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
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
		                    mailMensaje.setSubject(t.getTitulo().replace("$tipo", "FACTURA").replace("$numero"
		                            			   , mensaje.getFactura().getInfoTributaria().getSecuencial()));
		                    mailMensaje.setFrom(cacheBean.consultarParametro("S".equals(mensaje.getBanderaOferton())?Constantes.CORREO_REMITE_EL_OFERTON:Constantes.CORREO_REMITE, emisor.getId()).getValor());
		                    mailMensaje.setTo(Arrays.asList(mensaje.getCorreoElectronicoNotificacion().split(";")));
		                    mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(
		                            parametrosBody, t.getDescripcion(), t.getValor()));
		                    mailMensaje.setAttachment(Arrays.asList(
		                            rutaArchivoFirmadoXml, rutaArchivoAutorizacionPdf));
		                    MailDelivery.send(mailMensaje
		                    				 ,cacheBean.consultarParametro(Constantes.SMTP_HOST,emisor.getId()).getValor()
		                    				 ,cacheBean.consultarParametro(Constantes.SMTP_PORT,emisor.getId()).getValor()
		                    				 ,cacheBean.consultarParametro(Constantes.SMTP_USERNAME, emisor.getId()).getValor()
		                    				 ,cacheBean.consultarParametro(Constantes.SMTP_PASSWORD, emisor.getId()).getValorDesencriptado()
		                    				 ,"S".equals(mensaje.getBanderaOferton())?emisor.getAcronimoOpcional():emisor.getAcronimo());
		                }

		            } catch (Exception ex) {
		                log.error(ex.getMessage(), ex);
		            }
		            
		            log.info("Autorizado segunda Opcion .. ->" + comprobante.getId());
	                autorizaOffline(comprobante);
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
	  
	  
	  private ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud enviarComprobanteOffline(String wsdlLocation, String rutaArchivoXml) {
	        ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuesta = null;

	        int retardo = 8000;

	        try {
	            byte[] documentFirmado = DocumentUtil.readFile(rutaArchivoXml);
	            log.info("Inicia proceso reccepcion offline SRI ..." + wsdlLocation + " ..." + rutaArchivoXml);
	            // while (contador <= reintentos) {
	            try {
	                ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService servicioRecepcion = new ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService(
	                        new URL(wsdlLocation));

	                // log.info("Inicia proceso reccepcion offline SRI 111...");
	                ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion.getRecepcionComprobantesOfflinePort();

	                // log.info("Inicia proceso reccepcion offline SRI ...222");

	                ((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.connect.timeout", retardo);
	                ((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", retardo);

	                ((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", retardo);
	                ((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.request.timeout", retardo);

	                // log.info("Validando en reccepcion offline SRI
	                // 333..."+documentFirmado.length);

	                respuesta = recepcion.validarComprobante(documentFirmado);

	                log.info("Validando en reccepcion offline SRI 444..."+ respuesta.getEstado());

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

	
	  private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation, String claveAcceso) {
	        ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

	        // log.info("CLAVE DE ACCESO: " + claveAcceso);
	        int timeout = 7000;

	        try {
	            ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion 
	            = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
	                    new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion","AutorizacionComprobantesOfflineService"));

	            ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion.getAutorizacionComprobantesOfflinePort();

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
	                    respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);
	                }
	            }

	            if (respuestaComprobanteAut != null
	                    && respuestaComprobanteAut.getAutorizaciones() != null
	                    && respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
	                    && !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
	                    && respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {

	                ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacion = respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0);

	                for (ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion aut : respuestaComprobanteAut
	                        .getAutorizaciones().getAutorizacion()) {
	                    if (aut != null && "AUTORIZADO".equals(aut.getEstado())) {
	                        autorizacion = aut;
	                        break;
	                    }
	                }

	                autorizacion.setAmbiente(null);

	                autorizacion.setComprobante("<![CDATA["+ autorizacion.getComprobante() + "]]>");

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

	private Autorizacion autorizarComprobante(String wsdlLocation,String claveAcceso) {
		Autorizacion respuesta = null;

		// log.debug("CLAVE DE ACCESO: " + claveAcceso);
		int timeout = 7000;

		try {
			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion","AutorizacionComprobantesService"));

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
					respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);
				}
			}

			if (respuestaComprobanteAut != null
					&& respuestaComprobanteAut.getAutorizaciones() != null
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

				autorizacion.setAmbiente(null);

				autorizacion.setComprobante("<![CDATA["+ autorizacion.getComprobante() + "]]>");

				if (autorizacion.getMensajes() != null
						&& autorizacion.getMensajes().getMensaje() != null
						&& !autorizacion.getMensajes().getMensaje().isEmpty()) {

					for (Mensaje msj : autorizacion.getMensajes().getMensaje()) {
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
			respuesta = new Autorizacion();
			respuesta.setEstado("ERROR");
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new Autorizacion();
			respuesta.setEstado("ERROR");

		}

		return respuesta;
	}

	private RespuestaSolicitud enviarComprobante(String wsdlLocation,String rutaArchivoXml) {
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

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FacturaProcesarResponse procesar(FacturaProcesarRequest mensaje) {
		FacturaProcesarResponse respuesta = new FacturaProcesarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

		// log.debug("Inicia proceso facturacion");
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

		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// GENERA DE COMPROBANTE
		// TODO contigencia
		// boolean existioComprobante = false;
		try {

			// log.debug("Inicia validaciones previas");
			if (mensaje.getFactura() == null|| mensaje.getFactura().getInfoTributaria() == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema("Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
				respuesta.setMensajeCliente("Datos de comprobante incorrectos. Factura vacia");

				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getFactura().getInfoTributaria().getRuc());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "+ mensaje.getFactura().getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "+ mensaje.getFactura().getInfoTributaria().getRuc());
				return respuesta;
			}

			if (mensaje.getFactura().getInfoTributaria().getSecuencial() == null || mensaje.getFactura().getInfoTributaria().getSecuencial().isEmpty()) {
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

			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				wsdlLocationRecepcion = cacheBean.consultarParametro(Constantes.SERVICIO_REC_SRI_PRODUCCION, emisor.getId()).getValor();

			} else {
				wsdlLocationRecepcion = cacheBean.consultarParametro(Constantes.SERVICIO_REC_SRI_PRUEBA, emisor.getId()).getValor();
			}
			if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				wsdlLocationAutorizacion = cacheBean.consultarParametro(Constantes.SERVICIO_AUT_SRI_PRODUCCION, emisor.getId()).getValor();
			} else {
				wsdlLocationAutorizacion = cacheBean.consultarParametro(Constantes.SERVICIO_AUT_SRI_PRUEBA, emisor.getId()).getValor();
			}

			com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;
			try {
				comprobanteExistente = servicioFactura.obtenerComprobante(null,mensaje.getCodigoExterno(), mensaje.getFactura().getInfoTributaria().getRuc(),mensaje.getAgencia());
			} catch (Exception ex) {
				try {
					InitialContext ic = new InitialContext();
					servicioFactura = (FacturaDAO) ic.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
					comprobanteExistente = servicioFactura.obtenerComprobante(null, mensaje.getCodigoExterno(), mensaje.getFactura().getInfoTributaria().getRuc(),mensaje.getAgencia());
				} catch (Exception ex2) {
					log.warn("********* no actualiza soluciones", ex2);
				}
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

			mensaje.getFactura()
					.getInfoTributaria()
					.setDirMatriz(
							dirMatriz != null ? StringEscapeUtils
									.escapeXml(dirMatriz.trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "") : null);

			if (mensaje.getFactura().getInfoTributaria().getEstab() == null
					|| mensaje.getFactura().getInfoTributaria().getEstab()
							.isEmpty()) {
				mensaje.getFactura().getInfoTributaria()
						.setEstab(emisor.getEstablecimiento());

			}
			if (mensaje.getFactura().getInfoTributaria().getNombreComercial() == null
					|| mensaje.getFactura().getInfoTributaria()
							.getNombreComercial().isEmpty()) {

				mensaje.getFactura()
						.getInfoTributaria()
						.setNombreComercial(
								emisor.getNombreComercial() != null ? StringEscapeUtils
										.escapeXml(
												emisor.getNombreComercial()
														.trim())
										.replaceAll("[\n]", "")
										.replaceAll("[\b]", "") : null);
			}

			if (mensaje.getFactura().getInfoTributaria().getPtoEmi() == null
					|| mensaje.getFactura().getInfoTributaria().getPtoEmi()
							.isEmpty()) {
				mensaje.getFactura().getInfoTributaria()
						.setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getFactura()
					.getInfoTributaria()
					.setRazonSocial(
							emisor.getNombre() != null ? StringEscapeUtils
									.escapeXml(emisor.getNombre()) : null);

			mensaje.getFactura()
					.getInfoFactura()
					.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			if (mensaje.getFactura().getInfoFactura().getDirEstablecimiento() == null
					|| mensaje.getFactura().getInfoFactura()
							.getDirEstablecimiento().isEmpty()) {
				mensaje.getFactura()
						.getInfoFactura()
						.setDirEstablecimiento(
								emisor.getDirEstablecimiento() != null ? StringEscapeUtils
										.escapeXml(emisor
												.getDirEstablecimiento())
										: null);
			}
			mensaje.getFactura()
					.getInfoFactura()
					.setObligadoContabilidad(
							emisor.getEsObligadoContabilidad().getDescripcion()
									.toUpperCase());

			// TODO contigencia
			// if (existioComprobante
			// && comprobanteExistente != null
			// && comprobanteExistente.getTipoEmision().equals(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo())) {
			// claveAcceso = comprobanteExistente.getClaveAcceso();
			// mensaje.getFactura()
			// .getInfoTributaria()
			// .setTipoEmision(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo());
			// } else {
			// genera clave de acceso
			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getEstab());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getPtoEmi());
			// log.debug("codigo numerico - secuencial de factura: "
			// + mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);

			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje.getFactura().getInfoFactura().getFechaEmision(),
					codFacParametro.getValor(), mensaje.getFactura()
							.getInfoTributaria().getRuc(), mensaje.getFactura()
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
			// .generarClaveAccesoProveedor(mensaje.getFactura()
			// .getInfoFactura().getFechaEmision(),
			// codFacParametro.getValor(),
			// claveNoUsada.getClave());
			// mensaje.getFactura()
			// .getInfoTributaria()
			// .setTipoEmision(
			// TipoEmisionEnum.CONTINGENCIA.getCodigo());
			// mensaje.getFactura().getInfoTributaria()
			// .setClaveAcceso(claveAcceso);
			// }
			// } else {
			mensaje.getFactura().getInfoTributaria().setClaveAcceso(claveAcceso);
			// }
			// -jose--------------------------------------------------------------------------------------------------------------------------------*/

			// log.debug("finaliza validaciones previas");
			// log.debug("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil.convertirEsquemaAEntidadFactura(mensaje.getFactura());

			comprobante.setClaveInterna(mensaje.getCodigoExterno());
			comprobante.setCorreoNotificacion(mensaje.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getAgencia());

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			comprobante.setSecuencialOriginal(mensaje.getSid());
			
			
			Autorizacion respAutorizacionComExt = autorizarComprobante(wsdlLocationAutorizacion, claveAcceso);

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

							com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobanteXML);
							ReporteUtil reporte = new ReporteUtil();
							rutaArchivoAutorizacionPdf = reporte
									.generarReporte(
											Constantes.REP_FACTURA,
											new FacturaReporte(
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
											factAutorizadaXML.getInfoFactura()
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
										getFacturaXML(respAutorizacionComExt),
										mensaje.getFactura().getInfoFactura()
												.getFechaEmision(),
										claveAcceso, TipoComprobante.FACTURA
												.getDescripcion(), "autorizado");

					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}

					// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje
					// .getFactura().getInfoFactura()
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
							parametrosBody
									.put("nombre", mensaje.getFactura()
											.getInfoFactura()
											.getRazonSocialComprador());
							parametrosBody.put("tipo", "FACTURA");
							parametrosBody.put("secuencial", mensaje
									.getFactura().getInfoTributaria()
									.getSecuencial());
							parametrosBody.put("fechaEmision", mensaje
									.getFactura().getInfoFactura()
									.getFechaEmision());
							parametrosBody.put("total", mensaje.getFactura()
									.getInfoFactura().getImporteTotal()
									.toString());
							parametrosBody.put("emisor", emisor.getNombre());
							parametrosBody.put("ruc", emisor.getRuc());
							parametrosBody.put("numeroAutorizacion",
									respAutorizacionComExt
											.getNumeroAutorizacion());

							MailMessage mailMensaje = new MailMessage();
							mailMensaje.setSubject(t
									.getTitulo()
									.replace("$tipo", "FACTURA")
									.replace(
											"$numero",
											mensaje.getFactura()
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
			rutaArchivoXml = DocumentUtil.createDocument(getFacturaXML(mensaje
					.getFactura()), mensaje.getFactura().getInfoFactura()
					.getFechaEmision(), claveAcceso,
					com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA
							.getDescripcion(), "enviado");
			// log.debug("ruta de archivo es: " + rutaArchivoXml);

			String validacionXsd = ComprobanteUtil.validaArchivoXSD(
					rutaArchivoXml,
					com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
							+ "/gnvoice/recursos/xsd/factura.xsd");

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
				// log.debug("comprobante: " + comprobante + " archivo: "
				// + rutaArchivoXml);
				comprobante.setArchivo(rutaArchivoXml);
				return respuesta;
			}

			// log.debug("Comprobante Generado Correctamente!!");
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
				Thread.sleep(500L);
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

						com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobanteXML);

						ReporteUtil reporte = new ReporteUtil();
						rutaArchivoAutorizacionPdf = reporte.generarReporte(
								Constantes.REP_FACTURA, new FacturaReporte(
										factAutorizadaXML), respAutorizacion
										.getNumeroAutorizacion(), FechaUtil
										.formatearFecha(respAutorizacion
												.getFechaAutorizacion()
												.toGregorianCalendar()
												.getTime(),
												FechaUtil.patronFechaTiempo24),
								factAutorizadaXML.getInfoFactura()
										.getFechaEmision(), "autorizado",
								false, emisor.getLogoEmpresa());
					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}
				}
				respuesta.setMensajeCliente("Comprobante  AUTORIZADO");
				try {
					rutaArchivoAutorizacionXml = DocumentUtil.createDocument(
							getFacturaXML(respAutorizacion), mensaje
									.getFactura().getInfoFactura()
									.getFechaEmision(), claveAcceso,
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

				// usuarioUsuarioLocal.crearUsuarioConsulta(mensaje.getFactura()
				// .getInfoFactura().getIdentificacionComprador());

				try {
					if (mensaje.getCorreoElectronicoNotificacion() != null
							&& !mensaje.getCorreoElectronicoNotificacion()
									.isEmpty()) {
						Plantilla t = cacheBean.obtenerPlantilla(
								Constantes.NOTIFIACION, emisor.getId());

						Map<String, Object> parametrosBody = new HashMap<String, Object>();
						parametrosBody.put("nombre", mensaje.getFactura()
								.getInfoFactura().getRazonSocialComprador());
						parametrosBody.put("tipo", "FACTURA");
						parametrosBody.put("secuencial", mensaje.getFactura()
								.getInfoTributaria().getSecuencial());
						parametrosBody.put("fechaEmision", mensaje.getFactura()
								.getInfoFactura().getFechaEmision());
						parametrosBody.put("total", mensaje.getFactura()
								.getInfoFactura().getImporteTotal());
						parametrosBody.put("emisor", emisor.getNombre());
						parametrosBody.put("ruc", emisor.getRuc());
						parametrosBody.put("numeroAutorizacion",
								respAutorizacion.getNumeroAutorizacion());

						MailMessage mailMensaje = new MailMessage();
						mailMensaje.setSubject(t
								.getTitulo()
								.replace("$tipo", "FACTURA")
								.replace(
										"$numero",
										mensaje.getFactura()
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
				respuesta.setMensajeCliente("TRANSMITIDO SIN RESPUESTA AUTORIZACION");
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


	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Factura autoriza(Factura comprobante) {

		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = servicioFactura.update(comprobante);

			} else {
				comprobante = servicioFactura.persist(comprobante);

			}
		} catch (Exception ex) {
			try {
				InitialContext ic = new InitialContext();
				servicioFactura = (FacturaDAO) ic
						.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = servicioFactura.update(comprobante);

				} else {
					comprobante = servicioFactura.persist(comprobante);

				}
			} catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}

		return comprobante;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Factura autorizaOffline(Factura comprobante) {

		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = servicioFactura.update(comprobante);

			} else {
				comprobante = servicioFactura.persist(comprobante);

			}
		} catch (Exception ex) {
			try {
				InitialContext ic = new InitialContext();
				servicioFactura = (FacturaDAO) ic
						.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = servicioFactura.update(comprobante);

				} else {
					comprobante = servicioFactura.persist(comprobante);

				}
			} catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}

		return comprobante;
	}

	private String getFacturaXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirAObjeto(comprobanteXML,
				com.gizlocorp.gnvoice.xml.factura.Factura.class);
	}

	public List<Long> listarDocumentos(Connection conn, Long farmacia, Long tipo)
			throws GizloException {

		PreparedStatement ps = null;
		ResultSet set = null;
		List<Long> facturas = new ArrayList<Long>();

		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append("select f.documento_venta as codigo_externo ");
			sqlString.append("from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString.append("and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta  ");
			sqlString.append("and c.codigo = fa.farmacia and f.farmacia = ? ");

			// sqlString.append("and f.fecha >= to_date('01/10/2014','dd/mm/yyyy') ");
			sqlString.append("and f.fecha >= sysdate-91 ");
			sqlString.append("and not exists (select documento from farmacias.FA_DATOS_SRI_ELECTRONICA ");
			sqlString.append("where farmacia = ? and TIPO_COMPROBANTE = ? and estado in ('A','C') and aut_firmae is not null ");
			sqlString.append("and FA_DATOS_SRI_ELECTRONICA.Documento = f.documento_venta and FA_DATOS_SRI_ELECTRONICA.Farmacia = f.farmacia) ");
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

	private FacturaProcesarRequest listar(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException {

		PreparedStatement ps = null;
		ResultSet set = null;
		FacturaProcesarRequest facturaFybeca = null;

		if (documentoVenta == null || documentoVenta == 0) {
			throw new GizloException("numero de documento no existe");
		}
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select fa_secs.secuencia as secuencial, f.fecha as fechaEmision, c.calle||' '||c.numero||' '||c.interseccion as dirEstablecimiento, ");
			sqlString
					.append("fa.cp_char3 as contribuyenteEspecial, 'SI' as obligadoContabilidad, c.NOMBRE as nombreSucursal, ");
			sqlString
					.append("(f.primer_apellido ||' '|| f.segundo_apellido ||' '|| f.nombres) as razonSocialComprador, f.identificacion as identificacionComprador, ");
			sqlString
					.append("ROUND((f.venta_total_factura - f.valor_iva),2) as totalSinImpuestos, ");
			sqlString
					.append("'0' as totalDescuento, '0.00' as propina, ROUND(venta_total_factura,2) as importeTotal, ");
			sqlString
					.append("'DOLAR' as moneda, '1' as ambiente, '1' as tipoEmision, ");
			sqlString
					.append("fa.cp_var3 as razonSocial, fa.cp_var3 as nombreComercial, fa.cp_var6 as ruc, '01' as codDoc, ");
			sqlString
					.append("substr(c.numero_ruc, 11, 3) as estab, substr(f.numero_sri, 5, 3) as ptoEmi, ");
			// substr(c.numero_ruc, 11, 3) as ptoEmi, substr(f.numero_sri, 5, 3)
			// as estab
			sqlString.append("f.documento_venta as codigo_externo ");
			sqlString
					.append("from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString
					.append("and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta  ");
			sqlString.append("and c.codigo = fa.farmacia and f.farmacia = ? ");

			sqlString.append("and f.documento_venta = ? ");
			sqlString.append("and fa_secs.tipo_documento = ? ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);
			ps.setLong(3, tipo);

			set = ps.executeQuery();
			Map<String, Object> detalesTotalesImpuesto = null;
			List<Detalle> detalles = null;
			List<com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto> totalesImpuestos = null;
			CampoAdicional documentoInterno = null;
			CampoAdicional email = null;
			CampoAdicional clientaAbf = null;

			while (set.next()) {
				facturaFybeca = null;
				try {
					facturaFybeca = obtenerFactura(conn, set, farmacia, tipo,
							documentoVenta);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}

				if (facturaFybeca != null) {
					String correo = obtenerCorreo(conn,
							Long.valueOf(facturaFybeca.getCodigoExterno()),
							farmacia);

					facturaFybeca.setCorreoElectronicoNotificacion(StringUtil
							.validateEmail(correo));

					// AG<<
					detalesTotalesImpuesto = listarDetalles(conn,
							Long.valueOf(facturaFybeca.getCodigoExterno()),
							farmacia);

					detalles = (List<Detalle>) detalesTotalesImpuesto
							.get(Constantes.DETALLES);
					totalesImpuestos = (List<com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto>) detalesTotalesImpuesto
							.get(Constantes.TOTAL_IMPUESTO);
					facturaFybeca
							.getFactura()
							.getInfoFactura()
							.setTotalSinImpuestos(
									(BigDecimal) detalesTotalesImpuesto
											.get(Constantes.TOTAL_SIN_IMPUESTO));
					
					//compensacion ojo
					//pagos ojo
					
					/*List<Compensacion> compensacions = listarCompensacion(conn, documentoVenta, farmacia);
					if(compensacions != null && !compensacions.isEmpty()){
						facturaFybeca.getFactura().getInfoFactura().setCompensaciones(new Compensaciones());
						facturaFybeca.getFactura().getInfoFactura().getCompensaciones().getCompensacion().addAll(compensacions);
					}*/

					// AG>>

					if (totalesImpuestos != null && !totalesImpuestos.isEmpty()) {
						facturaFybeca
								.getFactura()
								.getInfoFactura()
								.setTotalConImpuestos(
										new com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos());
						facturaFybeca.getFactura().getInfoFactura()
								.getTotalConImpuestos().getTotalImpuesto()
								.addAll(totalesImpuestos);
					}

					// DETALLE IMPUESTO
					if (detalles != null && !detalles.isEmpty()) {
						facturaFybeca.getFactura().setDetalles(new Detalles());
						facturaFybeca.getFactura().getDetalles().getDetalle()
								.addAll(detalles);
					}

					if (facturaFybeca.getFactura().getInfoFactura()
							.getTotalDescuento() == null) {
						facturaFybeca.getFactura().getInfoFactura()
								.setTotalDescuento(BigDecimal.ZERO);
					}
					if (facturaFybeca.getFactura().getInfoFactura() != null
							&& facturaFybeca.getFactura().getInfoFactura()
									.getTotalDescuento() != null
							&& facturaFybeca.getFactura().getInfoFactura()
									.getImporteTotal() != null) {
						facturaFybeca
								.getFactura()
								.getInfoFactura()
								.setImporteTotal(
										facturaFybeca
												.getFactura()
												.getInfoFactura()
												.getImporteTotal()
												.subtract(
														facturaFybeca
																.getFactura()
																.getInfoFactura()
																.getTotalDescuento()));

					}
					
                   //OFERTON : recupera bandera para validar si esta farmacia esta o no en oferton
					
					facturaFybeca.setBanderaOferton(banderaOferton(conn,farmacia));

					InfoAdicional infoAdiconal = obtenerInfoAdicional(conn,
							farmacia,
							Long.valueOf(facturaFybeca.getCodigoExterno()), facturaFybeca.getBanderaOferton());

					if (facturaFybeca.getPaciente() != null
							&& !facturaFybeca.getPaciente().isEmpty()) {
						if (infoAdiconal == null) {
							infoAdiconal = new InfoAdicional();
						}

						clientaAbf = new CampoAdicional();
						clientaAbf.setNombre("Nombre Paciente");
						clientaAbf.setValue(facturaFybeca.getPaciente().trim());

						infoAdiconal.getCampoAdicional().add(clientaAbf);
					}

					if (facturaFybeca.getCodigoExterno() != null
							&& !facturaFybeca.getCodigoExterno().isEmpty()
							&& !facturaFybeca.getCodigoExterno().trim()
									.isEmpty()) {
						if (infoAdiconal == null) {
							infoAdiconal = new InfoAdicional();
						}

						documentoInterno = new CampoAdicional();
						documentoInterno.setNombre("DOCUMENTO INTERNO");
						documentoInterno.setValue(facturaFybeca
								.getCodigoExterno().trim());

						infoAdiconal.getCampoAdicional().add(documentoInterno);
					}

					if (facturaFybeca.getCorreoElectronicoNotificacion() != null
							&& !facturaFybeca
									.getCorreoElectronicoNotificacion()
									.isEmpty()) {

						if (infoAdiconal == null) {
							infoAdiconal = new InfoAdicional();
						}

						email = new CampoAdicional();
						email.setNombre("EMAIL");
						email.setValue(facturaFybeca
								.getCorreoElectronicoNotificacion());

						infoAdiconal.getCampoAdicional().add(email);

					}

//					if (facturaFybeca.getFactura().getInfoTributaria()
//							.getNombreComercial() != null
//							&& !facturaFybeca.getFactura().getInfoTributaria()
//									.getNombreComercial().isEmpty()) {
//
//						if (infoAdiconal == null) {
//							infoAdiconal = new InfoAdicional();
//						}
//
//						sucursal = new CampoAdicional();
//						sucursal.setNombre("NOMBRE SUCURSAL");
//						sucursal.setValue(StringUtil
//								.validateInfoXML(facturaFybeca.getFactura()
//										.getInfoTributaria()
//										.getNombreComercial()));
//
//						infoAdiconal.getCampoAdicional().add(sucursal);
//
//						facturaFybeca.getFactura().getInfoTributaria()
//								.setNombreComercial(null);
//
//					}

					if (infoAdiconal != null
							&& infoAdiconal.getCampoAdicional() != null
							&& !infoAdiconal.getCampoAdicional().isEmpty()) {
						facturaFybeca.getFactura().setInfoAdicional(
								infoAdiconal);
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
				if (set != null)
					set.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return facturaFybeca;

	}
	
	
	private String banderaOferton(Connection conn, Long farmacia) throws SQLException {
		PreparedStatement ps = null;
		String resultado = "N";
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
		return resultado;
	}

	private Map<String, Object> listarDetalles(Connection conn, Long idFactura,
			Long idFarmacia) throws GizloException, SQLException {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		PreparedStatement ps = null;
		List<Detalle> detalles = new ArrayList<Detalle>();
		Detalle detalleXML = null;
		String descripcion = null;

		// <<AG
		List<TotalImpuesto> totalesImpuestos = new ArrayList<TotalImpuesto>();
		TotalImpuesto totalImpuestoXML = null;
		Impuesto impuestoXML = null;
		BigDecimal total12 = new BigDecimal(0f), total0 = new BigDecimal(0f), baseImp0 = new BigDecimal(
				0f), baseImp12 = new BigDecimal(0f), totalSinImpuesto = new BigDecimal(
				0f), baseImp = new BigDecimal(0f), valor = new BigDecimal(0f);
		BigDecimal total14 = new BigDecimal(0f), baseImp14 = new BigDecimal(0f);

		List<Impuesto> detalleImpuestos = new ArrayList<Impuesto>();

		// AG>>
		StringBuilder sqlString = new StringBuilder();

		// ROUND((precio_fybeca - venta)/cantidad,2)
		sqlString
				.append("(SELECT (fa_detalles_servicios.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		// <<AG
		sqlString
				.append("ROUND((cantidad * venta * fa_detalles_servicios.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		// AG>>
		sqlString
				.append("ROUND(venta, 4) as venta, (fa_servicios.nombre) as descripcion ");
		sqlString.append("FROM fa_detalles_servicios inner join fa_servicios ");
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
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		ps.setLong(3, idFactura);
		ps.setLong(4, idFarmacia);

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			if (set.getBigDecimal("porc_iva") != null) {
				detalleXML = new Detalle();
				detalleXML.setCantidad(new BigDecimal(set.getLong("cantidad")));
				// detalle.setCodigoAuxiliar(set.getString("NOMBRE_PARAM"));
				detalleXML.setCodigoPrincipal(set.getString("item"));

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
							.setImpuestos(new com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle.Impuestos());
					detalleXML.getImpuestos().getImpuesto()
							.addAll(detalleImpuestos);

				}
				// AG>>

				detalles.add(detalleXML);

			}
		}

		// <<AG
		// 0%
		totalSinImpuesto = totalSinImpuesto.add(baseImp0);
		totalSinImpuesto = totalSinImpuesto.add(baseImp12);
		totalSinImpuesto = totalSinImpuesto.add(baseImp14);
		totalSinImpuesto = totalSinImpuesto.setScale(2,
				BigDecimal.ROUND_HALF_UP);

		baseImp0 = baseImp0.setScale(2, BigDecimal.ROUND_HALF_UP);
		total0 = total0.setScale(2, BigDecimal.ROUND_HALF_UP);

		totalImpuestoXML = new TotalImpuesto();
		totalImpuestoXML.setCodigo("2");
		totalImpuestoXML.setCodigoPorcentaje("0");
		totalImpuestoXML.setTarifa(new BigDecimal(0));
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
			totalImpuestoXML.setTarifa(new BigDecimal(12));
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
			totalImpuestoXML.setTarifa(new BigDecimal(14));
			totalImpuestoXML.setBaseImponible(baseImp14);
			total14General = baseImp14;
			totalImpuestoXML.setValor(total14);
			totalesImpuestos.add(totalImpuestoXML);
		}
		// AG>>

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		respuesta.put(Constantes.DETALLES, detalles);
		respuesta.put(Constantes.TOTAL_IMPUESTO, totalesImpuestos);
		respuesta.put(Constantes.TOTAL_SIN_IMPUESTO, totalSinImpuesto);

		return respuesta;
	}

	private FacturaProcesarRequest obtenerFactura(Connection conn,
			ResultSet set, Long farmacia, Long tipo, Long documentoVenta)
			throws SQLException, GizloException {
		com.gizlocorp.gnvoice.xml.factura.Factura factura = new com.gizlocorp.gnvoice.xml.factura.Factura();
		String tipoIdentificacionComprador = null;
		FacturaProcesarRequest facturaProcesarRequest = null;
		// infoFactura.setAdicionales(adicionales);
		InfoFactura infoFactura = new InfoFactura();
		String identificacionAbf = null;
		String nombresAbf = null;

		String identificacionClienteAbf = null;
		String nombresClienteAbf = null;

		String personaAbf = obtenerCodigoAbf(conn, documentoVenta, farmacia);

		if (personaAbf != null && !personaAbf.isEmpty()
				&& !personaAbf.trim().isEmpty()) {
			String[] parametros = personaAbf.split("&");
			identificacionAbf = parametros[0];
			nombresAbf = parametros[1];
		}

		String razonSocialComprador = set.getString("razonSocialComprador")
				.trim();
		if (identificacionAbf != null && !identificacionAbf.isEmpty()
				&& !identificacionAbf.trim().isEmpty() && nombresAbf != null
				&& !nombresAbf.isEmpty() && !nombresAbf.trim().isEmpty()) {

			if (razonSocialComprador != null && !razonSocialComprador.isEmpty()) {
				razonSocialComprador = StringUtil
						.validateInfoXML(razonSocialComprador.trim());
				infoFactura.setIdentificacionComprador(set
						.getString("identificacionComprador"));
				infoFactura.setRazonSocialComprador(razonSocialComprador);
			} else {
				String clienteAbf = obtenerClientePersonaAbf(conn,
						identificacionAbf);
				String[] parametros = clienteAbf.split("&");
				identificacionClienteAbf = parametros[0];
				nombresClienteAbf = parametros[1];
				infoFactura
						.setIdentificacionComprador(identificacionClienteAbf);
				infoFactura.setRazonSocialComprador(StringUtil
						.validateInfoXML(nombresClienteAbf));
			}
			// nombresAbf = StringUtil.validateInfoXML(nombresAbf.trim());
			// infoFactura.setIdentificacionComprador(identificacionAbf);
			// infoFactura.setRazonSocialComprador(nombresAbf);
		} else {
			razonSocialComprador = StringUtil
					.validateInfoXML(razonSocialComprador.trim());
			infoFactura.setIdentificacionComprador(set
					.getString("identificacionComprador"));
			infoFactura.setRazonSocialComprador(razonSocialComprador);
		}

		if (infoFactura.getRazonSocialComprador().trim().length() == 0)
			infoFactura.setRazonSocialComprador("N/D");
		
		// infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll("[^\\x00-\\x7F]",
		// ""));
		infoFactura.setRazonSocialComprador(infoFactura
				.getRazonSocialComprador().replaceAll("^", ""));
		String regex = "[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]";
		infoFactura.setRazonSocialComprador(infoFactura
				.getRazonSocialComprador().replaceAll(regex, ""));

		String dirEstablecimiento = set.getString("dirEstablecimiento");
		dirEstablecimiento = StringUtil.validateInfoXML(dirEstablecimiento);

		infoFactura
				.setDirEstablecimiento(dirEstablecimiento != null ? StringEscapeUtils
						.escapeXml(dirEstablecimiento.trim())
						.replaceAll("[\n]", "").replaceAll("[\b]", "")
						: null);

		// infoFactura.setImporteTotal(set.getBigDecimal("importeTotal"));
		infoFactura
				.setFechaEmision(set.getDate("fechaEmision") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fechaEmision").getTime()),
								FechaUtil.DATE_FORMAT) : null);
		// infoFactura.setFechaEmision(FechaUtil.formatearFecha(FechaUtil.convertirLongADate(Calendar.getInstance().getTime().getTime()),FechaUtil.DATE_FORMAT));
		infoFactura.setImporteTotal(set.getBigDecimal("importeTotal"));
		infoFactura.setMoneda(set.getString("moneda"));
		infoFactura.setPropina(set.getBigDecimal("propina"));
		// infoFactura.setTotalDescuento(set.getBigDecimal("totalDescuento"));
		// infoFactura.setTotalDescuento(obtenerTotalDescuento(credencialDS,
		// set.getLong("codigo_externo"), farmacia));
		infoFactura.setTotalDescuento(BigDecimal.ZERO);

		String identificacion = infoFactura.getIdentificacionComprador();
		if (identificacion != null && !identificacion.isEmpty()) {
			tipoIdentificacionComprador = obtenerIdentificacion(conn,
					identificacion);
			if (tipoIdentificacionComprador != null
					&& !tipoIdentificacionComprador.isEmpty()) {

				if (tipoIdentificacionComprador.equalsIgnoreCase("C")) {
					infoFactura.setTipoIdentificacionComprador("05");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("R")) {
					infoFactura.setTipoIdentificacionComprador("04");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("P")) {
					infoFactura.setTipoIdentificacionComprador("06");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("N")) {
					infoFactura.setTipoIdentificacionComprador("07");
				}
			}

			if (infoFactura != null
					&& infoFactura.getTipoIdentificacionComprador() != null
					&& infoFactura.getTipoIdentificacionComprador()
							.equalsIgnoreCase("07")) {
				infoFactura.setIdentificacionComprador("9999999999999");
				infoFactura.setRazonSocialComprador("CONSUMIDOR FINAL");
			} else {
				infoFactura.setTipoIdentificacionComprador("06");

				if (StringUtil.esNumero(identificacion)) {
					if (identificacion.length() == 10) {
						infoFactura.setTipoIdentificacionComprador("05");
					} else if (identificacion.length() == 13
							&& identificacion.substring(10).equals("001")) {
						infoFactura.setTipoIdentificacionComprador("04");
					}
				}

				infoFactura.setIdentificacionComprador(identificacion);
			}
		} else {
			infoFactura.setIdentificacionComprador("9999999999999");
			infoFactura.setTipoIdentificacionComprador("07");
			infoFactura.setRazonSocialComprador("CONSUMIDOR FINAL");
		}
		infoFactura
				.setTotalSinImpuestos(set.getBigDecimal("totalSinImpuestos"));
		factura.setInfoFactura(infoFactura);

		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setCodDoc(set.getString("codDoc"));
		infoTributaria.setEstab(set.getString("estab"));
		// infoTributaria.setEstab("001");
		// infoTributaria.setNombreComercial(item.getRA);
		// infoTributaria.setDirMatriz(item.get);
		infoTributaria.setPtoEmi(set.getString("ptoEmi"));
		// infoTributaria.setRazonSocial(item.getr);
		infoTributaria.setRuc(set.getString("ruc"));
		// infoTributaria.setRuc("1792366259001");

		String nombreSucursal = set.getString("nombreSucursal");
		nombreSucursal = StringUtil.validateInfoXML(nombreSucursal);
		infoTributaria.setNombreComercial(nombreSucursal);

		infoTributaria.setSecuencial(set.getLong("secuencial") + "");

		factura.setInfoTributaria(infoTributaria);
		
		//pagos ojo
		List<Pago> pagos = listarPagos(conn, documentoVenta, farmacia);
		if(pagos != null && !pagos.isEmpty()){
			factura.getInfoFactura().setPagos(new Pagos());
			factura.getInfoFactura().getPagos().getPago().addAll(pagos);
		}

		facturaProcesarRequest = new FacturaProcesarRequest();
		facturaProcesarRequest.setFactura(factura);
		facturaProcesarRequest.setCodigoExterno(set.getLong("codigo_externo")
				+ "");

		facturaProcesarRequest.setIdentificadorUsuario((set
				.getString("identificacionComprador") != null && !set
				.getString("identificacionComprador").isEmpty()) ? set
				.getString("identificacionComprador") : null);
		facturaProcesarRequest.setAgencia(farmacia.toString());
		facturaProcesarRequest.setPaciente(nombresAbf);
		return facturaProcesarRequest;
	}
	
	private List<Pago> listarPagos(Connection conn, Long idFactura, Long idFarmacia) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Pago pagoXml = null;
		List<Pago> listPagos = new ArrayList<Pago>();
		StringBuilder sqlString = new StringBuilder();
		log.info("****obteniendo pagos"+idFarmacia+"---"+idFactura);
		
		sqlString.append("select mfp.cp_varchar  forma_pago, ");
		sqlString.append("p.venta_factura total, ");
		sqlString.append("AD.Cuotas_Tarjetahabiente plazo, ");
		sqlString.append("decode( AD.Cuotas_Tarjetahabiente,null,null,'MESES') unidad_tiempo ");
		sqlString.append("from fA_facturas f, ");
		sqlString.append("fa_detalles_formas_pago p, ");
		sqlString.append("farmacias.fa_mapeo_formas_pago mfp, ");
		sqlString.append("ad_planes_credito ad ");
		sqlString.append("where f.farmacia = ? ");
		sqlString.append("and f.documento_venta = ? ");
		sqlString.append("and f.farmacia = p.farmacia ");
		sqlString.append("and f.documento_venta = p.documento_venta ");
		sqlString.append("and p.forma_pago = mfp.mapeo_pionus ");
		sqlString.append("and p.plan_credito = ad.codigo(+) ");
		
		
		
		ps = conn.prepareStatement(sqlString.toString());
		ps.setLong(1, idFarmacia);
		ps.setLong(2, idFactura);

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			pagoXml = new Pago();
			pagoXml.setFormaPago(set.getString("forma_pago"));
			pagoXml.setTotal(set.getBigDecimal("total"));
			pagoXml.setPlazo(set.getBigDecimal("plazo"));
			pagoXml.setUnidadTiempo(set.getString("unidad_tiempo"));
			listPagos.add(pagoXml);	
		}
		
		log.info("****obteniendo pagos 333"+listPagos.size());
			
		return listPagos;
				
	}
	
	private List<Compensacion> listarCompensacion(Connection conn, Long idFactura, Long idFarmacia) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlString = new StringBuilder();	
		log.info("****obteniendo Compensacion"+idFarmacia+"---"+idFactura);
		sqlString.append("SELECT VENTA_FACTURA ");
		sqlString.append("FROM fa_detalles_formas_pago ");
		sqlString.append("where forma_pago = 'SUB' ");
		sqlString.append("AND DOCUMENTO_VENTA = ?");
		sqlString.append("AND FARMACIA = ? ");
		
		ps = conn.prepareStatement(sqlString.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			compensacionXml = new Compensacion();
			compensacionXml.setCodigo(new BigDecimal("1"));
			compensacionXml.setTarifa(new BigDecimal("2"));
			compensacionXml.setValor(total14General.multiply(new BigDecimal("0.02")).setScale(2, BigDecimal.ROUND_HALF_UP));
			list.add(compensacionXml);
		}
		log.info("****obteniendo Compensacion 333"+list.size());
				
		return list;
				
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

	private String obtenerCodigoAbf(Connection conn, Long codigoVenta,
			Long farmacia) throws GizloException {

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		Long resultado = null;
		String personaAbf = null;
		try {
			StringBuilder sqlString = new StringBuilder();
			sqlString
					.append("select persona_abf from fa_facturas_mas where documento_venta = ? and farmacia = ? and persona_abf is not null ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, codigoVenta);
			ps.setLong(2, farmacia);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getLong("persona_abf");
			}

			if (resultado != null) {
				sqlString = new StringBuilder();
				sqlString
						.append("select identificacion_titular, nombres from ab_beneficiarios_abf where codigo = ? ");

				ps2 = conn.prepareStatement(sqlString.toString());
				ps2.setLong(1, resultado);

				ResultSet set2 = ps2.executeQuery();

				while (set2.next()) {
					personaAbf = set2.getString("identificacion_titular") + "&"
							+ set2.getString("nombres");
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return personaAbf;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String obtenerClientePersonaAbf(Connection conn,
			String identificacion) throws GizloException {

		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();
			sqlString
					.append("select identificacion as identificacion, (primer_apellido ||' '|| segundo_apellido ||' '|| primer_nombre ||' '|| segundo_nombre) as razonSocialComprador ");
			sqlString.append("from ab_personas where identificacion = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, identificacion);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("identificacion") + "&"
						+ set.getString("razonSocialComprador");
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

	private InfoAdicional obtenerInfoAdicional(Connection conn, Long farmacia,
			Long factura ,String banderaOferton) throws GizloException {
		try {

			InfoAdicional resultado = new InfoAdicional();
			String direccionStr = StringUtil.validateInfoXML(obtenerDireccion(
					conn, farmacia, factura));
			String descuentoStr = null;
			
			if(!"S".equals(banderaOferton))
					descuentoStr =  StringUtil.validateInfoXML(obtenerDescuento(conn, farmacia, factura));
			
		/*	String compensacion = StringUtil.validateInfoXML(obtenerCompensacion(
					conn, farmacia, factura));
			
			if(compensacion != null && compensacion.equals("S")){
				CampoAdicional direccion = new CampoAdicional();
				direccion.setNombre("COMPENSACION");
				direccion.setValue("Compensación solidaria 2% ");
				resultado.getCampoAdicional().add(direccion);
			}*/

			if (direccionStr != null && !direccionStr.isEmpty()) {
				CampoAdicional direccion = new CampoAdicional();
				direccion.setNombre("DIRECCION");
				direccion.setValue(direccionStr.replace(".", "")
						.replace("Â¿", "").replace(",", "").trim());
				resultado.getCampoAdicional().add(direccion);

			}

			if (descuentoStr != null && !descuentoStr.isEmpty()) {
				CampoAdicional descuento = new CampoAdicional();
				descuento.setNombre("DESCUENTO");
				descuento.setValue(descuentoStr.replace("-", "").trim());
				resultado.getCampoAdicional().add(descuento);

			}

			Map<String, String> deducibles = obtenerValorDeducible(conn,
					farmacia, factura);
			String valor = null;
			if (deducibles != null && !deducibles.isEmpty()) {
				CampoAdicional deducible = null;
				for (String nombre : deducibles.keySet()) {
					if (nombre != null && !nombre.isEmpty()) {
						valor = StringUtil.validateInfoXML(deducibles
								.get(nombre));
						if (valor != null && !valor.isEmpty()) {
							deducible = new CampoAdicional();
							if(!(nombre.toUpperCase().trim().equals("NO DEDUCIBLE".toUpperCase()))){
								deducible.setNombre("DEDUCIBLE " + nombre);
								deducible.setValue(valor.trim());
								resultado.getCampoAdicional().add(deducible);
							}							
						}
					}
				}
			}

			return resultado;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
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

	private String obtenerDescuento(Connection conn, Long farmacia, Long factura)
			throws SQLException {
		PreparedStatement ps = null;
		String resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString
				.append("select (pvp_total_factura-venta_total_factura) as valor from fa_facturas where documento_venta = ? and farmacia = ?");

		ps = conn.prepareStatement(sqlString.toString());

		ps.setLong(1, factura);
		ps.setLong(2, farmacia);

		ResultSet set = ps.executeQuery();

		while (set.next()) {
			resultado = set.getBigDecimal("valor") != null ? set.getBigDecimal(
					"valor").toString() : null;
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
		return resultado;
	}
	
	private String obtenerCompensacion(Connection conn, Long farmacia, Long factura)
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

	private Map<String, String> obtenerValorDeducible(Connection conn,
			Long farmacia, Long factura) throws SQLException {
		PreparedStatement ps = null;
		Map<String, String> resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString
				.append("select c.nombre as deduccion, round(sum((a.venta*a.cantidad)),2) as valor ");
		sqlString
				.append("from fa_detalles_factura a, pr_datos_adicionales_items b, pr_deducible_impuesto_renta c ");
		sqlString
				.append("where a.documento_venta = ? and a.farmacia = ? and b.item = a.item and c.codigo = b.campo_n3 group by b.campo_n3, c.nombre order by decode(b.campo_n3,1,2,2,1,b.campo_n3)");

		ps = conn.prepareStatement(sqlString.toString());

		ps.setLong(1, factura);
		ps.setLong(2, farmacia);

		ResultSet set = ps.executeQuery();
		resultado = new HashMap<String, String>();
		String key = null;
		while (set.next()) {
			key = set.getString("deduccion");
			if (key != null && !key.isEmpty()) {
				resultado.put(key, set.getBigDecimal("valor") != null ? set
						.getBigDecimal("valor").toString() : null);
			}
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		return resultado;
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

}
