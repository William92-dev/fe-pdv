package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Rol;
import com.gizlocorp.adm.modelo.UsuarioExterno;

@SessionScoped
@Named("usuarioDataBean")
public class UsuarioDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Parametros de Consulta */
	private String nombre;
	private String apellido;
	private String identificacion;

	private UsuarioExterno usuarioExternoSeleccionada;
	private List<UsuarioExterno> listUsuarioExternos;

	private Rol rol;

	private Estado estado;
	
	private Organizacion organizacion;

	private boolean esIngreso = true;
	private boolean esModificacion = true;

	private boolean editarArchivo = true;

	private Part uploadedFile;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public UsuarioExterno getUsuarioExternoSeleccionada() {
		return usuarioExternoSeleccionada;
	}

	public void setUsuarioExternoSeleccionada(
			UsuarioExterno usuarioExternoSeleccionada) {
		this.usuarioExternoSeleccionada = usuarioExternoSeleccionada;
	}

	public List<UsuarioExterno> getListUsuarioExternos() {
		return listUsuarioExternos;
	}

	public void setListUsuarioExternos(List<UsuarioExterno> listUsuarioExternos) {
		this.listUsuarioExternos = listUsuarioExternos;
	}

	public boolean isEsIngreso() {
		return esIngreso;
	}

	public void setEsIngreso(boolean esIngreso) {
		this.esIngreso = esIngreso;
	}

	public boolean isEsModificacion() {
		return esModificacion;
	}

	public void setEsModificacion(boolean esModificacion) {
		this.esModificacion = esModificacion;
	}

	public Part getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(Part uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public boolean isEditarArchivo() {
		return editarArchivo;
	}

	public void setEditarArchivo(boolean editarArchivo) {
		this.editarArchivo = editarArchivo;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}
	
	

}
