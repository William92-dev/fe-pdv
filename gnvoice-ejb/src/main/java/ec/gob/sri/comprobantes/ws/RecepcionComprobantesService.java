/*  1:   */ package ec.gob.sri.comprobantes.ws;
/*  2:   */ 
/*  3:   */ import java.net.URL;

/*  4:   */ import javax.xml.namespace.QName;
/*  5:   */ import javax.xml.ws.Service;
/*  6:   */ import javax.xml.ws.WebEndpoint;
/*  7:   */ import javax.xml.ws.WebServiceClient;
/*  8:   */ import javax.xml.ws.WebServiceException;
/*  9:   */ import javax.xml.ws.WebServiceFeature;
/* 10:   */ 
/* 11:   */ @WebServiceClient(name="RecepcionComprobantesService", targetNamespace="http://ec.gob.sri.ws.recepcion", wsdlLocation="/META-INF/wsdl/RecepcionComprobantes.wsdl")
/* 12:   */ public class RecepcionComprobantesService
/* 13:   */   extends Service
/* 14:   */ {
/* 15:   */   private static final URL RECEPCIONCOMPROBANTESSERVICE_WSDL_LOCATION;
/* 16:   */   private static final WebServiceException RECEPCIONCOMPROBANTESSERVICE_EXCEPTION;
/* 17:26 */   private static final QName RECEPCIONCOMPROBANTESSERVICE_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
/* 18:   */   
/* 19:   */   static
/* 20:   */   {
/* 21:29 */     RECEPCIONCOMPROBANTESSERVICE_WSDL_LOCATION = RecepcionComprobantesService.class.getResource("/META-INF/wsdl/RecepcionComprobantes.wsdl");
/* 22:30 */     WebServiceException e = null;
/* 23:31 */     if (RECEPCIONCOMPROBANTESSERVICE_WSDL_LOCATION == null) {
/* 24:32 */       e = new WebServiceException("Cannot find '/META-INF/wsdl/RecepcionComprobantes.wsdl' wsdl. Place the resource correctly in the classpath.");
/* 25:   */     }
/* 26:34 */     RECEPCIONCOMPROBANTESSERVICE_EXCEPTION = e;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public RecepcionComprobantesService()
/* 30:   */   {
/* 31:38 */     super(__getWsdlLocation(), RECEPCIONCOMPROBANTESSERVICE_QNAME);
/* 32:   */   }
/* 33:   */   
/* 34:   */   public RecepcionComprobantesService(URL wsdlLocation)
/* 35:   */   {
/* 36:42 */     super(wsdlLocation, RECEPCIONCOMPROBANTESSERVICE_QNAME);
/* 37:   */   }
/* 38:   */   
/* 39:   */   public RecepcionComprobantesService(URL wsdlLocation, QName serviceName)
/* 40:   */   {
/* 41:46 */     super(wsdlLocation, serviceName);
/* 42:   */   }
/* 43:   */   
/* 44:   */   @WebEndpoint(name="RecepcionComprobantesPort")
/* 45:   */   public RecepcionComprobantes getRecepcionComprobantesPort()
/* 46:   */   {
/* 47:56 */     return (RecepcionComprobantes)super.getPort(new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesPort"), RecepcionComprobantes.class);
/* 48:   */   }
/* 49:   */   
/* 50:   */   @WebEndpoint(name="RecepcionComprobantesPort")
/* 51:   */   public RecepcionComprobantes getRecepcionComprobantesPort(WebServiceFeature... features)
/* 52:   */   {
/* 53:68 */     return (RecepcionComprobantes)super.getPort(new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesPort"), RecepcionComprobantes.class, features);
/* 54:   */   }
/* 55:   */   
/* 56:   */   private static URL __getWsdlLocation()
/* 57:   */   {
/* 58:72 */     if (RECEPCIONCOMPROBANTESSERVICE_EXCEPTION != null) {
/* 59:73 */       throw RECEPCIONCOMPROBANTESSERVICE_EXCEPTION;
/* 60:   */     }
/* 61:75 */     return RECEPCIONCOMPROBANTESSERVICE_WSDL_LOCATION;
/* 62:   */   }
/* 63:   */ }


/* Location:           C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name:     ec.gob.sri.comprobantes.ws.RecepcionComprobantesService
 * JD-Core Version:    0.7.0.1
 */