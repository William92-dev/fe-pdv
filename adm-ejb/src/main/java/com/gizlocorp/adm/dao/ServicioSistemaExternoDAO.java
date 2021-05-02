package com.gizlocorp.adm.dao;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.SistemaExterno;


@Local
public interface ServicioSistemaExternoDAO extends GenericDAO<SistemaExterno, String>{
	
	SistemaExterno obtenerUsuarioSistemaExt(Long id) throws GizloException;
	
}
