/**
 * 
 */
package com.gizlocorp.gnvoice.utilitario;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Acceso al archivo viaticos.properties
 * 
 * @author
 * @version $Revision: 1.0 $
 */
public class PropertiesUtil {

	//private static final String URL_PROPIEDADES = "../standalone/configuration/viaticos.properties";
	private static final String URL_PROPIEDADES = "/gnvoice/recursos/gnvoice.properties";
	private static final String NOMBRE_HOST = "servicio.rest.host";
	private static final String PUERTO = "servicio.rest.puerto";
	private static final String CORREO_REMITE = "correo.remite";

	private static Logger log = Logger
			.getLogger(PropertiesUtil.class.getName());
	private static PropertiesUtil instance;
	private Properties propiedades;

	private PropertiesUtil(String dirServidor) {
		propiedades = new Properties();
		try {
			propiedades.load(new FileInputStream(dirServidor+URL_PROPIEDADES));
		} catch (IOException e) {
			log.severe("Error al leer el archivo " + URL_PROPIEDADES);
			throw new RuntimeException("Error al leer el archivo "
					+ URL_PROPIEDADES);
		}
	}

	public static PropertiesUtil getInstance(String dirServidor) {
		if (instance == null) {
			instance = new PropertiesUtil(dirServidor);
		}
		return instance;
	}

	public String getPropiedad(String clave) {
		return propiedades.getProperty(clave);
	}

	public String getNombreHost() {
		return propiedades.getProperty(NOMBRE_HOST);
	}

	public String getPuerto() {
		return propiedades.getProperty(PUERTO);
	}

	public String getCorreoRemite() {
		return propiedades.getProperty(CORREO_REMITE);
	}

}
