package com.gizlocorp.firmaelectronica.util.key;

import java.io.File;
import java.util.logging.Logger;

/**
 * Obtiene la implementacion correcta de KeyStoreProvider de acuerdo al sistema
 * operativo.
 */
public class KeyStoreProviderFactory {

	private static final Logger log = Logger
			.getLogger(KeyStoreProviderFactory.class.getName());

	/**
	 * Obtiene la implementacion correcta de KeyStoreProvider de acuerdo al
	 * sistema operativo.
	 * 
	 * @return implementacion de KeyStoreProvider
	 * 
	 *         Modificacion GEguiguren: Funciona en cualquier otro JDK de
	 *         Windows que no tenga implementado SUNMSCAPI, utilizando la
	 *         librería del driver(.dll o .so) respectivamente
	 * 
	 */
	public static KeyStoreProvider createKeyStoreProvider() {
		String osName = System.getProperty("os.name");

		if (osName.toUpperCase().indexOf("WINDOWS") == 0) {
			return new WindowsOtherKeyStoreProvider();
		} else if (osName.toUpperCase().indexOf("LINUX") == 0) {
			if (existeLibreriaLinux() == true) {
				return new LinuxEProKeyStoreProvider();
			} else {
				return new LinuxKeyStoreProvider();
			}
		} else if (osName.toUpperCase().indexOf("MAC") == 0) {
			if (existeLibreriaMac() == true) {
				return new DylibKeyStoreProvider();
			} else {
				return new AppleKeyStoreProvider();
			}
		} else {
			throw new IllegalArgumentException(
					"Sistema operativo no soportado!");
		}
	}

	/**
	 * Verifica que existe la nueva version de libreria antes de llamar el
	 * proveedor del keystore
	 * 
	 * @author Gabriel Eguiguren
	 * @return
	 */
	private static boolean existeLibreriaLinux() {
		boolean resultado = false;

		File lib = new File("/usr/lib/libeTPkcs11.so");
		File lib32 = new File("/usr/lib32/libeTPkcs11.so");
		File lib64 = new File("/usr/lib64/libeTPkcs11.so");

		if (lib.exists() == true || lib32.exists() == true
				|| lib64.exists() == true) {
			resultado = true;
		}
		return resultado;
	}

	/**
	 * Verifica que existe la nueva version de libreria antes de llamar el
	 * proveedor del keystore
	 * 
	 * @author Gabriel Eguiguren
	 * @return
	 */
	public static boolean existeLibreriaMac() {
		boolean resultado = false;

		File lib = new File("/usr/local/lib/libeTPkcs11.dylib");

		if (lib.exists() == true) {
			resultado = true;
		}
		return resultado;
	}
}