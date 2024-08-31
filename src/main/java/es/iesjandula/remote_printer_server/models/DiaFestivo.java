package es.iesjandula.remote_printer_server.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dia_festivo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiaFestivo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "fecha", nullable = false)
    private Date fecha ;

    @Column(name = "descripcion")
    private String descripcion ;
}
