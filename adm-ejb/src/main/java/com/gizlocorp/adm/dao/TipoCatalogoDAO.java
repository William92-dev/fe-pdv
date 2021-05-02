package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.modelo.TipoCatalogo;


@Local
public interface TipoCatalogoDAO extends GenericDAO<TipoCatalogo, String>{
	
	List<TipoCatalogo> obtenerBasico(String codigo, String nombre);

}
