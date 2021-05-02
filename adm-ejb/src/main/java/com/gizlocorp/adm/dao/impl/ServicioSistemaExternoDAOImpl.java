package com.gizlocorp.adm.dao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.dao.ServicioSistemaExternoDAO;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.SistemaExterno;


@Stateless
public class ServicioSistemaExternoDAOImpl extends GenericJpaDAO<SistemaExterno, String> implements ServicioSistemaExternoDAO{

	@Override
	public SistemaExterno obtenerUsuarioSistemaExt(Long id) throws GizloException{
		Query q = em
				.createQuery("select c from SistemaExterno c where c.id = :id ");
		q.setParameter("id", id);

		
		@SuppressWarnings("unchecked")
		List<SistemaExterno> result = q.getResultList();
		return result != null && !result.isEmpty() ? result.get(0) : null;
	}

	

}
