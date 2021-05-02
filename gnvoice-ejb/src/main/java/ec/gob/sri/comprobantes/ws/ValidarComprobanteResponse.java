/*  1:   */ package ec.gob.sri.comprobantes.ws;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlElement;
/*  6:   */ import javax.xml.bind.annotation.XmlType;
/*  7:   */ 
/*  8:   */ @XmlAccessorType(XmlAccessType.FIELD)
/*  9:   */ @XmlType(name="validarComprobanteResponse", propOrder={"respuestaRecepcionComprobante"})
/* 10:   */ public class ValidarComprobanteResponse
/* 11:   */ {
/* 12:   */   @XmlElement(name="RespuestaRecepcionComprobante")
/* 13:   */   protected RespuestaSolicitud respuestaRecepcionComprobante;
/* 14:   */   
/* 15:   */   public RespuestaSolicitud getRespuestaRecepcionComprobante()
/* 16:   */   {
/* 17:47 */     return this.respuestaRecepcionComprobante;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void setRespuestaRecepcionComprobante(RespuestaSolicitud value)
/* 21:   */   {
/* 22:59 */     this.respuestaRecepcionComprobante = value;
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.ValidarComprobanteResponse
 * JD-Core Version:    0.7.0.1
 */