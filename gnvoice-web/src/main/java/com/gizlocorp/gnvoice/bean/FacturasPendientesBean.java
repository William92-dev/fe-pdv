package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;

public class FacturasPendientesBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String farmacia                   ;
	private String documento_venta            ;
	private String fecha                      ;
	private String numero_sri                 ;
	private String costo_total_factura        ;
	private String pvp_total_factura          ;
	private String venta_total_factura        ;
	private String canal_venta                ;
	private String valor_iva                  ;
	private String cancelada                  ;
	private String tipo_documento             ;
	private String caja                       ;
	private String tipo_movimiento            ;
	private String clasificacion_movimiento   ;
	private String cliente                    ;
	private String documento_venta_padre      ;
	private String farmacia_padre             ;
	private String usuario                    ;
	private String empleado_realiza           ;
	private String empleado_cobra             ;
	private String persona                    ;
	private String primer_apellido            ;
	private String segundo_apellido           ;
	private String nombres                    ;
	private String identificacion             ;
	private String direccion                  ;
	private String fecha_sistema              ;
	private String donacion                   ;
	private String tratamiento_continuo       ;
	private String empleado_entrega           ;
	private String incluyeiva                 ;
	private String tomo_pedido_domicilio      ;
	private String direccion_envio            ;
	private String genero_nota_credito        ;
	private String error                      ;
	private String observacion_elec           ;
	private String fechaejecutado             ;
	private String tipo_comprobante           ;
	private String sid                        ;
	private String base                       ;
	private String fecha_f                    ;
	private String claveAcceso ;
	private String nombreFarmacias ;
	
	private String desEstado;
	
	 
	
	 
	public String getClaveAcceso() {
		return claveAcceso;
	}
	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}
	public String getNombreFarmacias() {
		return nombreFarmacias;
	}
	public void setNombreFarmacias(String nombreFarmacias) {
		this.nombreFarmacias = nombreFarmacias;
	}
	public String getFarmacia() {
		return farmacia;
	}
	public void setFarmacia(String farmacia) {
		this.farmacia = farmacia;
	}
	public String getDocumento_venta() {
		return documento_venta;
	}
	public void setDocumento_venta(String documento_venta) {
		this.documento_venta = documento_venta;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getNumero_sri() {
		return numero_sri;
	}
	public void setNumero_sri(String numero_sri) {
		this.numero_sri = numero_sri;
	}
	public String getCosto_total_factura() {
		return costo_total_factura;
	}
	public void setCosto_total_factura(String costo_total_factura) {
		this.costo_total_factura = costo_total_factura;
	}
	public String getPvp_total_factura() {
		return pvp_total_factura;
	}
	public void setPvp_total_factura(String pvp_total_factura) {
		this.pvp_total_factura = pvp_total_factura;
	}
	public String getVenta_total_factura() {
		return venta_total_factura;
	}
	public void setVenta_total_factura(String venta_total_factura) {
		this.venta_total_factura = venta_total_factura;
	}
	public String getCanal_venta() {
		return canal_venta;
	}
	public void setCanal_venta(String canal_venta) {
		this.canal_venta = canal_venta;
	}
	public String getValor_iva() {
		return valor_iva;
	}
	public void setValor_iva(String valor_iva) {
		this.valor_iva = valor_iva;
	}
	public String getCancelada() {
		return cancelada;
	}
	public void setCancelada(String cancelada) {
		this.cancelada = cancelada;
	}
	public String getTipo_documento() {
		return tipo_documento;
	}
	public void setTipo_documento(String tipo_documento) {
		this.tipo_documento = tipo_documento;
	}
	public String getCaja() {
		return caja;
	}
	public void setCaja(String caja) {
		this.caja = caja;
	}
	public String getTipo_movimiento() {
		return tipo_movimiento;
	}
	public void setTipo_movimiento(String tipo_movimiento) {
		this.tipo_movimiento = tipo_movimiento;
	}
	public String getClasificacion_movimiento() {
		return clasificacion_movimiento;
	}
	public void setClasificacion_movimiento(String clasificacion_movimiento) {
		this.clasificacion_movimiento = clasificacion_movimiento;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getDocumento_venta_padre() {
		return documento_venta_padre;
	}
	public void setDocumento_venta_padre(String documento_venta_padre) {
		this.documento_venta_padre = documento_venta_padre;
	}
	public String getFarmacia_padre() {
		return farmacia_padre;
	}
	public void setFarmacia_padre(String farmacia_padre) {
		this.farmacia_padre = farmacia_padre;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getEmpleado_realiza() {
		return empleado_realiza;
	}
	public void setEmpleado_realiza(String empleado_realiza) {
		this.empleado_realiza = empleado_realiza;
	}
	public String getEmpleado_cobra() {
		return empleado_cobra;
	}
	public void setEmpleado_cobra(String empleado_cobra) {
		this.empleado_cobra = empleado_cobra;
	}
	public String getPersona() {
		return persona;
	}
	public void setPersona(String persona) {
		this.persona = persona;
	}
	public String getPrimer_apellido() {
		return primer_apellido;
	}
	public void setPrimer_apellido(String primer_apellido) {
		this.primer_apellido = primer_apellido;
	}
	public String getSegundo_apellido() {
		return segundo_apellido;
	}
	public void setSegundo_apellido(String segundo_apellido) {
		this.segundo_apellido = segundo_apellido;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getFecha_sistema() {
		return fecha_sistema;
	}
	public void setFecha_sistema(String fecha_sistema) {
		this.fecha_sistema = fecha_sistema;
	}
	public String getDonacion() {
		return donacion;
	}
	public void setDonacion(String donacion) {
		this.donacion = donacion;
	}
	public String getTratamiento_continuo() {
		return tratamiento_continuo;
	}
	public void setTratamiento_continuo(String tratamiento_continuo) {
		this.tratamiento_continuo = tratamiento_continuo;
	}
	public String getEmpleado_entrega() {
		return empleado_entrega;
	}
	public void setEmpleado_entrega(String empleado_entrega) {
		this.empleado_entrega = empleado_entrega;
	}
	public String getIncluyeiva() {
		return incluyeiva;
	}
	public void setIncluyeiva(String incluyeiva) {
		this.incluyeiva = incluyeiva;
	}
	public String getTomo_pedido_domicilio() {
		return tomo_pedido_domicilio;
	}
	public void setTomo_pedido_domicilio(String tomo_pedido_domicilio) {
		this.tomo_pedido_domicilio = tomo_pedido_domicilio;
	}
	public String getDireccion_envio() {
		return direccion_envio;
	}
	public void setDireccion_envio(String direccion_envio) {
		this.direccion_envio = direccion_envio;
	}
	public String getGenero_nota_credito() {
		return genero_nota_credito;
	}
	public void setGenero_nota_credito(String genero_nota_credito) {
		this.genero_nota_credito = genero_nota_credito;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getObservacion_elec() {
		return observacion_elec;
	}
	public void setObservacion_elec(String observacion_elec) {
		this.observacion_elec = observacion_elec;
	}
	public String getFechaejecutado() {
		return fechaejecutado;
	}
	public void setFechaejecutado(String fechaejecutado) {
		this.fechaejecutado = fechaejecutado;
	}
	public String getTipo_comprobante() {
		return tipo_comprobante;
	}
	public void setTipo_comprobante(String tipo_comprobante) {
		this.tipo_comprobante = tipo_comprobante;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public String getFecha_f() {
		return fecha_f;
	}
	public void setFecha_f(String fecha_f) {
		this.fecha_f = fecha_f;
	}
	public String getDesEstado() {
		return desEstado;
	}
	public void setDesEstado(String desEstado) {
		this.desEstado = desEstado;
	}
	
	 
	
	
	
   
}
