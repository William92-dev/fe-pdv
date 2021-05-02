package com.gizlocorp.adm.utilitario;

import java.util.Random;

/**
 * Clase que genera passwords
 * 
 * @author Fausto De La Torre
 * @revision $Revision: 1.1 $
 */
public class PasswordGenerator {

	public static final char[] SECURE_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9' };

	/**
	 * Generates an eight characters long password consisting of hexadecimal
	 * characters.
	 * 
	 * @return the generated password
	 */
	public static String generate() {
		return generate(SECURE_CHARS, 8);
	}

	/**
	 * Generates a password consisting of hexadecimal characters.
	 * 
	 * @param length
	 *            of the password
	 * @return the generated password
	 */
	public static String generate(final int length) {
		return generate(SECURE_CHARS, length);
	}

	/**
	 * Generates a password according to the given parameters.
	 * 
	 * @param characters
	 *            that make up the password
	 * @param length
	 *            of the password
	 * @return the generated password
	 */
	public static String generate(final char[] characters, final int length) {
		Random random = new Random();
		StringBuffer buffer = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char randomChar = SECURE_CHARS[random.nextInt(SECURE_CHARS.length)];
			buffer.append(randomChar);
		}
		return buffer.toString();
	}
}
