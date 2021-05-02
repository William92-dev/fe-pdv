package com.gizlocorp.firmaelectronica.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.gizlocorp.firmaelectronica.exception.FileException;

import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;

/**
 * Class FileUtil. Utilitario permite leer un archivo y su contenido
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class FileUtil {

	public static final Logger log = Logger.getLogger(FileUtil.class);

	/**
	 * @param args
	 * @throws FileException
	 */
	public static void main(String[] args) throws FileException {
		log.info("File content: " + FileUtil.readContentFile("/opt/Con"));
	}

	/**
	 * @param filePath
	 * @return
	 * @throws FileException
	 */
	public static String readContentFile(String filePath) throws FileException {
		try {
			String content = org.apache.commons.io.FileUtils
					.readFileToString(new File(filePath));
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
			byte[] content = org.apache.commons.io.FileUtils
					.readFileToByteArray(file);
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
	public static byte[] archivoToByte(File file) throws IOException {

		byte[] buffer = new byte[(int) file.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if (ios.read(buffer) == -1) {
				throw new IOException(
						"EOF reached while trying to read the whole file");
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

	public static void saveDocumentToFile(Document document, String pathfile) {
		try {
			FileOutputStream fos = new FileOutputStream(pathfile);
			UtilidadTratarNodo.saveDocumentToOutputStream(document, fos, true);
		} catch (FileNotFoundException e) {
			log.error("Error al salvar el documento");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
