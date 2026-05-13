package es.iesjandula.reaktor.printers_server.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.printers_server.repository.IPrintActionRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Slf4j
@Component
public class PrinterScheduler
{
	@Autowired
	private IPrintActionRepository printActionRepository ;

	/** Formato de fecha para mostrar en los logs */
	private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss") ;

	/**
	 * Se ejecuta cada 5 minutos para calcular cuándo fue la última impresión realizada
	 * en cada impresora.
	 */
	@Scheduled(cron = "0 */5 * * * *")
	public void calcularUltimaImpresionPorImpresora()
	{
		log.info("Iniciando scheduler de cálculo de última impresión por impresora...") ;

		try
		{
			List<Object[]> ultimasImpresiones = this.printActionRepository.obtenerUltimaImpresionPorImpresora() ;

			if (ultimasImpresiones.isEmpty())
			{
				log.info("No hay impresiones realizadas registradas en ninguna impresora") ;
				return ;
			}

			log.info("Listado de última impresión por impresora:") ;

			for (Object[] fila : ultimasImpresiones)
			{
				String nombreImpresora = (String) fila[0] ;
				Date ultimaFecha       = (Date) fila[1] ;

				String fechaFormateada = FORMATO_FECHA.format(ultimaFecha) ;

				log.info("Impresora: " + nombreImpresora + " - Última impresión: " + fechaFormateada) ;
			}
		}
		catch (Exception exception)
		{
			log.error("Error procesando el cálculo de última impresión por impresora: " + exception.getMessage(), exception) ;
		}

		log.info("Scheduler de cálculo de última impresión finalizado.") ;
	}
}