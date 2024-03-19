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

	/** CONSTANTE - TO DO */
	public final static String TO_DO = "to do";
	
	/** CONSTANTE - SEND */
	public final static String SEND = "send";
	
	/** CONSTANTE - DONE */
	public final static String DONE = "done";
	
	/** CONSTANTE - ERROR */
	public final static String ERROR = "error";
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column
	private String fileName;

	@Column
	private String printerName;
	
	@Column
	private int numCopies;
	
	@Column
	private String color;
	
	@Column
	private String orientation;
	
	@Column
	private String status;
	
	@Column
	private Date date;
	
}
