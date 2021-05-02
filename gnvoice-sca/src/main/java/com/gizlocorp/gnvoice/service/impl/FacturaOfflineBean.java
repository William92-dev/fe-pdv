package com.gizlocorp.gnvoice.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.component.bean.Service;

import com.gizlocorp.adm.excepcion.ConverterException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.TripleDESUtil;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.excepcion.FileException;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaRequest;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaResponse;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWs;
import com.gizlocorp.gnvoice.firmaelectronica.ws.FirmaElectronicaWsService;
import com.gizlocorp.gnvoice.reporte.FacturaReporte;
import com.gizlocorp.gnvoice.service.FacturaOffline;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.utilitario.ReporteUtil;
import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringEscapeUtils;

@Service(FacturaOffline.class)
public class FacturaOfflineBean implements FacturaOffline {
	
	@EJB
	CacheBean cacheBean;
	
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;
	
	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;
	
	public static final Logger log = LoggerFactory.getLogger(FacturaOfflineBean.class);
	
	private ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud enviarComprobanteOffline(String wsdlLocation,
			String rutaArchivoXml) {
		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuesta = null;

		int retardo = 8000;

		try {
			byte[] documentFirmado = readFile(rutaArchivoXml);
			log.info("Inicia proceso reccepcion offline SRI ..." + wsdlLocation + " ..." + rutaArchivoXml);
			// while (contador <= reintentos) {
			try {
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService servicioRecepcion = new ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOfflineService(
						new URL(wsdlLocation));

				// log.info("Inicia proceso reccepcion offline SRI 111...");
				ec.gob.sri.comprobantes.ws.offline.rec.RecepcionComprobantesOffline recepcion = servicioRecepcion
						.getRecepcionComprobantesOfflinePort();

				// log.info("Inicia proceso reccepcion offline SRI ...222");

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.connect.timeout", retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
						retardo);

				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
						retardo);
				((BindingProvider) recepcion).getRequestContext().put("com.sun.xml.ws.request.timeout", retardo);

				// log.info("Validando en reccepcion offline SRI
				// 333..."+documentFirmado.length);

				respuesta = recepcion.validarComprobante(documentFirmado);

				log.info("Validando en reccepcion offline SRI 444..." + respuesta.getEstado());

				return respuesta;

			} catch (Exception e) {
				log.info("renvio por timeout.. " + e.getMessage());
			}

		} catch (Exception ex) {
			log.info(ex.getMessage(), ex);
			respuesta = new ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud();
			respuesta.setEstado(ex.getMessage());
		}

		if (respuesta == null || respuesta.getEstado() == null) {
			respuesta = new ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud();
			respuesta.setEstado("ENVIO NULL");

		}
		return respuesta;
	}

	private ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion autorizarComprobanteOffline(String wsdlLocation,
			String claveAcceso) {
		ec.gob.sri.comprobantes.ws.offline.aut.Autorizacion respuesta = null;

		// log.info("CLAVE DE ACCESO: " + claveAcceso);
		int timeout = 7000;

		try {
			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService servicioAutorizacion = new ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOfflineService(
					new URL(wsdlLocation),
					new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));

			ec.gob.sri.comprobantes.ws.offline.aut.AutorizacionComprobantesOffline autorizacionws = servicioAutorizacion
					.getAutorizacionComprobantesOfflinePort();

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.connect.timeout", timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout",
					timeout);

			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.internal.ws.request.timeout",
					timeout);
			((BindingProvider) autorizacionws).getRequestContext().put("com.sun.xml.ws.request.timeout", timeout);

			ec.gob.sri.comprobantes.ws.offline.aut.RespuestaComprobante respuestaComprobanteAut = autorizacionws
					.autorizacionComprobante(claveAcceso);

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


	@Override
	public FacturaConsultarResponse consultar(
			FacturaConsultarRequest facturaConsultarRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FacturaAutorizarResponse recibir(
			FacturaAutorizarRequest facturaAutorizarRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FacturaReceptarResponse procesar(
			FacturaReceptarRequest mensaje) {
		// TODO Auto-generated method stub
		FacturaReceptarResponse respuesta = new FacturaReceptarResponse();
		respuesta.setMensajes(new ArrayList<MensajeRespuesta>());

		log.info("Inicia proceso en modo offline facturacion");
		String rutaArchivoXml = null;
		String rutaArchivoFirmadoXml = null;
		String rutaArchivoAutorizacionXml = null;
		String rutaArchivoRechazadoXml = null;

		String rutaArchivoAutorizacionPdf = null;
		String rutaArchivoRechazadoPdf = null;

		String ambiente = null;
		String claveAcceso = null;
		Organizacion emisor = null;
		String wsdlLocationRecepcion = null;
		String wsdlLocationAutorizacion = null;

		com.gizlocorp.gnvoice.modelo.Factura comprobante = null;
		
		com.gizlocorp.gnvoice.modelo.Factura comprobanteExistente = null;

		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// GENERA DE COMPROBANTE
		try {

			log.info("Inicia validaciones previas");
			if (mensaje.getFactura() == null || mensaje.getFactura().getInfoTributaria() == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setMensajeSistema("Mensaje de entrada incorrecto. Nodo \"comprobante\" no puede venir vacio (null)");
				respuesta.setMensajeCliente("Datos de comprobante incorrectos. Factura vacia");

				return respuesta;
			}

			emisor = cacheBean.obtenerOrganizacion(mensaje.getFactura().getInfoTributaria().getRuc());

			if (emisor == null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("No existe registrado un emisor con RUC: " + mensaje.getFactura().getInfoTributaria().getRuc());
				respuesta.setMensajeCliente("No existe registrado un emisor con RUC: " + mensaje.getFactura().getInfoTributaria().getRuc());
				return respuesta;
			}

			if (mensaje.getFactura().getInfoTributaria().getSecuencial() == null || mensaje.getFactura().getInfoTributaria().getSecuencial().isEmpty()) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(null);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Secuencial no puede ser NULL");
				respuesta.setMensajeCliente("Secuencial no puede ser NULL");
				return respuesta;
			}

			Parametro ambParametro = cacheBean.consultarParametro(Constantes.AMBIENTE, emisor.getId());
			Parametro monParamtro = cacheBean.consultarParametro(Constantes.MONEDA, emisor.getId());
			Parametro codFacParametro = cacheBean.consultarParametro(Constantes.COD_FACTURA, emisor.getId());
			ambiente = ambParametro.getValor();

			//wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
			//wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
			
			wsdlLocationRecepcion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
			wsdlLocationAutorizacion = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

			//if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				//wsdlLocationRecepcion = cacheBean.consultarParametro(Constantes.SERVICIO_REC_SRI_PRODUCCION, emisor.getId()).getValor();

			//} else {
				//wsdlLocationRecepcion = cacheBean.consultarParametro(Constantes.SERVICIO_REC_SRI_PRUEBA, emisor.getId()).getValor();
			//}
			//if (TipoAmbienteEnum.PRODUCCION.getCode().equals(ambiente)) {
				//wsdlLocationAutorizacion = cacheBean.consultarParametro(Constantes.SERVICIO_AUT_SRI_PRODUCCION, emisor.getId()).getValor();
			//} else {
				//wsdlLocationAutorizacion = cacheBean.consultarParametro(Constantes.SERVICIO_AUT_SRI_PRUEBA, emisor.getId()).getValor();
			//}

			try {
				comprobanteExistente = servicioFactura.obtenerComprobante(null,mensaje.getCodigoExternoComprobante(), mensaje.getFactura().getInfoTributaria().getRuc(),mensaje.getCodigoAgencia());
			} catch (Exception ex) {
				InitialContext ic = new InitialContext();
				servicioFactura = (ServicioFactura) ic
						.lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
				comprobanteExistente = servicioFactura.obtenerComprobante(null,
						mensaje.getCodigoExternoComprobante(), 
						mensaje.getFactura().getInfoTributaria().getRuc(),
						mensaje.getCodigoAgencia());

			}
			//validando si el comprobante existe
			
			//si existe y est autorizado solo se retorna la respuesta
			if (comprobanteExistente != null && Estado.AUTORIZADO.getDescripcion().equals(comprobanteExistente.getEstado())) {
				respuesta.setEstado(Estado.AUTORIZADO.getDescripcion());
				respuesta.setMensajeCliente("Comprobante AUTORIZADO");
				respuesta.setMensajeSistema("Comprobante AUTORIZADO");
				respuesta.setClaveAccesoComprobante(comprobanteExistente.getClaveAcceso());
				respuesta.setFechaAutorizacion(comprobanteExistente.getFechaAutorizacion());
				respuesta.setNumeroAutorizacion(comprobanteExistente.getNumeroAutorizacion());

				return respuesta;
			}
			
			//si el comprobante no existe

			// Setea nodo principal
			mensaje.getFactura().setId("comprobante");
			//mensaje.getFactura().setVersion(Constantes.VERSION);
			mensaje.getFactura().setVersion(Constantes.VERSION_1);

			mensaje.getFactura().getInfoFactura().setMoneda(monParamtro.getValor());
			mensaje.getFactura().getInfoTributaria().setAmbiente(ambParametro.getValor());
			mensaje.getFactura().getInfoTributaria().setCodDoc(codFacParametro.getValor());

			if (mensaje.getFactura().getInfoTributaria().getSecuencial().length() < 9) {
				StringBuilder secuencial = new StringBuilder();

				int secuencialTam = 9 - mensaje.getFactura().getInfoTributaria().getSecuencial().length();
				for (int i = 0; i < secuencialTam; i++) {
					secuencial.append("0");
				}
				secuencial.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
				mensaje.getFactura().getInfoTributaria().setSecuencial(secuencial.toString());
			}

			mensaje.getFactura().getInfoTributaria().setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());

			String dirMatriz = emisor.getDireccion();

			mensaje.getFactura()
					.getInfoTributaria()
					.setDirMatriz(
							dirMatriz != null ? StringEscapeUtils
									.escapeXml(dirMatriz.trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "") : null);

			if (mensaje.getFactura().getInfoTributaria().getEstab() == null
					|| mensaje.getFactura().getInfoTributaria().getEstab()
							.isEmpty()) {
				mensaje.getFactura().getInfoTributaria()
						.setEstab(emisor.getEstablecimiento());

			}
			mensaje.getFactura()
					.getInfoTributaria()
					.setNombreComercial(
							emisor.getNombreComercial() != null ? StringEscapeUtils
									.escapeXml(
											emisor.getNombreComercial().trim())
									.replaceAll("[\n]", "")
									.replaceAll("[\b]", "")
									: null);

			if (mensaje.getFactura().getInfoTributaria().getPtoEmi() == null
					|| mensaje.getFactura().getInfoTributaria().getPtoEmi()
							.isEmpty()) {
				mensaje.getFactura().getInfoTributaria()
						.setPtoEmi(emisor.getPuntoEmision());
			}
			mensaje.getFactura()
					.getInfoTributaria()
					.setRazonSocial(
							emisor.getNombre() != null ? StringEscapeUtils
									.escapeXml(emisor.getNombre()) : null);

			mensaje.getFactura()
					.getInfoFactura()
					.setContribuyenteEspecial(emisor.getResolContribuyenteEsp());
			if (mensaje.getFactura().getInfoFactura().getDirEstablecimiento() == null
					|| mensaje.getFactura().getInfoFactura()
							.getDirEstablecimiento().isEmpty()) {
				mensaje.getFactura()
						.getInfoFactura()
						.setDirEstablecimiento(
								emisor.getDirEstablecimiento() != null ? StringEscapeUtils
										.escapeXml(emisor
												.getDirEstablecimiento())
										: null);
			}
			mensaje.getFactura()
					.getInfoFactura()
					.setObligadoContabilidad(
							emisor.getEsObligadoContabilidad().getDescripcion()
									.toUpperCase());

			
			StringBuilder codigoNumerico = new StringBuilder();
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getEstab());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getPtoEmi());
			log.info("codigo numerico - secuencial de factura: " + mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(mensaje.getFactura().getInfoTributaria().getSecuencial());
			codigoNumerico.append(Constantes.CODIGO_NUMERICO);
			//genera clave de accesso
			claveAcceso = ComprobanteUtil.generarClaveAccesoProveedor(mensaje
					.getFactura().getInfoFactura().getFechaEmision(),
					codFacParametro.getValor(), mensaje.getFactura()
							.getInfoTributaria().getRuc(), mensaje.getFactura()
							.getInfoTributaria().getAmbiente(),
					codigoNumerico.toString());
			
			mensaje.getFactura().getInfoTributaria().setClaveAcceso(claveAcceso);
			

			log.info("finaliza validaciones previas");
			log.info("Inicia generacion de comproabante");
			comprobante = ComprobanteUtil.convertirEsquemaAEntidadFactura(mensaje.getFactura());

			comprobante.setClaveInterna(mensaje.getCodigoExternoComprobante());
			comprobante.setCorreoNotificacion(mensaje.getCorreoElectronicoNotificacion());
			comprobante.setIdentificadorUsuario(mensaje.getIdentificadorUsuario());
			comprobante.setAgencia(mensaje.getCodigoAgencia());

			comprobante.setTipoEjecucion(TipoEjecucion.SEC);
			//comprobante.setSecuencialOriginal(mensaje.getSid());		

			//si el comprobanta no existe y es generado por primera vez
			
			// Genera comprobante
			rutaArchivoXml = DocumentUtil.createDocument(getFacturaXML(mensaje
					.getFactura()), mensaje.getFactura().getInfoFactura()
					.getFechaEmision(), claveAcceso,
					com.gizlocorp.gnvoice.enumeracion.TipoComprobante.FACTURA
							.getDescripcion(), "enviado");
			log.info("ruta de archivo es: " + rutaArchivoXml);
			
			String validacionXsd = ComprobanteUtil.validaArchivoXSD(
					rutaArchivoXml,
					com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
							+ "/gnvoice/recursos/xsd/factura.xsd");

			if (validacionXsd != null) {
				respuesta.setEstado(Estado.ERROR.getDescripcion());
				respuesta.setClaveAccesoComprobante(claveAcceso);
				respuesta.setFechaAutorizacion(null);
				respuesta.setNumeroAutorizacion(null);

				respuesta.setMensajeSistema("Error en esquema comprobante. " + validacionXsd);
				respuesta.setMensajeCliente("Error en la estructura del comprobante enviado.");
				// Actualiza estado comprobante
				log.info("comprobante: " + comprobante + " archivo: "+ rutaArchivoXml);
				comprobante.setArchivo(rutaArchivoXml);
				return respuesta;
			}

			log.info("Comprobante Generado Correctamente!!");
			// Actualiza estado comprobante
			comprobante.setArchivo(rutaArchivoXml);

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("Ha ocurrido un error en el sistema: "+ ex.getMessage());
			respuesta.setMensajeCliente("Ha ocurrido un error en el sistema, comprobante no Generado");

			if (comprobante != null && comprobante.getId() != null) {
				comprobante.setArchivo(rutaArchivoXml);
			}

			return respuesta;
		}
		/*-------------------------------------------------------------------------------------------------------------------------------------*/
		// FIRMA COMPROBANTE
		comprobante.setArchivo(rutaArchivoFirmadoXml);

		FirmaElectronicaWsService serviceFirma = new FirmaElectronicaWsService();
		FirmaElectronicaWs firmaElectronica = serviceFirma
				.getFirmaElectronicaWsPort();

		FirmaElectronicaRequest firmaRequest = new FirmaElectronicaRequest();
		try {
			firmaRequest.setClave(TripleDESUtil._decrypt(emisor.getClaveToken()));
		} catch (Exception ex) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("No se puede desencriptar clave Firma: " + ex.getMessage());
			respuesta.setMensajeCliente("No se puede desencriptar clave Firma: ");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		firmaRequest.setRutaFirma(emisor.getToken());
		firmaRequest.setRutaArchivo(rutaArchivoXml);
		FirmaElectronicaResponse firmaResponse = null;

		try {
			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.ws.connect.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.internal.ws.connect.timeout", 5000L);

			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.internal.ws.request.timeout", 5000L);
			((BindingProvider) firmaElectronica).getRequestContext().put(
					"com.sun.xml.ws.request.timeout", 5000L);

			firmaResponse = firmaElectronica.firmarDocumento(firmaRequest);
		} catch (Exception ex) {
			log.error("********* FIRMA", ex);
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("No se puede firmar: "+ ex.getMessage());
			respuesta.setMensajeCliente("No se puede firmar: ");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		if (firmaResponse == null || "ERROR".equals(firmaResponse.getEstado())) {
			respuesta.setEstado(Estado.ERROR.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			respuesta.setMensajeSistema("Error al firmar el documento: "
					+ firmaResponse.getMensajesistema());
			respuesta.setMensajeCliente("Error al firmar el documento");

			comprobante.setArchivo(rutaArchivoXml);

			return respuesta;
		}

		rutaArchivoFirmadoXml = firmaResponse.getRutaArchivoFirmado();
		comprobante.setArchivo(rutaArchivoFirmadoXml);

		comprobante.setArchivo(rutaArchivoFirmadoXml);
		//comprobante.setModoEnvio("OFFLINE");

		ec.gob.sri.comprobantes.ws.offline.rec.RespuestaSolicitud respuestaRecepcion = enviarComprobanteOffline(wsdlLocationRecepcion, rutaArchivoFirmadoXml);
		
		log.info("***receptando comprobante***!!"+respuestaRecepcion.getEstado());
		
		try {
			
			String comprobantexml = DocumentUtil.readContentFile(rutaArchivoFirmadoXml);
			
			com.gizlocorp.gnvoice.xml.factura.Factura factAutorizadaXML = getFacturaXML(comprobantexml);
			
			try {

				rutaArchivoAutorizacionXml = DocumentUtil
						.createDocument(
								getFacturaXML(factAutorizadaXML),
								mensaje.getFactura().getInfoFactura()
										.getFechaEmision(),
								claveAcceso, TipoComprobante.FACTURA
										.getDescripcion(), "autorizado");

			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
			
			ReporteUtil reporte = new ReporteUtil();
			rutaArchivoAutorizacionPdf = reporte.generarReporte(
					Constantes.REP_FACTURA, new FacturaReporte(factAutorizadaXML), 
					mensaje.getFactura().getInfoTributaria().getClaveAcceso(), null,
									mensaje.getFactura().getInfoFactura()
							.getFechaEmision(), "autorizado",
					false, emisor.getLogoEmpresa());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		
//		try {
//			if (mensaje.getCorreoElectronicoNotificacion() != null
//					&& !mensaje.getCorreoElectronicoNotificacion().isEmpty()) {
//				Plantilla t = cacheBean.obtenerPlantilla(
//						Constantes.NOTIFIACION, emisor.getId());
//
//				Map<String, Object> parametrosBody = new HashMap<String, Object>();
//				parametrosBody.put("nombre", mensaje.getFactura()
//						.getInfoFactura().getRazonSocialComprador());
//				parametrosBody.put("tipo", "FACTURA");
//				parametrosBody.put("secuencial", mensaje.getFactura()
//						.getInfoTributaria().getSecuencial());
//				parametrosBody.put("fechaEmision", mensaje.getFactura()
//						.getInfoFactura().getFechaEmision());
//				parametrosBody.put("total", mensaje.getFactura()
//						.getInfoFactura().getImporteTotal());
//				parametrosBody.put("emisor", emisor.getNombre());
//				parametrosBody.put("ruc", emisor.getRuc());
//				parametrosBody.put("numeroAutorizacion", mensaje.getFactura()
//						.getInfoTributaria().getClaveAcceso());
//
//				MailMessage mailMensaje = new MailMessage();
//				mailMensaje.setSubject(t
//						.getTitulo()
//						.replace("$tipo", "FACTURA")
//						.replace(
//								"$numero",
//								mensaje.getFactura().getInfoTributaria()
//										.getSecuencial()));
//				mailMensaje.setFrom(cacheBean.consultarParametro(
//						Constantes.CORREO_REMITE, emisor.getId()).getValor());
//				mailMensaje.setTo(Arrays.asList(mensaje
//						.getCorreoElectronicoNotificacion().split(";")));
//				mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(
//						parametrosBody, t.getDescripcion(), t.getValor()));
//				mailMensaje.setAttachment(Arrays.asList(rutaArchivoFirmadoXml,
//						rutaArchivoAutorizacionPdf));
//				MailDelivery.send(
//						mailMensaje,
//						cacheBean.consultarParametro(Constantes.SMTP_HOST,
//								emisor.getId()).getValor(),
//						cacheBean.consultarParametro(Constantes.SMTP_PORT,
//								emisor.getId()).getValor(),
//						cacheBean.consultarParametro(Constantes.SMTP_USERNAME,
//								emisor.getId()).getValor(),
//						cacheBean.consultarParametro(Constantes.SMTP_PASSWORD,
//								emisor.getId()).getValorDesencriptado(), emisor
//								.getAcronimo());
//			}
//
//		} catch (Exception ex) {
//			log.error(ex.getMessage(), ex);
//
//		}

		if ("RECIBIDA".equals(respuestaRecepcion.getEstado())) {
			comprobante.setArchivo(rutaArchivoFirmadoXml);
			
			log.info("***recibida***!!");

			respuesta.setEstado(Estado.RECIBIDA.getDescripcion());	
			
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setNumeroAutorizacion(claveAcceso);

			respuesta.setMensajeSistema("Comprobante AUTORIZADO");
			respuesta.setMensajeCliente("Comprobante AUTORIZADO");

			comprobante.setNumeroAutorizacion(claveAcceso);
			comprobante.setArchivo(rutaArchivoAutorizacionXml);
			comprobante.setArchivoLegible(rutaArchivoAutorizacionPdf);
			//comprobante.setEstadoOffline(Estado.RECIBIDA.getDescripcion());
			
			
			log.info("***recibida 222***!!");
			try {
				servicioFactura.autoriza(comprobante);
			} catch (Exception ex) {
				try {
					InitialContext ic = new InitialContext();
					servicioFactura = (ServicioFactura) ic.lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
					servicioFactura.autoriza(comprobante);

				} catch (Exception ex2) {
					log.warn("no se guardo el comprobante");
				}
			}
			
			try {
				Thread.currentThread();
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				log.info(e.getMessage(), e);
			}		
						
			log.info("***autorizando***!!");
			
			return respuesta;
			
			/*---FIN AUTORIZACION------------------------------------------------------------------------------------------------------------------*/
		} else if ("DEVUELTA".equals(respuestaRecepcion.getEstado())) {
			// DEVUELTA
			// Configura valores de respuesta
			StringBuilder mensajeRespuesta = new StringBuilder();
			if (respuestaRecepcion.getComprobantes() != null
					&& respuestaRecepcion.getComprobantes().getComprobante() != null
					&& !respuestaRecepcion.getComprobantes().getComprobante().isEmpty()) {
				MensajeRespuesta msgObj = null;
				for (ec.gob.sri.comprobantes.ws.offline.rec.Comprobante comprobanteRecepcion : respuestaRecepcion.getComprobantes().getComprobante()) {
					if (comprobanteRecepcion.getMensajes() != null) {
						for (ec.gob.sri.comprobantes.ws.offline.rec.Mensaje msg : comprobanteRecepcion.getMensajes().getMensaje()) {
							msgObj = new MensajeRespuesta();
							msgObj.setIdentificador(msg.getIdentificador());
							msgObj.setInformacionAdicional(msg.getInformacionAdicional());
							msgObj.setMensaje(msg.getMensaje());
							msgObj.setTipo(msg.getTipo());
							respuesta.getMensajes().add(msgObj);

							mensajeRespuesta.append("Identificador: ");
							mensajeRespuesta.append(msg.getIdentificador());
							mensajeRespuesta.append(" Mensaje: ");
							mensajeRespuesta.append(msg.getMensaje());
							mensajeRespuesta.append(" Informacion Adicional: ");
							mensajeRespuesta.append(msg.getInformacionAdicional());
							mensajeRespuesta.append(" <br>");
						}
					}
				}
				respuesta.setMensajeSistema(mensajeRespuesta.toString());
				respuesta.setMensajeCliente("Comprobante DEVUELTO");
			}
			
			respuesta.setEstado(Estado.DEVUELTA.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			comprobante.setArchivo(rutaArchivoRechazadoXml);
			comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);
			//comprobante.setEstadoOffline(EstadoOffline.RECHAZADO.getDescripcion());
			
			log.info("***recibida 222***!!");
			try {
				servicioFactura.autoriza(comprobante);
			} catch (Exception ex) {
				try {
					InitialContext ic = new InitialContext();
					servicioFactura = (ServicioFactura) ic.lookup("java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura");
					servicioFactura.autoriza(comprobante);

				} catch (Exception ex2) {
					log.warn("no se guardo el comprobante");
				}
			}
			
			try {
				Thread.currentThread();
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				log.info(e.getMessage(), e);
			}		
						
			log.info("***autorizando***!!");

			return respuesta;

		} else {

			// TRANSMITIDO SIN RESPUESTA
			respuesta.setMensajeCliente("TRANSMITIDO SIN RESPUESTA CONTIGENCIA");
			respuesta.setEstado(Estado.PENDIENTE.getDescripcion());
			respuesta.setClaveAccesoComprobante(claveAcceso);
			respuesta.setFechaAutorizacion(null);
			respuesta.setNumeroAutorizacion(null);

			comprobante.setArchivo(rutaArchivoRechazadoXml);
			comprobante.setArchivoLegible(rutaArchivoRechazadoPdf);

			return respuesta;
		}

	}

	
	
	private static byte[] readFile(String filePath) throws FileException {
		try {
			File file = new File(filePath);
			byte[] content = org.apache.commons.io.FileUtils.readFileToByteArray(file);
			return content;

		} catch (IOException e) {
			throw new FileException(e);
		}
	}
	
	private String getFacturaXML(Object comprobante) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return converter.convertirDeObjetoFormat(comprobante);
	}

	private com.gizlocorp.gnvoice.xml.factura.Factura getFacturaXML(
			String comprobanteXML) throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (com.gizlocorp.gnvoice.xml.factura.Factura) converter
				.convertirAObjeto(comprobanteXML,
						com.gizlocorp.gnvoice.xml.factura.Factura.class);
	}

	private Autorizacion getAutorizacionXML(String comprobanteXML)
			throws ConverterException {
		Converter converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
		return (Autorizacion) converter.convertirAObjeto(comprobanteXML,
				Autorizacion.class);
	}
}
