package com.gizlocorp.gnvoice.service;

import javax.ejb.Local;

import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarResponse;

@Local
public interface FacturaOffline {

	FacturaConsultarResponse consultar(FacturaConsultarRequest facturaConsultarRequest);

	FacturaAutorizarResponse recibir(FacturaAutorizarRequest facturaAutorizarRequest);
	
	FacturaReceptarResponse procesar(FacturaReceptarRequest facturaReceptarRequest);

}
