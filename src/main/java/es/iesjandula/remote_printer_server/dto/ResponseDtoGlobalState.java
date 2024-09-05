package es.iesjandula.remote_printer_server.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
