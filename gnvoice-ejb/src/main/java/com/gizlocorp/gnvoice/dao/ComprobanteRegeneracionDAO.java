package com.gizlocorp.gnvoice.dao;

import java.sql.Connection;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.xml.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion;


@Local
public interface ComprobanteRegeneracionDAO {
	
	Long obtenerTipo(Connection conn, String comprobante) throws GizloException;
	
	FacturaProcesarRequest listar(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException;
	
	Long listaSidOficina(Connection conn, String databaseSid) throws GizloException;
	
	Long listaSidSanaSana(Connection conn, String databaseSid) throws GizloException;
	
	Long listaOkiDoki(Connection conn, String databaseSid) throws GizloException;
	
	String listaSidGeneral(Connection conn, Long codigo) throws GizloException;
	
	NotaCreditoProcesarRequest listarNotaCredito(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException;
	
	FacturaProcesarRequest listarJDE(Connection conn, String claveAcceso) throws GizloException;
	
   
    ComprobanteRetencion listarRetencionJDE(Connection conn, String claveAcceso) throws GizloException;
	
}
