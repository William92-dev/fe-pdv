package com.gizlocorp.gnvoice.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.util.PDFMergerUtility;

import com.gizlocorp.adm.enumeracion.SistemaExternoEnum;
import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;
import com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.MailDelivery;
import com.gizlocorp.adm.utilitario.MailMessage;
import com.gizlocorp.adm.utilitario.RimEnum;
import com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.json.model.CredencialDS;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Conexion;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaPortType;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaRecibirRequest;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaRecibirResponse;
import com.gizlocorp.gnvoice.wsclient.factura.Factura_Service;
import com.gizlocorp.gnvoice.xml.FacturaProcesarRequest;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import oracle.sql.BLOB;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("facturaBeanClientesABF")
public class FacturaBeanClientesABF extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(FacturaBeanClientesABF.class.getName());

	private String numeroComprobante;

	private String claveContingencia;

	private String tipoEmision;

	private String tipoAmbiente;

	private String razonSocialComprador;
	
	private String correoProveedor;
	

	private TipoEjecucion tipoEjecucion;

	private String codigoExterno;

	private Date fechaDesde;

	private Date fechaHasta;
	
	private List<Factura> facturas;
	
	private List<Factura> listFacturas;

	private Factura factura;
	
	private FacturaRecepcion facturaRecepcion;
	
	private List<FacturaRecepcion> facturaRecepcions;
	
	

	private Object objFactura;

	private String estado = null;

	@Inject
	private SessionBean sessionBean;

	@Inject
	private ApplicationBean applicationBean;

	private String rucEmisor;
	
	private String emisor;

	private String rucComprador;

	private String identificadorUsuario;

	private String agencia;

	private String ordenCompra;
	
	private byte[] data;
	
	private String extension;
	
	private List<Factura> facturasSeleccionadas = new ArrayList<>();

	private static final String REPORTE_CLAVES_PATH = "/reportes/listafactura.jasper";
	private static final String REPORT_XLS_PATH = "listafactura.xls";
	private static final String REPORT_PDF_PATH = "listafactura.pdf";

	private static final String REPORTE_CLAVES_PATH_REIN = "/reportes/listafacturareim.jasper";

	private Converter converter;

	private Map<String, Object> listaParametros;

	private boolean buscarSegumiento = false;

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;
	
	private  ZipOutputStream zos = null;
	private  ZipEntry ze = null;
	private Boolean selectUne = false;

	@EJB(lookup = "java:global/adm-ejb/ServicioPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioPersonaLocal")
	ServicioPersonaLocal servicioPersonaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;
	
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRecepcionFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura")
	ServicioRecepcionFactura servicioRecepcionFactura;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioPlantillaImpl!com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal")
	ServicioPlantillaLocal servicioPlantilla;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;
	
	@EJB(lookup ="java:global/gnvoice-ejb/ComprobanteRegeneracionDAOImpl!com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO")
	ComprobanteRegeneracionDAO comprobanteRegeneracionDAO;


	

	public void descargarComprobantePDFMasivo() {
		
		if(facturasSeleccionadas==null || facturasSeleccionadas.isEmpty()){
			return;
		}

		String directorioZip = "/home/jboss/app/gnvoice/recursos/zip/";

		File[] archivosDelete = new File(directorioZip).listFiles();

		for (int i = 0; i < archivosDelete.length; i++) {
			if (archivosDelete[i].exists())
				archivosDelete[i].delete();
		}

		for (Factura data : facturasSeleccionadas) {

			String urlfactura = urlPDF(data);
			Path origenPath = FileSystems.getDefault().getPath(urlfactura);
			Path origenPathXml = FileSystems.getDefault().getPath(urlfactura.substring(0, urlfactura.length()-3)+"xml");
			Path destinoPath = FileSystems.getDefault().getPath(directorioZip + data.getClaveAcceso() + ".pdf");
			Path destinoPathXml = FileSystems.getDefault().getPath(directorioZip + data.getClaveAcceso() + ".xml");
			try {
				Files.move(origenPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
				Files.move(origenPathXml, destinoPathXml, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		String archivo = directorioZip + "Registros.zip";
		ficheros(directorioZip, new File(directorioZip).listFiles(), directorioZip + "Registros.zip");

		try {

			if (archivo != null && new File(archivo).exists()) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/zip");
				response.addHeader("Content-Disposition", "attachment; filename=Registros.zip");

				File file = new File(archivo);
				FileInputStream in = new FileInputStream(file);

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

		}

	}
	

	
	public void ficheros(final String folder, File[] ficheros, final String nombreZip) {
		try {
			zos = new ZipOutputStream(new FileOutputStream(new File(nombreZip)));
			for (int i = 0; i < ficheros.length; i++) {
				ze = new ZipEntry(ficheros[i].getName());
				zos.putNextEntry(ze);
				byte[] readAllBytesOfFile = Files.readAllBytes(new File(folder, ficheros[i].getName()).toPath());
				zos.write(readAllBytesOfFile, 0, readAllBytesOfFile.length);
			}
			zos.closeEntry();
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public void selectSingleRow(AjaxBehaviorEvent event) {
		Factura selectfactura = (Factura) event.getComponent().getAttributes().get("selectedFactura");
		if (selectfactura.getSelectFactura()) {
			facturasSeleccionadas.add(selectfactura);
		} else {
			for (int i = 0; i < facturasSeleccionadas.size(); i++) {
				if (selectfactura.getId().toString().equals(facturasSeleccionadas.get(i).getId().toString())) {
					facturasSeleccionadas.remove(i);
				}
			}
		}
	}	

	public void selectAllRow(AjaxBehaviorEvent event) {
		facturasSeleccionadas.clear();
		if (selectUne) {

			for (int i = 0; i < listFacturas.size(); i++) {
				listFacturas.get(i).setSelectFactura(true);
				facturasSeleccionadas.add(listFacturas.get(i));
			}
		}else{
			for (int i = 0; i < listFacturas.size(); i++) {
				listFacturas.get(i).setSelectFactura(false);
				
			}
			
		}
	}
	
	
	

	
	
	@SuppressWarnings({ "deprecation", "static-access" })
	public void descargarComprobantePDF() {

		try {
			
			factura = (Factura) objFactura;

			String archivo = factura.getArchivoLegible();

			String archivoXML = factura.getArchivo();

			String autorizacion = "";

			String fecha = "";

			String logo = "";

			String facturaPdf = "";

			Autorizacion autorizacionXML = null;
			
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;
			
			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML =null;			

			if (factura.getProceso() != null && !factura.getProceso().isEmpty() && factura.getProceso().equals( SistemaExternoEnum.SISTEMACRPDTA.name()) ) {
				try {

					facturaXML = descargarComprobanteJDE(factura).getFactura();
					autorizacionXML = new Autorizacion();
					
					autorizacionXML.setClaveAcceso(factura.getClaveAcceso());;
					autorizacionXML.setEstado(factura.getEstado());
					autorizacionXML.setNumeroAutorizacion(factura.getNumeroAutorizacion());
					
					Date dob=factura.getFechaAutorizacion();
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(factura.getFechaAutorizacion());
					autorizacionXML.setAmbiente(factura.getTipoAmbiente());
					
					autorizacionXML.setFechaAutorizacion(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize());
					
					String dirServidor = "/home/jboss/app";
					
					if (fecha.isEmpty()) {
						fecha = FechaUtil.formatearFecha(
								autorizacionXML.getFechaAutorizacion()
										.toGregorianCalendar().getTime(),
								FechaUtil.patronFechaTiempo24);
					}

					if (logo.isEmpty()) {
						logo = null;
					}
					
					archivo = ReporteUtil.generarReporte(Constantes.REP_FACTURA,new FacturaReporte(facturaXML), autorizacionXML.getNumeroAutorizacion(), fecha, facturaXML.getInfoFactura().getFechaEmision(), "autorizado",false, logo, dirServidor);

					if (archivo != null && new File(archivo).exists()) {
						FacesContext context = FacesContext.getCurrentInstance();
						HttpServletResponse response = (HttpServletResponse) context
								.getExternalContext().getResponse();
						OutputStream out = response.getOutputStream();

						response.setContentType("application/pdf");
						response.addHeader("Content-Disposition",
								"attachment; filename=factura.pdf");

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
					log.error(e.getMessage());
				}
				
				return ;
			} else {

				if ((archivo == null || archivo.isEmpty() || !new File(archivo).exists()) && (archivoXML == null || archivoXML.isEmpty() || !new File(archivoXML).exists())) 
				{
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
					String wsdlLocationAutorizacionOffline = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

					if (factura.getEstado().equals("AUTORIZADO")) {

						if(factura.getNumeroAutorizacion().length()>37){
							autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionOffline,factura.getClaveAcceso());
							log.info("offline"+autorizacionOfflineXML);	
						}else{
							autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion,factura.getClaveAcceso()); 
							log.info("online"+autorizacionXML);	
						}
						if ((autorizacionXML !=null && autorizacionXML.getComprobante() != null && !autorizacionXML.getComprobante().isEmpty())|| (autorizacionOfflineXML!=null && autorizacionOfflineXML.getComprobante() != null && !autorizacionOfflineXML.getComprobante().isEmpty())) {
						
							if(factura.getNumeroAutorizacion().length()>37){
								facturaPdf = autorizacionOfflineXML.getComprobante();
								facturaPdf = facturaPdf.replace("<![CDATA[", "");
								facturaPdf = facturaPdf.replace("]]>", "");
								log.info(facturaPdf.toString());
								facturaXML = getFacturaXML(facturaPdf);
								log.info(facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().size());	
								
							}else{
								facturaPdf = autorizacionXML.getComprobante();
								facturaPdf = facturaPdf.replace("<![CDATA[", "");
								facturaPdf = facturaPdf.replace("]]>", "");
								log.info(facturaPdf.toString());
								facturaXML = getFacturaXML(facturaPdf);
								log.info(facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().size());
							}
						
						} else {							
							
//							Long farmacia=0L;

								//***********//
								String lisfarmacia=null;
								if(factura.getAgencia() != null && !factura.getAgencia().isEmpty()){
									CredencialDS credencialDSOfi = new CredencialDS();
									credencialDSOfi.setDatabaseId("oficina");
									credencialDSOfi.setUsuario("WEBLINK");
									credencialDSOfi.setClave("weblink_2013");
									Connection connoficina = null;									
									try{				
										connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
										lisfarmacia = comprobanteRegeneracionDAO.listaSidGeneral(connoficina, Long.parseLong(factura.getAgencia())); 
										
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
								}
								
								if(lisfarmacia!=null && !lisfarmacia.isEmpty()){
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
										
										Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"FACTURA");

										if (tipo == null) {
											tipo = 1L;
										}
										
										com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = comprobanteRegeneracionDAO.
												listar(conn, Long.parseLong(factura.getClaveInterna()), farmacia2, tipo);
										if (item != null) {
											
											log.info("***Por agencia 333***");
											
											facturaXML = item.getFactura();
											
											
												fecha = FechaUtil.formatearFecha(
														factura.getFechaAutorizacion(),
														FechaUtil.patronFechaTiempo24);												
												
												
												List<Organizacion> emisores = servicioOrganizacionLocal
														.listarOrganizaciones(null, null, facturaXML
																.getInfoTributaria().getRuc(), null);
												
												Organizacion emisor=emisores.get(0);

												Parametro dirParametro = servicioParametro.consultarParametro(
														Constantes.DIRECTORIO_SERVIDOR, emisores.get(0).getId());
												String dirServidor = dirParametro != null ? dirParametro.getValor()
														: "";
											

											if (logo.isEmpty()) {
												logo = emisores.get(0).getLogoEmpresa();
											}
											
											Parametro ambParametro = servicioParametro.consultarParametro(
													Constantes.AMBIENTE, emisor.getId());
											Parametro monParamtro = servicioParametro.consultarParametro(
													Constantes.MONEDA, emisor.getId());
											Parametro codFacParametro = servicioParametro.consultarParametro(
													Constantes.COD_FACTURA, emisor.getId());
											//String ambiente = ambParametro.getValor();
											
											// Setea nodo principal
											item.getFactura().setId("comprobante");
											// mensaje.getFactura().setVersion(Constantes.VERSION);
											item.getFactura().setVersion(Constantes.VERSION_1);

											item.getFactura().getInfoFactura()
													.setMoneda(monParamtro.getValor());
											item.getFactura().getInfoTributaria()
													.setAmbiente(ambParametro.getValor());
											item.getFactura().getInfoTributaria()
													.setCodDoc(codFacParametro.getValor());

											if (item.getFactura().getInfoTributaria().getSecuencial()
													.length() < 9) {
												StringBuilder secuencial = new StringBuilder();

												int secuencialTam = 9 - item.getFactura()
														.getInfoTributaria().getSecuencial().length();
												for (int i = 0; i < secuencialTam; i++) {
													secuencial.append("0");
												}
												secuencial.append(item.getFactura().getInfoTributaria()
														.getSecuencial());
												item.getFactura().getInfoTributaria()
														.setSecuencial(secuencial.toString());
											}
											
											item.getFactura().getInfoTributaria().setClaveAcceso(factura.getClaveAcceso());

											item.getFactura().getInfoTributaria()
													.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

											String dirMatriz = emisor.getDireccion();

											item.getFactura()
													.getInfoTributaria()
													.setDirMatriz(
															dirMatriz != null ? StringEscapeUtils
																	.escapeXml(dirMatriz.trim())
																	.replaceAll("[\n]", "")
																	.replaceAll("[\b]", "") : null);

											if (item.getFactura().getInfoTributaria().getEstab() == null
													|| item.getFactura().getInfoTributaria().getEstab()
															.isEmpty()) {
												item.getFactura().getInfoTributaria()
														.setEstab(emisor.getEstablecimiento());

											}
											if (item.getFactura().getInfoTributaria().getNombreComercial() == null
													|| item.getFactura().getInfoTributaria()
															.getNombreComercial().isEmpty()) {

												item.getFactura()
														.getInfoTributaria()
														.setNombreComercial(
																emisor.getNombreComercial() != null ? StringEscapeUtils
																		.escapeXml(
																				emisor.getNombreComercial()
																						.trim())
																		.replaceAll("[\n]", "")
																		.replaceAll("[\b]", "") : null);
											}

											if (item.getFactura().getInfoTributaria().getPtoEmi() == null
													|| item.getFactura().getInfoTributaria().getPtoEmi()
															.isEmpty()) {
												item.getFactura().getInfoTributaria()
														.setPtoEmi(emisor.getPuntoEmision());
											}
											item.getFactura()
													.getInfoTributaria()
													.setRazonSocial(
															emisor.getNombre() != null ? StringEscapeUtils
																	.escapeXml(emisor.getNombre()) : null);

											item.getFactura()
													.getInfoFactura()
													.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
											if (item.getFactura().getInfoFactura().getDirEstablecimiento() == null
													|| item.getFactura().getInfoFactura()
															.getDirEstablecimiento().isEmpty()) {
												item.getFactura()
														.getInfoFactura()
														.setDirEstablecimiento(
																emisor.getDirEstablecimiento() != null ? StringEscapeUtils
																		.escapeXml(emisor
																				.getDirEstablecimiento())
																		: null);
											}
											item.getFactura()
													.getInfoFactura()
													.setObligadoContabilidad(
															emisor.getEsObligadoContabilidad().getDescripcion()
																	.toUpperCase());

											archivo = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
													new FacturaReporte(facturaXML), factura
															.getNumeroAutorizacion(), fecha, item.getFactura()
															.getInfoFactura().getFechaEmision(), "autorizado",
													false, logo, dirServidor);
											
											log.info("***Por agencia 444***"+archivo);

											// }
											// }
											//
											if (archivo != null && new File(archivo).exists()) {
												FacesContext context = FacesContext.getCurrentInstance();
												HttpServletResponse response = (HttpServletResponse) context
														.getExternalContext().getResponse();
												OutputStream out = response.getOutputStream();

												response.setContentType("application/pdf");
												response.addHeader("Content-Disposition",
														"attachment; filename=factura.pdf");

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
											
											log.info("***Por agencia 555***");
											return;

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
								
								//**********//
//							}
							//---------
							
						}
						
					}
				} else {

					autorizacion = DocumentUtil.readContentFile(archivoXML);

					this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
					
					try{
					autorizacionXML =(Autorizacion) this.converter.convertirAObjeto(autorizacion, Autorizacion.class);

					facturaPdf = autorizacionXML.getComprobante();
					facturaPdf = facturaPdf.replace("<![CDATA[", "");
					facturaPdf = facturaPdf.replace("]]>", "");
					
					log.info(facturaPdf.toString());
					facturaXML = getFacturaXML(facturaPdf);
					log.info(facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().size());
					
					}catch (Exception ex) {
						
					

						//***********//
						String lisfarmacia=null;
						if(factura.getAgencia() != null && !factura.getAgencia().isEmpty()){
							CredencialDS credencialDSOfi = new CredencialDS();
							credencialDSOfi.setDatabaseId("oficina");
							credencialDSOfi.setUsuario("WEBLINK");
							credencialDSOfi.setClave("weblink_2013");
							Connection connoficina = null;									
							try{				
								connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
								lisfarmacia = comprobanteRegeneracionDAO.listaSidGeneral(connoficina, Long.parseLong(factura.getAgencia())); 
								
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
						}
						
						if(lisfarmacia!=null && !lisfarmacia.isEmpty()){
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
								
								Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"FACTURA");

								if (tipo == null) {
									tipo = 1L;
								}
								
								com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = comprobanteRegeneracionDAO.
										listar(conn, Long.parseLong(factura.getClaveInterna()), farmacia2, tipo);
								if (item != null) {
									
									log.info("***Por agencia 333***");
									
									facturaXML = item.getFactura();
									
									
										fecha = FechaUtil.formatearFecha(
												factura.getFechaAutorizacion(),
												FechaUtil.patronFechaTiempo24);												
										
										
										List<Organizacion> emisores = servicioOrganizacionLocal
												.listarOrganizaciones(null, null, facturaXML
														.getInfoTributaria().getRuc(), null);
										
										Organizacion emisor=emisores.get(0);

										Parametro dirParametro = servicioParametro.consultarParametro(
												Constantes.DIRECTORIO_SERVIDOR, emisores.get(0).getId());
										String dirServidor = dirParametro != null ? dirParametro.getValor()
												: "";
									

									if (logo.isEmpty()) {
										logo = emisores.get(0).getLogoEmpresa();
									}
									
									Parametro ambParametro = servicioParametro.consultarParametro(
											Constantes.AMBIENTE, emisor.getId());
									Parametro monParamtro = servicioParametro.consultarParametro(
											Constantes.MONEDA, emisor.getId());
									Parametro codFacParametro = servicioParametro.consultarParametro(
											Constantes.COD_FACTURA, emisor.getId());
									//String ambiente = ambParametro.getValor();
									
									// Setea nodo principal
									item.getFactura().setId("comprobante");
									// mensaje.getFactura().setVersion(Constantes.VERSION);
									item.getFactura().setVersion(Constantes.VERSION_1);

									item.getFactura().getInfoFactura()
											.setMoneda(monParamtro.getValor());
									item.getFactura().getInfoTributaria()
											.setAmbiente(ambParametro.getValor());
									item.getFactura().getInfoTributaria()
											.setCodDoc(codFacParametro.getValor());

									if (item.getFactura().getInfoTributaria().getSecuencial()
											.length() < 9) {
										StringBuilder secuencial = new StringBuilder();

										int secuencialTam = 9 - item.getFactura()
												.getInfoTributaria().getSecuencial().length();
										for (int i = 0; i < secuencialTam; i++) {
											secuencial.append("0");
										}
										secuencial.append(item.getFactura().getInfoTributaria()
												.getSecuencial());
										item.getFactura().getInfoTributaria()
												.setSecuencial(secuencial.toString());
									}
									
									item.getFactura().getInfoTributaria().setClaveAcceso(factura.getClaveAcceso());

									item.getFactura().getInfoTributaria()
											.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

									String dirMatriz = emisor.getDireccion();

									item.getFactura()
											.getInfoTributaria()
											.setDirMatriz(
													dirMatriz != null ? StringEscapeUtils
															.escapeXml(dirMatriz.trim())
															.replaceAll("[\n]", "")
															.replaceAll("[\b]", "") : null);

									if (item.getFactura().getInfoTributaria().getEstab() == null
											|| item.getFactura().getInfoTributaria().getEstab()
													.isEmpty()) {
										item.getFactura().getInfoTributaria()
												.setEstab(emisor.getEstablecimiento());

									}
									if (item.getFactura().getInfoTributaria().getNombreComercial() == null
											|| item.getFactura().getInfoTributaria()
													.getNombreComercial().isEmpty()) {

										item.getFactura()
												.getInfoTributaria()
												.setNombreComercial(
														emisor.getNombreComercial() != null ? StringEscapeUtils
																.escapeXml(
																		emisor.getNombreComercial()
																				.trim())
																.replaceAll("[\n]", "")
																.replaceAll("[\b]", "") : null);
									}

									if (item.getFactura().getInfoTributaria().getPtoEmi() == null
											|| item.getFactura().getInfoTributaria().getPtoEmi()
													.isEmpty()) {
										item.getFactura().getInfoTributaria()
												.setPtoEmi(emisor.getPuntoEmision());
									}
									item.getFactura()
											.getInfoTributaria()
											.setRazonSocial(
													emisor.getNombre() != null ? StringEscapeUtils
															.escapeXml(emisor.getNombre()) : null);

									item.getFactura()
											.getInfoFactura()
											.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
									if (item.getFactura().getInfoFactura().getDirEstablecimiento() == null
											|| item.getFactura().getInfoFactura()
													.getDirEstablecimiento().isEmpty()) {
										item.getFactura()
												.getInfoFactura()
												.setDirEstablecimiento(
														emisor.getDirEstablecimiento() != null ? StringEscapeUtils
																.escapeXml(emisor
																		.getDirEstablecimiento())
																: null);
									}
									item.getFactura()
											.getInfoFactura()
											.setObligadoContabilidad(
													emisor.getEsObligadoContabilidad().getDescripcion()
															.toUpperCase());

									archivo = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
											new FacturaReporte(facturaXML), factura
													.getNumeroAutorizacion(), fecha, item.getFactura()
													.getInfoFactura().getFechaEmision(), "autorizado",
											false, logo, dirServidor);
									
									log.info("***Por agencia 444***"+archivo);

									// }
									// }
									//
									if (archivo != null && new File(archivo).exists()) {
										FacesContext context = FacesContext.getCurrentInstance();
										HttpServletResponse response = (HttpServletResponse) context
												.getExternalContext().getResponse();
										OutputStream out = response.getOutputStream();

										response.setContentType("application/pdf");
										response.addHeader("Content-Disposition",
												"attachment; filename=factura.pdf");

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
									
									log.info("***Por agencia 555***");
									return;

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
						
					}
				}

			}
			
			
			
			List<Organizacion> emisores = servicioOrganizacionLocal.listarOrganizaciones(null, null, facturaXML.getInfoTributaria().getRuc(), null);
			
			if (logo.isEmpty()) {
				logo = emisores.get(0).getLogoEmpresa();
			}
			Parametro dirParametro = servicioParametro.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, emisores.get(0).getId());
			String dirServidor = dirParametro != null ? dirParametro.getValor(): "";
			

			if (fecha.isEmpty()) {
				fecha = FechaUtil.formatearFecha(autorizacionXML!=null?autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime():autorizacionOfflineXML.getFechaAutorizacion().toGregorianCalendar().getTime(),FechaUtil.patronFechaTiempo24);
			}


			
			
			archivo = ReporteUtil.generarReporte( Constantes.REP_FACTURA	
												, new FacturaReporte(facturaXML)
												,autorizacionXML!=null?autorizacionXML.getNumeroAutorizacion():autorizacionOfflineXML.getNumeroAutorizacion()
												, fecha
												, facturaXML.getInfoFactura().getFechaEmision()
												, "autorizado",false, logo, dirServidor);
			
			
			
			log.info("***archivo pdf 999***"+archivo);

			if (archivo != null && new File(archivo).exists()) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition",
						"attachment; filename=factura.pdf");

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

	/***
	 * @autor spramirezv
	 * @descripcionDelCambio metodo para generar ride bajo demanda para abf
	 */
	public String urlPDF(Factura factura) {
		
		String result ="";
		try {
			String archivo = factura.getArchivoLegible();
			String archivoXML = factura.getArchivo();
			String autorizacion = "";
			String fecha = "";
			String logo = "";
			String facturaPdf = "";
			Autorizacion autorizacionXML = null;
			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML =null;			
			if (factura.getProceso() != null && !factura.getProceso().isEmpty() && factura.getProceso().equals( SistemaExternoEnum.SISTEMACRPDTA.name()) ) {
				try {
					facturaXML = descargarComprobanteJDE(factura).getFactura();
					autorizacionXML = new Autorizacion();
					autorizacionXML.setClaveAcceso(factura.getClaveAcceso());;
					autorizacionXML.setEstado(factura.getEstado());
					autorizacionXML.setNumeroAutorizacion(factura.getNumeroAutorizacion());
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(factura.getFechaAutorizacion());
					autorizacionXML.setAmbiente(factura.getTipoAmbiente());
					String dirServidor = "/home/jboss/app";
					
					if (fecha.isEmpty()) {
						fecha = FechaUtil.formatearFecha(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime(),FechaUtil.patronFechaTiempo24);
					}

					if (logo.isEmpty()) {
						logo = null;
					}
					
					archivo = ReporteUtil.generarReporte(Constantes.REP_FACTURA,new FacturaReporte(facturaXML), autorizacionXML.getNumeroAutorizacion(), fecha, facturaXML.getInfoFactura().getFechaEmision(), "autorizado",false, logo, dirServidor);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				return archivo ;
			} else {

				if ((archivo == null || archivo.isEmpty() || !new File(archivo).exists()) && (archivoXML == null || archivoXML.isEmpty() || !new File(archivoXML).exists())) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
					if (factura.getEstado().equals("AUTORIZADO")) {
						// metodo a usar para regenerar
						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion,factura.getClaveAcceso());

						if (autorizacionXML.getComprobante() != null && !autorizacionXML.getComprobante().isEmpty()) {
							facturaPdf = autorizacionXML.getComprobante();
							facturaPdf = facturaPdf.replace("<![CDATA[", "");
							facturaPdf = facturaPdf.replace("]]>", "");
							log.info(facturaPdf.toString());
							facturaXML = getFacturaXML(facturaPdf);
							log.info(facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().size());
							
						} else {							
							
								String lisfarmacia=null;
								if(factura.getAgencia() != null && !factura.getAgencia().isEmpty()){
									CredencialDS credencialDSOfi = new CredencialDS();
									credencialDSOfi.setDatabaseId("oficina");
									credencialDSOfi.setUsuario("WEBLINK");
									credencialDSOfi.setClave("weblink_2013");
									Connection connoficina = null;									
									try{				
										connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
										lisfarmacia = comprobanteRegeneracionDAO.listaSidGeneral(connoficina, Long.parseLong(factura.getAgencia())); 
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
								}
								
								if(lisfarmacia!=null && !lisfarmacia.isEmpty()){
									String[] parametros = lisfarmacia.split("&");
									Long farmacia2 = Long.parseLong(parametros[0]);
									String sid = parametros[1];
									CredencialDS credencialDS = new CredencialDS();
									credencialDS.setDatabaseId(sid);
									credencialDS.setUsuario("WEBLINK");
									credencialDS.setClave("weblink_2013");
									Connection conn = null;
									try {
										conn = Conexion.obtenerConexionFybeca(credencialDS);
										Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"FACTURA");
										if (tipo == null) {
											tipo = 1L;
										}
										
										com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = comprobanteRegeneracionDAO.
												listar(conn, Long.parseLong(factura.getClaveInterna()), farmacia2, tipo);

										if (item != null) {
											log.info("***Por agencia 333***");
											facturaXML = item.getFactura();
												fecha = FechaUtil.formatearFecha(
														factura.getFechaAutorizacion(),
														FechaUtil.patronFechaTiempo24);												
												List<Organizacion> emisores = servicioOrganizacionLocal
														.listarOrganizaciones(null, null, facturaXML
																.getInfoTributaria().getRuc(), null);
												
												Organizacion emisor=emisores.get(0);

												Parametro dirParametro = servicioParametro.consultarParametro(
														Constantes.DIRECTORIO_SERVIDOR, emisores.get(0).getId());
												String dirServidor = dirParametro != null ? dirParametro.getValor(): "";
											

											if (logo.isEmpty()) {
												logo = emisores.get(0).getLogoEmpresa();
											}
											
											Parametro ambParametro = servicioParametro.consultarParametro(
													Constantes.AMBIENTE, emisor.getId());
											Parametro monParamtro = servicioParametro.consultarParametro(
													Constantes.MONEDA, emisor.getId());
											Parametro codFacParametro = servicioParametro.consultarParametro(
													Constantes.COD_FACTURA, emisor.getId());
											//String ambiente = ambParametro.getValor();
											
											// Setea nodo principal
											item.getFactura().setId("comprobante");
											// mensaje.getFactura().setVersion(Constantes.VERSION);
											item.getFactura().setVersion(Constantes.VERSION_1);

											item.getFactura().getInfoFactura()
													.setMoneda(monParamtro.getValor());
											item.getFactura().getInfoTributaria()
													.setAmbiente(ambParametro.getValor());
											item.getFactura().getInfoTributaria()
													.setCodDoc(codFacParametro.getValor());

											if (item.getFactura().getInfoTributaria().getSecuencial()
													.length() < 9) {
												StringBuilder secuencial = new StringBuilder();

												int secuencialTam = 9 - item.getFactura()
														.getInfoTributaria().getSecuencial().length();
												for (int i = 0; i < secuencialTam; i++) {
													secuencial.append("0");
												}
												secuencial.append(item.getFactura().getInfoTributaria()
														.getSecuencial());
												item.getFactura().getInfoTributaria()
														.setSecuencial(secuencial.toString());
											}
											
											item.getFactura().getInfoTributaria().setClaveAcceso(factura.getClaveAcceso());

											item.getFactura().getInfoTributaria()
													.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

											String dirMatriz = emisor.getDireccion();

											item.getFactura()
													.getInfoTributaria()
													.setDirMatriz(
															dirMatriz != null ? StringEscapeUtils
																	.escapeXml(dirMatriz.trim())
																	.replaceAll("[\n]", "")
																	.replaceAll("[\b]", "") : null);

											if (item.getFactura().getInfoTributaria().getEstab() == null
													|| item.getFactura().getInfoTributaria().getEstab()
															.isEmpty()) {
												item.getFactura().getInfoTributaria()
														.setEstab(emisor.getEstablecimiento());

											}
											if (item.getFactura().getInfoTributaria().getNombreComercial() == null
													|| item.getFactura().getInfoTributaria()
															.getNombreComercial().isEmpty()) {

												item.getFactura()
														.getInfoTributaria()
														.setNombreComercial(
																emisor.getNombreComercial() != null ? StringEscapeUtils
																		.escapeXml(
																				emisor.getNombreComercial()
																						.trim())
																		.replaceAll("[\n]", "")
																		.replaceAll("[\b]", "") : null);
											}

											if (item.getFactura().getInfoTributaria().getPtoEmi() == null
													|| item.getFactura().getInfoTributaria().getPtoEmi()
															.isEmpty()) {
												item.getFactura().getInfoTributaria()
														.setPtoEmi(emisor.getPuntoEmision());
											}
											item.getFactura()
													.getInfoTributaria()
													.setRazonSocial(
															emisor.getNombre() != null ? StringEscapeUtils
																	.escapeXml(emisor.getNombre()) : null);

											item.getFactura()
													.getInfoFactura()
													.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
											if (item.getFactura().getInfoFactura().getDirEstablecimiento() == null
													|| item.getFactura().getInfoFactura()
															.getDirEstablecimiento().isEmpty()) {
												item.getFactura()
														.getInfoFactura()
														.setDirEstablecimiento(
																emisor.getDirEstablecimiento() != null ? StringEscapeUtils
																		.escapeXml(emisor
																				.getDirEstablecimiento())
																		: null);
											}
											item.getFactura()
													.getInfoFactura()
													.setObligadoContabilidad(
															emisor.getEsObligadoContabilidad().getDescripcion()
																	.toUpperCase());

											archivo = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
													new FacturaReporte(facturaXML), factura
															.getNumeroAutorizacion(), fecha, item.getFactura()
															.getInfoFactura().getFechaEmision(), "autorizado",
													false, logo, dirServidor);
											
											
											
											DocumentUtil.createDocument( getFacturaXML(item)
																				  ,item.getFactura().getInfoFactura().getFechaEmision()
																				  ,factura.getClaveAcceso()
																				  ,TipoComprobante.FACTURA.getDescripcion()
																				  , "autorizado");
											
											return archivo;

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
								
								//**********//
//							}
							//---------
							
						}
						
					}
				} else {

					autorizacion = DocumentUtil.readContentFile(archivoXML);

					this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
					
					try{
					autorizacionXML =(Autorizacion) this.converter.convertirAObjeto(autorizacion, Autorizacion.class);

					facturaPdf = autorizacionXML.getComprobante();
					facturaPdf = facturaPdf.replace("<![CDATA[", "");
					facturaPdf = facturaPdf.replace("]]>", "");
					
					log.info(facturaPdf.toString());
					facturaXML = getFacturaXML(facturaPdf);
					log.info(facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().size());
					
					}catch (Exception ex) {
						
						Long farmacia=0L;

						//***********//
						String lisfarmacia=null;
						if(factura.getAgencia() != null && !factura.getAgencia().isEmpty()){
							CredencialDS credencialDSOfi = new CredencialDS();
							credencialDSOfi.setDatabaseId("oficina");
							credencialDSOfi.setUsuario("WEBLINK");
							credencialDSOfi.setClave("weblink_2013");
							Connection connoficina = null;									
							try{				
								connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
								lisfarmacia = comprobanteRegeneracionDAO.listaSidGeneral(connoficina, Long.parseLong(factura.getAgencia())); 
								
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
						}
						
						if(lisfarmacia!=null && !lisfarmacia.isEmpty()){
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
								
								Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"FACTURA");

								if (tipo == null) {
									tipo = 1L;
								}
								
								com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = comprobanteRegeneracionDAO.
										listar(conn, Long.parseLong(factura.getClaveInterna()), farmacia2, tipo);
								if (item != null) {
									
									log.info("***Por agencia 333***");
									
									facturaXML = item.getFactura();
									
									
										fecha = FechaUtil.formatearFecha(
												factura.getFechaAutorizacion(),
												FechaUtil.patronFechaTiempo24);												
										
										
										List<Organizacion> emisores = servicioOrganizacionLocal
												.listarOrganizaciones(null, null, facturaXML
														.getInfoTributaria().getRuc(), null);
										
										Organizacion emisor=emisores.get(0);

										Parametro dirParametro = servicioParametro.consultarParametro(
												Constantes.DIRECTORIO_SERVIDOR, emisores.get(0).getId());
										String dirServidor = dirParametro != null ? dirParametro.getValor()
												: "";
									

									if (logo.isEmpty()) {
										logo = emisores.get(0).getLogoEmpresa();
									}
									
									Parametro ambParametro = servicioParametro.consultarParametro(
											Constantes.AMBIENTE, emisor.getId());
									Parametro monParamtro = servicioParametro.consultarParametro(
											Constantes.MONEDA, emisor.getId());
									Parametro codFacParametro = servicioParametro.consultarParametro(
											Constantes.COD_FACTURA, emisor.getId());
									//String ambiente = ambParametro.getValor();
									
									// Setea nodo principal
									item.getFactura().setId("comprobante");
									// mensaje.getFactura().setVersion(Constantes.VERSION);
									item.getFactura().setVersion(Constantes.VERSION_1);

									item.getFactura().getInfoFactura()
											.setMoneda(monParamtro.getValor());
									item.getFactura().getInfoTributaria()
											.setAmbiente(ambParametro.getValor());
									item.getFactura().getInfoTributaria()
											.setCodDoc(codFacParametro.getValor());

									if (item.getFactura().getInfoTributaria().getSecuencial()
											.length() < 9) {
										StringBuilder secuencial = new StringBuilder();

										int secuencialTam = 9 - item.getFactura()
												.getInfoTributaria().getSecuencial().length();
										for (int i = 0; i < secuencialTam; i++) {
											secuencial.append("0");
										}
										secuencial.append(item.getFactura().getInfoTributaria()
												.getSecuencial());
										item.getFactura().getInfoTributaria()
												.setSecuencial(secuencial.toString());
									}
									
									item.getFactura().getInfoTributaria().setClaveAcceso(factura.getClaveAcceso());

									item.getFactura().getInfoTributaria()
											.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

									String dirMatriz = emisor.getDireccion();

									item.getFactura()
											.getInfoTributaria()
											.setDirMatriz(
													dirMatriz != null ? StringEscapeUtils
															.escapeXml(dirMatriz.trim())
															.replaceAll("[\n]", "")
															.replaceAll("[\b]", "") : null);

									if (item.getFactura().getInfoTributaria().getEstab() == null
											|| item.getFactura().getInfoTributaria().getEstab()
													.isEmpty()) {
										item.getFactura().getInfoTributaria()
												.setEstab(emisor.getEstablecimiento());

									}
									if (item.getFactura().getInfoTributaria().getNombreComercial() == null
											|| item.getFactura().getInfoTributaria()
													.getNombreComercial().isEmpty()) {

										item.getFactura()
												.getInfoTributaria()
												.setNombreComercial(
														emisor.getNombreComercial() != null ? StringEscapeUtils
																.escapeXml(
																		emisor.getNombreComercial()
																				.trim())
																.replaceAll("[\n]", "")
																.replaceAll("[\b]", "") : null);
									}

									if (item.getFactura().getInfoTributaria().getPtoEmi() == null
											|| item.getFactura().getInfoTributaria().getPtoEmi()
													.isEmpty()) {
										item.getFactura().getInfoTributaria()
												.setPtoEmi(emisor.getPuntoEmision());
									}
									item.getFactura()
											.getInfoTributaria()
											.setRazonSocial(
													emisor.getNombre() != null ? StringEscapeUtils
															.escapeXml(emisor.getNombre()) : null);

									item.getFactura()
											.getInfoFactura()
											.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
									if (item.getFactura().getInfoFactura().getDirEstablecimiento() == null
											|| item.getFactura().getInfoFactura()
													.getDirEstablecimiento().isEmpty()) {
										item.getFactura()
												.getInfoFactura()
												.setDirEstablecimiento(
														emisor.getDirEstablecimiento() != null ? StringEscapeUtils
																.escapeXml(emisor
																		.getDirEstablecimiento())
																: null);
									}
									item.getFactura()
											.getInfoFactura()
											.setObligadoContabilidad(
													emisor.getEsObligadoContabilidad().getDescripcion()
															.toUpperCase());

									archivo = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
											new FacturaReporte(facturaXML), factura
													.getNumeroAutorizacion(), fecha, item.getFactura()
													.getInfoFactura().getFechaEmision(), "autorizado",
											false, logo, dirServidor);
									
									
									DocumentUtil.createDocument( getFacturaXML(item)
																		  ,item.getFactura().getInfoFactura().getFechaEmision()
																		  ,factura.getClaveAcceso()
																		  ,TipoComprobante.FACTURA.getDescripcion()
																		  , "autorizado");
									
									return archivo;

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
						
					}
				}

			}
			
			
			
			List<Organizacion> emisores = servicioOrganizacionLocal
					.listarOrganizaciones(null, null, facturaXML
							.getInfoTributaria().getRuc(), null);

			Parametro dirParametro = servicioParametro.consultarParametro(
					Constantes.DIRECTORIO_SERVIDOR, emisores.get(0).getId());
			
			String dirServidor = dirParametro != null ? dirParametro.getValor()
					: "";
			
			if (fecha.isEmpty()) {
				fecha = FechaUtil.formatearFecha(
						autorizacionXML.getFechaAutorizacion()
								.toGregorianCalendar().getTime(),
						FechaUtil.patronFechaTiempo24);
			}

			if (logo.isEmpty()) {
				logo = emisores.get(0).getLogoEmpresa();
			}

			
			
			archivo = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
												 new FacturaReporte(facturaXML)
												 ,autorizacionXML.getNumeroAutorizacion()
												 ,fecha
												 ,facturaXML.getInfoFactura().getFechaEmision()
												 ,"autorizado"
												 ,false
												 ,logo
												 ,dirServidor);
			
			
			DocumentUtil.createDocument( getFacturaXML(facturaXML)
												  ,facturaXML.getInfoFactura().getFechaEmision()
												  ,factura.getClaveAcceso()
												  ,TipoComprobante.FACTURA.getDescripcion()
												  , "autorizado");
			
			
			return archivo;

		} catch (Exception e) {
			log.error("Error ride unificado: " + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al descargar el documento");
		}
		return result;
	}
	
	/***
	 @author spramirezv
	 @decripcionDelCambio metodo genera objeto Factura JDE
	 validar  
	 */

	public com.gizlocorp.gnvoice.xml.FacturaProcesarRequest descargarComprobanteJDE(Factura factura){

		CredencialDS credencialDS = new CredencialDS();
		/*credencialDS.setDatabaseId("jdeprd");
		credencialDS.setUsuario("integra");
		credencialDS.setClave("Gpf2015");
		*/
		
		credencialDS.setDatabaseId("jde_desa");
		credencialDS.setUsuario("integra");
		credencialDS.setClave("Gpf2015");
		
		Connection conn = null;
		com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = new FacturaProcesarRequest();

		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
		    item = comprobanteRegeneracionDAO.listarJDE(conn,factura.getClaveAcceso());
					if (item != null) {
							item.getFactura().setId("comprobante");
							item.getFactura().setVersion(Constantes.VERSION_1);
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
	/**
	 * @finDelCambio
	 */
		
		

	public void descargarComprobanteXML() {
		try {
			factura = (Factura) objFactura;
			if (factura.getProceso() != null
					&& !factura.getProceso().isEmpty()
					&& factura.getProceso().equals(
							SistemaExternoEnum.SISTEMACRPDTA.name())) {
				log.info("***descargandoxml 222***");
				CredencialDS credencialDS = new CredencialDS();
				credencialDS.setDatabaseId("PROD");
				credencialDS.setUsuario("integraciones");
				credencialDS.setClave("1xnt3xgr4xBusD4xt0xs");
				Connection conn = null;
				String facturaxml = null;
				try {
					conn = Conexion.obtenerConexionFybeca(credencialDS);
					facturaxml = concsultarPorClaveAccesoXml(conn,
							factura.getClaveAcceso());
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
				String archivo = factura.getArchivo();

				boolean noExistio = true;

				if (archivo == null || archivo.isEmpty()
						|| !new File(archivo).exists()) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

					if (factura.getEstado().equals("AUTORIZADO")) {
						// metodo a usar para regenerar
						Autorizacion respAutorizacion = autorizarComprobante(
								wsdlLocationAutorizacion,
								factura.getClaveAcceso());
						if (respAutorizacion.getComprobante() != null
								&& !respAutorizacion.getComprobante().isEmpty()) {
							String comprobanteXML = respAutorizacion
									.getComprobante();
							comprobanteXML = comprobanteXML.replace(
									"<![CDATA[", "");
							comprobanteXML = comprobanteXML.replace("]]>", "");

							com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobanteXML);

							archivo = DocumentUtil.createDocument(
									getFacturaXML(respAutorizacion),
									factAutorizadaXML.getInfoFactura()
											.getFechaEmision(), factura
											.getClaveAcceso(),
									TipoComprobante.FACTURA.getDescripcion(),
									"autorizado");
							
							noExistio = false;
						} else {
							
							log.info("***generando xml desde la base de datos 111***");
							Long farmacia=0L;
							if(factura.getSecuencialOriginal() != null && !factura.getSecuencialOriginal().isEmpty()){							
								
								if(factura.getSecuencialOriginal().toUpperCase().contains("F".toUpperCase())){
									CredencialDS credencialDSOfi = new CredencialDS();
									credencialDSOfi.setDatabaseId("oficina");
									credencialDSOfi.setUsuario("WEBLINK");
									credencialDSOfi.setClave("weblink_2013");
									Connection connoficina = null;
									try{				
										connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
										farmacia = comprobanteRegeneracionDAO.listaSidOficina(connoficina, factura.getSecuencialOriginal()); 
										log.info("***farmacia oficina 111***"+farmacia);
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
									
								}
								if(factura.getSecuencialOriginal().toUpperCase().contains("S".toUpperCase())){
									CredencialDS credencialDSana = new CredencialDS();
									credencialDSana.setDatabaseId("sana");
									credencialDSana.setUsuario("WEBLINK");
									credencialDSana.setClave("weblink_2013");
									Connection connsana = null;
									try{				
										connsana = Conexion.obtenerConexionFybeca(credencialDSana);
										farmacia = comprobanteRegeneracionDAO.listaSidSanaSana(connsana, factura.getSecuencialOriginal()); 
										log.info("***farmacia sana ***"+farmacia);
									} catch (Exception e) {
										log.error(e.getMessage()+" "+e.getLocalizedMessage());
									} finally {
										try {
											if (connsana != null)
												connsana.close();
										} catch (Exception e) {
											log.error(e.getMessage()+" "+e.getLocalizedMessage());
										}
									}
								}
								if(factura.getSecuencialOriginal().toUpperCase().contains("O".toUpperCase())){
									CredencialDS credencialDOki = new CredencialDS();
									credencialDOki.setDatabaseId("okimaster");
									credencialDOki.setUsuario("WEBLINK");
									credencialDOki.setClave("weblink_2013");
									Connection connoki = null;
									try{				
										connoki = Conexion.obtenerConexionFybeca(credencialDOki);
										farmacia = comprobanteRegeneracionDAO.listaOkiDoki(connoki, factura.getSecuencialOriginal()); 
										log.info("***farmacia okimaster ***"+farmacia);
									} catch (Exception e) {
										log.error(e.getMessage()+" "+e.getLocalizedMessage());
									} finally {
										try {
											if (connoki != null)
												connoki.close();
										} catch (Exception e) {
											log.error(e.getMessage()+" "+e.getLocalizedMessage());
										}
									}
								}
								
								//extrayendo de la base
								CredencialDS credencialDS = new CredencialDS();
								credencialDS.setDatabaseId(factura.getSecuencialOriginal());
								credencialDS.setUsuario("WEBLINK");
								credencialDS.setClave("weblink_2013");
								
								Connection conn = null;
								try {
									conn = Conexion.obtenerConexionFybeca(credencialDS);
									log.info("***Conexion obtenida 111***");
									Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"FACTURA");

									if (tipo == null) {
										tipo = 1L;
									}
									
									log.info("***generando xml desde la base de datos 222***"+tipo);

									com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = comprobanteRegeneracionDAO.
											listar(conn, Long.parseLong(factura.getClaveInterna()), farmacia, tipo);
									if (item != null) {
										archivo = DocumentUtil
												.createDocument(
														getFacturaXML(item),
														item.getFactura().getInfoFactura()
																.getFechaEmision(),
														factura.getClaveAcceso(), TipoComprobante.FACTURA
																.getDescripcion(), "autorizado");
										
										log.info("***generando xml desde la base de datos 333***"+archivo);
										
										

										noExistio = false;
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
							}else{
								String lisfarmacia=null;
								
								log.info("***Por agencia ***");
								if(factura.getAgencia() != null && !factura.getAgencia().isEmpty()){
									CredencialDS credencialDSOfi = new CredencialDS();
									credencialDSOfi.setDatabaseId("oficina");
									credencialDSOfi.setUsuario("WEBLINK");
									credencialDSOfi.setClave("weblink_2013");
									Connection connoficina = null;									
									try{				
										connoficina = Conexion.obtenerConexionFybeca(credencialDSOfi);
										lisfarmacia = comprobanteRegeneracionDAO.listaSidGeneral(connoficina, Long.parseLong(factura.getAgencia())); 
										log.info("***Por agencia 111***"+farmacia);
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
								}
								
								if(lisfarmacia!=null && !lisfarmacia.isEmpty()){
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
										log.info("***Por agencia 222***");
										Long tipo = comprobanteRegeneracionDAO.obtenerTipo(conn,"FACTURA");

										if (tipo == null) {
											tipo = 1L;
										}
										
										log.info("***Por agencia 33***"+tipo);

										com.gizlocorp.gnvoice.xml.FacturaProcesarRequest item = comprobanteRegeneracionDAO.
												listar(conn, Long.parseLong(factura.getClaveInterna()), farmacia2, tipo);
										
										if (item != null) {
											log.info("***Por agencia 444***");	
											
											List<Organizacion> emisores = servicioOrganizacionLocal
													.listarOrganizaciones(null, null, item.getFactura()
															.getInfoTributaria().getRuc(), null);
											
											Organizacion emisor=emisores.get(0);											
											
											Parametro ambParametro = servicioParametro.consultarParametro(
													Constantes.AMBIENTE, emisor.getId());
											Parametro monParamtro = servicioParametro.consultarParametro(
													Constantes.MONEDA, emisor.getId());
											Parametro codFacParametro = servicioParametro.consultarParametro(
													Constantes.COD_FACTURA, emisor.getId());
											//String ambiente = ambParametro.getValor();
											
											// Setea nodo principal
											item.getFactura().setId("comprobante");
											// mensaje.getFactura().setVersion(Constantes.VERSION);
											item.getFactura().setVersion(Constantes.VERSION_1);

											item.getFactura().getInfoFactura()
													.setMoneda(monParamtro.getValor());
											item.getFactura().getInfoTributaria()
													.setAmbiente(ambParametro.getValor());
											item.getFactura().getInfoTributaria()
													.setCodDoc(codFacParametro.getValor());

											if (item.getFactura().getInfoTributaria().getSecuencial()
													.length() < 9) {
												StringBuilder secuencial = new StringBuilder();

												int secuencialTam = 9 - item.getFactura()
														.getInfoTributaria().getSecuencial().length();
												for (int i = 0; i < secuencialTam; i++) {
													secuencial.append("0");
												}
												secuencial.append(item.getFactura().getInfoTributaria()
														.getSecuencial());
												item.getFactura().getInfoTributaria()
														.setSecuencial(secuencial.toString());
											}

											item.getFactura().getInfoTributaria()
													.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

											String dirMatriz = emisor.getDireccion();

											item.getFactura()
													.getInfoTributaria()
													.setDirMatriz(
															dirMatriz != null ? StringEscapeUtils
																	.escapeXml(dirMatriz.trim())
																	.replaceAll("[\n]", "")
																	.replaceAll("[\b]", "") : null);

											if (item.getFactura().getInfoTributaria().getEstab() == null
													|| item.getFactura().getInfoTributaria().getEstab()
															.isEmpty()) {
												item.getFactura().getInfoTributaria()
														.setEstab(emisor.getEstablecimiento());

											}
											if (item.getFactura().getInfoTributaria().getNombreComercial() == null
													|| item.getFactura().getInfoTributaria()
															.getNombreComercial().isEmpty()) {

												item.getFactura()
														.getInfoTributaria()
														.setNombreComercial(
																emisor.getNombreComercial() != null ? StringEscapeUtils
																		.escapeXml(
																				emisor.getNombreComercial()
																						.trim())
																		.replaceAll("[\n]", "")
																		.replaceAll("[\b]", "") : null);
											}

											if (item.getFactura().getInfoTributaria().getPtoEmi() == null
													|| item.getFactura().getInfoTributaria().getPtoEmi()
															.isEmpty()) {
												item.getFactura().getInfoTributaria()
														.setPtoEmi(emisor.getPuntoEmision());
											}
											item.getFactura()
													.getInfoTributaria()
													.setRazonSocial(
															emisor.getNombre() != null ? StringEscapeUtils
																	.escapeXml(emisor.getNombre()) : null);

											item.getFactura()
													.getInfoFactura()
													.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
											if (item.getFactura().getInfoFactura().getDirEstablecimiento() == null
													|| item.getFactura().getInfoFactura()
															.getDirEstablecimiento().isEmpty()) {
												item.getFactura()
														.getInfoFactura()
														.setDirEstablecimiento(
																emisor.getDirEstablecimiento() != null ? StringEscapeUtils
																		.escapeXml(emisor
																				.getDirEstablecimiento())
																		: null);
											}
											item.getFactura()
													.getInfoFactura()
													.setObligadoContabilidad(
															emisor.getEsObligadoContabilidad().getDescripcion()
																	.toUpperCase());
											
											archivo = DocumentUtil
													.createDocument(
															getFacturaXML(item),
															item.getFactura().getInfoFactura()
																	.getFechaEmision(),
															factura.getClaveAcceso(), TipoComprobante.FACTURA
																	.getDescripcion(), "autorizado");
											
											log.info("***Por agencia 555***"+archivo);
											factura.setArchivo(archivo);
											servicioFactura.autoriza(factura); 
											
											noExistio = false;
											
										}
									}catch (Exception e) {
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
							}
							//---------
							log.info("***generando xml desde la base de datos 444***");
							
						}

					}
				}else{
					if(new File(archivo).exists()){
						noExistio = false;
					}
				}
				
				log.info("***generando xml desde la base de datos 555***");

				if (noExistio) {
					log.info("***El archivo no se encuentra disponible***");
					errorMessage("El archivo no se encuentra disponible en el SRI");
					return;
				}

				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/xml");
				response.addHeader("Content-Disposition",
						"attachment; filename=factura.xml");

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
	
	public void descargarComprobanteRecepcionXML() {
		try {
			
				facturaRecepcion = (FacturaRecepcion) objFactura;
				String archivo = facturaRecepcion.getArchivo();

				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/xml");
				response.addHeader("Content-Disposition",
						"attachment; filename=factura.xml");

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
			

		} catch (Exception e) {
			log.error("Error ride unificado: " + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al descargar el documento");
		}
	}

	public void descargarComprobanteRecepcionPDF() {
		try {
			
 
			Autorizacion autorizacionXML = null;
			
			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML =null;
			String facturaPdf ="";
			Calendar now = Calendar.getInstance();
			
			facturaRecepcion = (FacturaRecepcion) objFactura;

//			String archivo = facturaRecepcion.getArchivoLegible();
			
			String autorizacion = DocumentUtil.readContentFile(facturaRecepcion.getArchivo());

			this.converter = ConverterFactory
					.getConverter(XML_CONTENT_TYPE);
			autorizacionXML = this.converter.convertirAObjeto(
					autorizacion, Autorizacion.class);

			facturaPdf = autorizacionXML.getComprobante();
			facturaPdf = facturaPdf.replace("<![CDATA[", "");
			facturaPdf = facturaPdf.replace("]]>", "");
			
			log.info(facturaPdf.toString());
			
			facturaXML = getFacturaXML(facturaPdf);

			String rutaArchivoPdf = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
					new FacturaReporte(facturaXML), facturaRecepcion
							.getNumeroAutorizacion(), FechaUtil.formatearFecha(now.getTime(),"ddMMyyyy"), facturaXML
							.getInfoFactura().getFechaEmision(), "autorizado",
					false,"/home/jboss/app/gnvoice/recursos/reportes/Blanco.png", "/data");
			

			if (rutaArchivoPdf != null && new File(rutaArchivoPdf).exists()) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition",
						"attachment; filename=factura.pdf");

				File file = new File(rutaArchivoPdf);
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
	
	public void cambioEstado(){
		
	}
	
	private String getFacturaXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.factura.Factura) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.factura.Factura.class);
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
	
	/***
	 @author spramirezv
	 @descripcionDelCambio metodo para recuperar clase autorizacion
	 */
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private String consultarPorClaveAccesoXml(Connection conn,String claveAcceso) {
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
				respuesta = set.getString("CLAVE_ACCESO")+"&"+set.getString("ESTADO")+"&"+set.getString("AUTORIZACION")+"&"+set.getString("FECHA_AUTORIZACION")+"&"+set.getString("AMBIENTE");
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
	
	/***
	 @finDelCambio
	 */


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

	public void rideUnificado() {
		try {
			if (facturas != null && !facturas.isEmpty()) {
				PDFMergerUtility ut = new PDFMergerUtility();
				boolean existeDocumento = false;
				for (Factura factura : facturas) {
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
							+ "/gnvoice/recursos/reportes/comprobantes/rideunificado/factura"
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

	public void enviarCorreo() {
		try {
			factura = (Factura) objFactura;
			// /GENERA PDF
			try {
				String archivo = factura.getArchivoLegible();
				
				if (archivo == null || archivo.isEmpty() || archivo.contains("null.pdf")|| !(new File(archivo).exists())) {
					
					if(archivo!=null)
						archivo = factura.getArchivo().replace(".xml", ".pdf");

					if ((archivo == null || archivo.isEmpty() || !(new File(archivo).exists())) && factura.getEstado().equals("AUTORIZADO")) {

						String archivoXML = factura.getArchivo();

						String autorizacion = DocumentUtil.readContentFile(archivoXML);

						this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
						Autorizacion autorizacionXML = this.converter.convertirAObjeto(autorizacion,Autorizacion.class);

						String factura = autorizacionXML.getComprobante();
						factura = factura.replace("<![CDATA[", "");
						factura = factura.replace("]]>", "");

						com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = this.converter.convertirAObjeto(factura,com.gizlocorp.gnvoice.xml.factura.Factura.class);

						List<Organizacion> emisores = servicioOrganizacionLocal
								.listarOrganizaciones(null, null, facturaXML
										.getInfoTributaria().getRuc(), null);

						Parametro dirParametro = servicioParametro
								.consultarParametro(
										Constantes.DIRECTORIO_SERVIDOR,
										emisores.get(0).getId());
						String dirServidor = dirParametro != null ? dirParametro
								.getValor() : "";

						archivo = ReporteUtil.generarReporte(
								Constantes.REP_FACTURA, new FacturaReporte(
										facturaXML), autorizacionXML
										.getNumeroAutorizacion(), FechaUtil
										.formatearFecha(autorizacionXML
												.getFechaAutorizacion()
												.toGregorianCalendar()
												.getTime(),
												FechaUtil.patronFechaTiempo24),
								facturaXML.getInfoFactura().getFechaEmision(),
								"autorizado", false, emisores.get(0)
										.getLogoEmpresa(), dirServidor);

					}
				}

			} catch (Exception e) {
				log.error("Error ride unificado: " + e.getMessage(), e);
				errorMessage("Ha ocurrido un error al descargar el documento");
			}

			Organizacion org = servicioOrganizacionLocal
					.consultarOrganizacion(factura.getRuc());
			if (factura.getCorreoNotificacion() == null
					|| factura.getCorreoNotificacion().isEmpty()) {
				List<Persona> liPersonas = servicioPersonaLocal
						.obtenerPorParametros(
								factura.getIdentificacionComprador(), null,
								null, null);

				if (liPersonas != null && !liPersonas.isEmpty()) {
					factura.setCorreoNotificacion(liPersonas.get(0)
							.getCorreoElectronico());
				}
			}

			if (factura.getCorreoNotificacion() != null
					&& !factura.getCorreoNotificacion().isEmpty()) {
				Plantilla t = servicioPlantilla.obtenerPlantilla(
						Constantes.NOTIFIACION, org.getId());

				Map<String, Object> parametrosBody = new HashMap<String, Object>();

				parametrosBody.put("nombre", factura.getRazonSocialComprador());
				parametrosBody.put("tipo", "FACTURA");
				parametrosBody.put("secuencial", factura.getCodSecuencial());
				parametrosBody.put("fechaEmision",
						factura.getFechaEmisionBase());
				parametrosBody.put("total", factura.getImporteTotal());
				parametrosBody.put("emisor", factura.getRuc());
				parametrosBody.put("ruc", factura.getRuc());
				parametrosBody.put("numeroAutorizacion",
						factura.getNumeroAutorizacion());

				MailMessage mailMensaje = new MailMessage();
				mailMensaje.setSubject(t.getTitulo());
				mailMensaje.setFrom(servicioParametro.consultarParametro(
						Constantes.CORREO_REMITE, org.getId()).getValor());
				mailMensaje.setTo(Arrays.asList(factura.getCorreoNotificacion()
						.split(";")));
				mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(
						parametrosBody, t.getDescripcion(), t.getValor()));
				mailMensaje.setAttachment(Arrays.asList(factura.getArchivo(),
						factura.getArchivoLegible()));
				MailDelivery.send(
						mailMensaje,
						servicioParametro.consultarParametro(
								Constantes.SMTP_HOST, org.getId()).getValor(),
						servicioParametro.consultarParametro(
								Constantes.SMTP_PORT, org.getId()).getValor(),
						servicioParametro.consultarParametro(
								Constantes.SMTP_USERNAME, org.getId())
								.getValor(),
						servicioParametro.consultarParametro(
								Constantes.SMTP_PASSWORD, org.getId())
								.getValorDesencriptado(), org.getAcronimo());

				infoMessage("Correo enviado correctamente");
			}

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al enviar correo");
		}
	}
	
	public void ejecutaLecturaMail() {
		try {
			Parametro parametro= servicioParametro.consultarParametro("JOB_MAIL", null);
			if(parametro != null){
				parametro.setValor("1");
				servicioParametro.actualizarParametro(parametro);
			}

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Facturas");
		}
	}

	public void buscarFacturaRecibidos() {
		try {
			// crearenFtp();
			// facturas = servicioFactura.consultarCommprobantesRecibidos(
			// numeroComprobante, fechaDesde, fechaHasta, rucEmisor);

			log.info("***consultando recibidos***");
			buscarSegumiento = true;
			String rec = "REC";
			llenaParametros(rec);

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Facturas");
		}
	}

	public void enviarResagados() {		
		
		try {
			List<com.gizlocorp.gnvoice.modelo.Factura> facturas = servicioFactura
					.obtenerComprobantesResagados();
			if (facturas != null && !facturas.isEmpty()) {
				Factura_Service serviceFactura = new Factura_Service();
				FacturaPortType portFactura = serviceFactura.getFacturaPort();
				for (com.gizlocorp.gnvoice.modelo.Factura factura : facturas) {
					if (factura.getArchivo() != null
							&& factura.getCorreoNotificacion() != null
							&& factura.getOrdenCompra() != null && factura.getFechaEmisionBase().after(fechaDesde) ) {
						log.info("***resagados**" + factura.getArchivo()
								+ "***" + factura.getCorreoNotificacion());
						// servicioMailRecepcionLocal.enviaMailMdb(itera.getArchivo(),
						// itera.getCorreoNotificacion());
						FacturaRecibirRequest facturaRecibirRequest = new FacturaRecibirRequest();
						if (factura.getArchivo() != null) {
							facturaRecibirRequest
									.setComprobanteProveedor(factura
											.getArchivo());
						}
						if (factura.getArchivoLegible() != null) {
							facturaRecibirRequest
									.setComprobanteProveedorPDF(factura
											.getArchivoLegible());
						}
						facturaRecibirRequest.setProceso(RimEnum.REIM.name());
						if (factura.getOrdenCompra() != null
								&& !factura.getOrdenCompra().isEmpty()) {
							facturaRecibirRequest.setOrdenCompra(factura
									.getOrdenCompra());
						}
						log.info("**anviando a procesar***");

						facturaRecibirRequest.setMailOrigen(factura
								.getCorreoNotificacion());

						FacturaRecibirResponse respuestaFactura = portFactura
								.recibir(facturaRecibirRequest);

						if (respuestaFactura.getEstado().equalsIgnoreCase(
								"COMPLETADO")) {
							infoMessage("Proceso Realizado con exito");
						} else {
							infoMessage("Error al procesar la factura");
						}
					}
				}
			}

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Facturas");
		}
	}

	public void buscarFactura() {
		try {
			buscarSegumiento = true;
			String rec = "EMI";
			llenaParametros(rec);

			// facturas =
			// servicioFactura.consultarComprobantes(numeroComprobante,
			// fechaDesde, fechaHasta, rucComprador, codigoExterno,
			// identificadorUsuario, emisor);
			//
			// List<Organizacion> organizaciones = applicationBean
			// .getOrganizaciones();
			// if (facturas != null && !facturas.isEmpty()
			// && organizaciones != null && !organizaciones.isEmpty()) {
			// for (Factura factura : facturas) {
			// for (Organizacion organizacion : organizaciones) {
			// if (organizacion.getRuc().equals(factura.getRuc())) {
			// factura.setRazonSocial(organizacion.getNombre());
			//
			// }
			// }
			// }
			// }

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Facturas");
		}
	}

	public void buscarFacturaSeguimiento() {
		try {
			buscarSegumiento = true;
			/*String emi = "EMI";
	 	    llenaParametros(emi);*/
			
			//llenaParametros("EMI");
			 if (fechaHasta == null
			   && fechaDesde == null ) {
			  errorMessage("Ingresar un parametro de busqueda adicional.");
			 
			  } else {
				  
			  estado="AUTORIZADO";	  
				  //rucComprador="9999999999";
			  listFacturas = servicioFactura.consultarComprobantesSeguimientoAbf(
			  estado, numeroComprobante, fechaDesde, fechaHasta,
			  rucEmisor, codigoExterno, rucComprador,
			  identificadorUsuario, agencia, claveContingencia,
			  tipoEmision, tipoAmbiente, correoProveedor,
			  tipoEjecucion,
			  razonSocialComprador);
			
			 }

		} catch (Exception e) {
			e.printStackTrace();
			errorMessage("Ha ocurrido un error al listar Facturas");

		}
	}
	
	public void buscarFacturaSeguimientoPublica() {
		try {
			buscarSegumiento = true;
			listaParametros = new HashMap<String, Object>();
			listaParametros.put("tipoGeneracion", TipoGeneracion.EMI);			
			if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
				listaParametros.put("codSecuencial", "%" + numeroComprobante + "%");
			}			
			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				listaParametros.put("codigoExterno", codigoExterno);
			}			
			if (fechaDesde != null) {
				listaParametros.put("fechaDesde", fechaDesde);
			}
			if (fechaHasta != null) {
				listaParametros.put("fechaHasta", fechaHasta);
			}

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Facturas");

		}
	}

	private void llenaParametros(String tipoGeneracion) {
		listaParametros = new HashMap<String, Object>();
		String proceso = null;
		if (tipoGeneracion.equals(TipoGeneracion.EMI.name())) {
			listaParametros.put("tipoGeneracion", TipoGeneracion.EMI);
		} else {
			if (tipoGeneracion.equals(TipoGeneracion.REC.name())) {
				listaParametros.put("tipoGeneracion", TipoGeneracion.REC);
			}
			proceso = "REIM";
			if (proceso != null && !proceso.isEmpty()) {
				listaParametros.put("proceso", proceso);
			}
		}
		log.info("tipoGeneracion:*****" + tipoGeneracion);

		if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
			log.info("numeroComprobante:*****" + numeroComprobante);
			listaParametros.put("codSecuencial", "%" + numeroComprobante + "%");
		}
		if (estado != null && !estado.isEmpty()) {
			log.info("estado:*****" + estado);
			listaParametros.put("estado", estado);
		}
		
		if (rucComprador != null && !rucComprador.isEmpty()) {
			log.info("rucComprador:*****" + rucComprador);
			listaParametros.put("rucComprador", rucComprador);
		}
		if (fechaDesde != null) {
			listaParametros.put("fechaDesde", fechaDesde);
		}
		if (fechaHasta != null) {
			listaParametros.put("fechaHasta", fechaHasta);
		}
		
		if (rucEmisor != null && !rucEmisor.isEmpty()) {
			log.info("rucEmisor:*****" + rucEmisor);
			listaParametros.put("rucEmisor", rucEmisor);
		}
		if (emisor != null && !emisor.isEmpty()) {
			log.info("emisor:*****" + emisor);
			listaParametros.put("rucEmisor", emisor);
		}
		if (codigoExterno != null && !codigoExterno.isEmpty()) {
			log.info("codigoExterno:*****" + codigoExterno);
			listaParametros.put("codigoExterno", codigoExterno);
		}
		if(identificadorUsuario != null && !identificadorUsuario.isEmpty()){
		 listaParametros.put("identificadorUsuario", identificadorUsuario);
		}
		if (agencia != null && !agencia.isEmpty()) {
			listaParametros.put("agencia", agencia);
		}
		if (claveContingencia != null && !claveContingencia.isEmpty()) {
			log.info("claveContingencia:*****" + claveContingencia);
			listaParametros.put("claveContingencia", claveContingencia);
		}
		if (tipoEmision != null && !tipoEmision.isEmpty()) {
			log.info("tipoEmision:*****" + tipoEmision);
			listaParametros.put("tipoEmision", tipoEmision);
		}
		if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
			log.info("tipoAmbiente:*****" + tipoAmbiente);
			listaParametros.put("tipoAmbiente", tipoAmbiente);
		}
		if (correoProveedor != null && !correoProveedor.isEmpty()) {
			listaParametros.put("correoProveedor", correoProveedor);
		}
		if(razonSocialComprador != null && !razonSocialComprador.isEmpty()){
			listaParametros.put("razonSocialComprador", razonSocialComprador);
		}

		if (tipoEjecucion != null) {
			log.info("tipoEjecucion:*****" + tipoEjecucion);
			listaParametros.put("tipoEjecucion", tipoEjecucion);
		}

	}

	public void buscarSeguimiento() {
		try {
			log.info("Buscando Seguimiento***********");
		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Facturas");
		}
	}

	public void reprocesarFactura() {

		log.info("**anviando a procesar 111***" + facturaRecepcion.getArchivo());
		Factura_Service serviceFactura = new Factura_Service();
		FacturaPortType portFactura = serviceFactura.getFacturaPort();

		FacturaRecibirRequest facturaRecibirRequest = new FacturaRecibirRequest();
		if (facturaRecepcion.getArchivo() != null) {
			facturaRecibirRequest.setComprobanteProveedor(facturaRecepcion.getArchivo());
		}
		if (facturaRecepcion.getArchivoLegible() != null) {
			facturaRecibirRequest.setComprobanteProveedorPDF(facturaRecepcion
					.getArchivoLegible());
		}
		facturaRecibirRequest.setProceso(RimEnum.REIM.name());
		if (ordenCompra != null && !ordenCompra.isEmpty()) {
			facturaRecibirRequest.setOrdenCompra(ordenCompra);
		}
		log.info("**anviando a procesar***");

		facturaRecibirRequest.setMailOrigen(facturaRecepcion.getCorreoNotificacion());

		FacturaRecibirResponse respuestaFactura = portFactura
				.recibir(facturaRecibirRequest);
		
		log.info("**recibiendo respuesta***"+respuestaFactura.getMensajeCliente());

		if (respuestaFactura != null && !respuestaFactura.getEstado().isEmpty() && respuestaFactura.getEstado().equalsIgnoreCase("COMPLETADO")) {
			infoMessage("Proceso Realizado con exito");
		} else {
			infoMessage("Error al procesar la factura - respuestaFactura "+respuestaFactura);
			facturaRecepcion.setMensajeErrorReim(respuestaFactura.getMensajeSistema());
			try {
				servicioRecepcionFactura.recibirFactura(facturaRecepcion,"");
			} catch (GizloException e) {
				e.printStackTrace();
			}
		}

		System.out.println(respuestaFactura.getMensajeSistema() + " "
				+ respuestaFactura.getMensajeCliente());
	}

	private Autorizacion autorizarComprobante(String wsdlLocation,
			String claveAcceso) {
		Autorizacion respuesta = null;

		// log.debug("CLAVE DE ACCESO: " + claveAcceso);
		int timeout = 7000;

		try {

			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(
					new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion","AutorizacionComprobantesService"));

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

	@PostConstruct
	public void postContruct() {
		log.info("postContruction");
		try {
			fechaDesde = Calendar.getInstance().getTime();
//			rucEmisor = sessionBean.getRucOrganizacion() != null ? sessionBean
//					.getRucOrganizacion() : null;
			facturas = new ArrayList<Factura>();

			rucComprador = null;
			identificadorUsuario = null;
			if(sessionBean == null){
				return;
			}
			log.info("postContruction 222");
			List<UsuarioRol> usuarioRoles = sessionBean.getUsuario()
					.getUsuariosRoles();
			if (usuarioRoles != null && !usuarioRoles.isEmpty()) {
				log.info("postContruction 333");
				for (UsuarioRol usuRol : usuarioRoles) {
					if (usuRol.getRol().getCodigo().equals("CNABF") ) {
						rucComprador = sessionBean.getUsuario().getPersona()
								.getIdentificacion();
						
						log.info("****postContruction rucComprador"+rucComprador);
						identificadorUsuario = sessionBean.getUsuario()
								.getPersona().getIdentificacion();
						fechaDesde = null;
					}
					if(usuRol.getRol().getCodigo().equals("PROVE")){
						rucEmisor = sessionBean.getUsuario().getPersona()
								.getIdentificacion();
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
			if (facturas != null && !facturas.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Factura");

				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}

				ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH, map,
						REPORT_XLS_PATH, facturas);
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarXLSReim() {
		obtenerListaReim();
		log.info("descargando rein excel");
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			log.info("descargando rein excel 2" + facturaRecepcions.size());
			if (facturaRecepcions != null && !facturaRecepcions.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Factura");

				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}

				ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH_REIN, map,
						REPORT_XLS_PATH, facturaRecepcions);
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

	private void obtenerListaReim() {
		try {
			facturaRecepcions = servicioRecepcionFactura.consultarCommprobantesRecibidosReim(
					numeroComprobante, fechaDesde, fechaHasta, estado,null);
		} catch (GizloException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarPDFReim() {
		obtenerListaReim();
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (facturaRecepcions != null && !facturaRecepcions.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Factura");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH_REIN, map,
						REPORT_PDF_PATH, facturaRecepcions);
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
			if (facturas != null && !facturas.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Factura");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH, map,
						REPORT_PDF_PATH, facturas);
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

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
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

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
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

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public String getOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(String ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	public Map<String, Object> getListaParametros() {
		return listaParametros;
	}

	public void setListaParametros(Map<String, Object> listaParametros) {
		this.listaParametros = listaParametros;
	}

	public Object getObjFactura() {
		return objFactura;
	}

	public void setObjFactura(Object objFactura) {
		this.objFactura = objFactura;
	}

	public boolean isBuscarSegumiento() {
		return buscarSegumiento;
	}

	public void setBuscarSegumiento(boolean buscarSegumiento) {
		this.buscarSegumiento = buscarSegumiento;
	}

	public void cargarDocumento(org.richfaces.event.FileUploadEvent event) {
		org.richfaces.model.UploadedFile item = event.getUploadedFile();
		data = item.getData();
		extension = item.getFileExtension();
		System.out.println(data+"**"+extension);
		 
	}
	
	@SuppressWarnings({ "rawtypes", "resource" })
	public static void readXLSXFile(InputStream is) throws IOException {
//		InputStream ExcelFileToRead = new FileInputStream();
	

		org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(is);

		org.apache.poi.xssf.usermodel.XSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.xssf.usermodel.XSSFRow row;
		org.apache.poi.xssf.usermodel.XSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (org.apache.poi.xssf.usermodel.XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (org.apache.poi.xssf.usermodel.XSSFCell) cells.next();

				if (cell.getCellType() == org.apache.poi.xssf.usermodel.XSSFCell.CELL_TYPE_STRING) {
					System.out.print(cell.getStringCellValue() + " ");
				} 
			}
			System.out.println();
		}

	}

	@SuppressWarnings({ "rawtypes", "resource" })
	public static void readXLSFile() throws IOException {
		InputStream ExcelFileToRead = new FileInputStream("C:\\Users\\Usuario\\Desktop\\Libro1.xls");
		org.apache.poi.hssf.usermodel.HSSFWorkbook wb = new org.apache.poi.hssf.usermodel.HSSFWorkbook(ExcelFileToRead);

		org.apache.poi.hssf.usermodel.HSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.hssf.usermodel.HSSFRow row;
		org.apache.poi.hssf.usermodel.HSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (org.apache.poi.hssf.usermodel.HSSFRow) rows.next();
			Iterator cells = row.cellIterator();

			while (cells.hasNext()) {
				cell = (org.apache.poi.hssf.usermodel.HSSFCell) cells.next();
				
				if (cell.getCellType() == org.apache.poi.hssf.usermodel.HSSFCell.CELL_TYPE_STRING) {
					System.out.print(cell.getStringCellValue() + " ");
				} 
			}
			System.out.println();
		}

	}
	
	
	public FacturaRecepcion getFacturaRecepcion() {
		return facturaRecepcion;
	}

	public void setFacturaRecepcion(FacturaRecepcion facturaRecepcion) {
		this.facturaRecepcion = facturaRecepcion;
	}
 
	 
	
	
	
	public String getRazonSocialComprador() {
		return razonSocialComprador;
	}

	public void setRazonSocialComprador(String razonSocialComprador) {
		this.razonSocialComprador = razonSocialComprador;
	}

	public static void main(String[] args) throws Exception {
				String directorioZip = "/home/jboss/app/gnvoice/recursos/comprobantesfactura/autorizado/27012016/";
		    	File carpetaComprimir = new File(directorioZip);
		 
			
				if (carpetaComprimir.exists()) {
					File[] ficheros = carpetaComprimir.listFiles();
					System.out.println("Número de ficheros encontrados: " + ficheros.length);
		 
					for (int i = 0; i < ficheros.length; i++) {
						System.out.println("Nombre del fichero: " + ficheros[i].getName());
						String extension="";
						for (int j = 0; j < ficheros[i].getName().length(); j++) {
							if (ficheros[i].getName().charAt(j)=='.') {
								extension=ficheros[i].getName().substring(j, (int)ficheros[i].getName().length());
							}
						}
						try {
							ZipOutputStream zous = new ZipOutputStream(new FileOutputStream(directorioZip + ficheros[i].getName().replace(extension, ".zip")));
							
							ZipEntry entrada = new ZipEntry(ficheros[i].getName());
							zous.putNextEntry(entrada);
							
								System.out.println("Comprimiendo.....");
			 					FileInputStream fis = new FileInputStream(directorioZip+entrada.getName());
								int leer;
								byte[] buffer = new byte[1024];
								while (0 < (leer = fis.read(buffer))) {
									zous.write(buffer, 0, leer);
								}
								fis.close();
								zous.closeEntry();
							zous.close();					
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}				
					}
					System.out.println("Directorio de salida: " + directorioZip);
				} else {
					System.out.println("No se encontró el directorio..");
				}
			}

	public Boolean getSelectUne() {
		return selectUne;
	}

	public void setSelectUne(Boolean selectUne) {
		this.selectUne = selectUne;
	}

	public List<Factura> getListFacturas() {
		return listFacturas;
	}

	public void setListFacturas(List<Factura> listFacturas) {
		this.listFacturas = listFacturas;
	}
	
	
	    
	
	 
	
	 
	
}
