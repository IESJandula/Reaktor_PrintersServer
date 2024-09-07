package es.iesjandula.remote_printer_server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Entity
@Table(name = "printer")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Printer
{
	@Id
	private String name ;
	
	@Column
	private int statusId ;
	
	@Column
	private String status ;
	
	@Column
	private int printingQueue ;
}
