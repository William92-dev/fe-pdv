package com.gizlocorp.gnvoice.mdb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal;
import com.gizlocorp.adm.utilitario.MailDelivery;
import com.gizlocorp.adm.utilitario.MailMessage;
import com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia;
import com.gizlocorp.gnvoice.servicio.local.ServicioComprobante;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;

/**
 * Message-Driven Bean implementation class for: MensajeManagment
 */

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/claveContigenciaQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false") })
public class ClaveContigenciaManagment implements MessageListener {

	private static Logger log = Logger
			.getLogger(ClaveContigenciaManagment.class.getName());

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioComprobanteImpl!com.gizlocorp.gnvoice.servicio.local.ServicioComprobante")
	ServicioComprobante servicioComprobante;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB(lookup = "java:global/adm-ejb/ServicioPlantillaImpl!com.gizlocorp.adm.servicio.local.ServicioPlantillaLocal")
	ServicioPlantillaLocal servicioPlantilla;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioClaveContigenciaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia")
	ServicioClaveContigencia servicioClaveContigencia;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onMessage(final Message message) {
		ObjectMessage objectMessage = null;

		if (!(message instanceof ObjectMessage)) {
			log.error("Mensaje recuperado no es instancia de ObjectMessage, se desechara "
					+ message);
			return;
		}

		objectMessage = (ObjectMessage) message;
		try {
			if ((objectMessage == null)
					|| !(objectMessage.getObject() instanceof String)) {
				log.error("El objeto seteado en el mensaje no es de tipo String, se desechara "
						+ message);
				return;
			}

			String ruc = (String) objectMessage.getObject();

			if (ruc == null || ruc.length() != 13) {
				log.error("RUC no es valido para proceso " + ruc);
				return;
			}

			List<Organizacion> emisores = servicioOrganizacionLocal
					.listarOrganizaciones(null, null, ruc, null);

			if (emisores == null || emisores.isEmpty()) {
				log.error("RUC no es valido para proceso (ha sido inactivado o eliminado) "
						+ ruc);
				return;
			}

			Organizacion emisor = emisores.get(0);

			int clavesTotal = 0, clavesUsadas = 0;
			float porcentajeUso = 0;
			Parametro ambParametro = servicioParametro.consultarParametro(
					Constantes.AMBIENTE, emisor.getId());

			clavesTotal = servicioClaveContigencia.contarClaves(emisor.getId(),
					ambParametro.getValor(), null);
			clavesUsadas = servicioClaveContigencia.contarClaves(
					emisor.getId(), ambParametro.getValor(), Logico.S);

			porcentajeUso = (clavesTotal != 0) ? (clavesUsadas / (float) clavesTotal) * 100
					: 0;
			log.debug("EMISOR: " + emisor.getId() + " PORCENTAJE USO: "
					+ porcentajeUso + " CLAVES total: " + clavesTotal
					+ " CLAVES USADAS: " + clavesUsadas + " AMBIENTE: "
					+ ambParametro.getValor());

			if (porcentajeUso > 70) {
				ambParametro = servicioParametro.consultarParametro(
						Constantes.AMBIENTE, emisor.getId());

				Plantilla t = servicioPlantilla.obtenerPlantilla(
						Constantes.NOTIFICACION_CLAVE, emisor.getId());
				log.debug("Plantilla encontrada: " + t);

				Map<String, Object> parametrosBody = new HashMap<String, Object>();
				// parametrosBody.put("porcentajeUso",porcentajeUso);

				MailMessage mailMensaje = new MailMessage();
				mailMensaje.setSubject(t.getTitulo());
				mailMensaje.setFrom(servicioParametro.consultarParametro(
						Constantes.CORREO_REMITE, emisor.getId()).getValor());
				mailMensaje.setTo(Arrays.asList(servicioParametro
						.consultarParametro(Constantes.CORREO_ADMIN,
								emisor.getId()).getValor()));
				mailMensaje.setBody(ComprobanteUtil.generarCuerpoMensaje(
						parametrosBody, t.getDescripcion(), t.getValor()));

				MailDelivery.send(
						mailMensaje,
						servicioParametro.consultarParametro(
								Constantes.SMTP_HOST, emisor.getId())
								.getValor(),
						servicioParametro.consultarParametro(
								Constantes.SMTP_PORT, emisor.getId())
								.getValor(),
						servicioParametro.consultarParametro(
								Constantes.SMTP_USERNAME, emisor.getId())
								.getValor(),
						servicioParametro.consultarParametro(
								Constantes.SMTP_PASSWORD, emisor.getId())
								.getValorDesencriptado(), emisor.getAcronimo());

			}
		} catch (Exception e) {
			log.error("Error al recuperar el objeto del mensaje", e);
			return;
		}
	}
}
