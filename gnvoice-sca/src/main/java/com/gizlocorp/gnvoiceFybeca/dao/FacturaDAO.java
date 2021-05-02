package com.gizlocorp.gnvoiceFybeca.dao;

import java.sql.Connection;
import java.sql.Date;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarRequest;

@Local
public interface FacturaDAO {
	FacturaProcesarRequest listar(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException;

	Long obtenerSecuencialFactura(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException;

	Long obtenerSecuencialFacturaAutoimpresor(Connection conn,
			Long documentoVenta, Long farmacia, Long tipo)
			throws GizloException;

	Date obtenerFechaEmision(Connection conn, Long documentoVenta, Long farmacia)
			throws GizloException;
}
