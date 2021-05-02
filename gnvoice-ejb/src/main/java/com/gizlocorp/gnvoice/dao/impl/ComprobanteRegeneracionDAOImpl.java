package com.gizlocorp.gnvoice.dao.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.gizlocorp.gnvoice.dao.ComprobanteRegeneracionDAO;
import com.gizlocorp.gnvoice.enumeracion.TipoEmisionEnum;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.xml.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.InfoTributaria;
import com.gizlocorp.gnvoice.xml.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.factura.Factura;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles;
import com.gizlocorp.gnvoice.xml.factura.Factura.Detalles.Detalle;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoAdicional.CampoAdicional;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Pagos;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.Pagos.Pago;
import com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto;
import com.gizlocorp.gnvoice.xml.factura.Impuesto;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoNotaCredito;
import com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos;
import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion;
import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.Impuestos;
import com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoCompRetencion;



@Stateless
public class ComprobanteRegeneracionDAOImpl implements ComprobanteRegeneracionDAO{
	
	private static Logger log = Logger.getLogger(ComprobanteRegeneracionDAOImpl.class.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long obtenerTipo(Connection conn, String comprobante) throws GizloException {
		PreparedStatement ps = null;
		try {
			StringBuilder sqlString = new StringBuilder();
			sqlString.append("select * from farmacias.fa_tipo_comprobante where descripcion = ?");
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
	
	
	
	
	
	

	@SuppressWarnings("unchecked")
	@Override
	public FacturaProcesarRequest listar(Connection conn, Long documentoVenta,
			Long farmacia, Long tipo) throws GizloException {
		
		PreparedStatement ps = null;
		ResultSet set = null;
		FacturaProcesarRequest facturaFybeca = null;

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

			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, farmacia);
			ps.setLong(2, documentoVenta);
			ps.setLong(3, tipo);

			log.debug("parametros de dao factura son: " + farmacia + " doc: "
					+ documentoVenta);
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
					String correo = obtenerCorreo(conn,
							Long.valueOf(facturaFybeca.getCodigoExterno()),
							farmacia);

					facturaFybeca.setCorreoElectronicoNotificacion(StringUtil
							.validateEmail(correo));

					// AG<<
					detalesTotalesImpuesto = listarDetalles(conn,
							Long.valueOf(facturaFybeca.getCodigoExterno()),
							farmacia);

					detalles = (List<Detalle>) detalesTotalesImpuesto
							.get(Constantes.DETALLES);
					log.debug("**servicio listar detalles: "
							+ (detalles != null ? detalles.size() : 0));
					totalesImpuestos = (List<com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto>) detalesTotalesImpuesto
							.get(Constantes.TOTAL_IMPUESTO);
					facturaFybeca
							.getFactura()
							.getInfoFactura()
							.setTotalSinImpuestos(
									(BigDecimal) detalesTotalesImpuesto
											.get(Constantes.TOTAL_SIN_IMPUESTO));

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

					InfoAdicional infoAdiconal = obtenerInfoAdicional(conn,
							farmacia,
							Long.valueOf(facturaFybeca.getCodigoExterno()));
					
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
	
	
	
	/**
	 * 
	 @author spramirezv
	 @DecripcionDeCambio Cambio realializado para generacion de xml y pdf bajo demanda de JDE  
	 
	 */
	

	
	@Override
	@SuppressWarnings("unchecked")
	public FacturaProcesarRequest listarJDE(Connection conn, String claveAcceso) throws GizloException {
		
		PreparedStatement ps = null;
		ResultSet set = null;
		FacturaProcesarRequest facturaFybeca = null;

		if (claveAcceso == null || "".equals(claveAcceso)) {
			throw new GizloException("numero de documento no existe");
		}
		try {
			StringBuilder sqlString = new StringBuilder();
			
			
			sqlString.append("select distinct XMAA10 as secuencial, ");
			sqlString.append(""+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".juliano_fecha(XMTRDJ) as fechaEmision, ");
			sqlString.append("rtrim(XMC75XMLE4) || rtrim(XMC75XML10) || rtrim(XMC75XML11) || ");
			sqlString.append("rtrim(XMC75XML12) as dirEstablecimiento, ");
			sqlString.append("XMAA05 as contribuyenteEspecial, ");
			sqlString.append("XMAA02 as obligadoContabilidad, ");
			sqlString.append("rtrim(XMADD1) || rtrim(XMADD2) || rtrim(XMADD3) || ");
			sqlString.append("rtrim(XMADD4) as nombreSucursal, ");
			sqlString.append("XMC9DSC2 as razonSocialComprador, ");
			sqlString.append("XMTAXX as identificacionComprador, ");
			sqlString.append("XMAA / 100 as totalSinImpuestos, ");
			sqlString.append("XMADSC / 100 as totalDescuento, ");
			sqlString.append("XMATXN / 100 as propina, ");
			sqlString.append("XMAEXP / 100 as importeTotal, ");
			sqlString.append("XMAA15 as moneda, ");
			sqlString.append("XMCM01 as ambiente, ");
			sqlString.append("XMCM02 as tipoEmision, ");
			sqlString.append("XMGIOD as razonSocial, ");
			sqlString.append("XMC75XMLE2 as nombreComercial, ");
			sqlString.append("XMTAX as ruc, ");
			sqlString.append("XMDCTV as codDoc, ");
			sqlString.append("XMCTC1 as estab, ");
			sqlString.append("XMCTC2 as ptoEmi, ");
			sqlString.append("XMDOC as codigo_externo , ");
			sqlString.append("XMDS20 as guiaRemision ");
			sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxml ");
			sqlString.append("where  XMDCTV = '01' ");
			sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
			sqlString.append("union all  ");
			sqlString.append("select distinct XMAA10 as secuencial, ");
			sqlString.append(""+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".juliano_fecha(XMTRDJ) as fechaEmision, ");
			sqlString.append("rtrim(XMC75XMLE4) || rtrim(XMC75XML10) || rtrim(XMC75XML11) || ");
			sqlString.append("rtrim(XMC75XML12) as dirEstablecimiento, ");
			sqlString.append("XMAA05 as contribuyenteEspecial, ");
			sqlString.append("XMAA02 as obligadoContabilidad, ");
			sqlString.append("rtrim(XMADD1) || rtrim(XMADD2) || rtrim(XMADD3) || ");
			sqlString.append("rtrim(XMADD4) as nombreSucursal, ");
			sqlString.append("XMC9DSC2 as razonSocialComprador, ");
			sqlString.append("XMTAXX as identificacionComprador, ");
			sqlString.append("XMAA / 100 as totalSinImpuestos, ");
			sqlString.append("XMADSC / 100 as totalDescuento, ");
			sqlString.append("XMATXN / 100 as propina, ");
			sqlString.append("XMAEXP / 100 as importeTotal, ");
			sqlString.append("XMAA15 as moneda, ");
			sqlString.append("XMCM01 as ambiente, ");
			sqlString.append("XMCM02 as tipoEmision, ");
			sqlString.append("XMGIOD as razonSocial, ");
			sqlString.append("XMC75XMLE2 as nombreComercial, ");
			sqlString.append("XMTAX as ruc, ");
			sqlString.append("XMDCTV as codDoc, ");
			sqlString.append("XMCTC1 as estab, ");
			sqlString.append("XMCTC2 as ptoEmi, ");
			sqlString.append("XMDOC as codigo_externo, ");
			sqlString.append("XMDS20 as guiaRemision ");
			sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxmh ");
			sqlString.append("where  XMDCTV = '01'  ");
			sqlString.append("and XMOTGENKEY = '"+claveAcceso+"'");
			
			
			
		

			ps = conn.prepareStatement(sqlString.toString());
		//	ps.setString(1, claveAcceso);
		//	ps.setString(2, claveAcceso);

			set = ps.executeQuery();
			Map<String, Object> detalesTotalesImpuesto = null;
			List<Detalle> detalles = null;
			List<com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto> totalesImpuestos = null;
			CampoAdicional email = null;
			while (set.next()) {
				facturaFybeca = null;
				try {
					facturaFybeca = obtenerFacturaJDE(conn, set,claveAcceso);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}

				if (facturaFybeca != null) {
					detalesTotalesImpuesto = listarDetallesJDE(conn,claveAcceso);
					
					detalles = (List<Detalle>) detalesTotalesImpuesto.get(Constantes.DETALLES);
					totalesImpuestos = (List<com.gizlocorp.gnvoice.xml.factura.Factura.InfoFactura.TotalConImpuestos.TotalImpuesto>) detalesTotalesImpuesto.get(Constantes.TOTAL_IMPUESTO);
					facturaFybeca.getFactura().getInfoFactura().setTotalSinImpuestos((BigDecimal) detalesTotalesImpuesto.get(Constantes.TOTAL_SIN_IMPUESTO));

					

					if (totalesImpuestos != null && !totalesImpuestos.isEmpty()) {
						facturaFybeca.getFactura().getInfoFactura().setTotalConImpuestos(new Factura.InfoFactura.TotalConImpuestos());
						facturaFybeca.getFactura().getInfoFactura().getTotalConImpuestos().getTotalImpuesto().addAll(totalesImpuestos);
					}

					// DETALLE IMPUESTO
					if (detalles != null && !detalles.isEmpty()) {
						facturaFybeca.getFactura().setDetalles(new Detalles());
						facturaFybeca.getFactura().getDetalles().getDetalle().addAll(detalles);
					}

					if (facturaFybeca.getFactura().getInfoFactura().getTotalDescuento() == null) {
						facturaFybeca.getFactura().getInfoFactura().setTotalDescuento(BigDecimal.ZERO);
					}

					if (facturaFybeca.getFactura().getInfoFactura() != null&& facturaFybeca.getFactura().getInfoFactura().getTotalDescuento() != null) {
						facturaFybeca.getFactura().getInfoFactura().setImporteTotal(facturaFybeca.getFactura().getInfoFactura().getImporteTotal().subtract(facturaFybeca.getFactura().getInfoFactura().getTotalDescuento()));

					}

					InfoAdicional infoAdiconal = obtenerInfoAdicionalJDE(conn,claveAcceso);

					if (facturaFybeca.getCorreoElectronicoNotificacion() != null&& !facturaFybeca.getCorreoElectronicoNotificacion().isEmpty()) {

						if (infoAdiconal == null) {
							infoAdiconal = new InfoAdicional();
						}

						email = new CampoAdicional();
						email.setNombre("EMAIL");
						email.setValue(facturaFybeca.getCorreoElectronicoNotificacion());

						infoAdiconal.getCampoAdicional().add(email);

					}

					if (infoAdiconal != null&& infoAdiconal.getCampoAdicional() != null&& !infoAdiconal.getCampoAdicional().isEmpty()) {
						facturaFybeca.getFactura().setInfoAdicional(infoAdiconal);
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
	
	
	@Override
	public ComprobanteRetencion listarRetencionJDE(Connection conn, String claveAcceso) throws GizloException {
		
		PreparedStatement ps = null;
		ResultSet set = null;
		ComprobanteRetencion retencionFybeca = new ComprobanteRetencion();
		StringBuilder sqlString = new StringBuilder();
		if (claveAcceso == null || "".equals(claveAcceso)) {
			throw new GizloException("numero de documento no existe");
		}
		try {
			try {
				/**
				 *  InfoTributaria 
				 */
				InfoTributaria infoTributaria = null;
				sqlString.append("select distinct XMCM01 as ambiente, ");
				sqlString.append("XMCM02 as tipoEmision, ");
				sqlString.append("XMGIOD as razonSocial, ");
				sqlString.append("XMC75XMLE2 as nombreComercial, ");
				sqlString.append("XMTAX as ruc, ");
				sqlString.append("XMOTGENKEY as clave_acceso, ");
				sqlString.append("XMDCTV as codDoc, ");
				sqlString.append("XMCTC1 as estab, ");
				sqlString.append("XMCTC2 as ptoEmi, ");
				sqlString.append("XMDOC as secuencial, ");
				sqlString.append("rtrim(XMADD1) || rtrim(XMADD2) || rtrim(XMADD3) || ");
				sqlString.append("rtrim(XMADD4) as dirMatriz ");
				sqlString.append("from proddta.f76ecxml ");
				sqlString.append("where XMOTGENKEY = '"+claveAcceso+"' ");
				sqlString.append("and XMDCTV = '07' ");
				sqlString.append("union all ");
				sqlString.append("select distinct XMCM01 as ambiente, ");
				sqlString.append("XMCM02 as tipoEmision, ");
				sqlString.append("XMGIOD as razonSocial, ");
				sqlString.append("XMC75XMLE2 as nombreComercial, ");
				sqlString.append("XMTAX as ruc, ");
				sqlString.append("XMOTGENKEY as clave_acceso, ");
				sqlString.append("XMDCTV as codDoc, ");
				sqlString.append("XMCTC1 as estab, ");
				sqlString.append("XMCTC2 as ptoEmi, ");
				sqlString.append("XMDOC as secuencial, ");
				sqlString.append("rtrim(XMADD1) || rtrim(XMADD2) || rtrim(XMADD3) || ");
				sqlString.append("rtrim(XMADD4) as dirMatriz ");
				sqlString.append("from proddta.f76ecxmh ");
				sqlString.append("where XMOTGENKEY = '"+claveAcceso+"' ");
				sqlString.append("and XMDCTV = '07' ");
				ps = conn.prepareStatement(sqlString.toString());
				set = ps.executeQuery();
				while (set.next()) {
					infoTributaria = null;
					try {
						infoTributaria = getInfoTributaria(set) ;
					} catch (Exception ex) {
						log.error("Error en proceso DAO", ex);
					}
				}
				
				retencionFybeca.setInfoTributaria(infoTributaria);
			} catch (Exception e) {
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
			
			try {
				
				/**
				 *  InfoCompRetencion 
				 */
				InfoCompRetencion infoCompRetencion = null;
				sqlString = new StringBuilder();
				sqlString.append(" select distinct proddta.juliano_fecha(XMTRDJ) as fechaEmision, ");
				sqlString.append(" rtrim(XMC75XMLE4) as dirEstablecimiento,");
				sqlString.append(" XMAA05 as contribuyenteEspecial,");
				sqlString.append(" XMAA02 as obligadoContabilidad,");
				sqlString.append(" XMCTTY as tipoIdentifiSujetoRetenido,");
				sqlString.append(" XMTAXX as identificacionSujetoRetenido,");
				sqlString.append(" XMC9DSC2 as razonSocialSujetoRetenido , ");
				sqlString.append(" XMDS20 as periodoFiscal");
				sqlString.append(" from proddta.f76ecxml");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
				sqlString.append(" and XMDCTV = '07' ");
				sqlString.append(" union all ");
				sqlString.append(" select distinct proddta.juliano_fecha(XMTRDJ) as fechaEmision, ");
				sqlString.append(" rtrim(XMC75XMLE4) as dirEstablecimiento, ");
				sqlString.append(" XMAA05 as contribuyenteEspecial, ");
				sqlString.append(" XMAA02 as obligadoContabilidad, ");
				sqlString.append(" XMCTTY as tipoIdentifiSujetoRetenido, ");
				sqlString.append(" XMTAXX as identificacionSujetoRetenido, ");
				sqlString.append(" XMC9DSC2 as razonSocialSujetoRetenido , ");
				sqlString.append(" XMDS20 as periodoFiscal ");
				sqlString.append(" from proddta.f76ecxmh ");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"' ");
				sqlString.append(" and XMDCTV = '07' ");

				ps = conn.prepareStatement(sqlString.toString());
				set = ps.executeQuery();
				while (set.next()) {
					infoCompRetencion = null;
					try {
						infoCompRetencion = getInfoCompRetencion(set);
					} catch (Exception ex) {
						log.error("Error en proceso DAO", ex);
					}
				}
				
				retencionFybeca.setInfoCompRetencion(infoCompRetencion);

			} catch (Exception e) {
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
			
			try {
				
				/**
				 *  impuesto 
				 */
				
				com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.Impuestos impuestos = new com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.Impuestos();
				sqlString = new StringBuilder();
				sqlString.append(" SELECT XMEV04 as codigo,");
				sqlString.append(" XMBL01 as codigoRetencion,");
				sqlString.append(" XMAN04 / 100 as baseImponible,");
				sqlString.append(" XMAAN01 / 100 as porcentajeRetener,");
				sqlString.append(" XMAAN04 / 100 valorRetenido,");
				sqlString.append(" XMA201 as codDocSustento,");
				sqlString.append(" XMPNM01 as numDocSustento,");
				sqlString.append(" proddta.juliano_fecha(XMPDDJ) as fechaEmisionDocSustento");
				sqlString.append(" from proddta.f76ecxml");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
				sqlString.append(" and XMDCTV = '07' ");
				sqlString.append(" and xmlttr = '   '");
				sqlString.append(" and xmnxtr = '   '");
				sqlString.append(" union all");
				sqlString.append(" SELECT XMEV04 as codigo,");
				sqlString.append(" XMBL01 as codigoRetencion,");
				sqlString.append(" XMAN04 / 100 as baseImponible,");
				sqlString.append(" XMAAN01 / 100 as porcentajeRetener,");
				sqlString.append(" XMAAN04 / 100 valorRetenido,");
				sqlString.append(" XMA201 as codDocSustento,");
				sqlString.append(" XMPNM01 as numDocSustento,");
				sqlString.append(" proddta.juliano_fecha(XMPDDJ) as fechaEmisionDocSustento");
				sqlString.append(" from proddta.f76ecxmh");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
				sqlString.append(" and XMDCTV = '07' ");
				sqlString.append(" and xmlttr = '   '");
				sqlString.append(" and xmnxtr = '   '");

				ps = conn.prepareStatement(sqlString.toString());
				set = ps.executeQuery();
				
				
				while (set.next()) {
					com.gizlocorp.gnvoice.xml.retencion.Impuesto impuesto= new com.gizlocorp.gnvoice.xml.retencion.Impuesto();
					try {
						
						impuesto =  getImpuesto(set);
					} catch (Exception ex) {
						log.error("Error en proceso DAO", ex);
					}
					
					impuestos.getImpuesto().add(impuesto);
					
					
					
				}
				
				retencionFybeca.setImpuestos(impuestos);
				
			} catch (Exception e) {
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
			

			try {
				/**
				 *  Datos Adicionales 
				 */
				com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoAdicional infoAdicional = new com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoAdicional(); 
				sqlString = new StringBuilder();
				sqlString.append(" select distinct to_nchar('Dirección') as dato, XMBDS4 as valor");
				sqlString.append(" from proddta.f76ecxml");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
				sqlString.append(" and XMDCTV = '07' ");
				sqlString.append(" union all ");
				sqlString.append(" select distinct to_nchar('Dirección') dato, XMBDS4 as valor");
				sqlString.append(" from proddta.f76ecxmh");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
				sqlString.append(" and XMDCTV = '07'");
				sqlString.append(" union all");
				sqlString.append(" select distinct XMC75XMLE6 as dato, XMC75XMLE7 as valor");
				sqlString.append(" from proddta.f76ecxml");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
				sqlString.append(" and XMDCTV = '07'");
				sqlString.append(" union all");
				sqlString.append(" select distinct XMC75XMLE6 as dato, XMC75XMLE7 as valor");
				sqlString.append(" from proddta.f76ecxmh");
				sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
				sqlString.append(" and XMDCTV = '07' ");
				sqlString.append(" union all   ");
			    sqlString.append(" select distinct XMC75XMLE8 as dato, XMC75XML15 as valor");
			    sqlString.append(" from proddta.f76ecxml");
			    sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
			    sqlString.append(" and XMDCTV = '07' ");
			    sqlString.append(" union all ");
			    sqlString.append(" select distinct XMC75XMLE8 as dato, XMC75XML15 as valor");
			    sqlString.append(" from proddta.f76ecxmh");
			    sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
			    sqlString.append(" and XMDCTV = '07' ");
			    sqlString.append(" union all   ");
			    sqlString.append(" select distinct XMC75XML16 as dato, XMC75XML17 as valor");
			    sqlString.append(" from proddta.f76ecxml ");
			    sqlString.append(" where XMOTGENKEY = '"+claveAcceso+"'");
			    sqlString.append(" and XMDCTV = '07' ");
			    
			    ps = conn.prepareStatement(sqlString.toString());
				set = ps.executeQuery();
				while (set.next()) {
					 com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoAdicional.CampoAdicional campoAdicional = new  com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoAdicional.CampoAdicional();  
					try {
						campoAdicional = getInfoAdicional( set);
					} catch (Exception ex) {
						log.error("Error en proceso DAO", ex);
					}
					infoAdicional.getCampoAdicional().add(campoAdicional);
				}
				retencionFybeca.setInfoAdicional(infoAdicional);
			} catch (Exception e) {
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

		} catch (Exception e) {
			retencionFybeca =null;
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

		return retencionFybeca;
	}
	
	
	/***
	 * 
	@Fin Cambio
	 */
	
	
	
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

		List<Impuesto> detalleImpuestos = new ArrayList<Impuesto>();

		// AG>>
		StringBuilder sqlString = new StringBuilder();

		// ROUND((precio_fybeca - venta)/cantidad,2)
		log.debug("Detalle:  " + idFactura + " " + idFarmacia);
		sqlString
				.append("(SELECT (fa_detalles_servicios.porcentaje_iva) as porc_iva, item, cantidad, 0.00 as descuento12, 0.00 as descuento0, ROUND(cantidad * venta, 2) as precio_sin_imp, ");
		// <<AG
		sqlString
				.append("ROUND((cantidad * venta * fa_detalles_servicios.porcentaje_iva), 4) as valor, ROUND((cantidad * venta), 4) as baseImp, ");
		// AG>>
		sqlString
				.append("ROUND(venta, 4) as venta, (fa_servicios.nombre) as descripcion ");
		sqlString.append("FROM fa_detalles_servicios inner join fa_servicios ");
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
		totalSinImpuesto = totalSinImpuesto.setScale(2,
				BigDecimal.ROUND_HALF_UP);

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
	
	
	/** 
	    @author spramirezv
	    @descripcionDelCambio  Creación de query JDE detalle 09/08/2017
	  
	  **/
	
	
	private Map<String, Object> listarDetallesJDE(Connection conn, String claveAcceso) throws GizloException, SQLException {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		PreparedStatement ps = null;
		List<Detalle> detalles = new ArrayList<Detalle>();
		Detalle detalleXML = null;
		String descripcion = null;

		
		List<TotalImpuesto> totalesImpuestos = new ArrayList<TotalImpuesto>();
		TotalImpuesto totalImpuestoXML = null;
		Impuesto impuestoXML = null;
		BigDecimal total12 = new BigDecimal(0f)
				 , total0 = new BigDecimal(0f)
				 , baseImp0 = new BigDecimal(0f)
				 , baseImp12 = new BigDecimal(0f)
				 , totalSinImpuesto = new BigDecimal(0f)
				 , baseImp = new BigDecimal(0f)
				 , valor = new BigDecimal(0f);

		List<Impuesto> detalleImpuestos = new ArrayList<Impuesto>();
		StringBuilder sqlString = new StringBuilder();
		
		sqlString.append("SELECT distinct (select to_number(trim(drky),'999.999')/100 iva ");
		sqlString.append("from crpctl.f0005 ");
		sqlString.append("where drsy = '76E' ");
		sqlString.append("and DRRT = 'IV' ");
		sqlString.append("and drdl01 = XMBL01)porc_iva, ");
		sqlString.append("XMLITM as item, ");
		sqlString.append("XMUORG as cantidad, ");
		sqlString.append("XMAADSA as descuento12, ");
		sqlString.append("XMAADSC as descuento0, ");
		sqlString.append("XMCTXN / 100 as precio_sin_imp, ");
		sqlString.append("XMAAN04 / 100 as valor, ");
		sqlString.append("XMAN04 / 100 as baseImp, ");
		sqlString.append("XMLPRC / 10000 as venta, ");
		sqlString.append("XMDL011 || XMA030 as descripcion ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxml ");
		sqlString.append("where XMDCTV = '01' ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		sqlString.append("union all ");
		sqlString.append("SELECT distinct (select to_number(trim(drky),'999.999')/100 iva ");
		sqlString.append("from crpctl.f0005 ");
		sqlString.append("where drsy = '76E' ");
		sqlString.append("and DRRT = 'IV' ");
		sqlString.append("and drdl01 = XMBL01)porc_iva, ");
		sqlString.append("XMLITM as item, ");
		sqlString.append("XMUORG as cantidad, ");
		sqlString.append("XMAADSA as descuento12, ");
		sqlString.append("XMAADSC as descuento0, ");
		sqlString.append("XMCTXN / 100 as precio_sin_imp, ");
		sqlString.append("XMAAN04 / 100 as valor, ");
		sqlString.append("XMAN04 / 100 as baseImp, ");
		sqlString.append("XMLPRC / 10000 as venta, ");
		sqlString.append("XMDL011 || XMA030 as descripcion ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxmh ");
		sqlString.append("where XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");

		ps = conn.prepareStatement(sqlString.toString());
		//ps.setString(1, claveAcceso);
		//ps.setString(2, claveAcceso);


		ResultSet set = ps.executeQuery();
		while (set.next()) {
			if (set.getBigDecimal("porc_iva") != null) {
				detalleXML = new Detalle();
				detalleXML.setCantidad(new BigDecimal(set.getLong("cantidad")));
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
					detalleXML.setPrecioTotalSinImpuesto(set.getBigDecimal("precio_sin_imp"));
				} else {
					detalleXML.setPrecioTotalSinImpuesto(set.getBigDecimal("precio_sin_imp"));
				}
				detalleXML.setPrecioUnitario(set.getBigDecimal("venta"));

				detalleImpuestos = new ArrayList<Impuesto>();

				if (set.getBigDecimal("porc_iva").equals(new BigDecimal(0))) {
					// 0%
					total0 = total0.add((set.getBigDecimal("valor") != null ? set.getBigDecimal("valor") : new BigDecimal(0)));
					baseImp0 = baseImp0.add((set.getBigDecimal("baseImp") != null ? set.getBigDecimal("baseImp") : new BigDecimal(0)));

					baseImp = set.getBigDecimal("baseImp") != null ? set.getBigDecimal("baseImp") : BigDecimal.ZERO;
					valor = set.getBigDecimal("valor") != null ? set.getBigDecimal("valor") : BigDecimal.ZERO;

					baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
					valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);

					impuestoXML = new Impuesto();
					impuestoXML.setCodigo("2");
					impuestoXML.setCodigoPorcentaje("0");
					impuestoXML.setTarifa(new BigDecimal(0));
					impuestoXML.setBaseImponible(baseImp);
					impuestoXML.setValor(valor);
					detalleImpuestos.add(impuestoXML);

				}else {
					// 12%
					total12 = total12.add((set.getBigDecimal("valor") != null ? set.getBigDecimal("valor") : new BigDecimal(0)));
					baseImp12 = baseImp12.add((set.getBigDecimal("baseImp") != null ? set.getBigDecimal("baseImp") : new BigDecimal(0)));

					baseImp = set.getBigDecimal("baseImp") != null ? set.getBigDecimal("baseImp") : BigDecimal.ZERO;
					valor = set.getBigDecimal("valor") != null ? set.getBigDecimal("valor") : BigDecimal.ZERO;

					baseImp = baseImp.setScale(2, BigDecimal.ROUND_HALF_UP);
					valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);

					impuestoXML = new Impuesto();
					impuestoXML.setCodigo("2");
					impuestoXML.setCodigoPorcentaje("2");
					impuestoXML.setTarifa(new BigDecimal(12));
					impuestoXML.setBaseImponible(baseImp);
					impuestoXML.setValor(valor);
					detalleImpuestos.add(impuestoXML);
					
				}
				
				
				/*if (!set.getBigDecimal("porc_ice").equals(new BigDecimal(0))){
					totalIce = total12.add((set.getBigDecimal("porc_ice") != null ? set.getBigDecimal("porc_ice") : new BigDecimal(0)));
				}*/
				
				
				

				if (detalleImpuestos != null && !detalleImpuestos.isEmpty()) {
					detalleXML.setImpuestos(new Factura.Detalles.Detalle.Impuestos());
					detalleXML.getImpuestos().getImpuesto().addAll(detalleImpuestos);
				}
				detalles.add(detalleXML);
			}
		}
		
		totalSinImpuesto = totalSinImpuesto.add(baseImp0);
		totalSinImpuesto = totalSinImpuesto.add(baseImp12);
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
		
		
/*		if (!totalIce.equals(new BigDecimal(0))) {
			// ICE%
			baseImpIce = baseImpIce.setScale(2, BigDecimal.ROUND_HALF_UP);
			totalIce = totalIce.setScale(2, BigDecimal.ROUND_HALF_UP);

			totalImpuestoXML = new TotalImpuesto();
			totalImpuestoXML.setCodigo("3");
			//totalImpuestoXML.setCodigoPorcentaje("2");
			//totalImpuestoXML.setTarifa(new BigDecimal(12));
			//totalImpuestoXML.setBaseImponible(baseImpIce);
			totalImpuestoXML.setValor(totalIce);
			totalesImpuestos.add(totalImpuestoXML);
		}
*/
		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		log.debug("DETALLES RETORNADOS: "+ (detalles != null ? detalles.size() : 0));
		respuesta.put(Constantes.DETALLES, detalles);
		respuesta.put(Constantes.TOTAL_IMPUESTO, totalesImpuestos);
		respuesta.put(Constantes.TOTAL_SIN_IMPUESTO, totalSinImpuesto);

		return respuesta;
	}
	
	/**
	  @FinalDeCambio
	 */
	
	/**
	 @author spramirezv
	 @descripcionDelCambio obtener factura JDE 
	 */
	private FacturaProcesarRequest obtenerFacturaJDE(Connection conn,ResultSet set, String claveAcceso)throws SQLException, GizloException {
		
		Factura factura = null;
		String tipoIdentificacionComprador = null;
		FacturaProcesarRequest facturaProcesarRequest = null;
		factura = new Factura();
		InfoFactura infoFactura = new InfoFactura();
	
		
		String razonSocialComprador = set.getString("razonSocialComprador").trim();
		razonSocialComprador = StringUtil.validateInfoXML(razonSocialComprador.trim());
		infoFactura.setIdentificacionComprador(set.getString("identificacionComprador"));
		infoFactura.setRazonSocialComprador(razonSocialComprador);

			
		if(infoFactura.getRazonSocialComprador().trim().length()==0)
			infoFactura.setRazonSocialComprador("N/D");
       
       infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll("[^\\x00-\\x7F]", ""));
       infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll("^", ""));  
       String regex = "[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]";
       infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll(regex, ""));
       String dirEstablecimiento = set.getString("dirEstablecimiento");
	   dirEstablecimiento = StringUtil.validateInfoXML(dirEstablecimiento);

	   infoFactura.setDirEstablecimiento(dirEstablecimiento != null ? StringEscapeUtils.escapeXml(dirEstablecimiento.trim()).replaceAll("[\n]", "").replaceAll("[\b]", ""): null);

	   infoFactura.setFechaEmision(set.getDate("fechaEmision") != null ? FechaUtil.formatearFecha(FechaUtil.convertirLongADate(set.getDate("fechaEmision").getTime()),FechaUtil.DATE_FORMAT) : null);
	   infoFactura.setImporteTotal(set.getBigDecimal("importeTotal"));
	   infoFactura.setMoneda(set.getString("moneda"));
	   infoFactura.setPropina(set.getBigDecimal("propina"));
	   infoFactura.setTotalDescuento(BigDecimal.ZERO);
	   String identificacion = infoFactura.getIdentificacionComprador();

	   if (identificacion != null && !identificacion.isEmpty()) {
			tipoIdentificacionComprador = obtenerIdentificacion(conn,identificacion);
			if (tipoIdentificacionComprador != null&& !tipoIdentificacionComprador.isEmpty()) {

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

			if (infoFactura != null&& infoFactura.getTipoIdentificacionComprador() != null&& infoFactura.getTipoIdentificacionComprador().equalsIgnoreCase("07")) {
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
		infoFactura.setTotalSinImpuestos(set.getBigDecimal("totalSinImpuestos"));
		
		
		List<Pago> pagos = listarPagosJDE(conn, claveAcceso);
		if(pagos != null && !pagos.isEmpty()){
			infoFactura.setPagos(new Pagos());
			infoFactura.getPagos().getPago().addAll(pagos);
		}
		
		infoFactura.setContribuyenteEspecial(set.getString("contribuyenteEspecial"));
		infoFactura.setGuiaRemision(set.getString("guiaRemision"));
		infoFactura.setObligadoContabilidad(set.getString("obligadoContabilidad"));
		
		factura.setInfoFactura(infoFactura);
		

	

		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
		infoTributaria.setCodDoc(set.getString("codDoc"));
		infoTributaria.setEstab(set.getString("estab"));
		infoTributaria.setPtoEmi(set.getString("ptoEmi"));
		infoTributaria.setRuc(set.getString("ruc"));

		String nombreSucursal = set.getString("RAZONSOCIAL");
		nombreSucursal = StringUtil.validateInfoXML(nombreSucursal);
		infoTributaria.setNombreComercial(nombreSucursal);

		infoTributaria.setDirMatriz(set.getString("DIRESTABLECIMIENTO"));
		infoTributaria.setClaveAcceso(claveAcceso);
		
		infoTributaria.setSecuencial(set.getLong("secuencial") + "");

		factura.setInfoTributaria(infoTributaria);

		facturaProcesarRequest = new FacturaProcesarRequest();
		facturaProcesarRequest.setFactura(factura);
		

		facturaProcesarRequest.setIdentificadorUsuario((set
				.getString("identificacionComprador") != null && !set
				.getString("identificacionComprador").isEmpty()) ? set
				.getString("identificacionComprador") : null);
	
	
		return facturaProcesarRequest;
	}

	
	
	
    private InfoTributaria getInfoTributaria(ResultSet set) throws SQLException, GizloException {
		
    	InfoTributaria infoTributaria =  new InfoTributaria();
		infoTributaria.setAmbiente(set.getString("ambiente").trim());
    	infoTributaria.setClaveAcceso(set.getString("clave_acceso").trim());
    	infoTributaria.setCodDoc(set.getString("codDoc").trim());
    	infoTributaria.setDirMatriz(set.getString("dirMatriz").trim());
    	infoTributaria.setEstab(set.getString("estab").trim());
    	infoTributaria.setNombreComercial(set.getString("nombreComercial").trim());
    	infoTributaria.setPtoEmi(set.getString("ptoEmi").trim());
    	infoTributaria.setRazonSocial(set.getString("razonSocial").trim());
    	infoTributaria.setRuc(set.getString("ruc").trim());
    	if (set.getString("secuencial").trim().length() < 9) {
			StringBuilder secuencial = new StringBuilder();
			int secuencialTam = 9 - set.getString("secuencial").trim().length();
			for (int i = 0; i < secuencialTam; i++) {
				secuencial.append("0");
			}
			secuencial.append(set.getString("secuencial").trim());
			infoTributaria.setSecuencial(secuencial.toString());
		}
    	infoTributaria.setTipoEmision(set.getString("tipoEmision").trim());
		return infoTributaria;
	}
    
    
    private InfoCompRetencion getInfoCompRetencion(ResultSet set) throws SQLException, GizloException {
		
    	InfoCompRetencion infoCompRetencion =  new InfoCompRetencion();
		infoCompRetencion.setContribuyenteEspecial(set.getString("contribuyenteEspecial")!=null?set.getString("contribuyenteEspecial"):"  ");
		
		infoCompRetencion.setDirEstablecimiento(set.getString("dirEstablecimiento")!=null?set.getString("dirEstablecimiento").trim():null);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String text = df.format(set.getDate("fechaEmision"));
		
		infoCompRetencion.setFechaEmision(text);
		infoCompRetencion.setIdentificacionSujetoRetenido(set.getString("identificacionSujetoRetenido")!=null?set.getString("identificacionSujetoRetenido").trim():null);
		infoCompRetencion.setObligadoContabilidad(set.getString("obligadoContabilidad")!=null?set.getString("obligadoContabilidad").trim():"");
		infoCompRetencion.setTipoIdentificacionSujetoRetenido(set.getString("tipoIdentifiSujetoRetenido")!=null?set.getString("tipoIdentifiSujetoRetenido").trim():null);
		infoCompRetencion.setRazonSocialSujetoRetenido(set.getString("razonSocialSujetoRetenido")!=null?set.getString("razonSocialSujetoRetenido").trim():null );
		infoCompRetencion.setPeriodoFiscal(set.getString("periodoFiscal")!=null?set.getString("periodoFiscal").trim():null);
		return infoCompRetencion;
	}
	

    private com.gizlocorp.gnvoice.xml.retencion.Impuesto getImpuesto(ResultSet set) throws SQLException, GizloException {
		
    	com.gizlocorp.gnvoice.xml.retencion.Impuesto impuesto =  new com.gizlocorp.gnvoice.xml.retencion.Impuesto();
    	impuesto.setBaseImponible(set.getBigDecimal("baseImponible"));
    	impuesto.setCodDocSustento(set.getString("codDocSustento").trim());
    	impuesto.setCodigo(set.getString("codigo").trim());
    	impuesto.setCodigoRetencion(set.getString("codigoRetencion").trim());
    	impuesto.setFechaEmisionDocSustento(set.getString("fechaEmisionDocSustento").trim());
    	impuesto.setNumDocSustento(set.getString("numDocSustento").trim());
    	impuesto.setPorcentajeRetener(set.getBigDecimal("porcentajeRetener"));
    	impuesto.setValorRetenido(set.getBigDecimal("valorRetenido"));
		return impuesto;
	}
    
    
    private  com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoAdicional.CampoAdicional getInfoAdicional(ResultSet set) throws SQLException, GizloException {
    	com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoAdicional.CampoAdicional campoAdicional =  new com.gizlocorp.gnvoice.xml.retencion.ComprobanteRetencion.InfoAdicional.CampoAdicional();
    	campoAdicional.setNombre(set.getString("dato"));
    	campoAdicional.setValue(set.getString("valor"));
		return campoAdicional;
	}
   

	
	/**
	  @FinalDeCambio
	 */
	
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
       
       infoFactura.setRazonSocialComprador(infoFactura.getRazonSocialComprador().replaceAll("[^\\x00-\\x7F]", ""));
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
		
		
		//pagos ojo
		List<Pago> pagos = listarPagos(conn, documentoVenta, farmacia);
		if(pagos != null && !pagos.isEmpty()){
			infoFactura.setPagos(new Pagos());
			infoFactura.getPagos().getPago().addAll(pagos);
		}

		
		
		
		
		factura.setInfoFactura(infoFactura);

		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setTipoEmision(TipoEmisionEnum.NORMAL.getCodigo());
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
	
	/***
	 * 
	   @author spramirezv
	   @descripcionDelCambio meto para recuperar lista de pagos de JDE
	 */
	
	private List<Pago> listarPagosJDE(Connection conn, String claveAcceso) throws GizloException, SQLException {		
		PreparedStatement ps = null;
		Pago pagoXml = null;
		List<Pago> listPagos = new ArrayList<Pago>();
		StringBuilder sqlString = new StringBuilder();
		

		sqlString.append("select distinct XMPTAB as forma_pago, ");
		sqlString.append("XMAGPT / 100 as total, ");
		sqlString.append("XMBASP as plazo, ");
		sqlString.append("XMYRMNDY as unidad_tiempo ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxml ");
		sqlString.append("where XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		sqlString.append("union all ");
		sqlString.append("select distinct XMPTAB as forma_pago, ");
		sqlString.append("XMAGPT / 100 as total, ");
		sqlString.append("XMBASP as plazo, ");
		sqlString.append("XMYRMNDY as unidad_tiempo ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxmh ");
		sqlString.append("where XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		
		ps = conn.prepareStatement(sqlString.toString());
		/*ps.setString(1, claveAcceso);
		ps.setString(2, claveAcceso);*/
		
	
		ResultSet set = ps.executeQuery();
		while (set.next()) {
			pagoXml = new Pago();
			pagoXml.setFormaPago(set.getString("forma_pago").trim());
			pagoXml.setTotal(set.getBigDecimal("total"));
			pagoXml.setPlazo(set.getBigDecimal("plazo"));
			pagoXml.setUnidadTiempo(set.getString("unidad_tiempo"));
			listPagos.add(pagoXml);	
		}
		return listPagos;
				
	}
	
	/***
	 * 
	 * @finDelCambio
	 */
	

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
				sqlString = new StringBuilder();
				sqlString
						.append("select identificacion_titular, nombres from ab_beneficiarios_abf where codigo = ? ");

				ps2 = conn.prepareStatement(sqlString.toString());
				ps2.setLong(1, resultado);

				ResultSet set2 = ps2.executeQuery();

				while (set2.next()) {
					personaAbf = set2.getString("identificacion_titular") + "&"
							+ set2.getString("nombres");
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

	private String obtenerCorreo(Connection conn, Long documentoVenta,
			Long farmacia) throws GizloException, SQLException {

		// PreparedStatement ps = null;
		CallableStatement cstmt = null;
		String correo = null;

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

		if (cstmt != null)
			cstmt.close();

		return correo;

	}

	public InfoAdicional obtenerInfoAdicional(Connection conn, Long farmacia,
			Long factura) throws GizloException {
		try {

			InfoAdicional resultado = new InfoAdicional();
			String direccionStr = StringUtil.validateInfoXML(obtenerDireccion(
					conn, farmacia, factura));
			String descuentoStr = StringUtil.validateInfoXML(obtenerDescuento(
					conn, farmacia, factura));

			if (direccionStr != null && !direccionStr.isEmpty()) {
				CampoAdicional direccion = new CampoAdicional();
				direccion.setNombre("DIRECCION");
				direccion.setValue(direccionStr.replace(".", "").replace("Â¿", "").replace(",", "").trim());
				resultado.getCampoAdicional().add(direccion);

			}

			if (descuentoStr != null && !descuentoStr.isEmpty()) {
				CampoAdicional descuento = new CampoAdicional();
				descuento.setNombre("DESCUENTO");
				descuento.setValue(descuentoStr.replace("-", "").trim());
				resultado.getCampoAdicional().add(descuento);

			}

//			Map<String, String> deducibles = obtenerValorDeducible(conn,
//					farmacia, factura);
//			String valor = null;
//			if (deducibles != null && !deducibles.isEmpty()) {
//				CampoAdicional deducible = null;
//				for (String nombre : deducibles.keySet()) {
//					if (nombre != null && !nombre.isEmpty()) {
//						valor = StringUtil.validateInfoXML(deducibles
//								.get(nombre));
//						if (valor != null && !valor.isEmpty()) {
//							deducible = new CampoAdicional();
//							deducible.setNombre("DEDUCIBLE " + nombre);
//							deducible.setValue(valor.trim());
//							resultado.getCampoAdicional().add(deducible);
//						}
//					}
//				}
//			}

			return resultado;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}
	
	/**
	 *@author spramirezv
	 *descripcionDelCambio obtener informacion adicional JDE

	 */
	
	public InfoAdicional obtenerInfoAdicionalJDE_(Connection conn, String claveAcceso) throws GizloException {
		try {

			InfoAdicional resultado = new InfoAdicional();
			String direccionStr = StringUtil.validateInfoXML(obtenerDireccion(conn, 1l, 1l));
			String descuentoStr = StringUtil.validateInfoXML(obtenerDescuento(conn, 1l, 1l));

			if (direccionStr != null && !direccionStr.isEmpty()) {
				CampoAdicional direccion = new CampoAdicional();
				direccion.setNombre("DIRECCION");
				direccion.setValue(direccionStr.replace(".", "").replace("Â¿", "").replace(",", "").trim());
				resultado.getCampoAdicional().add(direccion);

			}

			if (descuentoStr != null && !descuentoStr.isEmpty()) {
				CampoAdicional descuento = new CampoAdicional();
				descuento.setNombre("DESCUENTO");
				descuento.setValue(descuentoStr.replace("-", "").trim());
				resultado.getCampoAdicional().add(descuento);

			}
			return resultado;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}
	
	/**
	 * 
		@finDelCambio
	 */
	
	/**
	 * @autor spramirezv
	 * @descripcionDelCambio obtener informacionadicional de JDE
	 * 
	 * */
	private InfoAdicional obtenerInfoAdicionalJDE (Connection conn, String claveAcceso) throws GizloException {
		
		try {
		PreparedStatement ps = null;
		InfoAdicional resultado = new InfoAdicional();
		StringBuilder sqlString = new StringBuilder();
		
		sqlString.append("select distinct to_nchar('Dirección') as dato, XMBDS4 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxml ");
		sqlString.append("where  XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		sqlString.append("union all ");
		sqlString.append("select distinct to_nchar('Dirección') dato, XMBDS4 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxmh ");
		sqlString.append("where  XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		sqlString.append("union all ");
		sqlString.append("select distinct XMC75XMLE6 as dato, XMC75XMLE7 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxml ");
		sqlString.append("where XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		sqlString.append("union all ");
		sqlString.append("select distinct XMC75XMLE6 as dato, XMC75XMLE7 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxmh ");
		sqlString.append("where  XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");  
		sqlString.append("union all    ");
		sqlString.append("select distinct XMC75XMLE8 as dato, XMC75XML15 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxml ");
		sqlString.append("where XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		sqlString.append("union all ");
		sqlString.append("select distinct XMC75XMLE8 as dato, XMC75XML15 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxmh ");
		sqlString.append("where XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");   
		sqlString.append("union all    ");
		sqlString.append("select distinct XMC75XML16 as dato, XMC75XML17 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxml ");
		sqlString.append("where  XMDCTV = '01'  ");
		sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
		sqlString.append("union all ");
		sqlString.append("select distinct XMC75XML16 as dato, XMC75XML17 as valor ");
		sqlString.append("from "+com.gizlocorp.adm.utilitario.Constantes.ESQUEMA_JDE+".f76ecxmh ");
		sqlString.append("where  XMDCTV = '01'  ");
	    sqlString.append("and XMOTGENKEY = '"+claveAcceso+"' ");
	    
	    
	    
	    ps = conn.prepareStatement(sqlString.toString());

		/*ps.setString(1, claveAcceso);
		ps.setString(2, claveAcceso);
		ps.setString(3, claveAcceso);
		ps.setString(4, claveAcceso);
		ps.setString(5, claveAcceso);
		ps.setString(6, claveAcceso);
		ps.setString(7, claveAcceso);
		ps.setString(8, claveAcceso);*/
	

		ResultSet set = ps.executeQuery();

		while (set.next()) {
			
			if(!"".equals(set.getString("dato"))){
				CampoAdicional campoAdicional = new CampoAdicional();
				campoAdicional.setNombre(set.getString("dato"));
				campoAdicional.setValue(set.getString("valor"));
				resultado.getCampoAdicional().add(campoAdicional);
			}
		}

		if (ps != null)
			ps.close();

		if (set != null)
			set.close();

		return resultado;
		
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	
	/***
	 * 
	 * @finDelCambio
	 */
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
		
		
		resultado = resultado != null ? resultado.replace("á", "a").replace("é", "e").replace("í", "i")
				.replace("ó", "o").replace("ú", "u").replace("Á", "A").replace("É", "E").replace("Í", "I")
				.replace("Ó", "O").replace("Ú", "U").replace("ñ", "n").replace("Ñ", "N") : resultado;

		return resultado;
	}

	private String obtenerDescuento(Connection conn, Long farmacia, Long factura)
			throws SQLException {
		PreparedStatement ps = null;
		String resultado = null;
		StringBuilder sqlString = new StringBuilder();

		sqlString
				.append("select (pvp_total_factura-venta_total_factura) as valor from fa_facturas where documento_venta = ? and farmacia = ?");

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
	
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long listaSidOficina(Connection conn, String databaseSid)
			throws GizloException {
		// TODO Auto-generated method stub
		Long respuesta = null;
		PreparedStatement ps = null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("select codigo from ad_farmacias where fma_autorizacion_farmaceutica = 'FS' and campo3 = 'S' and database_sid = ?");
		sqlString.append("AND EMPRESA in (1)  ORDER BY EMPRESA,CODIGO asc");
		try {
			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, databaseSid);
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				respuesta = set.getLong("codigo");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}


	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long listaSidSanaSana(Connection conn, String databaseSid) throws GizloException {
		// TODO Auto-generated method stub
		Long respuesta = null;
		PreparedStatement ps = null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("select codigo from ad_farmacias   where fma_autorizacion_farmaceutica = 'FS' and campo3 = 'S' and database_sid = ?");
		sqlString.append("AND EMPRESA in (8)  ORDER BY EMPRESA,CODIGO asc");
		try {
			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, databaseSid);
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				respuesta = set.getLong("codigo");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}


	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long listaOkiDoki(Connection conn, String databaseSid) throws GizloException {
		// TODO Auto-generated method stub
		Long respuesta = null;
		PreparedStatement ps = null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("select codigo,database_sid,nombre from ad_farmacias   where fma_autorizacion_farmaceutica = 'FS' and campo3 = 'S' and database_sid = ?");
		sqlString.append("AND EMPRESA in (11)  ORDER BY EMPRESA,CODIGO asc");
		try {
			ps = conn.prepareStatement(sqlString.toString());
			ps.setString(1, databaseSid);
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				respuesta = set.getLong("codigo");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String listaSidGeneral(Connection conn, Long codigo)
			throws GizloException {
		// TODO Auto-generated method stub
		List<String> respuesta = new ArrayList<String>();
		PreparedStatement ps = null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("select codigo,database_sid,nombre from ad_farmacias   where fma_autorizacion_farmaceutica = 'FS'  and codigo = ? ");
		sqlString.append("ORDER BY EMPRESA,CODIGO asc");
		try {
			ps = conn.prepareStatement(sqlString.toString());
			ps.setLong(1, codigo);
			ResultSet set = ps.executeQuery();
			while (set.next()) {
				respuesta.add(set.getInt("codigo")+"&"+set.getString("database_sid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return respuesta.get(0);
	}

	/***********nota de credito*************/
	
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public NotaCreditoProcesarRequest listarNotaCredito(Connection conn,
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
			
			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional email=null;
			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional docInterno=null;
			com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional dirEstablecimiento=null;

			while (set.next()) {
				notaCreditoFybeca = null;
				try {
					notaCreditoFybeca = obtenerNotaCredito(conn, set, farmacia,
							tipo);

				} catch (Exception ex) {
					log.error("Error en proceso DAO", ex);
				}
				if (notaCreditoFybeca != null) {

					String correo = obtenerCorreoNotaCrecito(conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					
					log.info("***obteniendo direccion 000***"+Long.valueOf(notaCreditoFybeca.getCodigoExterno())+"***"+farmacia);
					
					String direccion=obtenerDireccionNotaCrecito(conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					
					log.info("***obteniendo direccion 111***"+direccion);

					notaCreditoFybeca
							.setCorreoElectronicoNotificacion(StringUtil
									.validateEmail(correo));

					Map<String, Object> detalesTotalesImpuesto = listarDetallesNotaCrecito(
							conn,
							Long.valueOf(notaCreditoFybeca.getCodigoExterno()),
							farmacia);
					List<com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle> detalles = (List<com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle>) detalesTotalesImpuesto
							.get(Constantes.DETALLES);
					log.debug("**servicio listar detalles: "
							+ (detalles != null ? detalles.size() : 0));

					List<com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto> totalesImpuestos = (List<com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto>) detalesTotalesImpuesto
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

					// DETALLE IMPUESTO
					if (detalles != null && !detalles.isEmpty()) {
						notaCreditoFybeca.getNotaCredito().setDetalles(
								new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles());
						notaCreditoFybeca.getNotaCredito().getDetalles()
								.getDetalle().addAll(detalles);
					}
					// notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().setImporteTotal(notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getImporteTotal().subtract(notaCreditoFybeca.getNotaCredito().getInfoNotaCredito().getTotalDescuento()));

					com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional infoAdicional = null;
					
					if (notaCreditoFybeca.getCodigoExterno() != null
							&& !notaCreditoFybeca.getCodigoExterno().isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional();
						}
						
						docInterno = new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional();
						docInterno.setNombre("DOCUMENTO INTERNO");
						docInterno.setValue(notaCreditoFybeca.getCodigoExterno());
						
						infoAdicional.getCampoAdicional().add(docInterno);
						
					}
					
					if (notaCreditoFybeca.getCorreoElectronicoNotificacion() != null
							&& !notaCreditoFybeca.getCorreoElectronicoNotificacion().isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional();
						}
						
						email = new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional();
						email.setNombre("EMAIL");
						email.setValue(notaCreditoFybeca.getCorreoElectronicoNotificacion());
						
						infoAdicional.getCampoAdicional().add(email);
						
					}
					
					if (direccion != null
							&& !direccion.isEmpty()) {
						if (infoAdicional == null) {
							infoAdicional = new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional();
						}
						
						dirEstablecimiento = new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.InfoAdicional.CampoAdicional();
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

		return notaCreditoFybeca;

	}

	private Map<String, Object> listarDetallesNotaCrecito(Connection conn,
			Long idNotaCredito, Long idFarmacia) throws GizloException,
			SQLException {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		PreparedStatement ps = null;
		List<com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle> detalles = new ArrayList<com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle>();
		com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle detalleXML = null;

		// <<AG
		List<com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto> totalesImpuestos = new ArrayList<com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto>();
		List<com.gizlocorp.gnvoice.xml.notacredito.Impuesto> detalleImpuestos = new ArrayList<com.gizlocorp.gnvoice.xml.notacredito.Impuesto>();
		com.gizlocorp.gnvoice.xml.notacredito.Impuesto impuestoXML = null;
		com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto totalImpuestoXML = null;
		BigDecimal total12 = new BigDecimal(0f), total0 = new BigDecimal(0f), baseImp0 = new BigDecimal(
				0f), baseImp12 = new BigDecimal(0f), totalSinImpuesto = new BigDecimal(
				0f), valor = new BigDecimal(0f), baseImp = new BigDecimal(0f);
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
				detalleXML = new com.gizlocorp.gnvoice.xml.notacredito.NotaCredito.Detalles.Detalle();
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
				detalleImpuestos = new ArrayList<com.gizlocorp.gnvoice.xml.notacredito.Impuesto>();

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

					impuestoXML = new com.gizlocorp.gnvoice.xml.notacredito.Impuesto();
					impuestoXML.setCodigo("2");
					impuestoXML.setCodigoPorcentaje("0");
					impuestoXML.setTarifa(new BigDecimal(0));
					impuestoXML.setBaseImponible(baseImp);
					impuestoXML.setValor(valor);
					detalleImpuestos.add(impuestoXML);

				} else {
					// 12%
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

					impuestoXML = new com.gizlocorp.gnvoice.xml.notacredito.Impuesto();
					impuestoXML.setCodigo("2");
					impuestoXML.setCodigoPorcentaje("2");
					impuestoXML.setTarifa(new BigDecimal(12));
					impuestoXML.setBaseImponible(baseImp);
					impuestoXML.setValor(valor);
					detalleImpuestos.add(impuestoXML);
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
		totalSinImpuesto = totalSinImpuesto.setScale(2,
				BigDecimal.ROUND_HALF_UP);
		// 0%
		baseImp0 = baseImp0.setScale(2, BigDecimal.ROUND_HALF_UP);
		total0 = total0.setScale(2, BigDecimal.ROUND_HALF_UP);

		totalImpuestoXML = new com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto();
		totalImpuestoXML.setCodigo("2");
		totalImpuestoXML.setCodigoPorcentaje("0");
		totalImpuestoXML.setBaseImponible(baseImp0);
		totalImpuestoXML.setValor(total0);
		totalesImpuestos.add(totalImpuestoXML);

		if (!total12.equals(new BigDecimal(0))) {
			// 12%
			baseImp12 = baseImp12.setScale(2, BigDecimal.ROUND_HALF_UP);
			total12 = total12.setScale(2, BigDecimal.ROUND_HALF_UP);

			totalImpuestoXML = new com.gizlocorp.gnvoice.xml.notacredito.TotalConImpuestos.TotalImpuesto();
			totalImpuestoXML.setCodigo("2");
			totalImpuestoXML.setCodigoPorcentaje("2");
			totalImpuestoXML.setBaseImponible(baseImp12);
			totalImpuestoXML.setValor(total12);
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
		Long tipoDocModificado = obtenerTipoNotaCredito(conn,
				"FACTURA");

		StringBuilder secuencialFactura = new StringBuilder();
		Long secuencialFacturaLong = obtenerSecuencialFacturaNotaCredito(conn,
				set.getLong("factura_codigo"), farmacia, tipoDocModificado);

		if (secuencialFacturaLong == null) {
			secuencialFacturaLong = obtenerSecuencialFacturaAutoimpresor(conn,
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

		Date fecha = obtenerFechaEmision(conn,
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

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private Long obtenerTipoNotaCredito(Connection conn, String comprobante)
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
	
	private Long obtenerSecuencialFacturaNotaCredito(Connection conn, Long documentoVenta,
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
	private String obtenerIdentificacionNotaCrecito(Connection conn, String identificacion)
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

	private String obtenerCorreoNotaCrecito(Connection conn, Long documentoVenta,
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
	
	private String obtenerDireccionNotaCrecito(Connection conn, Long factura, Long farmacia)
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
	
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	public BigDecimal obtenerTotalDescuento(CredencialDS credencialDS,
//			Long documentoVenta, Long farmacia) throws GizloException {
//
//		Connection conn = null;
//		PreparedStatement ps = null;
//		BigDecimal resultado = null;
//		try {
//			conn = Conexion.obtenerConexionFybeca(credencialDS);
//			StringBuilder sqlString = new StringBuilder();
//
//			sqlString
//					.append("select (pvp_total_factura-venta_total_factura) as descuento from fa_facturas where documento_venta =? and farmacia =?");
//
//			ps = conn.prepareStatement(sqlString.toString());
//			ps.setLong(1, documentoVenta);
//			ps.setLong(2, farmacia);
//
//			ResultSet set = ps.executeQuery();
//
//			while (set.next()) {
//				resultado = set.getBigDecimal("descuento");
//			}
//
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		} finally {
//			try {
//				if (ps != null)
//					ps.close();
//			} catch (Exception e) {
//				log.error(e.getMessage());
//			}
//			try {
//				if (conn != null)
//					conn.close();
//			} catch (Exception e) {
//				log.error(e.getMessage());
//			}
//		}
//
//		return resultado;
//	}
	
}
