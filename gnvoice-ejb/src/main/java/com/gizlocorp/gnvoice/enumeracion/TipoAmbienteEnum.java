package com.gizlocorp.gnvoice.enumeracion;

/**
 * 
 * @author gizlo
 */
public enum TipoAmbienteEnum {
	PRODUCCION("2"), PRUEBAS("1");
	private String code;

	private TipoAmbienteEnum(final String code) {
		this.code = code;
	}

	/**
	 * obtiene el codigo de la numeracion
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}
}
