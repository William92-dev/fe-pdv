/**
 * 
 */
package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;

/**
 * 
 * @author
 * @version
 */
@Local
public interface ServicioOrganizacionLocal {
	List<Organizacion> listarOrganizaciones(String nombre, String acronimo,
			String ruc, Long idOrganizacion) throws GizloException;

	void ingresarOrganizacion(Organizacion organizacion) throws GizloException;

	void actualizarOrganizacion(Organizacion parametro) throws GizloException;
	
	void eliminarOrganizacion(Organizacion organizacion) throws GizloException;

	Organizacion consultarOrganizacion(String ruc) throws GizloException;
	
	Organizacion consultarOrganizacionID(Long id) throws GizloException;

	List<Organizacion> listarActivas() throws GizloException;
}
