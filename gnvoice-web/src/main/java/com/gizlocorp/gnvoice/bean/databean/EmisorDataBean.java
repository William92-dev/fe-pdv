package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.gizlocorp.adm.modelo.Organizacion;

@SessionScoped
@Named("emisorDataBean")
public class EmisorDataBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;
	private Organizacion organizacion;
	private List<Organizacion> organizaciones;
	private int opcion = 0;
	private Long idCiudad;
	private Long idOrganizacionPadre;
	private Part uploadedFile;

	private boolean editarArchivo = true;

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

	public List<Organizacion> getOrganizaciones() {
		return organizaciones;
	}

	public void setOrganizaciones(List<Organizacion> organizaciones) {
		this.organizaciones = organizaciones;
	}

	public int getOpcion() {
		return opcion;
	}

	public void setOpcion(int opcion) {
		this.opcion = opcion;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdOrganizacionPadre() {
		return idOrganizacionPadre;
	}

	public void setIdOrganizacionPadre(Long idOrganizacionPadre) {
		this.idOrganizacionPadre = idOrganizacionPadre;
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

}