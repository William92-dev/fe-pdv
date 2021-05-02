package com.gizlocorp.gnvoice.wsclient.notacredito;

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
 * <p>
 * Clase Java para anonymous complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="infoTributaria" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}infoTributaria"/>
 *         &lt;element name="infoNotaCredito">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="tipoIdentificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="razonSocialComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="identificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="rise" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="codDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="numDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="fechaEmisionDocSustento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalSinImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="valorModificacion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="moneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}totalConImpuestos"/>
 *                   &lt;element name="totalConImpuestos">
 *                   	&lt;:complexType>
 *                   		&lt;sequence>
 *                   			&lt;element maxOccurs="unbounded" name="totalImpuesto">
 *                   				&lt;complexType>
 *                   					&lt;sequence>
 *                   						&lt;element name="codigo" type="xs:string" />
 *                   						&lt;element name="codigoPorcentaje" type="xs:string" />
 *                   						&lt;element name="baseImponible" type="xs:decimal" />
 *                   						&lt;element name="valor" type="xs:decimal" />
 *                   					&lt;sequence>
 *                   				&lt;complexType>
 *                   			&lt;element>
 *                   		&lt;sequence>
 *                   	&lt;complexType>
 *                   &lt;element>
 *                   &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *                             &lt;element name="codigoInterno" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="codigoAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="precioUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="descuento" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
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
 *                                       &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded" minOccurs="0"/>
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
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "infoTributaria", "infoNotaCredito",
		"detalles", "infoAdicional" })
@XmlRootElement(name = "notaCredito")
public class NotaCredito {

	@XmlElement(required = true)
	protected InfoTributaria infoTributaria;
	@XmlElement(required = true)
	protected NotaCredito.InfoNotaCredito infoNotaCredito;
	@XmlElement(required = true)
	protected NotaCredito.Detalles detalles;
	protected NotaCredito.InfoAdicional infoAdicional;
	@XmlAttribute(name = "id", required = true)
	protected String id;
	@XmlAttribute(name = "version", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NMTOKEN")
	protected String version;

	/**
	 * Obtiene el valor de la propiedad infoTributaria.
	 * 
	 * @return possible object is {@link InfoTributaria }
	 * 
	 */
	public InfoTributaria getInfoTributaria() {
		return infoTributaria;
	}

	/**
	 * Define el valor de la propiedad infoTributaria.
	 * 
	 * @param value
	 *            allowed object is {@link InfoTributaria }
	 * 
	 */
	public void setInfoTributaria(InfoTributaria value) {
		this.infoTributaria = value;
	}

	/**
	 * Obtiene el valor de la propiedad infoNotaCredito.
	 * 
	 * @return possible object is {@link NotaCredito.InfoNotaCredito }
	 * 
	 */
	public NotaCredito.InfoNotaCredito getInfoNotaCredito() {
		return infoNotaCredito;
	}

	/**
	 * Define el valor de la propiedad infoNotaCredito.
	 * 
	 * @param value
	 *            allowed object is {@link NotaCredito.InfoNotaCredito }
	 * 
	 */
	public void setInfoNotaCredito(NotaCredito.InfoNotaCredito value) {
		this.infoNotaCredito = value;
	}

	/**
	 * Obtiene el valor de la propiedad detalles.
	 * 
	 * @return possible object is {@link NotaCredito.Detalles }
	 * 
	 */
	public NotaCredito.Detalles getDetalles() {
		return detalles;
	}

	/**
	 * Define el valor de la propiedad detalles.
	 * 
	 * @param value
	 *            allowed object is {@link NotaCredito.Detalles }
	 * 
	 */
	public void setDetalles(NotaCredito.Detalles value) {
		this.detalles = value;
	}

	/**
	 * Obtiene el valor de la propiedad infoAdicional.
	 * 
	 * @return possible object is {@link NotaCredito.InfoAdicional }
	 * 
	 */
	public NotaCredito.InfoAdicional getInfoAdicional() {
		return infoAdicional;
	}

	/**
	 * Define el valor de la propiedad infoAdicional.
	 * 
	 * @param value
	 *            allowed object is {@link NotaCredito.InfoAdicional }
	 * 
	 */
	public void setInfoAdicional(NotaCredito.InfoAdicional value) {
		this.infoAdicional = value;
	}

	/**
	 * Obtiene el valor de la propiedad id.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Define el valor de la propiedad id.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Obtiene el valor de la propiedad version.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Define el valor de la propiedad version.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setVersion(String value) {
		this.version = value;
	}

	/**
	 * <p>
	 * Clase Java para anonymous complex type.
	 * 
	 * <p>
	 * El siguiente fragmento de esquema especifica el contenido que se espera
	 * que haya en esta clase.
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
	 *                   &lt;element name="codigoInterno" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="codigoAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
	 *                   &lt;element name="precioUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
	 *                   &lt;element name="descuento" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
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
	 *                             &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded" minOccurs="0"/>
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
	@XmlType(name = "", propOrder = { "detalle" })
	public static class Detalles {

		@XmlElement(required = true)
		protected List<NotaCredito.Detalles.Detalle> detalle;

		/**
		 * Gets the value of the detalle property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the detalle property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getDetalle().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link NotaCredito.Detalles.Detalle }
		 * 
		 * 
		 */
		public List<NotaCredito.Detalles.Detalle> getDetalle() {
			if (detalle == null) {
				detalle = new ArrayList<NotaCredito.Detalles.Detalle>();
			}
			return this.detalle;
		}

		/**
		 * <p>
		 * Clase Java para anonymous complex type.
		 * 
		 * <p>
		 * El siguiente fragmento de esquema especifica el contenido que se
		 * espera que haya en esta clase.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="codigoInterno" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="codigoAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
		 *         &lt;element name="precioUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
		 *         &lt;element name="descuento" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
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
		 *                   &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded" minOccurs="0"/>
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
		@XmlType(name = "", propOrder = { "codigoInterno", "codigoAdicional",
				"descripcion", "cantidad", "precioUnitario", "descuento",
				"precioTotalSinImpuesto", "detallesAdicionales", "impuestos" })
		public static class Detalle {

			@XmlElement(required = true)
			protected String codigoInterno;
			protected String codigoAdicional;
			@XmlElement(required = true)
			protected String descripcion;
			@XmlElement(required = true)
			protected BigDecimal cantidad;
			@XmlElement(required = true)
			protected BigDecimal precioUnitario;
			protected BigDecimal descuento;
			@XmlElement(required = true)
			protected BigDecimal precioTotalSinImpuesto;
			protected NotaCredito.Detalles.Detalle.DetallesAdicionales detallesAdicionales;
			@XmlElement(required = true)
			protected NotaCredito.Detalles.Detalle.Impuestos impuestos;

			/**
			 * Obtiene el valor de la propiedad codigoInterno.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getCodigoInterno() {
				return codigoInterno;
			}

			/**
			 * Define el valor de la propiedad codigoInterno.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setCodigoInterno(String value) {
				this.codigoInterno = value;
			}

			/**
			 * Obtiene el valor de la propiedad codigoAdicional.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getCodigoAdicional() {
				return codigoAdicional;
			}

			/**
			 * Define el valor de la propiedad codigoAdicional.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setCodigoAdicional(String value) {
				this.codigoAdicional = value;
			}

			/**
			 * Obtiene el valor de la propiedad descripcion.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getDescripcion() {
				return descripcion;
			}

			/**
			 * Define el valor de la propiedad descripcion.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setDescripcion(String value) {
				this.descripcion = value;
			}

			/**
			 * Obtiene el valor de la propiedad cantidad.
			 * 
			 * @return possible object is {@link BigDecimal }
			 * 
			 */
			public BigDecimal getCantidad() {
				return cantidad;
			}

			/**
			 * Define el valor de la propiedad cantidad.
			 * 
			 * @param value
			 *            allowed object is {@link BigDecimal }
			 * 
			 */
			public void setCantidad(BigDecimal value) {
				this.cantidad = value;
			}

			/**
			 * Obtiene el valor de la propiedad precioUnitario.
			 * 
			 * @return possible object is {@link BigDecimal }
			 * 
			 */
			public BigDecimal getPrecioUnitario() {
				return precioUnitario;
			}

			/**
			 * Define el valor de la propiedad precioUnitario.
			 * 
			 * @param value
			 *            allowed object is {@link BigDecimal }
			 * 
			 */
			public void setPrecioUnitario(BigDecimal value) {
				this.precioUnitario = value;
			}

			/**
			 * Obtiene el valor de la propiedad descuento.
			 * 
			 * @return possible object is {@link BigDecimal }
			 * 
			 */
			public BigDecimal getDescuento() {
				return descuento;
			}

			/**
			 * Define el valor de la propiedad descuento.
			 * 
			 * @param value
			 *            allowed object is {@link BigDecimal }
			 * 
			 */
			public void setDescuento(BigDecimal value) {
				this.descuento = value;
			}

			/**
			 * Obtiene el valor de la propiedad precioTotalSinImpuesto.
			 * 
			 * @return possible object is {@link BigDecimal }
			 * 
			 */
			public BigDecimal getPrecioTotalSinImpuesto() {
				return precioTotalSinImpuesto;
			}

			/**
			 * Define el valor de la propiedad precioTotalSinImpuesto.
			 * 
			 * @param value
			 *            allowed object is {@link BigDecimal }
			 * 
			 */
			public void setPrecioTotalSinImpuesto(BigDecimal value) {
				this.precioTotalSinImpuesto = value;
			}

			/**
			 * Obtiene el valor de la propiedad detallesAdicionales.
			 * 
			 * @return possible object is
			 *         {@link NotaCredito.Detalles.Detalle.DetallesAdicionales }
			 * 
			 */
			public NotaCredito.Detalles.Detalle.DetallesAdicionales getDetallesAdicionales() {
				return detallesAdicionales;
			}

			/**
			 * Define el valor de la propiedad detallesAdicionales.
			 * 
			 * @param value
			 *            allowed object is
			 *            {@link NotaCredito.Detalles.Detalle.DetallesAdicionales }
			 * 
			 */
			public void setDetallesAdicionales(
					NotaCredito.Detalles.Detalle.DetallesAdicionales value) {
				this.detallesAdicionales = value;
			}

			/**
			 * Obtiene el valor de la propiedad impuestos.
			 * 
			 * @return possible object is
			 *         {@link NotaCredito.Detalles.Detalle.Impuestos }
			 * 
			 */
			public NotaCredito.Detalles.Detalle.Impuestos getImpuestos() {
				return impuestos;
			}

			/**
			 * Define el valor de la propiedad impuestos.
			 * 
			 * @param value
			 *            allowed object is
			 *            {@link NotaCredito.Detalles.Detalle.Impuestos }
			 * 
			 */
			public void setImpuestos(
					NotaCredito.Detalles.Detalle.Impuestos value) {
				this.impuestos = value;
			}

			/**
			 * <p>
			 * Clase Java para anonymous complex type.
			 * 
			 * <p>
			 * El siguiente fragmento de esquema especifica el contenido que se
			 * espera que haya en esta clase.
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
			@XmlType(name = "", propOrder = { "detAdicional" })
			public static class DetallesAdicionales {

				@XmlElement(required = true)
				protected List<NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional> detAdicional;

				/**
				 * Gets the value of the detAdicional property.
				 * 
				 * <p>
				 * This accessor method returns a reference to the live list,
				 * not a snapshot. Therefore any modification you make to the
				 * returned list will be present inside the JAXB object. This is
				 * why there is not a <CODE>set</CODE> method for the
				 * detAdicional property.
				 * 
				 * <p>
				 * For example, to add a new item, do as follows:
				 * 
				 * <pre>
				 * getDetAdicional().add(newItem);
				 * </pre>
				 * 
				 * 
				 * <p>
				 * Objects of the following type(s) are allowed in the list
				 * {@link NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional }
				 * 
				 * 
				 */
				public List<NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional> getDetAdicional() {
					if (detAdicional == null) {
						detAdicional = new ArrayList<NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional>();
					}
					return this.detAdicional;
				}

				/**
				 * <p>
				 * Clase Java para anonymous complex type.
				 * 
				 * <p>
				 * El siguiente fragmento de esquema especifica el contenido que
				 * se espera que haya en esta clase.
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
					 * @return possible object is {@link String }
					 * 
					 */
					public String getNombre() {
						return nombre;
					}

					/**
					 * Define el valor de la propiedad nombre.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setNombre(String value) {
						this.nombre = value;
					}

					/**
					 * Obtiene el valor de la propiedad valor.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getValor() {
						return valor;
					}

					/**
					 * Define el valor de la propiedad valor.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setValor(String value) {
						this.valor = value;
					}

				}

			}

			/**
			 * <p>
			 * Clase Java para anonymous complex type.
			 * 
			 * <p>
			 * El siguiente fragmento de esquema especifica el contenido que se
			 * espera que haya en esta clase.
			 * 
			 * <pre>
			 * &lt;complexType>
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="impuesto" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}impuesto" maxOccurs="unbounded" minOccurs="0"/>
			 *       &lt;/sequence>
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "impuesto" })
			public static class Impuestos {

				@XmlElement(nillable = true)
				protected List<Impuesto> impuesto;

				/**
				 * Gets the value of the impuesto property.
				 * 
				 * <p>
				 * This accessor method returns a reference to the live list,
				 * not a snapshot. Therefore any modification you make to the
				 * returned list will be present inside the JAXB object. This is
				 * why there is not a <CODE>set</CODE> method for the impuesto
				 * property.
				 * 
				 * <p>
				 * For example, to add a new item, do as follows:
				 * 
				 * <pre>
				 * getImpuesto().add(newItem);
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
	 * <p>
	 * Clase Java para anonymous complex type.
	 * 
	 * <p>
	 * El siguiente fragmento de esquema especifica el contenido que se espera
	 * que haya en esta clase.
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
	@XmlType(name = "", propOrder = { "campoAdicional" })
	public static class InfoAdicional {

		@XmlElement(required = true)
		protected List<NotaCredito.InfoAdicional.CampoAdicional> campoAdicional;

		/**
		 * Gets the value of the campoAdicional property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the campoAdicional property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getCampoAdicional().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link NotaCredito.InfoAdicional.CampoAdicional }
		 * 
		 * 
		 */
		public List<NotaCredito.InfoAdicional.CampoAdicional> getCampoAdicional() {
			if (campoAdicional == null) {
				campoAdicional = new ArrayList<NotaCredito.InfoAdicional.CampoAdicional>();
			}
			return this.campoAdicional;
		}

		/**
		 * <p>
		 * Clase Java para anonymous complex type.
		 * 
		 * <p>
		 * El siguiente fragmento de esquema especifica el contenido que se
		 * espera que haya en esta clase.
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
		@XmlType(name = "", propOrder = { "value" })
		public static class CampoAdicional {

			@XmlValue
			protected String value;
			@XmlAttribute(name = "nombre")
			protected String nombre;

			/**
			 * Obtiene el valor de la propiedad value.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getValue() {
				return value;
			}

			/**
			 * Define el valor de la propiedad value.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setValue(String value) {
				this.value = value;
			}

			/**
			 * Obtiene el valor de la propiedad nombre.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getNombre() {
				return nombre;
			}

			/**
			 * Define el valor de la propiedad nombre.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setNombre(String value) {
				this.nombre = value;
			}

		}

	}

	/**
	 * <p>
	 * Clase Java para anonymous complex type.
	 * 
	 * <p>
	 * El siguiente fragmento de esquema especifica el contenido que se espera
	 * que haya en esta clase.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="fechaEmision" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="tipoIdentificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="razonSocialComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="identificacionComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="rise" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="codDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="numDocModificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="fechaEmisionDocSustento" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="totalSinImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
	 *         &lt;element name="valorModificacion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
	 *         &lt;element name="moneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element ref="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}totalConImpuestos"/>
	 *         &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "fechaEmision", "dirEstablecimiento",
			"tipoIdentificacionComprador", "razonSocialComprador",
			"identificacionComprador", "contribuyenteEspecial",
			"obligadoContabilidad", "rise", "codDocModificado",
			"numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos",
			"valorModificacion", "moneda", "totalConImpuestos", "motivo" })
	public static class InfoNotaCredito {

		@XmlElement(required = true)
		protected String fechaEmision;
		protected String dirEstablecimiento;
		@XmlElement(required = true)
		protected String tipoIdentificacionComprador;
		@XmlElement(required = true)
		protected String razonSocialComprador;
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
		protected BigDecimal valorModificacion;
		protected String moneda;
		@XmlElement(required = true)
		protected TotalConImpuestos totalConImpuestos;
		@XmlElement(required = true)
		protected String motivo;

		/**
		 * Obtiene el valor de la propiedad fechaEmision.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getFechaEmision() {
			return fechaEmision;
		}

		/**
		 * Define el valor de la propiedad fechaEmision.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setFechaEmision(String value) {
			this.fechaEmision = value;
		}

		/**
		 * Obtiene el valor de la propiedad dirEstablecimiento.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDirEstablecimiento() {
			return dirEstablecimiento;
		}

		/**
		 * Define el valor de la propiedad dirEstablecimiento.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDirEstablecimiento(String value) {
			this.dirEstablecimiento = value;
		}

		/**
		 * Obtiene el valor de la propiedad tipoIdentificacionComprador.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTipoIdentificacionComprador() {
			return tipoIdentificacionComprador;
		}

		/**
		 * Define el valor de la propiedad tipoIdentificacionComprador.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTipoIdentificacionComprador(String value) {
			this.tipoIdentificacionComprador = value;
		}

		/**
		 * Obtiene el valor de la propiedad razonSocialComprador.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getRazonSocialComprador() {
			return razonSocialComprador;
		}

		/**
		 * Define el valor de la propiedad razonSocialComprador.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setRazonSocialComprador(String value) {
			this.razonSocialComprador = value;
		}

		/**
		 * Obtiene el valor de la propiedad identificacionComprador.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getIdentificacionComprador() {
			return identificacionComprador;
		}

		/**
		 * Define el valor de la propiedad identificacionComprador.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setIdentificacionComprador(String value) {
			this.identificacionComprador = value;
		}

		/**
		 * Obtiene el valor de la propiedad contribuyenteEspecial.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getContribuyenteEspecial() {
			return contribuyenteEspecial;
		}

		/**
		 * Define el valor de la propiedad contribuyenteEspecial.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setContribuyenteEspecial(String value) {
			this.contribuyenteEspecial = value;
		}

		/**
		 * Obtiene el valor de la propiedad obligadoContabilidad.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getObligadoContabilidad() {
			return obligadoContabilidad;
		}

		/**
		 * Define el valor de la propiedad obligadoContabilidad.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setObligadoContabilidad(String value) {
			this.obligadoContabilidad = value;
		}

		/**
		 * Obtiene el valor de la propiedad rise.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getRise() {
			return rise;
		}

		/**
		 * Define el valor de la propiedad rise.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setRise(String value) {
			this.rise = value;
		}

		/**
		 * Obtiene el valor de la propiedad codDocModificado.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodDocModificado() {
			return codDocModificado;
		}

		/**
		 * Define el valor de la propiedad codDocModificado.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodDocModificado(String value) {
			this.codDocModificado = value;
		}

		/**
		 * Obtiene el valor de la propiedad numDocModificado.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getNumDocModificado() {
			return numDocModificado;
		}

		/**
		 * Define el valor de la propiedad numDocModificado.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setNumDocModificado(String value) {
			this.numDocModificado = value;
		}

		/**
		 * Obtiene el valor de la propiedad fechaEmisionDocSustento.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getFechaEmisionDocSustento() {
			return fechaEmisionDocSustento;
		}

		/**
		 * Define el valor de la propiedad fechaEmisionDocSustento.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setFechaEmisionDocSustento(String value) {
			this.fechaEmisionDocSustento = value;
		}

		/**
		 * Obtiene el valor de la propiedad totalSinImpuestos.
		 * 
		 * @return possible object is {@link BigDecimal }
		 * 
		 */
		public BigDecimal getTotalSinImpuestos() {
			return totalSinImpuestos;
		}

		/**
		 * Define el valor de la propiedad totalSinImpuestos.
		 * 
		 * @param value
		 *            allowed object is {@link BigDecimal }
		 * 
		 */
		public void setTotalSinImpuestos(BigDecimal value) {
			this.totalSinImpuestos = value;
		}

		/**
		 * Obtiene el valor de la propiedad valorModificacion.
		 * 
		 * @return possible object is {@link BigDecimal }
		 * 
		 */
		public BigDecimal getValorModificacion() {
			return valorModificacion;
		}

		/**
		 * Define el valor de la propiedad valorModificacion.
		 * 
		 * @param value
		 *            allowed object is {@link BigDecimal }
		 * 
		 */
		public void setValorModificacion(BigDecimal value) {
			this.valorModificacion = value;
		}

		/**
		 * Obtiene el valor de la propiedad moneda.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getMoneda() {
			return moneda;
		}

		/**
		 * Define el valor de la propiedad moneda.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setMoneda(String value) {
			this.moneda = value;
		}

		/**
		 * Obtiene el valor de la propiedad totalConImpuestos.
		 * 
		 * @return possible object is {@link TotalConImpuestos }
		 * 
		 */
		public TotalConImpuestos getTotalConImpuestos() {
			return totalConImpuestos;
		}

		/**
		 * Define el valor de la propiedad totalConImpuestos.
		 * 
		 * @param value
		 *            allowed object is {@link TotalConImpuestos }
		 * 
		 */
		public void setTotalConImpuestos(TotalConImpuestos value) {
			this.totalConImpuestos = value;
		}

		/**
		 * Obtiene el valor de la propiedad motivo.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getMotivo() {
			return motivo;
		}

		/**
		 * Define el valor de la propiedad motivo.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setMotivo(String value) {
			this.motivo = value;
		}

	}

}
