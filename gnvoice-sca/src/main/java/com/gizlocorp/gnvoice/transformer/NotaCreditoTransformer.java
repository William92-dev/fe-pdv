/**
 * 
 */
package com.gizlocorp.gnvoice.transformer;

import org.apache.log4j.Logger;
import org.switchyard.annotations.Transformer;

import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoAutorizarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoAutorizarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoReceptarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoReceptarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirResponse;

/**
 * @author gizlocorp
 * 
 */
public final class NotaCreditoTransformer {
	private Converter converter;

	private static Logger log = Logger.getLogger(NotaCreditoTransformer.class
			.getName());

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;
	
	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoReceptarResponse")
	public String transformNotaCreditoReceptarResponseToNotaCreditoReceptarResponse(
			NotaCreditoReceptarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoAutorizarResponse")
	public String transformNotaCreditoAutorizarResponseToNotaCreditoAutorizarResponse(
			NotaCreditoAutorizarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoConsultarResponse")
	public String transformNotaCreditoConsultarResponseToNotaCreditoConsultarResponse(
			NotaCreditoConsultarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoProcesarResponse")
	public String transformNotaCreditoProcesarResponseToNotaCreditoProcesarResponse(
			NotaCreditoProcesarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoRecibirResponse")
	public String transformNotaCreditoRecibirResponseToNotaCreditoRecibirResponse(
			NotaCreditoRecibirResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoConsultarRequest")
	public NotaCreditoConsultarRequest transformNotaCreditoConsultarRequestToNotaCreditoConsultarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			NotaCreditoConsultarRequest factura = this.converter
					.convertirAObjeto(from, NotaCreditoConsultarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new NotaCreditoConsultarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoProcesarRequest")
	public NotaCreditoProcesarRequest transformNotaCreditoProcesarRequestToNotaCreditoProcesarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			NotaCreditoProcesarRequest factura = this.converter
					.convertirAObjeto(from, NotaCreditoProcesarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new NotaCreditoProcesarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoRecibirRequest")
	public NotaCreditoRecibirRequest transformNotaCreditoRecibirRequestToNotaCreditoRecibirRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			NotaCreditoRecibirRequest factura = this.converter
					.convertirAObjeto(from, NotaCreditoRecibirRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new NotaCreditoRecibirRequest();
	}
	
	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoReceptarRequest")
	public NotaCreditoReceptarRequest transformNotaCreditoReceptarRequestToNotaCreditoReceptarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			NotaCreditoReceptarRequest factura = this.converter.convertirAObjeto(
					from, NotaCreditoReceptarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new NotaCreditoReceptarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}notaCreditoAutorizarRequest")
	public NotaCreditoAutorizarRequest transformNotaCreditoAutorizarRequestToNotaCreditoAutorizarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			NotaCreditoAutorizarRequest factura = this.converter.convertirAObjeto(
					from, NotaCreditoAutorizarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new NotaCreditoAutorizarRequest();
	}

}
