/**
 * 
 */
package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Parametro;

/**
 * 
 * @author
 * @version
 */
@Local
public interface ServicioParametroLocal {
	List<Parametro> listarParametros(String codigo, Estado estado,Long idOrganizacion) throws GizloException;

	void ingresarParametro(Parametro parametro) throws GizloException;

	void actualizarParametro(Parametro parametro) throws GizloException;

	Parametro consultarParametro(String codigo, Long idOrganizacion) throws GizloException;
}
