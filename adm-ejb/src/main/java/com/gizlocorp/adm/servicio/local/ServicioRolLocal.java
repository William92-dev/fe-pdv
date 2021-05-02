package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.UsuarioRol;


@Local
public interface ServicioRolLocal {
	
	Rol buscarPorNombre(String nombreRol) throws GizloException;
	void guardarUsuarioRol(List<UsuarioRol> listaUsuarioRol) throws GizloException;
	Rol buscarRol(String codigo, String nombre) throws GizloException;
	List<Rol> listarRol() throws GizloException;
}
