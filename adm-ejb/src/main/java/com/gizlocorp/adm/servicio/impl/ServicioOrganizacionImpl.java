/**
 * 
 */
package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.dao.OrganizacionDAO;
import com.gizlocorp.adm.dao.ParametroDAO;
import com.gizlocorp.adm.dao.PlantillaDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.utilitario.TripleDESUtil;

/**
 * 
 * @author
 * @version
 */
@Stateless
public class ServicioOrganizacionImpl implements ServicioOrganizacionLocal {

	private static Logger log = Logger.getLogger(ServicioOrganizacionImpl.class
			.getName());
	@EJB
	OrganizacionDAO organizacionDAO;

	@EJB
	ParametroDAO parametroDAO;

	@EJB
	PlantillaDAO plantillaDAO;

	@Override
	public void ingresarOrganizacion(Organizacion organizacion)
			throws GizloException {
		try {
			organizacion.setEstado(Estado.ACT);
			organizacion = organizacionDAO.persist(organizacion);

			List<Parametro> parametros = parametroDAO.listaParametro(null,
					null, organizacion.getId());

			if (parametros == null || parametros.isEmpty()) {
				parametros = parametroDAO.listaParametro(null, null, null);
				if (parametros != null && !parametros.isEmpty()) {
					Parametro parametroOrganizacion = null;
					String valorDesencriptar = null;

					for (Parametro parametro : parametros) {
						parametroOrganizacion = new Parametro();
						parametroOrganizacion.setCodigo(parametro.getCodigo());
						parametroOrganizacion.setDescripcion(parametro
								.getDescripcion());
						parametroOrganizacion.setEncriptado(parametro
								.getEncriptado());
						parametroOrganizacion.setEstado(parametro.getEstado());
						parametroOrganizacion.setIdAplicacion(parametro
								.getIdAplicacion());
						parametroOrganizacion.setIdOrganizacion(organizacion
								.getId());
						parametroOrganizacion.setTipoParametro(parametro
								.getTipoParametro());
						parametroOrganizacion.setValor(parametro.getValor());

						if (parametro.isEsEncriptado()) {
							try {
								valorDesencriptar = TripleDESUtil
										._decrypt(parametro.getValor());
								parametroOrganizacion
										.setValor(valorDesencriptar);

							} catch (Exception ex) {
								log.error(
										"Error desencriptar: "
												+ parametro.getValor(), ex);
							}

						}

						parametroDAO.persist(parametroOrganizacion);
					}
				}
			}

			// log.debug("plantillas");
			List<Plantilla> plantillas = plantillaDAO.listaPlantilla(null,
					null, organizacion.getId());

			if (plantillas == null || plantillas.isEmpty()) {
				plantillas = plantillaDAO.listaPlantilla(null, null, null);
				// log.debug("Plantillas: "+plantillas);
				if (plantillas != null && !plantillas.isEmpty()) {
					Plantilla plantillaOrganizacion = null;

					for (Plantilla plantilla : plantillas) {
						plantillaOrganizacion = new Plantilla();
						plantillaOrganizacion.setCodigo(plantilla.getCodigo());
						plantillaOrganizacion.setDescripcion(plantilla
								.getDescripcion());
						plantillaOrganizacion.setEstado(plantilla.getEstado());
						plantillaOrganizacion.setIdAplicacion(plantilla
								.getIdAplicacion());
						plantillaOrganizacion.setIdOrganizacion(organizacion
								.getId());
						plantillaOrganizacion.setValor(plantilla.getValor());
						plantillaOrganizacion.setTitulo(plantilla.getTitulo());

						plantillaDAO.persist(plantillaOrganizacion);
					}
				}
			}
		} catch (Exception e) {
			throw new GizloException("Error al guardar Organizacion", e);
		}
	}

	@Override
	public List<Organizacion> listarOrganizaciones(String nombre,
			String acronimo, String ruc, Long idOrganizacion)
			throws GizloException {
		return organizacionDAO.listar(nombre, acronimo, ruc, idOrganizacion);
	}

	@Override
	public void actualizarOrganizacion(Organizacion organizacion)
			throws GizloException {
		try {
			organizacionDAO.update(organizacion);
		} catch (Exception e) {
			throw new GizloException("Error al actualizar Organizacion", e);
		}
	}

	@Override
	public Organizacion consultarOrganizacion(String ruc) throws GizloException {
		return organizacionDAO.consultarOrganizacion(ruc);
	}

	@Override
	public List<Organizacion> listarActivas() throws GizloException {
		return organizacionDAO.listarActivas();
	}

	@Override
	public void eliminarOrganizacion(Organizacion organizacion)
			throws GizloException {
		try {
			organizacionDAO.delete(organizacion);
		} catch (GizloDeleteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Organizacion consultarOrganizacionID(Long id) throws GizloException {
		// TODO Auto-generated method stub
		return organizacionDAO.consultarOrganizacionID(id);
	}
}
