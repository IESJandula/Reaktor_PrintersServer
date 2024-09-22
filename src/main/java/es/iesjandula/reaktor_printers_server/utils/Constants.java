package es.iesjandula.reaktor_printers_server.utils;

import java.io.File;
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
	
	/** Constante - Estado - CANCELED */
	public static final String STATE_CANCELED 	 = "Cancelada" ;
	
	/** Constante - Lista de estados */
	public static final List<String> STATES_LIST = Arrays.asList(new String[] { STATE_TODO, STATE_SEND, 
																				STATE_DONE, STATE_ERROR, 
																				STATE_CANCELED } ) ;
	
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
	
	/** Error - Mientras se conseguía la metainformación del fichero PDF */
	public static final int ERR_IOEXCEPTION_GETTING_METAINFO_PDF  = 110 ;
	
	/** Error - Usuario trató de cancelar una tarea que no existe */
	public static final int ERR_USER_TRIED_TO_CANCEL_NO_EXISTING_TASK = 111 ;
	
	/** Error - Usuario trató de cancelar una tarea no pendiente */
	public static final int ERR_USER_TRIED_TO_CANCEL_NO_PENDING_TASK = 112 ;
	
	/** Error - Usuario trató de cancelar una tarea que no pertenecía a este */
	public static final int ERR_USER_TRIED_TO_CANCEL_ANOTHER_USER_TASK = 113 ;
	
	/** Error - Usuario trató de cancelar una tarea que no existía el PDF en el sistema */
	public static final int ERR_USER_TRIED_TO_CANCEL_WITHOUT_PDF_IN_SYSTEM = 114 ;
	
	/** Error - Propiedad constante no encontrada en el sistema */
	public static final int ERR_CONSTANT_PROPERTY_NOT_FOUND = 115 ;
	
	/** Error - Propiedad constante no encontrada en el sistema */
	public static final int ERR_USER_TRIED_TO_PRINT_WITH_GLOBAL_ERROR = 116 ;
	

	/*********************************************************/
	/****************** Ficheros y carpetas ******************/
	/*********************************************************/
	
	/** Nombre de la carpeta de configuracion */
	public static final String PRINTERS_SERVER_CONFIG 	   = "printers_server_config" ;
	
	/** Nombre de la carpeta de configuracion al ejecutarse */
	public static final String PRINTERS_SERVER_CONFIG_EXEC = "printers_server_config_exec" ;
	
	/** Fichero días festivos */
	public static final String FICHERO_DIAS_FESTIVOS  	   = PRINTERS_SERVER_CONFIG_EXEC + File.separator + "dias_festivos.csv" ;
	
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

	
	/*********************************************************/
	/****************** Tabla Constantes *********************/
	/*********************************************************/
	
	/** Constante - Tabla Constantes - APP Deshabilitada */
	public static final String TABLA_CONST_IMPRESION_DESHABILITADA = "Impresion Deshabilitada" ;
	
	/** Constante - Tabla Constantes - Hora inicio impresión */
	public static final String TABLA_CONST_HORA_INICIO_IMPRESION   = "Hora inicio impresion" ;
	
	/** Constante - Tabla Constantes - Hora fin impresión */
	public static final String TABLA_CONST_HORA_FIN_IMPRESION      = "Hora fin impresion" ;
	
	/** Constante - Tabla Constantes - Día especial impresión */
	public static final String TABLA_CONST_DIA_ESPECIAL_IMPRESION  = "Dia especial impresion" ;
	
	/** Constante - Tabla Constantes - Hojas impresión */
	public static final String TABLA_CONST_MAXIMO_HOJAS_IMPRESION  = "Maximo hojas impresion" ;
	
	
	/*********************************************************/
	/******************* Parámetros YAML *********************/
	/*********************************************************/
	
	/** Constante - Parámetros YAML - Impresión Deshabilitada */
	public static final String PARAM_YAML_IMPRESION_DESHABILITADA = "reaktor.constantes.impresionDeshabilitada" ;
	
	/** Constante - Parámetros YAML - Hora inicio impresión */
	public static final String PARAM_YAML_HORA_INICIO_IMPRESION   = "reaktor.constantes.horaInicioImpresion" ;
	
	/** Constante - Parámetros YAML - Hora fin impresión */
	public static final String PARAM_YAML_HORA_FIN_IMPRESION      = "reaktor.constantes.horaFinImpresion" ;
	
	/** Constante - Parámetros YAML - Día especial impresión */
	public static final String PARAM_YAML_DIA_ESPECIAL_IMPRESION  = "reaktor.constantes.diaEspecialImpresion" ;
	
	/** Constante - Parámetros YAML - Hojas impresión */
	public static final String PARAM_YAML_MAXIMO_HOJAS_IMPRESION  = "reaktor.constantes.maximoHojasImpresion" ;
}

