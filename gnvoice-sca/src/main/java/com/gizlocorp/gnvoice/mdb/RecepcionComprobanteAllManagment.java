package com.gizlocorp.gnvoice.mdb;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;
import com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal;
import com.gizlocorp.adm.utilitario.Constantes;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.RimEnum;
import com.gizlocorp.adm.utilitario.StringUtil;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.reporte.NotaCreditoReporte;
import com.gizlocorp.gnvoice.service.Factura;
import com.gizlocorp.gnvoice.service.NotaCredito;
import com.gizlocorp.gnvoice.service.impl.CacheBean;
import com.gizlocorp.gnvoice.service.impl.ClienteRimBean;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.ConstantesRim;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.factura.Impuesto;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoice.xml.message.CeDisResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirResponse;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/recepcionComprobanteAll2Queue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "100"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "45000") })
public class RecepcionComprobanteAllManagment implements MessageListener {
	private static Logger log = Logger.getLogger(RecepcionComprobanteAllManagment.class.getName());

	@EJB
	CacheBean cacheBean;

	@EJB
	Factura factura;

	@EJB
	NotaCredito notaCredito;
	
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;
	
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito")
	ServicioNotaCredito servicioNotaCredito;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioParametroOrdenCompraImpl!com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal")
	ServicioParametroOrdenCompraLocal servicioParametroOrdenCompra;
	

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onMessage(Message message) {
		ObjectMessage objectMessage = null;

		if (!(message instanceof ObjectMessage)) {
			log.error("Mensaje recuperado no es instancia de ObjectMessage, se desechara " + message);

			return;
		}

		objectMessage = (ObjectMessage) message;
		try {
			if ((objectMessage == null) || (!(objectMessage.getObject() instanceof String))) {
				log.error("El objeto seteado en el mensaje no es de tipo String, se desechara " + message);

				return;
			}

			String messageStr = (String) objectMessage.getObject();

			if (messageStr == null) {
				log.error("mensaje no es valido para proceso " + messageStr);
				return;
			}

			try {
				String[] parameters = messageStr.split("&");
				String host = parameters[0];
				String user = parameters[1];
				String pass = parameters[2];
				String dirServer = parameters[3];
				String range = parameters[4];
				String lote = parameters[5];

				downloadEmailAttachments(host, user, pass, dirServer, Integer.valueOf(range), Integer.valueOf(lote));

			} catch (Exception ex) {
				log.info("Error procesar mensaje: " + messageStr, ex);
			}

		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}

	@SuppressWarnings("resource")
	public void downloadEmailAttachments(final String host, final String userName, final String password,
			final String dirServidor, int range, int lote) {

		Store store = null;

		try {
			Properties properties = new Properties();
			properties.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getInstance(properties, null);

			if (userName != null && password != null && !userName.isEmpty() && !password.isEmpty()
					&& !userName.toLowerCase().equals("recepcionfacturasservicios@corporaciongpf.com".toLowerCase())
					&& !userName.toLowerCase()
							.equals("g_gyerecepcionfacturasservicios@corporaciongpf.com".toLowerCase())) {

				store = session.getStore("imaps");
				store.connect(host, userName, password);

				Folder[] f = store.getDefaultFolder().list();				
				List<Folder> ff = new ArrayList<Folder>();
				Folder[] fl = null;

				for (Folder fd : f) {
					if ("[Gmail]".equals(fd.getName())) {
						continue;
					}
					if ("Naty N".equals(fd.getName())) {
						continue;
					}
					if ("GABBY N".equals(fd.getName())) {
						continue;
					}
					if ("N".equals(fd.getName())) {
						continue;
					}
					if ("SANOFI".equals(fd.getName())) {
						continue;
					}
					if ("SANOFI N".equals(fd.getName()) || "3 M N".equals(fd.getName())
							|| "CHOCONO C".equals(fd.getName()) || "DIBEAL N".equals(fd.getName())
							|| "ECUAQUIMICA N".equals(fd.getName())) {
						continue;
					}

					ff.add(fd);
					fl = fd.list();
					if (fl != null && fl.length > 0) {
						for (Folder ffl : fl) {
							ff.add(ffl);
						}
					}
				}
//				Folder fi = store.getFolder("INBOX");
//				ff.add(fi);
				int max = 0;
				int maxRange = 0;
				int minResult = 0;
				int maxResult = 0;

				for (Folder fd : ff) {
					try {
//						System.out.println(fd.getName());
//						
//						if(!fd.getName().equals("INBOX")){//borrar
//							continue;
//						}
						// valida que se mantenga la conecion abierta
						if (!store.isConnected()) {
							store = session.getStore("imaps");
							store.connect(host, userName, password);
						}
						// valida que la carpeta no este vacia
						if (!fd.isSubscribed()) {
							//log.info("** Carpeta vacia o no se puede leer: " + fd.getName() + " " + userName);
							continue;
						}

						fd.open(Folder.READ_WRITE);
						javax.mail.Message arrayMessages[] = fd.getMessages();
						javax.mail.Message message = null;

						//log.info(fd.getName() + "********** NUMERO DE MENSAJES " + arrayMessages.length);

						max = arrayMessages.length;
						System.out.println(max);
						maxRange = range * lote;
						minResult = 0;

						if (max > maxRange) {
							minResult = max - maxRange;
						}

						maxResult = minResult + range;

						if (maxResult > max) {
							maxResult = max;
						}

//						System.out.println(fd.getName() + "********** PROCESAR DE " + minResult + " a " + maxResult);
						for (int i = minResult; i < maxResult; i++) {
							Date fechaRecepcionMailTEMP = null;
							try {
								message = arrayMessages[i];
								String subFolder = null;
								Calendar now = Calendar.getInstance();
								String comprobante = null;
								String contentType = message.getContentType();
								fechaRecepcionMailTEMP = message.getReceivedDate();
//								System.out.println(message.getSubject()+message.getReceivedDate());
//								if(!message.getSubject().equals("Ha recibido un(a) Factura nuevo(a) No. 032-097-009450776")){//borrar
//									continue;
//								}
//								System.out.println("Mail:Fecha Llegada a bandeja: "+fechaRecepcionMailTEMP);
								if (contentType.contains("multipart")) {
									Multipart multiPart = (Multipart) message.getContent();
									int numberOfParts = multiPart.getCount();
									MimeBodyPart part = null;

									boolean hasXMLAttachment = false;
									for (int partCount = 0; partCount < numberOfParts; partCount++) {
										try {
											part = (MimeBodyPart) multiPart.getBodyPart(partCount);

											if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())
													&& (part.getFileName().contains(".xml")
															|| part.getFileName().contains(".XML"))) {
												subFolder = "comprobante";
												// this part is attachment
												String fileName = part.getFileName();
												byte[] buffer = DocumentUtil.inputStreamToByte(part.getInputStream());

												String claveAcceso = getClaveAcceso(buffer);
												if (claveAcceso != null && !claveAcceso.isEmpty()) {
													// TODO incluir validacion
													// por
													// clave de
													// acceso para no procesar
													// si ya
													// esta
													// procesado
													// si cumple validacion
													// hasXMLAttachment = true;
													// si cumple validacion
													// break;
													com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;
													try {
														comprobanteExistente = servicioFactura.obtenerComprobante(claveAcceso,null, null,null);
													} catch (Exception ex) {
														InitialContext ic = new InitialContext();
														servicioFactura = (ServicioFactura) ic
																.lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
														comprobanteExistente = servicioFactura.obtenerComprobante(claveAcceso,null, null,null);
													}
													if(comprobanteExistente != null){
														hasXMLAttachment = true;
														break;
													}
												}

												comprobante = DocumentUtil.createDocument(buffer,
														FechaUtil.formatearFecha(now.getTime(), "dd/MM/yyyy"), fileName,
														subFolder, "recibidoReim");

												if(recepcionGenericaMail(comprobante, userName, fechaRecepcionMailTEMP)){
													hasXMLAttachment = true;
												}

											}

											if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && (part
													.getFileName().toLowerCase().contains(".pdf".toLowerCase()))) {
												subFolder = "comprobante";

												String fileName = part.getFileName();

												byte[] buffer = DocumentUtil.inputStreamToByte(part.getInputStream());
												log.info("creando pdf" + part.getFileName());

												comprobante = DocumentUtil.createDocumentPdf(buffer,
														FechaUtil.formatearFecha(now.getTime(), "dd/MM/yyyy"), fileName,
														subFolder, "recibidoReim", "");

											}

											if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())
													&& (part.getFileName().contains(".zip")
															|| part.getFileName().contains(".ZIP"))) {
												subFolder = "comprobante";
												ZipInputStream zis;

												String fileName = part.getFileName();
												byte[] buffer = DocumentUtil.inputStreamToByte(part.getInputStream());

												comprobante = DocumentUtil.createDocumentZip(buffer,
														FechaUtil.formatearFecha(now.getTime(), "dd/MM/yyyy"), fileName,
														subFolder, "recibidoReim", "");

												FileInputStream fis = new FileInputStream(comprobante.toString());
												zis = new ZipInputStream(new BufferedInputStream(fis));

												ZipEntry entrada;

												while (null != (entrada = zis.getNextEntry())) {
													System.out.println(entrada.getName());
													if (entrada.getName().contains(".xml")
															|| entrada.getName().contains(".XML")) {

														StringBuilder pathFolder = new StringBuilder();

														pathFolder.append(Constantes.DIR_DOCUMENTOS);
														// pathFolder.append(Constantes.DIR_DOCUMENTOS);
														pathFolder.append(subFolder);
														pathFolder.append("/");
														pathFolder.append("recibidoReim");
														pathFolder.append("/");
														pathFolder.append(
																FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy"));
														pathFolder.append("/");

														createDirectory(pathFolder.toString());

														StringBuilder pathFile = new StringBuilder();
														pathFile.append(pathFolder.toString());
														pathFile.append("/");
														String nombreArchivo = entrada.getName().replace(".zip", "")
																.replace(".ZIP", "");

														pathFile.append(nombreArchivo);
														if (nombreArchivo.contains(".XML")
																|| nombreArchivo.contains(".xml")) {
															pathFile.append(".xml");
														}

														FileOutputStream fos2 = new FileOutputStream(
																pathFile.toString());
														int leido;
														byte[] buffer2 = new byte[20024];

														while (0 < (leido = zis.read(buffer2))) {
															fos2.write(buffer2, 0, leido);
														}

														fos2.close();
														zis.closeEntry();

														byte[] xmlZipContent = DocumentUtil
																.readFile(pathFile.toString());

														String claveAcceso = getClaveAcceso(xmlZipContent);
														if (claveAcceso != null && !claveAcceso.isEmpty()) {
															// TODO incluir
															// validacion por
															// clave de
															// acceso para no
															// procesar si ya
															// esta
															// procesado
															com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;
															try {
																comprobanteExistente = servicioFactura.obtenerComprobante(claveAcceso,null, null,null);
															} catch (Exception ex) {
																InitialContext ic = new InitialContext();
																servicioFactura = (ServicioFactura) ic
																		.lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
																comprobanteExistente = servicioFactura.obtenerComprobante(claveAcceso,null, null,null);
															}
															if(comprobanteExistente != null){
																hasXMLAttachment = true;
																break;
															}

														}
														comprobante = DocumentUtil.createDocument(xmlZipContent,
																FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy"),
																nombreArchivo, subFolder, "recibidoReim");

														if(recepcionGenericaMail(comprobante, userName, fechaRecepcionMailTEMP)){
															hasXMLAttachment = true;
														}

													}

												}
											}
										} catch (Exception ex) {
											log.error("Error en evaluacion de parte", ex);
											continue;
										}
									}

									if (hasXMLAttachment) {
										message.setFlag(Flags.Flag.SEEN, true);

									} else {
										// AG se agrega validacion para leer
										// archivo
										// XML del multipart
										byte[] xmlContent = getXMLFromMultipart(multiPart);

										if (xmlContent != null) {
											String claveAcceso = getClaveAcceso(xmlContent);
											if (claveAcceso != null && !claveAcceso.isEmpty()) {
												// TODO incluir validacion por
												// clave
												// de
												// acceso para no procesar si ya
												// esta
												// procesado
												com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;
												try {
													comprobanteExistente = servicioFactura.obtenerComprobante(claveAcceso,null, null,null);
												} catch (Exception ex) {
													InitialContext ic = new InitialContext();
													servicioFactura = (ServicioFactura) ic
															.lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
													comprobanteExistente = servicioFactura.obtenerComprobante(claveAcceso,null, null,null);
												}
												if(comprobanteExistente != null){
													hasXMLAttachment = true;
													continue;
												}

											}
											
											comprobante = DocumentUtil.createDocument(xmlContent,
													FechaUtil.formatearFecha(now.getTime(), "dd/MM/yyyy"),
													"comprobante" + Calendar.getInstance().getTimeInMillis(), subFolder,
													"recibidoReim");

											if(recepcionGenericaMail(comprobante, userName, fechaRecepcionMailTEMP)){
												message.setFlag(Flags.Flag.SEEN, true);
											}
											
										}

									}
								}
							} catch (Exception ex) {
								log.error("No se ejecuto mensaje: ", ex);
							}
						}
					} catch (Exception e) {
						log.error("error en procesamiento de carperta: ", e);
					}

					if (fd != null) {
						try {
							fd.close(false);
						} catch (Exception e) {
							log.error("Error al cerrar carpeta" + e.getMessage(), e);
						}

					}
				}

			}
		} catch (Exception ex) {
			log.error("error procesamiento MDB", ex);

		} finally {
			// disconnect
			if (store != null) {
				try {
					store.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

			}
		}

		// return documents;
	}

	private boolean recepcionGenericaMail(String comprobanteProveedor, String mail, Date fechcaTMP) {
		FacturaRecibirRequest facturaRecibirRequest;
		NotaCreditoRecibirRequest notaCreditoRecibirRequest;
		FacturaRecibirResponse facturaRecibirResponse;
		NotaCreditoRecibirResponse notaCreditoRecibirResponse;
		boolean retorno = false;
		try {
			if (comprobanteProveedor == null) {
				log.error("Parametros incompletos: " + comprobanteProveedor);
				return retorno;
			}

			String autorizacion = DocumentUtil.readContentFile(comprobanteProveedor);

			if (autorizacion.contains("<factura") || autorizacion.contains("&lt;factura")) {

				log.info("factura---->" + comprobanteProveedor);
				facturaRecibirRequest = new FacturaRecibirRequest();
				if ((comprobanteProveedor.contains(".xml")) || (comprobanteProveedor.contains(".XML"))) {
					facturaRecibirRequest.setComprobanteProveedor(comprobanteProveedor);
//					String comprobantePdf = comprobanteProveedor.replace(".xml", ".pdf").replace(".XML", ".PDF");
//					facturaRecibirRequest.setComprobanteProveedorPDF(comprobantePdf);
					facturaRecibirRequest.setProceso(RimEnum.REIM.name());
					facturaRecibirRequest.setMailOrigen(mail);
					log.info("enviando a recibir factura---->");
					//this.factura.recibir(facturaRecibirRequest);
					facturaRecibirResponse = recibirFactura(facturaRecibirRequest, fechcaTMP);
					if(facturaRecibirResponse.getEstado().equals(Estado.COMPLETADO.getDescripcion())){
						retorno = true;
						return retorno;
					}
					
				}
			}
			if (autorizacion.contains("<notaCredito") || autorizacion.contains("&lt;notaCredito")) {
				log.info("ruta notacredito---->" + comprobanteProveedor);
				notaCreditoRecibirRequest = new NotaCreditoRecibirRequest();
				if ((comprobanteProveedor.contains(".xml")) || (comprobanteProveedor.contains(".XML"))) {
					notaCreditoRecibirRequest.setComprobanteProveedor(comprobanteProveedor);
//					String comprobantePdf = comprobanteProveedor.replace(".xml", ".pdf").replace(".XML", ".PDF");
//					notaCreditoRecibirRequest.setComprobanteProveedorPdf(comprobantePdf);
					notaCreditoRecibirRequest.setProceso(RimEnum.REIM.name());
					notaCreditoRecibirRequest.setMailOrigen(mail);
					log.info("enviando a recibir notacredito---->");
					//this.notaCredito.recibir(notaCreditoRecibirRequest);
					notaCreditoRecibirResponse = notaCredito.recibir(notaCreditoRecibirRequest);
					if(notaCreditoRecibirResponse.getEstado().equals(Estado.COMPLETADO.getDescripcion())){
						retorno = true;
						return retorno;
					}
				}
			}
			
			
			
		} catch (Exception ex) {
			log.info("***execion 2***" + ex.getMessage(), ex);
		}
		
		return retorno;
	}

	public static void createDirectory(String path) throws FileNotFoundException {
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

	private byte[] getXMLFromMultipart(Multipart multiPart) {
		try {
			int numberOfParts = multiPart.getCount();
			BodyPart part = null;
			byte[] buffer = null;
			InputStream ios = null;
			ByteArrayOutputStream baos = null;
			byte[] xml = null;

			for (int partCount = 0; partCount < numberOfParts; partCount++) {
				try {
					part = multiPart.getBodyPart(partCount);
					ios = (InputStream) part.getInputStream();
					buffer = new byte[(int) 2048];
					baos = new ByteArrayOutputStream();

					int read = 0;
					while ((read = ios.read(buffer, 0, buffer.length)) != -1) {
						baos.write(buffer, 0, read);
					}
					baos.flush();
					ios.close();
					baos.close();

					String message = new String(baos.toByteArray());
					String messageParts[] = message.trim().split("\r\n\r");
					String respuesta = null;
					xml = null;

					for (int k = 0; k < messageParts.length; k++) {
						xml = Base64.decodeBase64(messageParts[k].trim().getBytes());
						respuesta = new String(xml, "UTF-8");

						if (respuesta.contains("<comprobante>")) {
							return xml;
						}
					}

				} catch (Exception ex) {
					log.error("error al leer el multipart", ex);
					//
				}
			}
		} catch (Exception e) {
			log.error("error al ejecutar el multipart", e);
			//
		}
		return null;
	}

	private String getClaveAcceso(byte[] xml) {

		if (xml != null) {
			try {
				String xmlStr = new String(xml, "UTF-8");
				if (xmlStr.contains("<claveAcceso>") && xmlStr.contains("</claveAcceso>")) {
					String[] a = xmlStr.split("<claveAcceso>");
					return a[1].split("</claveAcceso>")[0].trim();
				}

			} catch (Exception e) {
				log.error("error obtener clave de acceso", e);
			}

		}
		return null;

	}

//	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//	public FacturaRecibirResponse recibirFactura(FacturaRecibirRequest facturaRecibirRequest, Date fechaTMP) {
//
//		FacturaRecibirResponse respuesta = new FacturaRecibirResponse();
//		BigDecimal baseImponible0 = BigDecimal.ZERO;
//		BigDecimal baseImponible12 = BigDecimal.ZERO;
//		BigDecimal iva = BigDecimal.ZERO;
//		BigDecimal subtotal = BigDecimal.ZERO;
//		String nombreArchivo = "";
//
//		try {
//			String autorizacion = DocumentUtil.readContentFile(facturaRecibirRequest.getComprobanteProveedor());
//
//			Autorizacion autorizacionXML = null;
//
//			if (autorizacion.contains("&lt;claveAcceso&gt;")) {
//				
//				String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
//
//				String claveConsultar = "";
//				String numeroAutorizcion = "";
//				
//				int a = autorizacion.indexOf("&lt;claveAcceso&gt;");
//				
//
//				int desde = a + 19;
//				int hasta = desde + 49;
//
//				claveConsultar = autorizacion.substring(desde, hasta);
//				
//				
//				if (autorizacion.contains("&lt;numeroAutorizacion&gt;") && autorizacion.contains("&lt;/numeroAutorizacion&gt;")) {
//					String[] ab = autorizacion.split("&lt;numeroAutorizacion&gt;");
//					numeroAutorizcion = ab[1].split("&lt;/numeroAutorizacion&gt;")[0].trim();
//				}
//				
//				if (autorizacion.contains("<numeroAutorizacion>") && autorizacion.contains("</numeroAutorizacion>")) {
//					String[] ab = autorizacion.split("<numeroAutorizacion>");
//					numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
//				}
//
//				if(claveConsultar.equals(numeroAutorizcion)){
//					log.info("***Comprobante emitido en modo offline 1 "+claveConsultar);
//					respuesta.setEstado(Estado.ERROR.getDescripcion());
//					respuesta.setMensajeCliente("Comprobante emitido en modo offline " );
//					respuesta.setMensajeSistema("Comprobante emitido en modo offline ");
//					return respuesta;
//				}else{
//					autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
//				}
////				autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
//
//			} else {
//				if (autorizacion != null && !autorizacion.isEmpty()) {
//					String tmp = autorizacion;
//					for (int i = 0; i < tmp.length(); i++) {
//						if (tmp.charAt(i) == '<') {
//							break;
//						}
//						autorizacion = autorizacion.replace(tmp.charAt(i), ' ');
//					}
//
//					autorizacion = autorizacion.replace("<Autorizacion>", "<autorizacion>");
//					autorizacion = autorizacion.replace("</Autorizacion>", "</autorizacion>");
//
//					if (autorizacion.contains("<RespuestaAutorizacionComprobante>")) {
//						autorizacion = autorizacion.replace("<RespuestaAutorizacionComprobante>", "");
//						autorizacion = autorizacion.replace("</RespuestaAutorizacionComprobante>", "");
//					}
//
//					autorizacion = autorizacion.trim();
//				}
//
//				try {
//					autorizacionXML = getAutorizacionXML(autorizacion);
//				} catch (Exception ex) {
//					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
//
//					String claveConsultar = "";
//					String numeroAutorizcion = "";
//
//					int a = autorizacion.indexOf("<claveAcceso>");
//
//					int desde = a + 13;
//					int hasta = desde + 49;
//
//					claveConsultar = autorizacion.substring(desde, hasta);
//					
//					if (autorizacion.contains("<numeroAutorizacion>") && autorizacion.contains("</numeroAutorizacion>")) {
//						String[] ab = autorizacion.split("<numeroAutorizacion>");
//						numeroAutorizcion = ab[1].split("</numeroAutorizacion>")[0].trim();
//					}
//
//					if(claveConsultar.equals(numeroAutorizcion)){
//						log.info("***Comprobante emitido en modo offline 2 "+claveConsultar);
//						respuesta.setEstado(Estado.ERROR.getDescripcion());
//						respuesta.setMensajeCliente("Comprobante emitido en modo offline " );
//						respuesta.setMensajeSistema("Comprobante emitido en modo offline ");
//						return respuesta;
//					}else{
//						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
//					}
//				}
//			}
//
//			if (autorizacionXML == null) {	
//				log.info("***sri no devolvio ninguna autorizacion "+facturaRecibirRequest.getComprobanteProveedor()+"**mail**"+facturaRecibirRequest.getMailOrigen());
//				respuesta.setEstado(Estado.ERROR.getDescripcion());
//				respuesta.setMensajeCliente("No devolvio datos el sri " );
//				respuesta.setMensajeSistema("No devolvio datos el sri ");				
//				return respuesta;
//			}
//
//			// Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);
//			if (!(autorizacionXML.getComprobante() != null)) {
//				log.info("***sri no devolvio ninguna autorizacion "+facturaRecibirRequest.getComprobanteProveedor()+"**mail**"+facturaRecibirRequest.getMailOrigen());
//				respuesta.setEstado(Estado.ERROR.getDescripcion());
//				respuesta.setMensajeCliente("No devolvio datos el sri " );
//				respuesta.setMensajeSistema("No devolvio datos el sri ");				
//				return respuesta;
//			}
//			
//			String factura = autorizacionXML.getComprobante();
//			factura = factura.replace("<![CDATA[", "");
//			factura = factura.replace("]]>", "");
//
//			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = getFacturaXML(factura);
//			
//			if(facturaXML != null){
//				subtotal=facturaXML.getInfoFactura().getTotalSinImpuestos();				
//				if (facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto() != null
//						&& !facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().isEmpty()) {
//					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
//						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {						
//							if (impu.getCodigoPorcentaje().equals("0")) {							
//								baseImponible0 = impu.getBaseImponible();
//							} else {
//								if (impu.getCodigoPorcentaje().equals("2")) {
//									
//									baseImponible12 = impu.getBaseImponible();
//									iva = impu.getValor();
//								}
//							}
//						}
//					}
//				}
//			}
//			
//			nombreArchivo = "edidlinv_"+facturaXML.getInfoTributaria().getClaveAcceso();
//			log.info("***Traduciendo Reim factura 989***"+nombreArchivo);
//			
//
//			com.gizlocorp.gnvoice.modelo.Factura facturaObj = servicioFactura
//					.obtenerComprobante(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);
//
//			if (facturaObj != null && "AUTORIZADO".equals(facturaObj.getEstado())) {
//				respuesta.setEstado(Estado.ERROR.getDescripcion());
//				respuesta.setMensajeCliente("El comprobante con la clave de acceso " + facturaObj.getClaveAcceso()
//						+ " ya esta registrada en el sistema como " + facturaObj.getTipoGeneracion().getDescripcion());
//
//				respuesta.setMensajeSistema("El comprobante con la clave de acceso " + facturaObj.getClaveAcceso()
//						+ " ya esta registrada en el sistema como " + facturaObj.getTipoGeneracion().getDescripcion());
//				return respuesta;
//			}
//
//			if (facturaObj == null) {
//				facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFactura(facturaXML);
//
//			} else {
//				com.gizlocorp.gnvoice.modelo.Factura facturaAux = facturaObj;
//				facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFactura(facturaXML);
//				facturaObj.setId(facturaAux.getId());
//			}
//
//			facturaObj.setInfoAdicional(StringUtil.validateInfoXML(facturaXML.getInfoTributaria().getRazonSocial().trim()));
//
//			facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
//			facturaObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
//
//			facturaObj.setProceso(facturaRecibirRequest.getProceso());
//			facturaObj.setArchivo(facturaRecibirRequest.getComprobanteProveedor());
//			facturaObj.setFechaRecepcionMail(fechaTMP);			
//			facturaObj.setFechaLecturaTraductor(new Date());
//			facturaObj.setBaseCero(baseImponible0);
//			facturaObj.setBaseDoce(baseImponible12);
//			facturaObj.setIva(iva);
//
//			if (autorizacionXML.getNumeroAutorizacion() == null || autorizacionXML.getNumeroAutorizacion().isEmpty()) {
//				respuesta.setEstado(Estado.ERROR.getDescripcion());
//				respuesta.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
//				respuesta.setMensajeCliente("El comprobante no tiene numero de Autorizacion");
//
//				facturaObj.setEstado(Estado.ERROR.getDescripcion());
//				facturaObj = servicioFactura.recibirFactura(facturaObj, "Comprobante no tiene numero de Autorizacion");
//				return respuesta;
//
//			}
//
//			Organizacion emisor = cacheBean.obtenerOrganizacion(facturaXML.getInfoFactura().getIdentificacionComprador());
//
//			if (emisor == null) {
//				respuesta.setEstado(Estado.ERROR.getDescripcion());
//				respuesta.setMensajeSistema(
//						"No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
//				respuesta.setMensajeCliente(
//						"No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
//
//				facturaObj.setEstado(Estado.ERROR.getDescripcion());
//				facturaObj = servicioFactura.recibirFactura(facturaObj,
//						"No existe registrado un emisor con RUC: " + facturaXML.getInfoTributaria().getRuc());
//				return respuesta;
//			}
//
//			log.info("***Traduciendo Reim***");
//			
//
//			if (facturaRecibirRequest.getMailOrigen() != null && !facturaRecibirRequest.getMailOrigen().isEmpty()) {
//				facturaObj.setCorreoNotificacion(facturaRecibirRequest.getMailOrigen());
//			}
//
//			boolean manual = false;
//			
//			if ((facturaRecibirRequest.getOrdenCompra() != null)
//					&& (!facturaRecibirRequest.getOrdenCompra().isEmpty())) {
//				log.info("ingreso factura manual orden de compra  11" + facturaRecibirRequest.getOrdenCompra());
//				manual = true;
//			}
//
//			// TODO agregar velocity
//			if (facturaRecibirRequest.getProceso().equals(com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {
//				Parametro serRimWS = cacheBean.consultarParametro(com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_WS, emisor.getId());
//				String dirWsRim = serRimWS != null ? serRimWS.getValor() : "";
//
//				Parametro serRimUri = cacheBean.consultarParametro(com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_URI, emisor.getId());
//				String dirUriRim = serRimUri != null ? serRimUri.getValor() : "";
//
//
//				String ordenCompra = null;
//				CeDisResponse ceDisresponse = new CeDisResponse();
//				if (dirWsRim != null && !dirWsRim.isEmpty() && dirUriRim != null && !dirUriRim.isEmpty()) {
//
//					ClienteRimBean clienteRimBean = new ClienteRimBean(dirWsRim, dirUriRim);
//
//					String ordenCompraName = "orden";
//					String ordenCompraName2 = "compra";
//					String ordenCompraName3 = "Orden_de_entrega";
//					if (facturaXML.getInfoAdicional() != null) {
//						for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
//							if ((item.getNombre().trim().toUpperCase().contains(ordenCompraName.toUpperCase())) || (item
//									.getNombre().trim().toUpperCase().contains(ordenCompraName2.toUpperCase()))) {
//								if (item.getNombre().trim().toUpperCase().contains(ordenCompraName3.toUpperCase())) {
//									continue;
//								}
//								ordenCompra = item.getValue().trim();
//								break;
//							}
//						}
//					}
//
//					log.info("***Traduciendo Reim 4***");
//
//					if (ordenCompra != null) {
//						if (ordenCompra.matches("[0-9]*")) {
//							facturaObj.setOrdenCompra(ordenCompra);
//							ceDisresponse = clienteRimBean.llamadaPorOrdenFactura(ordenCompra);
//						} else {
//							ceDisresponse = null;
//						}
//					}
//
//					if ((facturaRecibirRequest.getOrdenCompra() != null)
//							&& (!facturaRecibirRequest.getOrdenCompra().isEmpty()) && manual) {
//						log.info("ingreso factura manual orden de compra 222" + facturaRecibirRequest.getOrdenCompra());
//						if (facturaRecibirRequest.getOrdenCompra().matches("[0-9]*")) {
//							facturaObj.setOrdenCompra(facturaRecibirRequest.getOrdenCompra());
//							log.info("ingreso factura manual orden de compra" + facturaRecibirRequest.getOrdenCompra());
//							ceDisresponse = clienteRimBean.llamadaPorOrdenFactura(facturaRecibirRequest.getOrdenCompra());
//							ordenCompra = facturaRecibirRequest.getOrdenCompra();
//						} else {
//							ceDisresponse = null;
//						}
//					} else {
//						log.info("guardando Ha ocurrido un error nose encuentra el nro orden de compra");
//						respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
//						respuesta.setMensajeCliente("Ha ocurrido un error nose encuentra el nro orden de compra");
//						respuesta.setMensajeSistema("Ha ocurrido un error nose encuentra el nro orden de compra");
//						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//						facturaObj.setEstado(Estado.ERROR.getDescripcion());
//						facturaObj.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra");
//						facturaObj = this.servicioFactura.recibirFactura(facturaObj, "El comprobante no tiene orden de compra ingresar manualmente");
//						return respuesta;
//					}
//
//					if (ceDisresponse == null) {
//						log.info("guardando Ha ocurrido un error no devolvio datos el web service");
//						respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
//						respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
//						respuesta.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
//						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//						facturaObj.setEstado(Estado.ERROR.getDescripcion());
//						facturaObj.setMensajeErrorReim(
//								"Ha ocurrido un error no devolvio datos el web service para en munero de compra."
//										+ ordenCompra);
//						facturaObj = this.servicioFactura.recibirFactura(facturaObj,
//								new StringBuilder().append("El web service no encontro informacion. para la orden")
//										.append(ordenCompra).toString());
//						return respuesta;
//					}
//					
//
//				}
//
//				// fin servicio web
//				log.info("****emvieando parametros****");
//				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");// dd/MM/yyyy
//				SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd/MM/yyyy");
//				Date now = new Date();
//				String strDate = sdfDate.format(now);
//				int num = 2;
//				ArrayList list = new ArrayList();
//				BigDecimal totalUnidades = new BigDecimal(0);
//				if (facturaXML.getDetalles().getDetalle() != null && !facturaXML.getDetalles().getDetalle().isEmpty()) {
//					// llenando columnas detalles.
//					for (Detalle itera : facturaXML.getDetalles().getDetalle()) {
//						num++;
//						Map map = new HashMap();
//						map.put("TDETL", "TDETL");
//						String linea = completar(String.valueOf(num).length(), num);
//
//						map.put("linea", linea);
//						if (itera.getCodigoPrincipal().length() >= 13) {
//							map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC,
//									itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
//						} else {
//							if (itera.getCodigoAuxiliar() != null) {
//								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC,
//										itera.getCodigoAuxiliar().length(), itera.getCodigoAuxiliar()));
//							} else {
//								map.put("UPC", completarEspaciosEmblancosLetras(ConstantesRim.UPC,
//										itera.getCodigoPrincipal().length(), itera.getCodigoPrincipal()));
//							}
//
//						}
//
//						map.put("UPC_Supplement",
//								completarEspaciosEmblancosLetras(ConstantesRim.UPC_Supplement, 0, ""));
//
//						map.put("Item", completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
//
//						map.put("VPN", completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
//
//						map.put("Original_Document_Quantity", completarEspaciosEmblancosNumeros(
//								ConstantesRim.Original_Document_Quantity, 4, String.valueOf(itera.getCantidad())));
//
//						if (itera.getDescuento() != null
//								&& itera.getDescuento().compareTo(new BigDecimal("0.00")) > 0) {
//							map.put("Original_Unit_Cost",
//									completarEspaciosEmblancosNumeros(ConstantesRim.Original_Unit_Cost, 4,
//											String.valueOf(itera.getPrecioTotalSinImpuesto()
//													.divide(itera.getCantidad(), 4, RoundingMode.CEILING)
//													.setScale(2, BigDecimal.ROUND_HALF_UP))));
//						} else {
//							map.put("Original_Unit_Cost", completarEspaciosEmblancosNumeros(
//									ConstantesRim.Original_Unit_Cost, 4,
//									String.valueOf(itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP))));
//						}
//
//						totalUnidades = totalUnidades.add(itera.getCantidad());
//						if (itera.getImpuestos().getImpuesto() != null) {
//							if (!itera.getImpuestos().getImpuesto().isEmpty()) {
//								for (Impuesto item : itera.getImpuestos().getImpuesto()) {
//									if (item.getCodigoPorcentaje().equals("0")) {
//										map.put("Original_VAT_Code",
//												completarEspaciosEmblancosLetras(6, "L".length(), "L"));
//										map.put("Original_VAT_rate", completarEspaciosEmblancosNumeros(20, 10,
//												String.valueOf(item.getTarifa())));
//									} else if (item.getCodigoPorcentaje().equals("2")) {
//										map.put("Original_VAT_Code",
//												completarEspaciosEmblancosLetras(6, "E".length(), "E"));
//										map.put("Original_VAT_rate", completarEspaciosEmblancosNumeros(20, 10,
//												String.valueOf(item.getTarifa())));
//									}
//								}
//							}
//						}
//						map.put("Total_Allowance", completarEspaciosEmblancosNumeros(20, 4, "0"));
//
//						list.add(map);
//					}
//				}
//				ArrayList listTotalImpuesto = new ArrayList();
//				BigDecimal totalImpuesto = new BigDecimal(0);
//				if (facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto() != null
//						&& !facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().isEmpty()) {
//					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
//						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
//							num++;
//							Map map = new HashMap();
//							map.put("TVATS", "TVATS");
//							String linea = completar(String.valueOf(num).length(), num);
//							map.put("linea", linea);
//
//							if (impu.getCodigoPorcentaje().equals("0")) {
//								map.put("VAT_code",
//										completarEspaciosEmblancosLetras(ConstantesRim.VAT_code, "L".length(), "L"));
//
//								map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
//										ConstantesRim.Decimales10, String.valueOf("0")));
//								
//								baseImponible0 = impu.getBaseImponible();
//							} else {
//								if (impu.getCodigoPorcentaje().equals("2")) {
//									map.put("VAT_code", completarEspaciosEmblancosLetras(ConstantesRim.VAT_code,
//											"E".length(), "E"));
//
//									map.put("VAT_rate", completarEspaciosEmblancosNumeros(ConstantesRim.VAT_rate,
//											ConstantesRim.Decimales10, String.valueOf("12")));
//									baseImponible12 = impu.getBaseImponible();
//									iva = impu.getValor();
//								} else {
//									log.info("La factura tiene codigo de impuesto  no contemplado en reim"
//											+ facturaXML.getInfoTributaria().getClaveAcceso());
//									respuesta
//											.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
//									respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
//									respuesta.setMensajeSistema(
//											"La factura tiene codigo de impuesto  no contemplado en reim");
//									respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//									facturaObj.setEstado(Estado.ERROR.getDescripcion());
//									facturaObj.setMensajeErrorReim(
//											"La factura tiene codigo de impuesto  no contemplado en reim");
//									facturaObj = this.servicioFactura.recibirFactura(facturaObj,
//											new StringBuilder()
//													.append("La factura tiene codigo de impuesto  no contemplado en reim")
//													.append(ordenCompra).toString());
//									return respuesta;
//
//								}
//							}
//							// map.put("VAT_rate",
//							// completarEspaciosEmblancosNumeros(
//							// ConstantesRim.VAT_rate,
//							// ConstantesRim.Decimales10,
//							// String.valueOf(impu.getTarifa())));
//
//							map.put("Cost_VAT_code", completarEspaciosEmblancosNumeros(ConstantesRim.Cost_VAT_code,
//									ConstantesRim.Decimales4,
//									String.valueOf(impu.getBaseImponible().setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//							totalImpuesto = totalImpuesto.add(impu.getValor());
//							listTotalImpuesto.add(map);
//						}
//					}
//				}
//				int fdetalle = num + 1;
//				int fin = num + 2;
//				int numLinea = num - 2;
//				int totalLinea = num;
//
//				log.info("***emisor ***" + emisor.getId());
//
//				Parametro dirParametro2 = cacheBean.consultarParametro(com.gizlocorp.gnvoice.utilitario.Constantes.DIR_RIM_ESQUEMA, emisor.getId());
//				// String dirServidor2 = dirParametro2 != null ?
//				// dirParametro2.getValor() : "";
//
//				String dirServidor2 = "/data/gnvoice/recursos/rim_vm/";
//
//
//				VelocityEngine ve = new VelocityEngine();
//				ve.setProperty(
//						RuntimeConstants.FILE_RESOURCE_LOADER_PATH,dirServidor2);
//				ve.setProperty(
//						RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
////				ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
////				ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//				ve.init();
//
//				Template t = ve.getTemplate("traductor.vm");
//				VelocityContext context = new VelocityContext();
//
//				context.put("fecha", strDate);
//				context.put("petList", list);
//
//				context.put("Document_Type",
//						completarEspaciosEmblancosLetras(ConstantesRim.Document_Type, "MRCHI".length(), "MRCHI"));
//
//				String vendorDocument = new StringBuilder().append(facturaXML.getInfoTributaria().getEstab()).append("")
//						.append(facturaXML.getInfoTributaria().getPtoEmi()).append("")
//						.append(facturaXML.getInfoTributaria().getSecuencial()).toString();
//
//				context.put("Vendor_Document_Number", completarEspaciosEmblancosLetras(
//						ConstantesRim.Vendor_Document_Number, vendorDocument.length(), vendorDocument));
//				context.put("Group_ID", completarEspaciosEmblancosLetras(ConstantesRim.Group_ID, "".length(), ""));
//				context.put("Vendor_Type", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Type,
//						ceDisresponse.getVendor_type().length(), ceDisresponse.getVendor_type()));
//
//				context.put("Vendor_ID", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_ID,
//						ceDisresponse.getVendor_id().length(), ceDisresponse.getVendor_id()));
//
//				Date fechaEmision = sdfDate2.parse(facturaXML.getInfoFactura().getFechaEmision());
//				context.put("Vendor_Document_Date", completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Document_Date,
//						sdfDate.format(fechaEmision).length(), sdfDate.format(fechaEmision)));
//
//				context.put("Order_Number",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Order_Number, 0, String.valueOf(ordenCompra)));
//
//				if ((ceDisresponse.getLocation() != null) && (!ceDisresponse.getLocation().isEmpty())) {
//					context.put("Location",
//							completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0, ceDisresponse.getLocation()));
//				} else {
//					context.put("Location", completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0, ""));
//				}
//
//				if ((ceDisresponse.getLocation() != null) && (!ceDisresponse.getLocation().isEmpty())) {
//					context.put("Location_Type", completarEspaciosEmblancosLetras(ConstantesRim.Location_Type,
//							ceDisresponse.getLocation().length(), ceDisresponse.getLocation_type()));
//				} else {
//					context.put("Location_Type",
//							completarEspaciosEmblancosLetras(ConstantesRim.Location_Type, "".length(), ""));
//				}
//
//				context.put("Terms", completarEspaciosEmblancosLetras(ConstantesRim.Terms, "".length(), ""));
//				context.put("Due_Date", completarEspaciosEmblancosLetras(ConstantesRim.Due_Date, "".length(), ""));
//				context.put("Payment_method",
//						completarEspaciosEmblancosLetras(ConstantesRim.Payment_method, "".length(), ""));
//				context.put("Currency_code",
//						completarEspaciosEmblancosLetras(ConstantesRim.Currency_code, "USD".length(), "USD"));
//
//				context.put("Exchange_rate",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Exchange_rate, 4, String.valueOf("1")));
//
//				
//				
//				context.put("Total_Cost", completarEspaciosEmblancosNumeros(ConstantesRim.Total_Cost, 4, String.valueOf(
//						facturaXML.getInfoFactura().getTotalSinImpuestos().setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//				context.put("Total_VAT_Amount", completarEspaciosEmblancosNumeros(ConstantesRim.Total_VAT_Amount, 4,
//						String.valueOf(totalImpuesto.setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//				context.put("Total_Quantity", completarEspaciosEmblancosNumeros(ConstantesRim.Total_Quantity, 4,
//						String.valueOf(totalUnidades)));
//
//				context.put("Total_Discount",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Discount, 4, String.valueOf(facturaXML
//								.getInfoFactura().getTotalDescuento().setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//				context.put("Freight_Type",
//						completarEspaciosEmblancosLetras(ConstantesRim.Freight_Type, "".length(), ""));
//				context.put("Paid_Ind", "N");
//				context.put("Multi_Location", "N");
//				context.put("Merchan_dise_Type", "N");
//				context.put("Deal_Id", completarEspaciosEmblancosLetras(ConstantesRim.Deal_Id, "".length(), ""));
//				context.put("Deal_Approval_Indicator",
//						completarEspaciosEmblancosLetras(ConstantesRim.Deal_Approval_Indicator, "".length(), ""));
//				context.put("RTV_indicator", "N");
//
//				context.put("Custom_Document_Reference_1",
//						completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_1,
//								autorizacionXML.getNumeroAutorizacion().length(),
//								autorizacionXML.getNumeroAutorizacion()));
//
//				String fechaAutorizacion = "";
//				if (autorizacionXML.getFechaAutorizacion() != null) {
//					fechaAutorizacion = FechaUtil.formatearFecha(
//							autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime(), "dd/MM/yyyy HH:mm");
//				} else {
//					fechaAutorizacion = facturaXML.getInfoFactura().getFechaEmision();
//				}
//
//				context.put("Custom_Document_Reference_2", completarEspaciosEmblancosLetras(
//						ConstantesRim.Custom_Document_Reference_2, fechaAutorizacion.length(), fechaAutorizacion));
//
//				context.put("Custom_Document_Reference_3",
//						completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_3,
//								emisor.getCeDisVirtual().length(), emisor.getCeDisVirtual()));
//
//				// context.put(
//				// "Custom_Document_Reference_3",
//				// completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_3,
//				// ceDisresponse
//				// .getCedis_virtual().length(), ceDisresponse
//				// .getCedis_virtual()));
//
//				context.put("Custom_Document_Reference_4",
//						completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_4, "".length(), ""));
//				context.put("Cross_reference",
//						completarEspaciosEmblancosLetras(ConstantesRim.Cross_reference, "".length(), ""));
//
//				context.put("petListTotalImpuesto", listTotalImpuesto);
//				context.put("secuencial", facturaXML.getInfoTributaria().getSecuencial());
//
//				context.put("finT", completar(String.valueOf(fdetalle).length(), fdetalle));
//				context.put("finF", completar(String.valueOf(fin).length(), fin));
//				context.put("numerolineaDestalle", completarLineaDetalle(String.valueOf(numLinea).length(), numLinea));
//				context.put("totalLinea", completar(String.valueOf(totalLinea).length(), totalLinea));
//
//				StringWriter writer = new StringWriter();
//				t.merge(context, writer);
//				Calendar now2 = Calendar.getInstance();
//				Parametro repositorioRim = this.cacheBean.consultarParametro("RIM_REPOSITO_ARCHIVO", emisor.getId());
//
//				String stvrepositorioRim = repositorioRim != null ? repositorioRim.getValor() : "";
//
//				Random rng = new Random();
//				int dig5 = rng.nextInt(90000)+10000; //siempre 5 digitos
//
////				String nombreArchivo = new StringBuilder().append("edidlinv_")
////						.append(FechaUtil.formatearFecha(now2.getTime(), "yyyyMMddHHmmssSSS").toString()).append(dig5).toString();
//
//				log.info("***creando archivo traducido all***"+nombreArchivo);
//
//				String archivoCreado = DocumentUtil.createDocumentText(writer.toString().getBytes(), nombreArchivo,
//						FechaUtil.formatearFecha(now2.getTime(), "yyyyMMdd").toString(), stvrepositorioRim);
//				String archivoCreadoProcesado = archivoCreado+ ".loaded";
//				File fileOut = new File(archivoCreado);
//				File fileLoaded = new File(archivoCreadoProcesado);
//				boolean existFileOut = fileOut!=null && fileOut.exists() && !fileOut.isDirectory();
//				boolean existFileLoaded = fileLoaded!= null && fileLoaded.exists() && !fileLoaded.isDirectory();
//				
//				if( existFileOut ||  existFileLoaded ) { 
//					facturaObj.setFechaEntReim(new Date());
//					facturaObj.setArchivoGenReim(archivoCreado);	
//				}else{
//					respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
//					respuesta.setEstado(Estado.ERROR.getDescripcion());
//					MensajeRespuesta msgObj = new MensajeRespuesta();
//					msgObj.setIdentificador("-100");
//					msgObj.setMensaje("Ha ocurrido un problema: " + "No se creo el archivo .out reim");
//					respuesta.getMensajes().add(msgObj);
//					respuesta.setMensajeCliente("No se creo el archivo .out reim");
//					respuesta.setMensajeSistema("No se creo el archivo .out reim");
//					System.out.println("No se creo el archivo .out reim");
//					return respuesta;
//				}			
//			}
//
//			// fin agregado rim
//
//			facturaObj.setNumeroAutorizacion(autorizacionXML.getNumeroAutorizacion());
//			if (autorizacionXML.getFechaAutorizacion() != null) {
//				facturaObj.setFechaAutorizacion(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime());
//			} else {
//				SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
//				facturaObj.setFechaAutorizacion(formato.parse(facturaXML.getInfoFactura().getFechaEmision()));
//			}
//
//			facturaObj.setEstado("AUTORIZADO");
//			facturaObj.setArchivo(facturaRecibirRequest.getComprobanteProveedor());
//			facturaObj.setTareaActual(Tarea.AUT);
//			facturaObj.setTipoGeneracion(TipoGeneracion.REC);
//
//			// si no existe pdf creo pdf
//			Parametro dirParametro = cacheBean.consultarParametro(com.gizlocorp.gnvoice.utilitario.Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
//			String dirServidor = dirParametro != null ? dirParametro.getValor() : "";
//			
//			facturaObj = servicioFactura.recibirFactura(facturaObj, "El comprobante se a cargado de forma exitosa");
//			respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//			respuesta.setMensajeCliente("El comprobante se a cargado de forma exitosa");
//			respuesta.setMensajeSistema("El comprobante se a cargado de forma exitosa");
//
//		} catch (Exception ex) {
//			log.info(ex.toString()+"**"+facturaRecibirRequest.getComprobanteProveedor());
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
//
//		return respuesta;
//	}
	
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
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FacturaRecibirResponse recibirFactura(
			FacturaRecibirRequest facturaRecibirRequest, Date fechaTMP) {

		FacturaRecibirResponse respuesta = new FacturaRecibirResponse();

		BigDecimal baseImponible0 = BigDecimal.ZERO;
		BigDecimal baseImponible12 = BigDecimal.ZERO;
		BigDecimal iva = BigDecimal.ZERO;
		BigDecimal subtotal = BigDecimal.ZERO;
		String nombreArchivo = "";
		
		BigDecimal baseImponibleICE = BigDecimal.ZERO;
		BigDecimal baseImponibleIRBP = BigDecimal.ZERO;
		BigDecimal ice = BigDecimal.ZERO;
		BigDecimal irbp = BigDecimal.ZERO;
		
		boolean offline = false;

		try {
			String autorizacion = DocumentUtil
					.readContentFile(facturaRecibirRequest
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
			
			/*********/

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
			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = null;
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
				String factura = autorizacionOfflineXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
//				facturaXML = getFacturaXML(factura);
				try{
					facturaXML = getFacturaXML(factura);
				}catch (Exception ex) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

					String claveConsultar = "";
					String numeroAutorizcion = "";
					
					if (autorizacion.contains("<claveAcceso>") && autorizacion.contains("</claveAcceso>")) {
						String[] ab = autorizacion.split("<claveAcceso>");
						claveConsultar = ab[1].split("</claveAcceso>")[0].trim();
					}

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
				String factura = autorizacionXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
//				facturaXML = getFacturaXML(factura);
				try{
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
						String[] ab = autorizacion
								.split("<numeroAutorizacion>");
						numeroAutorizcion = ab[1]
								.split("</numeroAutorizacion>")[0].trim();
					}
					

					if (claveConsultar.equals(numeroAutorizcion)) {				
						String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";						
						autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);
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

//			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = getFacturaXML(factura);
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
							 ice = impu.getValor();
						}
						
						if("5".equals(impu.getCodigo())){
							baseImponibleIRBP  = impu.getBaseImponible();
							irbp = impu.getValor();
						}
						
						
					}
				}
			}

			nombreArchivo = "edidlinv_"
					+ facturaXML.getInfoTributaria().getClaveAcceso();
			log.info("***Traduciendo Reim factura 989***" + nombreArchivo);

			com.gizlocorp.gnvoice.modelo.Factura facturaObj = servicioFactura
					.obtenerComprobante(facturaXML.getInfoTributaria()
							.getClaveAcceso(), null, null, null);

			if (facturaObj != null
					&& "AUTORIZADO".equals(facturaObj.getEstado())) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta
						.setMensajeCliente("El comprobante con la clave de acceso "
								+ facturaObj.getClaveAcceso()
								+ " ya esta registrada en el sistema como "
								+ facturaObj.getTipoGeneracion()
										.getDescripcion());

				respuesta
						.setMensajeSistema("El comprobante con la clave de acceso "
								+ facturaObj.getClaveAcceso()
								+ " ya esta registrada en el sistema como "
								+ facturaObj.getTipoGeneracion()
										.getDescripcion());
				return respuesta;
			}

			if (facturaObj == null) {
				facturaObj = ComprobanteUtil
						.convertirEsquemaAEntidadFactura(facturaXML);

			} else {
				com.gizlocorp.gnvoice.modelo.Factura facturaAux = facturaObj;
				facturaObj = ComprobanteUtil
						.convertirEsquemaAEntidadFactura(facturaXML);
				facturaObj.setId(facturaAux.getId());
			}
			
			if(facturaObj.getContribuyenteEspecial().length()>5){
				int leng = facturaObj.getContribuyenteEspecial().length();
				facturaObj.setContribuyenteEspecial(facturaObj.getContribuyenteEspecial().substring(leng-5, leng));
			}
			
			if(facturaObj.getObligadoContabilidad()== null){
				facturaObj.setObligadoContabilidad("NO");
			}			

			facturaObj.setInfoAdicional(StringUtil.validateInfoXML(facturaXML
					.getInfoTributaria().getRazonSocial().trim()));

			facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
			facturaObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			facturaObj.setProceso(facturaRecibirRequest.getProceso());
			facturaObj.setArchivo(facturaRecibirRequest
					.getComprobanteProveedor());

			facturaObj.setFechaLecturaTraductor(new Date());
			facturaObj.setBaseCero(baseImponible0);
			facturaObj.setBaseDoce(baseImponible12);
			facturaObj.setIva(iva);
			

		

			if(offline){
				if (autorizacionOfflineXML.getNumeroAutorizacion() == null
						|| autorizacionOfflineXML.getNumeroAutorizacion().isEmpty()) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta
							.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
					respuesta
							.setMensajeCliente("El comprobante no tiene numero de Autorizacion");

					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj = servicioFactura.recibirFactura(facturaObj,
							"Comprobante no tiene numero de Autorizacion");
					return respuesta;

				}
			}else{
				if (autorizacionXML.getNumeroAutorizacion() == null
						|| autorizacionXML.getNumeroAutorizacion().isEmpty()) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta
							.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
					respuesta
							.setMensajeCliente("El comprobante no tiene numero de Autorizacion");

					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj = servicioFactura.recibirFactura(facturaObj,
							"Comprobante no tiene numero de Autorizacion");
					return respuesta;

				}
			}
			

			Organizacion emisor = cacheBean.obtenerOrganizacion(facturaXML
					.getInfoFactura().getIdentificacionComprador());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta
						.setMensajeSistema("No existe registrado un emisor con RUC: "
								+ facturaXML.getInfoTributaria().getRuc());
				respuesta
						.setMensajeCliente("No existe registrado un emisor con RUC: "
								+ facturaXML.getInfoTributaria().getRuc());

				facturaObj.setEstado(Estado.ERROR.getDescripcion());
				facturaObj = servicioFactura.recibirFactura(facturaObj,
						"No existe registrado un emisor con RUC: "
								+ facturaXML.getInfoTributaria().getRuc());
				return respuesta;
			}

			

			if (facturaRecibirRequest.getMailOrigen() != null
					&& !facturaRecibirRequest.getMailOrigen().isEmpty()) {
				facturaObj.setCorreoNotificacion(facturaRecibirRequest
						.getMailOrigen());
			}

			boolean manual = false;

			if ((facturaRecibirRequest.getOrdenCompra() != null)
					&& (!facturaRecibirRequest.getOrdenCompra().isEmpty())) {
			
				manual = true;
			}

			// TODO agregar velocity
			if (facturaRecibirRequest.getProceso().equals(
					com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {
				Parametro serRimWS = cacheBean.consultarParametro(
						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_WS, emisor.getId());
				String dirWsRim = serRimWS != null ? serRimWS.getValor() : "";

				Parametro serRimUri = cacheBean.consultarParametro(
						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_URI, emisor.getId());
				String dirUriRim = serRimUri != null ? serRimUri.getValor()
						: "";

				String ordenCompra = null;
				CeDisResponse ceDisresponse = new CeDisResponse();
				if (dirWsRim != null && !dirWsRim.isEmpty()
						&& dirUriRim != null && !dirUriRim.isEmpty()) {

					ClienteRimBean clienteRimBean = new ClienteRimBean(
							dirWsRim, dirUriRim);

					String ordenCompraName = "orden";
					String ordenCompraName2 = "compra";
					String ordenCompraName3 = "Orden_de_entrega";
					
					
					
					if (facturaXML.getInfoAdicional() != null) {

						List<ParametroOrdenCompra> listaParametros = servicioParametroOrdenCompra.listaParametroRucProveedor(facturaXML.getInfoTributaria().getRuc());

						if (listaParametros != null && listaParametros.isEmpty()) {
							for (ParametroOrdenCompra parametro : listaParametros) {

								for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
									if (parametro.getValor().toUpperCase().equals(item.getNombre().trim().toUpperCase())) {
										ordenCompra = item.getValue().trim();
										break;
									}
								}
							}
						} else {
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
					
					/*if (facturaXML.getInfoAdicional() != null) {
						
						for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
							
							if ((item.getNombre().trim().toUpperCase().contains(ordenCompraName.toUpperCase()))
							   || (item.getNombre().trim().toUpperCase().contains(ordenCompraName2
													.toUpperCase()))) {
								
								if (item.getNombre().trim().toUpperCase().contains(ordenCompraName3.toUpperCase())) {
									continue;
								}
								ordenCompra = item.getValue().trim();
								break;
							}
						}
					}*/
					
					
					

					if (ordenCompra != null) {
						if (ordenCompra.matches("[0-9]*")) {
							facturaObj.setOrdenCompra(ordenCompra);
							// TODO
							ceDisresponse = clienteRimBean
									.llamadaPorOrdenFactura(ordenCompra);
						} else {
							ceDisresponse = null;
						}
					}

					if ((facturaRecibirRequest.getOrdenCompra() != null)
							&& (!facturaRecibirRequest.getOrdenCompra()
									.isEmpty()) && manual) {
						
						if (facturaRecibirRequest.getOrdenCompra().matches(
								"[0-9]*")) {
							facturaObj.setOrdenCompra(facturaRecibirRequest
									.getOrdenCompra());
							// TODO
							ceDisresponse = clienteRimBean
									.llamadaPorOrdenFactura(facturaRecibirRequest
											.getOrdenCompra());
							ordenCompra = facturaRecibirRequest
									.getOrdenCompra();
						} else {
							ceDisresponse = null;
						}
					}

					if (ordenCompra == null || ordenCompra.isEmpty()) {
						
						respuesta.setClaveAccesoComprobante(facturaXML
								.getInfoTributaria().getClaveAcceso());
						respuesta
								.setMensajeCliente("Ha ocurrido un error nose encuentra el nro orden de compra");
						respuesta
								.setMensajeSistema("Ha ocurrido un error nose encuentra el nro orden de compra");
						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
						facturaObj.setEstado(Estado.ERROR.getDescripcion());
						facturaObj
								.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra");
						facturaObj = this.servicioFactura
								.recibirFactura(facturaObj,
										"El comprobante no tiene orden de compra ingresar manualmente");
						return respuesta;
					}

					if (ceDisresponse == null) {
						
						respuesta.setClaveAccesoComprobante(facturaXML
								.getInfoTributaria().getClaveAcceso());
						respuesta
								.setMensajeCliente("Ha ocurrido un error en el proceso");
						respuesta
								.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
						facturaObj.setEstado(Estado.ERROR.getDescripcion());
						facturaObj
								.setMensajeErrorReim("Ha ocurrido un error no devolvio datos el web service para en munero de compra."
										+ ordenCompra);
						facturaObj = this.servicioFactura
								.recibirFactura(
										facturaObj,
										new StringBuilder()
												.append("El web service no encontro informacion. para la orden")
												.append(ordenCompra).toString());
						return respuesta;
					}

				}

				// fin servicio web

				SimpleDateFormat sdfDate = new SimpleDateFormat(
						"yyyyMMddHHmmss");// dd/MM/yyyy
				SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd/MM/yyyy");
				Date now = new Date();
				String strDate = sdfDate.format(now);
				int num = 2;
				ArrayList list = new ArrayList();
				BigDecimal totalUnidades = new BigDecimal(0);
				if (facturaXML.getDetalles().getDetalle() != null
						&& !facturaXML.getDetalles().getDetalle().isEmpty()) {
					// llenando columnas detalles.
					for (Detalle itera : facturaXML.getDetalles().getDetalle()) {
						num++;
						Map map = new HashMap();
						map.put("TDETL", "TDETL");
						String linea = completar(String.valueOf(num).length(),
								num);

						map.put("linea", linea);
						boolean booVpn=false;
						try{
							if (validaEAN(itera.getCodigoPrincipal())) {
								map.put("UPC",
										completarEspaciosEmblancosLetras(
												ConstantesRim.UPC, itera
														.getCodigoPrincipal()
														.length(), itera
														.getCodigoPrincipal()));							
							} else {
								if(validaEAN(itera.getCodigoAuxiliar())){
									map.put("UPC",
											completarEspaciosEmblancosLetras(
													ConstantesRim.UPC, itera
															.getCodigoAuxiliar()
															.length(), itera
															.getCodigoAuxiliar()));								
								}else{
									map.put("UPC",completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0, ""));
									booVpn = true;
								}
							}
						}catch(Exception e){
				        	e.printStackTrace();
				        }
						map.put("UPC_Supplement",
								completarEspaciosEmblancosLetras(
										ConstantesRim.UPC_Supplement, 0, ""));
						map.put("Item",
								completarEspaciosEmblancosLetras(
										ConstantesRim.Item, 0, ""));
						if(booVpn){
							map.put("VPN",
									completarEspaciosEmblancosLetras(
											ConstantesRim.VPN, itera
											.getCodigoPrincipal()
											.length(), itera
											.getCodigoPrincipal()));
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
							map.put("Original_Unit_Cost",
									completarEspaciosEmblancosNumeros(
											ConstantesRim.Original_Unit_Cost,
											4,
											String.valueOf(itera
													.getPrecioTotalSinImpuesto()
													.divide(itera.getCantidad(),
															4,
															RoundingMode.CEILING)
													.setScale(
															2,
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

						totalUnidades = totalUnidades.add(itera.getCantidad());
						if (itera.getImpuestos().getImpuesto() != null) {
							if (!itera.getImpuestos().getImpuesto().isEmpty()) {
								for (Impuesto item : itera.getImpuestos()
										.getImpuesto()) {
									if (item.getCodigoPorcentaje().equals("0")) {
										map.put("Original_VAT_Code",
												completarEspaciosEmblancosLetras(
														6, "L".length(), "L"));
										map.put("Original_VAT_rate",
												completarEspaciosEmblancosNumeros(
														20, 10,
														String.valueOf(item
																.getTarifa())));
									} else if (item.getCodigoPorcentaje()
											.equals("2")) {
										map.put("Original_VAT_Code",
												completarEspaciosEmblancosLetras(
														6, "E".length(), "E"));
										map.put("Original_VAT_rate",
												completarEspaciosEmblancosNumeros(
														20, 10,
														String.valueOf(item
																.getTarifa())));
									}else if (item.getCodigoPorcentaje()
											.equals("3")) {
										map.put("Original_VAT_Code",
												completarEspaciosEmblancosLetras(
														6, "E".length(), "E"));
										map.put("Original_VAT_rate",
												completarEspaciosEmblancosNumeros(
														20, 10,
														String.valueOf(item
																.getTarifa())));
									}
								}
							}
						}
						map.put("Total_Allowance",
								completarEspaciosEmblancosNumeros(20, 4, "0"));

						list.add(map);
					}
				}
				ArrayList listTotalImpuesto = new ArrayList();
				BigDecimal totalImpuesto = new BigDecimal(0);
				if (facturaXML.getInfoFactura().getTotalConImpuestos()
						.getTotalImpuesto() != null
						&& !facturaXML.getInfoFactura().getTotalConImpuestos()
								.getTotalImpuesto().isEmpty()) {
					for (TotalImpuesto impu : facturaXML.getInfoFactura()
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
								if (impu.getCodigoPorcentaje().equals("2") || impu.getCodigoPorcentaje().equals("3")) {
									map.put("VAT_code",
											completarEspaciosEmblancosLetras(
													ConstantesRim.VAT_code,
													"E".length(), "E"));

									map.put("VAT_rate",
											completarEspaciosEmblancosNumeros(
													ConstantesRim.VAT_rate,
													ConstantesRim.Decimales10,									
													String.valueOf("12")));
									
									if(impu.getCodigoPorcentaje().equals("3")){
										map.put("VAT_rate",
												completarEspaciosEmblancosNumeros(
														ConstantesRim.VAT_rate,
														ConstantesRim.Decimales10,
														String.valueOf("14")));
									}
									
									
								} else {
									log.info("La factura tiene codigo de impuesto  no contemplado en reim"
											+ facturaXML.getInfoTributaria()
													.getClaveAcceso());
									respuesta
											.setClaveAccesoComprobante(facturaXML
													.getInfoTributaria()
													.getClaveAcceso());
									respuesta
											.setMensajeCliente("Ha ocurrido un error en el proceso");
									respuesta
											.setMensajeSistema("La factura tiene codigo de impuesto  no contemplado en reim");
									respuesta.setEstado(Estado.COMPLETADO
											.getDescripcion());
									facturaObj.setEstado(Estado.ERROR
											.getDescripcion());
									facturaObj
											.setMensajeErrorReim("La factura tiene codigo de impuesto  no contemplado en reim");
									facturaObj = this.servicioFactura
											.recibirFactura(
													facturaObj,
													new StringBuilder()
															.append("La factura tiene codigo de impuesto  no contemplado en reim")
															.append(ordenCompra)
															.toString());
									return respuesta;

								}
							}
							log.info("***Traduciendo Reim factura 333***");
							// map.put("VAT_rate",
							// completarEspaciosEmblancosNumeros(
							// ConstantesRim.VAT_rate,
							// ConstantesRim.Decimales10,
							// String.valueOf(impu.getTarifa())));

							map.put("Cost_VAT_code",
									completarEspaciosEmblancosNumeros(
											ConstantesRim.Cost_VAT_code,
											ConstantesRim.Decimales4,
											String.valueOf(impu
													.getBaseImponible()
													.setScale(
															2,
															BigDecimal.ROUND_HALF_UP))));

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
						com.gizlocorp.gnvoice.utilitario.Constantes.DIR_RIM_ESQUEMA, emisor.getId());
				// String dirServidor2 = dirParametro2 != null ?
				// dirParametro2.getValor() : "";

				String dirServidor2 = "/data/gnvoice/recursos/rim_vm/";

				VelocityEngine ve = new VelocityEngine();
				ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
						dirServidor2);
				ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
						"true");
				// ve.setProperty(RuntimeConstants.RESOURCE_LOADER,
				// "classpath");
				// ve.setProperty("classpath.resource.loader.class",
				// ClasspathResourceLoader.class.getName());
				ve.init();

				Template t = ve.getTemplate("traductor.vm");
				VelocityContext context = new VelocityContext();

				context.put("fecha", strDate);
				context.put("petList", list);

				context.put(
						"Document_Type",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Document_Type, "MRCHI".length(),
								"MRCHI"));

				String vendorDocument = new StringBuilder()
						.append(facturaXML.getInfoTributaria().getEstab())
						.append("")
						.append(facturaXML.getInfoTributaria().getPtoEmi())
						.append("")
						.append(facturaXML.getInfoTributaria().getSecuencial())
						.toString();

				context.put(
						"Vendor_Document_Number",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Vendor_Document_Number,
								vendorDocument.length(), vendorDocument));
				context.put(
						"Group_ID",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Group_ID, "".length(), ""));
				log.info("***Traduciendo Reim factura 444***");
				if (ceDisresponse == null || ceDisresponse.getVendor_type()== null||ceDisresponse.getVendor_id()== null) {
					respuesta.setClaveAccesoComprobante(facturaXML
							.getInfoTributaria().getClaveAcceso());
					respuesta
							.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta
							.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service ");
					respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
					facturaObj.setEstado(Estado.ERROR.getDescripcion());
					facturaObj
							.setMensajeErrorReim("Ha ocurrido un error no devolvio datos el web service para en munero de compra."
									+ ordenCompra);
					facturaObj = this.servicioFactura
							.recibirFactura(
									facturaObj,
									new StringBuilder()
											.append("El web service no encontro informacion. para la orden")
											.append(ordenCompra).toString());
					return respuesta;
				}
				
				log.info("***Traduciendo Reim factura 555***");
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

				Date fechaEmision = sdfDate2.parse(facturaXML.getInfoFactura()
						.getFechaEmision());
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
								String.valueOf(ordenCompra)));

				if ((ceDisresponse.getLocation() != null)
						&& (!ceDisresponse.getLocation().isEmpty())) {
					context.put(
							"Location",
							completarEspaciosEmblancosNumeros(
									ConstantesRim.Location, 0,
									ceDisresponse.getLocation()));
				} else {
					context.put(
							"Location",
							completarEspaciosEmblancosNumeros(
									ConstantesRim.Location, 0, ""));
				}

				if ((ceDisresponse.getLocation() != null)
						&& (!ceDisresponse.getLocation().isEmpty())) {
					context.put(
							"Location_Type",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Location_Type, ceDisresponse
											.getLocation().length(),
									ceDisresponse.getLocation_type()));
				} else {
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
								String.valueOf(facturaXML.getInfoFactura()
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
								ConstantesRim.Total_Discount,
								4,
								String.valueOf(facturaXML.getInfoFactura()
										.getTotalDescuento()
										.setScale(2, BigDecimal.ROUND_HALF_UP))));

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
				context.put("RTV_indicator", "N");

				if(offline){
					context.put(
							"Custom_Document_Reference_1",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Custom_Document_Reference_1,
									autorizacionOfflineXML.getNumeroAutorizacion()
											.length(), autorizacionOfflineXML
											.getNumeroAutorizacion()));
				}else{
					context.put(
							"Custom_Document_Reference_1",
							completarEspaciosEmblancosLetras(
									ConstantesRim.Custom_Document_Reference_1,
									autorizacionXML.getNumeroAutorizacion()
											.length(), autorizacionXML
											.getNumeroAutorizacion()));
				}
				

				String fechaAutorizacion = "";
				if (autorizacionXML.getFechaAutorizacion() != null) {
					fechaAutorizacion = FechaUtil.formatearFecha(
							autorizacionXML.getFechaAutorizacion()
									.toGregorianCalendar().getTime(),
							"dd/MM/yyyy HH:mm");
				} else {
					fechaAutorizacion = facturaXML.getInfoFactura()
							.getFechaEmision();
				}

				context.put(
						"Custom_Document_Reference_2",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Custom_Document_Reference_2,
								fechaAutorizacion.length(), fechaAutorizacion));

				context.put(
						"Custom_Document_Reference_3",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Custom_Document_Reference_3,
								emisor.getCeDisVirtual().length(),
								emisor.getCeDisVirtual()));

				// context.put(
				// "Custom_Document_Reference_3",
				// completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_3,
				// ceDisresponse
				// .getCedis_virtual().length(), ceDisresponse
				// .getCedis_virtual()));

				context.put(
						"Custom_Document_Reference_4",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Custom_Document_Reference_4,
								"".length(), ""));
				context.put(
						"Cross_reference",
						completarEspaciosEmblancosLetras(
								ConstantesRim.Cross_reference, "".length(), ""));

				context.put("petListTotalImpuesto", listTotalImpuesto);
				context.put("secuencial", facturaXML.getInfoTributaria()
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

				// if(facturaXML.getInfoTributaria().getClaveAcceso() !=null &&
				// !facturaXML.getInfoTributaria().getClaveAcceso().isEmpty()){
				// nombreArchivo =
				// facturaXML.getInfoTributaria().getClaveAcceso().toString();
				// }else{
				// nombreArchivo = new StringBuilder().append("edidlinv_")
				// .append(FechaUtil.formatearFecha(now2.getTime(),
				// "yyyyMMddHHmmssSSS").toString()).append(dig5).toString();
				// }

				log.info("***creando archivo traducido factura***"
						+ nombreArchivo);

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
			facturaObj.setArchivo(facturaRecibirRequest
					.getComprobanteProveedor());
			facturaObj.setTareaActual(Tarea.AUT);
			facturaObj.setTipoGeneracion(TipoGeneracion.REC);

			// si no existe pdf creo pdf
			Parametro dirParametro = cacheBean.consultarParametro(
					com.gizlocorp.gnvoice.utilitario.Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
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
			log.info(ex.toString() + "**"
					+ facturaRecibirRequest.getComprobanteProveedor());
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

	private Autorizacion getAutorizacionXML(String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (Autorizacion) converter.convertirAObjeto(comprobanteXML, Autorizacion.class);
	}
	
	public boolean validaEAN(String codigo){
		boolean retorno = false;		
		if(codigo != null && !codigo.isEmpty() && (codigo.length() == 13)){
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

	
	private String getFacturaXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.factura.Factura) converter.convertirAObjeto(comprobanteXML,
				com.gizlocorp.gnvoice.xml.factura.Factura.class);
	}

//	public NotaCreditoRecibirResponse recibirNotaCredito(NotaCreditoRecibirRequest mensaje, Date fechcaTMP) {
//		NotaCreditoRecibirResponse respuesta = new NotaCreditoRecibirResponse();
//		String nombreArchivo = "";
//		try {
//			String autorizacion = DocumentUtil.readContentFile(mensaje
//					.getComprobanteProveedor());
//
//			if (autorizacion != null && !autorizacion.isEmpty()) {
//				String tmp = autorizacion;
//				for (int i = 0; i < tmp.length(); i++) {
//					if (tmp.charAt(i) == '<') {
//						break;
//					}
//					autorizacion = autorizacion.replace(tmp.charAt(i), ' ');
//				}
//
//				autorizacion = autorizacion.replace("<Autorizacion>",
//						"<autorizacion>");
//				autorizacion = autorizacion.replace("</Autorizacion>",
//						"</autorizacion>");
//
//				autorizacion = autorizacion.trim();
//			}
//			
//			Autorizacion autorizacionXML=null;
//			try{
//				autorizacionXML = getAutorizacionXML(autorizacion);
//			}catch(Exception ex) {
//				String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
//				
//				String claveConsultar="";
//				if(autorizacion.contains("&lt;claveAcceso&gt;")){
//					int a = autorizacion.indexOf("&lt;claveAcceso&gt;");
//					
//					
//					int desde = a+19;
//					int hasta = desde+49;
//					
//					claveConsultar = autorizacion.substring(desde, hasta);
//					
//					
//				}else{
//					int a = autorizacion.indexOf("<claveAcceso>");
//					
//					
//					int desde = a+13;
//					int hasta = desde+49;
//					
//					claveConsultar = autorizacion.substring(desde, hasta);
//				}
//				
//				autorizacionXML = autorizarComprobante( wsdlLocationAutorizacion, claveConsultar);
//			}
//			
//			if( autorizacionXML == null){
//				return respuesta;
//			}
//			
////			Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);
//
//			String documento = autorizacionXML.getComprobante();
//			documento = documento.replace("<![CDATA[", "");
//			documento = documento.replace("]]>", "");
//
//			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito documentoXML = getNotaCreditoXML(documento);
//			
//			nombreArchivo = "edidlinv_"+documentoXML.getInfoTributaria().getClaveAcceso();
//			log.info("***Traduciendo Reim nota credito 989***"+nombreArchivo);
//
//			com.gizlocorp.gnvoice.modelo.NotaCredito documentoObj = servicioNotaCredito
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
//			if (documentoObj == null) {
//				documentoObj = ComprobanteUtil
//						.convertirEsquemaAEntidadNotacredito(documentoXML);
//			} else {
//				com.gizlocorp.gnvoice.modelo.NotaCredito documentoAux = documentoObj;
//				documentoObj = ComprobanteUtil
//						.convertirEsquemaAEntidadNotacredito(documentoXML);
//				documentoObj.setId(documentoAux.getId());
//			}
//			
//			documentoObj.setInfoAdicional(StringUtil.validateInfoXML(documentoXML.getInfoTributaria().getRazonSocial().trim()));
//			documentoObj.setTipoEjecucion(TipoEjecucion.SEC);
//			documentoObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
//			documentoObj.setProceso(mensaje.getProceso());
//			documentoObj.setArchivo(mensaje.getComprobanteProveedor());
//			documentoObj.setFechaRecepcionMail(fechcaTMP);
//			documentoObj.setFechaLecturaTraductor(new Date());
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
//			Parametro ambParametro = cacheBean.consultarParametro(
//					Constantes.AMBIENTE, emisor.getId());
//			String ambiente = ambParametro.getValor();
//
//			//creacion de documentos doc class
//			
//			if(mensaje.getMailOrigen() != null && !mensaje.getMailOrigen().isEmpty()){
//				Parametro serRutaDocCalss = cacheBean.consultarParametro(
//						com.gizlocorp.gnvoice.utilitario.Constantes.DOC_CLASS_RUTA, emisor.getId());
//				String strRutaDocCalss = serRutaDocCalss != null ? serRutaDocCalss
//						.getValor() : "";
//				
//				DocumentUtil.createDocumentDocClass(
//						getNotaCreditoXML(autorizacionXML), documentoXML
//								.getInfoTributaria().getClaveAcceso(),
//						strRutaDocCalss);
//
//				if ((mensaje.getComprobanteProveedorPdf() != null)
//						&& (!mensaje.getComprobanteProveedorPdf().isEmpty())) {
//					File file = new File(mensaje.getComprobanteProveedorPdf()
//							.toString());
//					if (file.exists()) {
//						byte[] bFile = new byte[(int) file.length()];
//						DocumentUtil.createDocumentPdfDocClass(bFile,
//								documentoXML
//								.getInfoTributaria().getClaveAcceso(),
//								strRutaDocCalss);
//					}else{
//						File file2 = new File(mensaje.getComprobanteProveedorPdf().replace("pdf", "PDF"));						
//						if (file2.exists()) {
//							byte[] bFile = new byte[(int) file2.length()];
//							DocumentUtil.createDocumentPdfDocClass(bFile,
//									autorizacionXML.getClaveAcceso(),
//									strRutaDocCalss);
//						}
//					}
//
//				}
//				documentoObj.setCorreoNotificacion(mensaje.getMailOrigen());
//			}
//			
//			
//
//			// TODO agregar velocity
//			log.info("***Comenzando a traducir nota credito***");
//			
//			
//
//			if (mensaje.getProceso().equals(
//					com.gizlocorp.adm.utilitario.RimEnum.REIM.name())) {
//				Parametro serRimWS = cacheBean.consultarParametro(
//						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_WS, emisor.getId());
//				String dirWsRim = serRimWS != null ? serRimWS.getValor() : "";
//
//				Parametro serRimUri = cacheBean.consultarParametro(
//						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_URI, emisor.getId());
//				String dirUriRim = serRimUri != null ? serRimUri.getValor()
//						: "";
//
//				
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
//								log.info("***Comenzando a traducir 3333***"+ordenCompra);
//								break;
//							}
//							if (item.getNombre().toUpperCase()
//									.equals(idDevolucionName.toUpperCase())) {
//								idDevolucion = item.getValue();
//								esDevolucion = true;
//								log.info("***Comenzando a traducir 3333***"+idDevolucion);
//								break;
//							}
//						}
//					}
//					log.info("***Comenzando a traducir 4444***");
//					boolean sinCodigo = true;
//					if (ordenCompra != null) {
//						if (ordenCompra.matches("[0-9]*")) {
//							documentoObj.setOrdenCompra(ordenCompra);
//							ceDisresponse = clienteRimBean.llamadaPorOrdenFactura(ordenCompra);
//							sinCodigo = false;
//						}
//					} 
////					else 
//					if ((mensaje.getOrdenCompra() != null)
//							&& (!mensaje.getOrdenCompra().isEmpty())) {
//						if (mensaje.getOrdenCompra().matches("[0-9]*")) {
//							documentoObj.setOrdenCompra(mensaje.getOrdenCompra());
//							ceDisresponse = clienteRimBean.llamadaPorOrdenFactura(mensaje.getOrdenCompra());
//							
//							ordenCompra = mensaje.getOrdenCompra();
//							sinCodigo = false;
//						}							
//					} else if (idDevolucion != null) {
//						if (idDevolucion.matches("[0-9]*")) {
//							documentoObj.setOrdenCompra(idDevolucion);
//							ceDisresponse = clienteRimBean.llamadaPorDevolucion(idDevolucion);
//							sinCodigo = false;
//						}else if ((mensaje.getOrdenCompra() != null)
//								&& (!mensaje.getOrdenCompra().isEmpty())) {
//							if (mensaje.getOrdenCompra().matches("[0-9]*")) {
//								documentoObj.setOrdenCompra(mensaje.getOrdenCompra());
//								ceDisresponse = clienteRimBean.llamadaPorDevolucion(mensaje.getOrdenCompra());
//								sinCodigo = false;
//							}
//						}
//						
//					}
//
//					
//
//					if (sinCodigo) {
//						respuesta.setClaveAccesoComprobante(documentoXML.getInfoTributaria().getClaveAcceso());
//						respuesta.setMensajeCliente("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
//						respuesta.setMensajeSistema("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
//						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//						documentoObj.setMensajeErrorReim("Ha ocurrido un error nose encuentra el nro orden de compra o devolucion");
//						documentoObj.setEstado(Estado.ERROR.getDescripcion());
//						log.info("***Ha ocurrido un error nose encuentra el nro orden de compra o devolucion Nota credito***");
//						documentoObj = this.servicioNotaCredito.recibirNotaCredito(documentoObj,"El comprobante no tiene orden de compra ingresar manualmente");
//						return respuesta;
//					}
//
//					if (ceDisresponse == null) {
//						respuesta.setClaveAccesoComprobante(documentoXML.getInfoTributaria().getClaveAcceso());
//						respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
//						respuesta.setMensajeSistema("Ha ocurrido un error no devolvio datos el web service");
//						respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//						documentoObj.setMensajeErrorReim("Ha ocurrido un error no devolvio datos el web service");
//						documentoObj.setEstado(Estado.ERROR.getDescripcion());
//						log.info("***El web service no encontro informacion Nota credito***");
//						documentoObj = this.servicioNotaCredito.recibirNotaCredito(documentoObj,"El web service no encontro informacion.");
//						return respuesta;
//					}
//				
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
//						
//						if(itera.getCodigoInterno().length()>=13){
//							map.put("UPC",
//									completarEspaciosEmblancosLetras(
//											ConstantesRim.UPC, itera
//													.getCodigoInterno().length(),
//											itera.getCodigoInterno()));
//						}else{
//							map.put("UPC",
//									completarEspaciosEmblancosLetras(
//											ConstantesRim.UPC, itera
//													.getCodigoAdicional().length(),
//											itera.getCodigoAdicional()));
//						}
////						map.put("UPC",
////								completarEspaciosEmblancosLetras(
////										ConstantesRim.UPC, itera
////												.getCodigoInterno().length(),
////										itera.getCodigoInterno()));
//						map.put("UPC_Supplement",
//								completarEspaciosEmblancosLetras(
//										ConstantesRim.UPC_Supplement, 0, ""));
//						map.put("Item",
//								completarEspaciosEmblancosLetras(ConstantesRim.Item, 0, ""));
//						map.put("VPN",
//								completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0, ""));
//						map.put("Original_Document_Quantity",
//								completarEspaciosEmblancosNumeros(ConstantesRim.Original_Document_Quantity, 4,
//										String.valueOf(itera.getCantidad())));
//						
//						if(itera.getDescuento()!= null && itera.getDescuento().compareTo(new BigDecimal("0.00"))>0){
//							log.info("descuanto 11"+itera.getDescuento());
//							BigDecimal pu = itera.getPrecioTotalSinImpuesto().divide(itera.getCantidad(),4, RoundingMode.CEILING);
//							log.info("descuanto 22"+pu);
//							map.put("Original_Unit_Cost",
//									completarEspaciosEmblancosNumeros(
//											ConstantesRim.Original_Unit_Cost,
//											4,
//											String.valueOf(pu.setScale(2,BigDecimal.ROUND_HALF_UP))));
//						}else{
//							map.put("Original_Unit_Cost",
//									completarEspaciosEmblancosNumeros(
//											ConstantesRim.Original_Unit_Cost,
//											4,
//											String.valueOf(itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP))));
//						}
////						map.put("Original_Unit_Cost",
////								completarEspaciosEmblancosNumeros(ConstantesRim.Original_Unit_Cost, 4, String
////										.valueOf(itera.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//						totalUnidades = totalUnidades.add(itera.getCantidad());
//						if (itera.getImpuestos().getImpuesto() != null
//								&& !itera.getImpuestos().getImpuesto()
//										.isEmpty()) {
//							for (com.gizlocorp.gnvoice.xml.notacredito.Impuesto item : itera.getImpuestos()
//									.getImpuesto()) {
//								if (item.getCodigoPorcentaje().equals("0")) {
//									map.put("Original_VAT_Code",
//											completarEspaciosEmblancosLetras(ConstantesRim.Original_VAT_Code,
//													"L".length(), "L"));
//									map.put("Original_VAT_rate",
//											completarEspaciosEmblancosNumeros(
//													ConstantesRim.Original_VAT_rate, 10, String.valueOf(item
//															.getTarifa())));
//								} else if (item.getCodigoPorcentaje().equals(
//										"2")) {
//									map.put("Original_VAT_Code",
//											completarEspaciosEmblancosLetras(ConstantesRim.Original_VAT_Code,
//													"E".length(), "E"));
//									map.put("Original_VAT_rate",
//											completarEspaciosEmblancosNumeros(
//													ConstantesRim.Original_VAT_rate, 10, String.valueOf(item
//															.getTarifa())));
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
//								}else{
//									log.info("La nota credito tiene codigo de impuesto  no contemplado en reim"+documentoXML.getInfoTributaria().getClaveAcceso());
//									respuesta.setClaveAccesoComprobante(documentoXML.getInfoTributaria().getClaveAcceso());
//									respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
//									respuesta.setMensajeSistema("La nota credito tiene codigo de impuesto no contemplado en reim");
//									respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
//									documentoObj.setEstado(Estado.ERROR.getDescripcion());
//									documentoObj.setMensajeErrorReim("La nota credito tiene codigo de impuesto no contemplado en reim");
//									documentoObj = this.servicioNotaCredito.recibirNotaCredito(documentoObj,"La factura tiene codigo de impuesto no contemplado en reim.");
//									return respuesta;
//									
//								}
//							}
////							map.put("VAT_rate",
////									completarEspaciosEmblancosNumeros(
////											ConstantesRim.VAT_rate,
////											ConstantesRim.Decimales10,
////											String.valueOf(impu
////													.getCodigoPorcentaje())));
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
//				log.info("****comienza THEAD****");
//				// THEAD
//				context.put(
//						"Document_Type",
//						completarEspaciosEmblancosLetras(ConstantesRim.Document_Type, "CRDNT".length(),
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
//						completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Document_Number,
//								vendorDocument.length(), vendorDocument));
//				context.put("Group_ID",
//						completarEspaciosEmblancosLetras(ConstantesRim.Group_ID, "".length(), ""));
//				context.put(
//						"Vendor_Type",
//						completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Type, ceDisresponse
//								.getVendor_type().length(), ceDisresponse
//								.getVendor_type()));
//
//				context.put(
//						"Vendor_ID",
//						completarEspaciosEmblancosLetras(ConstantesRim.Vendor_ID, ceDisresponse
//								.getVendor_id().length(), ceDisresponse
//								.getVendor_id()));
//
//				Date fechaEmision = sdfDate2.parse(documentoXML
//						.getInfoNotaCredito().getFechaEmision());
//				context.put(
//						"Vendor_Document_Date",
//						completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Document_Date,
//								sdfDate.format(fechaEmision).length(),
//								sdfDate.format(fechaEmision)));
//
//				context.put(
//						"Order_Number",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Order_Number, 0,
//								String.valueOf(ceDisresponse.getOrder_number())));
//				if ((ceDisresponse.getLocation() != null)
//						&& (!ceDisresponse.getLocation().isEmpty()))
//					context.put(
//							"Location",
//							completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0,
//									ceDisresponse.getLocation()));
//				else {
//					context.put("Location",
//							completarEspaciosEmblancosNumeros(ConstantesRim.Location, 0, ""));
//				}
//
//				if ((ceDisresponse.getLocation() != null)
//						&& (!ceDisresponse.getLocation().isEmpty()))
//					context.put(
//							"Location_Type",
//							completarEspaciosEmblancosLetras(ConstantesRim.Location_Type, ceDisresponse
//									.getLocation().length(), ceDisresponse
//									.getLocation_type()));
//				else {
//					context.put(
//							"Location_Type",
//							completarEspaciosEmblancosLetras(ConstantesRim.Location_Type, "".length(), ""));
//				}
//				context.put("Terms",
//						completarEspaciosEmblancosLetras(ConstantesRim.Terms, "".length(), ""));
//				context.put("Due_Date",
//						completarEspaciosEmblancosLetras(ConstantesRim.Due_Date, "".length(), ""));
//				context.put("Payment_method",
//						completarEspaciosEmblancosLetras(ConstantesRim.Payment_method, "".length(), ""));
//				context.put(
//						"Currency_code",
//						completarEspaciosEmblancosLetras(ConstantesRim.Currency_code, "USD".length(),
//								"USD"));
//
//				context.put(
//						"Exchange_rate",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Exchange_rate, 4,
//								String.valueOf("1")));
//
//				context.put(
//						"Total_Cost",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Cost, 4, String
//								.valueOf(documentoXML.getInfoNotaCredito()
//										.getTotalSinImpuestos().setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//				context.put(
//						"Total_VAT_Amount",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Total_VAT_Amount, 4,
//								String.valueOf(totalImpuesto.setScale(2, BigDecimal.ROUND_HALF_UP))));
//
//				context.put(
//						"Total_Quantity",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Quantity, 4,
//								String.valueOf(totalUnidades)));
//
//				context.put(
//						"Total_Discount",
//						completarEspaciosEmblancosNumeros(ConstantesRim.Total_Discount, 4,
//								String.valueOf("0")));
//
//				context.put("Freight_Type",
//						completarEspaciosEmblancosLetras(ConstantesRim.Freight_Type, "".length(), ""));
//				context.put("Paid_Ind", "N");
//				context.put("Multi_Location", "N");
//				context.put("Merchan_dise_Type", "N");
//				context.put("Deal_Id",
//						completarEspaciosEmblancosLetras(ConstantesRim.Deal_Id, "".length(), ""));
//				context.put("Deal_Approval_Indicator",
//						completarEspaciosEmblancosLetras(ConstantesRim.Deal_Approval_Indicator, "".length(), ""));
//				
//				if ((idDevolucion != null) && (!idDevolucion.isEmpty()))
//					context.put("RTV_indicator", "Y");
//				else {
//					context.put("RTV_indicator", "N");
//				}
//
//				context.put(
//						"Custom_Document_Reference_1",
//						completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_1, autorizacionXML
//								.getNumeroAutorizacion().length(),
//								autorizacionXML.getNumeroAutorizacion()));
//
//				String fechaAutorizacion = FechaUtil.formatearFecha(
//						autorizacionXML.getFechaAutorizacion()
//								.toGregorianCalendar().getTime(),
//						"dd/MM/yyyy HH:mm");
//
//				context.put(
//						"Custom_Document_Reference_2",
//						completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_2,
//								fechaAutorizacion.length(), fechaAutorizacion));
//				if (esDevolucion)
//					context.put(
//							"Custom_Document_Reference_3",
//							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_3, emisor
//									.getCeDisVirtual().length(), emisor
//									.getCeDisVirtual()));
//				else {
//					context.put(
//							"Custom_Document_Reference_3",
//							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_3, emisor
//									.getCeDisVirtual().length(), emisor
//									.getCeDisVirtual()));
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
//							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_4,
//									parametro.length(), parametro));
//				} else {
//					context.put(
//							"Custom_Document_Reference_4",
//							completarEspaciosEmblancosLetras(ConstantesRim.Custom_Document_Reference_4,
//									numModificadostr.length(), numModificadostr));
//				}
//
//				context.put("Cross_reference",
//						completarEspaciosEmblancosLetras(ConstantesRim.Cross_reference, "".length(), ""));
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
//				int dig5 = rng.nextInt(90000)+10000; //siempre 5 digitos
//	
////				String nombreArchivo = new StringBuilder().append("edidlinv_")
////						.append(FechaUtil.formatearFecha(now2.getTime(), "yyyyMMddHHmmssSSS").toString()).append(dig5).toString();
//						
//				String archivoFinal = DocumentUtil.createDocumentText(writer.toString().getBytes(),
//						nombreArchivo,
//						FechaUtil.formatearFecha(now2.getTime(), "yyyyMMdd")
//								.toString(), stvrepositorioRim);
//
//				String archivoCreadoProcesado = archivoFinal+ ".loaded";
//				File fileOut = new File(archivoFinal);
//				File fileLoaded = new File(archivoCreadoProcesado);
//				boolean existFileOut = fileOut!=null && fileOut.exists() && !fileOut.isDirectory();
//				boolean existFileLoaded = fileLoaded!= null && fileLoaded.exists() && !fileLoaded.isDirectory();
//				
//				if( existFileOut ||  existFileLoaded ) { 
//					documentoObj.setFechaEntReim(new Date());
//					documentoObj.setArchivoGenReim(archivoFinal);	
//				}else{
//					respuesta.setMensajes(new ArrayList<MensajeRespuesta>());
//					respuesta.setEstado(Estado.ERROR.getDescripcion());
//					MensajeRespuesta msgObj = new MensajeRespuesta();
//					msgObj.setIdentificador("-100");
//					msgObj.setMensaje("Ha ocurrido un problema: " + "No se creo el archivo .out reim");
//					respuesta.getMensajes().add(msgObj);
//					respuesta.setMensajeCliente("No se creo el archivo .out reim");
//					respuesta.setMensajeSistema("No se creo el archivo .out reim");
//					System.out.println("No se creo el archivo .out reim");
//					return respuesta;
//				}
//
//				
//
//			}
//
//			// fin agregado rim
//
//			documentoObj.setArchivo(mensaje.getComprobanteProveedor());
//			documentoObj.setNumeroAutorizacion(autorizacionXML.getNumeroAutorizacion());
//			documentoObj.setFechaAutorizacion(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime());
//			documentoObj.setEstado("AUTORIZADO");
//			documentoObj.setTareaActual(Tarea.AUT);
//			documentoObj.setTipoGeneracion(TipoGeneracion.REC);
//
//			// Organizacion emisor = emisores.get(0);
//			Parametro dirParametro = cacheBean.consultarParametro(com.gizlocorp.gnvoice.utilitario.Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
//			String dirServidor = dirParametro != null ? dirParametro.getValor() : "";
//			if ((mensaje.getComprobanteProveedorPdf() == null)
//					|| (mensaje.getComprobanteProveedorPdf().isEmpty())) {
//				try {
//					ReporteUtil reporte = new ReporteUtil();
//					String rutaArchivoAutorizacionPdf = reporte
//							.generarReporteCreditoReim(
//									"/gnvoice/recursos/reportes/notaCreditoFinal.jasper",
//									new NotaCreditoReporte(documentoXML),
//									autorizacionXML.getNumeroAutorizacion(),
//									FechaUtil.formatearFecha(autorizacionXML
//											.getFechaAutorizacion()
//											.toGregorianCalendar().getTime(),
//											"yyyy/MM/dd HH:mm"),
//									documentoXML.getInfoNotaCredito()
//											.getFechaEmision(),
//									"recibidoReim",
//									false,
//									new StringBuilder()
//											.append(dirServidor)
//											.append("/gnvoice/recursos/reportes/Blanco.png")
//											.toString());
//
//					documentoObj.setArchivoLegible(rutaArchivoAutorizacionPdf);
//				} catch (Exception ex) {
//					log.warn(ex.getMessage(), ex);
//				}
//			}
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public NotaCreditoRecibirResponse recibirNotaCredito(NotaCreditoRecibirRequest mensaje, Date fechcaTMP ) {
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

			Parametro ambParametro = cacheBean.consultarParametro(
					Constantes.AMBIENTE, emisor.getId());
			String ambiente = ambParametro.getValor();

			// creacion de documentos doc class

			if (mensaje.getMailOrigen() != null
					&& !mensaje.getMailOrigen().isEmpty()) {
				Parametro serRutaDocCalss = cacheBean.consultarParametro(
						com.gizlocorp.gnvoice.utilitario.Constantes.DOC_CLASS_RUTA, emisor.getId());
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
						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_WS, emisor.getId());
				String dirWsRim = serRimWS != null ? serRimWS.getValor() : "";

				Parametro serRimUri = cacheBean.consultarParametro(
						com.gizlocorp.gnvoice.utilitario.Constantes.SER_RIM_URI, emisor.getId());
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
						for (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional item : documentoXML
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
					for (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle itera : documentoXML.getDetalles()
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
							for (com.gizlocorp.gnvoice.xml.notacredito.Impuesto item : itera.getImpuestos()
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
					for (com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto impu : documentoXML.getInfoNotaCredito()
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
						com.gizlocorp.gnvoice.utilitario.Constantes.DIR_RIM_ESQUEMA, emisor.getId());
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
					com.gizlocorp.gnvoice.utilitario.Constantes.DIRECTORIO_SERVIDOR, emisor.getId());
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

	
	
	
	public static void main(String[] args) {
		String host = "imap.gmail.com";
		String userName = "recepcionfacturaspuntodeventa@okidoki.com.ec";
		// String userName = "recepcionfacturaspuntodeventa@fybeca.com";
		// String userName = "jose.vinueza@gizlocorp.com";

		String pass = "costos2016";
		// String pass = "Comprasdirectas007";
		// String pass = "JoseVinueza872";

		RecepcionComprobanteAllManagment attachmentReceiver = new RecepcionComprobanteAllManagment();
		// obtiene los dos ultimos dos correos
		attachmentReceiver.downloadEmailAttachments(host, userName, pass, "",200, 10);
	}
}