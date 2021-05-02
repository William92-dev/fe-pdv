package ec.gob.sri.comprobantes.ws.offline.aut;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import oracle.retail.integration.custom.bo.cedisservice.v1.CeDisInvoiceRequest;
import oracle.retail.integration.custom.bo.cedisservice.v1.CeDisOrderRucRequest;
import oracle.retail.integration.custom.bo.cedisservice.v1.CeDisResponse;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.CeDisOrderService;
import oracle.retail.integration.custom.gpfservices.cedisservice.v1.CeDisWS;

import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPEnvelope;


public class Main {
	
	public static String wsURL = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";

	public static String mySoapRequest = new StringBuilder().
			append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ec=\"http://ec.gob.sri.ws.autorizacion\">"
					+ "<soapenv:Header/>   <soapenv:Body>      <ec:autorizacionComprobante>"
						+ "         <claveAccesoComprobante>2302201601179000573900120270100008515160085151611</claveAccesoComprobante>"
						+ "      </ec:autorizacionComprobante>"
						+ "   </soapenv:Body>"
						+ "</soapenv:Envelope>").toString();

	public static void main(String[] args) throws Exception {
//		oracle.retail.integration.custom.bo.cedisservice.v1.ObjectFactory fact = new oracle.retail.integration.custom.bo.cedisservice.v1.ObjectFactory(); 		
//		JAXBElement<BigDecimal> ruc = fact.createCeDisOrderRucRequestRucNumber(new BigDecimal("1791831233001"));
//		JAXBElement<BigDecimal> order = fact.createCeDisOrderRucRequestOrderNumber(new BigDecimal("404"));
//		
//		List<JAXBElement<BigDecimal>> list = new ArrayList<JAXBElement<BigDecimal>>();
//		list.add(order);
//		list.add(ruc);
//		
//		CeDisOrderRucRequest input = new CeDisOrderRucRequest();
//		input.setOrderNumberAndRucNumber(list);
//		 
//	    CeDisWS servicio = new CeDisWS();
//	    CeDisOrderService respuesta = servicio.getCeDisOrderServicePt();
//	    CeDisResponse ceDisOrderRuc = respuesta.getCeDisOrderRuc(input);
//	    com.gizlocorp.gnvoice.xml.message.CeDisResponse ceDisResponsePoo = com.gizlocorp.gnvoice.xml.message.CeDisResponse();
//	    
//	    for(oracle.retail.integration.custom.bo.cedisservice.v1.ValidItem itera: ceDisOrderRuc.getValidItemList().getValidItem() ){
//	    	System.out.println(itera.getItem());
//	    }
//	    System.out.println(dept_Name);
		    
		
		
//		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;
//
//		// log.info("CLAVE DE ACCESO: " + claveAcceso);
//		int timeout = 7000;
//		String wsdlLocation = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
//		//String wsdlLocation = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
//		String claveAcceso = "1111201501179071031900110020230003031395658032319";
//
//		try {
//			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
//					new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));
//
//			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion.getAutorizacionComprobantesOfflinePort();
//			
////			AutorizacionComprobantesService servicioAutorizacion = new AutorizacionComprobantesService(
////					new URL(wsdlLocation), new QName(
////							"http://ec.gob.sri.ws.autorizacion",
////							"AutorizacionComprobantesService"));
////
////			AutorizacionComprobantes autorizacionws = servicioAutorizacion.getAutorizacionComprobantesPort();
//
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.ws.connect.timeout", timeout);
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.internal.ws.connect.timeout", timeout);
//
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.internal.ws.request.timeout", timeout);
//			((BindingProvider) autorizacionws).getRequestContext().put(
//					"com.sun.xml.ws.request.timeout", timeout);
//
//			ec.gob.sri.comprobantes.ws.offline.aut.RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);
//			
//			System.out.println(respuestaComprobanteAut.getAutorizaciones());
//			
//			RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);
//
//			// 2 reintentos
//			for (int i = 0; i < 2; i++) {
//				if (respuestaComprobanteAut != null
//						&& respuestaComprobanteAut.getAutorizaciones() != null
//						&& respuestaComprobanteAut.getAutorizaciones()
//								.getAutorizacion() != null
//						&& !respuestaComprobanteAut.getAutorizaciones()
//								.getAutorizacion().isEmpty()
//						&& respuestaComprobanteAut.getAutorizaciones()
//								.getAutorizacion().get(0) != null) {
//					break;
//				} else {
//					// log.info("Renvio por autorizacion en blanco");
//					respuestaComprobanteAut = autorizacionws
//							.autorizacionComprobante(claveAcceso);
//
//				}
//			}
//
//			if (respuestaComprobanteAut != null
//					&& respuestaComprobanteAut.getAutorizaciones() != null
//					&& respuestaComprobanteAut.getAutorizaciones()
//							.getAutorizacion() != null
//					&& !respuestaComprobanteAut.getAutorizaciones()
//							.getAutorizacion().isEmpty()
//					&& respuestaComprobanteAut.getAutorizaciones()
//							.getAutorizacion().get(0) != null) {
//
//				ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizacion = respuestaComprobanteAut
//						.getAutorizaciones().getAutorizacion().get(0);
//
//				for (ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion aut : respuestaComprobanteAut
//						.getAutorizaciones().getAutorizacion()) {
//					if (aut != null && "AUTORIZADO".equals(aut.getEstado())) {
//						autorizacion = aut;
//						break;
//					}
//				}
//
//				autorizacion.setAmbiente(null);
//
//				autorizacion.setComprobante("<![CDATA["
//						+ autorizacion.getComprobante() + "]]>");
//
//				if (autorizacion.getMensajes() != null
//						&& autorizacion.getMensajes().getMensaje() != null
//						&& !autorizacion.getMensajes().getMensaje().isEmpty()) {
//
//					for (ec.gob.sri.comprobantes.ws.offline.aut.Mensaje msj : autorizacion
//							.getMensajes().getMensaje()) {
//						msj.setMensaje(msj.getMensaje() != null ? StringEscapeUtils
//								.escapeXml(msj.getMensaje()) : null);
//						msj.setInformacionAdicional(msj
//								.getInformacionAdicional() != null ? StringEscapeUtils
//								.escapeXml(msj.getInformacionAdicional())
//								: null);
//					}
//				}
//
//				if ("AUTORIZADO".equals(autorizacion.getEstado())) {
//					autorizacion.setMensajes(null);
//				}
//
//				System.out.println("***" + autorizacion.getEstado());
//			}
//
//		} catch (Exception ex) {
//			System.out.println("error "+ex);
//			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
//			respuesta.setEstado("ERROR");
//		}
		

	}
	


}
