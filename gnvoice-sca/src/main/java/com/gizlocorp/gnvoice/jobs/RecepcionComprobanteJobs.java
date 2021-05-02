package com.gizlocorp.gnvoice.jobs;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.utilitario.Constantes;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import com.gizlocorp.gnvoice.service.impl.CacheBean;

@Stateless
public class RecepcionComprobanteJobs {
	private static Logger log = Logger.getLogger(RecepcionComprobanteJobs.class.getName());

	@Resource(mappedName = "java:/queue/recepcionComprobante2Queue")
	private Queue queueMail;
	
	@Resource(mappedName = "java:/queue/recepcionComprobante3Queue")
	private Queue queue;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB
	CacheBean cacheBean;

	public void inqueue(String ruc) {
		Connection connection = null;
		Session session = null;
		try {
			connection = this.connectionFactory.createConnection();
			session = connection.createSession(false, 1);
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
			if (connection != null)
				try {
					connection.close();
				} catch (JMSException e) {
					log.error("Error proceso automatico", e);
				}
		}
	}
	
	public void inqueueMail(String ruc) {
		Connection connection = null;
		Session session = null;
		try {
			connection = this.connectionFactory.createConnection();
			session = connection.createSession(false, 1);
			MessageProducer producer = session.createProducer(this.queueMail);
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
			if (connection != null)
				try {
					connection.close();
				} catch (JMSException e) {
					log.error("Error proceso automatico", e);
				}
		}
	}
	
	@Schedule(second = "0", minute = "0", hour = "*", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*", info = "RecepcionDocumentos")
	//@Schedule(second = "0", minute = "*", hour = "*", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*", info = "RecepcionDocumentos")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void scheduledTimeout(Timer t) {
		String batch;
		try {
			batch = DocumentUtil.readContentFile(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS + "/gnvoice/recursos/traductor.txt");
			
			if ((batch != null) && ("1".equals(batch))) {
				StringBuilder message = null;
				int min=0;
				int max = 199;
				for (int i = 1; i <= 5; i++) {
					message = new StringBuilder();
					message.append(min);
					message.append("&");
					message.append(max);
					message.append("&");
					message.append("FACTURA");					
					min = max+1;
					max = max+200;
					inqueue(message.toString());
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {		
//			batch = DocumentUtil.readContentFile(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS + "/gnvoice/recursos/traductor.txt");
//			Parametro parametro= servicioParametro.consultarParametro("JOB_MAIL", null);
//			if(parametro != null){
//				if ((batch != null) && ("1".equals(batch)) && parametro.getValor().equals("1")) {
//					ejecutaMail();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		
		
		
//		StringBuilder messagenota = null;
//		int minnota=0;
//		int maxnota = 199;
//		for (int i = 1; i <= 3; i++) {
//			messagenota = new StringBuilder();
//			messagenota.append(minnota);
//			messagenota.append("&");
//			messagenota.append(maxnota);
//			messagenota.append("&");
//			messagenota.append("CREDITO");
//			log.info(">> " + messagenota.toString());
//			minnota = max+1;
//			maxnota = max+200;
//			inqueue(messagenota.toString());
//		}
	}


	private void ejecutaMail() {
		
		try {
			String batch = DocumentUtil.readContentFile(
					com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS + "/gnvoice/recursos/traductor.txt");
			
			if ((batch != null) && ("1".equals(batch))) {
				List<Organizacion> emisores = this.servicioOrganizacionLocal.listarActivas();

				if ((emisores != null) && (!emisores.isEmpty())) {
					for (Organizacion emisor : emisores) {
						if ((emisor != null) && (Estado.ACT.equals(emisor.getEstado()))) {
							if (emisor.getRuc().equals("1790710319001") || emisor.getRuc().equals("1792239710001")
									|| emisor.getRuc().equals("1791715772001")) {
								// AG Cambio de logica para enviar a la cola
								// mensaje por cada una de las carpetas del
								// correo que se necesita
								// inqueue(emisor.getRuc());

								try {
									String dirServidor = Constantes.DIR_RECURSOS;
									Parametro popHost = this.cacheBean.consultarParametro("POP_HOST", emisor.getId());
									Parametro popPort = this.cacheBean.consultarParametro("POP_PORT", emisor.getId());
									Parametro popUsername = this.cacheBean.consultarParametro("POP_USERNAME",emisor.getId());
									Parametro popPassword = this.cacheBean.consultarParametro("POP_PASSWORD",emisor.getId());									

									List<String> listMail = null;
									List<String> listPass = null;
									if (popHost != null && popPort != null && popUsername != null
											&& popPassword != null) {
										listMail = Arrays.asList(popUsername.getValor().split(";"));
										listPass = Arrays.asList(popPassword.getValorDesencriptado().split(";"));
									}

									if (listMail != null && listPass != null && !listMail.isEmpty()
											&& !listPass.isEmpty()) {
										int index = 0;
										StringBuilder message = null;

										for (String mail : listMail) {
											String pass = listPass.get(index).toString();
											if ((popHost != null) && (popPort != null) && (mail != null)
													&& (pass != null)) {
												try {
													if (mail != null && pass != null && !mail.isEmpty()
															&& !pass.isEmpty()&& !mail.toLowerCase().equals("recepcionfacturasservicios@corporaciongpf.com".toLowerCase())
															&& !mail.toLowerCase().equals("g_gyerecepcionfacturasservicios@corporaciongpf.com".toLowerCase())) {

														int range = 40;
														for (int i = 1; i <= 5; i++) {
															message = new StringBuilder();
															message.append("imap.gmail.com");
															message.append("&");
															message.append(mail);
															message.append("&");
															message.append(pass);
															message.append("&");
															message.append(dirServidor);
															message.append("&");
															message.append("" + range);
															message.append("&");
															message.append("" + i);
													
															log.info(">> " + message.toString());
															inqueueMail(message.toString());

														}

													}
												} catch (Exception ex) {
													log.error("No ejecuta carpera: " + mail, ex);
												}

											}
											index++;
										}
									}

								} catch (Exception ex) {
									log.info(
											"Error en proceso"
													+ ex.getMessage(), ex);
								}
							}

						}
					}
				}
			} else {
				log.info("****** No ejecuta integrcion ... archivo no esta configurado ");
			}
		} catch (Exception e) {
			log.info("No ejecuta integrcion ...  archivo no esta configurado");
		}
	}
}