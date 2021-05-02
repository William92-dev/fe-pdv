package ec.gob.sri.comprobantes.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "RecepcionComprobantes", targetNamespace = "http://ec.gob.sri.ws.recepcion")
@XmlSeeAlso({ ObjectFactory.class })
public abstract interface RecepcionComprobantes {
	@WebMethod
	@WebResult(name = "RespuestaRecepcionComprobante", targetNamespace = "")
	@RequestWrapper(localName = "validarComprobante", targetNamespace = "http://ec.gob.sri.ws.recepcion", className = "ec.gob.sri.comprobantes.ws.ValidarComprobante")
	@ResponseWrapper(localName = "validarComprobanteResponse", targetNamespace = "http://ec.gob.sri.ws.recepcion", className = "ec.gob.sri.comprobantes.ws.ValidarComprobanteResponse")
	public abstract RespuestaSolicitud validarComprobante(
			@WebParam(name = "xml", targetNamespace = "") byte[] paramArrayOfByte);
}

/*
 * Location: C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * 
 * Qualified Name: ec.gob.sri.comprobantes.ws.RecepcionComprobantes
 * 
 * JD-Core Version: 0.7.0.1
 */