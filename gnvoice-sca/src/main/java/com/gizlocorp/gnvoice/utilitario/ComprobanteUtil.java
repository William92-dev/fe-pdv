package com.gizlocorp.gnvoice.utilitario;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;
import com.gizlocorp.gnvoice.modelo.GuiaRemision;
import com.gizlocorp.gnvoice.modelo.NotaCredito;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;
import com.gizlocorp.gnvoice.modelo.Retencion;
import com.gizlocorp.gnvoice.xml.guia.Destinatario;
import com.gizlocorp.gnvoice.xml.retencion.Impuesto;

public class ComprobanteUtil {

	private static Logger log = Logger.getLogger(ComprobanteUtil.class
			.getName());

	public static String validaArchivoXSD(String pathArchivoXML,
			String pathArchivoXSD) throws Exception {
		String respuestaValidacion = null;
		ValidadorEstructuraDocumento validador = new ValidadorEstructuraDocumento();

		if (pathArchivoXML != null) {
			validador.setArchivoXML(new File(pathArchivoXML));
			validador.setArchivoXSD(new File(pathArchivoXSD));

			respuestaValidacion = validador.validacion();
		}

		return respuestaValidacion;
	}

	public static String generarCuerpoMensaje(
			final Map<String, Object> parametrosBody, final String descripcion,
			final String valor) throws Exception {
		Velocity.init();
		VelocityContext context = new VelocityContext();
		if (parametrosBody != null) {
			for (String key : parametrosBody.keySet()) {
				context.put(key, parametrosBody.get(key));
			}

		}
		StringWriter w = new StringWriter();
		w = new StringWriter();
		Velocity.evaluate(context, w, descripcion, valor);

		return w.toString();

	}

	public static String generarClaveAccesoProveedor(String fechaEmision,
			String tipoComprobante, String numeroRuc, String tipoAmbiente,
			String codigoNumerico) throws Exception {
		int digitoVerificador;
		String cadenaAVerificar = null;

		if (fechaEmision.contains("/")) {
			fechaEmision = fechaEmision.replace("/", "");
		} else if (fechaEmision.contains("-")) {
			fechaEmision = fechaEmision.replace("-", "");
		}

		cadenaAVerificar = fechaEmision + tipoComprobante + numeroRuc
				+ tipoAmbiente + codigoNumerico + "1";

		DigitoModulo11 digitoMoulo11 = new DigitoModulo11();

		digitoVerificador = digitoMoulo11.calculaDigito(cadenaAVerificar);
		String claveAcceso = cadenaAVerificar
				+ Integer.toString(digitoVerificador);
		return claveAcceso;
	}

	public static String generarClaveAccesoProveedor(String fechaEmision,
			String tipoComprobante, String claveContigencia) throws Exception {
		int digitoVerificador;
		String cadenaAVerificar = null;

		if (fechaEmision.contains("/")) {
			fechaEmision = fechaEmision.replace("/", "");
		} else if (fechaEmision.contains("-")) {
			fechaEmision = fechaEmision.replace("-", "");
		}

		cadenaAVerificar = fechaEmision + tipoComprobante + claveContigencia
				+ "2";

		log.debug(fechaEmision + "--" + tipoComprobante + "--"
				+ claveContigencia + "--" + "2");

		DigitoModulo11 digitoMoulo11 = new DigitoModulo11();
		digitoVerificador = digitoMoulo11.calculaDigito(cadenaAVerificar);
		String claveAcceso = cadenaAVerificar
				+ Integer.toString(digitoVerificador);
		return claveAcceso;
	}

	public static Factura convertirEsquemaAEntidadFactura(
			com.gizlocorp.gnvoice.xml.factura.Factura esquema) {
		log.debug("Convirtiendo a factura");
		Factura factura = new Factura();
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
		
		if(esquema.getInfoFactura().getCompensaciones() != null && esquema.getInfoFactura().getCompensaciones().getCompensacion() != null && 
				!esquema.getInfoFactura().getCompensaciones().getCompensacion().isEmpty()){					
			factura.setImporteTotal(esquema.getInfoFactura().getImporteTotal().subtract(esquema.getInfoFactura().getCompensaciones().getCompensacion().get(0).getValor()));
		}else{
			factura.setImporteTotal(esquema.getInfoFactura().getImporteTotal());
		}
		
		
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
		if(esquema.getInfoNotaCredito().getCompensaciones() != null && esquema.getInfoNotaCredito().getCompensaciones().getCompensacion() != null 
				&& !esquema.getInfoNotaCredito().getCompensaciones().getCompensacion().isEmpty()){
			BigDecimal totalcompensacion = new BigDecimal("0.00");
			for(com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito.Compensaciones.Compensacion itera: esquema.getInfoNotaCredito().getCompensaciones().getCompensacion()){
				log.info("lista compensacion 111***"+itera.getCodigo());
				if(itera.getCodigo().compareTo(new BigDecimal("1"))==0){					
					totalcompensacion = totalcompensacion.add(itera.getValor());
				}
				if(itera.getCodigo().compareTo(new BigDecimal("2"))==0){					
					totalcompensacion = totalcompensacion.add(itera.getValor());
				}
				if(itera.getCodigo().compareTo(new BigDecimal("4"))==0){					
					totalcompensacion = totalcompensacion.add(itera.getValor());
				}
			}
			notaCredito.setValorModificacion(esquema.getInfoNotaCredito().getValorModificacion().subtract(totalcompensacion));
		}else{
			notaCredito.setValorModificacion(esquema.getInfoNotaCredito().getValorModificacion());
		}
		
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

	public static GuiaRemision convertirEsquemaAEntidadGuiaRemision(
			com.gizlocorp.gnvoice.xml.guia.GuiaRemision esquema) {

		GuiaRemision guiaRemision = new GuiaRemision();

		guiaRemision.setClaveAcceso(esquema.getInfoTributaria()
				.getClaveAcceso());
		guiaRemision.setCodDoc(esquema.getInfoTributaria().getCodDoc());
		guiaRemision.setCodPuntoEmision(esquema.getInfoTributaria().getEstab());
		guiaRemision.setCodSecuencial(esquema.getInfoTributaria()
				.getSecuencial());
		guiaRemision.setDirEstablecimiento(esquema.getInfoGuiaRemision()
				.getDirEstablecimiento());
		guiaRemision.setDirPartida(esquema.getInfoGuiaRemision()
				.getDirPartida());
		guiaRemision.setRuc(esquema.getInfoTributaria().getRuc());

		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoGuiaRemision()
					.getFechaFinTransporte());
			guiaRemision.setFechaFinTransporteDb(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoGuiaRemision()
					.getFechaIniTransporte());
			guiaRemision.setFechaIniTransporteDb(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		guiaRemision.setPlaca(esquema.getInfoGuiaRemision().getPlaca());
		guiaRemision.setPtoEmision(esquema.getInfoTributaria().getPtoEmi());
		guiaRemision.setRazonSocialTransportista(esquema.getInfoGuiaRemision()
				.getRazonSocialTransportista());
		guiaRemision.setRise(esquema.getInfoGuiaRemision().getRise());
		guiaRemision.setRucTransportista(esquema.getInfoGuiaRemision()
				.getRucTransportista());
		guiaRemision.setTipoIdentificacionTransportista(esquema
				.getInfoGuiaRemision().getTipoIdentificacionTransportista());
		guiaRemision.setContribuyenteEspecial(esquema.getInfoGuiaRemision()
				.getContribuyenteEspecial());
		guiaRemision.setObligadoContabilidad(esquema.getInfoGuiaRemision()
				.getObligadoContabilidad());

		if (guiaRemision.getContribuyenteEspecial() == null) {
			guiaRemision.setContribuyenteEspecial("N");

		}

		guiaRemision.setTipoAmbiente(esquema.getInfoTributaria().getAmbiente());
		guiaRemision.setTipoEmision(esquema.getInfoTributaria()
				.getTipoEmision());

		if (esquema.getDestinatarios() != null
				&& esquema.getDestinatarios().getDestinatario() != null
				&& !esquema.getDestinatarios().getDestinatario().isEmpty()) {
			List<String> documentosRelacionado = new ArrayList<String>();

			for (Destinatario des : esquema.getDestinatarios()
					.getDestinatario()) {
				if (!documentosRelacionado.contains(des.getNumDocSustento())) {
					documentosRelacionado.add(des.getNumDocSustento());

				}
			}

			StringBuilder st = new StringBuilder();
			int i = 1;
			for (String des : documentosRelacionado) {
				st.append(des);
				if (i < documentosRelacionado.size()) {
					st.append(",");

				}
				i++;
			}

			guiaRemision.setDocumentosRelaciondos(st.toString());

		}

		return guiaRemision;
	}
	
	public static Retencion convertirEsquemaAEntidadComprobanteRetencion(
			com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion esquema) {

		Retencion comprobanteRetencion = new Retencion();

		comprobanteRetencion.setRazonSocial(esquema.getInfoTributaria()
				.getRazonSocial());
		comprobanteRetencion.setClaveAcceso(esquema.getInfoTributaria()
				.getClaveAcceso());
		comprobanteRetencion.setCodDoc(esquema.getInfoTributaria().getCodDoc());
		comprobanteRetencion.setCodPuntoEmision(esquema.getInfoTributaria()
				.getEstab());
		comprobanteRetencion.setCodSecuencial(esquema.getInfoTributaria()
				.getSecuencial());
		comprobanteRetencion.setContribuyenteEspecial(esquema
				.getInfoCompRetencion().getContribuyenteEspecial());
		comprobanteRetencion.setDirEstablecimiento(esquema
				.getInfoCompRetencion().getDirEstablecimiento());
		comprobanteRetencion.setRuc(esquema.getInfoTributaria().getRuc());

		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date newfecha = formatoFecha.parse(esquema.getInfoCompRetencion()
					.getFechaEmision());
			comprobanteRetencion.setFechaEmisionDb(newfecha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		comprobanteRetencion.setIdentificacionSujetoRetenido(esquema
				.getInfoCompRetencion().getIdentificacionSujetoRetenido());
		comprobanteRetencion.setObligadoContabilidad(esquema
				.getInfoCompRetencion().getObligadoContabilidad());
		comprobanteRetencion.setPeriodoFiscal(esquema.getInfoCompRetencion()
				.getPeriodoFiscal());
		comprobanteRetencion.setPtoEmision(esquema.getInfoTributaria()
				.getPtoEmi());
		comprobanteRetencion.setRazonSocialSujetoRetenido(esquema
				.getInfoCompRetencion().getRazonSocialSujetoRetenido());
		comprobanteRetencion.setTipoIdentificacionSujetoRetenido(esquema
				.getInfoCompRetencion().getIdentificacionSujetoRetenido());

		if (comprobanteRetencion.getContribuyenteEspecial() == null) {
			comprobanteRetencion.setContribuyenteEspecial("N");

		}

		comprobanteRetencion.setTipoAmbiente(esquema.getInfoTributaria()
				.getAmbiente());
		comprobanteRetencion.setTipoEmision(esquema.getInfoTributaria()
				.getTipoEmision());

		if (esquema.getImpuestos() != null
				&& esquema.getImpuestos().getImpuesto() != null
				&& !esquema.getImpuestos().getImpuesto().isEmpty()) {
			List<String> documentosRelacionado = new ArrayList<String>();

			for (Impuesto des : esquema.getImpuestos().getImpuesto()) {
				if (!documentosRelacionado.contains(des.getNumDocSustento())) {
					documentosRelacionado.add(des.getNumDocSustento());

				}
			}

			StringBuilder st = new StringBuilder();
			int i = 1;
			for (String des : documentosRelacionado) {
				st.append(des);
				if (i < documentosRelacionado.size()) {
					st.append(",");

				}
				i++;
			}
			comprobanteRetencion.setDocumentosRelaciondos(st.toString());

		}

		return comprobanteRetencion;

	}

}