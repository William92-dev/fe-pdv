/*   1:    */ package ec.gob.sri.comprobantes.ws;
/*   2:    */ 
/*   3:    */ import javax.xml.bind.JAXBElement;
/*   4:    */ import javax.xml.bind.annotation.XmlElementDecl;
/*   5:    */ import javax.xml.bind.annotation.XmlRegistry;
/*   6:    */ import javax.xml.namespace.QName;
/*   7:    */ 
/*   8:    */ @XmlRegistry
/*   9:    */ public class ObjectFactory
/*  10:    */ {
/*  11: 27 */   private static final QName _ValidarComprobante_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "validarComprobante");
/*  12: 28 */   private static final QName _ValidarComprobanteResponse_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "validarComprobanteResponse");
/*  13: 29 */   private static final QName _Mensaje_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "mensaje");
/*  14: 30 */   private static final QName _RespuestaSolicitud_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "RespuestaSolicitud");
/*  15: 31 */   private static final QName _Comprobante_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "comprobante");
/*  16:    */   
/*  17:    */   public Comprobante.Mensajes createComprobanteMensajes()
/*  18:    */   {
/*  19: 45 */     return new Comprobante.Mensajes();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ValidarComprobante createValidarComprobante()
/*  23:    */   {
/*  24: 53 */     return new ValidarComprobante();
/*  25:    */   }
/*  26:    */   
/*  27:    */   public RespuestaSolicitud createRespuestaSolicitud()
/*  28:    */   {
/*  29: 61 */     return new RespuestaSolicitud();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Comprobante createComprobante()
/*  33:    */   {
/*  34: 69 */     return new Comprobante();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public ValidarComprobanteResponse createValidarComprobanteResponse()
/*  38:    */   {
/*  39: 77 */     return new ValidarComprobanteResponse();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Mensaje createMensaje()
/*  43:    */   {
/*  44: 85 */     return new Mensaje();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public RespuestaSolicitud.Comprobantes createRespuestaSolicitudComprobantes()
/*  48:    */   {
/*  49: 93 */     return new RespuestaSolicitud.Comprobantes();
/*  50:    */   }
/*  51:    */   
/*  52:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.recepcion", name="validarComprobante")
/*  53:    */   public JAXBElement<ValidarComprobante> createValidarComprobante(ValidarComprobante value)
/*  54:    */   {
/*  55:102 */     return new JAXBElement(_ValidarComprobante_QNAME, ValidarComprobante.class, null, value);
/*  56:    */   }
/*  57:    */   
/*  58:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.recepcion", name="validarComprobanteResponse")
/*  59:    */   public JAXBElement<ValidarComprobanteResponse> createValidarComprobanteResponse(ValidarComprobanteResponse value)
/*  60:    */   {
/*  61:111 */     return new JAXBElement(_ValidarComprobanteResponse_QNAME, ValidarComprobanteResponse.class, null, value);
/*  62:    */   }
/*  63:    */   
/*  64:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.recepcion", name="mensaje")
/*  65:    */   public JAXBElement<Mensaje> createMensaje(Mensaje value)
/*  66:    */   {
/*  67:120 */     return new JAXBElement(_Mensaje_QNAME, Mensaje.class, null, value);
/*  68:    */   }
/*  69:    */   
/*  70:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.recepcion", name="RespuestaSolicitud")
/*  71:    */   public JAXBElement<RespuestaSolicitud> createRespuestaSolicitud(RespuestaSolicitud value)
/*  72:    */   {
/*  73:129 */     return new JAXBElement(_RespuestaSolicitud_QNAME, RespuestaSolicitud.class, null, value);
/*  74:    */   }
/*  75:    */   
/*  76:    */   @XmlElementDecl(namespace="http://ec.gob.sri.ws.recepcion", name="comprobante")
/*  77:    */   public JAXBElement<Comprobante> createComprobante(Comprobante value)
/*  78:    */   {
/*  79:138 */     return new JAXBElement(_Comprobante_QNAME, Comprobante.class, null, value);
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.ObjectFactory
 * JD-Core Version:    0.7.0.1
 */