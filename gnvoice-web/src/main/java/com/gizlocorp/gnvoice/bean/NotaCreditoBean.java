package com.gizlocorp.gnvoice.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import oracle.sql.BLOB;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.util.PDFMergerUtility;

import com.gizlocorp.adm.enumeracion.SistemaExternoEnum;
import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.RimEnum;
import com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.json.model.CredencialDS;
import com.gizlocorp.gnvoice.modelo.NotaCredito;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;
import com.gizlocorp.gnvoice.reporte.NotaCreditoReporte;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito;
import com.gizlocorp.gnvoice.utilitario.Conexion;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCreditoPortType;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCreditoRecibirRequest;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCreditoRecibirResponse;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCredito_Service;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("notaCreditoBean")
public class NotaCreditoBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(NotaCreditoBean.class
			.getName());

	private String numeroComprobante;

	private String codigoExterno;

	private Date fechaDesde;

	private Date fechaHasta;

	private List<NotaCredito> notaCreditos;
	
	private List<NotaCreditoRecepcion> notaCreditoRecepcion;
	
	private NotaCredito notaCredito;


	private String estado = null;

	@Inject
	private SessionBean sessionBean;

	private String rucEmisor;

	private String rucComprador;

	private String identificadorUsuario;

	private String numeroRelacionado;

	private String agencia;

	private String emisor;

	private String claveContingencia;

	private String tipoEmision;

	private String tipoAmbiente;

	private String correoProveedor;

	private TipoEjecucion tipoEjecucion;
	
	private String ordenCompra;

	private static final String REPORTE_CLAVES_PATH = "/reportes/listaNotaCredito.jasper";
	private static final String REPORT_XLS_PATH = "listaNotaCredito.xls";
	private static final String REPORT_PDF_PATH = "listaNotaCredito.pdf";
	
	
	private static final String REPORTE_V3_CLAVES_PATH = "/reportes/listaNotaCreditoV3.jasper";
	private static final String REPORT_V2_XLS_PATH = "listaNotaCreditoV3.xls";
	

	private static final String REPORTE_CLAVES_PATH_REIM = "/reportes/listaNotaCreditoRein.jasper";
	
	private Converter converter;

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCredito")
	ServicioNotaCredito servicioNotaCredito;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB(lookup ="java:global/gnvoice-ejb/ComprobanteRegeneracionDAOImpl!com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO")
	ComprobanteRegeneracionDAO comprobanteRegeneracionDAO;
	
	public void descargarComprobantePDF() {
		try {
			
			
			String archivo = notaCredito.getArchivoLegible();

			Autorizacion autorizacionXML = null;
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;

			String archivoXML = notaCredito.getArchivo();
			
			String autorizacion ="";
			
			String fecha="";
			
			String logo=""; 
			
			String notaCreditoPDF="";
			
			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCreditoXml=null;
			
			if (notaCredito.getProceso() != null
					&& !notaCredito.getProceso().isEmpty()
					&& notaCredito.getProceso().equals(
							SistemaExternoEnum.SISTEMACRPDTA.name())) {
				CredencialDS credencialDS = new CredencialDS();
				credencialDS.setDatabaseId("PROD");
				credencialDS.setUsuario("integraciones");
				credencialDS.setClave("1xnt3xgr4xBusD4xt0xs");
				Connection conn = null;
				try {
					conn = Conexion.obtenerConexionFybeca(credencialDS);
					autorizacion = concsultarPorClaveAccesoXml(conn,
							notaCredito.getClaveAcceso());
					fecha = FechaUtil.formatearFecha(notaCredito.getFechaAutorizacion(), FechaUtil.DATE_FORMAT);
					logo ="/home/jboss/app/gnvoice/recursos/reportes/Blanco.png";
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
			}else{
				if ((archivo == null || archivo.isEmpty() || !new File(archivo).exists())
				 && (archivoXML == null || archivoXML.isEmpty() || !new File(archivoXML).exists())) {
					
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
					String wsdlLocationAutorizacionOffline = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

					if (notaCredito.getEstado().equals("AUTORIZADO")) {
						
						if(notaCredito.getNumeroAutorizacion().length()>37){
							autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionOffline,notaCredito.getClaveAcceso());
							log.info("offline"+autorizacionOfflineXML);	
						}else{
							autorizacionXML  = autorizarComprobante(wsdlLocationAutorizacion,notaCredito.getClaveAcceso()); 
							log.info("online"+autorizacionXML);	
						}
						
					
						
						//if (autorizacionXML.getComprobante() != null && !autorizacionXML.getComprobante().isEmpty())
						if ((autorizacionXML !=null && autorizacionXML.getComprobante() != null && !autorizacionXML.getComprobante().isEmpty())|| (autorizacionOfflineXML!=null && autorizacionOfflineXML.getComprobante() != null && !autorizacionOfflineXML.getComprobante().isEmpty())){
							
							
							
							
							if(notaCredito.getNumeroAutorizacion().length()>37){
								notaCreditoPDF = autorizacionOfflineXML.getComprobante();
								notaCreditoPDF = notaCreditoPDF.replace("<![CDATA[", "");
								notaCreditoPDF = notaCreditoPDF.replace("]]>", "");
								log.info(notaCreditoPDF.toString());
								notaCreditoXml = getNotaCreditoXML(notaCreditoPDF);
								
							}else{
								notaCreditoPDF = autorizacionXML.getComprobante();
								notaCreditoPDF = notaCreditoPDF.replace("<![CDATA[", "");
								notaCreditoPDF = notaCreditoPDF.replace("]]>", "");
								log.info(notaCreditoPDF.toString());
								notaCreditoXml = getNotaCreditoXML(notaCreditoPDF);
							}
							List<Organizacion> emisores = servicioOrganizacionLocal
									.listarOrganizaciones(null, null, notaCreditoXml
											.getInfoTributaria().getRuc(), null);
			
							Parametro dirParametro = servicioParametro
									.consultarParametro(Constantes.DIRECTORIO_SERVIDOR,
											emisores.get(0).getId());
							String dirServidor = dirParametro != null ? dirParametro.getValor() : "";
									
							if(fecha.isEmpty()){
								fecha =FechaUtil.formatearFecha(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime(), FechaUtil.patronFechaTiempo24);
							}
							
							if(logo.isEmpty()){				
								//logo = emisores.get(0).getLogoEmpresa();
								
								if(notaCredito !=null && notaCredito.getIsOferton() != null && !notaCredito.getIsOferton().isEmpty() && "S".equals(notaCredito.getIsOferton()))
									logo = emisores.get(0).getLogoEmpresaOpcional();
								else
									logo = emisores.get(0).getLogoEmpresa();
								
							}
			
							archivo = ReporteUtil.generarReporte(
									Constantes.REP_NOTA_CREDITO,
									new NotaCreditoReporte(notaCreditoXml),
									autorizacionXML.getNumeroAutorizacion(), fecha,
									notaCreditoXml.getInfoNotaCredito()
											.getFechaEmision(), "autorizado", false,
									logo, dirServidor);
							
							
						}else{
							
							log.info("***generando xml desde la base de datos 111***");
							String lisfarmacia="";
							if(notaCredito.getAgencia() != null && !notaCredito.getAgencia().isEmpty()){							
								
								CredencialDS credencialDSOfi = new CredencialDS();
								credencialDSOfi.setDatabaseId("oficina");
								credencialDSOfi.setUsuario("WEBLINK");
								credencialDSOfi.setClave("weblink_2013");
								Connection connoficina = null;
								try{				
									connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
									lisfarmacia = comprobanteRegeneracionDAO.listaSidGeneral(connoficina, Long.parseLong(notaCredito.getAgencia())); 
									log.info("***farmacia oficina 111***"+lisfarmacia);
								} catch (Exception e) {
									log.error(e.getMessage()+" "+e.getLocalizedMessage());
								} finally {
									try {
										if (connoficina != null)
											connoficina.close();
									} catch (Exception e) {
										log.error(e.getMessage()+" "+e.getLocalizedMessage());
									}
								}
								
								String[] parametros = lisfarmacia.split("&");
								Long farmacia2 = Long.parseLong(parametros[0]);
								String sid = parametros[1];								
								
								//extrayendo de la base
								CredencialDS credencialDS = new CredencialDS();
								credencialDS.setDatabaseId(sid);
								credencialDS.setUsuario("WEBLINK");
								credencialDS.setClave("weblink_2013");
								
								Connection conn = null;
								try {
									conn = Conexion.obtenerConexionFybeca(credencialDS);
									log.info("***Conexion obtenida Nota Credito 111***");
									Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"NOTA DE CRÉDITO");

									if (tipo == null) {
										tipo = 1L;
									}
									
									log.info("***generando xml desde la base de datos 222***"+tipo);

									com.gizlocorp.gnvoice.xml.NotaCreditoProcesarRequest item = comprobanteRegeneracionDAO.
											listarNotaCredito(conn, Long.parseLong(notaCredito.getClaveInterna()), farmacia2, tipo);
									
									List<Organizacion> emisores = servicioOrganizacionLocal
											.listarOrganizaciones(null, null, item.getNotaCredito()
													.getInfoTributaria().getRuc(), null);
									
									Parametro dirParametro = servicioParametro
											.consultarParametro(Constantes.DIRECTORIO_SERVIDOR,
													emisores.get(0).getId());
									String dirServidor = dirParametro != null ? dirParametro
											.getValor() : "";
									
									Organizacion emisor=emisores.get(0);
									
									Parametro ambParametro = servicioParametro.consultarParametro(
											Constantes.AMBIENTE, emisor.getId());
									Parametro monParamtro = servicioParametro.consultarParametro(
											Constantes.MONEDA, emisor.getId());
									Parametro codFacParametro = servicioParametro.consultarParametro(
											Constantes.COD_NOTA_CREDITO, emisor.getId());
									//String ambiente = ambParametro.getValor();
									
									// Setea nodo principal
									item.getNotaCredito().setId("comprobante");
									item.getNotaCredito().setVersion(Constantes.VERSION_1);
									
									item.getNotaCredito().getInfoTributaria().setClaveAcceso(notaCredito.getClaveAcceso());

									item.getNotaCredito().getInfoNotaCredito()
											.setMoneda(monParamtro.getValor());
									item.getNotaCredito().getInfoTributaria()
											.setAmbiente(ambParametro.getValor());
									item.getNotaCredito().getInfoTributaria()
											.setCodDoc(codFacParametro.getValor());

									if (item.getNotaCredito().getInfoTributaria().getSecuencial()
											.length() < 9) {
										StringBuilder secuencial = new StringBuilder();

										int secuencialTam = 9 - item.getNotaCredito()
												.getInfoTributaria().getSecuencial().length();
										for (int i = 0; i < secuencialTam; i++) {
											secuencial.append("0");
										}
										secuencial.append(item.getNotaCredito().getInfoTributaria()
												.getSecuencial());
										item.getNotaCredito().getInfoTributaria()
												.setSecuencial(secuencial.toString());
									}

									item.getNotaCredito().getInfoTributaria()
											.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

									String dirMatriz = emisor.getDireccion();

									item.getNotaCredito()
											.getInfoTributaria()
											.setDirMatriz(
													dirMatriz != null ? StringEscapeUtils
															.escapeXml(dirMatriz.trim())
															.replaceAll("[\n]", "")
															.replaceAll("[\b]", "") : null);

									if (item.getNotaCredito().getInfoTributaria().getEstab() == null
											|| item.getNotaCredito().getInfoTributaria().getEstab()
													.isEmpty()) {
										item.getNotaCredito().getInfoTributaria()
												.setEstab(emisor.getEstablecimiento());

									}
									
									if (item.getNotaCredito().getInfoTributaria().getNombreComercial() == null
											|| item.getNotaCredito().getInfoTributaria()
													.getNombreComercial().isEmpty()) {
										
										item.getNotaCredito()
										.getInfoTributaria()
										.setNombreComercial(
												emisor.getNombreComercial() != null ? StringEscapeUtils
														.escapeXml(
																emisor.getNombreComercial().trim())
														.replaceAll("[\n]", "")
														.replaceAll("[\b]", "")
														: null);
										
									}
									

									if (item.getNotaCredito().getInfoTributaria().getPtoEmi() == null
											|| item.getNotaCredito().getInfoTributaria().getPtoEmi()
													.isEmpty()) {
										item.getNotaCredito().getInfoTributaria()
												.setPtoEmi(emisor.getPuntoEmision());
									}
									
									item.getNotaCredito()
											.getInfoTributaria()
											.setRazonSocial(
													emisor.getNombreComercial() != null ? StringEscapeUtils
															.escapeXml(emisor.getNombreComercial()) : null);

									item.getNotaCredito()
											.getInfoNotaCredito()
											.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
									if (item.getNotaCredito().getInfoNotaCredito()
											.getDirEstablecimiento() == null
											|| item.getNotaCredito().getInfoNotaCredito()
													.getDirEstablecimiento().isEmpty()) {
										item.getNotaCredito()
												.getInfoNotaCredito()
												.setDirEstablecimiento(
														emisor.getDirEstablecimiento() != null ? StringEscapeUtils
																.escapeXml(emisor
																		.getDirEstablecimiento())
																: null);
									}
									
									item.getNotaCredito()
											.getInfoNotaCredito()
											.setObligadoContabilidad(
													emisor.getEsObligadoContabilidad().getDescripcion()
															.toUpperCase());
									
									if(logo.isEmpty()){				
										logo = emisores.get(0).getLogoEmpresa();
									}
									
									archivo = ReporteUtil.generarReporte(
											Constantes.REP_NOTA_CREDITO,
											new NotaCreditoReporte(item.getNotaCredito()),
											notaCredito.getNumeroAutorizacion(), fecha,
											item.getNotaCredito().getInfoNotaCredito()
													.getFechaEmision(), "autorizado", false,
											logo, dirServidor);
									
									
									
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
							}
							//---------
							log.info("***generando xml desde la base de datos 444***"); 
						}
					
					}
				
				}else{
					autorizacion =DocumentUtil.readContentFile(archivoXML);
					
					this.converter = ConverterFactory
							.getConverter(XML_CONTENT_TYPE);
					autorizacionXML = this.converter
							.convertirAObjeto(autorizacion, Autorizacion.class);
	
					String notaCredito = autorizacionXML.getComprobante();
					notaCredito = notaCredito.replace("<![CDATA[", "");
					notaCredito = notaCredito.replace("]]>", "");
	
					notaCreditoXml =getNotaCreditoXML(notaCredito);
	
					List<Organizacion> emisores = servicioOrganizacionLocal
							.listarOrganizaciones(null, null, notaCreditoXml
									.getInfoTributaria().getRuc(), null);
	
					Parametro dirParametro = servicioParametro
							.consultarParametro(Constantes.DIRECTORIO_SERVIDOR,
									emisores.get(0).getId());
					String dirServidor = dirParametro != null ? dirParametro
							.getValor() : "";
							
					if(fecha.isEmpty()){
						fecha =FechaUtil.formatearFecha(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime(), FechaUtil.patronFechaTiempo24);
					}
					
					if(logo.isEmpty()){				
						//logo = emisores.get(0).getLogoEmpresa();
						
						if(this.notaCredito !=null && this.notaCredito.getIsOferton() != null && !this.notaCredito.getIsOferton().isEmpty() && "S".equals(this.notaCredito.getIsOferton()))
							logo = emisores.get(0).getLogoEmpresaOpcional();
						else
							logo = emisores.get(0).getLogoEmpresa();
					}
	
					archivo = ReporteUtil.generarReporte(
							Constantes.REP_NOTA_CREDITO,
							new NotaCreditoReporte(notaCreditoXml),
							autorizacionXML.getNumeroAutorizacion(), fecha,
							notaCreditoXml.getInfoNotaCredito()
									.getFechaEmision(), "autorizado", false,
							logo, dirServidor);
				}
			}
				
				
			if (archivo != null && new File(archivo).exists()) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition",
						"attachment; filename=notaCredito.pdf");

				File file = new File(archivo);
				// Open the file and output streams
				FileInputStream in = new FileInputStream(file);

				// Copy the contents of the file to the output stream
				byte[] buf = new byte[1024];

				int count = 0;
				while ((count = in.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}

				in.close();
				out.close();
				FacesContext.getCurrentInstance().responseComplete();

			}

			
		} catch (Exception e) {
			log.error("Error ride unificado: " + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al descargar el documento");
		}
	}

	public void descargarComprobanteXML() {
		try {
			
			
			
			if (notaCredito.getProceso() != null
					&& !notaCredito.getProceso().isEmpty()
					&& notaCredito.getProceso().equals(
							SistemaExternoEnum.SISTEMACRPDTA.name())) {
				CredencialDS credencialDS = new CredencialDS();
				credencialDS.setDatabaseId("PROD");
				credencialDS.setUsuario("integraciones");
				credencialDS.setClave("1xnt3xgr4xBusD4xt0xs");
				Connection conn = null;
				String facturaxml = null;
				try {
					conn = Conexion.obtenerConexionFybeca(credencialDS);
					facturaxml = concsultarPorClaveAccesoXml(conn,
							notaCredito.getClaveAcceso());
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
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/xml");
				response.addHeader("Content-Disposition",
						"attachment; filename=factura.xml");

				// File file = new File(archivo);
				// Open the file and output streams
				InputStream in = new ByteArrayInputStream(facturaxml.getBytes());
				// FileInputStream in = new FileInputStream(file);

				// Copy the contents of the file to the output stream
				byte[] buf = new byte[1024];

				int count = 0;
				while ((count = in.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}

				in.close();
				out.close();
				FacesContext.getCurrentInstance().responseComplete();

			} else {
			    String archivo = notaCredito.getArchivo();
			    
			    if (archivo == null || archivo.isEmpty()
						|| !new File(archivo).exists()) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

					if (notaCredito.getEstado().equals("AUTORIZADO")) {
						// metodo a usar para regenerar
						Autorizacion respAutorizacion = autorizarComprobante(
								wsdlLocationAutorizacion,
								notaCredito.getClaveAcceso());
						if (respAutorizacion.getComprobante() != null
								&& !respAutorizacion.getComprobante().isEmpty()) {
							String comprobanteXML = respAutorizacion
									.getComprobante();
							comprobanteXML = comprobanteXML.replace(
									"<![CDATA[", "");
							comprobanteXML = comprobanteXML.replace("]]>", "");

							com.gizlocorp.gnvoice.xml.notacredito.NotaCredito factAutorizadaXML = getNotaCreditoXML(comprobanteXML);

							archivo = DocumentUtil.createDocument(
									getNotaCreditoXML(respAutorizacion),
									factAutorizadaXML.getInfoNotaCredito()
											.getFechaEmision(), notaCredito
											.getClaveAcceso(),
									TipoComprobante.NOTA_CREDITO.getDescripcion(),
									"autorizado");							
							
						} else {
							
							log.info("***generando xml desde la base de datos 111***");
							String lisfarmacia="";
							if(notaCredito.getAgencia() != null && !notaCredito.getAgencia().isEmpty()){							
								
								CredencialDS credencialDSOfi = new CredencialDS();
								credencialDSOfi.setDatabaseId("oficina");
								credencialDSOfi.setUsuario("WEBLINK");
								credencialDSOfi.setClave("weblink_2013");
								Connection connoficina = null;
								try{				
									connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
									lisfarmacia = comprobanteRegeneracionDAO.listaSidGeneral(connoficina, Long.parseLong(notaCredito.getAgencia())); 
									log.info("***farmacia oficina 111***"+lisfarmacia);
								} catch (Exception e) {
									log.error(e.getMessage()+" "+e.getLocalizedMessage());
								} finally {
									try {
										if (connoficina != null)
											connoficina.close();
									} catch (Exception e) {
										log.error(e.getMessage()+" "+e.getLocalizedMessage());
									}
								}
								
								String[] parametros = lisfarmacia.split("&");
								Long farmacia2 = Long.parseLong(parametros[0]);
								String sid = parametros[1];								
								
								//extrayendo de la base
								CredencialDS credencialDS = new CredencialDS();
								credencialDS.setDatabaseId(sid);
								credencialDS.setUsuario("WEBLINK");
								credencialDS.setClave("weblink_2013");
								
								Connection conn = null;
								try {
									conn = Conexion.obtenerConexionFybeca(credencialDS);
									log.info("***Conexion obtenida Nota Credito 111***");
									Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"NOTA DE CRÉDITO");

									if (tipo == null) {
										tipo = 1L;
									}
									
									log.info("***generando xml desde la base de datos 222***"+tipo);

									com.gizlocorp.gnvoice.xml.NotaCreditoProcesarRequest item = comprobanteRegeneracionDAO.
											listarNotaCredito(conn, Long.parseLong(notaCredito.getClaveInterna()), farmacia2, tipo);
									
									List<Organizacion> emisores = servicioOrganizacionLocal
											.listarOrganizaciones(null, null, item.getNotaCredito()
													.getInfoTributaria().getRuc(), null);
									
									Organizacion emisor=emisores.get(0);
									
									Parametro ambParametro = servicioParametro.consultarParametro(
											Constantes.AMBIENTE, emisor.getId());
									Parametro monParamtro = servicioParametro.consultarParametro(
											Constantes.MONEDA, emisor.getId());
									Parametro codFacParametro = servicioParametro.consultarParametro(
											Constantes.COD_NOTA_CREDITO, emisor.getId());
									//String ambiente = ambParametro.getValor();
									
									// Setea nodo principal
									item.getNotaCredito().setId("comprobante");
									// mensaje.getNotaCredito().setVersion(Constantes.VERSION);
									item.getNotaCredito().setVersion(Constantes.VERSION_1);

									item.getNotaCredito().getInfoNotaCredito()
											.setMoneda(monParamtro.getValor());
									item.getNotaCredito().getInfoTributaria()
											.setAmbiente(ambParametro.getValor());
									item.getNotaCredito().getInfoTributaria()
											.setCodDoc(codFacParametro.getValor());

									if (item.getNotaCredito().getInfoTributaria().getSecuencial()
											.length() < 9) {
										StringBuilder secuencial = new StringBuilder();

										int secuencialTam = 9 - item.getNotaCredito()
												.getInfoTributaria().getSecuencial().length();
										for (int i = 0; i < secuencialTam; i++) {
											secuencial.append("0");
										}
										secuencial.append(item.getNotaCredito().getInfoTributaria()
												.getSecuencial());
										item.getNotaCredito().getInfoTributaria()
												.setSecuencial(secuencial.toString());
									}

									item.getNotaCredito().getInfoTributaria()
											.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

									String dirMatriz = emisor.getDireccion();

									item.getNotaCredito()
											.getInfoTributaria()
											.setDirMatriz(
													dirMatriz != null ? StringEscapeUtils
															.escapeXml(dirMatriz.trim())
															.replaceAll("[\n]", "")
															.replaceAll("[\b]", "") : null);

									if (item.getNotaCredito().getInfoTributaria().getEstab() == null
											|| item.getNotaCredito().getInfoTributaria().getEstab()
													.isEmpty()) {
										item.getNotaCredito().getInfoTributaria()
												.setEstab(emisor.getEstablecimiento());

									}
									
									if (item.getNotaCredito().getInfoTributaria().getNombreComercial() == null
											|| item.getNotaCredito().getInfoTributaria()
													.getNombreComercial().isEmpty()) {
										
										item.getNotaCredito()
										.getInfoTributaria()
										.setNombreComercial(
												emisor.getNombreComercial() != null ? StringEscapeUtils
														.escapeXml(
																emisor.getNombreComercial().trim())
														.replaceAll("[\n]", "")
														.replaceAll("[\b]", "")
														: null);
										
									}
									

									if (item.getNotaCredito().getInfoTributaria().getPtoEmi() == null
											|| item.getNotaCredito().getInfoTributaria().getPtoEmi()
													.isEmpty()) {
										item.getNotaCredito().getInfoTributaria()
												.setPtoEmi(emisor.getPuntoEmision());
									}
									item.getNotaCredito()
											.getInfoTributaria()
											.setRazonSocial(
													emisor.getNombreComercial() != null ? StringEscapeUtils
															.escapeXml(emisor.getNombreComercial()) : null);

									item.getNotaCredito()
											.getInfoNotaCredito()
											.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
									if (item.getNotaCredito().getInfoNotaCredito()
											.getDirEstablecimiento() == null
											|| item.getNotaCredito().getInfoNotaCredito()
													.getDirEstablecimiento().isEmpty()) {
										item.getNotaCredito()
												.getInfoNotaCredito()
												.setDirEstablecimiento(
														emisor.getDirEstablecimiento() != null ? StringEscapeUtils
																.escapeXml(emisor
																		.getDirEstablecimiento())
																: null);
									}
									item.getNotaCredito()
											.getInfoNotaCredito()
											.setObligadoContabilidad(
													emisor.getEsObligadoContabilidad().getDescripcion()
															.toUpperCase());
									
									
									if (item != null) {
										archivo = DocumentUtil
												.createDocument(
														getNotaCreditoXML(item),
														item.getNotaCredito().getInfoNotaCredito()
																.getFechaEmision(),
																notaCredito.getClaveAcceso(), TipoComprobante.NOTA_CREDITO
																.getDescripcion(), "autorizado");
										
										log.info("***generando xml Nota Credito 333***"+archivo);
										notaCredito.setArchivo(archivo);
										servicioNotaCredito.autoriza(notaCredito);
										
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
							}
							//---------
							log.info("***generando xml desde la base de datos 444***"); 
							
						}

					}
				}
	
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();
	
				response.setContentType("application/xml");
				response.addHeader("Content-Disposition",
						"attachment; filename=notaCredito.xml");
	
				File file = new File(archivo);
				// Open the file and output streams
				FileInputStream in = new FileInputStream(file);
	
				// Copy the contents of the file to the output stream
				byte[] buf = new byte[1024];
	
				int count = 0;
				while ((count = in.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}
	
				in.close();
				out.close();
				FacesContext.getCurrentInstance().responseComplete();
			}
		} catch (Exception e) {
			log.error("Error ride unificado: " + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al descargar el documento");
		}
	}

	public void buscarNotaCreditoNotaCreditoRecepcion() {
		try {
			notaCreditoRecepcion = servicioNotaCredito.consultarCommprobantesRecepcion(
					numeroComprobante, fechaDesde, fechaHasta, rucComprador,
					codigoExterno, identificadorUsuario, numeroRelacionado);
		} catch (Exception e) {
			log.debug("Ha ocurrido un error al listar NotaCreditos", e);
			errorMessage("Ha ocurrido un error al listar NotaCreditos");
		}
	}
	
	
	public void buscarNotaCredito() {
		try {
			notaCreditos = servicioNotaCredito.consultarCommprobantes(
					numeroComprobante, fechaDesde, fechaHasta, rucComprador,
					codigoExterno, identificadorUsuario, numeroRelacionado);
		} catch (Exception e) {
			log.debug("Ha ocurrido un error al listar NotaCreditos", e);
			errorMessage("Ha ocurrido un error al listar NotaCreditos");
		}
	}

	public void buscarNotaCreditoRecibidos() {
		try {
			notaCreditos = servicioNotaCredito.consultarCommprobantesRecibidos(
					numeroComprobante, fechaDesde, fechaHasta, rucEmisor);
		} catch (Exception e) {
			log.debug("Ha ocurrido un error al listar NotaCreditos", e);
			errorMessage("Ha ocurrido un error al listar NotaCreditos");
		}
	}

	public void rideUnificado() {
		try {
			if (notaCreditos != null && !notaCreditos.isEmpty()) {
				PDFMergerUtility ut = new PDFMergerUtility();
				boolean existeDocumento = false;
				for (NotaCredito factura : notaCreditos) {
					if (factura.getArchivoLegible() != null
							&& !factura.getArchivoLegible().isEmpty()) {
						ut.addSource(factura.getArchivoLegible());
						existeDocumento = true;

					}
				}

				if (existeDocumento) {
					File directory = new File(
							sessionBean.getDirectorioServidor()
									+ "/gnvoice/recursos/reportes/comprobantes/rideunificado/");
					if (!directory.exists()) {
						if (!directory.mkdirs()) {
							throw new FileNotFoundException();
						}
					}

					String rideUnificado = sessionBean.getDirectorioServidor()
							+ "/gnvoice/recursos/reportes/comprobantes/rideunificado/notaCredito"
							+ Calendar.getInstance().getTimeInMillis() + ".pdf";
					ut.setDestinationFileName(rideUnificado);
					ut.mergeDocuments();

					FacesContext context = FacesContext.getCurrentInstance();
					HttpServletResponse response = (HttpServletResponse) context
							.getExternalContext().getResponse();
					OutputStream out = response.getOutputStream();

					response.setContentType("application/pdf");
					response.addHeader("Content-Disposition",
							"attachment; filename=" + rideUnificado);

					File file = new File(rideUnificado);
					// Open the file and output streams
					FileInputStream in = new FileInputStream(file);

					// Copy the contents of the file to the output stream
					byte[] buf = new byte[1024];

					int count = 0;
					while ((count = in.read(buf)) >= 0) {
						out.write(buf, 0, count);
					}

					in.close();
					out.close();
					FacesContext.getCurrentInstance().responseComplete();
				} else {
					infoMessage("No existen documentos relacionados");
				}

			}
		} catch (Exception e) {
			log.error("Error ride unificado: " + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al imprimir documentos");
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private String concsultarPorClaveAccesoXml(Connection conn,
			String claveAcceso) {
		// Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		String respuesta = null;

		try {
			// conn = ConexionIntegracion.getConnection();
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("SELECT * FROM LOG_TRX_ELECTRONICA where CLAVE_ACCESO = ? ");

			ps = conn.prepareStatement(sqlString.toString());

			ps.setString(1, claveAcceso);

			set = ps.executeQuery();

			while (set.next()) {
				Clob clob = set.getClob("XML");
				String aux;
				StringBuffer strOut = new StringBuffer();
				BufferedReader br = new BufferedReader(
						clob.getCharacterStream());
				while ((aux = br.readLine()) != null) {
					strOut.append(aux);
				}
				respuesta = strOut.toString();
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

		return respuesta;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private BLOB concsultarPorClaveAccesoPdf(Connection conn, String claveAcceso) {
		// Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		BLOB respuesta = null;

		try {
			// conn = ConexionIntegracion.getConnection();
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("SELECT * FROM LOG_TRX_ELECTRONICA where CLAVE_ACCESO = ? ");

			ps = conn.prepareStatement(sqlString.toString());

			ps.setString(1, claveAcceso);

			set = ps.executeQuery();

			while (set.next()) {
				respuesta = (BLOB) set.getBlob("RIDE");
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

		return respuesta;
	}

	public void buscarNotaCreditoSeguimiento() {
		try {
			log.info("***NotaCreditos***" );
			
			notaCreditos = servicioNotaCredito
					.consultarComprobantesSeguimiento(estado,
							numeroComprobante, fechaDesde, fechaHasta,
							rucEmisor, codigoExterno, rucComprador,
							identificadorUsuario, numeroRelacionado, agencia,
							claveContingencia, tipoEmision, tipoAmbiente,
							correoProveedor, tipoEjecucion);

			log.debug("NotaCreditos" + notaCreditos);
		} catch (Exception e) {
			log.debug("Ha ocurrido un error al listar NotaCreditos", e);
			errorMessage("Ha ocurrido un error al listar NotaCreditos");
		}
	}
	
	public void reprocesarNotaCredito() {
		NotaCredito_Service serviceNotaCredito = new NotaCredito_Service();
		NotaCreditoPortType portNotaCredito = serviceNotaCredito.getNotaCreditoPort();

		NotaCreditoRecibirRequest notaCreditoRecibirRequest = new NotaCreditoRecibirRequest();
		if (notaCredito.getArchivo() != null) {
			notaCreditoRecibirRequest.setComprobanteProveedor(notaCredito.getArchivo());
		}
		if (notaCredito.getArchivoLegible() != null) {
			notaCreditoRecibirRequest.setComprobanteProveedorPDF(notaCredito.getArchivoLegible());
		}
		notaCreditoRecibirRequest.setProceso(RimEnum.REIM.name());
		if (ordenCompra != null && !ordenCompra.isEmpty()) {
			notaCreditoRecibirRequest.setOrdenCompra(ordenCompra);
		}
		
		notaCreditoRecibirRequest.setMailOrigen(notaCredito.getCorreoNotificacion());

		NotaCreditoRecibirResponse respuestaNotaCredito = portNotaCredito.recibir(notaCreditoRecibirRequest);

		if (respuestaNotaCredito.getEstado().equalsIgnoreCase("COMPLETADO")) {
			infoMessage("Proceso Realizado con exito");
		} else {
			infoMessage("Error al procesar la factura");
		}

		System.out.println(respuestaNotaCredito.getMensajeSistema() + " " + respuestaNotaCredito.getMensajeCliente());
	}

	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			if (estado.getDescripcion().equals("PENDIENTE")
					|| estado.getDescripcion().equals("DEVUELTA")
					|| estado.getDescripcion().equals("ERROR")
					|| estado.getDescripcion().equals("RECIBIDA")
					|| estado.getDescripcion().equals("AUTORIZADO")
					|| estado.getDescripcion().equals("RECHAZADO")) {
				items.add(new SelectItem(estado, estado.getDescripcion()));
			}
		}
		return items;
	}

	@PostConstruct
	public void postContruct() {
		try {
			rucEmisor = sessionBean.getRucOrganizacion() != null ? sessionBean
					.getRucOrganizacion() : null;
			notaCreditos = new ArrayList<NotaCredito>();

			rucComprador = null;
			identificadorUsuario = null;
			List<UsuarioRol> usuarioRoles = sessionBean.getUsuario()
					.getUsuariosRoles();
			if (usuarioRoles != null && !usuarioRoles.isEmpty()) {
				for (UsuarioRol usuRol : usuarioRoles) {
					if (usuRol.getRol().getCodigo().equals("CONSU")) {
						rucComprador = sessionBean.getUsuario().getPersona()
								.getIdentificacion();
						identificadorUsuario = sessionBean.getUsuario()
								.getPersona().getIdentificacion();
					}
				}
			}

			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarXLS() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (notaCreditos != null && !notaCreditos.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Nota de Credito");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH, map,
						REPORT_XLS_PATH, notaCreditos);
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarXLSReim() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (notaCreditoRecepcion != null && !notaCreditoRecepcion.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Nota de Credito");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				
				
				ReportUtil.exportarReporteXLS(REPORTE_V3_CLAVES_PATH, map,
						REPORT_V2_XLS_PATH, notaCreditoRecepcion);
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarPDFReim() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (notaCreditos != null && !notaCreditos.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Nota de Credito");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH_REIM, map,
						REPORT_PDF_PATH, notaCreditos);
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarPDF() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (notaCreditos != null && !notaCreditos.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Nota de Credito");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH, map,
						REPORT_PDF_PATH, notaCreditos);
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

	public String getNumeroComprobante() {
		return numeroComprobante;
	}

	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public List<NotaCredito> getNotaCreditos() {
		return notaCreditos;
	}

	public void setNotaCreditos(List<NotaCredito> notaCreditos) {
		this.notaCreditos = notaCreditos;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public NotaCredito getNotaCredito() {
		return notaCredito;
	}

	public void setNotaCredito(NotaCredito notaCredito) {
		this.notaCredito = notaCredito;
	}

	public String getCodigoExterno() {
		return codigoExterno;
	}

	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	public String getIdentificadorUsuario() {
		return identificadorUsuario;
	}

	public void setIdentificadorUsuario(String identificadorUsuario) {
		this.identificadorUsuario = identificadorUsuario;
	}

	public String getRucComprador() {
		return rucComprador;
	}

	public void setRucComprador(String rucComprador) {
		this.rucComprador = rucComprador;
	}

	public String getNumeroRelacionado() {
		return numeroRelacionado;
	}

	public void setNumeroRelacionado(String numeroRelacionado) {
		this.numeroRelacionado = numeroRelacionado;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getEmisor() {
		return emisor;
	}

	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getRucEmisor() {
		return rucEmisor;
	}

	public void setRucEmisor(String rucEmisor) {
		this.rucEmisor = rucEmisor;
	}

	public String getClaveContingencia() {
		return claveContingencia;
	}

	public void setClaveContingencia(String claveContingencia) {
		this.claveContingencia = claveContingencia;
	}

	public String getTipoEmision() {
		return tipoEmision;
	}

	public void setTipoEmision(String tipoEmision) {
		this.tipoEmision = tipoEmision;
	}

	public String getTipoAmbiente() {
		return tipoAmbiente;
	}

	public void setTipoAmbiente(String tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public String getCorreoProveedor() {
		return correoProveedor;
	}

	public void setCorreoProveedor(String correoProveedor) {
		this.correoProveedor = correoProveedor;
	}

	public TipoEjecucion getTipoEjecucion() {
		return tipoEjecucion;
	}

	public void setTipoEjecucion(TipoEjecucion tipoEjecucion) {
		this.tipoEjecucion = tipoEjecucion;
	}

	public List<SelectItem> getAmbientes() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoAmbienteEnum enumerador : TipoAmbienteEnum.values()) {
			items.add(new SelectItem(enumerador.getCode(), enumerador
					.toString()));
		}
		return items;
	}

	public List<SelectItem> getTiposEmision() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoEmisionEnum enumerador : TipoEmisionEnum.values()) {
			items.add(new SelectItem(enumerador.getCodigo(), enumerador
					.getDescripcion()));
		}
		return items;
	}

	public List<SelectItem> getTiposEjecucion() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoEjecucion enumerador : TipoEjecucion.values()) {
			items.add(new SelectItem(enumerador, enumerador.getDescripcion()
					.toUpperCase()));
		}
		return items;
	}

	public String getOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(String ordenCompra) {
		this.ordenCompra = ordenCompra;
	}
	
	
	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,
			String claveAcceso) {
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

		// log.debug("CLAVE DE ACCESO: " + claveAcceso);
		int timeout = 7000;

		try {
			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
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
						msj.setInformacionAdicional(msj.getInformacionAdicional() != null ? 
								StringEscapeUtils.escapeXml(msj.getInformacionAdicional())
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
			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
			respuesta.setEstado("ERROR");
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
			respuesta.setEstado("ERROR");

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
	
	private com.gizlocorp.gnvoice.xml.notacredito.NotaCredito getNotaCreditoXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.class);
	}
	
	private String getNotaCreditoXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	public List<NotaCreditoRecepcion> getNotaCreditoRecepcion() {
		return notaCreditoRecepcion;
	}

	public void setNotaCreditoRecepcion(List<NotaCreditoRecepcion> notaCreditoRecepcion) {
		this.notaCreditoRecepcion = notaCreditoRecepcion;
	}
	
	
	 
}