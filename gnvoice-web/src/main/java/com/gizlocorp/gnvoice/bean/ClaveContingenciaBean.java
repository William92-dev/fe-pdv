package com.gizlocorp.gnvoice.bean;

import java.io.FileInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.gnvoice.bean.databean.ClaveContingenciaDataBean;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
import com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserGnvoiceProvider.class)
@ViewScoped
@Named("claveContingBean")
public class ClaveContingenciaBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(ClaveContingenciaBean.class
			.getName());

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioClaveContigenciaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia")
	ServicioClaveContigencia servicioClaveContigencia;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;


	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	SessionBean sessionBean;

	// private static final String REPORTE_CLAVES_PATH =
	// "/reportes/rp_claves_contingencia.jasper";// jrxml
	private static final String REPORTE_CLAVES_PATH = "/reportes/rp_claves_contingencia.jasper";
	private static final String REPORT_PDF_PATH = "rp_claves_contingencia.pdf";
	private static final String REPORT_XLS_PATH = "rp_claves_contingencia.xls";

	private static final String SUB_REPORTS_PATH = "/reportes/";

	@Inject
	private ApplicationBean appBean;
	private ClaveContingencia claveContingencia;
	private Organizacion organizacion;
	private Long idOrganizacion;
	private Logico estado;
	private TipoAmbienteEnum tipoAmbiente;

	private List<Organizacion> organizaciones;

	@Inject
	private ClaveContingenciaDataBean claveContingenciaDataBean;

	public String buscarClaves() {
		try {
			log.debug("Listando claves por: " + idOrganizacion + " "
					+ tipoAmbiente + " " + estado);
			List<ClaveContingencia> claves = servicioClaveContigencia
					.listarClaves(idOrganizacion, tipoAmbiente, estado);

			if (claves == null) {
				claves = new ArrayList<ClaveContingencia>();
			}

			if (claves != null && !claves.isEmpty()) {
				for (ClaveContingencia clave : claves) {
					try {
						if (clave.getIdOrganizacion() != null) {
							// Organizacion org =
							// servicioOrganizacionLocal.consultarOrganizacion("1792366259001");
							clave.setOrganizacionObj(servicioOrganizacionLocal
									.consultarOrganizacionID(clave
											.getIdOrganizacion()));
						}
					} catch (Exception e) {
						// throw new GizloException("Organizacion invalida!",e);
					}
				}
			}

			claveContingenciaDataBean.setClaves(claves);

			

		} catch (GizloException e) {
			e.printStackTrace();
			errorMessage("Ha ocurrido un error al listar ClaveContingenciaes");
			log.debug(e.getMessage());
		}

		return null;
	}

	@PostConstruct
	public void postContruct() {
		try {
			List<Organizacion> organizaciones = servicioOrganizacionLocal
					.listarActivas();
			log.debug("organizaciones: " + organizaciones);
			if (organizaciones == null) {
				organizaciones = new ArrayList<Organizacion>();
			}
			this.setOrganizaciones(organizaciones);
			claveContingenciaDataBean
					.setClaves(new ArrayList<ClaveContingencia>());
			claveContingenciaDataBean.setDataArchivo(null);
			claveContingenciaDataBean.setContador(0);

			
		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar ClaveContingenciaes");
			log.debug(e.getMessage());
		}
	}

	public String guardarClaveContingencia() {
		try {
			// GUARDA
			/*
			 * if (claveContingenciaDataBean.getOpcion() == 0) {
			 * 
			 * servicioClaveContigencia.ingresarClaveContigencia(claveContingencia
			 * ); log.debug("guardando ");
			 * infoMessage("Ingresado correctamente!"
			 * ,"Ingresado correctamente!");
			 * 
			 * } else { log.debug("editando ");
			 * infoMessage("Actualizado correctamente!"
			 * ,"Actualizado correctamente!"); }
			 */
			if (claveContingenciaDataBean.getArchivoClave() == null) {
				errorMessage("Archivo es obligatorio", "Archivo es obligatorio");
				// throw new
				// Exception("Ha ocurrido un error al cargar archivo");
				return null;
			}
			if (claveContingenciaDataBean.getDataArchivo() == null) {
				throw new Exception("Ha ocurrido un error al cargar archivo");
			}
			String resultado = new String(
					claveContingenciaDataBean.getDataArchivo());
			String[] registros = resultado.split("\n");
			int contador = 0;
			if (registros != null) {
				for (int i = 1; i < registros.length; i++) {
					if (registros[i] != null && !registros[i].isEmpty()
							&& registros[i].length() != 38) {
						errorMessage("Error en contenido de archivo",
								"Error en contenido de archivo");
					}
					try {
						if (registros[i] != null && registros[i] != "") {
							log.debug("insertando clave "
									+ registros[i]
									+ " "
									+ claveContingenciaDataBean
											.getClaveContingencia()
											.getIdOrganizacion()
									+ " "
									+ claveContingenciaDataBean
											.getClaveContingencia()
											.getTipoAmbienteObj());
							ClaveContingencia clave = new ClaveContingencia();
							clave.setClave(registros[i]);
							clave.setIdOrganizacion(claveContingenciaDataBean
									.getClaveContingencia().getIdOrganizacion());
							clave.setTipoAmbiente(claveContingenciaDataBean
									.getClaveContingencia()
									.getTipoAmbienteObj() != null ? claveContingenciaDataBean
									.getClaveContingencia()
									.getTipoAmbienteObj().getCode()
									: null);
							// clave.setUsada(this.estado);
							servicioClaveContigencia
									.ingresarClaveContigencia(clave);
							contador++;
						} else {
							log.error("Clave Contingencia Nula");
						}
					} catch (Exception e) {
						log.error("Error en clave de contingencia: ", e);
					}

				}
				claveContingenciaDataBean.setContador(contador);
				claveContingenciaDataBean
						.setClaveContingencia(new ClaveContingencia());
				claveContingenciaDataBean.setArchivoClave("");
				infoMessage("Se cargaron " + contador
						+ " claves de contigencias");
			}

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al cargar claves ",
					"Ha ocurrido un error al cargar claves");
			log.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Redirecciona a formularioClaveContingencia
	 * 
	 * @return
	 */
	public String verClaveContingencia() {
		try {
			return "formularioClaveContingencia";
		} catch (Exception ex) {
			errorMessage("Error al cargar claves", "Error al cargar claves!");
			log.error(ex.getMessage());
			return null;
		}
	}

	public String agregarClaveContingencia() {
		claveContingenciaDataBean.setClaveContingencia(new ClaveContingencia());
		return "formularioClaveContingencia";
	}

	public String agregarClaveContingenciaEm() {
		claveContingenciaDataBean.setClaveContingencia(new ClaveContingencia());
		return "formularioClaveContingencia_em";
	}

	public void cargarDocumentoEm(org.richfaces.event.FileUploadEvent event) {
		try {
			org.richfaces.model.UploadedFile item = event.getUploadedFile();

			String filename = item.getName();
			log.debug(filename);
			byte[] bytes = item.getData();
			String archivoTexto = DocumentUtil
					.createDocumentText(
							bytes,
							filename,
							(claveContingenciaDataBean.getArchivoClave() != null && !claveContingenciaDataBean
									.getArchivoClave().isEmpty()) ? claveContingenciaDataBean
									.getArchivoClave() : "clave_"
									+ FechaUtil.formatearFecha(Calendar
											.getInstance().getTime(),
											FechaUtil.CUSTOM_FORMAT), "clave",
							sessionBean.getDirectorioServidor());

			claveContingenciaDataBean.setArchivoClave(archivoTexto);
			claveContingenciaDataBean.setDataArchivo(bytes);

		} catch (Exception ex) {
			errorMessage("Ha ocurrido un error al cargar el documento",
					"Ha ocurrido un error al cargar el documento");
			log.error("Error cargarDocumento: " + ex.getMessage(), ex);
		}

	}

	public void descargarPDF() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SUBREPORT_DIR", SUB_REPORTS_PATH);

		buscarClaves();
		try {
			List<ClaveContingencia> resultados = new ArrayList<>();
			ClaveContingencia resultado = new ClaveContingencia();
			resultado.setClaves(claveContingenciaDataBean.getClaves());
			resultados.add(resultado);

			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			map.put("USUARIO", sessionBean.getUsuario().getPersona()
					.getNombreCompleto());
			map.put("FECHA", format.format(Calendar.getInstance().getTime()));
			map.put("TITULO", "Reporte Clave Contigencia");

			try {
				map.put("LOGO",
						new FileInputStream(sessionBean.getLogoOrganizacion()));
			} catch (Exception ex) {
				log.debug("Error descargarXLS: " + ex.getMessage(), ex);
			}

			ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH, map,
					REPORT_PDF_PATH, resultados);
		} catch (Exception e) {
			errorMessage("Error descargarPDF", "Error descargarPDF");
			log.error("Error descargarPDF: " + e.getMessage(), e);
		}
	}

	public void descargarXLS() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("iniciadorNombre", "");
		map.put("SUBREPORT_DIR", SUB_REPORTS_PATH);

		buscarClaves();
		try {
			List<ClaveContingencia> resultados = new ArrayList<>();
			ClaveContingencia resultado = new ClaveContingencia();
			resultado.setClaves(claveContingenciaDataBean.getClaves());
			resultados.add(resultado);

			// List claves = Arrays.asList();
			// Generamos el informe

			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			map.put("USUARIO", sessionBean.getUsuario().getPersona()
					.getNombreCompleto());
			map.put("FECHA", format.format(Calendar.getInstance().getTime()));
			map.put("TITULO", "Reporte Factura");
			try {
				map.put("LOGO",
						new FileInputStream(sessionBean.getLogoOrganizacion()));
			} catch (Exception ex) {
				log.debug("Error descargarXLS: " + ex.getMessage(), ex);
			}
			ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH, map,
					REPORT_XLS_PATH, resultados);
		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

	public String regresar() {
		return "listaClaveContingencia";
	}

	public String regresarEm() {
		return "listaClaveContingencia_em";
	}

	public ClaveContingencia getClaveContingencia() {
		return claveContingencia;
	}

	public void setClaveContingencia(ClaveContingencia claveContingencia) {
		this.claveContingencia = claveContingencia;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

	public Long getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public Logico getEstado() {
		return estado;
	}

	public void setEstado(Logico estado) {
		this.estado = estado;
	}

	public TipoAmbienteEnum getTipoAmbiente() {
		return tipoAmbiente;
	}

	public void setTipoAmbiente(TipoAmbienteEnum tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public List<Organizacion> getOrganizaciones() {
		return organizaciones;
	}

	public void setOrganizaciones(List<Organizacion> organizaciones) {
		this.organizaciones = organizaciones;
	}

	public ApplicationBean getAppBean() {
		return appBean;
	}

	public void setAppBean(ApplicationBean appBean) {
		this.appBean = appBean;
	}

}