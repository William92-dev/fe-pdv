/*
 * FacturaModel.java
 *
 * Copyright (c) 2011 Servicio de Rentas Internas.
 * Todos los derechos reservados.
 */
package com.gizlocorp.firmaelectronica.util.key;

import java.io.File;

/**
 * Implementación que utiliza la nueva version del driver Aladin eToken PRO
 * 
 * @author Gabriel Eguiguren P.
 */
public class LinuxEProKeyStoreProvider extends PKCS11KeyStoreProvider {

	private static String CONFIG;
	private static String PATH;

	/**
	 * Constructor que revisa la ubicación de la librería
	 * 
	 */
	public LinuxEProKeyStoreProvider() {
		File lib = new File("/usr/lib/libeTPkcs11.so");
		File lib32 = new File("/usr/lib32/libeTPkcs11.so");
		File lib64 = new File("/usr/lib64/libeTPkcs11.so");

		if (lib.exists() == true) {
			PATH = "/usr/lib/";
		} else if (lib32.exists() == true) {
			PATH = "/usr/lib32/";
		} else if (lib64.exists() == true) {
			PATH = "/usr/lib64/";
		}

		StringBuffer config = new StringBuffer();
		config.append("name=eToken\n");
		config.append("library=");
		config.append(PATH);
		config.append("libeTPkcs11.so\n");

		CONFIG = config.toString();
	}

	@Override
	public String getConfig() {
		return CONFIG;
	}
}