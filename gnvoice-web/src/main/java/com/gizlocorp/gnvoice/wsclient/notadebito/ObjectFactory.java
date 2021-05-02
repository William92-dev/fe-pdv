
package com.gizlocorp.gnvoice.wsclient.notadebito;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gizlocorp.gnvoice.wsclient.notadebito package. 
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

    private final static QName _NotaDebitoRecibirResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaDebitoRecibirResponse");
    private final static QName _NotaDebitoConsultarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaDebitoConsultarResponse");
    private final static QName _NotaDebitoProcesarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaDebitoProcesarRequest");
    private final static QName _NotaDebitoProcesarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaDebitoProcesarResponse");
    private final static QName _NotaDebitoConsultarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "notaDebitoConsultarRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gizlocorp.gnvoice.wsclient.notadebito
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotaDebito }
     * 
     */
    public NotaDebito createNotaDebito() {
        return new NotaDebito();
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
     * Create an instance of {@link NotaDebito.InfoAdicional }
     * 
     */
    public NotaDebito.InfoAdicional createNotaDebitoInfoAdicional() {
        return new NotaDebito.InfoAdicional();
    }

    /**
     * Create an instance of {@link NotaDebito.Motivos }
     * 
     */
    public NotaDebito.Motivos createNotaDebitoMotivos() {
        return new NotaDebito.Motivos();
    }

    /**
     * Create an instance of {@link NotaDebito.InfoNotaDebito }
     * 
     */
    public NotaDebito.InfoNotaDebito createNotaDebitoInfoNotaDebito() {
        return new NotaDebito.InfoNotaDebito();
    }

    /**
     * Create an instance of {@link NotaDebitoConsultarResponse }
     * 
     */
    public NotaDebitoConsultarResponse createNotaDebitoConsultarResponse() {
        return new NotaDebitoConsultarResponse();
    }

    /**
     * Create an instance of {@link NotaDebitoRecibirResponse }
     * 
     */
    public NotaDebitoRecibirResponse createNotaDebitoRecibirResponse() {
        return new NotaDebitoRecibirResponse();
    }

    /**
     * Create an instance of {@link InfoTributaria }
     * 
     */
    public InfoTributaria createInfoTributaria() {
        return new InfoTributaria();
    }

    /**
     * Create an instance of {@link NotaDebitoConsultarRequest }
     * 
     */
    public NotaDebitoConsultarRequest createNotaDebitoConsultarRequest() {
        return new NotaDebitoConsultarRequest();
    }

    /**
     * Create an instance of {@link MensajeRespuesta }
     * 
     */
    public MensajeRespuesta createMensajeRespuesta() {
        return new MensajeRespuesta();
    }

    /**
     * Create an instance of {@link NotaDebitoProcesarResponse }
     * 
     */
    public NotaDebitoProcesarResponse createNotaDebitoProcesarResponse() {
        return new NotaDebitoProcesarResponse();
    }

    /**
     * Create an instance of {@link NotaDebitoProcesarRequest }
     * 
     */
    public NotaDebitoProcesarRequest createNotaDebitoProcesarRequest() {
        return new NotaDebitoProcesarRequest();
    }

    /**
     * Create an instance of {@link Detalle }
     * 
     */
    public Detalle createDetalle() {
        return new Detalle();
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
     * Create an instance of {@link NotaDebito.InfoAdicional.CampoAdicional }
     * 
     */
    public NotaDebito.InfoAdicional.CampoAdicional createNotaDebitoInfoAdicionalCampoAdicional() {
        return new NotaDebito.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link NotaDebito.Motivos.Motivo }
     * 
     */
    public NotaDebito.Motivos.Motivo createNotaDebitoMotivosMotivo() {
        return new NotaDebito.Motivos.Motivo();
    }

    /**
     * Create an instance of {@link NotaDebito.InfoNotaDebito.Impuestos }
     * 
     */
    public NotaDebito.InfoNotaDebito.Impuestos createNotaDebitoInfoNotaDebitoImpuestos() {
        return new NotaDebito.InfoNotaDebito.Impuestos();
    }

    /**
     * Create an instance of {@link NotaDebitoConsultarResponse.NotasDebito }
     * 
     */
    public NotaDebitoConsultarResponse.NotasDebito createNotaDebitoConsultarResponseNotasDebito() {
        return new NotaDebitoConsultarResponse.NotasDebito();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaDebitoRecibirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaDebitoRecibirResponse")
    public JAXBElement<NotaDebitoRecibirResponse> createNotaDebitoRecibirResponse(NotaDebitoRecibirResponse value) {
        return new JAXBElement<NotaDebitoRecibirResponse>(_NotaDebitoRecibirResponse_QNAME, NotaDebitoRecibirResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaDebitoConsultarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaDebitoConsultarResponse")
    public JAXBElement<NotaDebitoConsultarResponse> createNotaDebitoConsultarResponse(NotaDebitoConsultarResponse value) {
        return new JAXBElement<NotaDebitoConsultarResponse>(_NotaDebitoConsultarResponse_QNAME, NotaDebitoConsultarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaDebitoProcesarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaDebitoProcesarRequest")
    public JAXBElement<NotaDebitoProcesarRequest> createNotaDebitoProcesarRequest(NotaDebitoProcesarRequest value) {
        return new JAXBElement<NotaDebitoProcesarRequest>(_NotaDebitoProcesarRequest_QNAME, NotaDebitoProcesarRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaDebitoProcesarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaDebitoProcesarResponse")
    public JAXBElement<NotaDebitoProcesarResponse> createNotaDebitoProcesarResponse(NotaDebitoProcesarResponse value) {
        return new JAXBElement<NotaDebitoProcesarResponse>(_NotaDebitoProcesarResponse_QNAME, NotaDebitoProcesarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotaDebitoConsultarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "notaDebitoConsultarRequest")
    public JAXBElement<NotaDebitoConsultarRequest> createNotaDebitoConsultarRequest(NotaDebitoConsultarRequest value) {
        return new JAXBElement<NotaDebitoConsultarRequest>(_NotaDebitoConsultarRequest_QNAME, NotaDebitoConsultarRequest.class, null, value);
    }

}
