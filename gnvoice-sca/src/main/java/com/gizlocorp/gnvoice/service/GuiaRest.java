package com.gizlocorp.gnvoice.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gizlocorp.gnvoice.xml.message.GuiaRestGenerarClaveAccesoResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaRestProcesarResponse;

@Path("guia")
public interface GuiaRest {

	@GET
	@Path("/procesar/{parametros}")
	@Produces(MediaType.APPLICATION_XML)
	GuiaRestProcesarResponse procesar(@PathParam("parametros") String parametros);

	@GET
	@Path("/generarClaveAcceso/{parametros}")
	@Produces(MediaType.APPLICATION_XML)
	GuiaRestGenerarClaveAccesoResponse generarClaveAcceso(
			@PathParam("parametros") String parametros);

	@GET
	@Path("/batch/")
	@Produces(MediaType.APPLICATION_XML)
	GuiaRestProcesarResponse bacth();

}
