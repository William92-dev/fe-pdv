package com.gizlocorp.gnvoiceFybeca.mdb;

import java.sql.Connection;
import java.util.Calendar;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.gizlocorp.gnvoice.enumeracion.Estado;
import com.gizlocorp.gnvoice.service.NotaCredito;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarResponse;
import com.gizlocorp.gnvoiceFybeca.dao.NotaCreditoDAO;
import com.gizlocorp.gnvoiceFybeca.dao.RespuestaDAO;
import com.gizlocorp.gnvoiceFybeca.dao.TipoComprobanteDAO;
import com.gizlocorp.gnvoiceFybeca.enumeration.ComprobanteEnum;
import com.gizlocorp.gnvoiceFybeca.enumeration.EstadoEnum;
import com.gizlocorp.gnvoiceFybeca.enumeration.GeneradoEnum;
import com.gizlocorp.gnvoiceFybeca.enumeration.ProcesoEnum;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.modelo.Respuesta;
import com.gizlocorp.gnvoiceFybeca.utilitario.Conexion;

/**
 * Message-Driven Bean implementation class for: MensajeManagment
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/notaCreditoProcesarQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "500"),
		@ActivationConfigProperty(propertyName = "redeliverUnspecified", propertyValue = "false") })
public class NotaCreditoProcesarManagment implements MessageListener {

	@EJB
	private NotaCreditoDAO servicioNotaCredito;

	@EJB
	RespuestaDAO servicioRespuestaSRILocal;

	@EJB
	TipoComprobanteDAO servicioTipoComprobanteLocal;

	@EJB
	NotaCredito notaCreditoBean;

	private static Logger log = Logger
			.getLogger(NotaCreditoProcesarManagment.class.getName());

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onMessage(final Message message) {
		ObjectMessage objectMessage = null;
		if (!(message instanceof ObjectMessage)) {

			log.debug("Mensaje recuperado no es instancia de ObjectMessage, se desechara "
					+ message);
			return;
		}
		objectMessage = (ObjectMessage) message;
		try {
			if ((objectMessage == null)
					|| !(objectMessage.getObject() instanceof String)) {

				log.debug("El objeto seteado en el mensaje no es de tipo String, se desechara "
						+ message);
				return;
			}

			// delay
			Thread.sleep(400l);

			// log.debug("**Obteniendo parametros del servicio");

			String mensaje = (String) objectMessage.getObject();

			if (mensaje == null || mensaje.isEmpty()) {
				log.error("El mensaje es vacio o nulo");
				return;
			}

			if (mensaje != null && !mensaje.isEmpty() && mensaje.contains("&")) {

				String[] parametros = mensaje.split("&");

				if (parametros.length != 3) {
					log.error("Parametros incompletos: " + mensaje);
					return;
				}

				Long farmacia = Long.parseLong(parametros[0]);
				Long documentoVenta = Long.parseLong(parametros[1]);
				String sid = parametros[2];

				log.debug("Farmacia: " + farmacia + " - documento: "
						+ documentoVenta);

				CredencialDS credencialDS = new CredencialDS();
				credencialDS.setDatabaseId(sid);
				credencialDS.setUsuario("WEBLINK");
				credencialDS.setClave("weblink_2013");

				Connection conn = null;
				try {
					conn = Conexion.obtenerConexionFybeca(credencialDS);
					Long tipo = servicioTipoComprobanteLocal.obtenerTipo(conn,
							ComprobanteEnum.NOTA_CREDITO.getDescripcion());

					log.info("***000***");
					NotaCreditoProcesarRequest item = servicioNotaCredito.listar(conn, documentoVenta, farmacia, tipo);
					System.out.println("888");
					if (item != null) {
						NotaCreditoProcesarResponse response = null;
						Respuesta respuesta = null;

						try {
							item.setSid(sid);
							//response = notaCreditoBean.procesar(item);
							response = notaCreditoBean.procesarOffline(item); 
							
							log.debug("********* Farmacia: " + farmacia
									+ " - documento: " + documentoVenta
									+ " tipo: " + tipo + " Respuesta:"
									+ response.getEstado() + " "
									+ response.getMensajeCliente() + " "
									+ response.getMensajeSistema());

							respuesta = new Respuesta();
							respuesta.setAutFirmaE(response
									.getNumeroAutorizacion());
							respuesta.setClaveAcceso(response
									.getClaveAccesoComprobante());
							respuesta.setComprobanteFirmaE(response
									.getNumeroAutorizacion());
							respuesta.setCorreoElectronico(item
									.getCorreoElectronicoNotificacion());
							// respuesta.setEstado(response.getEstado());

							if (response.getEstado().equals(
									Estado.PENDIENTE.getDescripcion())
									|| response.getEstado().equals(
											Estado.COMPLETADO.getDescripcion())
									|| response.getEstado().equals(
											Estado.RECIBIDA.getDescripcion())) {
								respuesta.setEstado(EstadoEnum.PENDIENTE
										.getDescripcion());
							}
							if (response.getEstado().equals(
									Estado.AUTORIZADO.getDescripcion())) {
								respuesta.setEstado(EstadoEnum.APROBADO
										.getDescripcion());
							}
							if (response.getEstado().equals(
									Estado.CANCELADO.getDescripcion())
									|| response.getEstado().equals(
											Estado.ERROR.getDescripcion())
									|| response.getEstado().equals(
											Estado.DEVUELTA.getDescripcion())
									|| response.getEstado().equals(
											Estado.RECHAZADO.getDescripcion())) {
								respuesta.setEstado(EstadoEnum.RECHAZADO
										.getDescripcion());
							}

							respuesta
									.setFecha(Calendar.getInstance().getTime());
							respuesta.setFechaFirmaE(response
									.getFechaAutorizacion() != null ? response
									.getFechaAutorizacion() : null);

							if (response.getEstado().equals(
									EstadoEnum.APROBADO.getDescripcion())) {
								respuesta.setFirmaE(GeneradoEnum.SI
										.getDescripcion());
							} else {
								respuesta.setFirmaE(GeneradoEnum.NO
										.getDescripcion());
							}

							respuesta.setIdDocumento(documentoVenta);
							respuesta.setIdFarmacia(farmacia);
							respuesta.setObservacion(response
									.getMensajeSistema());
							respuesta.setTipoComprobante(tipo);
							respuesta.setTipoProceso(ProcesoEnum.LINEA
									.getDescripcion());
							respuesta.setUsuario("");
							respuesta.setSid(sid);

							// inqueue(respuesta);
							if (conn == null) {
								conn = Conexion
										.obtenerConexionFybeca(credencialDS);
							}

							if (servicioRespuestaSRILocal.consultar(conn,
									respuesta.getIdFarmacia(),
									respuesta.getIdDocumento(), null,
									respuesta.getTipoComprobante()) == null) {
								servicioRespuestaSRILocal.insertarRespuesta(
										conn, respuesta);
							} else {

								servicioRespuestaSRILocal.actualizarRespuesta(
										conn, respuesta);
							}

						} catch (Exception e) {
							log.error("Mensaje fallido", e);

						}
					}
				} catch (Exception e) {
					log.error(e.getMessage());
				} finally {
					try {
						if (conn != null)
							conn.close();
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}

			}

		} catch (Exception ex) {
			log.error("Mensaje fallido", ex);
		}
	}

}
