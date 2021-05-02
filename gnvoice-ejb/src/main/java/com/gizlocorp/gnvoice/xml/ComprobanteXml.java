package com.gizlocorp.gnvoice.xml;

import java.io.Serializable;

public class ComprobanteXml  implements Serializable {
	private String tipo;
	private String version;
	private String fileXML;

	public String getFileXML() {
		/* 20 */return this.fileXML;
	}

	public void setFileXML(String fileXML) {
		/* 24 */this.fileXML = ("<![CDATA[" + fileXML + "]]>");
	}

	public String getTipo() {
		/* 29 */return this.tipo;
	}

	public void setTipo(String tipo) {
		/* 33 */this.tipo = tipo;
	}

	public String getVersion() {
		/* 37 */return this.version;
	}

	public void setVersion(String version) {
		/* 41 */this.version = version;
	}
}

// ComprobanteXml

