package ec.gob.sri.comprobantes.ws.aut;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "AutorizacionComprobantesService", targetNamespace = "http://ec.gob.sri.ws.autorizacion", wsdlLocation = "/META-INF/wsdl/AutorizacionComprobantes.wsdl")
// @HandlerChain(file = "handler-chain.xml")
public class AutorizacionComprobantesService

/* 13: */extends Service
/* 14: */{
	/* 15: */private static final URL AUTORIZACIONCOMPROBANTESSERVICE_WSDL_LOCATION;
	/* 16: */private static final WebServiceException AUTORIZACIONCOMPROBANTESSERVICE_EXCEPTION;
	/* 17:26 */private static final QName AUTORIZACIONCOMPROBANTESSERVICE_QNAME = new QName(
			"http://ec.gob.sri.ws.autorizacion",
			"AutorizacionComprobantesService");
	/* 18: */
	/* 19: */static
	/* 20: */{
		/* 21:29 */AUTORIZACIONCOMPROBANTESSERVICE_WSDL_LOCATION = AutorizacionComprobantesService.class
				.getResource("/META-INF/wsdl/AutorizacionComprobantes.wsdl");
		/* 22:30 */WebServiceException e = null;
		/* 23:31 */if (AUTORIZACIONCOMPROBANTESSERVICE_WSDL_LOCATION == null) {
			/* 24:32 */e = new WebServiceException(
					"Cannot find '/META-INF/wsdl/AutorizacionComprobantes.wsdl' wsdl. Place the resource correctly in the classpath.");
			/* 25: */}
		/* 26:34 */AUTORIZACIONCOMPROBANTESSERVICE_EXCEPTION = e;
		/* 27: */}

	/* 28: */
	/* 29: */public AutorizacionComprobantesService()
	/* 30: */{
		/* 31:38 */super(__getWsdlLocation(),
				AUTORIZACIONCOMPROBANTESSERVICE_QNAME);
		/* 32: */}

	/* 33: */
	/* 34: */public AutorizacionComprobantesService(URL wsdlLocation)
	/* 35: */{
		/* 36:42 */super(wsdlLocation, AUTORIZACIONCOMPROBANTESSERVICE_QNAME);
		/* 37: */}

	/* 38: */
	/* 39: */public AutorizacionComprobantesService(URL wsdlLocation,
			QName serviceName)
	/* 40: */{
		/* 41:46 */super(wsdlLocation, serviceName);
		/* 42: */}

	/* 43: */
	/* 44: */@WebEndpoint(name = "AutorizacionComprobantesPort")
	/* 45: */public AutorizacionComprobantes getAutorizacionComprobantesPort()
	/* 46: */{
		/* 47:56 */return (AutorizacionComprobantes) super
				.getPort(new QName("http://ec.gob.sri.ws.autorizacion",
						"AutorizacionComprobantesPort"),
						AutorizacionComprobantes.class);
		/* 48: */}

	/* 49: */
	/* 50: */@WebEndpoint(name = "AutorizacionComprobantesPort")
	/* 51: */public AutorizacionComprobantes getAutorizacionComprobantesPort(
			WebServiceFeature... features)
	/* 52: */{
		/* 53:68 */return (AutorizacionComprobantes) super.getPort(new QName(
				"http://ec.gob.sri.ws.autorizacion",
				"AutorizacionComprobantesPort"),
				AutorizacionComprobantes.class, features);
		/* 54: */}

	/* 55: */
	/* 56: */private static URL __getWsdlLocation()
	/* 57: */{
		/* 58:72 */if (AUTORIZACIONCOMPROBANTESSERVICE_EXCEPTION != null) {
			/* 59:73 */throw AUTORIZACIONCOMPROBANTESSERVICE_EXCEPTION;
			/* 60: */}
		/* 61:75 */return AUTORIZACIONCOMPROBANTESSERVICE_WSDL_LOCATION;
		/* 62: */}
	/* 63: */
}

/*
 * Location: C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * 
 * Qualified Name:
 * ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService
 * 
 * JD-Core Version: 0.7.0.1
 */