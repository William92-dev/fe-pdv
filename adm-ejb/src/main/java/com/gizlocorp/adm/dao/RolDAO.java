package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.UsuarioRol;


@Local
public interface RolDAO extends GenericDAO<Rol, String>{
	
	List<Rol> obtenerPorBasico(String codigo, String nombre, Rol rolPadre);
	void guardarUsuarioRol(List<UsuarioRol> listaUsuarioRol)
			throws GizloException;
	Rol buscarPorNombre(String nombreRol)
			throws GizloException;

}
