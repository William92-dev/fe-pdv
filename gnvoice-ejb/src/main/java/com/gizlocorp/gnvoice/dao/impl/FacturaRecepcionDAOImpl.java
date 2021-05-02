/*
 * OfertaDao.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.gnvoice.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.AccessTimeout;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.FacturaRecepcionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoAmbienteEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;

/**
 * Clase
 * 
 * @author gizlo
 * @revision $Revision: 1.7 $
 */
@Stateless
@AccessTimeout(value = 750, unit = TimeUnit.MINUTES)
public class FacturaRecepcionDAOImpl extends GenericJpaDAO<FacturaRecepcion, Long> implements
		FacturaRecepcionDAO {

	
	
	@Override
	public FacturaRecepcion obtenerComprobanteRecepcion(String claveAcceso, String claveInterna,
			String ruc, String agencia) {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select f from FacturaRecepcion f where 1 = 1  ");

		if (ruc != null && !ruc.isEmpty()) {
			sql.append("and f.ruc = :ruc ");
			mapa.put("ruc", ruc);
		}

		if (claveAcceso != null && !claveAcceso.isEmpty()) {
			sql.append("and f.claveAcceso = :claveAcceso ");
			mapa.put("claveAcceso", claveAcceso);
		}

		if (claveInterna != null && !claveInterna.isEmpty()) {
			sql.append("and f.claveInterna = :claveInterna ");
			mapa.put("claveInterna", claveInterna);
		}

		if (agencia != null && !agencia.isEmpty()) {
			sql.append("and f.agencia = :agencia ");
			mapa.put("agencia", agencia);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (FacturaRecepcion) q
				.getResultList().get(0) : null;
	}
	
	
	@Override
	public List<FacturaRecepcion> obtenerComprobanteParaTraduccion(int min, int max) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select f from FacturaRecepcion f where   1 = 1 ");
		sql.append(" and f.ordenCompra is not null and f.estado = 'PENDIENTE' ");
		
		/*sql.append(" and clave_acceso in ( '1806202001099128844900120010080000249325374286117',");
		sql.append("'2705202001179212251100120011010002767230039810918', ");
		sql.append("'2905202001179212251100120011010002770200039863618', ");
		sql.append("'2704202001019014739800120011020000296481864253716', ");
		sql.append("'2204202001179031985700120021450031161784784869518', ");
		sql.append("'1205202001179000573900120270100044131860441318613', ");
		sql.append("'0105202001179141109900121100360030203980302039814', ");
		sql.append("'0406202001179031985700121401070032552806152758319', ");
		sql.append("'0305202001099000677600120080010018410950184109515', ");
		sql.append("'0405202001179000573900120060100040800810408008111', ");
		sql.append("'2804202001179000573900120270100043780010437800112', ");
		sql.append("'2804202001179212251100120011010002741380039301810' )");*/
		
	
		Query q = em.createQuery(sql.toString());		
		//q.setFirstResult(min);
		//q.setMaxResults(max);
		
		@SuppressWarnings("unchecked")
		List<FacturaRecepcion> resultado = q.getResultList();
		return (resultado != null && !resultado.isEmpty()) ? resultado : null;
	}

	@Override
	public FacturaRecepcion obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException {
		try {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Object> mapa = new HashMap<String, Object>();
			sql.append("select f from Factura f where 1 = 1 ");

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

			return (q.getResultList() != null && !q.getResultList().isEmpty()) ? (FacturaRecepcion) q
					.getResultList().get(0) : null;
		} catch (Exception e) {
			throw new GizloException("Error al buscar factura", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<FacturaRecepcion> obtenerFacturaPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from FacturaRecepcion t where t.tipoGeneracion = :tipoGeneracion");

			if (estado != null && !estado.isEmpty()) {
				hql.append(" and  t.estado = :estado");
			}

			if (rucComprador != null && !rucComprador.isEmpty()) {
				hql.append(" and  t.identificacionComprador = :rucComprador");
			}

			if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
				hql.append(" and t.codSecuencial = :codSecuencial");
			}

			if (codigoExterno != null && !codigoExterno.isEmpty()) {
				hql.append(" and t.claveInterna = :claveInterna");
			}

			if (usuario != null && !usuario.isEmpty()) {
				hql.append(" and (t.identificadorUsuario = :usuario or t.identificadorUsuario is null)");
			}

			if (agencia != null && !agencia.isEmpty()) {
				// hql.append(" and (t.agencia = :agencia or t.agencia is null)");
				hql.append(" and (t.agencia = :agencia)");
			}
			/*
			 * if (fecha != null) {
			 * hql.append(" and t.fechaEmisionBase = :fechaEmisionBase"); }
			 */
			if (desde != null) {
				hql.append(" and t.fechaEmisionBase >= :fechaDesde");
			}
			if (hasta != null) {
				hql.append(" and t.fechaEmisionBase <= :fechaHasta");
			}

			if (rucEmisor != null && !rucEmisor.isEmpty()) {
				hql.append(" and  t.ruc = :rucEmisor");
			}

			if (claveContingencia != null && !claveContingencia.isEmpty()) {
				hql.append(" and  t.claveAcceso = :claveContingencia and t.tipoEmision = :contigencia");
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

			hql.append(" order by t.codSecuencial desc,t.fechaEmisionBase desc");

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

			List<FacturaRecepcion> resultado = q.getResultList();

			// if (resultado != null && !resultado.isEmpty() && seguimiento) {
			// StringBuilder hqls = new StringBuilder(
			// "select t from SeguimientoFactura t where t.factura.claveAcceso = :claveAcceso");
			//
			// for (Factura factura : resultado) {
			// Query qs = em.createQuery(hqls.toString());
			// qs.setParameter("claveAcceso", factura.getClaveAcceso());
			//
			// factura.setSeguimiento(qs.getResultList());
			//
			// }
			// }

			// log.debug("GENERANDO QUERY LISTAR EVALUACIONES " + resultado);
			return resultado;
		} catch (Exception e) {
			// log.error("Error consultarEvaluaciones:" + e.getMessage());
			throw new GizloException(
					"Error al listar las factura (buscar factura)", e);
		}
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobantesReproceso(String ruc)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from Factura t where t.estado = :estado and t.tareaActual in (:enviado,:contigencia,:autorizar) and t.tipoGeneracion = :tipoGeneracion and t.ruc = :ruc ");
			Query q = em.createQuery(hql.toString());
			q.setParameter("estado", Estado.PENDIENTE.getDescripcion());
			q.setParameter("enviado", Tarea.ENV);
			q.setParameter("contigencia", Tarea.CON);
			q.setParameter("autorizar", Tarea.AUT);
			q.setParameter("tipoGeneracion", TipoGeneracion.EMI);
			q.setParameter("ruc", ruc);

			@SuppressWarnings("unchecked")
			List<FacturaRecepcion> resultado = q.getResultList();
			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las factura (buscar factura)", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacturaRecepcion> obtenerFactura(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from Factura t where 1 = 1");

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

			hql.append(" order by t.codSecuencial,t.fechaEmisionBase");

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

			List<FacturaRecepcion> resultado = q.getResultList();

			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las factura (buscar factura)", e);
		}
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException {
		try {
			StringBuilder hql = new StringBuilder(
					"select t from Factura t where t.estado = :estado and t.tareaActual in (:lote) and t.tipoGeneracion = :tipoGeneracion and t.ruc = :ruc ");
			Query q = em.createQuery(hql.toString());
			q.setParameter("estado", Estado.PENDIENTE.getDescripcion());
			q.setParameter("lote", Tarea.ASL);
			q.setParameter("tipoGeneracion", TipoGeneracion.EMI);
			q.setParameter("ruc", ruc);

			@SuppressWarnings("unchecked")
			List<FacturaRecepcion> resultado = q.getResultList();
			return resultado;
		} catch (Exception e) {
			throw new GizloException(
					"Error al listar las factura (buscar factura)", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacturaRecepcion> obtenerComprobantesConciliacion(String agencia)
			throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();

		sql.append("select f from Factura f where f.agencia = :agencia and f.requiereCancelacion is null and tipoAmbiente = :tipoAmbiente and f.secuencialOriginal is not null");
		mapa.put("agencia", agencia);
		mapa.put("tipoAmbiente", TipoAmbienteEnum.PRODUCCION.getCode());

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<FacturaRecepcion> resultado = q.getResultList();
		return resultado;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacturaRecepcion> obtenerRequireCancelacion(String agencia)
			throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();

		sql.append("select f from Factura f where f.agencia = :agencia and f.requiereCancelacion = :requiereCancelacion and f.estado != :estado and tipoAmbiente = :tipoAmbiente");
		mapa.put("agencia", agencia);
		mapa.put("requiereCancelacion", Logico.S);
		mapa.put("estado", Estado.CANCELADO.getDescripcion());
		mapa.put("tipoAmbiente", TipoAmbienteEnum.PRODUCCION.getCode());

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<FacturaRecepcion> resultado = q.getResultList();
		return resultado;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacturaRecepcion> obtenerComprobantesResagados() throws GizloException {
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select f from Factura f where f.proceso = :proceso and f.ordenCompra is not null and f.ordenCompra != :ordenCompra and estado = :estado");
//				+ "and t.fechaEmisionBase >= :fechaDesde"
//				+ "and t.fechaEmisionBase <= :fechaHasta");
		
		
		mapa.put("proceso", "REIM");
		mapa.put("ordenCompra", "0");
		mapa.put("estado", "ERROR");
//		mapa.put("fechaDesde", desde);
//		mapa.put("fechaHasta", hasta);
//		


		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}

		List<FacturaRecepcion> resultado = q.getResultList();
		return resultado;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacturaRecepcion> obtenerComprobante(String ruc, String claveAcceso,
			String codSecuencial, String claveInterna, String estado,
			Integer pagina, Integer tamanoPagina) {

		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("select f from Factura f where 1 = 1 ");

		if (ruc != null && !ruc.isEmpty()) {
			sql.append("and f.ruc = :ruc ");
			mapa.put("ruc", ruc);
		}

		if (claveAcceso != null && !claveAcceso.isEmpty()) {
			sql.append("and f.claveAcceso = :claveAcceso ");
			mapa.put("claveAcceso", claveAcceso);
		}
		
		if (codSecuencial != null && !codSecuencial.isEmpty()) {
			sql.append("and f.codSecuencial = :codSecuencial ");
			mapa.put("codSecuencial", codSecuencial);
		}

		if (claveInterna != null && !claveInterna.isEmpty()) {
			sql.append("and f.claveInterna = :claveInterna ");
			mapa.put("claveInterna", claveInterna);
		}

		if (estado != null && !estado.isEmpty()) {
			sql.append("and f.estado = :estado ");
			mapa.put("estado", estado);
		}

		Query q = em.createQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}
		List<FacturaRecepcion> lista = q.getResultList();
		System.out.println("R://Lista Consulta: " + lista==null?" 0 items ":lista.size() );
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> listaProveedores() throws GizloException {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct IDENTIFICACION_COMPRADOR from tb_factura_recepcion ");
		Query q = em.createNativeQuery(sql.toString());
		List<String> result = q.getResultList();
		
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void eliminarFacturas(String claveAcceso) throws GizloException {
		
		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		sql.append("delete from tb_factura_recepcion where clave_acceso =:claveAcceso ");
		mapa.put("claveAcceso", claveAcceso);
		Query q = em.createNativeQuery(sql.toString());
		for (String key : mapa.keySet()) {
			q.setParameter(key, mapa.get(key));
		}
		q.executeUpdate();
		
		
	}

	
}
