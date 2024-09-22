package es.iesjandula.reaktor_printers_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Francisco Manuel Benítez Chico
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DtoConstante
{
	/** Atributo clave de la constante */
	private String clave ;
	
	/** Atributo valor de la constante */
	private String valor ;
}

