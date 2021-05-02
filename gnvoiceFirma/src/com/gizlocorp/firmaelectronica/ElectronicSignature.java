package com.gizlocorp.firmaelectronica;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.w3c.dom.Document;

import com.gizlocorp.firmaelectronica.exception.DOMException;
import com.gizlocorp.firmaelectronica.exception.ElectronicSignatureException;
import com.gizlocorp.firmaelectronica.util.DOMUtil;

import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.pkstore.CertStoreException;
import es.mityc.javasign.pkstore.IPKStoreManager;
import es.mityc.javasign.pkstore.keystore.KSStore;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;

/**
 * Class ElectronicSignature. Permite firmar un documento XML con el estanda
 * xades
 * 
 * @author gizlocorp
 * @revision $Revision: 1.1 $$
 */
public class ElectronicSignature {

	public static final Logger log = Logger
			.getLogger(ElectronicSignature.class);

	/**
	 * Firma un documento XML a partir del estanda xadas creando los documentos
	 * fisicos
	 * 
	 * @param document
	 *            documento XML
	 * @param keyPath
	 *            ruta donde se encuantra la firma y la clave de la firma
	 * @param encryptionKey
	 *            clave con la cual se encripto la clave de la firma
	 * @return documento XML firmado
	 * 
	 * @throws ElectronicSignatureException
	 */
	public static byte[] signDocumentXMLDoc(Document document, String keyPath,
			String encryptionKey, boolean lote)
			throws ElectronicSignatureException {

		try {
			log.info("-->>" + keyPath);
			String fileCert = keyPath;
			String keySign = encryptionKey;
			// Obtencion del gestor de claves
			IPKStoreManager storeManager = getPKStoreManager(fileCert, keySign);
			if (storeManager == null) {
				throw new ElectronicSignatureException(
						"El gestor de claves no se ha obtenido correctamente)");
			}

			// Obtencion del certificado para firmar. Utilizaremos el primer
			// certificado del almacen.
			X509Certificate certificate = getFirstCertificate(storeManager);
			if (certificate == null) {
				throw new ElectronicSignatureException(
						"No existe ningún certificado para firmar.");
			}

			// valido fecha expiracion certificado
			// certificate.checkValidity(new GregorianCalendar().getTime());

			// String rucCertificado = getExtensionIdentifier(certificate,
			// obtenerOidAutoridad(certificate));

			// log.info("RUC Firma:" + rucCertificado);

			// Obtencin de la clave privada asociada al certificado
			PrivateKey privateKey = storeManager.getPrivateKey(certificate);

			// Obtención del provider encargado de las labores criptográficas
			Provider provider = storeManager.getProvider(certificate);

			/*
			 * Creacin del objeto que contiene tanto los datos a firmar como la
			 * configuracin del tipo de firma
			 */
			DataToSign dataToSign = createDataToSignXML(document, lote);
			// Firmamos el documento
			/*
			 * Creacin del objeto encargado de realizar la firma
			 */
			FirmaXML firma = createFirmaXML();
			Object[] res = firma.signFile(certificate, dataToSign, privateKey,
					provider);
			Document docSigned = (Document) res[0];

			return DOMUtil.xmlToString(docSigned).getBytes("UTF-8");

		} catch (es.mityc.javasign.pkstore.CertStoreException e) {
			e.printStackTrace();
			throw new ElectronicSignatureException(
					"Error al acceder al almacén..");
		} catch (DOMException e) {
			throw new ElectronicSignatureException(
					"Error al tranformar documento..");
		} catch (KeyStoreException ex) {
			throw new ElectronicSignatureException(
					"No se puede generar KeyStore PKCS12");
		} catch (NoSuchAlgorithmException ex) {
			throw new ElectronicSignatureException(
					"No se puede generar KeyStore PKCS12");
		} catch (CertificateException ex) {
			throw new ElectronicSignatureException(
					"No se puede generar KeyStore PKCS12");
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new ElectronicSignatureException(
					"No se puede generar KeyStore PKCS12");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ElectronicSignatureException(
					"Error al acceder al almacén.." + ex.getMessage());
		}

	}

	/**
	 * <p>
	 * Crea el objeto <code>FirmaXML</code> con las configuraciones necesarias
	 * que se encargará de realizar la firma del documento.
	 * </p>
	 * <p>
	 * En el caso más simple no es necesaria ninguna configuración específica.
	 * En otros casos podría ser necesario por lo que las implementaciones
	 * concretas de las diferentes firmas deberían sobreescribir este método
	 * (por ejemplo para añadir una autoridad de sello de tiempo en aquellas
	 * firmas en las que sea necesario)
	 * <p>
	 * 
	 * 
	 * @return firmaXML Objeto <code>FirmaXML</code> configurado listo para
	 *         usarse
	 */
	private static FirmaXML createFirmaXML() {
		return new FirmaXML();
	}

	/**
	 * <p>
	 * Crea el objeto DataToSign que contiene toda la información de la firma
	 * que se desea realizar. Todas las implementaciones deberán proporcionar
	 * una implementación de este método
	 * </p>
	 * 
	 * @return El objeto DataToSign que contiene toda la información de la firma
	 *         a realizar
	 * @throws DOMException
	 */
	private static DataToSign createDataToSignXML(Document docToSign,
			boolean lote) throws DOMException {

		DataToSign dataToSign = new DataToSign();
		dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
		dataToSign.setEsquema(XAdESSchemas.XAdES_132);
		dataToSign.setXMLEncoding("UTF-8");
		dataToSign.setEnveloped(true);

		if (lote) {
			dataToSign.addObject(new ObjectToSign(
					new InternObjectToSign("lote"), "Firma SRI", null,
					"text/xml", null));

			dataToSign.setParentSignNode("lote");

		} else {
			dataToSign.addObject(new ObjectToSign(new InternObjectToSign(
					"comprobante"), "Contenido Comprobante", null, "text/xml",
					null));

			dataToSign.setParentSignNode("comprobante");

		}
		dataToSign.setDocument(docToSign);
		return dataToSign;
	}

	/**
	 * <p>
	 * Devuelve el gestor de claves que se va a utilizar
	 * </p>
	 * 
	 * @return El gestor de claves que se va a utilizar</p>
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 */
	private static IPKStoreManager getPKStoreManager(String pathSign,
			String keySign) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance("pkcs12");
		InputStream doc = new FileInputStream(pathSign);
		ks.load(doc, keySign.toCharArray());
		return new KSStore(ks, new PassStoreKS(keySign));
	}

	/**
	 * <p>
	 * Recupera el primero de los certificados del almacén.
	 * </p>
	 * 
	 * @param storeManager
	 *            Interfaz de acceso al almacén
	 * @return Primer certificado disponible en el almacén
	 * @throws CertStoreException
	 */
	private static X509Certificate getFirstCertificate(
			final IPKStoreManager storeManager) throws CertStoreException {
		List<X509Certificate> certs = storeManager.getSignCertificates();
		X509Certificate certificate = certs.get(0);
		return certificate;
	}

	/**
	 * Obtiene y decodifica un oid determinado
	 * 
	 * @param cert
	 * @param oid
	 * @return
	 * @throws IOException
	 */
	public static String getExtensionIdentifier(X509Certificate cert, String oid)
			throws IOException {
		String id = null;
		DERObject derObject = null;
		byte[] extensionValue = cert.getExtensionValue(oid);
		if (extensionValue != null) {
			derObject = toDERObject(extensionValue);
			if (derObject instanceof DEROctetString) {
				DEROctetString derOctetString = (DEROctetString) derObject;
				derObject = toDERObject(derOctetString.getOctets());
			}
		}
		if (derObject != null) {
			id = derObject.toString();
		} else {
			id = null;
		}
		return id;
	}

	/**
	 * obtiene un objeto DER de un byte[]
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	static public DERObject toDERObject(byte[] data) throws IOException {
		ByteArrayInputStream inStream = new ByteArrayInputStream(data);
		@SuppressWarnings("resource")
		ASN1InputStream derInputStream = new ASN1InputStream(inStream);
		return derInputStream.readObject();

	}

	/**
	 * Obtiene el OID del RUC de un certificado digital
	 * 
	 * @param certificado
	 * @return
	 */
	public static String obtenerOidAutoridad(final X509Certificate certificado) {
		String oidRaiz = null;

		X500NameGeneral x500emisor = new X500NameGeneral(certificado
				.getIssuerDN().getName());
		String nombreAutoridad = x500emisor.getCN();

		if (nombreAutoridad.equals(AutoridadesCertificantes.BANCO_CENTRAL
				.getCn())) {
			oidRaiz = AutoridadesCertificantes.BANCO_CENTRAL.getOid();
		} else if (nombreAutoridad.equals(AutoridadesCertificantes.ANF.getCn())) {
			oidRaiz = AutoridadesCertificantes.ANF.getOid();
		} else if (nombreAutoridad
				.equals(AutoridadesCertificantes.SECURITY_DATA.getCn())) {
			oidRaiz = AutoridadesCertificantes.SECURITY_DATA.getOid();
		}

		oidRaiz = oidRaiz.concat(".3.11");
		return oidRaiz;
	}
}
