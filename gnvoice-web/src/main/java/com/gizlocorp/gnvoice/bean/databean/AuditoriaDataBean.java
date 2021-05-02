package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import com.gizlocorp.adm.auditoria.GizloAudData;
import com.gizlocorp.adm.enumeracion.Entidad;
import com.gizlocorp.adm.enumeracion.Operacion;

@SessionScoped
@Named("auditoriaDataBean")
public class AuditoriaDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Parametros de Consulta */
	private Entidad entidad;
	private Operacion operacion;
	private Date fechaDesde;
	private Date fechaHasta;

	private List<GizloAudData> listaAuditorias;

	public List<SelectItem> getEntidades() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Entidad tipo : Entidad.values()) {
			items.add(new SelectItem(tipo, tipo.name()));
		}
		return items;
	}

	public List<SelectItem> getOperaciones() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Operacion tipo : Operacion.values()) {
			items.add(new SelectItem(tipo, tipo.getDescripcion()));
		}
		return items;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Operacion getOperacion() {
		return operacion;
	}

	public void setOperacion(Operacion operacion) {
		this.operacion = operacion;
	}

	public List<GizloAudData> getListaAuditorias() {
		return listaAuditorias;
	}

	public void setListaAuditorias(List<GizloAudData> listaAuditorias) {
		this.listaAuditorias = listaAuditorias;
	}
}
