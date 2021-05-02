package com.gizlocorp.gnvoice.reporte;

/**
 *
 * @author gizlo  
 */
public class InformacionAdicional {
    private String valor;
    private String nombre;

    public InformacionAdicional(String valor, String nombre) {
        this.valor = valor;
        this.nombre = nombre;
    }

    public InformacionAdicional() {
    }
    
    

    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
