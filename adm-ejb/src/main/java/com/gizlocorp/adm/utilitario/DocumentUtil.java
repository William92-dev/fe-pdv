package com.gizlocorp.adm.utilitario;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 import org.alfresco.webservice.authentication.AuthenticationFault;
 import org.alfresco.webservice.content.Content;
 import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
 import org.alfresco.webservice.repository.UpdateResult;
 import org.alfresco.webservice.types.CML;
 import org.alfresco.webservice.types.CMLAddAspect;
 import org.alfresco.webservice.types.CMLCreate;
 import org.alfresco.webservice.types.ContentFormat;
 import org.alfresco.webservice.types.NamedValue;
 import org.alfresco.webservice.types.ParentReference;
 import org.alfresco.webservice.types.Reference;
 import org.alfresco.webservice.types.Store;
 import org.alfresco.webservice.util.AuthenticationUtils;
 import org.alfresco.webservice.util.Constants;
 import org.alfresco.webservice.util.ISO9075;
 import org.alfresco.webservice.util.Utils;
 import org.alfresco.webservice.util.WebServiceFactory;
 */
import org.apache.log4j.Logger;

//log4j.//logger;
import com.gizlocorp.adm.excepcion.FileException;

public class DocumentUtil {
	public static final Logger log = Logger.getLogger(DocumentUtil.class);

	// private static final Store STORE = new Store("workspace", "SpacesSt re");

	public static String createDocument(String xmlSource, String fechaEmision, String claveAcceso, String comprobante,
			String subcarpeta) throws IOException {
		StringBuilder pathFolder = new StringBuilder();
		
		
		log.info("XML--> "+ xmlSource );

		// pathFolder.append(dirServidor);
		// pathFolder.append("/gnvoice/recursos/comprobantes/");
		pathFolder.append(Constantes.DIR_DOCUMENTOS);
		pathFolder.append(comprobante);
		pathFolder.append("/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");

		if (fechaEmision.contains("/")) {
			fechaEmision = fechaEmision.replace("/", "");
		} else if (fechaEmision.contains("-")) {
			fechaEmision = fechaEmision.replace("-", "");
		}

		pathFolder.append(fechaEmision);
		pathFolder.append("/");

		log.debug("folder: " + pathFolder);
		createDirectory(pathFolder.toString());
		// createDirectoryAlfresco(comprobante, subcarpeta, fechaEmision);

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(claveAcceso);

		pathFile.append(".xml");
		stringToFile(xmlSource, pathFile.toString());

		// uploadComprobante(xmlSource.getBytes(), comprobante, subcarpeta,
		// fechaEmision, claveAcceso);

		return pathFile.toString();
	}

	public static String createDocumentDocClass(String xmlSource, String claveAcceso, String directorio)
			throws IOException {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(directorio);

		log.debug(new StringBuilder().append("folder: ").append(pathFolder).toString());
		// createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append("/");
		pathFile.append(claveAcceso);
		if ((!claveAcceso.contains(".xml")) && (!claveAcceso.contains(".XML"))) {
			pathFile.append(".xml");
		}
		stringToFile(xmlSource, pathFile.toString());

		return pathFile.toString();
	}

	public static String createDocumentPdfDocClass(byte[] pdf, String claveAcceso, String directorio) throws Exception {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(directorio);

		log.debug(new StringBuilder().append("folder: ").append(pathFolder).toString());
		// createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append("/");
		pathFile.append(claveAcceso);
		if ((!claveAcceso.contains(".pdf")) && (!claveAcceso.contains(".PDF"))) {
			pathFile.append(".pdf");
		}

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(pdf);
		fos.close();

		return pathFile.toString();
	}

	public static String createDocumentDocClass(byte[] xmlSource, String claveAcceso, String directorio)
			throws IOException {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(directorio);

		log.debug(new StringBuilder().append("folder: ").append(pathFolder).toString());
		// createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append("/");
		pathFile.append(claveAcceso);
		if ((!claveAcceso.contains(".xml")) && (!claveAcceso.contains(".XML"))) {
			pathFile.append(".xml");
		}
		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(xmlSource);
		fos.close();

		return pathFile.toString();
	}

	public static String createDocument(byte[] xml, String fechaEmision, String claveAcceso, String comprobante,
			String subcarpeta) throws Exception {
		StringBuilder pathFolder = new StringBuilder();		
		// pathFolder.append(dirServidor);
		// pathFolder.append("/gnvoice/recursos/comprobantes/");
		pathFolder.append(Constantes.DIR_DOCUMENTOS);
		pathFolder.append(comprobante);
		pathFolder.append("/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");

		if (fechaEmision.contains("/")) {
			fechaEmision = fechaEmision.replace("/", "");
		} else if (fechaEmision.contains("-")) {
			fechaEmision = fechaEmision.replace("-", "");
		}

		pathFolder.append(fechaEmision);
		pathFolder.append("/");

		log.debug("folder: " + pathFolder);
		createDirectory(pathFolder.toString());
		// createDirectoryAlfresco(comprobante, subcarpeta, fechaEmision);

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(claveAcceso);
		if (!(claveAcceso.contains(".xml") || claveAcceso.contains(".XML"))) {
			pathFile.append(".xml");
		}

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(xml);
		fos.close();

		// uploadComprobante(xml, comprobante, subcarpeta, fechaEmision,
		// claveAcceso);

		return pathFile.toString();
	}

	public static String createDocumentPdf(byte[] pdf, String fechaEmision, String claveAcceso, String comprobante,
			String subcarpeta, String dirServidor) throws Exception {
		StringBuilder pathFolder = new StringBuilder();

		// pathFolder.append(dirServidor);
		// pathFolder.append("/gnvoice/recursos/comprobantes/");
		pathFolder.append(Constantes.DIR_DOCUMENTOS);
		pathFolder.append(comprobante);
		pathFolder.append("/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");

		if (fechaEmision.contains("/")) {
			fechaEmision = fechaEmision.replace("/", "");
		} else if (fechaEmision.contains("-")) {
			fechaEmision = fechaEmision.replace("-", "");
		}

		pathFolder.append(fechaEmision);
		pathFolder.append("/");

		log.debug("folder: " + pathFolder);
		createDirectory(pathFolder.toString());
		// createDirectoryAlfresco(comprobante, subcarpeta, fechaEmision);

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(claveAcceso);

		if (!(claveAcceso.contains(".pdf") || claveAcceso.contains(".PDF"))) {
			pathFile.append(".pdf");
		}

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(pdf);
		fos.close();

		// uploadComprobante(xml, comprobante, subcarpeta, fechaEmision,
		// claveAcceso);

		return pathFile.toString();
	}

	public static String createDocumentZip(byte[] pdf, String fechaEmision, String claveAcceso, String comprobante,
			String subcarpeta, String dirServidor) throws Exception {
		StringBuilder pathFolder = new StringBuilder();

		// pathFolder.append(dirServidor);
		// pathFolder.append("C:\\peritos\\");
		pathFolder.append(Constantes.DIR_DOCUMENTOS);
		pathFolder.append(comprobante);
		pathFolder.append("/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");

		if (fechaEmision.contains("/")) {
			fechaEmision = fechaEmision.replace("/", "");
		} else if (fechaEmision.contains("-")) {
			fechaEmision = fechaEmision.replace("-", "");
		}

		pathFolder.append(fechaEmision);
		pathFolder.append("/");

		log.debug("folder: " + pathFolder);
		createDirectory(pathFolder.toString());
		// createDirectoryAlfresco(comprobante, subcarpeta, fechaEmision);

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(claveAcceso);
		if (!(claveAcceso.contains(".zip") || claveAcceso.contains(".ZIP"))) {
			pathFile.append(".zip");
		}

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(pdf);
		fos.close();

		// uploadComprobante(xml, comprobante, subcarpeta, fechaEmision,
		// claveAcceso);

		return pathFile.toString();
	}

	public static String createDocumentText(byte[] archivo, String nombreArchivo, String identificador,
			String subcarpeta, String dirServidor) throws IOException {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(dirServidor);
		pathFolder.append("/gnvoice/recursos/claves/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");
		pathFolder.append(identificador);
		pathFolder.append("/");

		createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(nombreArchivo);

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(archivo);
		fos.close();

		return pathFile.toString();
	}

	public static String createDocumentText(byte[] archivo, String nombreArchivo, String subcarpeta, String dirServidor)
			throws IOException {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(dirServidor);
		
		pathFolder.append("/");//para linux
		log.info("****revisando path"+pathFolder.toString());
		createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(nombreArchivo + ".out");

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(archivo);
		fos.close();

		return pathFile.toString();
	}

	public static String createDocumentImage(byte[] archivo, String nombreArchivo, String identificador,
			String subcarpeta, String dirServidor) throws IOException {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(dirServidor);
		pathFolder.append("/gnvoice/recursos/imagenes/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");
		pathFolder.append(identificador);
		pathFolder.append("/");

		createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(nombreArchivo);

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(archivo);
		fos.close();

		return pathFile.toString();
	}

	public static String createDocumentToken(byte[] archivo, String nombreArchivo, String identificador,
			String subcarpeta, String dirServidor) throws IOException {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(dirServidor);
		pathFolder.append("/gnvoice/recursos/firmaelectronica/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");
		pathFolder.append(identificador);
		pathFolder.append("/");

		createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(nombreArchivo);

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(archivo);
		fos.close();

		return pathFile.toString();
	}

	public static String createDocumentlogo(byte[] archivo, String nombreArchivo, String identificador,
			String subcarpeta, String dirServidor) throws IOException {
		StringBuilder pathFolder = new StringBuilder();

		pathFolder.append(dirServidor);
		pathFolder.append("/gnvoice/recursos/imagenes/");
		pathFolder.append(subcarpeta);
		pathFolder.append("/");
		pathFolder.append(identificador);
		pathFolder.append("/");

		createDirectory(pathFolder.toString());

		StringBuilder pathFile = new StringBuilder();
		pathFile.append(pathFolder.toString());
		pathFile.append(nombreArchivo);

		FileOutputStream fos = new FileOutputStream(pathFile.toString());
		fos.write(archivo);
		fos.close();

		return pathFile.toString();
	}

	/**
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static void createDirectory(String path) throws FileNotFoundException {
		StringBuffer pathname = new StringBuffer(path);

		if (!path.trim().endsWith("/")) {
			pathname.append("/");
		}
		// log.debug("Directorio URL: " + pathname.toString());
		File directory = new File(pathname.toString());
		// //log.debug(pathname.toString());
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new FileNotFoundException();
			}
		}
	}

	/**
	 * @param filePath
	 * @return
	 * @throws FileException
	 */
	public static String readContentFile(String filePath) throws FileException {
		try {
			File file = new File(filePath);
			String content = org.apache.commons.io.FileUtils.readFileToString(file);

			return content;

		} catch (IOException e) {
			throw new FileException(e);
		}
	}

	/**
	 * @param filePath
	 * @return
	 * @throws FileException
	 */
	public static byte[] readFile(String filePath) throws FileException {
		try {
			File file = new File(filePath);
			byte[] content = org.apache.commons.io.FileUtils.readFileToByteArray(file);
			return content;

		} catch (IOException e) {
			throw new FileException(e);
		}
	}

	/**
	 * Transforma un archivo (File) a bytes
	 * 
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] fileoByte(File file) throws IOException {

		byte[] buffer = new byte[(int) file.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if (ios.read(buffer) == -1) {
				throw new IOException("EOF reached while trying to read the whole file");
			}
		} finally {
			try {
				if (ios != null) {
					ios.close();
				}
			} catch (IOException e) {
				//
			}
		}

		return buffer;
	}

	/**
	 * Transforma un archivo (File) a bytes
	 * 
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStreamToByte(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[(int) 16384];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			int read = 0;
			while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, read);
			}
			baos.flush();
			return baos.toByteArray();

		} catch (IOException e) {
			//
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				//
			}
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				//
			}
		}

		return null;
	}

	private static void stringToFile(String xmlSource, String fileName) throws IOException {
		java.io.FileWriter fw = new java.io.FileWriter(fileName);
		fw.write(xmlSource);
		fw.close();
	}

	/*
	 * private static String buildAlfrescoPath(String[] rootFolder) {
	 * StringBuffer rf = new StringBuffer(); for (String folder : rootFolder) {
	 * rf.append("/"); rf.append("cm:" + ISO9075.encode(folder)); }
	 * 
	 * return rf.toString(); }
	 * 
	 * private static boolean createNestedFolders(String username, String
	 * password, String[] folders, String[] rootFolder) { boolean wasCreated =
	 * false; try { AuthenticationUtils.startSession(username, password);
	 * StringBuffer parent = new StringBuffer("/app:company_home" +
	 * buildAlfrescoPath(rootFolder)); for (String folder : folders) { try {
	 * ParentReference parentReference = new ParentReference( new
	 * Store("workspace", "SpacesStore"), null, parent.toString(),
	 * Constants.ASSOC_CONTAINS, Constants .createQNameString(
	 * "http://www.alfresco.org/model/content/1.0", folder));
	 * 
	 * NamedValue[] properties = { Utils.createNamedValue( Constants.PROP_NAME,
	 * folder) };
	 * 
	 * CMLCreate create = new CMLCreate("1", parentReference, null, null, null,
	 * Constants.TYPE_FOLDER, properties);
	 * 
	 * CML cml = new CML(); cml.setCreate(new CMLCreate[] { create }); //
	 * log.debug("Voy a crear carpeta " + folder + // " con padres "+ parent);
	 * 
	 * WebServiceFactory.getRepositoryService().update(cml); wasCreated = true;
	 * } catch (Exception e) { log.error(
	 * "Error en NESTED Folders--->EEEEEEEEEEEE---> " + e); }
	 * parent.append(buildAlfrescoPath(new String[] { folder })); // log.debug(
	 * "Parents " + parent); } } catch (Exception e) { log.error(
	 * "Error en NESTED Folders--->EEEEEEEEEEEE " + e + " Folders " + folders +
	 * "--->RootFolder " + rootFolder); } finally {
	 * AuthenticationUtils.endSession(); } return wasCreated; }
	 * 
	 * private static void createDirectoryAlfresco(String comprobante, String
	 * subcarpeta, String carpeta) { try { WebServiceFactory
	 * .setEndpointAddress("http://localhost:8081/alfresco/api/");
	 * 
	 * String[] folder = { carpeta }; String[] root = { "gnvoice", comprobante,
	 * subcarpeta }; createNestedFolders("admin", "RedHat1!", folder, root);
	 * 
	 * } catch (Exception e) { log.error("Error alfresco ", e); }
	 * 
	 * }
	 * 
	 * public static void main(String[] args) throws AuthenticationFault {
	 * WebServiceFactory
	 * .setEndpointAddress("http://localhost:8081/alfresco/api/");
	 * 
	 * String[] folder = { "12312" }; String[] root = { "gnvoice", "factura",
	 * "enviadas" }; createNestedFolders("admin", "admin", folder, root); }
	 * 
	 * public static String uploadComprobante(byte[] bytes, String comprobante,
	 * String subcarpeta, String carpeta, String claveAcceso) { try { String[]
	 * rootFolder = { "gnvoice", comprobante, subcarpeta, carpeta }; String cf =
	 * "/app:company_home" + buildAlfrescoPath(rootFolder); log.debug(
	 * "CF-----> " + cf); log.debug("Alfresco Start Session---> " + "admin" +
	 * "  --> " + "RedHat1!");
	 * 
	 * AuthenticationUtils.startSession("admin", "RedHat1!"); Store storeRef =
	 * new Store("workspace", "SpacesStore"); ParentReference companyHomeParent
	 * = new ParentReference(storeRef, null, cf, Constants.ASSOC_CONTAINS,
	 * null);
	 * 
	 * String name = "Comprobante Electronico" + claveAcceso;
	 * companyHomeParent.setChildName("cm:" + name);
	 * 
	 * NamedValue[] contentProps = new NamedValue[1]; contentProps[0] =
	 * Utils.createNamedValue(Constants.PROP_NAME, name); CMLCreate create = new
	 * CMLCreate("1", companyHomeParent, null, null, null,
	 * Constants.TYPE_CONTENT, contentProps);
	 * 
	 * NamedValue[] titledProps = new NamedValue[2]; titledProps[0] =
	 * Utils.createNamedValue(Constants.PROP_TITLE, name); titledProps[1] =
	 * Utils.createNamedValue(Constants.PROP_DESCRIPTION, name);
	 * 
	 * CMLAddAspect addAspect = new CMLAddAspect(Constants.ASPECT_TITLED,
	 * titledProps, null, "1");
	 * 
	 * CML cml = new CML(); cml.setCreate(new CMLCreate[] { create });
	 * cml.setAddAspect(new CMLAddAspect[] { addAspect });
	 * 
	 * UpdateResult[] result = WebServiceFactory.getRepositoryService()
	 * .update(cml);
	 * 
	 * Reference content = result[0].getDestination();
	 * ContentServiceSoapBindingStub contentService = WebServiceFactory
	 * .getContentService();
	 * 
	 * ContentFormat contentFormat = new ContentFormat("text/xml", "UTF-8");
	 * 
	 * Content contentRef = contentService.write(content,
	 * Constants.PROP_CONTENT, bytes, contentFormat);
	 * 
	 * log.debug("Nodo del archivo alfresco--->" +
	 * contentRef.getNode().getUuid());
	 * 
	 * log.debug("Path Nodo del archivo alfresco--->" +
	 * contentRef.getNode().getPath());
	 * 
	 * log.debug("Url del archivo alfresco--->" + contentRef.getUrl());
	 * 
	 * return contentRef.getNode().getUuid(); } catch (Exception e) { log.error(
	 * "Error al subir archivo en alfresco---> " + e); } finally {
	 * AuthenticationUtils.endSession(); } return null; }
	 */
}
