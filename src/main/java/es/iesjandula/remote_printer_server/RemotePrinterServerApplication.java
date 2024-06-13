package es.iesjandula.remote_printer_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "es.iesjandula.remote_printer_server")
@ComponentScan(basePackages = {"es.iesjandula"})
public class RemotePrinterServerApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(RemotePrinterServerApplication.class, args);
	}
}
