package com.gizlocorp.firmaelectronica.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gizlocorp.firmaelectronica.exception.DOMException;

/**
 * Class DOMUtil. Utilitario permite realizar transformacion de cadena de
 * caracteres a XML
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class DOMUtil {

	public static final Logger log = Logger.getLogger(DOMUtil.class);

	/**
	 * @param bytes
	 * @return
	 * @throws DOMException
	 */
	public static Document byteArrayToXml(byte[] bytes) throws DOMException {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			return factory.newDocumentBuilder().parse(
					new ByteArrayInputStream(bytes));

		} catch (Exception e) {
			throw new DOMException(e);
		}
	}

	/**
	 * @param document
	 * @return
	 * @throws DOMException
	 */
	public static String xmlToString(Document document) throws DOMException {
		try {

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
			// "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(
					writer));
			// return writer.getBuffer().toString().replaceAll("\n|\r", "");
			return writer.getBuffer().toString();

		} catch (Exception e) {
			throw new DOMException(e);
		}
	}

	/**
	 * <p>
	 * Devuelve el <code>Document</code> correspondiente al
	 * <code>resource</code> pasado como parámetro
	 * </p>
	 * 
	 * @param resource
	 *            El recurso que se desea obtener
	 * @return El <code>Document</code> asociado al <code>resource</code>
	 */
	public static Document getDocument(String resource) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			InputStream res = new FileInputStream(resource);
			doc = dbf.newDocumentBuilder().parse(res);
		} catch (ParserConfigurationException ex) {
			log.error("Error al parsear el documento");
			ex.printStackTrace();
			System.exit(-1);
		} catch (SAXException ex) {
			log.error("Error al parsear el documento");
			ex.printStackTrace();
			System.exit(-1);
		} catch (IOException ex) {
			log.error("Error al parsear el documento");
			ex.printStackTrace();
			System.exit(-1);
		} catch (IllegalArgumentException ex) {
			log.error("Error al parsear el documento");
			ex.printStackTrace();
			System.exit(-1);
		}
		return doc;
	}
}
