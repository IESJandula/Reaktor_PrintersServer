package es.iesjandula.reaktor_printers_server.models;

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
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "constantes")
public class Constante
{
	/** Atributo clave de la constante */
	@Id
	private String clave ;
	
	/** Atributo valor de la constante */
	@Column
	private String valor ;
}

