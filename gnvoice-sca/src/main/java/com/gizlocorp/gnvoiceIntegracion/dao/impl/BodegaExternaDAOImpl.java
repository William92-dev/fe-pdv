package com.gizlocorp.gnvoiceIntegracion.dao.impl;

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

import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
//import com.gizlocorp.gnvoiceFybeca.utilitario.ConexionIntegracion;
import com.gizlocorp.gnvoiceIntegracion.dao.BodegaExternaDAO;
import com.gizlocorp.gnvoiceIntegracion.model.BodegaExterna;

@Stateless
public class BodegaExternaDAOImpl implements BodegaExternaDAO {

	private static Logger log = Logger.getLogger(BodegaExternaDAOImpl.class.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<BodegaExterna> concsultarBodegaExterna(Connection conn,
			Date fechaDesde, Date fechaHasta, String movimiento,
			String identificacion, CredencialDS credencialDS) {
		log.info("***Ejecutando concsultarBodegaExterna dao***"+fechaDesde+fechaHasta+movimiento);

		PreparedStatement ps = null;
		ResultSet set = null;

		PreparedStatement ps2 = null;
		ResultSet set2 = null;

		List<String> claveAcceso = new ArrayList<String>();
		List<String> claveAcceso2 = new ArrayList<String>();
		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append(" select distinct ts.clave_acceso ");
			sqlString.append(" from ds_cliente_bodegas c, ds_transacciones t, ds_transaccion_datossri ts, ");
			sqlString.append(" ad_clasificacion_movimientos cm, ad_tipos_movimientos tm ");
			sqlString.append(" where  t.cliente = c.codigo_cba and   t.fecha >= ? and   t.fecha <= ? and ");
			sqlString.append(" t.empresa_cliente <> t.empresa and t.bodega = ts.bodega and t.documento_bodega = ts.documento_bodega ");
			sqlString.append(" and t.tsn_bodega is null and t.tipo_movimiento = cm.tipo_movimiento and t.clase_movimiento = cm.codigo and ");
			sqlString.append(" tm.codigo = cm.tipo_movimiento  and  t.tipo_movimiento  = ?  and ts.clave_acceso is not null ");
			
			ps = conn.prepareStatement(sqlString.toString());

			ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ps.setString(3, movimiento);

			set = ps.executeQuery();

			while (set.next()) {
				claveAcceso.add(set.getString("clave_acceso"));
			}

			StringBuilder sqlString2 = new StringBuilder();

			sqlString2.append(" select distinct t.clave_acceso ");
			sqlString2.append(" from in_facturas_x_cobrar t,in_detalle_facturas_x_cobrar ts, ");
			sqlString2.append(" ad_clasificacion_movimientos cm, ad_tipos_movimientos tm  ");
			sqlString2.append(" where  t.fecha >= ?  ");
			sqlString2.append(" and t.fecha <= ? and t.documento = ts.documento ");
			sqlString2.append(" and t.tipo_movimiento = cm.tipo_movimiento and  ");
			sqlString2.append(" t.clasificacion_movimiento = cm.codigo ");
			sqlString2.append(" and tm.codigo = cm.tipo_movimiento  and  t.tipo_documento  = ? and t.clave_acceso is not null ");

			ps2 = conn.prepareStatement(sqlString2.toString());

			ps2.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			ps2.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ps2.setString(3, movimiento);

			set2 = ps2.executeQuery();

			while (set2.next()) {
				claveAcceso2.add(set2.getString("clave_acceso"));
			}

			facturas = concsultarBodegaExternaIn(conn, fechaDesde, fechaHasta,movimiento, identificacion, claveAcceso, claveAcceso2);
		} catch (Exception e) {
			log.info(new StringBuilder().append("***Exception***").append(e).toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***").append(e).toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***").append(e).toString());
				log.error(e.getMessage(), e);
			}
		}

		log.info(new StringBuilder()
				.append("***DAO devuelve facturas de bodega externa:***")
				.append(facturas.size()).toString());

		return facturas;
	}

	public List<BodegaExterna> concsultarBodegaExternaIn(Connection conn,
			java.util.Date fechaDesde, java.util.Date fechaHasta,
			String movimiento, String identificacion, List<String> claveAcceso,
			List<String> claveAcceso2) {
		PreparedStatement ps = null;
		ResultSet set = null;

		PreparedStatement ps2 = null;
		ResultSet set2 = null;

		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		try {
			if ((claveAcceso != null) && (!claveAcceso.isEmpty())) {
				for (String itera : claveAcceso) {
					StringBuilder sqlString = new StringBuilder();

					sqlString.append(" select rownum codigo, t.fecha, t.valor_gravado, t.valor_no_gravado, t.descuento, t.iva, t.numero_factura, ts.clave_acceso, ");
					sqlString.append(" decode(ts.APROBADO_FIRMAE,'S','FIRMAELECTRONICA','AUTOIMPRESOR') TIPODOC, ");
					sqlString.append(" decode(ts.APROBADO_FIRMAE,'S',ts.aut_firmae ,t.autorizacion_sri) AUTORIZACION_SRI, ");
					sqlString.append(" tm.descripcion||' <> '||cm.descripcion movimiento, t.documento_bodega, t.bodega, t.tipo_movimiento, c.codigo_cba ");
					sqlString.append(" from ds_cliente_bodegas c, ds_transacciones t, ds_transaccion_datossri ts, ad_clasificacion_movimientos cm, ad_tipos_movimientos tm ");
					sqlString.append(" where  t.cliente = c.codigo_cba and   t.fecha >= ? and   t.fecha <= ? and ");
					sqlString.append(" t.empresa_cliente <> t.empresa and t.bodega = ts.bodega and t.documento_bodega = ts.documento_bodega  ");
					sqlString.append(" and t.tsn_bodega is null and t.tipo_movimiento = cm.tipo_movimiento and t.clase_movimiento = cm.codigo and ");
					sqlString.append(" tm.codigo = cm.tipo_movimiento  and  t.tipo_movimiento  = ?  and ts.clave_acceso = ? ");

					ps = conn.prepareStatement(sqlString.toString());
					ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
					ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
					ps.setString(3, movimiento);
					ps.setString(4, itera);

					set = ps.executeQuery();

					while (set.next()) {
						BodegaExterna bodegaExterna = new BodegaExterna();
						bodegaExterna.setCodigo(set.getString("codigo"));
						bodegaExterna.setFecha(set.getDate("fecha"));
						bodegaExterna.setNumeroFactura(set.getString("numero_factura"));
						bodegaExterna.setClaveAcceso(set.getString("clave_acceso"));
						bodegaExterna.setTipoDoc(set.getString("TIPODOC"));
						bodegaExterna.setNumeroAutorizacion(set.getString("AUTORIZACION_SRI"));
						facturas.add(bodegaExterna);
					}
				}

			}

			if ((claveAcceso2 != null) && (!claveAcceso2.isEmpty())) {
				for (String itera : claveAcceso2) {
					StringBuilder sqlString = new StringBuilder();

					sqlString.append(" select rownum codigo, t.fecha, t.venta, 0, t.descuento,   ");
					sqlString.append(" t.iva, decode(t.APROBADO_FIRMA_E,'S',t.comprobante_firma_e,t.numero_sri) numero_factura, t.clave_acceso,   ");
					sqlString.append(" decode(t.APROBADO_FIRMA_E,'S','FIRMAELECTRONICA','AUTOIMPRESOR') TIPODOC,   ");
					sqlString.append(" decode(t.APROBADO_FIRMA_E,'S',t.autorizacion_firma_e ,t.numero_sri) AUTORIZACION_SRI,   ");
					sqlString.append(" tm.descripcion||' <> '||cm.descripcion movimiento, t.documento, null  ,t.tipo_documento, t.cliente ");
					sqlString.append(" from in_facturas_x_cobrar t,in_detalle_facturas_x_cobrar ts, ad_clasificacion_movimientos cm, ad_tipos_movimientos tm   ");
					sqlString.append(" where  t.fecha >= ?   ");
					sqlString.append(" and t.fecha <= ? and t.documento = ts.documento  ");
					sqlString.append(" and t.tipo_movimiento = cm.tipo_movimiento and   ");
					sqlString.append(" t.clasificacion_movimiento = cm.codigo  ");
					sqlString.append(" and tm.codigo = cm.tipo_movimiento  and  t.tipo_documento  = ? and t.clave_acceso = ? ");

					ps2 = conn.prepareStatement(sqlString.toString());

					ps2.setDate(1, new java.sql.Date(fechaDesde.getTime()));
					ps2.setDate(2, new java.sql.Date(fechaHasta.getTime()));
					ps2.setString(3, movimiento);
					ps2.setString(4, itera);

					set2 = ps2.executeQuery();

					while (set2.next()) {
						BodegaExterna bodegaExterna = new BodegaExterna();
						bodegaExterna.setCodigo(set2.getString("codigo"));
						bodegaExterna.setFecha(set2.getDate("fecha"));
						bodegaExterna.setNumeroFactura(set2.getString("numero_factura"));
						bodegaExterna.setClaveAcceso(set2.getString("clave_acceso"));
						bodegaExterna.setTipoDoc(set2.getString("TIPODOC"));
						bodegaExterna.setNumeroAutorizacion(set2.getString("AUTORIZACION_SRI"));
						facturas.add(bodegaExterna);
					}
				}
			}

		} catch (Exception e) {
			log.info(new StringBuilder().append("***Exception***").append(e)
					.toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***")
						.append(e).toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder().append("***Exception***")
						.append(e).toString());
				log.error(e.getMessage(), e);
			}

		}

		return facturas;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<BodegaExterna> concsultarProveedoresExterna(Connection conn,
			java.util.Date fechaDesde, java.util.Date fechaHasta,
			String movimiento, String identificacion, CredencialDS credencialDS) {
		PreparedStatement ps = null;
		ResultSet set = null;
		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		List<String> claveAcceso = new ArrayList<String>();
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString.append("select distinct t.clave_acceso ");
			sqlString.append("from in_facturas_x_cobrar t,in_detalle_facturas_x_cobrar ts,   ad_clasificacion_movimientos cm, ad_tipos_movimientos tm    ");
			sqlString.append("where  t.fecha >= ? and   t.fecha <= ? ");
			sqlString.append("and t.documento = ts.documento ");
			sqlString.append("and t.tipo_movimiento = cm.tipo_movimiento  ");
			sqlString.append("and t.clasificacion_movimiento = cm.codigo   ");
			sqlString.append("and tm.codigo = cm.tipo_movimiento   ");
			sqlString.append("and t.tipo_documento  = ? ");
			sqlString.append("and t.clave_acceso is not null ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ps.setString(3, movimiento);

			set = ps.executeQuery();

			while (set.next()) {
				claveAcceso.add(set.getString("clave_acceso"));
			}

			facturas = concsultarProveedoresExternaIn(conn, fechaDesde, fechaHasta, movimiento, claveAcceso);
		} catch (Exception e) {
			log.info(new StringBuilder()
					.append("***Exception proveedores 1***").append(e)
					.toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder()
						.append("***Exception proveedores 2***").append(e)
						.toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder()
						.append("***Exception proveedores 3***").append(e)
						.toString());
				log.error(e.getMessage(), e);
			}

		}

		return facturas;
	}

	public List<BodegaExterna> concsultarProveedoresExternaIn(Connection conn,java.util.Date fechaDesde, java.util.Date fechaHasta,
			String movimiento, List<String> claveAcceso) {
		PreparedStatement ps = null;
		ResultSet set = null;
		List<BodegaExterna> facturas = new ArrayList<BodegaExterna>();
		try {
			if ((claveAcceso != null) && (!claveAcceso.isEmpty())) {
				for (String itera : claveAcceso) {
					StringBuilder sqlString = new StringBuilder();

					sqlString.append("select rownum codigo, t.fecha, t.venta as valor_gravado,0, t.descuento as descuento,  t.iva, ");
					sqlString.append("decode(t.APROBADO_FIRMA_E,'S',t.comprobante_firma_e,t.numero_sri) as numero_factura, t.clave_acceso,  ");
					sqlString.append("decode(t.APROBADO_FIRMA_E,'S','FIRMAELECTRONICA', 'AUTOIMPRESOR') TIPODOC,  ");
					sqlString.append("decode(t.APROBADO_FIRMA_E,'S',t.autorizacion_firma_e ,t.numero_sri) AUTORIZACION_SRI,  ");
					sqlString.append("tm.descripcion||' <> '|| cm.descripcion movimiento, t.documento as documento_bodega, null  ");
					sqlString.append("from in_facturas_x_cobrar t,in_detalle_facturas_x_cobrar ts,   ad_clasificacion_movimientos cm, ad_tipos_movimientos tm    ");
					sqlString.append("where  t.fecha >= ? and   t.fecha <= ? ");
					sqlString.append("and t.documento = ts.documento ");
					sqlString.append("and t.tipo_movimiento = cm.tipo_movimiento  ");
					sqlString.append("and t.clasificacion_movimiento = cm.codigo   ");
					sqlString.append("and tm.codigo = cm.tipo_movimiento   ");
					sqlString.append("and t.tipo_documento  = ? ");
					sqlString.append("and t.clave_acceso = ?");

					ps = conn.prepareStatement(sqlString.toString());
					ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));
					ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));
					ps.setString(3, movimiento);
					ps.setString(4, itera);

					set = ps.executeQuery();

					while (set.next()) {
						BodegaExterna bodegaExterna = new BodegaExterna();
						bodegaExterna.setCodigo(set.getString("codigo"));
						bodegaExterna.setFecha(set.getDate("fecha"));
						bodegaExterna.setNumeroFactura(set.getString("numero_factura"));
						bodegaExterna.setClaveAcceso(set.getString("clave_acceso"));
						bodegaExterna.setTipoDoc(set.getString("TIPODOC"));
						bodegaExterna.setNumeroAutorizacion(set.getString("AUTORIZACION_SRI"));
						facturas.add(bodegaExterna);
					}
				}

			}

		} catch (Exception e) {
			log.info(new StringBuilder()
					.append("***Exception proveedores 1***").append(e)
					.toString());
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (set != null)
					set.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.info(new StringBuilder()
						.append("***Exception proveedores 2***").append(e)
						.toString());
				log.error(e.getMessage(), e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.info(new StringBuilder()
						.append("***Exception proveedores 3***").append(e)
						.toString());
				log.error(e.getMessage(), e);
			}
		}

		log.info(new StringBuilder()
				.append("DAO devuelve facturas de provedores externa: movimiento")
				.append(movimiento).append(facturas.size()).toString());

		return facturas;
	}

}
