package com.gizlocorp.firmaelectronica;

/**
 * 
 * @author gizlocorp
 */
public enum AutoridadesCertificantes {

	ANF("ANF EC 1", "ANF Autoridad Intermedia", "ANF AC", "EC",
			"1.3.6.1.4.1.37442"), 
			 BANCO_CENTRAL("AC BANCO CENTRAL DEL ECUADOR",
			"ENTIDAD DE CERTIFICACION DE INFORMACION-ECIBCE",
			"BANCO CENTRAL DEL ECUADOR", 
			"EC", 
			"1.3.6.1.4.1.37947"),
			 SECURITY_DATA(
			"AUTORIDAD DE CERTIFICACION SUBCA-1 SECURITY DATA",
			"ENTIDAD DE CERTIFICACION DE INFORMACION", 
			"SECURITY DATA S.A. 1",
			"EC", 
			"1.3.6.1.4.1.37746");

	private final String cn;
	private final String ou;
	private final String o;
	private final String c;
	private final String oid;

	AutoridadesCertificantes(String cn, String ou, String o, String c,
			String oid) {
		this.c = c;
		this.o = o;
		this.cn = cn;
		this.ou = ou;
		this.oid = oid;
	}

	public String getC() {
		return c;
	}

	public String getCn() {
		return cn;
	}

	public String getO() {
		return o;
	}

	public String getOu() {
		return ou;
	}

	public String getOid() {
		return oid;
	}

}
