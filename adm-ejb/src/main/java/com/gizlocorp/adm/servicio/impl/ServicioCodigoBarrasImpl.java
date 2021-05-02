/**
 * 
 */
package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gizlocorp.adm.dao.ParametrizacionCodigoBarrasDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;
import com.gizlocorp.adm.modelo.ParametrizacionCodigoBarras;
import com.gizlocorp.adm.servicio.local.ServicioCodigoBarrasLocal;

/**
 * 
 * @author
 * @version
 */
@Stateless
public class ServicioCodigoBarrasImpl implements ServicioCodigoBarrasLocal {

	//private static Logger log = Logger.getLogger(ServicioCodigoBarrasImpl.class.getName());
	
	@EJB
	ParametrizacionCodigoBarrasDAO parametroDAO;
	
	@Override
	public List<ParametrizacionCodigoBarras> obtenerCodigoBarrasPorParametros(
			String codigoArticulo, String tipoArticulo, Estado estado, String ruc)
			throws GizloException {
		
		return parametroDAO.obtenerCodigoBarrasPorParametros(codigoArticulo, tipoArticulo, estado, ruc);
	}

	@Override
	public void guardar(ParametrizacionCodigoBarras codigoBarras) {
		if (codigoBarras.getId() == null) {
			try {
				parametroDAO.persist(codigoBarras);
			} catch (GizloPersistException e) {
				e.printStackTrace();
			}
		} else {
			try {
				parametroDAO.update(codigoBarras);
			} catch (GizloUpdateException e) {
				e.printStackTrace();
			}
		}
		
	}

	

}
