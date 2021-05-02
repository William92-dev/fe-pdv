/*  1:   */ package ec.gob.sri.comprobantes.ws;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlAccessType;
/*  4:   */ import javax.xml.bind.annotation.XmlAccessorType;
/*  5:   */ import javax.xml.bind.annotation.XmlType;
/*  6:   */ 
/*  7:   */ @XmlAccessorType(XmlAccessType.FIELD)
/*  8:   */ @XmlType(name="validarComprobante", propOrder={"xml"})
/*  9:   */ public class ValidarComprobante
/* 10:   */ {
/* 11:   */   protected byte[] xml;
/* 12:   */   
/* 13:   */   public byte[] getXml()
/* 14:   */   {
/* 15:44 */     return this.xml;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void setXml(byte[] value)
/* 19:   */   {
/* 20:55 */     this.xml = ((byte[])value);
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.ValidarComprobante
 * JD-Core Version:    0.7.0.1
 */