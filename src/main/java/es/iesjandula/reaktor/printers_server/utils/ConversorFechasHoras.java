package es.iesjandula.reaktor.printers_server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	
    /**
     * @param localDate LocalDate que se desea convertir
     * @return Date convertido a partir de LocalDate
	 * @throws ParseException excepción al parsear el String
     */
    public static Date convertirLocalDateToDate(LocalDate localDate) throws ParseException 
    {
        Date outcome = null;
        
        if (localDate != null)
        {
            String formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ;
            outcome = DATE_FORMAT.parse(formattedDate) ;
        }

        return outcome;
    }
}
