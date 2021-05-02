package com.gizlocorp.adm.servicio.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.ServicioSistemaExternoDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.SistemaExterno;
import com.gizlocorp.adm.servicio.local.ServicioSistemaExternoLocal;


@Stateless
public class ServicioSistemaExternoImpl implements ServicioSistemaExternoLocal{

	@EJB
	ServicioSistemaExternoDAO servicioSistemaExternoDAO;
	
	
	@Override
	public SistemaExterno guardar(SistemaExterno sistemaExterno) throws GizloException {
		// TODO Auto-generated method stub
		
		if(sistemaExterno.getId() != null){
			try {
				sistemaExterno = servicioSistemaExternoDAO.update(sistemaExterno);
			} catch (GizloUpdateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				sistemaExterno = servicioSistemaExternoDAO.persist(sistemaExterno);
			} catch (GizloPersistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sistemaExterno;
		
	}


	@Override
	public SistemaExterno obtenerUsuarioSistemaExt(Long id) throws GizloException{
		// TODO Auto-generated method stub
		try {
			return servicioSistemaExternoDAO.obtenerUsuarioSistemaExt(id);
		} catch (GizloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
