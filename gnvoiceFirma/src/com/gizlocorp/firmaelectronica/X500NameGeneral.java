/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gizlocorp.firmaelectronica;

public class X500NameGeneral {
	private java.lang.String CN = null;
	private java.lang.String OU = null;
	private java.lang.String O = null;
	private java.lang.String L = null;
	private java.lang.String ST = null;
	private java.lang.String C = null;

	public X500NameGeneral(String name) {
		java.util.StringTokenizer st = new java.util.StringTokenizer(name, ",");

		while (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			int idx = token.indexOf("=");
			if (idx >= 0) {
				String label = token.substring(0, idx);
				String value = token.substring(idx + 1);

				if ("CN".equals(label)) {
					CN = value;
				} else if ("OU".equals(label)) {
					OU = value;
				} else if ("O".equals(label)) {
					O = value;
				} else if ("C".equals(label)) {
					C = value;
				} else if ("L".equals(label)) {
					L = value;
				} else if ("ST".equals(label)) {
					ST = value;
				}
			}
		}
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getC() {
		return C;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getCN() {
		return CN;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getL() {
		return L;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getO() {
		return O;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getOU() {
		return OU;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getST() {
		return ST;
	}
}