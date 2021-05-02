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
import java.util.concurrent.TimeUnit;

import javax.ejb.AccessTimeout;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.RetencionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.Retencion;


/**
 * Clase
 * 
 * @author gizlo
 * @revision $Revision: 1.7 $
 */
@Stateless
@AccessTimeout(value = 750, unit = TimeUnit.MINUTES)
public class RetencionDAOImpl extends GenericJpaDAO<Retencion, Long> implements
		RetencionDAO {
	
	private static Logger log = Logger.getLogger(RetencionDAOImpl.class
			.getName());

	@SuppressWarnings("unchecked")
	public Retencion obtenerComprobante(String claveAcceso,
			String claveInterna, String ruc) {
		
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select c from Retencion c where 1=1 ");

		if (ruc != null && !ruc.isEmpty()) {
			sql.append("and c.ruc = :ruc ");
			mapa.put("ruc", ruc);
		}

		if (claveAcceso != null && !claveAcceso.isEmpty()) {
			sql.append("and c.claveAcceso = :claveAcceso ");
			mapa.put("claveAcceso", claveAcceso);
		}

		if (claveInterna != null && !claveInterna.isEmpty()) {
			sql.append("and c.claveInterna = :claveInterna ");
			mapa.put("claveInterna", claveInterna);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}
		

		List<Retencion> result = q.getResultList();

		return (result != null && !result.isEmpty()) ? (Retencion) result
				.get(0) : null;
	}

	

	@Override
	public List<Retencion> obtenerComprobantesReproceso(String ruc)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from Retencion t where t.estado = :estado and t.tareaActual = :contigencia and t.tipoGeneracion = :tipoGeneracion and t.ruc = :ruc ");
			Query q = em.createQuery(hql.toString());
			q.setParameter("estado", Estado.PENDIENTE.getDescripcion());
			q.setParameter("contigencia", Tarea.CON);
			q.setParameter("tipoGeneracion", TipoGeneracion.EMI);
			q.setParameter("ruc", ruc);

			@SuppressWarnings("unchecked")
			List<Retencion> resultado = q.getResultList();
			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las factura (buscar factura)", e);
		}
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public List<Retencion> obtenerRetencion(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from Retencion t where 1=1");

			if (estado != null && !estado.isEmpty()) {
				hql.append(" and t.estado = :estado");
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				hql.append(" and t.codigoExterno = :codigoExterno");
			}

			StringBuilder codSecuencial = new StringBuilder();

			if (secuencial != null && !secuencial.isEmpty()) {
				if (secuencial.length() > 9) {
					for (int j = 1; j <= (9 - secuencial.length()); j++) {
						codSecuencial.append("0");
					}
				}
				codSecuencial.append(secuencial);
				hql.append(" and t.codSecuencial = :codSecuencial");
			}

			if (claveAcceso != null && !claveAcceso.isEmpty()) {
				hql.append(" and t.claveAcceso = :claveAcceso");
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
				q.setParameter("codSecuencial", codSecuencial.toString());
			}

			if (claveAcceso != null && !claveAcceso.isEmpty()) {
				q.setParameter("claveAcceso", claveAcceso);
			}

			if (ruc != null && !ruc.isEmpty()) {
				q.setParameter("ruc", ruc);
			}

			List<Retencion> resultado = q.getResultList();

			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las retencion (buscar retencion)", e);
		}
	}

	@Override
	public Retencion obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			sql.append("select f from Retencion f where 1 = 1 ");

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

			return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (Retencion) q
					.getResultList().get(0) : null;
		} catch (Exception e) {
			throw new GizloException("Error al buscar retencion", e);
		}
	}

	@Override
	public List<Retencion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from Retencion t where t.estado = :estado and t.tareaActual in (:lote) and t.tipoGeneracion = :tipoGeneracion and t.ruc = :ruc ");
			Query q = em.createQuery(hql.toString());
			q.setParameter("estado", Estado.PENDIENTE.getDescripcion());
			q.setParameter("lote", Tarea.ASL);
			q.setParameter("tipoGeneracion", TipoGeneracion.EMI);
			q.setParameter("ruc", ruc);

			@SuppressWarnings("unchecked")
			List<Retencion> resultado = q.getResultList();
			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las factura (buscar factura)", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Retencion> obtenerRetencionPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado, String agencia,
			String claveContingencia, String tipoEmision, String tipoAmbiente,
			String correoProveedor, TipoEjecucion tipoEjecucion,
			Date desdeRecibido, Date hastaRecibido, String razonSocial)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from Retencion t where t.tipoGeneracion = :tipoGeneracion");

			if (estado != null && !estado.isEmpty()) {
				hql.append(" and  t.estado = :estado");
			}

			if (rucComprador != null && !rucComprador.isEmpty()) {
				hql.append(" and  t.identificacionSujetoRetenido = :rucComprador");
			}

			if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
				hql.append(" and t.codSecuencial like :codSecuencial");
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				hql.append(" and t.claveInterna like:claveInterna");
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

			if (razonSocial != null && !razonSocial.isEmpty()) {
				hql.append(" and  t.razonSocial like :razonSocial");
			}

			/*
			 * if (fecha != null) {
			 * hql.append(" and t.fechaEmisionDb = :fechaEmisionBase"); }
			 */
			if (desde != null) {
				hql.append(" and t.fechaEmisionDb >= :fechaDesde");
			}
			if (hasta != null) {
				hql.append(" and t.fechaEmisionDb <= :fechaHasta");
			}

			if (desdeRecibido != null) {
				hql.append(" and t.fechaCarga >= :fechaDesdeRecibido");
			}
			if (hastaRecibido != null) {
				hql.append(" and t.fechaCarga <= :fechaHastaRecibido");
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

			hql.append(" order by t.codSecuencial desc,t.fechaEmisionDb desc");

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

			if (razonSocial != null && !razonSocial.isEmpty()) {
				q.setParameter("razonSocial", razonSocial);
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

			if (desdeRecibido != null) {
				q.setParameter("fechaDesdeRecibido", desdeRecibido);
			}
			if (hastaRecibido != null) {
				q.setParameter("fechaHastaRecibido", hastaRecibido);
			}

			if (rucEmisor != null && !rucEmisor.isEmpty()) {
				q.setParameter("rucEmisor", rucEmisor);
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

			List<Retencion> resultado = q.getResultList();

			

			// log.debug("GENERANDO QUERY LISTAR EVALUACIONES " + resultado);
			return resultado;
		} catch (Exception e) {
			// log.error("Error consultarEvaluaciones:" + e.getMessage());
			throw new GizloException(
					"Error al listar las evaluaciones (buscar evaluaciones)", e);
		}
	}
}
