package com.gizlocorp.gnvoice.mdb;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.gizlocorp.gnvoice.modelo.ColaFileClave;
import com.gizlocorp.gnvoice.modelo.FacturaRecepcion;
import com.gizlocorp.gnvoice.servicio.local.ServicioColaFileClave;
import com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/recepcionComprobante2Queue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "100"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "45000") })
public class prueba implements MessageListener{
	
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioRecepcionFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura")
	ServicioRecepcionFactura servicioFactura;
	
	
	//WSA INI
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioColaFileClaveImpl!com.gizlocorp.gnvoice.servicio.local.ServicioColaFileClave")
	ServicioColaFileClave servicioColaFileClave;
		//WSA FIN
	
	private FacturaRecepcion factura;

	
	public static void main(String[] args) {
//		
		
		prueba prueba = new prueba();
		try {
			prueba.invocar();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void invocar() throws NamingException {
		
		try {
			
			List<ColaFileClave> lista= servicioColaFileClave.listarColasFiles(); 
			
			for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
				ColaFileClave colaFileClave = (ColaFileClave) iterator.next();
				System.out.println(colaFileClave.getRuta());
				
			}
			
			/*factura = servicioFactura.obtenerComprobanteRecepcion("2504202101179132159600120070030005825350058253511", null, null, null);
			//log.info("clave a procesar "+e.getKey());
			if(factura == null ){
				//FacturaRecepcion respuestaFactura = tranformaGuardaFactura("2504202101179132159600120070030005825350058253511", new Date());	
				//log.info("clave a procesado exitosamente "+e.getKey());
			}*/
			
		} catch (Exception e) {
			
			e.printStackTrace();
			//System.out.println(e.printStackTrace());
			//InitialContext ic = new InitialContext();
			//servicioFactura = (ServicioRecepcionFactura) ic.lookup("java:global/gnvoice-ejb/ServicioRecepcionFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioRecepcionFactura");
			//factura = servicioFactura.obtenerComprobanteRecepcion("2504202101179132159600120070030005825350058253511",null, null,null);
		}
		
		
	}


	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

}
