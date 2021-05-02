
package oracle.retail.integration.custom.bo.cedisservice.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValidItemList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValidItemList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="ValidItem" type="{http://oracle.com/retail/integration/custom/bo/CeDisService/v1}ValidItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidItemList", propOrder = {
    "validItem"
})
public class ValidItemList {

    @XmlElement(name = "ValidItem", nillable = true)
    protected List<ValidItem> validItem;

    /**
     * Gets the value of the validItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the validItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValidItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValidItem }
     * 
     * 
     */
    public List<ValidItem> getValidItem() {
        if (validItem == null) {
            validItem = new ArrayList<ValidItem>();
        }
        return this.validItem;
    }

}
