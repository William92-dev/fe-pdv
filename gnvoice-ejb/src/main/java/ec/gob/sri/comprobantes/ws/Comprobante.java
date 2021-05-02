/*   1:    */ package ec.gob.sri.comprobantes.ws;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;

/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlType;
/*   8:    */ 
/*   9:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  10:    */ @XmlType(name="comprobante", propOrder={"claveAcceso", "mensajes"})
/*  11:    */ public class Comprobante
/*  12:    */ {
/*  13:    */   protected String claveAcceso;
/*  14:    */   protected Mensajes mensajes;
/*  15:    */   
/*  16:    */   public String getClaveAcceso()
/*  17:    */   {
/*  18: 60 */     return this.claveAcceso;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void setClaveAcceso(String value)
/*  22:    */   {
/*  23: 72 */     this.claveAcceso = value;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Mensajes getMensajes()
/*  27:    */   {
/*  28: 84 */     return this.mensajes;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setMensajes(Mensajes value)
/*  32:    */   {
/*  33: 96 */     this.mensajes = value;
/*  34:    */   }
/*  35:    */   
/*  36:    */   @XmlAccessorType(XmlAccessType.FIELD)
/*  37:    */   @XmlType(name="", propOrder={"mensaje"})
/*  38:    */   public static class Mensajes
/*  39:    */   {
/*  40:    */     protected List<Mensaje> mensaje;
/*  41:    */     
/*  42:    */     public List<Mensaje> getMensaje()
/*  43:    */     {
/*  44:150 */       if (this.mensaje == null) {
/*  45:151 */         this.mensaje = new ArrayList();
/*  46:    */       }
/*  47:153 */       return this.mensaje;
/*  48:    */     }
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.Comprobante
 * JD-Core Version:    0.7.0.1
 */