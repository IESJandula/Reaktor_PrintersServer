package es.iesjandula.remote_printer_server.utils.resources_handler;

import java.io.File;
import java.net.URL;

import es.iesjandula.remote_printer_server.utils.PrintersServerException;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class ResourcesMaker
{
	/**
	 * @throws PrintersServerException 
	 */
	public void checkStaticFilesToCopyThem() throws PrintersServerException
	{
		ResourcesHandler renfeConfigFolder = this.getResourcesHandler("ficheroImprimir");
		if (renfeConfigFolder != null)
		{
			// Get the current directory
			File destinationFolder = new File("");

			// Copy to this directory
			renfeConfigFolder.copyToDirectory(destinationFolder);
		}
	}

	/**
	 * @param resourceFilePath resource file path
	 * @return resources handler
	 */
	private ResourcesHandler getResourcesHandler(String resourceFilePath)
	{
		ResourcesHandler outcome = null;

		URL baseDirSubfolderUrl = Thread.currentThread().getContextClassLoader().getResource(resourceFilePath);
		if (baseDirSubfolderUrl != null)
		{
			if (baseDirSubfolderUrl.getProtocol().equalsIgnoreCase("file"))
			{
				outcome = new ResourcesHandlerFile(baseDirSubfolderUrl);
			}
			else
			{
				outcome = new ResourcesHandlerJar(baseDirSubfolderUrl);
			}
		}

		return outcome;
	}
}
