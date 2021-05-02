/*   1:    */ package ec.gob.sri.comprobantes.ws;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;

/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   8:    */ import javax.xml.bind.annotation.XmlType;
/*   9:    */ 
/*  10:    */ @XmlRootElement
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="respuestaSolicitud", propOrder={"estado", "comprobantes"})
/*  13:    */ public class RespuestaSolicitud
/*  14:    */ {
/*  15:    */   protected String estado;
/*  16:    */   protected Comprobantes comprobantes;
/*  17:    */   
/*  18:    */   public String getEstado()
/*  19:    */   {
/*  20: 62 */     return this.estado;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void setEstado(String value)
/*  24:    */   {
/*  25: 74 */     this.estado = value;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public Comprobantes getComprobantes()
/*  29:    */   {
/*  30: 86 */     return this.comprobantes;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void setComprobantes(Comprobantes value)
/*  34:    */   {
/*  35: 98 */     this.comprobantes = value;
/*  36:    */   }
/*  37:    */   
/*  38:    */   @XmlAccessorType(XmlAccessType.FIELD)
/*  39:    */   @XmlType(name="", propOrder={"comprobante"})
/*  40:    */   public static class Comprobantes
/*  41:    */   {
/*  42:    */     protected List<Comprobante> comprobante;
/*  43:    */     
/*  44:    */     public List<Comprobante> getComprobante()
/*  45:    */     {
/*  46:152 */       if (this.comprobante == null) {
/*  47:153 */         this.comprobante = new ArrayList();
/*  48:    */       }
/*  49:155 */       return this.comprobante;
/*  50:    */     }
/*  51:    */   }
/*  52:    */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.RespuestaSolicitud
 * JD-Core Version:    0.7.0.1
 */