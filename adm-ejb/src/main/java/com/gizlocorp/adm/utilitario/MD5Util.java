package com.gizlocorp.adm.utilitario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.jboss.security.auth.spi.Util;

public class MD5Util {

	private static Logger log = Logger.getLogger(MD5Util.class.getName());

	private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int NUMERO_CARATERES = 5;

	/**
	 * Metodo encargado de obtener el md5 de un texto
	 * 
	 * @param texto
	 *            Texto del cual vas a sacar el MD5
	 * @return El MD5 del texto
	 */
	public static String getMD5(String texto) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(texto.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return
	 */
	public static String generarPassword() {
		StringBuffer sb = new StringBuffer(20);
		while (sb.length() <= NUMERO_CARATERES) {
			int pos = (int) (Math.random() * CARACTERES.length());
			sb.append(CARACTERES.charAt(pos));
		}
		return sb.toString();
	}

	public static void main(String args[]) throws NoSuchAlgorithmException {
		String createPasswordHash = Util.createPasswordHash("SHA-256", "hex",
				null, null, "0919348227");
		System.out.println(createPasswordHash);
		log.debug(createPasswordHash.toLowerCase());
	}

}
