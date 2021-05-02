/*
 * MacKeyStoreProvider.java
 *
 * Copyright (c) 2012 Servicio de Rentas Internas.
 * Todos los derechos reservados.
 */
package com.gizlocorp.firmaelectronica.util.key;

/**
 * Implementación que utiliza la nueva version del driver Aladin eToken PRO
 * 
 * @author Gabriel Eguiguren P.
 */
public class DylibKeyStoreProvider extends PKCS11KeyStoreProvider {

	private static String CONFIG;

	/**
	 * Constructor
	 * 
	 */
	public DylibKeyStoreProvider() {

		StringBuffer config = new StringBuffer();
		config.append("name=eToken\n");
		config.append("library=/usr/local/lib/libeTPkcs11.dylib\n");

		CONFIG = config.toString();
	}

	@Override
	public String getConfig() {
		return CONFIG;
	}
}