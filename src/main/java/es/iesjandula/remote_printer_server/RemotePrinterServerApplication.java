package es.iesjandula.remote_printer_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.iesjandula.remote_printer_server")
public class RemotePrinterServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemotePrinterServerApplication.class, args);
	}

}
