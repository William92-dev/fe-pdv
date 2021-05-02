package com.gizlocorp.gnvoice.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;

@Local
public interface NotaCreditoRecepcionDAO extends GenericDAO<NotaCreditoRecepcion, Long> {

	NotaCreditoRecepcion obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);
	
	List<NotaCreditoRecepcion> consultarCommprobantesAtraducir(int min, int max);

	/*
	 * List<NotaCredito> obtenerNotaCreditoPorParametros(String estado, String
	 * numeroComprobante, Date fecha, TipoGeneracion tipoGeneracion, boolean
	 * seguimiento) throws GizloException;
	 */
	List<NotaCreditoRecepcion> obtenerNotaCreditoPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado, String agencia,
			String claveContingencia, String tipoEmision, String tipoAmbiente,
			String correoProveedor, TipoEjecucion tipoEjecucion)
			throws GizloException;

	List<NotaCreditoRecepcion> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<NotaCreditoRecepcion> obtenerNotaCredito(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;

	NotaCreditoRecepcion obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException;

	List<NotaCreditoRecepcion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;
	
	
	NotaCreditoRecepcion obtenerComprobanteRecepcion(String claveAcceso,
			String claveInterna, String ruc, String agencia);
}
