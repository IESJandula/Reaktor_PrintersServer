package es.sutileza.remote_printer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.sutileza")
public class RemotePrinterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemotePrinterApplication.class, args);
	}

}
