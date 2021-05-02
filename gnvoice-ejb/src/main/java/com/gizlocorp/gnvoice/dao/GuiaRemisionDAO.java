package com.gizlocorp.gnvoice.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.GuiaRemision;

@Local
public interface GuiaRemisionDAO extends GenericDAO<GuiaRemision, Long> {

	GuiaRemision obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);

	/*
	 * List<GuiaRemision> obtenerGuiaRemisionPorParametros(String estado, String
	 * numeroComprobante, Date fecha, TipoGeneracion tipoGeneracion, boolean
	 * seguimiento) throws GizloException;
	 */
	List<GuiaRemision> obtenerGuiaRemisionPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado, String agencia,
			String claveContingencia, String tipoEmision, String tipoAmbiente,
			String correoProveedor, TipoEjecucion tipoEjecucion)
			throws GizloException;

	List<GuiaRemision> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<GuiaRemision> obtenerGuiaRemision(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;

	List<GuiaRemision> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	GuiaRemision obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException;
}
