package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.PlantillaDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal;

@Stateless
public class ServicioPlantillaImpl implements ServicioPlantillaLocal {

	@EJB
	PlantillaDAO plantillaDAO;

	@Override
	public void guardarPlantilla(Plantilla plantilla) throws GizloException {
		if (plantilla.getId() == null) {
			try {
				plantillaDAO.persist(plantilla);
			} catch (GizloPersistException e) {
				throw new GizloException(e);
			}
		} else {
			try {
				plantillaDAO.update(plantilla);
			} catch (GizloUpdateException e) {
				throw new GizloException(e);
			}
		}

	}

	@Override
	public List<Plantilla> listaPlantilla(String codigo, String titulo,
			Long idOrganizacion) throws GizloException {
		return plantillaDAO.listaPlantilla(codigo, titulo, idOrganizacion);
	}

	@Override
	public Plantilla obtenerPlantilla(String codigo, Long idOrganizacion)
			throws GizloException {
		return plantillaDAO.obtenerPlantilla(codigo, idOrganizacion);
	}

}
