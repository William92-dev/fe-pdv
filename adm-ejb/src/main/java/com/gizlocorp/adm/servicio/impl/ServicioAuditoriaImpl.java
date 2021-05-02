package com.gizlocorp.adm.servicio.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.gizlocorp.adm.auditoria.GizloAudData;
import com.gizlocorp.adm.dao.UsuarioRolDAO;
import com.gizlocorp.adm.enumeracion.Entidad;
import com.gizlocorp.adm.enumeracion.Operacion;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.servicio.local.ServicioAuditoriaLocal;
//import org.hibernate.envers.AuditReader;
//import org.hibernate.envers.AuditReaderFactory;
//import org.hibernate.envers.RevisionType;
//import org.hibernate.envers.query.AuditEntity;
//import org.hibernate.envers.query.AuditQuery;

@Stateless
public class ServicioAuditoriaImpl implements ServicioAuditoriaLocal {

	// private static Logger log = Logger.getLogger(ServicioAuditoriaImpl.class
	// .getName());

	@PersistenceContext
	protected EntityManager em;

	@EJB
	UsuarioRolDAO usuarioRoleDAO;

	// @SuppressWarnings("unchecked")
	public List<GizloAudData> obtenerInforAuditoria(Entidad entidad,
			Date fechaDesde, Date fechaHasta, Operacion operación)
			throws GizloException {

		return null;
		// try {
		// AuditReader reader = AuditReaderFactory.get(em);
		// AuditQuery query = null;
		// List<Object[]> results = null;
		// List<GizloAudData> resultado = null;
		//
		//
		// if (entidad != null) {
		// query = reader.createQuery().forRevisionsOfEntity(
		// entidad.getClazz(), false, true);
		// }
		// if (query != null) {
		// if (fechaDesde != null) {
		// query.add(AuditEntity.revisionProperty("timestamp").gt(
		// fechaDesde.getTime()));
		//
		// }
		// if (fechaHasta != null) {
		// query.add(AuditEntity.revisionProperty("timestamp").lt(
		// fechaHasta.getTime()));
		//
		// }
		//
		// if (operación != null) {
		// switch (operación) {
		// case ADD:
		// query.add(AuditEntity.revisionType().eq(
		// RevisionType.ADD));
		// break;
		// case MOD:
		// query.add(AuditEntity.revisionType().eq(
		// RevisionType.MOD));
		// break;
		// case DEL:
		// query.add(AuditEntity.revisionType().eq(
		// RevisionType.DEL));
		// break;
		// default:
		// break;
		// }
		// }
		//
		// query.addOrder(AuditEntity.revisionProperty("timestamp").asc());
		// results = query.getResultList();
		//
		// if (results != null && !results.isEmpty()) {
		// resultado = new ArrayList<GizloAudData>();
		// GizloAudData data = null;
		// GizloRevEntity dataAud = null;
		// RevisionType revisionType = null;
		//
		// for (Object[] o : results) {
		// data = new GizloAudData();
		// data.setEntidad(entidad.getDescripcion());
		// // dataAud = (GizloRevEntity) o[0];
		// dataAud = (GizloRevEntity) o[1];
		// revisionType = (RevisionType) o[2];
		// data.setFecha(new Date(dataAud.getTimestamp()));
		// data.setUsuario(dataAud.getUsuario());
		//
		// switch (revisionType) {
		// case ADD:
		// data.setOperacion("Ingresar");
		// break;
		// case MOD:
		// data.setOperacion("Modificar");
		// break;
		// case DEL:
		// data.setOperacion("Eliminar");
		// break;
		// default:
		// break;
		// }
		//
		// try {
		// data.setData((entidad.getClazz().cast(o[0]))
		// .toString());
		//
		// } catch (Exception e) {
		// log.error("No existe informacion en Auditoria ", e);
		// }
		// resultado.add(data);
		// }
		// }
		//
		// }
		// return resultado;
		//
		// } catch (Exception ex) {
		// log.error(ex);
		// throw new GizloException(ex);
		// }

	}
}