/**
 * 
 */
package com.gizlocorp.gnvoice.utilitario;

import java.math.BigDecimal;

public class Constantes {

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String EMPTY = "EMPTY";

	public static final String GENERO = "GENERO";
	public static final String ESTADO_CIVIL = "ESTADO CIVIL";

	public static final String ACTIVADO = "ACT";

	public static final String TIPO_REQUERIMIENTO = "TIPO REQUERIMIENTO";

	public static final String REP_FACTURA = "/gnvoice/recursos/reportes/factura.jasper";
	public static final String REP_FACTURA_OFFLINE = "/gnvoice/recursos/reportes/facturaOffline.jasper";
	public static final String REP_COMP_RETENCION = "/gnvoice/recursos/reportes/comprobanteRetencion.jasper";
	public static final String REP_NOTA_DEBITO = "/gnvoice/recursos/reportes/notaDebitoFinal.jasper";
	public static final String REP_NOTA_CREDITO = "/gnvoice/recursos/reportes/notaCreditoFinal.jasper";
	public static final String REP_GUIA_REMISION = "/gnvoice/recursos/reportes/guiaRemisionFinal.jasper";
	public static final String REP_FACTURA_14 = "/gnvoice/recursos/reportes/factura14.jasper";
	public static final String REP_NOTA_DEBITO_14 = "/gnvoice/recursos/reportes/notaDebitoFinal14.jasper";
	public static final String REP_NOTA_CREDITO_14 = "/gnvoice/recursos/reportes/notaCreditoFinal14.jasper";
	public static final String SUBREPORT_DIR = "/gnvoice/recursos/reportes/";

	public static final String IMAGEN_PRUEBAS = "/gnvoice/recursos/reportes/pruebas.jpeg";
	public static final String IMAGEN_PRODUCCION = "/gnvoice/recursos/reportes/produccion.jpeg";
	public static final String IMAGEN_RECHAZADO = "/gnvoice/recursos/reportes/rechazado.jpeg";
	public static final String LOGO = "/gnvoice/recursos/reportes/logo.jpeg";

	public static final String DIR_SUCURSAL = "DIR_SUCURSAL";
	public static final String CONT_ESPECIAL = "CONT_ESPECIAL";
	public static final String LLEVA_CONTABILIDAD = "LLEVA_CONTABILIDAD";
	public static final String RS_COMPRADOR = "RS_COMPRADOR";
	public static final String RUC_COMPRADOR = "RUC_COMPRADOR";
	public static final String FECHA_EMISION = "FECHA_EMISION";

	public static final String CORREO_REMITE = "CORREO_REMITE";
	public static final String CORREO_REMITE_EL_OFERTON = "CORREO_REMITE_OFER";

	public static final String REINTENTO_SRI = "REINTENTO_SRI";
	public static final String INTERVARLO_REINTENTO_SRI = "INT_REINTENTO_SRI";

	public static final String SERVICIO_AUT_SRI_PRUEBA = "SER_AUT_SRI_PRUEBA";
	public static final String SERVICIO_AUT_SRI_PRODUCCION = "SER_AUT_SRI_PROD";
	public static final String SERVICIO_REC_SRI_PRUEBA = "SER_REC_SRI_PRUEBA";
	public static final String SERVICIO_REC_SRI_PRODUCCION = "SER_REC_SRI_PROD";

	public static final String SER_AUT_SRI_PRUE_LOT = "SER_AUT_SRI_PRUE_LOT";
	public static final String SER_AUT_SRI_PROD_LOT = "SER_AUT_SRI_PROD_LOT";
	public static final String SER_REC_SRI_PRUE_LOT = "SER_REC_SRI_PRUE_LOT";
	public static final String SER_REC_SRI_PROD_LOT = "SER_REC_SRI_PROD_LOT";

	public static final String SMTP_HOST = "SMTP_HOST";
	public static final String SMTP_PORT = "SMTP_PORT";
	public static final String SMTP_USERNAME = "SMTP_USERNAME";
	public static final String SMTP_PASSWORD = "SMTP_PASSWORD";
	
	
	

	public static final String POP_HOST = "POP_HOST";
	public static final String POP_PORT = "POP_PORT";
	public static final String POP_USERNAME = "POP_USERNAME";
	public static final String POP_PASSWORD = "POP_PASSWORD";

	public static final String NOTIFIACION = "NOTIFICACION";
	public static final String CODIGO_NUMERICO = "56580323";
	public static final String CODIGO_NUMERICO_LOTE = "12345678901234567890";

	public static final String AMBIENTE = "AMBIENTE";
	public static final String MONEDA = "MONEDA";
	public static final String IVASRI = "IVASRI";

	public static final String COD_FACTURA = "COD_FACTURA";
	public static final String COD_GUIA = "COD_GUIA";
	public static final String COD_NOTA_CREDITO = "COD_NOTA_CREDITO";
	public static final String COD_NOTA_DEBITO = "COD_NOTA_DEBITO";
	public static final String COD_RETENCION = "COD_RETENCION";

	public static final String RECEPCION_WS = "recepcion";
	public static final String AUTORIZACION_WS = "autorizacion";

	public static final String VERSION = "1.0.0";
	public static final String VERSION_1 = "1.1.0";

	public static final String EVENTO_REPROCESO_FACTURA = "REPFA";
	public static final String EVENTO_REPROCESO_GUIA = "REPGR";
	public static final String EVENTO_REPROCESO_NOTA_CREDITO = "REPNC";
	public static final String EVENTO_REPROCESO_NOTA_DEBITO = "REPND";
	public static final String EVENTO_REPROCESO_RETENCION = "REPCR";

	public static final String EVENTO_PROCESO_LOTE_FACTURA = "PLOFA";
	public static final String EVENTO_PROCESO_LOTE_GUIA = "PLOGR";
	public static final String EVENTO_PROCESO_LOTE_NOTA_CREDITO = "PLONC";
	public static final String EVENTO_PROCESO_LOTE_NOTA_DEBITO = "PLOND";
	public static final String EVENTO_PROCESO_LOTE_RETENCION = "PLOCR";
	public static final String EVENTO_PROCESO_FTP = "FTP";

	public static final String EVENTO_NOTIFI_CLAVE = "NTCLV";
	public static final String EVENTO_VALIDA_USO_CLAVE = "VLDCL";
	public static final String EVENTO_INGRESO_SISTEMA = "LOGIN";
	public static final String EVENTO_ACCESO_MODULO = "MODUL";
	public static final String EVENTO_ERROR = "ERROR";
	public static final String EVENTO_ALERTA = "WARNI";
	public static final String EVENTO_RECEPCION_COMPROBANTE = "RECOM";

	public static final String FRECUENCIA_REPRO = "FRECUENCIA_REPRO";
	public static final String FRECUENCIA_LOTE = "FRECUENCIA_LOTE";
	public static final String FRECUENCIA_CLAVE = "FRECUENCIA_CLAVE";
	public static final String FRECUENCIA_RECEP = "FRECUENCIA_RECEP";
	public static final String FRECUENCIA_FTP = "FRECUENCIA_FTP";

	public static final String NOTIFI_CLAVE = "NTCLV";
	public static final String VALIDA_USO_CLAVE = "VLDCL";
	public static final String NOTIFICACION_CLAVE = "NOTIFICACION_CLAVE";
	public static final String CORREO_ADMIN = "CORREO_ADMIN";

	public static final Integer MAXIMO_COMPROBANTES_LOTE = new Integer(45);

	public static final String RESPUESTA_OK = "DOCUMENTOS PROGRAMADOS PARA PROCESAMIENTO";

	public static final String DIRECTORIO_SERVIDOR = "DIRECTORIO_SERVIDOR";
	public static final String URL_SERVIDOR = "URL_SERVIDOR";
	public static final String URL_CONSULTA_USUARIO = "URL_CONSULTA_USUARIO";
	public static final String TIPO_EJECUCION = "TIPO_EJECUCION";
	public static final String NUMERO_COMPROBANTES_LOTE = "NUM_COMPROB_LOTE";
	public static final String URL_ALFRESCO = "URL_ALFRESCO";
	public static final String USUARIO_ALFRESCO = "USUARIO_ALFRESCO";
	public static final String CLAVE_ALFRESCO = "CLAVE_ALFRESCO";

	public static final String DIR_RIM_ESQUEMA = "DIR_RIM_ESQUEMA";
	public static final String SER_RIM_WS = "SER_RIM_WS";
	public static final String SER_RIM_URI = "SER_RIM_URI";
	public static final String RIM_REPOSITO_ARCHIVO = "RIM_REPOSITO_ARCHIVO";
	public static final String RIM_REPOSITO_REMOTO = "RIM_REPOSITO_REMOTO";
	public static final String RIM_REPOSITO_FTP = "RIM_REPOSITO_FTP";
	public static final String RIM_REPO_FTP_USER = "RIM_REPO_FTP_USER";
	public static final String RIM_REPO_FTP_PASS = "RIM_REPO_FTP_USER";
	public static final String DOC_CLASS_RUTA = "DOC_CLASS_RUTA";
	public static final String MAIL = "mail";
	public static final String MANUAL = "manual";
	public static final String CONTINGENCIA = "CONTINGENCIA";

	public static final String EXECUTE_FTP = "EXECUTE_FTP";
	
	public static final String EXECUTE_FACTURA = "EXECUTE_FACTURA";
	public static final String EXECUTE_NOTADEB = "EXECUTE_NOTADEB";
	public static final String EXECUTE_NOTACRE = "EXECUTE_NOTACRE";
	public static final String EXECUTE_RETENC = "EXECUTE_RENTENC";
	public static final String EXECUTE_GUIAREM = "EXECUTE_GUIAREM";

	public static final String PENDIENTE = "PEN";
	public static final String ETL_FACTURA = "ETLFA";
	public static final String ETL_GUIA = "ETLGR";
	public static final String ETL_NOTA_CREDITO = "ETLNC";
	public static final String ETL_NOTA_DEBITO = "ETLND";
	public static final String ETL_RETENCION = "ETLCR";

	public static final String FRECUENCIA_ETL = "FRECUENCIA_ETL";
	
	public static final String ETL_FTP_REIM = "ETLFTPREIM";

	/************************* eventos ***********************************/
	public static final String REPROCESO_FACTURA = "REPFA";

	public static final BigDecimal divisor = new BigDecimal(100);

	public static final String INSERT_FA_DATOS_SRI_ELECTRONICA = "INSERT INTO farmacias.FA_DATOS_SRI_ELECTRONICA (NUMERO_INTERNO,FARMACIA,DOCUMENTO,TIPO_COMPROBANTE,FECHA,USUARIO,FIRMAE,AUT_FIRMAE,FECHA_FIRMAE,COMPROBANTE_FIRMAE,CLAVE_ACCESO,OBSERVACION_ELEC,CORREO_ELECTRONICO,ESTADO,TIPO_PROCESO) VALUES(farmacias.FA_DATOS_SRI_ELEC_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String UPDATE_FA_DATOS_SRI_ELECTRONICA = "UPDATE farmacias.FA_DATOS_SRI_ELECTRONICA SET FARMACIA = ?, DOCUMENTO = ?, TIPO_COMPROBANTE = ?, FECHA = ?, USUARIO = ?, FIRMAE = ?, AUT_FIRMAE = ?, FECHA_FIRMAE = ?, COMPROBANTE_FIRMAE = ?, CLAVE_ACCESO = ?, OBSERVACION_ELEC = ?, CORREO_ELECTRONICO = ?, ESTADO = ?, TIPO_PROCESO = ? WHERE FARMACIA = ? AND DOCUMENTO = ? AND TIPO_COMPROBANTE= ?";

	public static final String DETALLES = "detalles";
	public static final String TOTAL_IMPUESTO = "totalesImpuestos";
	public static final String TOTAL_SIN_IMPUESTO = "totalesSinImpuestos";
	public static final String DIR_DOCUMENTOS = "/factelectro/documentos2015/";

}
