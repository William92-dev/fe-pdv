package com.gizlocorp.gnvoice.xml;

import java.util.ArrayList;
import java.util.List;

public class LoteXml {
	private String version;
	private String claveAcceso;
	private String ruc;
	private List<ComprobanteXml> comprobantes;

	public LoteXml() {
		/* 24 */this.comprobantes = new ArrayList();
	}

	public String getClaveAcceso() {
		/* 28 */return this.claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		/* 32 */this.claveAcceso = claveAcceso;
	}

	public List<ComprobanteXml> getComprobantes() {
		/* 36 */return this.comprobantes;
	}

	public void setComprobantes(List<ComprobanteXml> comprobantes) {
		/* 40 */this.comprobantes = comprobantes;
	}

	public String getRuc() {
		/* 44 */return this.ruc;
	}

	public void setRuc(String ruc) {
		/* 48 */this.ruc = ruc;
	}

	public String getVersion() {
		/* 52 */return this.version;
	}

	public void setVersion(String version) {
		/* 56 */this.version = version;
	}
}

// LoteXml

