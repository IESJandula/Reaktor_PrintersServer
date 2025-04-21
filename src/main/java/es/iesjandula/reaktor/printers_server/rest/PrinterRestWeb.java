package es.iesjandula.reaktor.printers_server.rest;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.base.security.models.DtoUsuarioExtended;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.printers_server.configurations.InicializacionSistema;
import es.iesjandula.reaktor.printers_server.dto.DtoConstante;
import es.iesjandula.reaktor.printers_server.dto.RequestDtoPrintQuery;
import es.iesjandula.reaktor.printers_server.dto.ResponseDtoGlobalState;
import es.iesjandula.reaktor.printers_server.dto.ResponseDtoPrintAction;
import es.iesjandula.reaktor.printers_server.models.Constante;
import es.iesjandula.reaktor.printers_server.models.DiaFestivo;
import es.iesjandula.reaktor.printers_server.models.PrintAction;
import es.iesjandula.reaktor.printers_server.repository.IConstanteRepository;
import es.iesjandula.reaktor.printers_server.repository.IDiaFestivoRepository;
import es.iesjandula.reaktor.printers_server.repository.IPrintActionRepository;
import es.iesjandula.reaktor.printers_server.repository.IPrinterRepository;
import es.iesjandula.reaktor.printers_server.utils.Constants;
import es.iesjandula.reaktor.printers_server.utils.ConversorFechasHoras;
import es.iesjandula.reaktor.printers_server.utils.PdfMetaInfo;
import es.iesjandula.reaktor.printers_server.utils.PrintersServerException;
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
    private IConstanteRepository constantesRepository ;

	/**
	 * Devuelve las impresoras guardadas en base de datos
	 * 
	 * @return la lista de impresoras
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/printers")
	public ResponseEntity<?> obtenerImpresoras()
	{
		try
		{
			// Obtenemos la lista de impresoras
			return ResponseEntity.ok().body(this.printerRepository.getPrinters()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @return la lista de estados disponibles
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/states")
	public ResponseEntity<?> obtenerEstados()
	{
		try
		{
			// Obtenemos la lista de estados
			return ResponseEntity.ok().body(Constants.STATES_LIST) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @return la lista de orientaciones disponibles
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/orientations")
	public ResponseEntity<?> obtenerOrientaciones()
	{
		try
		{
			// Obtenemos la lista de orientaciones
			return ResponseEntity.ok().body(Constants.ORIENTATIONS_LIST) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @return la lista de colores disponibles
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/colors")
	public ResponseEntity<?> obtenerColores()
	{
		try
		{
			// Obtenemos la lista de colores
			return ResponseEntity.ok().body(Constants.COLORS_LIST) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @return la lista de caras disponibles
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/sides")
	public ResponseEntity<?> obtenerCaras()
	{
		try
		{
			// Obtenemos la lista de colores
			return ResponseEntity.ok().body(Constants.SIDES_LIST) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados",
											    exception) ;
	
			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerEstados", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @return response Dto Global State
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/validations")
	public ResponseEntity<?> validacionesGlobalesPreviasImpresion(@AuthenticationPrincipal DtoUsuarioExtended usuario)
	{
		ResponseDtoGlobalState responseDtoGlobalState = new ResponseDtoGlobalState() ;
		
		try
		{
			// Llamada al método interno para obtener error global si existiera
			responseDtoGlobalState.setGlobalError(this.validacionesGlobalesPreviasImpresionInternal(usuario)) ;
			
			// Obtenemos las impresoras y sus estados
			responseDtoGlobalState.setDtoPrinters(this.printerRepository.getPrinters()) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras",
	        									exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerImpresoras", printersServerException) ;
			
			// Seteamos como error interno
	    	responseDtoGlobalState.setGlobalError("Error interno. Impresión no permitida") ;
		}

	    // Si todo va bien, se devuelve un 200
	    return ResponseEntity.ok().body(responseDtoGlobalState) ;
	}

	/**
	 * Devuelve las impresiones filtradas por los parametros pasados como parametro, si no se envia ninguno manda todos
	 * 
	 * @param printerQuery parámetros de la query
	 * @return lista de ResponseDtoPrintAction con aquellos encontrados
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/filter")
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
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
        										BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarImpresiones",
										 		exception) ;
	        
			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "buscarImpresiones", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}
	
	/**
	 * Guarda en base de datos la peticion de impresion realizada desde la web y guarda el documento en el servidor
	 * 
	 * @param printer impresora
	 * @param numCopies número de copias
	 * @param orientation horizontal o vertical
	 * @param color color o blanco y negro
	 * @param sides una cara o doble cara
	 * @param user usuario
	 * @param file fichero
	 * @return ok si todos los parámetros eran correctos y no hubo error guardando en base de datos
	 */
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/print", consumes = "multipart/form-data")
	public ResponseEntity<?> imprimirPdf(@AuthenticationPrincipal DtoUsuarioExtended usuario,
										 @RequestParam(required = true) String printer,     @RequestParam(required = true) Integer numCopies,
										 @RequestParam(required = true) String orientation, @RequestParam(required = true) String color,
										 @RequestParam(required = true) String sides, 		@RequestParam(required = true) String user,
										 @RequestBody(required = true)  MultipartFile file)
	{
		try
		{
			// Llamada al método interno para realizar las validaciones
			String errorGlobal = this.validacionesGlobalesPreviasImpresionInternal(usuario) ;
			
			if (errorGlobal != null)
			{
				log.error(errorGlobal) ;
				throw new PrintersServerException(Constants.ERR_USER_TRIED_TO_PRINT_WITH_GLOBAL_ERROR, errorGlobal) ;
			}
			
			// Obtenemos los metadatos del fichero PDF
			PdfMetaInfo pdfMetaInfo = this.obtenerInformacionFicheroPdf(numCopies, sides, file) ;
			
			// Creamos y almacenamos la printAction en BBDD
			PrintAction printAction = this.imprimirPdfCrearYalmacenarPrintAction(printer, numCopies, orientation, color, sides, user, pdfMetaInfo);

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
		catch (PrintersServerException printersServerException)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(printersServerException.getBodyExceptionMessage()) ;
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
        										BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf",
										 		exception) ;
	        
			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}
	
	/**
	 * @return error global si existiera
	 * @throws PrintersServerException con un error
	 */
	private String validacionesGlobalesPreviasImpresionInternal(DtoUsuarioExtended usuario) throws PrintersServerException
	{
		// Vemos si está deshabilitada la impresion
		String outcome = this.validacionesGlobalesPreviasImpresionInternalImpresionDeshabilitada() ;
		
		if (outcome == null)
		{
			// Vemos si estamos en un día especial
		    boolean diaEspecialImpresion = this.validacionesGlobalesPreviasImpresionInternalDiaEspecialImpresion() ;
		    
		    if (!diaEspecialImpresion)
		    {
				// Vemos si el usuario tiene role DIRECCIÓN o ADMINISTRADOR
				boolean esAdminODireccion = usuario.getRoles().contains(BaseConstants.ROLE_DIRECCION) ||
											usuario.getRoles().contains(BaseConstants.ROLE_ADMINISTRADOR) ;
				
				if (!esAdminODireccion)
				{
					// Obtenemos la fecha y hora actual
					LocalDate fechaActual = LocalDate.now() ;
					
					if (outcome == null)
					{
						// Vemos si se cumplen los horarios de impresión
						outcome = this.validacionesGlobalesPreviasImpresionInternalHoraPermitida(fechaActual) ;
					}
		
					if (outcome == null)
					{
						// Vemos si no estamos en día de fiesta
						outcome = this.validacionesGlobalesPreviasImpresionInternalDiaFiesta(fechaActual) ;
					}
				}
		    }
		}
	    
	    return outcome ;
	}

	/**
	 * @throws PrintersServerException con un error
	 */
	private boolean validacionesGlobalesPreviasImpresionInternalDiaEspecialImpresion() throws PrintersServerException
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
	 * @return error global si existiera
	 * @throws PrintersServerException con un error
	 */
	private String validacionesGlobalesPreviasImpresionInternalImpresionDeshabilitada() throws PrintersServerException
	{
		String outcome = null ;
		
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
	    	outcome = optionalAppDeshabilitada.get().getValor() ;
	    }
	    
	    return outcome ;
	}
	
	/**
	 * @param fechaActual fecha actual
	 * @return error global si existiera
	 * @throws PrintersServerException con un error
	 */
	private String validacionesGlobalesPreviasImpresionInternalHoraPermitida(LocalDate fechaActual) throws PrintersServerException
	{
		String outcome = null ;
		
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
	    	
	    	outcome = "Impresión no permitida. Activa de lunes a viernes de " + 
	    									   horaInicioImpresionHoraString + ":" + horaInicioImpresionMinutosString + " a " +
	    									   horaFinImpresionHoraString 	  + ":" + horaFinImpresionMinutosString ;
	    }
	    
	    return outcome ;
	}
	
	/**
	 * @param fechaActual fecha actual
	 * @return error global si existiera
	 */
	private String validacionesGlobalesPreviasImpresionInternalDiaFiesta(LocalDate fechaActual)
	{
		String outcome = null ;
		
    	try
    	{
    		// Convertimos LocalDate a Date usando el método de utilidad
    		Date fechaActualDate = ConversorFechasHoras.convertirLocalDateToDate(fechaActual) ;
    		
    		// Verificamos si es un día festivo
    		Optional<DiaFestivo> diaFestivoOptional = this.diaFestivoRepository.findByFecha(fechaActualDate);
    		
    		if (diaFestivoOptional.isPresent())
    		{
    			DiaFestivo diaFestivo = diaFestivoOptional.get() ;
    			outcome = "Impresión no permitida. Hoy es día festivo: " + diaFestivo.getDescripcion() ;
    		}
    	}
    	catch (ParseException parseException)
    	{
    		outcome = "Error al leer los festivos: " + parseException.getMessage() ;	
    	}
    	
    	return outcome ;
	}

	/**
	 * Creamos y almacenamos la print action en BBDD
	 * @param printer impresora
	 * @param numCopies número de copias
	 * @param orientation horizontal o vertical
	 * @param color color o blanco y negro
	 * @param sides una cara o doble cara
	 * @param user usuario
	 * @param pdfMetaInfo PDF Meta information
	 * @return la referencia a la nueva Print Action creada
	 * @throws PrintersServerException con un error
	 */
	private PrintAction imprimirPdfCrearYalmacenarPrintAction(String printer, Integer numCopies, String orientation, String color,
															  String sides,   String user,       PdfMetaInfo pdfMetaInfo) 
						throws PrintersServerException
	{
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
		
		return printAction ;
	}
	
	
	/**
	 * @param id identificador de la impresión
	 * @return 200 si todo fue bien y error en otro caso
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/cancel")
	public ResponseEntity<?> cancelarImpresion(@AuthenticationPrincipal DtoUsuarioExtended usuario,
											   @RequestParam(required = true) Long id)
	{
		try
		{
			if (usuario == null)
			{
				log.error("Usuario no autenticado trató de cancelar la siguiente impresión: {}", id) ;
				
				throw new PrintersServerException(Constants.ERR_USER_NOT_AUTHENTICATED, Constants.ERR_USER_NOT_AUTHENTICATED_MSG) ;
		    }
			
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

        	// Por defecto ponemos que la canceló el usuario
        	printAction.setStatus(Constants.STATE_CANCELED_BY_USER) ;
        	
        	// Si la canceló el administrador, cambiamos el valor del estado por cancelado por el TDE
        	if (usuario.getRoles().contains(BaseConstants.ROLE_ADMINISTRADOR))
    		{
        		printAction.setStatus(Constants.STATE_CANCELED_BY_TDE) ;        		
    		}
        	
        	// Actualizamos la BBDD
        	this.printActionRepository.saveAndFlush(printAction) ;
            
            return ResponseEntity.ok().build() ;
		}
		catch (PrintersServerException printersServerException)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(printersServerException.getBodyExceptionMessage()) ;
		}
	    catch (Exception exception) 
	    {
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
        										BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf",
										 		exception) ;
	        
			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "imprimirPdf", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
	    }
	}

	/**
	 * @param id id de la tarea
	 * @param usuario usuario que la cancela
	 * @param printAction tarea a cancelar
	 * @throws PrintersServerException con un error
	 */
	private void cancelarImpresionValidacion(Long id, DtoUsuarioExtended usuario, PrintAction printAction) throws PrintersServerException
	{
		// Si la tarea no está pendiente, no se puede cancelar
		if (!printAction.getStatus().equals(Constants.STATE_TODO))
		{
			String infoUsuario = "ID: " + id + ", usuario: " + usuario.getNombre() + " " + usuario.getApellidos() + ". " ;
			String errorString = "Se intentó cancelar una tarea que no está pendiente" ;
			
			log.error(infoUsuario + errorString) ;
			throw new PrintersServerException(Constants.ERR_USER_TRIED_TO_CANCEL_NO_PENDING_TASK, errorString) ;
		}

		// Vemos si es un usuario normal
		if (!usuario.getRoles().contains(BaseConstants.ROLE_ADMINISTRADOR))
		{
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
	}
	
	/**
	 * @param id id de la tarea
	 * @param usuario usuario que la cancela
	 * @param printAction tarea a cancelar
	 * @throws PrintersServerException con un error
	 */
	private void borrarFicheroPdfYcarpetaDelSistema(Long id, DtoUsuarioExtended usuario, PrintAction printAction) throws PrintersServerException
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
	 * @return la lista de constantes disponibles
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/constantes")
	public ResponseEntity<?> actualizarConstantes()
	{
		try
		{
			// Obtenemos las constantes de BBDD
			List<DtoConstante> dtoConstanteList = this.constantesRepository.findAllAsDto() ;
			
			// Devolvemos la lista de constantes
			return ResponseEntity.ok().body(dtoConstanteList) ;
		}
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerConstantes",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerConstantes", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
	
	/**
	 * @param dtoConstanteList lista de constantes a actualizar
	 * @return la lista de constantes disponibles
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/constantes")
	public ResponseEntity<?> actualizarConstantes(@RequestBody(required = true) List<DtoConstante> dtoConstanteList)
	{
		try
		{
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
		catch (Exception exception)
		{
	        PrintersServerException printersServerException = 
	        		new PrintersServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerConstantes",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarConstantes", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
		}
	}
}
