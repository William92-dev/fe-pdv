package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.modelo.Plantilla;

@Local
public interface PlantillaDAO extends GenericDAO<Plantilla, Long> {

	Plantilla obtenerPlantilla(String codigo, Long idOrganizacion);

	public List<Plantilla> listaPlantilla(String codigo, String titulo,
			Long idOrganizacion);

}
