package es.iesjandula.reaktor_printers_server.configurations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import es.iesjandula.base.base_server.resources_handler.ResourcesHandler;
import es.iesjandula.base.base_server.resources_handler.ResourcesHandlerFile;
import es.iesjandula.base.base_server.resources_handler.ResourcesHandlerJar;
import es.iesjandula.base.base_server.utils.BaseServerException;
import es.iesjandula.reaktor_printers_server.models.Constante;
import es.iesjandula.reaktor_printers_server.models.DiaFestivo;
import es.iesjandula.reaktor_printers_server.repository.IConstanteRepository;
import es.iesjandula.reaktor_printers_server.repository.IDiaFestivoRepository;
import es.iesjandula.reaktor_printers_server.utils.Constants;
import es.iesjandula.reaktor_printers_server.utils.ConversorFechasHoras;
import es.iesjandula.reaktor_printers_server.utils.PrintersServerException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Slf4j
@Service
public class InicializacionSistema
{
    @Autowired
    private IDiaFestivoRepository diaFestivoRepository ;
    
    @Autowired
    private IConstanteRepository constantesRepository ;

	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String modoDdl ;
	
	@Value("${" + Constants.PARAM_YAML_IMPRESION_DESHABILITADA + "}")
	private String appDeshabilitada ;
	
	@Value("${" + Constants.PARAM_YAML_HORA_INICIO_IMPRESION + "}")
	private String horaInicioImpresion ;
	
	@Value("${" + Constants.PARAM_YAML_HORA_FIN_IMPRESION + "}")
	private String horaFinImpresion ;
	
	@Value("${" + Constants.PARAM_YAML_DIA_ESPECIAL_IMPRESION + "}")
	private String diaEspecialImpresion ;
	
	@Value("${" + Constants.PARAM_YAML_MAXIMO_HOJAS_IMPRESION + "}")
	private String maximoHojasImpresion ;
	
    /** Atributo - Carpeta con impresiones pendientes */
    private File carpetaConImpresionesPendientes ;
	
	/**
	 * Este método se encarga de inicializar el sistema
	 * ya sea en el entorno de desarrollo o ejecutando JAR
	 * @throws PrintersServerException con una excepción cargando los días festivos
	 * @throws BaseServerException con una excepción cargando las carpetas de resources
	 */
	@PostConstruct
	public void inicializarSistema() throws PrintersServerException, BaseServerException
	{
		// Esta es la carpeta con las subcarpetas y configuraciones
	    ResourcesHandler printersServerConfig = this.getResourcesHandler(Constants.PRINTERS_SERVER_CONFIG);
	    
	    if (printersServerConfig != null)
	    {
	    	// Nombre de la carpeta destino
	    	this.carpetaConImpresionesPendientes = new File(Constants.PRINTERS_SERVER_CONFIG_EXEC) ;
  
	    	// Copiamos las plantillas (origen) al destino
	    	printersServerConfig.copyToDirectory(this.carpetaConImpresionesPendientes) ;
	    }
	    
		// Si estamos creando la BBDD, entonces creamos las constantes por defecto
		if (Constants.MODO_DDL_CREATE.equalsIgnoreCase(this.modoDdl))
		{
			// Cargamos los días festivos desde el CSV
			this.cargarDiasFestivosDesdeCSVInternal() ;
			
			// Inicializamos las constantes
			this.inicializarSistemaConConstantes() ;
		}
	}
	
	/**
	 * 
	 * @param resourceFilePath con la carpeta origen que tiene las plantillas
	 * @return el manejador que crea la estructura
	 */
	private ResourcesHandler getResourcesHandler(String resourceFilePath)
	{
		ResourcesHandler outcome = null;

		URL baseDirSubfolderUrl = Thread.currentThread().getContextClassLoader().getResource(resourceFilePath);
		if (baseDirSubfolderUrl != null)
		{
			if (baseDirSubfolderUrl.getProtocol().equalsIgnoreCase("file"))
			{
				outcome = new ResourcesHandlerFile(baseDirSubfolderUrl);
			}
			else
			{
				outcome = new ResourcesHandlerJar(baseDirSubfolderUrl);
			}
		}
		
		return outcome;
	}
	
    /**
     * @return la carpeta con impresiones pendientes
     */
    public File getCarpetaConImpresionesPendientes()
    {
        return this.carpetaConImpresionesPendientes ;
    }
    
    /**
     * Carga días festivos desde CSV - Internal
     * @throws PrintersServerException excepción mientras se leían los días lectivos
     */
	private void cargarDiasFestivosDesdeCSVInternal() throws PrintersServerException
	{
    	// Inicializamos la lista de días festivos
        List<DiaFestivo> diasFestivos = new ArrayList<DiaFestivo>() ;
        
        BufferedReader reader = null ;

        try
        {
            // Leer el archivo CSV desde la carpeta de recursos
            reader = new BufferedReader(new FileReader(ResourceUtils.getFile(Constants.FICHERO_DIAS_FESTIVOS))) ;
            
            // Nos saltamos la primera línea
            reader.readLine() ;

            // Leemos la segunda línea que ya tiene datos
            String linea = reader.readLine() ;
            
            while (linea != null)
            {
            	// Leemos la línea y la spliteamos
                String[] valores = linea.split(",") ;

                // Procesamos la línea
                this.procesarDiaLectivo(diasFestivos, valores) ;
                
                // Leemos la siguiente línea
                linea = reader.readLine() ;
            }
        }
        catch (IOException ioException)
        {
			String errorString = "IOException mientras se leía línea de día festivo" ;
			
			log.error(errorString, ioException) ;
			throw new PrintersServerException(Constants.ERR_CODE_PROCESANDO_DIA_LECTIVO, errorString, ioException) ;
        }
        finally
        {
        	this.cerrarFlujo(reader) ;
        }

        // Guardamos los días festivos en la base de datos
        if (!diasFestivos.isEmpty())
        {
            this.diaFestivoRepository.saveAll(diasFestivos) ;
        }
	}

	/**
	 * @param diasFestivos lista con los días festivos ya cargados
	 * @param lineaConDiaFestivo info con el nuevo día festivo
	 * @throws PrintersServerException Error procesando línea de día lectivo
	 */
	private void procesarDiaLectivo(List<DiaFestivo> diasFestivos, String[] lineaConDiaFestivo) throws PrintersServerException
	{
		try
		{
		    // Parseamos la fecha
		    Date fecha = ConversorFechasHoras.convertirStringToDate(lineaConDiaFestivo[0].trim()) ;
		    
		    // Obtenemos la descripción
		    String descripcion = lineaConDiaFestivo[1] ;

		    // Creamos el día lectivo
		    DiaFestivo diaFestivo = new DiaFestivo(null, fecha, descripcion) ;
		    
		    // Lo añadimos a la lista
		    diasFestivos.add(diaFestivo) ;
		}
		catch (ParseException parseException)
		{
			String errorString = "ParseException mientras se leía línea con día festivo" ;
			
			log.error(errorString, parseException) ;
			throw new PrintersServerException(Constants.ERR_CODE_PROCESANDO_FECHA_DIA_LECTIVO, errorString, parseException) ;
		}
	}
	
	/**
	 * @param reader reader
	 * @throws PrintersServerException excepción mientras se cerraba el reader
	 */
	private void cerrarFlujo(BufferedReader reader) throws PrintersServerException
	{
		if (reader != null)
		{
		    try
		    {
		    	// Cierre del reader
				reader.close() ;
			}
		    catch (IOException ioException)
		    {
				String errorString = "IOException mientras se cerraba el reader de los días festivos" ;
				
				log.error(errorString, ioException) ;
				throw new PrintersServerException(Constants.ERR_CODE_CIERRE_READER_DIA_LECTIVO, errorString, ioException) ;
			}	
		}
	}
	
	/**
	 * Este método se encarga de inicializar el sistema con las constantes siempre que estemos 
	 * creando la base de datos ya sea en el entorno de desarrollo o ejecutando JAR
	 */
	private void inicializarSistemaConConstantes()
	{
		this.cargarPropiedad(Constants.TABLA_CONST_IMPRESION_DESHABILITADA, this.appDeshabilitada) ;
		this.cargarPropiedad(Constants.TABLA_CONST_HORA_INICIO_IMPRESION,   this.horaInicioImpresion) ;
		this.cargarPropiedad(Constants.TABLA_CONST_HORA_FIN_IMPRESION,      this.horaFinImpresion) ;
		this.cargarPropiedad(Constants.TABLA_CONST_DIA_ESPECIAL_IMPRESION,  this.diaEspecialImpresion) ;
		this.cargarPropiedad(Constants.TABLA_CONST_MAXIMO_HOJAS_IMPRESION,  this.maximoHojasImpresion) ;
	}
	
	/**
	 * @param key clave
	 * @param value valor
	 */
	private void cargarPropiedad(String key, String value)
	{
		// Verificamos si tiene algún valor
        Optional<Constante> property = this.constantesRepository.findById(key) ;
        
        // Si está vacío, lo seteamos con el valor del YAML
        if (property.isEmpty())
        {
        	Constante constante = new Constante() ;
        	
            constante.setClave(key) ;
            constante.setValor(value) ;
            
            // Almacenamos la constante en BBDD
            this.constantesRepository.save(constante) ;
        }
    }
}
