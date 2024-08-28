package es.iesjandula.remote_printer_server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class ConversorFechasHoras
{
	/** Attribute date format */
	private static SimpleDateFormat DATE_FORMAT  = new SimpleDateFormat("dd/MM/yyyy") ;
	
	/**
	 * @param formatoDiasMesesAnhos String en formato dd/MM/yyyy
	 * @return en formato HH:MM
	 * @throws ParseException excepción al parsear el String
	 */
	public static Date convertirStringToDate(String formatoDiasMesesAnhos) throws ParseException
	{
		Date outcome = null ;
		
		if (formatoDiasMesesAnhos != null && !formatoDiasMesesAnhos.isEmpty())
		{
			outcome = DATE_FORMAT.parse(formatoDiasMesesAnhos) ;
		}
		
		return outcome ;
	}
}
