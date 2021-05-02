package com.gizlocorp.gnvoice.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.modelo.NotaCreditoRecepcion;

@Local
public interface ServicioNotaCreditoRecepcion {
	
	NotaCreditoRecepcion obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);

	NotaCreditoRecepcion autoriza(NotaCreditoRecepcion notaCredito);
	
	List<NotaCreditoRecepcion> consultarCommprobantesAtraducir(int min, int max);

	NotaCreditoRecepcion recibirNotaCredito(NotaCreditoRecepcion comprobante, String observacion)
			throws GizloException;

	/*
	 * List<NotaCredito> consultarCommprobantes(String numeroComprobante, Date
	 * fecha) throws GizloException;
	 * 
	 * List<NotaCredito> consultarCommprobantesSeguimiento(String estado, String
	 * numeroComprobante, Date fecha) throws GizloException;
	 */

	List<NotaCreditoRecepcion> consultarCommprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException;

	List<NotaCreditoRecepcion> consultarCommprobantesRecibidos(String numeroComprobante,
			Date desde, Date hasta, String rucComprador) throws GizloException;

	List<NotaCreditoRecepcion> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String numeroRelacionado, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;

	List<NotaCreditoRecepcion> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<NotaCreditoRecepcion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	List<NotaCreditoRecepcion> obtenerComprobanteConsulta(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException;

	NotaCreditoRecepcion obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException;
	
	
	NotaCreditoRecepcion obtenerComprobanteRecepcion(String claveAcceso,
			String claveInterna, String ruc, String agencia);
}
