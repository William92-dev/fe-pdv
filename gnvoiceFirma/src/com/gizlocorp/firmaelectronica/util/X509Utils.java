package com.gizlocorp.firmaelectronica.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.xml.sax.SAXException;

import com.gizlocorp.firmaelectronica.AutoridadesCertificantes;
import com.gizlocorp.firmaelectronica.FirmasGenericasXAdES;
import com.gizlocorp.firmaelectronica.ValidacionBasica;
import com.gizlocorp.firmaelectronica.X500NameGeneral;
import com.gizlocorp.firmaelectronica.util.key.KeyStoreProviderFactory;

/**
 * Clase que gestiona los certificados y realiza la firma digital
 * 
 * @author Gabriel Eguiguren
 */
public class X509Utils {

	public static final int digitalSignature = 0; // OID 2.5.29.15
	public static final int nonRepudiation = 1; // OID 2.5.29.15
	public static final int keyEncipherment = 2; // OID 2.5.29.15
	public static final int dataEncipherment = 3; // OID 2.5.29.15
	public static final int keyAgreement = 4; // OID 2.5.29.15
	public static final int keyCertSign = 5; // OID 2.5.29.15
	public static final int cRLSign = 6; // OID 2.5.29.15

	/**
	 * Verifica si un certificado puede ser utilizado para firma
	 * 
	 * @param cert
	 * @return
	 */
	public static boolean puedeFirmar(X509Certificate cert) {
		boolean resp = false;
		if (cert.getKeyUsage() == null) {
			resp = true;
		}

		if (cert.getKeyUsage()[digitalSignature]
				|| cert.getKeyUsage()[nonRepudiation]) {
			resp = true;
		}
		return resp;
	}

	/**
	 * Devuelve un identificador de los key usages
	 * 
	 * @param cert
	 * @return
	 */
	public static String getUsage(X509Certificate cert) {

		StringBuilder sb = new StringBuilder();

		if (cert.getKeyUsage() == null) {
			sb.append("no key usage defined for certificate");
		} else {

			if (cert.getKeyUsage()[X509Utils.digitalSignature]) {
				sb.append(" digitalSignature ");
			}

			if (cert.getKeyUsage()[X509Utils.cRLSign]) {
				sb.append(" cRLSign ");
			}

			if (cert.getKeyUsage()[X509Utils.dataEncipherment]) {
				sb.append(" dataEncipherment ");
			}

			if (cert.getKeyUsage()[X509Utils.keyAgreement]) {
				sb.append(" keyAgreement ");
			}

			if (cert.getKeyUsage()[X509Utils.keyCertSign]) {
				sb.append(" keyCertSign ");
			}

			if (cert.getKeyUsage()[X509Utils.keyEncipherment]) {
				sb.append(" keyEncipherment ");
			}

			if (cert.getKeyUsage()[X509Utils.nonRepudiation]) {
				sb.append(" nonRepudiation ");
			}
		}
		return sb.toString();

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
		ASN1InputStream derInputStream = new ASN1InputStream(inStream);
		return derInputStream.readObject();

	}

	/**
	 * Selecciona un alias de certificado válido para segun el parametro
	 * ingresado en la UI.
	 * 
	 * @param keyStore
	 *            contenedor del certificado
	 * @param tokenSeleccionado
	 *            identificador del token seleccionado
	 * 
	 * @return alias del certificado de firma
	 * 
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateExpiredException
	 * @throws CertificateNotYetValidException
	 * @throws CertificateException
	 */
	public static String seleccionarCertificado(final KeyStore keyStore,
			final String tokenSeleccionado) throws KeyStoreException,
			IOException, NoSuchAlgorithmException, CertificateExpiredException,
			CertificateNotYetValidException, CertificateException {

		String aliasSeleccion = null;
		X509Certificate certificado = null;

		Enumeration nombres = keyStore.aliases();
		while (nombres.hasMoreElements()) {
			String aliasKey = (String) nombres.nextElement();
			certificado = (X509Certificate) keyStore.getCertificate(aliasKey);

			X500NameGeneral x500emisor = new X500NameGeneral(certificado
					.getIssuerDN().getName());
			X500NameGeneral x500sujeto = new X500NameGeneral(certificado
					.getSubjectDN().getName());

			// Security Data
			if (tokenSeleccionado.equals("SD_BIOPASS")
					|| tokenSeleccionado.equals("SD_EPASS3000")
					&& x500emisor.getCN().contains(
							AutoridadesCertificantes.SECURITY_DATA.getCn())) {

				// valido q el certificado tenga los mismos valores de O y C en
				// issuer y subject
				if (AutoridadesCertificantes.SECURITY_DATA.getO().equals(
						x500emisor.getO())
						&& AutoridadesCertificantes.SECURITY_DATA.getC()
								.equals(x500emisor.getC())
						&& AutoridadesCertificantes.SECURITY_DATA.getO()
								.equals(x500sujeto.getO())
						&& AutoridadesCertificantes.SECURITY_DATA.getC()
								.equals(x500sujeto.getC())) {

					if (certificado.getKeyUsage()[0]
							|| certificado.getKeyUsage()[1]) {
						aliasSeleccion = aliasKey;
						break;
					}
				}
				// BANCO CENTRAL
			} else if (tokenSeleccionado.equals("BCE_ALADDIN")
					|| tokenSeleccionado.equals("BCE_IKEY2032")
					&& x500emisor.getCN().contains(
							AutoridadesCertificantes.BANCO_CENTRAL.getCn())) {

				// valido q el certificado tenga los mismos valores de O y C en
				// issuer y subject
				if (x500emisor.getO().contains(
						AutoridadesCertificantes.BANCO_CENTRAL.getO())
						&& AutoridadesCertificantes.BANCO_CENTRAL.getC()
								.equals(x500emisor.getC())
						&& x500sujeto.getO().contains(
								AutoridadesCertificantes.BANCO_CENTRAL.getO())
						&& AutoridadesCertificantes.BANCO_CENTRAL.getC()
								.equals(x500sujeto.getC())) {

					if (certificado.getKeyUsage()[0]
							|| certificado.getKeyUsage()[1]) {
						aliasSeleccion = aliasKey;
						break;
					}

				}
				// ANF EC
			} else if (tokenSeleccionado.equals("ANF1")
					&& x500emisor.getCN().contains(
							AutoridadesCertificantes.ANF.getCn())) {

				// valido q el certificado tenga los mismos valores de O y C en
				// issuer y subject
				if (AutoridadesCertificantes.ANF.getO().equals(
						x500emisor.getO())
						&& AutoridadesCertificantes.ANF.getC().equals(
								x500emisor.getC())
						&& AutoridadesCertificantes.ANF.getC().toLowerCase()
								.equals(x500sujeto.getC())) {

					if (certificado.getKeyUsage()[0]
							|| certificado.getKeyUsage()[1]) {
						aliasSeleccion = aliasKey;
						break;
					}
				}
			}
		}

		return aliasSeleccion;
	}

	/**
	 * Realiza la firma digital de un archivo xml proporcionado
	 * 
	 * @param archivo
	 *            archivo xml a firmar
	 * @param dirPathSalida
	 *            directorio de salida del archivo firmado
	 * @param rucEmisor
	 *            RUC del emisor registrado en la UI
	 * @param tokenID
	 *            identificador del token seleccionado
	 * @param password
	 *            contraseña del token cuando corresponda
	 * @return
	 */
	public static String firmaValidaArchivo(final File archivo,
			final String dirPathSalida, final String rucEmisor,
			final String tokenID, final String password) {

		String aliaskey = null;
		String respuesta = null;
		PrivateKey clavePrivada = null;
		KeyStore ks = null;
		String jreVersion = System.getProperty("java.version");

		try {
			// WINDOWS
			if (System.getProperty("os.name").startsWith("Windows") == true
					&& (jreVersion.indexOf("1.6") == 0 || jreVersion
							.indexOf("1.7") == 0)) {

				ks = KeyStore.getInstance("Windows-MY");
				ks.load(null, null);
				fixAliases(ks);
			} else if (ks == null) {
				ks = KeyStoreProviderFactory.createKeyStoreProvider()
						.getKeystore(password.toCharArray());
			} else {
				respuesta = Mensajes.X509_SO;
			}

			aliaskey = seleccionarCertificado(ks, tokenID);

			if (password == null) {
				clavePrivada = (PrivateKey) ks.getKey(aliaskey, null);
			} else {
				final KeyStore tmpKs = ks;
				final PrivateKey key;
				key = (PrivateKey) tmpKs.getKey(aliaskey,
						password.toCharArray());
				clavePrivada = key;
			}

			if (aliaskey != null) {

				String archivoFirmado = dirPathSalida + File.separator
						+ archivo.getName();

				Provider provider = null;
				if (System.getProperty("os.name").toUpperCase().indexOf("MAC") == 0
						&& KeyStoreProviderFactory.existeLibreriaMac() == false) {
					provider = Security.getProvider("SunRsaSign");
				} else {
					provider = ks.getProvider();
				}
				FirmasGenericasXAdES firmador = new FirmasGenericasXAdES();
				X509Certificate certificado = (X509Certificate) ks
						.getCertificate(aliaskey);

				// valido fecha expiracion certificado
				certificado.checkValidity(new GregorianCalendar().getTime());

				String rucCertificado = X509Utils
						.getExtensionIdentifier(certificado,
								X509Utils.obtenerOidAutoridad(certificado));
				if (rucEmisor.equals(rucCertificado) && clavePrivada != null) {
					// firma el archivo
					firmador.ejecutarFirmaXades(archivo.getAbsolutePath(),
							null, archivoFirmado, provider, certificado,
							clavePrivada);

					// valida la nueva firma
					if (new ValidacionBasica().validarArchivo(new File(
							archivoFirmado)) == false) {
						respuesta = Mensajes.X509_FIRMA_INVALIDA;
					}
					if (System.getProperty("os.name").startsWith("Windows") == true) {
						ks.load(null, null);
					}
				} else if (rucCertificado == null) {
					respuesta = Mensajes.X509_CERT_NO_RUC;
				} else if (clavePrivada == null) {
					respuesta = Mensajes.X509_CLAVE_PRIVADA;
				} else {
					respuesta = Mensajes.X509_RUC_NO_COINCIDE_CERT;
				}
			} else {
				respuesta = Mensajes.X509_CERT_NO_PRESENTE;
			}

		} catch (CertificateExpiredException ex) {
			Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null,
					ex);
			return Mensajes.X509_CERT_EXPIRADO;
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null,
					ex);
			return Mensajes.X509_XML_INVALIDO;
		} catch (SAXException ex) {
			Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null,
					ex);
			return Mensajes.X509_XML_INVALIDO;
		} catch (Exception ex) {
			Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null,
					ex);
			if (ex.getMessage() == null) {
				respuesta = "Error al firmar archivo: "
						+ Mensajes.X509_CLAVE_PRIVADA;
			} else {
				respuesta = "Error al firmar archivo: " + ex.getMessage();
			}
			return respuesta;
		}
		return respuesta;
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

	/**
	 * Resuelve el bug cuando existen aliases idénticos en el KeyStore
	 * 
	 * Fuente: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6672015
	 * 
	 * @author desconocido
	 * @param keyStore
	 */
	private static void fixAliases(KeyStore keyStore) {
		Field field;
		KeyStoreSpi keyStoreVeritable;

		try {
			field = keyStore.getClass().getDeclaredField("keyStoreSpi");
			field.setAccessible(true);
			keyStoreVeritable = (KeyStoreSpi) field.get(keyStore);

			if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable
					.getClass().getName())) {
				Collection entries;
				String alias, hashCode;
				X509Certificate[] certificates;

				field = keyStoreVeritable.getClass().getEnclosingClass()
						.getDeclaredField("entries");
				field.setAccessible(true);
				entries = (Collection) field.get(keyStoreVeritable);

				for (Object entry : entries) {
					field = entry.getClass().getDeclaredField("certChain");
					field.setAccessible(true);
					certificates = (X509Certificate[]) field.get(entry);

					hashCode = Integer.toString(certificates[0].hashCode());

					field = entry.getClass().getDeclaredField("alias");
					field.setAccessible(true);
					alias = (String) field.get(entry);

					if (!alias.equals(hashCode)) {
						field.set(entry, alias.concat(" - ").concat(hashCode));
					} // if
				} // for
			} // if
		} catch (Exception ex) {
			Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null,
					ex);
		} // catch
	} // _fixAliases
}
