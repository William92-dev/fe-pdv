package com.gizlocorp.gnvoice.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;

@Local
public interface ServicioFactura {

	Factura obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);
	
	FacturaRecepcion obtenerComprobanteRecepcion(String claveAcceso, String claveInterna,
			String ruc, String agencia);
	
	List<Factura> obtenerComprobante(String ruc, String claveAcceso,
			String secuencias, String codigoExterno, String estado, Integer pagina, Integer tamanoPagina);

	/*
	 * List<Factura> consultarCommprobantes(String numeroComprobante, Date
	 * fecha) throws GizloException;
	 * 
	 * List<Factura> consultarCommprobantesSeguimiento(String estado, String
	 * numeroComprobante, Date fecha) throws GizloException;
	 */

	Factura obtenerComprobanteCancelar(String claveAcceso, String codigoExterno)
			throws GizloException;

	void actualizarConciliacion(Factura factura) throws GizloException;

	List<Factura> consultarComprobantes(String numeroComprobante, Date desde,
			Date hasta, String rucComprador, String codigoExterno,
			String usuario, String rucEmisor) throws GizloException;

	List<Factura> consultarCommprobantesRecibidos(String numeroComprobante,
			Date desde, Date hasta, String rucComprador) throws GizloException;
	
	List<Factura> consultarCommprobantesRecibidosReim(String numeroComprobante,
			Date desde, Date hasta, String estado) throws GizloException;

	List<Factura> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String agencia, String claveContingencia, String tipoEmision,
			String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;
	
	List<Factura> consultarComprobantesSeguimientoAbf(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String agencia, String claveContingencia, String tipoEmision,
			String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion,String razonSocialComprador) throws GizloException;

	Factura autoriza(Factura factura);
	
	Factura autorizaOffline(Factura factura);
	
	Factura contingencia(Factura factura);

	List<Factura> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<Factura> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	List<Factura> obtenerComprobanteConsulta(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;

	Factura recibirFactura(Factura comprobante, String observacion)
			throws GizloException;

	List<Factura> obtenerComprobantesConciliacion(String agencia)
			throws GizloException;

	List<Factura> obtenerRequireCancelacion(String agencia)
			throws GizloException;

	EntityManager getEntityManager();

	// void generaPdfMdb(FacturaGeneraPdfRequest factAutorizadaXML);

	Factura obtenerPorId(Long id);
	
	List<Factura> obtenerComprobantesResagados() throws GizloException;

}
