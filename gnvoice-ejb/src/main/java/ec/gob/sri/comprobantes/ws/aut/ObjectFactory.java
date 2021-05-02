/*   1:    */ package ec.gob.sri.comprobantes.ws.aut;
/*   2:    */ 
/*   3:    */ import javax.xml.bind.JAXBElement;
/*   4:    */ import javax.xml.bind.annotation.XmlElementDecl;
/*   5:    */ import javax.xml.bind.annotation.XmlRegistry;
/*   6:    */ import javax.xml.namespace.QName;
/*   7:    */ 
/*   8:    */ @XmlRegistry
/*   9:    */ public class ObjectFactory
/*  10:    */ {
/*  11: 27 */   private static final QName _AutorizacionComprobanteLote_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "autorizacionComprobanteLote");
/*  12: 28 */   private static final QName _RespuestaAutorizacion_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "RespuestaAutorizacion");
/*  13: 29 */   private static final QName _AutorizacionComprobanteResponse_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "autorizacionComprobanteResponse");
/*  14: 30 */   private static final QName _AutorizacionComprobanteLoteResponse_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "autorizacionComprobanteLoteResponse");
/*  15: 31 */   private static final QName _AutorizacionComprobante_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "autorizacionComprobante");
/*  16: 32 */   private static final QName _Autorizacion_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "autorizacion");
/*  17: 33 */   private static final QName _Mensaje_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "mensaje");
/*  18:    */   
/*  19:    */   public Autorizacion createAutorizacion()
/*  20:    */   {
/*  21: 47 */     return new Autorizacion();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public RespuestaLote createRespuestaLote()
/*  25:    */   {
/*  26: 55 */     return new RespuestaLote();
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Autorizacion.Mensajes createAutorizacionMensajes()
/*  30:    */   {
/*  31: 63 */     return new Autorizacion.Mensajes();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public RespuestaLote.Autorizaciones createRespuestaLoteAutorizaciones()
/*  35:    */   {
/*  36: 71 */     return new RespuestaLote.Autorizaciones();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Mensaje createMensaje()
/*  40:    */   {
/*  41: 79 */     return new Mensaje();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public AutorizacionComprobanteLote createAutorizacionComprobanteLote()
/*  45:    */   {
/*  46: 87 */     return new AutorizacionComprobanteLote();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public RespuestaComprobante createRespuestaComprobante()
/*  50:    */   {
/*  51: 95 */     return new RespuestaComprobante();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public AutorizacionComprobanteResponse createAutorizacionComprobanteResponse()
/*  55:    */   {
/*  56:103 */     return new AutorizacionComprobanteResponse();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public AutorizacionComprobante createAutorizacionComprobante()
/*  60:    */   {
/*  61:111 */     return new AutorizacionComprobante();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public RespuestaComprobante.Autorizaciones createRespuestaComprobanteAutorizaciones()
/*  65:    */   {
/*  66:119 */     return new RespuestaComprobante.Autorizaciones();
/*  67:    */   }
/*  68:    */   
/*  69:    */   public AutorizacionComprobanteLoteResponse createAutorizacionComprobanteLoteResponse()
/*  70:    */   {
/*  71:127 */     return new AutorizacionComprobanteLoteResponse();
/*  72:    */   }
/*  73:    */   
/*  74:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.autorizacion", name="autorizacionComprobanteLote")
/*  75:    */   public JAXBElement<AutorizacionComprobanteLote> createAutorizacionComprobanteLote(AutorizacionComprobanteLote value)
/*  76:    */   {
/*  77:136 */     return new JAXBElement(_AutorizacionComprobanteLote_QNAME, AutorizacionComprobanteLote.class, null, value);
/*  78:    */   }
/*  79:    */   
/*  80:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.autorizacion", name="RespuestaAutorizacion")
/*  81:    */   public JAXBElement<Object> createRespuestaAutorizacion(Object value)
/*  82:    */   {
/*  83:145 */     return new JAXBElement(_RespuestaAutorizacion_QNAME, Object.class, null, value);
/*  84:    */   }
/*  85:    */   
/*  86:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.autorizacion", name="autorizacionComprobanteResponse")
/*  87:    */   public JAXBElement<AutorizacionComprobanteResponse> createAutorizacionComprobanteResponse(AutorizacionComprobanteResponse value)
/*  88:    */   {
/*  89:154 */     return new JAXBElement(_AutorizacionComprobanteResponse_QNAME, AutorizacionComprobanteResponse.class, null, value);
/*  90:    */   }
/*  91:    */   
/*  92:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.autorizacion", name="autorizacionComprobanteLoteResponse")
/*  93:    */   public JAXBElement<AutorizacionComprobanteLoteResponse> createAutorizacionComprobanteLoteResponse(AutorizacionComprobanteLoteResponse value)
/*  94:    */   {
/*  95:163 */     return new JAXBElement(_AutorizacionComprobanteLoteResponse_QNAME, AutorizacionComprobanteLoteResponse.class, null, value);
/*  96:    */   }
/*  97:    */   
/*  98:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.autorizacion", name="autorizacionComprobante")
/*  99:    */   public JAXBElement<AutorizacionComprobante> createAutorizacionComprobante(AutorizacionComprobante value)
/* 100:    */   {
/* 101:172 */     return new JAXBElement(_AutorizacionComprobante_QNAME, AutorizacionComprobante.class, null, value);
/* 102:    */   }
/* 103:    */   
/* 104:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.autorizacion", name="autorizacion")
/* 105:    */   public JAXBElement<Autorizacion> createAutorizacion(Autorizacion value)
/* 106:    */   {
/* 107:181 */     return new JAXBElement(_Autorizacion_QNAME, Autorizacion.class, null, value);
/* 108:    */   }
/* 109:    */   
/* 110:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.autorizacion", name="mensaje")
/* 111:    */   public JAXBElement<Mensaje> createMensaje(Mensaje value)
/* 112:    */   {
/* 113:190 */     return new JAXBElement(_Mensaje_QNAME, Mensaje.class, null, value);
/* 114:    */   }
/* 115:    */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.aut.ObjectFactory
 * JD-Core Version:    0.7.0.1
 */