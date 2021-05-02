
package com.gizlocorp.gnvoice.wsclient.retencion;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Clase Java para retencionConsultarResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="retencionConsultarResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}gizloResponse">
 *       &lt;sequence>
 *         &lt;element name="rentenciones" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="rentencion" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="infoTributaria" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}infoTributaria"/>
 *                             &lt;element name="infoCompRetencion">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="tipoIdentificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="razonSocialSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="identificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="periodoFiscal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="impuestos">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="infoAdicional" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="campoAdicional" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;simpleContent>
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                               &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/extension>
 *                                           &lt;/simpleContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "retencionConsultarResponse", propOrder = {
    "rentenciones"
})
public class RetencionConsultarResponse
    extends GizloResponse
{

    protected RetencionConsultarResponse.Rentenciones rentenciones;

    /**
     * Obtiene el valor de la propiedad rentenciones.
     * 
     * @return
     *     possible object is
     *     {@link RetencionConsultarResponse.Rentenciones }
     *     
     */
    public RetencionConsultarResponse.Rentenciones getRentenciones() {
        return rentenciones;
    }

    /**
     * Define el valor de la propiedad rentenciones.
     * 
     * @param value
     *     allowed object is
     *     {@link RetencionConsultarResponse.Rentenciones }
     *     
     */
    public void setRentenciones(RetencionConsultarResponse.Rentenciones value) {
        this.rentenciones = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="rentencion" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="infoTributaria" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}infoTributaria"/>
     *                   &lt;element name="infoCompRetencion">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="tipoIdentificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="razonSocialSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="identificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="periodoFiscal" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="impuestos">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="infoAdicional" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="campoAdicional" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;simpleContent>
     *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                                     &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/extension>
     *                                 &lt;/simpleContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "rentencion"
    })
    public static class Rentenciones {

        protected List<RetencionConsultarResponse.Rentenciones.Rentencion> rentencion;

        /**
         * Gets the value of the rentencion property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the rentencion property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRentencion().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link RetencionConsultarResponse.Rentenciones.Rentencion }
         * 
         * 
         */
        public List<RetencionConsultarResponse.Rentenciones.Rentencion> getRentencion() {
            if (rentencion == null) {
                rentencion = new ArrayList<RetencionConsultarResponse.Rentenciones.Rentencion>();
            }
            return this.rentencion;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="infoTributaria" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}infoTributaria"/>
         *         &lt;element name="infoCompRetencion">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="tipoIdentificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="razonSocialSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="identificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="periodoFiscal" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="impuestos">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="infoAdicional" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="campoAdicional" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;simpleContent>
         *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                           &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/extension>
         *                       &lt;/simpleContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "infoTributaria",
            "infoCompRetencion",
            "impuestos",
            "infoAdicional"
        })
        public static class Rentencion {

            @XmlElement(required = true)
            protected InfoTributaria infoTributaria;
            @XmlElement(required = true)
            protected RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion infoCompRetencion;
            @XmlElement(required = true)
            protected RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos impuestos;
            protected RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional infoAdicional;
            @XmlAttribute(name = "id")
            protected String id;
            @XmlAttribute(name = "version", required = true)
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "NMTOKEN")
            protected String version;

            /**
             * Obtiene el valor de la propiedad infoTributaria.
             * 
             * @return
             *     possible object is
             *     {@link InfoTributaria }
             *     
             */
            public InfoTributaria getInfoTributaria() {
                return infoTributaria;
            }

            /**
             * Define el valor de la propiedad infoTributaria.
             * 
             * @param value
             *     allowed object is
             *     {@link InfoTributaria }
             *     
             */
            public void setInfoTributaria(InfoTributaria value) {
                this.infoTributaria = value;
            }

            /**
             * Obtiene el valor de la propiedad infoCompRetencion.
             * 
             * @return
             *     possible object is
             *     {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion }
             *     
             */
            public RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion getInfoCompRetencion() {
                return infoCompRetencion;
            }

            /**
             * Define el valor de la propiedad infoCompRetencion.
             * 
             * @param value
             *     allowed object is
             *     {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion }
             *     
             */
            public void setInfoCompRetencion(RetencionConsultarResponse.Rentenciones.Rentencion.InfoCompRetencion value) {
                this.infoCompRetencion = value;
            }

            /**
             * Obtiene el valor de la propiedad impuestos.
             * 
             * @return
             *     possible object is
             *     {@link RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos }
             *     
             */
            public RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos getImpuestos() {
                return impuestos;
            }

            /**
             * Define el valor de la propiedad impuestos.
             * 
             * @param value
             *     allowed object is
             *     {@link RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos }
             *     
             */
            public void setImpuestos(RetencionConsultarResponse.Rentenciones.Rentencion.Impuestos value) {
                this.impuestos = value;
            }

            /**
             * Obtiene el valor de la propiedad infoAdicional.
             * 
             * @return
             *     possible object is
             *     {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional }
             *     
             */
            public RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional getInfoAdicional() {
                return infoAdicional;
            }

            /**
             * Define el valor de la propiedad infoAdicional.
             * 
             * @param value
             *     allowed object is
             *     {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional }
             *     
             */
            public void setInfoAdicional(RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional value) {
                this.infoAdicional = value;
            }

            /**
             * Obtiene el valor de la propiedad id.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getId() {
                return id;
            }

            /**
             * Define el valor de la propiedad id.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setId(String value) {
                this.id = value;
            }

            /**
             * Obtiene el valor de la propiedad version.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getVersion() {
                return version;
            }

            /**
             * Define el valor de la propiedad version.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVersion(String value) {
                this.version = value;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded"/>
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
                "impuesto"
            })
            public static class Impuestos {

                @XmlElement(required = true)
                protected List<Impuesto> impuesto;

                /**
                 * Gets the value of the impuesto property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the impuesto property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getImpuesto().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Impuesto }
                 * 
                 * 
                 */
                public List<Impuesto> getImpuesto() {
                    if (impuesto == null) {
                        impuesto = new ArrayList<Impuesto>();
                    }
                    return this.impuesto;
                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="campoAdicional" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;simpleContent>
             *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *                 &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
             *               &lt;/extension>
             *             &lt;/simpleContent>
             *           &lt;/complexType>
             *         &lt;/element>
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
                "campoAdicional"
            })
            public static class InfoAdicional {

                @XmlElement(required = true)
                protected List<RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional.CampoAdicional> campoAdicional;

                /**
                 * Gets the value of the campoAdicional property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the campoAdicional property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getCampoAdicional().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional.CampoAdicional }
                 * 
                 * 
                 */
                public List<RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional.CampoAdicional> getCampoAdicional() {
                    if (campoAdicional == null) {
                        campoAdicional = new ArrayList<RetencionConsultarResponse.Rentenciones.Rentencion.InfoAdicional.CampoAdicional>();
                    }
                    return this.campoAdicional;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;simpleContent>
                 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                 *       &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/extension>
                 *   &lt;/simpleContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "value"
                })
                public static class CampoAdicional {

                    @XmlValue
                    protected String value;
                    @XmlAttribute(name = "nombre")
                    protected String nombre;

                    /**
                     * Obtiene el valor de la propiedad value.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getValue() {
                        return value;
                    }

                    /**
                     * Define el valor de la propiedad value.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setValue(String value) {
                        this.value = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad nombre.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getNombre() {
                        return nombre;
                    }

                    /**
                     * Define el valor de la propiedad nombre.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setNombre(String value) {
                        this.nombre = value;
                    }

                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="tipoIdentificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="razonSocialSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="identificacionSujetoRetenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="periodoFiscal" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                "fechaEmision",
                "dirEstablecimiento",
                "contribuyenteEspecial",
                "obligadoContabilidad",
                "tipoIdentificacionSujetoRetenido",
                "razonSocialSujetoRetenido",
                "identificacionSujetoRetenido",
                "periodoFiscal"
            })
            public static class InfoCompRetencion {

                @XmlElement(required = true)
                protected String fechaEmision;
                protected String dirEstablecimiento;
                protected String contribuyenteEspecial;
                protected String obligadoContabilidad;
                @XmlElement(required = true)
                protected String tipoIdentificacionSujetoRetenido;
                @XmlElement(required = true)
                protected String razonSocialSujetoRetenido;
                @XmlElement(required = true)
                protected String identificacionSujetoRetenido;
                @XmlElement(required = true)
                protected String periodoFiscal;

                /**
                 * Obtiene el valor de la propiedad fechaEmision.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getFechaEmision() {
                    return fechaEmision;
                }

                /**
                 * Define el valor de la propiedad fechaEmision.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setFechaEmision(String value) {
                    this.fechaEmision = value;
                }

                /**
                 * Obtiene el valor de la propiedad dirEstablecimiento.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getDirEstablecimiento() {
                    return dirEstablecimiento;
                }

                /**
                 * Define el valor de la propiedad dirEstablecimiento.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setDirEstablecimiento(String value) {
                    this.dirEstablecimiento = value;
                }

                /**
                 * Obtiene el valor de la propiedad contribuyenteEspecial.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getContribuyenteEspecial() {
                    return contribuyenteEspecial;
                }

                /**
                 * Define el valor de la propiedad contribuyenteEspecial.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setContribuyenteEspecial(String value) {
                    this.contribuyenteEspecial = value;
                }

                /**
                 * Obtiene el valor de la propiedad obligadoContabilidad.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getObligadoContabilidad() {
                    return obligadoContabilidad;
                }

                /**
                 * Define el valor de la propiedad obligadoContabilidad.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setObligadoContabilidad(String value) {
                    this.obligadoContabilidad = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipoIdentificacionSujetoRetenido.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getTipoIdentificacionSujetoRetenido() {
                    return tipoIdentificacionSujetoRetenido;
                }

                /**
                 * Define el valor de la propiedad tipoIdentificacionSujetoRetenido.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setTipoIdentificacionSujetoRetenido(String value) {
                    this.tipoIdentificacionSujetoRetenido = value;
                }

                /**
                 * Obtiene el valor de la propiedad razonSocialSujetoRetenido.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getRazonSocialSujetoRetenido() {
                    return razonSocialSujetoRetenido;
                }

                /**
                 * Define el valor de la propiedad razonSocialSujetoRetenido.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setRazonSocialSujetoRetenido(String value) {
                    this.razonSocialSujetoRetenido = value;
                }

                /**
                 * Obtiene el valor de la propiedad identificacionSujetoRetenido.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getIdentificacionSujetoRetenido() {
                    return identificacionSujetoRetenido;
                }

                /**
                 * Define el valor de la propiedad identificacionSujetoRetenido.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setIdentificacionSujetoRetenido(String value) {
                    this.identificacionSujetoRetenido = value;
                }

                /**
                 * Obtiene el valor de la propiedad periodoFiscal.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getPeriodoFiscal() {
                    return periodoFiscal;
                }

                /**
                 * Define el valor de la propiedad periodoFiscal.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setPeriodoFiscal(String value) {
                    this.periodoFiscal = value;
                }

            }

        }

    }

}
