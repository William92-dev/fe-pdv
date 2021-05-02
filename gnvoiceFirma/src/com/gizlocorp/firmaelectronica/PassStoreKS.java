package com.gizlocorp.firmaelectronica;

import es.mityc.javasign.pkstore.IPassStoreKS;
import java.security.cert.X509Certificate;

public class PassStoreKS implements IPassStoreKS {
	private transient String password;

	public PassStoreKS(String pass) {
		this.password = new String(pass);
	}

	public char[] getPassword(X509Certificate certificate, String alias) {
		return this.password.toCharArray();
	}
}
