package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioRol;

@Local
public interface UsuarioRolDAO extends GenericDAO<UsuarioRol, String> {

	List<UsuarioRol> obtenerUsuarioRolPorUsuario(Usuario usuario)
			throws GizloException;

}
