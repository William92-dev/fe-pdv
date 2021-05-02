package com.gizlocorp.gnvoice.service;

import javax.ejb.Local;

import com.gizlocorp.gnvoice.xml.message.GuiaConsultarRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaConsultarResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaProcesarResponse;
import com.gizlocorp.gnvoice.xml.message.GuiaRecibirRequest;
import com.gizlocorp.gnvoice.xml.message.GuiaRecibirResponse;

@Local
public interface Guia {

	GuiaProcesarResponse procesar(GuiaProcesarRequest mensaje);
	
	GuiaProcesarResponse procesarOffLine(GuiaProcesarRequest mensaje);
	
	GuiaConsultarResponse consultar(GuiaConsultarRequest mensaje) ;

	GuiaRecibirResponse recibir(GuiaRecibirRequest mensaje);
	


}
