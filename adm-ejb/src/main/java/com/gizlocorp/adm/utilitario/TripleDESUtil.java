package com.gizlocorp.adm.utilitario;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class TripleDESUtil {

	// private static Logger log =
	// Logger.getLogger(TripleDESUtil.class.getName());

	private final static String SECRET_KEY = "LKJADSTREGJH$987";

	public static void main(String args[]) throws Exception {
		
		System.out.println(_encrypt("FRrocio1973"));
		//System.out.println(_decrypt("61GOqyl3RNTnEaZFLCK30A=="));
	}

	public static String _encrypt(String message) throws Exception {

		if (message == null) {
			return null;
		}
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digestOfPassword = md.digest(SECRET_KEY.getBytes("utf-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

		SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] plainTextBytes = message.getBytes("utf-8");
		byte[] buf = cipher.doFinal(plainTextBytes);
		byte[] base64Bytes = Base64.encodeBase64(buf);
		String base64EncryptedString = new String(base64Bytes);

		return base64EncryptedString;
	}

	public static String _decrypt(String encryptedText) throws Exception {

		if (encryptedText == null) {
			return null;
		}

		byte[] message = Base64.decodeBase64(encryptedText.getBytes("utf-8"));

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digestOfPassword = md.digest(SECRET_KEY.getBytes("utf-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		SecretKey key = new SecretKeySpec(keyBytes, "DESede");

		Cipher decipher = Cipher.getInstance("DESede");
		decipher.init(Cipher.DECRYPT_MODE, key);

		byte[] plainText = decipher.doFinal(message);

		return new String(plainText, "UTF-8");
	}
}
