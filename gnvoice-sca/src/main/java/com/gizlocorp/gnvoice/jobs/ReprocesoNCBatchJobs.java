package com.gizlocorp.gnvoice.jobs;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.gnvoiceFybeca.dao.SidEmpresasDAO;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;

@Stateless
public class ReprocesoNCBatchJobs {

	private static Logger log = Logger.getLogger(ReprocesoNCBatchJobs.class
			.getName());

	@Resource(mappedName = "java:/queue/notaCreditoProcesarBatchQueue")
	private Queue queueReproceso;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@EJB
	SidEmpresasDAO sidEmpresasDAO;

	public ReprocesoNCBatchJobs() {
	}

	public void inqueue(String mensaje) {
		javax.jms.Connection connection = null;
		Session session = null;
		try {
			connection = this.connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(this.queueReproceso);
			ObjectMessage objmsg = session.createObjectMessage();
			objmsg.setObject(mensaje);
			producer.send(objmsg);
		} catch (Exception e) {
			e.printStackTrace();
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

	@Schedule(second = "0", minute = "0", hour = "*/3", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*", info = "reproceso")
	private void scheduledTimeout(final Timer t) {
		try {
			
			String batch = DocumentUtil
					.readContentFile(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS
							+ "/gnvoice/recursos/batch.txt");
			
			//String batch = "1";
			

			if (batch != null && "1".equals(batch)) {
				log.info("Ejecuta batch Nota de Credito... ");
				executebatch();
			}else{
				log.info("No ejecuta batch Nota de Credito ... ");
			}
		} catch (Exception e) {
			log.info("No ejecuta batch  Nota de Credito... ");
		}

	}

	private void executebatch() {
		try {						
			CredencialDS credencialDS = new CredencialDS();
			credencialDS.setDatabaseId("oficina");
			credencialDS.setUsuario("WEBLINK");
			credencialDS.setClave("weblink_2013");
			
			CredencialDS credencialDSana = new CredencialDS();
			credencialDSana.setDatabaseId("sana");
			credencialDSana.setUsuario("WEBLINK");
			credencialDSana.setClave("weblink_2013");
			
			CredencialDS credencialDOki = new CredencialDS();
			credencialDOki.setDatabaseId("okimaster");
			credencialDOki.setUsuario("WEBLINK");
			credencialDOki.setClave("weblink_2013");
			
		
			List<String> respuestaAll = new ArrayList<String>();
			
			Connection conn = null;
			List<String> respuesta = null;
			try{				
				conn = Conexion.obtenerConexionFybeca(credencialDS);
				respuesta = sidEmpresasDAO.listaSidOficina(conn);
				respuestaAll.addAll(respuesta);
			} catch (Exception e) {
				log.error(e.getMessage()+" "+e.getLocalizedMessage());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.error(e.getMessage()+" "+e.getLocalizedMessage());
				}
			}
			
			Connection connSana = null;
			List<String> respuestaSana = null;
			try{				
				connSana = Conexion.obtenerConexionFybeca(credencialDSana);
				respuestaSana = sidEmpresasDAO.listaSidSanaSana(connSana);
				respuestaAll.addAll(respuestaSana);
			} catch (Exception e) {
				log.error(e.getMessage()+" "+e.getLocalizedMessage());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.error(e.getMessage()+" "+e.getLocalizedMessage());
				}
			}

			Connection connOki = null;
			List<String> respuestaOki = null;
			try{
				connOki = Conexion.obtenerConexionFybeca(credencialDOki);
				respuestaOki = sidEmpresasDAO.listaOkiDoki(connOki);
				respuestaAll.addAll(respuestaOki);
			} catch (Exception e) {
				log.error(e.getMessage()+" "+e.getLocalizedMessage());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					log.error(e.getMessage()+" "+e.getLocalizedMessage());
				}
			}
			
			List<String> listaFarmaciasAll = new ArrayList<String>();
			if(respuestaAll != null && !respuestaAll.isEmpty()){
				for (String itera : respuestaAll) {
					if(!(listaFarmaciasAll.contains(itera))){
						listaFarmaciasAll.add(itera);
					}				
				}
			}
			
			
			if(listaFarmaciasAll != null && !listaFarmaciasAll.isEmpty()){
				for(String itera: listaFarmaciasAll){
					log.info(itera.toString());
					inqueue(itera);
				}
			}
			

		} catch (Exception e) {
			log.error("Error proceso automatico", e);
		}
/*
		for (String itera : listaFarmacias) {
			inqueue(itera.substring(1) + "&" + itera);
		}*/

	}
}