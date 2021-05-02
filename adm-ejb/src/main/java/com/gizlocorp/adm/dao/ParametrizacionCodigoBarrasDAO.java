package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;

@Local
public interface ParametrizacionCodigoBarrasDAO extends GenericDAO<ParametrizacionCodigoBarras, String> {

	

	List<ParametrizacionCodigoBarras> obtenerCodigoBarrasPorParametros(String codigoArticulo, String tipoArticulo,  Estado estado, String ruc)
			throws GizloException;
}
