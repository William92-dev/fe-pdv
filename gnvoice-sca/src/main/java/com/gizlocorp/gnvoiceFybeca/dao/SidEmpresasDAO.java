package com.gizlocorp.gnvoiceFybeca.dao;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarRequest;

@Local
public interface SidEmpresasDAO {
	
	List<String> listaSidOficina(Connection conn) throws GizloException;
	
	List<String> listaSidSanaSana(Connection conn) throws GizloException;
	
	List<String> listaOkiDoki(Connection conn) throws GizloException;
	
}
