/**
 * 
 */
package com.gizlocorp.adm.servicio.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.UbicacionGeograficaDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.UbicacionGeografica;
import com.gizlocorp.adm.servicio.local.ServicioUbicacionGeograficaLocal;

/**
 * 
 * @author
 * @version
 */
@Stateless
public class ServicioUbicacionGeograficaImpl implements
		ServicioUbicacionGeograficaLocal {

	@EJB
	UbicacionGeograficaDAO ubicacionGeograficaDAO;

	@Override
	public List<UbicacionGeografica> listarCiudades(Long idProvincia)
			throws GizloException {
		List<UbicacionGeografica> listado = null;
		try {

			List<UbicacionGeografica> ugs = ubicacionGeograficaDAO.findAll();
			if (ugs != null && !ugs.isEmpty()) {
				listado = new ArrayList<UbicacionGeografica>();
				//UbicacionGeografica ubicacionGeografica = null;

				for (UbicacionGeografica ug : ugs) {
					if ("ACT".equals(ug.getEstado())
							&& ug.getUbicacionPadre() != null
							&& ug.getUbicacionPadre().getId() != null
							&& ug.getUbicacionPadre().getId().longValue() == idProvincia) {
						listado.add(ug);
					}
				}
			}
			return listado;
		} catch (Exception ex) {
			throw new GizloException("Error al listar ciudades de provincia "
					+ idProvincia, ex);
		}
	}

	@Override
	public List<UbicacionGeografica> listarCiudades() throws GizloException {
		List<UbicacionGeografica> listado = null;
		try {
			List<UbicacionGeografica> provincias = ubicacionGeograficaDAO
					.findAll();
			if (provincias != null && !provincias.isEmpty()) {
				listado = new ArrayList<UbicacionGeografica>();

				for (UbicacionGeografica provincia : provincias) {
					if ("ACT".equals(provincia.getEstado())
							&& provincia.getUbicacionPadre() != null
							&& provincia.getUbicacionPadre().getId() != null
							&& provincia.getUbicacionPadre().getId()
									.longValue() == 1L) {

						for (UbicacionGeografica ciudad : provincias) {
							if ("ACT".equals(ciudad.getEstado())
									&& ciudad.getUbicacionPadre() != null
									&& ciudad.getUbicacionPadre().getId()
											.equals(provincia.getId())) {
								listado.add(ciudad);

							}
						}
					}
				}
			}
			return listado;
		} catch (Exception ex) {
			throw new GizloException("Error al listar ciudades", ex);
		}

	}

}
