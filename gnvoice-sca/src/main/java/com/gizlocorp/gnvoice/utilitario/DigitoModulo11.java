package com.gizlocorp.gnvoice.utilitario;

import org.apache.log4j.Logger;

/**
 * @author
 * 
 */

public class DigitoModulo11 {
	private static Logger log = Logger
			.getLogger(DigitoModulo11.class.getName());

	public int[] pasarADigitosNew(String cadenaAVerificar) {
		int cadenaVerificar[] = new int[48];
		for (int i = 0; i < 48; i++) {
			cadenaVerificar[i] = Integer.parseInt((cadenaAVerificar.substring(
					i, i + 1)));

		}

		return cadenaVerificar;
	}

	public int calculaDigito(String cadenaAVerificar) {

		int suma = 0;
		int modulo = 0;
		int[] peso = { 7, 6, 5, 4, 3, 2, 7, 6, 5, 4, 3, 2, 7, 6, 5, 4, 3, 2, 7,
				6, 5, 4, 3, 2, 7, 6, 5, 4, 3, 2, 7, 6, 5, 4, 3, 2, 7, 6, 5, 4,
				3, 2, 7, 6, 5, 4, 3, 2 };

		log.debug("cadena en verificacion de calculadigito: " + cadenaAVerificar);
		int claveAcceso[] = pasarADigitosNew(cadenaAVerificar);

		for (int i = 0; i < 48; i++) {

			suma = suma + (claveAcceso[i] * peso[i]);

		}

		modulo = suma % 11;
		int digitoResultado = 11 - modulo;

		// Para validar los casos especiales
		if (digitoResultado == 11) {
			digitoResultado = 0;

		}

		else {
			if (digitoResultado == 10) {
				digitoResultado = 1;

			}
		}

		return digitoResultado;

	}

	/*
	 * public static String calculaDigito11(String digStr) { int len =
	 * digStr.length(); int sum = 0, rem = 0; int[] digArr = new int[len]; for
	 * (int k=1; k<=len; k++) // compute weighted sum sum += (11 - k) *
	 * Character.getNumericValue(digStr.charAt(k - 1)); if ((rem = sum % 11) ==
	 * 0) return "0"; else if (rem == 1) return "X"; return (new Integer(11 -
	 * rem)).toString(); }
	 * 
	 * 
	 * public String addCheckDigit(String number) {
	 * 
	 * int claveAcceso[]=pasarADigitosNew(number); int Sum = 0; for (int i =
	 * number.length() - 1, Multiplier = 2; i >= 0; i--) //for (int i = 0,
	 * Multiplier = 2; i <= number.length() ; i++) { Sum += new
	 * Integer((claveAcceso[i]) * Multiplier);
	 * 
	 * if (++Multiplier == 8) Multiplier = 2; } String Validator = new
	 * Integer(11 - (Sum % 11)).toString();
	 * 
	 * if (Validator == "11") Validator = "0"; else if (Validator == "10")
	 * Validator = "X";
	 * 
	 * return number + Validator; }
	 */
}
