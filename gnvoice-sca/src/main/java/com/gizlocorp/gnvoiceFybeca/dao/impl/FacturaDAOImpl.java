package com.gizlocorp.gnvoiceFybeca.dao.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.gizlocorp.gnvoice.xml.factura.Factura;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Compensaciones;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Pagos;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Compensaciones.Compensacion;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Pagos.Pago;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoice.xml.factura.Impuesto;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarRequest;
import com.gizlocorp.gnvoiceFybeca.dao.FacturaDAO;

@Stateless
public class FacturaDAOImpl implements FacturaDAO {

	private static Logger log = Logger
			.getLogger(FacturaDAOImpl.class.getName());

	BigDecimal total14General = new BigDecimal(0f), baseImp14 = new BigDecimal(0f);
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FacturaProcesarRequest listar(Connection conn, Long documentoVenta,Long farmacia, Long tipo) throws GizloException {

		PreparedStatement ps = null;
		ResultSet set = null;
		FacturaProcesarRequest facturaFybeca = null;
		

		log.info("*** listar compensacion*** 9999");
		
		if (documentoVenta == null || documentoVenta == 0) {
			throw new GizloException("numero de documento no existe");
		}
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select fa_secs.secuencia as secuencial, f.fecha as fechaEmision, c.calle||' '||c.numero||' '||c.interseccion as dirEstablecimiento, ");
			sqlString
					.append("fa.cp_char3 as contribuyenteEspecial, 'SI' as obligadoContabilidad, c.NOMBRE as nombreSucursal, ");
			sqlString
					.append("(f.primer_apellido ||' '|| f.segundo_apellido ||' '|| f.nombres) as razonSocialComprador, f.identificacion as identificacionComprador, ");
			sqlString
					.append("ROUND((f.venta_total_factura - f.valor_iva),2) as totalSinImpuestos, ");
			sqlString
					.append("'0' as totalDescuento, '0.00' as propina, ROUND(venta_total_factura,2) as importeTotal, ");
			sqlString
					.append("'DOLAR' as moneda, '1' as ambiente, '1' as tipoEmision, ");
			sqlString
					.append("fa.cp_var3 as razonSocial, fa.cp_var3 as nombreComercial, fa.cp_var6 as ruc, '01' as codDoc, ");
			sqlString
					.append("substr(c.numero_ruc, 11, 3) as estab, substr(f.numero_sri, 5, 3) as ptoEmi, ");
			// substr(c.numero_ruc, 11, 3) as ptoEmi, substr(f.numero_sri, 5, 3)
			// as estab
			sqlString.append("f.documento_venta as codigo_externo ");
			sqlString
					.append("from fa_factura_adicional fa, fa_facturas f, ad_farmacias c, fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString
					.append("and fa_secs.farmacia = f.farmacia and fa_secs.documento_venta = f.documento_venta  ");
			sqlString.append("and c.codigo = fa.farmacia and f.farmacia = ? ");

			sqlString.append("and f.documento_venta = ? ");
			sqlString.append("and fa_secs.tipo_documento = ? ");

			
			log.info("*** listar compensacion*** 9910"+ sqlString.toString());
			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);
			ps.setLong(3, tipo);

			log.debug("parametros de dao factura son: " + farmacia + " doc: " + documentoVenta);
			set = ps.executeQuery();
			Map<String, Object> detalesTotalesImpuesto = null;
			List<Detalle> detalles = null;
			
			List<com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto> totalesImpuestos = null;
			CampoAdicional documentoInterno = null;
			CampoAdicional email = null;
			CampoAdicional sucursal = null;
			CampoAdicional clientaAbf = null;

			while (set.next()) {
				facturaFybeca = null;
				try {
					facturaFybeca = obtenerFactura(conn, set, farmacia, tipo,documentoVenta);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}

				if (facturaFybeca != null) {
					String correo = obtenerCorreo(conn,Long.valueOf(facturaFybeca.getCodigoExterno()),farmacia);

					facturaFybeca.setCorreoElectronicoNotificacion(StringUtil.validateEmail(correo));

					// AG<<
					detalesTotalesImpuesto = listarDetalles(conn,Long.valueOf(facturaFybeca.getCodigoExterno()),farmacia);

					detalles = (List<Detalle>) detalesTotalesImpuesto.get(Constantes.DETALLES);
					log.debug("**servicio listar detalles: "+ (detalles != null ? detalles.size() : 0));
					totalesImpuestos = (List<com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto>) detalesTotalesImpuesto.get(Constantes.TOTAL_IMPUESTO);
					facturaFybeca.getFactura().getInfoFactura().setTotalSinImpuestos((BigDecimal) detalesTotalesImpuesto.get(Constantes.TOTAL_SIN_IMPUESTO));
					
					//compensacion ojo
					//pagos ojo
					/*List<Compensacion> compensacions = listarCompensacion(conn, documentoVenta, farmacia);
					if(compensacions != null && !compensacions.isEmpty()){
						facturaFybeca.getFactura().getInfoFactura().setCompensaciones(new Compensaciones());
						facturaFybeca.getFactura().getInfoFactura().getCompensaciones().getCompensacion().addAll(compensacions);
					}*/

					// AG>>

					if (totalesImpuestos != null && !totalesImpuestos.isEmpty()) {
						facturaFybeca
								.getFactura()
								.getInfoFactura()
								.setTotalConImpuestos(
										new Factura.InfoFactura.TotalConImpuestos());
						facturaFybeca.getFactura().getInfoFactura()
								.getTotalConImpuestos().getTotalImpuesto()
								.addAll(totalesImpuestos);
					}

					// DETALLE IMPUESTO
					if (detalles != null && !detalles.isEmpty()) {
						facturaFybeca.getFactura().setDetalles(new Detalles());
						facturaFybeca.getFactura().getDetalles().getDetalle()
								.addAll(detalles);
					}

					if (facturaFybeca.getFactura().getInfoFactura()
							.getTotalDescuento() == null) {
						facturaFybeca.getFactura().getInfoFactura()
								.setTotalDescuento(BigDecimal.ZERO);
					}

					if (facturaFybeca.getFactura().getInfoFactura() != null
							&& facturaFybeca.getFactura().getInfoFactura()
									.getTotalDescuento() != null) {
						facturaFybeca
								.getFactura()
								.getInfoFactura()
								.setImporteTotal(
										facturaFybeca
												.getFactura()
												.getInfoFactura()
												.getImporteTotal()
												.subtract(
														facturaFybeca
																.getFactura()
																.getInfoFactura()
																.getTotalDescuento()));

					}

					
					
					//OFERTON : recupera bandera para validar si esta farmacia esta o no en oferton
					
					facturaFybeca.setBanderaOferton(banderaOferton(conn,farmacia));
					
					InfoAdicional infoAdiconal = obtenerInfoAdicional( conn,
																	   farmacia,
																	   Long.valueOf(facturaFybeca.getCodigoExterno()),
																	   facturaFybeca.getBanderaOferton());
					
					if (facturaFybeca.getPaciente() != null && !facturaFybeca.getPaciente().isEmpty()) {
						if (infoAdiconal == null) {
							infoAdiconal = new InfoAdicional();
						}

						clientaAbf = new CampoAdicional();
						clientaAbf.setNombre("Nombre Paciente");
						clientaAbf.setValue(StringUtil.validateInfoXML(facturaFybeca.getPaciente().trim()));

						infoAdiconal.getCampoAdicional().add(clientaAbf);
					}

					if (facturaFybeca.getCodigoExterno() != null
							&& !facturaFybeca.getCodigoExterno().isEmpty()
							&& !facturaFybeca.getCodigoExterno().trim()
									.isEmpty()) {
						if (infoAdiconal == null) {
							infoAdiconal = new InfoAdicional();
						}

						documentoInterno = new CampoAdicional();
						documentoInterno.setNombre("DOCUMENTO INTERNO");
						documentoInterno.setValue(facturaFybeca
								.getCodigoExterno().trim());

						infoAdiconal.getCampoAdicional().add(documentoInterno);
					}

					if (facturaFybeca.getCorreoElectronicoNotificacion() != null
							&& !facturaFybeca
									.getCorreoElectronicoNotificacion()
									.isEmpty()) {

						if (infoAdiconal == null) {
							infoAdiconal = new InfoAdicional();
						}

						email = new CampoAdicional();
						email.setNombre("EMAIL");
						email.setValue(facturaFybeca
								.getCorreoElectronicoNotificacion());

						infoAdiconal.getCampoAdicional().add(email);

					}

//					if (facturaFybeca.getFactura().getInfoTributaria()
//							.getNombreComercial() != null
//							&& !facturaFybeca.getFactura().getInfoTributaria()
//									.getNombreComercial().isEmpty()) {
//
//						if (infoAdiconal == null) {
//							infoAdiconal = new InfoAdicional();
//						}
//
//						sucursal = new CampoAdicional();
//						sucursal.setNombre("NOMBRE SUCURSAL");
//						sucursal.setValue(StringUtil
//								.validateInfoXML(facturaFybeca.getFactura()
//										.getInfoTributaria()
//										.getNombreComercial()));
//
//						infoAdiconal.getCampoAdicional().add(sucursal);
//
//						facturaFybeca.getFactura().getInfoTributaria()
//								.setNombreComercial(null);
//
//					}

					if (infoAdiconal != null
							&& infoAdiconal.getCampoAdicional() != null
							&& !infoAdiconal.getCampoAdicional().isEmpty()) {
						facturaFybeca.getFactura().setInfoAdicional(
								infoAdiconal);
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
				if (set != null)
					set.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return facturaFybeca;

	}
	

	private Map<String, Object> listarDetalles(Connection conn, Long idFactura,
			Long idFarmacia) throws GizloException, SQLException {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		PreparedStatement ps = null;
		List<Detalle> detalles = new ArrayList<Detalle>();
		Detalle detalleXML = null;
		String descripcion = null;

		// <<AG
		List<TotalImpuesto> totalesImpuestos = new ArrayList<TotalImpuesto>();
		TotalImpuesto totalImpuestoXML = null;
		Impuesto impuestoXML = null;
		BigDecimal total12 = new BigDecimal(0f), total0 = new BigDecimal(0f), baseImp0 = new BigDecimal(
				0f), baseImp12 = new BigDecimal(0f), totalSinImpuesto = new BigDecimal(
				0f), baseImp = new BigDecimal(0f), valor = new BigDecimal(0f);
		BigDecimal total14 = new BigDecimal(0f), baseImp14 = new BigDecimal(0f);

		List<Impuesto> detalleImpuestos = new ArrayList<Impuesto>();

		// AG>>
		StringBuilder sqlString = new StringBuilder();

		// ROUND((precio_fybeca - venta)/cantidad,2)
		log.debug("Detalle:  " + idFactura + " " + idFarmacia);
		sqlString.append("(SELECT (fa_detalles_servicios.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		sqlString.append("ROUND((cantidad * venta * fa_detalles_servicios.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		sqlString.append("ROUND(venta, 4) as venta, (fa_servicios.nombre) as descripcion ");
		sqlString.append("FROM fa_detalles_servicios inner join fa_servicios ");
		sqlString.append("on fa_detalles_servicios.item = fa_servicios.codigo ");
		sqlString.append("WHERE documento_venta = ? and farmacia = ? ) ");
		sqlString.append("union all ");
		sqlString.append("(SELECT (fa_detalles_factura.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		sqlString.append("ROUND((cantidad * venta * fa_detalles_factura.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		sqlString.append("ROUND(venta, 4) as venta, (pr_productos.nombre || pr_items.presentacion) as descripcion ");
		sqlString.append("FROM fa_detalles_factura ");
		sqlString.append("inner join pr_items on fa_detalles_factura.item = pr_items.codigo ");
		sqlString.append("inner join pr_productos ");
		sqlString.append("on pr_items.producto = pr_productos.codigo ");
		sqlString.append("WHERE documento_venta = ? and farmacia = ?)");

		ps = conn.prepareStatement(sqlString.toString());
		ps.setLong(1, idFactura);
		ps.setLong(2, idFarmacia);
		ps.setLong(3, idFactura);
		ps.setLong(4, idFarmacia);

		ResultSet set = ps.executeQuery();
		while (set.next()) {
			if (set.getBigDecimal("porc_iva") != null) {
				detalleXML = new Detalle();
				detalleXML.setCantidad(new BigDecimal(set.getLong("cantidad")));
				// detalle.setCodigoAuxiliar(set.getString("NOMBRE_PARAM"));
				detalleXML.setCodigoPrincipal(set.getString("item"));

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
				
//				log.info("***porcentaje***"+set.getBigDecimal("porc_iva"));

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
					if(set.getBigDecimal("porc_iva").equals(doce)){
						
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
						BigDecimal catorce = new BigDecimal("0.14");						
						if(set.getBigDecimal("porc_iva").equals(catorce)){
							
							total14 = total14
									.add((set.getBigDecimal("valor") != null ? set
											.getBigDecimal("valor") : new BigDecimal(0)));
							baseImp14 = baseImp14
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
							impuestoXML.setCodigoPorcentaje("3");
							impuestoXML.setTarifa(new BigDecimal(14));
							impuestoXML.setBaseImponible(baseImp);
							impuestoXML.setValor(valor);
							detalleImpuestos.add(impuestoXML);
						}
					}
				}

				if (detalleImpuestos != null && !detalleImpuestos.isEmpty()) {
					detalleXML
							.setImpuestos(new Factura.Detalles.Detalle.Impuestos());
					detalleXML.getImpuestos().getImpuesto()
							.addAll(detalleImpuestos);

				}
				// AG>>

				detalles.add(detalleXML);

			}
		}

		// <<AG
		// 0%
		totalSinImpuesto = totalSinImpuesto.add(baseImp0);
		totalSinImpuesto = totalSinImpuesto.add(baseImp12);
		totalSinImpuesto = totalSinImpuesto.add(baseImp14);
		totalSinImpuesto = totalSinImpuesto.setScale(2,BigDecimal.ROUND_HALF_UP);

		baseImp0 = baseImp0.setScale(2, BigDecimal.ROUND_HALF_UP);
		total0 = total0.setScale(2, BigDecimal.ROUND_HALF_UP);

		totalImpuestoXML = new TotalImpuesto();
		totalImpuestoXML.setCodigo("2");
		totalImpuestoXML.setCodigoPorcentaje("0");
		totalImpuestoXML.setTarifa(new BigDecimal(0));
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
			totalImpuestoXML.setTarifa(new BigDecimal(12));
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
			totalImpuestoXML.setTarifa(new BigDecimal(14));
			totalImpuestoXML.setBaseImponible(baseImp14);
			total14General = baseImp14.add(total14);
			log.info("*** compensacion ojo"+baseImp14);
			totalImpuestoXML.setValor(total14);
			totalesImpuestos.add(totalImpuestoXML);
		}
		// AG>>

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		log.debug("DETALLES RETORNADOS: "
				+ (detalles != null ? detalles.size() : 0));
		respuesta.put(Constantes.DETALLES, detalles);
		respuesta.put(Constantes.TOTAL_IMPUESTO, totalesImpuestos);
		respuesta.put(Constantes.TOTAL_SIN_IMPUESTO, totalSinImpuesto);

		return respuesta;
	}
	

	private FacturaProcesarRequest obtenerFactura(Connection conn,
			ResultSet set, Long farmacia, Long tipo, Long documentoVenta)
			throws SQLException, GizloException {
		Factura factura = null;
		String tipoIdentificacionComprador = null;
		FacturaProcesarRequest facturaProcesarRequest = null;
		// infoFactura.setAdicionales(adicionales);
		factura = new Factura();
		InfoFactura infoFactura = new InfoFactura();
		String identificacionAbf = null;
		String nombresAbf = null;
		
		String identificacionClienteAbf = null;
		String nombresClienteAbf = null;

		String personaAbf = obtenerCodigoAbf(conn, documentoVenta, farmacia);

		if (personaAbf != null && !personaAbf.isEmpty()) {
			String[] parametros = personaAbf.split("&");
			identificacionAbf = parametros[0];
			nombresAbf = parametros[1];

		}
		
		String razonSocialComprador = set.getString("razonSocialComprador").trim();
		if (identificacionAbf != null && !identificacionAbf.isEmpty()
				&& !identificacionAbf.trim().isEmpty() && nombresAbf != null
				&& !nombresAbf.isEmpty() && !nombresAbf.trim().isEmpty()) {
			
			if(razonSocialComprador != null && !razonSocialComprador.isEmpty()){				
				razonSocialComprador = StringUtil.validateInfoXML(razonSocialComprador.trim());
				infoFactura.setIdentificacionComprador(set.getString("identificacionComprador"));
				infoFactura.setRazonSocialComprador(razonSocialComprador);
			}else{
				String clienteAbf = obtenerClientePersonaAbf(conn,identificacionAbf);
				String[] parametros = clienteAbf.split("&");
				identificacionClienteAbf = parametros[0];
				nombresClienteAbf = parametros[1];
				infoFactura.setIdentificacionComprador(identificacionClienteAbf);
				infoFactura.setRazonSocialComprador(StringUtil.validateInfoXML(nombresClienteAbf));
			}			
			//nombresAbf = StringUtil.validateInfoXML(nombresAbf.trim());
			//infoFactura.setIdentificacionComprador(identificacionAbf);
			//infoFactura.setRazonSocialComprador(nombresAbf);
		} else {			
			razonSocialComprador = StringUtil.validateInfoXML(razonSocialComprador.trim());
			infoFactura.setIdentificacionComprador(set.getString("identificacionComprador"));
			infoFactura.setRazonSocialComprador(razonSocialComprador);
		}
		//cambio realizado por fybeca
		if(infoFactura.getRazonSocialComprador().trim().length()==0)
    	   infoFactura.setRazonSocialComprador("N/D");
       
       //infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll("[^\\x00-\\x7F]", ""));
       infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll("^", ""));  
       String regex = "[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]";
       infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll(regex, ""));
       //cadenaRevisar = cadenaRevisar.replaceAll("[^\\x00-\\x7F]", "");
       //termina cambio de fybeca
		String dirEstablecimiento = set.getString("dirEstablecimiento");
		dirEstablecimiento = StringUtil.validateInfoXML(dirEstablecimiento);

		infoFactura
				.setDirEstablecimiento(dirEstablecimiento != null ? StringEscapeUtils
						.escapeXml(dirEstablecimiento.trim())
						.replaceAll("[\n]", "").replaceAll("[\b]", "")
						: null);

		// infoFactura.setImporteTotal(set.getBigDecimal("importeTotal"));
		infoFactura
				.setFechaEmision(set.getDate("fechaEmision") != null ? FechaUtil
						.formatearFecha(
								FechaUtil.convertirLongADate(set.getDate(
										"fechaEmision").getTime()),
								FechaUtil.DATE_FORMAT) : null);
		// infoFactura.setFechaEmision(FechaUtil.formatearFecha(FechaUtil.convertirLongADate(Calendar.getInstance().getTime().getTime()),FechaUtil.DATE_FORMAT));
		infoFactura.setImporteTotal(set.getBigDecimal("importeTotal"));
		infoFactura.setMoneda(set.getString("moneda"));
		infoFactura.setPropina(set.getBigDecimal("propina"));
		// infoFactura.setTotalDescuento(set.getBigDecimal("totalDescuento"));
		// infoFactura.setTotalDescuento(obtenerTotalDescuento(credencialDS,
		// set.getLong("codigo_externo"), farmacia));
		infoFactura.setTotalDescuento(BigDecimal.ZERO);
		String identificacion = infoFactura.getIdentificacionComprador();

		if (identificacion != null && !identificacion.isEmpty()) {
			tipoIdentificacionComprador = obtenerIdentificacion(conn,
					identificacion);
			if (tipoIdentificacionComprador != null
					&& !tipoIdentificacionComprador.isEmpty()) {

				if (tipoIdentificacionComprador.equalsIgnoreCase("C")) {
					infoFactura.setTipoIdentificacionComprador("05");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("R")) {
					infoFactura.setTipoIdentificacionComprador("04");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("P")) {
					infoFactura.setTipoIdentificacionComprador("06");
				} else if (tipoIdentificacionComprador.equalsIgnoreCase("N")) {
					infoFactura.setTipoIdentificacionComprador("07");
				}
			}

			if (infoFactura != null
					&& infoFactura.getTipoIdentificacionComprador() != null
					&& infoFactura.getTipoIdentificacionComprador()
							.equalsIgnoreCase("07")) {
				infoFactura.setIdentificacionComprador("9999999999999");
				infoFactura.setRazonSocialComprador("CONSUMIDOR FINAL");
			} else {
				infoFactura.setTipoIdentificacionComprador("06");

				if (StringUtil.esNumero(identificacion)) {
					if (identificacion.length() == 10) {
						infoFactura.setTipoIdentificacionComprador("05");
					} else if (identificacion.length() == 13
							&& identificacion.substring(10).equals("001")) {
						infoFactura.setTipoIdentificacionComprador("04");
					}
				}

				infoFactura.setIdentificacionComprador(identificacion);
			}
		} else {
			infoFactura.setIdentificacionComprador("9999999999999");
			infoFactura.setTipoIdentificacionComprador("07");
			infoFactura.setRazonSocialComprador("CONSUMIDOR FINAL");
		}
		infoFactura
				.setTotalSinImpuestos(set.getBigDecimal("totalSinImpuestos"));
		factura.setInfoFactura(infoFactura);

		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setCodDoc(set.getString("codDoc"));
		infoTributaria.setEstab(set.getString("estab"));
		// infoTributaria.setEstab("001");
		// infoTributaria.setNombreComercial(item.getRA);
		// infoTributaria.setDirMatriz(item.get);
		infoTributaria.setPtoEmi(set.getString("ptoEmi"));
		// infoTributaria.setRazonSocial(item.getr);
		infoTributaria.setRuc(set.getString("ruc"));
		// infoTributaria.setRuc("1792366259001");

		String nombreSucursal = set.getString("nombreSucursal");
		nombreSucursal = StringUtil.validateInfoXML(nombreSucursal);
		infoTributaria.setNombreComercial(nombreSucursal);

		infoTributaria.setSecuencial(set.getLong("secuencial") + "");

		factura.setInfoTributaria(infoTributaria);
		
		//pagos ojo
		List<Pago> pagos = listarPagos(conn, documentoVenta, farmacia);
		if(pagos != null && !pagos.isEmpty()){
			factura.getInfoFactura().setPagos(new Pagos());
			factura.getInfoFactura().getPagos().getPago().addAll(pagos);
		}

		facturaProcesarRequest = new FacturaProcesarRequest();
		facturaProcesarRequest.setFactura(factura);
		facturaProcesarRequest.setCodigoExterno(set.getLong("codigo_externo")
				+ "");

		facturaProcesarRequest.setIdentificadorUsuario((set
				.getString("identificacionComprador") != null && !set
				.getString("identificacionComprador").isEmpty()) ? set
				.getString("identificacionComprador") : null);
		
		facturaProcesarRequest.setAgencia(farmacia.toString());
		facturaProcesarRequest.setPaciente(nombresAbf);
		return facturaProcesarRequest;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BigDecimal obtenerTotalDescuento(Connection conn,
			Long documentoVenta, Long farmacia) throws GizloException {

		PreparedStatement ps = null;
		BigDecimal resultado = null;
		try {
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
		}

		return resultado;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String obtenerIdentificacion(Connection conn, String identificacion)
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

	// ----------------jose
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String obtenerCodigoAbf(Connection conn, Long codigoVenta,
			Long farmacia) throws GizloException {

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		Long resultado = null;
		String personaAbf = null;
		try {
			StringBuilder sqlString = new StringBuilder();
			sqlString
					.append("select persona_abf from fa_facturas_mas where documento_venta = ? and farmacia = ? and persona_abf is not null ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, codigoVenta);
			ps.setLong(2, farmacia);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getLong("persona_abf");
			}

			if (resultado != null) {
				
				if(resultado>0){

					sqlString = new StringBuilder();
					sqlString.append("select identificacion_titular, nombres from ab_beneficiarios_abf where codigo = ? ");

					ps2 = conn.prepareStatement(sqlString.toString());
					ps2.setLong(1, resultado);

					ResultSet set2 = ps2.executeQuery();

					while (set2.next()) {
						personaAbf = set2.getString("identificacion_titular") + "&"
								+ set2.getString("nombres");
					}
				}
				
				if(resultado==0){
					sqlString = new StringBuilder();
					sqlString.append(" select fi.identificacion_titular as identificacion_titular,fi.nombres_titular as nombres ");
					sqlString.append(" from farmacias.fa_integracion_abf_aseg fi ");
					sqlString.append(" where fi.documento_venta = ? ");
                    sqlString.append(" and fi.farmacia = ? ");
                    
                    
					ps2 = conn.prepareStatement(sqlString.toString());
					ps2.setLong(1, codigoVenta);
					ps2.setLong(2, farmacia);

					ResultSet set2 = ps2.executeQuery();

					while (set2.next()) {
						personaAbf = set2.getString("identificacion_titular") + "&"
								+ set2.getString("nombres");
					}
				
					
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return personaAbf;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String obtenerClientePersonaAbf(Connection conn, String identificacion)
			throws GizloException {

		PreparedStatement ps = null;
		String resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();
			sqlString.append("select identificacion as identificacion, (primer_apellido ||' '|| segundo_apellido ||' '|| primer_nombre ||' '|| segundo_nombre) as razonSocialComprador ");
			sqlString.append("from ab_personas where identificacion = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, identificacion);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getString("identificacion") + "&" + set.getString("razonSocialComprador");
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

	// /---------fin

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Date obtenerFechaEmision(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException {

		PreparedStatement ps = null;
		Date resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select f.fecha as fechaEmision from fa_facturas f, ad_farmacias ");
			sqlString.append("where f.farmacia = ? and f.documento_venta = ?");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);

			ResultSet set = ps.executeQuery();

			while (set.next()) {
				resultado = set.getDate("fechaEmision");
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

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long obtenerSecuencialFactura(Connection conn, Long documentoVenta,
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

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long obtenerSecuencialFacturaAutoimpresor(Connection conn,
			Long documentoVenta, Long farmacia, Long tipo)
			throws GizloException {

		PreparedStatement ps = null;
		Long resultado = null;
		try {
			StringBuilder sqlString = new StringBuilder();

			sqlString
					.append("select nvl(fa_secs.secuencia,to_number(substr(f.numero_sri,9,15))) as secuencial ");
			sqlString.append("from fa_factura_adicional    fa, ");
			sqlString.append("fa_facturas             f, ");
			sqlString.append("ad_farmacias            c, ");
			sqlString.append("fa_secuencias_fact_elec fa_secs ");
			sqlString.append("where fa.documento_venta = f.documento_venta ");
			sqlString.append("and fa.farmacia = f.farmacia ");
			sqlString.append("and fa_secs.farmacia(+) = f.farmacia ");
			sqlString
					.append("and fa_secs.documento_venta(+) = f.documento_venta ");
			sqlString.append("and c.codigo = fa.farmacia ");
			sqlString.append("and f.farmacia        = ? ");
			sqlString.append("and f.documento_venta = ? ");
			sqlString.append("aND F.TIPO_MOVIMIENTO = '01' ");
			sqlString.append("AND F.CLASIFICACION_MOVIMIENTO = '01' ");
			sqlString.append("and exists (select *  ");
			sqlString.append("from fa_secuencias_fact_elec c ");
			sqlString
					.append("where c.documento_venta = f.documento_venta_padre ");
			sqlString.append("and c.farmacia = f.farmacia_padre ");
			sqlString.append("and c.tipo_documento = 4)  ");

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);
			// ps.setLong(3, tipo);

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

	private String obtenerCorreo(Connection conn, Long documentoVenta,Long farmacia) throws GizloException, SQLException {
		CallableStatement cstmt = null;
		String correo = null;
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("{call farmacias.fa_pkg_mov_general.pro_recupera_mail_cliente(?,?,?)}");
		cstmt = conn.prepareCall(sqlString.toString());
		cstmt.registerOutParameter(3, Types.VARCHAR);
		cstmt.setLong(1, documentoVenta);
		cstmt.setLong(2, farmacia);
		cstmt.execute();
		correo = cstmt.getString(3);
		if (cstmt != null)
			cstmt.close();

		return correo;

	}

	public InfoAdicional obtenerInfoAdicional(Connection conn, Long farmacia,Long factura,String banderaOferton) throws GizloException {
		try {

			InfoAdicional resultado = new InfoAdicional();
			String direccionStr = StringUtil.validateInfoXML(obtenerDireccion(conn, farmacia, factura));
			
			String descuentoStr = null;
			if(!"S".equals(banderaOferton))
					descuentoStr = StringUtil.validateInfoXML(obtenerDescuento(conn, farmacia, factura));

			if (direccionStr != null && !direccionStr.isEmpty()) {
				CampoAdicional direccion = new CampoAdicional();
				direccion.setNombre("DIRECCION");
				direccion.setValue(direccionStr.replace(".", "").replace("¿", "").replace(",", "").trim());
				resultado.getCampoAdicional().add(direccion);
			}
			if (descuentoStr != null && !descuentoStr.isEmpty()) {
				CampoAdicional descuento = new CampoAdicional();
				descuento.setNombre("DESCUENTO");
				descuento.setValue(descuentoStr.replace("-", "").trim());
				resultado.getCampoAdicional().add(descuento);

			}

			Map<String, String> deducibles = obtenerValorDeducible(conn,
					farmacia, factura);
			String valor = null;
			if (deducibles != null && !deducibles.isEmpty()) {
				CampoAdicional deducible = null;
				for (String nombre : deducibles.keySet()) {
					if (nombre != null && !nombre.isEmpty()) {
						valor = StringUtil.validateInfoXML(deducibles
								.get(nombre));
						if (valor != null && !valor.isEmpty()) {
							if(!(nombre.toUpperCase().trim().equals("NO DEDUCIBLE".toUpperCase()))){
								deducible = new CampoAdicional();
								deducible.setNombre("DEDUCIBLE " + nombre);
								deducible.setValue(valor.trim());
								resultado.getCampoAdicional().add(deducible);
							}
							
						}
					}
				}
			}

			return resultado;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}

	private String obtenerDireccion(Connection conn, Long farmacia, Long factura)
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

	private String obtenerDescuento(Connection conn, Long farmacia, Long factura)
			throws SQLException {
		PreparedStatement ps = null;
		String resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString.append("select (pvp_total_factura-venta_total_factura) as valor from fa_facturas where documento_venta = ? and farmacia = ?");

		ps = conn.prepareStatement(sqlString.toString());

		ps.setLong(1, factura);
		ps.setLong(2, farmacia);

		ResultSet set = ps.executeQuery();

		while (set.next()) {
			resultado = set.getBigDecimal("valor") != null ? set.getBigDecimal(
					"valor").toString() : null;
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();
		return resultado;
	}
	
	

	
	
	private String banderaOferton(Connection conn, Long farmacia) throws SQLException {
		PreparedStatement ps = null;
		String resultado = "N";
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
		return resultado;
	}
	
	
//	private String obtenerCompensacion(Connection conn, Long farmacia, Long factura)
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

	private Map<String, String> obtenerValorDeducible(Connection conn,
			Long farmacia, Long factura) throws SQLException {
		PreparedStatement ps = null;
		Map<String, String> resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString
				.append("select c.nombre as deduccion, round(sum((a.venta*a.cantidad)),2) as valor ");
		sqlString
				.append("from fa_detalles_factura a, pr_datos_adicionales_items b, pr_deducible_impuesto_renta c ");
		sqlString
				.append("where a.documento_venta = ? and a.farmacia = ? and b.item = a.item and c.codigo = b.campo_n3 group by b.campo_n3, c.nombre order by decode(b.campo_n3,1,2,2,1,b.campo_n3)");

		ps = conn.prepareStatement(sqlString.toString());

		ps.setLong(1, factura);
		ps.setLong(2, farmacia);

		ResultSet set = ps.executeQuery();
		resultado = new HashMap<String, String>();
		String key = null;
		while (set.next()) {
			key = set.getString("deduccion");
			if (key != null && !key.isEmpty()) {
				resultado.put(key, set.getBigDecimal("valor") != null ? set
						.getBigDecimal("valor").toString() : null);
			}
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		return resultado;
	}

	private List<Compensacion> listarCompensacion(Connection conn, Long idFactura, Long idFarmacia) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Compensacion compensacionXml = null;
		List<Compensacion> list = new ArrayList<Compensacion>();
		StringBuilder sqlString = new StringBuilder();	
		log.info("****obteniendo Compensacion"+idFarmacia+"---"+idFactura);
		sqlString.append("SELECT VENTA_FACTURA ");
		sqlString.append("FROM fa_detalles_formas_pago ");
		sqlString.append("where forma_pago = 'SUB' ");
		sqlString.append("AND DOCUMENTO_VENTA = ?");
		sqlString.append("AND FARMACIA = ? ");
		
		ps = conn.prepareStatement(sqlString.toString());
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
				
		return list;
				
	}
	
	private List<Pago> listarPagos(Connection conn, Long idFactura, Long idFarmacia) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Pago pagoXml = null;
		List<Pago> listPagos = new ArrayList<Pago>();
		StringBuilder sqlString = new StringBuilder();
		log.info("****obteniendo pagos"+idFarmacia+"---"+idFactura);
		
//		sqlString.append("select mfp.codigo forma_pago, p.venta_factura total, AD.Cuotas_Tarjetahabiente plazo, ");
//		sqlString.append("decode( AD.Cuotas_Tarjetahabiente,null,null,'MESES') unidad_tiempo ");
//		sqlString.append("from fA_facturas f, fa_detalles_formas_pago p,farmacias.fa_mapeo_formas_pago mfp, ad_planes_credito ad ");
//		sqlString.append("where f.farmacia = ? and f.documento_venta = ? ");
//		sqlString.append("and f.farmacia = p.farmacia and f.documento_venta = p.documento_venta ");
//		sqlString.append("and p.forma_pago = mfp.mapeo_pionus and p.plan_credito = ad.codigo(+) ");
		
		sqlString.append("select mfp.cp_varchar  forma_pago, ");
		sqlString.append("p.venta_factura total, ");
		sqlString.append("AD.Cuotas_Tarjetahabiente plazo, ");
		sqlString.append("decode( AD.Cuotas_Tarjetahabiente,null,null,'MESES') unidad_tiempo ");
		sqlString.append("from fA_facturas f, ");
		sqlString.append("fa_detalles_formas_pago p, ");
		sqlString.append("farmacias.fa_mapeo_formas_pago mfp, ");
		sqlString.append("ad_planes_credito ad ");
		sqlString.append("where f.farmacia = ? ");
		sqlString.append("and f.documento_venta = ? ");
		sqlString.append("and f.farmacia = p.farmacia ");
		sqlString.append("and f.documento_venta = p.documento_venta ");
		sqlString.append("and p.forma_pago = mfp.mapeo_pionus ");
		sqlString.append("and p.plan_credito = ad.codigo(+) ");
		
		ps = conn.prepareStatement(sqlString.toString());
		ps.setLong(1, idFarmacia);
		ps.setLong(2, idFactura);
		log.info("****obteniendo pagos 000");
		ResultSet set = ps.executeQuery();
		log.info("****obteniendo pagos 111");
		while (set.next()) {
			pagoXml = new Pago();
			log.info("****obteniendo pagos 333--"+set.getString("forma_pago"));
			pagoXml.setFormaPago(set.getString("forma_pago"));
			log.info("****obteniendo pagos 444--"+pagoXml.getFormaPago());
			pagoXml.setTotal(set.getBigDecimal("total"));
			pagoXml.setPlazo(set.getBigDecimal("plazo"));
			pagoXml.setUnidadTiempo(set.getString("unidad_tiempo"));
			listPagos.add(pagoXml);	
		}
		
		log.info("****obteniendo pagos 333"+listPagos.size());
			
		return listPagos;
				
	}
	
}
