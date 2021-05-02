package com.gizlocorp.gnvoice.servicio.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.NotaCreditoRecepcionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;
import com.gizlocorp.gnvoice.servicio.local.ServicioNotaCreditoRecepcion;

@Stateless
public class ServicioRecepcionNotaCreditoImpl implements ServicioNotaCreditoRecepcion {

	public static final Logger log = Logger
			.getLogger(ServicioRecepcionNotaCreditoImpl.class);

	@EJB
	NotaCreditoRecepcionDAO notaCreditoDAO;

	@Override
	public NotaCreditoRecepcion obtenerComprobante(String claveAcceso,
			String claveInterna, String ruc, String agencia) {
		return notaCreditoDAO.obtenerComprobante(claveAcceso, claveInterna,
				ruc, agencia);
	}
	
	
	@Override
	public NotaCreditoRecepcion obtenerComprobanteRecepcion(String claveAcceso,
			String claveInterna, String ruc, String agencia) {
		return notaCreditoDAO.obtenerComprobanteRecepcion(claveAcceso, claveInterna,
				ruc, agencia);
	}

	public NotaCreditoRecepcion obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException {
		return notaCreditoDAO.obtenerComprobanteCancelar(claveAcceso,
				codigoExterno);
	}

	@Override
	public NotaCreditoRecepcion autoriza(NotaCreditoRecepcion comprobante) {
		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = notaCreditoDAO.update(comprobante);

			} else {
				comprobante = notaCreditoDAO.persist(comprobante);

			}

		} catch (Exception ex) {
			log.error("Error factura", ex);
		}
		return comprobante;
	}

	@Override
	public List<NotaCreditoRecepcion> consultarCommprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException {
		return notaCreditoDAO.obtenerNotaCreditoPorParametros("AUTORIZADO",
				numeroComprobante, desde, hasta, TipoGeneracion.EMI, null,
				false, rucComprador, codigoExterno, usuario, numeroRelacionado,
				null, null, null, null, null, null);
	}

	@Override
	public List<NotaCreditoRecepcion> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String numeroRelacionado, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException {
		/*
		 * return notaCreditoDAO.obtenerNotaCreditoPorParametros(estado,
		 * numeroComprobante, fecha, TipoGeneracion.EMI, true);
		 */
		return notaCreditoDAO.obtenerNotaCreditoPorParametros(estado,
				numeroComprobante, desde, hasta, TipoGeneracion.EMI, rucEmisor,
				false, rucComprador, codigoExterno, usuario, numeroRelacionado,
				agencia, claveContingencia, tipoEmision, tipoAmbiente,
				correoProveedor, tipoEjecucion);
	}

	@Override
	public List<NotaCreditoRecepcion> obtenerComprobantesReproceso(String ruc)
			throws GizloException {
		return notaCreditoDAO.obtenerComprobantesReproceso(ruc);
	}
	
	

	@Override
	public List<NotaCreditoRecepcion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException {
		return notaCreditoDAO.obtenerComprobantesProcesoLote(ruc);
	}

	@Override
	public List<NotaCreditoRecepcion> obtenerComprobanteConsulta(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException {
		return notaCreditoDAO.obtenerNotaCredito(ruc, claveAcceso, secuencial,
				codigoExterno, estado);
	}

	@Override
	public NotaCreditoRecepcion recibirNotaCredito(NotaCreditoRecepcion comprobante,
			String observacion) throws GizloException {
		try {
			comprobante.setTipoGeneracion(TipoGeneracion.REC);
			comprobante.setTareaActual(Tarea.REP);

			if (comprobante.getId() != null) {
				comprobante = notaCreditoDAO.update(comprobante);
			} else {
				comprobante = notaCreditoDAO.persist(comprobante);
			}

			return comprobante;

		} catch (Exception ex) {
			log.error("Error factura", ex);
			throw new GizloException(ex);
		}
	}

	@Override
	public List<NotaCreditoRecepcion> consultarCommprobantesRecibidos(
			String numeroComprobante, Date desde, Date hasta,
			String rucComprador) throws GizloException {
		return notaCreditoDAO.obtenerNotaCreditoPorParametros(null,
				numeroComprobante, desde, hasta, TipoGeneracion.REC, null,
				false, rucComprador, null, null, null, null, null, null, null,
				null, null);
	}

	@Override
	public List<NotaCreditoRecepcion> consultarCommprobantesAtraducir(int min, int max) {
		return notaCreditoDAO.consultarCommprobantesAtraducir(min, max);
	}
}
