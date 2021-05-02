package com.gizlocorp.gnvoice.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.StringUtil;
import com.gizlocorp.gnvoice.bean.databean.ReceptarFacturaMailDataBean;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.modelo.ColaFileClave;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;
import com.gizlocorp.gnvoice.modelo.NotaCredito;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;
import com.gizlocorp.gnvoice.reporte.ComprobanteRetencionReporte;
import com.gizlocorp.gnvoice.reporte.GuiaRemisionReporte;
import com.gizlocorp.gnvoice.reporte.NotaCreditoReporte;
import com.gizlocorp.gnvoice.servicio.local.ServicioColaFileClave;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCreditoRecepcion;
import com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaRecibirResponse;
import com.gizlocorp.gnvoice.wsclient.factura.GizloResponse;
import com.gizlocorp.gnvoice.wsclient.notacredito.NotaCreditoRecibirResponse;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;

//@Interceptors(CurrentUserGnvoiceProvider.class)
@ViewScoped
@Named("receptarFacturaMailBean")
public class ReceptarFacturaMailBean extends BaseBean implements Serializable {

	public static final Logger log = Logger.getLogger(ReceptarFacturaMailBean.class);

	private static final long serialVersionUID = -6239437588285327644L;

	@Inject
	ReceptarFacturaMailDataBean receptarFacturaMailDataBean;
	
	@EJB
	CacheBean cacheBean;

	@Inject
	private SessionBean sessionBean;

	public static final int BUFFER_SIZE = 4096;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRecepcionFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura")
	ServicioRecepcionFactura servicioFactura;
	
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRecepcionNotaCreditoImpl!com.gizlocorp.gnvoice.servicio.local.ServicioNotaCreditoRecepcion")	
	ServicioNotaCreditoRecepcion servicioNotaCredito;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioParametroOrdenCompraImpl!com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal")
	ServicioParametroOrdenCompraLocal servicioParametroOrdenCompra;

	//WSA INI
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioColaFileClaveImpl!com.gizlocorp.gnvoice.servicio.local.ServicioColaFileClave")
	ServicioColaFileClave servicioColaFileClave;
	//WSA FIN
	
	private FacturaRecepcion factura;
	
	private Boolean validarPantalla;
	
	

	private NotaCreditoRecepcion notaCreditoRecepcion;
	
	Map<String, String> mapComprobante = new HashMap<String, String>();
	
	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@PostConstruct
	public void postContruct() {
		
		try {
		String valorDiaCorte  =  servicioParametro.consultarParametro("DIA_CORTE", null).getValor();
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		String strDate = dateFormat.format(date);
		Integer  intValorDiaCorte  =  Integer.parseInt(valorDiaCorte);
		receptarFacturaMailDataBean.setRespuestas(new ArrayList<GizloResponse>());
		receptarFacturaMailDataBean.setTipoComprobanteDes(TipoComprobante.FACTURA);
		if(!valorDiaCorte.isEmpty() &&  intValorDiaCorte >0 && intValorDiaCorte<=31 && !valorDiaCorte.equals(strDate.split("/")[0]) ){
			validarPantalla=true;
		}else {
			validarPantalla=false;
			errorMessage("Ha ocurrido un error en la paramtrizacion del dia");	
		}
	} catch (GizloException e) {
		e.printStackTrace();
	}
		
		//receptarFacturaMailDataBean.setRespuestas(new ArrayList<GizloResponse>());
		//receptarFacturaMailDataBean.setTipoComprobanteDes(TipoComprobante.FACTURA);
	}
	
	public void cargarDocumento(org.richfaces.event.FileUploadEvent event) {
		org.richfaces.model.UploadedFile item = event.getUploadedFile();
		receptarFacturaMailDataBean.setData(item.getData());
		receptarFacturaMailDataBean.setName(item.getName());
		receptarFacturaMailDataBean.setExtension(item.getFileExtension());

	}

	public void paint(OutputStream stream, Object object) throws IOException {
		stream.write(receptarFacturaMailDataBean.getData());
		stream.close();
	}
	
	public void procesar(){
		InputStream is = new ByteArrayInputStream(receptarFacturaMailDataBean.getData());
		 try {
			 if(receptarFacturaMailDataBean.getTipoComprobanteDes().getDescripcion().equals(TipoComprobante.FACTURA.getDescripcion())){
				 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xlsx".toLowerCase())){
					//WSA INI
					 System.out.println("VA A ENTRA SAVE");
					 //readXLSXFile(is);
					 //enviaRecibirFactura();
					 saveCola(FilenameUtils.getBaseName(receptarFacturaMailDataBean.getName()), receptarFacturaMailDataBean.getExtension(), is);
					 //WSA FIN 
					 System.out.println("SALE SAVE");
					 
				 }
				 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xls".toLowerCase())){
					//WSA INI
					 System.out.println("VA A ENTRA SAVE");
					 //readXLSXFile(is);
					 //enviaRecibirFactura();
					 saveCola(FilenameUtils.getBaseName(receptarFacturaMailDataBean.getName()), receptarFacturaMailDataBean.getExtension(), is);
					 //WSA FIN 
					 System.out.println("SALE SAVE");
				 }
			 }else{
				 if(receptarFacturaMailDataBean.getTipoComprobanteDes().getDescripcion().equals(TipoComprobante.NOTA_CREDITO.getDescripcion())){
					 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xlsx".toLowerCase())){
						 readXLSXFile(is);
						 enviaRecibirNotaCredito();
					 }
					 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xls".toLowerCase())){
						 readXLSFile(is);
						 enviaRecibirNotaCredito();
					 }
				 }
				 
				 
				 if(receptarFacturaMailDataBean.getTipoComprobanteDes().getDescripcion().equals(TipoComprobante.RETENCION.getDescripcion())){
					 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xlsx".toLowerCase())){
						 readXLSXFile(is);
						 enviaRecibirRetencion();
					 }
					 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xls".toLowerCase())){
						 readXLSFile(is);
						 enviaRecibirRetencion();
					 }
				 }
				 
				 
				 if(receptarFacturaMailDataBean.getTipoComprobanteDes().getDescripcion().equals(TipoComprobante.ELIMINAR_FACTURA.getDescripcion())){
					 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xlsx".toLowerCase())){
						 readXLSXFile(is);
						 enviaEliminarFacturas();
					 }
					 if(receptarFacturaMailDataBean.getExtension().toLowerCase().equals("xls".toLowerCase())){
						 readXLSFile(is);
						 enviaEliminarFacturas();
					 }
				 }
				 
			 }			
			 infoMessage("Proceso enviado Exitosamente!", "Proceso enviado Exitosamente!");
			
		} catch (IOException e) {
			errorMessage("Error al Procesar");
			e.printStackTrace();
		}
	}
	
	
	
	public void limpiarFormulario(){
		receptarFacturaMailDataBean.setRespuestas(new ArrayList<GizloResponse>());
		receptarFacturaMailDataBean.setTipoComprobanteDes(TipoComprobante.FACTURA);
		mapComprobante = new HashMap<String, String>();
		
	}
	
	@SuppressWarnings("unused")
	private void enviaRecibirFactura(){
		for (Entry<String, String> e: mapComprobante.entrySet()) {
			factura = servicioFactura.obtenerComprobanteRecepcion(e.getKey(), null, null, null);
			log.info("clave a procesar "+e.getKey());
			if(factura == null ){
				FacturaRecepcion respuestaFactura = tranformaGuardaFactura(e.getKey(), new Date());	
				log.info("clave a procesado exitosamente "+e.getKey());
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void enviaRecibirNotaCredito(){
		for (Entry<String, String> e: mapComprobante.entrySet()) {
			notaCreditoRecepcion = servicioNotaCredito.obtenerComprobante(e.getKey(), null, null, null);
			if(notaCreditoRecepcion == null ){
				NotaCreditoRecepcion respuestaNotaCreditoRecepcion = tranformaGuardaNotaCredito(e.getKey(), new Date());								
			}
		}
	}
	
	

	private void enviaRecibirRetencion(){
		for (Entry<String, String> e: mapComprobante.entrySet()) {
				 tranformaGuardaRetencion(e.getKey(), new Date());								
			
		}
		
		// tranformaGuardaRetencion("1703201907179071031900120010150003364398386559117", new Date());
	}
	
	
	
	

	private void enviaEliminarFacturas(){
		for (Entry<String, String> e: mapComprobante.entrySet()) {
			try {
				servicioFactura.eliminarFacturas(e.getKey());	
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	public com.gizlocorp.gnvoice.modelo.FacturaRecepcion tranformaGuardaFactura(String claveAccesop, Date fechcaTMP){
		com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaObj = new com.gizlocorp.gnvoice.modelo.FacturaRecepcion();
		
		FacturaRecibirResponse respuesta = new FacturaRecibirResponse();

		BigDecimal baseImponible0 = BigDecimal.ZERO;
		BigDecimal baseImponible12 = BigDecimal.ZERO;
		
		BigDecimal baseImponibleICE = BigDecimal.ZERO;
		BigDecimal baseImponibleIRBP = BigDecimal.ZERO;
		BigDecimal ice = BigDecimal.ZERO;
		BigDecimal irbp = BigDecimal.ZERO;
		BigDecimal cantidadTotal = BigDecimal.ZERO;

		BigDecimal iva = BigDecimal.ZERO;
		String subFolder = "comprobante";
		Calendar now = Calendar.getInstance();
		String rutaArchivoXml="";		
		boolean offline = false;
		
		if(!(claveAccesop.length() == 49) ){
			GizloResponse gizloResponse = new GizloResponse();
			gizloResponse.setClaveAccesoComprobante(claveAccesop);
			gizloResponse.setMensajeCliente("No es ina clave de acceso");
			gizloResponse.setMensajeSistema("No es ina clave de acceso");
			receptarFacturaMailDataBean.getRespuestas().add(gizloResponse);
			return null;
		}
		
		try{
			Autorizacion autorizacionXML = null;
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;
			if (claveAccesop != null && !claveAccesop.isEmpty()) {				
				String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";			
				String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";					
				autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveAccesop);					
				autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveAccesop);
				if(autorizacionOfflineXML != null && autorizacionOfflineXML.getComprobante() != null){
					offline = true;
				}
			}			
			com.gizlocorp.gnvoice.xml.factura.Factura facturaXML = null;
			if(offline){
				if (autorizacionOfflineXML == null) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}
				if (!(autorizacionOfflineXML.getComprobante() != null)) {
					
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}				
				String factura = autorizacionOfflineXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
				if(!factura.contains("<factura")){
					GizloResponse gizloResponse = new GizloResponse();
					gizloResponse.setClaveAccesoComprobante(claveAccesop);
					gizloResponse.setMensajeCliente("la clave de acceso no es factura");
					gizloResponse.setMensajeSistema("la clave de acceso no es factura");
					receptarFacturaMailDataBean.getRespuestas().add(gizloResponse);
					return null;
				}
				try{
					facturaXML = getFacturaXML(factura);
				}catch (Exception ex) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
									

					if (offline) {				
						String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";						
						autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveAccesop);
						offline = true;
						String factura3 = autorizacionOfflineXML.getComprobante();
						factura3 = factura3.replace("<![CDATA[", "");
						factura3 = factura3.replace("]]>", "");
						facturaXML = getFacturaXML(factura3);
					} else {
						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveAccesop);
						String factura2 = autorizacionXML.getComprobante();
						factura2 = factura2.replace("<![CDATA[", "");
						factura2 = factura2.replace("]]>", "");
						facturaXML = getFacturaXML(factura2);						
					}
				}
				
			}else{
				if (autorizacionXML == null) {					
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}
				if (!(autorizacionXML.getComprobante() != null)) {					
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}
				String factura = autorizacionXML.getComprobante();
				factura = factura.replace("<![CDATA[", "");
				factura = factura.replace("]]>", "");
				try{
					facturaXML = getFacturaXML(factura);
				} catch (Exception ex) {
					String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
					
					String claveConsultar = "";
					
					if (offline) {				
						String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";						
						autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveConsultar);
						offline = true;
						String factura3 = autorizacionOfflineXML.getComprobante();
						factura3 = factura3.replace("<![CDATA[", "");
						factura3 = factura3.replace("]]>", "");
						facturaXML = getFacturaXML(factura3);
					} else {
						autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveConsultar);
						String factura2 = autorizacionXML.getComprobante();
						factura2 = factura2.replace("<![CDATA[", "");
						factura2 = factura2.replace("]]>", "");
						facturaXML = getFacturaXML(factura2);
						
					}
				}				
			}			
			if (facturaXML != null) {
				if (facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto() != null 
					&& !facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto().isEmpty()) {
					
					for (TotalImpuesto impu : facturaXML.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
						if (impu.getBaseImponible().compareTo(BigDecimal.ZERO) == 1) {
							if (impu.getCodigoPorcentaje().equals("0")) {
								baseImponible0 = impu.getBaseImponible();
							} else {
								if (impu.getCodigoPorcentaje().equals("2") || impu.getCodigoPorcentaje().equals("3")) {
									baseImponible12 = impu.getBaseImponible();
									iva = impu.getValor();
								}
							}
						}
						
						if("3".equals(impu.getCodigo())){
							 baseImponibleICE  = impu.getBaseImponible();
							 ice = impu.getValor();
						}
						
						if("5".equals(impu.getCodigo())){
							baseImponibleIRBP  = impu.getBaseImponible();
							irbp = impu.getValor();
						}
						
						
					}
				}
				
				try {
					if(!facturaXML.getDetalles().getDetalle().isEmpty()){
						for(Detalle detalle: facturaXML.getDetalles().getDetalle() ){
							cantidadTotal = cantidadTotal.add(detalle.getCantidad());
						}
					}
					
				} catch (Exception e) {
					
					e.printStackTrace();
					// TODO: handle exception
				}
				
				
				

			}
			facturaObj = servicioFactura.obtenerComprobanteRecepcion(facturaXML.getInfoTributaria().getClaveAcceso(), null, null, null);

			if (facturaObj != null && ("AUTORIZADO".equals(facturaObj.getEstado())||"PENDIENTE".equals(facturaObj.getEstado()))) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeCliente("El comprobante con la clave de acceso "+ facturaObj.getClaveAcceso()+ " ya esta registrada en el sistema como "
								+ facturaObj.getTipoGeneracion()
										.getDescripcion());

				respuesta.setMensajeSistema("El comprobante con la clave de acceso "
								+ facturaObj.getClaveAcceso()
								+ " ya esta registrada en el sistema como "
								+ facturaObj.getTipoGeneracion()
										.getDescripcion());
				GizloResponse gizloResponse = new GizloResponse();
				gizloResponse.setClaveAccesoComprobante(claveAccesop);
				gizloResponse.setMensajeCliente("Comprobante ya Insertado");
				gizloResponse.setMensajeSistema("Comprobante ya Insertado");
				receptarFacturaMailDataBean.getRespuestas().add(gizloResponse);
				return null;
			}

			if (facturaObj == null) {
				facturaObj = convertirEsquemaAEntidadFacturaRecepcion(facturaXML);

			} else {
				com.gizlocorp.gnvoice.modelo.FacturaRecepcion facturaAux = facturaObj;
				facturaObj = convertirEsquemaAEntidadFacturaRecepcion(facturaXML);
				facturaObj.setId(facturaAux.getId());
			}

			if(facturaObj.getContribuyenteEspecial().length()>5){
				int leng = facturaObj.getContribuyenteEspecial().length();
				facturaObj.setContribuyenteEspecial(facturaObj.getContribuyenteEspecial().substring(leng-5, leng));
			}
			
			if(facturaObj.getObligadoContabilidad()== null){
				facturaObj.setObligadoContabilidad("NO");
			}
			
			facturaObj.setInfoAdicional(StringUtil.validateInfoXML(facturaXML.getInfoTributaria().getRazonSocial().trim()));

			facturaObj.setTipoEjecucion(TipoEjecucion.SEC);
			facturaObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			facturaObj.setProceso("REIM");

			facturaObj.setFechaLecturaTraductor(new Date());
			facturaObj.setBaseCero(baseImponible0);
			facturaObj.setBaseDoce(baseImponible12);
			facturaObj.setIva(iva);	
			
			facturaObj.setBaseIce(baseImponibleICE);
			facturaObj.setBaseIrbp(baseImponibleIRBP);
			facturaObj.setIce(ice);
			facturaObj.setIrbp(irbp);
			facturaObj.setCantidadTotal(cantidadTotal);

			if(offline){
				rutaArchivoXml = DocumentUtil.createDocument(
						getFacturaXML(autorizacionOfflineXML),
						FechaUtil.formatearFecha(now.getTime(),"ddMMyyyy"),
						facturaXML.getInfoTributaria().getClaveAcceso(), subFolder, "recibidoReim");
				facturaObj.setArchivo(rutaArchivoXml);	
//				String rutaArchivoPdf = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
//						new FacturaReporte(facturaXML), autorizacionOfflineXML
//								.getNumeroAutorizacion(), FechaUtil.formatearFecha(now.getTime(),"ddMMyyyy"), facturaXML
//								.getInfoFactura().getFechaEmision(), "autorizado",
//						false,"/home/jboss/app/gnvoice/recursos/reportes/Blanco.png", "/data");
//				facturaObj.set
			}else{
				rutaArchivoXml = DocumentUtil.createDocument(
						getFacturaXML(autorizacionXML),
						FechaUtil.formatearFecha(now.getTime(),"ddMMyyyy"),
						facturaXML.getInfoTributaria().getClaveAcceso(), subFolder, "recibidoReim");
				facturaObj.setArchivo(rutaArchivoXml);
//				String rutaArchivoPdf = ReporteUtil.generarReporte(Constantes.REP_FACTURA,
//						new FacturaReporte(facturaXML), autorizacionXML
//								.getNumeroAutorizacion(), FechaUtil.formatearFecha(now.getTime(),"ddMMyyyy"), facturaXML
//								.getInfoFactura().getFechaEmision(), "autorizado",
//						false,"/home/jboss/app/gnvoice/recursos/reportes/Blanco.png", "/data");
			}
			
			Organizacion emisor = cacheBean.obtenerOrganizacion(facturaXML.getInfoFactura().getIdentificacionComprador());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: "+ facturaXML.getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: "+ facturaXML.getInfoTributaria().getRuc());
				facturaObj.setEstado(Estado.ERROR.getDescripcion());
				facturaObj = servicioFactura.recibirFactura(facturaObj,"No existe registrado un emisor con RUC: "+ facturaXML.getInfoTributaria().getRuc());
				GizloResponse gizloResponse = new GizloResponse();
				gizloResponse.setClaveAccesoComprobante(claveAccesop);
				gizloResponse.setMensajeCliente("No existe registrado un emisor con RUC:");
				gizloResponse.setMensajeSistema("Comprobante Insertado");
				receptarFacturaMailDataBean.getRespuestas().add(gizloResponse);
				return null;
			}
			
			String ordenCompra = null;	
			String ordenCompraName = "orden";
			String ordenCompraName2 = "compra";
			String ordenCompraName3 = "Orden_de_entrega";
			
			

			
			if (facturaXML.getInfoAdicional() != null) {
				List<ParametroOrdenCompra> listaParametros = servicioParametroOrdenCompra.listaParametroRucProveedor(facturaXML.getInfoTributaria().getRuc());
				
				if (listaParametros != null && !listaParametros.isEmpty()) {
					for (ParametroOrdenCompra parametro : listaParametros) {

						for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
							if (parametro.getValor().toUpperCase().equals(item.getNombre().toUpperCase())) {
								ordenCompra = item.getValue().trim();
								break;
							}
						}
					}
				} else {
					if (facturaXML.getInfoAdicional() != null) {
						for (CampoAdicional item : facturaXML.getInfoAdicional().getCampoAdicional()) {
							if ((item.getNombre().trim().toUpperCase().contains(ordenCompraName.toUpperCase())) 
								 || (item.getNombre().trim().toUpperCase().contains(ordenCompraName2.toUpperCase()))) {

								if (item.getNombre().trim().toUpperCase().contains(ordenCompraName3.toUpperCase())) {
									continue;
								}
								ordenCompra = item.getValue().trim();
								break;
							}
						}
					}
				}
			}

			
			
			if (ordenCompra != null) {
				if (ordenCompra.matches("[0-9]*")) {
					facturaObj.setOrdenCompra(ordenCompra);							
				}else{
					String orden = ordenCompra.replaceAll("[^0-9]", "");
					if (orden.matches("[0-9]*")) {
						facturaObj.setOrdenCompra(orden);							
					}else{
						facturaObj.setOrdenCompra(null);
					}
					 
				}
			}
			if (facturaObj.getOrdenCompra() == null || facturaObj.getOrdenCompra().isEmpty()) {				
				respuesta.setClaveAccesoComprobante(facturaXML.getInfoTributaria().getClaveAcceso());
				respuesta.setMensajeCliente("Ha ocurrido un error no se encuentra el nro orden de compra");
				respuesta.setMensajeSistema("Ha ocurrido un error no se encuentra el nro orden de compra");
				facturaObj.setEstado(Estado.ERROR.getDescripcion());
				facturaObj.setMensajeErrorReim("Ha ocurrido un error no se encuentra el nro orden de compra");
				facturaObj = this.servicioFactura.recibirFactura(facturaObj,"El comprobante no tiene orden de compra ingresar manualmente");
				GizloResponse gizloResponse = new GizloResponse();
				gizloResponse.setClaveAccesoComprobante(claveAccesop);
				gizloResponse.setMensajeCliente("Ha ocurrido un error no se encuentra el nro orden de compra");
				gizloResponse.setMensajeSistema("Comprobante Insertado");
				receptarFacturaMailDataBean.getRespuestas().add(gizloResponse);
				return null;
			}
			
			
			facturaObj.setNumeroAutorizacion(autorizacionXML.getNumeroAutorizacion());
			if (autorizacionXML.getFechaAutorizacion() != null) {
				facturaObj.setFechaAutorizacion(autorizacionXML
								.getFechaAutorizacion().toGregorianCalendar()
								.getTime());
			} else {
				SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
				facturaObj.setFechaAutorizacion(formato.parse(facturaXML.getInfoFactura().getFechaEmision()));
			}
				
		}catch (Exception ex) {
			log.info("ex inicial");
		}
		
		facturaObj.setEstado(Estado.PENDIENTE.getDescripcion());	
		try {
			facturaObj = this.servicioFactura.recibirFactura(facturaObj,"Comprobante guardado");
			GizloResponse gizloResponse = new GizloResponse();
			gizloResponse.setClaveAccesoComprobante(claveAccesop);
			gizloResponse.setMensajeCliente("Comprobante Insertado");
			gizloResponse.setMensajeSistema("Comprobante Insertado");
			receptarFacturaMailDataBean.getRespuestas().add(gizloResponse);
		} catch (GizloException e) {
			GizloResponse gizloResponse = new GizloResponse();
			gizloResponse.setClaveAccesoComprobante(claveAccesop);
			gizloResponse.setMensajeCliente("Error al Insertar Comprobante");
			gizloResponse.setMensajeSistema("Error al Insertar Comprobante");
			receptarFacturaMailDataBean.getRespuestas().add(gizloResponse);
			e.printStackTrace();
		}
		return facturaObj;
	}
	
	private com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion tranformaGuardaNotaCredito(String claveAccesop,Date fechcaTMP) {

		boolean offline = false;
		Calendar now = Calendar.getInstance();
		com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion documentoObj = null;
		NotaCreditoRecibirResponse respuesta = new NotaCreditoRecibirResponse();
		
		try {
		
			Autorizacion autorizacionXML = null;
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;
			if (claveAccesop != null && !claveAccesop.isEmpty()) {
				String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
				String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
				autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveAccesop);
				autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveAccesop);
				if (autorizacionOfflineXML != null && autorizacionOfflineXML.getComprobante() != null) {
					offline = true;
				}
			}

			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito documentoXML = null;
			if (offline) {

				if (autorizacionOfflineXML == null) {
					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}
				if (!(autorizacionOfflineXML.getComprobante() != null)) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}

				String documento = autorizacionOfflineXML.getComprobante();
				documento = documento.replace("<![CDATA[", "");
				documento = documento.replace("]]>", "");
				documentoXML = getNotaCreditoXML(documento);
			} else {
				if (autorizacionXML == null) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}
				if (!(autorizacionXML.getComprobante() != null)) {

					respuesta.setEstado(Estado.ERROR.getDescripcion());
					respuesta.setMensajeCliente("No devolvio datos el sri ");
					respuesta.setMensajeSistema("No devolvio datos el sri ");
					return null;
				}
				String documento = autorizacionXML.getComprobante();
				documento = documento.replace("<![CDATA[", "");
				documento = documento.replace("]]>", "");
				documentoXML = getNotaCreditoXML(documento);
			}

			documentoObj = servicioNotaCredito.obtenerComprobanteRecepcion(claveAccesop, null, null, null);

			if (documentoObj != null && "AUTORIZADO".equals(documentoObj.getEstado())) {
				respuesta.setMensajeCliente("La clave de acceso ya esta registrada " + documentoObj.getTipoGeneracion().getDescripcion());
				respuesta.setMensajeSistema("La clave de acceso ya esta registrada" + documentoObj.getTipoGeneracion().getDescripcion());
				return null;
			}

			if (documentoObj == null) {
				documentoObj = convertirEsquemaAEntidadNotacreditoRecepcion(documentoXML);
			} else {
				com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion documentoAux = documentoObj;
				documentoObj = convertirEsquemaAEntidadNotacreditoRecepcion(documentoXML);
				documentoObj.setId(documentoAux.getId());
			}

			documentoObj.setInfoAdicional(StringUtil.validateInfoXML(documentoXML.getInfoTributaria().getRazonSocial().trim()));
			documentoObj.setTipoEjecucion(TipoEjecucion.SEC);
			documentoObj.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
			documentoObj.setProceso("REIM");

			if (offline) {
				
				log.info("OFFline ---->" + claveAccesop);

				
				String rutaArchivoAutorizacionXml = DocumentUtil.createDocument(  getNotaCreditoXML(autorizacionOfflineXML)
																				, FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy")
																				, claveAccesop
																				, TipoComprobante.NOTA_CREDITO.getDescripcion()
																				, "autorizado");

				documentoObj.setArchivo(rutaArchivoAutorizacionXml);

				Parametro dirParametro = servicioParametro.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, null);
				
				String dirServidor = dirParametro != null ? dirParametro.getValor() : "";

				String rutaArchivoAutorizacionPdf = ReporteUtil.generarReporte(Constantes.REP_NOTA_CREDITO
						,new NotaCreditoReporte(documentoXML)
						, autorizacionOfflineXML.getNumeroAutorizacion()
						,FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy")
						,documentoXML.getInfoNotaCredito().getFechaEmision(), "autorizado", false,
						"/data/gnvoice/recursos/reportes/Blanco.png", dirServidor);
				
				log.info("dirServidor ---->" + rutaArchivoAutorizacionPdf);

				documentoObj.setArchivoLegible(rutaArchivoAutorizacionPdf);

			} else {
				
				log.info("ON line ---->" + claveAccesop);
				
				String rutaArchivoAutorizacionXml = DocumentUtil.createDocument(getNotaCreditoXML(autorizacionXML),
						FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy"), claveAccesop,
						TipoComprobante.NOTA_CREDITO.getDescripcion(), "autorizado");

				documentoObj.setArchivo(rutaArchivoAutorizacionXml);

				Parametro dirParametro = servicioParametro.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, null);
				String dirServidor = dirParametro != null ? dirParametro.getValor() : "";

				String rutaArchivoAutorizacionPdf = ReporteUtil.generarReporte(Constantes.REP_NOTA_CREDITO,
																			    new NotaCreditoReporte(documentoXML),
																			    autorizacionXML.getNumeroAutorizacion(),
																			    FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy"),
																			    documentoXML.getInfoNotaCredito().getFechaEmision(), 
																			    "autorizado", 
																			    false,
																			    "/data/gnvoice/recursos/reportes/Blanco.png", 
																			    dirServidor);
				log.info(" dirServidor 2 --->" + rutaArchivoAutorizacionPdf);
				documentoObj.setArchivoLegible(rutaArchivoAutorizacionPdf);
			}

			if ((autorizacionXML.getNumeroAutorizacion() == null || autorizacionXML.getNumeroAutorizacion().isEmpty())
					&& (autorizacionOfflineXML.getNumeroAutorizacion() == null || autorizacionOfflineXML.getNumeroAutorizacion().isEmpty())) {
				respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
				respuesta.setMensajeSistema("El comprobante no tiene numero de Autorizacion");
				respuesta.setMensajeCliente("El Comprobante no tiene numero de Autorizacion");

				documentoObj.setEstado(Estado.ERROR.getDescripcion());
				documentoObj = servicioNotaCredito.recibirNotaCredito(documentoObj,"Comprobante no tiene numero de Autorizacion");
				
			
				return documentoObj;

			}

			Organizacion emisor = cacheBean.obtenerOrganizacion(documentoXML.getInfoNotaCredito().getIdentificacionComprador());

			if (emisor == null) {
				respuesta.setEstado(Estado.COMPLETADO.getDescripcion());
				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: " + documentoXML.getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: " + documentoXML.getInfoTributaria().getRuc());

				documentoObj.setEstado(Estado.ERROR.getDescripcion());
				documentoObj = servicioNotaCredito.recibirNotaCredito(documentoObj,"No existe registrado un emisor con RUC: " + documentoXML.getInfoTributaria().getRuc());
				return documentoObj;
			}

			Parametro serRutaDocCalss = this.cacheBean.consultarParametro("DOC_CLASS_RUTA", emisor.getId());
			
			
			log.info("URL********docuclass ---->" + serRutaDocCalss.getValor());
			

			String strRutaDocCalss = serRutaDocCalss != null ? serRutaDocCalss.getValor() : "";

			log.info("url docuclass  serRutaDocCalss ---->" + strRutaDocCalss);

			DocumentUtil.createDocumentDocClass(getNotaCreditoXML(autorizacionOfflineXML),documentoXML.getInfoTributaria().getClaveAcceso(), strRutaDocCalss);

			String rutaArchivoAutorizacionPdf = ReporteUtil.generarReporteDocuClass( Constantes.REP_NOTA_CREDITO,
																					new NotaCreditoReporte(documentoXML), autorizacionOfflineXML.getNumeroAutorizacion()
					                                                                , FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy")
					                                                                , documentoXML.getInfoNotaCredito().getFechaEmision(),
					                                                                "autorizado", false,
					"/data/gnvoice/recursos/reportes/Blanco.png", strRutaDocCalss);

			log.info(" url docuclass pdf ---->" + rutaArchivoAutorizacionPdf);
			documentoObj.setFechaAutorizacion(autorizacionOfflineXML.getFechaAutorizacion().toGregorianCalendar().getTime());
			documentoObj.setIva(documentoXML.getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto().get(0).getBaseImponible());
			documentoObj.setEstado(Estado.AUTORIZADO.getDescripcion());
			documentoObj = servicioNotaCredito.recibirNotaCredito(documentoObj,"El comprobante se a cargado de forma exitosa");

			log.info("FIN ********docuclass ---->***<---->");
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ex inicial");
		}
		return documentoObj;
	}	

	
	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,
			String claveAcceso) {
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

		// log.info("CLAVE DE ACCESO: " + claveAcceso);
		int timeout = 7000;
		   

		try {
			
			/*System.setProperty("javax.net.ssl.keyStore", "/data/certificados/cacerts"); 
			//System.setProperty("javax.net.ssl.keyStorePassword", "changeit"); 
			System.setProperty("javax.net.ssl.trustStore", "/data/certificados/cacerts"); 
			System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
			System.setProperty("javax.net.ssl.trustAnchors", "/data/certificados/cacerts"); 
			System.setProperty("javax.net.ssl.keyStorePassword", "changeit");  
			//Security.setProperty("ssl.TrustManagerFactory.algorithm" , "XTrust509");
			System.setProperty("org.jboss.security.ignoreHttpsHost", "true");
			*/
			
			
			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
					new URL(wsdlLocation),
					new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));

			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion.getAutorizacionComprobantesOfflinePort();

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);
			

			

			ec.gob.sri.comprobantes.ws.offline.aut.RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

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
			ex.printStackTrace();
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

	private Autorizacion autorizarComprobante(String wsdlLocation,
			String claveAcceso) {
		Autorizacion respuesta = null;

		log.info("enterndo sri ");
		int timeout = 7000;

		try {
			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion","AutorizacionComprobantesService"));

			AutorizacionComprobantes autorizacionws = servicioAutorizacion.getAutorizacionComprobantesPort();

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", timeout);

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);
			
			((BindingProvider) autorizacionws).getRequestContext().put("https.proxyHost", "uioproxy04.gfybeca.int");
			((BindingProvider) autorizacionws).getRequestContext().put("https.proxyPort", "3128");
			
			log.info("enterndo sri22 ");
			RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);

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
			log.info("enterndo sri 33");

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
				log.info("enterndo sri 444");

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

				log.info("enterndo sri555 ");

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
	
	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.factura.Factura) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.factura.Factura.class);
	}
	
	private String getFacturaXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}
	
	private String getNotaCreditoXML(Object comprobante)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}
	
	private String getRetencionXML(Object comprobante)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}
	
	

	private com.gizlocorp.gnvoice.xml.notacredito.NotaCredito getNotaCreditoXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.notacredito.NotaCredito) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.class);
	}
	
	
	private com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion getComprobanteRetencionXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.class);
	}
	
	
	
	public static FacturaRecepcion convertirEsquemaAEntidadFacturaRecepcion(
			com.gizlocorp.gnvoice.xml.factura.Factura esquema) {
		log.debug("Convirtiendo a factura");
		FacturaRecepcion factura = new FacturaRecepcion();
		factura.setClaveAcceso(esquema.getInfoTributaria().getClaveAcceso());

		factura.setRuc(esquema.getInfoTributaria().getRuc());
		factura.setCodDoc(esquema.getInfoTributaria().getCodDoc());
		factura.setCodPuntoEmision(esquema.getInfoTributaria().getEstab());
		factura.setCodSecuencial(esquema.getInfoTributaria().getSecuencial());
		factura.setContribuyenteEspecial(esquema.getInfoFactura()
				.getContribuyenteEspecial());
		factura.setDirEstablecimiento(esquema.getInfoFactura()
				.getDirEstablecimiento());

		if (factura.getContribuyenteEspecial() == null) {
			factura.setContribuyenteEspecial("N");

		}

		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		// SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoFactura()
					.getFechaEmision());
			factura.setFechaEmisionBase(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		factura.setGuiaRemision(esquema.getInfoFactura().getGuiaRemision());
		factura.setIdentificacionComprador(esquema.getInfoFactura()
				.getIdentificacionComprador());
		factura.setImporteTotal(esquema.getInfoFactura().getImporteTotal());
		factura.setMoneda(esquema.getInfoFactura().getMoneda());
		factura.setObligadoContabilidad(esquema.getInfoFactura()
				.getObligadoContabilidad());
		factura.setPropina(esquema.getInfoFactura().getPropina());
		factura.setPtoEmision(esquema.getInfoTributaria().getPtoEmi());
		factura.setRazonSocialComprador(esquema.getInfoFactura()
				.getRazonSocialComprador());
		factura.setTipoIdentificacionComprador(esquema.getInfoFactura()
				.getTipoIdentificacionComprador());
		factura.setTotalDescuento(esquema.getInfoFactura().getTotalDescuento());
		factura.setTotalSinImpuestos(esquema.getInfoFactura()
				.getTotalSinImpuestos());

		factura.setTipoAmbiente(esquema.getInfoTributaria().getAmbiente());
		factura.setTipoEmision(esquema.getInfoTributaria().getTipoEmision());

		return factura;
	}

	public static NotaCreditoRecepcion convertirEsquemaAEntidadNotacreditoRecepcion(
			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito esquema) {
		NotaCreditoRecepcion notaCredito = new NotaCreditoRecepcion();
		notaCredito
				.setClaveAcceso(esquema.getInfoTributaria().getClaveAcceso());
		notaCredito.setCodDoc(esquema.getInfoTributaria().getCodDoc());
		notaCredito.setCodDocModificado(esquema.getInfoNotaCredito()
				.getCodDocModificado());
		notaCredito.setCodPuntoEmision(esquema.getInfoTributaria().getEstab());
		notaCredito.setRuc(esquema.getInfoTributaria().getRuc());

		notaCredito.setDirEstablecimiento(esquema.getInfoNotaCredito()
				.getDirEstablecimiento());

		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoNotaCredito()
					.getFechaEmision());
			notaCredito.setFechaEmisionDb(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoNotaCredito()
					.getFechaEmisionDocSustento());
			notaCredito.setFechaEmisionDocSustentoDb(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		notaCredito.setIdentificacionComprador(esquema.getInfoNotaCredito()
				.getIdentificacionComprador());
		notaCredito.setMoneda(esquema.getInfoNotaCredito().getMoneda());
		notaCredito.setMotivo(esquema.getInfoNotaCredito().getMotivo());
		notaCredito.setNumDocModificado(esquema.getInfoNotaCredito()
				.getNumDocModificado());
		notaCredito.setObligadoContabilidad(esquema.getInfoNotaCredito()
				.getObligadoContabilidad());
		notaCredito.setPtoEmision(esquema.getInfoTributaria().getPtoEmi());
		notaCredito.setRazonSocialComprador(esquema.getInfoNotaCredito()
				.getRazonSocialComprador());
		notaCredito.setRise(esquema.getInfoNotaCredito().getRise());
		notaCredito.setTipoIdentificacionComprador(esquema.getInfoNotaCredito()
				.getTipoIdentificacionComprador());
		notaCredito.setTotalSinImpuestos(esquema.getInfoNotaCredito()
				.getTotalSinImpuestos());
		notaCredito.setValorModificacion(esquema.getInfoNotaCredito()
				.getValorModificacion());
		notaCredito.setCodSecuencial(esquema.getInfoTributaria()
				.getSecuencial());
		notaCredito.setContribuyenteEspecial(esquema.getInfoNotaCredito()
				.getContribuyenteEspecial());

		if (notaCredito.getContribuyenteEspecial() == null) {
			notaCredito.setContribuyenteEspecial("N");

		}

		notaCredito.setTipoAmbiente(esquema.getInfoTributaria().getAmbiente());
		notaCredito
				.setTipoEmision(esquema.getInfoTributaria().getTipoEmision());

		return notaCredito;

	}
	
	public static NotaCredito convertirEsquemaAEntidadNotacredito(
			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito esquema) {
		NotaCredito notaCredito = new NotaCredito();
		notaCredito
				.setClaveAcceso(esquema.getInfoTributaria().getClaveAcceso());
		notaCredito.setCodDoc(esquema.getInfoTributaria().getCodDoc());
		notaCredito.setCodDocModificado(esquema.getInfoNotaCredito()
				.getCodDocModificado());
		notaCredito.setCodPuntoEmision(esquema.getInfoTributaria().getEstab());
		notaCredito.setRuc(esquema.getInfoTributaria().getRuc());

		notaCredito.setDirEstablecimiento(esquema.getInfoNotaCredito()
				.getDirEstablecimiento());

		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoNotaCredito()
					.getFechaEmision());
			notaCredito.setFechaEmisionDb(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoNotaCredito()
					.getFechaEmisionDocSustento());
			notaCredito.setFechaEmisionDocSustentoDb(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		notaCredito.setIdentificacionComprador(esquema.getInfoNotaCredito()
				.getIdentificacionComprador());
		notaCredito.setMoneda(esquema.getInfoNotaCredito().getMoneda());
		notaCredito.setMotivo(esquema.getInfoNotaCredito().getMotivo());
		notaCredito.setNumDocModificado(esquema.getInfoNotaCredito()
				.getNumDocModificado());
		notaCredito.setObligadoContabilidad(esquema.getInfoNotaCredito()
				.getObligadoContabilidad());
		notaCredito.setPtoEmision(esquema.getInfoTributaria().getPtoEmi());
		notaCredito.setRazonSocialComprador(esquema.getInfoNotaCredito()
				.getRazonSocialComprador());
		notaCredito.setRise(esquema.getInfoNotaCredito().getRise());
		notaCredito.setTipoIdentificacionComprador(esquema.getInfoNotaCredito()
				.getTipoIdentificacionComprador());
		notaCredito.setTotalSinImpuestos(esquema.getInfoNotaCredito()
				.getTotalSinImpuestos());
		notaCredito.setValorModificacion(esquema.getInfoNotaCredito()
				.getValorModificacion());
		notaCredito.setCodSecuencial(esquema.getInfoTributaria()
				.getSecuencial());
		notaCredito.setContribuyenteEspecial(esquema.getInfoNotaCredito()
				.getContribuyenteEspecial());

		if (notaCredito.getContribuyenteEspecial() == null) {
			notaCredito.setContribuyenteEspecial("N");

		}

		notaCredito.setTipoAmbiente(esquema.getInfoTributaria().getAmbiente());
		notaCredito
				.setTipoEmision(esquema.getInfoTributaria().getTipoEmision());

		return notaCredito;

	}
	
	
	
	private com.gizlocorp.gnvoice.modelo.Retencion tranformaGuardaRetencion(String claveAccesop,Date fechcaTMP) {

		boolean offline = false;
		Calendar now = Calendar.getInstance();

		try {
		
			Autorizacion autorizacionXML = null;
			ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacionOfflineXML = null;
			if (claveAccesop != null && !claveAccesop.isEmpty()) {
				String wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
				String wsdlLocationAutorizacionoff = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
				autorizacionOfflineXML = autorizarComprobanteOffline(wsdlLocationAutorizacionoff, claveAccesop);
				autorizacionXML = autorizarComprobante(wsdlLocationAutorizacion, claveAccesop);
				if (autorizacionOfflineXML != null && autorizacionOfflineXML.getComprobante() != null) {
					offline = true;
				}
			}
			com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion documentoXML=null;

			if (offline) {
			    String documento = autorizacionOfflineXML.getComprobante();
				documento = documento.replace("<![CDATA[", "");
				documento = documento.replace("]]>", "");
				documentoXML = getComprobanteRetencionXML(documento);
			} else {
				String documento = autorizacionXML.getComprobante();
				documento = documento.replace("<![CDATA[", "");
				documento = documento.replace("]]>", "");
				documentoXML = getComprobanteRetencionXML(documento);
			}

			if (offline) {
				
				log.info("OFFline ---->" + claveAccesop);
                      DocumentUtil.createDocument(getRetencionXML(autorizacionOfflineXML)
																				, FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy")
																				, claveAccesop
																				, TipoComprobante.RETENCION.getDescripcion()
																				, "autorizado");

				Parametro dirParametro = servicioParametro.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, null);
				
				String dirServidor = dirParametro != null ? dirParametro.getValor() : "";

				String rutaArchivoAutorizacionPdf = ReporteUtil.generarReporteRetencion(Constantes.REP_NOTA_CREDITO
						, new ComprobanteRetencionReporte(documentoXML)
						, autorizacionOfflineXML.getNumeroAutorizacion()
						,FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy")
						,documentoXML.getInfoCompRetencion().getFechaEmision(), "autorizado", false,
						"/data/gnvoice/recursos/reportes/Blanco.png", dirServidor);
				
				log.info("dirServidor ---->" + rutaArchivoAutorizacionPdf);

			

			} else {
				
				log.info("ON line ---->" + claveAccesop);
				
				 DocumentUtil.createDocument(getRetencionXML(autorizacionXML)
						 					 ,FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy")
						 					 ,claveAccesop
						 					 ,TipoComprobante.RETENCION.getDescripcion()
						 					 ,"autorizado");

			

				Parametro dirParametro = servicioParametro.consultarParametro(Constantes.DIRECTORIO_SERVIDOR, null);
				String dirServidor = dirParametro != null ? dirParametro.getValor() : "";

				String rutaArchivoAutorizacionPdf = ReporteUtil.generarReporte(Constantes.REP_NOTA_CREDITO,
																			    new ComprobanteRetencionReporte(documentoXML),
																			    autorizacionXML.getNumeroAutorizacion(),
																			    FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy"),
																			    documentoXML.getInfoCompRetencion().getFechaEmision(), 
																			    "autorizado", 
																			    false,
																			    "/data/gnvoice/recursos/reportes/Blanco.png", 
																			    dirServidor);
				log.info(" dirServidor 2 --->" + rutaArchivoAutorizacionPdf);
				
			}


			String strRutaDocCalss = "/data/retenciones";

			log.info("url docuclass  serRutaDocCalss ---->" + strRutaDocCalss);
			DocumentUtil.createDocumentDocClass(getNotaCreditoXML(autorizacionXML),documentoXML.getInfoTributaria().getClaveAcceso(), strRutaDocCalss);
			String rutaArchivoAutorizacionPdf = ReporteUtil.generarReporteRetencion( Constantes.REP_COMP_RETENCION
																					,new ComprobanteRetencionReporte(documentoXML)
																					,autorizacionOfflineXML.getNumeroAutorizacion()
					                                                                ,FechaUtil.formatearFecha(now.getTime(), "ddMMyyyy")
					                                                                ,documentoXML.getInfoCompRetencion().getFechaEmision()
					                                                                ,"autorizado"
					                                                                ,false
					                                                                ,"/data/gnvoice/recursos/reportes/Blanco.png"
					                                                                ,strRutaDocCalss);

			log.info(" url docuclass pdf ---->" + rutaArchivoAutorizacionPdf);

			log.info("FIN ********docuclass ---->***<---->");
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ex inicial");
		}
		return null;
	}	
	
	
	
	@SuppressWarnings({ "rawtypes", "resource" })
	private void readXLSXFile(InputStream is) throws IOException {
		org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(is);
		org.apache.poi.xssf.usermodel.XSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.xssf.usermodel.XSSFRow row;
		org.apache.poi.xssf.usermodel.XSSFCell cell;		
		int index=0;
		Iterator rows = sheet.rowIterator();
		while (rows.hasNext()) {
			row = (org.apache.poi.xssf.usermodel.XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (org.apache.poi.xssf.usermodel.XSSFCell) cells.next();

				if(cell.getStringCellValue().trim().equals("CLAVE_ACCESO")){
					index = cell.getColumnIndex();
					break;					
				} 
			}
			break;
		}		
		while (rows.hasNext()) {
			row = (org.apache.poi.xssf.usermodel.XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (org.apache.poi.xssf.usermodel.XSSFCell) cells.next();
				if(cell.getColumnIndex()== index){
					mapComprobante.put(cell.getStringCellValue(),String.valueOf(cell.getColumnIndex()));
				}				
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "resource" })
	private  void readXLSFile(InputStream is) throws IOException {
		org.apache.poi.hssf.usermodel.HSSFWorkbook wb = new org.apache.poi.hssf.usermodel.HSSFWorkbook(is);
		org.apache.poi.hssf.usermodel.HSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.hssf.usermodel.HSSFRow row;
		org.apache.poi.hssf.usermodel.HSSFCell cell;
		int index=0;
		Iterator rows = sheet.rowIterator();
		while (rows.hasNext()) {
			row = (org.apache.poi.hssf.usermodel.HSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (org.apache.poi.hssf.usermodel.HSSFCell) cells.next();
				if(cell.getStringCellValue().trim().equals("CLAVE_ACCESO")){
					index = cell.getColumnIndex();
					break;					
				}				 
			}
			break;
		}		
		while (rows.hasNext()) {
			row = (org.apache.poi.hssf.usermodel.HSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (org.apache.poi.hssf.usermodel.HSSFCell) cells.next();
				if(cell.getColumnIndex()== index){
					System.out.println(cell.getStringCellValue());
					mapComprobante.put(cell.getStringCellValue(),String.valueOf(cell.getColumnIndex()));
				}			 
			}
		}
	}
		
	public List<SelectItem> getTipoComprobanteDesList() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoComprobante tipoComprobanteDes : TipoComprobante.values()) {
			if (validarPantalla) {
				if (tipoComprobanteDes.equals(TipoComprobante.FACTURA)
						|| tipoComprobanteDes.equals(TipoComprobante.NOTA_CREDITO) 
						|| tipoComprobanteDes.equals(TipoComprobante.RETENCION )
						|| tipoComprobanteDes.equals(TipoComprobante.ELIMINAR_FACTURA)) {
					items.add(new SelectItem(tipoComprobanteDes, tipoComprobanteDes
							.getEtiqueta()));
				}	
			}else {
				
				if (tipoComprobanteDes.equals(TipoComprobante.FACTURA)
						|| tipoComprobanteDes.equals(TipoComprobante.NOTA_CREDITO) 
						|| tipoComprobanteDes.equals(TipoComprobante.RETENCION )
						|| tipoComprobanteDes.equals(TipoComprobante.ELIMINAR_FACTURA)) {
					items.add(new SelectItem(tipoComprobanteDes, tipoComprobanteDes
							.getEtiqueta()));
				}
				
			}
			

		}
		return items;
	}
	
	///WSA INI 
	public void saveCola(String name, String ext,InputStream is) {
		
		
		String user = sessionBean.getUsuario().getUsername();  
		String NombreCompleto = sessionBean.getUsuario().getPersona().getNombreCompleto();
		
		ColaFileClave cola = new ColaFileClave(); 
		System.out.println("ENTRO AL SAVE user:"+user);
		String ruta = "/jboss/wildfly/app/ginvoce/recursos/files";
		
		 //String filename = name; 
		 //String extension = ext;
		
		 
		 ///factelectro/documentos2015/ /jboss/wildfly/app/ginvoce/recursos/files
		 //"/home/jboss/app/gnvoice/recursos/comprobantes";//para pruebas
		 Path folder = Paths.get(ruta);
		
		 
		 try {
			Path file = Files.createTempFile(folder, name, "."+ext);
			
			try (InputStream input = is) {
			    Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
			}

			System.out.println("Uploaded file successfully saved in " + name);
			
			
			 cola.setNombre(name+"."+ext); 
			 cola.setEstado("ING");
			 cola.setFechaRegistro(new Date());
			 cola.setUserName(user);
			 cola.setPersona(NombreCompleto);
			 cola.setTipoIngreso("RCA");
			 String path =ruta+"/"+file.getFileName();  
			 cola.setRuta(path);
			 
			/* System.out.println("Va a buscar Fact");
			 factura = servicioFactura.obtenerComprobanteRecepcion("2504202101179132159600120070030005825350058253511", null, null, null);
			 System.out.println("Dejo a buscar Fact");	
			 //log.info("clave a procesar "+e.getKey());
				if(factura == null ){
					FacturaRecepcion respuestaFactura = tranformaGuardaFactura("2504202101179132159600120070030005825350058253511", new Date());	
					//log.info("clave a procesado exitosamente "+e.getKey());
				}*/
			 
			 
			 
			/* System.out.println("Voy a ejecutar el list!!!");
			    List<ColaFileClave> lista = new ArrayList<>();
				try {
					lista = servicioColaFileClave.listarColasFiles();
					for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
						ColaFileClave colaFileClave = (ColaFileClave) iterator.next();
						System.out.println(colaFileClave.getRuta());
						
					}
				} catch (GizloException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/ 
				
				

			 

			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		 try {
		  servicioColaFileClave.saveColaFileClave(cola);
		} catch (/*GizloException*/Exception e) {
			 System.out.println(e.toString());
		} 
		
	}
	///WSA FIN
	
	public static void main(String[] args) throws FileNotFoundException {
		String ordenCompra = "78934";
		if (ordenCompra != null) {
			if (ordenCompra.matches("[0-9]*")) {
//				facturaObj.setOrdenCompra(ordenCompra);	
				System.out.println("opcion 1--"+ordenCompra);
			}else{
				String orden = ordenCompra.replaceAll("[^0-9]", "");
				if (orden.matches("[0-9]*")) {
//					facturaObj.setOrdenCompra(orden);
					System.out.println("opcion 2--"+orden);
				}else{
					System.out.println("opcion 3");
//					facturaObj.setOrdenCompra(null);
				}
				 
			}
		}
		
		
    }
	

	public Boolean getValidarPantalla() {
		return validarPantalla;
	}

	public void setValidarPantalla(Boolean validarPantalla) {
		this.validarPantalla = validarPantalla;
	}
	
	
}