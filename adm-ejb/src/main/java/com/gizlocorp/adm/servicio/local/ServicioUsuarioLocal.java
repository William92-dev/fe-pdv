package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioExterno;

@Local
public interface ServicioUsuarioLocal {

	void guardarUsuarioExterno(UsuarioExterno usuarioExterno);

	void eliminarUsuario(UsuarioExterno usuarioExterno) throws GizloException;

	List<UsuarioExterno> obtenerParametros(String nombres, String apellidos,
			String identificacion);

	List<UsuarioExterno> obtenerUsuariosAutogenerados();

	List<Usuario> obtenerUsuarioParametros(String nombres, String apellidos,
			String identificacion);

	List<String> obtenerUsuariosSincronizar();

	Usuario obtenerUsuario(String username);

	UsuarioExterno obtenerUsuarioExterno(String username);

	boolean tienePermiso(String username, String url) throws GizloException;

	void crearUsuarioConsulta(String identificacion);

}
