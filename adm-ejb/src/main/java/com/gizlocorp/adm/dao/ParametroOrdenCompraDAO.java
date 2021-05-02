package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametroOrdenCompra;

@Local
public interface ParametroOrdenCompraDAO extends GenericDAO<ParametroOrdenCompra, Long> {

	
	ParametroOrdenCompra obtenerParametro(String codigo, String rucProveedor);

	List<ParametroOrdenCompra> listaParametro(String codigo, Estado estado) throws GizloException;
	
	List<ParametroOrdenCompra> listaParametroRucProveedor(String rucProveedor) throws GizloException;
}
