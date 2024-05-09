package es.iesjandula.remote_printer_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.remote_printer_server.models.User;

public interface IUserRepository extends JpaRepository<User, String>
{
}
