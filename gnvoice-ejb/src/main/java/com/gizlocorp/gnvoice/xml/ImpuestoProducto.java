package com.gizlocorp.gnvoice.xml;

/*    */
/*    */public class ImpuestoProducto
/*    */{
	/*    */private Integer codigoProducto;
	/*    */private String codigoImpuesto;

	/*    */
	/*    */public ImpuestoProducto()
	/*    */{
		/*    */}

	/*    */
	/*    */public ImpuestoProducto(String codigoImpuesto)
	/*    */{
		/* 20 */this.codigoImpuesto = codigoImpuesto;
		/*    */}

	/*    */
	/*    */public Integer getCodigoProducto()
	/*    */{
		/* 27 */return this.codigoProducto;
		/*    */}

	/*    */
	/*    */public void setCodigoProducto(Integer codigoProducto)
	/*    */{
		/* 34 */this.codigoProducto = codigoProducto;
		/*    */}

	/*    */
	/*    */public String getCodigoImpuesto()
	/*    */{
		/* 41 */return this.codigoImpuesto;
		/*    */}

	/*    */
	/*    */public void setCodigoImpuesto(String codigoImpuesto)
	/*    */{
		/* 48 */this.codigoImpuesto = codigoImpuesto;
		/*    */}
	/*    */
}

/*
 * Location: /Applications/ComprobantesElectronicos/ComprobantesDesktop.jar
 * Qualified Name:
 * ec.gob.sri.comprobantes.administracion.modelo.ImpuestoProducto JD-Core
 * Version: 0.6.0
 */