package com.gizlocorp.gnvoice.dao;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
//import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
import com.gizlocorp.gnvoice.modelo.ColaFileClave;

@Local
public interface ColaFileClaveDAO extends
			GenericDAO<ColaFileClave, Long> { 
	
	
	//@Override
	//ColaFileClave persist(ColaFileClave entity) throws GizloPersistException;
	
	void persistCola(ColaFileClave entity); 
	
	List<ColaFileClave> listaColaCargada() throws GizloException;
	

}
