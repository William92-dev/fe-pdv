/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gizlocorp.gnvoice.json.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author GIZLO
 */
public class DatosPersona {
	@JsonProperty("datosPersona")
	private List<Dato> datosPersona;

	public DatosPersona() {
	}

	public DatosPersona(List<Dato> datosPersona) {
		this.datosPersona = datosPersona;
	}
	
	public List<Dato> getDatosPersona() {
		return datosPersona;
	}

	public void setDatosPersona(List<Dato> datosPersona) {
		this.datosPersona = datosPersona;
	}

	@Override
	public String toString() {
		String retorna = "datosPersona = \n";
		if(datosPersona!=null && !datosPersona.isEmpty()){
			for (Dato dato : datosPersona) {
				retorna += dato.toString() + "\n";
			}
		}
		return retorna;
	}

}
