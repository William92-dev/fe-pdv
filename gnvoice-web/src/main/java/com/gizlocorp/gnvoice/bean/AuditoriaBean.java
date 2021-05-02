package com.gizlocorp.gnvoice.bean;

import java.io.FileInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.servicio.local.ServicioAuditoriaLocal;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.gnvoice.bean.databean.AuditoriaDataBean;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("auditoriaBean")
public class AuditoriaBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(AuditoriaBean.class.getName());

	@Inject
	private AuditoriaDataBean auditoriaDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioAuditoriaImpl!com.gizlocorp.adm.servicio.local.ServicioAuditoriaLocal")
	ServicioAuditoriaLocal servicioAuditoriaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	private static final String REPORTE_CLAVES_PATH = "/reportes/listaAuditorias.jasper";
	private static final String REPORT_XLS_PATH = "listaAuditoria.xls";
	private static final String REPORT_PDF_PATH = "listaAuditoria.pdf";

	

	@Inject
	private SessionBean sessionBean;

	@PostConstruct
	public void postContruct() {

		

	}

	public void consultarAuditorias() {
		try {

			log.debug("Entidad: "
					+ (auditoriaDataBean.getEntidad() != null ? auditoriaDataBean
							.getEntidad().getClazz()
							+ " "
							+ auditoriaDataBean.getEntidad().getDescripcion()
							: "No clazz"));
			auditoriaDataBean.setListaAuditorias(servicioAuditoriaLocal
					.obtenerInforAuditoria(auditoriaDataBean.getEntidad(),
							auditoriaDataBean.getFechaDesde(),
							auditoriaDataBean.getFechaHasta(),
							auditoriaDataBean.getOperacion()));

		} catch (GizloException e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al Listar Auditorias");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarXLS() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (auditoriaDataBean.getListaAuditorias() != null
					&& !auditoriaDataBean.getListaAuditorias().isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
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

				ReportUtil
						.exportarReporteXLS(REPORTE_CLAVES_PATH, map,
								REPORT_XLS_PATH,
								auditoriaDataBean.getListaAuditorias());

				
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
			if (auditoriaDataBean.getListaAuditorias() != null
					&& !auditoriaDataBean.getListaAuditorias().isEmpty()) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
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

				ReportUtil
						.exportarReportePDF(REPORTE_CLAVES_PATH, map,
								REPORT_PDF_PATH,
								auditoriaDataBean.getListaAuditorias());

				
			}

		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

}
