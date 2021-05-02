package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;

@SessionScoped
@Named("claveContingenciaDataBean")
public class ClaveContingenciaDataBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;
	private ClaveContingencia claveContingencia;
	private List<ClaveContingencia> claves;
	private int opcion = 0;
	private Part uploadedFile;
	private boolean editarArchivo = true;
	private String archivoClave;
	private byte[] dataArchivo;
	private int contador = 0;
	private TipoAmbienteEnum claveTipoAmbiente;

	public List<SelectItem> getOpciones() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Logico estado : Logico.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	public List<SelectItem> getTiposAmbiente() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoAmbienteEnum tipo : TipoAmbienteEnum.values()) {
			items.add(new SelectItem(tipo, tipo.name()));
		}
		return items;
	}

	public ClaveContingencia getClaveContingencia() {
		return claveContingencia;
	}

	public void setClaveContingencia(ClaveContingencia claveContingencia) {
		this.claveContingencia = claveContingencia;
	}

	public List<ClaveContingencia> getClaves() {
		return claves;
	}

	public void setClaves(List<ClaveContingencia> claves) {
		this.claves = claves;
	}

	public int getOpcion() {
		return opcion;
	}

	public void setOpcion(int opcion) {
		this.opcion = opcion;
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

	public String getArchivoClave() {
		return archivoClave;
	}

	public void setArchivoClave(String archivoClave) {
		this.archivoClave = archivoClave;
	}

	public byte[] getDataArchivo() {
		return dataArchivo;
	}

	public void setDataArchivo(byte[] dataArchivo) {
		this.dataArchivo = dataArchivo;
	}

	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}

	public TipoAmbienteEnum getClaveTipoAmbiente() {
		return claveTipoAmbiente;
	}

	public void setClaveTipoAmbiente(TipoAmbienteEnum claveTipoAmbiente) {
		this.claveTipoAmbiente = claveTipoAmbiente;
	}

}