package es.iesjandula.reaktor.printers_server.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.iesjandula.reaktor.printers_server.dto.ResponseDtoPrintAction;
import es.iesjandula.reaktor.printers_server.models.PrintAction;

/**
 * @author Francisco Manuel Benítez Chico
 */
public interface IPrintActionRepository extends JpaRepository<PrintAction, Long>
{
	public List<PrintAction> findByStatusOrderByDateAsc(String status) ;
	
	@Query("SELECT new es.iesjandula.reaktor.printers_server.dto.ResponseDtoPrintAction(p.id, p.user, p.printer, p.status, p.fileName, "   +
			 																		   "p.copies, p.color, p.orientation, p.sides, " +
			 																		   "p.date, p.errorMessage, p.fileSizeInKB, " 	 + 
			 																		   "p.numeroPaginasPdf, p.hojasTotales, p.selectedPages) "   +
		   "FROM PrintAction p " +
		   "WHERE " +
				"(:user IS NULL OR p.user = :user) AND " 			+
				"(:printer IS NULL OR p.printer = :printer) AND "   +
				"(:status IS NULL OR p.status = :status) AND " 	    +
				"(:startDate IS NULL OR p.date >= :startDate) AND " +
				"(:endDate IS NULL OR p.date <= :endDate) " 		+
		   "ORDER BY p.date DESC")
	List<ResponseDtoPrintAction> findPrintActions(@Param("user") String user,
												  @Param("printer") String printer,
												  @Param("status") String status,
												  @Param("startDate") Date startDate,
												  @Param("endDate") Date endDate) ;

	@Query(value = "SELECT new es.iesjandula.reaktor.printers_server.dto.ResponseDtoPrintAction(p.id, p.user, p.printer, p.status, p.fileName, "   +
			 																		   "p.copies, p.color, p.orientation, p.sides, " +
			 																		   "p.date, p.errorMessage, p.fileSizeInKB, " 	 + 
			 																		   "p.numeroPaginasPdf, p.hojasTotales, p.selectedPages) "   +
		   "FROM PrintAction p " +
		   "WHERE " +
				"(:user IS NULL OR p.user = :user) AND " 			+
				"(:printer IS NULL OR p.printer = :printer) AND "   +
				"(:status IS NULL OR p.status = :status) AND " 	    +
				"(:startDate IS NULL OR p.date >= :startDate) AND " +
				"(:endDate IS NULL OR p.date <= :endDate) " 		+
		   "ORDER BY p.date DESC",
		   countQuery = "SELECT COUNT(p) FROM PrintAction p " +
		   				"WHERE " +
		   					"(:user IS NULL OR p.user = :user) AND " 			+
		   					"(:printer IS NULL OR p.printer = :printer) AND "   +
		   					"(:status IS NULL OR p.status = :status) AND " 	    +
		   					"(:startDate IS NULL OR p.date >= :startDate) AND " +
		   					"(:endDate IS NULL OR p.date <= :endDate)")
	Page<ResponseDtoPrintAction> findPrintActionsPaginated(@Param("user") String user,
														   @Param("printer") String printer,
														   @Param("status") String status,
														   @Param("startDate") Date startDate,
														   @Param("endDate") Date endDate,
														   Pageable pageable) ;
	
	/**
	 * Cuenta las hojas totales impresas agrupadas por color.
	 * Solo se contabilizan las impresiones que se han realizado correctamente (estado "Realizado").
	 * 
	 * @return Lista de Object[] con [color, SUM(hojasTotales)]
	 */
	@Query("SELECT p.color, SUM(p.hojasTotales) FROM PrintAction p " +
		   "WHERE p.status = 'Realizado' AND p.hojasTotales IS NOT NULL AND p.cursoAcademico = :cursoAcademico" +
		   "GROUP BY p.color")
	List<Object[]> contarHojasPorColor(String cursoAcademico) ;
	
	/**
	 * Cuenta el número de impresiones agrupadas por estado.
	 * 
	 * @return Lista de Object[] con [estado, COUNT(*)]
	 */
	@Query("SELECT p.status, COUNT(p) FROM PrintAction p " +
		   "WHERE p.cursoAcademico = :cursoAcademico" +
		   "GROUP BY p.status")
	List<Object[]> contarPorEstado(String cursoAcademico) ;
	
	/**
	 * Obtiene la fecha de la última impresión realizada por cada impresora.
	 * Solo se contabilizan las impresiones con estado "Realizado".
	 * 
	 * @return Lista de Object[] con [printer, MAX(date)]
	 */
	@Query("SELECT p.printer, MAX(p.date) FROM PrintAction p " +
		   "WHERE p.status = 'Realizado' " +
		   "GROUP BY p.printer")
	List<Object[]> obtenerUltimaImpresionPorImpresora() ;
}