package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Parametro;

@Local
public interface ParametroDAO extends GenericDAO<Parametro, Long> {

	Parametro obtenerParametro(String codigo, Long idOrganizacion);

	
	List<Parametro> listaParametro(String codigo, Estado estado,Long idOrganizacion) throws GizloException;
}
