package com.gizlocorp.gnvoice.service.impl;

import org.switchyard.component.bean.Service;

import com.gizlocorp.gnvoice.service.GuiaCECRest;
import com.gizlocorp.gnvoice.xml.message.GuiaRestGenerarClaveAccesoResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaRestProcesarResponse;

/*import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;

 import javax.annotation.Resource;
 import javax.ejb.EJB;
 import javax.jms.ConnectionFactory;
 import javax.jms.JMSException;
 import javax.jms.MessageProducer;
 import javax.jms.ObjectMessage;
 import javax.jms.Queue;
 import javax.jms.Session;

 import org.apache.log4j.Logger;
 import org.switchyard.component.bean.Service;

 import com.gizlocorp.adm.modelo.Organizacion;
 import com.gizlocorp.adm.modelo.Parametro;
 import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
 import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
 import com.gizlocorp.adm.utilitario.FechaUtil;
 import com.gizlocorp.gnvoice.enumeracion.Estado;
 import com.gizlocorp.gnvoice.service.GuiaCECRest;
 import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
 import com.gizlocorp.gnvoice.utilitario.Constantes;
 import com.gizlocorp.gnvoice.xml.message.GuiaRestGenerarClaveAccesoResponse;
 import com.gizlocorp.gnvoice.xml.message.GuiaRestProcesarResponse;
 import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;*/

@Service(GuiaCECRest.class)
public class GuiaCECRestBean implements GuiaCECRest {

	@Override
	public GuiaRestProcesarResponse procesar(String parametros) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GuiaRestGenerarClaveAccesoResponse generarClaveAcceso(
			String parametros) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Resource(mappedName = "java:/queue/guiaProcesarCECQueue") private Queue
	 * queue;
	 * 
	 * @Resource(mappedName = "java:/queue/guiaProcesarLoteCECQueue") private
	 * Queue queueLote;
	 * 
	 * @Resource(mappedName = "java:/queue/guiaReProcesarCECQueue") private
	 * Queue queueReproceso;
	 * 
	 * @Resource(mappedName = "java:/ConnectionFactory") private
	 * ConnectionFactory connectionFactory;
	 * 
	 * @EJB(lookup =
	 * "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal"
	 * ) ServicioOrganizacionLocal servicioOrganizacionLocal;
	 * 
	 * @EJB(lookup =
	 * "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal"
	 * ) ServicioParametroLocal servicioParametro;
	 * 
	 * private static Logger log =
	 * Logger.getLogger(GuiaRestBean.class.getName());
	 * 
	 * public String inqueue(String parametros, Queue queue) {
	 * javax.jms.Connection connection = null; Session session = null; String
	 * respuesta = null;
	 * 
	 * try { connection = this.connectionFactory.createConnection(); session =
	 * connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	 * MessageProducer producer = session.createProducer(queue); ObjectMessage
	 * objmsg = session.createObjectMessage(); objmsg.setObject(parametros);
	 * producer.send(objmsg);
	 * 
	 * } catch (Exception e) { log.error("Error proceso automatico", e);
	 * respuesta = "ERROR AL REGISTRAR PETICION";
	 * 
	 * } finally { if (session != null) { try { session.close(); } catch
	 * (JMSException e) { log.error("Error proceso automatico", e); } } if
	 * (connection != null) { try { connection.close(); } catch (JMSException e)
	 * { log.error("Error proceso automatico", e); } } } return respuesta; }
	 * 
	 * @Override public GuiaRestProcesarResponse procesar(String parametros) {
	 * log.debug("**Servicio procesar en linea"); String respuestaJMS =
	 * inqueue(parametros, this.queue);
	 * 
	 * GuiaRestProcesarResponse respuesta = new GuiaRestProcesarResponse();
	 * respuesta.setHttpStatus("104"); respuesta.setRespuesta(respuestaJMS !=
	 * null ? respuestaJMS : Constantes.RESPUESTA_OK); return respuesta;
	 * 
	 * }
	 * 
	 * @Override public GuiaRestProcesarResponse procesarLote(String parametros)
	 * { log.debug("**Servicio procesar en lote"); String respuestaJMS =
	 * inqueue(parametros, this.queueLote);
	 * 
	 * GuiaRestProcesarResponse respuesta = new GuiaRestProcesarResponse();
	 * respuesta.setHttpStatus("104"); respuesta.setRespuesta(respuestaJMS !=
	 * null ? respuestaJMS : Constantes.RESPUESTA_OK); return respuesta; }
	 * 
	 * @Override public GuiaRestProcesarResponse reprocesar(String parametros) {
	 * log.debug("**Servicio procesar en batch"); String respuestaJMS =
	 * inqueue(parametros, this.queueReproceso);
	 * 
	 * GuiaRestProcesarResponse respuesta = new GuiaRestProcesarResponse();
	 * respuesta.setHttpStatus("104"); respuesta.setRespuesta(respuestaJMS !=
	 * null ? respuestaJMS : Constantes.RESPUESTA_OK); return respuesta; }
	 * 
	 * @Override public GuiaRestGenerarClaveAccesoResponse generarClaveAcceso(
	 * String parametros) {
	 * 
	 * String codEstab = null, ptoEmision = null, secuencial = null, ruc = null,
	 * fechaEmision = null;
	 * 
	 * GuiaRestGenerarClaveAccesoResponse respuestaClaveAcceso = new
	 * GuiaRestGenerarClaveAccesoResponse();
	 * GuiaRestGenerarClaveAccesoResponse.Respuesta respuesta = new
	 * GuiaRestGenerarClaveAccesoResponse.Respuesta();
	 * log.debug("GENERAR CLAVE ACCESO"); try { if (parametros != null &&
	 * !parametros.isEmpty() && parametros.contains("&")) { String[] params =
	 * parametros.split("&"); codEstab = params[0]; ptoEmision = params[1];
	 * secuencial = params[2]; ruc = params[3]; fechaEmision = params[4];
	 * 
	 * log.debug("Parametros: codEstab: " + codEstab);
	 * log.debug("Parametros: ptoEmision: " + ptoEmision);
	 * log.debug("Parametros: secuencial: " + secuencial);
	 * log.debug("Parametros: ruc: " + ruc);
	 * log.debug("Parametros: fechaEmision: " + fechaEmision);
	 * 
	 * List<Organizacion> emisores = servicioOrganizacionLocal
	 * .listarOrganizaciones(null, null, ruc, null);
	 * 
	 * if (emisores == null || emisores.isEmpty()) {
	 * respuestaClaveAcceso.setEstado(Estado.ERROR .getDescripcion());
	 * respuestaClaveAcceso.setClaveAccesoComprobante(null);
	 * respuestaClaveAcceso.setFechaAutorizacion(null);
	 * respuestaClaveAcceso.setNumeroAutorizacion(null);
	 * 
	 * respuestaClaveAcceso
	 * .setMensajeSistema("No existe registrado un emisor con RUC: " + ruc);
	 * respuestaClaveAcceso
	 * .setMensajeCliente("No existe registrado un emisor con RUC: " + ruc);
	 * return respuestaClaveAcceso; }
	 * 
	 * Organizacion emisor = emisores.get(0);
	 * 
	 * Parametro codFacParametro = servicioParametro
	 * .consultarParametro(Constantes.COD_FACTURA, emisor.getId()); Parametro
	 * ambParametro = servicioParametro.consultarParametro( Constantes.AMBIENTE,
	 * emisor.getId()); String ambiente = ambParametro.getValor();
	 * 
	 * Parametro seqParametro = null; if (secuencial != null &&
	 * !secuencial.isEmpty()) { seqParametro = new Parametro();
	 * seqParametro.setValor(secuencial); } else {
	 * respuestaClaveAcceso.setEstado(Estado.ERROR .getDescripcion());
	 * respuestaClaveAcceso.setClaveAccesoComprobante(null);
	 * respuestaClaveAcceso.setFechaAutorizacion(null);
	 * respuestaClaveAcceso.setNumeroAutorizacion(null);
	 * 
	 * respuestaClaveAcceso
	 * .setMensajeSistema("Comprobante no tiene secuencial ");
	 * respuestaClaveAcceso
	 * .setMensajeCliente("Comprobante no tiene secuencial "); return
	 * respuestaClaveAcceso;
	 * 
	 * }
	 * 
	 * if (seqParametro.getValor().length() > 9) { seqParametro.setValor("1"); }
	 * else if (seqParametro.getValor().length() < 9) { StringBuilder
	 * secuencialBld = new StringBuilder();
	 * 
	 * int secuencialTam = 9 - seqParametro.getValor().length(); for (int i = 0;
	 * i < secuencialTam; i++) { secuencialBld.append("0"); }
	 * secuencialBld.append(seqParametro.getValor());
	 * seqParametro.setValor(secuencialBld.toString()); }
	 * 
	 * secuencial = seqParametro.getValor();
	 * 
	 * Date fechaEmisionObj = FechaUtil.toDate(fechaEmision,
	 * FechaUtil.DB_FORMAT); fechaEmision = fechaEmision != null ? FechaUtil
	 * .formatearFecha(FechaUtil .convertirLongADate(fechaEmisionObj.getTime()),
	 * FechaUtil.DATE_FORMAT) : null;
	 * 
	 * // setea clave de acceso StringBuilder codigoNumerico = new
	 * StringBuilder(); codigoNumerico.append(codEstab);
	 * codigoNumerico.append(ptoEmision); codigoNumerico.append(secuencial);
	 * codigoNumerico.append(Constantes.CODIGO_NUMERICO);
	 * 
	 * String claveAcceso = ComprobanteUtil
	 * .generarClaveAccesoProveedor(fechaEmision, codFacParametro.getValor(),
	 * ruc, ambiente, codigoNumerico.toString());
	 * 
	 * respuestaClaveAcceso.setHttpStatus("104");
	 * respuesta.setClaveAcceso(claveAcceso);
	 * respuestaClaveAcceso.setRespuesta(respuesta); }
	 * 
	 * } catch (Exception ex) { respuestaClaveAcceso.setMensajes(new
	 * ArrayList<MensajeRespuesta>());
	 * respuestaClaveAcceso.setEstado(Estado.ERROR.getDescripcion());
	 * MensajeRespuesta msgObj = new MensajeRespuesta();
	 * msgObj.setIdentificador("-100");
	 * msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
	 * respuestaClaveAcceso.getMensajes().add(msgObj);
	 * respuestaClaveAcceso.setHttpStatus("105"); log.error(ex.getMessage(),
	 * ex); } return respuestaClaveAcceso; }
	 */

}
