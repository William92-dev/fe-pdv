package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Catalogo;


@Local
public interface CatalogoDAO extends GenericDAO<Catalogo, String>{
	
	
	List<Catalogo> listObtenerPorParametros(String codigo, String nombre) throws GizloException;
	
	List<Catalogo> listCatalogoPorTipoCatalogo(String nombretipoCatalogo, String coigoTipoCatalogo) throws GizloException;
	
	public Catalogo obtenerPorId(Long idCatalogo) throws GizloException;
	
	

}
