package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioExterno;

@SessionScoped
@Named("restablecerContrasenaDataBean")
public class RestablecerContrasenaDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Organizacion emisor;
	private Usuario user;
	private UsuarioExterno usuarioExterno;

	/** Parametros de Consulta */
	private String usuario;
	private String contrasena;
	private String contrasenaProvicional;
	private String confirmarContrasena;
	private String contrasenaActual;
	private boolean envioMail = false;

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getConfirmarContrasena() {
		return confirmarContrasena;
	}

	public void setConfirmarContrasena(String confirmarContrasena) {
		this.confirmarContrasena = confirmarContrasena;
	}

	public String getContrasenaActual() {
		return contrasenaActual;
	}

	public void setContrasenaActual(String contrasenaActual) {
		this.contrasenaActual = contrasenaActual;
	}

	public Organizacion getEmisor() {
		return emisor;
	}

	public void setEmisor(Organizacion emisor) {
		this.emisor = emisor;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public boolean isEnvioMail() {
		return envioMail;
	}

	public void setEnvioMail(boolean envioMail) {
		this.envioMail = envioMail;
	}

	public UsuarioExterno getUsuarioExterno() {
		return usuarioExterno;
	}

	public void setUsuarioExterno(UsuarioExterno usuarioExterno) {
		this.usuarioExterno = usuarioExterno;
	}

	public String getContrasenaProvicional() {
		return contrasenaProvicional;
	}

	public void setContrasenaProvicional(String contrasenaProvicional) {
		this.contrasenaProvicional = contrasenaProvicional;
	}

}
