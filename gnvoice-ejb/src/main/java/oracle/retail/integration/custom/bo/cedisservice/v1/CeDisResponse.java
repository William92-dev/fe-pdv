
package oracle.retail.integration.custom.bo.cedisservice.v1;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="order_number" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}number12" minOccurs="0"/>
 *         &lt;element name="cedis_virtual" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}varchar290" minOccurs="0"/>
 *         &lt;element name="vendor_id" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}varchar210" minOccurs="0"/>
 *         &lt;element name="location" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}number10" minOccurs="0"/>
 *         &lt;element name="location_type" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}varchar21" minOccurs="0"/>
 *         &lt;element name="vendor_type" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}varchar26" minOccurs="0"/>
 *         &lt;element name="order_status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ValidItemList" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}ValidItemList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orderNumber",
    "cedisVirtual",
    "vendorId",
    "location",
    "locationType",
    "vendorType",
    "orderStatus",
    "validItemList"
})
@XmlRootElement(name = "CeDisResponse")
public class CeDisResponse {

    @XmlElementRef(name = "order_number", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false)
    protected JAXBElement<BigDecimal> orderNumber;
    @XmlElementRef(name = "cedis_virtual", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cedisVirtual;
    @XmlElementRef(name = "vendor_id", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false)
    protected JAXBElement<String> vendorId;
    @XmlElementRef(name = "location", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false)
    protected JAXBElement<BigDecimal> location;
    @XmlElementRef(name = "location_type", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false)
    protected JAXBElement<String> locationType;
    @XmlElementRef(name = "vendor_type", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false)
    protected JAXBElement<String> vendorType;
    @XmlElement(name = "order_status", required = true, nillable = true)
    protected String orderStatus;
    @XmlElement(name = "ValidItemList", required = true, nillable = true)
    protected ValidItemList validItemList;

    /**
     * Gets the value of the orderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the value of the orderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setOrderNumber(JAXBElement<BigDecimal> value) {
        this.orderNumber = value;
    }

    /**
     * Gets the value of the cedisVirtual property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCedisVirtual() {
        return cedisVirtual;
    }

    /**
     * Sets the value of the cedisVirtual property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCedisVirtual(JAXBElement<String> value) {
        this.cedisVirtual = value;
    }

    /**
     * Gets the value of the vendorId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVendorId() {
        return vendorId;
    }

    /**
     * Sets the value of the vendorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVendorId(JAXBElement<String> value) {
        this.vendorId = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setLocation(JAXBElement<BigDecimal> value) {
        this.location = value;
    }

    /**
     * Gets the value of the locationType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLocationType() {
        return locationType;
    }

    /**
     * Sets the value of the locationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLocationType(JAXBElement<String> value) {
        this.locationType = value;
    }

    /**
     * Gets the value of the vendorType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVendorType() {
        return vendorType;
    }

    /**
     * Sets the value of the vendorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVendorType(JAXBElement<String> value) {
        this.vendorType = value;
    }

    /**
     * Gets the value of the orderStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * Sets the value of the orderStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderStatus(String value) {
        this.orderStatus = value;
    }

    /**
     * Gets the value of the validItemList property.
     * 
     * @return
     *     possible object is
     *     {@link ValidItemList }
     *     
     */
    public ValidItemList getValidItemList() {
        return validItemList;
    }

    /**
     * Sets the value of the validItemList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidItemList }
     *     
     */
    public void setValidItemList(ValidItemList value) {
        this.validItemList = value;
    }

}
