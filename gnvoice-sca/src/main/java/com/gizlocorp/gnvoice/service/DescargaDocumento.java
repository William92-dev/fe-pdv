package com.gizlocorp.gnvoice.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("descargarDocumento")
public interface DescargaDocumento {

	@GET
	@Path("/obtenerFacturaPDF/{claveAcceso}")
	// @Produces("application/pdf")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerFacturaPDF(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerFacturaXML/{claveAcceso}")
	// @Produces("application/xml")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerFacturaXML(@PathParam("claveAcceso") String claveAcceso);
	
//	@GET
//	@Path("/obtenerFacturaXMLExterna/{claveAcceso}")
//	// @Produces("application/xml")
//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
//	Response obtenerFacturaXMLExterna(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerGuiaPDF/{claveAcceso}")
	// @Produces("application/pdf")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerGuiaPDF(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerGuiaXML/{claveAcceso}")
	// @Produces("application/xml")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerGuiaXML(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerRetencionPDF/{claveAcceso}")
	// @Produces("application/pdf")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerRetencionPDF(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerRetencionXML/{claveAcceso}")
	// @Produces("application/xml")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerRetencionXML(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerNotaCreditoPDF/{claveAcceso}")
	// @Produces("application/pdf")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerNotaCreditoPDF(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerNotaCreditoXML/{claveAcceso}")
	// @Produces("application/xml")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerNotaCreditoXML(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerNotaDebitoPDF/{claveAcceso}")
	// @Produces("application/pdf")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerNotaDebitoPDF(@PathParam("claveAcceso") String claveAcceso);

	@GET
	@Path("/obtenerNotaDebitoXML/{claveAcceso}")
	// @Produces("application/xml")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response obtenerNotaDebitoXML(@PathParam("claveAcceso") String claveAcceso);

}
