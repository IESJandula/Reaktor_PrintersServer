package es.iesjandula.remote_printer_server.rest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

	public static final String ONE_DAY_TIME = "0";
	public static final String THREE_DAYS_TIME = "1";
	public static final String ONE_WEEK_TIME = "2";
	public static final String ONE_MONTH_TIME = "3";
	public static final String ALL_TIME = "4";
	
	@Autowired
	private IPrinterRepository printerRepository;

	@Autowired
	private IPrintActionRepository printActionRepository;

	private String filePath = "." + File.separator + "files" + File.separator;

	
	/**
	 * Endpoint que guarda las impresoras guardadas en base de datos
	 * @param listPrinters
	 * @return ok si se guarda correctamente
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send/printers", consumes = "application/json")
	public ResponseEntity<?> sendPrinters(@RequestBody(required = true) List<Printer> listPrinters)
	{
		try
		{
			this.printerRepository.saveAllAndFlush(listPrinters);
			
			return ResponseEntity.ok().build();
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}
	
	/**
	 * Endpoint que devuelve el contenido del documento de la printAction dada mediante una id 
	 * @param id
	 * @return los bytes del documento
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/get/document", produces = "multipart/form-data")
	public ResponseEntity<?> getDocument(@RequestParam(required = true) Long id)
	{
		try
		{
			Optional<PrintAction> optional = this.printActionRepository.findById(id);
			
			if (optional.isPresent())
			{
	            byte[] bytes = Files.readAllBytes(Paths.get(this.filePath + File.separator + optional.get().getId() + File.separator + optional.get().getFileName()));
	            return ResponseEntity.ok().body(bytes);
			}else 
			{
				String message = "No existe printAction para ese id";
				log.error(message);
				return ResponseEntity.status(401).body(message);
			}
			
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * Devuelve las impresoras guardadas en base de datos
	 * @return la lista de impresoras
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/get/printers")
	public ResponseEntity<?> getPrinters()
	{
		try
		{

			List<Printer> availablePrinters = this.printerRepository.findAll();

			return ResponseEntity.ok().body(availablePrinters);
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * Guarda en base de datos la peticion de impresion realizada desde la web y guarda el documento en el servidor
	 * @param printerName
	 * @param numCopies
	 * @param orientation
	 * @param color
	 * @param faces
	 * @param user
	 * @param file
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/print", consumes = "multipart/form-data")
	public ResponseEntity<?> printPDF(@RequestParam(required = true) String printerName,
			@RequestParam(required = true) Integer numCopies, @RequestParam(required = true) String orientation,
			@RequestParam(required = true) String color,@RequestParam(required = true) String faces,
			@RequestParam(required = true) String user,@RequestBody(required = true) MultipartFile file)
	{
		try
		{
			PrintAction printAction = new PrintAction();
			//Crea el objeto printAction con la configuracion mandada
			Date date = new Date();
			printAction.setPrinterName(printerName);
			printAction.setFileName(file.getOriginalFilename());
			printAction.setNumCopies(numCopies);
			printAction.setColor(color);
			printAction.setOrientation(orientation);
			printAction.setFaces(faces);
			printAction.setUser(user);
			printAction.setStatus(PrintAction.TO_DO);
			printAction.setDate(date);
			this.printActionRepository.saveAndFlush(printAction);

			List<PrintAction> actions = this.printActionRepository.findByUserAndDate(user, date);
			
			//Guarda el fichero en el servidor
			File folder = new File(this.filePath + File.separator + actions.get(0).getId());
			
			folder.mkdir();
			
			this.writeText(this.filePath + File.separator + actions.get(0).getId() + File.separator + printAction.getFileName(), file.getBytes());

			return ResponseEntity.ok().build();
		} catch (Exception exception)
		{
			String error = "Error getting the printers";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}

	}

	/**
	 * Configura y envia a la maquina cliente la informacion para realizar la impresion
	 * @return
	 */
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

				File file = new File(this.filePath + File.separator +printAction.getId()+ File.separator + printAction.getFileName());

				byte[] contenidoDelFichero = Files.readAllBytes(file.toPath());

				InputStreamResource outcomeInputStreamResource = new InputStreamResource(
						new java.io.ByteArrayInputStream(contenidoDelFichero));

				HttpHeaders headers = new HttpHeaders();
				//Introducimos por header los parametros de la impresion
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
	
	/**
	 * Obtiene la información de la maquina cliente de como se ha finalizado una printAction
	 * @param id
	 * @param status
	 * @return
	 */
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
	
	/**
	 * Devuelve las impresiones filtradas por los parametros pasados como parametro, si no se envia ninguno manda todos
	 * @param numCopies
	 * @param date
	 * @param color
	 * @param faces
	 * @param orientation
	 * @param printerName
	 * @param user
	 * @param status
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/get/user/prints")
	public ResponseEntity<?> getUserPrints(
			@RequestParam(required = false) Integer numCopies,
			@RequestParam(required = false) String date,
			@RequestParam(required = false) String color,
			@RequestParam(required = false) String faces,
			@RequestParam(required = false) String orientation,
			@RequestParam(required = false) String printerName,
			@RequestParam(required = false) String user, 
			@RequestParam(required = false) String status)
	{
		try
		{	
			
			List<PrintAction> actions = this.printActionRepository.findAll();
			
			if (user != null && !user.equals(""))
			{
				log.info( "User:" + user);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getUser().equals(user)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (status != null && !status.equals(""))
			{
				log.info( "Status:" + status);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getStatus().equals(status)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (printerName != null && !printerName.equals(""))
			{
				log.info( "Printer:" + printerName);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getPrinterName().equals(printerName)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (date != null && !date.equals(""))
			{
				log.info( "date:" + date);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				
				Date d = format.parse(date);
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getDate().equals(d)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (numCopies != null)
			{
				
				log.info( "numCopies:" + numCopies);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getNumCopies() == numCopies) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (color != null && !color.equals(""))
			{
				log.info( "color:" + color);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getColor().equals(color)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (faces != null && !faces.equals(""))
			{
				log.info( "faces:" + faces);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getFaces().equals(faces)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (orientation != null && !orientation.equals(""))
			{
				log.info( "orientation:" + orientation);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getOrientation().equals(orientation)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
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
	 * Elimina las printActions en base a 2 parametros, fecha y el status en el que se encuetra
	 * @param date
	 * @param status
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/delete/print_actions")
	public ResponseEntity<?> deletePrintActions(
			@RequestParam(required = false) String date,
			@RequestParam(required = false) String status)
	{
		try
		{	
			
			List<PrintAction> actions = this.printActionRepository.findAll();
			
			if (status != null && !status.equals(""))
			{
				log.info( "Status:" + status);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getStatus().equals(status)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			if (date != null && !date.equals(""))
			{
				log.info( "date:" + date);
				List<PrintAction> filteredActions = new ArrayList<PrintAction>();
				
				Date d = this.selectDeteleDays(date);
				
				for (PrintAction printAction : actions)
				{
					if(printAction.getDate().before(d)) {
						filteredActions.add(printAction);
					}
				}
				actions = filteredActions;
			}
			
			for (PrintAction printAction : actions)
			{
				File file = new File(this.filePath + File.separator +printAction.getId()+ File.separator +printAction.getFileName());
				file.delete();
				file = new File(this.filePath + File.separator +printAction.getId());
				file.delete();
				this.printActionRepository.delete(printAction);
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception exception)
		{
			String error = "Error deleting the printersActions";
			log.error(error, exception);
			return ResponseEntity.status(500).build();
		}
	}
		
	/**
	 * Metodo que calcula hasta que dia se debe borrar las printAction
	 * @param selection
	 * @return
	 */
	private Date selectDeteleDays (String selection) 
	{
		
		Date date = new Date();
		
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); 

        calendar.add(Calendar.DAY_OF_MONTH, -1);
		
		switch (selection)
		{
			case PrinterRest.ONE_DAY_TIME:
			{
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			case PrinterRest.THREE_DAYS_TIME:
			{
				calendar.add(Calendar.DAY_OF_MONTH, -3);
			}
			case PrinterRest.ONE_WEEK_TIME:
			{
				calendar.add(Calendar.DAY_OF_MONTH, -7);
			}
			case PrinterRest.ONE_MONTH_TIME:
			{
				calendar.add(Calendar.MONTH, -1);
			}
		}
		return calendar.getTime();
	}
	
	/**
	 * Escribe un fichero en la ruta seleccionada
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
