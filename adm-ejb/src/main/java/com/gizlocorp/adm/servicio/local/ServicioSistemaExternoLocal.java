package com.gizlocorp.adm.servicio.local;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.SistemaExterno;


@Local
public interface ServicioSistemaExternoLocal {
	SistemaExterno guardar(SistemaExterno sistemaExterno)throws GizloException;
	SistemaExterno obtenerUsuarioSistemaExt(Long id) throws GizloException;
}
