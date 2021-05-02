/**
 * 
 */
package com.gizlocorp.adm.enumeracion;

import com.gizlocorp.adm.modelo.Catalogo;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.modelo.TipoCatalogo;
import com.gizlocorp.adm.modelo.Usuario;

/**
 * 
 * @author
 * @version $Revision: 1.0 $
 */
public enum Entidad {
	USUARIO("Usuario", Usuario.class), PLANTILLA("Planitlla", Plantilla.class), PARAMETRO(
			"Parametro", Parametro.class), ORGANIZACION("Emisor",
			Organizacion.class), TIPOCATALOGO("Tipo Catalogo",
			TipoCatalogo.class), CATALOGO("Catalogo", Catalogo.class);

	private String descripcion;

	private Class<?> clazz;

	private Entidad(String descripcion, Class<?> clazz) {
		this.descripcion = descripcion;
		this.clazz = clazz;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public Class<?> getClazz() {
		return this.clazz;
	}
}
