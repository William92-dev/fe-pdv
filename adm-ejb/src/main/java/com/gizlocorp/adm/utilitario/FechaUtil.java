package com.gizlocorp.adm.utilitario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
/*import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;*/
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class FechaUtil {

	private static Logger log = Logger
			.getLogger(FechaUtil.class.getName());
	
	
	public static final String patronFechaTiempo24 = "yyyy/MM/dd HH:mm";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String CUSTOM_FORMAT = "ddMMyyyy";
	public static final String DB_FORMAT = "dd-MM-yyyy";

	public static int JGREG = 15 + 31 * (10 + 12 * 1582);
	public static double HALFSECOND = 0.5;

	public static String formatearFecha(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return date == null ? "" : formatter.format(date);
	}

	public static Date toDate(String strFecha) {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = null;
		try {
			fecha = formatoDelTexto.parse(strFecha);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fecha;
	}

	public static Date toDate(String strFecha, String strFormato) {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat(strFormato);
		Date fecha = null;
		try {
			fecha = formatoDelTexto.parse(strFecha);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fecha;
	}
	
	public static Date convertirJulianADate(Long injulian)
			throws ParseException {
		Date date = new SimpleDateFormat("1yyDDD").parse(injulian.toString());
		return date;

	}

	public static Long convertirDateAJulian(Date inDate) throws ParseException {
		String julian = new SimpleDateFormat("1yyDDD").format(inDate);
		return new Long(julian);

	}

	public static Date convertirLongADate(Long fecha) {
		Date date = null;
		if (fecha != null) {
			date = new Date(fecha);
		}
		return date;
	}

	public static void main(String arg[]) throws ParseException {
		log.debug(convertirJulianADate(new Long(114290)));
		log.debug(convertirDateAJulian(Calendar.getInstance()
				.getTime()));
	}
	
	/* public static Date sumaRestarFecha(Date fecha, int sumaresta, String opcion){
	        LocalDate date = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        //Con Java9
	        //LocalDate date = LocalDate.ofInstant(input.toInstant(), ZoneId.systemDefault());
	        TemporalUnit unidadTemporal = null;
	        switch(opcion){
	            case "DAYS":
	                unidadTemporal = ChronoUnit.DAYS;
	                break;
	            case "MONTHS":
	                unidadTemporal = ChronoUnit.MONTHS;
	                break;
	            case "YEARS":
	                unidadTemporal = ChronoUnit.YEARS;
	                break;
	            default:
	                //Controlar error
	        }
	        LocalDate dateResultado = date.minus(sumaresta, unidadTemporal);
	        return Date.from(dateResultado.atStartOfDay(ZoneId.systemDefault()).toInstant());
	    }*/
}
