package es.iesjandula.reaktor.printers_server.scheduler;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.base.utils.BaseException;
import es.iesjandula.reaktor.base.utils.FechasUtils;
import es.iesjandula.reaktor.base_client.dtos.NotificationWebDto;
import es.iesjandula.reaktor.base_client.requests.notificaciones.RequestNotificacionesEnviarWeb;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;
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

	@Autowired
	private RequestNotificacionesEnviarWeb requestNotificacionesEnviarWeb ;

	/** Formato de fecha para mostrar en los logs */
	private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss") ;

	/**
	 * Se ejecuta cada 5 minutos entre las 7:45 y las 20:30 de los días laborables 
	 * para calcular cuándo fue la última impresión realizada en cada impresora.
	 */
	@Scheduled(cron = "0 45-59/5 7 * * MON-FRI")
	@Scheduled(cron = "0 */5 8-19 * * MON-FRI")
	@Scheduled(cron = "0 0-30/5 20 * * MON-FRI")
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
				// Obtenemos el nombre de la impresora y la fecha de la última impresión
				String nombreImpresora = (String) fila[0] ;
				Date ultimaFecha       = (Date) fila[1] ;

				// Formateamos la fecha
				String fechaFormateada = FORMATO_FECHA.format(ultimaFecha) ;

				// Enviamos una notificación para que se muestre en la web
				this.enviarNotificacionWeb(nombreImpresora, fechaFormateada);

				// Logueamos
				log.info("Impresora: " + nombreImpresora + " - Última impresión: " + fechaFormateada) ;
			}
		}
		catch (Exception exception)
		{
			log.error("Error procesando el cálculo de última impresión por impresora: " + exception.getMessage(), exception) ;
		}

		log.info("Scheduler de cálculo de última impresión finalizado.") ;
	}

	/**
	 * Envia una notificación web con el nombre de la impresora
	 * 
	 * @param nombreImpresora nombre de la impresora
	 * @param fechaFormateada fecha de la última impresión formateada
	 * @return Integer identificador de la notificación web
	 */
	private Integer enviarNotificacionWeb(String nombreImpresora, String fechaFormateada)
	{
		try
		{
			// Creamos el DTO de la notificación web
			NotificationWebDto notificationWebDto = new NotificationWebDto();
			
			// Definimos el texto de la notificación web
			final String textoNotificacion = "La impresora " + nombreImpresora + " fue usada por última vez el " + fechaFormateada ;

			// Seteamos el texto de la notificación web
			notificationWebDto.setTexto(textoNotificacion);

			// Seteamos la fecha de inicio y fin de la notificación web
	
			// La fecha de inicio es justo ahora
			LocalDateTime fechaInicio = LocalDateTime.now();

			// Le pongo una fecha fin holgada para que se borre al día siguiente como muy tarde
			LocalDateTime fechaFin    = fechaInicio.plusMinutes(5);

			notificationWebDto.setFechaInicio(FechasUtils.convertirFecha(fechaInicio));
			notificationWebDto.setHoraInicio(FechasUtils.convertirHora(fechaInicio));
			notificationWebDto.setFechaFin(FechasUtils.convertirFecha(fechaFin));
			notificationWebDto.setHoraFin(FechasUtils.convertirHora(fechaFin));

			// Seteamos el receptor de la notificación web
			notificationWebDto.setReceptor(BaseClientConstants.RECEPTOR_NOTIFICACION_CLAUSTRO);

			// Seteamos el tipo de notificación web
			notificationWebDto.setTipo(BaseClientConstants.TIPO_NOTIFICACION_SOLO_TEXTO);
	
			// Lo notificamos por web y devolvemos el identificador de la notificación web
			Integer idNotificacion = this.requestNotificacionesEnviarWeb.enviarNotificacionWeb(notificationWebDto); 

			// Logueamos
			log.info("Notificación web enviada correctamente con identificador: " + idNotificacion) ;

			// Devolvemos el identificador de la notificación web
			return idNotificacion;
		}
		catch (BaseException | BaseClientException reaktorException)
		{
			// El error ya ha sido logueado previamente
			return null;
		}
	}
}