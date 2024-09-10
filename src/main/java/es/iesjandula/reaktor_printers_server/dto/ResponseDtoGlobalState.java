package es.iesjandula.reaktor_printers_server.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Francisco Manuel Benítez Chico
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseDtoGlobalState
{
	/** Atributo - Global error */
	private String globalError ;
	
	/** Atributo - Printers state */
	private List<DtoPrinters> dtoPrinters ;
}
