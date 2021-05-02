package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioPersonaLocal;
import com.gizlocorp.gnvoice.bean.databean.PersonaDataBean;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

/**
 * 
 * @author Jose Vinueza
 * 
 */

// @Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("personaBean")
public class PersonaBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(PersonaBean.class.getName());

	@Inject
	private PersonaDataBean personaDataBean;

	@Inject
	private ApplicationBean appBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioPersonaImpl!com.gizlocorp.adm.servicio.local.ServicioPersonaLocal")
	ServicioPersonaLocal servicioPersonaLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Inject
	SessionBean sessionBean;

	@PostConstruct
	public void postContruct() {
		appBean.recargar();
		
	}

	/**
	 * Busca Persona
	 */
	public void buscarPersona() {
		personaDataBean.getNombre();
		List<Persona> listaPerona;
		try {
			listaPerona = servicioPersonaLocal.obtenerPorParametros(
					personaDataBean.getIdentificacion(),
					personaDataBean.getNombre(), personaDataBean.getApellido(),
					null);
			personaDataBean.setListPersonas(listaPerona);
		} catch (GizloException ex) {
			log.error("Error" + ex.getMessage(), ex);
			errorMessage("Ha ocurrido un error al listar Personas");
		}

	}

	/**
	 * nuevo persona
	 * 
	 * @return
	 */
	public String nuevoPersona() {
		personaDataBean.setPersonaSeleccionada(new Persona());
		personaDataBean.setOpcionNuevo(true);
		personaDataBean.setOpcionEditar(false);
		return "formularioPersona";
	}

	/**
	 * editar persona
	 * 
	 * @return
	 */
	public String editarPersona() {
		personaDataBean.setOpcionEditar(true);
		personaDataBean.setOpcionNuevo(false);
		return "formularioPersona";
	}

	/**
	 * regresa
	 * 
	 * @return
	 */
	public String regresar() {
		personaDataBean.setPersonaSeleccionada(new Persona());
		return "listaPersona";
	}

	/**
	 * guarda Persona
	 */
	public void guardar() {
		if (valida()) {
			if (personaDataBean.getPersonaSeleccionada().getId() != null) {
				servicioPersonaLocal.guardarPersona(personaDataBean
						.getPersonaSeleccionada());
				infoMessage("Modificado Correctamente!",
						"Modificado Correctamente!");
			} else {
				servicioPersonaLocal.guardarPersona(personaDataBean
						.getPersonaSeleccionada());
				infoMessage("Ingresado correctamente!",
						"Ingresado correctamente!");
			}
		}
	}

	/**
	 * valida persona
	 * 
	 * @return
	 */
	private boolean valida() {
		boolean retorno = true;

		if (!(personaDataBean.getPersonaSeleccionada().getIdentificacion()
				.length() == 10 || personaDataBean.getPersonaSeleccionada()
				.getIdentificacion().length() == 13)) {
			infoMessage("Numeros de Digitos de Identificacion Incorrectos",
					"Numeros de Digitos de Identificacion Incorrectos");
			return false;
		}
		if (personaDataBean.getPersonaSeleccionada().getNombres().length() < 3) {
			infoMessage("Nombre Clave Debe Tener Minimo 3 Caracteres",
					"Nombre Clave Debe Tener Minimo 3 Caracteres");
			return false;
		}
		if (personaDataBean.getPersonaSeleccionada().getApellidos().length() < 3) {
			infoMessage("Apellido Clave Debe Tener Minimo 3 Caracteres",
					"Apellido Clave Debe Tener Minimo 3 Caracteres");
			return false;
		}
		if (personaDataBean.isOpcionNuevo()) {
			List<Persona> personas;
			try {
				personas = servicioPersonaLocal.obtenerPorParametros(
						personaDataBean.getPersonaSeleccionada()
								.getIdentificacion(), null, null, null);
				if (personas != null) {
					infoMessage("Registro con Identificacion "
							+ personas.get(0).getIdentificacion()
							+ " ya Existe", "Registro con Identificacion "
							+ personas.get(0).getIdentificacion()
							+ " ya Existe");
					return false;
				}
			} catch (GizloException ex) {
				log.error("Error" + ex.getMessage(), ex);
				errorMessage("Ha ocurrido un error al listar Personas en metodo validar");
			}

		}

		return retorno;
	}

	public List<SelectItem> getOpcionesLogicas() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Logico estado : Logico.values()) {
			items.add(new SelectItem(estado, estado.getDescripcion()));
		}
		return items;
	}

	public PersonaDataBean getPersonaDataBean() {
		return personaDataBean;
	}

	public void setPersonaDataBean(PersonaDataBean personaDataBean) {
		this.personaDataBean = personaDataBean;
	}

}
