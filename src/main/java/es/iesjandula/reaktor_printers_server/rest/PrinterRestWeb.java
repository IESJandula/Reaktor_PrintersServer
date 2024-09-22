package es.iesjandula.reaktor_printers_server.rest;

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

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.base.base_server.firebase.AuthorizationService;
import es.iesjandula.base.base_server.firebase.DtoUsuario;
import es.iesjandula.base.base_server.utils.BaseServerConstants;
import es.iesjandula.base.base_server.utils.BaseServerException;
import es.iesjandula.reaktor_printers_server.configurations.InicializacionSistema;
import es.iesjandula.reaktor_printers_server.dto.DtoConstante;
import es.iesjandula.reaktor_printers_server.dto.RequestDtoPrintQuery;
import es.iesjandula.reaktor_printers_server.dto.ResponseDtoGlobalState;
import es.iesjandula.reaktor_printers_server.dto.ResponseDtoPrintAction;
import es.iesjandula.reaktor_printers_server.models.Constante;
import es.iesjandula.reaktor_printers_server.models.DiaFestivo;
import es.iesjandula.reaktor_printers_server.models.PrintAction;
import es.iesjandula.reaktor_printers_server.repository.IConstanteRepository;
import es.iesjandula.reaktor_printers_server.repository.IDiaFestivoRepository;
import es.iesjandula.reaktor_printers_server.repository.IPrintActionRepository;
import es.iesjandula.reaktor_printers_server.repository.IPrinterRepository;
import es.iesjandula.reaktor_printers_server.utils.Constants;
import es.iesjandula.reaktor_printers_server.utils.ConversorFechasHoras;
import es.iesjandula.reaktor_printers_server.utils.PdfMetaInfo;
import es.iesjandula.reaktor_printers_server.utils.PrintersServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Francisco Manuel Benítez Chico
 */
@RestController
@RequestMapping("/printers/web")
@Slf4j
public class PrinterRestWeb
{
    @Autowired
    private InicializacionSistema inicializacionCarpetas ;
	
	@Autowired
	private IPrinterRepository printerRepository ;

	@Autowired
	private IPrintActionRepository printActionRepository ;
	
	@Autowired
	private IDiaFestivoRepository diaFestivoRepository ;
	
	@Autowired
	private AuthorizationService authorizationService ;
	
    @Autowired
    private IConstanteRepository constantesRepository ;

	/**
	 * Devuelve las impresoras guardadas en base de datos
	 * @param authorizationHeader token JWT de autorización
	 * @return la lista de impresoras
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/printers")
	public ResponseEntity<?> obtenerImpresoras(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
			
			// Obtenemos la lista de impresoras
			return ResponseEntity.ok().body(this.printerRepository.getPrinters()) ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @param authorizationHeader token JWT de autorización
	 * @return la lista de estados disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/states")
	public ResponseEntity<?> obtenerEstados(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
		
			// Obtenemos la lista de estados
			return ResponseEntity.ok().body(Constants.STATES_LIST) ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @param authorizationHeader token JWT de autorización
	 * @return la lista de orientaciones disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/orientations")
	public ResponseEntity<?> obtenerOrientaciones(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
		
			// Obtenemos la lista de orientaciones
			return ResponseEntity.ok().body(Constants.ORIENTATIONS_LIST) ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @param authorizationHeader token JWT de autorización
	 * @return la lista de colores disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/colors")
	public ResponseEntity<?> obtenerColores(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
		
			// Obtenemos la lista de colores
			return ResponseEntity.ok().body(Constants.COLORS_LIST) ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @param authorizationHeader token JWT de autorización
	 * @return la lista de caras disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/sides")
	public ResponseEntity<?> obtenerCaras(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
		
			// Obtenemos la lista de colores
			return ResponseEntity.ok().body(Constants.SIDES_LIST) ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;
	
			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @param authorizationHeader token JWT de autorización
	 * @return response Dto Global State
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/validations")
	public ResponseEntity<?> validacionesGlobalesPreviasImpresion(@RequestHeader("Authorization") String authorizationHeader)
	{
		ResponseDtoGlobalState responseDtoGlobalState = new ResponseDtoGlobalState() ;
		
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;

			// Validamos el día actual
			this.validacionesGlobalesPreviasImpresionInternal(responseDtoGlobalState) ;
			
			// Obtenemos las impresoras y sus estados
			responseDtoGlobalState.setDtoPrinters(this.printerRepository.getPrinters()) ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras",
	        									exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras", printersServerException) ;
			
			// Seteamos como error interno
	    	responseDtoGlobalState.setGlobalError("Error interno. Impresión no permitida") ;
		}

	    // Si todo va bien, se devuelve un 200
	    return ResponseEntity.ok().body(responseDtoGlobalState) ;
	}

	/**
	 * @param responseDtoGlobalState Response DTO Global State 
	 * @throws PrintersServerException con un error
	 */
	private void validacionesGlobalesPreviasImpresionInternal(ResponseDtoGlobalState responseDtoGlobalState) throws PrintersServerException
	{
	    // Obtener la fecha y hora actual
	    LocalDate fechaActual = LocalDate.now() ;
	    
	    // Vemos si está deshabilitada la impresion
	    this.validacionesGlobalesPreviasImpresionInternalImpresionDeshabilitada(responseDtoGlobalState) ;
	    
		// Vemos si estamos en un día especial
	    boolean diaEspecialImpresion = this.validacionesGlobalesPreviasImpresionInternalDiaEspecialImpresion(responseDtoGlobalState) ;
	    
	    if (!diaEspecialImpresion)
	    {
		    if (responseDtoGlobalState.getGlobalError() == null)
		    {
			    // Vemos si se cumplen los horarios de impresión
			    this.validacionesGlobalesPreviasImpresionInternalHoraPermitida(responseDtoGlobalState, fechaActual) ;
		    }

		    if (responseDtoGlobalState.getGlobalError() == null)
		    {
		    	// Vemos si no estamos en día de fiesta
		    	this.validacionesGlobalesPreviasImpresionInternalDiaFiesta(responseDtoGlobalState, fechaActual) ;
		    }
	    }
	}

	/**
	 * @param responseDtoGlobalState Response DTO Global State 
	 * @throws PrintersServerException con un error
	 */
	private boolean validacionesGlobalesPreviasImpresionInternalDiaEspecialImpresion(ResponseDtoGlobalState responseDtoGlobalState)
					throws PrintersServerException
	{
	    Optional<Constante> optionalHoraInicioImpresion = this.constantesRepository.findByClave(Constants.TABLA_CONST_DIA_ESPECIAL_IMPRESION) ;
	    if (!optionalHoraInicioImpresion.isPresent())
	    {
	    	String errorString = "Error obteniendo parametros" ;
	    	
	    	log.error(errorString + ". " + Constants.TABLA_CONST_DIA_ESPECIAL_IMPRESION) ;
	    	throw new PrintersServerException(Constants.ERR_CONSTANT_PROPERTY_NOT_FOUND, errorString) ;
	    }
	    
	    // Devolvemos el valor
		return Boolean.valueOf(optionalHoraInicioImpresion.get().getValor()) ;
	}

	/**
	 * @param responseDtoGlobalState Response DTO Global State 
	 * @throws PrintersServerException con un error
	 */
	private void validacionesGlobalesPreviasImpresionInternalImpresionDeshabilitada(ResponseDtoGlobalState responseDtoGlobalState) 
				 throws PrintersServerException
	{
		// Vemos si la impresión está deshabilitada
	    Optional<Constante> optionalAppDeshabilitada = this.constantesRepository.findByClave(Constants.TABLA_CONST_IMPRESION_DESHABILITADA) ;
	    if (!optionalAppDeshabilitada.isPresent())
	    {
	    	String errorString = "Error obteniendo parametros" ;
	    	
	    	log.error(errorString + ". " + Constants.TABLA_CONST_IMPRESION_DESHABILITADA) ;
	    	throw new PrintersServerException(Constants.ERR_CONSTANT_PROPERTY_NOT_FOUND, errorString) ;
	    }
	    	
	    if (!optionalAppDeshabilitada.get().getValor().isEmpty())
	    {
	    	responseDtoGlobalState.setGlobalError(optionalAppDeshabilitada.get().getValor()) ;
	    }
	}
	
	/**
	 * @param responseDtoGlobalState Response DTO Global State 
	 * @param fechaActual fecha actual
	 */
	private void validacionesGlobalesPreviasImpresionInternalHoraPermitida(ResponseDtoGlobalState responseDtoGlobalState, LocalDate fechaActual) 
				 throws PrintersServerException
	{
	    DayOfWeek diaActual   = fechaActual.getDayOfWeek() ;
	    LocalTime horaActual  = LocalTime.now() ;
		
		// Obtenemos la hora de inicio impresion
	    Optional<Constante> optionalHoraInicioImpresion = this.constantesRepository.findByClave(Constants.TABLA_CONST_HORA_INICIO_IMPRESION) ;
	    if (!optionalHoraInicioImpresion.isPresent())
	    {
	    	String errorString = "Error obteniendo parametros" ;
	    	
	    	log.error(errorString + ". " + Constants.TABLA_CONST_HORA_INICIO_IMPRESION) ;
	    	throw new PrintersServerException(Constants.ERR_CONSTANT_PROPERTY_NOT_FOUND, errorString) ;
	    }
	    
		// Obtenemos la hora de fin impresion
	    Optional<Constante> optionalHoraFinImpresion = this.constantesRepository.findByClave(Constants.TABLA_CONST_HORA_FIN_IMPRESION) ;
	    if (!optionalHoraFinImpresion.isPresent())
	    {
	    	String errorString = "Error obteniendo parametros" ;
	    	
	    	log.error(errorString + ". " + Constants.TABLA_CONST_HORA_FIN_IMPRESION) ;
	    	throw new PrintersServerException(Constants.ERR_CONSTANT_PROPERTY_NOT_FOUND, errorString) ;
	    }
	    
	    // Hacemos split de ambas
	    String[] horaInicioImpresion = optionalHoraInicioImpresion.get().getValor().split(":") ;
	    String[] horaFinImpresion 	 = optionalHoraFinImpresion.get().getValor().split(":") ;
	    	
	    // Obtenemos los valores numéricos
	    int horaInicioImpresionHora    = Integer.valueOf(horaInicioImpresion[0]) ;
	    int horaInicioImpresionMinutos = Integer.valueOf(horaInicioImpresion[1]) ;
	    int horaFinImpresionHora       = Integer.valueOf(horaFinImpresion[0]) ;
	    int horaFinImpresionMinutos    = Integer.valueOf(horaFinImpresion[1]) ;
	    
	    // Verificamos si es fuera del horario permitido (antes de las 8 o después de las 20 de lunes a viernes)
	    if (diaActual == DayOfWeek.SATURDAY ||
	        diaActual == DayOfWeek.SUNDAY   || 
	        horaActual.isBefore(LocalTime.of(horaInicioImpresionHora, horaInicioImpresionMinutos)) || 
	        horaActual.isAfter(LocalTime.of(horaFinImpresionHora, horaFinImpresionMinutos)))
	    {

	    	String horaInicioImpresionHoraString 	= horaInicioImpresion[0] ;
	    	String horaInicioImpresionMinutosString = horaInicioImpresion[1] ;
	    	String horaFinImpresionHoraString       = horaFinImpresion[0] ;
	    	String horaFinImpresionMinutosString    = horaFinImpresion[1] ;
	    	
	    	responseDtoGlobalState.setGlobalError("Impresión no permitida. Activa de lunes a viernes de " + 
	    			horaInicioImpresionHoraString + ":" + horaInicioImpresionMinutosString + " a " +
	    			horaFinImpresionHoraString 	  + ":" + horaFinImpresionMinutosString) ;
	    }
	}
	
	/**
	 * @param responseDtoGlobalState Response DTO Global State 
	 * @param fechaActual fecha actual
	 */
	private void validacionesGlobalesPreviasImpresionInternalDiaFiesta(ResponseDtoGlobalState responseDtoGlobalState, LocalDate fechaActual)
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
	
	/**
	 * Devuelve las impresiones filtradas por los parametros pasados como parametro, si no se envia ninguno manda todos
	 * @param authorizationHeader token JWT de autorización
	 * @param printerQuery parámetros de la query
	 * @return lista de ResponseDtoPrintAction con aquellos encontrados
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/filter")
	public ResponseEntity<?> buscarImpresiones(@RequestHeader("Authorization") String authorizationHeader,
											   @RequestBody(required = true) RequestDtoPrintQuery printQuery) 
	{
	    try 
	    {
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
	    	
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
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
        										BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarImpresiones",
										 		exception) ;
	        
			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarImpresiones", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}
	
	/**
	 * Guarda en base de datos la peticion de impresion realizada desde la web y guarda el documento en el servidor
	 * @param authorizationHeader token JWT de autorización
	 * @param printer impresora
	 * @param numCopies número de copias
	 * @param orientation horizontal o vertical
	 * @param color color o blanco y negro
	 * @param sides una cara o doble cara
	 * @param user usuario
	 * @param file fichero
	 * @return ok si todos los parámetros eran correctos y no hubo error guardando en base de datos
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/print", consumes = "multipart/form-data")
	public ResponseEntity<?> imprimirPdf(@RequestHeader("Authorization") String authorizationHeader,
										 @RequestParam(required = true) String printer,     @RequestParam(required = true) Integer numCopies,
										 @RequestParam(required = true) String orientation, @RequestParam(required = true) String color,
										 @RequestParam(required = true) String sides, 		@RequestParam(required = true) String user,
										 @RequestBody(required = true)  MultipartFile file)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
			
			// Obtenemos los metadatos del fichero PDF
			PdfMetaInfo pdfMetaInfo = this.obtenerInformacionFicheroPdf(numCopies, sides, file) ;
			
			// Creamos el objeto printAction con la configuracion recibida
			PrintAction printAction = new PrintAction() ;
			
			printAction.setUser(user) ;
			printAction.setPrinter(printer) ;
			printAction.setStatus(Constants.STATE_TODO) ;
			printAction.setFileName(pdfMetaInfo.getOriginalFilename()) ;
			printAction.setCopies(numCopies) ;
			printAction.setColor(color) ;
			printAction.setOrientation(orientation) ;
			printAction.setSides(sides) ;
			printAction.setDate(new Date()) ;
			printAction.setFileSizeInKB(pdfMetaInfo.getFileSizeInKB()) ;
			printAction.setNumeroPaginasPdf(pdfMetaInfo.getNumeroPaginasPdf()) ;
			printAction.setHojasTotales(pdfMetaInfo.getHojasTotales()) ;
			
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
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (PrintersServerException printersServerException)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(printersServerException.getBodyExceptionMessage()) ;
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
        										BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf",
										 		exception) ;
	        
			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}
	
	
	/**
	 * @param authorizationHeader token JWT de autorización
	 * @param id identificador de la impresión
	 * @return 200 si todo fue bien y error en otro caso
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/cancel")
	public ResponseEntity<?> cancelarImpresion(@RequestHeader("Authorization") String authorizationHeader,
											   @RequestParam(required = true) Long id)
	{
		try
		{
			// Primero autorizamos la petición y obtenemos la información del usuario
			DtoUsuario usuario = this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
			
			// Buscamos la tarea de impresión por id
			Optional<PrintAction> optionalPrintAction = this.printActionRepository.findById(id) ;
			
            // Si existe la impresión ...
            if (!optionalPrintAction.isPresent())
            {
    			String infoUsuario = "ID: " + id + ", usuario: " + usuario.getNombre() + " " + usuario.getApellidos() + ". " ;
    			String errorString = "Se intentó cancelar una tarea que no existe" ;
    			
    			log.error(infoUsuario + errorString) ;
    			throw new PrintersServerException(Constants.ERR_USER_TRIED_TO_CANCEL_NO_EXISTING_TASK, errorString) ;
            }
            
        	// Obtenemos el valor
        	PrintAction printAction = optionalPrintAction.get() ;
        	
        	// Borramos el fichero PDF y la carpeta del sistema
        	this.borrarFicheroPdfYcarpetaDelSistema(id, usuario, printAction) ;
        	
        	// Validamos la cancelación
        	this.cancelarImpresionValidacion(id, usuario, printAction) ;
        	
        	// Cambiamos el valor a cancelar
        	printAction.setStatus(Constants.STATE_CANCELED) ;
        	
        	// Actualizamos la BBDD
        	this.printActionRepository.saveAndFlush(printAction) ;
            
            return ResponseEntity.ok().build() ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (PrintersServerException printersServerException)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(printersServerException.getBodyExceptionMessage()) ;
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
        										BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf",
										 		exception) ;
	        
			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}

	/**
	 * @param id id de la tarea
	 * @param usuario usuario que la cancela
	 * @param printAction tarea a cancelar
	 * @throws PrintersServerException con un error
	 */
	private void cancelarImpresionValidacion(Long id, DtoUsuario usuario, PrintAction printAction) throws PrintersServerException
	{
		// Si la tarea no está pendiente, no se puede cancelar
		if (!printAction.getStatus().equals(Constants.STATE_TODO))
		{
			String infoUsuario = "ID: " + id + ", usuario: " + usuario.getNombre() + " " + usuario.getApellidos() + ". " ;
			String errorString = "Se intentó cancelar una tarea que no está pendiente" ;
			
			log.error(infoUsuario + errorString) ;
			throw new PrintersServerException(Constants.ERR_USER_TRIED_TO_CANCEL_NO_PENDING_TASK, errorString) ;
		}
		
		// Obtenemos el nombre y apellidos del usuario
		String nombreYapellidos = usuario.getNombre() + " " + usuario.getApellidos() ;
		
		// Si el usuario trató de cancelar una tarea que no era suya, devolver error
		if (!printAction.getUser().equals(nombreYapellidos))
		{
			String infoUsuario = "ID: " + id + ", usuario: " + usuario.getNombre() + " " + usuario.getApellidos() + ". " ;
			String errorString = "Se intentó cancelar una tarea que no era suya" ;
			
			log.error(infoUsuario + errorString) ;
			throw new PrintersServerException(Constants.ERR_USER_TRIED_TO_CANCEL_ANOTHER_USER_TASK, errorString) ;           		
		}
	}
	
	/**
	 * @param id id de la tarea
	 * @param usuario usuario que la cancela
	 * @param printAction tarea a cancelar
	 * @throws PrintersServerException con un error
	 */
	private void borrarFicheroPdfYcarpetaDelSistema(Long id, DtoUsuario usuario, PrintAction printAction) throws PrintersServerException
	{
    	File carpetaFichero = new File(this.inicializacionCarpetas.getCarpetaConImpresionesPendientes() + File.separator + printAction.getId()) ; 
        File ficheroAborrar = new File(carpetaFichero, printAction.getFileName()) ;
        
        // Si no existe, generar una excepción
        if (!ficheroAborrar.exists())
        {
			String infoUsuario = "ID: " + id + ", usuario: " + usuario.getNombre() + " " + usuario.getApellidos() + ". " ;
			String errorString = "Se intentó cancelar una tarea que no tenía PDF en el sistema" ;
			
			log.error(infoUsuario + errorString) ;
			throw new PrintersServerException(Constants.ERR_USER_TRIED_TO_CANCEL_WITHOUT_PDF_IN_SYSTEM, errorString) ;             	
        }
        
        // Borramos primero el fichero y después el directorio
        ficheroAborrar.delete() ;
        carpetaFichero.delete() ;
	}
	
	/**
	 * @param numCopies número de copias
	 * @param sides a una cara o doble cara
	 * @param multipartFile fichero PDF
	 * @return PdfMetaInfo
	 * @throws PrintersServerException con un error
	 */
	private PdfMetaInfo obtenerInformacionFicheroPdf(Integer numCopies, String sides, MultipartFile multipartFile) throws PrintersServerException
	{
		if (multipartFile.isEmpty())
		{
			String errorString = "El fichero " + multipartFile.getOriginalFilename() + " está vacío" ;
            
			log.error(errorString) ;
			throw new PrintersServerException(Constants.ERR_IOEXCEPTION_GETTING_METAINFO_PDF, errorString) ;
        }
		
		try
		{
			// Cargamos el PDF
			PDDocument pdDocument = Loader.loadPDF(multipartFile.getBytes()) ;
			
			 // Obtenemos el tamaño del archivo en bytes y convertirlo a kilobytes
            long fileSizeInKB 	  = multipartFile.getSize() / 1024 ;
			
			// Obtenemos el número de páginas
			double numeroPaginasPdf  = pdDocument.getNumberOfPages() ;
			
			// Validamos si está a doble cara
			boolean dobleCara 	  = Constants.SIDES_DOUBLE_SIDE.equals(sides) ;

			// Calculamos el número de hojas totales
			double hojasTotales 	  = numeroPaginasPdf ;
			if (dobleCara)
			{
				hojasTotales = Math.ceil(numeroPaginasPdf / 2) ;
			}
			
			// Las hojas totales las multiplicamos por el número de copias
			hojasTotales = hojasTotales * numCopies ;
			
			// Guardo todo en un objeto que contenga esta información
			return new PdfMetaInfo(multipartFile.getOriginalFilename(), fileSizeInKB, (int) numeroPaginasPdf, (int) hojasTotales) ;
		}
		catch (IOException ioException)
		{
			String errorString = "IOException mientras se leía la metainformación del fichero PDF: " + multipartFile.getOriginalFilename() ;
			
			log.error(errorString, ioException) ;
			throw new PrintersServerException(Constants.ERR_IOEXCEPTION_GETTING_METAINFO_PDF, errorString, ioException) ;
		}
	}
	
	/**
	 * @param authorizationHeader token JWT de autorización
	 * @return la lista de constantes disponibles
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/constantes")
	public ResponseEntity<?> actualizarConstantes(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
			
			// Obtenemos las constantes de BBDD
			List<DtoConstante> dtoConstanteList = this.constantesRepository.findAllAsDto() ;
			
			// Devolvemos la lista de constantes
			return ResponseEntity.ok().body(dtoConstanteList) ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerConstantes",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerConstantes", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @param 
	 * @return la lista de constantes disponibles
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/constantes")
	public ResponseEntity<?> actualizarConstantes(@RequestHeader("Authorization") String authorizationHeader,
				  								  @RequestBody(required = true) List<DtoConstante> dtoConstanteList)
	{
		try
		{
			// Primero autorizamos la petición
			this.authorizationService.autorizarPeticion(authorizationHeader, BaseServerConstants.ROLE_PROFESOR) ;
			
			// Iteramos y vamos actualizando los cambios
			for (DtoConstante dtoConstante : dtoConstanteList)
			{
				// Creamos una instancia del modelo
				Constante constante = new Constante(dtoConstante.getClave(), dtoConstante.getValor()) ;
				
				// Almacenamos en BBDD
				this.constantesRepository.saveAndFlush(constante) ;
			}
			
			// Devolvemos 200
			return ResponseEntity.ok().build() ;
		}
		catch (BaseServerException baseServerException)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseServerException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerConstantes",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarConstantes", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
}
