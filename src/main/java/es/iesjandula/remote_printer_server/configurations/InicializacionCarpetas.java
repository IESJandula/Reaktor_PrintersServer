package es.iesjandula.remote_printer_server.configurations;

import java.io.File;
import java.net.URL;

import org.springframework.stereotype.Service;

import es.iesjandula.remote_printer_server.utils.Constants;
import es.iesjandula.remote_printer_server.utils.PrintersServerException;
import es.iesjandula.remote_printer_server.utils.resources_handler.ResourcesHandler;
import es.iesjandula.remote_printer_server.utils.resources_handler.ResourcesHandlerFile;
import es.iesjandula.remote_printer_server.utils.resources_handler.ResourcesHandlerJar;
import jakarta.annotation.PostConstruct;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Service
public class InicializacionCarpetas
{
    /** Atributo - Carpeta con impresiones pendientes */
    private File carpetaConImpresionesPendientes ;
	
	/**
	 * Este método se encarga de inicializar el sistema con las estaciones de Renfe
	 * ya sea en el entorno de desarrollo o ejecutando JAR
	 * @throws PrintersServerException con una excepción
	 */
	@PostConstruct
	public void inicializarSistemaConEstaciones() throws PrintersServerException
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
}
