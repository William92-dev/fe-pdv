package com.gizlocorp.firmaelectronica.util;

/**
 * 
 * @author Gabriel Eguiguren
 */
public class Mensajes {

	public static final String NO_PERMITIDOS_EN_VALORES = "`~!@#$%^&*()_+=\\|\"':;?/><, -";
	public static final String NO_PERMITIDOS_EN_NUMEROS = "`~!@#$%^&*()_+=\\|\"':;?/>.<, -";

	public static final String SOLO_VALORES = "Solo se permiten valores num\u00E9ricos";
	public static final String SOLO_NUMEROS = "Solo se permiten n\u00FAmeros";
	public static final String ERROR_VALOR = "Valor no permitido";
	public static final String SOLO_UN_SEPARADOR = "La cantidad ingresada no puede tener mas de un (.) decimal";
	public static final String LONGITUD_MAXIMA = "La longitud m\u00E1xima del campo ha sido alcanzada: ";
	public static final String VALOR_EXEDE_CONSUMIDOR_FINAL = "No se pueden emitir facturas mayores a USD 200.00 donde el comprador conste como: CONSUMIDOR FINAL";
	public static final String VALOR_EXEDE_PROPINA = "El valor ingresado en el campo Propina exede o es menor al 10% del Subtotal ";
	public static final String ITEM_YA_EXISTE = "El \u00EDtem seleccionado ya se encuentra en el detalle de la Factura, favor modifique su cantidad";
	public static final String TAM_GUIA_REMISION = "Gu\u00EDa de Remisi\u00F3n debe ser de 15 n\u00FAmeros: ej: 123-123-123456789";
	public static final String CAMPOS_OBLIGATORIOS = "Ingrese los campos obligatorios del comprobante";
	public static final String LIMITE_5 = "So puede ingresar hasta cinco detalles adicionales";

	public static final String NOTADEDEBITO_VACIA = "Ingrese los valores correspondientes a la Nota de D\u00E9bito";
	public static final String COMPROBANTE_VACIO = "\nAl menos debe a\u00F1adir un item al comprobante y todas las columnas de ICE deben estar llenas";
	public static final String GUIA_VACIO = "Al menos debe a\u00F1adir un DESTINATARIO en la Guía de Remisión";
	public static final String INGRESE_NO_COMPROBANTE = "\nIngrese el n\u00FAmero de comprobante a modificar";
	public static final String INGRESE_NO_AUTORIZACION = "Ingrese el n\u00FAmero de autorizaci\u00F3n del comprobante a modificar";
	public static final String INGRESE_FECHA = "\nIngrese la fecha de emisi\u00F3n del comprobante a modificar";
	public static final String INGRESE_ID = "Ingrese el RUC /CI o pasaporte o la Razón Social";
	public static final String FECHA_MAYOR = "No se permiten fechas mayores a la del d\u00EDa de hoy";
	public static final String FECHA_MENOR = "No se permiten fechas menores a la del d\u00EDa de hoy";
	public static final String FECHA_ERROR = "La fecha del Comprobante de Retención no puede ser menor a la fecha de emisión del Documento de Sustento";

	public static final String CLAVE_ACCESO = "\nLa clave de Acceso no puede ser nula";

	public static final String ERROR_IMPUESTO_PROD = "El producto seleccionado, no tiene asignado ningun impuesto,\nfavor edite el producto e ingrese los impuestos";

	public static final String INGRESE_MOTIVO = "\nIngrese el motivo de modificaci\u00F3n  del comprobante";
	public static final String CALCULAR_TOTAL = "\nNo se ha calculado el Valor Total del comprobante";
	public static final String CODIGO_ICE_NO_EXISTE = "El c\u00F3digo de ICE ingresado no se encuentra registrado";
	public static final String INGRESE_CODIGO_ICE = "Ingrese el c\u00F3digo ICE correspondiente al impuesto";

	// Comprobante de Retencion
	public static final String PERIODO_FISCAL = "\nEl mes y el año del período fiscal son obligatorios";
	public static final String ANIO_PERIODO_FISCAL = "\nEl año del período fiscal no puede ser mayor actual";
	public static final String SUJETO_RETENCION = "\nLos datos de Sujeto Retenido son obligatorios";
	public static final String PERIODO_FISCAL_MAYOR = "\nEl período fiscal no puede ser mayor a la fecha actual";
	public static final String COD_DOC_TAMANIO = "\n El Nro. del Comprobante debe de ser de 15 dígitos";

	// Guia de Remision
	public static final String INGRESE_DIREC_PARTIDA = "\nIngrese la dirección de partida del transportista";
	public static final String INGRESE_FECHA_FIN = "\nIngrese la fecha de fin del transporte";
	public static final String INGRESE_FECHA_INICIO = "\nIngrese la fecha de inicio del transporte";
	public static final String SELECCION_TRANS = "\nSeleccione un Transportista";
	public static final String INGRESE_COD_DOC = "\nIngrese el Código del Documento al que se refiere la opción OTROS";
	public static final String NUM_AUT_COD_DOC = "\n El Nro. de Autorización del comprobante debe de ser de 10 o 37 dígitos";
	public static final String FEC_EMI_COD_DOC = "\n Ingrese la Fecha de Emisión del Documento de Sustento";
	public static final String INGRESE_DIR_DEST = "\n Ingrese la Dirección de Destino";
	public static final String INGRESE_MOT_TRAS = "\n Ingrese el Motivo de Traslado";
	public static final String INGRESE_TAM_NUM_DOC = "\n El Nro. de comprobante debe de ser de 15 caracteres";
	public static final String NJRO_COMPROBANTE_OBLIGATORIO = "\nEl número de comprobante es obligatorio y debe ser de 15 caracteres";
	public static final String CONDIC_ADD_DESTINT = "Debe seleccionar un Transportista, un Destinatario y debe añadir al menos un Producto al detalle ";
	public static final String FECHA_MAYOR_INI = "La fecha de fin de transporte  no puede ser anterior a la fecha de inicio";

	public static final String NO_ENCONTRADO = "No existen registros almacenados para el dato buscado";

	public static final String NO_EXISTE_CERTIFICADO = "No se encontr\u00F3 un certificado v\u00E1lido o autorizado para firma electr\u00F3nica";
	public static final String CERTIFICADO_NO_VALIDO = "El certificado no es v\u00E1lido";
	public static final String CERTIFICADO_EXPIRADO = "El certificado se encuentra expirado, favor contacte al emisor";
	public static final String SISTEMA_OPERATIVO_NO_COMPATIBLE = "El aplicativo actualmente solo soporta Sistemas Operativos basados en Windows";

	public static final String ERROR_ENVIO_COMPROBANTE = "Error al momento de enviar el comprobante";
	public static final String ERROR_TAMANIO_LOTE = "El tama\u00F1o del lote exede el l\u00EDmite permitido (500 Kbytes)";
	public static final String ERROR_ENVIO_LOTE = "Se ha producido un error al enviar el lote";
	public static final String SELECCIONE_ARCHIVOS = "Seleccione al menos un archivo";
	public static final String ARCHIVO_GUARDADO = "El comprobante fue guardado exit\u00F3samente";
	public static final String ARCHIVO_FIRMADO = "El comprobante fue firmado exit\u00F3samente";
	public static final String ARCHIVO_GUARDADO_FIRMADO = "El comprobante fue guardado, firmado y enviado exit\u00F3samente, pero no fue Autorizado";
	public static final String ARCHIVO_AUTORIZADO = "El comprobante fue autorizado por el SRI";
	public static final String ERROR = "Se ha producido un error ";
	public static final String ERROR_FIRMAR = "Error al tratar de firmar digitalmente el archivo";
	public static final String ERROR_CREAR = "Error al tratar de crear el archivo correspondiente al comprobante";
	public static final String ERROR_ENVIAR = "Error al tratar de enviar el comprobante hacia el SRI";
	public static final String ERROR_AUTORIZAR = "Error al tratar de autorizar el comprobante por el SRI";
	public static final String ERROR_MOVER_RECHAZ = "Error al mover archivo a carpeta rechazados";
	public static final String ERROR_TRANSMITIDO = "Ha ocurrido un error en el proceso de la Autorización, por lo que se traslado el archivo a la carpeta de: transmitidosSinRespuesta";
	public static final String ERROR_MOVER_TRANSM = "\nError al mover archivo a carpeta de Transmitidos sin Respuesta";

	public static final String X509_SO = "Sistema operativo o JRE no compatible los los tokens de firma";
	public static final String X509_FIRMA_INVALIDA = "Se ha producido un error al momento de crear \nla firma del comprobante electr\u00F3nico, ya que el la firma digital no es v\u00E1lida";
	public static final String X509_CERT_NO_RUC = "El certificado digital proporcionado no posee los datos de RUC OID: 1.3.6.1.4.1.37XXX.3.11,\nraz\u00F3n por la cual usted no podr\u00E1 firmar digitalmente documentos para remitir al SRI,\nfavor actualize su certificado digital con la Autoridad Certificadora";
	public static final String X509_RUC_NO_COINCIDE_CERT = "El Ruc presente en el certificado digital, no coincide con el Ruc registrado en el aplicativo";
	public static final String X509_CERT_NO_PRESENTE = "No se pudo encontrar un certificado v\u00E1lido para firmar el archivo";
	public static final String X509_CERT_EXPIRADO = "El certificado con el que intenta firmar el comprobante esta expirado\nfavor actualize su certificado digital con la Autoridad Certificadora";
	public static final String X509_XML_INVALIDO = "Archivo XML a firmar mal definido o estructurado";
	public static final String X509_CLAVE_PRIVADA = "No se pudo acceder a la clave privada del certificado";
	public static final String CLAVES_CONTIGENCIA = "No existen claves de contingencia, por favor cargue claves en el Sistema o cambie su estado de Emisión a: NORMAL";
	public static final String INDISPONIBILIDAD_OSCP = "INTENTE NUEVAMENTE EN MINUTOS";

	public static final String CONSUMIDOR_FINAL = "CONSUMIDOR FINAL";
	public static final String SELECT_OPTION = "escoja...";
	public static final String ELIMINAR = "Eliminar";
	public static final String OPCION_NO = "NO";
	public static final String OPCION_SI = "SI";
	public static final String MONEDA = "DOLAR";
}
