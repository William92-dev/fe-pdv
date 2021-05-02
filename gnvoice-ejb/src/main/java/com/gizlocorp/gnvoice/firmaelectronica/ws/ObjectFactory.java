
package com.gizlocorp.gnvoice.firmaelectronica.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gizlocorp.gnvoice.firmaelectronica.ws package. 
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

    private final static QName _FirmarDocumentoResponse_QNAME = new QName("http://service.firmaelectronica.gizlocorp.com/", "firmarDocumentoResponse");
    private final static QName _FirmarDocumento_QNAME = new QName("http://service.firmaelectronica.gizlocorp.com/", "firmarDocumento");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gizlocorp.gnvoice.firmaelectronica.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FirmarDocumento }
     * 
     */
    public FirmarDocumento createFirmarDocumento() {
        return new FirmarDocumento();
    }

    /**
     * Create an instance of {@link FirmarDocumentoResponse }
     * 
     */
    public FirmarDocumentoResponse createFirmarDocumentoResponse() {
        return new FirmarDocumentoResponse();
    }

    /**
     * Create an instance of {@link FirmaElectronicaResponse }
     * 
     */
    public FirmaElectronicaResponse createFirmaElectronicaResponse() {
        return new FirmaElectronicaResponse();
    }

    /**
     * Create an instance of {@link FirmaElectronicaRequest }
     * 
     */
    public FirmaElectronicaRequest createFirmaElectronicaRequest() {
        return new FirmaElectronicaRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FirmarDocumentoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.firmaelectronica.gizlocorp.com/", name = "firmarDocumentoResponse")
    public JAXBElement<FirmarDocumentoResponse> createFirmarDocumentoResponse(FirmarDocumentoResponse value) {
        return new JAXBElement<FirmarDocumentoResponse>(_FirmarDocumentoResponse_QNAME, FirmarDocumentoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FirmarDocumento }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.firmaelectronica.gizlocorp.com/", name = "firmarDocumento")
    public JAXBElement<FirmarDocumento> createFirmarDocumento(FirmarDocumento value) {
        return new JAXBElement<FirmarDocumento>(_FirmarDocumento_QNAME, FirmarDocumento.class, null, value);
    }

}
