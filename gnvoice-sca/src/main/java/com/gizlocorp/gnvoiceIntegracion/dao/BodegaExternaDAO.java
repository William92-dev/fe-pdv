package com.gizlocorp.gnvoiceIntegracion.dao;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceIntegracion.model.BodegaExterna;

@Local
public interface BodegaExternaDAO {
	
	List<BodegaExterna> concsultarBodegaExterna(Connection conn, Date fechaDesde, Date fechaHasta, String movimiento, String identificacion, CredencialDS credencialDS);
	
	List<BodegaExterna> concsultarProveedoresExterna(Connection conn, Date fechaDesde, Date fechaHasta, String movimiento, String identificacion, CredencialDS credencialDS);
	

	
	

	
}
