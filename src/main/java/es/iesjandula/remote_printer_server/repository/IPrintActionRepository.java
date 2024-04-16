package es.iesjandula.remote_printer_server.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.remote_printer_server.models.PrintAction;

public interface IPrintActionRepository extends JpaRepository<PrintAction, Long>
{
	public List<PrintAction> findByStatus(String status);
	public List<PrintAction> findByUser(String user);
	public List<PrintAction> findByUserAndDate(String user, Date date);
}
