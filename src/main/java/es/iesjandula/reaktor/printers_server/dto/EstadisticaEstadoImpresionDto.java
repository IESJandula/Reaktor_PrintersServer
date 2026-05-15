package es.iesjandula.reaktor.printers_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO para la estadística de impresiones agrupadas por estado.
 * <p>
 * Este DTO se utiliza para representar la estadística de impresiones
 * agrupadas por su estado actual (Pendiente, Enviado, Realizado, Error,
 * Cancelada por usuario, Cancelada por TDE).
 * </p>
 */
@Data
@AllArgsConstructor
public class EstadisticaEstadoImpresionDto
{
	/** Estado de la impresión */
	private String estado ;
	
	/** Total de impresiones en ese estado */
	private Long totalImpresiones ;
}