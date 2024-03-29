package es.iesjandula.remote_printer_server.rest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.remote_printer_server.models.PrintAction;
import es.iesjandula.remote_printer_server.models.Printer;
import es.iesjandula.remote_printer_server.repository.IPrintActionRepository;
import es.iesjandula.remote_printer_server.repository.IPrinterRepository;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class PrinterRest
{

	@Autowired
	private IPrinterRepository printerRepository;

	@Autowired
	private IPrintActionRepository printActionRepository;

	private String filePath = "." + File.separator + "files" + File.separator;

	@RequestMapping(method = RequestMethod.POST, value = "/send/printers", consumes = "application/json")
	public ResponseEntity<?> sendPrinters(@RequestBody(required = true) List<String> listPrinters)
	{
		try
		{
			for (String printer : listPrinters)
			{
				this.printerRepository.saveAndFlush(new Printer(printer));
			}

			return ResponseEntity.ok().build();
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get/printers")
	public ResponseEntity<?> getPrinters()
	{
		try
		{
			List<String> listPrinters = new ArrayList<String>();

			List<Printer> availablePrinters = this.printerRepository.findAll();

			for (Printer printer : availablePrinters)
			{
				listPrinters.add(printer.getName());
			}

			return ResponseEntity.ok().body(listPrinters);
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/print", consumes = "multipart/form-data")
	public ResponseEntity<?> printPDF(@RequestParam(required = true) String printerName,
			@RequestParam(required = true) Integer numCopies, @RequestParam(required = true) String orientation,
			@RequestParam(required = true) String color,@RequestParam(required = true) String faces,
			@RequestParam(required = true) String user,@RequestBody(required = true) MultipartFile file)
	{
		try
		{
			PrintAction printAction = new PrintAction();

			printAction.setPrinterName(printerName);
			printAction.setFileName(file.getOriginalFilename());
			printAction.setNumCopies(numCopies);
			printAction.setColor(color);
			printAction.setOrientation(orientation);
			printAction.setFaces(faces);
			printAction.setUser(user);
			printAction.setStatus(PrintAction.TO_DO);
			printAction.setDate(new Date());

			this.writeText(this.filePath + printAction.getFileName(), file.getBytes());
			this.printActionRepository.saveAndFlush(printAction);

			return ResponseEntity.ok().build();
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = "/get/prints")
	public ResponseEntity<?> sendPrintAction()
	{
		try
		{

			List<PrintAction> actions = this.printActionRepository.findByStatus(PrintAction.TO_DO);

			if (!actions.isEmpty())
			{
				// --- ORDENAMOS LAS FECHAS ---
				actions.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));

				// --- OBTENEMOS LA PRIMERA TASK ---
				PrintAction printAction = actions.get(0);

				printAction.setStatus(PrintAction.SEND);
				this.printActionRepository.saveAndFlush(printAction);

				File file = new File(this.filePath + printAction.getFileName());

				byte[] contenidoDelFichero = Files.readAllBytes(file.toPath());

				InputStreamResource outcomeInputStreamResource = new InputStreamResource(
						new java.io.ByteArrayInputStream(contenidoDelFichero));

				HttpHeaders headers = new HttpHeaders();

				headers.set("Content-Disposition", "attachment; filename=" + file.getName());
				headers.set("numCopies", "" + printAction.getNumCopies());
				headers.set("printerName", printAction.getPrinterName());
				if (printAction.getColor().equalsIgnoreCase("Color"))
				{
					headers.set("color", "true");
				} else
				{
					headers.set("color", "false");
				}

				if (printAction.getOrientation().equalsIgnoreCase("Vertical"))
				{
					headers.set("orientation", "true");
				} else
				{
					headers.set("orientation", "false");
				}
				
				if (printAction.getFaces().equalsIgnoreCase("Single"))
				{
					headers.set("faces", "false");
				} else
				{
					headers.set("faces", "true");
				}
				
				headers.set("user", printAction.getUser());
				headers.set("id", String.valueOf(printAction.getId()));
				
				return ResponseEntity.ok().headers(headers).body(outcomeInputStreamResource);
			}

			return ResponseEntity.ok().build();

		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/get/print/status")
	public ResponseEntity<?> getPrintFinalization(
			@RequestHeader(name = "id") String id,
			@RequestHeader(name = "status")	String status)
	{
		try
		{		
			Optional<PrintAction> action = this.printActionRepository.findById(Long.valueOf(id));	
			if (action.isPresent())
			{
				if (status.equalsIgnoreCase(PrintAction.DONE))
				{
					action.get().setStatus(PrintAction.DONE);
				}else 
				{
					action.get().setStatus(PrintAction.ERROR);
				}
				this.printActionRepository.saveAndFlush(action.get());
				return ResponseEntity.ok().build();
			}
			else 
			{
				return ResponseEntity.status(400).body("No action found");
			}
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/get/user/prints")
	public ResponseEntity<?> getUserPrints(
			@RequestParam(name = "user")String user)
	{
		try
		{		
			List<PrintAction> actions = this.printActionRepository.findByUser(user);
			
			actions.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
			
			return ResponseEntity.ok().body(actions);
			
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * Method writeText
	 * 
	 * @param name
	 * @param content
	 */
	private void writeText(String name, byte[] content)
	{
		// DELCARAMOS FLUJOS
		FileOutputStream fileOutputStream = null;
		DataOutputStream dataOutputStream = null;

		try
		{
			// CREAMOS LOS FLUJOS
			fileOutputStream = new FileOutputStream(name);
			dataOutputStream = new DataOutputStream(fileOutputStream);

			// GUARDAMOS EL FICHERO
			dataOutputStream.write(content);
			// HACEMOS FLUSH
			dataOutputStream.flush();

		} catch (IOException exception)
		{
			String message = "Error";
			log.error(message, exception);
		} finally
		{
			if (dataOutputStream != null)
			{
				try
				{
					dataOutputStream.close();
				} catch (IOException exception)
				{
					String message = "Error";
					log.error(message, exception);
				}
			}

			if (fileOutputStream != null)
			{
				try
				{
					fileOutputStream.close();
				} catch (IOException exception)
				{
					String message = "Error";
					log.error(message, exception);
				}
			}
		}
	}

}
