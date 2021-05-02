package com.gizlocorp.gnvoiceIntegracion.dao;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.gnvoiceIntegracion.model.TrxElectronica;

@Local
public interface TrxElectronicaDAO {
	
	List<TrxElectronica> concsultarTodos(Connection conn, Date fechaDesde, Date fechaHasta);
	
	//void insert(TrxElectronica objeto);
	
	//String concsultarPorClaveAccesso(String claveAccesso);
	
	

	
}
