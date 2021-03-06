package com.gizlocorp.gnvoice.wsclient.retencion;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.7.7.redhat-1
 * 2014-08-05T18:24:41.263-05:00
 * Generated source version: 2.7.7.redhat-1
 * 
 */
@WebService(targetNamespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "RetencionPortType")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface RetencionPortType {

    @WebResult(name = "retencionRecibirResponse", targetNamespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", partName = "retencionRecibirResponse")
    @WebMethod(action = "recibir")
    public RetencionRecibirResponse recibir(
        @WebParam(partName = "retencionRecibirRequest", name = "retencionRecibirRequest", targetNamespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
        RetencionRecibirRequest retencionRecibirRequest
    );

    @WebResult(name = "retencionProcesarResponse", targetNamespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", partName = "retencionProcesarResponse")
    @WebMethod(action = "procesar")
    public RetencionProcesarResponse procesar(
        @WebParam(partName = "retencionProcesarRequest", name = "retencionProcesarRequest", targetNamespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
        RetencionProcesarRequest retencionProcesarRequest
    );

    @WebResult(name = "retencionConsultarResponse", targetNamespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", partName = "retencionConsultarResponse")
    @WebMethod(action = "consultar")
    public RetencionConsultarResponse consultar(
        @WebParam(partName = "retencionConsultarRequest", name = "retencionConsultarRequest", targetNamespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0")
        RetencionConsultarRequest retencionConsultarRequest
    );
}
