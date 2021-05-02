package com.gizlocorp.gnvoice.servicio.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.gnvoice.dao.RetencionDAO;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.Tarea;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.Retencion;
import com.gizlocorp.gnvoice.servicio.local.ServicioRetencion;

@Stateless
public class ServicioRetencionImpl implements ServicioRetencion {

	public static final Logger log = Logger
			.getLogger(ServicioRetencionImpl.class);

	@EJB
	RetencionDAO retencionDAO;

	

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	

	@Override
	public Retencion obtenerComprobante(String claveAcceso,
			String claveInterna, String ruc) {
		return retencionDAO.obtenerComprobante(claveAcceso, claveInterna, ruc);
	}
	
	@Override
	public Retencion autoriza(Retencion comprobante) {
		try {
			comprobante.setTareaActual(Tarea.AUT);
			comprobante.setEstado(Estado.AUTORIZADO.getDescripcion());
			comprobante.setTipoGeneracion(TipoGeneracion.EMI);

			if (comprobante.getId() != null) {
				comprobante = retencionDAO.update(comprobante);

			} else {
				comprobante = retencionDAO.persist(comprobante);

			}

		} catch (Exception ex) {
			log.error("Error factura", ex);
		}
		return comprobante;
	}

	

	@Override
	public List<Retencion> obtenerComprobanteConsulta(String ruc,
			String claveAcceso, String secuencial, String codigoExterno,
			String estado) throws GizloException {
		return retencionDAO.obtenerRetencion(ruc, claveAcceso, secuencial,
				codigoExterno, estado);
	}
	
	@Override
	public List<Retencion> consultarCommprobantes(String numeroComprobante,
			Date desde, Date hasta, String rucComprador, String codigoExterno,
			String usuario, String numeroRelacionado) throws GizloException {
		/*
		 * return retencionDAO.obtenerRetencionPorParametros("AUTORIZADO",
		 * numeroComprobante, fecha, TipoGeneracion.EMI, false);
		 */
		return retencionDAO.obtenerRetencionPorParametros("AUTORIZADO",
				numeroComprobante, desde, hasta, TipoGeneracion.EMI, null,
				false, rucComprador, codigoExterno, usuario, numeroRelacionado,
				null, null, null, null, null, null, null, null, null);
	}

	


}
