/**
 * 
 */
package com.gizlocorp.adm.servicio.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.dao.ParametroDAO;
import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.excepcion.GizloException;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.servicio.local.ServicioParametroLocal;
import com.gizlocorp.adm.utilitario.TripleDESUtil;

/**
 * 
 * @author
 * @version
 */
@Stateless
public class ServicioParametroImpl implements ServicioParametroLocal {

	private static Logger log = Logger.getLogger(ServicioParametroImpl.class.getName());
	@EJB
	ParametroDAO parametroDAO;

	@Override
	public List<Parametro> listarParametros(String codigo, Estado estado,
			Long idOrganizacion) throws GizloException {
		List<Parametro> resultado = parametroDAO.listaParametro(codigo, estado,idOrganizacion);

		if (resultado != null && !resultado.isEmpty()) {
			String valorDesEncriptar = null;

			for (int i = 0; i < resultado.size(); i++) {
				if (Logico.S.equals(resultado.get(i).getEncriptado())) {
					try {
						valorDesEncriptar = TripleDESUtil._decrypt(resultado
								.get(i).getValor());
						resultado.get(i).setValorDesencriptado(
								valorDesEncriptar);
					} catch (Exception e) {
						log.debug("Error de encriptacion Parametro");
					}
				}
			}
		}

		return resultado;
	}

	@Override
	public void ingresarParametro(Parametro parametro) throws GizloException {
		try {
			if (Logico.S.equals(parametro.getEncriptado())) {
				String valorEncriptado = TripleDESUtil._encrypt(parametro
						.getValor());
				parametro.setValor(valorEncriptado);
			}

			parametro.setEstado(Estado.ACT);
			parametroDAO.persist(parametro);
		} catch (Exception e) {
			throw new GizloException("Error al guardar Parametro", e);
		}
	}

	@Override
	public void actualizarParametro(Parametro parametro) throws GizloException {
		try {
			if (Logico.S.equals(parametro.getEncriptado())) {
				String valorEncriptado = TripleDESUtil._encrypt(parametro
						.getValor());
				parametro.setValor(valorEncriptado);
			}
			parametroDAO.update(parametro);
		} catch (Exception e) {
			throw new GizloException("Error al actualizar Parametro", e);
		}
	}

	@Override
	public Parametro consultarParametro(String codigo, Long idOrganizacion)
			throws GizloException {
		Parametro respuesta = parametroDAO.obtenerParametro(codigo,
				idOrganizacion);
		if (respuesta != null && Logico.S.equals(respuesta.getEncriptado())) {
			try {
				String valorDesEncriptar = TripleDESUtil._decrypt(respuesta
						.getValor());
				respuesta.setValorDesencriptado(valorDesEncriptar);
			} catch (Exception e) {
				log.debug("Error de encriptacion Parametro");
			}
		}
		return respuesta;
	}

}
