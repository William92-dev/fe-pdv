package com.gizlocorp.adm.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.modelo.UbicacionGeografica;


@Local
public interface UbicacionGeograficaDAO extends GenericDAO<UbicacionGeografica, String>{
	
	List<UbicacionGeografica> obtenerBasico(String codigo, String nombre);

}
