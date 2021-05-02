/**
 * 
 */
package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;

/**
 * 
 * @author
 * @version
 */
@Local
public interface ServicioCodigoBarrasLocal {
	
	List<ParametrizacionCodigoBarras> obtenerCodigoBarrasPorParametros(String codigoArticulo, String tipoArticulo,  Estado estado, String ruc)
			throws GizloException;
	
	void guardar(ParametrizacionCodigoBarras codigoBarras);
}
