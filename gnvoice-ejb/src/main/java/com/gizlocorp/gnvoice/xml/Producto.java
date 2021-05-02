package com.gizlocorp.gnvoice.xml;

/*     */
/*     *//*     */
import java.math.BigDecimal;
/*     */
import java.util.List;

/*     */
/*     */public class Producto
/*     */{
	/*     */private Integer codigo;
	/*     */private String codigoPrincipal;
	/*     */private String codigoAuxiliar;
	/*     */private String nombre;
	/*     */private BigDecimal valorUnitario;
	/*     */private String tipoProducto;
	/*     */private List<InformacionAdicionalProducto> infoAdicionalList;
	/*     */private List<ImpuestoProducto> impuestoProducto;
	/*     */private List<ImpuestoValor> impuestoValor;
	/*     */private Integer codigoImpuesto;
	/*     */private String iva;
	/*     */private String ice;
	/*     */private Double cantidad;

	/*     */
	/*     */public Integer getCodigo()
	/*     */{
		/* 32 */return this.codigo;
		/*     */}

	/*     */
	/*     */public void setCodigo(Integer codigo)
	/*     */{
		/* 39 */this.codigo = codigo;
		/*     */}

	/*     */
	/*     */public String getCodigoPrincipal()
	/*     */{
		/* 46 */return this.codigoPrincipal;
		/*     */}

	/*     */
	/*     */public void setCodigoPrincipal(String codigoPrincipal)
	/*     */{
		/* 53 */this.codigoPrincipal = codigoPrincipal;
		/*     */}

	/*     */
	/*     */public String getCodigoAuxiliar()
	/*     */{
		/* 60 */return this.codigoAuxiliar;
		/*     */}

	/*     */
	/*     */public void setCodigoAuxiliar(String codigoAuxiliar)
	/*     */{
		/* 67 */this.codigoAuxiliar = codigoAuxiliar;
		/*     */}

	/*     */
	/*     */public String getNombre()
	/*     */{
		/* 74 */return this.nombre;
		/*     */}

	/*     */
	/*     */public void setNombre(String nombre)
	/*     */{
		/* 81 */this.nombre = nombre;
		/*     */}

	/*     */
	/*     */public BigDecimal getValorUnitario()
	/*     */{
		/* 88 */return this.valorUnitario;
		/*     */}

	/*     */
	/*     */public void setValorUnitario(BigDecimal valorUnitario)
	/*     */{
		/* 95 */this.valorUnitario = valorUnitario;
		/*     */}

	/*     */
	/*     */public String getTipoProducto()
	/*     */{
		/* 102 */return this.tipoProducto;
		/*     */}

	/*     */
	/*     */public void setTipoProducto(String tipoProducto)
	/*     */{
		/* 109 */this.tipoProducto = tipoProducto;
		/*     */}

	/*     */
	/*     */public List<InformacionAdicionalProducto> getInfoAdicionalList()
	/*     */{
		/* 116 */return this.infoAdicionalList;
		/*     */}

	/*     */
	/*     */public void setInfoAdicionalList(
			List<InformacionAdicionalProducto> infoAdicionalList)
	/*     */{
		/* 123 */this.infoAdicionalList = infoAdicionalList;
		/*     */}

	/*     */
	/*     */public List<ImpuestoProducto> getImpuestoProducto()
	/*     */{
		/* 130 */return this.impuestoProducto;
		/*     */}

	/*     */
	/*     */public void setImpuestoProducto(List<ImpuestoProducto> impuestoProducto)
	/*     */{
		/* 137 */this.impuestoProducto = impuestoProducto;
		/*     */}

	/*     */
	/*     */public String getIva()
	/*     */{
		/* 145 */return this.iva;
		/*     */}

	/*     */
	/*     */public void setIva(String iva)
	/*     */{
		/* 152 */this.iva = iva;
		/*     */}

	/*     */
	/*     */public String getIce()
	/*     */{
		/* 159 */return this.ice;
		/*     */}

	/*     */
	/*     */public void setIce(String ice)
	/*     */{
		/* 166 */this.ice = ice;
		/*     */}

	/*     */
	/*     */public List<ImpuestoValor> getImpuestoValor()
	/*     */{
		/* 173 */return this.impuestoValor;
		/*     */}

	/*     */
	/*     */public void setImpuestoValor(List<ImpuestoValor> impuestoValor)
	/*     */{
		/* 180 */this.impuestoValor = impuestoValor;
		/*     */}

	/*     */
	/*     */public Double getCantidad() {
		/* 184 */return this.cantidad;
		/*     */}

	/*     */
	/*     */public void setCantidad(Double cantidad) {
		/* 188 */this.cantidad = cantidad;
		/*     */}

	/*     */
	/*     */public Integer getCodigoImpuesto() {
		/* 192 */return this.codigoImpuesto;
		/*     */}

	/*     */
	/*     */public void setCodigoImpuesto(Integer codigoImpuesto) {
		/* 196 */this.codigoImpuesto = codigoImpuesto;
		/*     */}
	/*     */
}

/*
 * Location: /Applications/ComprobantesElectronicos/ComprobantesDesktop.jar
 * Qualified Name: ec.gob.sri.comprobantes.administracion.modelo.Producto
 * JD-Core Version: 0.6.0
 */