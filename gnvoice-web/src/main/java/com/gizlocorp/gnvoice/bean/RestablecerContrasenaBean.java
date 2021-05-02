package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.security.auth.spi.Util;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Persona;
import com.gizlocorp.adm.modelo.UsuarioExterno;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.adm.utilitario.MailDelivery;
import com.gizlocorp.adm.utilitario.MailMessage;
import com.gizlocorp.adm.utilitario.PasswordGenerator;
import com.gizlocorp.gnvoice.bean.databean.RestablecerContrasenaDataBean;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;

/**
 * 
 * @author Jose Vinueza
 * 
 */

// @Interceptors(CurrentUserProvider.class)
@ViewScoped
@Named("restablecerContrasenaBean")
public class RestablecerContrasenaBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger
			.getLogger(RestablecerContrasenaBean.class.getName());

	@Inject
	private RestablecerContrasenaDataBean reContrasenaDataBean;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;

	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	public void enviarCorreo() {
		UsuarioExterno usuario = usuarioUsuarioLocal
				.obtenerUsuarioExterno(reContrasenaDataBean.getUsuario());

		if (usuario == null || usuario.getPersona() == null) {
			infoMessage("Usuario no existe en el sistema",
					"Usuario no existe en el sistema");
			return;
		}

		try {

			reContrasenaDataBean.setUsuarioExterno(usuario);
			Organizacion emisor = reContrasenaDataBean.getUsuarioExterno()
					.getPersona().getOrganizacion();

			reContrasenaDataBean.setUsuarioExterno(usuarioUsuarioLocal
					.obtenerUsuarioExterno(reContrasenaDataBean.getUsuario()));
			reContrasenaDataBean.setEmisor(emisor);

			Persona persona = reContrasenaDataBean.getUsuarioExterno()
					.getPersona();

			String claveProvicional = PasswordGenerator.generate();
			MailMessage mailMensaje = new MailMessage();
			mailMensaje.setSubject("Restablecer Contrasena");
			mailMensaje.setFrom(servicioParametro.consultarParametro(
					Constantes.CORREO_REMITE,
					emisor != null ? emisor.getId() : null).getValor());

			if (persona == null || persona.getCorreoElectronico() == null
					|| persona.getCorreoElectronico().isEmpty()
					|| "ACTUALIZA".equals(persona.getCorreoElectronico())) {
				infoMessage(
						"Usted no posee un correo electronico parametriza. Por favor Comuniquese con el departamento de sistemas",
						"Usted no posee un correo electronico parametriza. Por favor Comuniquese con el departamento de sistemas");
				return;
			}
			// mailMensaje.setSto(persona.getCorreoElectronico());
			List<String> listaCorreos = new ArrayList<>();
			listaCorreos.add(persona.getCorreoElectronico());
			mailMensaje.setTo(listaCorreos);

			StringBuilder body = new StringBuilder();
			body.append("<p>Estimado ");
			body.append(usuario.getPersona().getNombreCompleto());
			body.append("</p> ");
			body.append("<p>Usted a solicitado el cambio de contraseña, su contraseña provisional es: ");
			body.append(claveProvicional);
			body.append("</p> ");

			mailMensaje.setBody(body.toString());

			MailDelivery.send(
					mailMensaje,
					servicioParametro.consultarParametro(Constantes.SMTP_HOST,
							emisor != null ? emisor.getId() : null).getValor(),
					servicioParametro.consultarParametro(Constantes.SMTP_PORT,
							emisor != null ? emisor.getId() : null).getValor(),
					servicioParametro.consultarParametro(
							Constantes.SMTP_USERNAME,
							emisor != null ? emisor.getId() : null).getValor(),
					servicioParametro.consultarParametro(
							Constantes.SMTP_PASSWORD,
							emisor != null ? emisor.getId() : null)
							.getValorDesencriptado(),
					emisor != null ? emisor.getAcronimo() : null);

			reContrasenaDataBean.setContrasenaProvicional(claveProvicional);
			reContrasenaDataBean.getUsuarioExterno().setPassword(
					Util.createPasswordHash("SHA-256", "hex", null, null,
							claveProvicional));
			reContrasenaDataBean.getUsuarioExterno().setEsClaveAutogenerada(
					Logico.S);
			usuarioUsuarioLocal.guardarUsuarioExterno(reContrasenaDataBean
					.getUsuarioExterno());

			reContrasenaDataBean.setEnvioMail(true);
			infoMessage("Contrasena Profisional Enviada a su Mail",
					"Contrasena Profisional Enviada a su Mail");

		} catch (Exception e) {
			reContrasenaDataBean.setEnvioMail(false);
			e.printStackTrace();
			errorMessage("Error al enviar el mensaje");
		}
	}

	public String guardar() {
		if (verificarClaves()) {
//			String clave = Util.createPasswordHash("SHA-256", "hex", null,
//					null, reContrasenaDataBean.getContrasena());
			reContrasenaDataBean.getUsuarioExterno().setPassword(reContrasenaDataBean.getContrasena());
			reContrasenaDataBean.getUsuarioExterno().setEsClaveAutogenerada(
					Logico.N);
			// ThreadLocalCHAdm.setUsername(reContrasenaDataBean
			// .getUsuarioExterno().getUsername());
			usuarioUsuarioLocal.guardarUsuarioExterno(reContrasenaDataBean
					.getUsuarioExterno());

			infoMessage("Su Contrasena ha sido restablecida con Exito",
					"Su Contrasena Ha Sido Restablecida Con Exito");

			

			return "inicio";
		}

		return null;
	}

	private boolean verificarClaves() {
		boolean retorno = true;

		if (!(reContrasenaDataBean.getContrasenaActual() != null && !reContrasenaDataBean
				.getContrasenaActual().isEmpty())) {
			infoMessage("Debe Ingresar Contrasena Actual!",
					"Debe Ingresar Contrasena Actual!");
			return retorno = false;
		}
		if (!(reContrasenaDataBean.getContrasena() != null && !reContrasenaDataBean
				.getContrasena().isEmpty())) {
			infoMessage("Debe Ingresar Contrasena Nueva!",
					"Debe Ingresar Contrasena Nueva!");
			return retorno = false;
		}
		if (!(reContrasenaDataBean.getConfirmarContrasena() != null && !reContrasenaDataBean
				.getConfirmarContrasena().isEmpty())) {
			infoMessage("Debe Confirmar Contrasena!",
					"Debe Confirmar Contrasena!");
			return retorno = false;
		}

		// String clave = Util.createPasswordHash("SHA-256", "hex", null, null,
		// reContrasenaDataBean.getContrasenaActual());

		String claveBase = reContrasenaDataBean.getContrasenaProvicional();

		if (!claveBase.equals(reContrasenaDataBean.getContrasenaActual())) {
			infoMessage(
					"Contrasena Actual no coincide con la Contrasena Provicional!",
					"Contrasena Actual no coincide con la Contrasena Provicional!");
			return retorno = false;
		}

		if (!reContrasenaDataBean.getContrasena().equals(
				reContrasenaDataBean.getConfirmarContrasena())) {
			infoMessage("Contrasena Nueva no coinciden!",
					"Contrasena Nueva no coinciden!");
			return retorno = false;
		}

		return retorno;
	}

	public RestablecerContrasenaDataBean getReContrasenaDataBean() {
		return reContrasenaDataBean;
	}

	public void setReContrasenaDataBean(
			RestablecerContrasenaDataBean reContrasenaDataBean) {
		this.reContrasenaDataBean = reContrasenaDataBean;
	}

	@PostConstruct
	public void postContruct() {
		reContrasenaDataBean = new RestablecerContrasenaDataBean();
		reContrasenaDataBean.setUsuarioExterno(new UsuarioExterno());

		
	}

}
