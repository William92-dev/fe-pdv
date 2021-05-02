package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.Usuario;

@Local
public interface UsuarioDAO extends GenericDAO<Usuario, String> {

	List<Usuario> obtenerBasico(String username, Persona persona);

	List<Usuario> obtenerParametros(String nombres, String apellidos,
			String identificacion);

	Usuario obtenerUsuario(String username);

	List<String> obtenerUsuariosSincronizar();

}
