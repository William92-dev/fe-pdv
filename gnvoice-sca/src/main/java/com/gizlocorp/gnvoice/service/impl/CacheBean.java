package com.gizlocorp.gnvoice.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gizlocorp.adm.dao.OrganizacionDAO;
import com.gizlocorp.adm.dao.ParametroDAO;
import com.gizlocorp.adm.dao.PlantillaDAO;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.modelo.Organizacion;
import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.Plantilla;
import com.gizlocorp.adm.utilitario.TripleDESUtil;

@Startup
@Singleton
@AccessTimeout(value = 750, unit = TimeUnit.MINUTES)
public class CacheBean {

	public static final Logger log = LoggerFactory.getLogger(CacheBean.class);

	@EJB(lookup = "java:global/adm-ejb/ParametroDAOImpl!com.gizlocorp.adm.dao.ParametroDAO")
	ParametroDAO servicioParametro;

	@EJB(lookup = "java:global/adm-ejb/PlantillaDAOImpl!com.gizlocorp.adm.dao.PlantillaDAO")
	PlantillaDAO servicioPlantilla;

	@EJB(lookup = "java:global/adm-ejb/OrganizacionDAOImpl!com.gizlocorp.adm.dao.OrganizacionDAO")
	OrganizacionDAO servicioOrganizacionLocal;

	List<Organizacion> organizaciones;

	List<Plantilla> plantillas;

	List<Parametro> parametros;

	@PostConstruct
	void init() {
		try {
			organizaciones = servicioOrganizacionLocal.listarActivas();
			plantillas = servicioPlantilla.findAll();
			parametros = servicioParametro.findAll();

		} catch (Exception ex) {
			log.error("Error cache", ex);
		}
	}

	public Organizacion obtenerOrganizacion(String ruc) {
		Organizacion organizacion = null;
		try {
			if (organizaciones == null || organizaciones.isEmpty()) {
				organizaciones = servicioOrganizacionLocal.listarActivas();

			}

			if (organizaciones != null && !organizaciones.isEmpty()) {
				for (Organizacion org : organizaciones) {
					if (org.getRuc().equals(ruc)) {
						organizacion = org;
						break;
					}
				}
			}

		} catch (Exception ex) {
			log.error("Error cache", ex);
		}
		return organizacion;
	}

	public Parametro consultarParametro(String identificador, Long emisorId) {
		Parametro parametro = null;
		try {
			if (parametros == null || parametros.isEmpty()) {
				parametros = servicioParametro.findAll();

			}

			if (parametros != null && !parametros.isEmpty()) {
				for (Parametro org : parametros) {					
					if (org.getIdOrganizacion() != null
							&& org.getIdOrganizacion().equals(emisorId)
							&& org.getCodigo().equals(identificador)) {
						
						
						if (org != null && Logico.S.equals(org.getEncriptado())) {
							try {
								String valorDesEncriptar = TripleDESUtil._decrypt(org
										.getValor());
								org.setValorDesencriptado(valorDesEncriptar);
							} catch (Exception e) {
								log.debug("Error de encriptacion Parametro");
							}
						}
						
						parametro = org;
						break;
					}
				}
			}

		} catch (Exception ex) {
			log.error("Error cache", ex);
		}
		return parametro;
	}

	public Plantilla obtenerPlantilla(String identificador, Long emisorId) {
		Plantilla parametro = null;
		try {
			if (plantillas == null || plantillas.isEmpty()) {

			}

			if (plantillas != null && !plantillas.isEmpty()) {
				for (Plantilla org : plantillas) {
					if (org.getIdOrganizacion() != null
							&& org.getIdOrganizacion().equals(emisorId)
							&& org.getCodigo().equals(identificador)) {
						parametro = org;
						break;
					}
				}
			}

		} catch (Exception ex) {
			log.error("Error cache", ex);
		}
		return parametro;
	}

}
