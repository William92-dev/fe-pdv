/*  1:   */ package ec.gob.sri.comprobantes.ws.aut;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlElement;
/*  6:   */ import javax.xml.bind.annotation.XmlType;
/*  7:   */ 
/*  8:   */ @XmlAccessorType(XmlAccessType.FIELD)
/*  9:   */ @XmlType(name="autorizacionComprobanteResponse", propOrder={"respuestaAutorizacionComprobante"})
/* 10:   */ public class AutorizacionComprobanteResponse
/* 11:   */ {
/* 12:   */   @XmlElement(name="RespuestaAutorizacionComprobante")
/* 13:   */   protected RespuestaComprobante respuestaAutorizacionComprobante;
/* 14:   */   
/* 15:   */   public RespuestaComprobante getRespuestaAutorizacionComprobante()
/* 16:   */   {
/* 17:47 */     return this.respuestaAutorizacionComprobante;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void setRespuestaAutorizacionComprobante(RespuestaComprobante value)
/* 21:   */   {
/* 22:59 */     this.respuestaAutorizacionComprobante = value;
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobanteResponse
 * JD-Core Version:    0.7.0.1
 */