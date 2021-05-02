package com.gizlocorp.gnvoice.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
//import com.gizlocorp.adm.servicio.local.ServicioAuditoriaLocal;
import com.gizlocorp.gnvoice.servicio.local.ServicioColaFileClave;
import com.gizlocorp.gnvoice.dao.ClaveContingenciaDAO;
import com.gizlocorp.gnvoice.dao.ColaFileClaveDAO;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
import com.gizlocorp.gnvoice.modelo.ColaFileClave;

@Stateless
public class ServicioColaFileClaveImpl implements ServicioColaFileClave {
	
	public static final Logger log = Logger
			.getLogger(ServicioColaFileClaveImpl.class);
	
	
	@EJB
	ColaFileClaveDAO colaFileClaveDAO;

	
	
	public ColaFileClave saveColaFileClave(ColaFileClave entity) throws GizloException {
		try {
			System.out.println("LLEGO AL SERVICE");
			return colaFileClaveDAO.persist(entity);
		} catch (GizloPersistException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("SE CAYO WI!!!"); 
		}
		return null; 
	}
	
	

	public List<ColaFileClave> listarColasFiles()
			throws GizloException {
		List<ColaFileClave> colasCargadas = colaFileClaveDAO.listaColaCargada(); 
return colasCargadas;
	}
	
	

}
