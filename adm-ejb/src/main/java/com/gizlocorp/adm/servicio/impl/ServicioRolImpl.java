package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.RolDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.adm.servicio.local.ServicioRolLocal;


@Stateless
public class ServicioRolImpl implements ServicioRolLocal{

	@EJB 
	RolDAO rolDAO;
	
	public Rol buscarPorNombre(String nombreRol)
			throws GizloException {
		return rolDAO.buscarPorNombre(nombreRol);
	}
	
	public void guardarUsuarioRol(List<UsuarioRol> listaUsuarioRol)
			throws GizloException {
		rolDAO.guardarUsuarioRol(listaUsuarioRol);
	}

	@Override
	public Rol buscarRol(String codigo, String nombre) throws GizloException {
		List<Rol>  roles = rolDAO.obtenerPorBasico(codigo, nombre, null);
		return ((roles!=null && !roles.isEmpty())?roles.get(0):null);
	}
	
	@Override
	public List<Rol> listarRol() throws GizloException {		
		List<Rol>  roles = rolDAO.obtenerPorBasico(null, null, null);
		return ((roles!=null && !roles.isEmpty())?roles:null);
	}

}
