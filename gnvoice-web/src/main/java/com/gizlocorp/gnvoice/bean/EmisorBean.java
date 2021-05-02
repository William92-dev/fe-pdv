package com.gizlocorp.gnvoice.bean;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.UbicacionGeografica;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.adm.utilitario.TripleDESUtil;
import com.gizlocorp.gnvoice.bean.databean.EmisorDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

//@Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("emisorBean")
public class EmisorBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private static Logger log = Logger.getLogger(EmisorBean.class.getName());

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;

	private List<Organizacion> organizacionesPadre;

	@Inject
	private ApplicationBean appBean;

	@Inject
	private EmisorDataBean emisorDataBean;

	private String nombre;
	private String acronimo;
	private String ruc;
	private Estado estado;

	private UbicacionGeografica ciudad;
	private Organizacion organizacionPadre;

	private boolean datoValido = false;


	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	private SessionBean sessionBean;

	public List<SelectItem> getEstados() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Estado estado : Estado.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	public List<SelectItem> getOpciones() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Logico estado : Logico.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	public String buscarEmisor() {
		try {
			log.debug("Listando organizaciones");
			List<Organizacion> organizaciones = servicioOrganizacion
					.listarOrganizaciones(nombre, acronimo, ruc, null);
			if (organizaciones == null) {
				organizaciones = new ArrayList<>();
			}
			emisorDataBean.setOrganizaciones(organizaciones);
		} catch (GizloException e) {
			e.printStackTrace();
			errorMessage("Ha ocurrido un error al listar Organizaciones");
			log.debug(e.getMessage());
		}

		return null;
	}

	@PostConstruct
	public void postContruct() {
		try {
			organizacionesPadre = servicioOrganizacion.listarActivas();
			List<Organizacion> organizaciones = servicioOrganizacion
					.listarActivas();
			if (organizaciones == null) {
				organizaciones = new ArrayList<>();
			}
			emisorDataBean.setOrganizaciones(organizaciones);
			if (organizacionesPadre == null) {
				organizacionesPadre = new ArrayList<>();
			}

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al listar Organizaciones");
			log.debug(e.getMessage());
		}
	}

	public String guardarEmisor() {
		log.debug("organizacion es: "
				+ emisorDataBean.getOrganizacion().getNombre());
		datoValido = true;
		boolean esEncriptado = false;

		try {
			if (emisorDataBean.getOrganizacion().getNombre() == null
					|| emisorDataBean.getOrganizacion().getNombre().isEmpty()) {
				datoValido = false;
				infoMessage("Ingrese nombre de Organizacion",
						"Ingrese nombre de Organizacion");
			} else {
				log.debug("Id de ciudad: " + emisorDataBean.getIdCiudad());
				if (emisorDataBean.getIdCiudad() != null
						&& emisorDataBean.getIdCiudad() != 0) {
					emisorDataBean.getOrganizacion().setCiudad(buscarCiudad());
				} else {
					emisorDataBean.getOrganizacion().setCiudad(null);
				}
				if (emisorDataBean.getIdOrganizacionPadre() != null
						&& emisorDataBean.getIdOrganizacionPadre() != 0) {
					emisorDataBean.getOrganizacion().setOrganizacionPadre(
							buscarOrganizacionPadre());
				}

				// GUARDA
				if (emisorDataBean.getOpcion() == 0) {

					log.debug("guardando ");
					// bucar organizacion x ruc
					Organizacion organizacion = servicioOrganizacion
							.consultarOrganizacion(emisorDataBean
									.getOrganizacion().getRuc());
					if (organizacion != null) {
						infoMessage("Ruc ya registrado!. Ingrese otro",
								"Ruc ya registrado!. Ingrese otro");
					} else {

						emisorDataBean.getOrganizacion().setCiudad(null);
						emisorDataBean.getOrganizacion().setEnContigencia(
								Logico.N);
						emisorDataBean.getOrganizacion()
								.setEsObligadoContabilidad(Logico.N);
						emisorDataBean.getOrganizacion().setClaveToken(
								TripleDESUtil._encrypt(emisorDataBean
										.getOrganizacion().getClaveToken()));
						esEncriptado = true;
						servicioOrganizacion
								.ingresarOrganizacion(emisorDataBean
										.getOrganizacion());
						infoMessage("Ingresado correctamente!",
								"Ingresado correctamente!");

					}
				} else {
					log.debug("editando ");
					emisorDataBean.getOrganizacion().setClaveToken(
							TripleDESUtil._encrypt(emisorDataBean
									.getOrganizacion().getClaveToken()));
					esEncriptado = true;
					servicioOrganizacion.actualizarOrganizacion(emisorDataBean
							.getOrganizacion());
					infoMessage("Actualizado correctamente!",
							"Actualizado correctamente!");
				}

			}

		} catch (Exception e) {
			errorMessage("Ha ocurrido un error al guardar Organizacion",
					"Ha ocurrido un error al guardar Organizacion");
			log.error(e.getMessage());
		} finally {
			if (esEncriptado) {
				try {
					emisorDataBean.getOrganizacion().setClaveToken(
							TripleDESUtil._decrypt(emisorDataBean
									.getOrganizacion().getClaveToken()));
				} catch (Exception e1) {
					log.error("Ha ocurrido un error al guardar Organizacion",
							e1);
				}

			}
		}
		return null;
	}

	/**
	 * Redirecciona a formularioOrganizacion
	 * 
	 * @return
	 */
	public String verOrganizacionEm() {
		try {
			log.debug("organizacion: " + emisorDataBean.getOrganizacion());
			if (emisorDataBean.getOrganizacion().getCiudad() != null) {
				log.debug("no es null ciudad");
				emisorDataBean.setIdCiudad(emisorDataBean.getOrganizacion()
						.getCiudad().getId());
			} else {
				emisorDataBean.setIdCiudad(0L);
			}

			if (emisorDataBean.getOrganizacion().getOrganizacionPadre() != null) {
				emisorDataBean.setIdOrganizacionPadre(emisorDataBean
						.getOrganizacion().getOrganizacionPadre().getId());
			} else {
				emisorDataBean.setIdOrganizacionPadre(0L);
			}

			if (emisorDataBean.getOrganizacion() != null
					&& emisorDataBean.getOrganizacion().getId() != null) {
				if (emisorDataBean.getOrganizacion().getClaveToken() != null
						&& !emisorDataBean.getOrganizacion().getClaveToken()
								.isEmpty()) {
					String clave = TripleDESUtil._decrypt(emisorDataBean
							.getOrganizacion().getClaveToken());
					emisorDataBean.getOrganizacion().setClaveToken(clave);
				}

				emisorDataBean.setEditarArchivo(false);
				return "formularioEmisor_em";
			}
		} catch (Exception ex) {
			errorMessage("Error al cargar organizacion",
					"Error al cargar organizacion!");
			log.error(ex.getMessage());
			return null;
		}
		return null;
	}

	public void cargarDocumentoEm(org.richfaces.event.FileUploadEvent event) {
		try {
			org.richfaces.model.UploadedFile item = event.getUploadedFile();

			String filename = item.getName();
			log.debug(filename);
			byte[] bytes = item.getData();
			String token = DocumentUtil
					.createDocumentToken(bytes, filename,
							emisorDataBean.getOrganizacion() != null
									&& emisorDataBean.getOrganizacion()
											.getRuc() != null ? emisorDataBean
									.getOrganizacion().getRuc() : "999999999",
							"emisor", sessionBean.getDirectorioServidor());

			emisorDataBean.getOrganizacion().setToken(token);

		} catch (Exception ex) {
			errorMessage("Ha ocurrido un error al cargar el documento",
					"Ha ocurrido un error al cargar el documento");
			log.error("Error cargarDocumento: " + ex.getMessage(), ex);
		}

	}

	public void cargarLogoEm(org.richfaces.event.FileUploadEvent event) {
		try {

			org.richfaces.model.UploadedFile item = event.getUploadedFile();

			String filename = item.getName();
			log.debug(filename);
			byte[] bytes = item.getData();
			String logo = DocumentUtil
					.createDocumentlogo(bytes, filename,
							emisorDataBean.getOrganizacion() != null
									&& emisorDataBean.getOrganizacion()
											.getRuc() != null ? emisorDataBean
									.getOrganizacion().getRuc() : "999999999",
							"emisor", sessionBean.getDirectorioServidor());

			emisorDataBean.getOrganizacion().setLogoEmpresa(logo);

		} catch (Exception ex) {
			errorMessage("Ha ocurrido un error al cargar el documento",
					"Ha ocurrido un error al cargar el documento");
			log.error("Error cargarDocumento: " + ex.getMessage(), ex);
		}

	}

	public String eliminarOrganizacion() {
		try {
			emisorDataBean.getOrganizacion().setEstado(Estado.INA);
			servicioOrganizacion.actualizarOrganizacion(emisorDataBean
					.getOrganizacion());
			// servicioOrganizacion.eliminarOrganizacion(emisorDataBean.getOrganizacion());
		} catch (GizloException e) {
			errorMessage(
					"Ha ocurrido un error al eliminar Organizacion revisar dependencias",
					"Ha ocurrido un error al eliminar Organizacion revisar dependencias");
			log.error(e.getMessage());
		}

		return null;
	}

	public UbicacionGeografica buscarCiudad() {
		this.ciudad = null;
		for (UbicacionGeografica ciudadObj : appBean.getCiudades()) {
			if (ciudadObj.getId().equals(emisorDataBean.getIdCiudad())) {
				this.ciudad = ciudadObj;
				break;
			}
		}
		return ciudad;
	}

	public Organizacion buscarOrganizacionPadre() {
		this.organizacionPadre = null;
		for (Organizacion orgPadreObj : this.organizacionesPadre) {
			if (orgPadreObj.getId().equals(
					emisorDataBean.getIdOrganizacionPadre())) {
				this.organizacionPadre = orgPadreObj;
				break;
			}
		}
		return organizacionPadre;
	}

	public String agregarOrganizacion() {
		emisorDataBean.setIdCiudad(0L);
		emisorDataBean.setIdOrganizacionPadre(0L);
		emisorDataBean.setOrganizacion(new Organizacion());
		emisorDataBean.setEditarArchivo(false);
		return "formularioEmisor";
	}

	public String agregarOrganizacionEm() {
		emisorDataBean.setIdCiudad(0L);
		emisorDataBean.setIdOrganizacionPadre(0L);
		emisorDataBean.setOrganizacion(new Organizacion());
		emisorDataBean.setEditarArchivo(false);
		return "formularioEmisor_em";
	}

	public String regresar() {
		return "listaEmisor";
	}

	public String regresarEm() {
		return "listaEmisor_em";
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public boolean isDatoValido() {
		return datoValido;
	}

	public void setDatoValido(boolean datoValido) {
		this.datoValido = datoValido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getAcronimo() {
		return acronimo;
	}

	public void setAcronimo(String acronimo) {
		this.acronimo = acronimo;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public UbicacionGeografica getCiudad() {
		return ciudad;
	}

	public void setCiudad(UbicacionGeografica ciudad) {
		this.ciudad = ciudad;
	}

	public List<Organizacion> getOrganizacionesPadre() {
		return organizacionesPadre;
	}

	public void setOrganizacionesPadre(List<Organizacion> organizacionesPadre) {
		this.organizacionesPadre = organizacionesPadre;
	}

	public Organizacion getOrganizacionPadre() {
		return organizacionPadre;
	}

	public void setOrganizacionPadre(Organizacion organizacionPadre) {
		this.organizacionPadre = organizacionPadre;
	}
	
	@SuppressWarnings({ "rawtypes", "resource" })
	public static void readXLSFile() throws IOException {
		InputStream ExcelFileToRead = new FileInputStream("C:\\Users\\Usuario\\Desktop\\Libro1.xls");
		HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);

		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row;
		HSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (HSSFRow) rows.next();
			Iterator cells = row.cellIterator();

			while (cells.hasNext()) {
				cell = (HSSFCell) cells.next();
				
				if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					System.out.print(cell.getStringCellValue() + " ");
				} 
			}
			System.out.println();
		}

	}
	
	public static void main(String[] args) throws Exception {
//		readXLSXFile();
		System.out.println("888");
		readXLSFile();
	}

}