/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gizlocorp.gnvoice.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

import com.gizlocorp.gnvoice.xml.message.CeDisResponse;

/**
 * @author jruales
 */
public class ClienteRimBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger(ClienteRimBean.class.getName());

	private String url;
	private String serverURI;

	public ClienteRimBean(String url, String sURI) {
		this.url = url;
		this.serverURI = sURI;
	}

	public CeDisResponse llamadaPorOrdenFactura(String orderNumber) throws Exception {

		// Create SOAP Connection
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// String serverURI = "urn:ec.gob.funcionjudicial:rematejudicial:1.0";

		// SOAP Envelope
		// String serverURI =
		// "http://oracle.com/retail/integration/custom/bo/CeDisService/v1";
		// String url =
		// "http://10.10.202.108:8001/soa-infra/services/rms/CeDisService/CeDisWS?WSDL";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("urn", serverURI);

		SOAPHeader soapHead = envelope.getHeader();
		SOAPBody soapBody = envelope.getBody();
		// SOAPElement soapBodyElem =
		// soapBody.addChildElement("urn:generarDatosProvidencia");

		SOAPElement soapBodyElem0 = soapBody.addChildElement("CeDisOrderRequest", "urn");
		SOAPElement soapBodyElem1 = soapBodyElem0.addChildElement("order_number", "urn");
		soapBodyElem1.addTextNode(orderNumber);
		soapBodyElem0.addChildElement(soapBodyElem1);
		soapMessage.saveChanges();
		
		log.info("**orden numero***"+orderNumber);
		
		

		// Send SOAP Message to SOAP Server
		// String url =
		// "http://10.1.13.126:8080/rematejudicial/NotificarProvidencia?wsdl";
		log.info("ruta wsdl reim"+url);
		SOAPMessage soapResponse = soapConnection.call(soapMessage, url);

		SOAPBody soapBodyresponse;
		try {
			soapBodyresponse = soapResponse.getSOAPBody();
			// System.out.println("--->response");
			//soapResponse.writeTo(System.out);
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("SOAP Exception " + e1.toString());
			throw new Exception("SOAP Exception " + e1.toString());
		}

		// ejemplo de como obtener elemento a elemento
		NodeList order_number = soapBodyresponse.getElementsByTagNameNS(serverURI, "order_number");
		NodeList cedis_virtual = soapBodyresponse.getElementsByTagNameNS(serverURI, "cedis_virtual");
		NodeList vendor_id = soapBodyresponse.getElementsByTagNameNS(serverURI,"vendor_id");
		NodeList location = soapBodyresponse.getElementsByTagNameNS(serverURI,"location");
		NodeList location_type = soapBodyresponse.getElementsByTagNameNS(serverURI, "location_type");
		NodeList vendor_type = soapBodyresponse.getElementsByTagNameNS(serverURI, "vendor_type");
		

		CeDisResponse provid = new CeDisResponse();
		provid.setOrder_number(order_number.item(0).getTextContent());
		provid.setCedis_virtual(cedis_virtual.item(0).getTextContent());
		provid.setVendor_id(vendor_id.item(0).getTextContent());
		if (location.item(0).getTextContent() != null) {
			provid.setLocation(location.item(0).getTextContent());
		}
		if (location_type.item(0).getTextContent() != null) {
			provid.setLocation_type(location_type.item(0).getTextContent());
		}

		provid.setVendor_type(vendor_type.item(0).getTextContent());
		soapConnection.close();
		if (validarProvidencia(provid)) {
			return provid;
		} else
			return null;
	}

	public CeDisResponse llamadaPorDevolucion(String orderNumber) throws Exception {
		// Create SOAP Connection
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("urn", serverURI);

		SOAPHeader soapHead = envelope.getHeader();
		SOAPBody soapBody = envelope.getBody();

		SOAPElement soapBodyElem0 = soapBody.addChildElement("CeDisOrderRequest", "urn");//cambiar
		SOAPElement soapBodyElem1 = soapBodyElem0.addChildElement("order_number", "urn");//cambiar
		soapBodyElem1.addTextNode(orderNumber);
		soapBodyElem0.addChildElement(soapBodyElem1);
		soapMessage.saveChanges();

		SOAPMessage soapResponse = soapConnection.call(soapMessage, url);

		SOAPBody soapBodyresponse;
		try {
			soapBodyresponse = soapResponse.getSOAPBody();
			//soapResponse.writeTo(System.out);//para imprimir respuesta
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("SOAP Exception " + e1.toString());
			throw new Exception("SOAP Exception " + e1.toString());
		}
		// ejemplo de como obtener elemento a elemento
		NodeList order_number = soapBodyresponse.getElementsByTagNameNS(serverURI, "order_number");
		NodeList cedis_virtual = soapBodyresponse.getElementsByTagNameNS(serverURI, "cedis_virtual");
		NodeList vendor_id = soapBodyresponse.getElementsByTagNameNS(serverURI, "vendor_id");
		NodeList location = soapBodyresponse.getElementsByTagNameNS(serverURI, "location");
		NodeList location_type = soapBodyresponse.getElementsByTagNameNS(serverURI, "location_type");
		NodeList vendor_type = soapBodyresponse.getElementsByTagNameNS(serverURI, "vendor_type");
		
		CeDisResponse provid = new CeDisResponse();
		provid.setOrder_number(order_number.item(0).getTextContent());
		provid.setCedis_virtual(cedis_virtual.item(0).getTextContent());
		provid.setVendor_id(vendor_id.item(0).getTextContent());
		if (location.item(0).getTextContent() != null) {
			provid.setLocation(location.item(0).getTextContent());
		}
		if (location_type.item(0).getTextContent() != null) {
			provid.setLocation_type(location_type.item(0).getTextContent());
		}

		provid.setVendor_type(vendor_type.item(0).getTextContent());

		log.info("Fin MAPEO de campos _______________");
		soapConnection.close();
		if (validarProvidencia(provid)) {

			return provid;
		} else
			return null;
	}

	public CeDisResponse llamadaPorCodigoFactura(String codigoFactura)
			throws Exception {

		/**********************************/
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
				.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory
				.createConnection();

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// String serverURI = "urn:ec.gob.funcionjudicial:rematejudicial:1.0";

		// SOAP Envelope
		// serverURI =
		// "http://oracle.com/retail/integration/custom/bo/CeDisService/v1";
		// url =
		// "http://10.10.202.108:8001/soa-infra/services/rms/CeDisService/CeDisWS?WSDL";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("urn", serverURI);
		System.out.println(envelope.getNamespaceURI("urn"));
		SOAPHeader soapHead = envelope.getHeader();
		SOAPBody soapBody = envelope.getBody();
		// SOAPElement soapBodyElem =
		// soapBody.addChildElement("urn:generarDatosProvidencia");

		SOAPElement soapBodyElem0 = soapBody.addChildElement(
				"CeDisInvoiceRequest", "urn");
		SOAPElement soapBodyElem1 = soapBodyElem0.addChildElement(
				"invoice_number", "urn");
		soapBodyElem1.addTextNode(codigoFactura);
		soapBodyElem0.addChildElement(soapBodyElem1);
		soapMessage.saveChanges();
		try {
			soapBody = soapMessage.getSOAPBody();
		} catch (Exception e1) {
			System.out.println("\n\n --------------------- ");
			e1.printStackTrace();
			log.error("SOAP Exception " + e1.toString());
			throw new Exception("SOAP Exception " + e1.toString());
		}

		// ejemplo de como obtener elemento a elemento
		NodeList order_number = soapBody.getElementsByTagName("order_number");
		NodeList cedis_virtual = soapBody.getElementsByTagName("cedis_virtual");
		NodeList vendor_id = soapBody.getElementsByTagName("vendor_id");
		NodeList location = soapBody.getElementsByTagName("location");
		NodeList location_type = soapBody.getElementsByTagName("location_type");
		NodeList vendor_type = soapBody.getElementsByTagName("vendor_type");

		CeDisResponse provid = new CeDisResponse();
		provid.setOrder_number(order_number.item(0).getTextContent());
		provid.setCedis_virtual(cedis_virtual.item(0).getTextContent());
		provid.setVendor_id(vendor_id.item(0).getTextContent());
		provid.setLocation(location.item(0).getTextContent());
		provid.setLocation_type(location_type.item(0).getTextContent());
		provid.setVendor_type(vendor_type.item(0).getTextContent());

		log.debug("Fin MAPEO de campos _______________");
		soapConnection.close();
		if (validarProvidencia(provid))
			return provid;
		else
			return null;
	}

	public boolean validarProvidencia(CeDisResponse p) {

		if(p.getCedis_virtual().isEmpty() || p.getLocation().isEmpty() || 
				p.getLocation_type().isEmpty() || p.getOrder_number().isEmpty() ||
				p.getVendor_id().isEmpty() || p.getVendor_type().isEmpty()){
			log.info("Validacion Fallida de providencia ");
			return false;
		}

		return true;
	}

	public SOAPMessage createSOAPRequest(String idProvidencia, String user,
			String pass) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// String serverURI = "urn:ec.gob.funcionjudicial:rematejudicial:1.0";

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("urn", serverURI);

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyElem = soapBody
				.addChildElement("urn:generarDatosProvidencia");

		SOAPElement soapBodyElem0 = soapBodyElem.addChildElement("peticionDTO");
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("pass");
		soapBodyElem1.addTextNode(pass);
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("providencia");
		soapBodyElem2.addTextNode(idProvidencia);
		SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("usuario");
		soapBodyElem3.addTextNode(user);
		soapBodyElem0.addChildElement(soapBodyElem1);
		soapBodyElem0.addChildElement(soapBodyElem2);
		soapBodyElem0.addChildElement(soapBodyElem3);

		soapMessage.saveChanges();

		/* Print the request message */
		log.debug("Request SOAP Message:");
		soapMessage.writeTo(System.out);

		return soapMessage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getServerURI() {
		return serverURI;
	}

	public void setServerURI(String serverURI) {
		this.serverURI = serverURI;
	}

}
