package com.gizlocorp.gnvoice.servicio.local;

import javax.ejb.Local;

import com.gizlocorp.gnvoice.enumeracion.TipoDocumento;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;

@Local
public interface ServicioComprobante {

	Autorizacion autorizarComprobante(String ambienteEmision,
			String claveAcceso, Long codigoOrganizacion);

	String consultaRutaComprobante(
			com.gizlocorp.gnvoice.enumeracion.TipoComprobante comprobante,
			TipoDocumento tipoDocumento, String claveAcceso);

	RespuestaSolicitud enviarComprobante(String ambienteEmision,
			String rutaArchivoXml, Long codigoOrganizacion);

}
