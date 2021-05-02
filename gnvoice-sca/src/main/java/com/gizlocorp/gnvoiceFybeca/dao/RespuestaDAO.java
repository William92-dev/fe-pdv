package com.gizlocorp.gnvoiceFybeca.dao;

import java.sql.Connection;
import java.util.List;

import javax.ejb.Local;

import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.gnvoiceFybeca.modelo.CredencialDS;
import com.gizlocorp.gnvoiceFybeca.modelo.Respuesta;

@Local
public interface RespuestaDAO {
	void insertarRespuesta(Connection conn, Respuesta respuesta)
			throws GizloException;

	void actualizarRespuesta(Connection conn, Respuesta respuesta)
			throws GizloException;

	List<Respuesta> listar(CredencialDS credencialDS, Long farmacia,
			String estado, Long tipo) throws GizloException;

	Respuesta consultar(Connection conn, Long farmacia, Long documento,
			String estado, Long tipo) throws GizloException;

	List<Long> obtenerComprobantes(CredencialDS credencialDS, Long farmacia,
			String estado, Long tipo) throws GizloException;
}
