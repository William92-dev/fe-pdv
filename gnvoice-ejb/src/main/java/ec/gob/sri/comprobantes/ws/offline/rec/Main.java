package ec.gob.sri.comprobantes.ws.offline.rec;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import com.gizlocorp.adm.excepcion.FileException;
import com.gizlocorp.adm.utilitario.DocumentUtil;

public class Main {

	public static void main(String[] args) {
		
		int retardo = 8000;
		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuesta = null;
		try {
			
			String wsdlLocation = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
			byte[] documentFirmado = DocumentUtil.readFile("/Users/andresgiler/Documents/9999999999999_01_000816366_18112015signedGNVOICE.xml");
			// log.info("Inicia proceso reccepcion offline SRI ..."+ wsdlLocation+" ..."+rutaArchivoXml);
			// while (contador <= reintentos) {
			//try {
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService servicioRecepcion = new 
						ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService(new URL(wsdlLocation));
				
				//log.info("Inicia proceso reccepcion offline SRI 111...");
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion.getRecepcionComprobantesOfflinePort();
				
				//log.info("Inicia proceso reccepcion offline SRI ...222");
				
				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.internal.ws.connect.timeout", retardo);

				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.internal.ws.request.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put(
						"com.sun.xml.ws.request.timeout", retardo);
				
			//	log.info("Validando en reccepcion offline SRI 333..."+documentFirmado.length);

				respuesta = recepcion.validarComprobante(documentFirmado);

				// log.info("Estado enviado del SRI del servicio Recepcion: ...:"
				// + respuesta.getEstado());
				
				//log.info("Validando en reccepcion offline SRI 444..."+respuesta.getEstado());

				RespuestaSolicitud.Comprobantes  test = respuesta.getComprobantes();
				System.out.println(respuesta.getEstado());
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				//log.info("renvio por timeout.. "+e.getMessage());
			}

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
////			RespuestaComprobante respuestaComprobanteAut = autorizacionws.autorizacionComprobante(claveAcceso);
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
//				System.out.println("***" + autorizacion);
//			}
//
//		} catch (Exception ex) {
//			System.out.println("error "+ex);
//			respuesta = new ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion();
//			respuesta.setEstado("ERROR");
//		}

	}

	public static byte[] readFile(String filePath) throws FileException {
		try {
			File file = new File(filePath);
			byte[] content = org.apache.commons.io.FileUtils
					.readFileToByteArray(file);
			return content;

		} catch (IOException e) {
			throw new FileException(e);
		}
	}

}
