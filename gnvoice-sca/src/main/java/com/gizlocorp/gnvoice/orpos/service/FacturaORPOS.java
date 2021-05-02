package com.gizlocorp.gnvoice.orpos.service;

import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaAutorizarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarRequest;
import com.gizlocorp.gnvoice.xml.message.FacturaReceptarResponse;

public interface FacturaORPOS {

	public FacturaAutorizarResponse autorizar(
			FacturaAutorizarRequest facturaAutorizarRequest);

	public FacturaReceptarResponse receptar(
			FacturaReceptarRequest facturaReceptarRequest);

	public FacturaConsultarResponse consultar(
			FacturaConsultarRequest facturaConsultarRequest);
}
