package ec.gob.sri.comprobantes.ws.aut;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;

public class CDataHandler implements SOAPHandler<SOAPMessageContext> {

	public static final Logger log = Logger.getLogger(CDataHandler.class);

	@Override
	public boolean handleMessage(SOAPMessageContext context) {

		Boolean isRequest = (Boolean) context
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		// if this is a request, true for outbound messages, false for inbound
		if (!isRequest) {

			try {
				SOAPMessage soapMsg = context.getMessage();
				SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
				SOAPHeader soapHeader = soapEnv.getHeader();

				// if no header, add one
				if (soapHeader == null) {
					soapHeader = soapEnv.addHeader();
				}

				/*
				 * SOAPBody soapBody = soapEnv.getBody();
				 * 
				 * if (soapBody.getChildNodes() != null &&
				 * soapBody.getChildNodes().getLength() > 0 &&
				 * soapBody.getChildNodes().item(0) != null &&
				 * soapBody.getChildNodes().item(0).getChildNodes() .getLength()
				 * > 0 && soapBody.getChildNodes().item(0).getChildNodes()
				 * .item(0) != null &&
				 * soapBody.getChildNodes().item(0).getChildNodes()
				 * .item(0).getChildNodes() != null &&
				 * soapBody.getChildNodes().item(0).getChildNodes()
				 * .item(0).getChildNodes().getLength() > 0) { for (int i = 0; i
				 * < soapBody.getChildNodes().item(0)
				 * .getChildNodes().item(0).getChildNodes() .getLength(); i++) {
				 * log.debug(soapBody.getChildNodes().item(0)
				 * .getChildNodes().item(0).getChildNodes()
				 * .item(i).getNodeName()); if
				 * ("autorizaciones".equals(soapBody.getChildNodes()
				 * .item(0).getChildNodes().item(0)
				 * .getChildNodes().item(i).getNodeName()) &&
				 * soapBody.getChildNodes().item(0) .getChildNodes().item(0)
				 * .getChildNodes().item(i) .getChildNodes() != null &&
				 * soapBody.getChildNodes().item(0) .getChildNodes().item(0)
				 * .getChildNodes().item(i) .getChildNodes().getLength() > 0) {
				 * for (int j = 0; j < soapBody.getChildNodes()
				 * .item(0).getChildNodes().item(0)
				 * .getChildNodes().item(i).getChildNodes() .getLength(); j++) {
				 * log.debug(soapBody.getChildNodes()
				 * .item(0).getChildNodes().item(0) .getChildNodes().item(i)
				 * .getChildNodes().item(j).getNodeName()); if
				 * ("autorizacion".equals(soapBody .getChildNodes().item(0)
				 * .getChildNodes().item(0) .getChildNodes().item(i)
				 * .getChildNodes().item(j).getNodeName()) &&
				 * soapBody.getChildNodes().item(0) .getChildNodes().item(0)
				 * .getChildNodes().item(i) .getChildNodes().item(j)
				 * .getChildNodes() != null && soapBody.getChildNodes().item(0)
				 * .getChildNodes().item(0) .getChildNodes().item(i)
				 * .getChildNodes().item(j) .getChildNodes().getLength() > 0) {
				 * for (int k = 0; k < soapBody .getChildNodes().item(0)
				 * .getChildNodes().item(0) .getChildNodes().item(i)
				 * .getChildNodes().item(j) .getChildNodes().getLength(); k++) {
				 * log.debug(soapBody .getChildNodes().item(0)
				 * .getChildNodes().item(0) .getChildNodes().item(i)
				 * .getChildNodes().item(j) .getChildNodes().item(k)
				 * .getNodeName()); if ("comprobante".equals(soapBody
				 * .getChildNodes().item(0) .getChildNodes().item(0)
				 * .getChildNodes().item(i) .getChildNodes().item(j)
				 * .getChildNodes().item(k) .getNodeName())) { // TODO grabar
				 * comprobante soapBody.getChildNodes() .item(0)
				 * .getChildNodes() .item(0) .getChildNodes() .item(i)
				 * .getChildNodes() .item(j) .removeChild(
				 * soapBody.getChildNodes() .item(0) .getChildNodes() .item(0)
				 * .getChildNodes() .item(i) .getChildNodes() .item(j)
				 * .getChildNodes() .item(k));
				 * 
				 * }
				 * 
				 * }
				 * 
				 * } }
				 * 
				 * }
				 * 
				 * }
				 * 
				 * }
				 */
			} catch (SOAPException e) {
				log.debug(e);
			}

		}

		// continue other handler chain
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		log.debug("Client : handleFault()......");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		log.debug("Client : close()......");
	}

	@Override
	public Set<QName> getHeaders() {
		log.debug("Client : getHeaders()......");
		return null;
	}

}
