package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

/**
 * 
 * @author Usuario
 * 
 */

// @Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("homeBean")
public class HomeBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(HomeBean.class.getName());

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;

	private Usuario usuario;

	@Inject
	private SessionBean sessionBean;

	public void validarUsuario() {
		if (sessionBean.getUsuario() != null) {
			usuario = sessionBean.getUsuario();
			if (usuario.getEsClaveAutogenerada() != null
					&& usuario.getEsClaveAutogenerada().equals(Logico.S)) {
				try {
					NavigationHandler nh = FacesContext.getCurrentInstance()
							.getApplication().getNavigationHandler();
					nh.handleNavigation(FacesContext.getCurrentInstance(),
							null, "restablecerUsuario");
				} catch (Exception e) {
					log.error(e.getMessage(), e);

				}
			}
		}
	}
}
