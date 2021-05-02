package com.gizlocorp.gnvoice.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.modelo.NotaCredito;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;

@Local
public interface ServicioNotaCredito {
	NotaCredito obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);

	NotaCredito autoriza(NotaCredito notaCredito);
	
	NotaCredito autorizaOffline(NotaCredito notaCredito);

	NotaCredito recibirNotaCredito(NotaCredito comprobante, String observacion)
			throws GizloException;

	/*
	 * List<NotaCredito> consultarCommprobantes(String numeroComprobante, Date
	 * fecha) throws GizloException;
	 * 
	 * List<NotaCredito> consultarCommprobantesSeguimiento(String estado, String
	 * numeroComprobante, Date fecha) throws GizloException;
	 */

	List<NotaCredito> consultarCommprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException;
	
	
	List<NotaCreditoRecepcion> consultarCommprobantesRecepcion(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException;
	
	

	List<NotaCredito> consultarCommprobantesRecibidos(String numeroComprobante,
			Date desde, Date hasta, String rucComprador) throws GizloException;

	List<NotaCredito> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String numeroRelacionado, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;

	List<NotaCredito> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<NotaCredito> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	List<NotaCredito> obtenerComprobanteConsulta(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException;

	NotaCredito obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException;
}
