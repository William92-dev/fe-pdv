
package com.gizlocorp.gnvoice.wsclient.notacredito;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gizlocorp.gnvoice.wsclient.notacredito package. 
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

    private final static QName _NotaCreditoConsultarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaCreditoConsultarRequest");
    private final static QName _NotaCreditoRecibirRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaCreditoRecibirRequest");
    private final static QName _NotaCreditoRecibirResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaCreditoRecibirResponse");
    private final static QName _NotaCreditoConsultarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaCreditoConsultarResponse");
    private final static QName _NotaCreditoProcesarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaCreditoProcesarResponse");
    private final static QName _NotaCreditoProcesarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaCreditoProcesarRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gizlocorp.gnvoice.wsclient.notacredito
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotaCredito }
     * 
     */
    public NotaCredito createNotaCredito() {
        return new NotaCredito();
    }

    /**
     * Create an instance of {@link TotalConImpuestos }
     * 
     */
    public TotalConImpuestos createTotalConImpuestos() {
        return new TotalConImpuestos();
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
     * Create an instance of {@link NotaCreditoConsultarResponse }
     * 
     */
    public NotaCreditoConsultarResponse createNotaCreditoConsultarResponse() {
        return new NotaCreditoConsultarResponse();
    }

    /**
     * Create an instance of {@link NotaCredito.InfoAdicional }
     * 
     */
    public NotaCredito.InfoAdicional createNotaCreditoInfoAdicional() {
        return new NotaCredito.InfoAdicional();
    }

    /**
     * Create an instance of {@link NotaCredito.Detalles }
     * 
     */
    public NotaCredito.Detalles createNotaCreditoDetalles() {
        return new NotaCredito.Detalles();
    }

    /**
     * Create an instance of {@link NotaCredito.Detalles.Detalle }
     * 
     */
    public NotaCredito.Detalles.Detalle createNotaCreditoDetallesDetalle() {
        return new NotaCredito.Detalles.Detalle();
    }

    /**
     * Create an instance of {@link NotaCredito.Detalles.Detalle.DetallesAdicionales }
     * 
     */
    public NotaCredito.Detalles.Detalle.DetallesAdicionales createNotaCreditoDetallesDetalleDetallesAdicionales() {
        return new NotaCredito.Detalles.Detalle.DetallesAdicionales();
    }

    /**
     * Create an instance of {@link InfoTributaria }
     * 
     */
    public InfoTributaria createInfoTributaria() {
        return new InfoTributaria();
    }

    /**
     * Create an instance of {@link NotaCredito.InfoNotaCredito }
     * 
     */
    public NotaCredito.InfoNotaCredito createNotaCreditoInfoNotaCredito() {
        return new NotaCredito.InfoNotaCredito();
    }

    /**
     * Create an instance of {@link NotaCreditoRecibirResponse }
     * 
     */
    public NotaCreditoRecibirResponse createNotaCreditoRecibirResponse() {
        return new NotaCreditoRecibirResponse();
    }

    /**
     * Create an instance of {@link NotaCreditoRecibirRequest }
     * 
     */
    public NotaCreditoRecibirRequest createNotaCreditoRecibirRequest() {
        return new NotaCreditoRecibirRequest();
    }

    /**
     * Create an instance of {@link NotaCreditoConsultarRequest }
     * 
     */
    public NotaCreditoConsultarRequest createNotaCreditoConsultarRequest() {
        return new NotaCreditoConsultarRequest();
    }

    /**
     * Create an instance of {@link TotalConImpuestos.TotalImpuesto }
     * 
     */
    public TotalConImpuestos.TotalImpuesto createTotalConImpuestosTotalImpuesto() {
        return new TotalConImpuestos.TotalImpuesto();
    }

    /**
     * Create an instance of {@link NotaCreditoProcesarRequest }
     * 
     */
    public NotaCreditoProcesarRequest createNotaCreditoProcesarRequest() {
        return new NotaCreditoProcesarRequest();
    }

    /**
     * Create an instance of {@link NotaCreditoProcesarResponse }
     * 
     */
    public NotaCreditoProcesarResponse createNotaCreditoProcesarResponse() {
        return new NotaCreditoProcesarResponse();
    }

    /**
     * Create an instance of {@link MensajeRespuesta }
     * 
     */
    public MensajeRespuesta createMensajeRespuesta() {
        return new MensajeRespuesta();
    }

    /**
     * Create an instance of {@link com.gizlocorp.gnvoice.wsclient.notacredito.Detalle }
     * 
     */
    public com.gizlocorp.gnvoice.wsclient.notacredito.Detalle createDetalle() {
        return new com.gizlocorp.gnvoice.wsclient.notacredito.Detalle();
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
     * Create an instance of {@link NotaCreditoConsultarResponse.NotasCredito }
     * 
     */
    public NotaCreditoConsultarResponse.NotasCredito createNotaCreditoConsultarResponseNotasCredito() {
        return new NotaCreditoConsultarResponse.NotasCredito();
    }

    /**
     * Create an instance of {@link NotaCredito.InfoAdicional.CampoAdicional }
     * 
     */
    public NotaCredito.InfoAdicional.CampoAdicional createNotaCreditoInfoAdicionalCampoAdicional() {
        return new NotaCredito.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link NotaCredito.Detalles.Detalle.Impuestos }
     * 
     */
    public NotaCredito.Detalles.Detalle.Impuestos createNotaCreditoDetallesDetalleImpuestos() {
        return new NotaCredito.Detalles.Detalle.Impuestos();
    }

    /**
     * Create an instance of {@link NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional }
     * 
     */
    public NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional createNotaCreditoDetallesDetalleDetallesAdicionalesDetAdicional() {
        return new NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaCreditoConsultarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaCreditoConsultarRequest")
    public JAXBElement<NotaCreditoConsultarRequest> createNotaCreditoConsultarRequest(NotaCreditoConsultarRequest value) {
        return new JAXBElement<NotaCreditoConsultarRequest>(_NotaCreditoConsultarRequest_QNAME, NotaCreditoConsultarRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaCreditoRecibirRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaCreditoRecibirRequest")
    public JAXBElement<NotaCreditoRecibirRequest> createNotaCreditoRecibirRequest(NotaCreditoRecibirRequest value) {
        return new JAXBElement<NotaCreditoRecibirRequest>(_NotaCreditoRecibirRequest_QNAME, NotaCreditoRecibirRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaCreditoRecibirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaCreditoRecibirResponse")
    public JAXBElement<NotaCreditoRecibirResponse> createNotaCreditoRecibirResponse(NotaCreditoRecibirResponse value) {
        return new JAXBElement<NotaCreditoRecibirResponse>(_NotaCreditoRecibirResponse_QNAME, NotaCreditoRecibirResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaCreditoConsultarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaCreditoConsultarResponse")
    public JAXBElement<NotaCreditoConsultarResponse> createNotaCreditoConsultarResponse(NotaCreditoConsultarResponse value) {
        return new JAXBElement<NotaCreditoConsultarResponse>(_NotaCreditoConsultarResponse_QNAME, NotaCreditoConsultarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaCreditoProcesarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaCreditoProcesarResponse")
    public JAXBElement<NotaCreditoProcesarResponse> createNotaCreditoProcesarResponse(NotaCreditoProcesarResponse value) {
        return new JAXBElement<NotaCreditoProcesarResponse>(_NotaCreditoProcesarResponse_QNAME, NotaCreditoProcesarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaCreditoProcesarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaCreditoProcesarRequest")
    public JAXBElement<NotaCreditoProcesarRequest> createNotaCreditoProcesarRequest(NotaCreditoProcesarRequest value) {
        return new JAXBElement<NotaCreditoProcesarRequest>(_NotaCreditoProcesarRequest_QNAME, NotaCreditoProcesarRequest.class, null, value);
    }

}
