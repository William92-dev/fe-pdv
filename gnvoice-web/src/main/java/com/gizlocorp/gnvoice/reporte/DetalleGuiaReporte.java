package com.gizlocorp.gnvoice.reporte;

/**
 *
 * @author gizlo
 */
public class DetalleGuiaReporte {
    private String cantidad;
    private String descripcion;
    private String codigoPrincipal;
    private String codigoAuxiliar;

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
}
