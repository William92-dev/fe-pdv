package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.RelacionOrganizacion;


@Local
public interface RelacionOrganizacionDAO extends GenericDAO<RelacionOrganizacion, Long>{
	
	boolean existeRelacionOrganizacion(Organizacion organizacion,Organizacion organizacionRelacion, String tipoRelacion) throws GizloException;
	
	List<RelacionOrganizacion> buscarPorParametros(String ruc, String nombre, String tipoRelacon) throws GizloException;
	
	

}
