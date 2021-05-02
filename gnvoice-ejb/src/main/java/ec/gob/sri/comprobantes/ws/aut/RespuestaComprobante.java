/*   1:    */ package ec.gob.sri.comprobantes.ws.aut;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;

/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlType;
/*   8:    */ 
/*   9:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  10:    */ @XmlType(name="respuestaComprobante", propOrder={"claveAccesoConsultada", "numeroComprobantes", "autorizaciones"})
/*  11:    */ public class RespuestaComprobante
/*  12:    */ {
/*  13:    */   protected String claveAccesoConsultada;
/*  14:    */   protected String numeroComprobantes;
/*  15:    */   protected Autorizaciones autorizaciones;
/*  16:    */   
/*  17:    */   public String getClaveAccesoConsultada()
/*  18:    */   {
/*  19: 63 */     return this.claveAccesoConsultada;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void setClaveAccesoConsultada(String value)
/*  23:    */   {
/*  24: 75 */     this.claveAccesoConsultada = value;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String getNumeroComprobantes()
/*  28:    */   {
/*  29: 87 */     return this.numeroComprobantes;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setNumeroComprobantes(String value)
/*  33:    */   {
/*  34: 99 */     this.numeroComprobantes = value;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Autorizaciones getAutorizaciones()
/*  38:    */   {
/*  39:111 */     return this.autorizaciones;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setAutorizaciones(Autorizaciones value)
/*  43:    */   {
/*  44:123 */     this.autorizaciones = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   @XmlAccessorType(XmlAccessType.FIELD)
/*  48:    */   @XmlType(name="", propOrder={"autorizacion"})
/*  49:    */   public static class Autorizaciones
/*  50:    */   {
/*  51:    */     protected List<Autorizacion> autorizacion;
/*  52:    */     
/*  53:    */     public List<Autorizacion> getAutorizacion()
/*  54:    */     {
/*  55:177 */       if (this.autorizacion == null) {
/*  56:178 */         this.autorizacion = new ArrayList();
/*  57:    */       }
/*  58:180 */       return this.autorizacion;
/*  59:    */     }
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante
 * JD-Core Version:    0.7.0.1
 */