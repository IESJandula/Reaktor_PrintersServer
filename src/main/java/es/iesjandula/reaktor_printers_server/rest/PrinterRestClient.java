package es.iesjandula.reaktor_printers_server.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.base.base_server.firebase.AuthorizationService;
import es.iesjandula.base.base_server.utils.BaseServerConstants;
import es.iesjandula.reaktor_printers_server.configurations.InicializacionSistema;
import es.iesjandula.reaktor_printers_server.dto.DtoPrinters;
import es.iesjandula.reaktor_printers_server.models.PrintAction;
import es.iesjandula.reaktor_printers_server.models.Printer;
import es.iesjandula.reaktor_printers_server.repository.IPrintActionRepository;
import es.iesjandula.reaktor_printers_server.repository.IPrinterRepository;
import es.iesjandula.reaktor_printers_server.utils.Constants;
import es.iesjandula.reaktor_printers_server.utils.PrintersServerException;
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
	private AuthorizationService authorizationService ;

	/**
	 * Endpoint que guarda las impresoras guardadas en base de datos
	 * @param authorizationHeader token JWT de autorización
	 * @param listPrinters lista de impresoras actuales
	 * @return ok si se guarda correctamente
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/printers", consumes = "application/json")
	public ResponseEntity<?> actualizarImpresorasActuales(@RequestHeader("Authorization") String authorizationHeader,
														  @RequestBody(required = true) List<DtoPrinters> listPrinters)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_CLIENTE_IMPRESORA) ;
			
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
                    printer.setLastUpdate(dtoPrinter.getLastUpdate()) ;
                }
                else
                {
                    // Si no existe, creamos una nueva impresora
                	printer = new Printer(dtoPrinter.getName(),
                						  dtoPrinter.getStatusId(),
                						  dtoPrinter.getStatus(),
                						  dtoPrinter.getPrintingQueue(),
                						  dtoPrinter.getLastUpdate()) ;
                }
                
                // Actualizamos la base de datos
                this.printerRepository.saveAndFlush(printer) ;
            }

            return ResponseEntity.ok().build();
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarImpresorasActuales",
										 		exception) ;
	        
			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarImpresorasActuales", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}

	/**
	 * Configura y envia a la maquina cliente la informacion para realizar la impresion
	 * @param authorizationHeader token JWT de autorización
	 * @return obtiene una tarea para imprimir
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/print")
	public ResponseEntity<?> buscarTareaParaImprimir(@RequestHeader("Authorization") String authorizationHeader)
	{
		File carpetaFichero   = null ;
		File ficheroAimprimir = null ;
		
	    try
	    {
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_CLIENTE_IMPRESORA) ;
	    	
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
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarTareaParaImprimir",
	                                            exception) ;

	        log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarTareaParaImprimir", printersServerException) ;
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
	 * @param authorizationHeader token JWT de autorización
	 * @param id identificador de la tarea
	 * @param status estado de la tarea
	 * @param message mensaje de respuesta
	 * @return información del estado de la impresión
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/status")
	public ResponseEntity<?> asignarEstadoRespuestaImpresion(@RequestHeader("Authorization") String authorizationHeader,
															 @RequestHeader(name = "id") String id,
														     @RequestHeader(name = "status") String status,
														     @RequestHeader(name = "message", required = false) String message,
														     @RequestHeader(name = "exception", required = false) String exceptionMessage)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_CLIENTE_IMPRESORA) ;

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
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "asignarEstadoRespuestaImpresion",
												exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "asignarEstadoRespuestaImpresion", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
}
