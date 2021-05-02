package ec.gob.sri.comprobantes.ws.aut;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "AutorizacionComprobantes", targetNamespace = "http://ec.gob.sri.ws.autorizacion")
@XmlSeeAlso({ ObjectFactory.class })
public abstract interface AutorizacionComprobantes {
	@WebMethod
	@WebResult(name = "RespuestaAutorizacionComprobante", targetNamespace = "")
	@RequestWrapper(localName = "autorizacionComprobante", targetNamespace = "http://ec.gob.sri.ws.autorizacion", className = "ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobante")
	@ResponseWrapper(localName = "autorizacionComprobanteResponse", targetNamespace = "http://ec.gob.sri.ws.autorizacion", className = "ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobanteResponse")
	public abstract RespuestaComprobante autorizacionComprobante(
			@WebParam(name = "claveAccesoComprobante", targetNamespace = "") String paramString);

	@WebMethod
	@WebResult(name = "RespuestaAutorizacionLote", targetNamespace = "")
	@RequestWrapper(localName = "autorizacionComprobanteLote", targetNamespace = "http://ec.gob.sri.ws.autorizacion", className = "ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobanteLote")
	@ResponseWrapper(localName = "autorizacionComprobanteLoteResponse", targetNamespace = "http://ec.gob.sri.ws.autorizacion", className = "ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobanteLoteResponse")
	public abstract RespuestaLote autorizacionComprobanteLote(
			@WebParam(name = "claveAccesoLote", targetNamespace = "") String paramString);
}

/*
 * Location: C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * 
 * Qualified Name: ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes
 * 
 * JD-Core Version: 0.7.0.1
 */