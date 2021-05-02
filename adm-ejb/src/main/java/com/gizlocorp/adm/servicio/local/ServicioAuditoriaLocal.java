package com.gizlocorp.adm.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.auditoria.GizloAudData;
import com.gizlocorp.adm.enumeracion.Entidad;
import com.gizlocorp.adm.enumeracion.Operacion;
import com.gizlocorp.adm.excepcion.GizloException;

@Local
public interface ServicioAuditoriaLocal {

	List<GizloAudData> obtenerInforAuditoria(Entidad entidad, Date fechaDesde,
			Date fechaHasta, Operacion operación) throws GizloException;

}
