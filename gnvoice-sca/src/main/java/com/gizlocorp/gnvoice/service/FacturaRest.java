package com.gizlocorp.gnvoice.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gizlocorp.gnvoice.xml.message.FacturaRestGenerarClaveAccesoResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaRestProcesarResponse;

@Path("factura")
public interface FacturaRest {

	@GET
	@Path("/procesar/{parametros}")
	@Produces(MediaType.APPLICATION_XML)
	FacturaRestProcesarResponse procesar(
			@PathParam("parametros") String parametros);

	@GET
	@Path("/batch/{empresa}")
	@Produces(MediaType.APPLICATION_XML)
	String bacth(@PathParam("empresa") String empresa);
	
	@GET
	  @Path("/batchLocal/{codigoLocal}")
	  @Produces({"application/xml"})
	  public abstract String bacthLocal(@PathParam("codigoLocal") String paramString);

	@GET
	@Path("/generarClaveAcceso/{parametros}")
	@Produces(MediaType.APPLICATION_XML)
	FacturaRestGenerarClaveAccesoResponse generarClaveAcceso(
			@PathParam("parametros") String parametros);
}
