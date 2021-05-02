package com.gizlocorp.gnvoiceFybeca.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoiceFybeca.dao.RespuestaDAO;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.modelo.Respuesta;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;

@Stateless
public class RespuestaDAOImpl implements RespuestaDAO {

	private static Logger log = Logger.getLogger(RespuestaDAOImpl.class
			.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void insertarRespuesta(Connection conn, Respuesta respuesta)
			throws GizloException {

		PreparedStatement ps = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append(Constantes.INSERT_FA_DATOS_SRI_ELECTRONICA);
			ps = conn.prepareStatement(sqlString.toString());
			// ps.setLong(1, respuesta.getId());
			ps.setLong(1, respuesta.getIdFarmacia());
			ps.setLong(2, respuesta.getIdDocumento());
			ps.setLong(3, respuesta.getTipoComprobante());
			ps.setDate(4, respuesta.getFecha() != null ? new Date(respuesta
					.getFecha().getTime()) : null);
			ps.setString(5, respuesta.getUsuario());
			ps.setString(6, respuesta.getFirmaE());
			ps.setString(7, respuesta.getAutFirmaE());
			ps.setDate(8, respuesta.getFechaFirmaE() != null ? new Date(
					respuesta.getFechaFirmaE().getTime()) : null);
			ps.setString(9, respuesta.getComprobanteFirmaE());
			ps.setString(10, respuesta.getClaveAcceso());
			/*
			 * NClob clob = conn.createNClob(); clob.setString(1,
			 * respuesta.getObservacion()); ps.setNClob(11, clob);
			 */
			ps.setString(11, respuesta.getObservacion());
			ps.setString(12, respuesta.getCorreoElectronico());
			ps.setString(13, respuesta.getEstado());
			ps.setString(14, respuesta.getTipoProceso());

			ps.executeUpdate();
			// conn.commit();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			/*
			 * try { conn.rollback(); } catch (SQLException e1) {
			 * log.error(e1.getMessage()); }
			 */
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void actualizarRespuesta(Connection conn, Respuesta respuesta)
			throws GizloException {
		PreparedStatement ps = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append(Constantes.UPDATE_FA_DATOS_SRI_ELECTRONICA);
			ps = conn.prepareStatement(sqlString.toString());

			// ps.setLong(1, respuesta.getId());
			ps.setLong(1, respuesta.getIdFarmacia());
			ps.setLong(2, respuesta.getIdDocumento());
			ps.setLong(3, respuesta.getTipoComprobante());
			ps.setDate(4, respuesta.getFecha() != null ? new Date(respuesta
					.getFecha().getTime()) : null);
			ps.setString(5, respuesta.getUsuario());
			ps.setString(6, respuesta.getFirmaE());
			ps.setString(7, respuesta.getAutFirmaE());
			ps.setDate(8, respuesta.getFechaFirmaE() != null ? new Date(
					respuesta.getFechaFirmaE().getTime()) : null);
			ps.setString(9, respuesta.getComprobanteFirmaE());
			ps.setString(10, respuesta.getClaveAcceso());
			/*
			 * NClob clob = conn.createNClob(); clob.setString(1,
			 * respuesta.getObservacion()); ps.setNClob(11, clob);
			 */
			ps.setString(11, respuesta.getObservacion());
			ps.setString(12, respuesta.getCorreoElectronico());
			ps.setString(13, respuesta.getEstado());
			ps.setString(14, respuesta.getTipoProceso());

			ps.setLong(15, respuesta.getIdFarmacia());
			ps.setLong(16, respuesta.getIdDocumento());
			ps.setLong(17, respuesta.getTipoComprobante());

			ps.executeUpdate();
			// conn.commit();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			/*
			 * try { conn.rollback(); } catch (SQLException e1) {
			 * log.error(e1.getMessage()); }
			 */
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Respuesta> listar(CredencialDS credencialDS, Long farmacia,
			String estado, Long tipo) throws GizloException {
		Connection conn = null;
		PreparedStatement ps = null;
		List<Respuesta> listado = new ArrayList<Respuesta>();
		Respuesta respuesta = null;
		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select * from farmacias.FA_DATOS_SRI_ELECTRONICA where farmacia = ? and TIPO_COMPROBANTE = ? ");

			if (estado != null && !estado.isEmpty()) {
				sqlString.append("and estado = ?");
			}

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, tipo);

			if (estado != null && !estado.isEmpty()) {
				ps.setString(3, estado);
			}

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				respuesta = new Respuesta();
				respuesta.setId(set.getLong("NUMERO_INTERNO"));
				respuesta.setIdFarmacia(set.getLong("FARMACIA"));
				respuesta.setIdDocumento(set.getLong("DOCUMENTO"));
				respuesta.setTipoComprobante(set.getLong("TIPO_COMPROBANTE"));
				respuesta.setFecha(set.getDate("FECHA"));
				respuesta.setUsuario(set.getString("USUARIO"));
				respuesta.setFirmaE(set.getString("FIRMAE"));
				respuesta.setAutFirmaE(set.getString("AUT_FIRMAE"));
				respuesta.setFecha(set.getDate("FECHA_FIRMAE"));
				respuesta.setComprobanteFirmaE(set
						.getString("COMPROBANTE_FIRMAE"));
				respuesta.setClaveAcceso(set.getString("CLAVE_ACCESO"));
				respuesta.setObservacion(set.getString("OBSERVACION_ELEC"));
				/*
				 * respuesta .setObservacion(set.getNClob("OBSERVACION_ELEC") !=
				 * null ? set .getNClob("OBSERVACION_ELEC").toString() : null);
				 */
				respuesta.setCorreoElectronico(set
						.getString("CORREO_ELECTRONICO"));
				respuesta.setEstado(set.getString("ESTADO"));
				respuesta.setTipoProceso(set.getString("TIPO_PROCESO"));
				listado.add(respuesta);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
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

		return listado;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Respuesta consultar(Connection conn, Long farmacia, Long documento,
			String estado, Long tipo) throws GizloException {
		PreparedStatement ps = null;
		Respuesta respuesta = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select * from farmacias.FA_DATOS_SRI_ELECTRONICA where farmacia = ? and documento = ? and TIPO_COMPROBANTE = ? ");

			if (estado != null && !estado.isEmpty()) {
				sqlString.append("and estado = ?");
			}

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documento);
			ps.setLong(3, tipo);

			if (estado != null && !estado.isEmpty()) {
				ps.setString(4, estado);
			}

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				respuesta = new Respuesta();
				respuesta.setId(set.getLong("NUMERO_INTERNO"));
				respuesta.setIdFarmacia(set.getLong("FARMACIA"));
				respuesta.setIdDocumento(set.getLong("DOCUMENTO"));
				respuesta.setTipoComprobante(set.getLong("TIPO_COMPROBANTE"));
				respuesta.setFecha(set.getDate("FECHA"));
				respuesta.setUsuario(set.getString("USUARIO"));
				respuesta.setFirmaE(set.getString("FIRMAE"));
				respuesta.setAutFirmaE(set.getString("AUT_FIRMAE"));
				respuesta.setFecha(set.getDate("FECHA_FIRMAE"));
				respuesta.setComprobanteFirmaE(set
						.getString("COMPROBANTE_FIRMAE"));
				respuesta.setClaveAcceso(set.getString("CLAVE_ACCESO"));
				respuesta.setObservacion(set.getString("OBSERVACION_ELEC"));
				/*
				 * respuesta .setObservacion(set.getNClob("OBSERVACION_ELEC") !=
				 * null ? set .getNClob("OBSERVACION_ELEC").toString() : null);
				 */
				respuesta.setCorreoElectronico(set
						.getString("CORREO_ELECTRONICO"));
				respuesta.setEstado(set.getString("ESTADO"));
				respuesta.setTipoProceso(set.getString("TIPO_PROCESO"));
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return respuesta;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Long> obtenerComprobantes(CredencialDS credencialDS,
			Long farmacia, String estado, Long tipo) throws GizloException {
		Connection conn = null;
		PreparedStatement ps = null;
		List<Long> listado = new ArrayList<Long>();

		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select * from farmacias.FA_DATOS_SRI_ELECTRONICA where farmacia = ? and TIPO_COMPROBANTE = ? ");

			if (estado != null && !estado.isEmpty()) {
				sqlString.append("and (estado = ? or estado is null)");
			}

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, tipo);

			if (estado != null && !estado.isEmpty()) {
				ps.setString(3, estado);
			}

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				listado.add(set.getLong("DOCUMENTO"));
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return listado;
	}
}
