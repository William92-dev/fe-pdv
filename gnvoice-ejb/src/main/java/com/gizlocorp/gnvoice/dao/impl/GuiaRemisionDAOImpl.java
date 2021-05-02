/*
 * OfertaDao.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.gnvoice.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.GuiaRemisionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.GuiaRemision;

/**
 * Clase
 * 
 * @author gizlo
 * @revision $Revision: 1.7 $
 */
@Stateless
public class GuiaRemisionDAOImpl extends GenericJpaDAO<GuiaRemision, Long>
		implements GuiaRemisionDAO {

	@SuppressWarnings("unchecked")
	public GuiaRemision obtenerComprobante(String claveAcceso,
			String claveInterna, String ruc, String agencia) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select g from GuiaRemision g where 1=1 ");

		if (ruc != null && !ruc.isEmpty()) {
			sql.append("and g.ruc = :ruc ");
			mapa.put("ruc", ruc);
		}

		if (claveAcceso != null && !claveAcceso.isEmpty()) {
			sql.append("and g.claveAcceso = :claveAcceso ");
			mapa.put("claveAcceso", claveAcceso);
		}

		if (claveInterna != null && !claveInterna.isEmpty()) {
			sql.append("and g.claveInterna = :claveInterna ");
			mapa.put("claveInterna", claveInterna);
		}

		if (agencia != null && !agencia.isEmpty()) {
			sql.append("and g.agencia = :agencia ");
			mapa.put("agencia", agencia);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<GuiaRemision> result = q.getResultList();
		return (result != null && !result.isEmpty()) ? (GuiaRemision) result
				.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<GuiaRemision> obtenerGuiaRemisionPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado, String agencia,
			String claveContingencia, String tipoEmision, String tipoAmbiente,
			String correoProveedor, TipoEjecucion tipoEjecucion)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from GuiaRemision t where t.tipoGeneracion = :tipoGeneracion");

			if (estado != null && !estado.isEmpty()) {
				hql.append(" and  t.estado = :estado");
			}

			if (rucComprador != null && !rucComprador.isEmpty()) {
				hql.append(" and  t.rucTransportista = :rucComprador");
			}

			if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
				hql.append(" and t.codSecuencial like :codSecuencial");
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				hql.append(" and t.claveInterna like :claveInterna");
			}

			if (usuario != null && !usuario.isEmpty()) {
				hql.append(" and (t.identificadorUsuario = :usuario or t.identificadorUsuario is null)");
			}

			if (numeroRelacionado != null && !numeroRelacionado.isEmpty()) {
				hql.append(" and t.documentosRelaciondos like :numeroRelacionado");
			}

			if (agencia != null && !agencia.isEmpty()) {
				// hql.append(" and (t.agencia = :agencia or t.agencia is null)");
				hql.append(" and (t.agencia = :agencia)");
			}

			/*
			 * if (fecha != null) {
			 * hql.append(" and t.fechaIniTransporteDb = :fechaEmisionBase"); }
			 */
			if (desde != null) {
				hql.append(" and t.fechaIniTransporteDb >= :fechaDesde");
			}
			if (hasta != null) {
				hql.append(" and t.fechaIniTransporteDb <= :fechaHasta");
			}

			if (rucEmisor != null && !rucEmisor.isEmpty()) {
				hql.append(" and  t.ruc = :rucEmisor");
			}

			if (claveContingencia != null && !claveContingencia.isEmpty()) {
				hql.append(" and  t.claveAcceso like :claveContingencia and t.tipoEmision = :contigencia");
			}

			if (tipoEmision != null && !tipoEmision.isEmpty()) {
				hql.append(" and  t.tipoEmision = :tipoEmision");
			}

			if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
				hql.append(" and  t.tipoAmbiente = :tipoAmbiente");
			}

			if (correoProveedor != null && !correoProveedor.isEmpty()) {
				hql.append(" and  t.correoNotificacion like :correoProveedor");
			}

			if (tipoEjecucion != null) {
				hql.append(" and  t.tipoEjecucion = :tipoEjecucion");
			}

			hql.append(" order by t.codSecuencial desc,t.fechaIniTransporteDb desc");

			Query q = em.createQuery(hql.toString());
			q.setParameter("tipoGeneracion", tipoGeneracion);

			if (estado != null && !estado.isEmpty()) {
				q.setParameter("estado", estado);
			}

			if (rucComprador != null && !rucComprador.isEmpty()) {
				q.setParameter("rucComprador", rucComprador);
			}

			if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
				q.setParameter("codSecuencial", numeroComprobante);
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				q.setParameter("claveInterna", codigoExterno);
			}

			if (usuario != null && !usuario.isEmpty()) {
				q.setParameter("usuario", usuario);
			}

			if (numeroRelacionado != null && !numeroRelacionado.isEmpty()) {
				if (numeroRelacionado.contains("%")) {
					q.setParameter("numeroRelacionado", numeroRelacionado);

				} else {
					q.setParameter("numeroRelacionado", "%" + numeroRelacionado
							+ "%");

				}
			}

			if (agencia != null && !agencia.isEmpty()) {
				q.setParameter("agencia", agencia);
			}
			/*
			 * if (fecha != null) { q.setParameter("fechaEmisionBase", fecha); }
			 */
			if (desde != null) {
				q.setParameter("fechaDesde", desde);
			}
			if (hasta != null) {
				q.setParameter("fechaHasta", hasta);
			}

			if (rucEmisor != null && !rucEmisor.isEmpty()) {
				q.setParameter("rucEmisor", rucEmisor);
			}

			if (claveContingencia != null && !claveContingencia.isEmpty()) {
				q.setParameter("claveContingencia", claveContingencia);
				q.setParameter("contigencia",
						TipoEmisionEnum.CONTINGENCIA.getCodigo());
			}

			if (tipoEmision != null && !tipoEmision.isEmpty()) {
				q.setParameter("tipoEmision", tipoEmision);
			}

			if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
				q.setParameter("tipoAmbiente", tipoAmbiente);
			}

			if (correoProveedor != null && !correoProveedor.isEmpty()) {
				q.setParameter("correoProveedor", correoProveedor);
			}

			if (tipoEjecucion != null) {
				q.setParameter("tipoEjecucion", tipoEjecucion);
			}

			List<GuiaRemision> resultado = q.getResultList();

			// if (resultado != null && !resultado.isEmpty() && seguimiento) {
			// StringBuilder hqls = new StringBuilder(
			// "select t from SeguimientoGuiaRemision t where t.guiaRemision.claveAcceso = :claveAcceso");
			//
			// for (GuiaRemision guiaRemision : resultado) {
			// Query qs = em.createQuery(hqls.toString());
			// qs.setParameter("claveAcceso",
			// guiaRemision.getClaveAcceso());
			//
			// guiaRemision.setSeguimiento(qs.getResultList());
			//
			// }
			// }

			// log.debug("GENERANDO QUERY LISTAR EVALUACIONES " + resultado);
			return resultado;
		} catch (Exception e) {
			// log.error("Error consultarEvaluaciones:" + e.getMessage());
			throw new GizloException(
					"Error al listar las guiaRemision (buscar guiaRemision)", e);
		}
	}

	@Override
	public List<GuiaRemision> obtenerComprobantesReproceso(String ruc)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from GuiaRemision t where t.estado = :estado and t.tareaActual in (:enviado,:contigencia,:autorizar) and t.tipoGeneracion = :tipoGeneracion and t.ruc = :ruc ");
			Query q = em.createQuery(hql.toString());
			q.setParameter("estado", Estado.PENDIENTE.getDescripcion());
			q.setParameter("enviado", Tarea.ENV);
			q.setParameter("contigencia", Tarea.CON);
			q.setParameter("autorizar", Tarea.AUT);
			q.setParameter("tipoGeneracion", TipoGeneracion.EMI);
			q.setParameter("ruc", ruc);

			@SuppressWarnings("unchecked")
			List<GuiaRemision> resultado = q.getResultList();
			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las guiaRemision (buscar guiaRemision)", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GuiaRemision> obtenerGuiaRemision(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from GuiaRemision t where 1=1");

			if (estado != null && !estado.isEmpty()) {
				hql.append(" and t.estado = :estado");
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				hql.append(" and t.codigoExterno like :codigoExterno");
			}

			if (secuencial != null && !secuencial.isEmpty()) {
				hql.append(" and t.codSecuencial like :codSecuencial");
			}

			if (claveAcceso != null && !claveAcceso.isEmpty()) {
				hql.append(" and t.claveAcceso like :claveAcceso");
			}
			if (ruc != null && !ruc.isEmpty()) {
				hql.append(" and t.ruc = :ruc");
			}

			Query q = em.createQuery(hql.toString());

			if (estado != null && !estado.isEmpty()) {
				q.setParameter("estado", estado);
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				q.setParameter("codigoExterno", codigoExterno);
			}

			if (secuencial != null && !secuencial.isEmpty()) {
				q.setParameter("codSecuencial", secuencial);
			}

			if (claveAcceso != null && !claveAcceso.isEmpty()) {
				q.setParameter("claveAcceso", claveAcceso);
			}

			if (ruc != null && !ruc.isEmpty()) {
				q.setParameter("ruc", ruc);
			}

			List<GuiaRemision> resultado = q.getResultList();

			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las guiaRemision (buscar guiaRemision)", e);
		}
	}

	@Override
	public GuiaRemision obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			sql.append("select f from GuiaRemision f where 1 = 1 ");

			if (claveAcceso != null && !claveAcceso.isEmpty()) {
				sql.append("and f.claveAcceso = :claveAcceso ");
				mapa.put("claveAcceso", claveAcceso);
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				sql.append("and f.codigoExterno = :codigoExterno ");
				mapa.put("codigoExterno", codigoExterno);
			}

			Query q = em.createQuery(sql.toString());
			for (String key : mapa.keySet()) {
				q.setParameter(key, mapa.get(key));
			}

			return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (GuiaRemision) q
					.getResultList().get(0) : null;
		} catch (Exception e) {
			throw new GizloException("Error al buscar guiaRemision", e);
		}
	}

	@Override
	public List<GuiaRemision> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from GuiaRemision t where t.estado = :estado and t.tareaActual in (:lote) and t.tipoGeneracion = :tipoGeneracion and t.ruc = :ruc ");
			Query q = em.createQuery(hql.toString());
			q.setParameter("estado", Estado.PENDIENTE.getDescripcion());
			q.setParameter("lote", Tarea.ASL);
			q.setParameter("tipoGeneracion", TipoGeneracion.EMI);
			q.setParameter("ruc", ruc);

			@SuppressWarnings("unchecked")
			List<GuiaRemision> resultado = q.getResultList();
			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las guiaRemision (buscar guiaRemision)", e);
		}
	}
}
