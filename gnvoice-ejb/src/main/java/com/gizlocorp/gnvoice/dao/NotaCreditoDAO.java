package com.gizlocorp.gnvoice.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.NotaCredito;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;

@Local
public interface NotaCreditoDAO extends GenericDAO<NotaCredito, Long> {

	NotaCredito obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);

	/*
	 * List<NotaCredito> obtenerNotaCreditoPorParametros(String estado, String
	 * numeroComprobante, Date fecha, TipoGeneracion tipoGeneracion, boolean
	 * seguimiento) throws GizloException;
	 */
	List<NotaCredito> obtenerNotaCreditoPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado, String agencia,
			String claveContingencia, String tipoEmision, String tipoAmbiente,
			String correoProveedor, TipoEjecucion tipoEjecucion)
			throws GizloException;
	
	
	List<NotaCreditoRecepcion> obtenerNotaCreditoPorParametrosRecepcion(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado, String agencia,
			String claveContingencia, String tipoEmision, String tipoAmbiente,
			String correoProveedor, TipoEjecucion tipoEjecucion)
			throws GizloException;

	List<NotaCredito> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<NotaCredito> obtenerNotaCredito(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;
	
	
	

	NotaCredito obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException;

	List<NotaCredito> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;
}
