package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.UsuarioExterno;

@Local
public interface UsuarioExternoDAO extends GenericDAO<UsuarioExterno, String> {

	List<UsuarioExterno> obtenerParametros(String nombres, String apellidos,
			String identificacion);

	UsuarioExterno obtenerUsuarioExterno(String username);

	boolean existeUsuario(String username);

	List<UsuarioExterno> obtenerUsuariosAutogenerados() throws GizloException;

}
