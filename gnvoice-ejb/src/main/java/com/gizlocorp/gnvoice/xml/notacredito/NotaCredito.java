package com.gizlocorp.gnvoice.xml.notacredito;

import java.io.Serializable;
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

import com.gizlocorp.gnvoice.xml.InfoTributaria;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "infoTributaria", "infoNotaCredito",
		"detalles", "infoAdicional" })
@XmlRootElement(name = "notaCredito")
public class NotaCredito  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement(required = true)
	protected InfoTributaria infoTributaria;
	@XmlElement(required = true)
	protected InfoNotaCredito infoNotaCredito;
	@XmlElement(required = true)
	protected Detalles detalles;
	protected InfoAdicional infoAdicional;
	@XmlAttribute(required = true)
	protected String id;
	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NMTOKEN")
	protected String version;

	public InfoTributaria getInfoTributaria() {
		return this.infoTributaria;
	}

	public void setInfoTributaria(InfoTributaria value) {
		this.infoTributaria = value;
	}

	public InfoNotaCredito getInfoNotaCredito() {
		return this.infoNotaCredito;
	}

	public void setInfoNotaCredito(InfoNotaCredito value) {
		this.infoNotaCredito = value;
	}

	public Detalles getDetalles() {
		return this.detalles;
	}

	public void setDetalles(Detalles value) {
		this.detalles = value;
	}

	public InfoAdicional getInfoAdicional() {
		return this.infoAdicional;
	}

	public void setInfoAdicional(InfoAdicional value) {
		this.infoAdicional = value;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String value) {
		this.id = value;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String value) {
		this.version = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "detalle" })
	public static class Detalles  implements Serializable {
		@XmlElement(required = true)
		protected List<Detalle> detalle;

		public List<Detalle> getDetalle() {
			if (this.detalle == null) {
				this.detalle = new ArrayList();
			}
			return this.detalle;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "codigoInterno", "codigoAdicional",
				"descripcion", "cantidad", "precioUnitario", "descuento",
				"precioTotalSinImpuesto", "detallesAdicionales", "impuestos" })
		public static class Detalle  implements Serializable {
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
			protected DetallesAdicionales detallesAdicionales;
			@XmlElement(required = true)
			protected Impuestos impuestos;

			public String getCodigoInterno() {
				return this.codigoInterno;
			}

			public void setCodigoInterno(String value) {
				this.codigoInterno = value;
			}

			public String getCodigoAdicional() {
				return this.codigoAdicional;
			}

			public void setCodigoAdicional(String value) {
				this.codigoAdicional = value;
			}

			public String getDescripcion() {
				return this.descripcion;
			}

			public void setDescripcion(String value) {
				this.descripcion = value;
			}

			public BigDecimal getCantidad() {
				return this.cantidad;
			}

			public void setCantidad(BigDecimal value) {
				this.cantidad = value;
			}

			public BigDecimal getPrecioUnitario() {
				return this.precioUnitario;
			}

			public void setPrecioUnitario(BigDecimal value) {
				this.precioUnitario = value;
			}

			public BigDecimal getDescuento() {
				return this.descuento;
			}

			public void setDescuento(BigDecimal value) {
				this.descuento = value;
			}

			public BigDecimal getPrecioTotalSinImpuesto() {
				return this.precioTotalSinImpuesto;
			}

			public void setPrecioTotalSinImpuesto(BigDecimal value) {
				this.precioTotalSinImpuesto = value;
			}

			public DetallesAdicionales getDetallesAdicionales() {
				return this.detallesAdicionales;
			}

			public void setDetallesAdicionales(DetallesAdicionales value) {
				this.detallesAdicionales = value;
			}

			public Impuestos getImpuestos() {
				return this.impuestos;
			}

			public void setImpuestos(Impuestos value) {
				this.impuestos = value;
			}

			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "detAdicional" })
			public static class DetallesAdicionales  implements Serializable {
				@XmlElement(required = true)
				protected List<DetAdicional> detAdicional;

				public List<DetAdicional> getDetAdicional() {
					if (this.detAdicional == null) {
						this.detAdicional = new ArrayList();
					}
					return this.detAdicional;
				}

				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "")
				public static class DetAdicional  implements Serializable {
					@XmlAttribute
					protected String nombre;
					@XmlAttribute
					protected String valor;

					public String getNombre() {
						return this.nombre;
					}

					public void setNombre(String value) {
						this.nombre = value;
					}

					public String getValor() {
						return this.valor;
					}

					public void setValor(String value) {
						this.valor = value;
					}
				}
			}

			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "impuesto" })
			public static class Impuestos  implements Serializable {
				protected List<Impuesto> impuesto;

				public List<Impuesto> getImpuesto() {
					if (this.impuesto == null) {
						this.impuesto = new ArrayList();
					}
					return this.impuesto;
				}
			}
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "campoAdicional" })
	public static class InfoAdicional  implements Serializable {
		@XmlElement(required = true)
		protected List<CampoAdicional> campoAdicional;

		public List<CampoAdicional> getCampoAdicional() {
			if (this.campoAdicional == null) {
				this.campoAdicional = new ArrayList();
			}
			return this.campoAdicional;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "value" })
		public static class CampoAdicional  implements Serializable {
			@XmlValue
			protected String value;
			@XmlAttribute
			protected String nombre;

			public String getValue() {
				return this.value;
			}

			public void setValue(String value) {
				this.value = value;
			}

			public String getNombre() {
				return this.nombre;
			}

			public void setNombre(String value) {
				this.nombre = value;
			}
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "fechaEmision", "dirEstablecimiento",
			"tipoIdentificacionComprador", "razonSocialComprador",
			"identificacionComprador", "contribuyenteEspecial",
			"obligadoContabilidad", "rise", "codDocModificado",
			"numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos","compensaciones",
			"valorModificacion", "moneda", "totalConImpuestos", "motivo" })
	public static class InfoNotaCredito  implements Serializable {
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
		protected Compensaciones compensaciones;
		@XmlElement(required = true)
		protected String motivo;
		
		

		public Compensaciones getCompensaciones() {
			return compensaciones;
		}

		public void setCompensaciones(Compensaciones compensaciones) {
			this.compensaciones = compensaciones;
		}

		public String getFechaEmision() {
			return this.fechaEmision;
		}

		public void setFechaEmision(String value) {
			this.fechaEmision = value;
		}

		public String getDirEstablecimiento() {
			return this.dirEstablecimiento;
		}

		public void setDirEstablecimiento(String value) {
			this.dirEstablecimiento = value;
		}

		public String getTipoIdentificacionComprador() {
			return this.tipoIdentificacionComprador;
		}

		public void setTipoIdentificacionComprador(String value) {
			this.tipoIdentificacionComprador = value;
		}

		public String getRazonSocialComprador() {
			return this.razonSocialComprador;
		}

		public void setRazonSocialComprador(String value) {
			this.razonSocialComprador = value;
		}

		public String getIdentificacionComprador() {
			return this.identificacionComprador;
		}

		public void setIdentificacionComprador(String value) {
			this.identificacionComprador = value;
		}

		public String getContribuyenteEspecial() {
			return this.contribuyenteEspecial;
		}

		public void setContribuyenteEspecial(String value) {
			this.contribuyenteEspecial = value;
		}

		public String getObligadoContabilidad() {
			return this.obligadoContabilidad;
		}

		public void setObligadoContabilidad(String value) {
			this.obligadoContabilidad = value;
		}

		public String getRise() {
			return this.rise;
		}

		public void setRise(String value) {
			this.rise = value;
		}

		public String getCodDocModificado() {
			return this.codDocModificado;
		}

		public void setCodDocModificado(String value) {
			this.codDocModificado = value;
		}

		public String getNumDocModificado() {
			return this.numDocModificado;
		}

		public void setNumDocModificado(String value) {
			this.numDocModificado = value;
		}

		public String getFechaEmisionDocSustento() {
			return this.fechaEmisionDocSustento;
		}

		public void setFechaEmisionDocSustento(String value) {
			this.fechaEmisionDocSustento = value;
		}

		public BigDecimal getTotalSinImpuestos() {
			return this.totalSinImpuestos;
		}

		public void setTotalSinImpuestos(BigDecimal value) {
			this.totalSinImpuestos = value;
		}

		public BigDecimal getValorModificacion() {
			return this.valorModificacion;
		}

		public void setValorModificacion(BigDecimal value) {
			this.valorModificacion = value;
		}

		public String getMoneda() {
			return this.moneda;
		}

		public void setMoneda(String value) {
			this.moneda = value;
		}

		public TotalConImpuestos getTotalConImpuestos() {
			return this.totalConImpuestos;
		}

		public void setTotalConImpuestos(TotalConImpuestos value) {
			this.totalConImpuestos = value;
		}

		public String getMotivo() {
			return this.motivo;
		}

		public void setMotivo(String value) {
			this.motivo = value;
		}
		
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "compensacion" })
		public static class Compensaciones implements Serializable {
			@XmlElement(required = true)
			protected List<Compensacion> compensacion;

			public List<Compensacion> getCompensacion() {
				if (this.compensacion == null) {
					this.compensacion = new ArrayList();
				}
				return this.compensacion;
			}

			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "codigo","tarifa", "valor" })
			public static class Compensacion implements Serializable {
				
				@XmlElement(required = true)
				protected BigDecimal codigo;
				protected BigDecimal tarifa;
				@XmlElement(required = true)
				protected BigDecimal valor;
				public BigDecimal getCodigo() {
					return codigo;
				}
				public void setCodigo(BigDecimal codigo) {
					this.codigo = codigo;
				}
				public BigDecimal getTarifa() {
					return tarifa;
				}
				public void setTarifa(BigDecimal tarifa) {
					this.tarifa = tarifa;
				}
				public BigDecimal getValor() {
					return valor;
				}
				public void setValor(BigDecimal valor) {
					this.valor = valor;
				}				
			}
		}
	}
}
