
package com.gizlocorp.gnvoice.wsclient.guia;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gizlocorp.gnvoice.wsclient.guia package. 
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

    private final static QName _GuiaConsultarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "guiaConsultarResponse");
    private final static QName _GuiaRecibirResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "guiaRecibirResponse");
    private final static QName _GuiaProcesarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "guiaProcesarRequest");
    private final static QName _GuiaProcesarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "guiaProcesarResponse");
    private final static QName _GuiaRecibirRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "guiaRecibirRequest");
    private final static QName _GuiaConsultarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "guiaConsultarRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gizlocorp.gnvoice.wsclient.guia
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GuiaRemision }
     * 
     */
    public GuiaRemision createGuiaRemision() {
        return new GuiaRemision();
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
     * Create an instance of {@link Detalle }
     * 
     */
    public Detalle createDetalle() {
        return new Detalle();
    }

    /**
     * Create an instance of {@link Detalle.DetallesAdicionales }
     * 
     */
    public Detalle.DetallesAdicionales createDetalleDetallesAdicionales() {
        return new Detalle.DetallesAdicionales();
    }

    /**
     * Create an instance of {@link Destinatario }
     * 
     */
    public Destinatario createDestinatario() {
        return new Destinatario();
    }

    /**
     * Create an instance of {@link GuiaRemision.InfoAdicional }
     * 
     */
    public GuiaRemision.InfoAdicional createGuiaRemisionInfoAdicional() {
        return new GuiaRemision.InfoAdicional();
    }

    /**
     * Create an instance of {@link GuiaConsultarRequest }
     * 
     */
    public GuiaConsultarRequest createGuiaConsultarRequest() {
        return new GuiaConsultarRequest();
    }

    /**
     * Create an instance of {@link GuiaConsultarRequest.Guia }
     * 
     */
    public GuiaConsultarRequest.Guia createGuiaConsultarRequestGuia() {
        return new GuiaConsultarRequest.Guia();
    }

    /**
     * Create an instance of {@link GuiaConsultarRequest.Guia.InfoAdicional }
     * 
     */
    public GuiaConsultarRequest.Guia.InfoAdicional createGuiaConsultarRequestGuiaInfoAdicional() {
        return new GuiaConsultarRequest.Guia.InfoAdicional();
    }

    /**
     * Create an instance of {@link GuiaConsultarResponse }
     * 
     */
    public GuiaConsultarResponse createGuiaConsultarResponse() {
        return new GuiaConsultarResponse();
    }

    /**
     * Create an instance of {@link GuiaConsultarResponse.Guias }
     * 
     */
    public GuiaConsultarResponse.Guias createGuiaConsultarResponseGuias() {
        return new GuiaConsultarResponse.Guias();
    }

    /**
     * Create an instance of {@link GuiaConsultarResponse.Guias.Guia }
     * 
     */
    public GuiaConsultarResponse.Guias.Guia createGuiaConsultarResponseGuiasGuia() {
        return new GuiaConsultarResponse.Guias.Guia();
    }

    /**
     * Create an instance of {@link GuiaConsultarResponse.Guias.Guia.InfoAdicional }
     * 
     */
    public GuiaConsultarResponse.Guias.Guia.InfoAdicional createGuiaConsultarResponseGuiasGuiaInfoAdicional() {
        return new GuiaConsultarResponse.Guias.Guia.InfoAdicional();
    }

    /**
     * Create an instance of {@link GuiaProcesarRequest }
     * 
     */
    public GuiaProcesarRequest createGuiaProcesarRequest() {
        return new GuiaProcesarRequest();
    }

    /**
     * Create an instance of {@link GuiaProcesarRequest.Guia }
     * 
     */
    public GuiaProcesarRequest.Guia createGuiaProcesarRequestGuia() {
        return new GuiaProcesarRequest.Guia();
    }

    /**
     * Create an instance of {@link GuiaProcesarRequest.Guia.InfoAdicional }
     * 
     */
    public GuiaProcesarRequest.Guia.InfoAdicional createGuiaProcesarRequestGuiaInfoAdicional() {
        return new GuiaProcesarRequest.Guia.InfoAdicional();
    }

    /**
     * Create an instance of {@link GuiaProcesarResponse }
     * 
     */
    public GuiaProcesarResponse createGuiaProcesarResponse() {
        return new GuiaProcesarResponse();
    }

    /**
     * Create an instance of {@link GuiaRecibirResponse }
     * 
     */
    public GuiaRecibirResponse createGuiaRecibirResponse() {
        return new GuiaRecibirResponse();
    }

    /**
     * Create an instance of {@link MensajeRespuesta }
     * 
     */
    public MensajeRespuesta createMensajeRespuesta() {
        return new MensajeRespuesta();
    }

    /**
     * Create an instance of {@link InfoTributaria }
     * 
     */
    public InfoTributaria createInfoTributaria() {
        return new InfoTributaria();
    }

    /**
     * Create an instance of {@link GuiaRemision.InfoGuiaRemision }
     * 
     */
    public GuiaRemision.InfoGuiaRemision createGuiaRemisionInfoGuiaRemision() {
        return new GuiaRemision.InfoGuiaRemision();
    }

    /**
     * Create an instance of {@link GuiaRemision.Destinatarios }
     * 
     */
    public GuiaRemision.Destinatarios createGuiaRemisionDestinatarios() {
        return new GuiaRemision.Destinatarios();
    }

    /**
     * Create an instance of {@link GuiaRecibirRequest }
     * 
     */
    public GuiaRecibirRequest createGuiaRecibirRequest() {
        return new GuiaRecibirRequest();
    }

    /**
     * Create an instance of {@link GizloResponse.Mensajes.Mensaje }
     * 
     */
    public GizloResponse.Mensajes.Mensaje createGizloResponseMensajesMensaje() {
        return new GizloResponse.Mensajes.Mensaje();
    }

    /**
     * Create an instance of {@link Detalle.DetallesAdicionales.DetAdicional }
     * 
     */
    public Detalle.DetallesAdicionales.DetAdicional createDetalleDetallesAdicionalesDetAdicional() {
        return new Detalle.DetallesAdicionales.DetAdicional();
    }

    /**
     * Create an instance of {@link Destinatario.Detalles }
     * 
     */
    public Destinatario.Detalles createDestinatarioDetalles() {
        return new Destinatario.Detalles();
    }

    /**
     * Create an instance of {@link GuiaRemision.InfoAdicional.CampoAdicional }
     * 
     */
    public GuiaRemision.InfoAdicional.CampoAdicional createGuiaRemisionInfoAdicionalCampoAdicional() {
        return new GuiaRemision.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link GuiaConsultarRequest.Guia.InfoGuiaRemision }
     * 
     */
    public GuiaConsultarRequest.Guia.InfoGuiaRemision createGuiaConsultarRequestGuiaInfoGuiaRemision() {
        return new GuiaConsultarRequest.Guia.InfoGuiaRemision();
    }

    /**
     * Create an instance of {@link GuiaConsultarRequest.Guia.Destinatarios }
     * 
     */
    public GuiaConsultarRequest.Guia.Destinatarios createGuiaConsultarRequestGuiaDestinatarios() {
        return new GuiaConsultarRequest.Guia.Destinatarios();
    }

    /**
     * Create an instance of {@link GuiaConsultarRequest.Guia.InfoAdicional.CampoAdicional }
     * 
     */
    public GuiaConsultarRequest.Guia.InfoAdicional.CampoAdicional createGuiaConsultarRequestGuiaInfoAdicionalCampoAdicional() {
        return new GuiaConsultarRequest.Guia.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link GuiaConsultarResponse.Guias.Guia.InfoGuiaRemision }
     * 
     */
    public GuiaConsultarResponse.Guias.Guia.InfoGuiaRemision createGuiaConsultarResponseGuiasGuiaInfoGuiaRemision() {
        return new GuiaConsultarResponse.Guias.Guia.InfoGuiaRemision();
    }

    /**
     * Create an instance of {@link GuiaConsultarResponse.Guias.Guia.Destinatarios }
     * 
     */
    public GuiaConsultarResponse.Guias.Guia.Destinatarios createGuiaConsultarResponseGuiasGuiaDestinatarios() {
        return new GuiaConsultarResponse.Guias.Guia.Destinatarios();
    }

    /**
     * Create an instance of {@link GuiaConsultarResponse.Guias.Guia.InfoAdicional.CampoAdicional }
     * 
     */
    public GuiaConsultarResponse.Guias.Guia.InfoAdicional.CampoAdicional createGuiaConsultarResponseGuiasGuiaInfoAdicionalCampoAdicional() {
        return new GuiaConsultarResponse.Guias.Guia.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link GuiaProcesarRequest.Guia.InfoGuiaRemision }
     * 
     */
    public GuiaProcesarRequest.Guia.InfoGuiaRemision createGuiaProcesarRequestGuiaInfoGuiaRemision() {
        return new GuiaProcesarRequest.Guia.InfoGuiaRemision();
    }

    /**
     * Create an instance of {@link GuiaProcesarRequest.Guia.Destinatarios }
     * 
     */
    public GuiaProcesarRequest.Guia.Destinatarios createGuiaProcesarRequestGuiaDestinatarios() {
        return new GuiaProcesarRequest.Guia.Destinatarios();
    }

    /**
     * Create an instance of {@link GuiaProcesarRequest.Guia.InfoAdicional.CampoAdicional }
     * 
     */
    public GuiaProcesarRequest.Guia.InfoAdicional.CampoAdicional createGuiaProcesarRequestGuiaInfoAdicionalCampoAdicional() {
        return new GuiaProcesarRequest.Guia.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuiaConsultarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "guiaConsultarResponse")
    public JAXBElement<GuiaConsultarResponse> createGuiaConsultarResponse(GuiaConsultarResponse value) {
        return new JAXBElement<GuiaConsultarResponse>(_GuiaConsultarResponse_QNAME, GuiaConsultarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuiaRecibirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "guiaRecibirResponse")
    public JAXBElement<GuiaRecibirResponse> createGuiaRecibirResponse(GuiaRecibirResponse value) {
        return new JAXBElement<GuiaRecibirResponse>(_GuiaRecibirResponse_QNAME, GuiaRecibirResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuiaProcesarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "guiaProcesarRequest")
    public JAXBElement<GuiaProcesarRequest> createGuiaProcesarRequest(GuiaProcesarRequest value) {
        return new JAXBElement<GuiaProcesarRequest>(_GuiaProcesarRequest_QNAME, GuiaProcesarRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuiaProcesarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "guiaProcesarResponse")
    public JAXBElement<GuiaProcesarResponse> createGuiaProcesarResponse(GuiaProcesarResponse value) {
        return new JAXBElement<GuiaProcesarResponse>(_GuiaProcesarResponse_QNAME, GuiaProcesarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuiaRecibirRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "guiaRecibirRequest")
    public JAXBElement<GuiaRecibirRequest> createGuiaRecibirRequest(GuiaRecibirRequest value) {
        return new JAXBElement<GuiaRecibirRequest>(_GuiaRecibirRequest_QNAME, GuiaRecibirRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuiaConsultarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "guiaConsultarRequest")
    public JAXBElement<GuiaConsultarRequest> createGuiaConsultarRequest(GuiaConsultarRequest value) {
        return new JAXBElement<GuiaConsultarRequest>(_GuiaConsultarRequest_QNAME, GuiaConsultarRequest.class, null, value);
    }

}
