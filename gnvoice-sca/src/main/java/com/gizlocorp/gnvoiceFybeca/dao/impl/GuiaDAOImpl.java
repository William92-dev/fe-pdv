package com.gizlocorp.gnvoiceFybeca.dao.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.StringUtil;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.guia.Destinatario;
import com.gizlocorp.gnvoice.xml.guia.Detalle;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision.Destinatarios;
import com.gizlocorp.gnvoice.xml.guia.GuiaRemision.InfoGuiaRemision;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarRequest;
import com.gizlocorp.gnvoiceFybeca.dao.GuiaDAO;

@Stateless
public class GuiaDAOImpl implements GuiaDAO {

	private static Logger log = Logger.getLogger(GuiaDAOImpl.class.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public GuiaProcesarRequest listar(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException {

		PreparedStatement ps = null;

		GuiaProcesarRequest guiaFybeca = null;

		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select distinct fa_secs.secuencia as secuencial, mm.razon_social razonSocial, mm.razon_social nombreComercial, mm.ruc, ");
			sqlString.append("m.documento as codigo_externo, ");
			sqlString.append("m.guia_remision as numDocSustento, ");
			sqlString.append("substr(numero_ruc, 11, 3) as estab, ");
			sqlString.append("substr(m.guia_remision, 5, 3) as ptoEmi, ");
			sqlString.append("mm.direccion as dirEstablecimiento, ");
			sqlString
					.append("decode(rm.num2, null, 1002097, rm.num2) codigo_transportista, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_establecimiento, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_partida, ");
			sqlString
					.append("decode(rm.num2,null, 'CARLOS EDUARDO PAZMINO GAONA', ");
			sqlString
					.append("(p.primer_nombre||' '||p.segundo_nombre||' '||p.primer_apellido || ' ' || p.segundo_apellido)) razon_social_transportista, ");
			sqlString
					.append("decode(rm.num2,null,'C',p.tipo_identificacion) tipo_iden_transportista, ");
			sqlString
					.append("decode(rm.num2,null,'1714472873',p.identificacion) ruc_transportista, ");
			sqlString.append("'NO' obligado_contabilidad, ");
			sqlString.append("'NO' contribuyente_especial, ");
			sqlString.append("m.fecha fecha_inicio, ");
			sqlString.append("m.fecha fecha_fin, ");
			sqlString.append("decode(rm.car1,null,'POW-703',rm.car1) placa, ");
			sqlString.append("m.documento, m.farmacia, ");
			sqlString.append("m.clasificacion_movimiento, ");
			sqlString.append("m.tipo_movimiento ");
			sqlString.append("from fa_movimientos_mercaderia     m, ");
			sqlString.append("FA_OBSERVACIONES_MOVIMIENTO   o, ");
			sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
			sqlString.append("fa_movimientos_mercaderia_mas a, ");
			sqlString.append("fa_movimientos_mercaderia_adi mm, ");
			sqlString.append("ad_farmacias                  f, ");
			sqlString.append("ad_clasificacion_movimientos  cm, ");
			sqlString.append("fa_rutas_movmer               rm, ");
			sqlString.append("ab_personas                   p, ");
			sqlString.append("  ds_conductores_vehiculo       cv ");
			sqlString.append("where m.farmacia = O.farmacia(+) ");
			sqlString.append(" and m.documento = o.documento(+) ");
			sqlString.append("and fa_secs.documento_venta = m.documento ");
			sqlString.append("and fa_secs.farmacia = m.farmacia ");
			sqlString.append("and m.farmacia = a.farmacia(+) ");
			sqlString.append("and m.documento = a.documento(+) ");
			sqlString.append("and m.farmacia = mm.farmacia(+) ");
			sqlString.append("and m.documento = mm.documento(+) ");
			sqlString.append("and m.farmacia = f.codigo(+) ");
			sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
			sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
			sqlString.append("and m.farmacia = rm.farmacia(+) ");
			sqlString.append("and m.documento = rm.documento(+) ");
			sqlString.append("and p.codigo(+) = rm.num2 ");
			sqlString.append("and cv.persona(+) = rm.num2 ");
			sqlString.append("and cv.placa(+) = rm.car1 ");
			sqlString.append("and fa_secs.tipo_documento = ? ");
			sqlString
					.append("and m.tipo_movimiento || m.clasificacion_movimiento <> '0401' ");
			sqlString.append("and m.farmacia = ? ");
			sqlString.append("and m.documento = ? ");

			sqlString
					.append("and (rm.secuencia = ( select max(secuencia) from fa_rutas_movmer q where m.documento = q.documento ");
			sqlString
					.append(" and q.farmacia = m.farmacia) or rm.secuencia is null ) ");

			sqlString.append("union ");
			sqlString
					.append("select distinct fa_secs.secuencia as secuencial, mm.razon_social razonSocial, mm.razon_social nombreComercial, mm.ruc, ");
			sqlString.append("m.documento as codigo_externo, ");
			sqlString.append("m.guia_remision as numDocSustento, ");
			sqlString.append("substr(numero_ruc, 11, 3) as estab, ");
			sqlString.append("substr(m.guia_remision, 5, 3) as ptoEmi, ");
			sqlString.append("mm.direccion as dirEstablecimiento, ");
			sqlString.append("a.cp_num3 codigo_transportista, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_establecimiento, ");
			sqlString
					.append("f.calle||' '||f.numero||' '||f.interseccion dir_partida, ");
			sqlString
					.append("t.primer_nombre||' '||t.segundo_nombre||' '||t.primer_apellido ||' ' || t.segundo_apellido razon_social_transportista, ");
			sqlString.append("t.tipo_identificacion tipo_iden_transportista, ");
			sqlString.append("t.identificacion ruc_transportista, ");
			sqlString.append("'NO' obligado_contabilidad, ");
			sqlString.append("'NO' contribuyente_especial, ");
			sqlString.append("m.fecha fecha_inicio, ");
			sqlString.append("m.fecha fecha_fin, ");
			sqlString.append("p.placa, ");
			sqlString.append("m.documento, ");
			sqlString.append("m.farmacia, ");
			sqlString.append("m.clasificacion_movimiento, ");
			sqlString.append("m.tipo_movimiento ");
			sqlString.append("from fa_movimientos_mercaderia     m, ");
			sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
			sqlString.append("   FA_OBSERVACIONES_MOVIMIENTO   o, ");
			sqlString.append("fa_movimientos_mercaderia_mas a, ");
			sqlString.append("fa_movimientos_mercaderia_adi mm, ");
			sqlString.append("ad_farmacias                  f, ");
			sqlString.append("fa_datos_transportista        t, ");
			sqlString.append("fa_datos_vehiculo             p, ");
			sqlString.append("ad_clasificacion_movimientos  cm ");
			sqlString.append("where m.farmacia = O.farmacia(+) ");
			sqlString.append("and m.documento = o.documento(+) ");
			sqlString.append("and m.farmacia = a.farmacia(+) ");
			sqlString.append("and m.documento = a.documento(+) ");
			sqlString.append("and m.farmacia = mm.farmacia(+) ");
			sqlString.append("and m.documento = mm.documento(+) ");
			sqlString.append("and m.farmacia = f.codigo(+) ");
			sqlString.append("and a.cp_num3 = t.id(+) ");
			sqlString.append("and t.id = p.id(+) ");
			sqlString.append("and fa_secs.documento_venta = m.documento ");
			sqlString.append("and fa_secs.farmacia = m.farmacia ");
			sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
			sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
			sqlString.append("and m.TIPO_MOVIMIENTO = '04' ");
			sqlString.append("AND m.CLASIFICACION_MOVIMIENTO = '01' ");
			sqlString.append("and fa_secs.tipo_documento = ? ");
			sqlString.append("and m.farmacia = ? ");
			sqlString.append("and m.documento = ? ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, tipo);
			ps.setLong(2, farmacia);

			ps.setLong(3, documentoVenta);

			ps.setLong(4, tipo);
			ps.setLong(5, farmacia);
			ps.setLong(6, documentoVenta);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				guiaFybeca = null;
				try {
					guiaFybeca = obtenerGuia(conn, set, farmacia, tipo);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}
				if (guiaFybeca != null) {
					String correo = obtenerCorreo(conn,
							Long.valueOf(guiaFybeca.getCodigoExterno()),
							farmacia);

					guiaFybeca.setCorreoElectronicoNotificacion(StringUtil
							.validateEmail(correo));

					List<Destinatario> destinatarios = listarDestinatarios(
							conn, Long.valueOf(guiaFybeca.getCodigoExterno()),
							farmacia, tipo);// ,farmacia
					log.debug("**servicio listar destinatarios: "
							+ (destinatarios != null ? destinatarios.size() : 0));
					if (destinatarios != null && !destinatarios.isEmpty()) {
						for (Destinatario destinatario : destinatarios) {
							List<Detalle> detalles = listarDetallesDestinatario(
									conn,
									destinatario
											.getIdentificacionDestinatario(),
									Long.valueOf(guiaFybeca.getCodigoExterno()),
									farmacia);
							log.debug("**servicio listar total con detalles: "
									+ (detalles != null ? detalles.size() : 0));
							if (detalles != null && !detalles.isEmpty()) {
								destinatario
										.setDetalles(new Destinatario.Detalles());
								destinatario.getDetalles().getDetalle()
										.addAll(detalles);
							}
						}
						guiaFybeca.getGuia().setDestinatarios(
								new Destinatarios());
						guiaFybeca.getGuia().getDestinatarios()
								.getDestinatario().addAll(destinatarios);
					}

					break;
				}
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
		}

		return guiaFybeca;

	}

	// @Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	// public List<String> listarDocumentos(CredencialDS credencialDS,
	// Long farmacia, Long tipo) throws GizloException {
	//
	// Connection conn = null;
	// PreparedStatement ps = null;
	//
	// List<String> guias = new ArrayList<String>();
	//
	// try {
	// conn = Conexion.obtenerConexionFybeca(credencialDS);
	// StringBuilder sqlString = new StringBuilder();
	//
	// sqlString.append("select m.documento as codigo_externo ");
	// sqlString.append("from fa_movimientos_mercaderia     m, ");
	// sqlString.append("FA_OBSERVACIONES_MOVIMIENTO   o, ");
	// sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
	// sqlString.append("fa_movimientos_mercaderia_mas a, ");
	// sqlString.append("fa_movimientos_mercaderia_adi mm, ");
	// sqlString.append("ad_farmacias                  f, ");
	// sqlString.append("ad_clasificacion_movimientos  cm, ");
	// sqlString.append("fa_rutas_movmer               rm, ");
	// sqlString.append("ab_personas                   p, ");
	// sqlString.append("  ds_conductores_vehiculo       cv ");
	// sqlString.append("where m.farmacia = O.farmacia(+) ");
	// sqlString.append(" and m.documento = o.documento(+) ");
	// sqlString.append("and fa_secs.documento_venta = m.documento ");
	// sqlString.append("and fa_secs.farmacia = m.farmacia ");
	// sqlString.append("and m.farmacia = a.farmacia(+) ");
	// sqlString.append("and m.documento = a.documento(+) ");
	// sqlString.append("and m.farmacia = mm.farmacia(+) ");
	// sqlString.append("and m.documento = mm.documento(+) ");
	// sqlString.append("and m.farmacia = f.codigo(+) ");
	// sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
	// sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
	// sqlString.append("and m.farmacia = rm.farmacia(+) ");
	// sqlString.append("and m.documento = rm.documento(+) ");
	// sqlString.append("and p.codigo(+) = rm.num2 ");
	// sqlString.append("and cv.persona(+) = rm.num2 ");
	// sqlString.append("and cv.placa(+) = rm.car1 ");
	// sqlString.append("and fa_secs.tipo_documento = ? ");
	// sqlString
	// .append("and m.tipo_movimiento || m.clasificacion_movimiento <> '0401' ");
	// sqlString.append("and m.farmacia = ? ");
	// sqlString
	// .append("and m.documento not in (select documento from farmacias.FA_DATOS_SRI_ELECTRONICA where farmacia = ? and TIPO_COMPROBANTE = ? and estado = 'A'  or estado = 'C') ");
	//
	// sqlString.append("union ");
	// sqlString.append("select m.documento as codigo_externo ");
	// sqlString.append("from fa_movimientos_mercaderia     m, ");
	// sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
	// sqlString.append("   FA_OBSERVACIONES_MOVIMIENTO   o, ");
	// sqlString.append("fa_movimientos_mercaderia_mas a, ");
	// sqlString.append("fa_movimientos_mercaderia_adi mm, ");
	// sqlString.append("ad_farmacias                  f, ");
	// sqlString.append("fa_datos_transportista        t, ");
	// sqlString.append("fa_datos_vehiculo             p, ");
	// sqlString.append("ad_clasificacion_movimientos  cm ");
	// sqlString.append("where m.farmacia = O.farmacia(+) ");
	// sqlString.append("and m.documento = o.documento(+) ");
	// sqlString.append("and m.farmacia = a.farmacia(+) ");
	// sqlString.append("and m.documento = a.documento(+) ");
	// sqlString.append("and m.farmacia = mm.farmacia(+) ");
	// sqlString.append("and m.documento = mm.documento(+) ");
	// sqlString.append("and m.farmacia = f.codigo(+) ");
	// sqlString.append("and a.cp_num3 = t.id(+) ");
	// sqlString.append("and t.id = p.id(+) ");
	// sqlString.append("and fa_secs.documento_venta = m.documento ");
	// sqlString.append("and fa_secs.farmacia = m.farmacia ");
	// sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
	// sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
	// sqlString.append("and m.TIPO_MOVIMIENTO = '04' ");
	// sqlString.append("AND m.CLASIFICACION_MOVIMIENTO = '01' ");
	// sqlString.append("and fa_secs.tipo_documento = ? ");
	// sqlString.append("and m.farmacia = ? ");
	// sqlString
	// .append("and m.documento not in (select documento from farmacias.FA_DATOS_SRI_ELECTRONICA where farmacia = ? and TIPO_COMPROBANTE = ? and estado = 'A'  or estado = 'C') ");
	//
	// ps = conn.prepareStatement(sqlString.toString());
	// ps.setLong(1, tipo);
	// ps.setLong(2, farmacia);
	//
	// ps.setLong(3, farmacia);
	// ps.setLong(4, tipo);
	//
	// ps.setLong(5, tipo);
	// ps.setLong(6, farmacia);
	// ps.setLong(7, farmacia);
	// ps.setLong(8, tipo);
	//
	// ResultSet set = ps.executeQuery();
	//
	// while (set.next()) {
	// guias.add(set.getLong("codigo_externo") + "");
	// }
	//
	// } catch (Exception e) {
	// log.error(e.getMessage(), e);
	// } finally {
	// try {
	// if (ps != null)
	// ps.close();
	// } catch (Exception e) {
	// log.error(e.getMessage(), e);
	// }
	// try {
	// if (conn != null)
	// conn.close();
	// } catch (Exception e) {
	// log.error(e.getMessage(), e);
	// }
	// }
	//
	// return guias;
	//
	// }

	private GuiaProcesarRequest obtenerGuia(Connection conn, ResultSet set,
			Long farmacia, Long tipo) throws SQLException, GizloException {

		GuiaProcesarRequest guiaProcesarRequest = null;
		log.debug("Guia Generar: RUC: " + set.getString("ruc"));
		String tipoIdentificacion = "";
		GuiaRemision guia = new GuiaRemision();

		// INFO TRIBUTARIA
		InfoTributaria infoTributaria = new InfoTributaria();
		// infoTributaria.setCodDoc("04");
		infoTributaria.setEstab(set.getString("estab"));
		infoTributaria.setPtoEmi(set.getString("ptoEmi"));
		infoTributaria.setRuc(set.getString("ruc"));

		infoTributaria.setSecuencial(set.getLong("secuencial") + "");

		guia.setInfoTributaria(infoTributaria);

		// INFO GUIA
		InfoGuiaRemision infoGuiaRemision = new InfoGuiaRemision();

		String dirEstablecimiento = set.getString("dir_establecimiento");
		dirEstablecimiento = StringUtil.validateInfoXML(dirEstablecimiento);

		infoGuiaRemision
				.setDirEstablecimiento(dirEstablecimiento != null ? StringEscapeUtils
						.escapeXml(dirEstablecimiento.trim())
						.replaceAll("[\n]", "").replaceAll("[\b]", "")
						: null);

		String dirPartida = set.getString("dir_partida");
		dirPartida = StringUtil.validateInfoXML(dirPartida);

		infoGuiaRemision.setDirPartida(dirPartida.trim());

		infoGuiaRemision
				.setFechaIniTransporte((set.getDate("fecha_inicio") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fecha_inicio").getTime()),
								FechaUtil.DATE_FORMAT) : null));
		infoGuiaRemision
				.setFechaFinTransporte((set.getDate("fecha_fin") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fecha_fin").getTime()),
								FechaUtil.DATE_FORMAT) : null));

		infoGuiaRemision.setPlaca(set.getString("placa"));

		String razonSocialComprador = set
				.getString("razon_social_transportista");
		razonSocialComprador = StringUtil.validateInfoXML(razonSocialComprador);

		infoGuiaRemision.setRazonSocialTransportista(razonSocialComprador
				.trim());
		infoGuiaRemision
				.setRucTransportista(set.getString("ruc_transportista"));

		if (set.getString("tipo_iden_transportista") != null
				&& !set.getString("tipo_iden_transportista").isEmpty()) {
			tipoIdentificacion = set.getString("tipo_iden_transportista");

			if (tipoIdentificacion != null && !tipoIdentificacion.isEmpty()) {

				if (tipoIdentificacion.equalsIgnoreCase("C")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("05");
				} else if (tipoIdentificacion.equalsIgnoreCase("R")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("04");
				} else if (tipoIdentificacion.equalsIgnoreCase("P")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("06");
				} else if (tipoIdentificacion.equalsIgnoreCase("N")) {
					infoGuiaRemision.setTipoIdentificacionTransportista("07");
				}
			}

			if (infoGuiaRemision != null
					&& infoGuiaRemision.getTipoIdentificacionTransportista() != null
					&& infoGuiaRemision.getTipoIdentificacionTransportista()
							.equalsIgnoreCase("07")) {
				infoGuiaRemision
						.setTipoIdentificacionTransportista("9999999999999");
				infoGuiaRemision
						.setRazonSocialTransportista("CONSUMIDOR FINAL");
			} else {
				String identificacion = set
						.getString("tipo_iden_transportista");
				infoGuiaRemision.setTipoIdentificacionTransportista("06");

				if (StringUtil.esNumero(identificacion)) {
					if (identificacion.length() == 10) {
						infoGuiaRemision
								.setTipoIdentificacionTransportista("05");
					} else if (identificacion.length() == 13
							&& identificacion.substring(10).equals("001")) {
						infoGuiaRemision
								.setTipoIdentificacionTransportista("04");
					}
				}
			}
		} else {
			infoGuiaRemision
					.setTipoIdentificacionTransportista("9999999999999");
			infoGuiaRemision.setTipoIdentificacionTransportista("07");
			infoGuiaRemision.setRazonSocialTransportista("CONSUMIDOR FINAL");
		}

		guia.setInfoGuiaRemision(infoGuiaRemision);

		guiaProcesarRequest = new GuiaProcesarRequest();
		guiaProcesarRequest.setGuia(guia);
		guiaProcesarRequest
				.setCodigoExterno(set.getLong("codigo_externo") + "");
		guiaProcesarRequest.setIdentificadorUsuario(null);
		guiaProcesarRequest.setAgencia(farmacia.toString());
		return guiaProcesarRequest;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private String obtenerIdentificacion(Connection conn, String identificacion)
			throws GizloException {

		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select ap.tipo_identificacion as tipoIdentificacion from ab_personas ap where ap.identificacion = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, identificacion);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("tipoIdentificacion");
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

		return resultado;
	}

	private List<Detalle> listarDetallesDestinatario(Connection conn,
			String idDestinatario, Long idGuia, Long idFarmacia)
			throws GizloException {
		try {
			PreparedStatement ps = null;
			List<Detalle> detalles = new ArrayList<Detalle>();
			Detalle detalleXML = null;

			try {
				StringBuilder sqlString = new StringBuilder();

				log.debug("Guia Remision:  " + idGuia + " " + idFarmacia);
				sqlString
						.append("select item, (p.nombre || ' ' || i.presentacion) as descripcion, cantidad from fa_detalles_otros_movimientos d, ");
				sqlString
						.append("pr_items i, pr_productos p where d.item = i.codigo and i.producto  = p.codigo ");
				sqlString.append("and d.documento = ? and d.farmacia = ? ");

				ps = conn.prepareStatement(sqlString.toString());
				ps.setLong(1, idGuia);
				ps.setLong(2, idFarmacia);

				ResultSet set = ps.executeQuery();
				String descripcion = null;

				while (set.next()) {
					detalleXML = new Detalle();
					detalleXML.setCantidad(new BigDecimal(set
							.getLong("cantidad")));
					detalleXML.setCodigoInterno(set.getLong("item") + "");

					descripcion = set.getString("descripcion");
					descripcion = StringUtil.validateInfoXML(descripcion);

					detalleXML.setDescripcion(descripcion);
					detalles.add(detalleXML);
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

			return detalles;
		} catch (Exception e) {
			log.error("Error listar destinatarios" + e.getMessage());
			throw new GizloException("Error al listar destinatarios", e);
		}
	}

	private List<Destinatario> listarDestinatarios(Connection conn,
			Long documentoVenta, Long farmacia, Long tipo)
			throws GizloException {
		try {
			PreparedStatement ps = null;
			List<Destinatario> destinatarios = new ArrayList<Destinatario>();
			Destinatario destinatarioXML = null;

			try {
				StringBuilder sqlString = new StringBuilder();

				sqlString
						.append("select mm.ruc identificacion_dstino, mm.razon_social razon_social_dstino, ");
				sqlString
						.append("(select calle || ' ' || numero || ' ' || interseccion from ad_bodegas where codigo = m.bodega) dir_destino, ");
				sqlString.append("cm.descripcion motivo_traslado, ");
				sqlString
						.append("(select substr(numero_ruc, 11, 3) from ad_bodegas where codigo = m.bodega) estab_destino, ");
				sqlString
						.append("null ruta, m.documento codigo_externo, m.guia_remision doc_sustento, null numero_autorizacion, ");
				sqlString.append("m.fecha fecha_emision ");
				sqlString.append("from fa_movimientos_mercaderia     m, ");
				sqlString.append("FA_OBSERVACIONES_MOVIMIENTO   o, ");
				sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
				sqlString.append("fa_movimientos_mercaderia_mas a, ");
				sqlString.append("fa_movimientos_mercaderia_adi mm, ");
				sqlString.append("ad_farmacias                  f, ");
				sqlString.append("ad_clasificacion_movimientos  cm, ");
				sqlString.append("fa_rutas_movmer               rm, ");
				sqlString.append("ab_personas                   p, ");
				sqlString.append("  ds_conductores_vehiculo       cv ");
				sqlString.append("where m.farmacia = O.farmacia(+) ");
				sqlString.append(" and m.documento = o.documento(+) ");
				sqlString.append("and fa_secs.documento_venta = m.documento ");
				sqlString.append("and fa_secs.farmacia = m.farmacia ");
				sqlString.append("and m.farmacia = a.farmacia(+) ");
				sqlString.append("and m.documento = a.documento(+) ");
				sqlString.append("and m.farmacia = mm.farmacia(+) ");
				sqlString.append("and m.documento = mm.documento(+) ");
				sqlString.append("and m.farmacia = f.codigo(+) ");
				sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
				sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
				sqlString.append("and m.farmacia = rm.farmacia(+) ");
				sqlString.append("and m.documento = rm.documento(+) ");
				sqlString.append("and p.codigo(+) = rm.num2 ");
				sqlString.append("and cv.persona(+) = rm.num2 ");
				sqlString.append("and cv.placa(+) = rm.car1 ");
				sqlString.append("and fa_secs.tipo_documento = ? ");
				sqlString
						.append("and m.tipo_movimiento || m.clasificacion_movimiento <> '0401' ");
				sqlString.append("and m.farmacia = ? ");
				sqlString.append("and m.documento = ? ");

				sqlString
						.append("and (rm.secuencia = ( select max(secuencia) from fa_rutas_movmer q where m.documento = q.documento ");
				sqlString
						.append(" and q.farmacia = m.farmacia) or rm.secuencia is null ) ");

				sqlString.append("union ");

				sqlString
						.append("select mm.ruc identificacion_dstino, mm.razon_social razon_social_dstino, ");
				sqlString
						.append("(select calle || ' ' || numero || ' ' || interseccion from ad_farmacias where codigo = m.farmacia_transaccion) dir_destino, ");
				sqlString.append("cm.descripcion motivo_traslado, ");
				sqlString
						.append("(select substr(numero_ruc, 11, 3) from ad_farmacias where codigo = m.farmacia_transaccion) estab_destino, ");
				sqlString
						.append("null ruta, m.documento codigo_externo, m.guia_remision doc_sustento, null numero_autorizacion, ");
				sqlString.append("m.fecha fecha_emision ");
				sqlString.append("from fa_movimientos_mercaderia     m, ");
				sqlString.append("fa_secuencias_fact_elec       fa_secs, ");
				sqlString.append("   FA_OBSERVACIONES_MOVIMIENTO   o, ");
				sqlString.append("fa_movimientos_mercaderia_mas a, ");
				sqlString.append("fa_movimientos_mercaderia_adi mm, ");
				sqlString.append("ad_farmacias                  f, ");
				sqlString.append("fa_datos_transportista        t, ");
				sqlString.append("fa_datos_vehiculo             p, ");
				sqlString.append("ad_clasificacion_movimientos  cm ");
				sqlString.append("where m.farmacia = O.farmacia(+) ");
				sqlString.append("and m.documento = o.documento(+) ");
				sqlString.append("and m.farmacia = a.farmacia(+) ");
				sqlString.append("and m.documento = a.documento(+) ");
				sqlString.append("and m.farmacia = mm.farmacia(+) ");
				sqlString.append("and m.documento = mm.documento(+) ");
				sqlString.append("and m.farmacia = f.codigo(+) ");
				sqlString.append("and a.cp_num3 = t.id(+) ");
				sqlString.append("and t.id = p.id(+) ");
				sqlString.append("and fa_secs.documento_venta = m.documento ");
				sqlString.append("and fa_secs.farmacia = m.farmacia ");
				sqlString.append("and m.clasificacion_movimiento = cm.codigo ");
				sqlString.append("and m.tipo_movimiento = cm.tipo_movimiento ");
				sqlString.append("and m.TIPO_MOVIMIENTO = '04' ");
				sqlString.append("AND m.CLASIFICACION_MOVIMIENTO = '01' ");
				sqlString.append("and fa_secs.tipo_documento = ? ");
				sqlString.append("and m.farmacia = ? ");
				sqlString.append("and m.documento = ? ");

				ps = conn.prepareStatement(sqlString.toString());
				ps.setLong(1, tipo);
				ps.setLong(2, farmacia);
				ps.setLong(3, documentoVenta);
				ps.setLong(4, tipo);
				ps.setLong(5, farmacia);
				ps.setLong(6, documentoVenta);

				ResultSet set = ps.executeQuery();
				String razonSocialDestinatario = null;
				String motivoTraslado = null;

				while (set.next()) {
					destinatarioXML = new Destinatario();
					destinatarioXML.setCodDocSustento("01");
					destinatarioXML.setCodEstabDestino(set
							.getString("estab_destino"));
					destinatarioXML.setDirDestinatario(set
							.getString("dir_destino"));
					destinatarioXML.setDocAduaneroUnico(null);
					destinatarioXML.setFechaEmisionDocSustento(set
							.getDate("fecha_emision") != null ? FechaUtil
							.formatearFecha(
									FechaUtil.convertirLongADate(set.getDate(
											"fecha_emision").getTime()),
									FechaUtil.DATE_FORMAT) : null);
					destinatarioXML.setIdentificacionDestinatario(set
							.getString("identificacion_dstino"));

					motivoTraslado = set.getString("motivo_traslado");
					motivoTraslado = StringUtil.validateInfoXML(motivoTraslado);

					destinatarioXML.setMotivoTraslado(motivoTraslado);

					// destinatarioXML.setNumAutDocSustento(set.getString("doc_sustento"));
					destinatarioXML.setNumDocSustento(set.getString(
							"doc_sustento").replace("-152-", "-501-"));

					razonSocialDestinatario = set
							.getString("razon_social_dstino");
					razonSocialDestinatario = StringUtil
							.validateInfoXML(razonSocialDestinatario);

					destinatarioXML
							.setRazonSocialDestinatario(razonSocialDestinatario);
					destinatarios.add(destinatarioXML);
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

			return destinatarios;
		} catch (Exception e) {
			log.error("Error listar destinatarios" + e.getMessage(), e);
			throw new GizloException("Error al listar destinatarios", e);
		}
	}

	private String obtenerCorreo(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException {

		// PreparedStatement ps = null;
		CallableStatement cstmt = null;
		String correo = null;

		try {
			StringBuilder sqlString = new StringBuilder();

			// sqlString.append("select * from ab_medios_contacto where tipo = ? and persona in (select codigo from ab_personas where identificacion = ?) and ROWNUM <=1");
			sqlString
					.append("{call farmacias.fa_pkg_mov_general.pro_recupera_mail_cliente(?,?,?)}");
			cstmt = conn.prepareCall(sqlString.toString());
			cstmt.registerOutParameter(3, Types.VARCHAR);

			cstmt.setLong(1, documentoVenta);
			cstmt.setLong(2, farmacia);
			cstmt.execute();
			correo = cstmt.getString(3);

			// ps = conn.prepareStatement(sqlString.toString());
			// ps.setLong(1, tipo);
			// ps.setString(2, identificacion);

			// ResultSet set = ps.executeQuery();

			// while (set.next()) {
			// correo = set.getString("VALOR");
			// }

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				// if (ps != null)
				// ps.close();
				if (cstmt != null)
					cstmt.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return correo;

	}
}
