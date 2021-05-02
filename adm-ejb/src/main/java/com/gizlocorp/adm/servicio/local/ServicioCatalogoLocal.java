package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Catalogo;


@Local
public interface ServicioCatalogoLocal {
	
	void guardarCatalogo(Catalogo catalogo) throws GizloException;
	
	void eliminarCatalogo(Catalogo catalogo) throws GizloDeleteException;
	
	List<Catalogo> listObtenerPorParametros(String codigo, String nombre) throws GizloException;
	
	List<Catalogo> listCatalogoPorTipoCatalogo(String nombretipoCatalogo, String coigoTipoCatalogo) throws GizloException;
	
	Catalogo obtenerPorId(Long idCatalogo) throws GizloException;
	
	
	

}
