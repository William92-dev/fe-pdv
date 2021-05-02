/*
 * ValidadorEstructuraDocumento.java
 *
 * Copyright (c) 2011 Servicio de Rentas Internas.
 * Todos los derechos reservados.
 */
package com.gizlocorp.gnvoice.utilitario;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Comprueba que el archivo XML del {@link Seguimiento} este bien
 * formado y sea valido.
 * 
 * @author iapazmino
 * 
 */
public class ValidadorEstructuraDocumento {

	private File archivoXSD;
	private File archivoXML;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException
	 *             Si existe algun error en el esquema XSD o en la lectura del
	 *             archivo XML.
	 */
	public String validacion() {
		validarArchivo(archivoXSD, "archivoXSD");
		validarArchivo(archivoXML, "archivoXML");

		String mensaje = null;

		final SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema;
		try {
			schema = schemaFactory.newSchema(archivoXSD);
		} catch (SAXException e) {
			throw new IllegalStateException(
					"Existe un error en la sintaxis del esquema", e);
		}
		final Validator validator = schema.newValidator();
		try {
			validator.validate(new StreamSource(archivoXML));

		} catch (Exception e) {
			return e.getMessage();
		}
		return mensaje;
	}

	/**
	 * @return devuelve el mensaje de error para un documento mal formado o
	 *         invalido.
	 */
	protected void validarArchivo(final File archivo, String nombre)
			throws IllegalStateException {
		if (null == archivo || archivo.length() <= 0) {
			throw new IllegalStateException(nombre + " es nulo o esta vacio");
		}
	}

	/**
	 * @return the archivoXSD
	 */
	public File getArchivoXSD() {
		return archivoXSD;
	}

	/**
	 * @param archivoXSD
	 *            the archivoXSD to set
	 */
	public void setArchivoXSD(File archivoXSD) {
		this.archivoXSD = archivoXSD;
	}

	/**
	 * @return the archivoXML
	 */
	public File getArchivoXML() {
		return archivoXML;
	}

	/**
	 * @param archivoXML
	 *            the archivoXML to set
	 */
	public void setArchivoXML(File archivoXML) {
		this.archivoXML = archivoXML;
	}
}
