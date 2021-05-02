/**
 * 
 */
package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;

/**
 * 
 * @author
 * @version
 */
@Local
public interface ServicioParametroOrdenCompraLocal {
	List<ParametroOrdenCompra> listarParametros(String codigo, Estado estado) throws GizloException;

	void ingresarParametro(ParametroOrdenCompra parametro) throws GizloException;

	void actualizarParametro(ParametroOrdenCompra parametro) throws GizloException;

	ParametroOrdenCompra consultarParametro(String codigo, String rucProveedor)
			throws GizloException;
	
	List<ParametroOrdenCompra> listaParametroRucProveedor(String rucProveedor) throws GizloException;
}
