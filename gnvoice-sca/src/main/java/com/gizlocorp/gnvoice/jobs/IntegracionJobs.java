package com.gizlocorp.gnvoice.jobs;

import com.gizlocorp.adm.enumeracion.SistemaExternoEnum;
import com.gizlocorp.adm.utilitario.DocumentUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.log4j.Logger;

@Stateless
public class IntegracionJobs {
	private static Logger log = Logger.getLogger(IntegracionJobs.class.getName());

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/queue/facturaExternaQueue")
	private Queue queue;

	public void inqueue(String mensaje) {
		Connection connection = null;
		Session session = null;
		try {
			connection = this.connectionFactory.createConnection();
			session = connection.createSession(false, 1);

			MessageProducer producer = session.createProducer(this.queue);

			ObjectMessage objmsg = session.createObjectMessage();
			log.info("***mensaje***" + mensaje);
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
			if (connection != null)
				try {
					connection.close();
				} catch (JMSException e) {
					log.error("Error proceso automatico", e);
				}
		}
	}

   @Schedule(second="0", minute="0", hour="*", dayOfWeek="*", dayOfMonth="*", month="*", year="*", info="integracion")
   private void scheduledTimeout(Timer t)
   {
		
     try {
    	 String batch = DocumentUtil.readContentFile(com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+"/gnvoice/recursos/integracion.txt");
    	 log.info("*** iniciando jobs integrcion 2222***"+com.gizlocorp.adm.utilitario.Constantes.DIR_RECURSOS+"/gnvoice/recursos/integracion.txt"+"***"+batch);
 
       if ((batch != null) && ("1".equals(batch))) {
         log.info("*** iniciando jobs integrcion 2222***");
         Calendar cal = Calendar.getInstance();
         Date date = cal.getTime();
         cal.setTime(date);
         cal.add(5, -3);
         
         Calendar cal2 = Calendar.getInstance();
         Date date2 = cal.getTime();
         cal2.setTime(date2);
         cal2.add(5, -25);
         
         Calendar calendar = Calendar.getInstance();
         //Date date2 = cal.getTime();
         calendar.setTime(date); 
         calendar.add(Calendar.DAY_OF_YEAR, 2);
 
         Date fechaDesde = cal.getTime();
         Date fechaDesde2 = cal2.getTime();
         Date fechaHasta = calendar.getTime();
         DateFormat fecha = new SimpleDateFormat("yyyy-MM-dd");
 
         String mensajeBodegaFactura = fecha.format(fechaDesde) + "&" + fecha.format(fechaHasta) + "&" + SistemaExternoEnum.SISTEMABODEGA.name() + "&" + "01";
         String mensajeBodegaNotaCredito = fecha.format(fechaDesde) + "&" + fecha.format(fechaHasta) + "&" + SistemaExternoEnum.SISTEMABODEGA.name() + "&" + "03";
         //String mensajeBodegaFactura = "2015-09-01" + "&" + "2015-09-30" + "&" + SistemaExternoEnum.SISTEMABODEGA.name() + "&" + "01";
         //String mensajeBodegaNotaCredito = "2015-09-01" + "&" + "2015-09-30" + "&" + SistemaExternoEnum.SISTEMABODEGA.name() + "&" + "03";
         inqueue(mensajeBodegaFactura);
         inqueue(mensajeBodegaNotaCredito);
 
         String mensajeProveedoresFactura = fecha.format(fechaDesde) + "&" + fecha.format(fechaHasta) + "&" + SistemaExternoEnum.SISTEMAPROVEEDORES.name() + "&" + "01";
         String mensajeProveedoresNotaCredito = fecha.format(fechaDesde) + "&" + fecha.format(fechaHasta) + "&" + SistemaExternoEnum.SISTEMAPROVEEDORES.name() + "&" + "03";
         //String mensajeProveedoresFactura = "2015-09-01" + "&" + "2015-09-30" + "&" + SistemaExternoEnum.SISTEMAPROVEEDORES.name() + "&" + "01";
        // String mensajeProveedoresNotaCredito = "2015-09-01" + "&" + "2015-09-30" + "&" + SistemaExternoEnum.SISTEMAPROVEEDORES.name() + "&" + "03";
         inqueue(mensajeProveedoresFactura);
         inqueue(mensajeProveedoresNotaCredito);
         
        
         
         String mensajePDTA = "2016-04-01" + "&" + fecha.format(fechaHasta) + "&" + SistemaExternoEnum.SISTEMACRPDTA.name();
         inqueue(mensajePDTA);
         
       } else {
			log.info("No ejecuta integrcion ... archivo no esta configurado ");
		}

    }
     catch (Exception e)
     {
    	 log.info("*** iniciando jobs integrcion 444***");
    	 log.info("No ejecuta integrcion ...  archivo no esta configurado");
     }
   }
}
