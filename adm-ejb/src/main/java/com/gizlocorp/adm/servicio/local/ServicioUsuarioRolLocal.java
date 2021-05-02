package com.gizlocorp.adm.servicio.local;

import javax.ejb.Local;

import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioRol;

@Local
public interface ServicioUsuarioRolLocal {

	void guardarUsuarioRol(UsuarioRol usuarioRol);
	
	UsuarioRol obtenerUsuarioRolPorUsuario(Usuario usuario);
	

}
