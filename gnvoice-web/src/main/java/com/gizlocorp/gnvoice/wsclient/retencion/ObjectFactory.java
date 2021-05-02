
package com.gizlocorp.gnvoice.wsclient.retencion;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gizlocorp.gnvoice.wsclient.retencion package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RetencionConsultarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "retencionConsultarRequest");
    private final static QName _RetencionProcesarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "retencionProcesarRequest");
    private final static QName _RetencionConsultarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "retencionConsultarResponse");
    private final static QName _RetencionProcesarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "retencionProcesarResponse");
    private final static QName _RetencionRecibirRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "retencionRecibirRequest");
    private final static QName _RetencionRecibirResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "retencionRecibirResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gizlocorp.gnvoice.wsclient.retencion
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ComprobanteRetencion }
     * 
     */
    public ComprobanteRetencion createComprobanteRetencion() {
        return new ComprobanteRetencion();
    }

    /**
     * Create an instance of {@link GizloResponse }
     * 
     */
    public GizloResponse createGizloResponse() {
        return new GizloResponse();
    }

    /**
     * Create an instance of {@link GizloResponse.Mensajes }
     * 
     */
    public GizloResponse.Mensajes createGizloResponseMensajes() {
        return new GizloResponse.Mensajes();
    }

    /**
     * Create an instance of {@link RetencionConsultarResponse }
     * 
     */
    public RetencionConsultarResponse createRetencionConsultarResponse() {
        return new RetencionConsultarResponse();
    }

    /**
     * Create an instance of {@link RetencionConsultarResponse.Rentenciones }
     * 
     */
    public RetencionConsultarResponse.Rentenciones createRetencionConsultarResponseRentenciones() {
        return new RetencionConsultarResponse.Rentenciones();
    }

    /**
     * Create an instance of {@link RetencionConsultarResponse.Rentenciones.Rentencion }
     * 
     */
    public RetencionConsultarResponse.Rentenciones.Rentencion createRetencionConsultarResponseRentencionesRentencion() {
        return new RetencionConsultarResponse.Rentenciones.Rentencion();
    }

    /**
     * Create an instance of {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional }
     * 
     */
    public RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional createRetencionConsultarResponseRentencionesRentencionInfoAdicional() {
        return new RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional();
    }

    /**
     * Create an instance of {@link ComprobanteRetencion.InfoAdicional }
     * 
     */
    public ComprobanteRetencion.InfoAdicional createComprobanteRetencionInfoAdicional() {
        return new ComprobanteRetencion.InfoAdicional();
    }

    /**
     * Create an instance of {@link RetencionConsultarRequest }
     * 
     */
    public RetencionConsultarRequest createRetencionConsultarRequest() {
        return new RetencionConsultarRequest();
    }

    /**
     * Create an instance of {@link RetencionConsultarRequest.Rentencion }
     * 
     */
    public RetencionConsultarRequest.Rentencion createRetencionConsultarRequestRentencion() {
        return new RetencionConsultarRequest.Rentencion();
    }

    /**
     * Create an instance of {@link RetencionConsultarRequest.Rentencion.InfoAdicional }
     * 
     */
    public RetencionConsultarRequest.Rentencion.InfoAdicional createRetencionConsultarRequestRentencionInfoAdicional() {
        return new RetencionConsultarRequest.Rentencion.InfoAdicional();
    }

    /**
     * Create an instance of {@link RetencionProcesarRequest }
     * 
     */
    public RetencionProcesarRequest createRetencionProcesarRequest() {
        return new RetencionProcesarRequest();
    }

    /**
     * Create an instance of {@link RetencionProcesarRequest.Rentencion }
     * 
     */
    public RetencionProcesarRequest.Rentencion createRetencionProcesarRequestRentencion() {
        return new RetencionProcesarRequest.Rentencion();
    }

    /**
     * Create an instance of {@link RetencionProcesarRequest.Rentencion.InfoAdicional }
     * 
     */
    public RetencionProcesarRequest.Rentencion.InfoAdicional createRetencionProcesarRequestRentencionInfoAdicional() {
        return new RetencionProcesarRequest.Rentencion.InfoAdicional();
    }

    /**
     * Create an instance of {@link RetencionRecibirRequest }
     * 
     */
    public RetencionRecibirRequest createRetencionRecibirRequest() {
        return new RetencionRecibirRequest();
    }

    /**
     * Create an instance of {@link InfoTributaria }
     * 
     */
    public InfoTributaria createInfoTributaria() {
        return new InfoTributaria();
    }

    /**
     * Create an instance of {@link ComprobanteRetencion.InfoCompRetencion }
     * 
     */
    public ComprobanteRetencion.InfoCompRetencion createComprobanteRetencionInfoCompRetencion() {
        return new ComprobanteRetencion.InfoCompRetencion();
    }

    /**
     * Create an instance of {@link ComprobanteRetencion.Impuestos }
     * 
     */
    public ComprobanteRetencion.Impuestos createComprobanteRetencionImpuestos() {
        return new ComprobanteRetencion.Impuestos();
    }

    /**
     * Create an instance of {@link RetencionRecibirResponse }
     * 
     */
    public RetencionRecibirResponse createRetencionRecibirResponse() {
        return new RetencionRecibirResponse();
    }

    /**
     * Create an instance of {@link MensajeRespuesta }
     * 
     */
    public MensajeRespuesta createMensajeRespuesta() {
        return new MensajeRespuesta();
    }

    /**
     * Create an instance of {@link RetencionProcesarResponse }
     * 
     */
    public RetencionProcesarResponse createRetencionProcesarResponse() {
        return new RetencionProcesarResponse();
    }

    /**
     * Create an instance of {@link Impuesto }
     * 
     */
    public Impuesto createImpuesto() {
        return new Impuesto();
    }

    /**
     * Create an instance of {@link GizloResponse.Mensajes.Mensaje }
     * 
     */
    public GizloResponse.Mensajes.Mensaje createGizloResponseMensajesMensaje() {
        return new GizloResponse.Mensajes.Mensaje();
    }

    /**
     * Create an instance of {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion }
     * 
     */
    public RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion createRetencionConsultarResponseRentencionesRentencionInfoCompRetencion() {
        return new RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion();
    }

    /**
     * Create an instance of {@link RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos }
     * 
     */
    public RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos createRetencionConsultarResponseRentencionesRentencionImpuestos() {
        return new RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos();
    }

    /**
     * Create an instance of {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional.CampoAdicional }
     * 
     */
    public RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional.CampoAdicional createRetencionConsultarResponseRentencionesRentencionInfoAdicionalCampoAdicional() {
        return new RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link ComprobanteRetencion.InfoAdicional.CampoAdicional }
     * 
     */
    public ComprobanteRetencion.InfoAdicional.CampoAdicional createComprobanteRetencionInfoAdicionalCampoAdicional() {
        return new ComprobanteRetencion.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link RetencionConsultarRequest.Rentencion.InfoCompRetencion }
     * 
     */
    public RetencionConsultarRequest.Rentencion.InfoCompRetencion createRetencionConsultarRequestRentencionInfoCompRetencion() {
        return new RetencionConsultarRequest.Rentencion.InfoCompRetencion();
    }

    /**
     * Create an instance of {@link RetencionConsultarRequest.Rentencion.Impuestos }
     * 
     */
    public RetencionConsultarRequest.Rentencion.Impuestos createRetencionConsultarRequestRentencionImpuestos() {
        return new RetencionConsultarRequest.Rentencion.Impuestos();
    }

    /**
     * Create an instance of {@link RetencionConsultarRequest.Rentencion.InfoAdicional.CampoAdicional }
     * 
     */
    public RetencionConsultarRequest.Rentencion.InfoAdicional.CampoAdicional createRetencionConsultarRequestRentencionInfoAdicionalCampoAdicional() {
        return new RetencionConsultarRequest.Rentencion.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link RetencionProcesarRequest.Rentencion.InfoCompRetencion }
     * 
     */
    public RetencionProcesarRequest.Rentencion.InfoCompRetencion createRetencionProcesarRequestRentencionInfoCompRetencion() {
        return new RetencionProcesarRequest.Rentencion.InfoCompRetencion();
    }

    /**
     * Create an instance of {@link RetencionProcesarRequest.Rentencion.Impuestos }
     * 
     */
    public RetencionProcesarRequest.Rentencion.Impuestos createRetencionProcesarRequestRentencionImpuestos() {
        return new RetencionProcesarRequest.Rentencion.Impuestos();
    }

    /**
     * Create an instance of {@link RetencionProcesarRequest.Rentencion.InfoAdicional.CampoAdicional }
     * 
     */
    public RetencionProcesarRequest.Rentencion.InfoAdicional.CampoAdicional createRetencionProcesarRequestRentencionInfoAdicionalCampoAdicional() {
        return new RetencionProcesarRequest.Rentencion.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetencionConsultarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "retencionConsultarRequest")
    public JAXBElement<RetencionConsultarRequest> createRetencionConsultarRequest(RetencionConsultarRequest value) {
        return new JAXBElement<RetencionConsultarRequest>(_RetencionConsultarRequest_QNAME, RetencionConsultarRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetencionProcesarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "retencionProcesarRequest")
    public JAXBElement<RetencionProcesarRequest> createRetencionProcesarRequest(RetencionProcesarRequest value) {
        return new JAXBElement<RetencionProcesarRequest>(_RetencionProcesarRequest_QNAME, RetencionProcesarRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetencionConsultarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "retencionConsultarResponse")
    public JAXBElement<RetencionConsultarResponse> createRetencionConsultarResponse(RetencionConsultarResponse value) {
        return new JAXBElement<RetencionConsultarResponse>(_RetencionConsultarResponse_QNAME, RetencionConsultarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetencionProcesarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "retencionProcesarResponse")
    public JAXBElement<RetencionProcesarResponse> createRetencionProcesarResponse(RetencionProcesarResponse value) {
        return new JAXBElement<RetencionProcesarResponse>(_RetencionProcesarResponse_QNAME, RetencionProcesarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetencionRecibirRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "retencionRecibirRequest")
    public JAXBElement<RetencionRecibirRequest> createRetencionRecibirRequest(RetencionRecibirRequest value) {
        return new JAXBElement<RetencionRecibirRequest>(_RetencionRecibirRequest_QNAME, RetencionRecibirRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetencionRecibirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "retencionRecibirResponse")
    public JAXBElement<RetencionRecibirResponse> createRetencionRecibirResponse(RetencionRecibirResponse value) {
        return new JAXBElement<RetencionRecibirResponse>(_RetencionRecibirResponse_QNAME, RetencionRecibirResponse.class, null, value);
    }

}
