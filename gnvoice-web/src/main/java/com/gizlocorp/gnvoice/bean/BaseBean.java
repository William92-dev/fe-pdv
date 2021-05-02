/**
 * 
 */
package com.gizlocorp.gnvoice.bean;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	protected void infoMessage(String texto) {
		infoMessage(texto, null);
	}

	protected void infoMessage(String texto, String nombre) {

		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, texto,
				"");
		getFacesContext().addMessage(nombre, msg);
	}

	protected void errorMessage(String texto) {
		errorMessage(texto, null);
	}

	protected void errorMessage(String texto, String nombre) {

		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, texto,
				"");
		getFacesContext().addMessage(nombre, msg);
	}

	protected String getMensaje(String bundleName, String clave) {
		ResourceBundle bundle = getFacesContext().getApplication()
				.getResourceBundle(getFacesContext(), bundleName);
		return bundle.getString(clave);
	}

	protected String getMensaje(String bundleName, String clave, Object[] params) {
		ResourceBundle bundle = getFacesContext().getApplication()
				.getResourceBundle(getFacesContext(), bundleName);
		return MessageFormat.format(bundle.getString(clave), params);
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected ServletContext getServletContext() {
		return (ServletContext) getFacesContext().getExternalContext()
				.getContext();
	}

	protected String getRequestPath() {
		return getFacesContext().getExternalContext().getRequestContextPath();
	}

	protected void desplegarArchivo(byte[] data, String nombreArchivo, String contentType)
			throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		OutputStream responseStream = response.getOutputStream();
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ nombreArchivo + "\"");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentLength(data.length);
		responseStream.write(data);
		response.flushBuffer();
		responseStream.close();

		facesContext.responseComplete();
	}
}
