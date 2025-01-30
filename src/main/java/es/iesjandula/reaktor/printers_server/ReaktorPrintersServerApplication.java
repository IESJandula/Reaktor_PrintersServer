package es.iesjandula.reaktor.printers_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * @author Francisco Manuel Benítez Chico
 */
@SpringBootApplication
@EntityScan(basePackages = {"es.iesjandula"})
public class ReaktorPrintersServerApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ReaktorPrintersServerApplication.class, args);
	}
}
