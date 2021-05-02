package com.gizlocorp.gnvoice.utilitario;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.gizlocorp.gnvoice.bean.SessionBean;

/**
 * Servlet Filter implementation class AutorizacionFilter
 */

// @WebFilter("/AutorizacionFilter")
public class AutorizacionFilter implements Filter {

	@Inject
	SessionBean sessionBean;

	private static Logger log = Logger.getLogger(AutorizacionFilter.class
			.getName());

	private String errorPage;

	// @Inject
	// private SessionBean sessionBean;
	/**
	 * Default constructor.
	 */
	public AutorizacionFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	private void returnError(ServletRequest request, ServletResponse response,
			String errorString) throws ServletException, IOException {
		request.setAttribute("error", errorString);
		request.getRequestDispatcher(errorPage).forward(request, response);
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		boolean authorized = true;

		if (response != null) {
			try {

				String URI = ((HttpServletRequest) request).getRequestURI();
				String url = (URI != null && URI.length() > 4) ? URI.substring(
						0, URI.length()) : null;

				String rol = null;
				if (sessionBean != null) {
					rol = sessionBean.getCodigoRol();
				}

				if (!url.contains("error")
						&& !url.contains("/javax.faces.resource")
						&& !url.contains("/noAutorizada")
						&& !url.contains("/home") && !url.contains("/index")
						&& !url.contains("/login")
						&& !url.contains("/restablecerContrasena")
						&& !url.contains("/seguimientoFacturaPublica")
						&& !url.contains("/seguimientoNotaCreditoPublica")
						&& !url.contains("/restablecerUsuario")
						&& !url.contains("/template")) {
					authorized = false;

					if ("ADMIN".equals(rol)) {
						authorized = true;
					}
					if ("SOPOR".equals(rol)
							&& (url.contains("Usuario") || url
									.contains("/seguimiento"))) {
						authorized = true;
					}
					if ("SEGUM".equals(rol)
							&& (url.contains("/recepcion") || url
									.contains("/seguimiento"))) {
						authorized = true;
					}
					if ("CONSU".equals(rol) && url.contains("/consulta")) {
						authorized = true;
					}
					if ("REC".equals(rol)) {
						authorized = true;
					}
					if ("ASE".equals(rol)) {
						authorized = true;
					}
					if ("PROVE".equals(rol)) {
						authorized = true;
					}
					
					if ("CNABF".equals(rol)) {
						authorized = true;
					}
					if ("SGABF".equals(rol)) {
						authorized = true;
					}
					
				}

				if (authorized) {
					chain.doFilter(request, response);

				} else {
					returnError(request, response,
							"Usuario no autorizado para acceder a este recurso");
				}

			} catch (Exception e) {
				log.error("Ha ocurrido un error en filtro autorizacion: ", e);
			}
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		if (fConfig != null) {
			errorPage = fConfig.getInitParameter("error_page");
		}
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
