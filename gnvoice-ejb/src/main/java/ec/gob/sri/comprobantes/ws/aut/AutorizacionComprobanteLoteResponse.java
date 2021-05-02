/*  1:   */ package ec.gob.sri.comprobantes.ws.aut;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlElement;
/*  6:   */ import javax.xml.bind.annotation.XmlType;
/*  7:   */ 
/*  8:   */ @XmlAccessorType(XmlAccessType.FIELD)
/*  9:   */ @XmlType(name="autorizacionComprobanteLoteResponse", propOrder={"respuestaAutorizacionLote"})
/* 10:   */ public class AutorizacionComprobanteLoteResponse
/* 11:   */ {
/* 12:   */   @XmlElement(name="RespuestaAutorizacionLote")
/* 13:   */   protected RespuestaLote respuestaAutorizacionLote;
/* 14:   */   
/* 15:   */   public RespuestaLote getRespuestaAutorizacionLote()
/* 16:   */   {
/* 17:47 */     return this.respuestaAutorizacionLote;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void setRespuestaAutorizacionLote(RespuestaLote value)
/* 21:   */   {
/* 22:59 */     this.respuestaAutorizacionLote = value;
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobanteLoteResponse
 * JD-Core Version:    0.7.0.1
 */