//package com.gizlocorp.gnvoice.servicio.impl;
//
//import javax.annotation.Resource;
//import javax.ejb.Stateless;
//import javax.jms.ConnectionFactory;
//import javax.jms.JMSException;
//import javax.jms.MessageProducer;
//import javax.jms.ObjectMessage;
//import javax.jms.Queue;
//import javax.jms.Session;
//
//import org.apache.log4j.Logger;
//
//import com.gizlocorp.gnvoice.servicio.local.ServicioFactruraExterna;
//
//
//@Stateless
//public class ServicioFactruraExternaImpl implements ServicioFactruraExterna {
//
//	private static Logger log = Logger.getLogger(ServicioFactruraExternaImpl.class.getName());
//	
//	
//	@Resource(mappedName = "java:/ConnectionFactory")
//	private ConnectionFactory connectionFactory;
//
//	@Resource(mappedName = "java:/queue/facturaExternaQueue")
//	private Queue queue;
//	
//	@Override
//	public void insertFacturaMdb(String mensaje) {
//		javax.jms.Connection connection = null;
//		Session session = null;
//		try {
//			connection = this.connectionFactory.createConnection();
//			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			MessageProducer producer = session.createProducer(this.queue);
//			ObjectMessage objmsg = session.createObjectMessage();
//			log.info("***Enviando Mensaje a la Cola***facturaExternaQueue***"+mensaje);
//			objmsg.setObject(mensaje);
//			producer.send(objmsg);
//
//		} catch (Exception e) {
//			log.error("Error proceso automatico", e);
//		} finally {
//			if (session != null) {
//				try {
//					session.close();
//				} catch (JMSException e) {
//					log.error("Error proceso automatico", e);
//				}
//			}
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (JMSException e) {
//					log.error("Error proceso automatico", e);
//				}
//			}
//		}
//	}
//		
//	
//
// }
