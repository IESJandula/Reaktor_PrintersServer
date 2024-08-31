package es.iesjandula.remote_printer_server.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.iesjandula.remote_printer_server.models.DiaFestivo;

@Repository
public interface IDiaFestivoRepository extends JpaRepository<DiaFestivo, Long>
{
    Optional<DiaFestivo> findByFecha(Date fecha);
}
