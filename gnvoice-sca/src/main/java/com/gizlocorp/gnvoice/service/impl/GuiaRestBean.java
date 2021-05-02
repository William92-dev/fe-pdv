package com.gizlocorp.gnvoice.service.impl;

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
import com.gizlocorp.gnvoice.modelo.ClaveContingencia;
import com.gizlocorp.gnvoice.service.GuiaRest;
import com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia;
import com.gizlocorp.gnvoice.utilitario.ComprobanteUtil;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.xml.message.GuiaRestGenerarClaveAccesoResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaRestProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.MensajeRespuesta;

@Service(GuiaRest.class)
public class GuiaRestBean implements GuiaRest {

	@Resource(mappedName = "java:/queue/guiaProcesarQueue")
	private Queue queue;

	@Resource(mappedName = "java:/queue/guiaProcesarBatchQueue")
	private Queue queueBatch;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacionLocal;

	@EJB(lookup = "java:global/adm-ejb/ServicioParametroImpl!com.gizlocorp.adm.servicio.local.ServicioParametroLocal")
	ServicioParametroLocal servicioParametro;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioClaveContigenciaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioClaveContigencia")
	ServicioClaveContigencia servicioClaveContigencia;

	private static Logger log = Logger.getLogger(GuiaRestBean.class.getName());

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
	public GuiaRestProcesarResponse procesar(String parametros) {
		log.debug("**Servicio procesar en linea");
		String respuestaJMS = inqueue(parametros, this.queue);

		GuiaRestProcesarResponse respuesta = new GuiaRestProcesarResponse();
		respuesta.setHttpStatus("104");
		respuesta.setRespuesta(respuestaJMS != null ? respuestaJMS
				: Constantes.RESPUESTA_OK);
		return respuesta;

	}

	@Override
	public GuiaRestGenerarClaveAccesoResponse generarClaveAcceso(
			String parametros) {

		String codEstab = null, ptoEmision = null, secuencial = null, ruc = null, fechaEmision = null;

		GuiaRestGenerarClaveAccesoResponse respuestaClaveAcceso = new GuiaRestGenerarClaveAccesoResponse();
		GuiaRestGenerarClaveAccesoResponse.Respuesta respuesta = new GuiaRestGenerarClaveAccesoResponse.Respuesta();
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
				Parametro parametroContingencia = servicioParametro
						.consultarParametro(Constantes.CONTINGENCIA,
								emisor.getId());
				if (parametroContingencia != null
						&& parametroContingencia.getValor().equalsIgnoreCase(
								"S")) {
					// verifico si la clave generada tiene una clave de acceso
					// relacionada...
					List<ClaveContingencia> listClaves = servicioClaveContigencia
							.recuperarClaveRelacionada(claveAcceso);
					if (listClaves != null && !listClaves.isEmpty()) {
						claveAcceso = ComprobanteUtil
								.generarClaveAccesoProveedor(fechaEmision,
										codFacParametro.getValor(), listClaves
												.get(0).getClave());
					} else {
						// verifico si hay claves de contingencias disponibles.
						ClaveContingencia claveNoUsada = servicioClaveContigencia
								.recuperarNoUsada(emisor.getId(), ambiente,
										claveAcceso);
						if (claveNoUsada != null) {
							claveAcceso = ComprobanteUtil
									.generarClaveAccesoProveedor(fechaEmision,
											codFacParametro.getValor(),
											claveNoUsada.getClave());
						}
					}
				}
				// fin

				respuestaClaveAcceso.setHttpStatus("104");
				respuesta.setClaveAcceso(claveAcceso);
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
	public GuiaRestProcesarResponse bacth() {
		log.debug("**Servicio procesar en batch");
		try {
			inqueue("1794" + "&" + "OKIMASTER");
			inqueue("1710" + "&" + "O1710");
			inqueue("1713" + "&" + "O1713");
			inqueue("1714" + "&" + "O1714");
			inqueue("1723" + "&" + "O1723");
			inqueue("1733" + "&" + "OKIMASTER");
			inqueue("1742" + "&" + "OKIMASTER");
			inqueue("1824" + "&" + "O1824");
			inqueue("1866" + "&" + "O1866");
			inqueue("1884" + "&" + "OKIMASTER");
			inqueue("1905" + "&" + "OKIMASTER");
			inqueue("1927" + "&" + "OKIMASTER");
			inqueue("1941" + "&" + "O1941");
			inqueue("1978" + "&" + "OKIMASTER");
			inqueue("1983" + "&" + "OKIMASTER");
			inqueue("2061" + "&" + "OKIMASTER");
			inqueue("2062" + "&" + "O2062");
			inqueue("2064" + "&" + "OKIMASTER");
			inqueue("2072" + "&" + "OKIMASTER");
			inqueue("2073" + "&" + "OKIMASTER");
			inqueue("2074" + "&" + "OKIMASTER");
			inqueue("2080" + "&" + "OKIMASTER");
			inqueue("2119" + "&" + "OKIMASTER");
			inqueue("2125" + "&" + "OKIMASTER");
			inqueue("2129" + "&" + "OKIMASTER");
			inqueue("2130" + "&" + "OKIMASTER");
			inqueue("2135" + "&" + "OKIMASTER");
			inqueue("2136" + "&" + "OKIMASTER");
			inqueue("2138" + "&" + "OKIMASTER");
			inqueue("2139" + "&" + "OKIMASTER");
			inqueue("2140" + "&" + "OKIMASTER");
			inqueue("2141" + "&" + "OKIMASTER");
			inqueue("2189" + "&" + "OKIMASTER");
			inqueue("2190" + "&" + "OKIMASTER");
			inqueue("2195" + "&" + "OKIMASTER");
			inqueue("2196" + "&" + "OKIMASTER");
			inqueue("2199" + "&" + "OKIMASTER");

			List<String> listaFarmacias = new ArrayList<String>();
			inqueue("1724" + "&" + "O1724");
			listaFarmacias.add("S142");
			listaFarmacias.add("F1719");
			listaFarmacias.add("S164");
			listaFarmacias.add("S1851");
			listaFarmacias.add("S1450");

			listaFarmacias.add("F1");
			listaFarmacias.add("F2");
			listaFarmacias.add("F3");
			listaFarmacias.add("F4");
			listaFarmacias.add("F5");
			listaFarmacias.add("F9");
			listaFarmacias.add("F12");
			listaFarmacias.add("F15");
			listaFarmacias.add("F17");
			listaFarmacias.add("F19");
			listaFarmacias.add("F20");
			listaFarmacias.add("F21");
			listaFarmacias.add("F23");
			listaFarmacias.add("F24");
			listaFarmacias.add("F25");
			listaFarmacias.add("F26");
			listaFarmacias.add("F28");
			listaFarmacias.add("F29");
			listaFarmacias.add("F31");
			listaFarmacias.add("F32");
			listaFarmacias.add("F33");
			listaFarmacias.add("F34");
			listaFarmacias.add("F45");
			listaFarmacias.add("F47");
			listaFarmacias.add("F48");
			listaFarmacias.add("F53");
			listaFarmacias.add("F58");
			listaFarmacias.add("F60");
			listaFarmacias.add("F61");
			listaFarmacias.add("F65");
			listaFarmacias.add("F79");
			listaFarmacias.add("F80");
			listaFarmacias.add("F82");
			listaFarmacias.add("F83");
			listaFarmacias.add("F84");
			listaFarmacias.add("F85");
			listaFarmacias.add("F87");
			listaFarmacias.add("F93");
			listaFarmacias.add("F94");
			listaFarmacias.add("F95");
			listaFarmacias.add("F128");
			listaFarmacias.add("F135");
			listaFarmacias.add("F140");
			listaFarmacias.add("F141");
			listaFarmacias.add("F148");
			listaFarmacias.add("F271");
			listaFarmacias.add("F451");
			listaFarmacias.add("F471");
			listaFarmacias.add("F531");
			listaFarmacias.add("F631");
			listaFarmacias.add("F651");
			listaFarmacias.add("F671");
			listaFarmacias.add("F691");
			listaFarmacias.add("F731");
			listaFarmacias.add("F772");
			listaFarmacias.add("F791");
			listaFarmacias.add("F1134");
			listaFarmacias.add("F1135");
			listaFarmacias.add("F1271");
			listaFarmacias.add("F1432");
			listaFarmacias.add("F1441");
			listaFarmacias.add("F1461");
			listaFarmacias.add("F1462");
			listaFarmacias.add("F1466");
			listaFarmacias.add("F1505");
			listaFarmacias.add("F1515");
			listaFarmacias.add("F1536");
			listaFarmacias.add("F1538");
			listaFarmacias.add("F1685");
			listaFarmacias.add("F1704");
			listaFarmacias.add("F1721");
			listaFarmacias.add("F1727");
			listaFarmacias.add("F1734");
			listaFarmacias.add("F1735");
			listaFarmacias.add("F1736");
			listaFarmacias.add("F1760");
			listaFarmacias.add("F1770");
			listaFarmacias.add("F1790");
			listaFarmacias.add("F1795");
			listaFarmacias.add("F1802");
			listaFarmacias.add("F1810");
			listaFarmacias.add("F1811");
			listaFarmacias.add("F1821");
			listaFarmacias.add("F1901");
			listaFarmacias.add("F1914");
			listaFarmacias.add("F1929");
			listaFarmacias.add("F1938");
			listaFarmacias.add("F2056");
			listaFarmacias.add("F2075");
			listaFarmacias.add("F2078");
			listaFarmacias.add("F2120");
			listaFarmacias.add("S35");
			listaFarmacias.add("S36");
			listaFarmacias.add("S37");
			listaFarmacias.add("S38");
			listaFarmacias.add("S39");
			listaFarmacias.add("S40");
			listaFarmacias.add("S41");
			listaFarmacias.add("S43");
			listaFarmacias.add("S44");
			listaFarmacias.add("S67");
			listaFarmacias.add("S68");
			listaFarmacias.add("S69");
			listaFarmacias.add("S70");
			listaFarmacias.add("S71");
			listaFarmacias.add("S72");
			listaFarmacias.add("S73");
			listaFarmacias.add("S74");
			listaFarmacias.add("S75");
			listaFarmacias.add("S76");
			listaFarmacias.add("S77");
			listaFarmacias.add("S78");
			listaFarmacias.add("S96");
			listaFarmacias.add("S97");
			listaFarmacias.add("S98");
			listaFarmacias.add("S99");
			listaFarmacias.add("S100");
			listaFarmacias.add("S101");
			listaFarmacias.add("S103");
			listaFarmacias.add("S104");
			listaFarmacias.add("S105");
			listaFarmacias.add("S106");
			listaFarmacias.add("S107");
			listaFarmacias.add("S108");
			listaFarmacias.add("S109");
			listaFarmacias.add("S110");
			listaFarmacias.add("S111");
			listaFarmacias.add("S112");
			listaFarmacias.add("S113");
			listaFarmacias.add("S116");
			listaFarmacias.add("S117");
			listaFarmacias.add("S118");
			listaFarmacias.add("S119");
			listaFarmacias.add("S120");
			listaFarmacias.add("S121");
			listaFarmacias.add("S123");
			listaFarmacias.add("S124");
			listaFarmacias.add("S125");
			listaFarmacias.add("S126");
			listaFarmacias.add("S127");
			listaFarmacias.add("S130");
			listaFarmacias.add("S131");
			listaFarmacias.add("S132");
			listaFarmacias.add("S133");
			listaFarmacias.add("S136");
			listaFarmacias.add("S137");
			listaFarmacias.add("S138");

			listaFarmacias.add("S143");
			listaFarmacias.add("S144");
			listaFarmacias.add("S145");
			listaFarmacias.add("S147");
			listaFarmacias.add("S149");
			listaFarmacias.add("S150");
			listaFarmacias.add("S151");
			listaFarmacias.add("S152");
			listaFarmacias.add("S153");
			listaFarmacias.add("S154");
			listaFarmacias.add("S155");
			listaFarmacias.add("S156");
			listaFarmacias.add("S157");
			listaFarmacias.add("S159");
			listaFarmacias.add("S160");
			listaFarmacias.add("S161");
			listaFarmacias.add("S162");
			listaFarmacias.add("S163");

			listaFarmacias.add("S165");
			listaFarmacias.add("S166");
			listaFarmacias.add("S167");
			listaFarmacias.add("S168");
			listaFarmacias.add("S169");
			listaFarmacias.add("S170");
			listaFarmacias.add("S171");
			listaFarmacias.add("S172");
			listaFarmacias.add("S173");
			listaFarmacias.add("S174");
			listaFarmacias.add("S176");
			listaFarmacias.add("S177");
			listaFarmacias.add("S178");
			listaFarmacias.add("S179");
			listaFarmacias.add("S180");
			listaFarmacias.add("S181");
			listaFarmacias.add("S182");
			listaFarmacias.add("S183");
			listaFarmacias.add("S184");
			listaFarmacias.add("S185");
			listaFarmacias.add("S186");
			listaFarmacias.add("S187");
			listaFarmacias.add("S188");
			listaFarmacias.add("S189");
			listaFarmacias.add("S191");
			listaFarmacias.add("S192");
			listaFarmacias.add("S193");
			listaFarmacias.add("S194");
			listaFarmacias.add("S195");
			listaFarmacias.add("S197");
			listaFarmacias.add("S198");
			listaFarmacias.add("S199");
			listaFarmacias.add("S200");
			listaFarmacias.add("S202");
			listaFarmacias.add("S203");
			listaFarmacias.add("S204");
			listaFarmacias.add("S205");
			listaFarmacias.add("S206");
			listaFarmacias.add("S207");
			listaFarmacias.add("S208");
			listaFarmacias.add("S209");
			listaFarmacias.add("S210");
			listaFarmacias.add("S211");
			listaFarmacias.add("S212");
			listaFarmacias.add("S213");
			listaFarmacias.add("S214");
			listaFarmacias.add("S215");
			listaFarmacias.add("S216");
			listaFarmacias.add("S217");
			listaFarmacias.add("S218");
			listaFarmacias.add("S219");
			listaFarmacias.add("S220");
			listaFarmacias.add("S221");
			listaFarmacias.add("S222");
			listaFarmacias.add("S223");
			listaFarmacias.add("S224");
			listaFarmacias.add("S225");
			listaFarmacias.add("S226");
			listaFarmacias.add("S227");
			listaFarmacias.add("S228");
			listaFarmacias.add("S230");
			listaFarmacias.add("S231");
			listaFarmacias.add("S232");
			listaFarmacias.add("S233");
			listaFarmacias.add("S234");
			listaFarmacias.add("S235");
			listaFarmacias.add("S236");
			listaFarmacias.add("S237");
			listaFarmacias.add("S238");
			listaFarmacias.add("S239");
			listaFarmacias.add("S240");
			listaFarmacias.add("S242");
			listaFarmacias.add("S243");
			listaFarmacias.add("S244");
			listaFarmacias.add("S245");
			listaFarmacias.add("S246");
			listaFarmacias.add("S247");
			listaFarmacias.add("S248");
			listaFarmacias.add("S249");
			listaFarmacias.add("S250");
			listaFarmacias.add("S252");
			listaFarmacias.add("S253");
			listaFarmacias.add("S254");
			listaFarmacias.add("S270");
			listaFarmacias.add("S272");
			listaFarmacias.add("S273");
			listaFarmacias.add("S274");
			listaFarmacias.add("S311");
			listaFarmacias.add("S331");
			listaFarmacias.add("S891");
			listaFarmacias.add("S911");
			listaFarmacias.add("S931");
			listaFarmacias.add("S951");
			listaFarmacias.add("S971");
			listaFarmacias.add("S972");
			listaFarmacias.add("S973");
			listaFarmacias.add("S991");
			listaFarmacias.add("S992");
			listaFarmacias.add("S1011");
			listaFarmacias.add("S1031");
			listaFarmacias.add("S1051");
			listaFarmacias.add("S1071");
			listaFarmacias.add("S1091");
			listaFarmacias.add("S1111");
			listaFarmacias.add("S1131");
			listaFarmacias.add("S1132");
			listaFarmacias.add("S1151");
			listaFarmacias.add("S1191");
			listaFarmacias.add("S1192");
			listaFarmacias.add("S1211");
			listaFarmacias.add("S1231");
			listaFarmacias.add("S1232");
			listaFarmacias.add("S1233");
			listaFarmacias.add("S1251");
			listaFarmacias.add("S1272");
			listaFarmacias.add("S1312");
			listaFarmacias.add("S1313");
			listaFarmacias.add("S1331");
			listaFarmacias.add("S1351");
			listaFarmacias.add("S1352");
			listaFarmacias.add("S1353");
			listaFarmacias.add("S1371");
			listaFarmacias.add("S1391");
			listaFarmacias.add("S1412");
			listaFarmacias.add("S1413");
			listaFarmacias.add("S1414");
			listaFarmacias.add("S1431");
			listaFarmacias.add("S1433");
			listaFarmacias.add("S1434");
			listaFarmacias.add("S1435");
			listaFarmacias.add("S1436");
			listaFarmacias.add("S1437");
			listaFarmacias.add("S1440");
			listaFarmacias.add("S1451");
			listaFarmacias.add("S1453");
			listaFarmacias.add("S1454");
			listaFarmacias.add("S1455");
			listaFarmacias.add("S1457");
			listaFarmacias.add("S1458");
			listaFarmacias.add("S1459");
			listaFarmacias.add("S1460");
			listaFarmacias.add("S1463");
			listaFarmacias.add("S1465");
			listaFarmacias.add("S1467");
			listaFarmacias.add("S1468");
			listaFarmacias.add("S1469");
			listaFarmacias.add("S1470");
			listaFarmacias.add("S1471");
			listaFarmacias.add("S1472");
			listaFarmacias.add("S1473");
			listaFarmacias.add("S1475");
			listaFarmacias.add("S1476");
			listaFarmacias.add("S1478");
			listaFarmacias.add("S1481");
			listaFarmacias.add("S1483");
			listaFarmacias.add("S1484");
			listaFarmacias.add("S1485");
			listaFarmacias.add("S1486");
			listaFarmacias.add("S1487");
			listaFarmacias.add("S1501");
			listaFarmacias.add("S1503");
			listaFarmacias.add("S1504");
			listaFarmacias.add("S1506");
			listaFarmacias.add("S1507");
			listaFarmacias.add("S1508");
			listaFarmacias.add("S1510");
			listaFarmacias.add("S1512");
			listaFarmacias.add("S1516");
			listaFarmacias.add("S1519");
			listaFarmacias.add("S1522");
			listaFarmacias.add("S1524");
			listaFarmacias.add("S1525");
			listaFarmacias.add("S1526");
			listaFarmacias.add("S1527");
			listaFarmacias.add("S1528");
			listaFarmacias.add("S1529");
			listaFarmacias.add("S1535");
			listaFarmacias.add("S1537");
			listaFarmacias.add("S1602");
			listaFarmacias.add("S1603");
			listaFarmacias.add("S1604");
			listaFarmacias.add("S1606");
			listaFarmacias.add("S1607");
			listaFarmacias.add("S1608");
			listaFarmacias.add("S1621");
			listaFarmacias.add("S1622");
			listaFarmacias.add("S1624");
			listaFarmacias.add("S1625");
			listaFarmacias.add("S1627");
			listaFarmacias.add("S1628");
			listaFarmacias.add("S1629");
			listaFarmacias.add("S1631");
			listaFarmacias.add("S1634");
			listaFarmacias.add("S1635");
			listaFarmacias.add("S1636");
			listaFarmacias.add("S1637");
			listaFarmacias.add("S1638");
			listaFarmacias.add("S1639");
			listaFarmacias.add("S1641");
			listaFarmacias.add("S1642");
			listaFarmacias.add("S1643");
			listaFarmacias.add("S1648");
			listaFarmacias.add("S1649");
			listaFarmacias.add("S1650");
			listaFarmacias.add("S1651");
			listaFarmacias.add("S1652");
			listaFarmacias.add("S1653");
			listaFarmacias.add("S1654");
			listaFarmacias.add("S1655");
			listaFarmacias.add("S1656");
			listaFarmacias.add("S1660");
			listaFarmacias.add("S1661");
			listaFarmacias.add("S1664");
			listaFarmacias.add("S1667");
			listaFarmacias.add("S1668");
			listaFarmacias.add("S1669");
			listaFarmacias.add("S1674");
			listaFarmacias.add("S1677");
			listaFarmacias.add("S1678");
			listaFarmacias.add("S1679");
			listaFarmacias.add("S1680");
			listaFarmacias.add("S1681");
			listaFarmacias.add("S1688");
			listaFarmacias.add("S1705");
			listaFarmacias.add("S1706");
			listaFarmacias.add("S1707");
			listaFarmacias.add("S1708");
			listaFarmacias.add("S1709");
			listaFarmacias.add("S1718");
			listaFarmacias.add("S1725");
			listaFarmacias.add("S1726");
			listaFarmacias.add("S1728");
			listaFarmacias.add("S1729");
			listaFarmacias.add("S1730");
			listaFarmacias.add("S1731");
			listaFarmacias.add("S1732");
			listaFarmacias.add("S1737");
			listaFarmacias.add("S1747");
			listaFarmacias.add("S1748");
			listaFarmacias.add("S1749");
			listaFarmacias.add("S1768");
			listaFarmacias.add("S1769");
			listaFarmacias.add("S1789");
			listaFarmacias.add("S1791");
			listaFarmacias.add("S1797");
			listaFarmacias.add("S1804");
			listaFarmacias.add("S1805");
			listaFarmacias.add("S1808");
			listaFarmacias.add("S1809");
			listaFarmacias.add("S1815");
			listaFarmacias.add("S1816");
			listaFarmacias.add("S1822");
			listaFarmacias.add("S1823");
			listaFarmacias.add("S1826");
			listaFarmacias.add("S1827");
			listaFarmacias.add("S1828");
			listaFarmacias.add("S1829");
			listaFarmacias.add("S1830");
			listaFarmacias.add("S1831");
			listaFarmacias.add("S1832");
			listaFarmacias.add("S1833");
			listaFarmacias.add("S1834");
			listaFarmacias.add("S1835");
			listaFarmacias.add("S1836");
			listaFarmacias.add("S1838");
			listaFarmacias.add("S1840");
			listaFarmacias.add("S1841");
			listaFarmacias.add("S1843");
			listaFarmacias.add("S1845");
			listaFarmacias.add("S1846");
			listaFarmacias.add("S1847");
			listaFarmacias.add("S1848");
			listaFarmacias.add("S1849");

			listaFarmacias.add("S1852");
			listaFarmacias.add("S1853");
			listaFarmacias.add("S1854");
			listaFarmacias.add("S1855");
			listaFarmacias.add("S1856");
			listaFarmacias.add("S1859");
			listaFarmacias.add("S1860");
			listaFarmacias.add("S1861");
			listaFarmacias.add("S1862");
			listaFarmacias.add("S1868");
			listaFarmacias.add("S1872");
			listaFarmacias.add("S1873");
			listaFarmacias.add("S1874");
			listaFarmacias.add("S1875");
			listaFarmacias.add("S1876");
			listaFarmacias.add("S1877");
			listaFarmacias.add("S1878");
			listaFarmacias.add("S1879");
			listaFarmacias.add("S1880");
			listaFarmacias.add("S1882");
			listaFarmacias.add("S1885");
			listaFarmacias.add("S1887");
			listaFarmacias.add("S1888");
			listaFarmacias.add("S1889");
			listaFarmacias.add("S1893");
			listaFarmacias.add("S1898");
			listaFarmacias.add("S1899");
			listaFarmacias.add("S1902");
			listaFarmacias.add("S1903");
			listaFarmacias.add("S1908");
			listaFarmacias.add("S1909");
			listaFarmacias.add("S1910");
			listaFarmacias.add("S1911");
			listaFarmacias.add("S1916");
			listaFarmacias.add("S1917");
			listaFarmacias.add("S1920");
			listaFarmacias.add("S1921");
			listaFarmacias.add("S1922");
			listaFarmacias.add("S1923");
			listaFarmacias.add("S1928");
			listaFarmacias.add("S1940");
			listaFarmacias.add("S1943");
			listaFarmacias.add("S1944");
			listaFarmacias.add("S1945");
			listaFarmacias.add("S1946");
			listaFarmacias.add("S1948");
			listaFarmacias.add("S1951");
			listaFarmacias.add("S1984");
			listaFarmacias.add("S1985");
			listaFarmacias.add("S1993");
			listaFarmacias.add("S1994");
			listaFarmacias.add("S1995");
			listaFarmacias.add("S2001");
			listaFarmacias.add("S2017");
			listaFarmacias.add("S2038");
			listaFarmacias.add("S2059");
			listaFarmacias.add("S2063");
			listaFarmacias.add("S2069");
			listaFarmacias.add("S2090");
			listaFarmacias.add("S2093");
			listaFarmacias.add("S2094");
			listaFarmacias.add("S2097");
			listaFarmacias.add("S2104");
			listaFarmacias.add("S2105");
			listaFarmacias.add("S2110");
			listaFarmacias.add("S2112");
			listaFarmacias.add("S2113");
			listaFarmacias.add("S2137");
			listaFarmacias.add("S2198");

			for (String itera : listaFarmacias) {
				inqueue(itera.substring(1) + "&" + itera);
			}

		} catch (Exception e) {
			log.error("Error proceso automatico", e);
		}
		GuiaRestProcesarResponse respuesta = new GuiaRestProcesarResponse();
		respuesta.setHttpStatus("104");
		respuesta.setRespuesta(Constantes.RESPUESTA_OK);
		return respuesta;
	}

}
