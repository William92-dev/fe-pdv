package com.gizlocorp.gnvoiceFybeca.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoiceFybeca.dao.TipoComprobanteDAO;

@Stateless
public class TipoComprobanteDAOImpl implements TipoComprobanteDAO {

	private static Logger log = Logger.getLogger(TipoComprobanteDAOImpl.class
			.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long obtenerTipo(Connection conn, String comprobante)
			throws GizloException {
		PreparedStatement ps = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select * from farmacias.fa_tipo_comprobante where descripcion = ?");
			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, comprobante);
			ResultSet set = ps.executeQuery();

			while (set.next()) {
				return set.getLong("codigo");
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return null;
	}

}
