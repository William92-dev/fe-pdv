package com.gizlocorp.gnvoiceFybeca.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoiceFybeca.dao.SidEmpresasDAO;

@Stateless
public class SidEmpresasDAOImpl implements SidEmpresasDAO {

	private static Logger log = Logger
			.getLogger(SidEmpresasDAOImpl.class.getName());

	
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> listaSidOficina(Connection conn)
			throws GizloException {
		// TODO Auto-generated method stub
		List<String> respuesta = new ArrayList<String>();
		PreparedStatement ps = null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("select codigo,database_sid,nombre from ad_farmacias   where fma_autorizacion_farmaceutica = 'FS' and campo3 = 'S' ");
		sqlString.append("AND EMPRESA in (1)  ORDER BY EMPRESA,CODIGO asc");
		try {
			ps = conn.prepareStatement(sqlString.toString());
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				respuesta.add(set.getInt("codigo")+"&"+set.getString("database_sid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}


	@Override
	public List<String> listaSidSanaSana(Connection conn) throws GizloException {
		// TODO Auto-generated method stub
		List<String> respuesta = new ArrayList<String>();
		PreparedStatement ps = null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("select codigo,database_sid,nombre from ad_farmacias   where fma_autorizacion_farmaceutica = 'FS' and campo3 = 'S' ");
		sqlString.append("AND EMPRESA in (8)  ORDER BY EMPRESA,CODIGO asc");
		try {
			ps = conn.prepareStatement(sqlString.toString());
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				respuesta.add(set.getInt("codigo")+"&"+set.getString("database_sid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}


	@Override
	public List<String> listaOkiDoki(Connection conn) throws GizloException {
		// TODO Auto-generated method stub
		List<String> respuesta = new ArrayList<String>();
		PreparedStatement ps = null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("select codigo,database_sid,nombre from ad_farmacias   where fma_autorizacion_farmaceutica = 'FS' and campo3 = 'S' ");
		sqlString.append("AND EMPRESA in (11)  ORDER BY EMPRESA,CODIGO asc");
		try {
			ps = conn.prepareStatement(sqlString.toString());
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				respuesta.add(set.getInt("codigo")+"&"+set.getString("database_sid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}


}
