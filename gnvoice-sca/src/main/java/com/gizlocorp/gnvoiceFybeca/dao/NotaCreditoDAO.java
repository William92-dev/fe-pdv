package com.gizlocorp.gnvoiceFybeca.dao;

import java.sql.Connection;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarRequest;

@Local
public interface NotaCreditoDAO {
	NotaCreditoProcesarRequest listar(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException;

	Long obtenerSecuencial(Connection conn, Long documentoVenta, Long farmacia,
			Long tipo) throws GizloException;
}
