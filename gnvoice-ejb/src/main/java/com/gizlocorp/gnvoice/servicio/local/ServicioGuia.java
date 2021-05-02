package com.gizlocorp.gnvoice.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.GuiaRemision;

@Local
public interface ServicioGuia {

	GuiaRemision obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);

	GuiaRemision autoriza(GuiaRemision guia);

	GuiaRemision recibirGuiaRemision(GuiaRemision comprobante)
			throws GizloException;

	/*
	 * List<GuiaRemision> consultarCommprobantes(String numeroComprobante, Date
	 * fecha) throws GizloException;
	 * 
	 * List<GuiaRemision> consultarCommprobantesSeguimiento(String estado,
	 * String numeroComprobante, Date fecha) throws GizloException;
	 */
	List<GuiaRemision> consultarCommprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException;

	List<GuiaRemision> consultarCommprobantesRecibidos(
			String numeroComprobante, Date desde, Date hasta,
			String rucComprador) throws GizloException;

	List<GuiaRemision> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String numeroRelacionado, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;

	List<GuiaRemision> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<GuiaRemision> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	List<GuiaRemision> obtenerComprobanteConsulta(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException;

	GuiaRemision obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException;
	
	
	GuiaRemision autorizaOffline(GuiaRemision comprobante);
}
