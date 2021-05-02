/**
 * 
 */
package com.gizlocorp.adm.utilitario;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author
 * @version $Revision: 1.0 $
 */
public class Constantes {

	public static final String USUARIO_INTERNO = "INT";
	public static final String USUARIO_EXTERNO = "EXT";

	public static final String RECURSO_APLICACION = "APP";
	public static final String RECURSO_MODULO = "MOD";

	//public static final String DIR_DOCUMENTOS = "/home/jboss/app/gnvoice/recursos/comprobantes";//para pruebas
	public static final String DIR_DOCUMENTOS = "/factelectro/documentos2015/"; //produccion
	
	public static final String DIR_RECURSOS = "/data"; //produccion
  // public static final String DIR_RECURSOS = "/home/jboss/app";//pruebas
    
    public static final String BASE_INTEGRA = "PROD";//PRODUCCION
    public static final String USUARIO_INTEGRA = "integraciones";//PRODUCCION
    public static final String CLAVE_INTEGRA= "1xnt3xgr4xBusD4xt0xs";//PRODUCCION
    public static final String ESQUEMA_JDE= "proddta";
    
    /*public static final String BASE_INTEGRA = "prod_desa";//PRUEBAS
    public static final String USUARIO_INTEGRA = "integraciones";//PRUEBAS
    public static final String CLAVE_INTEGRA= "int3gr4.2015";//PRUEBAS
    public static final String ESQUEMA_JDE= "crpdta"; // PRUEBAS
     */
     
     public static final String BASE_JDE = "jdeprd";//PRODUCCION
     public static final String USUARIO_JDE = "integra";//PRODUCCION
     public static final String CLAVE_JDE= "Gpf2015";//PRODUCCION
     
/*     public static final String BASE_JDE = "jde_desa";//PRUEBAS
     public static final String USUARIO_JDE = "integra";//PRUEBAS
     public static final String CLAVE_JDE= "Gpf2015";//PRUEBAS
  */  
       
    

	public static final String LDAP_PROVIDER = "ldap.provider";
	public static final String LDAP_SECURITY_AUTHENTICATION = "ldap.security.authentication";
	public static final String LDAP_PRINCIPAL_DOMAIN = "ldap.principal.domain";
	public static final String LDAP_USER = "ldap.user";
	public static final String LDAP_PASSWORD = "ldap.password";
	public static final String CARACTER_ESPECIAL = ",";
	public static final String LDAP_BASE_GENERAL = "OU=PROVINCIAS,DC=fj,DC=local";
	public static final String LDAP_BASE_OU = "OU=";
	public static final String LDAP_BASE_USUARIOS = "OU=Internos,OU=Usuarios";
	public static final String LDAP_EXCEDE_RESULTADOS = "Los resultados de la busqueda exceden el limite, por favor agregar otro parametro de busqueda";
	public static final String LDAP_NO_RESULTADOS = "No se encontraron resultados, verifique los parametros de busqueda";
	public static final String LDAP_PARAMETRO_ORDENAR = "cn";
	public static final String LDAP_PAIS_PRINCIPAL = "ECUADOR";

	public static final String ENCRYPT_PASSWORD = "encrypt.password";
	public static final String ENCRYPT_TOLERANCE = "encrypt.token.tolerance";

	private static final String[] ENABLED_USER_ACCOUNT = { "512", "544",
			"66048", "66080", "262656", "262688", "328192", "328224" };

	public static final int LDAP_PAGES = 20;

	public static final String CLIENT_USER = "adm-remote.user";
	public static final String CLIENT_PASSWORD = "adm-remote.password";
	public static final String CLIENT_URL_PROVIDER = "adm.remote.provider";
	public static final String CLIENT_MODULO = "adm.modulo";

	public static final String GENERO = "genero";
	public static final String CODIGO_GENERO = "C23";

	public static final String ESTADO_CIVIL = "Estado Civil";
	public static final String CODIGO_ESTADO_CIVIL = "C24";

	public static final String TIPO_IDENTIFICACION = "Tipo Identificacion";
	public static final String CODIGO_TIPO_IDENTIFICACION = "C25";

	public static final String CODIGO_EVENTO = "EVENT";
	
	public static final String 	RUTA_INTEGRACION_BODEGA_XMLFactura = "/doc_windows/facturaElectronica/";
	public static final String 	RUTA_INTEGRACION_BODEGA_PDFFactura = "/doc_windows/facturaElectronica/pdf/";
	public static final String 	RUTA_INTEGRACION_BODEGA_XMLNotaCredito = "/doc_windows/notacredito/";
	public static final String 	RUTA_INTEGRACION_BODEGA_PDFNotaCredito = "/doc_windows/notacredito/pdf/";
	
	public static final String DIR_DOC_CLASS_SER_UIO = "/Servicios_Quito";
	public static final String DIR_DOC_CLASS_SER_GUA = "/Servicios_Guayaquil";

	public static final String AMBIENTE = "AMBIENTE";
	
	public static List<String> getEnabledUserAccount() {
		return Collections
				.unmodifiableList(Arrays.asList(ENABLED_USER_ACCOUNT));
	}

}
