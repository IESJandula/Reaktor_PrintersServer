package es.iesjandula.reaktor.printers_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Francisco Manuel Benítez Chico
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestDtoPrintQuery
{
	/** Atributo - Start date */
	private String user ; 
	
	/** Atributo - Printer */
	private String printer ;
	
	/** Atributo - Status */
	private String status ;
	
	/** Atributo - Start date */
	private String startDate ;
	
	/** Atributo - End date */
	private String endDate ;
}
