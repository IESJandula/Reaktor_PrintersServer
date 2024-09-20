package es.iesjandula.reaktor_printers_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor_printers_server.models.Constante;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Repository
public interface IConstanteRepository extends JpaRepository<Constante, String>
{
    /**
     * Búsqueda de constante por clave
     * @param clave clave de la constante
     * @return constante encontrada
     */
	Optional<Constante> findByClave(String clave) ;
}

