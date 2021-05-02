
package ec.gob.sri.comprobantes.ws.offline.rec;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "RecepcionComprobantesOffline", targetNamespace = "http://ec.gob.sri.ws.recepcion")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface RecepcionComprobantesOffline {


    /**
     * 
     * @param xml
     * @return
     *     returns recepcion.ws.sri.gob.ec.RespuestaSolicitud
     */
    @WebMethod
    @WebResult(name = "RespuestaRecepcionComprobante", targetNamespace = "")
    @RequestWrapper(localName = "validarComprobante", targetNamespace = "http://ec.gob.sri.ws.recepcion", className = "ec.gob.sri.comprobantes.ws.offline.rec.ValidarComprobante")
    @ResponseWrapper(localName = "validarComprobanteResponse", targetNamespace = "http://ec.gob.sri.ws.recepcion", className = "ec.gob.sri.comprobantes.ws.offline.rec.ValidarComprobanteResponse")
    public RespuestaSolicitud validarComprobante(
        @WebParam(name = "xml", targetNamespace = "")
        byte[] xml);

}
