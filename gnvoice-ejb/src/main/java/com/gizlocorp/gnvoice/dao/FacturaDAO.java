package com.gizlocorp.gnvoice.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.EntityManager;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.FacturaHist;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;

@Local
public interface FacturaDAO extends GenericDAO<Factura, Long> {

	Factura obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc, String agencia);
	
	FacturaRecepcion obtenerComprobanteRecepcion(String claveAcceso, String claveInterna,
			String ruc, String agencia);

	/*
	 * List<Factura> obtenerFacturaPorParametros(String estado, String
	 * numeroComprobante, Date fecha, TipoGeneracion tipoGeneracion, boolean
	 * seguimiento) throws GizloException;
	 */
	List<Factura> obtenerFacturaPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;
	
	List<FacturaHist> obtenerFacturaHistPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion) throws GizloException;
	
	List<Factura> obtenerFacturaPorParametrosAbf(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String agencia, String claveContingencia,
			String tipoEmision, String tipoAmbiente, String correoProveedor,
			TipoEjecucion tipoEjecucion,String razonSocialComprador) throws GizloException;

	List<Factura> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<Factura> obtenerFactura(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;

	Factura obtenerComprobanteCancelar(String claveAcceso, String codigoExterno)
			throws GizloException;

	List<Factura> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;

	List<Factura> obtenerComprobantesConciliacion(String agencia)
			throws GizloException;

	List<Factura> obtenerRequireCancelacion(String agencia)
			throws GizloException;

	EntityManager getEntityManager();
	
	List<Factura> obtenerComprobantesResagados() throws GizloException;

	public List<Factura>  obtenerComprobante(String ruc, String claveAcceso,
			String secuencias, String codigoExterno, String estado,
			Integer pagina, Integer tamanoPagina);
}
