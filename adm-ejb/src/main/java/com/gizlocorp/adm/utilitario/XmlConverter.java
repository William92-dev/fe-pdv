/*
 * XmlConverter.java
 * 
 * Copyright (c) 2012 FARCOMED.
 * Todos los derechos reservados.
 */

package com.gizlocorp.adm.utilitario;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringEscapeUtils;

import com.gizlocorp.adm.excepcion.ConverterException;

/**
 * Clase
 * 
 * @author
 * @revision $Revision: 1.2 $
 */
public class XmlConverter implements Converter {

	@SuppressWarnings("unchecked")
	public <T> T convertirAObjeto(final InputStream valor, final Class<T> clase)
			throws ConverterException {
		try {
			JAXBContext context = JAXBContext.newInstance(clase);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			T result = (T) unmarshaller.unmarshal(valor);
			return result;
		} catch (JAXBException e) {
			throw new ConverterException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T convertirAObjeto(final String valor, final Class<T> clase)
			throws ConverterException {
		try {
			JAXBContext context = JAXBContext.newInstance(clase);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader reader = new StringReader(valor);
			T result = (T) unmarshaller.unmarshal(reader);
			return result;
		} catch (JAXBException e) {
			throw new ConverterException(e);
		}
	}

	@Override
	public String convertirDeObjeto(final Object o) throws ConverterException {
		try {
			final JAXBContext context = JAXBContext.newInstance(o.getClass());
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			final StringWriter stringWriter = new StringWriter();
			marshaller.marshal(o, stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			throw new ConverterException(e);
		}
	}

	@Override
	public String convertirDeObjetoFormat(final Object o)
			throws ConverterException {
		try {
			final JAXBContext context = JAXBContext.newInstance(o.getClass());
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			final StringWriter stringWriter = new StringWriter();
			marshaller.marshal(o, stringWriter);
			return StringEscapeUtils.unescapeXml(stringWriter.toString());
			// return stringWriter.toString();
		} catch (JAXBException e) {
			throw new ConverterException(e);
		}
	}
}
