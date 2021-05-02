package com.gizlocorp.gnvoice.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gizlocorp.adm.utilitario.RimEnum;
import com.gizlocorp.gnvoice.bean.databean.ReceptarOrdenCompraDataBean;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactura;
import com.gizlocorp.gnvoice.utilitario.Constantes;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaPortType;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaRecibirRequest;
import com.gizlocorp.gnvoice.wsclient.factura.FacturaRecibirResponse;
import com.gizlocorp.gnvoice.wsclient.factura.Factura_Service;
import com.gizlocorp.gnvoice.wsclient.factura.GizloResponse;

//@Interceptors(CurrentUserGnvoiceProvider.class)
@ViewScoped
@Named("receptarOrdenCompraBean")
public class ReceptarOrdenCompraBean extends BaseBean implements Serializable {

	public static final Logger log = Logger
			.getLogger(ReceptarOrdenCompraBean.class);

	private static final long serialVersionUID = -6239437588285327644L;

	@Inject
	ReceptarOrdenCompraDataBean receptarOrdenCompraDataBean;

//	@Inject
//	private SessionBean sessionBean;

	public static final int BUFFER_SIZE = 4096;

	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFacturaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactura")
	ServicioFactura servicioFactura;
	
	private Factura factura;
	
	Map<String, String> mapComprobante = new HashMap<String, String>();

	@PostConstruct
	public void postContruct() {
		receptarOrdenCompraDataBean.setRespuestas(new ArrayList<GizloResponse>());
		receptarOrdenCompraDataBean.setTipoComprobanteDes(TipoComprobante.FACTURA);
	}

	public void cargarDocumento(org.richfaces.event.FileUploadEvent event) {
		org.richfaces.model.UploadedFile item = event.getUploadedFile();
		receptarOrdenCompraDataBean.setData(item.getData());
		receptarOrdenCompraDataBean.setName(item.getName());
		receptarOrdenCompraDataBean.setExtension(item.getFileExtension());

	}

	public void paint(OutputStream stream, Object object) throws IOException {
		stream.write(receptarOrdenCompraDataBean.getData());
		stream.close();
	}
	
	public void procesar(){
		InputStream is = new ByteArrayInputStream(receptarOrdenCompraDataBean.getData());
		 try {
			 if(receptarOrdenCompraDataBean.getExtension().toLowerCase().equals("xlsx".toLowerCase())){
				 readXLSXFile(is);
				 enviaRecibirFactura();
			 }
			 if(receptarOrdenCompraDataBean.getExtension().toLowerCase().equals("xls".toLowerCase())){
				 readXLSFile(is);
				 enviaRecibirFactura();
			 }
			 if(receptarOrdenCompraDataBean.getExtension().toLowerCase().equals("txt".toLowerCase())){
				 readTXTFile(is);
				 enviaRecibirFactura();
			 }
			 if(receptarOrdenCompraDataBean.getExtension().toLowerCase().equals("xml".toLowerCase())){
				 readXMLFile(is);
				 enviaRecibirFactura();
			 }
			 infoMessage("Proceso enviado Exitosamente!", "Proceso enviado Exitosamente!");
			
		} catch (IOException e) {
			errorMessage("Error al Procesar");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void enviaRecibirFactura(){
		for (Entry<String, String> e: mapComprobante.entrySet()) {
			factura = servicioFactura.obtenerComprobante(e.getKey(), null, null, null);
			if(factura != null && factura.getEstado().equals("ERROR") && (factura.getOrdenCompra() ==null || factura.getOrdenCompra().isEmpty())
					&& factura.getProceso().equals(RimEnum.REIM.name())){
				Factura_Service serviceFactura = new Factura_Service();
				FacturaPortType portFactura = serviceFactura.getFacturaPort();

				FacturaRecibirRequest facturaRecibirRequest = new FacturaRecibirRequest();
				if (factura != null) {
					facturaRecibirRequest.setComprobanteProveedor(factura.getArchivo());
				}
				facturaRecibirRequest.setOrdenCompra(e.getValue());
				facturaRecibirRequest.setProceso(RimEnum.REIM.name());
				facturaRecibirRequest.setTipo(Constantes.MANUAL);
				try{
					FacturaRecibirResponse respuestaFactura = portFactura.recibir(facturaRecibirRequest);
				}catch(Exception ex){
					ex.printStackTrace();
					continue;
				}				
			}
		}
	}
	
	private void readTXTFile(InputStream is){
		BufferedReader br = null;

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				String[] parametros = line.split("&&");
				mapComprobante.put(parametros[0],parametros[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void readXMLFile(InputStream is){
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String linestring ="";
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((linestring = br.readLine()) != null) {
				sb.append(linestring);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(!sb.toString().isEmpty()){
			try{
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			    InputSource isx = new InputSource();
			    isx.setCharacterStream(new StringReader(sb.toString()));

			    Document doc = db.parse(isx);
			    NodeList nodes = doc.getElementsByTagName("claveOrdene");
			    String clave="";
			    String orden="";
			    for (int i = 0; i < nodes.getLength(); i++) {
			      Element element = (Element) nodes.item(i);

			      NodeList name = element.getElementsByTagName("clave");
			      Element line = (Element) name.item(0);			      
			      clave = getCharacterDataFromElement(line);

			      NodeList title = element.getElementsByTagName("orden");
			      line = (Element) title.item(0);
			      orden = getCharacterDataFromElement(line);
			      mapComprobante.put(clave,orden);
			    }
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "resource" })
	private void readXLSXFile(InputStream is) throws IOException {

		org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(is);

		org.apache.poi.xssf.usermodel.XSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.xssf.usermodel.XSSFRow row;
		org.apache.poi.xssf.usermodel.XSSFCell cell;
		String clave="";
		String orden="";

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (org.apache.poi.xssf.usermodel.XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (org.apache.poi.xssf.usermodel.XSSFCell) cells.next();

				if (cell.getCellType() == org.apache.poi.xssf.usermodel.XSSFCell.CELL_TYPE_STRING) {					
					if(cell.getColumnIndex()==0){
						clave = cell.getStringCellValue();
					}
					if(cell.getColumnIndex()==1){
						orden = cell.getStringCellValue();
					}
				} 
				mapComprobante.put(clave,orden);
			}
		}

	}

	@SuppressWarnings({ "rawtypes", "resource" })
	private  void readXLSFile(InputStream is) throws IOException {
		org.apache.poi.hssf.usermodel.HSSFWorkbook wb = new org.apache.poi.hssf.usermodel.HSSFWorkbook(is);

		org.apache.poi.hssf.usermodel.HSSFSheet sheet = wb.getSheetAt(0);
		org.apache.poi.hssf.usermodel.HSSFRow row;
		org.apache.poi.hssf.usermodel.HSSFCell cell;
		String clave="";
		String orden="";

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (org.apache.poi.hssf.usermodel.HSSFRow) rows.next();
			Iterator cells = row.cellIterator();

			while (cells.hasNext()) {
				cell = (org.apache.poi.hssf.usermodel.HSSFCell) cells.next();
				
				if (cell.getCellType() == org.apache.poi.hssf.usermodel.HSSFCell.CELL_TYPE_STRING) {
					if(cell.getColumnIndex()==0){
						clave = cell.getStringCellValue();
					}
					if(cell.getColumnIndex()==1){
						orden = cell.getStringCellValue();
					}					
				} 
			}
			mapComprobante.put(clave,orden);
		}

	}
	
	private static String getCharacterDataFromElement(Element e) {
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	      CharacterData cd = (CharacterData) child;
	      return cd.getData();
	    }
	    return "";
	  }
	
	public static void main(String[] args) {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"></soap:Envelope>";
 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
 
            // Use String reader
            Document document = builder.parse( new InputSource(
                    new StringReader( xmlString ) ) );
 
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            Source src = new DOMSource( document );
            System.out.println(src.toString());
            Result dest = new StreamResult( new File( "xmlFileName.xml" ) );
            aTransformer.transform( src, dest );
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
}