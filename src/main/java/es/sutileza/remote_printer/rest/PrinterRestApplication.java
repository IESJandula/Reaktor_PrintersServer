package es.sutileza.remote_printer.rest;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.sutileza.remote_printer.utils.PDFPrintable;


@RestController
@RequestMapping("/printer")
public class PrinterRestApplication
{
	
	/** Logger de la clase */
	private static Logger log = LogManager.getLogger();

	
	@RequestMapping(method = RequestMethod.GET, value = "/get/printers")
	public ResponseEntity<?> getPrinters()
	{
		try
		{
			List<String> listPrinters = new ArrayList<String>();
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

	        for (PrintService printer : printServices) {
	            listPrinters.add(printer.getName());
	        }
			
			return ResponseEntity.ok().body(listPrinters);
		}
		catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/print")
	public ResponseEntity<?> printPDF(
			@RequestHeader(required = true) String printerName, 
			@RequestBody(required = true) MultipartFile file)
	{
		
        // Obtener todas las impresoras disponibles en el sistema
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        // Buscar la impresora por su nombre
        PrintService selectedPrinter = null;
        for (PrintService printer : printServices) {
            if (printer.getName().equals(printerName)) {
                selectedPrinter = printer;
                break;
            }
        }

        if (selectedPrinter != null) {
            // Crear un trabajo de impresión
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            
            // Cargar el documento a imprimir
            try {
     
                PDDocument pdDocument  = PDDocument.load(file.getBytes());
                
                PDFPrintable pdfPrintable = new PDFPrintable(pdDocument);
                
            	printerJob.setPrintService(selectedPrinter);
                printerJob.setPrintable(pdfPrintable); // Establecer el documento a imprimir
                printerJob.print(); // Este método imprimirá el documento
                return ResponseEntity.ok().build();
            } catch (Exception exception)
    		{
    			String error = "Error imprimiendo";
    			log.error(error, exception);
    			return ResponseEntity.status(500).body(error);
    		}
        } else {
            String error = "Printer erronea";
			log.error(error);
			return ResponseEntity.status(400).body(error);
        }
		
	}
	
}
