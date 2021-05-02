
package com.gizlocorp.gnvoice.wsclient.notadebito;

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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element name="infoNotaDebito">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="tipoIdentificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="razonSocialComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="identificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="rise" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="codDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="numDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="fechaEmisionDocSustento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalSinImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
 *                   &lt;element name="valorTotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="motivos">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="motivo" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="razon" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "infoNotaDebito",
    "motivos",
    "infoAdicional"
})
@XmlRootElement(name = "notaDebito")
public class NotaDebito {

    @XmlElement(required = true)
    protected InfoTributaria infoTributaria;
    @XmlElement(required = true)
    protected NotaDebito.InfoNotaDebito infoNotaDebito;
    @XmlElement(required = true)
    protected NotaDebito.Motivos motivos;
    protected NotaDebito.InfoAdicional infoAdicional;
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
     * Obtiene el valor de la propiedad infoNotaDebito.
     * 
     * @return
     *     possible object is
     *     {@link NotaDebito.InfoNotaDebito }
     *     
     */
    public NotaDebito.InfoNotaDebito getInfoNotaDebito() {
        return infoNotaDebito;
    }

    /**
     * Define el valor de la propiedad infoNotaDebito.
     * 
     * @param value
     *     allowed object is
     *     {@link NotaDebito.InfoNotaDebito }
     *     
     */
    public void setInfoNotaDebito(NotaDebito.InfoNotaDebito value) {
        this.infoNotaDebito = value;
    }

    /**
     * Obtiene el valor de la propiedad motivos.
     * 
     * @return
     *     possible object is
     *     {@link NotaDebito.Motivos }
     *     
     */
    public NotaDebito.Motivos getMotivos() {
        return motivos;
    }

    /**
     * Define el valor de la propiedad motivos.
     * 
     * @param value
     *     allowed object is
     *     {@link NotaDebito.Motivos }
     *     
     */
    public void setMotivos(NotaDebito.Motivos value) {
        this.motivos = value;
    }

    /**
     * Obtiene el valor de la propiedad infoAdicional.
     * 
     * @return
     *     possible object is
     *     {@link NotaDebito.InfoAdicional }
     *     
     */
    public NotaDebito.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * Define el valor de la propiedad infoAdicional.
     * 
     * @param value
     *     allowed object is
     *     {@link NotaDebito.InfoAdicional }
     *     
     */
    public void setInfoAdicional(NotaDebito.InfoAdicional value) {
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
        protected List<NotaDebito.InfoAdicional.CampoAdicional> campoAdicional;

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
         * {@link NotaDebito.InfoAdicional.CampoAdicional }
         * 
         * 
         */
        public List<NotaDebito.InfoAdicional.CampoAdicional> getCampoAdicional() {
            if (campoAdicional == null) {
                campoAdicional = new ArrayList<NotaDebito.InfoAdicional.CampoAdicional>();
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
     *         &lt;element name="tipoIdentificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="razonSocialComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="identificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="rise" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="codDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="numDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="fechaEmisionDocSustento" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalSinImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
     *         &lt;element name="valorTotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
        "tipoIdentificacionComprador",
        "razonSocialComprador",
        "identificacionComprador",
        "contribuyenteEspecial",
        "obligadoContabilidad",
        "rise",
        "codDocModificado",
        "numDocModificado",
        "fechaEmisionDocSustento",
        "totalSinImpuestos",
        "impuestos",
        "valorTotal"
    })
    public static class InfoNotaDebito {

        @XmlElement(required = true)
        protected String fechaEmision;
        protected String dirEstablecimiento;
        @XmlElement(required = true)
        protected String tipoIdentificacionComprador;
        protected String razonSocialComprador;
        @XmlElement(required = true)
        protected String identificacionComprador;
        protected String contribuyenteEspecial;
        protected String obligadoContabilidad;
        protected String rise;
        @XmlElement(required = true)
        protected String codDocModificado;
        @XmlElement(required = true)
        protected String numDocModificado;
        @XmlElement(required = true)
        protected String fechaEmisionDocSustento;
        @XmlElement(required = true)
        protected BigDecimal totalSinImpuestos;
        @XmlElement(required = true)
        protected NotaDebito.InfoNotaDebito.Impuestos impuestos;
        @XmlElement(required = true)
        protected BigDecimal valorTotal;

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
         * Obtiene el valor de la propiedad rise.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRise() {
            return rise;
        }

        /**
         * Define el valor de la propiedad rise.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRise(String value) {
            this.rise = value;
        }

        /**
         * Obtiene el valor de la propiedad codDocModificado.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodDocModificado() {
            return codDocModificado;
        }

        /**
         * Define el valor de la propiedad codDocModificado.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodDocModificado(String value) {
            this.codDocModificado = value;
        }

        /**
         * Obtiene el valor de la propiedad numDocModificado.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNumDocModificado() {
            return numDocModificado;
        }

        /**
         * Define el valor de la propiedad numDocModificado.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNumDocModificado(String value) {
            this.numDocModificado = value;
        }

        /**
         * Obtiene el valor de la propiedad fechaEmisionDocSustento.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFechaEmisionDocSustento() {
            return fechaEmisionDocSustento;
        }

        /**
         * Define el valor de la propiedad fechaEmisionDocSustento.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFechaEmisionDocSustento(String value) {
            this.fechaEmisionDocSustento = value;
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
         * Obtiene el valor de la propiedad impuestos.
         * 
         * @return
         *     possible object is
         *     {@link NotaDebito.InfoNotaDebito.Impuestos }
         *     
         */
        public NotaDebito.InfoNotaDebito.Impuestos getImpuestos() {
            return impuestos;
        }

        /**
         * Define el valor de la propiedad impuestos.
         * 
         * @param value
         *     allowed object is
         *     {@link NotaDebito.InfoNotaDebito.Impuestos }
         *     
         */
        public void setImpuestos(NotaDebito.InfoNotaDebito.Impuestos value) {
            this.impuestos = value;
        }

        /**
         * Obtiene el valor de la propiedad valorTotal.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getValorTotal() {
            return valorTotal;
        }

        /**
         * Define el valor de la propiedad valorTotal.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setValorTotal(BigDecimal value) {
            this.valorTotal = value;
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
     *         &lt;element name="motivo" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="razon" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "motivo"
    })
    public static class Motivos {

        @XmlElement(required = true)
        protected List<NotaDebito.Motivos.Motivo> motivo;

        /**
         * Gets the value of the motivo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the motivo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMotivo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotaDebito.Motivos.Motivo }
         * 
         * 
         */
        public List<NotaDebito.Motivos.Motivo> getMotivo() {
            if (motivo == null) {
                motivo = new ArrayList<NotaDebito.Motivos.Motivo>();
            }
            return this.motivo;
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
         *         &lt;element name="razon" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "razon",
            "valor"
        })
        public static class Motivo {

            @XmlElement(required = true)
            protected String razon;
            @XmlElement(required = true)
            protected BigDecimal valor;

            /**
             * Obtiene el valor de la propiedad razon.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRazon() {
                return razon;
            }

            /**
             * Define el valor de la propiedad razon.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRazon(String value) {
                this.razon = value;
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
