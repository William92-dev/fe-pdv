package com.gizlocorp.adm.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.TipoCatalogo;


@Local
public interface ServicioTipoCatalogoLocal {
	
	void guardarTipoCatalogo(TipoCatalogo tipoCatalogo) throws GizloException;
	
	void eliminarTipoCatalogo(TipoCatalogo tipoCatalogo) throws GizloDeleteException;
	
	List<TipoCatalogo> listObtenerPorParametros(String codigo, String nombre) throws GizloException;
	
	List<TipoCatalogo> listTipoCatalogoAll() throws GizloException;
	
	
	
	

}
