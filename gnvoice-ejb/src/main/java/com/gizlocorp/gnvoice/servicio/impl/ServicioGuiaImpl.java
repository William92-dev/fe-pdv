package com.gizlocorp.gnvoice.servicio.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.GuiaRemisionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.GuiaRemision;
import com.gizlocorp.gnvoice.servicio.local.ServicioGuia;

@Stateless
public class ServicioGuiaImpl implements ServicioGuia {

	public static final Logger log = Logger.getLogger(ServicioGuiaImpl.class);
	@EJB
	GuiaRemisionDAO guiaRemisionDAO;

	public GuiaRemision obtenerComprobante(String claveAcceso,
			String claveInterna, String ruc, String agencia) {
		return guiaRemisionDAO.obtenerComprobante(claveAcceso, claveInterna,
				ruc, agencia);
	}

	public GuiaRemision obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException {
		return guiaRemisionDAO.obtenerComprobanteCancelar(claveAcceso,
				codigoExterno);
	}

	@Override
	public GuiaRemision autoriza(GuiaRemision comprobante) {
		try {

			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = guiaRemisionDAO.update(comprobante);

			} else {
				comprobante = guiaRemisionDAO.persist(comprobante);

			}

		} catch (Exception ex) {
			log.error("Error factura", ex);
		}
		return comprobante;
	}
	
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public GuiaRemision autorizaOffline(GuiaRemision comprobante) {
		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = guiaRemisionDAO.update(comprobante);

			} else {
				comprobante = guiaRemisionDAO.persist(comprobante);

			}

		} catch (Exception ex) {
			log.error("Error factura", ex);

		}
		return comprobante;
	}


	@Override
	public List<GuiaRemision> consultarCommprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException {
		/*
		 * return guiaRemisionDAO.obtenerGuiaRemisionPorParametros("AUTORIZADO",
		 * numeroComprobante, fecha, TipoGeneracion.EMI, false);
		 */
		return guiaRemisionDAO.obtenerGuiaRemisionPorParametros("AUTORIZADO",
				numeroComprobante, desde, hasta, TipoGeneracion.EMI, null,
				false, rucComprador, codigoExterno, usuario, numeroRelacionado,
				null, null, null, null, null, null);
	}

	@Override
	public List<GuiaRemision> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String numeroRelacionado, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException {
		/*
		 * return guiaRemisionDAO.obtenerGuiaRemisionPorParametros(estado,
		 * numeroComprobante, fecha, TipoGeneracion.EMI, true);
		 */
		return guiaRemisionDAO.obtenerGuiaRemisionPorParametros(estado,
				numeroComprobante, desde, hasta, TipoGeneracion.EMI, rucEmisor,
				false, rucComprador, codigoExterno, usuario, numeroRelacionado,
				agencia, claveContingencia, tipoEmision, tipoAmbiente,
				correoProveedor, tipoEjecucion);
	}

	@Override
	public List<GuiaRemision> obtenerComprobantesReproceso(String ruc)
			throws GizloException {
		return guiaRemisionDAO.obtenerComprobantesReproceso(ruc);
	}

	@Override
	public List<GuiaRemision> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException {
		return guiaRemisionDAO.obtenerComprobantesProcesoLote(ruc);
	}

	@Override
	public List<GuiaRemision> obtenerComprobanteConsulta(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException {
		return guiaRemisionDAO.obtenerGuiaRemision(ruc, claveAcceso,
				secuencial, codigoExterno, estado);
	}

	@Override
	public GuiaRemision recibirGuiaRemision(GuiaRemision comprobante)
			throws GizloException {
		try {
			comprobante.setTipoGeneracion(TipoGeneracion.REC);
			return guiaRemisionDAO.persist(comprobante);

		} catch (Exception ex) {
			log.error("Error recibir", ex);
			throw new GizloException(ex);
		}
	}

	@Override
	public List<GuiaRemision> consultarCommprobantesRecibidos(
			String numeroComprobante, Date desde, Date hasta,
			String rucComprador) throws GizloException {
		return guiaRemisionDAO.obtenerGuiaRemisionPorParametros(null,
				numeroComprobante, desde, hasta, TipoGeneracion.REC, null,
				false, rucComprador, null, null, null, null, null, null, null,
				null, null);
	}
}
