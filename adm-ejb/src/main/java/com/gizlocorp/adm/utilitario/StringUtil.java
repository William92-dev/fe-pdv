package com.gizlocorp.adm.utilitario;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author gizlo
 */
public class StringUtil {

	private static Logger log = Logger.getLogger(StringUtil.class.getName());

	public static String validateEmail(String email) {
		return validatePattern(
				email,
				"([A-Za-z0-9]+[_A-Za-z0-9-.][^..]+(\\.[A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))");
	          //"([A-Za-z0-9]+[_A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))");

	}

	public static String validateInfo(String value) {
		return validatePattern(value,
				"([A-Za-z0-9ñÑ]+([A-Za-z0-9ñÑ.,-_]|[^\\n])*)");

	}

	public static boolean esNumero(String value) {
		Pattern pattern = Pattern.compile("-?\\d+");
		return value != null && pattern.matcher(value).matches();

	}

	public static String validateInfoXML(String value) {
		String respuesta = validateInfo(value);

		if (respuesta == null) {
			return "";
		}

		respuesta = respuesta.replace("/", "").replace("\\", "")
				.replace("#", "").replace("→", "").replace("%", "")
				.replace("?", "").replace("¿", "").replace("Ã", "")
				.replace("Â", "").replace("¡", "").replace("!", "")
				.replace("ƒ", "").replace("±", "").replace(";", "")
				.replace(",", "").replace("^", "");

		respuesta = StringEscapeUtils.escapeXml(respuesta);
		respuesta = respuesta.replace("&#147;", "").replace("&#148;", "")
				.replace("&#195;", "").replace("&#194;", "")
				.replace("&#8594;", "").replace("&#131;", "")
				.replace("&#145;", "").replace("&#211;", "").replace("&#209;", "Ñ")
	            .replace("&#241;", "ñ");
		
		respuesta = respuesta.replaceAll("[&#]+\\d{3}+[;]", "");

		if (respuesta == null || respuesta.isEmpty()) {
			respuesta = value.replace("/", "").replace("\\", "")
					.replace("#", "").replace("→", "").replace("%", "")
					.replace("?", "").replace("¿", "").replace("Ã", "")
					.replace("Â", "").replace("¡", "").replace("!", "")
					.replace("ƒ", "").replace("±", "").replace(";", "")
					.replace(",", "").replace("^", "");

			respuesta = StringEscapeUtils.escapeXml(respuesta);
			respuesta = respuesta.replace("&#147;", "").replace("&#148;", "")
					.replace("&#195;", "").replace("&#194;", "")
					.replace("&#8594;", "").replace("¿", "")
					.replace("&#131;", "").replace("&#145;", "").replace("&#211;", "").replace("&#209;", "Ñ").replace("&#241;", "ñ");
			
			respuesta = respuesta.replaceAll("[&#]+\\d{3}+[;]", "");

		}

		return respuesta;
	}

	private static String validatePattern(String value, String pattern) {

		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		value = value.trim();
		String respuesta = null;
		Pattern limpiar = Pattern.compile(pattern);
		Matcher buscar = limpiar.matcher(value);
		while (buscar.find()) {
			respuesta = buscar.group(1);

		}

		if (respuesta == null) {
			log.error("ERROR VALOR NO CUMPLE FORMATO: " + value);
		}

		return respuesta;

	}

	public static String getTipoIdentificacion(String tipoIddentificacion) {
		if (tipoIddentificacion.equals("CEDULA")) {
			return "C";
		}
		if (tipoIddentificacion.equals("RUC")) {
			return "R";
		}
		if (tipoIddentificacion.equals("PASAPORTE")) {
			return "P";
		}
		if (tipoIddentificacion.equals("PASAPORTE")) {
			return null;
		}
		return null;
	}

	public static String getSlectedItem(String tipoIddentificacion) {
		if (tipoIddentificacion.equals("C")) {
			return "CEDULA";
		}
		if (tipoIddentificacion.equals("R")) {
			return "RUC";
		}
		if (tipoIddentificacion.equals("P")) {
			return "PASAPORTE";
		}
		if (tipoIddentificacion.equals("")) {
			return "SELECCIONE";
		}
		return null;
	}

	public static String getSlectedItemTipoProducto(String tipoIddentificacion) {
		if (tipoIddentificacion.equals("B")) {
			return "BIEN";
		}
		if (tipoIddentificacion.equals("S")) {
			return "SERVICIO";
		}
		return null;
	}

	public static String getSelectedTipoAmbiente(String tipoIddentificacion) {
		if (tipoIddentificacion.equals("2")) {
			return "PRODUCCION";
		}
		if (tipoIddentificacion.equals("1")) {
			return "PRUEBAS";
		}
		return null;
	}

	public static String obtenerTipoEmision(String valorCombo) {
		if (valorCombo.equalsIgnoreCase("NORMAL")) {
			return "1";
		}
		if (valorCombo.equalsIgnoreCase("INDISPONIBILIDAD DE SISTEMA")) {
			return "2";
		}
		return null;
	}

	public static String obtenerNumeroTipoEmision(String tipoEmision) {
		if (tipoEmision.equalsIgnoreCase("1")) {
			return "NORMAL";
		}
		if (tipoEmision.equalsIgnoreCase("3")) {
			return "BAJA CONECTIVIDAD";
		}
		if (tipoEmision.equalsIgnoreCase("2")) {
			return "INDISPONIBILIDAD DE SISTEMA";
		}
		return null;
	}

	public static boolean validarExpresionRegular(String patron, String valor) {
		if (patron != null && valor != null) {
			Pattern pattern = Pattern.compile(patron);
			Matcher matcher = pattern.matcher(valor);
			return matcher.matches();
		}
		return false;
	}

	public static String obtenerDocumentoModificado(String codDoc) {
		if ("01".equals(codDoc)) {
			return "FACTURA";
		}
		if ("04".equals(codDoc)) {
			return "NOTA DE CREDITO";
		}
		if ("05".equals(codDoc)) {
			return "NOTA DE DEBITO";
		}
		if ("06".equals(codDoc)) {
			return "GUIA REMISION";
		}
		if ("07".equals(codDoc)) {
			return "COMPROBANTE DE RETENCION";
		}
		return null;
	}

	public static String completaCadena(int numeroaCompletar, String cadena) {
		StringBuilder sb = new StringBuilder();

		for (int toPrepend = numeroaCompletar - cadena.length(); toPrepend > 0; toPrepend--) {
			sb.append('0');
		}

		return sb.append(cadena).toString();
	}

	public static void main(String[] args) {
		/*String abc = "Los años son  Ñ:";
		String regex = "[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]";	    
		
		String ab=StringUtil.validateInfoXML("Los años son  Ñ:");
		String ab2=StringEscapeUtils.escapeXml("Los años son  Ñ:");
		System.out.println(ab);
		System.out.println(ab2);
		System.out.println(abc.replaceAll(regex, ""));*/
		
		String datos = validateEmail("s.ramirez@gmail.com");
		
		System.out.println(datos);

	}
}
