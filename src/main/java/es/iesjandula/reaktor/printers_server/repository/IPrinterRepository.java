package es.iesjandula.reaktor.printers_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.printers_server.dto.DtoPrinters;
import es.iesjandula.reaktor.printers_server.models.Printer;

/**
 * @author Francisco Manuel Benítez Chico
 */
public interface IPrinterRepository extends JpaRepository<Printer, String>
{
	@Query("SELECT new es.iesjandula.reaktor.printers_server.dto.DtoPrinters(p.name, p.statusId, p.status, p.printingQueue, p.lastUpdate) "   +
		   "FROM Printer p")
	List<DtoPrinters> getPrinters() ;

	/** 
	 * Búsqueda de la primera impresora que encuentre que esté operativa (estado sea cero)
	 * @return la impresora encontrada
	 */
	Optional<Printer> findFirstByStatusId(int statusId);
}
