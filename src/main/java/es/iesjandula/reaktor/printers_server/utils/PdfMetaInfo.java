package es.iesjandula.reaktor.printers_server.utils;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class PdfMetaInfo
{
	/** Atributo - Original file name */
    private final String originalFilename ;
    
    /** Atributo - File size in KB */
    private final long fileSizeInKB ;
    
    /** Atributo - Numero de páginas  PDF */
    private final int numeroPaginasPdf ;
    
    /** Atributo - Hojas totales */
    private final int hojasTotales ;

    /**
     * @param originalFilename Original file name
     * @param fileSizeInKB File size in KB
     * @param numeroPaginasPdf Numero de páginas  PDF
     * @param hojasTotales Hojas totales
     */
    public PdfMetaInfo(String originalFilename, long fileSizeInKB, int numeroPaginasPdf, int hojasTotales)
    {
        this.originalFilename = originalFilename ;
        this.fileSizeInKB 	  = fileSizeInKB ;
        this.numeroPaginasPdf = numeroPaginasPdf ;
        this.hojasTotales 	  = hojasTotales ;
    }

	/**
	 * @return the originalFilename
	 */
	public String getOriginalFilename()
	{
		return this.originalFilename ;
	}

	/**
	 * @return the fileSizeInKB
	 */
	public long getFileSizeInKB()
	{
		return this.fileSizeInKB ;
	}

	/**
	 * @return the numeroPaginasPdf
	 */
	public int getNumeroPaginasPdf()
	{
		return this.numeroPaginasPdf ;
	}

	/**
	 * @return the hojasTotales
	 */
	public int getHojasTotales()
	{
		return this.hojasTotales ;
	}
}

