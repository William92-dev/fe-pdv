package com.gizlocorp.gnvoiceFybeca.dao;

import java.sql.Connection;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;

@Local
public interface TipoComprobanteDAO {

	Long obtenerTipo(Connection conn, String comprobante) throws GizloException;
}
