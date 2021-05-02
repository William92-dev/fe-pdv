package com.gizlocorp.gnvoice.orpos.service;

import com.gizlocorp.gnvoice.xml.message.NotaCreditoAutorizarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoAutorizarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoReceptarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoReceptarResponse;

public interface NotaCreditoORPOS {

	public NotaCreditoAutorizarResponse autorizar(
			NotaCreditoAutorizarRequest notaCreditoAutorizarRequest);

	public NotaCreditoReceptarResponse receptar(
			NotaCreditoReceptarRequest notaCreditoReceptarRequest);

	public NotaCreditoConsultarResponse consultar(
			NotaCreditoConsultarRequest notaCreditoConsultarRequest);
}
