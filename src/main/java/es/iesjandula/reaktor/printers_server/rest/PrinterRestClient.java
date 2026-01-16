package es.iesjandula.reaktor.printers_server.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.base.utils.FechasUtils;
import es.iesjandula.reaktor.base_client.dtos.NotificationWebDto;
import es.iesjandula.reaktor.base_client.requests.notificaciones.RequestNotificacionesEnviarWeb;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;
import es.iesjandula.reaktor.printers_server.configurations.InicializacionSistema;
import es.iesjandula.reaktor.printers_server.dto.DtoPrinters;
import es.iesjandula.reaktor.printers_server.models.PrintAction;
import es.iesjandula.reaktor.printers_server.models.Printer;
import es.iesjandula.reaktor.printers_server.repository.IPrintActionRepository;
import es.iesjandula.reaktor.printers_server.repository.IPrinterRepository;
import es.iesjandula.reaktor.printers_server.utils.Constants;
import es.iesjandula.reaktor.printers_server.utils.PrintersServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Francisco Manuel Benítez Chico
 */
@RestController
@RequestMapping("/printers/client")
@Slf4j
public class PrinterRestClient
{
    @Autowired
    private InicializacionSistema inicializacionCarpetas ;
	
	@Autowired
	private IPrinterRepository printerRepository ;

	@Autowired
	private IPrintActionRepository printActionRepository ;
	
	@Autowired
	private RequestNotificacionesEnviarWeb requestNotificacionesEnviarWeb ;

	/**
	 * Endpoint que guarda las impresoras guardadas en base de datos
	 * 
	 * @param listPrinters lista de impresoras actuales
	 * @return ok si se guarda correctamente
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_IMPRESORA + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/printers", consumes = "application/json")
	public ResponseEntity<?> actualizarImpresorasActuales(@RequestBody(required = true) List<DtoPrinters> listPrinters)
	{
		try
		{
			// Iteramos sobre todas las impresoras recibidas
            for (DtoPrinters dtoPrinter : listPrinters)
            {
                // Buscamos la impresora por nombre (clave primaria)
                Optional<Printer> optionalPrinter = this.printerRepository.findById(dtoPrinter.getName()) ;

                // Si existe la impresora ...
                if (optionalPrinter.isPresent())
                {
					// Actualizamos la impresora actual ya existente
					this.actualizarImpresorasActualesYaExistente(dtoPrinter, optionalPrinter.get()) ;
                }
                else
                {
					// Introducimos los datos de la nueva impresora
					this.actualizarImpresorasActualesNueva(dtoPrinter) ;
                }
            }

            return ResponseEntity.ok().build();
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarImpresorasActuales",
										 		exception) ;
	        
			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarImpresorasActuales", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}

	/**
	 * Actualiza las impresoras actuales ya existentes
	 * 
	 * @param dtoPrinter DTO de la impresora
	 * @param printer Impresora actual ya existente
	 */
	private void actualizarImpresorasActualesYaExistente(DtoPrinters dtoPrinter, Printer printer)
	{
		// Si no coincide el estado de la impresora en BBDD con el nuevo recibido, es porque algo ha sucedido
		if (printer.getStatusId() != dtoPrinter.getStatusId())
		{
			// Si pasamos de un estado sin error a otro con error, enviamos una notificación web
			if (printer.getStatusId() == 0 && dtoPrinter.getStatusId() != 0)
			{
				// Enviamos una notificación web para informar de que la impresora no está disponible
				Integer idNotificacion = this.enviarNotificacionWeb(dtoPrinter.getName()) ;

				// Si la notificación web se envió correctamente, guardamos el identificador en la BBDD
				if (idNotificacion != null)
				{
					// Guardamos el identificador de la notificación web en la BBDD
					printer.setIdNotificacionWeb(idNotificacion) ;
				}
			}
			// Si pasamos a un estado sin error, borramos la notificación web si existe
			else if (printer.getIdNotificacionWeb() != null)
			{
				// Eliminamos la notificación web
				this.eliminarNotificacionWeb(printer.getIdNotificacionWeb()) ;
			}
		}

		// Actualizamos la impresora en BBDD
		printer.setStatusId(dtoPrinter.getStatusId()) ;
		printer.setStatus(dtoPrinter.getStatus()) ;
		printer.setPrintingQueue(dtoPrinter.getPrintingQueue()) ;
		printer.setLastUpdate(dtoPrinter.getLastUpdate()) ;

		// Actualizamos la base de datos
		this.printerRepository.saveAndFlush(printer) ;
	}

	/**
	 * Actualiza la impresora en BBDD
	 * 
	 * @param dtoPrinter DTO de la impresora
	 */
	private void actualizarImpresorasActualesNueva(DtoPrinters dtoPrinter)
	{
		// Inicializamos el identificador de la notificación web
		Integer idNotificacion = null;

		// Si el estado de la impresora es diferente de cero, enviamos una notificación web
		if (dtoPrinter.getStatusId() != 0)
		{
			// Enviamos una notificación web con el nombre de la impresora
			idNotificacion = this.enviarNotificacionWeb(dtoPrinter.getName()) ;
		}

		// Creamos una nueva impresora
		Printer printer = new Printer(dtoPrinter.getName(),
									  dtoPrinter.getStatusId(),
									  dtoPrinter.getStatus(),
									  dtoPrinter.getPrintingQueue(),
									  dtoPrinter.getLastUpdate(),
									  idNotificacion) ;

		// Actualizamos la base de datos
		this.printerRepository.saveAndFlush(printer) ;
	}

	/**
	 * Envia una notificación web con el nombre de la impresora
	 * 
	 * @param nombreImpresora nombre de la impresora
	 * @return Integer identificador de la notificación web
	 */
	private Integer enviarNotificacionWeb(String nombreImpresora)
	{
		try
		{
			// Creamos el DTO de la notificación web
			NotificationWebDto notificationWebDto = new NotificationWebDto();
			
			// Definimos el texto de la notificación web
			final String textoNotificacion = "La impresora " + nombreImpresora + " no está disponible" ;

			// Seteamos el texto de la notificación web
			notificationWebDto.setTexto(textoNotificacion);

			// Seteamos la fecha de inicio y fin de la notificación web
	
			// La fecha de inicio es justo ahora
			LocalDateTime fechaInicio = LocalDateTime.now();

			// Le pongo una fecha fin holgada para que se borre al día siguiente como muy tarde
			LocalDateTime fechaFin    = fechaInicio.plusDays(1);

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
		catch (BaseClientException baseClientException)
		{
			// El error ya ha sido logueado previamente
			return null;
		}
	}

	/**
	 * Elimina una notificación web
	 * 
	 * @param idNotificacion identificador de la notificación web
	 */
	private void eliminarNotificacionWeb(Integer idNotificacion)
	{
		try
		{
			// Eliminamos la notificación web
			this.requestNotificacionesEnviarWeb.eliminarNotificacionWeb(idNotificacion) ;

			// Logueamos
			log.info("Notificación web eliminada correctamente con identificador: " + idNotificacion) ;
		}
		catch (BaseClientException baseClientException)
		{
			// El error ya ha sido logueado previamente
		}
	}

	/**
	 * Configura y envia a la maquina cliente la informacion para realizar la impresion
	 * 
	 * @return obtiene una tarea para imprimir
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_IMPRESORA + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/print")
	public ResponseEntity<?> buscarTareaParaImprimir()
	{
		File carpetaFichero   = null ;
		File ficheroAimprimir = null ;
		
	    try
	    {
	        // Obtenemos todas las acciones con estado "TO DO" ordenadas por fecha ascendente
	        List<PrintAction> printActions = this.printActionRepository.findByStatusOrderByDateAsc(Constants.STATE_TODO) ;

	        if (!printActions.isEmpty())
	        {
	            // Obtenemos la primera tarea para imprimir (la más antigua)
	        	PrintAction printAction = this.buscarTareaParaImprimir(printActions) ;

	        	if (printAction != null)
	        	{
	        		// Construimos la ruta de la carpeta del fichero
	        		carpetaFichero = new File(this.inicializacionCarpetas.getCarpetaConImpresionesPendientes() + File.separator + printAction.getId()) ; 
	        		
	        		// Construimos la ruta del fichero a partir de la configuración
	        		ficheroAimprimir = new File(carpetaFichero, printAction.getFileName()) ;
	        		
	        		// Leemos el contenido del fichero en bytes
	        		byte[] contenidoDelFichero = Files.readAllBytes(ficheroAimprimir.toPath()) ;
	        		
	        		// Creamos un InputStreamResource a partir del contenido leído
	        		InputStreamResource outcomeInputStreamResource = new InputStreamResource(new java.io.ByteArrayInputStream(contenidoDelFichero)) ;
	        		
	        		// Preparamos los headers de la respuesta HTTP
	        		HttpHeaders headers = printAction.generaCabecera(ficheroAimprimir) ;
	        		
	        		// Actualizamos el estado de la acción a "Enviado"
	        		printAction.setStatus(Constants.STATE_SEND) ;
	        		this.printActionRepository.saveAndFlush(printAction) ;
	        		
	        		// Devolvemos la respuesta con el archivo y los headers
	        		return ResponseEntity.ok().headers(headers).body(outcomeInputStreamResource) ;
	        	}
	        }

	        // Si no hay acciones disponibles, devolvemos una respuesta vacía con estado 200
	        return ResponseEntity.ok().build() ;
	    }
	    catch (IOException ioException)
	    {
	        String errorString = "IOException mientras se leía el contenido del fichero para enviar a imprimir" ;
	        
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_IOEXCEPTION_FILE_READING_CODE, errorString, ioException) ;

	        log.error(errorString, printersServerException) ;
	        return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	    catch (Exception exception)
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarTareaParaImprimir",
	                                            exception) ;

	        log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarTareaParaImprimir", printersServerException) ;
	        return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	    finally
	    {
	    	// Si se cogió fichero para imprimir ...
	    	if (ficheroAimprimir != null)
	    	{
	    		// ... lo borramos
	    		ficheroAimprimir.delete() ;
	    	}

			// Si se cogió carpeta para borrar ...
			if (carpetaFichero != null)
			{
				// ... lo borramos
				carpetaFichero.delete() ;
			}
	    }
	}

	/**
	 * @param actions lista de print actions
	 * @return la tarea a imprimir
	 * @throws PrintersServerException con un error
	 */
	private PrintAction buscarTareaParaImprimir(List<PrintAction> printActions) throws PrintersServerException
	{
		PrintAction outcome = null ;

		int i=0 ;
		while (i < printActions.size() && outcome == null)
		{
			PrintAction temp = printActions.get(i) ;
			
        	// Construimos la ruta de la carpeta del fichero
        	File carpetaFichero   = new File(this.inicializacionCarpetas.getCarpetaConImpresionesPendientes() + File.separator + temp.getId()) ;
    		File ficheroAimprimir = new File(carpetaFichero, temp.getFileName()) ;
        	
        	// Si la carpeta o fichero no existe ...
    		if (!carpetaFichero.exists() || !ficheroAimprimir.exists())
    		{
    			// Logueamos esta situación anómala
    			log.error("Se trató de buscar una tarea de impresión pero la carpeta o fichero no existe: {}. " + 
    					  "Se va a actualizar su estado como ERROR", ficheroAimprimir.getAbsolutePath()) ;
    			
    			// Actualizamos la tarea de impresión como error
    			temp.setStatus(Constants.STATE_ERROR) ;
    			temp.setErrorMessage("El fichero para imprimir no existe en el servidor") ;
    			this.printActionRepository.saveAndFlush(temp) ;
    		}
    		else
    		{
    			outcome = temp ;
    		}
			
			i++ ;
		}

		return outcome ;
	}

	/**
	 * Obtiene la información de la maquina cliente de como se ha finalizado una printAction
	 * 
	 * @param id identificador de la tarea
	 * @param status estado de la tarea
	 * @param message mensaje de respuesta
	 * @return información del estado de la impresión
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_IMPRESORA + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/status")
	public ResponseEntity<?> asignarEstadoRespuestaImpresion(@RequestHeader(name = "id") String id,
														     @RequestHeader(name = "status") String status,
														     @RequestHeader(name = "message", required = false) String message,
														     @RequestHeader(name = "exception", required = false) String exceptionMessage)
	{
		try
		{

			// Buscamos la tarea de impresión por id
			Optional<PrintAction> action = this.printActionRepository.findById(Long.valueOf(id)) ;	
			
			// Si no la encontramos, informamos del error
			if (!action.isPresent())
			{
				String errorString = "La tarea con id " + id + " no fue encontrada para actualizar su status a " + status ;
				
				log.error(errorString) ;
				
				PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_PRINT_ACTION_NOT_FOUND_BY_ID, errorString) ;
		        return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
			}
			
			// Obtenemos la printAction
			PrintAction printAction = action.get() ; 
			
			// Una vez encontrada, actualizamos su estado
			printAction.setStatus(status) ;
			
			// Si el estado es "Error" entonces apuntamos el error
			if (Constants.STATE_ERROR.equals(status))
			{
				// Seteamos el mensaje de error
				printAction.setErrorMessage(message) ;
				
				// Logueamos el error como warning
				log.warn(message + " con la excepción: " + exceptionMessage) ;
			}
			
			// Guardamos en BBDD
			this.printActionRepository.saveAndFlush(printAction) ;
			
			return ResponseEntity.ok().build();
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "asignarEstadoRespuestaImpresion",
												exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "asignarEstadoRespuestaImpresion", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
}
