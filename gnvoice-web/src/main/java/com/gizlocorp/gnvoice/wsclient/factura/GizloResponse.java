package com.gizlocorp.gnvoice.wsclient.factura;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Clase Java para gizloResponse complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="gizloResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="claveAccesoComprobante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mensajeCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mensajeSistema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mensajes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="mensaje" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="mensaje" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="informacionAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 *         &lt;element name="numeroAutorizacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaAutorizacion" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gizloResponse", propOrder = { "claveAccesoComprobante",
		"estado", "mensajeCliente", "mensajeSistema", "mensajes",
		"numeroAutorizacion", "fechaAutorizacion" })
@XmlSeeAlso({ FacturaProcesarResponse.class, FacturaRecibirResponse.class,
		FacturaConsultarResponse.class })
public class GizloResponse {

	protected String claveAccesoComprobante;
	protected String estado;
	protected String mensajeCliente;
	protected String mensajeSistema;
	protected GizloResponse.Mensajes mensajes;
	protected String numeroAutorizacion;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar fechaAutorizacion;

	/**
	 * Obtiene el valor de la propiedad claveAccesoComprobante.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getClaveAccesoComprobante() {
		return claveAccesoComprobante;
	}

	/**
	 * Define el valor de la propiedad claveAccesoComprobante.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setClaveAccesoComprobante(String value) {
		this.claveAccesoComprobante = value;
	}

	/**
	 * Obtiene el valor de la propiedad estado.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * Define el valor de la propiedad estado.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEstado(String value) {
		this.estado = value;
	}

	/**
	 * Obtiene el valor de la propiedad mensajecliente.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMensajeCliente() {
		return mensajeCliente;
	}

	/**
	 * Define el valor de la propiedad mensajecliente.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMensajeCliente(String value) {
		this.mensajeCliente = value;
	}

	/**
	 * Obtiene el valor de la propiedad mensajesistema.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMensajeSistema() {
		return mensajeSistema;
	}

	/**
	 * Define el valor de la propiedad mensajesistema.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMensajeSistema(String value) {
		this.mensajeSistema = value;
	}

	/**
	 * Obtiene el valor de la propiedad mensajes.
	 * 
	 * @return possible object is {@link GizloResponse.Mensajes }
	 * 
	 */
	public GizloResponse.Mensajes getMensajes() {
		return mensajes;
	}

	/**
	 * Define el valor de la propiedad mensajes.
	 * 
	 * @param value
	 *            allowed object is {@link GizloResponse.Mensajes }
	 * 
	 */
	public void setMensajes(GizloResponse.Mensajes value) {
		this.mensajes = value;
	}

	/**
	 * Obtiene el valor de la propiedad numeroAutorizacion.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNumeroAutorizacion() {
		return numeroAutorizacion;
	}

	/**
	 * Define el valor de la propiedad numeroAutorizacion.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNumeroAutorizacion(String value) {
		this.numeroAutorizacion = value;
	}

	/**
	 * Obtiene el valor de la propiedad fechaAutorizacion.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	/**
	 * Define el valor de la propiedad fechaAutorizacion.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setFechaAutorizacion(XMLGregorianCalendar value) {
		this.fechaAutorizacion = value;
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
	 *         &lt;element name="mensaje" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="mensaje" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="informacionAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
	@XmlType(name = "", propOrder = { "mensaje" })
	public static class Mensajes {

		protected List<GizloResponse.Mensajes.Mensaje> mensaje;

		/**
		 * Gets the value of the mensaje property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the mensaje property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getMensaje().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link GizloResponse.Mensajes.Mensaje }
		 * 
		 * 
		 */
		public List<GizloResponse.Mensajes.Mensaje> getMensaje() {
			if (mensaje == null) {
				mensaje = new ArrayList<GizloResponse.Mensajes.Mensaje>();
			}
			return this.mensaje;
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
		 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="mensaje" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="informacionAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "identificador", "mensaje",
				"informacionAdicional", "tipo" })
		public static class Mensaje {

			protected String identificador;
			protected String mensaje;
			protected String informacionAdicional;
			protected String tipo;

			/**
			 * Obtiene el valor de la propiedad identificador.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getIdentificador() {
				return identificador;
			}

			/**
			 * Define el valor de la propiedad identificador.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setIdentificador(String value) {
				this.identificador = value;
			}

			/**
			 * Obtiene el valor de la propiedad mensaje.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getMensaje() {
				return mensaje;
			}

			/**
			 * Define el valor de la propiedad mensaje.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setMensaje(String value) {
				this.mensaje = value;
			}

			/**
			 * Obtiene el valor de la propiedad informacionAdicional.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getInformacionAdicional() {
				return informacionAdicional;
			}

			/**
			 * Define el valor de la propiedad informacionAdicional.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setInformacionAdicional(String value) {
				this.informacionAdicional = value;
			}

			/**
			 * Obtiene el valor de la propiedad tipo.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getTipo() {
				return tipo;
			}

			/**
			 * Define el valor de la propiedad tipo.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setTipo(String value) {
				this.tipo = value;
			}

		}

	}

}
