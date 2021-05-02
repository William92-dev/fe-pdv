package com.gizlocorp.gnvoiceIntegracion.dao.impl;

import java.io.BufferedReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.gizlocorp.gnvoiceIntegracion.dao.TrxElectronicaDAO;
import com.gizlocorp.gnvoiceIntegracion.model.TrxElectronica;

@Stateless
public class TrxElectronicaDAOImpl implements TrxElectronicaDAO {

	private static Logger log = Logger.getLogger(TrxElectronicaDAOImpl.class.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<TrxElectronica> concsultarTodos(Connection conn, Date fechaDesde, Date fechaHasta) {
		//Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		List<TrxElectronica> lista = new ArrayList<TrxElectronica>();

		try {
			//conn = ConexionIntegracion.getConnection();
			StringBuilder sqlString = new StringBuilder();

			sqlString.append("SELECT * FROM LOG_TRX_ELECTRONICA where 1 = 1 ");
			
			if(fechaDesde != null){
				sqlString.append("and fecha >= ? ");
			}
			
			if(fechaHasta != null){
				sqlString.append("and fecha <= ? ");
			}

			ps = conn.prepareStatement(sqlString.toString());

			if(fechaDesde != null){
				ps.setDate(1, (new java.sql.Date(fechaDesde.getTime())));
			}
			if(fechaHasta != null){
				ps.setDate(2, (new java.sql.Date(fechaHasta.getTime())));
			}
			

			set = ps.executeQuery();

			while (set.next()) {
				TrxElectronica objeto = new TrxElectronica();
				objeto.setId(set.getLong("ID"));
				objeto.setAutorizacion(set.getString("AUTORIZACION"));
				objeto.setFechaAutorizacion(set.getString("FECHA_AUTORIZACION"));
				
			/*	Clob clob = set.getClob("XML");
				String aux;
				StringBuffer strOut = new StringBuffer();
				BufferedReader br = new BufferedReader(clob.getCharacterStream());
				while ((aux = br.readLine()) != null) {
					strOut.append(aux);
				}
				objeto.setXml(strOut.toString());*/
				lista.add(objeto);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lista;
	}
	
	/*@Override
	public String concsultarPorClaveAccesso(String claveAccesso) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		String objetoXml = "";

		try {
			conn = ConexionIntegracion.getConnection();
			StringBuilder sqlString = new StringBuilder();

			sqlString.append("SELECT * FROM LOG_TRX_ELECTRONICA where CLAVE_ACCESO >= ? ");
			
			ps = conn.prepareStatement(sqlString.toString());

			ps.setString(1, claveAccesso);
			
			set = ps.executeQuery();

			while (set.next()) {
				TrxElectronica objeto = new TrxElectronica();
				Clob clob = set.getClob("XML");
				String aux;
				StringBuffer strOut = new StringBuffer();
				BufferedReader br = new BufferedReader(clob.getCharacterStream());
				while ((aux = br.readLine()) != null) {
					strOut.append(aux);
				}
				objeto.setXml(strOut.toString());
				objetoXml = objeto.getXml();
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return objetoXml;
	}*/

	/*public void insert(TrxElectronica objeto) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		FileInputStream fis = null;

		try {
			conn = ConexionIntegracion.getConnection();
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("Insert into LOG_TRX_ELECTRONICA(ID, COMPANIA, TIPO_COMPROBANTE, NUMERO_COMPROBANTE, XML)");
			sqlString.append(" Values(?,?,?,?,?)");

			ps = conn.prepareStatement(sqlString.toString());

			ps.setLong(1, objeto.getId());
			ps.setString(2, objeto.getCompania());
			ps.setString(3, objeto.getTipoComprobante());
			ps.setString(4, objeto.getNumeroComprobante());
			File file = new File(objeto.getXml());
			fis = new FileInputStream(file);
			byte b[] = new byte[(int) file.length()];
			fis.read(b);
			System.out.println(b.length);
			java.sql.Blob b2 = new SerialBlob(b);
			ps.setBlob(5, b2);

			ps.executeUpdate();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}*/

	

}
