package com.gizlocorp.gnvoice.service;

import javax.ejb.Local;

import com.gizlocorp.gnvoice.xml.message.FacturaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaRecibirResponse;

@Local
public interface Factura {

	FacturaProcesarResponse procesar(
			FacturaProcesarRequest facturaProcesarRequest);

	FacturaProcesarResponse procesarOffline(
			FacturaProcesarRequest facturaProcesarRequest);

	FacturaConsultarResponse consultar(
			FacturaConsultarRequest facturaConsultarRequest);

	FacturaRecibirResponse recibir(FacturaRecibirRequest facturaRecibirRequest);

}
