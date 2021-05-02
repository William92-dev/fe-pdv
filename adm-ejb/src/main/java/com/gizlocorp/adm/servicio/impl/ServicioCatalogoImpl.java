package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.CatalogoDAO;
import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.Catalogo;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;

@Stateless
public class ServicioCatalogoImpl implements ServicioCatalogoLocal {

	@EJB
	CatalogoDAO catalogoDAO;

	@Override
	public void guardarCatalogo(Catalogo catalogo) throws GizloException {
		if (catalogo.getId() == null) {
			try {
				catalogoDAO.persist(catalogo);
			} catch (GizloPersistException e) {
				throw new GizloException("Error al guardar Catalogo", e);
			}
		} else {
			try {
				catalogoDAO.update(catalogo);
			} catch (GizloUpdateException e) {
				throw new GizloException("Error al modificar Catalogo", e);
			}
		}

	}

	@Override
	public void eliminarCatalogo(Catalogo catalogo) throws GizloDeleteException {
		try {
			catalogoDAO.delete(catalogo);
		} catch (GizloDeleteException e) {
			new GizloDeleteException("Error al eliminar");
		}

	}

	@Override
	public List<Catalogo> listObtenerPorParametros(String codigo, String nombre)
			throws GizloException {
		List<Catalogo> lisCatalogos = catalogoDAO.listObtenerPorParametros(
				codigo, nombre);
		return lisCatalogos;
	}

	@Override
	public List<Catalogo> listCatalogoPorTipoCatalogo(
			String nombretipoCatalogo, String coigoTipoCatalogo)
			throws GizloException {
		List<Catalogo> lisCatalogos = catalogoDAO.listCatalogoPorTipoCatalogo(
				nombretipoCatalogo, coigoTipoCatalogo);
		return lisCatalogos;
	}

	@Override
	public Catalogo obtenerPorId(Long idCatalogo) throws GizloException {

		return catalogoDAO.obtenerPorId(idCatalogo);
	}

}