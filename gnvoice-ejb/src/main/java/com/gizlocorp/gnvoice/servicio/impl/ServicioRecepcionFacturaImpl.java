package com.gizlocorp.gnvoice.servicio.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.dao.FacturaRecepcionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;
//import javax.ejb.EJB;
//import com.gizlocorp.gnvoice.service.ProcesadorSRI;
//import com.gizlocorp.gnvoice.xml.message.ComprobanteRequest;
//import com.gizlocorp.gnvoice.xml.message.ComprobanteResponse;
import com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura;

@Stateless
public class ServicioRecepcionFacturaImpl implements ServicioRecepcionFactura {
	public static final Logger log = Logger
			.getLogger(ServicioRecepcionFacturaImpl.class);

	@EJB
	FacturaRecepcionDAO facturaDAO;

	
	public FacturaRecepcion obtenerComprobanteRecepcion(String claveAcceso, String claveInterna,
			String ruc, String agencia) {
		return facturaDAO.obtenerComprobanteRecepcion(claveAcceso, claveInterna, ruc,
				agencia);
	}
	
	@Override
	public List<FacturaRecepcion> obtenerComprobanteParaTraduccion(int min, int max) {
		return facturaDAO.obtenerComprobanteParaTraduccion(min, max);
	}
	
	

	public FacturaRecepcion obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException {
		return facturaDAO
				.obtenerComprobanteCancelar(claveAcceso, codigoExterno);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public FacturaRecepcion autoriza(FacturaRecepcion comprobante) {
		try {
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = facturaDAO.update(comprobante);

			} else {
				comprobante = facturaDAO.persist(comprobante);

			}

		} catch (Exception ex) {
			log.error("Error factura", ex);

		}
		return comprobante;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public FacturaRecepcion contingencia(FacturaRecepcion comprobante) {
		try {
			comprobante.setEstado(Estado.CONTINGENCIA.getDescripcion());
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = facturaDAO.update(comprobante);

			} else {
				comprobante = facturaDAO.persist(comprobante);

			}

		} catch (Exception ex) {
			log.error("Error factura", ex);

		}
		return comprobante;
	}

	@Override
	public List<FacturaRecepcion> consultarComprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String rucEmisor) throws GizloException {
		/*
		 * return facturaDAO.obtenerFacturaPorParametros("AUTORIZADO",
		 * numeroComprobante, fecha, TipoGeneracion.EMI, false);
		 */
		return facturaDAO.obtenerFacturaPorParametros("AUTORIZADO",
				numeroComprobante, desde, hasta, TipoGeneracion.EMI, rucEmisor,
				false, rucComprador, codigoExterno, usuario, null, null, null,
				null, null, null);
	}

	@Override
	public List<FacturaRecepcion> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String agencia, String claveContingencia, String tipoEmision,
			String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException {
		/*
		 * return facturaDAO.obtenerFacturaPorParametros(estado,
		 * numeroComprobante, fecha, TipoGeneracion.EMI, true);
		 */
		return facturaDAO.obtenerFacturaPorParametros(estado,
				numeroComprobante, desde, hasta, TipoGeneracion.EMI, rucEmisor,
				false, rucComprador, codigoExterno, usuario, agencia,
				claveContingencia, tipoEmision, tipoAmbiente, correoProveedor,
				tipoEjecucion);
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobantesReproceso(String ruc)
			throws GizloException {
		return facturaDAO.obtenerComprobantesReproceso(ruc);
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException {
		return facturaDAO.obtenerComprobantesProcesoLote(ruc);
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobanteConsulta(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException {
		return facturaDAO.obtenerFactura(ruc, claveAcceso, secuencial,
				codigoExterno, estado);
	}

	@Override
	public FacturaRecepcion recibirFactura(FacturaRecepcion comprobante, String observacion)
			throws GizloException {
		try {
			comprobante.setTipoGeneracion(TipoGeneracion.REC);
			comprobante.setTareaActual(Tarea.REP);

			if (comprobante.getId() != null) {
				comprobante = facturaDAO.update(comprobante);
			} else {
				comprobante = facturaDAO.persist(comprobante);
			}

			return comprobante;

		} catch (Exception ex) {
			log.error("Error factura", ex);
			throw new GizloException(ex);
		}
	}
	
	

	@Override
	public List<FacturaRecepcion> consultarCommprobantesRecibidos(
			String numeroComprobante, Date desde, Date hasta,
			String rucComprador) throws GizloException {
		return facturaDAO.obtenerFacturaPorParametros(null, numeroComprobante,
				desde, hasta, TipoGeneracion.REC, null, false, rucComprador,
				null, null, null, null, null, null, null, null);
	}

	@Override
	public List<FacturaRecepcion> consultarCommprobantesRecibidosReim(
			String numeroComprobante, Date desde, Date hasta, String estado, String rucComprador)
			throws GizloException {
		return facturaDAO.obtenerFacturaPorParametros(estado,
				numeroComprobante, desde, hasta, TipoGeneracion.REC, null,
				false, rucComprador, null, null, null, null, null, null, null, null);
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobantesConciliacion(String agencia)
			throws GizloException {
		return facturaDAO.obtenerComprobantesConciliacion(agencia);
	}

	@Override
	public List<FacturaRecepcion> obtenerRequireCancelacion(String agencia)
			throws GizloException {
		return facturaDAO.obtenerRequireCancelacion(agencia);
	}

	@Override
	public void actualizarConciliacion(FacturaRecepcion factura) throws GizloException {
		try {
			FacturaRecepcion facturaObj = facturaDAO.findById(factura.getId());
			facturaObj.setRequiereCancelacion(factura.getRequiereCancelacion());
			facturaObj.setObservacionCancelacion(factura
					.getObservacionCancelacion());
			facturaObj.setSecuencialNotaCredito(factura
					.getSecuencialNotaCredito());
			facturaDAO.update(facturaObj);
		} catch (Exception ex) {
			log.error("Error factura", ex);
			throw new GizloException(ex);
		}

	}

	@Override
	public EntityManager getEntityManager() {

		return facturaDAO.getEntityManager();
	}

	// @Override
	// public void generaPdfMdb(FacturaGeneraPdfRequest factAutorizadaXML) {
	//
	// javax.jms.Connection connection = null;
	// Session session = null;
	// try {
	// connection = this.connectionFactory.createConnection();
	// session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	// MessageProducer producer = session.createProducer(this.queue);
	//
	// HashMap<String, FacturaGeneraPdfRequest> comprobanteMap = new
	// HashMap<String, FacturaGeneraPdfRequest>();
	// comprobanteMap.put("comprobante", factAutorizadaXML);
	//
	// ObjectMessage objmsg = session.createObjectMessage();
	// objmsg.setObject(factAutorizadaXML);
	// producer.send(objmsg);
	//
	// } catch (Exception e) {
	// log.error("Error proceso automatico", e);
	// } finally {
	// if (session != null) {
	// try {
	// session.close();
	// } catch (JMSException e) {
	// log.error("Error proceso automatico", e);
	// }
	// }
	// if (connection != null) {
	// try {
	// connection.close();
	// } catch (JMSException e) {
	// log.error("Error proceso automatico", e);
	// }
	// }
	// }
	//
	// }

	@Override
	public FacturaRecepcion obtenerPorId(Long id) {
		return facturaDAO.findById(id);
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobantesResagados() throws GizloException {
		// TODO Auto-generated method stub
		return facturaDAO.obtenerComprobantesResagados();
	}

	@Override
	public List<FacturaRecepcion> obtenerComprobante(String ruc, String claveAcceso,
			String secuencias, String codigoExterno, String estado,
			Integer pagina, Integer tamanoPagina) {
		
		return facturaDAO.obtenerComprobante( ruc,  claveAcceso,
				 secuencias,  codigoExterno,  estado,
				 pagina,  tamanoPagina);
		
	}

	@Override
	public List<String> listaProveedores() throws GizloException {
		return facturaDAO.listaProveedores();
	}

	@Override
	public void eliminarFacturas(String claveAcceso) throws GizloException {
		facturaDAO.eliminarFacturas( claveAcceso);
	}
}