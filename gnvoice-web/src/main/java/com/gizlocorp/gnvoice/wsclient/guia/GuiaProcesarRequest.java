package com.gizlocorp.gnvoice.wsclient.guia;

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
 * Clase Java para guiaProcesarRequest complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="guiaProcesarRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guia" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="infoTributaria" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}infoTributaria"/>
 *                   &lt;element name="infoGuiaRemision">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="dirPartida" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="razonSocialTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="tipoIdentificacionTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="rucTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="rise" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="fechaIniTransporte" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="fechaFinTransporte" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="destinatarios">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="destinatario" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}destinatario" maxOccurs="unbounded"/>
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
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *          &lt;element name="codigoExterno" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="correoElectronicoNotificacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "guiaProcesarRequest", propOrder = { "guia", "codigoExterno",
		"correoElectronicoNotificacion" })
@XmlRootElement(name = "guiaProcesarRequest")
public class GuiaProcesarRequest {

	protected GuiaProcesarRequest.Guia guia;
	@XmlElement(required = true)
	protected String codigoExterno;
	@XmlElement(required = true)
	protected String correoElectronicoNotificacion;

	/**
	 * Obtiene el valor de la propiedad guia.
	 * 
	 * @return possible object is {@link GuiaProcesarRequest.Guia }
	 * 
	 */
	public GuiaProcesarRequest.Guia getGuia() {
		return guia;
	}

	/**
	 * Define el valor de la propiedad guia.
	 * 
	 * @param value
	 *            allowed object is {@link GuiaProcesarRequest.Guia }
	 * 
	 */
	public void setGuia(GuiaProcesarRequest.Guia value) {
		this.guia = value;
	}

	/**
	 * Obtiene el valor de la propiedad codigoExterno.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodigoExterno() {
		return codigoExterno;
	}

	/**
	 * Define el valor de la propiedad codigoExterno.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	/**
	 * Obtiene el valor de la propiedad correoElectronicoNotificacion.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCorreoElectronicoNotificacion() {
		return correoElectronicoNotificacion;
	}

	/**
	 * Define el valor de la propiedad correoElectronicoNotificacion.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCorreoElectronicoNotificacion(String value) {
		this.correoElectronicoNotificacion = value;
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
	 *         &lt;element name="infoTributaria" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}infoTributaria"/>
	 *         &lt;element name="infoGuiaRemision">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="dirPartida" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="razonSocialTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="tipoIdentificacionTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="rucTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="rise" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="fechaIniTransporte" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="fechaFinTransporte" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                 &lt;/sequence>
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *         &lt;element name="destinatarios">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="destinatario" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}destinatario" maxOccurs="unbounded"/>
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
	@XmlType(name = "", propOrder = { "infoTributaria", "infoGuiaRemision",
			"destinatarios", "infoAdicional" })
	public static class Guia {

		@XmlElement(required = true)
		protected InfoTributaria infoTributaria;
		@XmlElement(required = true)
		protected GuiaProcesarRequest.Guia.InfoGuiaRemision infoGuiaRemision;
		@XmlElement(required = true)
		protected GuiaProcesarRequest.Guia.Destinatarios destinatarios;
		protected GuiaProcesarRequest.Guia.InfoAdicional infoAdicional;
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
		 * Obtiene el valor de la propiedad infoGuiaRemision.
		 * 
		 * @return possible object is
		 *         {@link GuiaProcesarRequest.Guia.InfoGuiaRemision }
		 * 
		 */
		public GuiaProcesarRequest.Guia.InfoGuiaRemision getInfoGuiaRemision() {
			return infoGuiaRemision;
		}

		/**
		 * Define el valor de la propiedad infoGuiaRemision.
		 * 
		 * @param value
		 *            allowed object is
		 *            {@link GuiaProcesarRequest.Guia.InfoGuiaRemision }
		 * 
		 */
		public void setInfoGuiaRemision(
				GuiaProcesarRequest.Guia.InfoGuiaRemision value) {
			this.infoGuiaRemision = value;
		}

		/**
		 * Obtiene el valor de la propiedad destinatarios.
		 * 
		 * @return possible object is
		 *         {@link GuiaProcesarRequest.Guia.Destinatarios }
		 * 
		 */
		public GuiaProcesarRequest.Guia.Destinatarios getDestinatarios() {
			return destinatarios;
		}

		/**
		 * Define el valor de la propiedad destinatarios.
		 * 
		 * @param value
		 *            allowed object is
		 *            {@link GuiaProcesarRequest.Guia.Destinatarios }
		 * 
		 */
		public void setDestinatarios(
				GuiaProcesarRequest.Guia.Destinatarios value) {
			this.destinatarios = value;
		}

		/**
		 * Obtiene el valor de la propiedad infoAdicional.
		 * 
		 * @return possible object is
		 *         {@link GuiaProcesarRequest.Guia.InfoAdicional }
		 * 
		 */
		public GuiaProcesarRequest.Guia.InfoAdicional getInfoAdicional() {
			return infoAdicional;
		}

		/**
		 * Define el valor de la propiedad infoAdicional.
		 * 
		 * @param value
		 *            allowed object is
		 *            {@link GuiaProcesarRequest.Guia.InfoAdicional }
		 * 
		 */
		public void setInfoAdicional(
				GuiaProcesarRequest.Guia.InfoAdicional value) {
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
		 * El siguiente fragmento de esquema especifica el contenido que se
		 * espera que haya en esta clase.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="destinatario" type="{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}destinatario" maxOccurs="unbounded"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "destinatario" })
		public static class Destinatarios {

			@XmlElement(required = true)
			protected List<Destinatario> destinatario;

			/**
			 * Gets the value of the destinatario property.
			 * 
			 * <p>
			 * This accessor method returns a reference to the live list, not a
			 * snapshot. Therefore any modification you make to the returned
			 * list will be present inside the JAXB object. This is why there is
			 * not a <CODE>set</CODE> method for the destinatario property.
			 * 
			 * <p>
			 * For example, to add a new item, do as follows:
			 * 
			 * <pre>
			 * getDestinatario().add(newItem);
			 * </pre>
			 * 
			 * 
			 * <p>
			 * Objects of the following type(s) are allowed in the list
			 * {@link Destinatario }
			 * 
			 * 
			 */
			public List<Destinatario> getDestinatario() {
				if (destinatario == null) {
					destinatario = new ArrayList<Destinatario>();
				}
				return this.destinatario;
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
			protected List<GuiaProcesarRequest.Guia.InfoAdicional.CampoAdicional> campoAdicional;

			/**
			 * Gets the value of the campoAdicional property.
			 * 
			 * <p>
			 * This accessor method returns a reference to the live list, not a
			 * snapshot. Therefore any modification you make to the returned
			 * list will be present inside the JAXB object. This is why there is
			 * not a <CODE>set</CODE> method for the campoAdicional property.
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
			 * {@link GuiaProcesarRequest.Guia.InfoAdicional.CampoAdicional }
			 * 
			 * 
			 */
			public List<GuiaProcesarRequest.Guia.InfoAdicional.CampoAdicional> getCampoAdicional() {
				if (campoAdicional == null) {
					campoAdicional = new ArrayList<GuiaProcesarRequest.Guia.InfoAdicional.CampoAdicional>();
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
		 * El siguiente fragmento de esquema especifica el contenido que se
		 * espera que haya en esta clase.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="dirEstablecimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="dirPartida" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="razonSocialTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="tipoIdentificacionTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="rucTransportista" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="rise" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="obligadoContabilidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="contribuyenteEspecial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="fechaIniTransporte" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="fechaFinTransporte" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "dirEstablecimiento", "dirPartida",
				"razonSocialTransportista", "tipoIdentificacionTransportista",
				"rucTransportista", "rise", "obligadoContabilidad",
				"contribuyenteEspecial", "fechaIniTransporte",
				"fechaFinTransporte", "placa" })
		public static class InfoGuiaRemision {

			protected String dirEstablecimiento;
			@XmlElement(required = true)
			protected String dirPartida;
			@XmlElement(required = true)
			protected String razonSocialTransportista;
			@XmlElement(required = true)
			protected String tipoIdentificacionTransportista;
			@XmlElement(required = true)
			protected String rucTransportista;
			protected String rise;
			protected String obligadoContabilidad;
			protected String contribuyenteEspecial;
			@XmlElement(required = true)
			protected String fechaIniTransporte;
			@XmlElement(required = true)
			protected String fechaFinTransporte;
			@XmlElement(required = true)
			protected String placa;

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
			 * Obtiene el valor de la propiedad dirPartida.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getDirPartida() {
				return dirPartida;
			}

			/**
			 * Define el valor de la propiedad dirPartida.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setDirPartida(String value) {
				this.dirPartida = value;
			}

			/**
			 * Obtiene el valor de la propiedad razonSocialTransportista.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getRazonSocialTransportista() {
				return razonSocialTransportista;
			}

			/**
			 * Define el valor de la propiedad razonSocialTransportista.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setRazonSocialTransportista(String value) {
				this.razonSocialTransportista = value;
			}

			/**
			 * Obtiene el valor de la propiedad tipoIdentificacionTransportista.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getTipoIdentificacionTransportista() {
				return tipoIdentificacionTransportista;
			}

			/**
			 * Define el valor de la propiedad tipoIdentificacionTransportista.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setTipoIdentificacionTransportista(String value) {
				this.tipoIdentificacionTransportista = value;
			}

			/**
			 * Obtiene el valor de la propiedad rucTransportista.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getRucTransportista() {
				return rucTransportista;
			}

			/**
			 * Define el valor de la propiedad rucTransportista.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setRucTransportista(String value) {
				this.rucTransportista = value;
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
			 * Obtiene el valor de la propiedad fechaIniTransporte.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getFechaIniTransporte() {
				return fechaIniTransporte;
			}

			/**
			 * Define el valor de la propiedad fechaIniTransporte.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setFechaIniTransporte(String value) {
				this.fechaIniTransporte = value;
			}

			/**
			 * Obtiene el valor de la propiedad fechaFinTransporte.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getFechaFinTransporte() {
				return fechaFinTransporte;
			}

			/**
			 * Define el valor de la propiedad fechaFinTransporte.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setFechaFinTransporte(String value) {
				this.fechaFinTransporte = value;
			}

			/**
			 * Obtiene el valor de la propiedad placa.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getPlaca() {
				return placa;
			}

			/**
			 * Define el valor de la propiedad placa.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setPlaca(String value) {
				this.placa = value;
			}

		}

	}

}
