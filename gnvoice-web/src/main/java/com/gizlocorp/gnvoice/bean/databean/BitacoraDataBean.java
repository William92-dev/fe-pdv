package com.gizlocorp.gnvoice.bean.databean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.gizlocorp.adm.modelo.BitacoraEvento;

@SessionScoped
@Named("bitacoraDataBean")
public class BitacoraDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Parametros de Consulta */
	private Long idEvento;
	private String usuario;
	private String detalle;
	private Date fechaDesde;
	private Date fechaHasta;

	private List<BitacoraEvento> listBitacoraEventos;

	public Long getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(Long idEvento) {
		this.idEvento = idEvento;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
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

	public List<BitacoraEvento> getListBitacoraEventos() {
		return listBitacoraEventos;
	}

	public void setListBitacoraEventos(List<BitacoraEvento> listBitacoraEventos) {
		this.listBitacoraEventos = listBitacoraEventos;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

}
