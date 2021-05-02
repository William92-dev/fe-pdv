package com.gizlocorp.gnvoice.enumeracion;

/**
 * 
 * @author gizlo
 */
public enum TipoEmisionEnum {
	NORMAL("NORMAL", "1"), CONTINGENCIA("INDISPONIBILIDAD DE SISTEMA", "2");
	private String descripcion;
	private String codigo;

	private TipoEmisionEnum(final String descripcion, final String codigo) {
		this.descripcion = descripcion;
		this.codigo = codigo;
	}

	/**
	 * obtiene el codigo de la numeracion
	 * 
	 * @return
	 */
	public String getDescripcion() {
		return descripcion;
	}

	public String getCodigo() {
		return codigo;
	}
}
