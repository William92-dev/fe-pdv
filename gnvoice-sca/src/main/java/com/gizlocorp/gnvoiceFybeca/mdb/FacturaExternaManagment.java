package com.gizlocorp.gnvoiceFybeca.mdb;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.SistemaExternoEnum;
import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.adm.utilitario.Constantes;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO;
import com.gizlocorp.gnvoice.dao.FacturaDAO;
import com.gizlocorp.gnvoice.dao.NotaCreditoDAO;
import com.gizlocorp.gnvoice.dao.RetencionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.Retencion;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.xml.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;
import com.gizlocorp.gnvoiceIntegracion.model.BodegaExterna;
import com.gizlocorp.gnvoiceIntegracion.model.TrxElectronica;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/facturaExternaQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "100"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false") ,
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "45000") })
public class FacturaExternaManagment implements MessageListener {
	private static Logger log = Logger.getLogger(FacturaExternaManagment.class
			.getName());


	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;
	
	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@EJB(lookup = "java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO")
	FacturaDAO servicioFactura;

	@EJB(lookup = "java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO")
	com.gizlocorp.gnvoice.dao.NotaCreditoDAO servicioNotaCredito;
	
	@EJB(lookup = "java:global/gnvoice-ejb/RetencionDAOImpl!com.gizlocorp.gnvoice.dao.RetencionDAO")	
	RetencionDAO servicioRetencion;
	
	
	@EJB(lookup ="java:global/gnvoice-ejb/ComprobanteRegeneracionDAOImpl!com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO")
	ComprobanteRegeneracionDAO comprobanteRegeneracionDAO;

	public void onMessage(Message message) {
		ObjectMessage objectMessage = null;
		if (!(message instanceof ObjectMessage)) {
			log.error("Mensaje recuperado no es instancia de ObjectMessage, se desechara "+ message);
			return;
		}

		objectMessage = (ObjectMessage) message;
		try {
			if ((objectMessage == null) || (!(objectMessage.getObject() instanceof String))) {
				log.error("El objeto seteado en el mensaje no es de tipo String, se desechara "+ message);
				return;
			}

			String mensaje = (String) objectMessage.getObject();

			if ((mensaje == null) || (mensaje.isEmpty())) {
				log.error("RUC no es valido para proceso ");
				return;
			}

			if ((mensaje != null) && (!mensaje.isEmpty()) && (mensaje.contains("&"))) {
				String[] parametros = mensaje.split("&");

				if (parametros.length < 2) {
					log.error("Parametros incompletos: " + mensaje);
					return;
				}

				SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
				Date fechaDesde = formato.parse(parametros[0]);
				Date fechaHasta = formato.parse(parametros[1]);
				String sistemaExterno = parametros[2];

				if (sistemaExterno.equals(SistemaExternoEnum.SISTEMACRPDTA.name())) {	
					log.info("***numero de lista a procesar SISTEMACRPDTA 111***"+fechaDesde+"***"+fechaHasta);
					procesaCrpdta(fechaDesde, fechaHasta, sistemaExterno);
				}
				if (sistemaExterno.equals(SistemaExternoEnum.SISTEMABODEGA.name())) {
					String movimiento = null;
					String identificacion = null;
					
					if ((parametros[3] != null) && (!parametros[3].isEmpty())) {
						movimiento = parametros[3];
					}

					procesaBodega(fechaDesde, fechaHasta, sistemaExterno, movimiento, identificacion);
				}
				if (sistemaExterno.equals(SistemaExternoEnum.SISTEMAPROVEEDORES.name())) {
					String movimiento = null;
					String identificacion = null;
					if ((parametros[3] != null) && (!parametros[3].isEmpty())) {
						movimiento = parametros[3];
					}

					procesaProveedores(fechaDesde, fechaHasta, sistemaExterno, movimiento, identificacion);
				}
			}

		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}
	
	/**
		@autor spramirezv 
	    @descipcionDelcambio cambio metodos para elminar campo xml
	 */
	
	
	
	private void procesaCrpdta(Date fechaDesde, Date fechaHasta,String sistemaExterno) {
		try {
			
			log.info("***numero de lista a procesar cprda 1***");
			
			CredencialDS credencialDS = new CredencialDS();
			credencialDS.setDatabaseId(Constantes.BASE_INTEGRA);
			credencialDS.setUsuario(Constantes.USUARIO_INTEGRA);
			credencialDS.setClave(Constantes.CLAVE_INTEGRA);
			
			Connection conn = null;
			List<TrxElectronica> listaExterna = null;
			
			try{
				conn = Conexion.obtenerConexionCRPDTA(credencialDS);	
				listaExterna = concsultarTodos(conn, fechaDesde, fechaHasta);
				log.info("***numero de lista a procesar cprda 2***"+listaExterna.size());
			
			} catch (Exception e) {
				log.info("***numero de lista a procesar cprda 3***");
				log.error(e.getMessage());
			} finally {
				try {					
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.info("***numero de lista a procesar cprda 5***");
					log.error(e.getMessage());
				}
			}
			
			
			if(listaExterna != null && !listaExterna.isEmpty()){
				log.info("***numero de lista a procesar***"  +listaExterna.size());
				
				for (TrxElectronica itera : listaExterna) {

					if(itera.getTipoComprobante().equals("01")) {
						com.gizlocorp.gnvoice.xml.factura.Factura facturaXML =    descargarComprobanteJDE(itera.getClaveAccesso()).getFactura();
						log.info("Factura recuperada de JDE a traducir ->"+facturaXML.getInfoTributaria().getClaveAcceso());
						com.gizlocorp.gnvoice.modelo.Factura facturaObj =null;
						try {

							facturaObj = this.servicioFactura.obtenerComprobante(itera.getClaveAccesso(), null, null, null);
							
						} catch (Exception ex) {
							try {
								InitialContext ic = new InitialContext();
								servicioFactura = (FacturaDAO) ic.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
								facturaObj = this.servicioFactura.obtenerComprobante(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);
								
							} catch (Exception ex2) {
								log.info("********* no actualiza soluciones", ex2);
							}
						}
						
						if (facturaObj == null) {
							facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFactura(facturaXML);
							
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							
							Date fecha = formato.parse(itera.getFechaAutorizacion());

							facturaObj.setNumeroAutorizacion(itera.getAutorizacion());
							facturaObj.setFechaAutorizacion(fecha);
							facturaObj.setEstado("AUTORIZADO");
							facturaObj.setTareaActual(Tarea.AUT);
							facturaObj.setTipoGeneracion(TipoGeneracion.EMI);
							facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
							facturaObj.setProceso(sistemaExterno);

							facturaObj = autorizaFactura(facturaObj);
							log.info("*** Proceso Encripta - Factura procesada y actulizada con id ->"+ facturaObj.getId()+"**");
							this.usuarioUsuarioLocal.crearUsuarioConsulta(facturaXML.getInfoFactura().getIdentificacionComprador());
							updateTrxElectronica(facturaObj.getClaveAcceso());
						}
							
					}
					
					String autorizacion ="";
					
					
					if(itera.getTipoComprobante().equals("04") && (autorizacion.contains("<Autorizacion>")||autorizacion.contains("<autorizacion>"))){
						
						com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXML = descargarNotaCreditoJDE(itera.getClaveAccesso()).getNotaCredito();
						com.gizlocorp.gnvoice.modelo.NotaCredito notaCreditoObj = null;
						try{
							notaCreditoObj = servicioNotaCredito.obtenerComprobante(itera.getClaveAccesso(),null, null, null);
						
						} catch (Exception ex) {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (NotaCreditoDAO) ic.lookup("java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO");
							notaCreditoObj = servicioNotaCredito.obtenerComprobante(itera.getClaveAccesso(),null, null, null);	
						}
						
						
						if(notaCreditoObj == null){
							
							notaCreditoObj = ComprobanteUtil.convertirEsquemaAEntidadNotacredito(notaCreditoXML);
							
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							
							Date fecha = formato.parse(itera.getFechaAutorizacion());

							notaCreditoObj.setNumeroAutorizacion(itera.getAutorizacion());
							notaCreditoObj.setFechaAutorizacion(fecha);
							notaCreditoObj.setEstado("AUTORIZADO");
							notaCreditoObj.setTareaActual(Tarea.AUT);
							notaCreditoObj.setTipoGeneracion(TipoGeneracion.EMI);
							notaCreditoObj.setTipoEjecucion(TipoEjecucion.SEC);
							notaCreditoObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
							notaCreditoObj.setProceso(sistemaExterno);

							notaCreditoObj = autorizaCredito(notaCreditoObj);
							log.info("*** Proceso Encripta - Factura procesada y actulizada con id ->"+ notaCreditoObj.getId()+"**");
							this.usuarioUsuarioLocal.crearUsuarioConsulta(notaCreditoXML.getInfoNotaCredito().getIdentificacionComprador());
						}
						
					}
					if(itera.getTipoComprobante().equals("07")){
						com.gizlocorp.gnvoice.modelo.Retencion retencionObj = null;
						String claveAccesoTemp = consultaClaveAcceso(itera);
						try{
							retencionObj = servicioRetencion.obtenerComprobante(claveAccesoTemp,null, null);
						} catch (Exception ex) {
							InitialContext ic = new InitialContext();
							servicioRetencion = (RetencionDAO) ic.lookup("java:global/gnvoice-ejb/RetencionDAOImpl!com.gizlocorp.gnvoice.dao.RetencionDAO");
							retencionObj = servicioRetencion.obtenerComprobante(claveAccesoTemp,null, null);	
						}
						if (retencionObj == null) {
							com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion notaCreditoXML = descargarComprobanteRetencionJDE(claveAccesoTemp);
							if (notaCreditoXML != null) {
								retencionObj = ComprobanteUtil.convertirEsquemaAEntidadComprobanteRetencion(notaCreditoXML);
								SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								Date fecha = formato.parse(itera.getFechaAutorizacion());
								retencionObj.setNumeroAutorizacion(itera.getAutorizacion());
								retencionObj.setFechaAutorizacion(fecha);
								retencionObj.setEstado("AUTORIZADO");
								retencionObj.setTareaActual(Tarea.AUT);
								retencionObj.setTipoGeneracion(TipoGeneracion.EMI);
								retencionObj.setTipoEjecucion(TipoEjecucion.SEC);
								retencionObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
								retencionObj.setProceso(sistemaExterno);
								retencionObj = autorizaRetencion(retencionObj);
								log.info("*** Proceso Encripta - Retencion procesada y actulizada con id ->"+ retencionObj.getId() + "**");
								this.usuarioUsuarioLocal.crearUsuarioConsulta(notaCreditoXML.getInfoCompRetencion().getIdentificacionSujetoRetenido());
								updateTrxElectronica(itera.getClaveAccesso());
							}
						}
					}
				}				
			}

		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}
	
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private String consultaClaveAcceso(TrxElectronica itera) {

		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId(Constantes.BASE_JDE);
		credencialDS.setUsuario(Constantes.USUARIO_JDE);
		credencialDS.setClave(Constantes.CLAVE_JDE);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		String result = itera.getClaveAccesso();
		String xmctc1 = null, xmctc2 = null, xmaa10 = null;
		String[] numeroComprobante = itera.getNumeroComprobante().split("-");
		xmctc1 = numeroComprobante[0];
		xmctc2 = numeroComprobante[1];
		xmaa10 = numeroComprobante[2];
		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
			StringBuilder sqlString = new StringBuilder();
			sqlString.append("select xmotgenkey"
					+ " from proddta.f76ecxml"
					+ " where xmdctv='"+itera.getTipoComprobante()+"'"
					+ " and xmctc1='"+xmctc1+"'"
					+ " and xmctc2 ='"+xmctc2+"'"
					+ " and xmaa10='"+xmaa10+"'"
					+ " and xmkco = '"+itera.getCompania()+"' ");
			
			ps = conn.prepareStatement(sqlString.toString());


			set = ps.executeQuery();

			while (set.next()) {
				result =set.getString("xmotgenkey").trim();
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
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return result;
	}
	
	
	/***
	 * 
	 * @autor spramirezv
	 * @descripcionDelCambio metodo para recuperar factura de jde 
	 */
	
	public com.gizlocorp.gnvoice.xml.FacturaProcesarRequest descargarComprobanteJDE(String claveAcceso){

		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId(Constantes.BASE_JDE);
		credencialDS.setUsuario(Constantes.USUARIO_JDE);
		credencialDS.setClave(Constantes.CLAVE_JDE);
		Connection conn = null;
		com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = new FacturaProcesarRequest();

		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
		    item = comprobanteRegeneracionDAO.listarJDE(conn,claveAcceso);
					if (item != null) {
							item.getFactura().setId("comprobante");
							item.getFactura().setVersion("1.1.0");
							item.getFactura().getInfoFactura().setMoneda("DOLAR");
							item.getFactura().getInfoTributaria().setAmbiente("2");
							

							if (item.getFactura().getInfoTributaria().getSecuencial().length() < 9) {
							StringBuilder secuencial = new StringBuilder();

							int secuencialTam = 9 - item.getFactura().getInfoTributaria().getSecuencial().length();
							for (int i = 0; i < secuencialTam; i++) {
								secuencial.append("0");
							}
							secuencial.append(item.getFactura().getInfoTributaria().getSecuencial());
							item.getFactura().getInfoTributaria().setSecuencial(secuencial.toString());
						} 
						
					


					}
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

		return item;

	}
	
	
	
	
	public com.gizlocorp.gnvoice.xml.NotaCreditoProcesarRequest descargarNotaCreditoJDE(String claveAcceso){

		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId(Constantes.BASE_JDE);
		credencialDS.setUsuario(Constantes.USUARIO_JDE);
		credencialDS.setClave(Constantes.CLAVE_JDE);
		Connection conn = null;
		com.gizlocorp.gnvoice.xml.NotaCreditoProcesarRequest item = new com.gizlocorp.gnvoice.xml.NotaCreditoProcesarRequest();

		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
		    item = null;//comprobanteRegeneracionDAO.listarJDE(conn,claveAcceso);
					if (item != null) {
							item.getNotaCredito().setId("comprobante");
							item.getNotaCredito().setVersion("1.1.0");
							item.getNotaCredito().getInfoNotaCredito().setMoneda("DOLAR");
							item.getNotaCredito().getInfoTributaria().setAmbiente("2");
							

							if (item.getNotaCredito().getInfoTributaria().getSecuencial().length() < 9) {
							StringBuilder secuencial = new StringBuilder();

							int secuencialTam = 9 - item.getNotaCredito().getInfoTributaria().getSecuencial().length();
							for (int i = 0; i < secuencialTam; i++) {
								secuencial.append("0");
							}
							secuencial.append(item.getNotaCredito().getInfoTributaria().getSecuencial());
							item.getNotaCredito().getInfoTributaria().setSecuencial(secuencial.toString());
						} 
						
					


					}
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

		return item;

	}
	
	
	
	private com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion descargarComprobanteRetencionJDE(String claveAcceso) {
		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId(com.gizlocorp.adm.utilitario.Constantes.BASE_JDE);
		credencialDS.setUsuario(com.gizlocorp.adm.utilitario.Constantes.USUARIO_JDE);
		credencialDS.setClave(com.gizlocorp.adm.utilitario.Constantes.CLAVE_JDE);

		Connection conn = null;
		com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion item = null;
		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
			item = new ComprobanteRetencion();
			item = comprobanteRegeneracionDAO.listarRetencionJDE(conn, claveAcceso);
		} catch (Exception e) {
			item = null;
			log.error(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			}catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return item;
	}

	
	/***
	 * 
	 * @descripcionDelCambio metodo que actualiza el estado de la integracion
	
	 */
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void updateTrxElectronica(String claveAcceso) {
		PreparedStatement ps = null;
		ResultSet set = null;
		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId(Constantes.BASE_INTEGRA);
		credencialDS.setUsuario(Constantes.USUARIO_INTEGRA);
		credencialDS.setClave(Constantes.CLAVE_INTEGRA);
		Connection conn = null;
		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
			StringBuilder sqlString = new StringBuilder();
			sqlString.append("update LOG_TRX_ELECTRONICA set estado_proceso = 'SI' where CLAVE_ACCESO =  ?");
			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, claveAcceso);;
			ps.executeUpdate();

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
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		
	}

	
	/**
	 * @finDelCambio
	 * **/

	private void procesaCrpdtaLOAD(Date fechaDesde, Date fechaHasta,String sistemaExterno) {
		try {
			
			log.info("***numero de lista a procesar cprda 1***");
			CredencialDS credencialDS = new CredencialDS();
			credencialDS.setDatabaseId("PROD");
			credencialDS.setUsuario("integraciones");
			credencialDS.setClave("1xnt3xgr4xBusD4xt0xs");
			
			Connection conn = null;
			List<TrxElectronica> listaExterna = null;
			try{
				conn = Conexion.obtenerConexionCRPDTA(credencialDS);	
				listaExterna = concsultarTodos(conn, fechaDesde, fechaHasta);
				log.info("***numero de lista a procesar cprda 2***"+listaExterna.size());
			
			} catch (Exception e) {
				log.info("***numero de lista a procesar cprda 3***");
				log.error(e.getMessage());
			} finally {
				try {					
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.info("***numero de lista a procesar cprda 5***");
					log.error(e.getMessage());
				}
			}
			
			
			if(listaExterna != null && !listaExterna.isEmpty()){
				log.info("***numero de lista a procesar***"  +listaExterna.size());
				
				for (TrxElectronica itera : listaExterna) {
					//String autorizacion = itera.getXml();
					
					String autorizacion ="";// itera.getXml();

					if(itera.getTipoComprobante().equals("01") && (autorizacion.contains("<Autorizacion>")||autorizacion.contains("<autorizacion>"))) {
						
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
							
							
							if(autorizacion.contains("<RespuestaAutorizacionComprobante>")){
								autorizacion = autorizacion.replace("<RespuestaAutorizacionComprobante>", "");
								autorizacion = autorizacion.replace("</RespuestaAutorizacionComprobante>", "");
							}
							
							autorizacion = autorizacion.trim();
						}

						Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);
						String factura = autorizacionXML.getComprobante();
						factura = factura.replace("<![CDATA[", "");
						factura = factura.replace("]]>", "");
						
						com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = getFacturaXML(factura);
						com.gizlocorp.gnvoice.modelo.Factura facturaObj =null;
						
						try {

							facturaObj = this.servicioFactura.obtenerComprobante(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);
							
						
						} catch (Exception ex) {
							try {
								InitialContext ic = new InitialContext();
								servicioFactura = (FacturaDAO) ic.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
								facturaObj = this.servicioFactura.obtenerComprobante(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);
								
							
							} catch (Exception ex2) {
								log.info("********* no actualiza soluciones", ex2);
							}
						}
						
						if (facturaObj == null) {
							facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFactura(facturaXML);
							
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							
							Date fecha = formato.parse(itera.getFechaAutorizacion());

							facturaObj.setNumeroAutorizacion(itera.getAutorizacion());
							facturaObj.setFechaAutorizacion(fecha);
							facturaObj.setEstado("AUTORIZADO");
							facturaObj.setTareaActual(Tarea.AUT);
							facturaObj.setTipoGeneracion(TipoGeneracion.EMI);
							facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
							facturaObj.setProceso(sistemaExterno);

							facturaObj = autorizaFactura(facturaObj);
							log.info("***numero de lista a procesar 6 guardado  factura***");
							this.usuarioUsuarioLocal.crearUsuarioConsulta(facturaXML.getInfoFactura().getIdentificacionComprador());
						}
					}
				
					if(itera.getTipoComprobante().equals("04") && (autorizacion.contains("<Autorizacion>")||autorizacion.contains("<autorizacion>"))){
						com.gizlocorp.gnvoice.modelo.NotaCredito notaCreditoObj = null;
						if (autorizacion != null && !autorizacion.isEmpty()) {
							String tmp = autorizacion;
							for (int i = 0; i < tmp.length(); i++) {
								if (tmp.charAt(i) == '<') {
									break;
								}
								autorizacion = autorizacion.replace(tmp.charAt(i), ' ');
							}
							
							autorizacion = autorizacion.replace("<Autorizacion>","<autorizacion>");
							autorizacion = autorizacion.replace("</Autorizacion>","</autorizacion>");
							
							
							if(autorizacion.contains("<RespuestaAutorizacionComprobante>")){
								autorizacion = autorizacion.replace("<RespuestaAutorizacionComprobante>","");
								autorizacion = autorizacion.replace("</RespuestaAutorizacionComprobante>","");
							}
							
							
							autorizacion = autorizacion.trim();
						}

						Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);
						String notacredito = autorizacionXML.getComprobante();
						notacredito = notacredito.replace("<![CDATA[", "");
						notacredito = notacredito.replace("]]>", "");
						try{
							notaCreditoObj = servicioNotaCredito.obtenerComprobante(itera.getClaveAccesso(),null, null, null);
						
						} catch (Exception ex) {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (NotaCreditoDAO) ic.lookup("java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO");
							notaCreditoObj = servicioNotaCredito.obtenerComprobante(itera.getClaveAccesso(),null, null, null);	
						}
						
						if(notaCreditoObj == null){
							com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXML = getNotaCreditoXML(notacredito);
							
							notaCreditoObj = ComprobanteUtil.convertirEsquemaAEntidadNotacredito(notaCreditoXML);
							
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							
							Date fecha = formato.parse(itera.getFechaAutorizacion());

							notaCreditoObj.setNumeroAutorizacion(itera.getAutorizacion());
							notaCreditoObj.setFechaAutorizacion(fecha);
							notaCreditoObj.setEstado("AUTORIZADO");
							notaCreditoObj.setTareaActual(Tarea.AUT);
							notaCreditoObj.setTipoGeneracion(TipoGeneracion.EMI);
							notaCreditoObj.setTipoEjecucion(TipoEjecucion.SEC);
							notaCreditoObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
							notaCreditoObj.setProceso(sistemaExterno);

							notaCreditoObj = autorizaCredito(notaCreditoObj);
							log.info("***numero de lista a procesar 6 guardado  nota credito***");
							this.usuarioUsuarioLocal.crearUsuarioConsulta(notaCreditoXML.getInfoNotaCredito().getIdentificacionComprador());
						}					
					}
					if(itera.getTipoComprobante().equals("07") && (autorizacion.contains("<Autorizacion>")||autorizacion.contains("<autorizacion>"))){
						
						com.gizlocorp.gnvoice.modelo.Retencion retencionObj = null;
						
						if (autorizacion != null && !autorizacion.isEmpty()) {
							String tmp = autorizacion;
							for (int i = 0; i < tmp.length(); i++) {
								if (tmp.charAt(i) == '<') {
									break;
								}
								autorizacion = autorizacion.replace(tmp.charAt(i), ' ');
							}
							
							autorizacion = autorizacion.replace("<Autorizacion>","<autorizacion>");
							autorizacion = autorizacion.replace("</Autorizacion>","</autorizacion>");
							
							
							if(autorizacion.contains("<RespuestaAutorizacionComprobante>")){
								autorizacion = autorizacion.replace("<RespuestaAutorizacionComprobante>","");
								autorizacion = autorizacion.replace("</RespuestaAutorizacionComprobante>","");
							}
							
							
							autorizacion = autorizacion.trim();
						}

						Autorizacion autorizacionXML = getAutorizacionXML(autorizacion);
						String notacredito = autorizacionXML.getComprobante();
						notacredito = notacredito.replace("<![CDATA[", "");
						notacredito = notacredito.replace("]]>", "");
						try{
							retencionObj = servicioRetencion.obtenerComprobante(itera.getClaveAccesso(),null, null);
						
						} catch (Exception ex) {
							InitialContext ic = new InitialContext();
							servicioRetencion = (RetencionDAO) ic.lookup("java:global/gnvoice-ejb/RetencionDAOImpl!com.gizlocorp.gnvoice.dao.RetencionDAO");
							retencionObj = servicioRetencion.obtenerComprobante(itera.getClaveAccesso(),null, null);	
						}
						
						if(retencionObj == null){
							 com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion notaCreditoXML = getRetencionXML(notacredito);
							
							 retencionObj = ComprobanteUtil.convertirEsquemaAEntidadComprobanteRetencion(notaCreditoXML);
							
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							
							Date fecha = formato.parse(itera.getFechaAutorizacion());

							retencionObj.setNumeroAutorizacion(itera.getAutorizacion());
							retencionObj.setFechaAutorizacion(fecha);
							retencionObj.setEstado("AUTORIZADO");
							retencionObj.setTareaActual(Tarea.AUT);
							retencionObj.setTipoGeneracion(TipoGeneracion.EMI);
							retencionObj.setTipoEjecucion(TipoEjecucion.SEC);
							retencionObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
							retencionObj.setProceso(sistemaExterno);
							
							retencionObj = autorizaRetencion(retencionObj);
							log.info("***numero de lista a procesar 6 guardado  retencion***");
							this.usuarioUsuarioLocal.crearUsuarioConsulta(notaCreditoXML.getInfoCompRetencion().getIdentificacionSujetoRetenido());
						}
						
					}
				}				
			}

		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}
	
	
	private void procesaBodega(Date fechaDesde, Date fechaHasta,
			String sistemaExterno, String movimiento, String identificacion) {
		log.info("***numero de lista a procesar bodega 222***");
		try {
			CredencialDS credencialDS = new CredencialDS();
			credencialDS.setDatabaseId("oficina");
			credencialDS.setUsuario("WEBLINK");
			credencialDS.setClave("weblink_2013");
			Connection conn = null;
			List<BodegaExterna> listaExterna = null;
			try{				
				conn = Conexion.obtenerConexionFybeca(credencialDS);
				listaExterna = concsultarBodegaExterna(conn, fechaDesde, fechaHasta, movimiento, identificacion,
						credencialDS);
				
				
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

			if(listaExterna != null && !listaExterna.isEmpty()){
				log.info("***numero de lista a procesaBodega***"+listaExterna.size());
				if (movimiento.equals("01")) {
					for (BodegaExterna factura : listaExterna) {						
						com.gizlocorp.gnvoice.modelo.Factura facturaObj = null;
						try {

							facturaObj = this.servicioFactura.obtenerComprobante(factura.getClaveAcceso(), null, null, null);
						} catch (Exception ex) {
							try {
								InitialContext ic = new InitialContext();
								servicioFactura = (FacturaDAO) ic
										.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
								facturaObj = this.servicioFactura.obtenerComprobante(factura.getClaveAcceso(), null, null, null);
							} catch (Exception ex2) {
								log.warn("********* no actualiza soluciones", ex2);
							}
						}

						if (facturaObj == null) {
							if (factura.getTipoDoc().equals("FIRMAELECTRONICA")) {
								String ruta = Constantes.RUTA_INTEGRACION_BODEGA_XMLFactura+ factura.getClaveAcceso() + ".xml";
								String rutaSri = Constantes.RUTA_INTEGRACION_BODEGA_XMLFactura+ factura.getClaveAcceso() + "SRI" + ".xml";
								String rutaPf = Constantes.RUTA_INTEGRACION_BODEGA_PDFFactura+ factura.getClaveAcceso() + ".pdf";

								if (new File(ruta).exists()) {
									String autorizacion = DocumentUtil.readContentFile(ruta);

									if ((factura.getNumeroAutorizacion() != null) && (!factura.getNumeroAutorizacion().isEmpty())) {
										com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = getFacturaXML(autorizacion);
										facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFactura(facturaXML);

										facturaObj.setNumeroAutorizacion(factura.getNumeroAutorizacion());

										facturaObj.setEstado("AUTORIZADO");
										facturaObj.setArchivo(rutaSri);
										facturaObj.setArchivoLegible(rutaPf);
										facturaObj.setTareaActual(Tarea.AUT);	
										facturaObj.setTipoGeneracion(TipoGeneracion.EMI);
										facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
										facturaObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
										facturaObj.setProceso(sistemaExterno);

										facturaObj = autorizaFactura(facturaObj);

										this.usuarioUsuarioLocal.crearUsuarioConsulta(facturaXML.getInfoFactura().getIdentificacionComprador());
									}
								} else {
									log.info("***no existe en ruta compartida***" + factura.getClaveAcceso());
								}
							}
						}
					}
				}

				if (movimiento.equals("03")) {
					for (BodegaExterna notaCredito : listaExterna) {
						
						
						com.gizlocorp.gnvoice.modelo.NotaCredito notaCreditoObj = null;
						try{
							notaCreditoObj = servicioNotaCredito
									.obtenerComprobante(notaCredito.getClaveAcceso(),
											null, null, null);
						
						} catch (Exception ex) {
							InitialContext ic = new InitialContext();
							servicioNotaCredito = (NotaCreditoDAO) ic
									.lookup("java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO");
							notaCreditoObj = servicioNotaCredito
									.obtenerComprobante(notaCredito.getClaveAcceso(),
											null, null, null);
						}
						if ((notaCreditoObj == null)
								&& (notaCredito.getTipoDoc()
										.equals("FIRMAELECTRONICA"))) {
							String ruta = Constantes.RUTA_INTEGRACION_BODEGA_XMLNotaCredito+ notaCredito.getClaveAcceso() + ".xml";
							String rutaSri = Constantes.RUTA_INTEGRACION_BODEGA_XMLNotaCredito+ notaCredito.getClaveAcceso() + "SRI" + ".xml";
							String rutaPf = Constantes.RUTA_INTEGRACION_BODEGA_PDFNotaCredito+ notaCredito.getClaveAcceso() + ".pdf";
							
							if (new File(ruta).exists()) {
								String autorizacion = DocumentUtil
										.readContentFile(ruta);

								if ((notaCredito.getNumeroAutorizacion() != null)
										&& (!notaCredito.getNumeroAutorizacion()
												.isEmpty())) {
									com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXML = getNotaCreditoXML(autorizacion);
									notaCreditoObj = ComprobanteUtil
											.convertirEsquemaAEntidadNotacredito(notaCreditoXML);

									notaCreditoObj
											.setNumeroAutorizacion(notaCredito
													.getNumeroAutorizacion());

									notaCreditoObj.setEstado("AUTORIZADO");
									notaCreditoObj.setArchivo(rutaSri);
									notaCreditoObj.setArchivoLegible(rutaPf);
									notaCreditoObj.setTareaActual(Tarea.AUT);
									notaCreditoObj.setTipoGeneracion(TipoGeneracion.EMI);
									notaCreditoObj.setTipoEjecucion(TipoEjecucion.SEC);
									notaCreditoObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
									notaCreditoObj.setProceso(sistemaExterno);

									notaCreditoObj = autorizaCredito(notaCreditoObj);

									this.usuarioUsuarioLocal
											.crearUsuarioConsulta(notaCreditoXML
													.getInfoNotaCredito()
													.getIdentificacionComprador());

								}
							} else {
								log.info("***nota de credito bodega archivo no existe***"
										+ notaCredito.getClaveAcceso());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}

	private void procesaProveedores(Date fechaDesde, Date fechaHasta,String sistemaExterno, String movimiento, String identificacion) {
		try {
			CredencialDS credencialDS = new CredencialDS();
			credencialDS.setDatabaseId("oficina");
			credencialDS.setUsuario("WEBLINK");
			credencialDS.setClave("weblink_2013");

			CredencialDS credencialDS_sana = new CredencialDS();
			credencialDS_sana.setDatabaseId("sana");
			credencialDS_sana.setUsuario("WEBLINK");
			credencialDS_sana.setClave("weblink_2013");

			CredencialDS credencialDS_okimaster = new CredencialDS();
			credencialDS_okimaster.setDatabaseId("okimaster");
			credencialDS_okimaster.setUsuario("WEBLINK");
			credencialDS_okimaster.setClave("weblink_2013");
			
			List<BodegaExterna> listaExternaAll = new ArrayList<BodegaExterna>();
			
			Connection conn = null;
			List<BodegaExterna> listaExternaOficiana = null;
			try{				
				conn = Conexion.obtenerConexionFybeca(credencialDS);
				listaExternaOficiana = concsultarProveedoresExterna(conn, fechaDesde, fechaHasta,movimiento, identificacion, credencialDS);
				listaExternaAll.addAll(listaExternaOficiana);
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
			
			Connection connSana = null;
			List<BodegaExterna> listaExternaSana = null;
			try{				
				connSana = Conexion.obtenerConexionFybeca(credencialDS_sana);
				listaExternaSana = concsultarProveedoresExterna(connSana, fechaDesde,fechaHasta, movimiento, identificacion,credencialDS);
				listaExternaAll.addAll(listaExternaSana);
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

			Connection connOkimaster = null;
			List<BodegaExterna> listaExternaOkimaster = null;
			try{
				connOkimaster = Conexion.obtenerConexionFybeca(credencialDS_okimaster);
				listaExternaOkimaster = concsultarProveedoresExterna(connOkimaster, fechaDesde,
								fechaHasta, movimiento, identificacion,
								credencialDS);
				listaExternaAll.addAll(listaExternaOkimaster);
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

			if(listaExternaAll!=null && !listaExternaAll.isEmpty()){
				log.info("***numero de lista a procesaProveedores***"+listaExternaAll.size());
				if (movimiento.equals("01")) {
					for (BodegaExterna factura : listaExternaAll) {
						
						com.gizlocorp.gnvoice.modelo.Factura facturaObj = this.servicioFactura.obtenerComprobante(factura.getClaveAcceso(), null, null, null);

						if ((facturaObj == null) && (factura.getTipoDoc().equals("FIRMAELECTRONICA"))) {
							String ruta = Constantes.RUTA_INTEGRACION_BODEGA_XMLFactura+ factura.getClaveAcceso() + ".xml";
							String rutaSri = Constantes.RUTA_INTEGRACION_BODEGA_XMLFactura+ factura.getClaveAcceso() + "SRI" + ".xml";
							String rutaPf = Constantes.RUTA_INTEGRACION_BODEGA_PDFFactura+ factura.getClaveAcceso() + ".pdf";
							
							if (new File(ruta).exists()) {
								String autorizacion = DocumentUtil.readContentFile(ruta);

								if ((factura.getNumeroAutorizacion() != null) && (!factura.getNumeroAutorizacion().isEmpty())) {
									com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = getFacturaXML(autorizacion);

									facturaObj = ComprobanteUtil.convertirEsquemaAEntidadFactura(facturaXML);

									facturaObj.setNumeroAutorizacion(factura.getNumeroAutorizacion());

									facturaObj.setEstado("AUTORIZADO");
									facturaObj.setArchivo(rutaSri);
									facturaObj.setArchivoLegible(rutaPf);
									facturaObj.setTareaActual(Tarea.AUT);
									facturaObj.setTipoGeneracion(TipoGeneracion.EMI);
									facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
									facturaObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
									facturaObj.setProceso(sistemaExterno);

									facturaObj = autorizaFactura(facturaObj);

									this.usuarioUsuarioLocal
											.crearUsuarioConsulta(facturaXML
													.getInfoFactura()
													.getIdentificacionComprador());

									log.info("***guardado correctamente Proveedores factura***");
								}
							} else {
								log.info("***Factura proveedores archivo no existe***"
										+ factura.getClaveAcceso());
							}
						}

					}

				}
				if (movimiento.equals("03")) {
					for (BodegaExterna notaCredito : listaExternaAll) {
						com.gizlocorp.gnvoice.modelo.NotaCredito notaCreditoObj = this.servicioNotaCredito
								.obtenerComprobante(notaCredito.getClaveAcceso(),
										null, null, null);

						if ((notaCreditoObj == null)
								&& (notaCredito.getTipoDoc()
										.equals("FIRMAELECTRONICA"))) {
							
							String ruta = Constantes.RUTA_INTEGRACION_BODEGA_XMLNotaCredito+ notaCredito.getClaveAcceso() + ".xml";
							String rutaSri = Constantes.RUTA_INTEGRACION_BODEGA_XMLNotaCredito+ notaCredito.getClaveAcceso() + "SRI" + ".xml";
							String rutaPf = Constantes.RUTA_INTEGRACION_BODEGA_PDFNotaCredito+ notaCredito.getClaveAcceso() + ".pdf";
							
							if (new File(ruta).exists()) {
								String autorizacion = DocumentUtil
										.readContentFile(ruta);

								if ((notaCredito.getNumeroAutorizacion() != null)
										&& (!notaCredito.getNumeroAutorizacion()
												.isEmpty())) {
									com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXML = getNotaCreditoXML(autorizacion);
									notaCreditoObj = ComprobanteUtil
											.convertirEsquemaAEntidadNotacredito(notaCreditoXML);
									notaCreditoObj
											.setNumeroAutorizacion(notaCredito
													.getNumeroAutorizacion());

									notaCreditoObj.setEstado("AUTORIZADO");
									notaCreditoObj.setArchivo(rutaSri);
									notaCreditoObj.setArchivoLegible(rutaPf);
									notaCreditoObj.setTareaActual(Tarea.AUT);
									notaCreditoObj
											.setTipoGeneracion(TipoGeneracion.EMI);
									notaCreditoObj.setTipoEjecucion(TipoEjecucion.SEC);
									notaCreditoObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
									notaCreditoObj.setProceso(sistemaExterno);

									notaCreditoObj = autorizaCredito(notaCreditoObj);

									this.usuarioUsuarioLocal
											.crearUsuarioConsulta(notaCreditoXML
													.getInfoNotaCredito()
													.getIdentificacionComprador());

									log.info("***guardado correctamente Proveedores nota credito***");
								}
							} else {
								log.info("***nota de credito proveedores archivo no existe***"
										+ notaCredito.getClaveAcceso());
							}
						}
					}
				}
			}			
		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}

	
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private List<TrxElectronica> concsultarTodos(Connection conn, Date fechaDesde, Date fechaHasta) {
		//Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		List<TrxElectronica> lista = new ArrayList<TrxElectronica>();

		try {
			
			String and = "";
			try {
				and = DocumentUtil.readContentFile(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+"/gnvoice/recursos/integracionWhere.txt");
			} catch (Exception e) {}
					
			StringBuilder sqlString = new StringBuilder();
			sqlString.append("SELECT * "
						  + " FROM LOG_TRX_ELECTRONICA "
						  + " where 1 = 0 " );/*tipo_comprobante = '07'"
						  + " and fecha > to_date('01/09/2017', 'dd/MM/yyyy')"
						  + " and fecha <= to_date('30/09/2017', 'dd/MM/yyyy') "
						  + " and estado_proceso is null");// estado_proceso is null ");
			
			/*sqlString.append("SELECT * FROM LOG_TRX_ELECTRONICA where 1 = 1 ");
			
			if(fechaDesde != null){
				sqlString.append("and fecha >= ? ");
			}
			
			if(fechaHasta != null){
				sqlString.append("and fecha <= ? ");
			}*/
			sqlString.append(and);
			log.info("*** Proceso Encripta - Query ->"+ sqlString.toString()+"**");
			ps = conn.prepareStatement(sqlString.toString());

			/*if(fechaDesde != null){
				ps.setDate(1, (new java.sql.Date(fechaDesde.getTime())));
			}
			if(fechaHasta != null){
				ps.setDate(2, (new java.sql.Date(fechaHasta.getTime())));
			}*/
			
			

			set = ps.executeQuery();

			while (set.next()) {
				TrxElectronica objeto = new TrxElectronica();
				objeto.setId(set.getLong("ID"));
				objeto.setAutorizacion(set.getString("AUTORIZACION"));
				objeto.setFechaAutorizacion(set.getString("FECHA_AUTORIZACION"));
				objeto.setTipoComprobante(set.getString("TIPO_COMPROBANTE"));
				objeto.setClaveAccesso(set.getString("CLAVE_ACCESO"));
				objeto.setCompania(set.getString("COMPANIA"));
				objeto.setNumeroComprobante(set.getString("NUMERO_COMPROBANTE"));
				
				/*Clob clob = set.getClob("XML");
				String aux;
				StringBuffer strOut = new StringBuffer();
				BufferedReader br = new BufferedReader(clob.getCharacterStream());
				while ((aux = br.readLine()) != null) {
					strOut.append(aux);
				}
				objeto.setXml(strOut.toString());*/
				lista.add(objeto);
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
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lista;
	}
	
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private List<BodegaExterna> concsultarBodegaExterna(Connection conn,
			Date fechaDesde, Date fechaHasta, String movimiento,
			String identificacion, CredencialDS credencialDS) {
		log.info("***Ejecutando concsultarBodegaExterna dao***"+fechaDesde+fechaHasta+movimiento);

		PreparedStatement ps = null;
		ResultSet set = null;

		PreparedStatement ps2 = null;
		ResultSet set2 = null;

		List<String> claveAcceso = new ArrayList<String>();
		List<String> claveAcceso2 = new ArrayList<String>();
		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append(" select distinct ts.clave_acceso ");
			sqlString.append(" from ds_cliente_bodegas c, ds_transacciones t, ds_transaccion_datossri ts, ");
			sqlString.append(" ad_clasificacion_movimientos cm, ad_tipos_movimientos tm ");
			sqlString.append(" where  t.cliente = c.codigo_cba and   t.fecha >= ? and   t.fecha <= ? and ");
			sqlString.append(" t.empresa_cliente <> t.empresa and t.bodega = ts.bodega and t.documento_bodega = ts.documento_bodega ");
			sqlString.append(" and t.tsn_bodega is null and t.tipo_movimiento = cm.tipo_movimiento and t.clase_movimiento = cm.codigo and ");
			sqlString.append(" tm.codigo = cm.tipo_movimiento  and  t.tipo_movimiento  = ?  and ts.clave_acceso is not null ");
			
			ps = conn.prepareStatement(sqlString.toString());

			ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ps.setString(3, movimiento);

			set = ps.executeQuery();

			while (set.next()) {
				claveAcceso.add(set.getString("clave_acceso"));
			}

			StringBuilder sqlString2 = new StringBuilder();

			sqlString2.append(" select distinct t.clave_acceso ");
			sqlString2.append(" from in_facturas_x_cobrar t,in_detalle_facturas_x_cobrar ts, ");
			sqlString2.append(" ad_clasificacion_movimientos cm, ad_tipos_movimientos tm  ");
			sqlString2.append(" where  t.fecha >= ?  ");
			sqlString2.append(" and t.fecha <= ? and t.documento = ts.documento ");
			sqlString2.append(" and t.tipo_movimiento = cm.tipo_movimiento and  ");
			sqlString2.append(" t.clasificacion_movimiento = cm.codigo ");
			sqlString2.append(" and tm.codigo = cm.tipo_movimiento  and  t.tipo_documento  = ? and t.clave_acceso is not null ");

			ps2 = conn.prepareStatement(sqlString2.toString());

			ps2.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			ps2.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ps2.setString(3, movimiento);

			set2 = ps2.executeQuery();

			while (set2.next()) {
				claveAcceso2.add(set2.getString("clave_acceso"));
			}

			facturas = concsultarBodegaExternaIn(conn, fechaDesde, fechaHasta,movimiento, identificacion, claveAcceso, claveAcceso2);
		} catch (Exception e) {
			log.info(new StringBuilder().append("***Exception***").append(e).toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***").append(e).toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***").append(e).toString());
				log.error(e.getMessage(), e);
			}
		}

		log.info(new StringBuilder()
				.append("***DAO devuelve facturas de bodega externa:***")
				.append(facturas.size()).toString());

		return facturas;
	}

	
	private List<BodegaExterna> concsultarBodegaExternaIn(Connection conn,
			java.util.Date fechaDesde, java.util.Date fechaHasta,
			String movimiento, String identificacion, List<String> claveAcceso,
			List<String> claveAcceso2) {
		PreparedStatement ps = null;
		ResultSet set = null;

		PreparedStatement ps2 = null;
		ResultSet set2 = null;

		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		try {
			if ((claveAcceso != null) && (!claveAcceso.isEmpty())) {
				for (String itera : claveAcceso) {
					StringBuilder sqlString = new StringBuilder();

					sqlString.append(" select rownum codigo, t.fecha, t.valor_gravado, t.valor_no_gravado, t.descuento, t.iva, t.numero_factura, ts.clave_acceso, ");
					sqlString.append(" decode(ts.APROBADO_FIRMAE,'S','FIRMAELECTRONICA','AUTOIMPRESOR') TIPODOC, ");
					sqlString.append(" decode(ts.APROBADO_FIRMAE,'S',ts.aut_firmae ,t.autorizacion_sri) AUTORIZACION_SRI, ");
					sqlString.append(" tm.descripcion||' <> '||cm.descripcion movimiento, t.documento_bodega, t.bodega, t.tipo_movimiento, c.codigo_cba ");
					sqlString.append(" from ds_cliente_bodegas c, ds_transacciones t, ds_transaccion_datossri ts, ad_clasificacion_movimientos cm, ad_tipos_movimientos tm ");
					sqlString.append(" where  t.cliente = c.codigo_cba and   t.fecha >= ? and   t.fecha <= ? and ");
					sqlString.append(" t.empresa_cliente <> t.empresa and t.bodega = ts.bodega and t.documento_bodega = ts.documento_bodega  ");
					sqlString.append(" and t.tsn_bodega is null and t.tipo_movimiento = cm.tipo_movimiento and t.clase_movimiento = cm.codigo and ");
					sqlString.append(" tm.codigo = cm.tipo_movimiento  and  t.tipo_movimiento  = ?  and ts.clave_acceso = ? ");

					ps = conn.prepareStatement(sqlString.toString());
					ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
					ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
					ps.setString(3, movimiento);
					ps.setString(4, itera);

					set = ps.executeQuery();

					while (set.next()) {
						BodegaExterna bodegaExterna = new BodegaExterna();
						bodegaExterna.setCodigo(set.getString("codigo"));
						bodegaExterna.setFecha(set.getDate("fecha"));
						bodegaExterna.setNumeroFactura(set.getString("numero_factura"));
						bodegaExterna.setClaveAcceso(set.getString("clave_acceso"));
						bodegaExterna.setTipoDoc(set.getString("TIPODOC"));
						bodegaExterna.setNumeroAutorizacion(set.getString("AUTORIZACION_SRI"));
						facturas.add(bodegaExterna);
					}
				}

			}

			if ((claveAcceso2 != null) && (!claveAcceso2.isEmpty())) {
				for (String itera : claveAcceso2) {
					StringBuilder sqlString = new StringBuilder();
					log.info("--------> "+itera);

					sqlString.append(" select rownum codigo, t.fecha, t.venta, 0, t.descuento,   ");
					sqlString.append(" t.iva, decode(t.APROBADO_FIRMA_E,'S',t.comprobante_firma_e,t.numero_sri) numero_factura, t.clave_acceso,   ");
					sqlString.append(" decode(t.APROBADO_FIRMA_E,'S','FIRMAELECTRONICA','AUTOIMPRESOR') TIPODOC,   ");
					sqlString.append(" decode(t.APROBADO_FIRMA_E,'S',t.autorizacion_firma_e ,t.numero_sri) AUTORIZACION_SRI,   ");
					sqlString.append(" tm.descripcion||' <> '||cm.descripcion movimiento, t.documento, null  ,t.tipo_documento, t.cliente ");
					sqlString.append(" from in_facturas_x_cobrar t,in_detalle_facturas_x_cobrar ts, ad_clasificacion_movimientos cm, ad_tipos_movimientos tm   ");
					sqlString.append(" where  t.fecha >= ?   ");
					sqlString.append(" and t.fecha <= ? and t.documento = ts.documento  ");
					sqlString.append(" and t.tipo_movimiento = cm.tipo_movimiento and   ");
					sqlString.append(" t.clasificacion_movimiento = cm.codigo  ");
					sqlString.append(" and tm.codigo = cm.tipo_movimiento  and  t.tipo_documento  = ? and t.clave_acceso = ? ");

					ps2 = conn.prepareStatement(sqlString.toString());

					ps2.setDate(1, new java.sql.Date(fechaDesde.getTime()));
					ps2.setDate(2, new java.sql.Date(fechaHasta.getTime()));
					ps2.setString(3, movimiento);
					ps2.setString(4, itera);

					set2 = ps2.executeQuery();

					while (set2.next()) {
						BodegaExterna bodegaExterna = new BodegaExterna();
						bodegaExterna.setCodigo(set2.getString("codigo"));
						bodegaExterna.setFecha(set2.getDate("fecha"));
						bodegaExterna.setNumeroFactura(set2.getString("numero_factura"));
						bodegaExterna.setClaveAcceso(set2.getString("clave_acceso"));
						bodegaExterna.setTipoDoc(set2.getString("TIPODOC"));
						bodegaExterna.setNumeroAutorizacion(set2.getString("AUTORIZACION_SRI"));
						facturas.add(bodegaExterna);
					}
				}
			}

		} catch (Exception e) {
			log.info(new StringBuilder().append("***Exception***").append(e)
					.toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***")
						.append(e).toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***")
						.append(e).toString());
				log.error(e.getMessage(), e);
			}

		}

		return facturas;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<BodegaExterna> concsultarProveedoresExterna(Connection conn,
			java.util.Date fechaDesde, java.util.Date fechaHasta,
			String movimiento, String identificacion, CredencialDS credencialDS) {
		PreparedStatement ps = null;
		ResultSet set = null;
		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		List<String> claveAcceso = new ArrayList<String>();
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append("select distinct t.clave_acceso ");
			sqlString.append("from in_facturas_x_cobrar t, in_detalle_facturas_x_cobrar ts, ad_clasificacion_movimientos cm, ad_tipos_movimientos tm    ");
			sqlString.append("where  t.fecha >= ? and   t.fecha <= ? ");
			sqlString.append("and t.documento = ts.documento ");
			sqlString.append("and t.tipo_movimiento = cm.tipo_movimiento  ");
			sqlString.append("and t.clasificacion_movimiento = cm.codigo   ");
			sqlString.append("and tm.codigo = cm.tipo_movimiento   ");
			sqlString.append("and t.tipo_documento  = ? ");
			sqlString.append("and t.clave_acceso is not null ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ps.setString(3, movimiento);

			set = ps.executeQuery();

			while (set.next()) {
				claveAcceso.add(set.getString("clave_acceso"));
			}

			facturas = concsultarProveedoresExternaIn(conn, fechaDesde, fechaHasta, movimiento, claveAcceso);
		} catch (Exception e) {
			log.info(new StringBuilder().append("***Exception proveedores 1***").append(e).toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception proveedores 2***").append(e).toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception proveedores 3***").append(e).toString());
				log.error(e.getMessage(), e);
			}

		}

		return facturas;
	}

	
	public List<BodegaExterna> concsultarProveedoresExternaIn(Connection conn,java.util.Date fechaDesde, java.util.Date fechaHasta,
			String movimiento, List<String> claveAcceso) {
		PreparedStatement ps = null;
		ResultSet set = null;
		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		try {
			if ((claveAcceso != null) && (!claveAcceso.isEmpty())) {
				for (String itera : claveAcceso) {
					StringBuilder sqlString = new StringBuilder();

					sqlString.append("select rownum codigo, t.fecha, t.venta as valor_gravado,0, t.descuento as descuento,  t.iva, ");
					sqlString.append("decode(t.APROBADO_FIRMA_E,'S',t.comprobante_firma_e,t.numero_sri) as numero_factura, t.clave_acceso,  ");
					sqlString.append("decode(t.APROBADO_FIRMA_E,'S','FIRMAELECTRONICA', 'AUTOIMPRESOR') TIPODOC,  ");
					sqlString.append("decode(t.APROBADO_FIRMA_E,'S',t.autorizacion_firma_e ,t.numero_sri) AUTORIZACION_SRI,  ");
					sqlString.append("tm.descripcion||' <> '|| cm.descripcion movimiento, t.documento as documento_bodega, null  ");
					sqlString.append("from in_facturas_x_cobrar t,in_detalle_facturas_x_cobrar ts,   ad_clasificacion_movimientos cm, ad_tipos_movimientos tm    ");
					sqlString.append("where  t.fecha >= ? and   t.fecha <= ? ");
					sqlString.append("and t.documento = ts.documento ");
					sqlString.append("and t.tipo_movimiento = cm.tipo_movimiento  ");
					sqlString.append("and t.clasificacion_movimiento = cm.codigo   ");
					sqlString.append("and tm.codigo = cm.tipo_movimiento   ");
					sqlString.append("and t.tipo_documento  = ? ");
					sqlString.append("and t.clave_acceso = ?");

					ps = conn.prepareStatement(sqlString.toString());
					ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
					ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
					ps.setString(3, movimiento);
					ps.setString(4, itera);

					set = ps.executeQuery();

					while (set.next()) {
						BodegaExterna bodegaExterna = new BodegaExterna();
						bodegaExterna.setCodigo(set.getString("codigo"));
						bodegaExterna.setFecha(set.getDate("fecha"));
						bodegaExterna.setNumeroFactura(set.getString("numero_factura"));
						bodegaExterna.setClaveAcceso(set.getString("clave_acceso"));
						bodegaExterna.setTipoDoc(set.getString("TIPODOC"));
						bodegaExterna.setNumeroAutorizacion(set.getString("AUTORIZACION_SRI"));
						facturas.add(bodegaExterna);
					}
				}

			}

		} catch (Exception e) {
			log.info(new StringBuilder()
					.append("***Exception proveedores 1***").append(e)
					.toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder()
						.append("***Exception proveedores 2***").append(e)
						.toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder()
						.append("***Exception proveedores 3***").append(e)
						.toString());
				log.error(e.getMessage(), e);
			}
		}

		log.info(new StringBuilder().append("DAO devuelve facturas de provedores externa: movimiento").append(movimiento).append(facturas.size()).toString());

		return facturas;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Factura autorizaFactura(Factura comprobante) {

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
				servicioFactura = (FacturaDAO) ic.lookup("java:global/gnvoice-ejb/FacturaDAOImpl!com.gizlocorp.gnvoice.dao.FacturaDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = servicioFactura.update(comprobante);

				} else {
					comprobante = servicioFactura.persist(comprobante);

				}
			}catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}

		return comprobante;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public com.gizlocorp.gnvoice.modelo.NotaCredito autorizaCredito(
			com.gizlocorp.gnvoice.modelo.NotaCredito comprobante) {

		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = servicioNotaCredito.update(comprobante);

			} else {
				comprobante = servicioNotaCredito.persist(comprobante);

			}
		} catch (Exception ex) {
			try {
				InitialContext ic = new InitialContext();
				servicioNotaCredito = (NotaCreditoDAO) ic
						.lookup("java:global/gnvoice-ejb/NotaCreditoDAOImpl!com.gizlocorp.gnvoice.dao.NotaCreditoDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = servicioNotaCredito.update(comprobante);

				} else {
					comprobante = servicioNotaCredito.persist(comprobante);

				}
			} catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}

		return comprobante;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public com.gizlocorp.gnvoice.modelo.Retencion autorizaRetencion(Retencion comprobante) {
		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = servicioRetencion.update(comprobante);

			}else {
				comprobante = servicioRetencion.persist(comprobante);

			}

		} catch (Exception ex) {
			try {
				InitialContext ic = new InitialContext();
				servicioRetencion = (RetencionDAO) ic
						.lookup("java:global/gnvoice-ejb/RetencionDAOImpl!com.gizlocorp.gnvoice.dao.RetencionDAO");
				comprobante.setTareaActual(Tarea.AUT);
				comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
				comprobante.setTipoGeneracion(TipoGeneracion.EMI);

				if (comprobante.getId() != null) {
					comprobante = servicioRetencion.update(comprobante);

				} else {
					comprobante = servicioRetencion.persist(comprobante);

				}
			} catch (Exception ex2) {
				log.warn("no actualiza soluciones", ex2);
			}
		}
		return comprobante;
	}
	
	
	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.factura.Factura) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.factura.Factura.class);
	}

	
	private com.gizlocorp.gnvoice.xml.notacredito.NotaCredito getNotaCreditoXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.class);
	}
	
	private com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion getRetencionXML(
			String comprobante)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion) converter
				.convertirAObjeto(comprobante,
						com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.class);
	}
	
	private Autorizacion getAutorizacionXML(String comprobanteXML)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (Autorizacion) converter.convertirAObjeto(comprobanteXML,
				Autorizacion.class);
	}

//	private Autorizacion getAutorizacionXML(String comprobanteXML)
//			throws ConverterException {
//		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
//		return (Autorizacion) converter.convertirAObjeto(comprobanteXML,
//				Autorizacion.class);
//	}
}