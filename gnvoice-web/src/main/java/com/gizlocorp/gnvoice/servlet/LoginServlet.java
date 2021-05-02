package com.gizlocorp.gnvoice.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.SistemaExterno;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.servicio.local.ServicioSistemaExternoLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.adm.utilitario.TripleDESUtil;
import com.gizlocorp.gnvoice.bean.SessionBean;
import com.gizlocorp.gnvoice.utilitario.Constantes;

public class LoginServlet extends HttpServlet {

	/** 
	 * 
	 */ 
	private static final long serialVersionUID = -510039371546227482L;

	
	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametroLocal;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioSistemaExternoImpl!com.gizlocorp.adm.servicio.local.ServicioSistemaExternoLocal")
	ServicioSistemaExternoLocal servicioSistemaExternoLocal;
	
	@Inject
	private SessionBean sessionBean;

	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		String resource = req.getParameter("id");

		try {
				
			login(resource, req, resp);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	public String login(String id, final HttpServletRequest req, final HttpServletResponse resp) {
		try {
			SistemaExterno sistemaExterno = null;
			sistemaExterno = servicioSistemaExternoLocal.obtenerUsuarioSistemaExt(Long.parseLong(id));
			if(sistemaExterno != null){

				String pasw = TripleDESUtil._decrypt(sistemaExterno.getClave());
				req.login(sistemaExterno.getUsuarioSistema(), pasw );

				cargarUsuario(sistemaExterno);
				req.getRequestDispatcher("/").forward(req, resp);
				
				
			}
			

			return "inicio";
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}
	
	private void cargarUsuario(SistemaExterno sistemaExterno) {
		try {
			if (sessionBean.getUsuario() == null) {
				Usuario usuario = usuarioUsuarioLocal.obtenerUsuario(sistemaExterno.getUsuarioAplicacion());

				if (usuario != null && usuario.getPersona() != null) {
						sessionBean.setUsuario(usuario);

					if (usuario.getPersona().getOrganizacion() != null) {
						sessionBean.setIdOrganizacion(usuario.getPersona()
								.getOrganizacion().getId());
						sessionBean.setRucOrganizacion(usuario.getPersona()
								.getOrganizacion().getRuc());
						sessionBean.setNombreOrganizacion(usuario
								.getPersona().getOrganizacion()
								.getNombreComercial());
						sessionBean.setLogoOrganizacion(usuario
								.getPersona().getOrganizacion()
								.getLogoEmpresa());
					}

					Parametro parametro = servicioParametroLocal
							.consultarParametro(
									Constantes.URL_SERVIDOR,
									usuario.getPersona().getOrganizacion() != null ? usuario
											.getPersona().getOrganizacion()
											.getId()
											: null);
					sessionBean
							.setUrlServidor(parametro != null ? parametro
									.getValor() : "");

					parametro = servicioParametroLocal
							.consultarParametro(
									Constantes.DIRECTORIO_SERVIDOR,
									usuario.getPersona().getOrganizacion() != null ? usuario
											.getPersona().getOrganizacion()
											.getId()
											: null);
					sessionBean
							.setDirectorioServidor(parametro != null ? parametro
									.getValor() : "");

					if (usuario != null
							&& usuario.getUsuariosRoles() != null
							&& !usuario.getUsuariosRoles().isEmpty()) {
						sessionBean.setRoleStr(usuario.getUsuariosRoles()
								.get(0).getRol().getNombre());
						sessionBean.setCodigoRol(usuario.getUsuariosRoles()
								.get(0).getRol().getCodigo());
					}

					
				}

			}

		} catch (Exception e) {
			e.getStackTrace();
		}
	}

}
