package es.iesjandula.reaktor.printers_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO para la estadística de páginas impresas por color (Blanco y negro / Color).
 * <p>
 * Este DTO se utiliza para representar la estadística de hojas impresas
 * agrupadas por tipo de color en el sistema.
 * </p>
 */
@Data
@AllArgsConstructor
public class EstadisticaColorImpresionDto
{
	/** Tipo de color (Blanco y negro o Color) */
	private String color ;
	
	/** Total de hojas impresas para ese color */
	private Long totalHojas ;
}