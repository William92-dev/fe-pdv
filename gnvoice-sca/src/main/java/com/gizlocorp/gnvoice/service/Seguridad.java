package com.gizlocorp.gnvoice.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gizlocorp.gnvoice.xml.message.SeguridadRecuperarCredencialesResponse;

@Path("seguridad")
public interface Seguridad {

	@GET
	@Path("/recuperarCredenciales/{identificacion}")
	@Produces(MediaType.APPLICATION_XML)
	SeguridadRecuperarCredencialesResponse recuperarCredenciales(
			@PathParam("identificacion") String identificacion);

}
