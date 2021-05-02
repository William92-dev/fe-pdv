package com.gizlocorp.gnvoice.utilitario;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.adm.utilitario.DeleteThread;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.StringUtil;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoImpuestoEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoImpuestoIvaEnum;
import com.gizlocorp.gnvoice.reporte.ComprobanteRetencionReporte;
import com.gizlocorp.gnvoice.reporte.DetallesAdicionalesReporte;
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.reporte.GuiaRemisionReporte;
import com.gizlocorp.gnvoice.reporte.InformacionAdicional;
import com.gizlocorp.gnvoice.reporte.NotaCreditoReporte;
import com.gizlocorp.gnvoice.reporte.NotaDebitoReporte;
import com.gizlocorp.gnvoice.reporte.TotalComprobante;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.factura.Factura;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Pagos.Pago;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision.InfoGuiaRemision;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito.Compensaciones.Compensacion;
import com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos;
import com.gizlocorp.gnvoice.xml.notadebito.Impuesto;
import com.gizlocorp.gnvoice.xml.notadebito.NotaDebito;
import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.modelo.SubsidioNotaCredito;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author gizlo
 */
public class ReporteUtil {
	
	private static Logger log = Logger.getLogger(ReporteUtil.class.getName());

	private String fechaFolder(String fechaEmision) {
		if (fechaEmision.contains("/")) {
			fechaEmision = fechaEmision.replace("/", "");
		} else if (fechaEmision.contains("-")) {
			fechaEmision = fechaEmision.replace("-", "");
		}
		return fechaEmision;
	}

	// METODO RIDE FACTURA
	public String generarReporte(String urlReporte
								, FacturaReporte fact
								, String numAut
								, String fechaAut
								, String fechaEmision
								, String subcarpeta
								, boolean rechazado
								, String logo) throws Exception {

		if (!"autorizado".equals(subcarpeta)) {
			return null;
		}

		FileInputStream is = new FileInputStream(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+ urlReporte);
		JRDataSource dataSource = new JRBeanCollectionDataSource(fact.getDetallesAdiciones());
		JasperPrint print = JasperFillManager.fillReport(is
														, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(fact.getFactura().getInfoTributaria()
														, numAut
														, fechaAut
														, rechazado
														, logo
														, com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS)
														, obtenerInfoFactura(fact.getFactura().getInfoFactura(), fact))
														, dataSource);

		StringBuilder outputFile = new StringBuilder();
		outputFile.append(com.gizlocorp.adm.utilitario.Constantes.DIR_DOCUMENTOS);
		outputFile.append(TipoComprobante.FACTURA.getDescripcion());
		outputFile.append("/");
		outputFile.append(subcarpeta);
		outputFile.append("/");
		outputFile.append(fechaFolder(fechaEmision));
		outputFile.append("/");

		DocumentUtil.createDirectory(outputFile.toString());
		outputFile.append(fact.getFactura().getInfoTributaria().getClaveAcceso());
		outputFile.append(".pdf");

		JasperExportManager.exportReportToPdfFile(print, outputFile.toString());

		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		DeleteThread delete = new DeleteThread(outputFile.toString());
		executor.schedule(delete, 10, TimeUnit.MINUTES);

		return outputFile.toString();

	}
	
	public String generarReporteReim(String urlReporte, FacturaReporte fact,
			String numAut, String fechaAut, String fechaEmision,
			String subcarpeta, boolean rechazado, String logo) throws Exception {
		
//		if (!("autorizado".equals(subcarpeta) || "recibidoManual".equals(subcarpeta))) {
//			return null;
//		}
		

		FileInputStream is = new FileInputStream(
				com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS + urlReporte);
		JRDataSource dataSource = new JRBeanCollectionDataSource(fact.getDetallesAdiciones());
		JasperPrint print = JasperFillManager.fillReport(
						is,
						obtenerMapaParametrosReportes(
								obtenerParametrosInfoTriobutaria(
										fact.getFactura().getInfoTributaria(),
										numAut,
										fechaAut,
										rechazado,
										logo,
										com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS),
								obtenerInfoFactura(fact.getFactura()
										.getInfoFactura(), fact)), dataSource);
		
		StringBuilder outputFile = new StringBuilder();
		// outputFile.append(dirServidor + "/gnvoice/recursos/comprobantes/");
		outputFile
				.append(com.gizlocorp.adm.utilitario.Constantes.DIR_DOCUMENTOS);
		outputFile.append(TipoComprobante.FACTURA.getDescripcion());
		outputFile.append("/");
		outputFile.append(subcarpeta);
		outputFile.append("/");
		outputFile.append(fechaFolder(fechaEmision));
		outputFile.append("/");
		
		

		DocumentUtil.createDirectory(outputFile.toString());

		outputFile.append(fact.getFactura().getInfoTributaria()
				.getClaveAcceso());

		outputFile.append(".pdf");

		JasperExportManager.exportReportToPdfFile(print, outputFile.toString());

//		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
//				1);
//		DeleteThread delete = new DeleteThread(outputFile.toString());
//		executor.schedule(delete, 10, TimeUnit.MINUTES);

		return outputFile.toString();

	}
	
	public String generarReporteCreditoReim(String urlReporte, NotaCreditoReporte rep, String numAut, String fechaAut, String fechaEmision, String subcarpeta, boolean rechazado, String logo)
		    throws Exception
		  {
		    FileInputStream is = new FileInputStream(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS + urlReporte);

		    JRDataSource dataSource = new JRBeanCollectionDataSource(rep.getDetallesAdiciones());

		    JasperPrint print = JasperFillManager.fillReport(is, 
		      obtenerMapaParametrosReportes(
		      obtenerParametrosInfoTriobutaria(rep
		      .getNotaCredito()
		      .getInfoTributaria(), numAut, fechaAut, rechazado, logo, com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS), 
		      obtenerInfoNC(rep
		      .getNotaCredito()
		      .getInfoNotaCredito(), rep)), dataSource);

		    StringBuilder outputFile = new StringBuilder();

		    outputFile.append(com.gizlocorp.adm.utilitario.Constantes.DIR_DOCUMENTOS);
		    outputFile.append(TipoComprobante.NOTA_CREDITO.getDescripcion());
		    outputFile.append("/");
		    outputFile.append(subcarpeta);
		    outputFile.append("/");
		    outputFile.append(fechaFolder(fechaEmision));
		    outputFile.append("/");

		    DocumentUtil.createDirectory(outputFile.toString());

		    outputFile.append(rep.getNotaCredito().getInfoTributaria()
		      .getClaveAcceso());

		    outputFile.append(".pdf");

		    JasperExportManager.exportReportToPdfFile(print, outputFile.toString());

		    return outputFile.toString();
		  }


	public String generarReporte(String urlReporte, NotaDebitoReporte rep,
			String numAut, String fechaAut, String fechaEmision,
			String subcarpeta, boolean rechazado, String logo,
			String dirServidor) throws Exception {

		if (!"autorizado".equals(subcarpeta)) {
			return null;
		}

		FileInputStream is = new FileInputStream(dirServidor + urlReporte);
		JRDataSource dataSource = new JRBeanCollectionDataSource(
				rep.getDetallesAdiciones());

		JasperPrint print = JasperFillManager
				.fillReport(
						is,
						obtenerMapaParametrosReportes(
								obtenerParametrosInfoTriobutaria(rep
										.getNotaDebito().getInfoTributaria(),
										numAut, fechaAut, rechazado, logo,
										dirServidor), obtenerInfoND(rep
										.getNotaDebito().getInfoNotaDebito())),
						dataSource);

		StringBuilder outputFile = new StringBuilder();
		// outputFile.append(dirServidor + "/gnvoice/recursos/comprobantes/");
		outputFile
				.append(com.gizlocorp.adm.utilitario.Constantes.DIR_DOCUMENTOS);
		outputFile.append(TipoComprobante.NOTA_DEBITO.getDescripcion());
		outputFile.append("/");
		outputFile.append(subcarpeta);
		outputFile.append("/");
		outputFile.append(fechaFolder(fechaEmision));
		outputFile.append("/");

		DocumentUtil.createDirectory(outputFile.toString());

		// outputFile.append(rep.getNotaDebito().getInfoTributaria().getSecuencial());
		outputFile.append(rep.getNotaDebito().getInfoTributaria().getClaveAcceso());

		outputFile.append(".pdf");

		JasperExportManager.exportReportToPdfFile(print, outputFile.toString());

		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
				1);
		DeleteThread delete = new DeleteThread(outputFile.toString());
		executor.schedule(delete, 2, TimeUnit.MINUTES);

		return outputFile.toString();
	}

	public String generarReporte(String urlReporte, NotaCreditoReporte rep,
			String numAut, String fechaAut, String fechaEmision,
			String subcarpeta, boolean rechazado, String logo) throws Exception {

		if (!"autorizado".equals(subcarpeta)) {
			return null;
		}

		FileInputStream is = new FileInputStream(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+ urlReporte);
		JRDataSource dataSource = new JRBeanCollectionDataSource(rep.getDetallesAdiciones());

		JasperPrint print = JasperFillManager.fillReport(is,
				                    obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(rep.getNotaCredito().getInfoTributaria(),
				                        		    					                           numAut,
				                        		    					                           fechaAut,
										                                                           rechazado,
										                                                           logo,
										                                                           com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS),
								                                 obtenerInfoNC(rep.getNotaCredito().getInfoNotaCredito(), rep)),
				                    		
				                    dataSource);

		StringBuilder outputFile = new StringBuilder();
		// outputFile.append(dirServidor + "/gnvoice/recursos/comprobantes/");
		outputFile.append(com.gizlocorp.adm.utilitario.Constantes.DIR_DOCUMENTOS);
		outputFile.append(TipoComprobante.NOTA_CREDITO.getDescripcion());
		outputFile.append("/");
		outputFile.append(subcarpeta);
		outputFile.append("/");
		outputFile.append(fechaFolder(fechaEmision));
		outputFile.append("/");

		DocumentUtil.createDirectory(outputFile.toString());

		// outputFile.append(rep.getNotaCredito().getInfoTributaria().getSecuencial());
		outputFile.append(rep.getNotaCredito().getInfoTributaria().getClaveAcceso());

		outputFile.append(".pdf");

		JasperExportManager.exportReportToPdfFile(print, outputFile.toString());

		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
				1);
		DeleteThread delete = new DeleteThread(outputFile.toString());
		executor.schedule(delete, 2, TimeUnit.MINUTES);

		return outputFile.toString();
	}

	public String generarReporte(String urlReporte, GuiaRemisionReporte rep,
			String numAut, String fechaAut, GuiaRemision guiaRemision,
			String fechaEmision, String subcarpeta, boolean rechazado,
			String logo) throws Exception {

		if (!"autorizado".equals(subcarpeta)) {
			return null;
		}

		FileInputStream is = new FileInputStream(
				com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
						+ urlReporte);
		JRDataSource dataSource = new JRBeanCollectionDataSource(
				rep.getGuiaRemisionList());

		JasperPrint print = JasperFillManager
				.fillReport(
						is,
						obtenerMapaParametrosReportes(
								obtenerParametrosInfoTriobutaria(
										rep.getGuiaRemision()
												.getInfoTributaria(),
										numAut,
										fechaAut,
										rechazado,
										logo,
										com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS),
								obtenerInfoGR(rep.getGuiaRemision()
										.getInfoGuiaRemision(), guiaRemision)),
						dataSource);

		StringBuilder outputFile = new StringBuilder();
		// outputFile.append(dirServidor + "/gnvoice/recursos/comprobantes/");
		outputFile
				.append(com.gizlocorp.adm.utilitario.Constantes.DIR_DOCUMENTOS);
		outputFile.append(TipoComprobante.GUIA.getDescripcion());
		outputFile.append("/");
		outputFile.append(subcarpeta);
		outputFile.append("/");
		outputFile.append(fechaFolder(fechaEmision));
		outputFile.append("/");

		DocumentUtil.createDirectory(outputFile.toString());

		// outputFile.append(rep.getGuiaRemision().getInfoTributaria().getSecuencial());
		outputFile.append(rep.getGuiaRemision().getInfoTributaria()
				.getClaveAcceso());

		outputFile.append(".pdf");

		JasperExportManager.exportReportToPdfFile(print, outputFile.toString());

		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
				1);
		DeleteThread delete = new DeleteThread(outputFile.toString());
		executor.schedule(delete, 2, TimeUnit.MINUTES);

		return outputFile.toString();
	}

	public String generarReporte(String urlReporte,
			ComprobanteRetencionReporte rep, String numAut, String fechaAut,
			String fechaEmision, String subcarpeta, boolean rechazado,
			String logo, String dirServidor) throws Exception {

		if (!"autorizado".equals(subcarpeta)) {
			return null;
		}

		FileInputStream is = new FileInputStream(dirServidor + urlReporte);
		JRDataSource dataSource = new JRBeanCollectionDataSource(
				rep.getDetallesAdiciones());

		JasperPrint print = JasperFillManager
				.fillReport(
						is,
						obtenerMapaParametrosReportes(
								obtenerParametrosInfoTriobutaria(rep
										.getComprobanteRetencion()
										.getInfoTributaria(), numAut, fechaAut,
										rechazado, logo, dirServidor),
								obtenerInfoCompRetencion(rep
										.getComprobanteRetencion()
										.getInfoCompRetencion())), dataSource);

		StringBuilder outputFile = new StringBuilder();
		// outputFile.append(dirServidor + "/gnvoice/recursos/comprobantes/");
		outputFile
				.append(com.gizlocorp.adm.utilitario.Constantes.DIR_DOCUMENTOS);
		outputFile.append(TipoComprobante.RETENCION.getDescripcion());
		outputFile.append("/");
		outputFile.append(subcarpeta);
		outputFile.append("/");
		outputFile.append(fechaFolder(fechaEmision));
		outputFile.append("/");

		DocumentUtil.createDirectory(outputFile.toString());

		// outputFile.append(rep.getComprobanteRetencion().getInfoTributaria().getSecuencial());
		outputFile.append(rep.getComprobanteRetencion().getInfoTributaria()
				.getClaveAcceso());
		outputFile.append(".pdf");

		JasperExportManager.exportReportToPdfFile(print, outputFile.toString());

		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
				1);
		DeleteThread delete = new DeleteThread(outputFile.toString());
		executor.schedule(delete, 2, TimeUnit.MINUTES);

		return outputFile.toString();
	}

	private Map<String, Object> obtenerMapaParametrosReportes(
			Map<String, Object> mapa1, Map<String, Object> mapa2) {
		mapa1.putAll(mapa2);
		return mapa1;
	}

	private Map<String, Object> obtenerParametrosInfoTriobutaria(
			InfoTributaria infoTributaria, String numAut, String fechaAut,
			boolean rechazado, String logo, String dirServidor)
			throws SQLException, ClassNotFoundException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("RUC", infoTributaria.getRuc());
		param.put("CLAVE_ACC", infoTributaria.getClaveAcceso());
		param.put("RAZON_SOCIAL",
				StringEscapeUtils.unescapeXml(infoTributaria.getRazonSocial()));
		param.put("DIR_MATRIZ",
				StringEscapeUtils.unescapeXml(infoTributaria.getDirMatriz()));
		try {
			param.put("LOGO", new FileInputStream(logo));
		} catch (Exception ex) {

			Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		param.put("SUBREPORT_DIR", dirServidor + Constantes.SUBREPORT_DIR);
		param.put("TIPO_EMISION", obtenerTipoEmision(infoTributaria));
		param.put("NUM_AUT", numAut);
		param.put("FECHA_AUT", fechaAut);

		param.put(
				"MARCA_AGUA",
				obtenerMarcaAgua(infoTributaria.getAmbiente(), rechazado,
						dirServidor));
		param.put("NUM_FACT",
				infoTributaria.getEstab() + "-" + infoTributaria.getPtoEmi()
						+ "-" + infoTributaria.getSecuencial());
		param.put("AMBIENTE", obtenerAmbiente(infoTributaria));
		param.put("NOM_COMERCIAL", StringEscapeUtils.unescapeXml(infoTributaria
				.getNombreComercial()));
		return param;
	}

	private String obtenerAmbiente(InfoTributaria infoTributaria) {
		if (infoTributaria.getAmbiente().equals("2")) {
			return TipoAmbienteEnum.PRODUCCION.toString();
		} else {
			return TipoAmbienteEnum.PRUEBAS.toString();
		}
	}

	private String obtenerTipoEmision(InfoTributaria infoTributaria) {
		if (infoTributaria.getTipoEmision().equals("2")) {
			return TipoEmisionEnum.CONTINGENCIA.getDescripcion();
		}
		if (infoTributaria.getTipoEmision().equals("1")) {
			return TipoEmisionEnum.NORMAL.getDescripcion();
		}
		return null;
	}

	private InputStream obtenerMarcaAgua(String ambiente, boolean rechazado,
			String dirServidor) {
		try {
			if (rechazado) {
				InputStream is = new BufferedInputStream(new FileInputStream(
						dirServidor + Constantes.IMAGEN_RECHAZADO));
				return is;
			} else if (ambiente.equals(TipoAmbienteEnum.PRODUCCION.getCode())) {
				InputStream is = new BufferedInputStream(new FileInputStream(
						dirServidor + Constantes.IMAGEN_PRODUCCION));
				return is;
			} else {
				InputStream is = new BufferedInputStream(new FileInputStream(
						dirServidor + Constantes.IMAGEN_PRUEBAS));
				return is;
			}
		} catch (FileNotFoundException fe) {
			Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE,
					null, fe);
		}
		return null;
	}

	private Map<String, Object> obtenerInfoFactura(Factura.InfoFactura infoFactura, FacturaReporte fact) {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("DIR_SUCURSAL", StringEscapeUtils.unescapeXml(infoFactura
				.getDirEstablecimiento()));
		param.put("CONT_ESPECIAL", infoFactura.getContribuyenteEspecial());
		param.put("LLEVA_CONTABILIDAD", infoFactura.getObligadoContabilidad());
		param.put("RS_COMPRADOR", StringEscapeUtils.unescapeXml(infoFactura
				.getRazonSocialComprador()));
		param.put("RUC_COMPRADOR", infoFactura.getIdentificacionComprador());
		param.put("FECHA_EMISION", infoFactura.getFechaEmision());
		param.put("GUIA", infoFactura.getGuiaRemision());

		TotalComprobante tc = getTotales(infoFactura);
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);

		param.put("VALOR_TOTAL", df.format(infoFactura.getImporteTotal()));
		param.put("DESCUENTO", df.format(infoFactura.getTotalDescuento()));
		
//		log.info("Reporte util---->12 "+tc.getIva12()+"***"+!tc.getIva12().equals("0.00"));
		
		Long porcentajeIva =obtenerPorcentajeIva(fact.getFactura().getInfoFactura().getFechaEmision());
		
		log.info("Reporte util PorcentajeIva--->"+porcentajeIva+"***");

		if(12L ==porcentajeIva){
			param.put("IVA", tc.getIva12());
			param.put("labelIva", "12%");
		}else if(14L ==porcentajeIva){
			param.put("IVA", tc.getIva14());
			param.put("labelIva", "14%");
			log.info("Reporte util---->14"+tc.getIva14()+"***");
			log.info("getSubtotal14---->14"+tc.getSubtotal14()+"***");
		}
		
		/*if(!tc.getIva12().equals("0.00")){
			param.put("IVA", tc.getIva12());
			param.put("labelIva", "12%");
		}else{
			param.put("IVA", tc.getIva14());
			param.put("labelIva", "14%");
			log.info("Reporte util---->14"+tc.getIva14()+"***");
			log.info("getSubtotal14---->14"+tc.getSubtotal14()+"***");
		}*/	
		
		param.put("IVA_14", tc.getSubtotal14());
		param.put("IVA_0", tc.getSubtotal0());
		param.put("IVA_12", tc.getSubtotal12());
		param.put("EXENTO_IVA", df.format(tc.getSubtotalExentoIVA()));
		param.put("ICE", tc.getTotalIce());
		param.put("IRBPNR", df.format(tc.getTotalIRBPNR()));
		param.put("NO_OBJETO_IVA", tc.getSubtotalNoSujetoIva());
		param.put("SUBTOTAL", df.format(infoFactura.getTotalSinImpuestos()));
		if (infoFactura.getPropina() != null) {
			param.put("PROPINA", df.format(infoFactura.getPropina()));
		}
		param.put("TOTAL_DESCUENTO", calcularDescuento(fact));
		
		if(infoFactura.getCompensaciones() != null && infoFactura.getCompensaciones().getCompensacion() != null && !infoFactura.getCompensaciones().getCompensacion().isEmpty()){
			param.put("compensacion", infoFactura.getCompensaciones().getCompensacion().get(0).getValor());
//			BigDecimal compensacion = new BigDecimal("0.00");
//			compensacion = infoFactura.getCompensaciones().getCompensacion().get(0).getValor();
			param.put("totalcompensacion", infoFactura.getImporteTotal().subtract(infoFactura.getCompensaciones().getCompensacion().get(0).getValor()));
		}else{
			param.put("compensacion", null);
		}
		if(infoFactura.getPagos() != null && infoFactura.getPagos().getPago() != null && !infoFactura.getPagos().getPago().isEmpty()){
			System.out.println("enviando nuevo parametro 2222");
			for(Pago itera: infoFactura.getPagos().getPago()){
				if(itera.getFormaPago().equals("01")){
					itera.setFormaPago("SIN UTILIZACION DEL SISTEMA FINANCIERO");
				}
				if(itera.getFormaPago().equals("02")){
					itera.setFormaPago("CHEQUE PROPIO");
				}
				if(itera.getFormaPago().equals("03")){
					itera.setFormaPago("CHEQUE CERTIFICADO");
				}
				if(itera.getFormaPago().equals("04")){
					itera.setFormaPago("CHEQUE DE GERENCIA");
				}
				if(itera.getFormaPago().equals("05")){
					itera.setFormaPago("CHEQUE DEL EXTERIOR");
				}
				if(itera.getFormaPago().equals("06")){
					itera.setFormaPago("CHEQUE DEL EXTERIOR");
				}
				if(itera.getFormaPago().equals("07")){
					itera.setFormaPago("TRANSFERENCIA PROPIO BANCO");
				}
				if(itera.getFormaPago().equals("08")){
					itera.setFormaPago("TRANSFERENCIA OTRO BANCO NACIONAL");
				}
				if(itera.getFormaPago().equals("09")){
					itera.setFormaPago("TRANSFERENCIA BANCO EXTERIOR");
				}
				if(itera.getFormaPago().equals("10")){
					itera.setFormaPago("TARJETA DE CRÉDITO NACIONAL");
				}
				if(itera.getFormaPago().equals("11")){
					itera.setFormaPago("TARJETA DE CRÉDITO INTERNACIONAL");
				}
				if(itera.getFormaPago().equals("12")){
					itera.setFormaPago("GIRO");
				}
				if(itera.getFormaPago().equals("13")){
					itera.setFormaPago("DEPOSITO EN CUENTA (CORRIENTE/AHORROS)");
				}
				if(itera.getFormaPago().equals("14")){
					itera.setFormaPago("ENDOSO DE INVERSIÒN");
				}
				if(itera.getFormaPago().equals("15")){
					itera.setFormaPago("COMPENSACIÓN DE DEUDAS");
				}
				if(itera.getFormaPago().equals("16")){
					itera.setFormaPago("TARJETA DE DÉBITO");
				}
				if(itera.getFormaPago().equals("17")){
					itera.setFormaPago("DINERO ELECTRÓNICO");
				}
				if(itera.getFormaPago().equals("18")){
					itera.setFormaPago("TARJETA PREPAGO");
				}
				if(itera.getFormaPago().equals("19")){
					itera.setFormaPago("TARJETA DE CRÉDITO");
				}
				if(itera.getFormaPago().equals("20")){
					itera.setFormaPago("OTROS CON UTILIZACION DEL SISTEMA FINANCIERO");
				}
				if(itera.getFormaPago().equals("21")){
					itera.setFormaPago("ENDOSO DE TÍTULOS");
				}	
			}
			param.put("listapagos", infoFactura.getPagos().getPago());
		}else{
			param.put("listapagos", null);
		}
		return param;
	}

	private String calcularDescuento(FacturaReporte fact) {
		BigDecimal descuento = new BigDecimal(0);
		for (DetallesAdicionalesReporte detalle : fact.getDetallesAdiciones()) {
			descuento = descuento.add(new BigDecimal(detalle.getDescuento()));
		}
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);

		return df.format(descuento);
	}

	private TotalComprobante getTotales(Factura.InfoFactura infoFactura) {
		BigDecimal totalIva14 = new BigDecimal(0.0D);
		BigDecimal totalIva12 = new BigDecimal(0.0D);
		BigDecimal totalIva0 = new BigDecimal(0.0D);
		BigDecimal totalExentoIVA = new BigDecimal(0.0D);
		BigDecimal iva12 = new BigDecimal(0.0D);
		BigDecimal iva14 = new BigDecimal(0.0D);
		BigDecimal totalICE = new BigDecimal(0.0D);
		BigDecimal totalIRBPNR = new BigDecimal(0.0D);
		BigDecimal totalSinImpuesto = new BigDecimal(0.0D);
		TotalComprobante tc = new TotalComprobante();
		
		for (Factura.InfoFactura.TotalConImpuestos.TotalImpuesto ti : infoFactura.getTotalConImpuestos().getTotalImpuesto()) {
			Integer cod = new Integer(ti.getCodigo());
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue()) && (TipoImpuestoIvaEnum.IVA_VENTA_12.getCode().equals(ti.getCodigoPorcentaje()))) {
				totalIva12 = totalIva12.add(ti.getBaseImponible());
				iva12 = iva12.add(ti.getValor());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue()) && (TipoImpuestoIvaEnum.IVA_VENTA_14.getCode().equals(ti.getCodigoPorcentaje()))) {
				totalIva14 = totalIva14.add(ti.getBaseImponible());
				iva14 = iva14.add(ti.getValor());
			}
			
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue()) && (TipoImpuestoIvaEnum.IVA_VENTA_0.getCode().equals(ti.getCodigoPorcentaje()))) {
				totalIva0 = totalIva0.add(ti.getBaseImponible());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue()) && (TipoImpuestoIvaEnum.IVA_NO_OBJETO.getCode().equals(ti .getCodigoPorcentaje()))) {
				totalSinImpuesto = totalSinImpuesto.add(ti.getBaseImponible());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue()) && (TipoImpuestoIvaEnum.IVA_EXCENTO.getCode().equals(ti .getCodigoPorcentaje()))) {
				totalExentoIVA = totalExentoIVA.add(ti.getBaseImponible());
			}
			if (TipoImpuestoEnum.ICE.getCode() == cod.intValue()) {
				totalICE = totalICE.add(ti.getValor());
			}
			if (TipoImpuestoEnum.IRBPNR.getCode() == cod.intValue()) {
				totalIRBPNR = totalIRBPNR.add(ti.getValor());
			}
		}
		/*
		 * tc.setIva12(iva12.toString()); tc.setSubtotal0(totalIva0.toString());
		 * tc.setSubtotal12(totalIva12.toString());
		 * tc.setTotalIce(totalICE.toString());
		 * tc.setSubtotal(totalIva0.add(totalIva12));
		 * tc.setSubtotalExentoIVA(totalExentoIVA);
		 * tc.setTotalIRBPNR(totalIRBPNR);
		 * tc.setSubtotalNoSujetoIva(totalSinImpuesto.toString());
		 */
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);

		tc.setIva14(df.format(iva14));
		tc.setSubtotal14(df.format(totalIva14));
		tc.setIva12(df.format(iva12));
		tc.setSubtotal0(df.format(totalIva0));
		tc.setSubtotal12(df.format(totalIva12));
		tc.setTotalIce(df.format(totalICE));
		tc.setSubtotal(totalIva0.add(totalIva12));
		tc.setSubtotalExentoIVA(totalExentoIVA);
		tc.setTotalIRBPNR(totalIRBPNR);
		tc.setSubtotalNoSujetoIva(df.format(totalSinImpuesto));

		return tc;

	}

	private TotalComprobante getTotalesNC(NotaCredito.InfoNotaCredito infoNc) {
		BigDecimal totalIva12 = new BigDecimal(0.0D);
		BigDecimal totalIva0 = new BigDecimal(0.0D);
		BigDecimal iva12 = new BigDecimal(0.0D);
		BigDecimal totalExentoIVA = new BigDecimal(0.0D);
		BigDecimal totalICE = new BigDecimal(0.0D);
		BigDecimal totalSinImpuesto = new BigDecimal(0.0D);
		BigDecimal totalIRBPNR = new BigDecimal(0.0D);
		BigDecimal totalIva14 = new BigDecimal(0.0D);
		BigDecimal iva14 = new BigDecimal(0.0D);
		TotalComprobante tc = new TotalComprobante();
		for (TotalConImpuestos.TotalImpuesto ti : infoNc.getTotalConImpuestos()
				.getTotalImpuesto()) {
			Integer cod = new Integer(ti.getCodigo());
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_VENTA_12.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalIva12 = totalIva12.add(ti.getBaseImponible());
				iva12 = iva12.add(ti.getValor());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_VENTA_14.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalIva14 = totalIva14.add(ti.getBaseImponible());
				iva14 = iva14.add(ti.getValor());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_VENTA_0.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalIva0 = totalIva0.add(ti.getBaseImponible());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_NO_OBJETO.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalSinImpuesto = totalSinImpuesto.add(ti.getBaseImponible());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_EXCENTO.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalExentoIVA = totalExentoIVA.add(ti.getBaseImponible());
			}
			if (TipoImpuestoEnum.ICE.getCode() == cod.intValue()) {
				totalICE = totalICE.add(ti.getValor());
			}
			if (TipoImpuestoEnum.IRBPNR.getCode() == cod.intValue()) {
				totalIRBPNR = totalIRBPNR.add(ti.getValor());
			}
		}
		/*
		 * tc.setIva12(iva12.toString()); tc.setSubtotal0(totalIva0.toString());
		 * tc.setSubtotal12(totalIva12.toString());
		 * tc.setTotalIce(totalICE.toString());
		 * tc.setSubtotal(totalIva0.add(totalIva12));
		 * tc.setSubtotalExentoIVA(totalExentoIVA);
		 * tc.setTotalIRBPNR(totalIRBPNR);
		 * tc.setSubtotalNoSujetoIva(totalSinImpuesto.toString());
		 */

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);

		tc.setIva14(df.format(iva14));
		tc.setSubtotal14(df.format(totalIva14));
		tc.setIva12(df.format(iva12));
		tc.setSubtotal0(df.format(totalIva0));
		tc.setSubtotal12(df.format(totalIva12));
		tc.setTotalIce(df.format(totalICE));
		tc.setSubtotal(totalIva0.add(totalIva12));
		tc.setSubtotalExentoIVA(totalExentoIVA);
		tc.setTotalIRBPNR(totalIRBPNR);
		tc.setSubtotalNoSujetoIva(df.format(totalSinImpuesto));

		return tc;
	}

	private TotalComprobante getTotalesND(
			NotaDebito.InfoNotaDebito infoNotaDebito) {
		BigDecimal totalIva12 = new BigDecimal(0.0D);
		BigDecimal totalIva0 = new BigDecimal(0.0D);
		BigDecimal totalICE = new BigDecimal(0.0D);
		BigDecimal iva12 = new BigDecimal(0.0D);
		BigDecimal totalExentoIVA = new BigDecimal(0.0D);
		BigDecimal totalSinImpuesto = new BigDecimal(0.0D);
		TotalComprobante tc = new TotalComprobante();
		for (Impuesto ti : infoNotaDebito.getImpuestos().getImpuesto()) {
			Integer cod = new Integer(ti.getCodigo());
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_VENTA_12.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalIva12 = totalIva12.add(ti.getBaseImponible());
				iva12 = iva12.add(ti.getValor());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_VENTA_0.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalIva0 = totalIva0.add(ti.getBaseImponible());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_NO_OBJETO.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalSinImpuesto = totalSinImpuesto.add(ti.getBaseImponible());
			}
			if ((TipoImpuestoEnum.IVA.getCode() == cod.intValue())
					&& (TipoImpuestoIvaEnum.IVA_EXCENTO.getCode().equals(ti
							.getCodigoPorcentaje()))) {
				totalExentoIVA = totalExentoIVA.add(ti.getBaseImponible());
			}
			if (TipoImpuestoEnum.ICE.getCode() == cod.intValue()) {
				totalICE = totalICE.add(ti.getValor());
			}
		}
		/*
		 * tc.setSubtotal0(totalIva0.toString());
		 * tc.setSubtotal12(totalIva12.toString());
		 * tc.setTotalIce(totalICE.toString()); tc.setIva12(iva12.toString());
		 * tc.setSubtotalExentoIVA(totalExentoIVA);
		 * tc.setSubtotalNoSujetoIva(totalSinImpuesto.toPlainString());
		 */

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);
		tc.setSubtotal0(df.format(totalIva0));
		tc.setSubtotal12(df.format(totalIva12));
		tc.setTotalIce(df.format(totalICE));
		tc.setIva12(df.format(iva12));
		tc.setSubtotalExentoIVA(totalExentoIVA);
		tc.setSubtotalNoSujetoIva(df.format(totalSinImpuesto));

		return tc;
	}

	private Map<String, Object> obtenerInfoNC(InfoNotaCredito infoNC,
			NotaCreditoReporte nc) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("DIR_SUCURSAL",
				StringEscapeUtils.unescapeXml(infoNC.getDirEstablecimiento()));
		param.put("CONT_ESPECIAL", infoNC.getContribuyenteEspecial());
		param.put("LLEVA_CONTABILIDAD", infoNC.getObligadoContabilidad());
		param.put("RS_COMPRADOR",
				StringEscapeUtils.unescapeXml(infoNC.getRazonSocialComprador()));
		param.put("RUC_COMPRADOR", infoNC.getIdentificacionComprador());
		param.put("FECHA_EMISION", infoNC.getFechaEmision());
		TotalComprobante tc = getTotalesNC(infoNC);
		param.put("IVA_0", tc.getSubtotal0());
		param.put("IVA_12", tc.getSubtotal12());
		param.put("IVA_14", tc.getSubtotal14());
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);
		param.put("EXENTO_IVA", df.format(tc.getSubtotalExentoIVA()));
		param.put("ICE", tc.getTotalIce());
		param.put("IRBPNR", df.format(tc.getTotalIRBPNR()));
		param.put("VALOR_TOTAL", infoNC.getValorModificacion());
		
		
         Long porcentajeIva =obtenerPorcentajeIva(infoNC.getFechaEmision());
		
		log.info("Reporte util PorcentajeIva--->"+porcentajeIva+"***");

		if(12L ==porcentajeIva){
			param.put("IVA", tc.getIva12());
			param.put("labelIva", "12%");
		}else if(14L ==porcentajeIva){
			param.put("IVA", tc.getIva14());
			param.put("labelIva", "14%");
			log.info("Reporte util---->14"+tc.getIva14()+"***");
			log.info("getSubtotal14---->14"+tc.getSubtotal14()+"***");
		}
		
		
		/*
//		param.put("IVA", tc.getIva12());
		if(!tc.getIva12().equals("0.00")){
			param.put("IVA", tc.getIva12());
			param.put("labelIva", "12%");
		}else{
			param.put("IVA", tc.getIva14());
			param.put("labelIva", "14%");
		}*/	
		
		param.put("SUBTOTAL", df.format(infoNC.getTotalSinImpuestos()));
		param.put("NO_OBJETO_IVA", tc.getSubtotalNoSujetoIva());
		param.put("NUM_DOC_MODIFICADO", infoNC.getNumDocModificado());
		param.put("FECHA_EMISION_DOC_SUSTENTO",
				infoNC.getFechaEmisionDocSustento());
		param.put("DOC_MODIFICADO", StringUtil
				.obtenerDocumentoModificado(infoNC.getCodDocModificado()));
		param.put("TOTAL_DESCUENTO", obtenerTotalDescuento(nc));
		param.put("RAZON_MODIF",
				StringEscapeUtils.unescapeXml(infoNC.getMotivo()));
		
		if(infoNC.getCompensaciones() != null && infoNC.getCompensaciones().getCompensacion() != null && !infoNC.getCompensaciones().getCompensacion().isEmpty()){
			SubsidioNotaCredito subsidioNotaCredito= null;
			List<SubsidioNotaCredito> list = new ArrayList<SubsidioNotaCredito>();
			BigDecimal totalcompensacion = new BigDecimal("0.00");
			for(Compensacion itera: infoNC.getCompensaciones().getCompensacion()){
				log.info("lista compensacion 111***"+itera.getCodigo());
				if(itera.getCodigo().compareTo(new BigDecimal("1"))==0){
					subsidioNotaCredito = new SubsidioNotaCredito();
					subsidioNotaCredito.setNombre("(-)Descuento Solidario 2%");
					subsidioNotaCredito.setValor(itera.getValor());
					list.add(subsidioNotaCredito);
					totalcompensacion = totalcompensacion.add(itera.getValor());
				}
				if(itera.getCodigo().compareTo(new BigDecimal("2"))==0){
					subsidioNotaCredito = new SubsidioNotaCredito();
					subsidioNotaCredito.setNombre("(-)Devolución Dinero Electrónico 4%");
					subsidioNotaCredito.setValor(itera.getValor());
					list.add(subsidioNotaCredito);
					totalcompensacion = totalcompensacion.add(itera.getValor());
				}
				if(itera.getCodigo().compareTo(new BigDecimal("4"))==0){
					subsidioNotaCredito = new SubsidioNotaCredito();
					subsidioNotaCredito.setNombre("(-)Devolución Tarjeta de Crédito 1%");
					subsidioNotaCredito.setValor(itera.getValor());
					list.add(subsidioNotaCredito);
					totalcompensacion = totalcompensacion.add(itera.getValor());
				}
			}
			subsidioNotaCredito = new SubsidioNotaCredito();
			subsidioNotaCredito.setNombre("Valor a Pagar");
			subsidioNotaCredito.setValor(infoNC.getValorModificacion().subtract(totalcompensacion));
			list.add(subsidioNotaCredito);
			log.info("lista compensacion 222***"+list.size());
			param.put("compensacions", list);
			param.put("totalcompensacion", infoNC.getValorModificacion().subtract(totalcompensacion));
		}else{
			param.put("compensacions", null);
			param.put("totalcompensacion", null);
		}
		
		return param;
	}

	private Map<String, Object> obtenerInfoGR(InfoGuiaRemision igr,
			GuiaRemision guiaRemision) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("DIR_SUCURSAL",
				StringEscapeUtils.unescapeXml(igr.getDirEstablecimiento()));
		param.put("CONT_ESPECIAL", igr.getContribuyenteEspecial());
		param.put("LLEVA_CONTABILIDAD", igr.getObligadoContabilidad());
		param.put("FECHA_INI_TRANSPORTE", igr.getFechaIniTransporte());
		param.put("FECHA_FIN_TRANSPORTE", igr.getFechaFinTransporte());
		param.put("RUC_TRANSPORTISTA", igr.getRucTransportista());
		param.put("RS_TRANSPORTISTA", StringEscapeUtils.unescapeXml(igr
				.getRazonSocialTransportista()));
		param.put("PLACA", igr.getPlaca());
		param.put("PUNTO_PARTIDA", igr.getDirPartida());
		param.put("INFO_ADICIONAL", getInfoAdicional(guiaRemision));
		return param;
	}

	public List<InformacionAdicional> getInfoAdicional(GuiaRemision guiaRemision) {
		List<InformacionAdicional> infoAdicional = new ArrayList<InformacionAdicional>();
		if (guiaRemision.getInfoAdicional() != null) {
			for (GuiaRemision.InfoAdicional.CampoAdicional ca : guiaRemision
					.getInfoAdicional().getCampoAdicional()) {
				infoAdicional.add(new InformacionAdicional(ca.getValue(), ca
						.getNombre()));
			}
		}
		if (infoAdicional != null && !infoAdicional.isEmpty()) {
			return infoAdicional;
		}
		return null;
	}

	private String obtenerTotalDescuento(NotaCreditoReporte nc) {
		BigDecimal descuento = new BigDecimal(0);
		for (DetallesAdicionalesReporte detalle : nc.getDetallesAdiciones()) {
			descuento = descuento.add(new BigDecimal(detalle.getDescuento()));
		}
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);
		return df.format(descuento);
	}

	private Map<String, Object> obtenerInfoND(
			NotaDebito.InfoNotaDebito notaDebito) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("DIR_SUCURSAL", StringEscapeUtils.unescapeXml(notaDebito
				.getDirEstablecimiento()));
		param.put("CONT_ESPECIAL", notaDebito.getContribuyenteEspecial());
		param.put("LLEVA_CONTABILIDAD", notaDebito.getObligadoContabilidad());
		param.put("RS_COMPRADOR", StringEscapeUtils.unescapeXml(notaDebito
				.getRazonSocialComprador()));
		param.put("RUC_COMPRADOR", notaDebito.getIdentificacionComprador());
		param.put("FECHA_EMISION", notaDebito.getFechaEmision());
		TotalComprobante tc = getTotalesND(notaDebito);
		param.put("IVA_0", tc.getSubtotal0());
		param.put("IVA_12", tc.getSubtotal12());

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
				Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);

		param.put("EXENTO_IVA", df.format(tc.getSubtotalExentoIVA()));
		param.put("ICE", tc.getTotalIce());
		param.put("TOTAL", df.format(notaDebito.getValorTotal()));
		param.put("IVA", tc.getIva12());
		param.put("NO_OBJETO_IVA", tc.getSubtotalNoSujetoIva());
		param.put("NUM_DOC_MODIFICADO", notaDebito.getNumDocModificado());
		param.put("FECHA_EMISION_DOC_SUSTENTO",
				notaDebito.getFechaEmisionDocSustento());
		param.put("DOC_MODIFICADO", StringUtil
				.obtenerDocumentoModificado(notaDebito.getCodDocModificado()));
		param.put("TOTAL_SIN_IMP", df.format(notaDebito.getTotalSinImpuestos()));
		return param;
	}

	private Map<String, Object> obtenerInfoCompRetencion(
			ComprobanteRetencion.InfoCompRetencion infoComp) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("DIR_SUCURSAL",
				StringEscapeUtils.unescapeXml(infoComp.getDirEstablecimiento()));
		param.put("RS_COMPRADOR", StringEscapeUtils.unescapeXml(infoComp
				.getRazonSocialSujetoRetenido()));
		param.put("RUC_COMPRADOR", infoComp.getIdentificacionSujetoRetenido());
		param.put("FECHA_EMISION", infoComp.getFechaEmision());
		param.put("CONT_ESPECIAL", infoComp.getContribuyenteEspecial());
		param.put("LLEVA_CONTABILIDAD", infoComp.getObligadoContabilidad());
		param.put("EJERCICIO_FISCAL", infoComp.getPeriodoFiscal());
		return param;
	}
	

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long obtenerPorcentajeIva(String fechaEmision)  {

		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId("PROD");
		credencialDS.setUsuario("WEBLINK");
		credencialDS.setClave("weblink_2013");
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		
		
		/*try {
			conn = Conexion.obtenerConexionCRPDTA(credencialDS);

			StringBuilder sqlString = new StringBuilder();

			sqlString.append(" select VALOR from fa_parametros_farmacia"
					+ " where campo = 'IVA_SECTORIZADO'"
					+ " AND ACTIVO = 'S'"
					+ " AND FARMACIA = ? ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, fechaEmision);
			ResultSet set = ps.executeQuery();

			while (set.next()) {
				return set.getLong("VALOR");
			}

		} catch (Exception e) {
			Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
		} finally {

			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
			}

			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		*/
		
		
		
		try {
			conn = Conexion.obtenerConexionCRPDTA(credencialDS);

			StringBuilder sqlString = new StringBuilder();

			sqlString.append(" select *  from ad_impuesto_detalles where codigo_impto = 1"
					       + " and to_date ( ? , 'dd/mm/yyyy') between fecha_inicio and  nvl(fecha_fin, sysdate)");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, fechaEmision);
			ResultSet set = ps.executeQuery();

			while (set.next()) {
				return set.getLong("valor");
			}

		} catch (Exception e) {
			Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
		} finally {

			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
			}

			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				Logger.getLogger(ReporteUtil.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		return 0L;

	}

}