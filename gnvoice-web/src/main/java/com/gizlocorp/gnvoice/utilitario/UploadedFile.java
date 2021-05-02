package com.gizlocorp.gnvoice.utilitario;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.utilitario.DocumentUtil;

public class UploadedFile {
	private static Logger log = Logger.getLogger(UploadedFile.class.getName());

	private String name;
	private String file;
	private String id;
	private String folder;
	private String mime;
	private long length;
	private byte[] data;

	public static String uploadFile(UploadedFile archivo, String dirServidor) throws IOException {
		// PropertiesUtil util = PropertiesUtil.getInstance();
		// log.debug(util.getAlfrescoEndPoint());

		byte[] bFile = archivo.getData();

		return DocumentUtil.createDocumentImage(bFile, archivo.getFile(),
				archivo.getId(), archivo.getFolder(), dirServidor);
		// implementacion con alfresco
		/*
		 * AlfrescoManager am = new AlfrescoManager(util.getAlfrescoEndPoint());
		 * String[] folder = { "" };
		 * 
		 * if (util.getAlfrescoFolder() != null) { folder =
		 * util.getAlfrescoFolder().split(","); }
		 * 
		 * log.debug("Cargar Archivo: " + archivo.getName()); return
		 * am.uploadFile(util.getAlfrescoUser(), util.getAlfrescoPassword(),
		 * bFile, archivo.getName(), archivo.getName(), archivo.getMime(),
		 * folder);
		 */
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		int extDot = name.lastIndexOf('.');
		if (extDot > 0) {
			String extension = name.substring(extDot + 1);
			if ("bmp".equals(extension)) {
				mime = "image/bmp";
			} else if ("jpg".equals(extension)) {
				mime = "image/jpeg";
			} else if ("gif".equals(extension)) {
				mime = "image/gif";
			} else if ("png".equals(extension)) {
				mime = "image/png";
			} else if ("pdf".equals(extension)) {
				mime = "application/pdf";

			} else {
				mime = "image/unknown";
			}
		}
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getMime() {
		return mime;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		UploadedFile.log = log;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
}
