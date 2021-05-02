package com.gizlocorp.gnvoice.service.impl;

import java.sql.Connection;
import java.util.ArrayList;
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
import com.gizlocorp.gnvoice.service.NotaCreditoRest;
import com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRestGenerarClaveAccesoResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRestProcesarResponse;
import com.gizlocorp.gnvoiceFybeca.dao.SidEmpresasDAO;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;

@Service(NotaCreditoRest.class)
public class NotaCreditoRestBean implements NotaCreditoRest {

	@Resource(mappedName = "java:/queue/notaCreditoProcesarQueue")
	private Queue queue;

	@Resource(mappedName = "java:/queue/notaCreditoProcesarBatchQueue")
	private Queue queueBatch;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioClaveContigenciaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia")
	ServicioClaveContigencia servicioClaveContigencia;
	
	@EJB
	SidEmpresasDAO sidEmpresasDAO;

	private static Logger log = Logger.getLogger(NotaCreditoRestBean.class
			.getName());

	public void inqueue(String mensaje) {
		javax.jms.Connection connection = null;
		Session session = null;
		try {
			connection = this.connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(this.queueBatch);
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

	public String inqueue(String parametros, Queue queue) {
		javax.jms.Connection connection = null;
		Session session = null;
		String respuesta = null;

		try {
			connection = this.connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			ObjectMessage objmsg = session.createObjectMessage();
			objmsg.setObject(parametros);
			producer.send(objmsg);

		} catch (Exception e) {
			log.error("Error proceso automatico", e);
			respuesta = "ERROR AL REGISTRAR PETICION";

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
		return respuesta;
	}

	@Override
	public NotaCreditoRestProcesarResponse procesar(String parametros) {
		log.debug("**Servicio procesar en linea");
		String respuestaJMS = inqueue(parametros, this.queue);

		NotaCreditoRestProcesarResponse respuesta = new NotaCreditoRestProcesarResponse();
		respuesta.setHttpStatus("104");
		respuesta.setRespuesta(respuestaJMS != null ? respuestaJMS
				: Constantes.RESPUESTA_OK);
		return respuesta;

	}

	@Override
	public NotaCreditoRestGenerarClaveAccesoResponse generarClaveAcceso(
			String parametros) {

		String codEstab = null, ptoEmision = null, secuencial = null, ruc = null, fechaEmision = null;

		NotaCreditoRestGenerarClaveAccesoResponse respuestaClaveAcceso = new NotaCreditoRestGenerarClaveAccesoResponse();
		NotaCreditoRestGenerarClaveAccesoResponse.Respuesta respuesta = new NotaCreditoRestGenerarClaveAccesoResponse.Respuesta();
		log.debug("GENERAR CLAVE ACCESO");
		try {
			if (parametros != null && !parametros.isEmpty()
					&& parametros.contains("&")) {
				String[] params = parametros.split("&");
				codEstab = params[0];
				ptoEmision = params[1];
				secuencial = params[2];
				ruc = params[3];
				fechaEmision = params[4];

				log.debug("Parametros: codEstab: " + codEstab);
				log.debug("Parametros: ptoEmision: " + ptoEmision);
				log.debug("Parametros: secuencial: " + secuencial);
				log.debug("Parametros: ruc: " + ruc);
				log.debug("Parametros: fechaEmision: " + fechaEmision);

				List<Organizacion> emisores = servicioOrganizacionLocal
						.listarOrganizaciones(null, null, ruc, null);

				if (emisores == null || emisores.isEmpty()) {
					respuestaClaveAcceso.setEstado(Estado.ERROR
							.getDescripcion());
					respuestaClaveAcceso.setClaveAccesoComprobante(null);
					respuestaClaveAcceso.setFechaAutorizacion(null);
					respuestaClaveAcceso.setNumeroAutorizacion(null);

					respuestaClaveAcceso
							.setMensajeSistema("No existe registrado un emisor con RUC: "
									+ ruc);
					respuestaClaveAcceso
							.setMensajeCliente("No existe registrado un emisor con RUC: "
									+ ruc);
					return respuestaClaveAcceso;
				}

				Organizacion emisor = emisores.get(0);

				// preguntar
				Parametro codFacParametro = servicioParametro
						.consultarParametro(Constantes.COD_FACTURA,
								emisor.getId());
				Parametro ambParametro = servicioParametro.consultarParametro(
						Constantes.AMBIENTE, emisor.getId());
				String ambiente = ambParametro.getValor();

				Parametro seqParametro = null;
				if (secuencial != null && !secuencial.isEmpty()) {
					seqParametro = new Parametro();
					seqParametro.setValor(secuencial);
				} else {
					respuestaClaveAcceso.setEstado(Estado.ERROR
							.getDescripcion());
					respuestaClaveAcceso.setClaveAccesoComprobante(null);
					respuestaClaveAcceso.setFechaAutorizacion(null);
					respuestaClaveAcceso.setNumeroAutorizacion(null);

					respuestaClaveAcceso
							.setMensajeSistema("Comprobante no tiene secuencial ");
					respuestaClaveAcceso
							.setMensajeCliente("Comprobante no tiene secuencial ");
					return respuestaClaveAcceso;

				}

				if (seqParametro.getValor().length() > 9) {
					seqParametro.setValor("1");
				} else if (seqParametro.getValor().length() < 9) {
					StringBuilder secuencialBld = new StringBuilder();

					int secuencialTam = 9 - seqParametro.getValor().length();
					for (int i = 0; i < secuencialTam; i++) {
						secuencialBld.append("0");
					}
					secuencialBld.append(seqParametro.getValor());
					seqParametro.setValor(secuencialBld.toString());
				}

				secuencial = seqParametro.getValor();

				Date fechaEmisionObj = FechaUtil.toDate(fechaEmision,
						FechaUtil.DB_FORMAT);
				fechaEmision = fechaEmision != null ? FechaUtil
						.formatearFecha(FechaUtil
								.convertirLongADate(fechaEmisionObj.getTime()),
								FechaUtil.DATE_FORMAT) : null;

				// setea clave de acceso
				StringBuilder codigoNumerico = new StringBuilder();
				codigoNumerico.append(codEstab);
				codigoNumerico.append(ptoEmision);
				codigoNumerico.append(secuencial);
				codigoNumerico.append(Constantes.CODIGO_NUMERICO);

				String claveAcceso = ComprobanteUtil
						.generarClaveAccesoProveedor(fechaEmision,
								codFacParametro.getValor(), ruc, ambiente,
								codigoNumerico.toString());

				// inicio jose
				// verificando si esta en contingencia
//				Parametro parametroContingencia = servicioParametro
//						.consultarParametro(Constantes.CONTINGENCIA,
//								emisor.getId());
//				if (parametroContingencia != null
//						&& parametroContingencia.getValor().equalsIgnoreCase(
//								"S")) {
//					// verifico si la clave generada tiene una clave de acceso
//					// relacionada...
//					List<ClaveContingencia> listClaves = servicioClaveContigencia
//							.recuperarClaveRelacionada(claveAcceso);
//					if (listClaves != null && !listClaves.isEmpty()) {
//						claveAcceso = ComprobanteUtil
//								.generarClaveAccesoProveedor(fechaEmision,
//										codFacParametro.getValor(), listClaves
//												.get(0).getClave());
//					} else {
//						// verifico si hay claves de contingencias disponibles.
//						ClaveContingencia claveNoUsada = servicioClaveContigencia
//								.recuperarNoUsada(emisor.getId(), ambiente,
//										claveAcceso);
//						if (claveNoUsada != null) {
//							claveAcceso = ComprobanteUtil
//									.generarClaveAccesoProveedor(fechaEmision,
//											codFacParametro.getValor(),
//											claveNoUsada.getClave());
//						}
//					}
//				}
				// fin

				respuesta.setClaveAcceso(claveAcceso);
				respuestaClaveAcceso.setHttpStatus("104");
				respuestaClaveAcceso.setRespuesta(respuesta);
			}

		} catch (Exception ex) {
			respuestaClaveAcceso.setMensajes(new ArrayList<MensajeRespuesta>());
			respuestaClaveAcceso.setEstado(Estado.ERROR.getDescripcion());
			MensajeRespuesta msgObj = new MensajeRespuesta();
			msgObj.setIdentificador("-100");
			msgObj.setMensaje("Ha ocurrido un problema: " + ex.getMessage());
			respuestaClaveAcceso.getMensajes().add(msgObj);
			respuestaClaveAcceso.setHttpStatus("105");
			log.error(ex.getMessage(), ex);
		}
		return respuestaClaveAcceso;
	}

	@Override
	public String bacth(String empresa) {
		log.debug("**Servicio procesar en batch");
		try {


			
			CredencialDS credencialDS = new CredencialDS();
			CredencialDS credencialDSana = new CredencialDS();
			CredencialDS credencialDOki = new CredencialDS();
			
			if(empresa.trim().toLowerCase().equals("null") || empresa.trim().equals("1")){
				
				credencialDS.setDatabaseId("oficina");
				credencialDS.setUsuario("WEBLINK");
				credencialDS.setClave("weblink_2013");
			}
			if(empresa.trim().toLowerCase().equals("null") || empresa.trim().equals("8")){

				credencialDSana.setDatabaseId("sana");
				credencialDSana.setUsuario("WEBLINK");
				credencialDSana.setClave("weblink_2013");
			}
			if(empresa.trim().toLowerCase().equals("null") || empresa.trim().equals("11")){

				credencialDOki.setDatabaseId("okimaster");
				credencialDOki.setUsuario("WEBLINK");
				credencialDOki.setClave("weblink_2013");
			}
			
		
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
			
			for (String itera : listaFarmaciasAll) {
					inqueue(itera);
			}

		} catch (Exception e) {
			log.error("Error proceso automatico", e);
		}		
		return "Empieza el demasdre NC";
	}
	
	
	  public String bacthLocal(String local)
	  {
	    log.info(local);
	    inqueue(local);
	    return "EMPEIZA EL DESMADRE X LOCAL " + local;
	  }

}
