package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.gnvoice.utilitario.Constantes;

@SessionScoped
@Named("sessionBean")
public class SessionBean extends BaseBean implements Serializable {

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametroLocal;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Usuario usuario;

	private Long idOrganizacion;

	private String nombreOrganizacion;

	private String logoOrganizacion;

	private String rucOrganizacion;

	private String roleStr;

	private String codigoRol;

	private String urlServidor;

	private String directorioServidor;

	private String skin;

	public void resetear() {
		this.usuario = null;
		this.idOrganizacion = null;
		this.nombreOrganizacion = null;
		this.logoOrganizacion = null;
		this.rucOrganizacion = null;
		this.roleStr = null;
		this.urlServidor = null;
		this.directorioServidor = null;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Long getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public String getNombreOrganizacion() {
		return nombreOrganizacion;
	}

	public void setNombreOrganizacion(String nombreOrganizacion) {
		this.nombreOrganizacion = nombreOrganizacion;
	}

	public String getRucOrganizacion() {
		return rucOrganizacion;
	}

	public void setRucOrganizacion(String rucOrganizacion) {
		this.rucOrganizacion = rucOrganizacion;
	}

	public String getRoleStr() {
		return roleStr;
	}

	public void setRoleStr(String roleStr) {
		this.roleStr = roleStr;
	}

	public String getLogoOrganizacion() {
		return logoOrganizacion;
	}

	public void setLogoOrganizacion(String logoOrganizacion) {
		this.logoOrganizacion = logoOrganizacion;
	}

	public String getUrlServidor() {
		if (urlServidor == null) {
			try {
				Parametro parametro = servicioParametroLocal
						.consultarParametro(Constantes.URL_SERVIDOR, null);
				return parametro != null ? parametro.getValor()
						: "www.corporaciongpf.com";
			} catch (Exception ex) {
				return "www.corporaciongpf.com";
			}
		}
		return urlServidor;
	}

	public void setUrlServidor(String urlServidor) {
		this.urlServidor = urlServidor;
	}

	public String getDirectorioServidor() {
		return directorioServidor;
	}

	public void setDirectorioServidor(String directorioServidor) {
		this.directorioServidor = directorioServidor;
	}

	public String getSkin() {
		if (skin == null) {
			// skin = "ruby";
			skin = "classic";
		}
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public String getCodigoRol() {
		return codigoRol;
	}

	public void setCodigoRol(String codigoRol) {
		this.codigoRol = codigoRol;
	}
}
