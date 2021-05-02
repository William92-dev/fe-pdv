package com.gizlocorp.gnvoice.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;

@Local
public interface ServicioRecepcionFactura {
	
	FacturaRecepcion obtenerComprobanteRecepcion(String claveAcceso, String claveInterna,
			String ruc, String agencia);
	
	List<FacturaRecepcion> obtenerComprobanteParaTraduccion(int min, int max);
	
	List<FacturaRecepcion> obtenerComprobante(String ruc, String claveAcceso,
			String secuencias, String codigoExterno, String estado, Integer pagina, Integer tamanoPagina);

	FacturaRecepcion obtenerComprobanteCancelar(String claveAcceso, String codigoExterno)
			throws GizloException;

	void actualizarConciliacion(FacturaRecepcion factura) throws GizloException;

	List<FacturaRecepcion> consultarComprobantes(String numeroComprobante, Date desde,
			Date hasta, String rucComprador, String codigoExterno,
			String usuario, String rucEmisor) throws GizloException;

	List<FacturaRecepcion> consultarCommprobantesRecibidos(String numeroComprobante,
			Date desde, Date hasta, String rucComprador) throws GizloException;
	
	List<FacturaRecepcion> consultarCommprobantesRecibidosReim(String numeroComprobante,
			Date desde, Date hasta, String estado,String rucComprador) throws GizloException;

	List<FacturaRecepcion> consultarComprobantesSeguimiento(String estado,
			String numeroComprobante, Date desde, Date hasta, String rucEmisor,
			String codigoExterno, String rucComprador, String usuario,
			String agencia, String claveContingencia, String tipoEmision,
			String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;

	FacturaRecepcion autoriza(FacturaRecepcion factura);
	
	FacturaRecepcion contingencia(FacturaRecepcion factura);

	List<FacturaRecepcion> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<FacturaRecepcion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	List<FacturaRecepcion> obtenerComprobanteConsulta(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;
	
	FacturaRecepcion recibirFactura(FacturaRecepcion comprobante, String observacion)
			throws GizloException;

	List<FacturaRecepcion> obtenerComprobantesConciliacion(String agencia)
			throws GizloException;

	List<FacturaRecepcion> obtenerRequireCancelacion(String agencia)
			throws GizloException;

	EntityManager getEntityManager();

	// void generaPdfMdb(FacturaGeneraPdfRequest factAutorizadaXML);

	FacturaRecepcion obtenerPorId(Long id);
	
	List<FacturaRecepcion> obtenerComprobantesResagados() throws GizloException;
	
	List<String> listaProveedores()throws GizloException;
	
	void eliminarFacturas(String claveAcceso) throws GizloException;

}
