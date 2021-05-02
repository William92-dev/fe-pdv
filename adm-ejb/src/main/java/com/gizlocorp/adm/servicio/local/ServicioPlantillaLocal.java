package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Plantilla;

@Local
public interface ServicioPlantillaLocal {

	void guardarPlantilla(Plantilla plantilla) throws GizloException;

	public List<Plantilla> listaPlantilla(String codigo, String titulo,
			Long idOrganizacion) throws GizloException;

	Plantilla obtenerPlantilla(String codigo, Long idOrganizacion)
			throws GizloException;

}
