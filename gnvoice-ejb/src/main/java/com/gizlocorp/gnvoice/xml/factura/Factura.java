package com.gizlocorp.gnvoice.xml.factura;

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

import com.gizlocorp.gnvoice.xml.InfoTributaria;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "infoTributaria", "infoFactura", "detalles","infoAdicional" })
@XmlRootElement(name = "factura")
public class Factura implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2491019260468327168L;
	@XmlElement(required = true)
	protected InfoTributaria infoTributaria;
	@XmlElement(required = true)
	protected InfoFactura infoFactura;
	@XmlElement(required = true)
	protected Detalles detalles;
	protected InfoAdicional infoAdicional;
	@XmlAttribute
	protected String id;
	@XmlAttribute
	@XmlSchemaType(name = "anySimpleType")
	protected String version;

	public InfoTributaria getInfoTributaria() {
		return this.infoTributaria;
	}

	public void setInfoTributaria(InfoTributaria value) {
		this.infoTributaria = value;
	}

	public InfoFactura getInfoFactura() {
		return this.infoFactura;
	}

	public void setInfoFactura(InfoFactura value) {
		this.infoFactura = value;
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
	public static class Detalles implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1738272480964808323L;
		@XmlElement(required = true)
		protected List<Detalle> detalle;

		public List<Detalle> getDetalle() {
			if (this.detalle == null) {
				this.detalle = new ArrayList();
			}
			return this.detalle;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "codigoPrincipal", "codigoAuxiliar",
				"descripcion", "cantidad", "precioUnitario", "descuento",
				"precioTotalSinImpuesto", "detallesAdicionales", "impuestos" })
		public static class Detalle implements Serializable {
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
			protected DetallesAdicionales detallesAdicionales;
			@XmlElement(required = true)
			protected Impuestos impuestos;

			public String getCodigoPrincipal() {
				return this.codigoPrincipal;
			}

			public void setCodigoPrincipal(String value) {
				this.codigoPrincipal = value;
			}

			public String getCodigoAuxiliar() {
				return this.codigoAuxiliar;
			}

			public void setCodigoAuxiliar(String value) {
				this.codigoAuxiliar = value;
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
			public static class DetallesAdicionales implements Serializable {
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
				public static class DetAdicional implements Serializable {
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
			public static class Impuestos implements Serializable {
				@XmlElement(required = true)
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
	public static class InfoAdicional implements Serializable {
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
		public static class CampoAdicional implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8147904294050138903L;
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
			"contribuyenteEspecial", "obligadoContabilidad",
			"tipoIdentificacionComprador", "guiaRemision",
			"razonSocialComprador", "identificacionComprador","direccionComprador",
			"totalSinImpuestos", "totalDescuento", "totalConImpuestos","compensaciones",
			"propina", "importeTotal", "moneda","pagos" })
	public static class InfoFactura implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -846353447639760508L;
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
		protected TotalConImpuestos totalConImpuestos;
		@XmlElement(required = true)
		protected BigDecimal propina;
		@XmlElement(required = true)
		protected BigDecimal importeTotal;
		protected String moneda;
		@XmlElement(required = true)
		protected Pagos pagos;
		@XmlElement(required = true)
		protected Compensaciones compensaciones;
		@XmlElement(required = false)
		protected String direccionComprador;

		public String getDireccionComprador() {
			return direccionComprador;
		}

		public void setDireccionComprador(String direccionComprador) {
			this.direccionComprador = direccionComprador;
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

		public String getTipoIdentificacionComprador() {
			return this.tipoIdentificacionComprador;
		}

		public void setTipoIdentificacionComprador(String value) {
			this.tipoIdentificacionComprador = value;
		}

		public String getGuiaRemision() {
			return this.guiaRemision;
		}

		public void setGuiaRemision(String value) {
			this.guiaRemision = value;
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

		public BigDecimal getTotalSinImpuestos() {
			return this.totalSinImpuestos;
		}

		public void setTotalSinImpuestos(BigDecimal value) {
			this.totalSinImpuestos = value;
		}

		public BigDecimal getTotalDescuento() {
			return this.totalDescuento;
		}

		public void setTotalDescuento(BigDecimal value) {
			this.totalDescuento = value;
		}

		public TotalConImpuestos getTotalConImpuestos() {
			return this.totalConImpuestos;
		}

		public void setTotalConImpuestos(TotalConImpuestos value) {
			this.totalConImpuestos = value;
		}

		public Pagos getPagos() {
			return pagos;
		}

		public void setPagos(Pagos pagos) {
			this.pagos = pagos;
		}

		public Compensaciones getCompensaciones() {
			return compensaciones;
		}

		public void setCompensaciones(Compensaciones compensaciones) {
			this.compensaciones = compensaciones;
		}

		public BigDecimal getPropina() {
			return this.propina;
		}

		public void setPropina(BigDecimal value) {
			this.propina = value;
		}

		public BigDecimal getImporteTotal() {
			return this.importeTotal;
		}

		public void setImporteTotal(BigDecimal value) {
			this.importeTotal = value;
		}

		public String getMoneda() {
			return this.moneda;
		}

		public void setMoneda(String value) {
			this.moneda = value;
		}
		
		//pagos
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "pago" })
				public static class Pagos implements Serializable {
					/**
					 * 
					 */
					private static final long serialVersionUID = -3453626771381063138L;
					/**
					 * 
					 */
					@XmlElement(required = true)
					protected List<Pago> pago;

					public List<Pago> getPago() {
						if (this.pago == null) {
							this.pago = new ArrayList();
						}
						return this.pago;
					}

					@XmlAccessorType(XmlAccessType.FIELD)
					@XmlType(name = "", propOrder = { "formaPago", "total",
							"plazo", "unidadTiempo" })
					public static class Pago implements Serializable {
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;
						@XmlElement(required = true)
						protected String formaPago;
						@XmlElement(required = true)
						protected BigDecimal total;
						@XmlElement(required = true)
						protected BigDecimal plazo;
						@XmlElement(required = true)
						protected String unidadTiempo;
						
						public String getFormaPago() {
							return formaPago;
						}
						public void setFormaPago(String formaPago) {
							this.formaPago = formaPago;
						}
						public BigDecimal getTotal() {
							return total;
						}
						public void setTotal(BigDecimal total) {
							this.total = total;
						}
						public BigDecimal getPlazo() {
							return plazo;
						}
						public void setPlazo(BigDecimal plazo) {
							this.plazo = plazo;
						}
						public String getUnidadTiempo() {
							return unidadTiempo;
						}
						public void setUnidadTiempo(String unidadTiempo) {
							this.unidadTiempo = unidadTiempo;
						}
						
					}
				}
				//pagos

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "totalImpuesto" })
		public static class TotalConImpuestos implements Serializable {
			@XmlElement(required = true)
			protected List<TotalImpuesto> totalImpuesto;

			public List<TotalImpuesto> getTotalImpuesto() {
				if (this.totalImpuesto == null) {
					this.totalImpuesto = new ArrayList();
				}
				return this.totalImpuesto;
			}

			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "codigo", "codigoPorcentaje",
					"baseImponible", "tarifa", "valor" })
			public static class TotalImpuesto implements Serializable {
				@XmlElement(required = true)
				protected String codigo;
				@XmlElement(required = true)
				protected String codigoPorcentaje;
				@XmlElement(required = true)
				protected BigDecimal baseImponible;
				protected BigDecimal tarifa;
				@XmlElement(required = true)
				protected BigDecimal valor;

				public String getCodigo() {
					return this.codigo;
				}

				public void setCodigo(String value) {
					this.codigo = value;
				}

				public String getCodigoPorcentaje() {
					return this.codigoPorcentaje;
				}

				public void setCodigoPorcentaje(String value) {
					this.codigoPorcentaje = value;
				}

				public BigDecimal getBaseImponible() {
					return this.baseImponible;
				}

				public void setBaseImponible(BigDecimal value) {
					this.baseImponible = value;
				}

				public BigDecimal getTarifa() {
					return this.tarifa;
				}

				public void setTarifa(BigDecimal value) {
					this.tarifa = value;
				}

				public BigDecimal getValor() {
					return this.valor;
				}

				public void setValor(BigDecimal value) {
					this.valor = value;
				}
			}
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
