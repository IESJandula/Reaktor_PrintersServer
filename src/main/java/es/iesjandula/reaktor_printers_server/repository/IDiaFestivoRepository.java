package es.iesjandula.reaktor_printers_server.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor_printers_server.models.DiaFestivo;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Repository
public interface IDiaFestivoRepository extends JpaRepository<DiaFestivo, Long>
{
    Optional<DiaFestivo> findByFecha(Date fecha);
}
