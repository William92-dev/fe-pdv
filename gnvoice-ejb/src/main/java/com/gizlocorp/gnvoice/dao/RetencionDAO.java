package com.gizlocorp.gnvoice.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.Retencion;

@Local
public interface RetencionDAO extends GenericDAO<Retencion, Long> {

	Retencion obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc);

	


	List<Retencion> obtenerComprobantesReproceso(String ruc)
			throws GizloException;

	List<Retencion> obtenerComprobantesProcesoLote(String ruc)
			throws GizloException;


	List<Retencion> obtenerRetencion(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;

	Retencion obtenerComprobanteCancelar(String claveAcceso,
			String codigoExterno) throws GizloException;
	
	List<Retencion> obtenerRetencionPorParametros(String estado,
			String numeroComprobante, Date desde, Date hasta,
			TipoGeneracion tipoGeneracion, String rucEmisor,
			boolean seguimiento, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado, String agencia,
			String claveContingencia, String tipoEmision, String tipoAmbiente,
			String correoProveedor, TipoEjecucion tipoEjecucion,
			Date desdeRecibido, Date hastaRecibido, String razonSocial)
			throws GizloException;
}
