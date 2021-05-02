/**
 * 
 */
package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.UbicacionGeografica;



/**
 * 
 * @author 
 * @version 
 */
@Local
public interface ServicioUbicacionGeograficaLocal {
	List<UbicacionGeografica> listarCiudades(Long idProvincia) throws GizloException;
	List<UbicacionGeografica> listarCiudades() throws GizloException;
}
