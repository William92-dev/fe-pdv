package com.gizlocorp.gnvoice.mdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.servicio.local.ServicioCodigoBarrasLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;
import com.gizlocorp.gnvoice.service.impl.CacheBean;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCreditoRecepcion;
import com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ConstantesRim;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Compensaciones.Compensacion;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoice.xml.factura.Impuesto;
import com.gizlocorp.gnvoice.xml.message.CeDisResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoice.xml.message.ValidItem;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import oracle.retail.integration.custom.bo.cedisservice.v1.CeDisOrderRucRequest;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.CeDisOrderService;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.CeDisWS;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.FaultExceptionMessage;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/recepcionComprobante3Queue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "100"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "45000") })
public class RecepcionComprobanteArchivoManagment implements MessageListener {
	private static Logger log = Logger
			.getLogger(RecepcionComprobanteArchivoManagment.class.getName());

	@EJB
	CacheBean cacheBean;


	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRecepcionFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura")
	ServicioRecepcionFactura servicioFactura;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRecepcionNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCreditoRecepcion")	
	ServicioNotaCreditoRecepcion servicioNotaCredito;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioCodigoBarrasImpl!com.gizlocorp.adm.servicio.local.ServicioCodigoBarrasLocal")
	ServicioCodigoBarrasLocal servicioCodigoBarrasLocal;

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	CeDisResponse ceDisresponse = new CeDisResponse();
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onMessage(Message message) {
		ObjectMessage objectMessage = null;

		if (!(message instanceof ObjectMessage)) {
			log.error("Mensaje recuperado no es instancia de ObjectMessage, se desechara "+ message);

			return;
		}

		objectMessage = (ObjectMessage) message;
		try {
			if ((objectMessage == null)
					|| (!(objectMessage.getObject() instanceof String))) {
				log.error("El objeto seteado en el mensaje no es de tipo String, se desechara "
						+ message);

				return;
			}

			String messageStr = (String) objectMessage.getObject();

			if (messageStr == null) {
				log.error("mensaje no es valido para proceso " + messageStr);
				return;
			}

			try {
				String[] parameters = messageStr.split("&");
				int min = Integer.parseInt(parameters[0]);
				int max = Integer.parseInt(parameters[1]);
				String tipo=parameters[2];
				if(tipo.equals("FACTURA"))
					procesarFactura(min, max);
				else
					procesarNotaCredito(min, max);

			} catch (Exception ex) {
				log.info("Error procesar mensaje: " + messageStr, ex);
			}

		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}
	
	private void procesarFactura(int min, int max){
		List<com.gizlocorp.gnvoice.modelo.FacturaRecepcion> listFacturaRecepcion = servicioFactura.obtenerComprobanteParaTraduccion(min, max);
		if(listFacturaRecepcion != null && !listFacturaRecepcion.isEmpty()){
			for(com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaRecepcion: listFacturaRecepcion){
				recibirFactura(facturaRecepcion, new Date());
			}
		}
				
	}
	
	private void procesarNotaCredito(int min, int max){
//		List<com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion> listCreditoRecepcions = servicioNotaCredito.consultarCommprobantesAtraducir(min, max);
//		if(listCreditoRecepcions != null && !listCreditoRecepcions.isEmpty()){
//			for(com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion notaRecepcion: listCreditoRecepcions){
//				recibirNotaCredito(notaRecepcion, new Date());
//			}
//		}
		System.out.println("implementando");
				
	}

	public static void createDirectory(String path)
			throws FileNotFoundException {
		StringBuffer pathname = new StringBuffer(path);

		if (!path.trim().endsWith("/")) {
			pathname.append("/");
		}
		// log.debug("Directorio URL: " + pathname.toString());
		File directory = new File(pathname.toString());
		// //log.debug(pathname.toString());
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new FileNotFoundException();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FacturaRecibirResponse recibirFactura(FacturaRecepcion facturaRecepcion, Date fechaTMP) {

		FacturaRecibirResponse respuesta = new FacturaRecibirResponse();

		BigDecimal baseImponible0 = BigDecimal.ZERO;
		BigDecimal baseImponible12 = BigDecimal.ZERO;
		BigDecimal iva = BigDecimal.ZERO;
		BigDecimal subtotal = BigDecimal.ZERO;
		String nombreArchivo = "";
		
		boolean offline = false;

		Autorizacion autorizacionXML = null;
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;
		
		try {
			if(!(facturaRecepcion != null && facturaRecepcion.getId() != null)){
				return null;
			}
			String autorizacion = DocumentUtil.readContentFile(facturaRecepcion.getArchivo());

			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = null;
			
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
			}

			autorizacion = autorizacion.trim();
			
			autorizacionXML = getAutorizacionXML(autorizacion);
			
			String factura = autorizacionXML.getComprobante();
			factura = factura.replace("<![CDATA[", "");
			factura = factura.replace("]]>", "");

			facturaXML = getFacturaXML(factura);

			nombreArchivo = "edidlinv_"+FechaUtil.formatearFecha(Calendar.getInstance().getTime(), "yyyyMMdd")+"_"+ facturaXML.getInfoTributaria().getClaveAcceso();

			com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaObj = servicioFactura.obtenerComprobanteRecepcion(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);

			if (facturaObj != null && "AUTORIZADO".equals(facturaObj.getEstado())) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeCliente("El comprobante con la clave de acceso "+ facturaObj.getClaveAcceso()+ " ya esta registrada en el sistema como "
								+ facturaObj.getTipoGeneracion().getDescripcion());

				respuesta.setMensajeSistema("El comprobante con la clave de acceso "
								+ facturaObj.getClaveAcceso()
								+ " ya esta registrada en el sistema como "
								+ facturaObj.getTipoGeneracion().getDescripcion());
				return respuesta;
			}

			Organizacion emisor = cacheBean.obtenerOrganizacion(facturaXML.getInfoFactura().getIdentificacionComprador());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "+ facturaXML.getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "+ facturaXML.getInfoTributaria().getRuc());
				facturaObj.setEstado(Estado.ERROR.getDescripcion());
				facturaObj = servicioFactura.recibirFactura(facturaObj,"No existe registrado un emisor con RUC: "+ facturaXML.getInfoTributaria().getRuc());
				return respuesta;
			}

			
			// TODO agregar velocity
			if (facturaObj.getProceso().equals(com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {
				String ordenCompra = facturaObj.getOrdenCompra();											
				if (ordenCompra != null) {
					ordenCompra = ordenCompra.trim();
					if (ordenCompra.matches("[0-9]*")) {
						facturaObj.setOrdenCompra(ordenCompra);
						ceDisresponse = consultaWebservice(facturaObj.getRuc(), facturaObj.getOrdenCompra());
					} else {
						ceDisresponse = null;
					}
				}
				if (ordenCompra == null || ordenCompra.isEmpty()) {					
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error no se encuentra el nro orden de compra");
					respuesta.setMensajeSistema("Ha ocurrido un error no se encuentra el nro orden de compra");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra");
					facturaObj = this.servicioFactura.recibirFactura(facturaObj,"El comprobante no tiene orden de compra ingresar manualmente");
					return respuesta;
				}
				
				
				if (ceDisresponse == null) {					
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Ha ocurrido un error no devolvio datos el web service para en munero de compra."+ ordenCompra);
					facturaObj = this.servicioFactura.recibirFactura( facturaObj
																	, new StringBuilder()
																	  .append("El web service no encontro informacion. para la orden")
											                          .append(ordenCompra).toString());
					return respuesta;
				}
				
				

				
				if (validaCantidadZero (facturaXML)) {					
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Ha ocurrido un error no se puede dividir para 0, Cantidad");
					respuesta.setMensajeSistema("Ha ocurrido un error no se puede dividir para 0, Cantidad con clave de acceso " + facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Ha ocurrido un error no se puede dividir para 0, Cantidad con clave de acceso ");
					facturaObj = this.servicioFactura.recibirFactura(facturaObj,"Ha ocurrido un error no se puede dividir para 0, Cantidad");
					return respuesta;
				}
				
				log.info("codigo de orden invalido***"+ceDisresponse.getOrder_status());
				if(ceDisresponse != null && !ceDisresponse.getOrder_status().isEmpty()){
					respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
					respuesta.setMensajeCliente("Error en el estatus de la Orden "+ceDisresponse.getOrder_status());
					respuesta.setMensajeSistema("Error en el estatus de la Orden "+ceDisresponse.getOrder_status());
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj.setMensajeErrorReim("Error en el estatus de la Orden "+ceDisresponse.getOrder_status());
					facturaObj = this.servicioFactura
							.recibirFactura(facturaObj,"Error en el estatus de la Orden "+ceDisresponse.getOrder_status());
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
					facturaObj = this.servicioFactura.recibirFactura( facturaObj
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
										.setScale(4, BigDecimal.ROUND_HALF_UP));
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
									facturaObj = this.servicioFactura.recibirFactura(facturaObj,
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
								facturaObj = this.servicioFactura.recibirFactura(facturaObj,
										new StringBuilder()
												.append("Nose encontraron los codigos " + itera.getCodigoPrincipal())
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
//						map.put("Original_Unit_Cost", completarEspaciosEmblancosNumeros(
//								ConstantesRim.Original_Unit_Cost, 4,
//								String.valueOf(itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP))));
						pu = itera.getPrecioUnitario().setScale(4, BigDecimal.ROUND_HALF_UP);
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
										//booCambioA14 = true;
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
					              String.valueOf(itera
					              .getPrecioTotalSinImpuesto().add(precioUnitarioaux).divide(itera.getCantidad(), 4, RoundingMode.CEILING)
					              .setScale(4, 4))));

					            precioUnitarioaux = new BigDecimal(0);
					            
						map.put("Total_Allowance", completarEspaciosEmblancosNumeros(20, 4, "0"));

						list.add(map);
					}
				}
				ArrayList listTotalImpuesto = new ArrayList();// lista para
																// codigos iva
				ArrayList listTotalImpuestoIceIbrp = new ArrayList();// lista para codigos iva
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
					descuentosolidario = auxValor.setScale(4, BigDecimal.ROUND_HALF_UP);
					mapiceibrp.put("MER_AMT", completarEspaciosEmblancosNumeros(ConstantesRim.MER_AMT,
							ConstantesRim.Decimales4, String.valueOf(auxValor.setScale(4, BigDecimal.ROUND_HALF_UP))));
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

						descuentosolidario = compen.getValor().setScale(4, BigDecimal.ROUND_HALF_UP);

						mapiceibrp.put("MER_AMT",
								completarEspaciosEmblancosNumeros(ConstantesRim.MER_AMT, ConstantesRim.Decimales4,
										String.valueOf(compen.getValor().setScale(4, BigDecimal.ROUND_HALF_UP))));
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

								facturaObj = this.servicioFactura.recibirFactura(facturaObj,
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
														.setScale(4, BigDecimal.ROUND_HALF_UP))));

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
										//impu.setValor(impu.getBaseImponible().multiply(new BigDecimal("0.12")));
										impu.setValor(impu.getValor());
										//boodescuentosoli = true;
									}
									if (impu.getCodigoPorcentaje().equals("3")) {
										map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
												ConstantesRim.Decimales10, String.valueOf("14")));
									}
									map.put("VAT_SIGNO", "+");
									map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code, ConstantesRim.Decimales4,
											String.valueOf(totalsinimpiesto14.setScale(4, BigDecimal.ROUND_HALF_UP))));

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
											.subtract(descuentosolidario).setScale(4, BigDecimal.ROUND_HALF_UP))));
						} else {
							if (!boodescuentosoli && booiceIbrp) {
								map.put("VAT_SIGNO", "+");
								map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
										ConstantesRim.Decimales4,
										String.valueOf(totalimpuestoIceIbrp.setScale(4, BigDecimal.ROUND_HALF_UP))));
							} else {
								if (boodescuentosoli && !booiceIbrp) {
									map.put("VAT_SIGNO", "-");
									map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code, ConstantesRim.Decimales4,
											String.valueOf(descuentosolidario.setScale(4, BigDecimal.ROUND_HALF_UP))));
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
					facturaObj.setMensajeErrorReim("Ha ocurrido un error no devolvio datos el web service para en munero de compra."
									                + ordenCompra);
					facturaObj = this.servicioFactura.recibirFactura(facturaObj
																	,new StringBuilder()
																	.append("El web service no encontro informacion. para la orden")
																	.append(ordenCompra).toString());
					return respuesta;
				}

				log.info("***Traduciendo Reim factura 555***");
				context.put("Vendor_Type", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Type,
						ceDisresponse.getVendor_type().length(), ceDisresponse.getVendor_type()));

				context.put("Vendor_ID", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_ID,
						ceDisresponse.getVendor_id().length(), ceDisresponse.getVendor_id()));

				Date fechaEmision =  sdfDate2.parse(facturaXML.getInfoFactura().getFechaEmision());
				
				//System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"+sdfDate.format(fechaEmision).length());
				
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
										.setScale(4, BigDecimal.ROUND_HALF_UP))));

				context.put("Total_VAT_Amount", completarEspaciosEmblancosNumeros(ConstantesRim.Total_VAT_Amount, 4,
						String.valueOf(totalImpuesto.setScale(4, BigDecimal.ROUND_HALF_UP))));

				context.put("Total_Quantity", completarEspaciosEmblancosNumeros(ConstantesRim.Total_Quantity, 4,
						String.valueOf(totalUnidades)));

				context.put("Total_Discount",
						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Discount, 4, String.valueOf(facturaXML
								.getInfoFactura().getTotalDescuento().setScale(4, BigDecimal.ROUND_HALF_UP))));

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

				String archivoCreado = DocumentUtil.createDocumentText(writer
						.toString().getBytes(), nombreArchivo, FechaUtil
						.formatearFecha(now2.getTime(), "yyyyMMdd").toString(),
						stvrepositorioRim);

				String archivoCreadoProcesado = archivoCreado + ".loaded";
				File fileOut = new File(archivoCreado);
				File fileLoaded = new File(archivoCreadoProcesado);
				boolean existFileOut = fileOut != null && fileOut.exists()
						&& !fileOut.isDirectory();
				boolean existFileLoaded = fileLoaded != null
						&& fileLoaded.exists() && !fileLoaded.isDirectory();

				if (existFileOut || existFileLoaded) {
					facturaObj.setFechaEntReim(new Date());
					facturaObj.setArchivoGenReim(archivoCreado);
				} else {
					respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					MensajeRespuesta msgObj = new MensajeRespuesta();
					msgObj.setIdentificador("-100");
					msgObj.setMensaje("Ha ocurrido un problema: "
							+ "No se creo el archivo .out reim");
					respuesta.getMensajes().add(msgObj);
					respuesta
							.setMensajeCliente("No se creo el archivo .out reim");
					respuesta
							.setMensajeSistema("No se creo el archivo .out reim");
					System.out.println("No se creo el archivo .out reim");
					return respuesta;
				}

			}

			// fin agregado rim

			facturaObj.setNumeroAutorizacion(autorizacionXML
					.getNumeroAutorizacion());
			if (autorizacionXML.getFechaAutorizacion() != null) {
				facturaObj
						.setFechaAutorizacion(autorizacionXML
								.getFechaAutorizacion().toGregorianCalendar()
								.getTime());
			} else {
				SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
				facturaObj.setFechaAutorizacion(formato.parse(facturaXML
						.getInfoFactura().getFechaEmision()));
			}

			facturaObj.setEstado("AUTORIZADO");
			facturaObj.setTareaActual(Tarea.AUT);
			facturaObj.setTipoGeneracion(TipoGeneracion.REC);

			// si no existe pdf creo pdf
			Parametro dirParametro = cacheBean.consultarParametro(
					Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
			String dirServidor = dirParametro != null ? dirParametro.getValor()
					: "";

			facturaObj = servicioFactura.recibirFactura(facturaObj,
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

	public boolean validaCedisVirtual(String cedisVirtual, String ruc) {

		if ("1790710319001".equals(ruc) && "8101".equals(cedisVirtual)) {
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
	
	

//	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,
//			String claveAcceso) {
//		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;
//
//		// log.info("CLAVE DE ACCESO: " + claveAcceso);
//		int timeout = 7000;
//
//		try {
//			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
//					new URL(wsdlLocation),
//					new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));
//
//			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion
//					.getAutorizacionComprobantesOfflinePort();
//
//			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
//			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
//					timeout);
//
//			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
//					timeout);
//			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);
//
//			ec.gob.sri.comprobantes.ws.offline.aut.RespuestaComprobante respuestaComprobanteAut = autorizacionws
//					.autorizacionComprobante(claveAcceso);
//
//			// 2 reintentos
//			for (int i = 0; i < 2; i++) {
//				if (respuestaComprobanteAut != null && respuestaComprobanteAut.getAutorizaciones() != null
//						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
//						&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
//						&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {
//					break;
//				} else {
//					// log.info("Renvio por autorizacion en blanco");
//					respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);
//
//				}
//			}
//
//			if (respuestaComprobanteAut != null && respuestaComprobanteAut.getAutorizaciones() != null
//					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion() != null
//					&& !respuestaComprobanteAut.getAutorizaciones().getAutorizacion().isEmpty()
//					&& respuestaComprobanteAut.getAutorizaciones().getAutorizacion().get(0) != null) {
//
//				ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacion = respuestaComprobanteAut
//						.getAutorizaciones().getAutorizacion().get(0);
//
//				for (ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion aut : respuestaComprobanteAut
//						.getAutorizaciones().getAutorizacion()) {
//					if (aut != null && "AUTORIZADO".equals(aut.getEstado())) {
//						autorizacion = aut;
//						break;
//					}
//				}
//
//				autorizacion.setAmbiente(null);
//
//				autorizacion.setComprobante("<![CDATA[" + autorizacion.getComprobante() + "]]>");
//
//				if (autorizacion.getMensajes() != null && autorizacion.getMensajes().getMensaje() != null
//						&& !autorizacion.getMensajes().getMensaje().isEmpty()) {
//
//					for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msj : autorizacion.getMensajes().getMensaje()) {
//						msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils.escapeXml(msj.getMensaje()) : null);
//						msj.setInformacionAdicional(msj.getInformacionAdicional() != null
//								? StringEscapeUtils.escapeXml(msj.getInformacionAdicional()) : null);
//					}
//				}
//
//				if ("AUTORIZADO".equals(autorizacion.getEstado())) {
//					autorizacion.setMensajes(null);
//				}
//
//				return autorizacion;
//
//			}
//
//		} catch (Exception ex) {
//			log.info("Renvio por timeout");
//			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
//			respuesta.setEstado("ERROR");
//		}
//
//		if (respuesta == null || respuesta.getEstado() == null) {
//			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
//			respuesta.setEstado("ERROR");
//
//		}
//
//		return respuesta;
//	}
	
//	private Autorizacion autorizarComprobante(String wsdlLocation,
//			String claveAcceso) {
//		Autorizacion respuesta = null;
//
//		log.info("enterndo sri ");
//		int timeout = 7000;
//
//		try {
//			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(
//					new URL(wsdlLocation), new QName(
//							"http://ec.gob.sri.ws.autorizacion",
//							"AutorizacionComprobantesService"));
//
//			AutorizacionComprobantes autorizacionws = servicioAutorizacion
//					.getAutorizacionComprobantesPort();
//
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.ws.connect.timeout", timeout);
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.internal.ws.connect.timeout", timeout);
//
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.internal.ws.request.timeout", timeout);
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.ws.request.timeout", timeout);
//			log.info("enterndo sri22 ");
//			RespuestaComprobante respuestaComprobanteAut = autorizacionws
//					.autorizacionComprobante(claveAcceso);
//
//			// 2 reintentos
//			for (int i = 0; i < 2; i++) {
//				if (respuestaComprobanteAut != null
//						&& respuestaComprobanteAut.getAutorizaciones() != null
//						&& respuestaComprobanteAut.getAutorizaciones()
//								.getAutorizacion() != null
//						&& !respuestaComprobanteAut.getAutorizaciones()
//								.getAutorizacion().isEmpty()
//						&& respuestaComprobanteAut.getAutorizaciones()
//								.getAutorizacion().get(0) != null) {
//					break;
//				} else {
//					// log.debug("Renvio por autorizacion en blanco");
//					respuestaComprobanteAut = autorizacionws
//							.autorizacionComprobante(claveAcceso);
//
//				}
//			}
//			log.info("enterndo sri 33");
//
//			if (respuestaComprobanteAut != null
//					&& respuestaComprobanteAut.getAutorizaciones() != null
//					&& respuestaComprobanteAut.getAutorizaciones()
//							.getAutorizacion() != null
//					&& !respuestaComprobanteAut.getAutorizaciones()
//							.getAutorizacion().isEmpty()
//					&& respuestaComprobanteAut.getAutorizaciones()
//							.getAutorizacion().get(0) != null) {
//
//				Autorizacion autorizacion = respuestaComprobanteAut
//						.getAutorizaciones().getAutorizacion().get(0);
//
//				for (Autorizacion aut : respuestaComprobanteAut
//						.getAutorizaciones().getAutorizacion()) {
//					if (aut != null && "AUTORIZADO".equals(aut.getEstado())) {
//						autorizacion = aut;
//						break;
//					}
//				}
//				log.info("enterndo sri 444");
//
//				autorizacion.setAmbiente(null);
//
//				autorizacion.setComprobante("<![CDATA["
//						+ autorizacion.getComprobante() + "]]>");
//
//				if (autorizacion.getMensajes() != null
//						&& autorizacion.getMensajes().getMensaje() != null
//						&& !autorizacion.getMensajes().getMensaje().isEmpty()) {
//
//					for (Mensaje msj : autorizacion.getMensajes().getMensaje()) {
//						msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils
//								.escapeXml(msj.getMensaje()) : null);
//						msj.setInformacionAdicional(msj
//								.getInformacionAdicional() != null ? StringEscapeUtils
//								.escapeXml(msj.getInformacionAdicional())
//								: null);
//					}
//				}
//
//				log.info("enterndo sri555 ");
//
//				if ("AUTORIZADO".equals(autorizacion.getEstado())) {
//					autorizacion.setMensajes(null);
//				}
//
//				return autorizacion;
//
//			}
//
//		} catch (Exception ex) {
//			log.debug("Renvio por timeout");
//			respuesta = new Autorizacion();
//			respuesta.setEstado("ERROR");
//		}
//
//		if (respuesta == null || respuesta.getEstado() == null) {
//			respuesta = new Autorizacion();
//			respuesta.setEstado("ERROR");
//
//		}
//
//		return respuesta;
//	}

	private Autorizacion getAutorizacionXML(String comprobanteXML)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (Autorizacion) converter.convertirAObjeto(comprobanteXML,
				Autorizacion.class);
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
	
	public Boolean validaCantidadZero (com.gizlocorp.gnvoice.xml.factura.Factura facturaXML ){
	
		for (Detalle itera : facturaXML.getDetalles().getDetalle()) {
			if(itera.getCantidad().equals(new BigDecimal("0.00"))){
				return true;
			}
			
			if(itera.getCantidad().equals(new BigDecimal("0.0"))){
				return true;
			}
			
			if(itera.getCantidad().equals(new BigDecimal("0"))){
				return true;
			}
			
		}
		return false;
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

	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.factura.Factura) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.factura.Factura.class);
	}
	
	public static void main(String[] args) {
//		RecepcionComprobanteArchivoManagment reManagment = new RecepcionComprobanteArchivoManagment();
//		reManagment.downloadEmailAttachments("imap.gmail.com", "davidherrerav872@gmail.com", "JoseVinueza872", "", 1, 1);
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//	public NotaCreditoRecibirResponse recibirNotaCredito(NotaCreditoRecepcion mensaje, Date fechcaTMP ) {
//		NotaCreditoRecibirResponse respuesta = new NotaCreditoRecibirResponse();
//		boolean offline = false;
//		try {
//			
//			Autorizacion autorizacionXML = null;
//			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;
//			
//			String autorizacion = DocumentUtil.readContentFile(mensaje.getArchivo());
//			
//				if (autorizacion != null && !autorizacion.isEmpty()) {
//					String tmp = autorizacion;
//					for (int i = 0; i < tmp.length(); i++) {
//						if (tmp.charAt(i) == '<') {
//							break;
//						}
//						autorizacion = autorizacion.replace(tmp.charAt(i), ' ');
//					}
//
//					autorizacion = autorizacion.replace("<Autorizacion>",
//							"<autorizacion>");
//					autorizacion = autorizacion.replace("</Autorizacion>",
//							"</autorizacion>");
//
//					if (autorizacion
//							.contains("<RespuestaAutorizacionComprobante>")) {
//						autorizacion = autorizacion.replace(
//								"<RespuestaAutorizacionComprobante>", "");
//						autorizacion = autorizacion.replace(
//								"</RespuestaAutorizacionComprobante>", "");
//					}
//
//					autorizacion = autorizacion.trim();
//				}
//				autorizacionXML = getAutorizacionXML(autorizacion);			
//
//			// Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);
//
//			
//			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito documentoXML = null;
//				
//			String documento = autorizacionXML.getComprobante();
//			documento = documento.replace("<![CDATA[", "");
//			documento = documento.replace("]]>", "");
//			documentoXML = getNotaCreditoXML(documento);
//			
//
//
//			com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion documentoObj = servicioNotaCredito
//					.obtenerComprobante(documentoXML.getInfoTributaria()
//							.getClaveAcceso(), null, null, null);
//
//			
//
//			if (documentoObj != null
//					&& "AUTORIZADO".equals(documentoObj.getEstado())) {
//				respuesta
//						.setMensajeCliente("La clave de acceso ya esta registrada "
//								+ documentoObj.getTipoGeneracion()
//										.getDescripcion());
//				respuesta
//						.setMensajeSistema("La clave de acceso ya esta registrada"
//								+ documentoObj.getTipoGeneracion()
//										.getDescripcion());
//				return respuesta;
//			}
//
//			
//
//			if (autorizacionXML.getNumeroAutorizacion() == null
//					|| autorizacionXML.getNumeroAutorizacion().isEmpty()) {
//				respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//				respuesta
//						.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
//				respuesta
//						.setMensajeCliente("El Comprobante no tiene numero de Autorizacion");
//
//				documentoObj.setEstado(Estado.ERROR.getDescripcion());
//				documentoObj = servicioNotaCredito.recibirNotaCredito(
//						documentoObj,
//						"Comprobante no tiene numero de Autorizacion");
//				return respuesta;
//
//			}
//
//			Organizacion emisor = cacheBean.obtenerOrganizacion(documentoXML
//					.getInfoNotaCredito().getIdentificacionComprador());
//
//			if (emisor == null) {
//				respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//				respuesta
//						.setMensajeSistema("No existe registrado un emisor con RUC: "
//								+ documentoXML.getInfoTributaria().getRuc());
//				respuesta
//						.setMensajeCliente("No existe registrado un emisor con RUC: "
//								+ documentoXML.getInfoTributaria().getRuc());
//
//				documentoObj.setEstado(Estado.ERROR.getDescripcion());
//				documentoObj = servicioNotaCredito.recibirNotaCredito(
//						documentoObj,
//						"No existe registrado un emisor con RUC: "
//								+ documentoXML.getInfoTributaria().getRuc());
//				return respuesta;
//			}
//
////			Parametro ambParametro = cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
////			String ambiente = ambParametro.getValor();
//
//			
//
//			// TODO agregar velocity
//			
//
//			if (mensaje.getProceso().equals(com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {
//				Parametro serRimWS = cacheBean.consultarParametro(
//						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_WS, emisor.getId());
//				String dirWsRim = serRimWS != null ? serRimWS.getValor() : "";
//
//				Parametro serRimUri = cacheBean.consultarParametro(
//						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_URI, emisor.getId());
//				String dirUriRim = serRimUri != null ? serRimUri.getValor()
//						: "";
//
//				String ordenCompra = null;
//				String idDevolucion = null;
//				boolean esDevolucion = false;
//				log.info("***Comenzando a traducir 2222***");
//				CeDisResponse ceDisresponse = new CeDisResponse();
//				if ((dirWsRim != null) && (!dirWsRim.isEmpty())
//						&& (dirUriRim != null) && (!dirUriRim.isEmpty())) {
//					ClienteRimBean clienteRimBean = new ClienteRimBean(
//							dirWsRim, dirUriRim);
//					String ordenCompraName = "orden";
//					String ordenCompraName2 = "compra";
//					String idDevolucionName = "ID Devolucion";
//					if (documentoXML.getInfoAdicional() != null) {
//						for (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional item : documentoXML
//								.getInfoAdicional().getCampoAdicional()) {
//							if ((item.getNombre().trim().toUpperCase()
//									.contains(ordenCompraName.toUpperCase()))
//									|| (item.getNombre().trim().toUpperCase()
//											.contains(ordenCompraName2
//													.toUpperCase()))) {
//								ordenCompra = item.getValue();
//								log.info("***Comenzando a traducir 3333***"
//										+ ordenCompra);
//								break;
//							}
//							if (item.getNombre().toUpperCase()
//									.equals(idDevolucionName.toUpperCase())) {
//								idDevolucion = item.getValue();
//								esDevolucion = true;
//								log.info("***Comenzando a traducir 3333***"
//										+ idDevolucion);
//								break;
//							}
//						}
//					}
//					
//					boolean sinCodigo = true;
//					if (ordenCompra != null) {
//						if (ordenCompra.matches("[0-9]*")) {
//							documentoObj.setOrdenCompra(ordenCompra);
//							ceDisresponse = clienteRimBean
//									.llamadaPorOrdenFactura(ordenCompra);
//							sinCodigo = false;
//						}
//					}
//					// else
//					if ((mensaje.getOrdenCompra() != null)
//							&& (!mensaje.getOrdenCompra().isEmpty())) {
//						if (mensaje.getOrdenCompra().matches("[0-9]*")) {
//							documentoObj.setOrdenCompra(mensaje
//									.getOrdenCompra());
//							ceDisresponse = clienteRimBean
//									.llamadaPorOrdenFactura(mensaje
//											.getOrdenCompra());
//
//							ordenCompra = mensaje.getOrdenCompra();
//							sinCodigo = false;
//						}
//					} else if (idDevolucion != null) {
//						if (idDevolucion.matches("[0-9]*")) {
//							documentoObj.setOrdenCompra(idDevolucion);
//							ceDisresponse = clienteRimBean
//									.llamadaPorDevolucion(idDevolucion);
//							sinCodigo = false;
//						} else if ((mensaje.getOrdenCompra() != null)
//								&& (!mensaje.getOrdenCompra().isEmpty())) {
//							if (mensaje.getOrdenCompra().matches("[0-9]*")) {
//								documentoObj.setOrdenCompra(mensaje
//										.getOrdenCompra());
//								ceDisresponse = clienteRimBean
//										.llamadaPorDevolucion(mensaje
//												.getOrdenCompra());
//								sinCodigo = false;
//							}
//						}
//
//					}
//
//					if (sinCodigo) {
//						respuesta.setClaveAccesoComprobante(documentoXML
//								.getInfoTributaria().getClaveAcceso());
//						respuesta
//								.setMensajeCliente("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
//						respuesta
//								.setMensajeSistema("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
//						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//						documentoObj
//								.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
//						documentoObj.setEstado(Estado.ERROR.getDescripcion());
//						log.info("***Ha ocurrido un error nose encuentra el nro orden de compra o devolucion Nota credito***");
//						documentoObj = this.servicioNotaCredito
//								.recibirNotaCredito(documentoObj,
//										"El comprobante no tiene orden de compra ingresar manualmente");
//						return respuesta;
//					}
//
//					if (ceDisresponse == null) {
//						respuesta.setClaveAccesoComprobante(documentoXML
//								.getInfoTributaria().getClaveAcceso());
//						respuesta
//								.setMensajeCliente("Ha ocurrido un error en el proceso");
//						respuesta
//								.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
//						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//						documentoObj
//								.setMensajeErrorReim("Ha ocurrido un error no devolvio datos el web service");
//						documentoObj.setEstado(Estado.ERROR.getDescripcion());
//						log.info("***El web service no encontro informacion Nota credito***");
//						documentoObj = this.servicioNotaCredito
//								.recibirNotaCredito(documentoObj,
//										"El web service no encontro informacion.");
//						return respuesta;
//					}
//
//				}
//
//				// fin servicio web
//				// log.info("****paso wb****");
//				SimpleDateFormat sdfDate = new SimpleDateFormat(
//						"yyyyMMddHHmmss");// dd/MM/yyyy
//				SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd/MM/yyyy");
//				Date now = new Date();
//				String strDate = sdfDate.format(now);
//				int num = 2;
//				ArrayList list = new ArrayList();
//				BigDecimal totalUnidades = new BigDecimal(0);
//				if (documentoXML.getDetalles().getDetalle() != null
//						&& !documentoXML.getDetalles().getDetalle().isEmpty()) {
//					for (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle itera : documentoXML.getDetalles()
//							.getDetalle()) {
//						num++;
//						Map map = new HashMap();
//						map.put("TDETL", "TDETL");
//						String linea = completar(String.valueOf(num).length(),
//								num);
//						map.put("linea", linea);						
//						boolean booVpn=false;
//						try{
//							if (validaEAN(itera.getCodigoInterno())) {
//								map.put("UPC",
//										completarEspaciosEmblancosLetras(
//												ConstantesRim.UPC, itera
//														.getCodigoInterno()
//														.length(), itera
//														.getCodigoInterno()));							
//							} else {
//								if(validaEAN(itera.getCodigoAdicional())){
//									map.put("UPC",
//											completarEspaciosEmblancosLetras(
//													ConstantesRim.UPC, itera
//															.getCodigoAdicional()
//															.length(), itera
//															.getCodigoAdicional()));								
//								}else{
//									map.put("UPC",completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
//									booVpn = true;
//								}
//							}
//						}catch(Exception e){
//				        	e.printStackTrace();
//				        }
//						// map.put("UPC",
//						// completarEspaciosEmblancosLetras(
//						// ConstantesRim.UPC, itera
//						// .getCodigoInterno().length(),
//						// itera.getCodigoInterno()));
//						map.put("UPC_Supplement",
//								completarEspaciosEmblancosLetras(
//										ConstantesRim.UPC_Supplement, 0, ""));
//						map.put("Item",
//								completarEspaciosEmblancosLetras(
//										ConstantesRim.Item, 0, ""));
////						map.put("VPN",
////								completarEspaciosEmblancosLetras(
////										ConstantesRim.VPN, 0, ""));
//						if(booVpn){
//							map.put("VPN",
//									completarEspaciosEmblancosLetras(
//											ConstantesRim.VPN, itera
//											.getCodigoInterno()
//											.length(), itera
//											.getCodigoInterno()));
//						}else{
//							map.put("VPN",
//									completarEspaciosEmblancosLetras(
//											ConstantesRim.VPN, 0, ""));
//						}
//						map.put("Original_Document_Quantity",
//								completarEspaciosEmblancosNumeros(
//										ConstantesRim.Original_Document_Quantity,
//										4, String.valueOf(itera.getCantidad())));
//
//						if (itera.getDescuento() != null
//								&& itera.getDescuento().compareTo(
//										new BigDecimal("0.00")) > 0) {
//							log.info("descuanto 11" + itera.getDescuento());
//							BigDecimal pu = itera.getPrecioTotalSinImpuesto()
//									.divide(itera.getCantidad(), 4,
//											RoundingMode.CEILING);
//							log.info("descuanto 22" + pu);
//							map.put("Original_Unit_Cost",
//									completarEspaciosEmblancosNumeros(
//											ConstantesRim.Original_Unit_Cost,
//											4, String.valueOf(pu.setScale(2,
//													BigDecimal.ROUND_HALF_UP))));
//						} else {
//							map.put("Original_Unit_Cost",
//									completarEspaciosEmblancosNumeros(
//											ConstantesRim.Original_Unit_Cost,
//											4,
//											String.valueOf(itera
//													.getPrecioUnitario()
//													.setScale(
//															2,
//															BigDecimal.ROUND_HALF_UP))));
//						}
//						// map.put("Original_Unit_Cost",
//						// completarEspaciosEmblancosNumeros(ConstantesRim.Original_Unit_Cost,
//						// 4, String
//						// .valueOf(itera.getPrecioUnitario().setScale(2,
//						// BigDecimal.ROUND_HALF_UP))));
//
//						totalUnidades = totalUnidades.add(itera.getCantidad());
//						if (itera.getImpuestos().getImpuesto() != null
//								&& !itera.getImpuestos().getImpuesto()
//										.isEmpty()) {
//							for (com.gizlocorp.gnvoice.xml.notacredito.Impuesto item : itera.getImpuestos()
//									.getImpuesto()) {
//								if (item.getCodigoPorcentaje().equals("0")) {
//									map.put("Original_VAT_Code",
//											completarEspaciosEmblancosLetras(
//													ConstantesRim.Original_VAT_Code,
//													"L".length(), "L"));
//									map.put("Original_VAT_rate",
//											completarEspaciosEmblancosNumeros(
//													ConstantesRim.Original_VAT_rate,
//													10, String.valueOf(item
//															.getTarifa())));
//								}else {
//									if (item.getCodigoPorcentaje().equals("2") || item.getCodigoPorcentaje().equals("3")) {
//										map.put("VAT_code",
//												completarEspaciosEmblancosLetras(
//														ConstantesRim.VAT_code,
//														"E".length(), "E"));
//
//										map.put("VAT_rate",
//												completarEspaciosEmblancosNumeros(
//														ConstantesRim.VAT_rate,
//														ConstantesRim.Decimales10,									
//														String.valueOf("12")));
//										
//										if(item.getCodigoPorcentaje().equals("3")){
//											map.put("VAT_rate",
//													completarEspaciosEmblancosNumeros(
//															ConstantesRim.VAT_rate,
//															ConstantesRim.Decimales10,
//															String.valueOf("14")));
//										}
//										
//										
//									} else {
//										log.info("La factura tiene codigo de impuesto  no contemplado en reim"
//												+ documentoXML.getInfoTributaria()
//														.getClaveAcceso());
//										respuesta
//												.setClaveAccesoComprobante(documentoXML
//														.getInfoTributaria()
//														.getClaveAcceso());
//										respuesta
//												.setMensajeCliente("Ha ocurrido un error en el proceso");
//										respuesta
//												.setMensajeSistema("La factura tiene codigo de impuesto  no contemplado en reim");
//										respuesta.setEstado(Estado.COMPLETADO
//												.getDescripcion());
//										documentoObj.setEstado(Estado.ERROR
//												.getDescripcion());
//										documentoObj
//												.setMensajeErrorReim("La factura tiene codigo de impuesto  no contemplado en reim");
//										documentoObj = this.servicioNotaCredito
//												.recibirNotaCredito(
//														documentoObj,
//														new StringBuilder()
//																.append("La factura tiene codigo de impuesto  no contemplado en reim")
//																.append(ordenCompra)
//																.toString());
//										return respuesta;
//
//									}
//								}
//							}
//						}
//						map.put("Total_Allowance",
//								completarEspaciosEmblancosNumeros(
//										ConstantesRim.Total_Allowance,
//										ConstantesRim.Decimales4, "0"));
//						list.add(map);
//					}
//				}
//				ArrayList listTotalImpuesto = new ArrayList();
//				BigDecimal totalImpuesto = new BigDecimal(0);
//				if (documentoXML.getInfoNotaCredito().getTotalConImpuestos()
//						.getTotalImpuesto() != null
//						&& !documentoXML.getInfoNotaCredito()
//								.getTotalConImpuestos().getTotalImpuesto()
//								.isEmpty()) {
//					for (com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto impu : documentoXML.getInfoNotaCredito()
//							.getTotalConImpuestos().getTotalImpuesto()) {
//						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
//							num++;
//							Map map = new HashMap();
//							map.put("TVATS", "TVATS");
//							String linea = completar(String.valueOf(num)
//									.length(), num);
//							map.put("linea", linea);
//							if (impu.getCodigoPorcentaje().equals("0")) {
//								map.put("VAT_code",
//										completarEspaciosEmblancosLetras(
//												ConstantesRim.VAT_code,
//												"L".length(), "L"));
//
//								map.put("VAT_rate",
//										completarEspaciosEmblancosNumeros(
//												ConstantesRim.VAT_rate,
//												ConstantesRim.Decimales10,
//												String.valueOf("0")));
//							} else {
//								if (impu.getCodigoPorcentaje().equals("2")) {
//									map.put("VAT_code",
//											completarEspaciosEmblancosLetras(
//													ConstantesRim.VAT_code,
//													"E".length(), "E"));
//
//									map.put("VAT_rate",
//											completarEspaciosEmblancosNumeros(
//													ConstantesRim.VAT_rate,
//													ConstantesRim.Decimales10,
//													String.valueOf("12")));
//								} else {
//									log.info("La nota credito tiene codigo de impuesto  no contemplado en reim"
//											+ documentoXML.getInfoTributaria()
//													.getClaveAcceso());
//									respuesta
//											.setClaveAccesoComprobante(documentoXML
//													.getInfoTributaria()
//													.getClaveAcceso());
//									respuesta
//											.setMensajeCliente("Ha ocurrido un error en el proceso");
//									respuesta
//											.setMensajeSistema("La nota credito tiene codigo de impuesto no contemplado en reim");
//									respuesta.setEstado(Estado.COMPLETADO
//											.getDescripcion());
//									documentoObj.setEstado(Estado.ERROR
//											.getDescripcion());
//									documentoObj
//											.setMensajeErrorReim("La nota credito tiene codigo de impuesto no contemplado en reim");
//									documentoObj = this.servicioNotaCredito
//											.recibirNotaCredito(documentoObj,
//													"La factura tiene codigo de impuesto no contemplado en reim.");
//									return respuesta;
//
//								}
//							}
//							// map.put("VAT_rate",
//							// completarEspaciosEmblancosNumeros(
//							// ConstantesRim.VAT_rate,
//							// ConstantesRim.Decimales10,
//							// String.valueOf(impu
//							// .getCodigoPorcentaje())));
//							map.put("Cost_VAT_code",
//									completarEspaciosEmblancosNumeros(
//											ConstantesRim.Cost_VAT_code,
//											ConstantesRim.Decimales4,
//											String.valueOf(impu
//													.getBaseImponible())));
//							totalImpuesto = totalImpuesto.add(impu.getValor());
//							listTotalImpuesto.add(map);
//						}
//
//					}
//				}
//				int fdetalle = num + 1;
//				int fin = num + 2;
//				int numLinea = num - 2;
//				int totalLinea = num;
//				Parametro dirParametro2 = cacheBean.consultarParametro(
//						com.gizlocorp.gnvoice.utilitario.Constantes.DIR_RIM_ESQUEMA, emisor.getId());
//				String dirServidor2 = dirParametro2 != null ? dirParametro2
//						.getValor() : "";
//				Velocity.setProperty(
//						RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
//						dirServidor2);
//				Velocity.setProperty(
//						RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
//				Velocity.init();
//				Template t = Velocity.getTemplate("traductorNotaCredito.vm");
//				VelocityContext context = new VelocityContext();
//				context.put("fecha", strDate);
//				context.put("petList", list);
//				
//				// THEAD
//				context.put(
//						"Document_Type",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Document_Type, "CRDNT".length(),
//								"CRDNT"));
//
//				String vendorDocument = new StringBuilder()
//						.append(documentoXML.getInfoTributaria().getEstab())
//						.append("")
//						.append(documentoXML.getInfoTributaria().getPtoEmi())
//						.append("")
//						.append(documentoXML.getInfoTributaria()
//								.getSecuencial()).toString();
//				context.put(
//						"Vendor_Document_Number",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Vendor_Document_Number,
//								vendorDocument.length(), vendorDocument));
//				context.put(
//						"Group_ID",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Group_ID, "".length(), ""));
//				context.put(
//						"Vendor_Type",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Vendor_Type, ceDisresponse
//										.getVendor_type().length(),
//								ceDisresponse.getVendor_type()));
//
//				context.put(
//						"Vendor_ID",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Vendor_ID, ceDisresponse
//										.getVendor_id().length(), ceDisresponse
//										.getVendor_id()));
//
//				Date fechaEmision = sdfDate2.parse(documentoXML
//						.getInfoNotaCredito().getFechaEmision());
//				context.put(
//						"Vendor_Document_Date",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Vendor_Document_Date, sdfDate
//										.format(fechaEmision).length(), sdfDate
//										.format(fechaEmision)));
//
//				context.put(
//						"Order_Number",
//						completarEspaciosEmblancosNumeros(
//								ConstantesRim.Order_Number, 0,
//								String.valueOf(ceDisresponse.getOrder_number())));
//				if ((ceDisresponse.getLocation() != null)
//						&& (!ceDisresponse.getLocation().isEmpty()))
//					context.put(
//							"Location",
//							completarEspaciosEmblancosNumeros(
//									ConstantesRim.Location, 0,
//									ceDisresponse.getLocation()));
//				else {
//					context.put(
//							"Location",
//							completarEspaciosEmblancosNumeros(
//									ConstantesRim.Location, 0, ""));
//				}
//
//				if ((ceDisresponse.getLocation() != null)
//						&& (!ceDisresponse.getLocation().isEmpty()))
//					context.put(
//							"Location_Type",
//							completarEspaciosEmblancosLetras(
//									ConstantesRim.Location_Type, ceDisresponse
//											.getLocation().length(),
//									ceDisresponse.getLocation_type()));
//				else {
//					context.put(
//							"Location_Type",
//							completarEspaciosEmblancosLetras(
//									ConstantesRim.Location_Type, "".length(),
//									""));
//				}
//				context.put(
//						"Terms",
//						completarEspaciosEmblancosLetras(ConstantesRim.Terms,
//								"".length(), ""));
//				context.put(
//						"Due_Date",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Due_Date, "".length(), ""));
//				context.put(
//						"Payment_method",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Payment_method, "".length(), ""));
//				context.put(
//						"Currency_code",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Currency_code, "USD".length(),
//								"USD"));
//
//				context.put(
//						"Exchange_rate",
//						completarEspaciosEmblancosNumeros(
//								ConstantesRim.Exchange_rate, 4,
//								String.valueOf("1")));
//
//				context.put(
//						"Total_Cost",
//						completarEspaciosEmblancosNumeros(
//								ConstantesRim.Total_Cost,
//								4,
//								String.valueOf(documentoXML
//										.getInfoNotaCredito()
//										.getTotalSinImpuestos()
//										.setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//				context.put(
//						"Total_VAT_Amount",
//						completarEspaciosEmblancosNumeros(
//								ConstantesRim.Total_VAT_Amount, 4, String
//										.valueOf(totalImpuesto.setScale(2,
//												BigDecimal.ROUND_HALF_UP))));
//
//				context.put(
//						"Total_Quantity",
//						completarEspaciosEmblancosNumeros(
//								ConstantesRim.Total_Quantity, 4,
//								String.valueOf(totalUnidades)));
//
//				context.put(
//						"Total_Discount",
//						completarEspaciosEmblancosNumeros(
//								ConstantesRim.Total_Discount, 4,
//								String.valueOf("0")));
//
//				context.put(
//						"Freight_Type",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Freight_Type, "".length(), ""));
//				context.put("Paid_Ind", "N");
//				context.put("Multi_Location", "N");
//				context.put("Merchan_dise_Type", "N");
//				context.put(
//						"Deal_Id",
//						completarEspaciosEmblancosLetras(ConstantesRim.Deal_Id,
//								"".length(), ""));
//				context.put(
//						"Deal_Approval_Indicator",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Deal_Approval_Indicator,
//								"".length(), ""));
//
//				if ((idDevolucion != null) && (!idDevolucion.isEmpty()))
//					context.put("RTV_indicator", "Y");
//				else {
//					context.put("RTV_indicator", "N");
//				}
//
//				context.put(
//						"Custom_Document_Reference_1",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Custom_Document_Reference_1,
//								autorizacionXML.getNumeroAutorizacion()
//										.length(), autorizacionXML
//										.getNumeroAutorizacion()));
//
//				String fechaAutorizacion = FechaUtil.formatearFecha(
//						autorizacionXML.getFechaAutorizacion()
//								.toGregorianCalendar().getTime(),
//						"dd/MM/yyyy HH:mm");
//
//				context.put(
//						"Custom_Document_Reference_2",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Custom_Document_Reference_2,
//								fechaAutorizacion.length(), fechaAutorizacion));
//				if (esDevolucion)
//					context.put(
//							"Custom_Document_Reference_3",
//							completarEspaciosEmblancosLetras(
//									ConstantesRim.Custom_Document_Reference_3,
//									emisor.getCeDisVirtual().length(),
//									emisor.getCeDisVirtual()));
//				else {
//					context.put(
//							"Custom_Document_Reference_3",
//							completarEspaciosEmblancosLetras(
//									ConstantesRim.Custom_Document_Reference_3,
//									emisor.getCeDisVirtual().length(),
//									emisor.getCeDisVirtual()));
//				}
//
//				String numModificadostr = documentoXML.getInfoNotaCredito()
//						.getNumDocModificado();
//
//				if (numModificadostr.contains("-")) {
//					String parametro = numModificadostr.replace("-", "");
//
//					context.put(
//							"Custom_Document_Reference_4",
//							completarEspaciosEmblancosLetras(
//									ConstantesRim.Custom_Document_Reference_4,
//									parametro.length(), parametro));
//				} else {
//					context.put(
//							"Custom_Document_Reference_4",
//							completarEspaciosEmblancosLetras(
//									ConstantesRim.Custom_Document_Reference_4,
//									numModificadostr.length(), numModificadostr));
//				}
//
//				context.put(
//						"Cross_reference",
//						completarEspaciosEmblancosLetras(
//								ConstantesRim.Cross_reference, "".length(), ""));
//
//				context.put("petListTotalImpuesto", listTotalImpuesto);
//				context.put("secuencial", documentoXML.getInfoTributaria()
//						.getSecuencial());
//
//				context.put("finT",
//						completar(String.valueOf(fdetalle).length(), fdetalle));
//				context.put("finF",
//						completar(String.valueOf(fin).length(), fin));
//				context.put(
//						"numerolineaDestalle",
//						completarLineaDetalle(
//								String.valueOf(numLinea).length(), numLinea));
//				context.put(
//						"totalLinea",
//						completar(String.valueOf(totalLinea).length(),
//								totalLinea));
//
//				StringWriter writer = new StringWriter();
//				t.merge(context, writer);
//				Calendar now2 = Calendar.getInstance();
//
//				Parametro repositorioRim = this.cacheBean.consultarParametro(
//						"RIM_REPOSITO_ARCHIVO", emisor.getId());
//
//				String stvrepositorioRim = repositorioRim != null ? repositorioRim
//						.getValor() : "";
//
//				Random rng = new Random();
//				int dig5 = rng.nextInt(90000) + 10000; // siempre 5 digitos
//
//				String nombreArchivo = new StringBuilder()
//						.append("edidlinv_")
//						.append(FechaUtil.formatearFecha(now2.getTime(),
//								"yyyyMMddHHmmssSSS").toString()).append(dig5)
//						.toString();
//				String archivoFinal = DocumentUtil.createDocumentText(writer
//						.toString().getBytes(), nombreArchivo, FechaUtil
//						.formatearFecha(now2.getTime(), "yyyyMMdd").toString(),
//						stvrepositorioRim);
//				documentoObj.setFechaEntReim(new Date());
//				documentoObj.setArchivoGenReim(archivoFinal);
//
//			}
//
//			// fin agregado rim
//
//			documentoObj.setNumeroAutorizacion(autorizacionXML
//					.getNumeroAutorizacion());
//			documentoObj.setFechaAutorizacion(autorizacionXML
//					.getFechaAutorizacion().toGregorianCalendar().getTime());
//			documentoObj.setEstado("AUTORIZADO");
//			documentoObj.setTareaActual(Tarea.AUT);
//			documentoObj.setTipoGeneracion(TipoGeneracion.REC);
//
//			documentoObj = servicioNotaCredito.recibirNotaCredito(documentoObj,
//					"El comprobante se a cargado de forma exitosa");
//			respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//			respuesta
//					.setMensajeCliente("El comprobante se a cargado de forma exitosa");
//			respuesta
//					.setMensajeSistema("El comprobante se a cargado de forma exitosa");
//
//		} catch (Exception ex) {
//			respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
//			respuesta.setEstado(Estado.ERROR.getDescripcion());
//			MensajeRespuesta msgObj = new MensajeRespuesta();
//			msgObj.setIdentificador("-100");
//			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
//			respuesta.getMensajes().add(msgObj);
//			respuesta.setMensajeCliente("Ha ocurrido un problema");
//			respuesta.setMensajeSistema(ex.getMessage());
//
//			log.error(ex.getMessage(), ex);
//		}
//		return respuesta;
//	}

	

	private com.gizlocorp.gnvoice.xml.notacredito.NotaCredito getNotaCreditoXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.class);
	}
	


	private CeDisResponse consultaWebservice(String rucp, String ordenp){
		CeDisResponse ceDisResponsePoo = null;
		try {
			oracle.retail.integration.custom.bo.cedisservice.v1.ObjectFactory fact = new oracle.retail.integration.custom.bo.cedisservice.v1.ObjectFactory(); 		
			JAXBElement<String> ruc = fact.createCeDisOrderRucRequestRucNumber(rucp);
			JAXBElement<String> order = fact.createCeDisOrderRucRequestOrderNumber(ordenp);
			
			List<JAXBElement<String>> list = new ArrayList<JAXBElement<String>>();
			list.add(order);
			list.add(ruc);
			
			CeDisOrderRucRequest input = new CeDisOrderRucRequest();
			input.setOrderNumberAndRucNumber(list);
			 
		    CeDisWS servicio = new CeDisWS();
		    CeDisOrderService respuesta = servicio.getCeDisOrderServicePt();
		    oracle.retail.integration.custom.bo.cedisservice.v1.CeDisResponse ceDisOrderRuc = respuesta.getCeDisOrderRuc(input);

			if(ceDisOrderRuc != null){
				ceDisResponsePoo = new CeDisResponse();
				ValidItem validItem = new ValidItem();
			    ceDisResponsePoo.setListItem(new ArrayList<ValidItem>());
			    if(ceDisOrderRuc.getCedisVirtual().getValue() != null && !ceDisOrderRuc.getCedisVirtual().getValue().isEmpty()){
			    	ceDisResponsePoo.setCedis_virtual(ceDisOrderRuc.getCedisVirtual().getValue());	
			    }else{
			    	ceDisResponsePoo.setCedis_virtual(null);	
			    }
			    if(ceDisOrderRuc.getLocation().getValue() != null ){
			    	ceDisResponsePoo.setLocation(String.valueOf(ceDisOrderRuc.getLocation().getValue()));
			    }else{
			    	ceDisResponsePoo.setLocation(null);
			    }
			    	    
			    ceDisResponsePoo.setLocation_type(ceDisOrderRuc.getLocationType().getValue());
			    if(ceDisOrderRuc.getOrderNumber().getValue() != null ){
			    	ceDisResponsePoo.setOrder_number(String.valueOf(ceDisOrderRuc.getOrderNumber().getValue()));
			    }else{
			    	ceDisResponsePoo.setOrder_number(null);
			    }		    
			    ceDisResponsePoo.setVendor_id(ceDisOrderRuc.getVendorId().getValue());
			    ceDisResponsePoo.setVendor_type(ceDisOrderRuc.getVendorType().getValue());	
			    
			    if(!ceDisOrderRuc.getOrderStatus().equals("'VALID_ORDER'")){
			    	System.out.println(ceDisOrderRuc.getOrderStatus());
			    	ceDisResponsePoo.setOrder_status(ceDisOrderRuc.getOrderStatus());	
			    	return ceDisResponsePoo;
			    }else{
			    	ceDisResponsePoo.setOrder_status("");
			    }
			    for(oracle.retail.integration.custom.bo.cedisservice.v1.ValidItem itera: ceDisOrderRuc.getValidItemList().getValidItem() ){		    	
			    	validItem = new ValidItem();
			    	validItem.setItem(itera.getItem());
			    	validItem.setItemType(itera.getItemType());
			    	ceDisResponsePoo.getListItem().add(validItem);
			    }
			}
		   
		} catch (FaultExceptionMessage e) {
			e.printStackTrace();
		}
		return ceDisResponsePoo;
	}

	public String validaVpn(String codigo){
		String tipo =null;
//		String codigoManipulado = codigo.replaceAll("[^0-9]", "");
		if(codigo== null || codigo.isEmpty()){
			return tipo;
		}
		for(ValidItem item: ceDisresponse.getListItem()){
			if(item.getItem().equals(codigo!=null?codigo.trim():codigo)){
				if(item.getItemType().equals("ITEM")){
					return tipo = "ITEM";
				}
				if(item.getItemType().equals("NPP") || item.getItemType().equals("VPN")){
					return tipo = "VPN";
				}
				if(item.getItemType().equals("UPC-A") || item.getItemType().equals("EAN8") || item.getItemType().equals("UCC14") || item.getItemType().equals("ISBN13") 
						|| item.getItemType().equals("EAN13") || item.getItemType().equals("UPC") || item.getItemType().equals("MANL")){
					return tipo = "UPC";
				}
			}
			
		}		
		return tipo;
	}
	
	public String validaExcepcion(String codigo, String ruc){
		String tipo =null;
		
		if(codigo== null || codigo.isEmpty()){
			return tipo;
		}
//		String codigoManipulado = codigo.replaceAll("[^0-9]", "");
		try {
			List<ParametrizacionCodigoBarras> listCodigoBarras=servicioCodigoBarrasLocal.obtenerCodigoBarrasPorParametros(null, null, com.gizlocorp.adm.enumeracion.Estado.ACT, ruc);
			
			if(listCodigoBarras !=null){
				for(ParametrizacionCodigoBarras itera: listCodigoBarras){
					if(itera.getCodigoArticulo().equals(codigo)){
						return tipo = "EXCE";
					}
				}
			}
			
		} catch (GizloException e) {
			e.printStackTrace();
		}
		return null;
	}
}