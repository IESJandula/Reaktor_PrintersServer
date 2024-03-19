package es.iesjandula.remote_printer_server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "printer")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Printer
{

	@Id
	private String name;
	
}
