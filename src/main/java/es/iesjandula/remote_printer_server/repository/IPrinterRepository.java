package es.iesjandula.remote_printer_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.remote_printer_server.dto.DtoPrinters;
import es.iesjandula.remote_printer_server.models.Printer;

/**
 * @author Francisco Manuel Benítez Chico
 */
public interface IPrinterRepository extends JpaRepository<Printer, String>
{
	@Query("SELECT new es.iesjandula.remote_printer_server.dto.DtoPrinters(p.name, p.statusId, p.status, p.printingQueue) "   +
		   "FROM Printer p")
	List<DtoPrinters> getPrinters() ;
}
