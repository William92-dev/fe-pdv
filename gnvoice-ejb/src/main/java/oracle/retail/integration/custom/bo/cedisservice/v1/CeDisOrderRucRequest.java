
package oracle.retail.integration.custom.bo.cedisservice.v1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
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
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="order_number" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}number12"/>
 *         &lt;element name="ruc_number" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}number12"/>
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
    "orderNumberAndRucNumber"
})
@XmlRootElement(name = "CeDisOrderRucRequest")
public class CeDisOrderRucRequest {

    @XmlElementRefs({
        @XmlElementRef(name = "ruc_number", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "order_number", namespace = "http://oracle.com/retail/integration/custom/bo/CeDisService/v1", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<String>> orderNumberAndRucNumber;

	public List<JAXBElement<String>> getOrderNumberAndRucNumber() {
		return orderNumberAndRucNumber;
	}

	public void setOrderNumberAndRucNumber(
			List<JAXBElement<String>> orderNumberAndRucNumber) {
		this.orderNumberAndRucNumber = orderNumberAndRucNumber;
	}

    /**
     * Gets the value of the orderNumberAndRucNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orderNumberAndRucNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderNumberAndRucNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * 
     * 
     */
   

}
