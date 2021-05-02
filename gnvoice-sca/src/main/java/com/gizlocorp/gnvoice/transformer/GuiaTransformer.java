/**
 * 
 */
package com.gizlocorp.gnvoice.transformer;

import org.apache.log4j.Logger;
import org.switchyard.annotations.Transformer;

import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.gnvoice.xml.message.GuiaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaRecibirResponse;

/**
 * @author gizlocorp
 * 
 */
public final class GuiaTransformer {
	private Converter converter;

	private static Logger log = Logger.getLogger(GuiaTransformer.class
			.getName());

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}guiaConsultarResponse")
	public String transformGuiaConsultarResponseToGuiaConsultarResponse(
			GuiaConsultarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}guiaProcesarResponse")
	public String transformGuiaProcesarResponseToGuiaProcesarResponse(
			GuiaProcesarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}guiaRecibirResponse")
	public String transformGuiaRecibirResponseToGuiaRecibirResponse(
			GuiaRecibirResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}guiaConsultarRequest")
	public GuiaConsultarRequest transformGuiaConsultarRequestToGuiaConsultarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			GuiaConsultarRequest factura = this.converter.convertirAObjeto(
					from, GuiaConsultarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new GuiaConsultarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}guiaProcesarRequest")
	public GuiaProcesarRequest transformGuiaProcesarRequestToGuiaProcesarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			GuiaProcesarRequest factura = this.converter.convertirAObjeto(from,
					GuiaProcesarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new GuiaProcesarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}guiaRecibirRequest")
	public GuiaRecibirRequest transformGuiaRecibirRequestToGuiaRecibirRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			GuiaRecibirRequest factura = this.converter.convertirAObjeto(from,
					GuiaRecibirRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new GuiaRecibirRequest();
	}

}
