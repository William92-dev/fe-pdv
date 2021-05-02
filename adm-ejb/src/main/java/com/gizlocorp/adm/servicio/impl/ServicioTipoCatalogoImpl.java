package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.TipoCatalogoDAO;
import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.TipoCatalogo;
import com.gizlocorp.adm.servicio.local.ServicioTipoCatalogoLocal;


@Stateless
public class ServicioTipoCatalogoImpl implements ServicioTipoCatalogoLocal{

	@EJB TipoCatalogoDAO tipoCatalogoDAO;
	
	
	@Override
	public void guardarTipoCatalogo(TipoCatalogo tipoCatalogo) throws GizloException{
		if(tipoCatalogo.getId() == null){
			try {
				tipoCatalogoDAO.persist(tipoCatalogo);
			} catch (GizloPersistException e) {
				e.printStackTrace();
			}
		}else{
			try {
				tipoCatalogoDAO.update(tipoCatalogo);
			} catch (GizloUpdateException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void eliminarTipoCatalogo(TipoCatalogo tipoCatalogo) throws GizloDeleteException {
		try {
			tipoCatalogoDAO.delete(tipoCatalogo);
		} catch (GizloDeleteException e) {
			new GizloDeleteException("Error al eliminar");
		}
	}


	@Override
	public List<TipoCatalogo> listObtenerPorParametros(String codigo, String nombre) throws GizloException {		
		return tipoCatalogoDAO.obtenerBasico(codigo, nombre);
	}


	@Override
	public List<TipoCatalogo> listTipoCatalogoAll() throws GizloException {		
		return tipoCatalogoDAO.findAll();
	}




	
	

}
