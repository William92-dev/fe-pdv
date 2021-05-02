package com.gizlocorp.gnvoiceFybeca.dao.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.utilitario.FechaUtil;
import com.gizlocorp.adm.utilitario.StringUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.notacredito.Impuesto;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito.Compensaciones;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito.Compensaciones.Compensacion;
import com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos;
import com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoiceFybeca.dao.FacturaDAO;
import com.gizlocorp.gnvoiceFybeca.dao.NotaCreditoDAO;
import com.gizlocorp.gnvoiceFybeca.dao.TipoComprobanteDAO;
import com.gizlocorp.gnvoiceFybeca.enumeration.ComprobanteEnum;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;

@Stateless
public class NotaCreditoDAOImpl implements NotaCreditoDAO {

	private static Logger log = Logger.getLogger(NotaCreditoDAOImpl.class
			.getName());

	@EJB
	TipoComprobanteDAO tipoComprobanteDAO;

	@EJB
	FacturaDAO facturaDAO;

	boolean cartorce= false;
	boolean boodoce= false;
	
	/**
	 * Busca facturas por documentoVenta
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public NotaCreditoProcesarRequest listar(Connection conn,
			Long documentoVenta, Long farmacia, Long tipo)
			throws GizloException {

		PreparedStatement ps = null;

		NotaCreditoProcesarRequest notaCreditoFybeca = null;

		try {
			StringBuilder sqlString = new StringBuilder();

			// substr(fa.numero_ruc, 11, 3) establecimiento,
			// substr(f.numero_sri, 5, 3) punto_emision,

			sqlString
					.append("select fa_secs.secuencia as secuencial, f.fecha as fechaEmision, c.calle||' '||c.numero||' '||c.interseccion as dirEstablecimiento, fa.cp_char3 as contribuyenteEspecial, ");
			sqlString
					.append("'SI' as obligadoContabilidad, f.primer_apellido||' '|| f.segundo_apellido||' '|| f.nombres as razonSocialComprador, c.NOMBRE as nombreSucursal, ");
			sqlString
					.append("f.identificacion as identificacionComprador, ROUND((f.venta_total_factura - f.valor_iva),2) as totalSinImpuestos, ");
			sqlString
					.append("'0' as totalDescuento, '0.00' as propina, ROUND(venta_total_factura,2) as importeTotal, 'DOLAR' as moneda, ");
			sqlString
					.append("'1' as ambiente, '1' as tipoEmision, fa.cp_var3 as razonSocial, fa.cp_var3 as nombreComercial, fa.cp_var6 as ruc, ");
			sqlString
					.append("substr(c.numero_ruc, 11, 3) as estab, substr(f.numero_sri, 5, 3) as ptoEmi, f.documento_venta as codigo_externo, ");
			sqlString.append("f.documento_venta_padre as factura_codigo "); // FACTURA
			sqlString
					.append("from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString
					.append("and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta ");
			sqlString.append("and c.codigo = fa.farmacia and f.farmacia = ? ");

			sqlString.append("and f.documento_venta = ? ");
			sqlString.append("and fa_secs.tipo_documento = ? ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);

			ps.setLong(2, documentoVenta);
			ps.setLong(3, tipo);

			ResultSet set = ps.executeQuery();
			
			CampoAdicional email=null;
			CampoAdicional docInterno=null;
			CampoAdicional dirEstablecimiento=null;
			CampoAdicional compensacion=null;

			while (set.next()) {
				notaCreditoFybeca = null;
				try {
					notaCreditoFybeca = obtenerNotaCredito(conn, set, farmacia,
							tipo);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}
				if (notaCreditoFybeca != null) {

					String correo = obtenerCorreo(conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					
					log.info("***111***");
					
					String direccion=obtenerDireccion(conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					
					log.info("***222***");

					notaCreditoFybeca
							.setCorreoElectronicoNotificacion(StringUtil
									.validateEmail(correo));

					Map<String, Object> detalesTotalesImpuesto = listarDetalles(
							conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					List<Detalle> detalles = (List<Detalle>) detalesTotalesImpuesto
							.get(Constantes.DETALLES);
					log.debug("**servicio listar detalles: "
							+ (detalles != null ? detalles.size() : 0));

					List<TotalImpuesto> totalesImpuestos = (List<TotalImpuesto>) detalesTotalesImpuesto
							.get(Constantes.TOTAL_IMPUESTO);
					log.debug("**servicio listar total con impuesto: "
							+ (totalesImpuestos != null ? totalesImpuestos
									.size() : 0));

					if (totalesImpuestos != null && !totalesImpuestos.isEmpty()) {
						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito()
								.setTotalConImpuestos(new TotalConImpuestos());
						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito()
								.getTotalConImpuestos().getTotalImpuesto()
								.addAll(totalesImpuestos);
					}

					notaCreditoFybeca
							.getNotaCredito()
							.getInfoNotaCredito()
							.setTotalSinImpuestos(
									(BigDecimal) detalesTotalesImpuesto
											.get(Constantes.TOTAL_SIN_IMPUESTO));
					
//					List<Compensacion> compensacions = listarCompensacionISS(conn, documentoVenta, farmacia, cartorce, boodoce);
//					List<Compensacion> compensacions2 = listarCompensacionIDE(conn, documentoVenta, farmacia, cartorce, boodoce);
//					List<Compensacion> compensacions3 = listarCompensacionITC(conn, documentoVenta, farmacia, cartorce, boodoce);					
//					List<Compensacion> compensacionsAll = new ArrayList<Compensacion>();
//
//					log.info("***333***");
//					if(compensacions != null && !compensacions.isEmpty()){
//						compensacionsAll.addAll(compensacions);
//					}
//					if(compensacions2 != null && !compensacions2.isEmpty()){
//						compensacionsAll.addAll(compensacions2);
//					}
//					if(compensacions3 != null && !compensacions3.isEmpty()){
//						compensacionsAll.addAll(compensacions3);
//					}
//					if(compensacionsAll != null && !compensacionsAll.isEmpty()){
//						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().setCompensaciones(new Compensaciones());
//						notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getCompensaciones().getCompensacion().addAll(compensacionsAll);
//					} 

					// DETALLE IMPUESTO
					if (detalles != null && !detalles.isEmpty()) {
						notaCreditoFybeca.getNotaCredito().setDetalles(
								new Detalles());
						notaCreditoFybeca.getNotaCredito().getDetalles()
								.getDetalle().addAll(detalles);
					}
					// notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().setImporteTotal(notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getImporteTotal().subtract(notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getTotalDescuento()));

					InfoAdicional infoAdicional = null;
					
					//compensainCompensación solidaria 2%
//					String compensacionStr = StringUtil.validateInfoXML(obtenerCompensacion(
//							conn, farmacia));
//					
//					if(compensacionStr != null && compensacionStr.equals("S")){
//						if (infoAdicional == null) {
//							infoAdicional = new InfoAdicional();
//						}
//						
//						compensacion = new CampoAdicional();
//						compensacion.setNombre("COMPENSACION");
//						compensacion.setValue("Compensación solidaria 2% ");;
//						
//						infoAdicional.getCampoAdicional().add(docInterno);
//						
//					}
					
					notaCreditoFybeca.setBanderaOferton(banderaOferton(conn,farmacia));
					
					if (notaCreditoFybeca.getCodigoExterno() != null
							&& !notaCreditoFybeca.getCodigoExterno().isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new InfoAdicional();
						}
						
						docInterno = new CampoAdicional();
						docInterno.setNombre("DOCUMENTO INTERNO");
						docInterno.setValue(notaCreditoFybeca.getCodigoExterno());
						
						infoAdicional.getCampoAdicional().add(docInterno);
						
					}
					
					if (notaCreditoFybeca.getCorreoElectronicoNotificacion() != null
							&& !notaCreditoFybeca.getCorreoElectronicoNotificacion().isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new InfoAdicional();
						}
						
						email = new CampoAdicional();
						email.setNombre("EMAIL");
						email.setValue(notaCreditoFybeca.getCorreoElectronicoNotificacion());
						
						infoAdicional.getCampoAdicional().add(email);
						
					}
					
					if (direccion != null
							&& !direccion.isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new InfoAdicional();
						}
						
						dirEstablecimiento = new CampoAdicional();
						dirEstablecimiento.setNombre("DIRECCION");
						dirEstablecimiento.setValue(StringEscapeUtils.escapeXml(direccion));
						
						infoAdicional.getCampoAdicional().add(dirEstablecimiento);
						
					}
					
					if (infoAdicional != null
							&& infoAdicional.getCampoAdicional() != null
							&& !infoAdicional.getCampoAdicional().isEmpty()) {
						notaCreditoFybeca.getNotaCredito().setInfoAdicional(infoAdicional);
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
		log.info("***444***");
		return notaCreditoFybeca;

	}

	private Map<String, Object> listarDetalles(Connection conn,
			Long idNotaCredito, Long idFarmacia) throws GizloException,
			SQLException {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		PreparedStatement ps = null;
		List<Detalle> detalles = new ArrayList<Detalle>();
		Detalle detalleXML = null;

		// <<AG
		List<TotalImpuesto> totalesImpuestos = new ArrayList<TotalImpuesto>();
		List<Impuesto> detalleImpuestos = new ArrayList<Impuesto>();
		Impuesto impuestoXML = null;
		TotalImpuesto totalImpuestoXML = null;
		BigDecimal total12 = new BigDecimal(0f), total0 = new BigDecimal(0f), baseImp0 = new BigDecimal(
				0f), baseImp12 = new BigDecimal(0f), totalSinImpuesto = new BigDecimal(
				0f), valor = new BigDecimal(0f), baseImp = new BigDecimal(0f);
		BigDecimal total14 = new BigDecimal(0f), baseImp14 = new BigDecimal(0f);
		// AG>>
		StringBuilder sqlString = new StringBuilder();

		// ROUND((precio_fybeca - venta)/cantidad,2)
		log.debug("Detalle:  " + idNotaCredito + " " + idFarmacia);
		sqlString
				.append("(SELECT (fa_detalles_servicios.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		// <<AG
		sqlString
				.append("ROUND((cantidad * venta * fa_detalles_servicios.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		// AG>>
		sqlString
				.append("ROUND(venta, 4) as venta, (fa_servicios.nombre) as descripcion from fa_detalles_servicios inner join fa_servicios ");
		sqlString
				.append("on fa_detalles_servicios.item = fa_servicios.codigo ");
		sqlString.append("WHERE documento_venta = ? and farmacia = ? ) ");
		sqlString.append("union all ");
		sqlString
				.append("(SELECT (fa_detalles_factura.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		// <<AG
		sqlString
				.append("ROUND((cantidad * venta * fa_detalles_factura.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		// AG>>
		sqlString
				.append("ROUND(venta, 4) as venta, (pr_productos.nombre || pr_items.presentacion) as descripcion ");
		sqlString.append("FROM fa_detalles_factura ");
		sqlString
				.append("inner join pr_items on fa_detalles_factura.item = pr_items.codigo ");
		sqlString.append("inner join pr_productos ");
		sqlString.append("on pr_items.producto = pr_productos.codigo ");
		sqlString.append("WHERE documento_venta = ? and farmacia = ?)");

		ps = conn.prepareStatement(sqlString.toString());
		ps.setLong(1, idNotaCredito);
		ps.setLong(2, idFarmacia);
		ps.setLong(3, idNotaCredito);
		ps.setLong(4, idFarmacia);

		ResultSet set = ps.executeQuery();
		String descripcion = null;

		while (set.next()) {
			if (set.getBigDecimal("porc_iva") != null) {
				detalleXML = new Detalle();
				detalleXML.setCantidad(new BigDecimal(set.getLong("cantidad")));
				// detalle.setCodigoAuxiliar(set.getString("NOMBRE_PARAM"));
				detalleXML.setCodigoInterno(set.getString("item"));

				descripcion = set.getString("descripcion");
				descripcion = StringUtil.validateInfoXML(descripcion);

				detalleXML.setDescripcion(descripcion);

				if (set.getBigDecimal("porc_iva").equals(new BigDecimal(0))) {
					detalleXML.setDescuento(set.getBigDecimal("descuento0"));
				} else {
					detalleXML.setDescuento(set.getBigDecimal("descuento12"));
				}

				if (set.getBigDecimal("porc_iva").equals(new BigDecimal(0))) {
					detalleXML.setPrecioTotalSinImpuesto(set
							.getBigDecimal("precio_sin_imp"));
				} else {
					detalleXML.setPrecioTotalSinImpuesto(set
							.getBigDecimal("precio_sin_imp"));
				}
				detalleXML.setPrecioUnitario(set.getBigDecimal("venta"));

				// <<AG
				detalleImpuestos = new ArrayList<Impuesto>();

				if (set.getBigDecimal("porc_iva").equals(new BigDecimal(0))) {
					// 0%
					total0 = total0
							.add((set.getBigDecimal("valor") != null ? set
									.getBigDecimal("valor") : new BigDecimal(0)));
					baseImp0 = baseImp0
							.add((set.getBigDecimal("baseImp") != null ? set
									.getBigDecimal("baseImp") : new BigDecimal(
									0)));

					baseImp = set.getBigDecimal("baseImp") != null ? set
							.getBigDecimal("baseImp") : BigDecimal.ZERO;
					valor = set.getBigDecimal("valor") != null ? set
							.getBigDecimal("valor") : BigDecimal.ZERO;

					baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
					valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);

					impuestoXML = new Impuesto();
					impuestoXML.setCodigo("2");
					impuestoXML.setCodigoPorcentaje("0");
					impuestoXML.setTarifa(new BigDecimal(0));
					impuestoXML.setBaseImponible(baseImp);
					impuestoXML.setValor(valor);
					detalleImpuestos.add(impuestoXML);

				} else {
					// 12%
					BigDecimal doce = new BigDecimal("0.12");
					log.info("***ingreso 12% 111***"+set.getBigDecimal("porc_iva").equals(doce));
					if(set.getBigDecimal("porc_iva").equals(doce)){
						boodoce = true;
						total12 = total12
								.add((set.getBigDecimal("valor") != null ? set
										.getBigDecimal("valor") : new BigDecimal(0)));
						baseImp12 = baseImp12
								.add((set.getBigDecimal("baseImp") != null ? set
										.getBigDecimal("baseImp") : new BigDecimal(
										0)));
	
						baseImp = set.getBigDecimal("baseImp") != null ? set
								.getBigDecimal("baseImp") : BigDecimal.ZERO;
						valor = set.getBigDecimal("valor") != null ? set
								.getBigDecimal("valor") : BigDecimal.ZERO;
	
						baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
						valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	
						impuestoXML = new Impuesto();
						impuestoXML.setCodigo("2");
						impuestoXML.setCodigoPorcentaje("2");
						impuestoXML.setTarifa(new BigDecimal(12));
						impuestoXML.setBaseImponible(baseImp);
						impuestoXML.setValor(valor);
						detalleImpuestos.add(impuestoXML);
					}else{
						//14%
						cartorce = true;
						BigDecimal catorce = new BigDecimal("0.14");
						log.info("***ingreso 14% 111***"+set.getBigDecimal("porc_iva").equals(catorce));						
						if(set.getBigDecimal("porc_iva").equals(catorce)){
//							total14 = total14
//									.add((set.getBigDecimal("valor") != null ? set
//											.getBigDecimal("valor") : new BigDecimal(0)));
//							baseImp14 = baseImp14
//									.add((set.getBigDecimal("baseImp") != null ? set
//											.getBigDecimal("baseImp") : new BigDecimal(
//											0)));

							baseImp = set.getBigDecimal("baseImp") != null ? set
									.getBigDecimal("baseImp") : BigDecimal.ZERO;
							valor = set.getBigDecimal("valor") != null ? set
									.getBigDecimal("valor") : BigDecimal.ZERO;

							baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
							valor = baseImp.multiply(doce);//borrar ojo
							valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);
							
							total12 = total12.add(valor);//borrar ojo
							baseImp12 = baseImp12.add(baseImp);//borrar ojo

							impuestoXML = new Impuesto();
							impuestoXML.setCodigo("2");
//							impuestoXML.setCodigoPorcentaje("3");
							impuestoXML.setCodigoPorcentaje("2");//borrar ojo
//							impuestoXML.setTarifa(new BigDecimal(14));
							impuestoXML.setTarifa(new BigDecimal(12));
							impuestoXML.setBaseImponible(baseImp);
							impuestoXML.setValor(valor);
							detalleImpuestos.add(impuestoXML);
						}
					}
				}

				if (detalleImpuestos != null && !detalleImpuestos.isEmpty()) {
					detalleXML
							.setImpuestos(new NotaCredito.Detalles.Detalle.Impuestos());
					detalleXML.getImpuestos().getImpuesto()
							.addAll(detalleImpuestos);

				}
				// AG>>

				detalles.add(detalleXML);
			}

		}

		totalSinImpuesto = totalSinImpuesto.add(baseImp0);
		totalSinImpuesto = totalSinImpuesto.add(baseImp12);
		totalSinImpuesto = totalSinImpuesto.add(baseImp14);
		totalSinImpuesto = totalSinImpuesto.setScale(2,
				BigDecimal.ROUND_HALF_UP);
		
		log.info("****total sin impuesto****"+baseImp14);
		// 0%
		baseImp0 = baseImp0.setScale(2, BigDecimal.ROUND_HALF_UP);
		total0 = total0.setScale(2, BigDecimal.ROUND_HALF_UP);

		totalImpuestoXML = new TotalImpuesto();
		totalImpuestoXML.setCodigo("2");
		totalImpuestoXML.setCodigoPorcentaje("0");
		totalImpuestoXML.setBaseImponible(baseImp0);
		totalImpuestoXML.setValor(total0);
		totalesImpuestos.add(totalImpuestoXML);

		if (!total12.equals(new BigDecimal(0))) {
			// 12%
			baseImp12 = baseImp12.setScale(2, BigDecimal.ROUND_HALF_UP);
			total12 = total12.setScale(2, BigDecimal.ROUND_HALF_UP);

			totalImpuestoXML = new TotalImpuesto();
			totalImpuestoXML.setCodigo("2");
			totalImpuestoXML.setCodigoPorcentaje("2");
			totalImpuestoXML.setBaseImponible(baseImp12);
			totalImpuestoXML.setValor(total12);
			totalesImpuestos.add(totalImpuestoXML);
		}
		if (!total14.equals(new BigDecimal(0))) {
			// 14%
			baseImp14 = baseImp14.setScale(2, BigDecimal.ROUND_HALF_UP);
			total14 = total14.setScale(2, BigDecimal.ROUND_HALF_UP);

			totalImpuestoXML = new TotalImpuesto();
			totalImpuestoXML.setCodigo("2");
			totalImpuestoXML.setCodigoPorcentaje("3");
			totalImpuestoXML.setBaseImponible(baseImp14);
			totalImpuestoXML.setValor(total14);
			totalesImpuestos.add(totalImpuestoXML);
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		respuesta.put(Constantes.DETALLES, detalles);
		respuesta.put(Constantes.TOTAL_IMPUESTO, totalesImpuestos);
		respuesta.put(Constantes.TOTAL_SIN_IMPUESTO, totalSinImpuesto);

		return respuesta;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private NotaCreditoProcesarRequest obtenerNotaCredito(Connection conn,
			ResultSet set, Long farmacia, Long tipo) throws SQLException,
			GizloException {
		NotaCredito notaCredito = null;
		String tipoIdentificacionComprador = null;
		NotaCreditoProcesarRequest notaCreditoProcesarRequest = null;
		// infoNotaCredito.setAdicionales(adicionales);
		notaCredito = new NotaCredito();
		InfoNotaCredito infoNotaCredito = new InfoNotaCredito();

		String razonSocialComprador = set.getString("razonSocialComprador");
		razonSocialComprador = StringUtil.validateInfoXML(razonSocialComprador);

		infoNotaCredito.setRazonSocialComprador(razonSocialComprador.trim());

		String dirEstablecimiento = set.getString("dirEstablecimiento");
		dirEstablecimiento = StringUtil.validateInfoXML(dirEstablecimiento);
		infoNotaCredito
				.setDirEstablecimiento(dirEstablecimiento != null ? dirEstablecimiento
						.replaceAll("[\n]", "").replaceAll("[\b]", "") : null);

		infoNotaCredito
				.setFechaEmision(set.getDate("fechaEmision") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fechaEmision").getTime()),
								FechaUtil.DATE_FORMAT) : null);
		
		//infoNotaCredito.setFechaEmision("30/06/2016");
		
		// infoNotaCredito.setFechaEmision(FechaUtil.formatearFecha(FechaUtil.convertirLongADate(Calendar.getInstance().getTime().getTime()),FechaUtil.DATE_FORMAT));

		infoNotaCredito.setMoneda(set.getString("moneda"));

		// infoNotaCredito.setTotalDescuento(set.getBigDecimal("totalDescuento"));
		// log.debug("**totalDescuento: "+obtenerTotalDescuento(credencialDS,
		// set.getLong("codigo_externo"),farmacia));

		if (set.getString("identificacionComprador") != null
				&& !set.getString("identificacionComprador").isEmpty()) {
			tipoIdentificacionComprador = obtenerIdentificacion(conn,
					set.getString("identificacionComprador"));
			if (tipoIdentificacionComprador != null
					&& !tipoIdentificacionComprador.isEmpty()) {

				if (tipoIdentificacionComprador.equalsIgnoreCase("C")) {
					infoNotaCredito.setTipoIdentificacionComprador("05");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("R")) {
					infoNotaCredito.setTipoIdentificacionComprador("04");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("P")) {
					infoNotaCredito.setTipoIdentificacionComprador("06");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("N")) {
					infoNotaCredito.setTipoIdentificacionComprador("07");	
				}
			}

			if (infoNotaCredito != null
					&& infoNotaCredito.getTipoIdentificacionComprador() != null
					&& infoNotaCredito.getTipoIdentificacionComprador()
							.equalsIgnoreCase("07")) {
				infoNotaCredito.setIdentificacionComprador("9999999999999");
				infoNotaCredito.setRazonSocialComprador("CONSUMIDOR FINAL");
			} else {
				String identificacion = set
						.getString("identificacionComprador");
				infoNotaCredito.setTipoIdentificacionComprador("06");

				if (StringUtil.esNumero(identificacion)) {
					if (identificacion.length() == 10) {
						infoNotaCredito.setTipoIdentificacionComprador("05");
					} else if (identificacion.length() == 13
							&& identificacion.substring(10).equals("001")) {
						infoNotaCredito.setTipoIdentificacionComprador("04");
					}
				}

				infoNotaCredito.setIdentificacionComprador(set
						.getString("identificacionComprador"));
			}
		} else {
			infoNotaCredito.setIdentificacionComprador("9999999999999");
			infoNotaCredito.setTipoIdentificacionComprador("07");
			infoNotaCredito.setRazonSocialComprador("CONSUMIDOR FINAL");
		}

		infoNotaCredito.setTotalSinImpuestos(set
				.getBigDecimal("totalSinImpuestos"));

		// FACTURA
		infoNotaCredito.setCodDocModificado("01");
		Long tipoDocModificado = tipoComprobanteDAO.obtenerTipo(conn,
				ComprobanteEnum.FACTURA.getDescripcion());

		StringBuilder secuencialFactura = new StringBuilder();
		Long secuencialFacturaLong = facturaDAO.obtenerSecuencialFactura(conn,
				set.getLong("factura_codigo"), farmacia, tipoDocModificado);

		if (secuencialFacturaLong == null) {
			secuencialFacturaLong = facturaDAO
					.obtenerSecuencialFacturaAutoimpresor(conn,
							set.getLong("factura_codigo"), farmacia,
							tipoDocModificado);
		}
		String secuencialTmp = secuencialFacturaLong != null ? secuencialFacturaLong
				+ ""
				: null;

		if (secuencialTmp != null) {
			int secuencialTam = 9 - secuencialTmp.length();
			for (int i = 0; i < secuencialTam; i++) {
				secuencialFactura.append("0");
			}

		}
		secuencialFactura.append(secuencialTmp);

		infoNotaCredito.setNumDocModificado(set.getString("estab") + "-"
				+ set.getString("ptoEmi") + "-" + secuencialFactura.toString()); // factura_codigo

		Date fecha = facturaDAO.obtenerFechaEmision(conn,
				set.getLong("factura_codigo"), farmacia);
		infoNotaCredito.setFechaEmisionDocSustento(fecha != null ? FechaUtil
				.formatearFecha(FechaUtil.convertirLongADate(fecha.getTime()),
						FechaUtil.DATE_FORMAT) : null);
		infoNotaCredito.setValorModificacion(set.getBigDecimal("importeTotal"));

		String motivo = obtenerMotivo(conn, set.getLong("codigo_externo"),
				farmacia);
		if (motivo == null) {
			motivo = "MOTIVO";
		}

		motivo = StringUtil.validateInfoXML(motivo);

		infoNotaCredito.setMotivo(motivo);
		notaCredito.setInfoNotaCredito(infoNotaCredito);

		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setCodDoc("04");// set.getString("codDoc")
		infoTributaria.setEstab(set.getString("estab"));
		infoTributaria.setPtoEmi(set.getString("ptoEmi"));
		infoTributaria.setRuc(set.getString("ruc"));
		
		String nombreSucursal = set.getString("nombreSucursal");
		nombreSucursal = StringUtil.validateInfoXML(nombreSucursal);
		infoTributaria.setNombreComercial(nombreSucursal);

		
		infoTributaria.setSecuencial(set.getLong("secuencial") + "");

		notaCredito.setInfoTributaria(infoTributaria);

		notaCreditoProcesarRequest = new NotaCreditoProcesarRequest();
		notaCreditoProcesarRequest.setNotaCredito(notaCredito);
		notaCreditoProcesarRequest.setCodigoExterno(set
				.getLong("codigo_externo") + "");

		notaCreditoProcesarRequest.setIdentificadorUsuario((set
				.getString("identificacionComprador") != null && !set
				.getString("identificacionComprador").isEmpty()) ? set
				.getString("identificacionComprador") : null);
		notaCreditoProcesarRequest.setAgencia(farmacia.toString());
		return notaCreditoProcesarRequest;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BigDecimal obtenerTotalDescuento(CredencialDS credencialDS,
			Long documentoVenta, Long farmacia) throws GizloException {

		Connection conn = null;
		PreparedStatement ps = null;
		BigDecimal resultado = null;
		try {
			conn = Conexion.obtenerConexionFybeca(credencialDS);
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select (pvp_total_factura-venta_total_factura) as descuento from fa_facturas where documento_venta =? and farmacia =?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, documentoVenta);
			ps.setLong(2, farmacia);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getBigDecimal("descuento");
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
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return resultado;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private String obtenerMotivo(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException {

		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select observacion_factura as motivo from fa_factura_datos fd where fd.farmacia = ? and fd.documento_venta = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("motivo");
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

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private String obtenerIdentificacion(Connection conn, String identificacion)
			throws GizloException {
		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select ap.tipo_identificacion as tipoIdentificacionComprador from ab_personas ap where ap.identificacion = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, identificacion);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("tipoIdentificacionComprador");
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

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long obtenerSecuencial(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException {

		PreparedStatement ps = null;
		Long resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select fa_secs.secuencia as secuencial from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString
					.append("where fa.documento_venta = f.documento_venta and fa.farmacia = f.farmacia and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta ");
			sqlString
					.append("and c.codigo = fa.farmacia and f.farmacia = ? and f.documento_venta = ? and fa_secs.tipo_documento = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);
			ps.setLong(3, tipo);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getLong("secuencial");
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

	private String obtenerCorreo(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException {

		// PreparedStatement ps = null;
		CallableStatement cstmt = null;
		String correo = null;

		try {
			StringBuilder sqlString = new StringBuilder();

			// sqlString.append("select * from ab_medios_contacto where tipo = ? and persona in (select codigo from ab_personas where identificacion = ?) and ROWNUM <=1");
			sqlString.append("{call farmacias.fa_pkg_mov_general.pro_recupera_mail_cliente(?,?,?)}");
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
	
	private String obtenerDireccion(Connection conn, Long factura, Long farmacia)
			throws SQLException {
		PreparedStatement ps = null;
		String resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString
				.append("select direccion from fa_facturas where documento_venta = ? and farmacia = ?");

		ps = conn.prepareStatement(sqlString.toString());

		ps.setLong(1, factura);
		ps.setLong(2, farmacia);
		
		ResultSet set = ps.executeQuery();

		while (set.next()) {
			resultado = set.getString("direccion");
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		return resultado;
	}
	
	
	private String banderaOferton(Connection conn, Long farmacia) {
		PreparedStatement ps = null;
		String resultado = "N";
		try {

			StringBuilder sqlStringValOferton = new StringBuilder();
			sqlStringValOferton.append(" select nvl(valor, 'N') valor  ");
			sqlStringValOferton.append("  from fA_parametros_farmacia ");
			sqlStringValOferton.append(" where farmacia = ?           ");
			sqlStringValOferton.append("   and campo = 'PDV_OFERTON'  ");
			sqlStringValOferton.append("   and activo = 'S'           ");

			ps = conn.prepareStatement(sqlStringValOferton.toString());
			ps.setLong(1, farmacia);
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				resultado = set.getString("valor");
			}
			if (ps != null)
				ps.close();
			if (set != null)
				set.close();
		} catch (Exception e) {
		}
		return resultado;
	}
	
//	private String obtenerCompensacion(Connection conn, Long farmacia)
//			throws SQLException {
//		PreparedStatement ps = null;
//		String resultado = null;
//		StringBuilder sqlString = new StringBuilder();
//
//		sqlString
//				.append("select valor from fa_parametros_farmacia where campo = 'IVA_COMPENSACION'  and farmacia = ?");
//
//		ps = conn.prepareStatement(sqlString.toString());
//
//		
//		ps.setLong(1, farmacia);
//
//		ResultSet set = ps.executeQuery();
//
//		while (set.next()) {
//			resultado = set.getString("valor") != null ? set.getString("valor").toString() : null;
//		}
//
//		if (ps != null)
//			ps.close();
//
//		if (set != null)
//			set.close();
//		return resultado;
//	}

	private List<Compensacion> listarCompensacionISS(Connection conn, Long idFactura, Long idFarmacia, boolean cartorce, boolean doce) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlStringSubsidio = new StringBuilder();	
		log.info("****obteniendo Compensacion"+idFarmacia+"---"+idFactura);
		sqlStringSubsidio.append("SELECT VENTA_FACTURA ");
		sqlStringSubsidio.append("FROM fa_detalles_formas_pago ");
		sqlStringSubsidio.append("where forma_pago = 'ISS' ");
		sqlStringSubsidio.append("AND DOCUMENTO_VENTA = ?");
		sqlStringSubsidio.append("AND FARMACIA = ? ");
	
		
		ps = conn.prepareStatement(sqlStringSubsidio.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			compensacionXml = new Compensacion();
			compensacionXml.setCodigo(new BigDecimal("1"));
			compensacionXml.setTarifa(new BigDecimal("2"));
			compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
			list.add(compensacionXml);
		}
		log.info("****obteniendo Compensacion 333"+list.size());
		
		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
				
		return list;
				
	}
	
	private List<Compensacion> listarCompensacionIDE(Connection conn, Long idFactura, Long idFarmacia, boolean cartorce, boolean doce) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlStringSubsidio = new StringBuilder();	
		
		sqlStringSubsidio.append("SELECT VENTA_FACTURA ");
		sqlStringSubsidio.append("FROM fa_detalles_formas_pago ");
		sqlStringSubsidio.append("where forma_pago = 'IDE' ");
		sqlStringSubsidio.append("AND DOCUMENTO_VENTA = ?");
		sqlStringSubsidio.append("AND FARMACIA = ? ");
	
		
		ps = conn.prepareStatement(sqlStringSubsidio.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			compensacionXml = new Compensacion();
			if(cartorce){
				compensacionXml.setCodigo(new BigDecimal("2"));
				compensacionXml.setTarifa(new BigDecimal("4"));
				compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
				list.add(compensacionXml);
			}else{
				if(doce){
					compensacionXml.setCodigo(new BigDecimal("5"));
					compensacionXml.setTarifa(new BigDecimal("2"));
					compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
					list.add(compensacionXml);
				}				
			}
			
			
		}
		log.info("****obteniendo Compensacion 333"+list.size());
		
		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
				
		return list;
				
	}
	
	private List<Compensacion> listarCompensacionITC(Connection conn, Long idFactura, Long idFarmacia, boolean cartorce, boolean doce) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlStringSubsidio = new StringBuilder();	
		
		sqlStringSubsidio.append("SELECT VENTA_FACTURA ");
		sqlStringSubsidio.append("FROM fa_detalles_formas_pago ");
		sqlStringSubsidio.append("where forma_pago = 'ITC' ");
		sqlStringSubsidio.append("AND DOCUMENTO_VENTA = ?");
		sqlStringSubsidio.append("AND FARMACIA = ? ");
	
		
		ps = conn.prepareStatement(sqlStringSubsidio.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			compensacionXml = new Compensacion();
			if(cartorce){
				compensacionXml.setCodigo(new BigDecimal("4"));
				compensacionXml.setTarifa(new BigDecimal("1"));
				compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
				list.add(compensacionXml);
			}else{
				if(doce){
					compensacionXml.setCodigo(new BigDecimal("7"));
					compensacionXml.setTarifa(new BigDecimal("1"));
					compensacionXml.setValor(set.getBigDecimal("VENTA_FACTURA"));
					list.add(compensacionXml);
				}				
			}
		}
		log.info("****obteniendo Compensacion 333"+list.size());
		
		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
				
		return list;
				
	}
}
