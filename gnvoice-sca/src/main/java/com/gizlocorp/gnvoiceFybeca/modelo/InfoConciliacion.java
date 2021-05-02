package com.gizlocorp.gnvoiceFybeca.modelo;

import java.io.Serializable;

/**
 * The persistent class for the Factura database table.
 * 
 */
// @Entity
public class InfoConciliacion implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idFarmacia;

	private Long idDocumento;

	private String secuencial;

	private String secuencialNotaCredito;

	public Long getIdFarmacia() {
		return idFarmacia;
	}

	public void setIdFarmacia(Long idFarmacia) {
		this.idFarmacia = idFarmacia;
	}

	public Long getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getSecuencial() {
		return secuencial;
	}

	public void setSecuencial(String secuencial) {
		this.secuencial = secuencial;
	}

	public String getSecuencialNotaCredito() {
		return secuencialNotaCredito;
	}

	public void setSecuencialNotaCredito(String secuencialNotaCredito) {
		this.secuencialNotaCredito = secuencialNotaCredito;
	}

}