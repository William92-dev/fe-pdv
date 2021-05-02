/**
 * 
 */
package com.gizlocorp.gnvoice.transformer;

import org.apache.log4j.Logger;
import org.switchyard.annotations.Transformer;

import com.gizlocorp.adm.utilitario.ContentTypeEnum;
import com.gizlocorp.adm.utilitario.Converter;
import com.gizlocorp.adm.utilitario.ConverterFactory;
import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirResponse;

/**
 * @author gizlocorp
 * 
 */
public final class FacturaTransformer {
	private Converter converter;

	private static Logger log = Logger.getLogger(FacturaTransformer.class
			.getName());

	private static final ContentTypeEnum XML_CONTENT_TYPE = ContentTypeEnum.XML;

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaReceptarResponse")
	public String transformFacturaReceptarResponseToFacturaReceptarResponse(
			FacturaReceptarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaAutorizarResponse")
	public String transformFacturaAutorizarResponseToFacturaAutorizarResponse(
			FacturaAutorizarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaConsultarResponse")
	public String transformFacturaConsultarResponseToFacturaConsultarResponse(
			FacturaConsultarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaProcesarResponse")
	public String transformFacturaProcesarResponseToFacturaProcesarResponse(
			FacturaProcesarResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(to = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaRecibirResponse")
	public String transformFacturaRecibirResponseToFacturaRecibirResponse(
			FacturaRecibirResponse from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			return this.converter.convertirDeObjeto(from);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaConsultarRequest")
	public FacturaConsultarRequest transformFacturaConsultarRequestToFacturaConsultarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			FacturaConsultarRequest factura = this.converter.convertirAObjeto(
					from, FacturaConsultarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new FacturaConsultarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaProcesarRequest")
	public FacturaProcesarRequest transformFacturaProcesarRequestToFacturaProcesarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			FacturaProcesarRequest factura = this.converter.convertirAObjeto(
					from, FacturaProcesarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new FacturaProcesarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaRecibirRequest")
	public FacturaRecibirRequest transformFacturaRecibirRequestToFacturaRecibirRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			FacturaRecibirRequest factura = this.converter.convertirAObjeto(
					from, FacturaRecibirRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new FacturaRecibirRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaReceptarRequest")
	public FacturaReceptarRequest transformFacturaReceptarRequestToFacturaReceptarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			FacturaReceptarRequest factura = this.converter.convertirAObjeto(
					from, FacturaReceptarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new FacturaReceptarRequest();
	}

	@Transformer(from = "{urn:com.gizlocorp.gnvoice:gnvoice-sca:1.0}facturaAutorizarRequest")
	public FacturaAutorizarRequest transformFacturaAutorizarRequestToFacturaAutorizarRequest(
			String from) {
		try {
			this.converter = ConverterFactory.getConverter(XML_CONTENT_TYPE);
			log.debug(from);
			FacturaAutorizarRequest factura = this.converter.convertirAObjeto(
					from, FacturaAutorizarRequest.class);
			log.debug(factura);
			return factura;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new FacturaAutorizarRequest();
	}

}
