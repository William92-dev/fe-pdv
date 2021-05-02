package com.gizlocorp.gnvoice.bean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.RimEnum;
import com.gizlocorp.gnvoice.bean.databean.ReceptarComprobanteDataBean;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ConstantesRim;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaPortType;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaRecibirRequest;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaRecibirResponse;
import com.gizlocorp.gnvoice.wsclient.factura.Factura_Service;
import com.gizlocorp.gnvoice.wsclient.factura.GizloResponse;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCreditoPortType;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCreditoRecibirRequest;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCreditoRecibirResponse;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCredito_Service;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle.Impuestos;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoice.xml.factura.Impuesto;

//@Interceptors(CurrentUserGnvoiceProvider.class)
@ViewScoped
@Named("receptarComprobanteBean")
public class ReceptarComprobanteBean extends BaseBean implements Serializable {

	public static final Logger log = Logger
			.getLogger(ReceptarComprobanteBean.class);

	private static final long serialVersionUID = -6239437588285327644L;

	@Inject
	ReceptarComprobanteDataBean receptarComprobanteDataBean;

	@Inject
	private SessionBean sessionBean;

	public static final int BUFFER_SIZE = 4096;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;
	

	@PostConstruct
	public void postContruct() {
		receptarComprobanteDataBean
				.setRespuestas(new ArrayList<GizloResponse>());
		receptarComprobanteDataBean
				.setTipoComprobanteDes(TipoComprobante.FACTURA);


	}

	private ZipInputStream zis;

	public void cargarDocumento(org.richfaces.event.FileUploadEvent event) {
		org.richfaces.model.UploadedFile item = event.getUploadedFile();
		receptarComprobanteDataBean.setData(item.getData());
		receptarComprobanteDataBean.setName(item.getName());

	}

	public void paint(OutputStream stream, Object object) throws IOException {
		// if(object !=null){
		stream.write(receptarComprobanteDataBean.getData());
		stream.close();
		// }

	}

	public void cargarDocumentoPdf(org.richfaces.event.FileUploadEvent event) {
		org.richfaces.model.UploadedFile item = event.getUploadedFile();
		receptarComprobanteDataBean.setDataPdf(item.getData());
		receptarComprobanteDataBean.setNamePdf(item.getName());

	}

	public void cargarDocumentoMasivo(org.richfaces.event.FileUploadEvent event) {
		org.richfaces.model.UploadedFile item = event.getUploadedFile();
		receptarComprobanteDataBean.setDataMasivo(item.getData());
		receptarComprobanteDataBean.setNameMasivo(item.getName());
	}

	@SuppressWarnings("incomplete-switch")
	public void guardarMasivo() {
		receptarComprobanteDataBean
				.setRespuestas(new ArrayList<GizloResponse>());
		GizloResponse respuesta = null;

		StringBuilder fileStr = new StringBuilder();
		Calendar now = Calendar.getInstance();

		try {
			fileStr.append(sessionBean.getDirectorioServidor()
					+ "/gnvoice/recursos/comprobantes/factura/recibidoManual/");
			fileStr.append(now.get(Calendar.DAY_OF_MONTH));
			fileStr.append(now.get(Calendar.MONTH));
			fileStr.append(now.get(Calendar.YEAR));
			fileStr.append("/");
			createDirectory(fileStr.toString());
			fileStr.append("comprobante");
			fileStr.append(now.getTimeInMillis());
			fileStr.append(".zip");

			FileOutputStream fos = new FileOutputStream(fileStr.toString());
			fos.write(receptarComprobanteDataBean.getDataMasivo());
			fos.close();

			FileInputStream fis = new FileInputStream(fileStr.toString());
			zis = new ZipInputStream(new BufferedInputStream(fis));

			ZipEntry entrada;
			String comprobante = null;
			int i = 0;

			while (null != (entrada = zis.getNextEntry())) {
				try {
					respuesta = new GizloResponse();
					fileStr = new StringBuilder();
					if (!entrada.getName().contains(".xml")) {

						respuesta.setClaveAccesoComprobante(entrada.getName());
						respuesta
								.setMensajeCliente("Ha ocurrido un error en el proceso");
						respuesta.setMensajeSistema("Documento No es XML");
						respuesta.setEstado("ERROR");
						continue;
					}

					switch (receptarComprobanteDataBean.getTipoComprobanteDes()) {

					case FACTURA:
						fileStr.append(sessionBean.getDirectorioServidor()
								+ "/gnvoice/recursos/comprobantes/factura/recibidoManual/");
						fileStr.append(now.get(Calendar.DAY_OF_MONTH));
						fileStr.append(now.get(Calendar.MONTH));
						fileStr.append(now.get(Calendar.YEAR));
						fileStr.append("/");
						createDirectory(fileStr.toString());

						fileStr.append("factura");
						fileStr.append(now.getTimeInMillis());
						fileStr.append(i);
						fileStr.append(".xml");

						FileOutputStream fos2 = new FileOutputStream(
								fileStr.toString());
						int leido;
						byte[] buffer = new byte[BUFFER_SIZE];
						while (0 < (leido = zis.read(buffer))) {
							fos2.write(buffer, 0, leido);
						}

						fos2.close();
						zis.closeEntry();

						comprobante = DocumentUtil.createDocument(DocumentUtil
								.readFile(fileStr.toString()), FechaUtil
								.formatearFecha(now.getTime(), "dd/MM/yyyy"),
								"factura" + now.getTimeInMillis(),
								TipoComprobante.FACTURA.getDescripcion(),
								"recibidoManual");

						Factura_Service serviceFactura = new Factura_Service();
						FacturaPortType portFactura = serviceFactura
								.getFacturaPort();

						FacturaRecibirRequest facturaRecibirRequest = new FacturaRecibirRequest();
						//facturaRecibirRequest
								//.setComprobanteProveedor(comprobante);								
						if (comprobante != null) {
							facturaRecibirRequest.setComprobanteProveedor(comprobante);
						}
								
						if (receptarComprobanteDataBean.getProceso().equals(
								RimEnum.REIM)) {
							facturaRecibirRequest
									.setProceso(receptarComprobanteDataBean
											.getProceso().name());
						}
						FacturaRecibirResponse respuestaFactura = portFactura
								.recibir(facturaRecibirRequest);

						respuesta.setClaveAccesoComprobante("factura"
								+ now.getTimeInMillis() + i + ".xml");
						respuesta.setEstado(respuestaFactura.getEstado());
						respuesta.setMensajeCliente(respuestaFactura
								.getMensajeCliente());
						respuesta.setMensajeSistema(respuestaFactura
								.getMensajeSistema());

						break;

					case NOTA_CREDITO:
						fileStr.append(sessionBean.getDirectorioServidor()
								+ "/gnvoice/recursos/comprobantes/notaCredito/recibidoManual/");
						fileStr.append(now.get(Calendar.DAY_OF_MONTH));
						fileStr.append(now.get(Calendar.MONTH));
						fileStr.append(now.get(Calendar.YEAR));
						fileStr.append("/");
						createDirectory(fileStr.toString());

						fileStr.append("notaCredito");
						fileStr.append(now.getTimeInMillis());
						fileStr.append(i);
						fileStr.append(".xml");

						FileOutputStream fos2nc = new FileOutputStream(
								fileStr.toString());
						int leidonc;
						byte[] buffernc = new byte[BUFFER_SIZE];
						while (0 < (leidonc = zis.read(buffernc))) {
							fos2nc.write(buffernc, 0, leidonc);
						}

						fos2nc.close();
						zis.closeEntry();

						comprobante = DocumentUtil.createDocument(DocumentUtil
								.readFile(fileStr.toString()), FechaUtil
								.formatearFecha(now.getTime(), "dd/MM/yyyy"),
								"notaCredito" + now.getTimeInMillis(),
								TipoComprobante.NOTA_CREDITO.getDescripcion(),
								"recibidoManual");

						NotaCredito_Service serviceNotaCredito = new NotaCredito_Service();
						NotaCreditoPortType portNotaCredito = serviceNotaCredito
								.getNotaCreditoPort();

						NotaCreditoRecibirRequest notaCreditoRecibirRequest = new NotaCreditoRecibirRequest();
						//notaCreditoRecibirRequest.setComprobanteProveedor(comprobante);
						if (comprobante != null) {
							notaCreditoRecibirRequest.setComprobanteProveedor(comprobante);
						}
						if (receptarComprobanteDataBean.getProceso().equals(RimEnum.REIM)) {
							notaCreditoRecibirRequest.setProceso(receptarComprobanteDataBean.getProceso().name());
						}
						NotaCreditoRecibirResponse respuestaNotaCredito = portNotaCredito
								.recibir(notaCreditoRecibirRequest);

						respuesta.setClaveAccesoComprobante("notaCredito"
								+ now.getTimeInMillis() + i + ".xml");
						respuesta.setEstado(respuestaNotaCredito.getEstado());
						respuesta.setMensajeCliente(respuestaNotaCredito
								.getMensajeCliente());
						respuesta.setMensajeSistema(respuestaNotaCredito
								.getMensajeSistema());

						break;

					}
					i++;
				} catch (Exception ex) {
					respuesta = new GizloResponse();
					respuesta
							.setClaveAccesoComprobante(receptarComprobanteDataBean
									.getNameMasivo());
					respuesta
							.setMensajeCliente("Ha ocurrido un error en el proceso");
					respuesta.setMensajeSistema(ex.getMessage());
					respuesta.setEstado("ERROR");

				}
				receptarComprobanteDataBean.getRespuestas().add(respuesta);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			respuesta = new GizloResponse();
			respuesta.setClaveAccesoComprobante(receptarComprobanteDataBean
					.getName());
			respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
			respuesta.setMensajeSistema(ex.getMessage());
			respuesta.setEstado("ERROR");
			receptarComprobanteDataBean.getRespuestas().add(respuesta);

		}
	}

	@SuppressWarnings("incomplete-switch")
	public void guardar() {
		receptarComprobanteDataBean.setRespuestas(new ArrayList<GizloResponse>());
		GizloResponse respuesta = null;

		Calendar now = Calendar.getInstance();
		StringBuilder fileStr = new StringBuilder();
		String comprobante = null;
		String comprobantePDF = null;

		if (receptarComprobanteDataBean.getData() == null) {
			errorMessage("Debe agregar un archivo xml",
					"Debe agregar un archivo xml");
			return;
		}

		try {
			respuesta = new GizloResponse();
			switch (receptarComprobanteDataBean.getTipoComprobanteDes()) {
			case FACTURA:
				fileStr.append("factura");
				fileStr.append(now.getTimeInMillis());

				if (receptarComprobanteDataBean.getData() != null) {
					comprobante = DocumentUtil.createDocument(receptarComprobanteDataBean.getData(),
							FechaUtil.formatearFecha(now.getTime(),"dd/MM/yyyy"), fileStr.toString(),
							TipoComprobante.FACTURA.getDescripcion(), "recibidoManual");
				}

				if (receptarComprobanteDataBean.getDataPdf() != null) {
					comprobantePDF = DocumentUtil.createDocumentPdf(
							receptarComprobanteDataBean.getDataPdf(),
							FechaUtil.formatearFecha(now.getTime(),
									"dd/MM/yyyy"), fileStr.toString(),
							TipoComprobante.FACTURA.getDescripcion(),
							"recibidoManual", sessionBean
									.getDirectorioServidor());
				}

				Factura_Service serviceFactura = new Factura_Service();
				FacturaPortType portFactura = serviceFactura.getFacturaPort();

				FacturaRecibirRequest facturaRecibirRequest = new FacturaRecibirRequest();
				if (comprobante != null) {
					facturaRecibirRequest.setComprobanteProveedor(comprobante);
				}
				if (comprobantePDF != null) {
					facturaRecibirRequest.setComprobanteProveedorPDF(comprobantePDF);
				}
				if (receptarComprobanteDataBean.getProceso().equals(RimEnum.REIM)) {
					facturaRecibirRequest.setProceso(receptarComprobanteDataBean.getProceso().name());
				}
				facturaRecibirRequest.setTipo(Constantes.MANUAL);
				log.info("***Enviando a traducir factura***");
				FacturaRecibirResponse respuestaFactura = portFactura.recibir(facturaRecibirRequest);

				respuesta.setClaveAccesoComprobante(receptarComprobanteDataBean.getName());
				respuesta.setEstado(respuestaFactura.getEstado());
				respuesta.setMensajeCliente(respuestaFactura.getMensajeCliente());
				respuesta.setMensajeSistema(respuestaFactura.getMensajeSistema());

				break;

			case NOTA_CREDITO:
				fileStr.append("notaCredito");
				fileStr.append(now.getTimeInMillis());
				if (receptarComprobanteDataBean.getData() != null) {
					comprobante = DocumentUtil.createDocument(
							receptarComprobanteDataBean.getData(),
							FechaUtil.formatearFecha(now.getTime(),
									"dd/MM/yyyy"), fileStr.toString(),
							TipoComprobante.NOTA_CREDITO.getDescripcion(),
							"recibidoManual");
				}
				if (receptarComprobanteDataBean.getDataPdf() != null) {
					comprobantePDF = DocumentUtil.createDocumentPdf(
							receptarComprobanteDataBean.getDataPdf(),
							FechaUtil.formatearFecha(now.getTime(),
									"dd/MM/yyyy"), fileStr.toString(),
							TipoComprobante.NOTA_CREDITO.getDescripcion(),
							"recibidoManual", sessionBean
									.getDirectorioServidor());
				}

				NotaCredito_Service serviceNotaCredito = new NotaCredito_Service();
				NotaCreditoPortType portNotaCredito = serviceNotaCredito
						.getNotaCreditoPort();

				NotaCreditoRecibirRequest notaCreditoRecibirRequest = new NotaCreditoRecibirRequest();
				if (comprobante != null) {
					notaCreditoRecibirRequest
							.setComprobanteProveedor(comprobante);
				}
				if (comprobantePDF != null) {
					notaCreditoRecibirRequest
							.setComprobanteProveedorPDF(comprobantePDF);
				}
				if (receptarComprobanteDataBean.getProceso().equals(
						RimEnum.REIM)) {
					notaCreditoRecibirRequest.setProceso(receptarComprobanteDataBean.getProceso().name());
				}
				NotaCreditoRecibirResponse respuestaNotaCredito = portNotaCredito.recibir(notaCreditoRecibirRequest);

				respuesta.setClaveAccesoComprobante(receptarComprobanteDataBean.getName());
				respuesta.setEstado(respuestaNotaCredito.getEstado());
				respuesta.setMensajeCliente(respuestaNotaCredito.getMensajeCliente());
				respuesta.setMensajeSistema(respuestaNotaCredito.getMensajeSistema());

				break;

			}
			receptarComprobanteDataBean.getRespuestas().add(respuesta);
			log.debug(respuesta.getMensajeSistema());

		} catch (Exception ex) {
			ex.printStackTrace();
			respuesta = new GizloResponse();
			respuesta.setClaveAccesoComprobante(receptarComprobanteDataBean
					.getName());
			respuesta.setMensajeCliente("Ha ocurrido un error en el proceso");
			respuesta.setMensajeSistema(ex.getMessage());
			respuesta.setEstado("ERROR");

		}
	}

	public List<SelectItem> getTipoComprobanteDesList() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoComprobante tipoComprobanteDes : TipoComprobante.values()) {
			if (tipoComprobanteDes.equals(TipoComprobante.FACTURA)
					|| tipoComprobanteDes.equals(TipoComprobante.NOTA_CREDITO)) {
				items.add(new SelectItem(tipoComprobanteDes, tipoComprobanteDes
						.getEtiqueta()));
			}

		}
		return items;
	}

	public List<SelectItem> getRim() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (RimEnum tipoComprobanteDes : RimEnum.values()) {
			items.add(new SelectItem(tipoComprobanteDes, tipoComprobanteDes
					.getDescripcion()));
		}
		return items;
	}

	/**
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static void createDirectory(String path)
			throws FileNotFoundException {
		StringBuffer pathname = new StringBuffer(path);

		if (!path.trim().endsWith("/")) {
			pathname.append("/");
		}
		log.debug("Directorio URL: " + pathname.toString());
		File directory = new File(pathname.toString());
		// log.debug(pathname.toString());
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new FileNotFoundException();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void prueba() throws Exception {
		// Converter converter = null;
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");// dd/MM/yyyy
		SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd/MM/yyyy");
		Date now = new Date();
		String strDate = sdfDate.format(now);

		com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = new com.gizlocorp.gnvoice.xml.factura.Factura();

		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setSecuencial("12345");

		facturaXML.setInfoTributaria(infoTributaria);

		InfoFactura infoFactura = new InfoFactura();
		infoFactura.setFechaEmision("01/09/2014");
		Date fechaEmision = sdfDate2.parse(infoFactura.getFechaEmision());
		infoFactura.setFechaEmision(sdfDate.format(fechaEmision));
		infoFactura.setTotalConImpuestos(new TotalConImpuestos());
		TotalImpuesto totalConImpuesto = new TotalImpuesto();
		totalConImpuesto.setCodigoPorcentaje("0");
		totalConImpuesto.setTarifa(new BigDecimal(0));
		totalConImpuesto.setBaseImponible(new BigDecimal(30));
		totalConImpuesto.setValor(new BigDecimal(0));
		infoFactura.getTotalConImpuestos().getTotalImpuesto()
				.add(totalConImpuesto);
		TotalImpuesto totalConImpuesto2 = new TotalImpuesto();
		totalConImpuesto2.setCodigoPorcentaje("2");
		totalConImpuesto2.setTarifa(new BigDecimal(12));
		totalConImpuesto2.setBaseImponible(new BigDecimal(60));
		totalConImpuesto2.setValor(new BigDecimal(7.20));
		infoFactura.getTotalConImpuestos().getTotalImpuesto()
				.add(totalConImpuesto2);

		facturaXML.setInfoFactura(infoFactura);

		Detalle detalleXML = new Detalle();
		detalleXML.setCantidad(new BigDecimal(3));
		detalleXML.setCodigoPrincipal("23rfh");
		detalleXML.setPrecioUnitario(new BigDecimal(10));
		detalleXML.setPrecioTotalSinImpuesto(new BigDecimal(30));

		detalleXML.setImpuestos(new Impuestos());
		Impuesto impuesto = new Impuesto();
		impuesto.setCodigoPorcentaje("0");
		impuesto.setTarifa(new BigDecimal(0));
		impuesto.setValor(new BigDecimal(30));
		detalleXML.getImpuestos().getImpuesto().add(impuesto);

		Detalle detalleXML2 = new Detalle();
		detalleXML2.setCantidad(new BigDecimal(3));
		detalleXML2.setCodigoPrincipal("23rfh2");
		detalleXML2.setPrecioUnitario(new BigDecimal(20));
		detalleXML2.setPrecioTotalSinImpuesto(new BigDecimal(60));

		detalleXML2.setImpuestos(new Impuestos());
		Impuesto impuesto2 = new Impuesto();
		impuesto2.setCodigoPorcentaje("2");
		impuesto2.setTarifa(new BigDecimal(12));
		impuesto2.setValor(new BigDecimal(7.20));
		detalleXML2.getImpuestos().getImpuesto().add(impuesto2);

		Detalle detalleXML3 = new Detalle();
		detalleXML3.setCantidad(new BigDecimal(3));
		detalleXML3.setCodigoPrincipal("23rfh");
		detalleXML3.setPrecioUnitario(new BigDecimal(10));
		detalleXML3.setPrecioTotalSinImpuesto(new BigDecimal(30));

		detalleXML3.setImpuestos(new Impuestos());
		Impuesto impuesto3 = new Impuesto();
		impuesto3.setCodigoPorcentaje("0");
		impuesto3.setTarifa(new BigDecimal(0));
		impuesto3.setValor(new BigDecimal(30));
		detalleXML3.getImpuestos().getImpuesto().add(impuesto3);

		Detalle detalleXML4 = new Detalle();
		detalleXML4.setCantidad(new BigDecimal(3));
		detalleXML4.setCodigoPrincipal("23rfh2");
		detalleXML4.setPrecioUnitario(new BigDecimal(20));
		detalleXML4.setPrecioTotalSinImpuesto(new BigDecimal(60));

		detalleXML4.setImpuestos(new Impuestos());
		Impuesto impuesto4 = new Impuesto();
		impuesto4.setCodigoPorcentaje("2");
		impuesto4.setTarifa(new BigDecimal(12));
		impuesto4.setValor(new BigDecimal(7.20));
		detalleXML4.getImpuestos().getImpuesto().add(impuesto4);

		facturaXML.setDetalles(new Detalles());
		facturaXML.getDetalles().getDetalle().add(detalleXML);
		facturaXML.getDetalles().getDetalle().add(detalleXML2);
		facturaXML.getDetalles().getDetalle().add(detalleXML3);
		facturaXML.getDetalles().getDetalle().add(detalleXML4);

		int num = 2;
		ArrayList list = new ArrayList();
		if (facturaXML.getDetalles().getDetalle() != null
				&& !facturaXML.getDetalles().getDetalle().isEmpty()) {
			for (Detalle itera : facturaXML.getDetalles().getDetalle()) {
				num++;
				Map map = new HashMap();
				map.put("TDETL", "TDETL");
				String linea = completar(String.valueOf(num).length(), num);
				map.put("linea", linea);
				map.put("UPC",
						completarEspaciosEmblancosLetras(ConstantesRim.UPC, 0,
								""));
				map.put("UPC_Supplement",
						completarEspaciosEmblancosLetras(
								ConstantesRim.UPC_Supplement, 0, ""));
				map.put("Item",
						completarEspaciosEmblancosLetras(ConstantesRim.Item,
								itera.getCodigoPrincipal().length(),
								itera.getCodigoPrincipal()));
				map.put("VPN",
						completarEspaciosEmblancosLetras(ConstantesRim.VPN, 0,
								""));
				map.put("Original_Document_Quantity",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Original_Document_Quantity,
								String.valueOf(itera.getPrecioUnitario())
										.length(), String.valueOf(itera
										.getPrecioUnitario())));
				map.put("Original_Unit_Cost",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Original_Unit_Cost, String
										.valueOf(itera.getCantidad()).length(),
								String.valueOf(itera.getCantidad())));
				if (itera.getImpuestos().getImpuesto() != null
						&& !itera.getImpuestos().getImpuesto().isEmpty()) {
					for (Impuesto item : itera.getImpuestos().getImpuesto()) {
						if (item.getCodigoPorcentaje().equals("0")) {
							map.put("Original_VAT_Code",
									completarEspaciosEmblancosLetras(
											ConstantesRim.Original_VAT_Code,
											"L".length(), "L"));
							map.put("Original_VAT_rate",
									completarEspaciosEmblancosNumeros(
											ConstantesRim.Original_VAT_rate,
											"0".length(), "0"));
						} else {
							if (item.getCodigoPorcentaje().equals("2")) {
								map.put("Original_VAT_Code",
										completarEspaciosEmblancosLetras(
												ConstantesRim.Original_VAT_Code,
												"E".length(), "E"));
								map.put("Original_VAT_rate",
										completarEspaciosEmblancosNumeros(
												ConstantesRim.Original_VAT_rate,
												"12".length(), "12"));
							}
						}
					}
				}

				map.put("Total_Allowance",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Total_Allowance, "0".length(),
								"0"));

				list.add(map);
			}
		}
		ArrayList listTotalImpuesto = new ArrayList();
		if (facturaXML.getInfoFactura().getTotalConImpuestos()
				.getTotalImpuesto() != null
				&& !facturaXML.getInfoFactura().getTotalConImpuestos()
						.getTotalImpuesto().isEmpty()) {
			for (TotalImpuesto impu : facturaXML.getInfoFactura()
					.getTotalConImpuestos().getTotalImpuesto()) {
				num++;
				Map map = new HashMap();
				map.put("TVATS", "TVATS");
				String linea = completar(String.valueOf(num).length(), num);
				map.put("linea", linea);
				if (impu.getCodigoPorcentaje().equals("0")) {
					map.put("VAT_code",
							completarEspaciosEmblancosLetras(
									ConstantesRim.VAT_code, "L".length(), "L"));
				} else {
					if (impu.getCodigoPorcentaje().equals("2")) {
						map.put("VAT_code",
								completarEspaciosEmblancosLetras(
										ConstantesRim.VAT_code, "E".length(),
										"E"));
					}
				}
				map.put("VAT_rate",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.VAT_rate,
								String.valueOf(impu.getTarifa()).length(),
								String.valueOf(impu.getTarifa())));
				map.put("Cost_VAT_code",
						completarEspaciosEmblancosNumeros(
								ConstantesRim.Cost_VAT_code,
								String.valueOf(impu.getBaseImponible())
										.length(), String.valueOf(impu
										.getBaseImponible())));

				listTotalImpuesto.add(map);

			}
		}

		int fdetalle = num + 1;
		int fin = num + 2;
		int numLinea = num - 2;
		int totalLinea = num;

		Velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
				"C:\\Users\\Usuario\\Desktop\\velocity\\");
		Velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
				"true");

		Velocity.init();

		Template t = Velocity.getTemplate("helloworld.vm");
		VelocityContext context = new VelocityContext();
		context.put("fecha", strDate);
		context.put("petList", list);
		// THEAD
		context.put(
				"Document_Type",
				completarEspaciosEmblancosLetras(ConstantesRim.Document_Type,
						"MRCHI".length(), "MRCHI"));
		context.put(
				"Vendor_Document_Number",
				completarEspaciosEmblancosLetras(
						ConstantesRim.Vendor_Document_Number, "".length(), ""));
		context.put(
				"Group_ID",
				completarEspaciosEmblancosLetras(ConstantesRim.Group_ID,
						"".length(), ""));
		context.put(
				"Vendor_Type",
				completarEspaciosEmblancosLetras(ConstantesRim.Vendor_Type,
						"SUPP".length(), "SUPP"));
		context.put(
				"Vendor_ID",
				completarEspaciosEmblancosLetras(ConstantesRim.Vendor_ID,
						"".length(), ""));
		context.put(
				"Vendor_Document_Date",
				completarEspaciosEmblancosLetras(
						ConstantesRim.Vendor_Document_Date, facturaXML
								.getInfoFactura().getFechaEmision().length(),
						facturaXML.getInfoFactura().getFechaEmision()));
		context.put(
				"Order_Number",
				completarEspaciosEmblancosNumeros(ConstantesRim.Order_Number,
						String.valueOf(12345).length(), String.valueOf(12345)));
		context.put(
				"Location",
				completarEspaciosEmblancosNumeros(ConstantesRim.Location,
						String.valueOf("").length(), String.valueOf("")));
		context.put(
				"Location_Type",
				completarEspaciosEmblancosLetras(ConstantesRim.Location_Type,
						"".length(), ""));
		context.put(
				"Terms",
				completarEspaciosEmblancosLetras(ConstantesRim.Terms,
						"".length(), ""));
		context.put(
				"Due_Date",
				completarEspaciosEmblancosLetras(ConstantesRim.Due_Date,
						"".length(), ""));
		context.put(
				"Payment_method",
				completarEspaciosEmblancosLetras(ConstantesRim.Payment_method,
						"".length(), ""));
		context.put(
				"Currency_code",
				completarEspaciosEmblancosLetras(ConstantesRim.Currency_code,
						"".length(), ""));
		context.put(
				"Exchange_rate",
				completarEspaciosEmblancosNumeros(ConstantesRim.Exchange_rate,
						String.valueOf("0").length(), String.valueOf("0")));
		context.put(
				"Total_Cost",
				completarEspaciosEmblancosNumeros(ConstantesRim.Total_Cost,
						String.valueOf("120").length(), String.valueOf("120")));
		context.put(
				"Total_VAT_Amount",
				completarEspaciosEmblancosNumeros(
						ConstantesRim.Total_VAT_Amount, String.valueOf("0")
								.length(), String.valueOf("0")));
		context.put(
				"Total_Quantity",
				completarEspaciosEmblancosNumeros(ConstantesRim.Total_Quantity,
						String.valueOf("0").length(), String.valueOf("0")));
		context.put(
				"Total_Discount",
				completarEspaciosEmblancosNumeros(ConstantesRim.Total_Discount,
						String.valueOf("0").length(), String.valueOf("0")));
		context.put(
				"Freight_Type",
				completarEspaciosEmblancosLetras(ConstantesRim.Freight_Type,
						"".length(), ""));
		context.put("Paid_Ind", "N");
		context.put("Multi_Location", "N");
		context.put("Merchan_dise_Type", "N");
		context.put(
				"Deal_Id",
				completarEspaciosEmblancosLetras(ConstantesRim.Deal_Id,
						"".length(), ""));
		context.put(
				"Deal_Approval_Indicator",
				completarEspaciosEmblancosLetras(
						ConstantesRim.Deal_Approval_Indicator, "".length(), ""));
		context.put("RTV_indicator", "N");

		context.put("petListTotalImpuesto", listTotalImpuesto);
		context.put("secuencial", facturaXML.getInfoTributaria()
				.getSecuencial());
		context.put("vendorType", "SUPP");// preguntar donde lo tomo
		context.put("vendorId", "20297");// preguntar donde lo tomo

		// lineas finales
		context.put("finT",
				completar(String.valueOf(fdetalle).length(), fdetalle));
		context.put("finF", completar(String.valueOf(fin).length(), fin));
		context.put(
				"numerolineaDestalle",
				completarLineaDetalle(String.valueOf(numLinea).length(),
						numLinea));
		context.put("totalLinea",
				completar(String.valueOf(totalLinea).length(), totalLinea));
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		System.out.println(writer.toString());
		DocumentUtil.createDocumentText(writer.toString().getBytes(), "0001",
				"2", "C:");
	}

	public String completar(int num, int linea) {
		StringBuilder secuencial = new StringBuilder();

		int secuencialTam = 10 - num;
		for (int i = 0; i < secuencialTam; i++) {
			secuencial.append("0");
		}
		String retorno = secuencial.append(String.valueOf(linea)).toString();
		return retorno;
	}

	public String completarLineaDetalle(int num, int linea) {
		StringBuilder secuencial = new StringBuilder();

		int secuencialTam = 6 - num;
		for (int i = 0; i < secuencialTam; i++) {
			secuencial.append("0");
		}
		String retorno = secuencial.append(String.valueOf(linea)).toString();
		return retorno;
	}

	public String completarEspaciosEmblancosNumeros(int numEspacios,
			int numOcupados, String valor) {
		StringBuilder secuencial = new StringBuilder();
		if (numEspacios >= numOcupados) {
			int secuencialTam = numEspacios - numOcupados;
			for (int i = 0; i < secuencialTam; i++) {
				secuencial.append(" ");
			}
		}
		secuencial.append(valor).toString();
		return secuencial.toString();
	}

	public String completarEspaciosEmblancosLetras(int numEspacios,
			int numOcupados, String valor) {
		StringBuilder secuencial = new StringBuilder();
		secuencial.append(valor).toString();
		if (numEspacios >= numOcupados) {
			int secuencialTam = numEspacios - numOcupados;
			for (int i = 0; i < secuencialTam; i++) {
				secuencial.append(" ");
			}
		}

		return secuencial.toString();
	}

}