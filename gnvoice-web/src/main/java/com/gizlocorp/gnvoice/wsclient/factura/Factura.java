
package com.gizlocorp.gnvoice.wsclient.factura;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


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
 *         &lt;element name="infoFactura">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="tipoIdentificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="guiaRemision" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="razonSocialComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="identificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalSinImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="totalDescuento" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="totalConImpuestos">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="totalImpuesto" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="codigoPorcentaje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="baseImponible" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                                       &lt;element name="tarifa" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="propina" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="importeTotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="moneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="detalles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="detalle" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="codigoPrincipal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="codigoAuxiliar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="precioUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="descuento" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="precioTotalSinImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="detallesAdicionales" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="detAdicional" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="valor" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
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
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
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
    "infoFactura",
    "detalles",
    "infoAdicional"
})
@XmlRootElement(name = "factura")
public class Factura {

    @XmlElement(required = true)
    protected InfoTributaria infoTributaria;
    @XmlElement(required = true)
    protected Factura.InfoFactura infoFactura;
    @XmlElement(required = true)
    protected Factura.Detalles detalles;
    protected Factura.InfoAdicional infoAdicional;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "version")
    @XmlSchemaType(name = "anySimpleType")
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
     * Obtiene el valor de la propiedad infoFactura.
     * 
     * @return
     *     possible object is
     *     {@link Factura.InfoFactura }
     *     
     */
    public Factura.InfoFactura getInfoFactura() {
        return infoFactura;
    }

    /**
     * Define el valor de la propiedad infoFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link Factura.InfoFactura }
     *     
     */
    public void setInfoFactura(Factura.InfoFactura value) {
        this.infoFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad detalles.
     * 
     * @return
     *     possible object is
     *     {@link Factura.Detalles }
     *     
     */
    public Factura.Detalles getDetalles() {
    	 if (detalles == null) {
             detalles = new Factura.Detalles();
         }
        return detalles;
    }

    /**
     * Define el valor de la propiedad detalles.
     * 
     * @param value
     *     allowed object is
     *     {@link Factura.Detalles }
     *     
     */
    public void setDetalles(Factura.Detalles value) {
        this.detalles = value;
    }

    /**
     * Obtiene el valor de la propiedad infoAdicional.
     * 
     * @return
     *     possible object is
     *     {@link Factura.InfoAdicional }
     *     
     */
    public Factura.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * Define el valor de la propiedad infoAdicional.
     * 
     * @param value
     *     allowed object is
     *     {@link Factura.InfoAdicional }
     *     
     */
    public void setInfoAdicional(Factura.InfoAdicional value) {
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
     *         &lt;element name="detalle" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="codigoPrincipal" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="codigoAuxiliar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="precioUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="descuento" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="precioTotalSinImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="detallesAdicionales" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="detAdicional" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="valor" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
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
     *                 &lt;/sequence>
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
        "detalle"
    })
    public static class Detalles {

        @XmlElement(required = true)
        protected List<Factura.Detalles.Detalle> detalle;

        /**
         * Gets the value of the detalle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the detalle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDetalle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Factura.Detalles.Detalle }
         * 
         * 
         */
        public List<Factura.Detalles.Detalle> getDetalle() {
            if (detalle == null) {
                detalle = new ArrayList<Factura.Detalles.Detalle>();
            }
            return this.detalle;
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
         *         &lt;element name="codigoPrincipal" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="codigoAuxiliar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="precioUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="descuento" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="precioTotalSinImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="detallesAdicionales" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="detAdicional" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                           &lt;/sequence>
         *                           &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="valor" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
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
            "codigoPrincipal",
            "codigoAuxiliar",
            "descripcion",
            "cantidad",
            "precioUnitario",
            "descuento",
            "precioTotalSinImpuesto",
            "detallesAdicionales",
            "impuestos"
        })
        public static class Detalle {

            @XmlElement(required = true)
            protected String codigoPrincipal;
            protected String codigoAuxiliar;
            @XmlElement(required = true)
            protected String descripcion;
            @XmlElement(required = true)
            protected BigDecimal cantidad;
            @XmlElement(required = true)
            protected BigDecimal precioUnitario;
            @XmlElement(required = true)
            protected BigDecimal descuento;
            @XmlElement(required = true)
            protected BigDecimal precioTotalSinImpuesto;
            protected Factura.Detalles.Detalle.DetallesAdicionales detallesAdicionales;
            @XmlElement(required = true)
            protected Factura.Detalles.Detalle.Impuestos impuestos;

            /**
             * Obtiene el valor de la propiedad codigoPrincipal.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodigoPrincipal() {
                return codigoPrincipal;
            }

            /**
             * Define el valor de la propiedad codigoPrincipal.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodigoPrincipal(String value) {
                this.codigoPrincipal = value;
            }

            /**
             * Obtiene el valor de la propiedad codigoAuxiliar.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodigoAuxiliar() {
                return codigoAuxiliar;
            }

            /**
             * Define el valor de la propiedad codigoAuxiliar.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodigoAuxiliar(String value) {
                this.codigoAuxiliar = value;
            }

            /**
             * Obtiene el valor de la propiedad descripcion.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDescripcion() {
                return descripcion;
            }

            /**
             * Define el valor de la propiedad descripcion.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDescripcion(String value) {
                this.descripcion = value;
            }

            /**
             * Obtiene el valor de la propiedad cantidad.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCantidad() {
                return cantidad;
            }

            /**
             * Define el valor de la propiedad cantidad.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCantidad(BigDecimal value) {
                this.cantidad = value;
            }

            /**
             * Obtiene el valor de la propiedad precioUnitario.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPrecioUnitario() {
                return precioUnitario;
            }

            /**
             * Define el valor de la propiedad precioUnitario.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPrecioUnitario(BigDecimal value) {
                this.precioUnitario = value;
            }

            /**
             * Obtiene el valor de la propiedad descuento.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getDescuento() {
                return descuento;
            }

            /**
             * Define el valor de la propiedad descuento.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setDescuento(BigDecimal value) {
                this.descuento = value;
            }

            /**
             * Obtiene el valor de la propiedad precioTotalSinImpuesto.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPrecioTotalSinImpuesto() {
                return precioTotalSinImpuesto;
            }

            /**
             * Define el valor de la propiedad precioTotalSinImpuesto.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPrecioTotalSinImpuesto(BigDecimal value) {
                this.precioTotalSinImpuesto = value;
            }

            /**
             * Obtiene el valor de la propiedad detallesAdicionales.
             * 
             * @return
             *     possible object is
             *     {@link Factura.Detalles.Detalle.DetallesAdicionales }
             *     
             */
            public Factura.Detalles.Detalle.DetallesAdicionales getDetallesAdicionales() {            	
                return detallesAdicionales;
            }

            /**
             * Define el valor de la propiedad detallesAdicionales.
             * 
             * @param value
             *     allowed object is
             *     {@link Factura.Detalles.Detalle.DetallesAdicionales }
             *     
             */
            public void setDetallesAdicionales(Factura.Detalles.Detalle.DetallesAdicionales value) {
                this.detallesAdicionales = value;
            }

            /**
             * Obtiene el valor de la propiedad impuestos.
             * 
             * @return
             *     possible object is
             *     {@link Factura.Detalles.Detalle.Impuestos }
             *     
             */
            public Factura.Detalles.Detalle.Impuestos getImpuestos() {
                return impuestos;
            }

            /**
             * Define el valor de la propiedad impuestos.
             * 
             * @param value
             *     allowed object is
             *     {@link Factura.Detalles.Detalle.Impuestos }
             *     
             */
            public void setImpuestos(Factura.Detalles.Detalle.Impuestos value) {
                this.impuestos = value;
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
             *         &lt;element name="detAdicional" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                 &lt;/sequence>
             *                 &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="valor" type="{http://www.w3.org/2001/XMLSchema}string" />
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
                "detAdicional"
            })
            public static class DetallesAdicionales {

                @XmlElement(required = true)
                protected List<Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional> detAdicional;

                /**
                 * Gets the value of the detAdicional property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the detAdicional property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getDetAdicional().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional }
                 * 
                 * 
                 */
                public List<Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional> getDetAdicional() {
                    if (detAdicional == null) {
                        detAdicional = new ArrayList<Factura.Detalles.Detalle.DetallesAdicionales.DetAdicional>();
                    }
                    return this.detAdicional;
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
                 *       &lt;/sequence>
                 *       &lt;attribute name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="valor" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class DetAdicional {

                    @XmlAttribute(name = "nombre")
                    protected String nombre;
                    @XmlAttribute(name = "valor")
                    protected String valor;

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

                    /**
                     * Obtiene el valor de la propiedad valor.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getValor() {
                        return valor;
                    }

                    /**
                     * Define el valor de la propiedad valor.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setValor(String value) {
                        this.valor = value;
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
        protected List<Factura.InfoAdicional.CampoAdicional> campoAdicional;

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
         * {@link Factura.InfoAdicional.CampoAdicional }
         * 
         * 
         */
        public List<Factura.InfoAdicional.CampoAdicional> getCampoAdicional() {
            if (campoAdicional == null) {
                campoAdicional = new ArrayList<Factura.InfoAdicional.CampoAdicional>();
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
     *         &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="tipoIdentificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="guiaRemision" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="razonSocialComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="identificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalSinImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="totalDescuento" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="totalConImpuestos">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="totalImpuesto" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="codigoPorcentaje" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="baseImponible" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                             &lt;element name="tarifa" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="propina" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="importeTotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="moneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "tipoIdentificacionComprador",
        "guiaRemision",
        "razonSocialComprador",
        "identificacionComprador",
        "totalSinImpuestos",
        "totalDescuento",
        "totalConImpuestos",
        "propina",
        "importeTotal",
        "moneda"
    })
    public static class InfoFactura {

        @XmlElement(required = true)
        protected String fechaEmision;
        @XmlElement(required = true)
        protected String dirEstablecimiento;
        protected String contribuyenteEspecial;
        protected String obligadoContabilidad;
        @XmlElement(required = true)
        protected String tipoIdentificacionComprador;
        protected String guiaRemision;
        @XmlElement(required = true)
        protected String razonSocialComprador;
        @XmlElement(required = true)
        protected String identificacionComprador;
        @XmlElement(required = true)
        protected BigDecimal totalSinImpuestos;
        @XmlElement(required = true)
        protected BigDecimal totalDescuento;
        @XmlElement(required = true)
        protected Factura.InfoFactura.TotalConImpuestos totalConImpuestos;
        @XmlElement(required = true)
        protected BigDecimal propina;
        @XmlElement(required = true)
        protected BigDecimal importeTotal;
        protected String moneda;

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
         * Obtiene el valor de la propiedad tipoIdentificacionComprador.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTipoIdentificacionComprador() {
            return tipoIdentificacionComprador;
        }

        /**
         * Define el valor de la propiedad tipoIdentificacionComprador.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTipoIdentificacionComprador(String value) {
            this.tipoIdentificacionComprador = value;
        }

        /**
         * Obtiene el valor de la propiedad guiaRemision.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGuiaRemision() {
            return guiaRemision;
        }

        /**
         * Define el valor de la propiedad guiaRemision.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGuiaRemision(String value) {
            this.guiaRemision = value;
        }

        /**
         * Obtiene el valor de la propiedad razonSocialComprador.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRazonSocialComprador() {
            return razonSocialComprador;
        }

        /**
         * Define el valor de la propiedad razonSocialComprador.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRazonSocialComprador(String value) {
            this.razonSocialComprador = value;
        }

        /**
         * Obtiene el valor de la propiedad identificacionComprador.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdentificacionComprador() {
            return identificacionComprador;
        }

        /**
         * Define el valor de la propiedad identificacionComprador.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdentificacionComprador(String value) {
            this.identificacionComprador = value;
        }

        /**
         * Obtiene el valor de la propiedad totalSinImpuestos.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalSinImpuestos() {
            return totalSinImpuestos;
        }

        /**
         * Define el valor de la propiedad totalSinImpuestos.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalSinImpuestos(BigDecimal value) {
            this.totalSinImpuestos = value;
        }

        /**
         * Obtiene el valor de la propiedad totalDescuento.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalDescuento() {
            return totalDescuento;
        }

        /**
         * Define el valor de la propiedad totalDescuento.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalDescuento(BigDecimal value) {
            this.totalDescuento = value;
        }

        /**
         * Obtiene el valor de la propiedad totalConImpuestos.
         * 
         * @return
         *     possible object is
         *     {@link Factura.InfoFactura.TotalConImpuestos }
         *     
         */
        public Factura.InfoFactura.TotalConImpuestos getTotalConImpuestos() {
            return totalConImpuestos;
        }

        /**
         * Define el valor de la propiedad totalConImpuestos.
         * 
         * @param value
         *     allowed object is
         *     {@link Factura.InfoFactura.TotalConImpuestos }
         *     
         */
        public void setTotalConImpuestos(Factura.InfoFactura.TotalConImpuestos value) {
            this.totalConImpuestos = value;
        }

        /**
         * Obtiene el valor de la propiedad propina.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getPropina() {
            return propina;
        }

        /**
         * Define el valor de la propiedad propina.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setPropina(BigDecimal value) {
            this.propina = value;
        }

        /**
         * Obtiene el valor de la propiedad importeTotal.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getImporteTotal() {
            return importeTotal;
        }

        /**
         * Define el valor de la propiedad importeTotal.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setImporteTotal(BigDecimal value) {
            this.importeTotal = value;
        }

        /**
         * Obtiene el valor de la propiedad moneda.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMoneda() {
            return moneda;
        }

        /**
         * Define el valor de la propiedad moneda.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMoneda(String value) {
            this.moneda = value;
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
         *         &lt;element name="totalImpuesto" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="codigoPorcentaje" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="baseImponible" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *                   &lt;element name="tarifa" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *                 &lt;/sequence>
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
            "totalImpuesto"
        })
        public static class TotalConImpuestos {

            @XmlElement(required = true)
            protected List<Factura.InfoFactura.TotalConImpuestos.TotalImpuesto> totalImpuesto;

            /**
             * Gets the value of the totalImpuesto property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the totalImpuesto property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getTotalImpuesto().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Factura.InfoFactura.TotalConImpuestos.TotalImpuesto }
             * 
             * 
             */
            public List<Factura.InfoFactura.TotalConImpuestos.TotalImpuesto> getTotalImpuesto() {
                if (totalImpuesto == null) {
                    totalImpuesto = new ArrayList<Factura.InfoFactura.TotalConImpuestos.TotalImpuesto>();
                }
                return this.totalImpuesto;
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
             *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="codigoPorcentaje" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="baseImponible" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
             *         &lt;element name="tarifa" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
                "codigo",
                "codigoPorcentaje",
                "baseImponible",
                "tarifa",
                "valor"
            })
            public static class TotalImpuesto {

                @XmlElement(required = true)
                protected String codigo;
                @XmlElement(required = true)
                protected String codigoPorcentaje;
                @XmlElement(required = true)
                protected BigDecimal baseImponible;
                protected BigDecimal tarifa;
                @XmlElement(required = true)
                protected BigDecimal valor;

                /**
                 * Obtiene el valor de la propiedad codigo.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCodigo() {
                    return codigo;
                }

                /**
                 * Define el valor de la propiedad codigo.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCodigo(String value) {
                    this.codigo = value;
                }

                /**
                 * Obtiene el valor de la propiedad codigoPorcentaje.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCodigoPorcentaje() {
                    return codigoPorcentaje;
                }

                /**
                 * Define el valor de la propiedad codigoPorcentaje.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCodigoPorcentaje(String value) {
                    this.codigoPorcentaje = value;
                }

                /**
                 * Obtiene el valor de la propiedad baseImponible.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getBaseImponible() {
                    return baseImponible;
                }

                /**
                 * Define el valor de la propiedad baseImponible.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setBaseImponible(BigDecimal value) {
                    this.baseImponible = value;
                }

                /**
                 * Obtiene el valor de la propiedad tarifa.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getTarifa() {
                    return tarifa;
                }

                /**
                 * Define el valor de la propiedad tarifa.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setTarifa(BigDecimal value) {
                    this.tarifa = value;
                }

                /**
                 * Obtiene el valor de la propiedad valor.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getValor() {
                    return valor;
                }

                /**
                 * Define el valor de la propiedad valor.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setValor(BigDecimal value) {
                    this.valor = value;
                }

            }

        }

    }

}
