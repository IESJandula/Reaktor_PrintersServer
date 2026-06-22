package es.iesjandula.reaktor.printers_server.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.base.utils.FechasUtils;
import es.iesjandula.reaktor.printers_server.dto.EstadisticaColorImpresionDto;
import es.iesjandula.reaktor.printers_server.dto.EstadisticaEstadoImpresionDto;
import es.iesjandula.reaktor.printers_server.repository.IPrintActionRepository;
import es.iesjandula.reaktor.printers_server.utils.Constants;
import es.iesjandula.reaktor.printers_server.utils.PrintersServerException;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/printers/estadisticas")
@RestController
@Slf4j
public class EstadisticasController
{
	@Autowired
	private IPrintActionRepository printActionRepository ;

	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/color-impresion")
	public ResponseEntity<?> obtenerHojasPorColor()
	{
		try
		{
			// Mapa para acumular totales (clave = tipo de color y valor = total de hojas impresas)
			Map<String, Long> mapaTotales = new HashMap<>() ;

			// Obtenemos el curso académico actual
			String cursoAcademico = FechasUtils.obtenerCursoAcademicoActual() ;

			// Obtenemos las hojas agrupadas por color desde el repositorio
			List<Object[]> hojasPorColor = this.printActionRepository.contarHojasPorColor(cursoAcademico) ;

			for (Object[] fila : hojasPorColor)
			{
				String color = (String) fila[0] ;
				Long totalHojas = ((Number) fila[1]).longValue() ;

				// Acumulamos los datos en el mapa.
				Long totalActual = mapaTotales.get(color) ;
				if (totalActual == null)
				{
					mapaTotales.put(color, totalHojas) ;
				}
				else
				{
					mapaTotales.put(color, totalActual + totalHojas) ;
				}
			}

			// Convertimos el mapa a una lista de DTOs
			List<EstadisticaColorImpresionDto> listaResultados = new ArrayList<>() ;
			for (String color : mapaTotales.keySet())
			{
				Long total = mapaTotales.get(color) ;
				listaResultados.add(new EstadisticaColorImpresionDto(color, total)) ;
			}

			// Ordenamos la lista de mayor a menor usando el algoritmo burbuja
			this.ordenarResultadosColor(listaResultados) ;

			return ResponseEntity.ok(listaResultados) ;
		}
		// Si ocurre un error lo capturamos, lo registramos en el log y devolvemos un error con un mensaje JSON.
		catch (Exception exception)
		{
			String mensajeError = "Error inesperado al obtener las estadísticas de color de impresión" ;
			log.error(mensajeError, exception) ;
			PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_ESTADISTICAS, mensajeError, exception) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}

	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/estado-impresion")
	public ResponseEntity<?> obtenerImpresionesPorEstado()
	{
		try
		{			
			// Mapa para acumular totales (clave = estado y valor = total de impresiones)
			Map<String, Long> mapaTotales = new HashMap<>() ;
		
			// Obtenemos el curso académico actual
			String cursoAcademico = FechasUtils.obtenerCursoAcademicoActual() ;

			// Obtenemos las impresiones agrupadas por estado desde el repositorio
			List<Object[]> impresionesPorEstado = this.printActionRepository.contarPorEstado(cursoAcademico) ;

			for (Object[] fila : impresionesPorEstado)
			{
				String estado = (String) fila[0] ;
				Long totalImpresiones = ((Number) fila[1]).longValue() ;

				// Acumulamos los datos en el mapa.
				Long totalActual = mapaTotales.get(estado) ;
				if (totalActual == null)
				{
					mapaTotales.put(estado, totalImpresiones) ;
				}
				else
				{
					mapaTotales.put(estado, totalActual + totalImpresiones) ;
				}
			}

			// Convertimos el mapa a una lista de DTOs
			List<EstadisticaEstadoImpresionDto> listaResultados = new ArrayList<>() ;
			for (String estado : mapaTotales.keySet())
			{
				Long total = mapaTotales.get(estado) ;
				listaResultados.add(new EstadisticaEstadoImpresionDto(estado, total)) ;
			}

			// Ordenamos la lista de mayor a menor usando el algoritmo burbuja
			this.ordenarResultadosEstado(listaResultados) ;

			return ResponseEntity.ok(listaResultados) ;
		}
		// Si ocurre un error lo capturamos, lo registramos en el log y devolvemos un error con un mensaje JSON.
		catch (Exception exception)
		{
			String mensajeError = "Error inesperado al obtener las estadísticas de estado de impresión" ;
			log.error(mensajeError, exception) ;
			PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_ESTADISTICAS, mensajeError, exception) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}

	// Método para ordenar la cantidad de hojas por color.

	private void ordenarResultadosColor(List<EstadisticaColorImpresionDto> lista)
	{
		int tamano = lista.size() ;
		for (int i = 0 ; i < tamano - 1 ; i++)
		{
			for (int j = 0 ; j < tamano - i - 1 ; j++)
			{
				EstadisticaColorImpresionDto actual = lista.get(j) ;
				EstadisticaColorImpresionDto siguiente = lista.get(j + 1) ;
				if (actual.getTotalHojas() < siguiente.getTotalHojas())
				{
					lista.set(j, siguiente) ;
					lista.set(j + 1, actual) ;
				}
			}
		}
	}

	// Método para ordenar la cantidad de impresiones por estado.

	private void ordenarResultadosEstado(List<EstadisticaEstadoImpresionDto> lista)
	{
		int tamano = lista.size() ;
		for (int i = 0 ; i < tamano - 1 ; i++)
		{
			for (int j = 0 ; j < tamano - i - 1 ; j++)
			{
				EstadisticaEstadoImpresionDto actual = lista.get(j) ;
				EstadisticaEstadoImpresionDto siguiente = lista.get(j + 1) ;
				if (actual.getTotalImpresiones() < siguiente.getTotalImpresiones())
				{
					lista.set(j, siguiente) ;
					lista.set(j + 1, actual) ;
				}
			}
		}
	}
}