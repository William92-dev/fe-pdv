package com.gizlocorp.gnvoice.utilitario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FechaUtil {
	
	public static final String patronFechaTiempo24 = "yyyy/MM/dd HH:mm";
	
	public static String formatearFecha(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return date == null ? "" : formatter.format(date);
	}

	public static Date toDate(String strFecha){
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = null;
		try {
			fecha = formatoDelTexto.parse(strFecha);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fecha;
	}
}
