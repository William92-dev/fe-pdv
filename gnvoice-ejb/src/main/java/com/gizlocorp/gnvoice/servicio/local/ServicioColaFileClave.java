package com.gizlocorp.gnvoice.servicio.local;

import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.modelo.ColaFileClave;

@Local
public interface ServicioColaFileClave {
	
	ColaFileClave saveColaFileClave(ColaFileClave entity)throws GizloException;
	
	public List<ColaFileClave> listarColasFiles()
			throws GizloException; 

}
