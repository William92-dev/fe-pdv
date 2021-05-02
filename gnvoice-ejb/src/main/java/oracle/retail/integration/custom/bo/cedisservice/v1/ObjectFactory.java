
package oracle.retail.integration.custom.bo.cedisservice.v1;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.oracle.retail.integration.custom.bo.cedisservice.v1 package. 
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

    private final static QName _CeDisOrderRucRequestRucNumber_QNAME = new QName("http://oracle.com/retail/integration/custom/bo/CeDisService/v1", "ruc_number");
    private final static QName _CeDisOrderRucRequestOrderNumber_QNAME = new QName("http://oracle.com/retail/integration/custom/bo/CeDisService/v1", "order_number");
    private final static QName _CeDisResponseCedisVirtual_QNAME = new QName("http://oracle.com/retail/integration/custom/bo/CeDisService/v1", "cedis_virtual");
    private final static QName _CeDisResponseVendorType_QNAME = new QName("http://oracle.com/retail/integration/custom/bo/CeDisService/v1", "vendor_type");
    private final static QName _CeDisResponseVendorId_QNAME = new QName("http://oracle.com/retail/integration/custom/bo/CeDisService/v1", "vendor_id");
    private final static QName _CeDisResponseLocationType_QNAME = new QName("http://oracle.com/retail/integration/custom/bo/CeDisService/v1", "location_type");
    private final static QName _CeDisResponseLocation_QNAME = new QName("http://oracle.com/retail/integration/custom/bo/CeDisService/v1", "location");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.oracle.retail.integration.custom.bo.cedisservice.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CeDisResponse }
     * 
     */
    public CeDisResponse createCeDisResponse() {
        return new CeDisResponse();
    }

    /**
     * Create an instance of {@link ValidItemList }
     * 
     */
    public ValidItemList createValidItemList() {
        return new ValidItemList();
    }

    /**
     * Create an instance of {@link CeDisOrderRucRequest }
     * 
     */
    public CeDisOrderRucRequest createCeDisOrderRucRequest() {
        return new CeDisOrderRucRequest();
    }

    /**
     * Create an instance of {@link CeDisInvoiceRequest }
     * 
     */
    public CeDisInvoiceRequest createCeDisInvoiceRequest() {
        return new CeDisInvoiceRequest();
    }

    /**
     * Create an instance of {@link CeDisOrderRequest }
     * 
     */
    public CeDisOrderRequest createCeDisOrderRequest() {
        return new CeDisOrderRequest();
    }

    /**
     * Create an instance of {@link CeDisRTVRequest }
     * 
     */
    public CeDisRTVRequest createCeDisRTVRequest() {
        return new CeDisRTVRequest();
    }

    /**
     * Create an instance of {@link ValidItem }
     * 
     */
    public ValidItem createValidItem() {
        return new ValidItem();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "ruc_number", scope = CeDisOrderRucRequest.class)
    public JAXBElement<String> createCeDisOrderRucRequestRucNumber(String value) {
        return new JAXBElement<String>(_CeDisOrderRucRequestRucNumber_QNAME, String.class, CeDisOrderRucRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "order_number", scope = CeDisOrderRucRequest.class)
    public JAXBElement<String> createCeDisOrderRucRequestOrderNumber(String value) {
        return new JAXBElement<String>(_CeDisOrderRucRequestOrderNumber_QNAME, String.class, CeDisOrderRucRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "cedis_virtual", scope = CeDisResponse.class)
    public JAXBElement<String> createCeDisResponseCedisVirtual(String value) {
        return new JAXBElement<String>(_CeDisResponseCedisVirtual_QNAME, String.class, CeDisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "vendor_type", scope = CeDisResponse.class)
    public JAXBElement<String> createCeDisResponseVendorType(String value) {
        return new JAXBElement<String>(_CeDisResponseVendorType_QNAME, String.class, CeDisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "vendor_id", scope = CeDisResponse.class)
    public JAXBElement<String> createCeDisResponseVendorId(String value) {
        return new JAXBElement<String>(_CeDisResponseVendorId_QNAME, String.class, CeDisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "location_type", scope = CeDisResponse.class)
    public JAXBElement<String> createCeDisResponseLocationType(String value) {
        return new JAXBElement<String>(_CeDisResponseLocationType_QNAME, String.class, CeDisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "order_number", scope = CeDisResponse.class)
    public JAXBElement<BigDecimal> createCeDisResponseOrderNumber(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_CeDisOrderRucRequestOrderNumber_QNAME, BigDecimal.class, CeDisResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", name = "location", scope = CeDisResponse.class)
    public JAXBElement<BigDecimal> createCeDisResponseLocation(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_CeDisResponseLocation_QNAME, BigDecimal.class, CeDisResponse.class, value);
    }

}
