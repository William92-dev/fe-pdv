package com.gizlocorp.gnvoice.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.pdfbox.util.PDFMergerUtility;

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
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.modelo.GuiaRemision;
import com.gizlocorp.gnvoice.reporte.GuiaRemisionReporte;
import com.gizlocorp.gnvoice.servicio.local.ServicioGuia;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("guiaBean")
public class GuiaBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(GuiaBean.class.getName());

	private String numeroComprobante;

	private String codigoExterno;

	private Date fechaDesde;

	private Date fechaHasta;

	private List<GuiaRemision> guias;

	private GuiaRemision guia;

	private String estado = null;

	@Inject
	private SessionBean sessionBean;

	private String rucEmisor;

	private String rucTransportista;

	private String identificadorUsuario;

	private String numeroRelacionado;

	private String agencia;

	private String emisor;

	private String claveContingencia;

	private String tipoEmision;

	private String tipoAmbiente;

	private String correoProveedor;

	private TipoEjecucion tipoEjecucion;

	private static final String REPORTE_CLAVES_PATH = "/reportes/listaGuiaRemision.jasper";
	private static final String REPORT_XLS_PATH = "listaGuiaRemision.xls";
	private static final String REPORT_PDF_PATH = "listaGuiaRemision.PDF";

	private Converter converter;

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;


	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioGuiaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioGuia")
	ServicioGuia servicioGuia;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	public void descargarComprobantePDF() {
		try {
			String archivo = guia.getArchivoLegible();
			if (archivo == null || archivo.isEmpty() || archivo.contains("null.pdf") || !(new File(archivo).exists())) {
				archivo = guia.getArchivo().replace(".xml", ".pdf");

				if (archivo == null || archivo.isEmpty() || !(new File(archivo).exists())) {

					String archivoXML = guia.getArchivo();

					String autorizacion = DocumentUtil.readContentFile(archivoXML);

					this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
					Autorizacion autorizacionXML = this.converter.convertirAObjeto(autorizacion, Autorizacion.class);

					String guia = autorizacionXML.getComprobante();
					guia = guia.replace("<![CDATA[", "");
					guia = guia.replace("]]>", "");

					com.gizlocorp.gnvoice.xml.guia.GuiaRemision guiaXML = this.converter.convertirAObjeto(
									guia,
									com.gizlocorp.gnvoice.xml.guia.GuiaRemision.class);

					List<Organizacion> emisores = servicioOrganizacionLocal
							.listarOrganizaciones(null, null, guiaXML
									.getInfoTributaria().getRuc(), null);

					Parametro dirParametro = servicioParametro
							.consultarParametro(Constantes.DIRECTORIO_SERVIDOR,
									emisores.get(0).getId());
					String dirServidor = dirParametro != null ? dirParametro
							.getValor() : "";

					archivo = ReporteUtil.generarReporte(
							Constantes.REP_GUIA_REMISION,
							new GuiaRemisionReporte(guiaXML), autorizacionXML
									.getNumeroAutorizacion(), FechaUtil
									.formatearFecha(autorizacionXML
											.getFechaAutorizacion()
											.toGregorianCalendar().getTime(),
											FechaUtil.patronFechaTiempo24),
							guiaXML, guiaXML.getInfoGuiaRemision()
									.getFechaIniTransporte(), "autorizado",
							false, emisores.get(0).getLogoEmpresa(),
							dirServidor);

				}
			}

			if (archivo != null && new File(archivo).exists()) {
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) context
						.getExternalContext().getResponse();
				OutputStream out = response.getOutputStream();

				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition",
						"attachment; filename=guia.pdf");

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
			String archivo = guia.getArchivo();

			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();
			OutputStream out = response.getOutputStream();

			response.setContentType("application/xml");
			response.addHeader("Content-Disposition",
					"attachment; filename=guia.xml");

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

	public void buscarGuia() {
		try {
			guias = servicioGuia.consultarCommprobantes(numeroComprobante,
					fechaDesde, fechaHasta, rucTransportista, codigoExterno,
					identificadorUsuario, numeroRelacionado);
		} catch (Exception e) {
			log.debug("Ha ocurrido un error al listar guias", e);
			errorMessage("Ha ocurrido un error al listar Guias");
		}
	}

	public void buscarGuiaRecibidos() {
		try {
			guias = servicioGuia.consultarCommprobantesRecibidos(
					numeroComprobante, fechaDesde, fechaHasta, rucEmisor);
		} catch (Exception e) {
			log.debug("Ha ocurrido un error al listar guias", e);
			errorMessage("Ha ocurrido un error al listar Guias");
		}
	}

	public void rideUnificado() {
		try {
			if (guias != null && !guias.isEmpty()) {
				PDFMergerUtility ut = new PDFMergerUtility();
				boolean existeDocumento = false;
				for (GuiaRemision factura : guias) {
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
							+ "/gnvoice/recursos/reportes/comprobantes/rideunificado/guiaRemision"
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

	public void buscarGuiaSeguimiento() {
		try {
			guias = servicioGuia.consultarComprobantesSeguimiento(estado,
					numeroComprobante, fechaDesde, fechaHasta, rucEmisor,
					codigoExterno, rucTransportista, identificadorUsuario,
					numeroRelacionado, agencia, claveContingencia, tipoEmision,
					tipoAmbiente, correoProveedor, tipoEjecucion);

			log.debug("guias" + guias);
		} catch (Exception e) {
			log.debug("Ha ocurrido un error al listar guias", e);
			errorMessage("Ha ocurrido un error al listar guias");
		}
	}

	public void buscarSeguimiento() {
		try {

//			guiaRemisions = servicioGuia.consultarSeguimiento(guia);

			log.debug("factura" + guia);
		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Facturas");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarXLS() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (guias != null && !guias.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Guia");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH, map,
						REPORT_XLS_PATH, guias);
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
			if (guias != null && !guias.isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				map.put("USUARIO", sessionBean.getUsuario().getPersona()
						.getNombreCompleto());
				map.put("FECHA",
						format.format(Calendar.getInstance().getTime()));
				map.put("TITULO", "Reporte Guia");
				try {
					map.put("LOGO",
							new FileInputStream(sessionBean
									.getLogoOrganizacion()));
				} catch (Exception ex) {
					log.debug("Error descargarXLS: " + ex.getMessage(), ex);
				}
				ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH, map,
						REPORT_PDF_PATH, guias);
			}
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
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
			guias = new ArrayList<GuiaRemision>();

			rucTransportista = null;
			identificadorUsuario = null;
			List<UsuarioRol> usuarioRoles = sessionBean.getUsuario()
					.getUsuariosRoles();
			if (usuarioRoles != null && !usuarioRoles.isEmpty()) {
				for (UsuarioRol usuRol : usuarioRoles) {
					if (usuRol.getRol().getCodigo().equals("CONSU")) {
						rucTransportista = sessionBean.getUsuario()
								.getPersona().getIdentificacion();
						identificadorUsuario = sessionBean.getUsuario()
								.getPersona().getIdentificacion();
					}
				}
			}

			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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

	public List<GuiaRemision> getGuias() {
		return guias;
	}

	public void setGuias(List<GuiaRemision> guias) {
		this.guias = guias;
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

	

	public GuiaRemision getGuia() {
		return guia;
	}

	public void setGuia(GuiaRemision guia) {
		this.guia = guia;
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

	public String getRucTransportista() {
		return rucTransportista;
	}

	public void setRucTransportista(String rucTransportista) {
		this.rucTransportista = rucTransportista;
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

}