/*  1:   */ package ec.gob.sri.comprobantes.ws.aut;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlType;
/*  6:   */ 
/*  7:   */ @XmlAccessorType(XmlAccessType.FIELD)
/*  8:   */ @XmlType(name="autorizacionComprobante", propOrder={"claveAccesoComprobante"})
/*  9:   */ public class AutorizacionComprobante
/* 10:   */ {
/* 11:   */   protected String claveAccesoComprobante;
/* 12:   */   
/* 13:   */   public String getClaveAccesoComprobante()
/* 14:   */   {
/* 15:45 */     return this.claveAccesoComprobante;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void setClaveAccesoComprobante(String value)
/* 19:   */   {
/* 20:57 */     this.claveAccesoComprobante = value;
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobante
 * JD-Core Version:    0.7.0.1
 */