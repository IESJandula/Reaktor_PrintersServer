package es.iesjandula.remote_printer_server.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "print_action")
@NoArgsConstructor
@AllArgsConstructor
@Data
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
}
