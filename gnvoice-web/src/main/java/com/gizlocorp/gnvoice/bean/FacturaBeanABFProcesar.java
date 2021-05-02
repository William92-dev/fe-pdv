package com.gizlocorp.gnvoice.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.json.model.CredencialDS;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;
import com.gizlocorp.gnvoice.utilitario.Conexion;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("facturaBeanABFProcesar")
public class FacturaBeanABFProcesar extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(FacturaBeanABFProcesar.class.getName());

	private String numeroComprobante;

	private String claveContingencia;

	private String tipoEmision;

	private String tipoAmbiente;

	private String razonSocialComprador;
	
	private String correoProveedor;
	

	private TipoEjecucion tipoEjecucion;

	private String codigoExterno;

	private Date fechaDesde;

	private Date fechaHasta;
	
	private List<Factura> facturas;
	
	private List<Factura> listFacturas;

	private Factura factura;
	
	private FacturaRecepcion facturaRecepcion;
	
	
	private List<FacturasPendientesBean>  lisFacturasPendientesBean;
	
	private List<AdFarmaciasBean> listAdFarmaciasBean;
	
	

	private Object objFactura;

	private String estado = null;

	@Inject
	private SessionBean sessionBean;

	@Inject
	private ApplicationBean applicationBean;

	private String rucEmisor;
	
	private String codigoFarmacia;
	
	private String emisor;

	private String rucComprador;

	private String identificadorUsuario;

	private String agencia;

	private String ordenCompra;
	
	private byte[] data;
	
	private String extension;
	

	private Map<String, Object> listaParametros;

	private boolean buscarSegumiento = false;

	
	private Boolean selectUne = false;



	@PostConstruct
	public void postContruct() {
		log.info("postContruction");
		try {
			fechaDesde = Calendar.getInstance().getTime();
//			rucEmisor = sessionBean.getRucOrganizacion() != null ? sessionBean
//					.getRucOrganizacion() : null;
			facturas = new ArrayList<Factura>();

			rucComprador = null;
			identificadorUsuario = null;
			if(sessionBean == null){
				return;
			}
			log.info("postContruction 222");
			List<UsuarioRol> usuarioRoles = sessionBean.getUsuario()
					.getUsuariosRoles();
			if (usuarioRoles != null && !usuarioRoles.isEmpty()) {
				log.info("postContruction 333");
				for (UsuarioRol usuRol : usuarioRoles) {
					if (usuRol.getRol().getCodigo().equals("CONSU") ) {
						rucComprador = sessionBean.getUsuario().getPersona()
								.getIdentificacion();
						
						log.info("****postContruction rucComprador"+rucComprador);
						identificadorUsuario = sessionBean.getUsuario()
								.getPersona().getIdentificacion();
						fechaDesde = null;
					}
					if(usuRol.getRol().getCodigo().equals("PROVE")){
						rucEmisor = sessionBean.getUsuario().getPersona()
								.getIdentificacion();
					}
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	
	
	public void listarFarmcias() {
		listAdFarmaciasBean  = new ArrayList<AdFarmaciasBean>();
		PreparedStatement ps = null;
		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId("prod");
		credencialDS.setUsuario("weblink");
		credencialDS.setClave("weblink_2013");
		Connection conn = null;
		ResultSet set = null;

		
			try {

				conn = Conexion.obtenerConexionFybeca(credencialDS);
				StringBuilder sqlString = new StringBuilder();
				sqlString.append(" SELECT f.codigo, f.nombre");
				sqlString.append(" FROM ad_farmacias f,");
			 	sqlString.append("  ad_ciudades  c");
				sqlString.append("  WHERE campo3 = 'S'");
				sqlString.append("  AND fma_autorizacion_farmaceutica = 'FS'");
				sqlString.append("  AND f.ciudad = c.codigo     ");
				
				if("prod".equals( rucEmisor)){
					sqlString.append(" and empresa = 1       ");
					sqlString.append(" and database_sid LIKE '%F%'");
				}else if("sana".equals( rucEmisor)){
					sqlString.append("  and empresa = 8      ");
					sqlString.append(" and database_sid LIKE '%S%'");
				}else if("okimaster".equals( rucEmisor)){
					sqlString.append("  and empresa = 11     ");
					sqlString.append(" and database_sid LIKE '%O%'");
				}
				sqlString.append(" order by 2");
				ps = conn.prepareStatement(sqlString.toString());
				
				List<Map<String, Object>> datos = query(conn, sqlString.toString(), Collections.EMPTY_LIST);
				for (Map<String, Object> dato : datos) {
					AdFarmaciasBean  adFarmaciasBean = new AdFarmaciasBean();
					adFarmaciasBean.setCodigo(Long.parseLong(dato.get("CODIGO").toString()));
					adFarmaciasBean.setNombre(dato.get("NOMBRE").toString());
					listAdFarmaciasBean.add(adFarmaciasBean);
				}

			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				try {
					if (ps != null)
						ps.close();

					if (set != null)
						set.close();

					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}

	
	
	public void procesarDocumentosPendientes() {
        int con =0;
		for (int i=0 ;i<lisFacturasPendientesBean.size();i++) {
			try {
				StringBuffer urlString = new StringBuffer("http://uiofacelegen03.gfybeca.int:8330/facturacionServiciosGPF/factura/procesar/");
				urlString.append(lisFacturasPendientesBean.get(i).getFarmacia());
				urlString.append("&"+lisFacturasPendientesBean.get(i).getDocumento_venta());
				urlString.append("&"+lisFacturasPendientesBean.get(i).getSid());
				URL url = new URL(urlString.toString());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}
				conn.disconnect();
				con++;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		
		infoMessage(con+" documentos enviados a procesar ");

	}
	
	

	public void listarDeDocumentosPendientes() {
		lisFacturasPendientesBean = new ArrayList<FacturasPendientesBean>();
		PreparedStatement ps = null;
		CredencialDS credencialDS = new CredencialDS();
		credencialDS.setDatabaseId(rucEmisor);
		credencialDS.setUsuario("weblink");
		credencialDS.setClave("weblink_2013");
		Connection conn = null;
		ResultSet set = null;

		if (rucEmisor == null && rucEmisor.isEmpty() && fechaHasta == null && fechaDesde == null) {
			errorMessage("Ingresar un parametro de busqueda adicional. Cadena, Fecha Desde Emision y Fecha Hasta Emision");

		} else {

			try {

				conn = Conexion.obtenerConexionFybeca(credencialDS);
				StringBuilder sqlString = new StringBuilder();
				sqlString.append(" select b.*,                                                  ");
				sqlString.append("       a.error,                                               ");
				sqlString.append("       a.observacion_elec,                                    ");
				sqlString.append("       a.fecha fechaejecutado,                                ");
				sqlString.append("       decode(b.tipo_movimiento, '01', 1, 4) tipo_comprobante,");
				sqlString.append("       substr(c.database_sid, 1, 1) || b.farmacia sid,        ");
				sqlString.append("       substr(c.database_sid, 1, 1) BASE,                     ");
				sqlString.append("       b.fecha fecha_f, c.nombre as nombreFarmacias,          ");
				sqlString.append("       DECODE (A.estado,'A','AUTORIZADO','P','PENDIENTE','R','ERROR') AS desEstado,");
				sqlString.append("       a.CLAVE_ACCESO          ");
				sqlString.append("  from fa_datos_sri_electronica a,           		            ");
				sqlString.append("       fa_facturas              b, 							");
				sqlString.append("       ad_farmacias             c,                            ");
				sqlString.append("      fa_detalles_formas_pago  z                              ");
				sqlString.append(" where nvl(a.estado, 'P') <> 'A'                              ");
				sqlString.append("   and b.farmacia = c.codigo                                  ");
				sqlString.append("   and a.documento(+) = b.documento_venta                     ");
				sqlString.append("   and a.farmacia(+) = b.farmacia                             ");
				sqlString.append("  and b.documento_venta = z.documento_venta  					");
				sqlString.append("	and b.farmacia = z.farmacia     							");
				sqlString.append("	and z.forma_pago = 'CF'  		    						");
				sqlString.append("  and b.tipo_movimiento in ('01', '02')                      ");
				sqlString.append("  and b.clasificacion_movimiento = '01'                      ");
				sqlString.append("  and a.tipo_comprobante = 1                                 ");
				sqlString.append("  and exists                                                 ");
				sqlString.append(" (select 1                                                    ");
				sqlString.append("          from fa_secuencias_fact_elec f1                     ");
				sqlString.append("         where f1.documento_venta = b.documento_venta         ");
				sqlString.append("           and f1.farmacia = b. farmacia)                     ");

				
				if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
					sqlString.append(" and  b.NUMERO_SRI = :numeroComprobante");
				}

				if (codigoExterno != null && !codigoExterno.isEmpty()) {
					sqlString.append(" and  b.DOCUMENTO_VENTA = :codigoExterno");
				}

				if (fechaDesde != null) {
					sqlString.append("   and trunc(b.fecha) >= :fechaDesde  ");
				}

				if (fechaDesde != null) {
					sqlString.append("   and trunc(b.fecha) <= :fechaHasta  ");
				}

				if (rucComprador != null && !rucComprador.isEmpty()) {
					//sqlString.append("    and b.farmacia =:codigoFarmacia  ");
				}

				if (claveContingencia != null && !claveContingencia.isEmpty()) {
					sqlString.append(" and  a.CLAVE_ACCESO = :claveContingencia");
				}

				if (codigoFarmacia != null && !codigoFarmacia.isEmpty()) {
					sqlString.append("    and b.farmacia =:codigoFarmacia  ");
				}

				sqlString.append(" order by  b.NUMERO_SRI desc, b.fecha desc");


				ps = conn.prepareStatement(sqlString.toString());
				List<Object> parameters = new ArrayList<Object>();
				
				
				if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
					parameters.add(numeroComprobante);
				}

				if (codigoExterno != null && !codigoExterno.isEmpty()) {
					parameters.add(codigoExterno);
				}

				if (fechaDesde != null) {
					parameters.add(new java.sql.Date(fechaDesde.getTime()));
				}

				if (fechaHasta != null) {
					parameters.add(new java.sql.Date(fechaHasta.getTime()));
				}

				if (rucComprador != null && !rucComprador.isEmpty()) {
					//sqlString.append("    and b.farmacia =:codigoFarmacia  ");
				}

				if (claveContingencia != null && !claveContingencia.isEmpty()) {
					parameters.add(claveContingencia);
					
				}

				if (codigoFarmacia != null && !codigoFarmacia.isEmpty()) {
					parameters.add(codigoFarmacia);
				}
			

				List<Map<String, Object>> datos = query(conn, sqlString.toString(), parameters);

				for (Map<String, Object> dato : datos) {
					FacturasPendientesBean facturasPendientesBean = new FacturasPendientesBean();
					facturasPendientesBean.setNombreFarmacias(dato.get("NOMBREFARMACIAS").toString());
					
					facturasPendientesBean.setFarmacia(dato.get("FARMACIA").toString());
					facturasPendientesBean.setSid(dato.get("SID").toString());
					
					facturasPendientesBean.setDocumento_venta(dato.get("DOCUMENTO_VENTA").toString());
					facturasPendientesBean.setNumero_sri(dato.get("NUMERO_SRI").toString());
					facturasPendientesBean.setObservacion_elec(dato.get("OBSERVACION_ELEC") != null ? dato.get("OBSERVACION_ELEC").toString() : null);
					facturasPendientesBean.setDesEstado(dato.get("DESESTADO") != null ? dato.get("DESESTADO").toString() : null);
					facturasPendientesBean.setClaveAcceso(dato.get("CLAVE_ACCESO") != null ? dato.get("CLAVE_ACCESO").toString() : null);

					lisFacturasPendientesBean.add(facturasPendientesBean);
				}

			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				try {
					if (ps != null)
						ps.close();

					if (set != null)
						set.close();

					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}

	}
	
	
	public static List<Map<String, Object>> query(Connection connection, String sql, List<Object> parameters) throws SQLException {
        List<Map<String, Object>> results = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);

            int i = 0;
            for (Object parameter : parameters) {
                ps.setObject(++i, parameter);
            }
            rs = ps.executeQuery();
            results = map(rs);
        } finally {
            close(rs);
            close(ps);
        }
        return results;
    }
	
	
	public static List<Map<String, Object>> map(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        try {
            if (rs != null) {
                ResultSetMetaData meta = rs.getMetaData();
                int numColumns = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<String, Object>();
                    for (int i = 1; i <= numColumns; ++i) {
                        String name = meta.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(name, value);
                    }
                    results.add(row);
                }
            }
        } finally {
            close(rs);
        }
        return results;
    }
	
	
	 public static void close(Connection connection) {
	        try {
	            if (connection != null) {
	                connection.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }


	    public static void close(Statement st) {
	        try {
	            if (st != null) {
	                st.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void close(ResultSet rs) {
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	

	
	
	

	

	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			if (estado.getDescripcion().equals("PENDIENTE")
					|| estado.getDescripcion().equals("DEVUELTA")
					|| estado.getDescripcion().equals("ERROR")
					|| estado.getDescripcion().equals("RECIBIDA")
					|| estado.getDescripcion().equals("AUTORIZADO")
					|| estado.getDescripcion().equals("RECHAZADO")) {
				items.add(new SelectItem(estado, estado.getDescripcion()));
			}
		}
		return items;
	}

	

	
	
	public String getNumeroComprobante() {
		return numeroComprobante;
	}

	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public String getCodigoExterno() {
		return codigoExterno;
	}

	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	public String getIdentificadorUsuario() {
		return identificadorUsuario;
	}

	public void setIdentificadorUsuario(String identificadorUsuario) {
		this.identificadorUsuario = identificadorUsuario;
	}

	public String getRucComprador() {
		return rucComprador;
	}

	public void setRucComprador(String rucComprador) {
		this.rucComprador = rucComprador;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getEmisor() {
		return emisor;
	}

	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}

	public String getClaveContingencia() {
		return claveContingencia;
	}

	public void setClaveContingencia(String claveContingencia) {
		this.claveContingencia = claveContingencia;
	}

	public String getTipoEmision() {
		return tipoEmision;
	}

	public void setTipoEmision(String tipoEmision) {
		this.tipoEmision = tipoEmision;
	}

	public String getTipoAmbiente() {
		return tipoAmbiente;
	}

	public void setTipoAmbiente(String tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public String getCorreoProveedor() {
		return correoProveedor;
	}

	public void setCorreoProveedor(String correoProveedor) {
		this.correoProveedor = correoProveedor;
	}

	public TipoEjecucion getTipoEjecucion() {
		return tipoEjecucion;
	}

	public void setTipoEjecucion(TipoEjecucion tipoEjecucion) {
		this.tipoEjecucion = tipoEjecucion;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getRucEmisor() {
		return rucEmisor;
	}

	public void setRucEmisor(String rucEmisor) {
		this.rucEmisor = rucEmisor;
	}

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public String getOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(String ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	public Map<String, Object> getListaParametros() {
		return listaParametros;
	}

	public void setListaParametros(Map<String, Object> listaParametros) {
		this.listaParametros = listaParametros;
	}

	public Object getObjFactura() {
		return objFactura;
	}

	public void setObjFactura(Object objFactura) {
		this.objFactura = objFactura;
	}

	public boolean isBuscarSegumiento() {
		return buscarSegumiento;
	}

	public void setBuscarSegumiento(boolean buscarSegumiento) {
		this.buscarSegumiento = buscarSegumiento;
	}

	public void cargarDocumento(org.richfaces.event.FileUploadEvent event) {
		org.richfaces.model.UploadedFile item = event.getUploadedFile();
		data = item.getData();
		extension = item.getFileExtension();
		System.out.println(data+"**"+extension);
		 
	}
	
	@SuppressWarnings({ "rawtypes", "resource" })
	public static void readXLSXFile(InputStream is) throws IOException {
//		InputStream ExcelFileToRead = new FileInputStream();
	

		org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(is);

		org.apache.poi.xssf.usermodel.XSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.xssf.usermodel.XSSFRow row;
		org.apache.poi.xssf.usermodel.XSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (org.apache.poi.xssf.usermodel.XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (org.apache.poi.xssf.usermodel.XSSFCell) cells.next();

				if (cell.getCellType() == org.apache.poi.xssf.usermodel.XSSFCell.CELL_TYPE_STRING) {
					System.out.print(cell.getStringCellValue() + " ");
				} 
			}
			System.out.println();
		}

	}

	@SuppressWarnings({ "rawtypes", "resource" })
	public static void readXLSFile() throws IOException {
		InputStream ExcelFileToRead = new FileInputStream("C:\\Users\\Usuario\\Desktop\\Libro1.xls");
		org.apache.poi.hssf.usermodel.HSSFWorkbook wb = new org.apache.poi.hssf.usermodel.HSSFWorkbook(ExcelFileToRead);

		org.apache.poi.hssf.usermodel.HSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.hssf.usermodel.HSSFRow row;
		org.apache.poi.hssf.usermodel.HSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (org.apache.poi.hssf.usermodel.HSSFRow) rows.next();
			Iterator cells = row.cellIterator();

			while (cells.hasNext()) {
				cell = (org.apache.poi.hssf.usermodel.HSSFCell) cells.next();
				
				if (cell.getCellType() == org.apache.poi.hssf.usermodel.HSSFCell.CELL_TYPE_STRING) {
					System.out.print(cell.getStringCellValue() + " ");
				} 
			}
			System.out.println();
		}

	}
	
	
	public FacturaRecepcion getFacturaRecepcion() {
		return facturaRecepcion;
	}

	public void setFacturaRecepcion(FacturaRecepcion facturaRecepcion) {
		this.facturaRecepcion = facturaRecepcion;
	}
 
	 
	
	
	
	public String getRazonSocialComprador() {
		return razonSocialComprador;
	}

	public void setRazonSocialComprador(String razonSocialComprador) {
		this.razonSocialComprador = razonSocialComprador;
	}

	public static void main(String[] args) throws Exception {
				String directorioZip = "/home/jboss/app/gnvoice/recursos/comprobantesfactura/autorizado/27012016/";
		    	File carpetaComprimir = new File(directorioZip);
		 
			
				if (carpetaComprimir.exists()) {
					File[] ficheros = carpetaComprimir.listFiles();
					System.out.println("Número de ficheros encontrados: " + ficheros.length);
		 
					for (int i = 0; i < ficheros.length; i++) {
						System.out.println("Nombre del fichero: " + ficheros[i].getName());
						String extension="";
						for (int j = 0; j < ficheros[i].getName().length(); j++) {
							if (ficheros[i].getName().charAt(j)=='.') {
								extension=ficheros[i].getName().substring(j, (int)ficheros[i].getName().length());
							}
						}
						try {
							ZipOutputStream zous = new ZipOutputStream(new FileOutputStream(directorioZip + ficheros[i].getName().replace(extension, ".zip")));
							
							ZipEntry entrada = new ZipEntry(ficheros[i].getName());
							zous.putNextEntry(entrada);
							
								System.out.println("Comprimiendo.....");
			 					FileInputStream fis = new FileInputStream(directorioZip+entrada.getName());
								int leer;
								byte[] buffer = new byte[1024];
								while (0 < (leer = fis.read(buffer))) {
									zous.write(buffer, 0, leer);
								}
								fis.close();
								zous.closeEntry();
							zous.close();					
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}				
					}
					System.out.println("Directorio de salida: " + directorioZip);
				} else {
					System.out.println("No se encontró el directorio..");
				}
			}

	public Boolean getSelectUne() {
		return selectUne;
	}

	public void setSelectUne(Boolean selectUne) {
		this.selectUne = selectUne;
	}

	public List<Factura> getListFacturas() {
		return listFacturas;
	}

	public void setListFacturas(List<Factura> listFacturas) {
		this.listFacturas = listFacturas;
	}



	public List<FacturasPendientesBean> getLisFacturasPendientesBean() {
		return lisFacturasPendientesBean;
	}



	public void setLisFacturasPendientesBean(List<FacturasPendientesBean> lisFacturasPendientesBean) {
		this.lisFacturasPendientesBean = lisFacturasPendientesBean;
	}



	public String getCodigoFarmacia() {
		return codigoFarmacia;
	}



	public void setCodigoFarmacia(String codigoFarmacia) {
		this.codigoFarmacia = codigoFarmacia;
	}



	public List<AdFarmaciasBean> getListAdFarmaciasBean() {
		return listAdFarmaciasBean;
	}



	public void setListAdFarmaciasBean(List<AdFarmaciasBean> listAdFarmaciasBean) {
		this.listAdFarmaciasBean = listAdFarmaciasBean;
	}
}
