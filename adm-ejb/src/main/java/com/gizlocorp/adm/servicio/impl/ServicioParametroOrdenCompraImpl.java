/**
 * 
 */
package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.dao.ParametroOrdenCompraDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;
import com.gizlocorp.adm.servicio.local.ServicioParametroOrdenCompraLocal;

/**
 * 
 * @author
 * @version
 */
@Stateless
public class ServicioParametroOrdenCompraImpl implements ServicioParametroOrdenCompraLocal {

	private static Logger log = Logger.getLogger(ServicioParametroOrdenCompraImpl.class.getName());
	@EJB
	ParametroOrdenCompraDAO parametroDAO;

	@Override
	public List<ParametroOrdenCompra> listarParametros(String codigo, Estado estado) throws GizloException {
		List<ParametroOrdenCompra> resultado = parametroDAO.listaParametro(codigo, estado);
		return resultado;
	}

	@Override
	public void ingresarParametro(ParametroOrdenCompra parametro) throws GizloException {
		try {

			parametro.setEstado(Estado.ACT);
			parametroDAO.persist(parametro);
		} catch (Exception e) {
			throw new GizloException("Error al guardar Parametro", e);
		}
	}

	@Override
	public void actualizarParametro(ParametroOrdenCompra parametro) throws GizloException {
		try {
			
			parametroDAO.update(parametro);
		} catch (Exception e) {
			throw new GizloException("Error al actualizar Parametro", e);
		}
	}

	@Override
	public ParametroOrdenCompra consultarParametro(String codigo, String rucProveedor)
			throws GizloException {
		ParametroOrdenCompra respuesta = parametroDAO.obtenerParametro(codigo,rucProveedor);
		return respuesta;
	}
	
	
	@Override
	public List<ParametroOrdenCompra> listaParametroRucProveedor(String rucProveedor) throws GizloException {
		List<ParametroOrdenCompra> resultado = parametroDAO.listaParametroRucProveedor(rucProveedor);
		return resultado;
	}
	


}
