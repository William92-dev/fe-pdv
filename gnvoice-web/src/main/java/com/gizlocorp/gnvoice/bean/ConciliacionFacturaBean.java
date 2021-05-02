package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.inject.Named;

import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("conciliacionFacturaBean")
public class ConciliacionFacturaBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;
	//
	// private Converter converter;
	//
	// private static final ContentTypeEnum XML_CONTENT_TYPE =
	// ContentTypeEnum.XML;
	//
	// private static Logger log =
	// Logger.getLogger(ConciliacionFacturaBean.class
	// .getName());
	//
	// private String agencia;
	//
	// private List<Factura> facturas;

//	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
//	ServicioFactura servicioFactura;

	//@EJB(lookup = "java:global/gnvoice-sca/ServicioConciliacionImpl!com.gizlocorp.gnvoiceFybeca.servicio.local.ServicioConciliacionLocal")
	//ServicioConciliacionLocal servicioConciliacion;
	//
	// public void actualizarDocumentos() {
	// try {
	// List<Factura> facturasActualizar = servicioFactura
	// .consultarComprobantesSeguimiento("AUTORIZADO", null, null,
	// null, null, null, null, null, agencia, null, null,
	// "2", null, null);
	//
	// if (facturasActualizar != null && !facturasActualizar.isEmpty()) {
	// for (Factura factura : facturasActualizar) {
	// if (factura.getSecuencialOriginal() != null
	// && !factura.getSecuencialOriginal().isEmpty()) {
	// Respuesta respuesta = new Respuesta();
	// respuesta.setClaveAcceso(factura.getClaveAcceso());
	// respuesta.setCorreoElectronico(factura
	// .getCorreoNotificacion());
	//
	// respuesta.setEstado(EstadoEnum.APROBADO
	// .getDescripcion());
	//
	// respuesta.setFecha(Calendar.getInstance().getTime());
	// respuesta.setFirmaE(GeneradoEnum.SI.getDescripcion());
	// respuesta.setAutFirmaE(factura.getNumeroAutorizacion());
	// respuesta.setComprobanteFirmaE(factura
	// .getNumeroAutorizacion());
	// respuesta
	// .setFechaFirmaE(factura.getFechaAutorizacion() != null ? factura
	// .getFechaAutorizacion() : null);
	//
	// respuesta.setIdDocumento(Long.valueOf(factura
	// .getClaveInterna()));
	// respuesta.setIdFarmacia(Long.valueOf(factura
	// .getAgencia()));
	// respuesta.setObservacion("Comprobante AUTORIZADO");
	// respuesta.setTipoProceso(ProcesoEnum.BATCH
	// .getDescripcion());
	// respuesta.setUsuario("");
	//
	// servicioConciliacion.actualizarDocumento(respuesta,
	// factura.getSecuencialOriginal());
	//
	// }
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// errorMessage("Ha ocurrido un error al listar Facturas");
	// log.error(e.getMessage(), e);
	// }
	// }
	//
	// public void conciliar() {
	// try {
	// List<Factura> facturasConcilidadas = servicioFactura
	// .obtenerComprobantesConciliacion(agencia);
	//
	// if (facturasConcilidadas != null && !facturasConcilidadas.isEmpty()) {
	// InfoConciliacion info = null;
	// int contador = 0;
	// for (Factura factura : facturasConcilidadas) {
	// try {
	// info = servicioConciliacion.obtenerInfoConciliacion(
	// Long.valueOf(factura.getAgencia()),
	// Long.valueOf(factura.getClaveInterna()),
	// factura.getSecuencialOriginal());
	//
	// if (info.getSecuencial() == null
	// || info.getSecuencial().isEmpty()
	// || !factura.getCodSecuencial().equals(
	// info.getSecuencial())) {
	// factura.setRequiereCancelacion(Logico.S);
	// factura.setObservacionCancelacion("No existe secuencial en BD Fybeca "
	// + info.getSecuencial() != null ? info
	// .getSecuencial() : "");
	// factura.setSecuencialNotaCredito(info
	// .getSecuencialNotaCredito());
	// servicioFactura.actualizarConciliacion(factura);
	// } else {
	// factura.setRequiereCancelacion(Logico.N);
	// factura.setObservacionCancelacion("Comprobante correcto en conciliacion");
	// servicioFactura.actualizarConciliacion(factura);
	// }
	// contador++;
	// } catch (Exception e) {
	// log.error(
	// "Error en conciliacion"
	// + factura.getClaveInterna()
	// + e.getMessage(), e);
	// }
	// }
	// infoMessage("Se conciliaron " + contador + " Facturas");
	//
	// }
	//
	// buscarFacturaRequiereAnulacion();
	//
	// } catch (Exception e) {
	// errorMessage("Ha ocurrido un error al listar Facturas");
	// log.error(e.getMessage(), e);
	// }
	// }
	//
	// public void cancelar() {
	// try {
	// // facturas = servicioFactura.obtenerRequireCancelacion(agencia);
	//
	// if (facturas != null && !facturas.isEmpty()) {
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito notaCredito = null;
	// com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = null;
	// String facturaStr = null;
	// String autorizacion = null;
	// Autorizacion autorizacionXML = null;
	//
	// for (Factura factura : facturas) {
	// log.debug("secuencial nota credito: "
	// + factura.getSecuencialNotaCredito());
	// try {
	// if (Estado.AUTORIZADO.getDescripcion().equals(
	// factura.getEstado())) {
	// if (factura.getSecuencialNotaCredito() != null) {
	// // genera nota de credito
	// autorizacion = DocumentUtil
	// .readContentFile(factura.getArchivo());
	//
	// this.converter = ConverterFactory
	// .getConverter(XML_CONTENT_TYPE);
	// autorizacionXML = this.converter
	// .convertirAObjeto(autorizacion,
	// Autorizacion.class);
	//
	// facturaStr = autorizacionXML.getComprobante();
	// facturaStr = facturaStr
	// .replace("<![CDATA[", "");
	// facturaStr = facturaStr.replace("]]>", "");
	//
	// facturaXML = this.converter
	// .convertirAObjeto(
	// facturaStr,
	// com.gizlocorp.gnvoice.xml.factura.Factura.class);
	//
	// notaCredito = new NotaCredito();
	// com.gizlocorp.gnvoice.xml.InfoTributaria infoTributaria = new
	// com.gizlocorp.gnvoice.xml.InfoTributaria();
	// infoTributaria.setDirMatriz(StringEscapeUtils
	// .escapeXml(facturaXML
	// .getInfoTributaria()
	// .getDirMatriz()));
	// infoTributaria.setEstab(facturaXML
	// .getInfoTributaria().getEstab());
	// infoTributaria.setNombreComercial(facturaXML
	// .getInfoTributaria()
	// .getNombreComercial());
	// infoTributaria.setPtoEmi(facturaXML
	// .getInfoTributaria().getPtoEmi());
	// infoTributaria.setRazonSocial(facturaXML
	// .getInfoTributaria().getRazonSocial());
	// infoTributaria.setRuc(facturaXML
	// .getInfoTributaria().getRuc());
	// infoTributaria.setSecuencial(factura
	// .getSecuencialNotaCredito());
	// infoTributaria
	// .setTipoEmision(TipoEmisionEnum.NORMAL
	// .getCodigo());
	//
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles detalles = new
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles();
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle
	// detalle = null;
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle.Impuestos
	// impuestos = new
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle.Impuestos();
	// com.gizlocorp.gnvoice.xml.notacredito.Impuesto impuesto = null;
	//
	// for (com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle
	// detalleFac : facturaXML
	// .getDetalles().getDetalle()) {
	// detalle = new
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle();
	// detalle.setCantidad(detalleFac
	// .getCantidad());
	// detalle.setCodigoAdicional(detalleFac
	// .getCodigoAuxiliar());
	// detalle.setCodigoInterno(detalleFac
	// .getCodigoPrincipal());
	// detalle.setDescripcion(StringEscapeUtils
	// .escapeXml(detalleFac
	// .getDescripcion()));
	// detalle.setDescuento(detalleFac
	// .getDescuento());
	//
	// impuestos = new
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle.Impuestos();
	//
	// for (com.gizlocorp.gnvoice.xml.factura.Impuesto impuestoFac : detalleFac
	// .getImpuestos().getImpuesto()) {
	// impuesto = new com.gizlocorp.gnvoice.xml.notacredito.Impuesto();
	// impuesto.setBaseImponible(impuestoFac
	// .getBaseImponible());
	// impuesto.setCodigo(impuestoFac
	// .getCodigo());
	// impuesto.setCodigoPorcentaje(impuestoFac
	// .getCodigoPorcentaje());
	// impuesto.setTarifa(impuestoFac
	// .getTarifa());
	// impuesto.setValor(impuestoFac
	// .getValor());
	//
	// impuestos.getImpuesto().add(impuesto);
	//
	// }
	//
	// detalle.setImpuestos(impuestos);
	// detalle.setPrecioTotalSinImpuesto(detalleFac
	// .getPrecioTotalSinImpuesto());
	// detalle.setPrecioUnitario(detalleFac
	// .getPrecioUnitario());
	//
	// detalles.getDetalle().add(detalle);
	//
	// }
	//
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito
	// infoNotaCredito = new
	// com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito();
	// infoNotaCredito.setCodDocModificado(facturaXML
	// .getInfoTributaria().getCodDoc());
	// infoNotaCredito
	// .setContribuyenteEspecial(facturaXML
	// .getInfoFactura()
	// .getContribuyenteEspecial());
	// infoNotaCredito
	// .setDirEstablecimiento(facturaXML
	// .getInfoFactura()
	// .getDirEstablecimiento());
	// infoNotaCredito
	// .setFechaEmision(obtenerFechaValida(facturaXML
	// .getInfoFactura()
	// .getFechaEmision()));
	// infoNotaCredito
	// .setFechaEmisionDocSustento(facturaXML
	// .getInfoFactura()
	// .getFechaEmision());
	// infoNotaCredito
	// .setIdentificacionComprador(facturaXML
	// .getInfoFactura()
	// .getIdentificacionComprador());
	// infoNotaCredito.setMoneda(facturaXML
	// .getInfoFactura().getMoneda());
	// infoNotaCredito
	// .setMotivo("Documento autorizado como Autorimpreso");
	// infoNotaCredito.setNumDocModificado(facturaXML
	// .getInfoTributaria().getEstab()
	// + "-"
	// + facturaXML.getInfoTributaria()
	// .getPtoEmi()
	// + "-"
	// + facturaXML.getInfoTributaria()
	// .getSecuencial());
	// infoNotaCredito
	// .setObligadoContabilidad(facturaXML
	// .getInfoFactura()
	// .getObligadoContabilidad());
	// infoNotaCredito
	// .setRazonSocialComprador(facturaXML
	// .getInfoFactura()
	// .getRazonSocialComprador());
	// infoNotaCredito
	// .setTipoIdentificacionComprador(facturaXML
	// .getInfoFactura()
	// .getTipoIdentificacionComprador());
	//
	// com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos totalConImpuesto
	// = new com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos();
	// com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto
	// totalImpuesto = null;
	//
	// for
	// (com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto
	// impuestoFac : facturaXML
	// .getInfoFactura()
	// .getTotalConImpuestos()
	// .getTotalImpuesto()) {
	// totalImpuesto = new
	// com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto();
	// totalImpuesto.setBaseImponible(impuestoFac
	// .getBaseImponible());
	// totalImpuesto.setCodigo(impuestoFac
	// .getCodigo());
	// totalImpuesto
	// .setCodigoPorcentaje(impuestoFac
	// .getCodigoPorcentaje());
	// totalImpuesto.setValor(impuestoFac
	// .getValor());
	//
	// totalConImpuesto.getTotalImpuesto().add(
	// totalImpuesto);
	//
	// }
	//
	// infoNotaCredito
	// .setTotalConImpuestos(totalConImpuesto);
	// infoNotaCredito.setTotalSinImpuestos(facturaXML
	// .getInfoFactura()
	// .getTotalSinImpuestos());
	// infoNotaCredito.setValorModificacion(facturaXML
	// .getInfoFactura().getImporteTotal());
	//
	// notaCredito.setDetalles(detalles);
	// notaCredito.setInfoNotaCredito(infoNotaCredito);
	// notaCredito.setInfoTributaria(infoTributaria);
	//
	// NotaCredito_Service service = new NotaCredito_Service();
	// NotaCreditoPortType port = service
	// .getNotaCreditoPort();
	//
	// ((BindingProvider) port).getRequestContext()
	// .put("com.sun.xml.ws.connect.timeout",
	// 180000);
	// ((BindingProvider) port)
	// .getRequestContext()
	// .put("com.sun.xml.internal.ws.connect.timeout",
	// 180000);
	//
	// ((BindingProvider) port)
	// .getRequestContext()
	// .put("com.sun.xml.internal.ws.request.timeout",
	// 180000);
	// ((BindingProvider) port).getRequestContext()
	// .put("com.sun.xml.ws.request.timeout",
	// 180000);
	//
	// NotaCreditoProcesarRequest notaCreditoProcesarRequest = new
	// NotaCreditoProcesarRequest();
	// notaCreditoProcesarRequest
	// .setCorreoElectronicoNotificacion(factura
	// .getCorreoNotificacion());
	// notaCreditoProcesarRequest
	// .setIdentificadorUsuario(factura
	// .getIdentificadorUsuario());
	// notaCreditoProcesarRequest.setAgencia(factura
	// .getAgencia());
	// notaCreditoProcesarRequest
	// .setCodigoExterno(factura
	// .getClaveInterna());
	// notaCreditoProcesarRequest
	// .setNotaCredito(notaCredito);
	//
	// NotaCreditoProcesarResponse response = port
	// .procesar(notaCreditoProcesarRequest);
	//
	// if (response != null
	// && response.getEstado().equals(
	// Estado.AUTORIZADO
	// .getDescripcion())) {
	// // cancela en GNVOICE
	// factura.setEstado(Estado.CANCELADO
	// .getDescripcion());
	// servicioFactura
	// .finalizarTarea(factura, Tarea.AUT,
	// Estado.CANCELADO,
	// "Comprobante Cancelado por Conciliacion");
	// // cancela en FYBECA
	// Respuesta respuestaCancelar = new Respuesta();
	// respuestaCancelar.setClaveAcceso(factura
	// .getClaveAcceso());
	// respuestaCancelar
	// .setCorreoElectronico(factura
	// .getCorreoNotificacion());
	//
	// respuestaCancelar
	// .setEstado(EstadoEnum.RECHAZADO
	// .getDescripcion());
	//
	// respuestaCancelar.setFecha(Calendar
	// .getInstance().getTime());
	// respuestaCancelar.setFirmaE(GeneradoEnum.NO
	// .getDescripcion());
	//
	// respuestaCancelar
	// .setIdDocumento(Long
	// .valueOf(factura
	// .getClaveInterna()));
	// respuestaCancelar.setIdFarmacia(Long
	// .valueOf(factura.getAgencia()));
	// respuestaCancelar
	// .setObservacion("Cancelado por conciliacion");
	// respuestaCancelar
	// .setTipoProceso(ProcesoEnum.LINEA
	// .getDescripcion());
	// respuestaCancelar.setUsuario("");
	//
	// servicioConciliacion.cancelarDocumento(
	// respuestaCancelar, null,
	// factura.getSecuencialOriginal());
	//
	// Respuesta respuesta = new Respuesta();
	// respuesta
	// .setIdDocumento(Long
	// .valueOf(factura
	// .getClaveInterna()));
	// respuesta.setIdFarmacia(Long
	// .valueOf(factura.getAgencia()));
	// respuesta.setAutFirmaE(response
	// .getNumeroAutorizacion());
	// respuesta.setClaveAcceso(response
	// .getClaveAccesoComprobante());
	// respuesta.setComprobanteFirmaE(response
	// .getNumeroAutorizacion());
	// respuesta.setCorreoElectronico(factura
	// .getCorreoNotificacion());
	// // respuesta.setEstado(response.getEstado());
	//
	// respuesta.setEstado(EstadoEnum.APROBADO
	// .getDescripcion());
	//
	// respuesta.setFecha(Calendar.getInstance()
	// .getTime());
	// respuesta
	// .setFechaFirmaE(response
	// .getFechaAutorizacion() != null ? response
	// .getFechaAutorizacion()
	// .toGregorianCalendar()
	// .getTime()
	// : null);
	//
	// respuesta.setFirmaE(GeneradoEnum.SI
	// .getDescripcion());
	//
	// respuestaCancelar
	// .setIdDocumento(Long
	// .valueOf(factura
	// .getClaveInterna()));
	// respuestaCancelar.setIdFarmacia(Long
	// .valueOf(factura.getAgencia()));
	// respuesta.setObservacion(response
	// .getMensajeSistema());
	// respuesta.setTipoProceso(ProcesoEnum.LINEA
	// .getDescripcion());
	// respuesta.setUsuario("");
	//
	// servicioConciliacion.cancelarDocumento(
	// respuestaCancelar, respuesta,
	// factura.getSecuencialOriginal());
	// }
	// }
	//
	// } else {
	// // cancela en GNVOICE
	// factura.setEstado(Estado.CANCELADO.getDescripcion());
	// servicioFactura.finalizarTarea(factura, Tarea.AUT,
	// Estado.CANCELADO,
	// "Comprobante Cancelado por Conciliacion");
	// // cancela en FYBECA
	// Respuesta respuestaCancelar = new Respuesta();
	// respuestaCancelar.setClaveAcceso(factura
	// .getClaveAcceso());
	// respuestaCancelar.setCorreoElectronico(factura
	// .getCorreoNotificacion());
	//
	// respuestaCancelar.setEstado(EstadoEnum.RECHAZADO
	// .getDescripcion());
	//
	// respuestaCancelar.setFecha(Calendar.getInstance()
	// .getTime());
	// respuestaCancelar.setFirmaE(GeneradoEnum.NO
	// .getDescripcion());
	//
	// respuestaCancelar.setIdDocumento(Long
	// .valueOf(factura.getClaveInterna()));
	// respuestaCancelar.setIdFarmacia(Long
	// .valueOf(factura.getAgencia()));
	// respuestaCancelar
	// .setObservacion("Cancelado por conciliacion");
	// respuestaCancelar.setTipoProceso(ProcesoEnum.LINEA
	// .getDescripcion());
	// respuestaCancelar.setUsuario("");
	//
	// servicioConciliacion.cancelarDocumento(
	// respuestaCancelar, null,
	// factura.getSecuencialOriginal());
	// }
	// } catch (Exception e) {
	// log.error("Error en Cancelación");
	// }
	// }
	// }
	// } catch (Exception e) {
	// errorMessage("Ha ocurrido un error al listar Facturas");
	// log.error(e.getMessage(), e);
	// }
	//
	// buscarFacturaRequiereAnulacion();
	// }
	//
	// public void buscarFacturaRequiereAnulacion() {
	// try {
	// facturas = servicioFactura.obtenerRequireCancelacion(agencia);
	// } catch (Exception e) {
	// errorMessage("Ha ocurrido un error al listar Facturas");
	// log.error(e.getMessage(), e);
	// }
	// }
	//
	// public String getAgencia() {
	// return agencia;
	// }
	//
	// public void setAgencia(String agencia) {
	// this.agencia = agencia;
	// }
	//
	// public List<Factura> getFacturas() {
	// return facturas;
	// }
	//
	// public void setFacturas(List<Factura> facturas) {
	// this.facturas = facturas;
	// }
	//
	// private String obtenerFechaValida(String fecha) {
	// try {
	// Calendar cal = Calendar.getInstance();
	// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	// cal.setTime(sdf.parse(fecha));
	//
	// Calendar validate = Calendar.getInstance();
	// validate.add(Calendar.MINUTE, -43201);
	//
	// if (cal.before(validate)) {
	// return sdf.format(validate.getTime());
	// } else {
	// return sdf.format(cal.getTime());
	// }
	// } catch (ParseException e) {
	// log.error(e.getMessage(), e);
	// }
	//
	// return fecha;
	// }

}
