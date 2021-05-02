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
import com.gizlocorp.adm.servicio.local.ServicioBitacoraEventoLocal;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.gnvoice.bean.databean.BitacoraDataBean;
import com.gizlocorp.gnvoice.utilitario.ReportUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("bitacoraBean")
public class BitacoraBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(BitacoraBean.class.getName());

	@Inject
	private BitacoraDataBean bitacoraDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioBitacoraEventoImpl!com.gizlocorp.adm.servicio.local.ServicioBitacoraEventoLocal")
	ServicioBitacoraEventoLocal servicioBitacoraEvento;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	private SessionBean sessionBean;

	private static final String REPORTE_CLAVES_PATH = "/reportes/listabitacora.jasper";
	private static final String REPORT_XLS_PATH = "listaBitacora.xls";
	private static final String REPORT_PDF_PATH = "listaBitacora.pdf";

	@PostConstruct
	public void postContruct() {
		
	}

	public void consultarBitacoraEvento() {
		try {
			bitacoraDataBean.setListBitacoraEventos(servicioBitacoraEvento
					.obtenerEventosPorParametros(
							bitacoraDataBean.getIdEvento(),
							bitacoraDataBean.getUsuario(),
							bitacoraDataBean.getFechaDesde(),
							bitacoraDataBean.getFechaHasta(),
							bitacoraDataBean.getDetalle()));

		} catch (GizloException e) {
			log.error("Error" + e.getMessage(), e);
			errorMessage("Ha ocurrido un error al Listar Bitacora Evento");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void descargarXLS() {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		try {
			if (bitacoraDataBean.getListBitacoraEventos() != null
					&& !bitacoraDataBean.getListBitacoraEventos().isEmpty()) {
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

				ReportUtil.exportarReporteXLS(REPORTE_CLAVES_PATH, map,
						REPORT_XLS_PATH,
						bitacoraDataBean.getListBitacoraEventos());

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
			if (bitacoraDataBean.getListBitacoraEventos() != null
					&& !bitacoraDataBean.getListBitacoraEventos().isEmpty()) {
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

				ReportUtil.exportarReportePDF(REPORTE_CLAVES_PATH, map,
						REPORT_PDF_PATH,
						bitacoraDataBean.getListBitacoraEventos());

			}

		} catch (Exception e) {
			errorMessage("Error descargarXLS", "Error descargarXLS");
			log.error("Error descargarXLS: " + e.getMessage(), e);
		}
	}

}
