package es.iesjandula.remote_printer_server.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class Constants
{
	/** Modo DDL - Create */
	public static final String MODO_DDL_CREATE = "create" ;
	
	/*********************************************************/
	/*********************** Estados *************************/
	/*********************************************************/
	
	/** Constante - Estado - TO DO */
	public static final String STATE_TODO 		 = "Pendiente" ;
	
	/** Constante - Estado - SEND */
	public static final String STATE_SEND 		 = "Enviado" ;
	
	/** Constante - Estado - DONE */
	public static final String STATE_DONE 		 = "Realizado" ;
	
	/** Constante - Estado - ERROR */
	public static final String STATE_ERROR 	 	 = "Error" ;
	
	/** Constante - Lista de estados */
	public static final List<String> STATES_LIST = Arrays.asList(new String[] { STATE_TODO, STATE_SEND, STATE_DONE, STATE_ERROR } ) ;
	
	
	/*********************************************************/
	/********************* Orientacion ***********************/
	/*********************************************************/
	
	/** Constante - Orientación - Vertical */
	public static final String ORIENTATION_VERTICAL    = "Vertical" ;
	
	/** Constante - Orientación - Paisaje */
	public static final String ORIENTATION_PAISAJE     = "Horizontal" ;
	
	/** Constante - Lista de orientaciones */
	public static final List<String> ORIENTATIONS_LIST = Arrays.asList(new String[] { ORIENTATION_VERTICAL, ORIENTATION_PAISAJE } ) ;
	
	
	/*********************************************************/
	/************************ Caras **************************/
	/*********************************************************/
	
	/** Constante - Orientación - Vertical */
	public static final String SIDES_DOUBLE_SIDE = "Doble cara" ;
	
	/** Constante - Orientación - Horizontal */
	public static final String SIDES_ONE_SIDE    = "Una cara" ;
	
	/** Constante - Lista de caras */
	public static final List<String> SIDES_LIST  = Arrays.asList(new String[] { SIDES_DOUBLE_SIDE, SIDES_ONE_SIDE } ) ;
	
	
	/*********************************************************/
	/*********************** Colores *************************/
	/*********************************************************/
	
	/** Constante - Colores - Vertical */
	public static final String COLOR_BLACK_AND_WHITE = "Blanco y negro" ;
	
	/** Constante - Colores - Horizontal */
	public static final String COLOR_COLOR			 = "Color" ;
	
	/** Constante - Lista de colores */
	public static final List<String> COLORS_LIST     = Arrays.asList(new String[] { COLOR_BLACK_AND_WHITE, COLOR_COLOR } ) ;

	
	/*********************************************************/
	/*********************** Errores *************************/
	/*********************************************************/
	
	/** Error - Excepción genérica - Código */
	public static final int ERR_GENERIC_EXCEPTION_CODE 			  = 100 ;
	
	/** Error - Excepción genérica - Mensaje */
	public static final String ERR_GENERIC_EXCEPTION_MSG 		  = "Excepción genérica en " ;
	
	/** Error - IOException - Mientras se leía el fichero Multipart - Código */
	public static final int ERR_IOEXCEPTION_FILE_READING_CODE 	  = 101 ;
	
	/** Error - Codigo - Procesando fecha día lectivo */
	public static final int ERR_CODE_PROCESANDO_FECHA_DIA_LECTIVO = 102 ;
	
	/** Error - Codigo - Procesando día lectivo */
	public static final int ERR_CODE_PROCESANDO_DIA_LECTIVO 	  = 103 ;
	
	/** Error - Codigo - Cierre reader días lectivos */
	public static final int ERR_CODE_CIERRE_READER_DIA_LECTIVO    = 104 ;
	
	/** Error - Estado no válido - Código */
	public static final int ERR_INVALID_STATUS_CODE 			  = 105 ;

	/** Error - Color no válido - Código */
	public static final int ERR_INVALID_COLOR_CODE 			  	  = 106 ;

	/** Error - Orientación no válida - Código */
	public static final int ERR_INVALID_ORIENTATION_CODE  		  = 107 ;

	/** Error - Tipo de cara no válido - Código */
	public static final int ERR_INVALID_SIDES_CODE 			  	  = 108 ;
	
	/** Error - Tarea no encontrada por id */
	public static final int ERR_PRINT_ACTION_NOT_FOUND_BY_ID 	  = 109 ;
	
	/** Error - ResourcesHandler */
	public static final int EXC_ERR_CODE_RESOURCES_HANDLER 		  = 110 ;


	/*********************************************************/
	/****************** Ficheros y carpetas ******************/
	/*********************************************************/
	
	/** Fichero días festivos */
	public static final String FICHERO_DIAS_FESTIVOS  	   = "dias_festivos.csv" ;
	
	/** Nombre de la carpeta de configuracion */
	public static final String PRINTERS_SERVER_CONFIG 	   = "printers_server_config" ;
	
	/** Nombre de la carpeta de configuracion al ejecutarse */
	public static final String PRINTERS_SERVER_CONFIG_EXEC = "printers_server_config_exec" ;
	
	/*********************************************************/
	/*********************** Headers *************************/
	/*********************************************************/

	/** Constante - Header - ID */
	public static final String HEADER_PRINT_ID = "id" ;

	/** Constante - Header - Usuario */
	public static final String HEADER_PRINT_USER = "user" ;

	/** Constante - Header - Content-Disposition */
	public static final String HEADER_PRINT_CONTENT_DISPOSITION = "Content-Disposition" ;

	/** Constante - Header - Impresora */
	public static final String HEADER_PRINT_PRINTER = "printer" ;

	/** Constante - Header - Número de Copias */
	public static final String HEADER_PRINT_COPIES = "copies" ;

	/** Constante - Header - Color */
	public static final String HEADER_PRINT_COLOR = "color" ;

	/** Constante - Header - Orientación */
	public static final String HEADER_PRINT_ORIENTATION = "orientation" ;

	/** Constante - Header - Caras */
	public static final String HEADER_PRINT_SIDES = "sides" ;

}

