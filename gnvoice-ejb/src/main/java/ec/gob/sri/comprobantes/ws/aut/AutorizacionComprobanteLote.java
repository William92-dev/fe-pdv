/*  1:   */ package ec.gob.sri.comprobantes.ws.aut;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlType;
/*  6:   */ 
/*  7:   */ @XmlAccessorType(XmlAccessType.FIELD)
/*  8:   */ @XmlType(name="autorizacionComprobanteLote", propOrder={"claveAccesoLote"})
/*  9:   */ public class AutorizacionComprobanteLote
/* 10:   */ {
/* 11:   */   protected String claveAccesoLote;
/* 12:   */   
/* 13:   */   public String getClaveAccesoLote()
/* 14:   */   {
/* 15:45 */     return this.claveAccesoLote;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void setClaveAccesoLote(String value)
/* 19:   */   {
/* 20:57 */     this.claveAccesoLote = value;
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobanteLote
 * JD-Core Version:    0.7.0.1
 */