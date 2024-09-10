package es.iesjandula.reaktor_printers_server.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Francisco Manuel Benítez Chico
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseDtoPrintAction
{
	/** Atributo - User */
	private String user ; 
	
	/** Atributo - Printer */
	private String printer ;
	
	/** Atributo - Status */
	private String status ;
	
	/** Atributo - File name */
    private String fileName ;
    
	/** Atributo - Copies */
    private int copies ;
    
	/** Atributo - Color */
    private String color ;

	/** Atributo - Orientation */
    private String orientation ;

	/** Atributo - Sides */
    private String sides ;

	/** Atributo - Date */
    private Date date ;
    
	/** Atributo - Error Message */
    private String errorMessage ;
}
