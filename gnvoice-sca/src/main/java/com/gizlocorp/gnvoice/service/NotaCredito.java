package com.gizlocorp.gnvoice.service;

import javax.ejb.Local;

import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.NotaCreditoRecibirResponse;

@Local
public interface NotaCredito {

	NotaCreditoProcesarResponse procesar(NotaCreditoProcesarRequest mensaje);
	
	NotaCreditoProcesarResponse procesarOffline(NotaCreditoProcesarRequest mensaje);
	
	NotaCreditoConsultarResponse consultar(NotaCreditoConsultarRequest mensaje);

	NotaCreditoRecibirResponse recibir(NotaCreditoRecibirRequest mensaje);
}
