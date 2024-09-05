package es.iesjandula.remote_printer_server.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.remote_printer_server.configurations.InicializacionSistema;
import es.iesjandula.remote_printer_server.dto.DtoPrinters;
import es.iesjandula.remote_printer_server.dto.RequestDtoPrintQuery;
import es.iesjandula.remote_printer_server.dto.ResponseDtoGlobalState;
import es.iesjandula.remote_printer_server.dto.ResponseDtoPrintAction;
import es.iesjandula.remote_printer_server.models.DiaFestivo;
import es.iesjandula.remote_printer_server.models.PrintAction;
import es.iesjandula.remote_printer_server.models.Printer;
import es.iesjandula.remote_printer_server.repository.IDiaFestivoRepository;
import es.iesjandula.remote_printer_server.repository.IPrintActionRepository;
import es.iesjandula.remote_printer_server.repository.IPrinterRepository;
import es.iesjandula.remote_printer_server.utils.Constants;
import es.iesjandula.remote_printer_server.utils.ConversorFechasHoras;
import es.iesjandula.remote_printer_server.utils.PrintersServerException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/printers")
@Slf4j
public class PrinterRest
{
    @Autowired
    private InicializacionSistema inicializacionCarpetas ;
	
	@Autowired
	private IPrinterRepository printerRepository ;

	@Autowired
	private IPrintActionRepository printActionRepository;
	
	@Autowired
	private IDiaFestivoRepository diaFestivoRepository ;

	/**
	 * Devuelve las impresoras guardadas en base de datos
	 * @return la lista de impresoras
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/web/printers")
	public ResponseEntity<?> obtenerImpresoras()
	{
		try
		{
			return ResponseEntity.ok().body(this.printerRepository.getPrinters()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
					  Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras",
					  exception) ;

			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @return la lista de estados disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/web/states")
	public ResponseEntity<List<String>> obtenerEstados()
	{
		return ResponseEntity.ok().body(Constants.STATES_LIST) ;
	}
	
	/**
	 * @return la lista de orientaciones disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/web/orientations")
	public ResponseEntity<List<String>> obtenerOrientaciones()
	{
		return ResponseEntity.ok().body(Constants.ORIENTATIONS_LIST) ;
	}
	
	/**
	 * @return la lista de colores disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/web/colors")
	public ResponseEntity<List<String>> obtenerColores()
	{
		return ResponseEntity.ok().body(Constants.COLORS_LIST) ;
	}
	
	/**
	 * @return la lista de caras disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/web/sides")
	public ResponseEntity<List<String>> obtenerCaras()
	{
		return ResponseEntity.ok().body(Constants.SIDES_LIST) ;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/web/validations")
	public ResponseEntity<?> validacionesGlobalesPreviasImpresion()
	{
		// Validamos el día actual
		ResponseDtoGlobalState responseDtoGlobalState = this.validacionesGlobalesPreviasImpresionValidarDia() ;
		
		try
		{
			// Obtenemos las impresoras y sus estados
			responseDtoGlobalState.setDtoPrinters(this.printerRepository.getPrinters()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
					  Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras",
					  exception) ;

			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras", printersServerException) ;
			
	    	responseDtoGlobalState.setGlobalError("Error interno. Impresión no permitida") ;
		}

	    // Si todo va bien, se devuelve un 200
	    return ResponseEntity.ok().body(responseDtoGlobalState) ;
	}

	/**
	 * @return una instancia de ResponseDtoGlobalState
	 */
	private ResponseDtoGlobalState validacionesGlobalesPreviasImpresionValidarDia()
	{
		ResponseDtoGlobalState responseDtoGlobalState = new ResponseDtoGlobalState() ;
		
	    // Obtener la fecha y hora actual
	    LocalDate fechaActual = LocalDate.now() ;
	    DayOfWeek diaActual   = fechaActual.getDayOfWeek() ;
	    LocalTime horaActual  = LocalTime.now() ;

	    // Verificamos si es fuera del horario permitido (antes de las 8 o después de las 20 de lunes a viernes)
	    if (diaActual == DayOfWeek.SATURDAY || diaActual == DayOfWeek.SUNDAY || horaActual.isBefore(LocalTime.of(8, 0)) || horaActual.isAfter(LocalTime.of(20, 0)))
	    {
	    	responseDtoGlobalState.setGlobalError("Impresión no permitida. Solo activa de lunes a viernes de 8:00 a 20:00") ;
	    }
	    
	    if (responseDtoGlobalState.getGlobalError() == null)
	    {
	    	try
	    	{
	    		// Convertimos LocalDate a Date usando el método de utilidad
	    		Date fechaActualDate = ConversorFechasHoras.convertirLocalDateToDate(fechaActual) ;
	    		
	    		// Verificamos si es un día festivo
	    		Optional<DiaFestivo> diaFestivoOptional = this.diaFestivoRepository.findByFecha(fechaActualDate);
	    		
	    		if (diaFestivoOptional.isPresent())
	    		{
	    			DiaFestivo diaFestivo = diaFestivoOptional.get() ;
	    			responseDtoGlobalState.setGlobalError("Impresión no permitida. Hoy es día festivo: " + diaFestivo.getDescripcion()) ;
	    		}
	    	}
	    	catch (ParseException parseException)
	    	{
	    		responseDtoGlobalState.setGlobalError("Error al leer los festivos: " + parseException.getMessage()) ;	
	    	}	    	
	    }
	    
		return responseDtoGlobalState ;
	}
	
	/**
	 * Devuelve las impresiones filtradas por los parametros pasados como parametro, si no se envia ninguno manda todos
	 * @param printerQuery parámetros de la query
	 * @return lista de ResponseDtoPrintAction con aquellos encontrados
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/web/filter")
	public ResponseEntity<?> buscarImpresiones(@RequestBody(required = true) RequestDtoPrintQuery printQuery) 
	{
	    try 
	    {
	    	// Convertimos las fechas string a date
	        Date startDate = ConversorFechasHoras.convertirStringToDate(printQuery.getStartDate()) ;
	        Date endDate   = ConversorFechasHoras.convertirStringToDate(printQuery.getEndDate()) ;

	        // Llamada a la query personalizada
	        List<ResponseDtoPrintAction> actions = this.printActionRepository.findPrintActions(printQuery.getUser(),
	        																				   printQuery.getPrinter(),
	        																				   printQuery.getStatus(),
	        																				   startDate,
	        																				   endDate) ;
	        // Devolvemos el resultado
	        return ResponseEntity.ok().body(actions);
	    } 
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
										 												  Constants.ERR_GENERIC_EXCEPTION_MSG + "buscarImpresiones",
										 												  exception) ;
	        
			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "buscarImpresiones", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}
	
	/**
	 * Guarda en base de datos la peticion de impresion realizada desde la web y guarda el documento en el servidor
	 * @param printer impresora
	 * @param numCopies número de copias
	 * @param orientation horizontal o vertical
	 * @param color color o blanco y negro
	 * @param sides una cara o doble cara
	 * @param user usuario
	 * @param file fichero
	 * @return ok si todos los parámetros eran correctos y no hubo error guardando en base de datos
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/web/print", consumes = "multipart/form-data")
	public ResponseEntity<?> imprimirPdf(@RequestParam(required = true) String printer,     @RequestParam(required = true) Integer numCopies,
										 @RequestParam(required = true) String orientation, @RequestParam(required = true) String color,
										 @RequestParam(required = true) String sides, 		@RequestParam(required = true) String user,
										 @RequestBody(required = true)  MultipartFile file)
	{
		try
		{
			// Creamos el objeto printAction con la configuracion recibida
			PrintAction printAction = new PrintAction() ;
			
			printAction.setUser(user) ;
			printAction.setPrinter(printer) ;
			printAction.setStatus(Constants.STATE_TODO) ;
			printAction.setFileName(file.getOriginalFilename()) ;
			printAction.setCopies(numCopies) ;
			printAction.setColor(color) ;
			printAction.setOrientation(orientation) ;
			printAction.setSides(sides) ;
			printAction.setDate(new Date()) ;
			
			// Almacenamos la instancia en BBDD
			this.printActionRepository.saveAndFlush(printAction) ;

			// Creamos un directorio temporal donde guardar el fichero
			File folder = new File(this.inicializacionCarpetas.getCarpetaConImpresionesPendientes() + File.separator + printAction.getId()) ;
			folder.mkdirs() ;
			
			// Creamos la ruta del fichero
			String filePath = this.inicializacionCarpetas.getCarpetaConImpresionesPendientes() + File.separator + printAction.getId() + File.separator + printAction.getFileName() ;
			
			// Guardamos el fichero en el servidor
			Files.write(Paths.get(filePath), file.getBytes()) ;

			return ResponseEntity.ok().build() ;
		}
		catch (IOException ioException)
		{
			String errorString = "IOException mientras se leía/escribía el contenido del fichero del usuario" ;
			
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_IOEXCEPTION_FILE_READING_CODE, errorString, ioException) ;

			log.error(errorString, printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
										 												  Constants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf",
										 												  exception) ;
	        
			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}
	
	/**
	 * Endpoint que guarda las impresoras guardadas en base de datos
	 * @param listPrinters lista de impresoras actuales
	 * @return ok si se guarda correctamente
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/client/printers", consumes = "application/json")
	public ResponseEntity<?> actualizarImpresorasActuales(@RequestBody(required = true) List<DtoPrinters> listPrinters)
	{
		try
		{
			// Iteramos sobre todas las impresoras recibidas
            for (DtoPrinters dtoPrinter : listPrinters)
            {
                // Buscamos la impresora por nombre (clave primaria)
                Optional<Printer> optionalPrinter = this.printerRepository.findById(dtoPrinter.getName()) ;

                Printer printer = null ;
                
                // Si existe la impresora ...
                if (optionalPrinter.isPresent())
                {
                    // ... la actualizamos
                    printer = optionalPrinter.get() ;
                    
                    printer.setStatusId(dtoPrinter.getStatusId()) ;
                    printer.setStatus(dtoPrinter.getStatus()) ;
                    printer.setPrintingQueue(dtoPrinter.getPrintingQueue()) ;
                }
                else
                {
                    // Si no existe, creamos una nueva impresora
                	printer = new Printer(dtoPrinter.getName(), dtoPrinter.getStatusId(), dtoPrinter.getStatus(), dtoPrinter.getPrintingQueue()) ;
                }
                
                // Actualizamos la base de datos
                this.printerRepository.saveAndFlush(printer) ;
            }

            return ResponseEntity.ok().build();
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
										 												  Constants.ERR_GENERIC_EXCEPTION_MSG + "actualizarImpresorasActuales",
										 												  exception) ;
	        
			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "actualizarImpresorasActuales", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}

	/**
	 * Configura y envia a la maquina cliente la informacion para realizar la impresion
	 * @return 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/client/print")
	public ResponseEntity<?> buscarTareaParaImprimir()
	{
		File carpetaFichero   = null ;
		File ficheroAimprimir = null ;
		
	    try
	    {
	        // Obtenemos todas las acciones con estado "TO DO" ordenadas por fecha ascendente
	        List<PrintAction> actions = this.printActionRepository.findByStatusOrderByDateAsc(Constants.STATE_TODO) ;

	        if (!actions.isEmpty())
	        {
	            // Obtenemos la primera tarea para imprimir (la más antigua)
	        	PrintAction printAction = actions.get(0) ;

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
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
	                                                                                      Constants.ERR_GENERIC_EXCEPTION_MSG + "buscarTareaParaImprimir",
	                                                                                      exception) ;

	        log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "buscarTareaParaImprimir", printersServerException) ;
	        return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	    finally
	    {
	    	// Si se cogió fichero para imprimir ...
	    	if (ficheroAimprimir != null)
	    	{
	    		// ... lo borramos junto con la carpeta del id
	    		
	    		ficheroAimprimir.delete() ;
	    		carpetaFichero.delete() ;
	    	}
	    }
	}

	/**
	 * Obtiene la información de la maquina cliente de como se ha finalizado una printAction
	 * @param id
	 * @param status
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/client/status")
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
	        PrintersServerException printersServerException = new PrintersServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
																	                      Constants.ERR_GENERIC_EXCEPTION_MSG + "asignarEstadoRespuestaImpresion",
																	                      exception) ;

			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "asignarEstadoRespuestaImpresion", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
}
