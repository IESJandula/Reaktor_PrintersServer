package es.iesjandula.remote_printer_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.remote_printer_server.models.Printer;

public interface IPrinterRepository extends JpaRepository<Printer, String>
{

}
