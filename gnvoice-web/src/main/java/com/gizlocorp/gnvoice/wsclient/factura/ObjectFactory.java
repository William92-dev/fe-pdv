
package com.gizlocorp.gnvoice.wsclient.factura;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gizlocorp.gnvoice.wsclient.factura package. 
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

    private final static QName _FacturaProcesarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "facturaProcesarResponse");
    private final static QName _FacturaProcesarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "facturaProcesarRequest");
    private final static QName _FacturaRecibirRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "facturaRecibirRequest");
    private final static QName _FacturaConsultarResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "facturaConsultarResponse");
    private final static QName _FacturaConsultarRequest_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "facturaConsultarRequest");
    private final static QName _FacturaRecibirResponse_QNAME = new QName("urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", "facturaRecibirResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gizlocorp.gnvoice.wsclient.factura
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Factura }
     * 
     */
    public Factura createFactura() {
        return new Factura();
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
     * Create an instance of {@link FacturaConsultarResponse }
     * 
     */
    public FacturaConsultarResponse createFacturaConsultarResponse() {
        return new FacturaConsultarResponse();
    }

    /**
     * Create an instance of {@link Factura.InfoAdicional }
     * 
     */
    public Factura.InfoAdicional createFacturaInfoAdicional() {
        return new Factura.InfoAdicional();
    }

    /**
     * Create an instance of {@link Factura.Detalles }
     * 
     */
    public Factura.Detalles createFacturaDetalles() {
        return new Factura.Detalles();
    }

    /**
     * Create an instance of {@link Factura.Detalles.Detalle }
     * 
     */
    public Factura.Detalles.Detalle createFacturaDetallesDetalle() {
        return new Factura.Detalles.Detalle();
    }

    /**
     * Create an instance of {@link Factura.Detalles.Detalle.DetallesAdicionales }
     * 
     */
    public Factura.Detalles.Detalle.DetallesAdicionales createFacturaDetallesDetalleDetallesAdicionales() {
        return new Factura.Detalles.Detalle.DetallesAdicionales();
    }

    /**
     * Create an instance of {@link Factura.InfoFactura }
     * 
     */
    public Factura.InfoFactura createFacturaInfoFactura() {
        return new Factura.InfoFactura();
    }

    /**
     * Create an instance of {@link Factura.InfoFactura.TotalConImpuestos }
     * 
     */
    public Factura.InfoFactura.TotalConImpuestos createFacturaInfoFacturaTotalConImpuestos() {
        return new Factura.InfoFactura.TotalConImpuestos();
    }

    /**
     * Create an instance of {@link FacturaProcesarRequest }
     * 
     */
    public FacturaProcesarRequest createFacturaProcesarRequest() {
        return new FacturaProcesarRequest();
    }

    /**
     * Create an instance of {@link FacturaProcesarResponse }
     * 
     */
    public FacturaProcesarResponse createFacturaProcesarResponse() {
        return new FacturaProcesarResponse();
    }

    /**
     * Create an instance of {@link InfoTributaria }
     * 
     */
    public InfoTributaria createInfoTributaria() {
        return new InfoTributaria();
    }

    /**
     * Create an instance of {@link MensajeRespuesta }
     * 
     */
    public MensajeRespuesta createMensajeRespuesta() {
        return new MensajeRespuesta();
    }

    /**
     * Create an instance of {@link FacturaRecibirResponse }
     * 
     */
    public FacturaRecibirResponse createFacturaRecibirResponse() {
        return new FacturaRecibirResponse();
    }

    /**
     * Create an instance of {@link FacturaRecibirRequest }
     * 
     */
    public FacturaRecibirRequest createFacturaRecibirRequest() {
        return new FacturaRecibirRequest();
    }

    /**
     * Create an instance of {@link FacturaConsultarRequest }
     * 
     */
    public FacturaConsultarRequest createFacturaConsultarRequest() {
        return new FacturaConsultarRequest();
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
     * Create an instance of {@link FacturaConsultarResponse.Facturas }
     * 
     */
    public FacturaConsultarResponse.Facturas createFacturaConsultarResponseFacturas() {
        return new FacturaConsultarResponse.Facturas();
    }

    /**
     * Create an instance of {@link Factura.InfoAdicional.CampoAdicional }
     * 
     */
    public Factura.InfoAdicional.CampoAdicional createFacturaInfoAdicionalCampoAdicional() {
        return new Factura.InfoAdicional.CampoAdicional();
    }

    /**
     * Create an instance of {@link Factura.Detalles.Detalle.Impuestos }
     * 
     */
    public Factura.Detalles.Detalle.Impuestos createFacturaDetallesDetalleImpuestos() {
        return new Factura.Detalles.Detalle.Impuestos();
    }

    /**
     * Create an instance of {@link Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional }
     * 
     */
    public Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional createFacturaDetallesDetalleDetallesAdicionalesDetAdicional() {
        return new Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional();
    }

    /**
     * Create an instance of {@link Factura.InfoFactura.TotalConImpuestos.TotalImpuesto }
     * 
     */
    public Factura.InfoFactura.TotalConImpuestos.TotalImpuesto createFacturaInfoFacturaTotalConImpuestosTotalImpuesto() {
        return new Factura.InfoFactura.TotalConImpuestos.TotalImpuesto();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FacturaProcesarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "facturaProcesarResponse")
    public JAXBElement<FacturaProcesarResponse> createFacturaProcesarResponse(FacturaProcesarResponse value) {
        return new JAXBElement<FacturaProcesarResponse>(_FacturaProcesarResponse_QNAME, FacturaProcesarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FacturaProcesarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "facturaProcesarRequest")
    public JAXBElement<FacturaProcesarRequest> createFacturaProcesarRequest(FacturaProcesarRequest value) {
        return new JAXBElement<FacturaProcesarRequest>(_FacturaProcesarRequest_QNAME, FacturaProcesarRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FacturaRecibirRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "facturaRecibirRequest")
    public JAXBElement<FacturaRecibirRequest> createFacturaRecibirRequest(FacturaRecibirRequest value) {
        return new JAXBElement<FacturaRecibirRequest>(_FacturaRecibirRequest_QNAME, FacturaRecibirRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FacturaConsultarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "facturaConsultarResponse")
    public JAXBElement<FacturaConsultarResponse> createFacturaConsultarResponse(FacturaConsultarResponse value) {
        return new JAXBElement<FacturaConsultarResponse>(_FacturaConsultarResponse_QNAME, FacturaConsultarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FacturaConsultarRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "facturaConsultarRequest")
    public JAXBElement<FacturaConsultarRequest> createFacturaConsultarRequest(FacturaConsultarRequest value) {
        return new JAXBElement<FacturaConsultarRequest>(_FacturaConsultarRequest_QNAME, FacturaConsultarRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FacturaRecibirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0", name = "facturaRecibirResponse")
    public JAXBElement<FacturaRecibirResponse> createFacturaRecibirResponse(FacturaRecibirResponse value) {
        return new JAXBElement<FacturaRecibirResponse>(_FacturaRecibirResponse_QNAME, FacturaRecibirResponse.class, null, value);
    }

}
