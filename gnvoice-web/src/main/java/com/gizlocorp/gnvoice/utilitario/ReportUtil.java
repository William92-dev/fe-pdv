package com.gizlocorp.gnvoice.utilitario;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

public class ReportUtil {

	private static ReportUtil instance;

	/**
	 * Gets the single instance of Util.
	 * 
	 * @return single instance of Util
	 */
	public static ReportUtil getInstance() {
		if (instance == null) {
			instance = new ReportUtil();
		}
		return instance;
	}

	public static void exportarReportePDF(String reportUrl,
			Map<String, Object> parameters, String outputFileName, List<?> datos)
			throws Exception {

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();

		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ outputFileName);
		final ServletOutputStream ouputStream = response.getOutputStream();

		InputStream reportStream = getInstance().getClass().getClassLoader()
				.getResourceAsStream(reportUrl);
		JRBeanCollectionDataSource cds = new JRBeanCollectionDataSource(datos);
		JasperPrint reportJasperPrint = JasperFillManager.fillReport(
				reportStream, parameters, cds);
		JasperExportManager.exportReportToPdfStream(reportJasperPrint,
				response.getOutputStream());

		ouputStream.flush();
		ouputStream.close();
		
		context.responseComplete();
	}

	public static void exportarReporteXLS(String reportUrl,
			Map<String, Object> parameters, String outputFileName, List<?> datos)
			throws Exception {

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "attachment; filename="+ outputFileName);
		InputStream reportStream = getInstance().getClass().getClassLoader().getResourceAsStream(reportUrl);
		JRBeanCollectionDataSource cds = new JRBeanCollectionDataSource(datos);
		JasperPrint reportJasperPrint = JasperFillManager.fillReport(reportStream, parameters, cds);
		JRXlsExporter export = new JRXlsExporter();
		final ServletOutputStream ouputStream = response.getOutputStream();

		export.setParameter(JRXlsExporterParameter.JASPER_PRINT,reportJasperPrint);
		export.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, ouputStream);

		export.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,Boolean.TRUE);
		export.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,Boolean.FALSE);
		export.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED,Boolean.TRUE);
		export.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,Boolean.TRUE);
		export.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,Boolean.TRUE);
		export.exportReport();

		ouputStream.flush();
		ouputStream.close();
		
		context.responseComplete();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		HashMap map = new HashMap();
		map.put("iniciadorNombre", "abc");
		map.put("numRegistro", "123456789");
		List list = new ArrayList();
		try {
			ReportUtil.exportarReportePDF("C:\\Users\\Usuario\\Desktop\\RegistroLicencia.jasper", map, "listafactura.pdf", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
