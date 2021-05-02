package com.gizlocorp.gnvoice.jobs;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;

//@Interceptors(CurrentUserADMProvider.class)
@Stateless
public class ClaveContigenciaJobs {

	private static Logger log = Logger.getLogger(ClaveContigenciaJobs.class
			.getName());

	@Resource(mappedName = "java:/queue/claveContigenciaQueue")
	private Queue queue;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB(lookup = "java:global/adm-ejb/ServicioCatalogoImpl!com.gizlocorp.adm.servicio.local.ServicioCatalogoLocal")
	ServicioCatalogoLocal servicioCatalogoLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	public ClaveContigenciaJobs() {
	}

	public void inqueue(String ruc) {
		javax.jms.Connection connection = null;
		Session session = null;
		try {
			connection = this.connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(this.queue);
			ObjectMessage objmsg = session.createObjectMessage();
			objmsg.setObject(ruc);
			producer.send(objmsg);

		} catch (Exception e) {
			log.error("Error proceso automatico", e);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					log.error("Error proceso automatico", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					log.error("Error proceso automatico", e);
				}
			}
		}
	}

	// @Schedule(second = "0", minute = "*/1", hour = "*", dayOfWeek = "*",
	// dayOfMonth = "*", month = "*", year = "*", info = "ClaveContigencia")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void scheduledTimeout(final Timer t) {
		try {
			List<Organizacion> emisores = servicioOrganizacionLocal
					.listarOrganizaciones(null, null, null, null);
			if (emisores != null && !emisores.isEmpty()) {
				for (Organizacion emisor : emisores) {
					if (emisor != null && Estado.ACT.equals(emisor.getEstado())) {
						try {
							inqueue(emisor.getRuc());

						} catch (Exception e) {
							log.error("Error proceso automatico", e);
						}
					}

				}
			}
		} catch (Exception e) {
			log.error("Error proceso automatico", e);
		}

	}
}