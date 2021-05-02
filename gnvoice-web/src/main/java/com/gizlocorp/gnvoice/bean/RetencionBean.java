package com.gizlocorp.gnvoice.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
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

import oracle.sql.BLOB;

import org.apache.log4j.Logger;
import org.apache.pdfbox.util.PDFMergerUtility;

import com.gizlocorp.adm.enumeracion.SistemaExternoEnum;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.json.model.CredencialDS;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.Retencion;
import com.gizlocorp.gnvoice.reporte.ComprobanteRetencionReporte;
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.servicio.local.ServicioRetencion;
import com.gizlocorp.gnvoice.utilitario.Conexion;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;
import com.gizlocorp.gnvoice.xml.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("retencionBean")
public class RetencionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(RetencionBean.class.getName());

	private String numeroComprobante;

	private String codigoExterno;

	private Date fechaDesde;

	private Date fechaHasta;

	private List<Retencion> retencions;
	private Retencion retencion;

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
	
	private Retencion retencionObj;

	private static final String REPORTE_CLAVES_PATH = "/reportes/listaRetencion.jasper";
	private static final String REPORT_XLS_PATH = "listaRetencion.xls";
	private static final String REPORT_PDF_PATH = "listaRetencion.pdf";
	
	private Converter converter;
	
	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRetencionImpl!com.gizlocorp.gnvoice.servicio.local.ServicioRetencion")
	ServicioRetencion servicioRetencion;

	

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;
	
	@EJB(lookup ="java:global/gnvoice-ejb/ComprobanteRegeneracionDAOImpl!com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO")
	ComprobanteRegeneracionDAO comprobanteRegeneracionDAO;
	

	public void buscarRetencion() {
		try {
			retencions = servicioRetencion.consultarCommprobantes(
					numeroComprobante, fechaDesde, fechaHasta, rucComprador,
					codigoExterno, identificadorUsuario, numeroRelacionado);
		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Retencions");
		}
	}
	
	
	
	public void descargarComprobantePDF() {
		try {
			retencion = (Retencion) retencionObj;
			String archivo = retencionObj.getArchivoLegible();
			String fecha="";
			String logo=""; 
			
			com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion facturaXML = null;
			facturaXML =  descargarComprobanteJDE(retencion) ;
			fecha = retencion.getFechaAutorizacionStr();
			logo ="/data/gnvoice/recursos/reportes/Blanco.png";
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			Autorizacion autorizacionXML = new Autorizacion();
			autorizacionXML.setClaveAcceso(retencion.getClaveAcceso());;
			autorizacionXML.setEstado(retencion.getEstado());
			autorizacionXML.setNumeroAutorizacion(retencion.getNumeroAutorizacion());
			
			List<Organizacion> emisores = servicioOrganizacionLocal.listarOrganizaciones(null, null, facturaXML.getInfoTributaria().getRuc(), null);
			if(fecha.isEmpty()){
				fecha =FechaUtil.formatearFecha(autorizacionXML.getFechaAutorizacion().toGregorianCalendar().getTime(), FechaUtil.patronFechaTiempo24);
			}
			if(logo.isEmpty()){				
				logo = emisores.get(0).getLogoEmpresa();
			}
					
			archivo = ReporteUtil.generarReporte(
					Constantes.REP_COMP_RETENCION,
					new ComprobanteRetencionReporte(facturaXML),
					autorizacionXML.getNumeroAutorizacion(),
					fecha,
					facturaXML.getInfoCompRetencion().getFechaEmision(),
					"autorizado", false, logo,
					"/data");

			if (archivo != null && new File(archivo).exists()) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition","attachment; filename=factura.pdf");

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
	
	
	
	public com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion descargarComprobanteJDE(Retencion factura) {
		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId(com.gizlocorp.adm.utilitario.Constantes.BASE_JDE);
		credencialDS.setUsuario(com.gizlocorp.adm.utilitario.Constantes.USUARIO_JDE);
		credencialDS.setClave(com.gizlocorp.adm.utilitario.Constantes.CLAVE_JDE);

		Connection conn = null;
		com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion item = new ComprobanteRetencion();

		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
			item = comprobanteRegeneracionDAO.listarRetencionJDE(conn, factura.getClaveAcceso());

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
	
	

	public void descargarComprobanteXML() {
		try {
			String rutaArchivoAutorizacionXml="";

			if (retencionObj.getProceso() != null && !retencionObj.getProceso().isEmpty() && retencionObj.getProceso().equals(SistemaExternoEnum.SISTEMACRPDTA.name())) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();
				try {
					com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion facturaXML = null;
					facturaXML =  descargarComprobanteJDE(retencionObj) ;
					this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
					String comprobante = this.converter.convertirDeObjeto(facturaXML);
					
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					String fechaEmision = df.format(retencionObj.getFechaEmisionDb());
					
					
					
					
					Autorizacion autorizacionXML = new Autorizacion();
					autorizacionXML.setClaveAcceso(retencionObj.getClaveAcceso());;
					autorizacionXML.setEstado(retencionObj.getEstado());
					autorizacionXML.setNumeroAutorizacion(retencionObj.getNumeroAutorizacion());
					
					comprobante = comprobante.split("<comprobanteRetencion>")[1];
					
					comprobante = comprobante.split("</comprobanteRetencion>")[0];
					
					comprobante =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								 + "<autorizacion>"
								 + "<estado>AUTORIZADO</estado>"
								 + "<numeroAutorizacion>"+retencionObj.getNumeroAutorizacion()+"</numeroAutorizacion>"
								 + "<fechaAutorizacion>"+retencionObj.getFechaAutorizacionStr()+"</fechaAutorizacion>"
								 + "<ambiente>PRODUCCION</ambiente>"
								 + "<comprobante><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
								 + "<comprobanteRetencion id=\"comprobante\" version=\"1.1.0\">"
								 + comprobante
								 + "</comprobanteRetencion>"
								 + "]]></comprobante>"
								 + "</autorizacion> ";
					  
					  
					rutaArchivoAutorizacionXml = DocumentUtil.createDocument(comprobante
							 , fechaEmision
							 , retencionObj.getClaveAcceso()
							 , TipoComprobante.RETENCION.getDescripcion()
							 , "autorizado");
					
				} catch (Exception e) {
					log.info(e.getMessage(), e);
				}
				response.setContentType("application/xml");
				response.addHeader("Content-Disposition","attachment; filename=factura.xml");
				File file = new File(rutaArchivoAutorizacionXml);
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
			log.error("Error ride unificado: " + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al descargar el documento");
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

//	public void buscarRetencionRecibidos() {
//		try {
//			retencions = servicioRetencion.consultarCommprobantesRecibidos(
//					numeroComprobante, fechaDesde, fechaHasta, rucEmisor);
//		} catch (Exception e) {
//			errorMessage("Ha ocurrido un error al listar Retencions");
//		}
//	}

	public void rideUnificado() {
		try {
			if (retencions != null && !retencions.isEmpty()) {
				PDFMergerUtility ut = new PDFMergerUtility();
				boolean existeDocumento = false;
				for (Retencion factura : retencions) {
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
							+ "/gnvoice/recursos/reportes/comprobantes/rideunificado/retencion"
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
			retencions = new ArrayList<Retencion>();

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
			log.error("Regstro al cargar modulo", e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarXLS() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (retencions != null && !retencions.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Retencion");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH, map,
						REPORT_XLS_PATH, retencions);
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
			if (retencions != null && !retencions.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Retencion");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH, map,
						REPORT_PDF_PATH, retencions);
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

	public List<Retencion> getRetencions() {
		return retencions;
	}

	public void setRetencions(List<Retencion> retencions) {
		this.retencions = retencions;
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

	

	public Retencion getRetencion() {
		return retencion;
	}

	public void setRetencion(Retencion retencion) {
		this.retencion = retencion;
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

	public Retencion getRetencionObj() {
		return retencionObj;
	}

	public void setRetencionObj(Retencion retencionObj) {
		this.retencionObj = retencionObj;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

}