package com.gizlocorp.gnvoice.servicio.local;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.modelo.Retencion;

@Local
public interface ServicioRetencion {
	Retencion obtenerComprobante(String claveAcceso, String claveInterna,
			String ruc);
	
	 Retencion autoriza(Retencion comprobante);
	


	List<Retencion> obtenerComprobanteConsulta(String ruc, String claveAcceso,
			String secuencial, String codigoExterno, String estado)
			throws GizloException;
	
	List<Retencion> consultarCommprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException;

}