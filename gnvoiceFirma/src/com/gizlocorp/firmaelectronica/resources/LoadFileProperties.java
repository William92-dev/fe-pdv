package com.gizlocorp.firmaelectronica.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class LoadFileProperties. Utilitario ppara cargar archivo de propiedades
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class LoadFileProperties {
	private static final String BUNDLE_NAME = "com.gizlocorp.firmaelectronica.resources.firmaelectronica";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * Metodo que obtiene el valor como String de una propiedad especifica
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
		}
		return key;
	}

}
