package com.gizlocorp.gnvoice.xml;

/*     */
/*     */import java.util.Date;

/*     */
/*     */public class ImpuestoValor
/*     */{
	/*     */private String codigo;
	/*     */private Integer codigoImpuesto;
	/*     */private Double porcentaje;
	/*     */private Double porcentajeRentencion;
	/*     */private String tipoImpuesto;
	/*     */private String descripcion;
	/*     */private Date fechaInicio;
	/*     */private Date fechaFin;

	/*     */
	/*     */public String getCodigo()
	/*     */{
		/* 28 */return this.codigo;
		/*     */}

	/*     */
	/*     */public ImpuestoValor() {
		/*     */}

	/*     */
	/*     */public ImpuestoValor(ImpuestoValor i) {
		/* 35 */this.codigo = i.getCodigo();
		/* 36 */this.codigoImpuesto = i.getCodigoImpuesto();
		/* 37 */this.porcentaje = i.getPorcentaje();
		/* 38 */this.porcentajeRentencion = i.getPorcentajeRentencion();
		/* 39 */this.tipoImpuesto = i.getTipoImpuesto();
		/* 40 */this.descripcion = i.getDescripcion();
		/* 41 */this.fechaInicio = i.getFechaInicio();
		/* 42 */this.fechaFin = i.getFechaFin();
		/*     */}

	/*     */
	/*     */public ImpuestoValor(Integer codigoImpuesto, String tipoImpuesto) {
		/* 46 */this.codigoImpuesto = codigoImpuesto;
		/* 47 */this.tipoImpuesto = tipoImpuesto;
		/*     */}

	/*     */
	/*     */public void setCodigo(String codigo)
	/*     */{
		/* 54 */this.codigo = codigo;
		/*     */}

	/*     */
	/*     */public Integer getCodigoImpuesto()
	/*     */{
		/* 61 */return this.codigoImpuesto;
		/*     */}

	/*     */
	/*     */public void setCodigoImpuesto(Integer codigoImpuesto)
	/*     */{
		/* 68 */this.codigoImpuesto = codigoImpuesto;
		/*     */}

	/*     */
	/*     */public Double getPorcentaje()
	/*     */{
		/* 75 */return this.porcentaje;
		/*     */}

	/*     */
	/*     */public void setPorcentaje(Double porcentaje)
	/*     */{
		/* 82 */this.porcentaje = porcentaje;
		/*     */}

	/*     */
	/*     */public Double getPorcentajeRentencion()
	/*     */{
		/* 89 */return this.porcentajeRentencion;
		/*     */}

	/*     */
	/*     */public void setPorcentajeRentencion(Double porcentajeRentencion)
	/*     */{
		/* 96 */this.porcentajeRentencion = porcentajeRentencion;
		/*     */}

	/*     */
	/*     */public String getTipoImpuesto()
	/*     */{
		/* 103 */return this.tipoImpuesto;
		/*     */}

	/*     */
	/*     */public void setTipoImpuesto(String tipoImpuesto)
	/*     */{
		/* 110 */this.tipoImpuesto = tipoImpuesto;
		/*     */}

	/*     */
	/*     */public String getDescripcion()
	/*     */{
		/* 117 */return this.descripcion;
		/*     */}

	/*     */
	/*     */public void setDescripcion(String descripcion)
	/*     */{
		/* 124 */this.descripcion = descripcion;
		/*     */}

	/*     */
	/*     */public Date getFechaFin() {
		/* 128 */return this.fechaFin;
		/*     */}

	/*     */
	/*     */public void setFechaFin(Date fechaFin) {
		/* 132 */this.fechaFin = fechaFin;
		/*     */}

	/*     */
	/*     */public Date getFechaInicio() {
		/* 136 */return this.fechaInicio;
		/*     */}

	/*     */
	/*     */public void setFechaInicio(Date fechaInicio) {
		/* 140 */this.fechaInicio = fechaInicio;
		/*     */}

	/*     */
	/*     */public String toString()
	/*     */{
		/* 147 */if (((this.codigoImpuesto.intValue() == 1)
				|| (this.codigoImpuesto.intValue() == 2) || (this.codigoImpuesto
				.intValue() == 3))
				&& ((this.tipoImpuesto.equals("R"))
						|| (this.tipoImpuesto.equals("I")) || (this.tipoImpuesto
							.equals("A")))) {
			/* 148 */if (getCodigo() != null) {
				/* 149 */return getCodigo() + " - " + getDescripcion();
				/*     */}
			/* 151 */return "Seleccione....";
			/*     */}
		/*     */
		/* 154 */return null;
		/*     */}
	/*     */
}

/*
 * Location: /Applications/ComprobantesElectronicos/ComprobantesDesktop.jar
 * Qualified Name: ec.gob.sri.comprobantes.administracion.modelo.ImpuestoValor
 * JD-Core Version: 0.6.0
 */