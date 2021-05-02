package com.gizlocorp.gnvoice.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;

@Local
public interface FacturaRecepcionDAO extends GenericDAO<FacturaRecepcion, Long> {

	
	FacturaRecepcion obtenerComprobanteRecepcion(String claveAcceso, String claveInterna,
			String ruc, String agencia);
	
	List<FacturaRecepcion> obtenerComprobanteParaTraduccion(int min, int max);

	/*
	 * List<Factura> obtenerFacturaPorParametros(String estado, String
	 * numeroComprobante, Date fecha, TipoGeneracion tipoGeneracion, boolean
	 * seguimiento) throws GizloException;
	 */
	List<FacturaRecepcion> obtenerFacturaPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;

	List<FacturaRecepcion> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<FacturaRecepcion> obtenerFactura(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;

	FacturaRecepcion obtenerComprobanteCancelar(String claveAcceso, String codigoExterno)
			throws GizloException;

	List<FacturaRecepcion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	List<FacturaRecepcion> obtenerComprobantesConciliacion(String agencia)
			throws GizloException;

	List<FacturaRecepcion> obtenerRequireCancelacion(String agencia)
			throws GizloException;

	EntityManager getEntityManager();
	
	List<FacturaRecepcion> obtenerComprobantesResagados() throws GizloException;

	public List<FacturaRecepcion>  obtenerComprobante(String ruc, String claveAcceso,
			String secuencias, String codigoExterno, String estado,
			Integer pagina, Integer tamanoPagina);
	
	List<String> listaProveedores()throws GizloException;
	
	
	void eliminarFacturas(String claveAcceso) throws GizloException;
}
