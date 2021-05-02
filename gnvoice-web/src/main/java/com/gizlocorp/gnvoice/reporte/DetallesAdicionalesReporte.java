package com.gizlocorp.gnvoice.reporte;

import java.util.List;

/**
 *
 * @author gizlo 
 */
public class DetallesAdicionalesReporte {
    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String cantidad;
    private String descripcion;
    private String precioUnitario;
    private String precioTotalSinImpuesto;
    private String descuento;
    private String numeroComprobante;
    private String nombreComprobante;
    private String detalle1;
    private String detalle2;
    private String detalle3;
    private String fechaEmisionCcompModificado;
    private List<InformacionAdicional> infoAdicional;
    /**
     * NotaDebito
     */
    private String razonModificacion;
    private String valorModificacion;
        
    /*
     *Comprobante retencion 
     */
    private String baseImponible;
    private String nombreImpuesto;
    private String porcentajeRetener;
    private String valorRetenido;
    
    
    /**
     * @return the codigoPrincipal
     */
    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    /**
     * @param codigoPrincipal the codigoPrincipal to set
     */
    public void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
    }

    /**
     * @return the codigoAuxiliar
     */
    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    /**
     * @param codigoAuxiliar the codigoAuxiliar to set
     */
    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    /**
     * @return the cantidad
     */
    public String getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the precioUnitario
     */
    public String getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * @param precioUnitario the precioUnitario to set
     */
    public void setPrecioUnitario(String precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * @return the precioTotalSinImpuesto
     */
    public String getPrecioTotalSinImpuesto() {
        return precioTotalSinImpuesto;
    }

    /**
     * @param precioTotalSinImpuesto the precioTotalSinImpuesto to set
     */
    public void setPrecioTotalSinImpuesto(String precioTotalSinImpuesto) {
        this.precioTotalSinImpuesto = precioTotalSinImpuesto;
    }

    /**
     * @return the detalle1
     */
    public String getDetalle1() {
        return detalle1;
    }

    /**
     * @param detalle1 the detalle1 to set
     */
    public void setDetalle1(String detalle1) {
        this.detalle1 = detalle1;
    }

    /**
     * @return the detalle2
     */
    public String getDetalle2() {
        return detalle2;
    }

    /**
     * @param detalle2 the detalle2 to set
     */
    public void setDetalle2(String detalle2) {
        this.detalle2 = detalle2;
    }

    /**
     * @return the detalle3
     */
    public String getDetalle3() {
        return detalle3;
    }

    /**
     * @param detalle3 the detalle3 to set
     */
    public void setDetalle3(String detalle3) {
        this.detalle3 = detalle3;
    }

    /**
     * @return the infoAdicional
     */
    public List<InformacionAdicional> getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * @param infoAdicional the infoAdicional to set
     */
    public void setInfoAdicional(List<InformacionAdicional> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    /**
     * @return the razonModificacion
     */
    public String getRazonModificacion() {
        return razonModificacion;
    }

    /**
     * @param razonModificacion the razonModificacion to set
     */
    public void setRazonModificacion(String razonModificacion) {
        this.razonModificacion = razonModificacion;
    }

    /**
     * @return the valorModificacion
     */
    public String getValorModificacion() {
        return valorModificacion;
    }

    /**
     * @param valorModificacion the valorModificacion to set
     */
    public void setValorModificacion(String valorModificacion) {
        this.valorModificacion = valorModificacion;
    }

    /**
     * @return the baseImponible
     */
    public String getBaseImponible() {
        return baseImponible;
    }

    /**
     * @param baseImponible the baseImponible to set
     */
    public void setBaseImponible(String baseImponible) {
        this.baseImponible = baseImponible;
    }

    /**
     * @return the nombreImpuesto
     */
    public String getNombreImpuesto() {
        return nombreImpuesto;
    }

    /**
     * @param nombreImpuesto the nombreImpuesto to set
     */
    public void setNombreImpuesto(String nombreImpuesto) {
        this.nombreImpuesto = nombreImpuesto;
    }

    /**
     * @return the porcentajeRetener
     */
    public String getPorcentajeRetener() {
        return porcentajeRetener;
    }

    /**
     * @param porcentajeRetener the porcentajeRetener to set
     */
    public void setPorcentajeRetener(String porcentajeRetener) {
        this.porcentajeRetener = porcentajeRetener;
    }

    /**
     * @return the valorRetenido
     */
    public String getValorRetenido() {
        return valorRetenido;
    }

    /**
     * @param valorRetenido the valorRetenido to set
     */
    public void setValorRetenido(String valorRetenido) {
        this.valorRetenido = valorRetenido;
    }

    /**
     * @return the descuento
     */
    public String getDescuento() {
        return descuento;
    }

    /**
     * @param descuento the descuento to set
     */
    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    /**
     * @return the numeroComprobante
     */
    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    /**
     * @param numeroComprobante the numeroComprobante to set
     */
    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    /**
     * @return the nombreComprobante
     */
    public String getNombreComprobante() {
        return nombreComprobante;
    }

    /**
     * @param nombreComprobante the nombreComprobante to set
     */
    public void setNombreComprobante(String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }

    /**
     * @return the fechaEmisionCcompModificado
     */
    public String getFechaEmisionCcompModificado() {
        return fechaEmisionCcompModificado;
    }

    /**
     * @param fechaEmisionCcompModificado the fechaEmisionCcompModificado to set
     */
    public void setFechaEmisionCcompModificado(String fechaEmisionCcompModificado) {
        this.fechaEmisionCcompModificado = fechaEmisionCcompModificado;
    }
    
}
