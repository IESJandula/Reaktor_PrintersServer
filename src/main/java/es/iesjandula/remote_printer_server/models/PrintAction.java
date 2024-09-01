package es.iesjandula.remote_printer_server.models;

import java.io.File;
import java.util.Date;

import org.springframework.http.HttpHeaders;

import es.iesjandula.remote_printer_server.utils.Constants;
import es.iesjandula.remote_printer_server.utils.PrintersServerException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "print_action")
public class PrintAction
{
    @Id
    @GeneratedValue
    private long id ;
    
    /** Atributo - User */
    @Column
    private String user ;
    
    /** Atributo - Printer */
    @Column
    private String printer ;
    
    /** Atributo - Status */
    @Column
    private String status ;
    
    /** Atributo - File name */
    @Column
    private String fileName ;

    /** Atributo - Copies */
    @Column
    private int copies ;
    
    /** Atributo - Color */
    @Column
    private String color ;

    /** Atributo - Orientation */
    @Column
    private String orientation ;

    /** Atributo - Sides */
    @Column
    private String sides ;

    /** Atributo - Date */
    @Column
    private Date date ;

    /**
     * Default constructor
     */
    public PrintAction()
    {
    	// Empty
    }
    
    /**
     * Obtiene el identificador único de la acción de impresión.
     * 
     * @return el identificador único (id)
     */
    public long getId()
    {
        return this.id ;
    }

    /**
     * Establece el identificador único de la acción de impresión.
     * 
     * @param id el identificador único (id) a establecer
     */
    public void setId(long id)
    {
        this.id = id ;
    }

    /**
     * Obtiene el nombre del usuario que realiza la acción de impresión.
     * 
     * @return el nombre del usuario
     */
    public String getUser()
    {
        return this.user ;
    }

    /**
     * Establece el nombre del usuario que realiza la acción de impresión.
     * 
     * @param user el nombre del usuario a establecer
     */
    public void setUser(String user)
    {
        this.user = user ;
    }

    /**
     * Obtiene el nombre de la impresora.
     * 
     * @return el nombre de la impresora
     */
    public String getPrinter()
    {
        return this.printer ;
    }

    /**
     * Establece el nombre de la impresora.
     * 
     * @param printer el nombre de la impresora a establecer
     */
    public void setPrinter(String printer)
    {
        this.printer = printer ;
    }

    /**
     * Obtiene el estado de la acción de impresión.
     * 
     * @return el estado de la impresión
     */
    public String getStatus()
    {
        return this.status ;
    }

    /**
     * Establece el estado de la acción de impresión.
     * 
     * @param status el estado a establecer
     * @throws PrintersServerException si el estado no es válido
     */
    public void setStatus(String status) throws PrintersServerException
    {
        if (!Constants.STATES_LIST.contains(status))
        {
            String errorMessage = "El estado '" + status + "' no es válido." ;
            log.error(errorMessage) ;
            
            throw new PrintersServerException(Constants.ERR_INVALID_STATUS_CODE, errorMessage) ;
        }
        
        this.status = status ;
    }

    /**
     * Obtiene el nombre del archivo que se imprimirá.
     * 
     * @return el nombre del archivo
     */
    public String getFileName()
    {
        return this.fileName ;
    }

    /**
     * Establece el nombre del archivo que se imprimirá.
     * 
     * @param fileName el nombre del archivo a establecer
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName ;
    }

    /**
     * Obtiene el número de copias que se deben imprimir.
     * 
     * @return el número de copias
     */
    public int getCopies()
    {
        return this.copies ;
    }

    /**
     * Establece el número de copias que se deben imprimir.
     * 
     * @param copies el número de copias a establecer
     */
    public void setCopies(int copies)
    {
        this.copies = copies ;
    }

    /**
     * Obtiene el color de la impresión.
     * 
     * @return el color de la impresión
     */
    public String getColor()
    {
        return this.color ;
    }

    /**
     * Establece el color de la impresión.
     * 
     * @param color el color a establecer
     * @throws PrintersServerException si el color no es válido
     */
    public void setColor(String color) throws PrintersServerException
    {
        if (!Constants.COLORS_LIST.contains(color))
        {
            String errorMessage = "El color '" + color + "' no es válido." ;
            
            log.error(errorMessage) ;
            throw new PrintersServerException(Constants.ERR_INVALID_COLOR_CODE, errorMessage) ;
        }
        
        this.color = color ;
    }

    /**
     * Obtiene la orientación de la impresión.
     * 
     * @return la orientación de la impresión
     */
    public String getOrientation()
    {
        return this.orientation ;
    }

    /**
     * Establece la orientación de la impresión.
     * 
     * @param orientation la orientación a establecer
     * @throws PrintersServerException si la orientación no es válida
     */
    public void setOrientation(String orientation) throws PrintersServerException
    {
        if (!Constants.ORIENTATIONS_LIST.contains(orientation))
        {
            String errorMessage = "La orientación '" + orientation + "' no es válida." ;
            
            log.error(errorMessage) ;
            throw new PrintersServerException(Constants.ERR_INVALID_ORIENTATION_CODE, errorMessage) ;
        }
        
        this.orientation = orientation ;
    }

    /**
     * Obtiene el tipo de caras de la impresión.
     * 
     * @return el tipo de caras de la impresión
     */
    public String getSides()
    {
        return this.sides ;
    }

    /**
     * Establece el tipo de caras de la impresión.
     * 
     * @param sides el tipo de caras a establecer
     * @throws PrintersServerException si el tipo de caras no es válido
     */
    public void setSides(String sides) throws PrintersServerException
    {
        if (!Constants.SIDES_LIST.contains(sides))
        {
            String errorMessage = "El tipo de cara '" + sides + "' no es válido." ;
            
            log.error(errorMessage) ;
            throw new PrintersServerException(Constants.ERR_INVALID_SIDES_CODE, errorMessage) ;
        }
        
        this.sides = sides ;
    }

    /**
     * Obtiene la fecha de la acción de impresión.
     * 
     * @return la fecha de la impresión
     */
    public Date getDate()
    {
        return this.date ;
    }

    /**
     * Establece la fecha de la acción de impresión.
     * 
     * @param date la fecha a establecer
     */
    public void setDate(Date date)
    {
        this.date = date ;
    }
    
    /**
     * Genera los encabezados HTTP para la acción de impresión.
     * 
     * @param file El archivo asociado con la acción de impresión.
     * @return Los encabezados HTTP configurados para la respuesta.
     */
    public HttpHeaders generaCabecera(File file)
    {
        HttpHeaders headers = new HttpHeaders() ;

        headers.set(Constants.HEADER_PRINT_ID, String.valueOf(this.id)) ;
        headers.set(Constants.HEADER_PRINT_USER, this.user) ;
        headers.set(Constants.HEADER_PRINT_CONTENT_DISPOSITION, "attachment; filename=" + file.getName()) ;
        headers.set(Constants.HEADER_PRINT_PRINTER, this.printer) ;
        headers.set(Constants.HEADER_PRINT_COPIES, String.valueOf(this.copies)) ;
        headers.set(Constants.HEADER_PRINT_COLOR, this.color.equals(Constants.COLOR_BLACK_AND_WHITE) ? Boolean.TRUE.toString() : Boolean.FALSE.toString()) ;
        headers.set(Constants.HEADER_PRINT_ORIENTATION, this.orientation.equals(Constants.ORIENTATION_VERTICAL) ? Boolean.TRUE.toString() : Boolean.FALSE.toString()) ;
        headers.set(Constants.HEADER_PRINT_SIDES, this.sides.equals(Constants.SIDES_DOUBLE_SIDE) ? Boolean.TRUE.toString() : Boolean.FALSE.toString()) ;
        
        return headers ;
    }
}
